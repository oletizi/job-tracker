package com.orionletizi.job.exec.exec;

import com.orionletizi.job.exec.CompletionListener;
import com.orionletizi.job.exec.ExecutionContext;
import com.orionletizi.job.exec.ExecutionResult;
import logging.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ExecutionContextTest {

  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionContextTest.class);
  private ExecutionResult result;


  @Before
  public void before() {
    result = mock(ExecutionResult.class);
  }

  @Test
  public void testBasics() throws Exception {

    final String[] command = {"echo", "Hello, world!"};
    final ExecutionContext ctxt = new ExecutionContext("my id", command);

    final LinkedBlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();

    final CompletionListener listener = result -> {
      try {
        completionQueue.put(result);
      } catch (InterruptedException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    };

    ctxt.onCompletion(listener);
    ctxt.notifyComplete(result);

    // check to see if the completion listeners are notified
    assertEquals(1, completionQueue.size());
    assertEquals(result, completionQueue.take());

    // check to see if new listeners added after completion are notified
    ctxt.onCompletion(listener);
    assertEquals(1, completionQueue.size());
    assertEquals(result, completionQueue.take());
  }

}