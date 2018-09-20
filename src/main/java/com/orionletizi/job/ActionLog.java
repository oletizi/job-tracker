package com.orionletizi.job;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActionLog {
  private static final DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SS");
  private final CircularFifoQueue<ActionLogEntry> entries = new CircularFifoQueue<>(8);
  private final String name;
  private final PrintStream out;
  private final PrintStream err;

  public ActionLog(final String name) {
    this(name, System.out, System.err);
  }

  private ActionLog(final String name, final PrintStream out, final PrintStream err) {
    this.name = name;
    this.out = out;
    this.err = err;
  }

  public ActionLog info(final Object msg) {
    //System.out.println(df.format(new Date()) +":INFO:" + PublisherService.class.getSimpleName() + ": " + msg);
    println(err, "INFO", msg);
    return this;
  }

  public void warn(Object msg) {
    println(err, "WARN", msg);
  }

  public ActionLog error(final Object msg) {
    println(err, "ERROR", msg);
    return this;
  }

  public ActionLog log(final String action, final String status, final String message) {
    final String date = df.format(new Date());
    info(date + ": action: " + action + ", status: " + status + ", message: " + message);
    entries.add(new ActionLogEntry(date, action, status, message));
    return this;
  }

  public List<ActionLogEntry> getEntries() {
    return new ArrayList<>(entries);
  }

  public String format(final Date date) {
    return df.format(date);
  }


  private void println(final PrintStream stream, final String level, final Object msg) {
    stream.println(df.format(new Date()) + ":" + level + ":" + name + ": " + msg);
  }


}
