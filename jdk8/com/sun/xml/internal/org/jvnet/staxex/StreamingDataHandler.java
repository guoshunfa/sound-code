package com.sun.xml.internal.org.jvnet.staxex;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public abstract class StreamingDataHandler extends DataHandler implements Closeable {
   public StreamingDataHandler(Object o, String s) {
      super(o, s);
   }

   public StreamingDataHandler(URL url) {
      super(url);
   }

   public StreamingDataHandler(DataSource dataSource) {
      super(dataSource);
   }

   public abstract InputStream readOnce() throws IOException;

   public abstract void moveTo(File var1) throws IOException;

   public abstract void close() throws IOException;
}
