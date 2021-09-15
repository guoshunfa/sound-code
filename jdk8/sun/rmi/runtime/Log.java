package sun.rmi.runtime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import sun.security.action.GetPropertyAction;

public abstract class Log {
   public static final Level BRIEF;
   public static final Level VERBOSE;
   private static final Log.LogFactory logFactory;

   public abstract boolean isLoggable(Level var1);

   public abstract void log(Level var1, String var2);

   public abstract void log(Level var1, String var2, Throwable var3);

   public abstract void setOutputStream(OutputStream var1);

   public abstract PrintStream getPrintStream();

   public static Log getLog(String var0, String var1, int var2) {
      Level var3;
      if (var2 < 0) {
         var3 = null;
      } else if (var2 == 0) {
         var3 = Level.OFF;
      } else if (var2 > 0 && var2 <= 10) {
         var3 = BRIEF;
      } else if (var2 > 10 && var2 <= 20) {
         var3 = VERBOSE;
      } else {
         var3 = Level.FINEST;
      }

      return logFactory.createLog(var0, var1, var3);
   }

   public static Log getLog(String var0, String var1, boolean var2) {
      Level var3 = var2 ? VERBOSE : null;
      return logFactory.createLog(var0, var1, var3);
   }

   private static String[] getSource() {
      StackTraceElement[] var0 = (new Exception()).getStackTrace();
      return new String[]{var0[3].getClassName(), var0[3].getMethodName()};
   }

