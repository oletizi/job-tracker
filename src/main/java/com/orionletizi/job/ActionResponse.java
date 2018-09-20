package com.orionletizi.job;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionResponse {
  private String jobId;
  private String status;
  private String message;

  ActionResponse(String jobId, String status, String message) {
    this.jobId = jobId;
    this.status = status;
    this.message = message;
  }

  ActionResponse() {
    // noop for object mapper
  }

  @JsonProperty
  public String getJobId() {
    return jobId;
  }

  @JsonProperty
  public String getStatus() {
    return status;
  }

  @JsonProperty
  public String getMessage() {
    return message;
  }
}
