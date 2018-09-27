package com.orionletizi.job.task;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AbstractTaskTest {

  private String name;
  private TestTask task;
  private TaskLogger logger;

  @Before
  public void before() throws Exception {
    name = "my name";
    task = new TestTask(name);
    logger = mock(TaskLogger.class);
    task.setLogger(logger);

  }

  @Test
  public void testSuccess() throws IOException {

    assertEquals(name, task.getName());

    // test to make sure that the output resources get closed

    task.completed();
    verify(logger).close();

  }


  @Test
  public void testError() throws IOException {
    task.error(new RuntimeException());
    verify(logger).close();
  }

  static final class TestTask extends AbstractTask {

    private TaskLogger logger;

    TestTask(final String name) {
      super(name);
    }

    @Override
    TaskLogger getLogger() {
      return logger;
    }

    @Override
    public void run() {
      // noop
    }

    @Override
    public void setLogger(TaskLogger logger) {
      this.logger = logger;
    }
  }
}