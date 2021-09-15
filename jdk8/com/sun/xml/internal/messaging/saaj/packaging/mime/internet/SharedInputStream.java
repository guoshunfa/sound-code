package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.io.InputStream;
import java.io.OutputStream;

public interface SharedInputStream {
   long getPosition();

   InputStream newStream(long var1, long var3);

   void writeTo(long var1, long var3, OutputStream var5);
}
