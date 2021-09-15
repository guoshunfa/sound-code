package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;

public class IORSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new IORSystemException(var1);
      }
   };
   public static final int ORT_NOT_INITIALIZED = 1398080689;
   public static final int NULL_POA = 1398080690;
   public static final int BAD_MAGIC = 1398080691;
   public static final int STRINGIFY_WRITE_ERROR = 1398080692;
   public static final int TAGGED_PROFILE_TEMPLATE_FACTORY_NOT_FOUND = 1398080693;
   public static final int INVALID_JDK1_3_1_PATCH_LEVEL = 1398080694;
   public static final int GET_LOCAL_SERVANT_FAILURE = 1398080695;
   public static final int ADAPTER_ID_NOT_AVAILABLE = 1398080689;
   public static final int SERVER_ID_NOT_AVAILABLE = 1398080690;
   public static final int ORB_ID_NOT_AVAILABLE = 1398080691;
   public static final int OBJECT_ADAPTER_ID_NOT_AVAILABLE = 1398080692;
   public static final int BAD_OID_IN_IOR_TEMPLATE_LIST = 1398080689;
   public static final int INVALID_TAGGED_PROFILE = 1398080690;
   public static final int BAD_IIOP_ADDRESS_PORT = 1398080691;
   public static final int IOR_MUST_HAVE_IIOP_PROFILE = 1398080689;

   public IORSystemException(Logger var1) {
      super(var1);
   }

   public static IORSystemException get(ORB var0, String var1) {
      IORSystemException var2 = (IORSystemException)var0.getLogWrapper(var1, "IOR", factory);
      return var2;
   }

   public static IORSystemException get(String var0) {
      IORSystemException var1 = (IORSystemException)ORB.staticGetLogWrapper(var0, "IOR", factory);
      return var1;
   }

   public INTERNAL ortNotInitialized(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.ortNotInitialized", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL ortNotInitialized(CompletionStatus var1) {
      return this.ortNotInitialized(var1, (Throwable)null);
   }

   public INTERNAL ortNotInitialized(Throwable var1) {
      return this.ortNotInitialized(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL ortNotInitialized() {
      return this.ortNotInitialized(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL nullPoa(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.nullPoa", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL nullPoa(CompletionStatus var1) {
      return this.nullPoa(var1, (Throwable)null);
   }

   public INTERNAL nullPoa(Throwable var1) {
      return this.nullPoa(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL nullPoa() {
      return this.nullPoa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badMagic(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080691, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "IOR.badMagic", var5, IORSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL badMagic(CompletionStatus var1, Object var2) {
      return this.badMagic(var1, (Throwable)null, var2);
   }

   public INTERNAL badMagic(Throwable var1, Object var2) {
      return this.badMagic(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL badMagic(Object var1) {
      return this.badMagic(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL stringifyWriteError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.stringifyWriteError", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL stringifyWriteError(CompletionStatus var1) {
      return this.stringifyWriteError(var1, (Throwable)null);
   }

   public INTERNAL stringifyWriteError(Throwable var1) {
      return this.stringifyWriteError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL stringifyWriteError() {
      return this.stringifyWriteError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL taggedProfileTemplateFactoryNotFound(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080693, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "IOR.taggedProfileTemplateFactoryNotFound", var5, IORSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL taggedProfileTemplateFactoryNotFound(CompletionStatus var1, Object var2) {
      return this.taggedProfileTemplateFactoryNotFound(var1, (Throwable)null, var2);
   }

   public INTERNAL taggedProfileTemplateFactoryNotFound(Throwable var1, Object var2) {
      return this.taggedProfileTemplateFactoryNotFound(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL taggedProfileTemplateFactoryNotFound(Object var1) {
      return this.taggedProfileTemplateFactoryNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL invalidJdk131PatchLevel(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080694, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "IOR.invalidJdk131PatchLevel", var5, IORSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL invalidJdk131PatchLevel(CompletionStatus var1, Object var2) {
      return this.invalidJdk131PatchLevel(var1, (Throwable)null, var2);
   }

   public INTERNAL invalidJdk131PatchLevel(Throwable var1, Object var2) {
      return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL invalidJdk131PatchLevel(Object var1) {
      return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL getLocalServantFailure(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080695, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "IOR.getLocalServantFailure", var5, IORSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL getLocalServantFailure(CompletionStatus var1, Object var2) {
      return this.getLocalServantFailure(var1, (Throwable)null, var2);
   }

   public INTERNAL getLocalServantFailure(Throwable var1, Object var2) {
      return this.getLocalServantFailure(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL getLocalServantFailure(Object var1) {
      return this.getLocalServantFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_OPERATION adapterIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.adapterIdNotAvailable", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION adapterIdNotAvailable(CompletionStatus var1) {
      return this.adapterIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION adapterIdNotAvailable(Throwable var1) {
      return this.adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION adapterIdNotAvailable() {
      return this.adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION serverIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.serverIdNotAvailable", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION serverIdNotAvailable(CompletionStatus var1) {
      return this.serverIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION serverIdNotAvailable(Throwable var1) {
      return this.serverIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION serverIdNotAvailable() {
      return this.serverIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION orbIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.orbIdNotAvailable", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION orbIdNotAvailable(CompletionStatus var1) {
      return this.orbIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION orbIdNotAvailable(Throwable var1) {
      return this.orbIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION orbIdNotAvailable() {
      return this.orbIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.objectAdapterIdNotAvailable", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus var1) {
      return this.objectAdapterIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION objectAdapterIdNotAvailable(Throwable var1) {
      return this.objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION objectAdapterIdNotAvailable() {
      return this.objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM badOidInIorTemplateList(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.badOidInIorTemplateList", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM badOidInIorTemplateList(CompletionStatus var1) {
      return this.badOidInIorTemplateList(var1, (Throwable)null);
   }

   public BAD_PARAM badOidInIorTemplateList(Throwable var1) {
      return this.badOidInIorTemplateList(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM badOidInIorTemplateList() {
      return this.badOidInIorTemplateList(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidTaggedProfile(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.invalidTaggedProfile", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM invalidTaggedProfile(CompletionStatus var1) {
      return this.invalidTaggedProfile(var1, (Throwable)null);
   }

   public BAD_PARAM invalidTaggedProfile(Throwable var1) {
      return this.invalidTaggedProfile(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM invalidTaggedProfile() {
      return this.invalidTaggedProfile(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM badIiopAddressPort(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398080691, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "IOR.badIiopAddressPort", var5, IORSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM badIiopAddressPort(CompletionStatus var1, Object var2) {
      return this.badIiopAddressPort(var1, (Throwable)null, var2);
   }

   public BAD_PARAM badIiopAddressPort(Throwable var1, Object var2) {
      return this.badIiopAddressPort(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM badIiopAddressPort(Object var1) {
      return this.badIiopAddressPort(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INV_OBJREF iorMustHaveIiopProfile(CompletionStatus var1, Throwable var2) {
      INV_OBJREF var3 = new INV_OBJREF(1398080689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "IOR.iorMustHaveIiopProfile", (Object[])var4, IORSystemException.class, var3);
      }

      return var3;
   }

   public INV_OBJREF iorMustHaveIiopProfile(CompletionStatus var1) {
      return this.iorMustHaveIiopProfile(var1, (Throwable)null);
   }

   public INV_OBJREF iorMustHaveIiopProfile(Throwable var1) {
      return this.iorMustHaveIiopProfile(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_OBJREF iorMustHaveIiopProfile() {
      return this.iorMustHaveIiopProfile(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
