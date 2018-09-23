package com.orionletizi.job.exec.exec;

import com.orionletizi.job.Job;
import logging.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExecutionManagerTest {

  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionManagerTest.class);

  private ExecutionManager manager;
  private ExecutionResult result;

  @Before
  public void before() throws InterruptedException {
    ExecutionEngine engine = mock(ExecutionEngine.class);
    manager = new ExecutionManager(engine);
    result = mock(ExecutionResult.class);
    doAnswer(invocation -> {
      final Object[] arguments = invocation.getArguments();
      final ExecutionContext ctxt = (ExecutionContext) arguments[0];
      logger.info("Notifying complete: " + ctxt);
      ctxt.notifyComplete(result);
      return null;
    }).when(engine).execute(any(ExecutionContext.class));
  }

  @Test
  public void testBasics() throws Exception {
    final Job job = mock(Job.class);
    final String[] command = {"echo", "Hello, world!"};
    final ExecutionContext ctxt = manager.execute(job, command);
    assertNotNull(ctxt);

    final BlockingQueue<ExecutionResult> completionQueue = new LinkedBlockingQueue<>();

    final CompletionListener listener = result -> {
      try {
        completionQueue.put(result);
      } catch (InterruptedException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    };

    ctxt.listenForCompletion(listener);
    assertEquals(1, completionQueue.size());
    verify(job, times(1)).addExecutionContext(ctxt);

    // check to see if new listeners added after completion are notified
    ctxt.listenForCompletion(listener);
    assertEquals(2, completionQueue.size());
  }

}