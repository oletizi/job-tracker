package com.orionletizi.job.exec;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
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
  public void testBasics() throws Exception {

    final Executable executable = mock(Executable.class);
    final ExecutionContext ctxt = mock(ExecutionContext.class);
    final ExecutionResult result = mock(ExecutionResult.class);
    when(ctxt.getExecutable()).thenReturn(executable);

    // Set up the executable mock to invoke the notifyComplete callback
    doAnswer((invocation) -> {
      final CompletionListener listener = invocation.getArgumentAt(0, CompletionListener.class);
      listener.notifyComplete(result);
      return null;
    }).when(executable).onCompletion(any(CompletionListener.class));

    engine.run(ctxt);

    verify(executable, times(1)).run();
    verify(executable, times(1)).onCompletion(any(CompletionListener.class));
    verify(executable, times(1)).setLogger(any(ExecutableLogger.class));
    verify(ctxt, times(1)).setStderrName(anyString());
    verify(ctxt, times(1)).notifyComplete(result);
  }


  @Test
  public void integrationTest() throws Exception{

    final ExecutionResult result = new ExecutionResult();
    final BlockingQueue<ExecutionResult> completionQueue = new LinkedBlockingQueue();
    final CompletionListener listener = result1 -> completionQueue.offer(result1);


    final Executable executable = new Executable() {
      private ExecutableLogger logger;
      private CompletionListener listener;

      @Override
      public void onCompletion(CompletionListener listener) {
        this.listener = listener;
      }

      @Override
      public void setLogger(ExecutableLogger logger) {
        this.logger = logger;
      }

      @Override
      public void run() {
        logger.info("Info");
        logger.warning("Warning");
        logger.error(new RuntimeException("I'm an error!"));
        listener.notifyComplete(result);
      }
    };


    final ExecutionContext ctxt = new ExecutionContext("my-id", executable);
    ctxt.onCompletion(listener);
    engine.run(ctxt);

    final ExecutionResult actualResult = completionQueue.take();

    assertEquals(result, actualResult);

    final File file = new File(tmpDir, ctxt.getStderrName());
    assertTrue(file.isFile());

    System.err.println(FileUtils.readFileToString(file, "UTF-8"));
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