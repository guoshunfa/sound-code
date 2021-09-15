package com.sun.xml.internal.ws.encoding;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class StringDataContentHandler implements DataContentHandler {
   private static final ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, "text/plain", "Text String");

   protected ActivationDataFlavor getDF() {
      return myDF;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{this.getDF()};
   }

   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
      return this.getDF().equals(df) ? this.getContent(ds) : null;
   }

   public Object getContent(DataSource ds) throws IOException {
      String enc = null;

      InputStreamReader is;
      try {
         enc = this.getCharset(ds.getContentType());
         is = new InputStreamReader(ds.getInputStream(), enc);
      } catch (IllegalArgumentException var16) {
         throw new UnsupportedEncodingException(enc);
      }

      try {
         int pos = 0;
         char[] buf = new char[1024];

         int count;
         while((count = is.read(buf, pos, buf.length - pos)) != -1) {
            pos += count;
            if (pos >= buf.length) {
               int size = buf.length;
               if (size < 262144) {
                  size += size;
               } else {
                  size += 262144;
               }

               char[] tbuf = new char[size];
               System.arraycopy(buf, 0, tbuf, 0, pos);
               buf = tbuf;
            }
         }

         String var18 = new String(buf, 0, pos);
         return var18;
      } finally {
         try {
            is.close();
         } catch (IOException var15) {
         }

      }
   }

   public void writeTo(Object obj, String type, OutputStream os) throws IOException {
      if (!(obj instanceof String)) {
         throw new IOException("\"" + this.getDF().getMimeType() + "\" DataContentHandler requires String object, was given object of type " + obj.getClass().toString());
      } else {
         String enc = null;

         OutputStreamWriter osw;
         try {
            enc = this.getCharset(type);
            osw = new OutputStreamWriter(os, enc);
         } catch (IllegalArgumentException var7) {
            throw new UnsupportedEncodingException(enc);
         }

         String s = (String)obj;
         osw.write((String)s, 0, s.length());
         osw.flush();
      }
   }

   private String getCharset(String type) {
      try {
         ContentType ct = new ContentType(type);
         String charset = ct.getParameter("charset");
         if (charset == null) {
            charset = "us-ascii";
         }

         return Charset.forName(charset).name();
      } catch (Exception var4) {
         return null;
      }
   }
}
