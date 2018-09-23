package com.orionletizi.job.exec.exec;

import com.orionletizi.job.Job;
import logging.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ProcessExecutionEngine implements ExecutionEngine {

  private static final Logger logger = new LoggerFactory().getLoggerFor(ProcessExecutionEngine.class);
  private static final int STATUS_BARF = -1 * (0xba * 0xf);
  private static final ExecutorService IO_PUMP = Executors.newFixedThreadPool(4);
  private final File tmpDir;

  public ProcessExecutionEngine(final File tmpDir) {

    this.tmpDir = tmpDir;
  }

  @Override
  public void execute(final ExecutionContext ctxt) throws InterruptedException {
    final ExecutionResult result = new ExecutionResult();

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

      result.setOutLog(outLogName);
      ctxt.addStdout(outLogName);

      result.setErrLog(errLogName);
      ctxt.addStderr(errLogName);

      logger.info("Starting process for: " + ctxt);
      final Process proc = builder.start();

      final PipedOutputStream outSink = new PipedOutputStream();
      final PipedOutputStream errSink = new PipedOutputStream();


      // Tee output and error streams
      logger.info("Setting up output handling...");
      final TeeOutputStream out = new TeeOutputStream(System.out, outSink);
      final TeeOutputStream err = new TeeOutputStream(System.err, errSink);



      IO_PUMP.submit(new StreamSink(new PipedInputStream(outSink), outLog));
      IO_PUMP.submit(new StreamSink(new PipedInputStream(errSink), errLog));
      IO_PUMP.submit(new StreamPump(proc.getInputStream(), out));
      IO_PUMP.submit(new StreamPump(proc.getErrorStream(), err));


      logger.info("Waiting for process: " + ctxt);
      final int status = proc.waitFor();
      logger.info("Process complete. Status: " + status);

      result.setStatus(status);

    } catch (IOException | InterruptedException e) {
      result.setStatus(STATUS_BARF);
      result.setException(e);
      e.printStackTrace();
    }
    logger.info("Return result: " + result);
    ctxt.notifyComplete(result);
  }

  private static class StreamPump implements Runnable {

    private final InputStream in;
    private final OutputStream out;

    StreamPump(final InputStream in, final OutputStream out) {
      this.in = in;
      this.out = out;
    }

    @Override
    public void run() {
      try {
        IOUtils.copy(in, out);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class StreamSink implements Runnable {

    private final InputStream in;
    private File outFile;

    StreamSink(final InputStream in, final File outFile) {
      this.in = in;
      this.outFile = outFile;
    }

    @Override
    public void run() {
      try {
        final FileOutputStream out = new FileOutputStream(outFile);
        IOUtils.copy(in, out);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
