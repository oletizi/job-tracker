package com.orionletizi.job.exec;

import com.orionletizi.job.exec.ExecutionResult;

public interface CompletionListener {
  void notifyComplete(ExecutionResult result);
}
