package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class UTF8Reader extends Reader {
   public static final int DEFAULT_BUFFER_SIZE = 2048;
   private static final boolean DEBUG_READ = false;
   protected InputStream fInputStream;
   protected byte[] fBuffer;
   protected int fOffset;
   private int fSurrogate;
   private MessageFormatter fFormatter;
   private Locale fLocale;

   public UTF8Reader(InputStream inputStream) {
      this(inputStream, 2048, new XMLMessageFormatter(), Locale.getDefault());
   }

   public UTF8Reader(InputStream inputStream, MessageFormatter messageFormatter, Locale locale) {
      this(inputStream, 2048, messageFormatter, locale);
   }

   public UTF8Reader(InputStream inputStream, int size, MessageFormatter messageFormatter, Locale locale) {
      this.fSurrogate = -1;
      this.fFormatter = null;
      this.fLocale = null;
      this.fInputStream = inputStream;
      BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
      this.fBuffer = ba.getByteBuffer(size);
      if (this.fBuffer == null) {
         this.fBuffer = new byte[size];
      }

      this.fFormatter = messageFormatter;
      this.fLocale = locale;
   }

   public int read() throws IOException {
      int c = this.fSurrogate;
      if (this.fSurrogate == -1) {
         int index = 0;
         int b0 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
         if (b0 == -1) {
            return -1;
         }

         if (b0 < 128) {
            c = (char)b0;
         } else {
            int b1;
            if ((b0 & 224) == 192 && (b0 & 30) != 0) {
               b1 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
               if (b1 == -1) {
                  this.expectedByte(2, 2);
               }

               if ((b1 & 192) != 128) {
                  this.invalidByte(2, 2, b1);
               }

               c = b0 << 6 & 1984 | b1 & 63;
            } else {
               int b2;
               if ((b0 & 240) == 224) {
                  b1 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
                  if (b1 == -1) {
                     this.expectedByte(2, 3);
                  }

                  if ((b1 & 192) != 128 || b0 == 237 && b1 >= 160 || (b0 & 15) == 0 && (b1 & 32) == 0) {
                     this.invalidByte(2, 3, b1);
                  }

                  b2 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
                  if (b2 == -1) {
                     this.expectedByte(3, 3);
                  }

                  if ((b2 & 192) != 128) {
                     this.invalidByte(3, 3, b2);
                  }

                  c = b0 << 12 & '\uf000' | b1 << 6 & 4032 | b2 & 63;
               } else if ((b0 & 248) == 240) {
                  b1 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
                  if (b1 == -1) {
                     this.expectedByte(2, 4);
                  }

                  if ((b1 & 192) != 128 || (b1 & 48) == 0 && (b0 & 7) == 0) {
                     this.invalidByte(2, 3, b1);
                  }

                  b2 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
                  if (b2 == -1) {
                     this.expectedByte(3, 4);
                  }

                  if ((b2 & 192) != 128) {
                     this.invalidByte(3, 3, b2);
                  }

                  int b3 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 255;
                  if (b3 == -1) {
                     this.expectedByte(4, 4);
                  }

                  if ((b3 & 192) != 128) {
                     this.invalidByte(4, 4, b3);
                  }

                  int uuuuu = b0 << 2 & 28 | b1 >> 4 & 3;
                  if (uuuuu > 16) {
                     this.invalidSurrogate(uuuuu);
                  }

                  int wwww = uuuuu - 1;
                  int hs = '\ud800' | wwww << 6 & 960 | b1 << 2 & 60 | b2 >> 4 & 3;
                  int ls = '\udc00' | b2 << 6 & 960 | b3 & 63;
                  c = hs;
                  this.fSurrogate = ls;
               } else {
                  this.invalidByte(1, 1, b0);
               }
            }
         }
      } else {
         this.fSurrogate = -1;
      }

      return c;
   }

   public int read(char[] ch, int offset, int length) throws IOException {
      int out = offset;
      if (this.fSurrogate != -1) {
         ch[offset + 1] = (char)this.fSurrogate;
         this.fSurrogate = -1;
         --length;
         out = offset + 1;
      }

      int count = false;
      int count;
      if (this.fOffset == 0) {
         if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
         }

         count = this.fInputStream.read(this.fBuffer, 0, length);
         if (count == -1) {
            return -1;
         }

         count += out - offset;
      } else {
         count = this.fOffset;
         this.fOffset = 0;
      }

      int total = count;
      byte byte0 = false;

      int in;
      byte byte1;
      for(in = 0; in < total; ++in) {
         byte1 = this.fBuffer[in];
         if (byte1 < 0) {
            break;
         }

         ch[out++] = (char)byte1;
      }

      for(; in < total; ++in) {
         byte1 = this.fBuffer[in];
         if (byte1 >= 0) {
            ch[out++] = (char)byte1;
         } else {
            int b0 = byte1 & 255;
            boolean b1;
            int b1;
            int b2;
            if ((b0 & 224) == 192 && (b0 & 30) != 0) {
               b1 = true;
               ++in;
               if (in < total) {
                  b1 = this.fBuffer[in] & 255;
               } else {
                  b1 = this.fInputStream.read();
                  if (b1 == -1) {
                     if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fOffset = 1;
                        return out - offset;
                     }

                     this.expectedByte(2, 2);
                  }

                  ++count;
               }

               if ((b1 & 192) != 128) {
                  if (out > offset) {
                     this.fBuffer[0] = (byte)b0;
                     this.fBuffer[1] = (byte)b1;
                     this.fOffset = 2;
                     return out - offset;
                  }

                  this.invalidByte(2, 2, b1);
               }

               b2 = b0 << 6 & 1984 | b1 & 63;
               ch[out++] = (char)b2;
               --count;
            } else {
               boolean b2;
               int b3;
               if ((b0 & 240) == 224) {
                  b1 = true;
                  ++in;
                  if (in < total) {
                     b1 = this.fBuffer[in] & 255;
                  } else {
                     b1 = this.fInputStream.read();
                     if (b1 == -1) {
                        if (out > offset) {
                           this.fBuffer[0] = (byte)b0;
                           this.fOffset = 1;
                           return out - offset;
                        }

                        this.expectedByte(2, 3);
                     }

                     ++count;
                  }

                  if ((b1 & 192) != 128 || b0 == 237 && b1 >= 160 || (b0 & 15) == 0 && (b1 & 32) == 0) {
                     if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fOffset = 2;
                        return out - offset;
                     }

                     this.invalidByte(2, 3, b1);
                  }

                  b2 = true;
                  ++in;
                  if (in < total) {
                     b2 = this.fBuffer[in] & 255;
                  } else {
                     b2 = this.fInputStream.read();
                     if (b2 == -1) {
                        if (out > offset) {
                           this.fBuffer[0] = (byte)b0;
                           this.fBuffer[1] = (byte)b1;
                           this.fOffset = 2;
                           return out - offset;
                        }

                        this.expectedByte(3, 3);
                     }

                     ++count;
                  }

                  if ((b2 & 192) != 128) {
                     if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fBuffer[2] = (byte)b2;
                        this.fOffset = 3;
                        return out - offset;
                     }

                     this.invalidByte(3, 3, b2);
                  }

                  b3 = b0 << 12 & '\uf000' | b1 << 6 & 4032 | b2 & 63;
                  ch[out++] = (char)b3;
                  count -= 2;
               } else if ((b0 & 248) == 240) {
                  b1 = true;
                  ++in;
                  if (in < total) {
                     b1 = this.fBuffer[in] & 255;
                  } else {
                     b1 = this.fInputStream.read();
                     if (b1 == -1) {
                        if (out > offset) {
                           this.fBuffer[0] = (byte)b0;
                           this.fOffset = 1;
                           return out - offset;
                        }

                        this.expectedByte(2, 4);
                     }

                     ++count;
                  }

                  if ((b1 & 192) != 128 || (b1 & 48) == 0 && (b0 & 7) == 0) {
                     if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fOffset = 2;
                        return out - offset;
                     }

                     this.invalidByte(2, 4, b1);
                  }

                  b2 = true;
                  ++in;
                  if (in < total) {
                     b2 = this.fBuffer[in] & 255;
                  } else {
                     b2 = this.fInputStream.read();
                     if (b2 == -1) {
                        if (out > offset) {
                           this.fBuffer[0] = (byte)b0;
                           this.fBuffer[1] = (byte)b1;
                           this.fOffset = 2;
                           return out - offset;
                        }

                        this.expectedByte(3, 4);
                     }

                     ++count;
                  }

                  if ((b2 & 192) != 128) {
                     if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fBuffer[2] = (byte)b2;
                        this.fOffset = 3;
                        return out - offset;
                     }

                     this.invalidByte(3, 4, b2);
                  }

                  int b3 = true;
                  ++in;
                  if (in < total) {
                     b3 = this.fBuffer[in] & 255;
                  } else {
                     b3 = this.fInputStream.read();
                     if (b3 == -1) {
                        if (out > offset) {
                           this.fBuffer[0] = (byte)b0;
                           this.fBuffer[1] = (byte)b1;
                           this.fBuffer[2] = (byte)b2;
                           this.fOffset = 3;
                           return out - offset;
                        }

                        this.expectedByte(4, 4);
                     }

                     ++count;
                  }

                  if ((b3 & 192) != 128) {
                     if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fBuffer[2] = (byte)b2;
                        this.fBuffer[3] = (byte)b3;
                        this.fOffset = 4;
                        return out - offset;
                     }

                     this.invalidByte(4, 4, b2);
                  }

                  if (out + 1 >= ch.length) {
                     this.fBuffer[0] = (byte)b0;
                     this.fBuffer[1] = (byte)b1;
                     this.fBuffer[2] = (byte)b2;
                     this.fBuffer[3] = (byte)b3;
                     this.fOffset = 4;
                     return out - offset;
                  }

                  int uuuuu = b0 << 2 & 28 | b1 >> 4 & 3;
                  if (uuuuu > 16) {
                     this.invalidSurrogate(uuuuu);
                  }

                  int wwww = uuuuu - 1;
                  int zzzz = b1 & 15;
                  int yyyyyy = b2 & 63;
                  int xxxxxx = b3 & 63;
                  int hs = '\ud800' | wwww << 6 & 960 | zzzz << 2 | yyyyyy >> 4;
                  int ls = '\udc00' | yyyyyy << 6 & 960 | xxxxxx;
                  ch[out++] = (char)hs;
                  ch[out++] = (char)ls;
                  count -= 2;
               } else {
                  if (out > offset) {
                     this.fBuffer[0] = (byte)b0;
                     this.fOffset = 1;
                     return out - offset;
                  }

                  this.invalidByte(1, 1, b0);
               }
            }
         }
      }

      return count;
   }

   public long skip(long n) throws IOException {
      long remaining = n;
      char[] ch = new char[this.fBuffer.length];

      do {
         int length = (long)ch.length < remaining ? ch.length : (int)remaining;
         int count = this.read(ch, 0, length);
         if (count <= 0) {
            break;
         }

         remaining -= (long)count;
      } while(remaining > 0L);

      long skipped = n - remaining;
      return skipped;
   }

   public boolean ready() throws IOException {
      return false;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int readAheadLimit) throws IOException {
      throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[]{"mark()", "UTF-8"}));
   }

   public void reset() throws IOException {
      this.fOffset = 0;
      this.fSurrogate = -1;
   }

   public void close() throws IOException {
      BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
      ba.returnByteBuffer(this.fBuffer);
      this.fBuffer = null;
      this.fInputStream.close();
   }

   private void expectedByte(int position, int count) throws MalformedByteSequenceException {
      throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[]{Integer.toString(position), Integer.toString(count)});
   }

   private void invalidByte(int position, int count, int c) throws MalformedByteSequenceException {
      throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidByte", new Object[]{Integer.toString(position), Integer.toString(count)});
   }

   private void invalidSurrogate(int uuuuu) throws MalformedByteSequenceException {
      throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidHighSurrogate", new Object[]{Integer.toHexString(uuuuu)});
   }
}
