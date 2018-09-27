package com.orionletizi.job.task;

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
  public void run(final ExecutionContext ctxt) {
    try {
      final File errfile = new File(tmpDir, ctxt.getId() + "-stderr");
      final BufferedWriter err = new BufferedWriter(new FileWriter(errfile));

      ctxt.setStderrName(errfile.getName());

      final Task task = ctxt.getTask();
      task.setLogger(new TaskLogger(task, err));

      executorService.submit(task);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
