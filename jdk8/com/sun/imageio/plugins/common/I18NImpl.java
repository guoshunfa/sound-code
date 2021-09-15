package com.sun.imageio.plugins.common;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

public class I18NImpl {
   protected static final String getString(String var0, String var1, String var2) {
      PropertyResourceBundle var3 = null;

      try {
         InputStream var4 = Class.forName(var0).getResourceAsStream(var1);
         var3 = new PropertyResourceBundle(var4);
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }

      return (String)var3.handleGetObject(var2);
   }
}
