package com.sun.xml.internal.ws.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
   public static InputStream hasSomeData(InputStream in) {
      if (in != null) {
         try {
            if (((InputStream)in).available() < 1) {
               if (!((InputStream)in).markSupported()) {
                  in = new BufferedInputStream((InputStream)in);
               }

               ((InputStream)in).mark(1);
               if (((InputStream)in).read() != -1) {
                  ((InputStream)in).reset();
               } else {
                  in = null;
               }
            }
         } catch (IOException var2) {
            in = null;
         }
      }

      return (InputStream)in;
   }
}
