package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;

public class ActivationSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new ActivationSystemException(var1);
      }
   };
   public static final int CANNOT_READ_REPOSITORY_DB = 1398079889;
   public static final int CANNOT_ADD_INITIAL_NAMING = 1398079890;
   public static final int CANNOT_WRITE_REPOSITORY_DB = 1398079889;
   public static final int SERVER_NOT_EXPECTED_TO_REGISTER = 1398079891;
   public static final int UNABLE_TO_START_PROCESS = 1398079892;
   public static final int SERVER_NOT_RUNNING = 1398079894;
   public static final int ERROR_IN_BAD_SERVER_ID_HANDLER = 1398079889;

   public ActivationSystemException(Logger var1) {
      super(var1);
   }

   public static ActivationSystemException get(ORB var0, String var1) {
      ActivationSystemException var2 = (ActivationSystemException)var0.getLogWrapper(var1, "ACTIVATION", factory);
      return var2;
   }

   public static ActivationSystemException get(String var0) {
      ActivationSystemException var1 = (ActivationSystemException)ORB.staticGetLogWrapper(var0, "ACTIVATION", factory);
      return var1;
   }

   public INITIALIZE cannotReadRepositoryDb(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079889, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.cannotReadRepositoryDb", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE cannotReadRepositoryDb(CompletionStatus var1) {
      return this.cannotReadRepositoryDb(var1, (Throwable)null);
   }

   public INITIALIZE cannotReadRepositoryDb(Throwable var1) {
      return this.cannotReadRepositoryDb(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE cannotReadRepositoryDb() {
      return this.cannotReadRepositoryDb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE cannotAddInitialNaming(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079890, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.cannotAddInitialNaming", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE cannotAddInitialNaming(CompletionStatus var1) {
      return this.cannotAddInitialNaming(var1, (Throwable)null);
   }

   public INITIALIZE cannotAddInitialNaming(Throwable var1) {
      return this.cannotAddInitialNaming(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE cannotAddInitialNaming() {
      return this.cannotAddInitialNaming(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL cannotWriteRepositoryDb(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079889, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.cannotWriteRepositoryDb", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL cannotWriteRepositoryDb(CompletionStatus var1) {
      return this.cannotWriteRepositoryDb(var1, (Throwable)null);
   }

   public INTERNAL cannotWriteRepositoryDb(Throwable var1) {
      return this.cannotWriteRepositoryDb(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL cannotWriteRepositoryDb() {
      return this.cannotWriteRepositoryDb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL serverNotExpectedToRegister(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079891, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.serverNotExpectedToRegister", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL serverNotExpectedToRegister(CompletionStatus var1) {
      return this.serverNotExpectedToRegister(var1, (Throwable)null);
   }

   public INTERNAL serverNotExpectedToRegister(Throwable var1) {
      return this.serverNotExpectedToRegister(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL serverNotExpectedToRegister() {
      return this.serverNotExpectedToRegister(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unableToStartProcess(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079892, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.unableToStartProcess", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL unableToStartProcess(CompletionStatus var1) {
      return this.unableToStartProcess(var1, (Throwable)null);
   }

   public INTERNAL unableToStartProcess(Throwable var1) {
      return this.unableToStartProcess(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL unableToStartProcess() {
      return this.unableToStartProcess(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL serverNotRunning(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079894, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.serverNotRunning", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL serverNotRunning(CompletionStatus var1) {
      return this.serverNotRunning(var1, (Throwable)null);
   }

   public INTERNAL serverNotRunning(Throwable var1) {
      return this.serverNotRunning(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL serverNotRunning() {
      return this.serverNotRunning(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST errorInBadServerIdHandler(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079889, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ACTIVATION.errorInBadServerIdHandler", (Object[])var4, ActivationSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST errorInBadServerIdHandler(CompletionStatus var1) {
      return this.errorInBadServerIdHandler(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST errorInBadServerIdHandler(Throwable var1) {
      return this.errorInBadServerIdHandler(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST errorInBadServerIdHandler() {
      return this.errorInBadServerIdHandler(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
