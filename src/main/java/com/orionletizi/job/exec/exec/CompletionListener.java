package com.orionletizi.job.exec.exec;

import com.orionletizi.job.exec.exec.ExecutionResult;

public interface CompletionListener {
  void notifyComplete(ExecutionResult result);
}
