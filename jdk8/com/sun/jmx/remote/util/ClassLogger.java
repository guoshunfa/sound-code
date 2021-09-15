package com.sun.jmx.remote.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassLogger {
   private static final boolean ok;
   private final String className;
   private final Logger logger;

   public ClassLogger(String var1, String var2) {
      if (ok) {
         this.logger = Logger.getLogger(var1);
      } else {
         this.logger = null;
      }

      this.className = var2;
   }

   public final boolean traceOn() {
      return this.finerOn();
   }

   public final boolean debugOn() {
      return this.finestOn();
   }

   public final boolean warningOn() {
      return ok && this.logger.isLoggable(Level.WARNING);
   }

   public final boolean infoOn() {
      return ok && this.logger.isLoggable(Level.INFO);
   }

   public final boolean configOn() {
      return ok && this.logger.isLoggable(Level.CONFIG);
   }

   public final boolean fineOn() {
      return ok && this.logger.isLoggable(Level.FINE);
   }

   public final boolean finerOn() {
      return ok && this.logger.isLoggable(Level.FINER);
   }

   public final boolean finestOn() {
      return ok && this.logger.isLoggable(Level.FINEST);
   }

   public final void debug(String var1, String var2) {
      this.finest(var1, var2);
   }

   public final void debug(String var1, Throwable var2) {
      this.finest(var1, var2);
   }

   public final void debug(String var1, String var2, Throwable var3) {
      this.finest(var1, var2, var3);
   }

   public final void trace(String var1, String var2) {
      this.finer(var1, var2);
   }

   public final void trace(String var1, Throwable var2) {
      this.finer(var1, var2);
   }

   public final void trace(String var1, String var2, Throwable var3) {
      this.finer(var1, var2, var3);
   }

   public final void error(String var1, String var2) {
      this.severe(var1, var2);
   }

   public final void error(String var1, Throwable var2) {
      this.severe(var1, var2);
   }

   public final void error(String var1, String var2, Throwable var3) {
      this.severe(var1, var2, var3);
   }

   public final void finest(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.FINEST, this.className, var1, var2);
      }

   }

   public final void finest(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.FINEST, this.className, var1, var2.toString(), var2);
      }

   }

   public final void finest(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.FINEST, this.className, var1, var2, var3);
      }

   }

   public final void finer(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.FINER, this.className, var1, var2);
      }

   }

   public final void finer(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.FINER, this.className, var1, var2.toString(), var2);
      }

   }

   public final void finer(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.FINER, this.className, var1, var2, var3);
      }

   }

   public final void fine(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.FINE, this.className, var1, var2);
      }

   }

   public final void fine(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.FINE, this.className, var1, var2.toString(), var2);
      }

   }

   public final void fine(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.FINE, this.className, var1, var2, var3);
      }

   }

   public final void config(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.CONFIG, this.className, var1, var2);
      }

   }

   public final void config(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.CONFIG, this.className, var1, var2.toString(), var2);
      }

   }

   public final void config(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.CONFIG, this.className, var1, var2, var3);
      }

   }

   public final void info(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.INFO, this.className, var1, var2);
      }

   }

   public final void info(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.INFO, this.className, var1, var2.toString(), var2);
      }

   }

   public final void info(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.INFO, this.className, var1, var2, var3);
      }

   }

   public final void warning(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.WARNING, this.className, var1, var2);
      }

   }

   public final void warning(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.WARNING, this.className, var1, var2.toString(), var2);
      }

   }

   public final void warning(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.WARNING, this.className, var1, var2, var3);
      }

   }

   public final void severe(String var1, String var2) {
      if (ok) {
         this.logger.logp(Level.SEVERE, this.className, var1, var2);
      }

   }

   public final void severe(String var1, Throwable var2) {
      if (ok) {
         this.logger.logp(Level.SEVERE, this.className, var1, var2.toString(), var2);
      }

   }

   public final void severe(String var1, String var2, Throwable var3) {
      if (ok) {
         this.logger.logp(Level.SEVERE, this.className, var1, var2, var3);
      }

   }

   static {
      boolean var0 = false;

      try {
         Class var1 = Logger.class;
         var0 = true;
      } catch (Error var2) {
      }

      ok = var0;
   }
}
