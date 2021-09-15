package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class Base64Data extends Pcdata {
   private DataHandler dataHandler;
   private byte[] data;
   private int dataLen;
   @Nullable
   private String mimeType;

   public void set(byte[] data, int len, @Nullable String mimeType) {
      this.data = data;
      this.dataLen = len;
      this.dataHandler = null;
      this.mimeType = mimeType;
   }

   public void set(byte[] data, @Nullable String mimeType) {
      this.set(data, data.length, mimeType);
   }

   public void set(DataHandler data) {
      assert data != null;

      this.dataHandler = data;
      this.data = null;
   }

   public DataHandler getDataHandler() {
      if (this.dataHandler == null) {
         this.dataHandler = new DataHandler(new DataSource() {
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
         });
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
         } catch (IOException var3) {
            this.dataLen = 0;
         }
      }

      return this.data;
   }

   public int getDataLen() {
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
         return DatatypeConverterImpl.encode(this.data[base] >> 2);
      case 1:
         if (base + 1 < this.dataLen) {
            b1 = this.data[base + 1];
         } else {
            b1 = 0;
         }

         return DatatypeConverterImpl.encode((this.data[base] & 3) << 4 | b1 >> 4 & 15);
      case 2:
         if (base + 1 < this.dataLen) {
            b1 = this.data[base + 1];
            byte b2;
            if (base + 2 < this.dataLen) {
               b2 = this.data[base + 2];
            } else {
               b2 = 0;
            }

            return DatatypeConverterImpl.encode((b1 & 15) << 2 | b2 >> 6 & 3);
         }

         return '=';
      case 3:
         if (base + 2 < this.dataLen) {
            return DatatypeConverterImpl.encode(this.data[base + 2] & 63);
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
      return DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen);
   }

   public void writeTo(char[] buf, int start) {
      this.get();
      DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen, (char[])buf, start);
   }

   public void writeTo(UTF8XmlOutput output) throws IOException {
      this.get();
      output.text(this.data, this.dataLen);
   }

   public void writeTo(XMLStreamWriter output) throws IOException, XMLStreamException {
      this.get();
      DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen, output);
   }
}
