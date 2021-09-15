package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public final class UTF8OutputStreamWriter extends Writer {
   OutputStream out;
   int lastUTF16CodePoint = 0;

   public UTF8OutputStreamWriter(OutputStream out) {
      this.out = out;
   }

   public String getEncoding() {
      return "UTF-8";
   }

   public void write(int c) throws IOException {
      if (this.lastUTF16CodePoint != 0) {
         int uc = ((this.lastUTF16CodePoint & 1023) << 10 | c & 1023) + 65536;
         if (uc >= 0 && uc < 2097152) {
            this.out.write(240 | uc >> 18);
            this.out.write(128 | uc >> 12 & 63);
            this.out.write(128 | uc >> 6 & 63);
            this.out.write(128 | uc & 63);
            this.lastUTF16CodePoint = 0;
         } else {
            throw new IOException("Atttempting to write invalid Unicode code point '" + uc + "'");
         }
      } else {
         if (c < 128) {
            this.out.write(c);
         } else if (c < 2048) {
            this.out.write(192 | c >> 6);
            this.out.write(128 | c & 63);
         } else if (c <= 65535) {
            if (!XMLChar.isHighSurrogate(c) && !XMLChar.isLowSurrogate(c)) {
               this.out.write(224 | c >> 12);
               this.out.write(128 | c >> 6 & 63);
               this.out.write(128 | c & 63);
            } else {
               this.lastUTF16CodePoint = c;
            }
         }

      }
   }

   public void write(char[] cbuf) throws IOException {
      for(int i = 0; i < cbuf.length; ++i) {
         this.write(cbuf[i]);
      }

   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         this.write(cbuf[off + i]);
      }

   }

   public void write(String str) throws IOException {
      int len = str.length();

      for(int i = 0; i < len; ++i) {
         this.write(str.charAt(i));
      }

   }

   public void write(String str, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         this.write(str.charAt(off + i));
      }

   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      if (this.lastUTF16CodePoint != 0) {
         throw new IllegalStateException("Attempting to close a UTF8OutputStreamWriter while awaiting for a UTF-16 code unit");
      } else {
         this.out.close();
      }
   }
}
