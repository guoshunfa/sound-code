package java.io;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class BufferedWriter extends Writer {
   private Writer out;
   private char[] cb;
   private int nChars;
   private int nextChar;
   private static int defaultCharBufferSize = 8192;
   private String lineSeparator;

   public BufferedWriter(Writer var1) {
      this(var1, defaultCharBufferSize);
   }

   public BufferedWriter(Writer var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.out = var1;
         this.cb = new char[var2];
         this.nChars = var2;
         this.nextChar = 0;
         this.lineSeparator = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));
      }
   }

   private void ensureOpen() throws IOException {
      if (this.out == null) {
         throw new IOException("Stream closed");
      }
   }

   void flushBuffer() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (this.nextChar != 0) {
            this.out.write((char[])this.cb, 0, this.nextChar);
            this.nextChar = 0;
         }
      }
   }

   public void write(int var1) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (this.nextChar >= this.nChars) {
            this.flushBuffer();
         }

         this.cb[this.nextChar++] = (char)var1;
      }
   }

   private int min(int var1, int var2) {
      return var1 < var2 ? var1 : var2;
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            if (var3 != 0) {
               if (var3 >= this.nChars) {
                  this.flushBuffer();
                  this.out.write(var1, var2, var3);
               } else {
                  int var5 = var2;
                  int var6 = var2 + var3;

                  while(var5 < var6) {
                     int var7 = this.min(this.nChars - this.nextChar, var6 - var5);
                     System.arraycopy(var1, var5, this.cb, this.nextChar, var7);
                     var5 += var7;
                     this.nextChar += var7;
                     if (this.nextChar >= this.nChars) {
                        this.flushBuffer();
                     }
                  }

               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void write(String var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         int var5 = var2;
         int var6 = var2 + var3;

         while(var5 < var6) {
            int var7 = this.min(this.nChars - this.nextChar, var6 - var5);
            var1.getChars(var5, var5 + var7, this.cb, this.nextChar);
            var5 += var7;
            this.nextChar += var7;
            if (this.nextChar >= this.nChars) {
               this.flushBuffer();
            }
         }

      }
   }

   public void newLine() throws IOException {
      this.write(this.lineSeparator);
   }

   public void flush() throws IOException {
      synchronized(this.lock) {
         this.flushBuffer();
         this.out.flush();
      }
   }

   public void close() throws IOException {
      synchronized(this.lock) {
         if (this.out != null) {
            try {
               Writer var2 = this.out;
               Throwable var3 = null;

               try {
                  this.flushBuffer();
               } catch (Throwable var22) {
                  var3 = var22;
                  throw var22;
               } finally {
                  if (var2 != null) {
                     if (var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var21) {
                           var3.addSuppressed(var21);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }
            } finally {
               this.out = null;
               this.cb = null;
            }

         }
      }
   }
}
