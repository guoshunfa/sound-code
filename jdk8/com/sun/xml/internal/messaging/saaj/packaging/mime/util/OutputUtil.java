package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract class OutputUtil {
   private static byte[] newline = new byte[]{13, 10};

   public static void writeln(String s, OutputStream out) throws IOException {
      writeAsAscii(s, out);
      writeln(out);
   }

   public static void writeAsAscii(String s, OutputStream out) throws IOException {
      int len = s.length();

      for(int i = 0; i < len; ++i) {
         out.write((byte)s.charAt(i));
      }

   }

   public static void writeln(OutputStream out) throws IOException {
      out.write(newline);
   }
}
