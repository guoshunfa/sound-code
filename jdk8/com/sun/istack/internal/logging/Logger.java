package com.sun.istack.internal.logging;

import com.sun.istack.internal.NotNull;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class Logger {
   private static final String WS_LOGGING_SUBSYSTEM_NAME_ROOT = "com.sun.metro";
   private static final String ROOT_WS_PACKAGE = "com.sun.xml.internal.ws.";
   private static final Level METHOD_CALL_LEVEL_VALUE;
   private final String componentClassName;
   private final java.util.logging.Logger logger;

   protected Logger(String systemLoggerName, String componentName) {
      this.componentClassName = "[" + componentName + "] ";
      this.logger = java.util.logging.Logger.getLogger(systemLoggerName);
   }

   @NotNull
   public static Logger getLogger(@NotNull Class<?> componentClass) {
      return new Logger(getSystemLoggerName(componentClass), componentClass.getName());
   }

   @NotNull
   public static Logger getLogger(@NotNull String customLoggerName, @NotNull Class<?> componentClass) {
      return new Logger(customLoggerName, componentClass.getName());
   }

   static final String getSystemLoggerName(@NotNull Class<?> componentClass) {
      StringBuilder sb = new StringBuilder(componentClass.getPackage().getName());
      int lastIndexOfWsPackage = sb.lastIndexOf("com.sun.xml.internal.ws.");
      if (lastIndexOfWsPackage > -1) {
         sb.replace(0, lastIndexOfWsPackage + "com.sun.xml.internal.ws.".length(), "");
         StringTokenizer st = new StringTokenizer(sb.toString(), ".");
         sb = (new StringBuilder("com.sun.metro")).append(".");
         if (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("api".equals(token)) {
               token = st.nextToken();
            }

            sb.append(token);
         }
      }

      return sb.toString();
   }

   public void log(Level level, String message) {
      if (this.logger.isLoggable(level)) {
         this.logger.logp(level, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void log(Level level, String message, Object param1) {
      if (this.logger.isLoggable(level)) {
         this.logger.logp(level, this.componentClassName, getCallerMethodName(), message, param1);
      }
   }

   public void log(Level level, String message, Object[] params) {
      if (this.logger.isLoggable(level)) {
         this.logger.logp(level, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void log(Level level, String message, Throwable thrown) {
      if (this.logger.isLoggable(level)) {
         this.logger.logp(level, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void finest(String message) {
      if (this.logger.isLoggable(Level.FINEST)) {
         this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void finest(String message, Object[] params) {
      if (this.logger.isLoggable(Level.FINEST)) {
         this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void finest(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.FINEST)) {
         this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void finer(String message) {
      if (this.logger.isLoggable(Level.FINER)) {
         this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void finer(String message, Object[] params) {
      if (this.logger.isLoggable(Level.FINER)) {
         this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void finer(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.FINER)) {
         this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void fine(String message) {
      if (this.logger.isLoggable(Level.FINE)) {
         this.logger.logp(Level.FINE, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void fine(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.FINE)) {
         this.logger.logp(Level.FINE, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void info(String message) {
      if (this.logger.isLoggable(Level.INFO)) {
         this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void info(String message, Object[] params) {
      if (this.logger.isLoggable(Level.INFO)) {
         this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void info(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.INFO)) {
         this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void config(String message) {
      if (this.logger.isLoggable(Level.CONFIG)) {
         this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void config(String message, Object[] params) {
      if (this.logger.isLoggable(Level.CONFIG)) {
         this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void config(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.CONFIG)) {
         this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void warning(String message) {
      if (this.logger.isLoggable(Level.WARNING)) {
         this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void warning(String message, Object[] params) {
      if (this.logger.isLoggable(Level.WARNING)) {
         this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void warning(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.WARNING)) {
         this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public void severe(String message) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), message);
      }
   }

   public void severe(String message, Object[] params) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), message, params);
      }
   }

   public void severe(String message, Throwable thrown) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), message, thrown);
      }
   }

   public boolean isMethodCallLoggable() {
      return this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE);
   }

   public boolean isLoggable(Level level) {
      return this.logger.isLoggable(level);
   }

   public void setLevel(Level level) {
      this.logger.setLevel(level);
   }

   public void entering() {
      if (this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
         this.logger.entering(this.componentClassName, getCallerMethodName());
      }
   }

   public void entering(Object... parameters) {
      if (this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
         this.logger.entering(this.componentClassName, getCallerMethodName(), parameters);
      }
   }

   public void exiting() {
      if (this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
         this.logger.exiting(this.componentClassName, getCallerMethodName());
      }
   }

   public void exiting(Object result) {
      if (this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
         this.logger.exiting(this.componentClassName, getCallerMethodName(), result);
      }
   }

   public <T extends Throwable> T logSevereException(T exception, Throwable cause) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         if (cause == null) {
            this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage());
         } else {
            exception.initCause(cause);
            this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage(), cause);
         }
      }

      return exception;
   }

   public <T extends Throwable> T logSevereException(T exception, boolean logCause) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         if (logCause && exception.getCause() != null) {
            this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
         } else {
            this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage());
         }
      }

      return exception;
   }

   public <T extends Throwable> T logSevereException(T exception) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         if (exception.getCause() == null) {
            this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage());
         } else {
            this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
         }
      }

      return exception;
   }

   public <T extends Throwable> T logException(T exception, Throwable cause, Level level) {
      if (this.logger.isLoggable(level)) {
         if (cause == null) {
            this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage());
         } else {
            exception.initCause(cause);
            this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage(), cause);
         }
      }

      return exception;
   }

   public <T extends Throwable> T logException(T exception, boolean logCause, Level level) {
      if (this.logger.isLoggable(level)) {
         if (logCause && exception.getCause() != null) {
            this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
         } else {
            this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage());
         }
      }

      return exception;
   }

   public <T extends Throwable> T logException(T exception, Level level) {
      if (this.logger.isLoggable(level)) {
         if (exception.getCause() == null) {
            this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage());
         } else {
            this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
         }
      }

      return exception;
   }

   private static String getCallerMethodName() {
      return getStackMethodName(5);
   }

   private static String getStackMethodName(int methodIndexInStack) {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      String methodName;
      if (stack.length > methodIndexInStack + 1) {
         methodName = stack[methodIndexInStack].getMethodName();
      } else {
         methodName = "UNKNOWN METHOD";
      }

      return methodName;
   }

   static {
      METHOD_CALL_LEVEL_VALUE = Level.FINEST;
   }
}
