package java.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BufferedReader extends Reader {
   private Reader in;
   private char[] cb;
   private int nChars;
   private int nextChar;
   private static final int INVALIDATED = -2;
   private static final int UNMARKED = -1;
   private int markedChar;
   private int readAheadLimit;
   private boolean skipLF;
   private boolean markedSkipLF;
   private static int defaultCharBufferSize = 8192;
   private static int defaultExpectedLineLength = 80;

   public BufferedReader(Reader var1, int var2) {
      super(var1);
      this.markedChar = -1;
      this.readAheadLimit = 0;
      this.skipLF = false;
      this.markedSkipLF = false;
      if (var2 <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.in = var1;
         this.cb = new char[var2];
         this.nextChar = this.nChars = 0;
      }
   }

   public BufferedReader(Reader var1) {
      this(var1, defaultCharBufferSize);
   }

   private void ensureOpen() throws IOException {
      if (this.in == null) {
         throw new IOException("Stream closed");
      }
   }

   private void fill() throws IOException {
      int var1;
      int var2;
      if (this.markedChar <= -1) {
         var1 = 0;
      } else {
         var2 = this.nextChar - this.markedChar;
         if (var2 >= this.readAheadLimit) {
            this.markedChar = -2;
            this.readAheadLimit = 0;
            var1 = 0;
         } else {
            if (this.readAheadLimit <= this.cb.length) {
               System.arraycopy(this.cb, this.markedChar, this.cb, 0, var2);
               this.markedChar = 0;
               var1 = var2;
            } else {
               char[] var3 = new char[this.readAheadLimit];
               System.arraycopy(this.cb, this.markedChar, var3, 0, var2);
               this.cb = var3;
               this.markedChar = 0;
               var1 = var2;
            }

            this.nextChar = this.nChars = var2;
         }
      }

      do {
         var2 = this.in.read(this.cb, var1, this.cb.length - var1);
      } while(var2 == 0);

      if (var2 > 0) {
         this.nChars = var1 + var2;
         this.nextChar = var1;
      }

   }

   public int read() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();

         while(true) {
            if (this.nextChar >= this.nChars) {
               this.fill();
               if (this.nextChar >= this.nChars) {
                  return -1;
               }
            }

            if (!this.skipLF) {
               break;
            }

            this.skipLF = false;
            if (this.cb[this.nextChar] != '\n') {
               break;
            }

            ++this.nextChar;
         }

         return this.cb[this.nextChar++];
      }
   }

   private int read1(char[] var1, int var2, int var3) throws IOException {
      if (this.nextChar >= this.nChars) {
         if (var3 >= this.cb.length && this.markedChar <= -1 && !this.skipLF) {
            return this.in.read(var1, var2, var3);
         }

         this.fill();
      }

      if (this.nextChar >= this.nChars) {
         return -1;
      } else {
         if (this.skipLF) {
            this.skipLF = false;
            if (this.cb[this.nextChar] == '\n') {
               ++this.nextChar;
               if (this.nextChar >= this.nChars) {
                  this.fill();
               }

               if (this.nextChar >= this.nChars) {
                  return -1;
               }
            }
         }

         int var4 = Math.min(var3, this.nChars - this.nextChar);
         System.arraycopy(this.cb, this.nextChar, var1, var2, var4);
         this.nextChar += var4;
         return var4;
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            if (var3 == 0) {
               return 0;
            } else {
               int var5 = this.read1(var1, var2, var3);
               if (var5 <= 0) {
                  return var5;
               } else {
                  while(var5 < var3 && this.in.ready()) {
                     int var6 = this.read1(var1, var2 + var5, var3 - var5);
                     if (var6 <= 0) {
                        break;
                     }

                     var5 += var6;
                  }

                  return var5;
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   String readLine(boolean var1) throws IOException {
      StringBuffer var2 = null;
      synchronized(this.lock) {
         this.ensureOpen();
         boolean var5 = var1 || this.skipLF;

         while(true) {
            if (this.nextChar >= this.nChars) {
               this.fill();
            }

            if (this.nextChar >= this.nChars) {
               if (var2 != null && var2.length() > 0) {
                  return var2.toString();
               }

               return null;
            }

            boolean var6 = false;
            char var7 = 0;
            if (var5 && this.cb[this.nextChar] == '\n') {
               ++this.nextChar;
            }

            this.skipLF = false;
            var5 = false;

            int var8;
            for(var8 = this.nextChar; var8 < this.nChars; ++var8) {
               var7 = this.cb[var8];
               if (var7 == '\n' || var7 == '\r') {
                  var6 = true;
                  break;
               }
            }

            int var3 = this.nextChar;
            this.nextChar = var8;
            if (var6) {
               String var9;
               if (var2 == null) {
                  var9 = new String(this.cb, var3, var8 - var3);
               } else {
                  var2.append(this.cb, var3, var8 - var3);
                  var9 = var2.toString();
               }

               ++this.nextChar;
               if (var7 == '\r') {
                  this.skipLF = true;
               }

               return var9;
            }

            if (var2 == null) {
               var2 = new StringBuffer(defaultExpectedLineLength);
            }

            var2.append(this.cb, var3, var8 - var3);
         }
      }
   }

   public String readLine() throws IOException {
      return this.readLine(false);
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         synchronized(this.lock) {
            this.ensureOpen();

            long var4;
            for(var4 = var1; var4 > 0L; this.nextChar = this.nChars) {
               if (this.nextChar >= this.nChars) {
                  this.fill();
               }

               if (this.nextChar >= this.nChars) {
                  break;
               }

               if (this.skipLF) {
                  this.skipLF = false;
                  if (this.cb[this.nextChar] == '\n') {
                     ++this.nextChar;
                  }
               }

               long var6 = (long)(this.nChars - this.nextChar);
               if (var4 <= var6) {
                  this.nextChar = (int)((long)this.nextChar + var4);
                  var4 = 0L;
                  break;
               }

               var4 -= var6;
            }

            return var1 - var4;
         }
      }
   }

   public boolean ready() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (this.skipLF) {
            if (this.nextChar >= this.nChars && this.in.ready()) {
               this.fill();
            }

            if (this.nextChar < this.nChars) {
               if (this.cb[this.nextChar] == '\n') {
                  ++this.nextChar;
               }

               this.skipLF = false;
            }
         }

         return this.nextChar < this.nChars || this.in.ready();
      }
   }

   public boolean markSupported() {
      return true;
   }

   public void mark(int var1) throws IOException {
      if (var1 < 0) {
         throw new IllegalArgumentException("Read-ahead limit < 0");
      } else {
         synchronized(this.lock) {
            this.ensureOpen();
            this.readAheadLimit = var1;
            this.markedChar = this.nextChar;
            this.markedSkipLF = this.skipLF;
         }
      }
   }

   public void reset() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (this.markedChar < 0) {
            throw new IOException(this.markedChar == -2 ? "Mark invalid" : "Stream not marked");
         } else {
            this.nextChar = this.markedChar;
            this.skipLF = this.markedSkipLF;
         }
      }
   }

   public void close() throws IOException {
      synchronized(this.lock) {
         if (this.in != null) {
            try {
               this.in.close();
            } finally {
               this.in = null;
               this.cb = null;
            }

         }
      }
   }

   public Stream<String> lines() {
      Iterator var1 = new Iterator<String>() {
         String nextLine = null;

         public boolean hasNext() {
            if (this.nextLine != null) {
               return true;
            } else {
               try {
                  this.nextLine = BufferedReader.this.readLine();
                  return this.nextLine != null;
               } catch (IOException var2) {
                  throw new UncheckedIOException(var2);
               }
            }
         }

         public String next() {
            if (this.nextLine == null && !this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               String var1 = this.nextLine;
               this.nextLine = null;
               return var1;
            }
         }
      };
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator)var1, 272), false);
   }
}
