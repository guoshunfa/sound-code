package java.io;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Formatter;
import java.util.Locale;

public class PrintStream extends FilterOutputStream implements Appendable, Closeable {
   private final boolean autoFlush;
   private boolean trouble;
   private Formatter formatter;
   private BufferedWriter textOut;
   private OutputStreamWriter charOut;
   private boolean closing;

   private static <T> T requireNonNull(T var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1);
      } else {
         return var0;
      }
   }

   private static Charset toCharset(String var0) throws UnsupportedEncodingException {
      requireNonNull(var0, "charsetName");

      try {
         return Charset.forName(var0);
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var2) {
         throw new UnsupportedEncodingException(var0);
      }
   }

   private PrintStream(boolean var1, OutputStream var2) {
      super(var2);
      this.trouble = false;
      this.closing = false;
      this.autoFlush = var1;
      this.charOut = new OutputStreamWriter(this);
      this.textOut = new BufferedWriter(this.charOut);
   }

   private PrintStream(boolean var1, OutputStream var2, Charset var3) {
      super(var2);
      this.trouble = false;
      this.closing = false;
      this.autoFlush = var1;
      this.charOut = new OutputStreamWriter(this, var3);
      this.textOut = new BufferedWriter(this.charOut);
   }

   private PrintStream(boolean var1, Charset var2, OutputStream var3) throws UnsupportedEncodingException {
      this(var1, var3, var2);
   }

   public PrintStream(OutputStream var1) {
      this(var1, false);
   }

   public PrintStream(OutputStream var1, boolean var2) {
      this(var2, (OutputStream)requireNonNull(var1, "Null output stream"));
   }

   public PrintStream(OutputStream var1, boolean var2, String var3) throws UnsupportedEncodingException {
      this(var2, (OutputStream)requireNonNull(var1, "Null output stream"), toCharset(var3));
   }

   public PrintStream(String var1) throws FileNotFoundException {
      this(false, new FileOutputStream(var1));
   }

   public PrintStream(String var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
      this(false, (Charset)toCharset(var2), (OutputStream)(new FileOutputStream(var1)));
   }

   public PrintStream(File var1) throws FileNotFoundException {
      this(false, new FileOutputStream(var1));
   }

   public PrintStream(File var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
      this(false, (Charset)toCharset(var2), (OutputStream)(new FileOutputStream(var1)));
   }

   private void ensureOpen() throws IOException {
      if (this.out == null) {
         throw new IOException("Stream closed");
      }
   }

   public void flush() {
      synchronized(this) {
         try {
            this.ensureOpen();
            this.out.flush();
         } catch (IOException var4) {
            this.trouble = true;
         }

      }
   }

   public void close() {
      synchronized(this) {
         if (!this.closing) {
            this.closing = true;

            try {
               this.textOut.close();
               this.out.close();
            } catch (IOException var4) {
               this.trouble = true;
            }

            this.textOut = null;
            this.charOut = null;
            this.out = null;
         }

      }
   }

   public boolean checkError() {
      if (this.out != null) {
         this.flush();
      }

      if (this.out instanceof PrintStream) {
         PrintStream var1 = (PrintStream)this.out;
         return var1.checkError();
      } else {
         return this.trouble;
      }
   }

   protected void setError() {
      this.trouble = true;
   }

   protected void clearError() {
      this.trouble = false;
   }

   public void write(int var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.out.write(var1);
            if (var1 == 10 && this.autoFlush) {
               this.out.flush();
            }
         }
      } catch (InterruptedIOException var5) {
         Thread.currentThread().interrupt();
      } catch (IOException var6) {
         this.trouble = true;
      }

   }

   public void write(byte[] var1, int var2, int var3) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.out.write(var1, var2, var3);
            if (this.autoFlush) {
               this.out.flush();
            }
         }
      } catch (InterruptedIOException var7) {
         Thread.currentThread().interrupt();
      } catch (IOException var8) {
         this.trouble = true;
      }

   }

   private void write(char[] var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.textOut.write(var1);
            this.textOut.flushBuffer();
            this.charOut.flushBuffer();
            if (this.autoFlush) {
               for(int var3 = 0; var3 < var1.length; ++var3) {
                  if (var1[var3] == '\n') {
                     this.out.flush();
                  }
               }
            }
         }
      } catch (InterruptedIOException var6) {
         Thread.currentThread().interrupt();
      } catch (IOException var7) {
         this.trouble = true;
      }

   }

   private void write(String var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.textOut.write(var1);
            this.textOut.flushBuffer();
            this.charOut.flushBuffer();
            if (this.autoFlush && var1.indexOf(10) >= 0) {
               this.out.flush();
            }
         }
      } catch (InterruptedIOException var5) {
         Thread.currentThread().interrupt();
      } catch (IOException var6) {
         this.trouble = true;
      }

   }

   private void newLine() {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.textOut.newLine();
            this.textOut.flushBuffer();
            this.charOut.flushBuffer();
            if (this.autoFlush) {
               this.out.flush();
            }
         }
      } catch (InterruptedIOException var4) {
         Thread.currentThread().interrupt();
      } catch (IOException var5) {
         this.trouble = true;
      }

   }

   public void print(boolean var1) {
      this.write(var1 ? "true" : "false");
   }

   public void print(char var1) {
      this.write(String.valueOf(var1));
   }

   public void print(int var1) {
      this.write(String.valueOf(var1));
   }

   public void print(long var1) {
      this.write(String.valueOf(var1));
   }

   public void print(float var1) {
      this.write(String.valueOf(var1));
   }

   public void print(double var1) {
      this.write(String.valueOf(var1));
   }

   public void print(char[] var1) {
      this.write(var1);
   }

   public void print(String var1) {
      if (var1 == null) {
         var1 = "null";
      }

      this.write(var1);
   }

   public void print(Object var1) {
      this.write(String.valueOf(var1));
   }

   public void println() {
      this.newLine();
   }

   public void println(boolean var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(char var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(int var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(long var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(float var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(double var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(char[] var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(String var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(Object var1) {
      String var2 = String.valueOf(var1);
      synchronized(this) {
         this.print(var2);
         this.newLine();
      }
   }

   public PrintStream printf(String var1, Object... var2) {
      return this.format(var1, var2);
   }

   public PrintStream printf(Locale var1, String var2, Object... var3) {
      return this.format(var1, var2, var3);
   }

   public PrintStream format(String var1, Object... var2) {
      try {
         synchronized(this) {
            this.ensureOpen();
            if (this.formatter == null || this.formatter.locale() != Locale.getDefault()) {
               this.formatter = new Formatter(this);
            }

            this.formatter.format(Locale.getDefault(), var1, var2);
         }
      } catch (InterruptedIOException var6) {
         Thread.currentThread().interrupt();
      } catch (IOException var7) {
         this.trouble = true;
      }

      return this;
   }

   public PrintStream format(Locale var1, String var2, Object... var3) {
      try {
         synchronized(this) {
            this.ensureOpen();
            if (this.formatter == null || this.formatter.locale() != var1) {
               this.formatter = new Formatter(this, var1);
            }

            this.formatter.format(var1, var2, var3);
         }
      } catch (InterruptedIOException var7) {
         Thread.currentThread().interrupt();
      } catch (IOException var8) {
         this.trouble = true;
      }

      return this;
   }

   public PrintStream append(CharSequence var1) {
      if (var1 == null) {
         this.print("null");
      } else {
         this.print(var1.toString());
      }

      return this;
   }

   public PrintStream append(CharSequence var1, int var2, int var3) {
      Object var4 = var1 == null ? "null" : var1;
      this.write(((CharSequence)var4).subSequence(var2, var3).toString());
      return this;
   }

   public PrintStream append(char var1) {
      this.print(var1);
      return this;
   }
}
