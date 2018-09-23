package com.orionletizi.job.exec.exec;

import logging.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ExecutionContext {
  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionContext.class);
  private final String id;
  private final String[] command;
  private String outLogName;
  private String errLogName;
  private final BlockingQueue<ExecutionResult> completionQueue = new LinkedBlockingQueue();

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

  public ExecutionResult waitFor() throws InterruptedException {
    return completionQueue.take();
  }

  public void notifyComplete(final ExecutionResult result) throws InterruptedException {
    logger.info("notifyComplete(result: " + result + ")");
    completionQueue.put(result);
  }

  void addStdout(String outLogName) {
    this.outLogName = outLogName;
  }

  void addStderr(String errLogName) {
    this.errLogName = errLogName;
  }
}
