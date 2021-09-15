package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MIMEPartStreamingDataHandler extends StreamingDataHandler {
   private final MIMEPartStreamingDataHandler.StreamingDataSource ds = (MIMEPartStreamingDataHandler.StreamingDataSource)this.getDataSource();

   public MIMEPartStreamingDataHandler(MIMEPart part) {
      super((DataSource)(new MIMEPartStreamingDataHandler.StreamingDataSource(part)));
   }

   public InputStream readOnce() throws IOException {
      return this.ds.readOnce();
   }

   public void moveTo(File file) throws IOException {
      this.ds.moveTo(file);
   }

   public void close() throws IOException {
      this.ds.close();
   }

   private static final class MyIOException extends IOException {
      private final Exception linkedException;

      MyIOException(Exception linkedException) {
         this.linkedException = linkedException;
      }

      public Throwable getCause() {
         return this.linkedException;
      }
   }

   private static final class StreamingDataSource implements DataSource {
      private final MIMEPart part;

      StreamingDataSource(MIMEPart part) {
         this.part = part;
      }

      public InputStream getInputStream() throws IOException {
         return this.part.read();
      }

      InputStream readOnce() throws IOException {
         try {
            return this.part.readOnce();
         } catch (Exception var2) {
            throw new MIMEPartStreamingDataHandler.MyIOException(var2);
         }
      }

      void moveTo(File file) throws IOException {
         this.part.moveTo(file);
      }

      public OutputStream getOutputStream() throws IOException {
         return null;
      }

      public String getContentType() {
         return this.part.getContentType();
      }

      public String getName() {
         return "";
      }

      public void close() throws IOException {
         this.part.close();
      }
   }
}
