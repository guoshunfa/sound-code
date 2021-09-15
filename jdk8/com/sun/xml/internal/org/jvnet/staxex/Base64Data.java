package com.sun.xml.internal.org.jvnet.staxex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Base64Data implements CharSequence, Cloneable {
   private DataHandler dataHandler;
   private byte[] data;
   private int dataLen;
   private boolean dataCloneByRef;
   private String mimeType;
   private static final Logger logger = Logger.getLogger(Base64Data.class.getName());
   private static final int CHUNK_SIZE;

   public Base64Data() {
   }

   public Base64Data(Base64Data that) {
      that.get();
      if (that.dataCloneByRef) {
         this.data = that.data;
      } else {
         this.data = new byte[that.dataLen];
         System.arraycopy(that.data, 0, this.data, 0, that.dataLen);
      }

      this.dataCloneByRef = true;
      this.dataLen = that.dataLen;
      this.dataHandler = null;
      this.mimeType = that.mimeType;
   }

   public void set(byte[] data, int len, String mimeType, boolean cloneByRef) {
      this.data = data;
      this.dataLen = len;
      this.dataCloneByRef = cloneByRef;
      this.dataHandler = null;
      this.mimeType = mimeType;
   }

   public void set(byte[] data, int len, String mimeType) {
      this.set(data, len, mimeType, false);
   }

   public void set(byte[] data, String mimeType) {
      this.set(data, data.length, mimeType, false);
   }

   public void set(DataHandler data) {
      assert data != null;

      this.dataHandler = data;
      this.data = null;
   }

   public DataHandler getDataHandler() {
      if (this.dataHandler == null) {
         this.dataHandler = new Base64Data.Base64StreamingDataHandler(new Base64Data.Base64DataSource());
      } else if (!(this.dataHandler instanceof StreamingDataHandler)) {
         this.dataHandler = new Base64Data.FilterDataHandler(this.dataHandler);
      }

      return this.dataHandler;
   }

   public byte[] getExact() {
      this.get();
      if (this.dataLen != this.data.length) {
         byte[] buf = new byte[this.dataLen];
         System.arraycopy(this.data, 0, buf, 0, this.dataLen);
         this.data = buf;
      }

      return this.data;
   }

   public InputStream getInputStream() throws IOException {
      return (InputStream)(this.dataHandler != null ? this.dataHandler.getInputStream() : new ByteArrayInputStream(this.data, 0, this.dataLen));
   }

   public boolean hasData() {
      return this.data != null;
   }

   public byte[] get() {
      if (this.data == null) {
         try {
            ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx(1024);
            InputStream is = this.dataHandler.getDataSource().getInputStream();
            baos.readFrom(is);
            is.close();
            this.data = baos.getBuffer();
            this.dataLen = baos.size();
            this.dataCloneByRef = true;
         } catch (IOException var3) {
            this.dataLen = 0;
         }
      }

      return this.data;
   }

   public int getDataLen() {
      this.get();
      return this.dataLen;
   }

   public String getMimeType() {
      return this.mimeType == null ? "application/octet-stream" : this.mimeType;
   }

   public int length() {
      this.get();
      return (this.dataLen + 2) / 3 * 4;
   }

   public char charAt(int index) {
      int offset = index % 4;
      int base = index / 4 * 3;
      byte b1;
      switch(offset) {
      case 0:
         return Base64Encoder.encode(this.data[base] >> 2);
      case 1:
         if (base + 1 < this.dataLen) {
            b1 = this.data[base + 1];
         } else {
            b1 = 0;
         }

         return Base64Encoder.encode((this.data[base] & 3) << 4 | b1 >> 4 & 15);
      case 2:
         if (base + 1 < this.dataLen) {
            b1 = this.data[base + 1];
            byte b2;
            if (base + 2 < this.dataLen) {
               b2 = this.data[base + 2];
            } else {
               b2 = 0;
            }

            return Base64Encoder.encode((b1 & 15) << 2 | b2 >> 6 & 3);
         }

         return '=';
      case 3:
         if (base + 2 < this.dataLen) {
            return Base64Encoder.encode(this.data[base + 2] & 63);
         }

         return '=';
      default:
         throw new IllegalStateException();
      }
   }

   public CharSequence subSequence(int start, int end) {
      StringBuilder buf = new StringBuilder();
      this.get();

      for(int i = start; i < end; ++i) {
         buf.append(this.charAt(i));
      }

      return buf;
   }

   public String toString() {
      this.get();
      return Base64Encoder.print(this.data, 0, this.dataLen);
   }

   public void writeTo(char[] buf, int start) {
      this.get();
      Base64Encoder.print(this.data, 0, this.dataLen, buf, start);
   }

   public void writeTo(XMLStreamWriter output) throws IOException, XMLStreamException {
      if (this.data == null) {
         try {
            InputStream is = this.dataHandler.getDataSource().getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Base64EncoderStream encWriter = new Base64EncoderStream(output, outStream);
            byte[] buffer = new byte[CHUNK_SIZE];

            int b;
            while((b = is.read(buffer)) != -1) {
               encWriter.write(buffer, 0, b);
            }

            outStream.close();
            encWriter.close();
         } catch (IOException var7) {
            this.dataLen = 0;
            throw var7;
         }
      } else {
         String s = Base64Encoder.print(this.data, 0, this.dataLen);
         output.writeCharacters(s);
      }

   }

   public Base64Data clone() {
      try {
         Base64Data clone = (Base64Data)super.clone();
         clone.get();
         if (clone.dataCloneByRef) {
            this.data = clone.data;
         } else {
            this.data = new byte[clone.dataLen];
            System.arraycopy(clone.data, 0, this.data, 0, clone.dataLen);
         }

         this.dataCloneByRef = true;
         this.dataLen = clone.dataLen;
         this.dataHandler = null;
         this.mimeType = clone.mimeType;
         return clone;
      } catch (CloneNotSupportedException var2) {
         Logger.getLogger(Base64Data.class.getName()).log(Level.SEVERE, (String)null, (Throwable)var2);
         return null;
      }
   }

   static String getProperty(final String propName) {
      return System.getSecurityManager() == null ? System.getProperty(propName) : (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(propName);
         }
      });
   }

   static {
      int bufSize = 1024;

      try {
         String bufSizeStr = getProperty("com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize");
         if (bufSizeStr != null) {
            bufSize = Integer.parseInt(bufSizeStr);
         }
      } catch (Exception var2) {
         logger.log(Level.INFO, (String)"Error reading com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize property", (Throwable)var2);
      }

      CHUNK_SIZE = bufSize;
   }

   private static final class FilterDataHandler extends StreamingDataHandler {
      FilterDataHandler(DataHandler dh) {
         super(dh.getDataSource());
      }

      public InputStream readOnce() throws IOException {
         return this.getDataSource().getInputStream();
      }

      public void moveTo(File dst) throws IOException {
         byte[] buf = new byte[8192];
         InputStream in = null;
         FileOutputStream out = null;

         try {
            in = this.getDataSource().getInputStream();
            out = new FileOutputStream(dst);

            while(true) {
               int amountRead = in.read(buf);
               if (amountRead == -1) {
                  return;
               }

               out.write(buf, 0, amountRead);
            }
         } finally {
            if (in != null) {
               try {
                  in.close();
               } catch (IOException var14) {
               }
            }

            if (out != null) {
               try {
                  out.close();
               } catch (IOException var13) {
               }
            }

         }
      }

      public void close() throws IOException {
      }
   }

   private final class Base64StreamingDataHandler extends StreamingDataHandler {
      Base64StreamingDataHandler(DataSource source) {
         super(source);
      }

      public InputStream readOnce() throws IOException {
         return this.getDataSource().getInputStream();
      }

      public void moveTo(File dst) throws IOException {
         FileOutputStream fout = new FileOutputStream(dst);

         try {
            fout.write(Base64Data.this.data, 0, Base64Data.this.dataLen);
         } finally {
            fout.close();
         }

      }

      public void close() throws IOException {
      }
   }

   private final class Base64DataSource implements DataSource {
      private Base64DataSource() {
      }

      public String getContentType() {
         return Base64Data.this.getMimeType();
      }

      public InputStream getInputStream() {
         return new ByteArrayInputStream(Base64Data.this.data, 0, Base64Data.this.dataLen);
      }

      public String getName() {
         return null;
      }

      public OutputStream getOutputStream() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      Base64DataSource(Object x1) {
         this();
      }
   }
}
