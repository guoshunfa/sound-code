package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BASE64EncoderStream extends FilterOutputStream {
   private byte[] buffer;
   private int bufsize;
   private int count;
   private int bytesPerLine;
   private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

   public BASE64EncoderStream(OutputStream out, int bytesPerLine) {
      super(out);
      this.bufsize = 0;
      this.count = 0;
      this.buffer = new byte[3];
      this.bytesPerLine = bytesPerLine;
   }

   public BASE64EncoderStream(OutputStream out) {
      this(out, 76);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         this.write(b[off + i]);
      }

   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(int c) throws IOException {
      this.buffer[this.bufsize++] = (byte)c;
      if (this.bufsize == 3) {
         this.encode();
         this.bufsize = 0;
      }

   }

   public void flush() throws IOException {
      if (this.bufsize > 0) {
         this.encode();
         this.bufsize = 0;
      }

      this.out.flush();
   }

   public void close() throws IOException {
      this.flush();
      this.out.close();
   }

   private void encode() throws IOException {
      if (this.count + 4 > this.bytesPerLine) {
         this.out.write(13);
         this.out.write(10);
         this.count = 0;
      }

      byte a;
      if (this.bufsize == 1) {
         a = this.buffer[0];
         byte b = 0;
         byte c = false;
         this.out.write(pem_array[a >>> 2 & 63]);
         this.out.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
         this.out.write(61);
         this.out.write(61);
      } else {
         byte b;
         if (this.bufsize == 2) {
            a = this.buffer[0];
            b = this.buffer[1];
            byte c = 0;
            this.out.write(pem_array[a >>> 2 & 63]);
            this.out.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
            this.out.write(pem_array[(b << 2 & 60) + (c >>> 6 & 3)]);
            this.out.write(61);
         } else {
            a = this.buffer[0];
            b = this.buffer[1];
            byte c = this.buffer[2];
            this.out.write(pem_array[a >>> 2 & 63]);
            this.out.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
            this.out.write(pem_array[(b << 2 & 60) + (c >>> 6 & 3)]);
            this.out.write(pem_array[c & 63]);
         }
      }

      this.count += 4;
   }

   public static byte[] encode(byte[] inbuf) {
      if (inbuf.length == 0) {
         return inbuf;
      } else {
         byte[] outbuf = new byte[(inbuf.length + 2) / 3 * 4];
         int inpos = 0;
         int outpos = 0;

         for(int size = inbuf.length; size > 0; size -= 3) {
            byte a;
            if (size == 1) {
               a = inbuf[inpos++];
               byte b = 0;
               byte c = false;
               outbuf[outpos++] = (byte)pem_array[a >>> 2 & 63];
               outbuf[outpos++] = (byte)pem_array[(a << 4 & 48) + (b >>> 4 & 15)];
               outbuf[outpos++] = 61;
               outbuf[outpos++] = 61;
            } else {
               byte b;
               if (size == 2) {
                  a = inbuf[inpos++];
                  b = inbuf[inpos++];
                  byte c = 0;
                  outbuf[outpos++] = (byte)pem_array[a >>> 2 & 63];
                  outbuf[outpos++] = (byte)pem_array[(a << 4 & 48) + (b >>> 4 & 15)];
                  outbuf[outpos++] = (byte)pem_array[(b << 2 & 60) + (c >>> 6 & 3)];
                  outbuf[outpos++] = 61;
               } else {
                  a = inbuf[inpos++];
                  b = inbuf[inpos++];
                  byte c = inbuf[inpos++];
                  outbuf[outpos++] = (byte)pem_array[a >>> 2 & 63];
                  outbuf[outpos++] = (byte)pem_array[(a << 4 & 48) + (b >>> 4 & 15)];
                  outbuf[outpos++] = (byte)pem_array[(b << 2 & 60) + (c >>> 6 & 3)];
                  outbuf[outpos++] = (byte)pem_array[c & 63];
               }
            }
         }

         return outbuf;
      }
   }
}
