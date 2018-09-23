package com.orionletizi.job.exec;

@SuppressWarnings("WeakerAccess")
public class ExecutionResult {

  private int status;
  private Throwable throwable;

  public void setStatus(int status) {
    this.status = status;
  }

  public void setThrowable(Throwable t) {
    this.throwable = t;
  }

  public int getStatus() {
    return status;
  }

  public Throwable getThrowable() {
    return throwable;
  }

}
