package com.orionletizi.job.task;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AbstractTaskTest {

  @Test
  public void testBasics() throws IOException {
    String name = "my name";
    final TestTask task = new TestTask(name);

    assertEquals(name, task.getName());

    // test to make sure that the output resources get closed
    final TaskLogger logger = mock(TaskLogger.class);
    task.setLogger(logger);

    task.completed();
    verify(logger).close();
  }

  static final class TestTask extends AbstractTask {

    TestTask(final String name) {
      super(name);
    }

    @Override
    public void run() {
      // noop
    }
  }
}