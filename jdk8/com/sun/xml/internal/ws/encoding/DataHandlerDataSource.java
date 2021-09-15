package com.sun.xml.internal.ws.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class DataHandlerDataSource implements DataSource {
   private final DataHandler dataHandler;

   public DataHandlerDataSource(DataHandler dh) {
      this.dataHandler = dh;
   }

   public InputStream getInputStream() throws IOException {
      return this.dataHandler.getInputStream();
   }

   public OutputStream getOutputStream() throws IOException {
      return this.dataHandler.getOutputStream();
   }

   public String getContentType() {
      return this.dataHandler.getContentType();
   }

   public String getName() {
      return this.dataHandler.getName();
   }
}
