package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BASE64DecoderStream extends FilterInputStream {
   private byte[] buffer = new byte[3];
   private int bufsize = 0;
   private int index = 0;
   private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static final byte[] pem_convert_array = new byte[256];
   private byte[] decode_buffer = new byte[4];

   public BASE64DecoderStream(InputStream in) {
      super(in);
   }

   public int read() throws IOException {
      if (this.index >= this.bufsize) {
         this.decode();
         if (this.bufsize == 0) {
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

   private void decode() throws IOException {
      this.bufsize = 0;
      int got = 0;

      while(true) {
         int i;
         do {
            if (got >= 4) {
               byte a = pem_convert_array[this.decode_buffer[0] & 255];
               byte b = pem_convert_array[this.decode_buffer[1] & 255];
               this.buffer[this.bufsize++] = (byte)(a << 2 & 252 | b >>> 4 & 3);
               if (this.decode_buffer[2] == 61) {
                  return;
               }

               a = b;
               b = pem_convert_array[this.decode_buffer[2] & 255];
               this.buffer[this.bufsize++] = (byte)(a << 4 & 240 | b >>> 2 & 15);
               if (this.decode_buffer[3] == 61) {
                  return;
               }

               a = b;
               b = pem_convert_array[this.decode_buffer[3] & 255];
               this.buffer[this.bufsize++] = (byte)(a << 6 & 192 | b & 63);
               return;
            }

            i = this.in.read();
            if (i == -1) {
               if (got == 0) {
                  return;
               }

               throw new IOException("Error in encoded stream, got " + got);
            }
         } while((i < 0 || i >= 256 || i != 61) && pem_convert_array[i] == -1);

         this.decode_buffer[got++] = (byte)i;
      }
   }

   public static byte[] decode(byte[] inbuf) {
      int size = inbuf.length / 4 * 3;
      if (size == 0) {
         return inbuf;
      } else {
         if (inbuf[inbuf.length - 1] == 61) {
            --size;
            if (inbuf[inbuf.length - 2] == 61) {
               --size;
            }
         }

         byte[] outbuf = new byte[size];
         int inpos = 0;
         int outpos = 0;

         for(size = inbuf.length; size > 0; size -= 4) {
            byte a = pem_convert_array[inbuf[inpos++] & 255];
            byte b = pem_convert_array[inbuf[inpos++] & 255];
            outbuf[outpos++] = (byte)(a << 2 & 252 | b >>> 4 & 3);
            if (inbuf[inpos] == 61) {
               return outbuf;
            }

            a = b;
            b = pem_convert_array[inbuf[inpos++] & 255];
            outbuf[outpos++] = (byte)(a << 4 & 240 | b >>> 2 & 15);
            if (inbuf[inpos] == 61) {
               return outbuf;
            }

            a = b;
            b = pem_convert_array[inbuf[inpos++] & 255];
            outbuf[outpos++] = (byte)(a << 6 & 192 | b & 63);
         }

         return outbuf;
      }
   }

   static {
      int i;
      for(i = 0; i < 255; ++i) {
         pem_convert_array[i] = -1;
      }

      for(i = 0; i < pem_array.length; ++i) {
         pem_convert_array[pem_array[i]] = (byte)i;
      }

   }
}
