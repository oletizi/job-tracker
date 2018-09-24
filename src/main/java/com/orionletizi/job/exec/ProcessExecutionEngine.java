package com.orionletizi.job.exec;

import logging.LoggerFactory;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.*;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ProcessExecutionEngine extends RunnableExecutionEngine {

  private static final Logger logger = new LoggerFactory().getLoggerFor(ProcessExecutionEngine.class);
  private static final int STATUS_BARF = -1 * (0xba * 0xf);
  private static final ExecutorService IO_PUMP = Executors.newFixedThreadPool(4);
  private final File tmpDir;
  private final ExecutorService executor;

  public ProcessExecutionEngine(final File tmpDir, final ExecutorService executor) {
    super(tmpDir, executor);
    this.tmpDir = tmpDir;
    this.executor = executor;
  }

  /**
   * Asynchronously executes the command described in the execution context
   *
   * @param ctxt
   */
  @Override
  public void execute(final ExecutionContext ctxt) {
    final ExecutionResult result = new ExecutionResult();
    executor.submit(() -> {

      final ProcessBuilder builder = new ProcessBuilder(ctxt.getCommand());

      try {
        final File outLog = File.createTempFile(ctxt.getId() + "-stdout-", ".txt", tmpDir);
        outLog.deleteOnExit();

        final File errLog = File.createTempFile(ctxt.getId() + "-stderr-", ".txt", tmpDir);
        outLog.deleteOnExit();

        logger.info("STDOUT log: " + outLog);
        logger.info("STDERR log: " + errLog);


        String outLogName = outLog.getName();
        String errLogName = errLog.getName();

        ctxt.setStdoutName(outLogName);

        ctxt.setStderrName(errLogName);

        logger.info("Starting process for: " + ctxt);
        final Process proc = builder.start();



        logger.info("Setting up output handling...");
        // create some piped output streams to
        final OutputStream outFileStream = new FileOutputStream(outLog);
        final OutputStream errFileStream = new FileOutputStream(errLog);

        // Tee output and error streams
        final TeeOutputStream out = new TeeOutputStream(System.out, outFileStream);
        final TeeOutputStream err = new TeeOutputStream(System.err, errFileStream);


        final StreamPump outPump = new StreamPump(proc.getInputStream(), out);
        final StreamPump errPump = new StreamPump(proc.getErrorStream(), err);

        synchronized (IO_PUMP) {
          IO_PUMP.submit(outPump);
          IO_PUMP.submit(errPump);
        }


        logger.info("Waiting for process: " + ctxt);
        final int status = proc.waitFor();

        logger.info("Process isComplete. Status: " + status);

        result.setStatus(status);

        logger.info("Waiting for STDOUT sink...");
        outPump.waitFor();

        logger.info("Waiting for STDERR sink...");
        errPump.waitFor();

        logger.info("Notifying isComplete...");
        ctxt.notifyComplete(result);

      } catch (Throwable e) {
        result.setStatus(STATUS_BARF);
        result.setThrowable(e);
        e.printStackTrace();
      }
    });

  }

  private static class StreamPump implements Runnable {

    private final BlockingQueue<Date> completionQueue = new LinkedBlockingQueue<>();
    private final InputStream in;
    private final OutputStream out;

    StreamPump(final InputStream in, final OutputStream out) {
      this.in = in;
      this.out = out;
    }

    @Override
    public void run() {
      try {
        byte[] buf = new byte[2048];
        int bytesRead;
        while ((bytesRead = in.read(buf)) != 0) {
          out.write(buf, 0, bytesRead);
        }
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        complete();
      }
    }

    void complete() {
      completionQueue.offer(new Date());
    }

    void waitFor() {
      try {
        final Date completed = completionQueue.take();
        logger.info("Pump completed: " + completed);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
