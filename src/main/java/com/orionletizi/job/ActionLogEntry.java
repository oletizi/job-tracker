package com.orionletizi.job;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionLogEntry {
  @JsonProperty
  private String date;
  @JsonProperty
  private String action;
  @JsonProperty
  private String status;
  @JsonProperty
  private String message;

  @SuppressWarnings("unused")
  public ActionLogEntry() {
    // noop for object mapper
  }

  public ActionLogEntry(final String date, final String action, final String status, final String message) {
    this.date = date;
    this.action = action;
    this.status = status;
    this.message = message;
  }

  public String getDate() {
    return date;
  }

  public String getAction() {
    return action;
  }

  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
