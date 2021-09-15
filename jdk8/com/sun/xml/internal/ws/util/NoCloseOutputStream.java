package com.sun.xml.internal.ws.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NoCloseOutputStream extends FilterOutputStream {
   public NoCloseOutputStream(OutputStream out) {
      super(out);
   }

   public void close() throws IOException {
   }

   public void doClose() throws IOException {
      super.close();
   }
}
