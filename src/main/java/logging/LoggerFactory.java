package logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class LoggerFactory {
  private static final Map<String, Logger> loggers = new HashMap<>();
  private static final DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
  private static ConsoleHandler logHandler;

  static {
    logHandler = new ConsoleHandler();
    logHandler.setFormatter(new Formatter() {
      @Override
      public String format(LogRecord record) {
        final StringBuilder rv = new StringBuilder(df.format(new Date()))
            .append("| ").append(StringUtils.rightPad(record.getLevel().toString(), 7))
            .append("| ").append(StringUtils.rightPad(StringUtils.abbreviateMiddle(record.getLoggerName(), "...", 20), 20))
            .append("| ");
        boolean firstLine = true;
        final String message = record.getMessage();
        if (message != null) {
          for (String line : message.split("\n")) {
            line = line.replaceAll("\t", "    ");
            if (!firstLine) {
              rv.append("\n");
              for (int i = 0; i < 50; i++) {
                rv.append(" ");
              }
              rv.append("| ");
            }
            rv.append(line);
            firstLine = false;
          }
        }

        if (record.getThrown() != null) {
          rv.append("\n").append(ExceptionUtils.getStackTrace(record.getThrown()));
        }
        rv.append("\n");
        return rv.toString();
      }
    });
  }

  public Logger getLoggerFor(final Class clazz) {
    return getLoggerFor(clazz.getSimpleName());
  }

  public Logger getLoggerFor(final String name) {
    synchronized (loggers) {
      Logger logger = loggers.get(name);
      if (logger == null) {
        logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.addHandler(logHandler);
        logger.setLevel(Level.INFO);
      }
      return logger;
    }
  }
}
