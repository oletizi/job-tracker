package com.orionletizi.job.exec;

public class ExecutionResult {

  private String outLog;
  private String errLog;
  private int status;
  private Throwable throwable;


  void setStatus(int status) {
    this.status = status;
  }

  void setThrowable(Throwable t) {
    this.throwable = t;
  }

  void setStdoutName(String outLog) {
    this.outLog = outLog;
  }

  void setStderrName(String errLog) {
    this.errLog = errLog;
  }

  public String getOutLog() {
    return outLog;
  }

  public String getErrLog() {
    return errLog;
  }

  public int getStatus() {
    return status;
  }

  public Throwable getThrowable() {
    return throwable;
  }

}
