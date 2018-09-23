package com.orionletizi.job.exec.exec;

import com.orionletizi.job.Job;

public interface ExecutionEngine {

  void execute(ExecutionContext ctxt) throws InterruptedException;

}
