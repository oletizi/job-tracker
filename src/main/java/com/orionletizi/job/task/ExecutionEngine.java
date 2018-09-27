package com.orionletizi.job.task;

public interface ExecutionEngine {

  void execute(ExecutionContext ctxt);
  void run(ExecutionContext ctxt);

}