   static {
      BRIEF = Level.FINE;
      VERBOSE = Level.FINER;
      boolean var0 = Boolean.valueOf((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.log.useOld"))));
      logFactory = (Log.LogFactory)(var0 ? new Log.LogStreamLogFactory() : new Log.LoggerLogFactory());
   }

   private static class LogStreamLog extends Log {
      private final LogStream stream;
      private int levelValue;

      private LogStreamLog(LogStream var1, Level var2) {
         this.levelValue = Level.OFF.intValue();
         if (var1 != null && var2 != null) {
            this.levelValue = var2.intValue();
         }

         this.stream = var1;
      }

      public synchronized boolean isLoggable(Level var1) {
         return var1.intValue() >= this.levelValue;
      }

      public void log(Level var1, String var2) {
         if (this.isLoggable(var1)) {
            String[] var3 = Log.getSource();
            this.stream.println(unqualifiedName(var3[0]) + "." + var3[1] + ": " + var2);
         }

      }

      public void log(Level var1, String var2, Throwable var3) {
         if (this.isLoggable(var1)) {
            synchronized(this.stream) {
               String[] var5 = Log.getSource();
               this.stream.println(unqualifiedName(var5[0]) + "." + var5[1] + ": " + var2);
               var3.printStackTrace((PrintStream)this.stream);
            }
         }

      }

      public PrintStream getPrintStream() {
         return this.stream;
      }

      public synchronized void setOutputStream(OutputStream var1) {
         if (var1 != null) {
            if (VERBOSE.intValue() < this.levelValue) {
               this.levelValue = VERBOSE.intValue();
            }

            this.stream.setOutputStream(var1);
         } else {
            this.levelValue = Level.OFF.intValue();
         }

      }

      private static String unqualifiedName(String var0) {
         int var1 = var0.lastIndexOf(".");
         if (var1 >= 0) {
            var0 = var0.substring(var1 + 1);
         }

         var0 = var0.replace('$', '.');
         return var0;
      }

      // $FF: synthetic method
      LogStreamLog(LogStream var1, Level var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class LogStreamLogFactory implements Log.LogFactory {
      LogStreamLogFactory() {
      }

      public Log createLog(String var1, String var2, Level var3) {
         LogStream var4 = null;
         if (var2 != null) {
            var4 = LogStream.log(var2);
         }

         return new Log.LogStreamLog(var4, var3);
      }
   }

   private static class LoggerPrintStream extends PrintStream {
      private final Logger logger;
      private int last;
      private final ByteArrayOutputStream bufOut;

      private LoggerPrintStream(Logger var1) {
         super((OutputStream)(new ByteArrayOutputStream()));
         this.last = -1;
         this.bufOut = (ByteArrayOutputStream)super.out;
         this.logger = var1;
      }

      public void write(int var1) {
         if (this.last == 13 && var1 == 10) {
            this.last = -1;
         } else {
            if (var1 != 10 && var1 != 13) {
               super.write(var1);
            } else {
               try {
                  String var2 = Thread.currentThread().getName() + ": " + this.bufOut.toString();
                  this.logger.logp(Level.INFO, "LogStream", "print", var2);
               } finally {
                  this.bufOut.reset();
               }
            }

            this.last = var1;
         }
      }

      public void write(byte[] var1, int var2, int var3) {
         if (var3 < 0) {
            throw new ArrayIndexOutOfBoundsException(var3);
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               this.write(var1[var2 + var4]);
            }

         }
      }

      public String toString() {
         return "RMI";
      }

      // $FF: synthetic method
      LoggerPrintStream(Logger var1, Object var2) {
         this(var1);
      }
   }

   private static class InternalStreamHandler extends StreamHandler {
      InternalStreamHandler(OutputStream var1) {
         super(var1, new SimpleFormatter());
      }

      public void publish(LogRecord var1) {
         super.publish(var1);
         this.flush();
      }

      public void close() {
         this.flush();
      }
   }

   private static class LoggerLog extends Log {
      private static final Handler alternateConsole = (Handler)AccessController.doPrivileged(new PrivilegedAction<Handler>() {
         public Handler run() {
            Log.InternalStreamHandler var1 = new Log.InternalStreamHandler(System.err);
            var1.setLevel(Level.ALL);
            return var1;
         }
      });
      private Log.InternalStreamHandler copyHandler;
      private final Logger logger;
      private Log.LoggerPrintStream loggerSandwich;

      private LoggerLog(final Logger var1, final Level var2) {
         this.copyHandler = null;
         this.logger = var1;
         if (var2 != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  if (!var1.isLoggable(var2)) {
                     var1.setLevel(var2);
                  }

                  var1.addHandler(Log.LoggerLog.alternateConsole);
                  return null;
               }
            });
         }

      }

      public boolean isLoggable(Level var1) {
         return this.logger.isLoggable(var1);
      }

      public void log(Level var1, String var2) {
         if (this.isLoggable(var1)) {
            String[] var3 = Log.getSource();
            this.logger.logp(var1, var3[0], var3[1], Thread.currentThread().getName() + ": " + var2);
         }

      }

      public void log(Level var1, String var2, Throwable var3) {
         if (this.isLoggable(var1)) {
            String[] var4 = Log.getSource();
            this.logger.logp(var1, var4[0], var4[1], Thread.currentThread().getName() + ": " + var2, var3);
         }

      }

      public synchronized void setOutputStream(OutputStream var1) {
         if (var1 != null) {
            if (!this.logger.isLoggable(VERBOSE)) {
               this.logger.setLevel(VERBOSE);
            }

            this.copyHandler = new Log.InternalStreamHandler(var1);
            this.copyHandler.setLevel(Log.VERBOSE);
            this.logger.addHandler(this.copyHandler);
         } else {
            if (this.copyHandler != null) {
               this.logger.removeHandler(this.copyHandler);
            }

            this.copyHandler = null;
         }

      }

      public synchronized PrintStream getPrintStream() {
         if (this.loggerSandwich == null) {
            this.loggerSandwich = new Log.LoggerPrintStream(this.logger);
         }

         return this.loggerSandwich;
      }

      // $FF: synthetic method
      LoggerLog(Logger var1, Level var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class LoggerLogFactory implements Log.LogFactory {
      LoggerLogFactory() {
      }

      public Log createLog(String var1, String var2, Level var3) {
         Logger var4 = Logger.getLogger(var1);
         return new Log.LoggerLog(var4, var3);
      }
   }

   private interface LogFactory {
      Log createLog(String var1, String var2, Level var3);
   }
}
