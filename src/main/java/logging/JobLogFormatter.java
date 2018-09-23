package logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JobLogFormatter extends Formatter {
  private static final DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
  private static final String NL = System.getProperty("line.separator");

  @Override
  public String format(final LogRecord record) {
    final StringBuilder rv = new StringBuilder(df.format(new Date()))
        .append("| ").append(StringUtils.rightPad(record.getLevel().toString(), 7))
        .append("| ").append(StringUtils.rightPad(StringUtils.abbreviateMiddle(record.getLoggerName(), "...", 20), 20))
        .append("| ");
    boolean firstLine = true;
    final String message = record.getMessage();
    if (message != null) {
      for (String line : message.split(NL)) {
        line = line.replaceAll("\t", "    ");
        if (!firstLine) {
          rv.append(NL);
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
      rv.append(NL).append(ExceptionUtils.getStackTrace(record.getThrown()));
    }
    rv.append(NL);
    return rv.toString();
  }
}
