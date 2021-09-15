package java.io;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import sun.misc.JavaIOAccess;
import sun.misc.SharedSecrets;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Console implements Flushable {
   private Object readLock;
   private Object writeLock;
   private Reader reader;
   private Writer out;
   private PrintWriter pw;
   private Formatter formatter;
   private Charset cs;
   private char[] rcb;
   private static boolean echoOff;
   private static Console cons;

   public PrintWriter writer() {
      return this.pw;
   }

   public Reader reader() {
      return this.reader;
   }

   public Console format(String var1, Object... var2) {
      this.formatter.format(var1, var2).flush();
      return this;
   }

   public Console printf(String var1, Object... var2) {
      return this.format(var1, var2);
   }

   public String readLine(String var1, Object... var2) {
      String var3 = null;
      synchronized(this.writeLock) {
         synchronized(this.readLock) {
            if (var1.length() != 0) {
               this.pw.format(var1, var2);
            }

            try {
               char[] var6 = this.readline(false);
               if (var6 != null) {
                  var3 = new String(var6);
               }
            } catch (IOException var9) {
               throw new IOError(var9);
            }
         }

         return var3;
      }
   }

   public String readLine() {
      return this.readLine("");
   }

   public char[] readPassword(String var1, Object... var2) {
      char[] var3 = null;
      synchronized(this.writeLock) {
         synchronized(this.readLock) {
            try {
               echoOff = echo(false);
            } catch (IOException var21) {
               throw new IOError(var21);
            }

            IOError var6 = null;

            try {
               if (var1.length() != 0) {
                  this.pw.format(var1, var2);
               }

               var3 = this.readline(true);
            } catch (IOException var20) {
               var6 = new IOError(var20);
            } finally {
               try {
                  echoOff = echo(true);
               } catch (IOException var22) {
                  if (var6 == null) {
                     var6 = new IOError(var22);
                  } else {
                     var6.addSuppressed(var22);
                  }
               }

               if (var6 != null) {
                  throw var6;
               }

            }

            this.pw.println();
         }

         return var3;
      }
   }

   public char[] readPassword() {
      return this.readPassword("");
   }

   public void flush() {
      this.pw.flush();
   }

   private static native String encoding();

   private static native boolean echo(boolean var0) throws IOException;

   private char[] readline(boolean var1) throws IOException {
      int var2 = this.reader.read(this.rcb, 0, this.rcb.length);
      if (var2 < 0) {
         return null;
      } else {
         if (this.rcb[var2 - 1] == '\r') {
            --var2;
         } else if (this.rcb[var2 - 1] == '\n') {
            --var2;
            if (var2 > 0 && this.rcb[var2 - 1] == '\r') {
               --var2;
            }
         }

         char[] var3 = new char[var2];
         if (var2 > 0) {
            System.arraycopy(this.rcb, 0, var3, 0, var2);
            if (var1) {
               Arrays.fill((char[])this.rcb, 0, var2, (char)' ');
            }
         }

         return var3;
      }
   }

   private char[] grow() {
      assert Thread.holdsLock(this.readLock);

      char[] var1 = new char[this.rcb.length * 2];
      System.arraycopy(this.rcb, 0, var1, 0, this.rcb.length);
      this.rcb = var1;
      return this.rcb;
   }

   private static native boolean istty();

   private Console() {
      this.readLock = new Object();
      this.writeLock = new Object();
      String var1 = encoding();
      if (var1 != null) {
         try {
            this.cs = Charset.forName(var1);
         } catch (Exception var3) {
         }
      }

      if (this.cs == null) {
         this.cs = Charset.defaultCharset();
      }

      this.out = StreamEncoder.forOutputStreamWriter(new FileOutputStream(FileDescriptor.out), this.writeLock, (Charset)this.cs);
      this.pw = new PrintWriter(this.out, true) {
         public void close() {
         }
      };
      this.formatter = new Formatter(this.out);
      this.reader = new Console.LineReader(StreamDecoder.forInputStreamReader(new FileInputStream(FileDescriptor.in), this.readLock, (Charset)this.cs));
      this.rcb = new char[1024];
   }

   // $FF: synthetic method
   Console(Object var1) {
      this();
   }

   static {
      try {
         SharedSecrets.getJavaLangAccess().registerShutdownHook(0, false, new Runnable() {
            public void run() {
               try {
                  if (Console.echoOff) {
                     Console.echo(true);
                  }
               } catch (IOException var2) {
               }

            }
         });
      } catch (IllegalStateException var1) {
      }

      SharedSecrets.setJavaIOAccess(new JavaIOAccess() {
         public Console console() {
            if (Console.istty()) {
               if (Console.cons == null) {
                  Console.cons = new Console();
               }

               return Console.cons;
            } else {
               return null;
            }
         }

         public Charset charset() {
            return Console.cons.cs;
         }
      });
   }

   class LineReader extends Reader {
      private Reader in;
      private char[] cb;
      private int nChars;
      private int nextChar;
      boolean leftoverLF;

      LineReader(Reader var2) {
         this.in = var2;
         this.cb = new char[1024];
         this.nextChar = this.nChars = 0;
         this.leftoverLF = false;
      }

      public void close() {
      }

      public boolean ready() throws IOException {
         return this.in.ready();
      }

      public int read(char[] var1, int var2, int var3) throws IOException {
         int var4 = var2;
         int var5 = var2 + var3;
         if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var5 >= 0 && var5 <= var1.length) {
            synchronized(Console.this.readLock) {
               boolean var7 = false;
               boolean var8 = false;

               do {
                  if (this.nextChar >= this.nChars) {
                     boolean var9 = false;

                     int var13;
                     do {
                        var13 = this.in.read(this.cb, 0, this.cb.length);
                     } while(var13 == 0);

                     if (var13 <= 0) {
                        if (var4 - var2 == 0) {
                           return -1;
                        }

                        return var4 - var2;
                     }

                     this.nChars = var13;
                     this.nextChar = 0;
                     if (var13 < this.cb.length && this.cb[var13 - 1] != '\n' && this.cb[var13 - 1] != '\r') {
                        var7 = true;
                     }
                  }

                  if (this.leftoverLF && var1 == Console.this.rcb && this.cb[this.nextChar] == '\n') {
                     ++this.nextChar;
                  }

                  this.leftoverLF = false;

                  while(this.nextChar < this.nChars) {
                     char var12 = var1[var4++] = this.cb[this.nextChar];
                     this.cb[this.nextChar++] = 0;
                     if (var12 == '\n') {
                        return var4 - var2;
                     }

                     if (var12 == '\r') {
                        if (var4 == var5) {
                           if (var1 != Console.this.rcb) {
                              this.leftoverLF = true;
                              return var4 - var2;
                           }

                           var1 = Console.this.grow();
                           var5 = var1.length;
                        }

                        if (this.nextChar == this.nChars && this.in.ready()) {
                           this.nChars = this.in.read(this.cb, 0, this.cb.length);
                           this.nextChar = 0;
                        }

                        if (this.nextChar < this.nChars && this.cb[this.nextChar] == '\n') {
                           var1[var4++] = '\n';
                           ++this.nextChar;
                        }

                        return var4 - var2;
                     }

                     if (var4 == var5) {
                        if (var1 != Console.this.rcb) {
                           return var4 - var2;
                        }

                        var1 = Console.this.grow();
                        var5 = var1.length;
                     }
                  }
               } while(!var7);

               return var4 - var2;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }
}
