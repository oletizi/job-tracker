package com.orionletizi.job.task;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static junit.framework.TestCase.assertTrue;

import static org.mockito.Mockito.*;

public class RunnableExecutionEngineTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File tmpDir;
  private ExecutorService executorService;
  private TestableRunnableExecutionEngine engine;

  @Before
  public void before() throws Exception {
    tmpDir = tmp.newFolder();
    executorService = Executors.newSingleThreadExecutor();
    engine = new TestableRunnableExecutionEngine(tmpDir, executorService);
  }

  @Test
  public void testBasics() throws Exception{

    final BlockingQueue<Object> completionQueue = new LinkedBlockingQueue<>();

    final Task task = mock(Task.class);
    doAnswer(invocationOnMock -> completionQueue.offer(new Object())).when(task).run();

    final ExecutionContext ctxt = new ExecutionContext("my-id", task);

    engine.run(ctxt);

    // block until run() is called
    completionQueue.take();

    verify(task, times(1)).run();
  }

  static class TestableRunnableExecutionEngine extends RunnableExecutionEngine {

    TestableRunnableExecutionEngine(File tmpDir, ExecutorService executorService) {
      super(tmpDir, executorService);
    }

    @Override
    public void execute(ExecutionContext ctxt) {
      throw new RuntimeException("Don't test me.");
    }
  }

}