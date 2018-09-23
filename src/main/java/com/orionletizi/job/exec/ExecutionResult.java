package com.orionletizi.job.exec;

public class ExecutionResult {

  private int status;
  private Throwable throwable;


  void setStatus(int status) {
    this.status = status;
  }

  void setThrowable(Throwable t) {
    this.throwable = t;
  }

  public int getStatus() {
    return status;
  }

  public Throwable getThrowable() {
    return throwable;
  }

}
