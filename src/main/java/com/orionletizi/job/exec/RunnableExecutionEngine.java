package com.orionletizi.job.exec;

import java.io.*;
import java.util.concurrent.ExecutorService;

public abstract class RunnableExecutionEngine implements ExecutionEngine {

  private File tmpDir;
  private ExecutorService executorService;

  RunnableExecutionEngine(File tmpDir, final ExecutorService executorService) {
    this.tmpDir = tmpDir;
    this.executorService = executorService;
  }

  @Override
  public void run(ExecutionContext ctxt) {
    try {
      final File errfile = new File(tmpDir, ctxt.getId() + "-stderr");
      final BufferedWriter err = new BufferedWriter(new FileWriter(errfile));

      ctxt.setStderrName(errfile.getName());

      final Executable executable = ctxt.getExecutable();
      executable.setLogger(new ExecutableLogger(executable, err));

      executable.onCompletion(result -> {
        // close writers
        try {
          err.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        // notify the context that it is complete.
        ctxt.notifyComplete(result);
      });
      executorService.submit(executable);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
