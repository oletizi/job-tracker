package com.orionletizi.job.lifecycle;

public interface LifecycleListener {

  void started();
  void completed();
  void error(Throwable t);

}
