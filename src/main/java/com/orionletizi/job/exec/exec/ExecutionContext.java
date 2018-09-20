package com.orionletizi.job.exec.exec;

import com.orionletizi.job.Job;

public interface ExecutionContext {

  ExecutionResult execute(Job job, String pathToExecutable);
}
