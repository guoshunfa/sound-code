package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.UNKNOWN;

public class InterceptorsSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new InterceptorsSystemException(var1);
      }
   };
   public static final int TYPE_OUT_OF_RANGE = 1398080289;
   public static final int NAME_NULL = 1398080290;
   public static final int RIR_INVALID_PRE_INIT = 1398080289;
   public static final int BAD_STATE1 = 1398080290;
   public static final int BAD_STATE2 = 1398080291;
   public static final int IOEXCEPTION_DURING_CANCEL_REQUEST = 1398080289;
   public static final int EXCEPTION_WAS_NULL = 1398080289;
   public static final int OBJECT_HAS_NO_DELEGATE = 1398080290;
   public static final int DELEGATE_NOT_CLIENTSUB = 1398080291;
   public static final int OBJECT_NOT_OBJECTIMPL = 1398080292;
   public static final int EXCEPTION_INVALID = 1398080293;
   public static final int REPLY_STATUS_NOT_INIT = 1398080294;
   public static final int EXCEPTION_IN_ARGUMENTS = 1398080295;
   public static final int EXCEPTION_IN_EXCEPTIONS = 1398080296;
   public static final int EXCEPTION_IN_CONTEXTS = 1398080297;
   public static final int EXCEPTION_WAS_NULL_2 = 1398080298;
   public static final int SERVANT_INVALID = 1398080299;
   public static final int CANT_POP_ONLY_PICURRENT = 1398080300;
   public static final int CANT_POP_ONLY_CURRENT_2 = 1398080301;
   public static final int PI_DSI_RESULT_IS_NULL = 1398080302;
   public static final int PI_DII_RESULT_IS_NULL = 1398080303;
   public static final int EXCEPTION_UNAVAILABLE = 1398080304;
   public static final int CLIENT_INFO_STACK_NULL = 1398080305;
   public static final int SERVER_INFO_STACK_NULL = 1398080306;
   public static final int MARK_AND_RESET_FAILED = 1398080307;
   public static final int SLOT_TABLE_INVARIANT = 1398080308;
   public static final int INTERCEPTOR_LIST_LOCKED = 1398080309;
   public static final int SORT_SIZE_MISMATCH = 1398080310;
   public static final int PI_ORB_NOT_POLICY_BASED = 1398080289;
   public static final int ORBINITINFO_INVALID = 1398080289;
   public static final int UNKNOWN_REQUEST_INVOKE = 1398080289;

   public InterceptorsSystemException(Logger var1) {
      super(var1);
   }

   public static InterceptorsSystemException get(ORB var0, String var1) {
      InterceptorsSystemException var2 = (InterceptorsSystemException)var0.getLogWrapper(var1, "INTERCEPTORS", factory);
      return var2;
   }

   public static InterceptorsSystemException get(String var0) {
      InterceptorsSystemException var1 = (InterceptorsSystemException)ORB.staticGetLogWrapper(var0, "INTERCEPTORS", factory);
      return var1;
   }

   public BAD_PARAM typeOutOfRange(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398080289, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "INTERCEPTORS.typeOutOfRange", var5, InterceptorsSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM typeOutOfRange(CompletionStatus var1, Object var2) {
      return this.typeOutOfRange(var1, (Throwable)null, var2);
   }

   public BAD_PARAM typeOutOfRange(Throwable var1, Object var2) {
      return this.typeOutOfRange(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM typeOutOfRange(Object var1) {
      return this.typeOutOfRange(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM nameNull(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080290, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.nameNull", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM nameNull(CompletionStatus var1) {
      return this.nameNull(var1, (Throwable)null);
   }

   public BAD_PARAM nameNull(Throwable var1) {
      return this.nameNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM nameNull() {
      return this.nameNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER rirInvalidPreInit(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398080289, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.rirInvalidPreInit", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER rirInvalidPreInit(CompletionStatus var1) {
      return this.rirInvalidPreInit(var1, (Throwable)null);
   }

   public BAD_INV_ORDER rirInvalidPreInit(Throwable var1) {
      return this.rirInvalidPreInit(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER rirInvalidPreInit() {
      return this.rirInvalidPreInit(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badState1(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      BAD_INV_ORDER var5 = new BAD_INV_ORDER(1398080290, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "INTERCEPTORS.badState1", var6, InterceptorsSystemException.class, var5);
      }

      return var5;
   }

   public BAD_INV_ORDER badState1(CompletionStatus var1, Object var2, Object var3) {
      return this.badState1(var1, (Throwable)null, var2, var3);
   }

   public BAD_INV_ORDER badState1(Throwable var1, Object var2, Object var3) {
      return this.badState1(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public BAD_INV_ORDER badState1(Object var1, Object var2) {
      return this.badState1(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public BAD_INV_ORDER badState2(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      BAD_INV_ORDER var6 = new BAD_INV_ORDER(1398080291, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "INTERCEPTORS.badState2", var7, InterceptorsSystemException.class, var6);
      }

      return var6;
   }

   public BAD_INV_ORDER badState2(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.badState2(var1, (Throwable)null, var2, var3, var4);
   }

   public BAD_INV_ORDER badState2(Throwable var1, Object var2, Object var3, Object var4) {
      return this.badState2(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public BAD_INV_ORDER badState2(Object var1, Object var2, Object var3) {
      return this.badState2(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public COMM_FAILURE ioexceptionDuringCancelRequest(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398080289, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.ioexceptionDuringCancelRequest", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE ioexceptionDuringCancelRequest(CompletionStatus var1) {
      return this.ioexceptionDuringCancelRequest(var1, (Throwable)null);
   }

   public COMM_FAILURE ioexceptionDuringCancelRequest(Throwable var1) {
      return this.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE ioexceptionDuringCancelRequest() {
      return this.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionWasNull(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080289, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionWasNull", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionWasNull(CompletionStatus var1) {
      return this.exceptionWasNull(var1, (Throwable)null);
   }

   public INTERNAL exceptionWasNull(Throwable var1) {
      return this.exceptionWasNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionWasNull() {
      return this.exceptionWasNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL objectHasNoDelegate(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080290, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.objectHasNoDelegate", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL objectHasNoDelegate(CompletionStatus var1) {
      return this.objectHasNoDelegate(var1, (Throwable)null);
   }

   public INTERNAL objectHasNoDelegate(Throwable var1) {
      return this.objectHasNoDelegate(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL objectHasNoDelegate() {
      return this.objectHasNoDelegate(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL delegateNotClientsub(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080291, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.delegateNotClientsub", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL delegateNotClientsub(CompletionStatus var1) {
      return this.delegateNotClientsub(var1, (Throwable)null);
   }

   public INTERNAL delegateNotClientsub(Throwable var1) {
      return this.delegateNotClientsub(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL delegateNotClientsub() {
      return this.delegateNotClientsub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL objectNotObjectimpl(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080292, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.objectNotObjectimpl", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL objectNotObjectimpl(CompletionStatus var1) {
      return this.objectNotObjectimpl(var1, (Throwable)null);
   }

   public INTERNAL objectNotObjectimpl(Throwable var1) {
      return this.objectNotObjectimpl(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL objectNotObjectimpl() {
      return this.objectNotObjectimpl(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionInvalid(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080293, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInvalid", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionInvalid(CompletionStatus var1) {
      return this.exceptionInvalid(var1, (Throwable)null);
   }

   public INTERNAL exceptionInvalid(Throwable var1) {
      return this.exceptionInvalid(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionInvalid() {
      return this.exceptionInvalid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL replyStatusNotInit(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080294, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.replyStatusNotInit", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL replyStatusNotInit(CompletionStatus var1) {
      return this.replyStatusNotInit(var1, (Throwable)null);
   }

   public INTERNAL replyStatusNotInit(Throwable var1) {
      return this.replyStatusNotInit(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL replyStatusNotInit() {
      return this.replyStatusNotInit(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionInArguments(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080295, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInArguments", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionInArguments(CompletionStatus var1) {
      return this.exceptionInArguments(var1, (Throwable)null);
   }

   public INTERNAL exceptionInArguments(Throwable var1) {
      return this.exceptionInArguments(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionInArguments() {
      return this.exceptionInArguments(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionInExceptions(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080296, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInExceptions", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionInExceptions(CompletionStatus var1) {
      return this.exceptionInExceptions(var1, (Throwable)null);
   }

   public INTERNAL exceptionInExceptions(Throwable var1) {
      return this.exceptionInExceptions(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionInExceptions() {
      return this.exceptionInExceptions(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionInContexts(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080297, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInContexts", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionInContexts(CompletionStatus var1) {
      return this.exceptionInContexts(var1, (Throwable)null);
   }

   public INTERNAL exceptionInContexts(Throwable var1) {
      return this.exceptionInContexts(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionInContexts() {
      return this.exceptionInContexts(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionWasNull2(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080298, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionWasNull2", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionWasNull2(CompletionStatus var1) {
      return this.exceptionWasNull2(var1, (Throwable)null);
   }

   public INTERNAL exceptionWasNull2(Throwable var1) {
      return this.exceptionWasNull2(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionWasNull2() {
      return this.exceptionWasNull2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL servantInvalid(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080299, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.servantInvalid", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL servantInvalid(CompletionStatus var1) {
      return this.servantInvalid(var1, (Throwable)null);
   }

   public INTERNAL servantInvalid(Throwable var1) {
      return this.servantInvalid(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL servantInvalid() {
      return this.servantInvalid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL cantPopOnlyPicurrent(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080300, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.cantPopOnlyPicurrent", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL cantPopOnlyPicurrent(CompletionStatus var1) {
      return this.cantPopOnlyPicurrent(var1, (Throwable)null);
   }

   public INTERNAL cantPopOnlyPicurrent(Throwable var1) {
      return this.cantPopOnlyPicurrent(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL cantPopOnlyPicurrent() {
      return this.cantPopOnlyPicurrent(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL cantPopOnlyCurrent2(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080301, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.cantPopOnlyCurrent2", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL cantPopOnlyCurrent2(CompletionStatus var1) {
      return this.cantPopOnlyCurrent2(var1, (Throwable)null);
   }

   public INTERNAL cantPopOnlyCurrent2(Throwable var1) {
      return this.cantPopOnlyCurrent2(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL cantPopOnlyCurrent2() {
      return this.cantPopOnlyCurrent2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL piDsiResultIsNull(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080302, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.piDsiResultIsNull", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL piDsiResultIsNull(CompletionStatus var1) {
      return this.piDsiResultIsNull(var1, (Throwable)null);
   }

   public INTERNAL piDsiResultIsNull(Throwable var1) {
      return this.piDsiResultIsNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL piDsiResultIsNull() {
      return this.piDsiResultIsNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL piDiiResultIsNull(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080303, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.piDiiResultIsNull", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL piDiiResultIsNull(CompletionStatus var1) {
      return this.piDiiResultIsNull(var1, (Throwable)null);
   }

   public INTERNAL piDiiResultIsNull(Throwable var1) {
      return this.piDiiResultIsNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL piDiiResultIsNull() {
      return this.piDiiResultIsNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionUnavailable(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080304, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.exceptionUnavailable", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionUnavailable(CompletionStatus var1) {
      return this.exceptionUnavailable(var1, (Throwable)null);
   }

   public INTERNAL exceptionUnavailable(Throwable var1) {
      return this.exceptionUnavailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionUnavailable() {
      return this.exceptionUnavailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL clientInfoStackNull(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080305, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.clientInfoStackNull", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL clientInfoStackNull(CompletionStatus var1) {
      return this.clientInfoStackNull(var1, (Throwable)null);
   }

   public INTERNAL clientInfoStackNull(Throwable var1) {
      return this.clientInfoStackNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL clientInfoStackNull() {
      return this.clientInfoStackNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL serverInfoStackNull(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080306, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.serverInfoStackNull", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL serverInfoStackNull(CompletionStatus var1) {
      return this.serverInfoStackNull(var1, (Throwable)null);
   }

   public INTERNAL serverInfoStackNull(Throwable var1) {
      return this.serverInfoStackNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL serverInfoStackNull() {
      return this.serverInfoStackNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL markAndResetFailed(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080307, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.markAndResetFailed", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL markAndResetFailed(CompletionStatus var1) {
      return this.markAndResetFailed(var1, (Throwable)null);
   }

   public INTERNAL markAndResetFailed(Throwable var1) {
      return this.markAndResetFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL markAndResetFailed() {
      return this.markAndResetFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL slotTableInvariant(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398080308, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "INTERCEPTORS.slotTableInvariant", var6, InterceptorsSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL slotTableInvariant(CompletionStatus var1, Object var2, Object var3) {
      return this.slotTableInvariant(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL slotTableInvariant(Throwable var1, Object var2, Object var3) {
      return this.slotTableInvariant(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL slotTableInvariant(Object var1, Object var2) {
      return this.slotTableInvariant(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL interceptorListLocked(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080309, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.interceptorListLocked", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL interceptorListLocked(CompletionStatus var1) {
      return this.interceptorListLocked(var1, (Throwable)null);
   }

   public INTERNAL interceptorListLocked(Throwable var1) {
      return this.interceptorListLocked(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL interceptorListLocked() {
      return this.interceptorListLocked(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL sortSizeMismatch(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080310, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.sortSizeMismatch", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL sortSizeMismatch(CompletionStatus var1) {
      return this.sortSizeMismatch(var1, (Throwable)null);
   }

   public INTERNAL sortSizeMismatch(Throwable var1) {
      return this.sortSizeMismatch(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL sortSizeMismatch() {
      return this.sortSizeMismatch(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT piOrbNotPolicyBased(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398080289, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "INTERCEPTORS.piOrbNotPolicyBased", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT piOrbNotPolicyBased(CompletionStatus var1) {
      return this.piOrbNotPolicyBased(var1, (Throwable)null);
   }

   public NO_IMPLEMENT piOrbNotPolicyBased(Throwable var1) {
      return this.piOrbNotPolicyBased(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT piOrbNotPolicyBased() {
      return this.piOrbNotPolicyBased(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST orbinitinfoInvalid(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398080289, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "INTERCEPTORS.orbinitinfoInvalid", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST orbinitinfoInvalid(CompletionStatus var1) {
      return this.orbinitinfoInvalid(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST orbinitinfoInvalid(Throwable var1) {
      return this.orbinitinfoInvalid(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST orbinitinfoInvalid() {
      return this.orbinitinfoInvalid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownRequestInvoke(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080289, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "INTERCEPTORS.unknownRequestInvoke", (Object[])var4, InterceptorsSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownRequestInvoke(CompletionStatus var1) {
      return this.unknownRequestInvoke(var1, (Throwable)null);
   }

   public UNKNOWN unknownRequestInvoke(Throwable var1) {
      return this.unknownRequestInvoke(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownRequestInvoke() {
      return this.unknownRequestInvoke(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
