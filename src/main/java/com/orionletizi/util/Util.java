package com.orionletizi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@SuppressWarnings("WeakerAccess")
public class Util {
  public static final DateFormat DF_ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
  static {
    DF_ISO.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
}
