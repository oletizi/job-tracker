package com.orionletizi.job.exec.exec;

import logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ExecutionContext {
  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionContext.class);
  private final String id;
  private final String[] command;
  private String stdoutName;
  private String stderrName;
  private Collection<CompletionListener> listeners = new ArrayList<>();
  private ExecutionResult result;

  ExecutionContext(final String id, final String[] command) {
    this.id = id;
    this.command = command;
  }

  public String getId() {
    return id;
  }

  public String[] getCommand() {
    return command;
  }

  public synchronized void notifyComplete(final ExecutionResult result) throws InterruptedException {
    logger.info("notifyComplete(result: " + result + ")");
    if (this.result != null) {
      throw new RuntimeException("Attempt to notify complete more than once!");
    }
    this.result = result;
    for (CompletionListener listener : listeners) {
      listener.notifyComplete(result);
    }
  }


  public synchronized void listenForCompletion(CompletionListener listener) {
    if (result != null) {
      // we're already complete
      listener.notifyComplete(result);
    } else {
      // still waiting for completion.
      listeners.add(listener);
    }
  }

  public String getStdoutName() {
    return stdoutName;
  }

  public ExecutionContext setStdoutName(String stdoutName) {
    this.stdoutName = stdoutName;
    return this;
  }

  public String getStderrName() {
    return stderrName;
  }

  public ExecutionContext setStderrName(String stderrName) {
    this.stderrName = stderrName;
    return this;
  }

}
