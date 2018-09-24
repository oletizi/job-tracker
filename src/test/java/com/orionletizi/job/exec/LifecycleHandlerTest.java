package com.orionletizi.job.exec;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LifecycleHandlerTest {

  @Test
  public void testBasics() {
    final Status status = mock(Status.class);
    final LifecycleHandler handler = new LifecycleHandler(status);

    final ExecutionResult result = mock(ExecutionResult.class);
    final CompletionListener listener1 = mock(CompletionListener.class);
    final CompletionListener listener2 = mock(CompletionListener.class);

    // add listener1 twice
    handler.onCompletion(listener1);
    handler.onCompletion(listener1);

    // verify notifyComplete hasn't been called yet
    verify(listener1, times(0)).notifyComplete(any(ExecutionResult.class));
    verify(listener2, times(0)).notifyComplete(any(ExecutionResult.class));

    // call notifyComplete on the handler
    handler.notifyComplete(result);

    // verify the status object got updated
    verify(status, times(1)).stop();

    // verify listener1 has been notified once
    // verify listener2 has not been notified
    verify(listener1, times(1)).notifyComplete(result);
    verify(listener2, times(0)).notifyComplete(result);

    // add listener2 and verify that listener1 is not notified again, but listener2 is notified, even when registered after completion
    handler.onCompletion(listener2);
    verify(listener1, times(1)).notifyComplete(result);
    verify(listener2, times(1)).notifyComplete(result);
  }

}