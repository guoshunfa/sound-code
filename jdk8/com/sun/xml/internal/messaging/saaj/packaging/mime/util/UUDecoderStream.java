package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUDecoderStream extends FilterInputStream {
   private String name;
   private int mode;
   private byte[] buffer;
   private int bufsize = 0;
   private int index = 0;
   private boolean gotPrefix = false;
   private boolean gotEnd = false;
   private LineInputStream lin;

   public UUDecoderStream(InputStream in) {
      super(in);
      this.lin = new LineInputStream(in);
      this.buffer = new byte[45];
   }

   public int read() throws IOException {
      if (this.index >= this.bufsize) {
         this.readPrefix();
         if (!this.decode()) {
            return -1;
         }

         this.index = 0;
      }

      return this.buffer[this.index++] & 255;
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      int i;
      for(i = 0; i < len; ++i) {
         int c;
         if ((c = this.read()) == -1) {
            if (i == 0) {
               i = -1;
            }
            break;
         }

         buf[off + i] = (byte)c;
      }

      return i;
   }

   public boolean markSupported() {
      return false;
   }

   public int available() throws IOException {
      return this.in.available() * 3 / 4 + (this.bufsize - this.index);
   }

   public String getName() throws IOException {
      this.readPrefix();
      return this.name;
   }

   public int getMode() throws IOException {
      this.readPrefix();
      return this.mode;
   }

   private void readPrefix() throws IOException {
      if (!this.gotPrefix) {
         String s;
         do {
            s = this.lin.readLine();
            if (s == null) {
               throw new IOException("UUDecoder error: No Begin");
            }
         } while(!s.regionMatches(true, 0, "begin", 0, 5));

         try {
            this.mode = Integer.parseInt(s.substring(6, 9));
         } catch (NumberFormatException var3) {
            throw new IOException("UUDecoder error: " + var3.toString());
         }

         this.name = s.substring(10);
         this.gotPrefix = true;
      }
   }

   private boolean decode() throws IOException {
      if (this.gotEnd) {
         return false;
      } else {
         this.bufsize = 0;

         String line;
         do {
            line = this.lin.readLine();
            if (line == null) {
               throw new IOException("Missing End");
            }

            if (line.regionMatches(true, 0, "end", 0, 3)) {
               this.gotEnd = true;
               return false;
            }
         } while(line.length() == 0);

         int count = line.charAt(0);
         if (count < ' ') {
            throw new IOException("Buffer format error");
         } else {
            int count = count - 32 & 63;
            if (count == 0) {
               line = this.lin.readLine();
               if (line != null && line.regionMatches(true, 0, "end", 0, 3)) {
                  this.gotEnd = true;
                  return false;
               } else {
                  throw new IOException("Missing End");
               }
            } else {
               int need = (count * 8 + 5) / 6;
               if (line.length() < need + 1) {
                  throw new IOException("Short buffer error");
               } else {
                  int var4 = 1;

                  while(this.bufsize < count) {
                     byte a = (byte)(line.charAt(var4++) - 32 & 63);
                     byte b = (byte)(line.charAt(var4++) - 32 & 63);
                     this.buffer[this.bufsize++] = (byte)(a << 2 & 252 | b >>> 4 & 3);
                     if (this.bufsize < count) {
                        a = b;
                        b = (byte)(line.charAt(var4++) - 32 & 63);
                        this.buffer[this.bufsize++] = (byte)(a << 4 & 240 | b >>> 2 & 15);
                     }

                     if (this.bufsize < count) {
                        a = b;
                        b = (byte)(line.charAt(var4++) - 32 & 63);
                        this.buffer[this.bufsize++] = (byte)(a << 6 & 192 | b & 63);
                     }
                  }

                  return true;
               }
            }
         }
      }
   }
}
