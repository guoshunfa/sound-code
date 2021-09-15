package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;

public final class RuntimeVersion {
   public static final Version VERSION;

   public String getVersion() {
      return VERSION.toString();
   }

   static {
      Version version = null;
      InputStream in = RuntimeVersion.class.getResourceAsStream("version.properties");

      try {
         version = Version.create(in);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException var8) {
            }
         }

      }

      VERSION = version == null ? Version.create((InputStream)null) : version;
   }
}
