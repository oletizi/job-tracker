package com.orionletizi.job.task.task;

import com.orionletizi.job.lifecycle.LifecycleListener;
import com.orionletizi.job.task.Command;
import com.orionletizi.job.task.ExecutionContext;
import com.orionletizi.job.task.ProcessExecutionEngine;
import logging.LoggerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

import java.io.File;
import java.util.concurrent.BlockingQueue;
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
    final Command command = new Command(new String[]{
        "echo", echoString
    });

    final ExecutionContext ctxt = new ExecutionContext("my id", command);

    final BlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();

    command.onLifecycleEvent(new LifecycleListener() {
      @Override
      public void started() {

      }

      @Override
      public void completed() {
        completionQueue.offer(new Object());
      }

      @Override
      public void error(Throwable t) {
      }
    });

    engine.execute(ctxt);
    final Object poll = completionQueue.poll(10, TimeUnit.SECONDS);
    assertNotNull(poll);
    final String stderrName = ctxt.getStderrName();
    assertNotNull(stderrName);
  }

  @Test
  public void testError() throws Exception {
    final ExecutionContext ctxt = new ExecutionContext("my id", new Command(new String[]{
        "ls", "/no/such/file/exists/on/this/filesystem"
    }));

    engine.execute(ctxt);

    final LinkedBlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();


  }

}