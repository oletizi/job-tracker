package com.orionletizi.job.exec.exec;

import com.orionletizi.job.exec.ExecutionContext;
import com.orionletizi.job.exec.ExecutionResult;
import com.orionletizi.job.exec.ProcessExecutionEngine;
import logging.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ProcessExecutionEngineTest {

  private static final Logger logger = new LoggerFactory().getLoggerFor(ProcessExecutionEngineTest.class);

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  @Rule
  public Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

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

    ctxt.onCompletion(result -> {
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
        assertEquals(echoString + "\n", stdout);
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

  @Test
  public void testError() throws Exception {
    final ExecutionContext ctxt = new ExecutionContext("my id", new String[]{
        "ls", "/no/such/file/exists/on/this/filesystem"
    });

    engine.execute(ctxt);

    final LinkedBlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();

    ctxt.onCompletion(result -> {
      Object completion = null;
      try {
        final int status = result.getStatus();
        logger.info("Status: " + status);
        assertNotEquals(0, status);

        final String stderrName = result.getErrLog();
        final File stderrFile = new File(this.logFolder, stderrName);
        logger.info("stderrFile: " + stderrFile);
        logger.info("stderrFile.isFile(): " + stderrFile.isFile());
        assertTrue(stderrFile.isFile());
        assertFalse(stderrFile.isDirectory());

        logger.info("Reading stderr...");
        final BufferedReader in = new BufferedReader(new FileReader(stderrFile));
        final String stderr = in.readLine();
        assertNotNull("stderr should not be null.", stderr);
        logger.info("Done reading stderr.");
        logger.info("stderr: " + stderr);
        logger.info("Putting result in completion queue...");
        completion = result;
      } catch (Throwable e) {
        logger.info("Yikes! Failure: " + e.getMessage());
        e.printStackTrace();
        logger.info("Trying to put error message into completion queue...");
        //completionQueue.put(e);
        completion = e;
        logger.info("Done putting error message into completion queue.");
      } finally {
        try {
          logger.info("Adding completion to completion queue: " + completion);
          completionQueue.put(completion);
        } catch (InterruptedException e1) {
          logger.warning("Barfed adding to completion queue.");
          e1.printStackTrace();
        }
      }
    });

    logger.info("Checking the completion queue...");

    final Object result = completionQueue.take();
    if (!(result instanceof ExecutionResult)) {
      fail("" + result);
    }


  }

}