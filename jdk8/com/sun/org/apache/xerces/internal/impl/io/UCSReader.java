package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class UCSReader extends Reader {
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   public static final short UCS2LE = 1;
   public static final short UCS2BE = 2;
   public static final short UCS4LE = 4;
   public static final short UCS4BE = 8;
   protected InputStream fInputStream;
   protected byte[] fBuffer;
   protected short fEncoding;

   public UCSReader(InputStream inputStream, short encoding) {
      this(inputStream, 8192, encoding);
   }

   public UCSReader(InputStream inputStream, int size, short encoding) {
      this.fInputStream = inputStream;
      BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
      this.fBuffer = ba.getByteBuffer(size);
      if (this.fBuffer == null) {
         this.fBuffer = new byte[size];
      }

      this.fEncoding = encoding;
   }

   public int read() throws IOException {
      int b0 = this.fInputStream.read() & 255;
      if (b0 == 255) {
         return -1;
      } else {
         int b1 = this.fInputStream.read() & 255;
         if (b1 == 255) {
            return -1;
         } else if (this.fEncoding >= 4) {
            int b2 = this.fInputStream.read() & 255;
            if (b2 == 255) {
               return -1;
            } else {
               int b3 = this.fInputStream.read() & 255;
               if (b3 == 255) {
                  return -1;
               } else {
                  System.err.println("b0 is " + (b0 & 255) + " b1 " + (b1 & 255) + " b2 " + (b2 & 255) + " b3 " + (b3 & 255));
                  return this.fEncoding == 8 ? (b0 << 24) + (b1 << 16) + (b2 << 8) + b3 : (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
               }
            }
         } else {
            return this.fEncoding == 2 ? (b0 << 8) + b1 : (b1 << 8) + b0;
         }
      }
   }

   public int read(char[] ch, int offset, int length) throws IOException {
      int byteLength = length << (this.fEncoding >= 4 ? 2 : 1);
      if (byteLength > this.fBuffer.length) {
         byteLength = this.fBuffer.length;
      }

      int count = this.fInputStream.read(this.fBuffer, 0, byteLength);
      if (count == -1) {
         return -1;
      } else {
         int numToRead;
         int i;
         int i;
         int b0;
         if (this.fEncoding >= 4) {
            numToRead = 4 - (count & 3) & 3;

            label63:
            for(i = 0; i < numToRead; ++i) {
               i = this.fInputStream.read();
               if (i == -1) {
                  b0 = i;

                  while(true) {
                     if (b0 >= numToRead) {
                        break label63;
                     }

                     this.fBuffer[count + b0] = 0;
                     ++b0;
                  }
               }

               this.fBuffer[count + i] = (byte)i;
            }

            count += numToRead;
         } else {
            numToRead = count & 1;
            if (numToRead != 0) {
               ++count;
               i = this.fInputStream.read();
               if (i == -1) {
                  this.fBuffer[count] = 0;
               } else {
                  this.fBuffer[count] = (byte)i;
               }
            }
         }

         numToRead = count >> (this.fEncoding >= 4 ? 2 : 1);
         i = 0;

         for(i = 0; i < numToRead; ++i) {
            b0 = this.fBuffer[i++] & 255;
            int b1 = this.fBuffer[i++] & 255;
            if (this.fEncoding >= 4) {
               int b2 = this.fBuffer[i++] & 255;
               int b3 = this.fBuffer[i++] & 255;
               if (this.fEncoding == 8) {
                  ch[offset + i] = (char)((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
               } else {
                  ch[offset + i] = (char)((b3 << 24) + (b2 << 16) + (b1 << 8) + b0);
               }
            } else if (this.fEncoding == 2) {
               ch[offset + i] = (char)((b0 << 8) + b1);
            } else {
               ch[offset + i] = (char)((b1 << 8) + b0);
            }
         }

         return numToRead;
      }
   }

   public long skip(long n) throws IOException {
      int charWidth = this.fEncoding >= 4 ? 2 : 1;
      long bytesSkipped = this.fInputStream.skip(n << charWidth);
      return (bytesSkipped & (long)(charWidth | 1)) == 0L ? bytesSkipped >> charWidth : (bytesSkipped >> charWidth) + 1L;
   }

   public boolean ready() throws IOException {
      return false;
   }

   public boolean markSupported() {
      return this.fInputStream.markSupported();
   }

   public void mark(int readAheadLimit) throws IOException {
      this.fInputStream.mark(readAheadLimit);
   }

   public void reset() throws IOException {
      this.fInputStream.reset();
   }

   public void close() throws IOException {
      BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
      ba.returnByteBuffer(this.fBuffer);
      this.fBuffer = null;
      this.fInputStream.close();
   }
}
