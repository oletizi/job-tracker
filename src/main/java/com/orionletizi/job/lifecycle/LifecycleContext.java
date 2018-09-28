package com.orionletizi.job.lifecycle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.orionletizi.util.Util.DF_ISO;

@SuppressWarnings("unused")
public class LifecycleContext implements LifecycleListener {

  private static final ExecutorService executor = Executors.newSingleThreadExecutor();
  private String status = "INIT";
  private final Collection<LifecycleListener> listeners = new ArrayList<>();
  private Throwable throwable;
  private Date startTime;
  private Date stopTime;
  private boolean isComplete;

  public synchronized void onLifecycleEvent(final LifecycleListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public synchronized void started() {
    startTime = new Date();
    status = "IN-PROGRESS";
    executor.submit(() -> {
      for (LifecycleListener listener : listeners) {
        listener.started();
      }
    });
  }

  @Override
  public synchronized void completed() {
    stopTime = new Date();
    status = "COMPLETE:SUCCESS";
    verifyAndSetComplete();
    executor.submit(() -> {
      for (LifecycleListener listener : listeners) {
        listener.completed();
      }
    });
  }

  @Override
  public synchronized void error(final Throwable t) {
    stopTime = new Date();
    status = "COMPLETE:ERROR";
    verifyAndSetComplete();
    this.throwable = t;
    executor.submit(() -> {
      for (LifecycleListener listener : listeners) {
        listener.error(t);
      }
    });
  }

  private void verifyAndSetComplete() {
    if (isComplete) {
      throw new RuntimeException("Already completed!");
    }
    isComplete = true;
  }

  @JsonProperty
  public synchronized String getStatus() {
    return status;
  }

  @JsonProperty
  public synchronized String getStartTime() {
    return formatDate(startTime);
  }

  @JsonProperty
  public synchronized String getStopTime() {
    return formatDate(stopTime);
  }

  @JsonProperty
  public synchronized String getError() {
    final StringBuilder msg = new StringBuilder();
    Throwable t = throwable;
    while (t != null) {
      msg.append(t.getClass().getSimpleName());
      if (t.getMessage() != null) {
        msg.append(": " + t.getMessage());
      }
      final Throwable cause = t.getCause();
      if (cause != null) {
        msg.append("; Caused by: ");
      }
      t = cause;
    }
    return msg.toString();
  }

  @JsonProperty
  public synchronized boolean hasError() {
    return throwable != null;
  }

  @JsonProperty
  public synchronized boolean isComplete() {
    return status.startsWith("COMPLETE");
  }

  @JsonIgnore
  public synchronized Throwable getThrowable() {
    return throwable;
  }

  private static String formatDate(final Date date) {
    return date != null ? DF_ISO.format(date) : "UNKNOWN";
  }
}
