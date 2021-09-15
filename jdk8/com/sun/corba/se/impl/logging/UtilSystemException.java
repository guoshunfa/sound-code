package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.UNKNOWN;

public class UtilSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new UtilSystemException(var1);
      }
   };
   public static final int STUB_FACTORY_COULD_NOT_MAKE_STUB = 1398080889;
   public static final int ERROR_IN_MAKE_STUB_FROM_REPOSITORY_ID = 1398080890;
   public static final int CLASS_CAST_EXCEPTION_IN_LOAD_STUB = 1398080891;
   public static final int EXCEPTION_IN_LOAD_STUB = 1398080892;
   public static final int NO_POA = 1398080890;
   public static final int CONNECT_WRONG_ORB = 1398080891;
   public static final int CONNECT_NO_TIE = 1398080892;
   public static final int CONNECT_TIE_WRONG_ORB = 1398080893;
   public static final int CONNECT_TIE_NO_SERVANT = 1398080894;
   public static final int LOAD_TIE_FAILED = 1398080895;
   public static final int BAD_HEX_DIGIT = 1398080889;
   public static final int UNABLE_LOCATE_VALUE_HELPER = 1398080890;
   public static final int INVALID_INDIRECTION = 1398080891;
   public static final int OBJECT_NOT_CONNECTED = 1398080889;
   public static final int COULD_NOT_LOAD_STUB = 1398080890;
   public static final int OBJECT_NOT_EXPORTED = 1398080891;
   public static final int ERROR_SET_OBJECT_FIELD = 1398080889;
   public static final int ERROR_SET_BOOLEAN_FIELD = 1398080890;
   public static final int ERROR_SET_BYTE_FIELD = 1398080891;
   public static final int ERROR_SET_CHAR_FIELD = 1398080892;
   public static final int ERROR_SET_SHORT_FIELD = 1398080893;
   public static final int ERROR_SET_INT_FIELD = 1398080894;
   public static final int ERROR_SET_LONG_FIELD = 1398080895;
   public static final int ERROR_SET_FLOAT_FIELD = 1398080896;
   public static final int ERROR_SET_DOUBLE_FIELD = 1398080897;
   public static final int ILLEGAL_FIELD_ACCESS = 1398080898;
   public static final int BAD_BEGIN_UNMARSHAL_CUSTOM_VALUE = 1398080899;
   public static final int CLASS_NOT_FOUND = 1398080900;
   public static final int UNKNOWN_SYSEX = 1398080889;

   public UtilSystemException(Logger var1) {
      super(var1);
   }

   public static UtilSystemException get(ORB var0, String var1) {
      UtilSystemException var2 = (UtilSystemException)var0.getLogWrapper(var1, "UTIL", factory);
      return var2;
   }

   public static UtilSystemException get(String var0) {
      UtilSystemException var1 = (UtilSystemException)ORB.staticGetLogWrapper(var0, "UTIL", factory);
      return var1;
   }

   public BAD_OPERATION stubFactoryCouldNotMakeStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080889, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "UTIL.stubFactoryCouldNotMakeStub", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION stubFactoryCouldNotMakeStub(CompletionStatus var1) {
      return this.stubFactoryCouldNotMakeStub(var1, (Throwable)null);
   }

   public BAD_OPERATION stubFactoryCouldNotMakeStub(Throwable var1) {
      return this.stubFactoryCouldNotMakeStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION stubFactoryCouldNotMakeStub() {
      return this.stubFactoryCouldNotMakeStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION errorInMakeStubFromRepositoryId(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080890, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "UTIL.errorInMakeStubFromRepositoryId", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION errorInMakeStubFromRepositoryId(CompletionStatus var1) {
      return this.errorInMakeStubFromRepositoryId(var1, (Throwable)null);
   }

   public BAD_OPERATION errorInMakeStubFromRepositoryId(Throwable var1) {
      return this.errorInMakeStubFromRepositoryId(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION errorInMakeStubFromRepositoryId() {
      return this.errorInMakeStubFromRepositoryId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION classCastExceptionInLoadStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080891, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "UTIL.classCastExceptionInLoadStub", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION classCastExceptionInLoadStub(CompletionStatus var1) {
      return this.classCastExceptionInLoadStub(var1, (Throwable)null);
   }

   public BAD_OPERATION classCastExceptionInLoadStub(Throwable var1) {
      return this.classCastExceptionInLoadStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION classCastExceptionInLoadStub() {
      return this.classCastExceptionInLoadStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION exceptionInLoadStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080892, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "UTIL.exceptionInLoadStub", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION exceptionInLoadStub(CompletionStatus var1) {
      return this.exceptionInLoadStub(var1, (Throwable)null);
   }

   public BAD_OPERATION exceptionInLoadStub(Throwable var1) {
      return this.exceptionInLoadStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION exceptionInLoadStub() {
      return this.exceptionInLoadStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM noPoa(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080890, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.noPoa", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM noPoa(CompletionStatus var1) {
      return this.noPoa(var1, (Throwable)null);
   }

   public BAD_PARAM noPoa(Throwable var1) {
      return this.noPoa(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM noPoa() {
      return this.noPoa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM connectWrongOrb(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080891, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "UTIL.connectWrongOrb", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM connectWrongOrb(CompletionStatus var1) {
      return this.connectWrongOrb(var1, (Throwable)null);
   }

   public BAD_PARAM connectWrongOrb(Throwable var1) {
      return this.connectWrongOrb(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM connectWrongOrb() {
      return this.connectWrongOrb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM connectNoTie(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080892, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.connectNoTie", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM connectNoTie(CompletionStatus var1) {
      return this.connectNoTie(var1, (Throwable)null);
   }

   public BAD_PARAM connectNoTie(Throwable var1) {
      return this.connectNoTie(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM connectNoTie() {
      return this.connectNoTie(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM connectTieWrongOrb(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080893, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.connectTieWrongOrb", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM connectTieWrongOrb(CompletionStatus var1) {
      return this.connectTieWrongOrb(var1, (Throwable)null);
   }

   public BAD_PARAM connectTieWrongOrb(Throwable var1) {
      return this.connectTieWrongOrb(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM connectTieWrongOrb() {
      return this.connectTieWrongOrb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM connectTieNoServant(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080894, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.connectTieNoServant", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM connectTieNoServant(CompletionStatus var1) {
      return this.connectTieNoServant(var1, (Throwable)null);
   }

   public BAD_PARAM connectTieNoServant(Throwable var1) {
      return this.connectTieNoServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM connectTieNoServant() {
      return this.connectTieNoServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM loadTieFailed(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398080895, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "UTIL.loadTieFailed", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM loadTieFailed(CompletionStatus var1, Object var2) {
      return this.loadTieFailed(var1, (Throwable)null, var2);
   }

   public BAD_PARAM loadTieFailed(Throwable var1, Object var2) {
      return this.loadTieFailed(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM loadTieFailed(Object var1) {
      return this.loadTieFailed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION badHexDigit(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398080889, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.badHexDigit", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badHexDigit(CompletionStatus var1) {
      return this.badHexDigit(var1, (Throwable)null);
   }

   public DATA_CONVERSION badHexDigit(Throwable var1) {
      return this.badHexDigit(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badHexDigit() {
      return this.badHexDigit(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unableLocateValueHelper(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398080890, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.unableLocateValueHelper", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL unableLocateValueHelper(CompletionStatus var1) {
      return this.unableLocateValueHelper(var1, (Throwable)null);
   }

   public MARSHAL unableLocateValueHelper(Throwable var1) {
      return this.unableLocateValueHelper(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL unableLocateValueHelper() {
      return this.unableLocateValueHelper(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL invalidIndirection(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398080891, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "UTIL.invalidIndirection", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL invalidIndirection(CompletionStatus var1, Object var2) {
      return this.invalidIndirection(var1, (Throwable)null, var2);
   }

   public MARSHAL invalidIndirection(Throwable var1, Object var2) {
      return this.invalidIndirection(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL invalidIndirection(Object var1) {
      return this.invalidIndirection(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INV_OBJREF objectNotConnected(CompletionStatus var1, Throwable var2, Object var3) {
      INV_OBJREF var4 = new INV_OBJREF(1398080889, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "UTIL.objectNotConnected", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public INV_OBJREF objectNotConnected(CompletionStatus var1, Object var2) {
      return this.objectNotConnected(var1, (Throwable)null, var2);
   }

   public INV_OBJREF objectNotConnected(Throwable var1, Object var2) {
      return this.objectNotConnected(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INV_OBJREF objectNotConnected(Object var1) {
      return this.objectNotConnected(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INV_OBJREF couldNotLoadStub(CompletionStatus var1, Throwable var2, Object var3) {
      INV_OBJREF var4 = new INV_OBJREF(1398080890, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "UTIL.couldNotLoadStub", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public INV_OBJREF couldNotLoadStub(CompletionStatus var1, Object var2) {
      return this.couldNotLoadStub(var1, (Throwable)null, var2);
   }

   public INV_OBJREF couldNotLoadStub(Throwable var1, Object var2) {
      return this.couldNotLoadStub(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INV_OBJREF couldNotLoadStub(Object var1) {
      return this.couldNotLoadStub(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INV_OBJREF objectNotExported(CompletionStatus var1, Throwable var2, Object var3) {
      INV_OBJREF var4 = new INV_OBJREF(1398080891, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "UTIL.objectNotExported", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public INV_OBJREF objectNotExported(CompletionStatus var1, Object var2) {
      return this.objectNotExported(var1, (Throwable)null, var2);
   }

   public INV_OBJREF objectNotExported(Throwable var1, Object var2) {
      return this.objectNotExported(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INV_OBJREF objectNotExported(Object var1) {
      return this.objectNotExported(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL errorSetObjectField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080889, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetObjectField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetObjectField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetObjectField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetObjectField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetObjectField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetObjectField(Object var1, Object var2, Object var3) {
      return this.errorSetObjectField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetBooleanField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080890, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetBooleanField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetBooleanField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetBooleanField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetBooleanField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetBooleanField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetBooleanField(Object var1, Object var2, Object var3) {
      return this.errorSetBooleanField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetByteField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080891, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetByteField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetByteField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetByteField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetByteField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetByteField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetByteField(Object var1, Object var2, Object var3) {
      return this.errorSetByteField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetCharField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080892, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetCharField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetCharField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetCharField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetCharField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetCharField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetCharField(Object var1, Object var2, Object var3) {
      return this.errorSetCharField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetShortField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080893, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetShortField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetShortField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetShortField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetShortField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetShortField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetShortField(Object var1, Object var2, Object var3) {
      return this.errorSetShortField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetIntField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080894, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetIntField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetIntField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetIntField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetIntField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetIntField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetIntField(Object var1, Object var2, Object var3) {
      return this.errorSetIntField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetLongField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080895, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetLongField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetLongField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetLongField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetLongField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetLongField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetLongField(Object var1, Object var2, Object var3) {
      return this.errorSetLongField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetFloatField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080896, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetFloatField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetFloatField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetFloatField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetFloatField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetFloatField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetFloatField(Object var1, Object var2, Object var3) {
      return this.errorSetFloatField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL errorSetDoubleField(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398080897, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "UTIL.errorSetDoubleField", var7, UtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL errorSetDoubleField(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.errorSetDoubleField(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL errorSetDoubleField(Throwable var1, Object var2, Object var3, Object var4) {
      return this.errorSetDoubleField(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL errorSetDoubleField(Object var1, Object var2, Object var3) {
      return this.errorSetDoubleField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL illegalFieldAccess(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080898, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "UTIL.illegalFieldAccess", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL illegalFieldAccess(CompletionStatus var1, Object var2) {
      return this.illegalFieldAccess(var1, (Throwable)null, var2);
   }

   public INTERNAL illegalFieldAccess(Throwable var1, Object var2) {
      return this.illegalFieldAccess(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL illegalFieldAccess(Object var1) {
      return this.illegalFieldAccess(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL badBeginUnmarshalCustomValue(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080899, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.badBeginUnmarshalCustomValue", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badBeginUnmarshalCustomValue(CompletionStatus var1) {
      return this.badBeginUnmarshalCustomValue(var1, (Throwable)null);
   }

   public INTERNAL badBeginUnmarshalCustomValue(Throwable var1) {
      return this.badBeginUnmarshalCustomValue(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badBeginUnmarshalCustomValue() {
      return this.badBeginUnmarshalCustomValue(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL classNotFound(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080900, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "UTIL.classNotFound", var5, UtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL classNotFound(CompletionStatus var1, Object var2) {
      return this.classNotFound(var1, (Throwable)null, var2);
   }

   public INTERNAL classNotFound(Throwable var1, Object var2) {
      return this.classNotFound(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL classNotFound(Object var1) {
      return this.classNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public UNKNOWN unknownSysex(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080889, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "UTIL.unknownSysex", (Object[])var4, UtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownSysex(CompletionStatus var1) {
      return this.unknownSysex(var1, (Throwable)null);
   }

   public UNKNOWN unknownSysex(Throwable var1) {
      return this.unknownSysex(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownSysex() {
      return this.unknownSysex(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
