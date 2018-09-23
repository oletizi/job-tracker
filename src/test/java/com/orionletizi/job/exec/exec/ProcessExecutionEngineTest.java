package com.orionletizi.job.exec.exec;

import logging.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ProcessExecutionEngineTest {

  private static final Logger logger = new LoggerFactory().getLoggerFor(ProcessExecutionEngineTest.class);

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File logFolder;
  private ProcessExecutionEngine engine;

  @Before
  public void before() throws Exception {
    logFolder = tmp.newFolder();
    engine = new ProcessExecutionEngine(logFolder, Executors.newSingleThreadExecutor());
  }

  @Test
  public void testSuccess() throws Exception {

    final String echoString = "Hello, world!";
    final ExecutionContext ctxt = new ExecutionContext("my id", new String[]{
        "echo", echoString
    });

    engine.execute(ctxt);

    final LinkedBlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();

    ctxt.listenForCompletion(result -> {
      try {
        final int status = result.getStatus();
        logger.info("Status: " + status);
        assertEquals(0, status);

        final String stdoutName = result.getOutLog();
        final File stdoutFile = new File(logFolder, stdoutName);
        logger.info("stdout: " + stdoutFile);
        assertTrue(stdoutFile.isFile());

        final String stdout = FileUtils.readFileToString(stdoutFile, "UTF-8");
        logger.info("stdout: " + stdout);
        assertEquals(echoString, stdout);
        completionQueue.put(result);
      } catch (Throwable e) {
        e.printStackTrace();
        try {
          completionQueue.put(e);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });

    final Object result = completionQueue.take();
    if (! (result instanceof  ExecutionResult)) {
      fail("" + result);
    }
  }

  @Test
  public void testError() throws Exception {
    final ExecutionContext ctxt = new ExecutionContext("my id", new String[]{
        "ls", "/no/such/file/exists/on/this/filesystem"
    });

    engine.execute(ctxt);

    final LinkedBlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();

    ctxt.listenForCompletion(result -> {
      try {
        final int status = result.getStatus();
        logger.info("Status: " + status);
        assertNotEquals(0, status);

        final String stderrName = result.getErrLog();
        final File stderrFile = new File(this.logFolder, stderrName);
        logger.info("stderrFile: " + stderrFile);
        assertTrue(stderrFile.isFile());

        final String stderr;

        stderr = FileUtils.readFileToString(stderrFile, "UTF-8");
        logger.info("stderr: " + stderr);
        logger.info("Putting result in completion queue...");
        completionQueue.put(result);
      } catch (Throwable e) {
        e.printStackTrace();
        try {
          completionQueue.put(e);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });

    final Object result = completionQueue.take();
    if (!(result instanceof ExecutionResult)) {
      fail("" + result);
    }


  }

}