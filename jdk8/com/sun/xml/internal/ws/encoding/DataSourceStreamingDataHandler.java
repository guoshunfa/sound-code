package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;

public class DataSourceStreamingDataHandler extends StreamingDataHandler {
   public DataSourceStreamingDataHandler(DataSource ds) {
      super(ds);
   }

   public InputStream readOnce() throws IOException {
      return this.getInputStream();
   }

   public void moveTo(File file) throws IOException {
      InputStream in = this.getInputStream();
      FileOutputStream os = new FileOutputStream(file);

      try {
         byte[] temp = new byte[8192];

         int len;
         while((len = in.read(temp)) != -1) {
            os.write(temp, 0, len);
         }

         in.close();
      } finally {
         if (os != null) {
            os.close();
         }

      }
   }

   public void close() throws IOException {
   }
}
