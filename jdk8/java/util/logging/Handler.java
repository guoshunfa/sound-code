package java.util.logging;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

public abstract class Handler {
   private static final int offValue;
   private final LogManager manager = LogManager.getLogManager();
   private volatile Filter filter;
   private volatile Formatter formatter;
   private volatile Level logLevel;
   private volatile ErrorManager errorManager;
   private volatile String encoding;
   boolean sealed;

   protected Handler() {
      this.logLevel = Level.ALL;
      this.errorManager = new ErrorManager();
      this.sealed = true;
   }

   public abstract void publish(LogRecord var1);

   public abstract void flush();

   public abstract void close() throws SecurityException;

   public synchronized void setFormatter(Formatter var1) throws SecurityException {
      this.checkPermission();
      var1.getClass();
      this.formatter = var1;
   }

   public Formatter getFormatter() {
      return this.formatter;
   }

   public synchronized void setEncoding(String var1) throws SecurityException, UnsupportedEncodingException {
      this.checkPermission();
      if (var1 != null) {
         try {
            if (!Charset.isSupported(var1)) {
               throw new UnsupportedEncodingException(var1);
            }
         } catch (IllegalCharsetNameException var3) {
            throw new UnsupportedEncodingException(var1);
         }
      }

      this.encoding = var1;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public synchronized void setFilter(Filter var1) throws SecurityException {
      this.checkPermission();
      this.filter = var1;
   }

   public Filter getFilter() {
      return this.filter;
   }

   public synchronized void setErrorManager(ErrorManager var1) {
      this.checkPermission();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.errorManager = var1;
      }
   }

   public ErrorManager getErrorManager() {
      this.checkPermission();
      return this.errorManager;
   }

   protected void reportError(String var1, Exception var2, int var3) {
      try {
         this.errorManager.error(var1, var2, var3);
      } catch (Exception var5) {
         System.err.println("Handler.reportError caught:");
         var5.printStackTrace();
      }

   }

   public synchronized void setLevel(Level var1) throws SecurityException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.checkPermission();
         this.logLevel = var1;
      }
   }

   public Level getLevel() {
      return this.logLevel;
   }

   public boolean isLoggable(LogRecord var1) {
      int var2 = this.getLevel().intValue();
      if (var1.getLevel().intValue() >= var2 && var2 != offValue) {
         Filter var3 = this.getFilter();
         return var3 == null ? true : var3.isLoggable(var1);
      } else {
         return false;
      }
   }

   void checkPermission() throws SecurityException {
      if (this.sealed) {
         this.manager.checkPermission();
      }

   }

   static {
      offValue = Level.OFF.intValue();
   }
}
