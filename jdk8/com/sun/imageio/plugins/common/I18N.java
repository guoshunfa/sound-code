package com.sun.imageio.plugins.common;

public final class I18N extends I18NImpl {
   private static final String resource_name = "iio-plugin.properties";

   public static String getString(String var0) {
      return getString("com.sun.imageio.plugins.common.I18N", "iio-plugin.properties", var0);
   }
}
