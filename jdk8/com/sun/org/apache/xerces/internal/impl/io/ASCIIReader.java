package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class ASCIIReader extends Reader {
   public static final int DEFAULT_BUFFER_SIZE = 2048;
   protected InputStream fInputStream;
   protected byte[] fBuffer;
   private MessageFormatter fFormatter;
   private Locale fLocale;

   public ASCIIReader(InputStream inputStream, MessageFormatter messageFormatter, Locale locale) {
      this(inputStream, 2048, messageFormatter, locale);
   }

   public ASCIIReader(InputStream inputStream, int size, MessageFormatter messageFormatter, Locale locale) {
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
      int b0 = this.fInputStream.read();
      if (b0 >= 128) {
         throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[]{Integer.toString(b0)});
      } else {
         return b0;
      }
   }

   public int read(char[] ch, int offset, int length) throws IOException {
      if (length > this.fBuffer.length) {
         length = this.fBuffer.length;
      }

      int count = this.fInputStream.read(this.fBuffer, 0, length);

      for(int i = 0; i < count; ++i) {
         int b0 = this.fBuffer[i];
         if (b0 < 0) {
            throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[]{Integer.toString(b0 & 255)});
         }

         ch[offset + i] = (char)b0;
      }

      return count;
   }

   public long skip(long n) throws IOException {
      return this.fInputStream.skip(n);
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
