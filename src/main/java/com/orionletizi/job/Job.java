package com.orionletizi.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orionletizi.job.exec.ExecutionContext;
import logging.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Job {
  private static final Logger logger = new LoggerFactory().getLoggerFor(Job.class);
  private final CountDownLatch closeLatch = new CountDownLatch(1);
  private static final DateTimeFormatter df = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  private String id;
  private String startTime = timestamp();
  private String stopTime = "UNKNOWN";
  private String action;
  private String status = "in progress";
  private Map<String, String> stdout = new HashMap<>();
  private Map<String, String> stderr = new HashMap<>();
  private List<ActionLogEntry> log = new ArrayList<>();
  private boolean isOpen = true;
  private final Collection<ExecutionContext> executionContexts = new ArrayList<>();

  @SuppressWarnings("unused")
  Job() {
    // noop for object mapper
  }

  Job(final String id, final String action) {
    this.id = id;
    this.action = action;
  }

  /** Properties **/

  @SuppressWarnings("WeakerAccess")
  @JsonProperty
  public String getId() {
    return id;
  }

  @JsonProperty
  public String getAction() {
    return action;
  }

  @JsonProperty
  public synchronized List<ExecutionContext> getExecutionLog() {
    return new ArrayList<>(executionContexts);
  }

  public void addExecutionContext(ExecutionContext ctxt) {
    executionContexts.add(ctxt);
  }

  @JsonProperty
  public synchronized String getStatus() {
    return status;
  }

  @JsonProperty
  public String getStartTime() {
    return startTime;
  }

  @JsonProperty
  public String getStopTime() {
    return stopTime;
  }

  /** Logic **/

  synchronized void close(final String status) {
    logger.info("Closing job: " + id);
    isOpen = false;
    this.status = status;
    closeLatch.countDown();
    logger.info("Done closing job: " + id);
    stopTime = timestamp();
  }

  private String timestamp() {
    return df.format(OffsetDateTime.now());
  }

  synchronized void waitUntilClosed() throws InterruptedException {
    logger.info("Waiting until job is closed: " + id + "...");
    closeLatch.await();
    logger.info("Done waiting for job close: " + id);
  }

  private void checkOpen() {
    if (! isOpen){
      throw new IllegalStateException("Attempt to modify a closed job");
    }
  }

}
