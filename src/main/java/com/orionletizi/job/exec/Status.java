package com.orionletizi.job.exec;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import static com.orionletizi.job.com.orionletizi.util.Util.DF_ISO;

@SuppressWarnings("unused")
public class Status {
  private Date startTime;
  private Date stopTime;

  public synchronized void start() {
    this.startTime = new Date();
  }

  public synchronized void stop() {
    this.stopTime = new Date();
  }

  @JsonProperty
  public String getStartTime() {
    return startTime != null ? DF_ISO.format(startTime) : "UNKNOWN";
  }

  @JsonProperty
  public String getStopTime() {
    return stopTime != null ? DF_ISO.format(stopTime) : "UNKNOWN";
  }

  @JsonProperty
  public String getStatus() {
    return stopTime == null ? "IN-PROGRESS" : "COMPLETE";
  }
}
