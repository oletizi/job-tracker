package com.orionletizi.job.task;

import logging.JobLogFormatter;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@SuppressWarnings("WeakerAccess")
public class TaskLogger {
  private final JobLogFormatter formatter = new JobLogFormatter();
  private final Task task;
  private final Writer out;

  public TaskLogger(final Task task, final Writer out) {
    this.task = task;
    this.out = out;
  }

  @SuppressWarnings("unused")
  public void info(final Object msg) {
    write(new LogRecord(Level.INFO, "" + msg));
  }

  @SuppressWarnings("unused")
  public void warning(final Object msg) {
    write(new LogRecord(Level.WARNING, "" + msg));
  }

  @SuppressWarnings("unused")
  public void error(final Throwable t) {
    final LogRecord record = new LogRecord(Level.SEVERE, t.getMessage());
    record.setThrown(t);
    write(record);
  }

  private void write(final LogRecord record) {
    try {
      record.setLoggerName(task.getClass().getSimpleName());
      final String msg = formatter.format(record);
      out.write(msg);
      System.err.print(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() throws IOException {
    out.close();
  }
}
