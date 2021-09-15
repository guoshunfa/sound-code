package java.rmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LoggingPermission;

/** @deprecated */
@Deprecated
public class LogStream extends PrintStream {
   private static Map<String, LogStream> known = new HashMap(5);
   private static PrintStream defaultStream;
   private String name;
   private OutputStream logOut;
   private OutputStreamWriter logWriter;
   private StringBuffer buffer = new StringBuffer();
   private ByteArrayOutputStream bufOut;
   public static final int SILENT = 0;
   public static final int BRIEF = 10;
   public static final int VERBOSE = 20;

   /** @deprecated */
   @Deprecated
   private LogStream(String var1, OutputStream var2) {
      super((OutputStream)(new ByteArrayOutputStream()));
      this.bufOut = (ByteArrayOutputStream)super.out;
      this.name = var1;
      this.setOutputStream(var2);
   }

   /** @deprecated */
   @Deprecated
   public static LogStream log(String var0) {
      synchronized(known) {
         LogStream var1 = (LogStream)known.get(var0);
         if (var1 == null) {
            var1 = new LogStream(var0, defaultStream);
         }

         known.put(var0, var1);
         return var1;
      }
   }

   /** @deprecated */
   @Deprecated
   public static synchronized PrintStream getDefaultStream() {
      return defaultStream;
   }

   /** @deprecated */
   @Deprecated
   public static synchronized void setDefaultStream(PrintStream var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new LoggingPermission("control", (String)null));
      }

      defaultStream = var0;
   }

   /** @deprecated */
   @Deprecated
   public synchronized OutputStream getOutputStream() {
      return this.logOut;
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setOutputStream(OutputStream var1) {
      this.logOut = var1;
      this.logWriter = new OutputStreamWriter(this.logOut);
   }

   /** @deprecated */
   @Deprecated
   public void write(int var1) {
      if (var1 == 10) {
         synchronized(this) {
            synchronized(this.logOut) {
               this.buffer.setLength(0);
               this.buffer.append((new Date()).toString());
               this.buffer.append(':');
               this.buffer.append(this.name);
               this.buffer.append(':');
               this.buffer.append(Thread.currentThread().getName());
               this.buffer.append(':');

               try {
                  this.logWriter.write(this.buffer.toString());
                  this.logWriter.flush();
                  this.bufOut.writeTo(this.logOut);
                  this.logOut.write(var1);
                  this.logOut.flush();
               } catch (IOException var12) {
                  this.setError();
               } finally {
                  this.bufOut.reset();
               }
            }
         }
      } else {
         super.write(var1);
      }

   }

   /** @deprecated */
   @Deprecated
   public void write(byte[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException(var3);
      } else {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.write(var1[var2 + var4]);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public String toString() {
      return this.name;
   }

   /** @deprecated */
   @Deprecated
   public static int parseLevel(String var0) {
      if (var0 != null && var0.length() >= 1) {
         try {
            return Integer.parseInt(var0);
         } catch (NumberFormatException var2) {
            if (var0.length() < 1) {
               return -1;
            } else if ("SILENT".startsWith(var0.toUpperCase())) {
               return 0;
            } else if ("BRIEF".startsWith(var0.toUpperCase())) {
               return 10;
            } else {
               return "VERBOSE".startsWith(var0.toUpperCase()) ? 20 : -1;
            }
         }
      } else {
         return -1;
      }
   }

   static {
      defaultStream = System.err;
   }
}
