package com.orionletizi.job.exec;

public interface ExecutionEngine {

  void execute(ExecutionContext ctxt);
  void run(ExecutionContext ctxt);

}