package com.orionletizi.job.lifecycle;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import static com.orionletizi.util.Util.DF_ISO;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Status {
  private Date startTime;
  private Date stopTime;
  private String status = "INIT";
  private boolean isStopped = false;

  public synchronized void start() {
    this.status = "IN-PROGRESS";
    this.startTime = new Date();
  }

  public synchronized void stop(final String status) {
    if (isStopped) {
      throw new RuntimeException("Attempt to stop an already completed status.");
    }
    isStopped = true;
    this.stopTime = new Date();
    this.status = status;
  }

  @JsonProperty
  public synchronized String getStartTime() {
    return startTime != null ? DF_ISO.format(startTime) : "UNKNOWN";
  }

  @JsonProperty
  public synchronized String getStopTime() {
    return stopTime != null ? DF_ISO.format(stopTime) : "UNKNOWN";
  }

  @JsonProperty
  public synchronized String getStatus() {
    return status;
  }
}
