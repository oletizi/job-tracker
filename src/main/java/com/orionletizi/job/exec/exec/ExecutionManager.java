package com.orionletizi.job.exec.exec;

import com.orionletizi.job.Job;
import logging.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ExecutionManager {
  private static final Logger logger = new LoggerFactory().getLoggerFor(ExecutionManager.class);
  private static final long CREATE_TIME = System.currentTimeMillis();
  private static final AtomicLong SEQUENCE = new AtomicLong();
  private final Map<String, ExecutionContext> contextsById = new HashMap<>();
  private final ExecutionEngine engine;

  public ExecutionManager(final ExecutionEngine engine) {
    this.engine = engine;
  }

  public ExecutionContext execute(final Job job, final String[] command) {
    logger.info("execute(job: " + job + ", command: " + ArrayUtils.toString(command) + ")");
    final ExecutionContext ctxt = new ExecutionContext(nextId(), command);
    synchronized (contextsById) {
      contextsById.put(ctxt.getId(), ctxt);
    }
    job.addExecutionContext(ctxt);
    try {
      engine.execute(ctxt);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return ctxt;
  }

  private String nextId() {
    return CREATE_TIME + "-" + SEQUENCE + "-" + System.currentTimeMillis();
  }

  public ExecutionResult waitFor(String id) throws InterruptedException {
    synchronized (contextsById) {
      final ExecutionContext ctxt = contextsById.get(id);
      ExecutionResult rv = null;
      if (ctxt != null) {
        rv = ctxt.waitFor();
      }
      return rv;
    }
  }
}