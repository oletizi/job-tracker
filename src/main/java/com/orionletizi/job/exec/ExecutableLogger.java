package com.orionletizi.job.exec;

import logging.JobLogFormatter;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@SuppressWarnings("WeakerAccess")
public final class ExecutableLogger {
  private final JobLogFormatter formatter = new JobLogFormatter();
  private final Executable executable;
  private final Writer out;

  public ExecutableLogger(final Executable executable, final Writer out) {
    this.executable = executable;
    this.out = out;
  }

  public void info(final Object msg) {
    write(new LogRecord(Level.INFO, "" + msg));
  }

  public void warning(final Object msg) {
    write(new LogRecord(Level.WARNING, "" + msg));
  }

  public void error(final Throwable t) {
    final LogRecord record = new LogRecord(Level.SEVERE, t.getMessage());
    record.setThrown(t);
    write(record);
  }

  private void write(final LogRecord record) {
    try {
      record.setLoggerName(executable.getClass().getSimpleName());
      final String msg = formatter.format(record);
      out.write(msg);
      System.err.print(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
