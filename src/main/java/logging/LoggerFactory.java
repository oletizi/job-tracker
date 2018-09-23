package logging;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {
  private static final Map<String, Logger> loggers = new HashMap<>();
  private static ConsoleHandler logHandler;

  static {
    logHandler = new ConsoleHandler();
    final Formatter logFormatter = new JobLogFormatter();
    logHandler.setFormatter(logFormatter);
  }

  public Logger getLoggerFor(final Class clazz) {
    return getLoggerFor(clazz.getSimpleName());
  }

  @SuppressWarnings("WeakerAccess")
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
