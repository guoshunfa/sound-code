package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;

public class JAXMStreamSource extends StreamSource {
   InputStream in;
   Reader reader;
   private static final boolean lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");

   public JAXMStreamSource(InputStream is) throws IOException {
      if (lazyContentLength) {
         this.in = is;
      } else if (is instanceof ByteInputStream) {
         this.in = (ByteInputStream)is;
      } else {
         ByteOutputStream bout = new ByteOutputStream();
         bout.write(is);
         this.in = bout.newInputStream();
      }

   }

   public JAXMStreamSource(Reader rdr) throws IOException {
      if (lazyContentLength) {
         this.reader = rdr;
      } else {
         CharWriter cout = new CharWriter();
         char[] temp = new char[1024];

         int len;
         while(-1 != (len = rdr.read(temp))) {
            cout.write(temp, 0, len);
         }

         this.reader = new CharReader(cout.getChars(), cout.getCount());
      }
   }

   public InputStream getInputStream() {
      return this.in;
   }

   public Reader getReader() {
      return this.reader;
   }

   public void reset() throws IOException {
      if (this.in != null) {
         this.in.reset();
      }

      if (this.reader != null) {
         this.reader.reset();
      }

   }
}
