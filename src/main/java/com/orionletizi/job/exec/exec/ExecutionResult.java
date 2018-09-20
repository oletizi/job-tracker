package com.orionletizi.job.exec.exec;

public class ExecutionResult {

  private String outLog;
  private String errLog;
  private int status;
  private Exception exception;


  void setStatus(int status) {
    this.status = status;
  }

  void setException(Exception e) {
    this.exception = e;
  }

  void setOutLog(String outLog) {
    this.outLog = outLog;
  }

  void setErrLog(String errLog) {
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

  public Exception getException() {
    return exception;
  }

}
