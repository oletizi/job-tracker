package com.orionletizi.job.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import logging.LoggerFactory;

import java.util.logging.Logger;
@SuppressWarnings({"unused", "WeakerAccess"})
public class ExecutionContext {
  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionContext.class);

  @JsonProperty
  private final String id;
  private final Command command;
  private final Task task;
  private String stdoutName;
  private String stderrName;

  public ExecutionContext(final String id, final Command command) {
    this.id = id;
    this.command = command;
    this.task = null;
  }

  public ExecutionContext(final String id, final Task task) {
    this.id = id;
    this.task = task;
    this.command = null;
  }

  @JsonProperty
  public String getId() {
    return id;
  }

  @JsonProperty
  public Command getCommand() {
    return command;
  }

  @JsonProperty
  public Task getTask() {
    return task;
  }

  @JsonProperty
  public String getStdoutName() {
    return stdoutName;
  }

  @JsonProperty
  public String getStderrName() {
    return stderrName;
  }


  void setStdoutName(String stdoutName) {
    this.stdoutName = stdoutName;
  }

  void setStderrName(String stderrName) {
    this.stderrName = stderrName;
  }

}
