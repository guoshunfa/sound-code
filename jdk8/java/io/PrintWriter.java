package java.io;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import sun.security.action.GetPropertyAction;

public class PrintWriter extends Writer {
   protected Writer out;
   private final boolean autoFlush;
   private boolean trouble;
   private Formatter formatter;
   private PrintStream psOut;
   private final String lineSeparator;

   private static Charset toCharset(String var0) throws UnsupportedEncodingException {
      Objects.requireNonNull(var0, (String)"charsetName");

      try {
         return Charset.forName(var0);
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var2) {
         throw new UnsupportedEncodingException(var0);
      }
   }

   public PrintWriter(Writer var1) {
      this(var1, false);
   }

   public PrintWriter(Writer var1, boolean var2) {
      super(var1);
      this.trouble = false;
      this.psOut = null;
      this.out = var1;
      this.autoFlush = var2;
      this.lineSeparator = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));
   }

   public PrintWriter(OutputStream var1) {
      this(var1, false);
   }

   public PrintWriter(OutputStream var1, boolean var2) {
      this((Writer)(new BufferedWriter(new OutputStreamWriter(var1))), var2);
      if (var1 instanceof PrintStream) {
         this.psOut = (PrintStream)var1;
      }

   }

   public PrintWriter(String var1) throws FileNotFoundException {
      this((Writer)(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var1)))), false);
   }

   private PrintWriter(Charset var1, File var2) throws FileNotFoundException {
      this((Writer)(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var2), var1))), false);
   }

   public PrintWriter(String var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
      this(toCharset(var2), new File(var1));
   }

   public PrintWriter(File var1) throws FileNotFoundException {
      this((Writer)(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var1)))), false);
   }

   public PrintWriter(File var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
      this(toCharset(var2), var1);
   }

   private void ensureOpen() throws IOException {
      if (this.out == null) {
         throw new IOException("Stream closed");
      }
   }

   public void flush() {
      try {
         synchronized(this.lock) {
            this.ensureOpen();
            this.out.flush();
         }
      } catch (IOException var4) {
         this.trouble = true;
      }

   }

   public void close() {
      try {
         synchronized(this.lock) {
            if (this.out == null) {
               return;
            }

            this.out.close();
            this.out = null;
         }
      } catch (IOException var4) {
         this.trouble = true;
      }

   }

   public boolean checkError() {
      if (this.out != null) {
         this.flush();
      }

      if (this.out instanceof PrintWriter) {
         PrintWriter var1 = (PrintWriter)this.out;
         return var1.checkError();
      } else {
         return this.psOut != null ? this.psOut.checkError() : this.trouble;
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
         synchronized(this.lock) {
            this.ensureOpen();
            this.out.write(var1);
         }
      } catch (InterruptedIOException var5) {
         Thread.currentThread().interrupt();
      } catch (IOException var6) {
         this.trouble = true;
      }

   }

   public void write(char[] var1, int var2, int var3) {
      try {
         synchronized(this.lock) {
            this.ensureOpen();
            this.out.write(var1, var2, var3);
         }
      } catch (InterruptedIOException var7) {
         Thread.currentThread().interrupt();
      } catch (IOException var8) {
         this.trouble = true;
      }

   }

   public void write(char[] var1) {
      this.write((char[])var1, 0, var1.length);
   }

   public void write(String var1, int var2, int var3) {
      try {
         synchronized(this.lock) {
            this.ensureOpen();
            this.out.write(var1, var2, var3);
         }
      } catch (InterruptedIOException var7) {
         Thread.currentThread().interrupt();
      } catch (IOException var8) {
         this.trouble = true;
      }

   }

   public void write(String var1) {
      this.write((String)var1, 0, var1.length());
   }

   private void newLine() {
      try {
         synchronized(this.lock) {
            this.ensureOpen();
            this.out.write(this.lineSeparator);
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
      this.write(var1);
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
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(char var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(int var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(long var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(float var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(double var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(char[] var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(String var1) {
      synchronized(this.lock) {
         this.print(var1);
         this.println();
      }
   }

   public void println(Object var1) {
      String var2 = String.valueOf(var1);
      synchronized(this.lock) {
         this.print(var2);
         this.println();
      }
   }

   public PrintWriter printf(String var1, Object... var2) {
      return this.format(var1, var2);
   }

   public PrintWriter printf(Locale var1, String var2, Object... var3) {
      return this.format(var1, var2, var3);
   }

   public PrintWriter format(String var1, Object... var2) {
      try {
         synchronized(this.lock) {
            this.ensureOpen();
            if (this.formatter == null || this.formatter.locale() != Locale.getDefault()) {
               this.formatter = new Formatter(this);
            }

            this.formatter.format(Locale.getDefault(), var1, var2);
            if (this.autoFlush) {
               this.out.flush();
            }
         }
      } catch (InterruptedIOException var6) {
         Thread.currentThread().interrupt();
      } catch (IOException var7) {
         this.trouble = true;
      }

      return this;
   }

   public PrintWriter format(Locale var1, String var2, Object... var3) {
      try {
         synchronized(this.lock) {
            this.ensureOpen();
            if (this.formatter == null || this.formatter.locale() != var1) {
               this.formatter = new Formatter(this, var1);
            }

            this.formatter.format(var1, var2, var3);
            if (this.autoFlush) {
               this.out.flush();
            }
         }
      } catch (InterruptedIOException var7) {
         Thread.currentThread().interrupt();
      } catch (IOException var8) {
         this.trouble = true;
      }

      return this;
   }

   public PrintWriter append(CharSequence var1) {
      if (var1 == null) {
         this.write("null");
      } else {
         this.write(var1.toString());
      }

      return this;
   }

   public PrintWriter append(CharSequence var1, int var2, int var3) {
      Object var4 = var1 == null ? "null" : var1;
      this.write(((CharSequence)var4).subSequence(var2, var3).toString());
      return this;
   }

   public PrintWriter append(char var1) {
      this.write(var1);
      return this;
   }
}
