package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_CONTEXT;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INTF_REPOS;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_RESOURCES;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;

public class OMGSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new OMGSystemException(var1);
      }
   };
   public static final int IDL_CONTEXT_NOT_FOUND = 1330446337;
   public static final int NO_MATCHING_IDL_CONTEXT = 1330446338;
   public static final int DEP_PREVENT_DESTRUCTION = 1330446337;
   public static final int DESTROY_INDESTRUCTIBLE = 1330446338;
   public static final int SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK = 1330446339;
   public static final int BAD_OPERATION_AFTER_SHUTDOWN = 1330446340;
   public static final int BAD_INVOKE = 1330446341;
   public static final int BAD_SET_SERVANT_MANAGER = 1330446342;
   public static final int BAD_ARGUMENTS_CALL = 1330446343;
   public static final int BAD_CTX_CALL = 1330446344;
   public static final int BAD_RESULT_CALL = 1330446345;
   public static final int BAD_SEND = 1330446346;
   public static final int BAD_POLL_BEFORE = 1330446347;
   public static final int BAD_POLL_AFTER = 1330446348;
   public static final int BAD_POLL_SYNC = 1330446349;
   public static final int INVALID_PI_CALL1 = 1330446350;
   public static final int INVALID_PI_CALL2 = 1330446350;
   public static final int INVALID_PI_CALL3 = 1330446350;
   public static final int INVALID_PI_CALL4 = 1330446350;
   public static final int SERVICE_CONTEXT_ADD_FAILED = 1330446351;
   public static final int POLICY_FACTORY_REG_FAILED = 1330446352;
   public static final int CREATE_POA_DESTROY = 1330446353;
   public static final int PRIORITY_REASSIGN = 1330446354;
   public static final int XA_START_OUTSIZE = 1330446355;
   public static final int XA_START_PROTO = 1330446356;
   public static final int BAD_SERVANT_MANAGER_TYPE = 1330446337;
   public static final int OPERATION_UNKNOWN_TO_TARGET = 1330446338;
   public static final int UNABLE_REGISTER_VALUE_FACTORY = 1330446337;
   public static final int RID_ALREADY_DEFINED = 1330446338;
   public static final int NAME_USED_IFR = 1330446339;
   public static final int TARGET_NOT_CONTAINER = 1330446340;
   public static final int NAME_CLASH = 1330446341;
   public static final int NOT_SERIALIZABLE = 1330446342;
   public static final int SO_BAD_SCHEME_NAME = 1330446343;
   public static final int SO_BAD_ADDRESS = 1330446344;
   public static final int SO_BAD_SCHEMA_SPECIFIC = 1330446345;
   public static final int SO_NON_SPECIFIC = 1330446346;
   public static final int IR_DERIVE_ABS_INT_BASE = 1330446347;
   public static final int IR_VALUE_SUPPORT = 1330446348;
   public static final int INCOMPLETE_TYPECODE = 1330446349;
   public static final int INVALID_OBJECT_ID = 1330446350;
   public static final int TYPECODE_BAD_NAME = 1330446351;
   public static final int TYPECODE_BAD_REPID = 1330446352;
   public static final int TYPECODE_INV_MEMBER = 1330446353;
   public static final int TC_UNION_DUP_LABEL = 1330446354;
   public static final int TC_UNION_INCOMPATIBLE = 1330446355;
   public static final int TC_UNION_BAD_DISC = 1330446356;
   public static final int SET_EXCEPTION_BAD_ANY = 1330446357;
   public static final int SET_EXCEPTION_UNLISTED = 1330446358;
   public static final int NO_CLIENT_WCHAR_CODESET_CTX = 1330446359;
   public static final int ILLEGAL_SERVICE_CONTEXT = 1330446360;
   public static final int ENUM_OUT_OF_RANGE = 1330446361;
   public static final int INVALID_SERVICE_CONTEXT_ID = 1330446362;
   public static final int RIR_WITH_NULL_OBJECT = 1330446363;
   public static final int INVALID_COMPONENT_ID = 1330446364;
   public static final int INVALID_PROFILE_ID = 1330446365;
   public static final int POLICY_TYPE_DUPLICATE = 1330446366;
   public static final int BAD_ONEWAY_DEFINITION = 1330446367;
   public static final int DII_FOR_IMPLICIT_OPERATION = 1330446368;
   public static final int XA_CALL_INVAL = 1330446369;
   public static final int UNION_BAD_DISCRIMINATOR = 1330446370;
   public static final int CTX_ILLEGAL_PROPERTY_NAME = 1330446371;
   public static final int CTX_ILLEGAL_SEARCH_STRING = 1330446372;
   public static final int CTX_ILLEGAL_NAME = 1330446373;
   public static final int CTX_NON_EMPTY = 1330446374;
   public static final int INVALID_STREAM_FORMAT_VERSION = 1330446375;
   public static final int NOT_A_VALUEOUTPUTSTREAM = 1330446376;
   public static final int NOT_A_VALUEINPUTSTREAM = 1330446377;
   public static final int MARSHALL_INCOMPLETE_TYPECODE = 1330446337;
   public static final int BAD_MEMBER_TYPECODE = 1330446338;
   public static final int ILLEGAL_PARAMETER = 1330446339;
   public static final int CHAR_NOT_IN_CODESET = 1330446337;
   public static final int PRIORITY_MAP_FAILRE = 1330446338;
   public static final int NO_USABLE_PROFILE = 1330446337;
   public static final int PRIORITY_RANGE_RESTRICT = 1330446337;
   public static final int NO_SERVER_WCHAR_CODESET_CMP = 1330446337;
   public static final int CODESET_COMPONENT_REQUIRED = 1330446338;
   public static final int IOR_POLICY_RECONCILE_ERROR = 1330446337;
   public static final int POLICY_UNKNOWN = 1330446338;
   public static final int NO_POLICY_FACTORY = 1330446339;
   public static final int XA_RMERR = 1330446337;
   public static final int XA_RMFAIL = 1330446338;
   public static final int NO_IR = 1330446337;
   public static final int NO_INTERFACE_IN_IR = 1330446338;
   public static final int UNABLE_LOCATE_VALUE_FACTORY = 1330446337;
   public static final int SET_RESULT_BEFORE_CTX = 1330446338;
   public static final int BAD_NVLIST = 1330446339;
   public static final int NOT_AN_OBJECT_IMPL = 1330446340;
   public static final int WCHAR_BAD_GIOP_VERSION_SENT = 1330446341;
   public static final int WCHAR_BAD_GIOP_VERSION_RETURNED = 1330446342;
   public static final int UNSUPPORTED_FORMAT_VERSION = 1330446343;
   public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1 = 1330446344;
   public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE2 = 1330446344;
   public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE3 = 1330446344;
   public static final int MISSING_LOCAL_VALUE_IMPL = 1330446337;
   public static final int INCOMPATIBLE_VALUE_IMPL = 1330446338;
   public static final int NO_USABLE_PROFILE_2 = 1330446339;
   public static final int DII_LOCAL_OBJECT = 1330446340;
   public static final int BIO_RESET = 1330446341;
   public static final int BIO_META_NOT_AVAILABLE = 1330446342;
   public static final int BIO_GENOMIC_NO_ITERATOR = 1330446343;
   public static final int PI_OPERATION_NOT_SUPPORTED1 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED2 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED3 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED4 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED5 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED6 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED7 = 1330446337;
   public static final int PI_OPERATION_NOT_SUPPORTED8 = 1330446337;
   public static final int NO_CONNECTION_PRIORITY = 1330446338;
   public static final int XA_RB = 1330446337;
   public static final int XA_NOTA = 1330446338;
   public static final int XA_END_TRUE_ROLLBACK_DEFERRED = 1330446339;
   public static final int POA_REQUEST_DISCARD = 1330446337;
   public static final int NO_USABLE_PROFILE_3 = 1330446338;
   public static final int REQUEST_CANCELLED = 1330446339;
   public static final int POA_DESTROYED = 1330446340;
   public static final int UNREGISTERED_VALUE_AS_OBJREF = 1330446337;
   public static final int NO_OBJECT_ADAPTOR = 1330446338;
   public static final int BIO_NOT_AVAILABLE = 1330446339;
   public static final int OBJECT_ADAPTER_INACTIVE = 1330446340;
   public static final int ADAPTER_ACTIVATOR_EXCEPTION = 1330446337;
   public static final int BAD_SERVANT_TYPE = 1330446338;
   public static final int NO_DEFAULT_SERVANT = 1330446339;
   public static final int NO_SERVANT_MANAGER = 1330446340;
   public static final int BAD_POLICY_INCARNATE = 1330446341;
   public static final int PI_EXC_COMP_ESTABLISHED = 1330446342;
   public static final int NULL_SERVANT_RETURNED = 1330446343;
   public static final int UNKNOWN_USER_EXCEPTION = 1330446337;
   public static final int UNSUPPORTED_SYSTEM_EXCEPTION = 1330446338;
   public static final int PI_UNKNOWN_USER_EXCEPTION = 1330446339;

   public OMGSystemException(Logger var1) {
      super(var1);
   }

   public static OMGSystemException get(ORB var0, String var1) {
      OMGSystemException var2 = (OMGSystemException)var0.getLogWrapper(var1, "OMG", factory);
      return var2;
   }

   public static OMGSystemException get(String var0) {
      OMGSystemException var1 = (OMGSystemException)ORB.staticGetLogWrapper(var0, "OMG", factory);
      return var1;
   }

   public BAD_CONTEXT idlContextNotFound(CompletionStatus var1, Throwable var2) {
      BAD_CONTEXT var3 = new BAD_CONTEXT(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.idlContextNotFound", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_CONTEXT idlContextNotFound(CompletionStatus var1) {
      return this.idlContextNotFound(var1, (Throwable)null);
   }

   public BAD_CONTEXT idlContextNotFound(Throwable var1) {
      return this.idlContextNotFound(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_CONTEXT idlContextNotFound() {
      return this.idlContextNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_CONTEXT noMatchingIdlContext(CompletionStatus var1, Throwable var2) {
      BAD_CONTEXT var3 = new BAD_CONTEXT(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noMatchingIdlContext", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_CONTEXT noMatchingIdlContext(CompletionStatus var1) {
      return this.noMatchingIdlContext(var1, (Throwable)null);
   }

   public BAD_CONTEXT noMatchingIdlContext(Throwable var1) {
      return this.noMatchingIdlContext(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_CONTEXT noMatchingIdlContext() {
      return this.noMatchingIdlContext(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER depPreventDestruction(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.depPreventDestruction", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER depPreventDestruction(CompletionStatus var1) {
      return this.depPreventDestruction(var1, (Throwable)null);
   }

   public BAD_INV_ORDER depPreventDestruction(Throwable var1) {
      return this.depPreventDestruction(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER depPreventDestruction() {
      return this.depPreventDestruction(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER destroyIndestructible(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.destroyIndestructible", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER destroyIndestructible(CompletionStatus var1) {
      return this.destroyIndestructible(var1, (Throwable)null);
   }

   public BAD_INV_ORDER destroyIndestructible(Throwable var1) {
      return this.destroyIndestructible(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER destroyIndestructible() {
      return this.destroyIndestructible(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.shutdownWaitForCompletionDeadlock", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(CompletionStatus var1) {
      return this.shutdownWaitForCompletionDeadlock(var1, (Throwable)null);
   }

   public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(Throwable var1) {
      return this.shutdownWaitForCompletionDeadlock(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER shutdownWaitForCompletionDeadlock() {
      return this.shutdownWaitForCompletionDeadlock(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badOperationAfterShutdown(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badOperationAfterShutdown", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badOperationAfterShutdown(CompletionStatus var1) {
      return this.badOperationAfterShutdown(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badOperationAfterShutdown(Throwable var1) {
      return this.badOperationAfterShutdown(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badOperationAfterShutdown() {
      return this.badOperationAfterShutdown(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badInvoke(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446341, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badInvoke", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badInvoke(CompletionStatus var1) {
      return this.badInvoke(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badInvoke(Throwable var1) {
      return this.badInvoke(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badInvoke() {
      return this.badInvoke(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badSetServantManager(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446342, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badSetServantManager", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badSetServantManager(CompletionStatus var1) {
      return this.badSetServantManager(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badSetServantManager(Throwable var1) {
      return this.badSetServantManager(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badSetServantManager() {
      return this.badSetServantManager(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badArgumentsCall(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446343, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badArgumentsCall", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badArgumentsCall(CompletionStatus var1) {
      return this.badArgumentsCall(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badArgumentsCall(Throwable var1) {
      return this.badArgumentsCall(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badArgumentsCall() {
      return this.badArgumentsCall(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badCtxCall(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446344, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badCtxCall", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badCtxCall(CompletionStatus var1) {
      return this.badCtxCall(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badCtxCall(Throwable var1) {
      return this.badCtxCall(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badCtxCall() {
      return this.badCtxCall(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badResultCall(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446345, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badResultCall", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badResultCall(CompletionStatus var1) {
      return this.badResultCall(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badResultCall(Throwable var1) {
      return this.badResultCall(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badResultCall() {
      return this.badResultCall(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badSend(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446346, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badSend", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badSend(CompletionStatus var1) {
      return this.badSend(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badSend(Throwable var1) {
      return this.badSend(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badSend() {
      return this.badSend(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badPollBefore(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446347, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badPollBefore", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badPollBefore(CompletionStatus var1) {
      return this.badPollBefore(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badPollBefore(Throwable var1) {
      return this.badPollBefore(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badPollBefore() {
      return this.badPollBefore(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badPollAfter(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446348, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badPollAfter", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badPollAfter(CompletionStatus var1) {
      return this.badPollAfter(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badPollAfter(Throwable var1) {
      return this.badPollAfter(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badPollAfter() {
      return this.badPollAfter(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER badPollSync(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446349, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badPollSync", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER badPollSync(CompletionStatus var1) {
      return this.badPollSync(var1, (Throwable)null);
   }

   public BAD_INV_ORDER badPollSync(Throwable var1) {
      return this.badPollSync(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER badPollSync() {
      return this.badPollSync(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall1(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446350, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.invalidPiCall1", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER invalidPiCall1(CompletionStatus var1) {
      return this.invalidPiCall1(var1, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall1(Throwable var1) {
      return this.invalidPiCall1(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER invalidPiCall1() {
      return this.invalidPiCall1(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall2(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446350, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.invalidPiCall2", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER invalidPiCall2(CompletionStatus var1) {
      return this.invalidPiCall2(var1, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall2(Throwable var1) {
      return this.invalidPiCall2(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER invalidPiCall2() {
      return this.invalidPiCall2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall3(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446350, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.invalidPiCall3", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER invalidPiCall3(CompletionStatus var1) {
      return this.invalidPiCall3(var1, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall3(Throwable var1) {
      return this.invalidPiCall3(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER invalidPiCall3() {
      return this.invalidPiCall3(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall4(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446350, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.invalidPiCall4", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER invalidPiCall4(CompletionStatus var1) {
      return this.invalidPiCall4(var1, (Throwable)null);
   }

   public BAD_INV_ORDER invalidPiCall4(Throwable var1) {
      return this.invalidPiCall4(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER invalidPiCall4() {
      return this.invalidPiCall4(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER serviceContextAddFailed(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_INV_ORDER var4 = new BAD_INV_ORDER(1330446351, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "OMG.serviceContextAddFailed", var5, OMGSystemException.class, var4);
      }

      return var4;
   }

   public BAD_INV_ORDER serviceContextAddFailed(CompletionStatus var1, Object var2) {
      return this.serviceContextAddFailed(var1, (Throwable)null, var2);
   }

   public BAD_INV_ORDER serviceContextAddFailed(Throwable var1, Object var2) {
      return this.serviceContextAddFailed(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_INV_ORDER serviceContextAddFailed(Object var1) {
      return this.serviceContextAddFailed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_INV_ORDER policyFactoryRegFailed(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_INV_ORDER var4 = new BAD_INV_ORDER(1330446352, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "OMG.policyFactoryRegFailed", var5, OMGSystemException.class, var4);
      }

      return var4;
   }

   public BAD_INV_ORDER policyFactoryRegFailed(CompletionStatus var1, Object var2) {
      return this.policyFactoryRegFailed(var1, (Throwable)null, var2);
   }

   public BAD_INV_ORDER policyFactoryRegFailed(Throwable var1, Object var2) {
      return this.policyFactoryRegFailed(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_INV_ORDER policyFactoryRegFailed(Object var1) {
      return this.policyFactoryRegFailed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_INV_ORDER createPoaDestroy(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446353, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.createPoaDestroy", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER createPoaDestroy(CompletionStatus var1) {
      return this.createPoaDestroy(var1, (Throwable)null);
   }

   public BAD_INV_ORDER createPoaDestroy(Throwable var1) {
      return this.createPoaDestroy(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER createPoaDestroy() {
      return this.createPoaDestroy(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER priorityReassign(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446354, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.priorityReassign", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER priorityReassign(CompletionStatus var1) {
      return this.priorityReassign(var1, (Throwable)null);
   }

   public BAD_INV_ORDER priorityReassign(Throwable var1) {
      return this.priorityReassign(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER priorityReassign() {
      return this.priorityReassign(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER xaStartOutsize(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446355, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaStartOutsize", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER xaStartOutsize(CompletionStatus var1) {
      return this.xaStartOutsize(var1, (Throwable)null);
   }

   public BAD_INV_ORDER xaStartOutsize(Throwable var1) {
      return this.xaStartOutsize(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER xaStartOutsize() {
      return this.xaStartOutsize(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER xaStartProto(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1330446356, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaStartProto", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER xaStartProto(CompletionStatus var1) {
      return this.xaStartProto(var1, (Throwable)null);
   }

   public BAD_INV_ORDER xaStartProto(Throwable var1) {
      return this.xaStartProto(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER xaStartProto() {
      return this.xaStartProto(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION badServantManagerType(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badServantManagerType", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION badServantManagerType(CompletionStatus var1) {
      return this.badServantManagerType(var1, (Throwable)null);
   }

   public BAD_OPERATION badServantManagerType(Throwable var1) {
      return this.badServantManagerType(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION badServantManagerType() {
      return this.badServantManagerType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION operationUnknownToTarget(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.operationUnknownToTarget", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION operationUnknownToTarget(CompletionStatus var1) {
      return this.operationUnknownToTarget(var1, (Throwable)null);
   }

   public BAD_OPERATION operationUnknownToTarget(Throwable var1) {
      return this.operationUnknownToTarget(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION operationUnknownToTarget() {
      return this.operationUnknownToTarget(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM unableRegisterValueFactory(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.unableRegisterValueFactory", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM unableRegisterValueFactory(CompletionStatus var1) {
      return this.unableRegisterValueFactory(var1, (Throwable)null);
   }

   public BAD_PARAM unableRegisterValueFactory(Throwable var1) {
      return this.unableRegisterValueFactory(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM unableRegisterValueFactory() {
      return this.unableRegisterValueFactory(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM ridAlreadyDefined(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.ridAlreadyDefined", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM ridAlreadyDefined(CompletionStatus var1) {
      return this.ridAlreadyDefined(var1, (Throwable)null);
   }

   public BAD_PARAM ridAlreadyDefined(Throwable var1) {
      return this.ridAlreadyDefined(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM ridAlreadyDefined() {
      return this.ridAlreadyDefined(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM nameUsedIfr(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.nameUsedIfr", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM nameUsedIfr(CompletionStatus var1) {
      return this.nameUsedIfr(var1, (Throwable)null);
   }

   public BAD_PARAM nameUsedIfr(Throwable var1) {
      return this.nameUsedIfr(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM nameUsedIfr() {
      return this.nameUsedIfr(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM targetNotContainer(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.targetNotContainer", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM targetNotContainer(CompletionStatus var1) {
      return this.targetNotContainer(var1, (Throwable)null);
   }

   public BAD_PARAM targetNotContainer(Throwable var1) {
      return this.targetNotContainer(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM targetNotContainer() {
      return this.targetNotContainer(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM nameClash(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446341, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.nameClash", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM nameClash(CompletionStatus var1) {
      return this.nameClash(var1, (Throwable)null);
   }

   public BAD_PARAM nameClash(Throwable var1) {
      return this.nameClash(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM nameClash() {
      return this.nameClash(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM notSerializable(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1330446342, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "OMG.notSerializable", var5, OMGSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM notSerializable(CompletionStatus var1, Object var2) {
      return this.notSerializable(var1, (Throwable)null, var2);
   }

   public BAD_PARAM notSerializable(Throwable var1, Object var2) {
      return this.notSerializable(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM notSerializable(Object var1) {
      return this.notSerializable(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM soBadSchemeName(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446343, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.soBadSchemeName", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM soBadSchemeName(CompletionStatus var1) {
      return this.soBadSchemeName(var1, (Throwable)null);
   }

   public BAD_PARAM soBadSchemeName(Throwable var1) {
      return this.soBadSchemeName(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM soBadSchemeName() {
      return this.soBadSchemeName(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM soBadAddress(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446344, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.soBadAddress", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM soBadAddress(CompletionStatus var1) {
      return this.soBadAddress(var1, (Throwable)null);
   }

   public BAD_PARAM soBadAddress(Throwable var1) {
      return this.soBadAddress(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM soBadAddress() {
      return this.soBadAddress(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM soBadSchemaSpecific(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446345, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.soBadSchemaSpecific", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM soBadSchemaSpecific(CompletionStatus var1) {
      return this.soBadSchemaSpecific(var1, (Throwable)null);
   }

   public BAD_PARAM soBadSchemaSpecific(Throwable var1) {
      return this.soBadSchemaSpecific(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM soBadSchemaSpecific() {
      return this.soBadSchemaSpecific(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM soNonSpecific(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446346, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.soNonSpecific", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM soNonSpecific(CompletionStatus var1) {
      return this.soNonSpecific(var1, (Throwable)null);
   }

   public BAD_PARAM soNonSpecific(Throwable var1) {
      return this.soNonSpecific(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM soNonSpecific() {
      return this.soNonSpecific(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM irDeriveAbsIntBase(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446347, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.irDeriveAbsIntBase", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM irDeriveAbsIntBase(CompletionStatus var1) {
      return this.irDeriveAbsIntBase(var1, (Throwable)null);
   }

   public BAD_PARAM irDeriveAbsIntBase(Throwable var1) {
      return this.irDeriveAbsIntBase(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM irDeriveAbsIntBase() {
      return this.irDeriveAbsIntBase(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM irValueSupport(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446348, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.irValueSupport", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM irValueSupport(CompletionStatus var1) {
      return this.irValueSupport(var1, (Throwable)null);
   }

   public BAD_PARAM irValueSupport(Throwable var1) {
      return this.irValueSupport(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM irValueSupport() {
      return this.irValueSupport(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM incompleteTypecode(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446349, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.incompleteTypecode", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM incompleteTypecode(CompletionStatus var1) {
      return this.incompleteTypecode(var1, (Throwable)null);
   }

   public BAD_PARAM incompleteTypecode(Throwable var1) {
      return this.incompleteTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM incompleteTypecode() {
      return this.incompleteTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidObjectId(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446350, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.invalidObjectId", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM invalidObjectId(CompletionStatus var1) {
      return this.invalidObjectId(var1, (Throwable)null);
   }

   public BAD_PARAM invalidObjectId(Throwable var1) {
      return this.invalidObjectId(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM invalidObjectId() {
      return this.invalidObjectId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM typecodeBadName(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446351, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.typecodeBadName", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM typecodeBadName(CompletionStatus var1) {
      return this.typecodeBadName(var1, (Throwable)null);
   }

   public BAD_PARAM typecodeBadName(Throwable var1) {
      return this.typecodeBadName(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM typecodeBadName() {
      return this.typecodeBadName(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM typecodeBadRepid(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446352, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.typecodeBadRepid", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM typecodeBadRepid(CompletionStatus var1) {
      return this.typecodeBadRepid(var1, (Throwable)null);
   }

   public BAD_PARAM typecodeBadRepid(Throwable var1) {
      return this.typecodeBadRepid(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM typecodeBadRepid() {
      return this.typecodeBadRepid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM typecodeInvMember(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446353, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.typecodeInvMember", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM typecodeInvMember(CompletionStatus var1) {
      return this.typecodeInvMember(var1, (Throwable)null);
   }

   public BAD_PARAM typecodeInvMember(Throwable var1) {
      return this.typecodeInvMember(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM typecodeInvMember() {
      return this.typecodeInvMember(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM tcUnionDupLabel(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446354, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.tcUnionDupLabel", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM tcUnionDupLabel(CompletionStatus var1) {
      return this.tcUnionDupLabel(var1, (Throwable)null);
   }

   public BAD_PARAM tcUnionDupLabel(Throwable var1) {
      return this.tcUnionDupLabel(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM tcUnionDupLabel() {
      return this.tcUnionDupLabel(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM tcUnionIncompatible(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446355, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.tcUnionIncompatible", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM tcUnionIncompatible(CompletionStatus var1) {
      return this.tcUnionIncompatible(var1, (Throwable)null);
   }

   public BAD_PARAM tcUnionIncompatible(Throwable var1) {
      return this.tcUnionIncompatible(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM tcUnionIncompatible() {
      return this.tcUnionIncompatible(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM tcUnionBadDisc(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446356, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.tcUnionBadDisc", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM tcUnionBadDisc(CompletionStatus var1) {
      return this.tcUnionBadDisc(var1, (Throwable)null);
   }

   public BAD_PARAM tcUnionBadDisc(Throwable var1) {
      return this.tcUnionBadDisc(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM tcUnionBadDisc() {
      return this.tcUnionBadDisc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM setExceptionBadAny(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446357, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.setExceptionBadAny", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM setExceptionBadAny(CompletionStatus var1) {
      return this.setExceptionBadAny(var1, (Throwable)null);
   }

   public BAD_PARAM setExceptionBadAny(Throwable var1) {
      return this.setExceptionBadAny(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM setExceptionBadAny() {
      return this.setExceptionBadAny(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM setExceptionUnlisted(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446358, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.setExceptionUnlisted", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM setExceptionUnlisted(CompletionStatus var1) {
      return this.setExceptionUnlisted(var1, (Throwable)null);
   }

   public BAD_PARAM setExceptionUnlisted(Throwable var1) {
      return this.setExceptionUnlisted(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM setExceptionUnlisted() {
      return this.setExceptionUnlisted(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM noClientWcharCodesetCtx(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446359, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noClientWcharCodesetCtx", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM noClientWcharCodesetCtx(CompletionStatus var1) {
      return this.noClientWcharCodesetCtx(var1, (Throwable)null);
   }

   public BAD_PARAM noClientWcharCodesetCtx(Throwable var1) {
      return this.noClientWcharCodesetCtx(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM noClientWcharCodesetCtx() {
      return this.noClientWcharCodesetCtx(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM illegalServiceContext(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446360, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.illegalServiceContext", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM illegalServiceContext(CompletionStatus var1) {
      return this.illegalServiceContext(var1, (Throwable)null);
   }

   public BAD_PARAM illegalServiceContext(Throwable var1) {
      return this.illegalServiceContext(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM illegalServiceContext() {
      return this.illegalServiceContext(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM enumOutOfRange(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446361, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.enumOutOfRange", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM enumOutOfRange(CompletionStatus var1) {
      return this.enumOutOfRange(var1, (Throwable)null);
   }

   public BAD_PARAM enumOutOfRange(Throwable var1) {
      return this.enumOutOfRange(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM enumOutOfRange() {
      return this.enumOutOfRange(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidServiceContextId(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446362, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.invalidServiceContextId", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM invalidServiceContextId(CompletionStatus var1) {
      return this.invalidServiceContextId(var1, (Throwable)null);
   }

   public BAD_PARAM invalidServiceContextId(Throwable var1) {
      return this.invalidServiceContextId(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM invalidServiceContextId() {
      return this.invalidServiceContextId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM rirWithNullObject(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446363, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.rirWithNullObject", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM rirWithNullObject(CompletionStatus var1) {
      return this.rirWithNullObject(var1, (Throwable)null);
   }

   public BAD_PARAM rirWithNullObject(Throwable var1) {
      return this.rirWithNullObject(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM rirWithNullObject() {
      return this.rirWithNullObject(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidComponentId(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1330446364, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "OMG.invalidComponentId", var5, OMGSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM invalidComponentId(CompletionStatus var1, Object var2) {
      return this.invalidComponentId(var1, (Throwable)null, var2);
   }

   public BAD_PARAM invalidComponentId(Throwable var1, Object var2) {
      return this.invalidComponentId(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM invalidComponentId(Object var1) {
      return this.invalidComponentId(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM invalidProfileId(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446365, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.invalidProfileId", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM invalidProfileId(CompletionStatus var1) {
      return this.invalidProfileId(var1, (Throwable)null);
   }

   public BAD_PARAM invalidProfileId(Throwable var1) {
      return this.invalidProfileId(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM invalidProfileId() {
      return this.invalidProfileId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM policyTypeDuplicate(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446366, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.policyTypeDuplicate", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM policyTypeDuplicate(CompletionStatus var1) {
      return this.policyTypeDuplicate(var1, (Throwable)null);
   }

   public BAD_PARAM policyTypeDuplicate(Throwable var1) {
      return this.policyTypeDuplicate(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM policyTypeDuplicate() {
      return this.policyTypeDuplicate(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM badOnewayDefinition(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446367, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badOnewayDefinition", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM badOnewayDefinition(CompletionStatus var1) {
      return this.badOnewayDefinition(var1, (Throwable)null);
   }

   public BAD_PARAM badOnewayDefinition(Throwable var1) {
      return this.badOnewayDefinition(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM badOnewayDefinition() {
      return this.badOnewayDefinition(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM diiForImplicitOperation(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446368, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.diiForImplicitOperation", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM diiForImplicitOperation(CompletionStatus var1) {
      return this.diiForImplicitOperation(var1, (Throwable)null);
   }

   public BAD_PARAM diiForImplicitOperation(Throwable var1) {
      return this.diiForImplicitOperation(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM diiForImplicitOperation() {
      return this.diiForImplicitOperation(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM xaCallInval(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446369, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaCallInval", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM xaCallInval(CompletionStatus var1) {
      return this.xaCallInval(var1, (Throwable)null);
   }

   public BAD_PARAM xaCallInval(Throwable var1) {
      return this.xaCallInval(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM xaCallInval() {
      return this.xaCallInval(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM unionBadDiscriminator(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446370, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.unionBadDiscriminator", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM unionBadDiscriminator(CompletionStatus var1) {
      return this.unionBadDiscriminator(var1, (Throwable)null);
   }

   public BAD_PARAM unionBadDiscriminator(Throwable var1) {
      return this.unionBadDiscriminator(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM unionBadDiscriminator() {
      return this.unionBadDiscriminator(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM ctxIllegalPropertyName(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446371, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.ctxIllegalPropertyName", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM ctxIllegalPropertyName(CompletionStatus var1) {
      return this.ctxIllegalPropertyName(var1, (Throwable)null);
   }

   public BAD_PARAM ctxIllegalPropertyName(Throwable var1) {
      return this.ctxIllegalPropertyName(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM ctxIllegalPropertyName() {
      return this.ctxIllegalPropertyName(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM ctxIllegalSearchString(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446372, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.ctxIllegalSearchString", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM ctxIllegalSearchString(CompletionStatus var1) {
      return this.ctxIllegalSearchString(var1, (Throwable)null);
   }

   public BAD_PARAM ctxIllegalSearchString(Throwable var1) {
      return this.ctxIllegalSearchString(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM ctxIllegalSearchString() {
      return this.ctxIllegalSearchString(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM ctxIllegalName(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446373, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.ctxIllegalName", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM ctxIllegalName(CompletionStatus var1) {
      return this.ctxIllegalName(var1, (Throwable)null);
   }

   public BAD_PARAM ctxIllegalName(Throwable var1) {
      return this.ctxIllegalName(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM ctxIllegalName() {
      return this.ctxIllegalName(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM ctxNonEmpty(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446374, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.ctxNonEmpty", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM ctxNonEmpty(CompletionStatus var1) {
      return this.ctxNonEmpty(var1, (Throwable)null);
   }

   public BAD_PARAM ctxNonEmpty(Throwable var1) {
      return this.ctxNonEmpty(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM ctxNonEmpty() {
      return this.ctxNonEmpty(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidStreamFormatVersion(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1330446375, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "OMG.invalidStreamFormatVersion", var5, OMGSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM invalidStreamFormatVersion(CompletionStatus var1, Object var2) {
      return this.invalidStreamFormatVersion(var1, (Throwable)null, var2);
   }

   public BAD_PARAM invalidStreamFormatVersion(Throwable var1, Object var2) {
      return this.invalidStreamFormatVersion(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM invalidStreamFormatVersion(Object var1) {
      return this.invalidStreamFormatVersion(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM notAValueoutputstream(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446376, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.notAValueoutputstream", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM notAValueoutputstream(CompletionStatus var1) {
      return this.notAValueoutputstream(var1, (Throwable)null);
   }

   public BAD_PARAM notAValueoutputstream(Throwable var1) {
      return this.notAValueoutputstream(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM notAValueoutputstream() {
      return this.notAValueoutputstream(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM notAValueinputstream(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1330446377, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.notAValueinputstream", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM notAValueinputstream(CompletionStatus var1) {
      return this.notAValueinputstream(var1, (Throwable)null);
   }

   public BAD_PARAM notAValueinputstream(Throwable var1) {
      return this.notAValueinputstream(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM notAValueinputstream() {
      return this.notAValueinputstream(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_TYPECODE marshallIncompleteTypecode(CompletionStatus var1, Throwable var2) {
      BAD_TYPECODE var3 = new BAD_TYPECODE(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.marshallIncompleteTypecode", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_TYPECODE marshallIncompleteTypecode(CompletionStatus var1) {
      return this.marshallIncompleteTypecode(var1, (Throwable)null);
   }

   public BAD_TYPECODE marshallIncompleteTypecode(Throwable var1) {
      return this.marshallIncompleteTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_TYPECODE marshallIncompleteTypecode() {
      return this.marshallIncompleteTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_TYPECODE badMemberTypecode(CompletionStatus var1, Throwable var2) {
      BAD_TYPECODE var3 = new BAD_TYPECODE(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badMemberTypecode", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_TYPECODE badMemberTypecode(CompletionStatus var1) {
      return this.badMemberTypecode(var1, (Throwable)null);
   }

   public BAD_TYPECODE badMemberTypecode(Throwable var1) {
      return this.badMemberTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_TYPECODE badMemberTypecode() {
      return this.badMemberTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_TYPECODE illegalParameter(CompletionStatus var1, Throwable var2) {
      BAD_TYPECODE var3 = new BAD_TYPECODE(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.illegalParameter", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public BAD_TYPECODE illegalParameter(CompletionStatus var1) {
      return this.illegalParameter(var1, (Throwable)null);
   }

   public BAD_TYPECODE illegalParameter(Throwable var1) {
      return this.illegalParameter(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_TYPECODE illegalParameter() {
      return this.illegalParameter(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION charNotInCodeset(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.charNotInCodeset", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION charNotInCodeset(CompletionStatus var1) {
      return this.charNotInCodeset(var1, (Throwable)null);
   }

   public DATA_CONVERSION charNotInCodeset(Throwable var1) {
      return this.charNotInCodeset(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION charNotInCodeset() {
      return this.charNotInCodeset(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION priorityMapFailre(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.priorityMapFailre", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION priorityMapFailre(CompletionStatus var1) {
      return this.priorityMapFailre(var1, (Throwable)null);
   }

   public DATA_CONVERSION priorityMapFailre(Throwable var1) {
      return this.priorityMapFailre(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION priorityMapFailre() {
      return this.priorityMapFailre(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public IMP_LIMIT noUsableProfile(CompletionStatus var1, Throwable var2) {
      IMP_LIMIT var3 = new IMP_LIMIT(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noUsableProfile", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public IMP_LIMIT noUsableProfile(CompletionStatus var1) {
      return this.noUsableProfile(var1, (Throwable)null);
   }

   public IMP_LIMIT noUsableProfile(Throwable var1) {
      return this.noUsableProfile(CompletionStatus.COMPLETED_NO, var1);
   }

   public IMP_LIMIT noUsableProfile() {
      return this.noUsableProfile(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE priorityRangeRestrict(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.priorityRangeRestrict", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE priorityRangeRestrict(CompletionStatus var1) {
      return this.priorityRangeRestrict(var1, (Throwable)null);
   }

   public INITIALIZE priorityRangeRestrict(Throwable var1) {
      return this.priorityRangeRestrict(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE priorityRangeRestrict() {
      return this.priorityRangeRestrict(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INV_OBJREF noServerWcharCodesetCmp(CompletionStatus var1, Throwable var2) {
      INV_OBJREF var3 = new INV_OBJREF(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noServerWcharCodesetCmp", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INV_OBJREF noServerWcharCodesetCmp(CompletionStatus var1) {
      return this.noServerWcharCodesetCmp(var1, (Throwable)null);
   }

   public INV_OBJREF noServerWcharCodesetCmp(Throwable var1) {
      return this.noServerWcharCodesetCmp(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_OBJREF noServerWcharCodesetCmp() {
      return this.noServerWcharCodesetCmp(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INV_OBJREF codesetComponentRequired(CompletionStatus var1, Throwable var2) {
      INV_OBJREF var3 = new INV_OBJREF(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.codesetComponentRequired", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INV_OBJREF codesetComponentRequired(CompletionStatus var1) {
      return this.codesetComponentRequired(var1, (Throwable)null);
   }

   public INV_OBJREF codesetComponentRequired(Throwable var1) {
      return this.codesetComponentRequired(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_OBJREF codesetComponentRequired() {
      return this.codesetComponentRequired(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INV_POLICY iorPolicyReconcileError(CompletionStatus var1, Throwable var2) {
      INV_POLICY var3 = new INV_POLICY(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.iorPolicyReconcileError", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INV_POLICY iorPolicyReconcileError(CompletionStatus var1) {
      return this.iorPolicyReconcileError(var1, (Throwable)null);
   }

   public INV_POLICY iorPolicyReconcileError(Throwable var1) {
      return this.iorPolicyReconcileError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_POLICY iorPolicyReconcileError() {
      return this.iorPolicyReconcileError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INV_POLICY policyUnknown(CompletionStatus var1, Throwable var2) {
      INV_POLICY var3 = new INV_POLICY(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.policyUnknown", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INV_POLICY policyUnknown(CompletionStatus var1) {
      return this.policyUnknown(var1, (Throwable)null);
   }

   public INV_POLICY policyUnknown(Throwable var1) {
      return this.policyUnknown(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_POLICY policyUnknown() {
      return this.policyUnknown(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INV_POLICY noPolicyFactory(CompletionStatus var1, Throwable var2) {
      INV_POLICY var3 = new INV_POLICY(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noPolicyFactory", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INV_POLICY noPolicyFactory(CompletionStatus var1) {
      return this.noPolicyFactory(var1, (Throwable)null);
   }

   public INV_POLICY noPolicyFactory(Throwable var1) {
      return this.noPolicyFactory(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_POLICY noPolicyFactory() {
      return this.noPolicyFactory(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL xaRmerr(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaRmerr", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL xaRmerr(CompletionStatus var1) {
      return this.xaRmerr(var1, (Throwable)null);
   }

   public INTERNAL xaRmerr(Throwable var1) {
      return this.xaRmerr(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL xaRmerr() {
      return this.xaRmerr(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL xaRmfail(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaRmfail", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL xaRmfail(CompletionStatus var1) {
      return this.xaRmfail(var1, (Throwable)null);
   }

   public INTERNAL xaRmfail(Throwable var1) {
      return this.xaRmfail(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL xaRmfail() {
      return this.xaRmfail(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTF_REPOS noIr(CompletionStatus var1, Throwable var2) {
      INTF_REPOS var3 = new INTF_REPOS(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noIr", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INTF_REPOS noIr(CompletionStatus var1) {
      return this.noIr(var1, (Throwable)null);
   }

   public INTF_REPOS noIr(Throwable var1) {
      return this.noIr(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTF_REPOS noIr() {
      return this.noIr(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTF_REPOS noInterfaceInIr(CompletionStatus var1, Throwable var2) {
      INTF_REPOS var3 = new INTF_REPOS(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noInterfaceInIr", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public INTF_REPOS noInterfaceInIr(CompletionStatus var1) {
      return this.noInterfaceInIr(var1, (Throwable)null);
   }

   public INTF_REPOS noInterfaceInIr(Throwable var1) {
      return this.noInterfaceInIr(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTF_REPOS noInterfaceInIr() {
      return this.noInterfaceInIr(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unableLocateValueFactory(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.unableLocateValueFactory", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL unableLocateValueFactory(CompletionStatus var1) {
      return this.unableLocateValueFactory(var1, (Throwable)null);
   }

   public MARSHAL unableLocateValueFactory(Throwable var1) {
      return this.unableLocateValueFactory(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL unableLocateValueFactory() {
      return this.unableLocateValueFactory(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL setResultBeforeCtx(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.setResultBeforeCtx", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL setResultBeforeCtx(CompletionStatus var1) {
      return this.setResultBeforeCtx(var1, (Throwable)null);
   }

   public MARSHAL setResultBeforeCtx(Throwable var1) {
      return this.setResultBeforeCtx(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL setResultBeforeCtx() {
      return this.setResultBeforeCtx(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badNvlist(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badNvlist", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badNvlist(CompletionStatus var1) {
      return this.badNvlist(var1, (Throwable)null);
   }

   public MARSHAL badNvlist(Throwable var1) {
      return this.badNvlist(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badNvlist() {
      return this.badNvlist(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL notAnObjectImpl(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.notAnObjectImpl", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL notAnObjectImpl(CompletionStatus var1) {
      return this.notAnObjectImpl(var1, (Throwable)null);
   }

   public MARSHAL notAnObjectImpl(Throwable var1) {
      return this.notAnObjectImpl(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL notAnObjectImpl() {
      return this.notAnObjectImpl(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL wcharBadGiopVersionSent(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446341, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.wcharBadGiopVersionSent", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL wcharBadGiopVersionSent(CompletionStatus var1) {
      return this.wcharBadGiopVersionSent(var1, (Throwable)null);
   }

   public MARSHAL wcharBadGiopVersionSent(Throwable var1) {
      return this.wcharBadGiopVersionSent(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL wcharBadGiopVersionSent() {
      return this.wcharBadGiopVersionSent(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL wcharBadGiopVersionReturned(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446342, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.wcharBadGiopVersionReturned", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL wcharBadGiopVersionReturned(CompletionStatus var1) {
      return this.wcharBadGiopVersionReturned(var1, (Throwable)null);
   }

   public MARSHAL wcharBadGiopVersionReturned(Throwable var1) {
      return this.wcharBadGiopVersionReturned(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL wcharBadGiopVersionReturned() {
      return this.wcharBadGiopVersionReturned(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unsupportedFormatVersion(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446343, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.unsupportedFormatVersion", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL unsupportedFormatVersion(CompletionStatus var1) {
      return this.unsupportedFormatVersion(var1, (Throwable)null);
   }

   public MARSHAL unsupportedFormatVersion(Throwable var1) {
      return this.unsupportedFormatVersion(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL unsupportedFormatVersion() {
      return this.unsupportedFormatVersion(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible1(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446344, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible1", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL rmiiiopOptionalDataIncompatible1(CompletionStatus var1) {
      return this.rmiiiopOptionalDataIncompatible1(var1, (Throwable)null);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible1(Throwable var1) {
      return this.rmiiiopOptionalDataIncompatible1(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible1() {
      return this.rmiiiopOptionalDataIncompatible1(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible2(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446344, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible2", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL rmiiiopOptionalDataIncompatible2(CompletionStatus var1) {
      return this.rmiiiopOptionalDataIncompatible2(var1, (Throwable)null);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible2(Throwable var1) {
      return this.rmiiiopOptionalDataIncompatible2(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible2() {
      return this.rmiiiopOptionalDataIncompatible2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible3(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1330446344, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.rmiiiopOptionalDataIncompatible3", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL rmiiiopOptionalDataIncompatible3(CompletionStatus var1) {
      return this.rmiiiopOptionalDataIncompatible3(var1, (Throwable)null);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible3(Throwable var1) {
      return this.rmiiiopOptionalDataIncompatible3(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL rmiiiopOptionalDataIncompatible3() {
      return this.rmiiiopOptionalDataIncompatible3(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT missingLocalValueImpl(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.missingLocalValueImpl", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT missingLocalValueImpl(CompletionStatus var1) {
      return this.missingLocalValueImpl(var1, (Throwable)null);
   }

   public NO_IMPLEMENT missingLocalValueImpl(Throwable var1) {
      return this.missingLocalValueImpl(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT missingLocalValueImpl() {
      return this.missingLocalValueImpl(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT incompatibleValueImpl(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.incompatibleValueImpl", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT incompatibleValueImpl(CompletionStatus var1) {
      return this.incompatibleValueImpl(var1, (Throwable)null);
   }

   public NO_IMPLEMENT incompatibleValueImpl(Throwable var1) {
      return this.incompatibleValueImpl(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT incompatibleValueImpl() {
      return this.incompatibleValueImpl(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT noUsableProfile2(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noUsableProfile2", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT noUsableProfile2(CompletionStatus var1) {
      return this.noUsableProfile2(var1, (Throwable)null);
   }

   public NO_IMPLEMENT noUsableProfile2(Throwable var1) {
      return this.noUsableProfile2(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT noUsableProfile2() {
      return this.noUsableProfile2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT diiLocalObject(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.diiLocalObject", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT diiLocalObject(CompletionStatus var1) {
      return this.diiLocalObject(var1, (Throwable)null);
   }

   public NO_IMPLEMENT diiLocalObject(Throwable var1) {
      return this.diiLocalObject(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT diiLocalObject() {
      return this.diiLocalObject(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT bioReset(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446341, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.bioReset", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT bioReset(CompletionStatus var1) {
      return this.bioReset(var1, (Throwable)null);
   }

   public NO_IMPLEMENT bioReset(Throwable var1) {
      return this.bioReset(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT bioReset() {
      return this.bioReset(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT bioMetaNotAvailable(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446342, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.bioMetaNotAvailable", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT bioMetaNotAvailable(CompletionStatus var1) {
      return this.bioMetaNotAvailable(var1, (Throwable)null);
   }

   public NO_IMPLEMENT bioMetaNotAvailable(Throwable var1) {
      return this.bioMetaNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT bioMetaNotAvailable() {
      return this.bioMetaNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT bioGenomicNoIterator(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1330446343, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.bioGenomicNoIterator", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT bioGenomicNoIterator(CompletionStatus var1) {
      return this.bioGenomicNoIterator(var1, (Throwable)null);
   }

   public NO_IMPLEMENT bioGenomicNoIterator(Throwable var1) {
      return this.bioGenomicNoIterator(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT bioGenomicNoIterator() {
      return this.bioGenomicNoIterator(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported1(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported1", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported1(CompletionStatus var1) {
      return this.piOperationNotSupported1(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported1(Throwable var1) {
      return this.piOperationNotSupported1(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported1() {
      return this.piOperationNotSupported1(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported2(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported2", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported2(CompletionStatus var1) {
      return this.piOperationNotSupported2(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported2(Throwable var1) {
      return this.piOperationNotSupported2(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported2() {
      return this.piOperationNotSupported2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported3(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported3", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported3(CompletionStatus var1) {
      return this.piOperationNotSupported3(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported3(Throwable var1) {
      return this.piOperationNotSupported3(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported3() {
      return this.piOperationNotSupported3(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported4(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported4", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported4(CompletionStatus var1) {
      return this.piOperationNotSupported4(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported4(Throwable var1) {
      return this.piOperationNotSupported4(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported4() {
      return this.piOperationNotSupported4(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported5(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported5", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported5(CompletionStatus var1) {
      return this.piOperationNotSupported5(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported5(Throwable var1) {
      return this.piOperationNotSupported5(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported5() {
      return this.piOperationNotSupported5(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported6(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported6", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported6(CompletionStatus var1) {
      return this.piOperationNotSupported6(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported6(Throwable var1) {
      return this.piOperationNotSupported6(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported6() {
      return this.piOperationNotSupported6(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported7(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported7", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported7(CompletionStatus var1) {
      return this.piOperationNotSupported7(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported7(Throwable var1) {
      return this.piOperationNotSupported7(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported7() {
      return this.piOperationNotSupported7(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported8(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.piOperationNotSupported8", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES piOperationNotSupported8(CompletionStatus var1) {
      return this.piOperationNotSupported8(var1, (Throwable)null);
   }

   public NO_RESOURCES piOperationNotSupported8(Throwable var1) {
      return this.piOperationNotSupported8(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES piOperationNotSupported8() {
      return this.piOperationNotSupported8(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_RESOURCES noConnectionPriority(CompletionStatus var1, Throwable var2) {
      NO_RESOURCES var3 = new NO_RESOURCES(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noConnectionPriority", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public NO_RESOURCES noConnectionPriority(CompletionStatus var1) {
      return this.noConnectionPriority(var1, (Throwable)null);
   }

   public NO_RESOURCES noConnectionPriority(Throwable var1) {
      return this.noConnectionPriority(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_RESOURCES noConnectionPriority() {
      return this.noConnectionPriority(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSACTION_ROLLEDBACK xaRb(CompletionStatus var1, Throwable var2) {
      TRANSACTION_ROLLEDBACK var3 = new TRANSACTION_ROLLEDBACK(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaRb", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSACTION_ROLLEDBACK xaRb(CompletionStatus var1) {
      return this.xaRb(var1, (Throwable)null);
   }

   public TRANSACTION_ROLLEDBACK xaRb(Throwable var1) {
      return this.xaRb(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSACTION_ROLLEDBACK xaRb() {
      return this.xaRb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSACTION_ROLLEDBACK xaNota(CompletionStatus var1, Throwable var2) {
      TRANSACTION_ROLLEDBACK var3 = new TRANSACTION_ROLLEDBACK(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaNota", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSACTION_ROLLEDBACK xaNota(CompletionStatus var1) {
      return this.xaNota(var1, (Throwable)null);
   }

   public TRANSACTION_ROLLEDBACK xaNota(Throwable var1) {
      return this.xaNota(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSACTION_ROLLEDBACK xaNota() {
      return this.xaNota(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(CompletionStatus var1, Throwable var2) {
      TRANSACTION_ROLLEDBACK var3 = new TRANSACTION_ROLLEDBACK(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.xaEndTrueRollbackDeferred", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(CompletionStatus var1) {
      return this.xaEndTrueRollbackDeferred(var1, (Throwable)null);
   }

   public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(Throwable var1) {
      return this.xaEndTrueRollbackDeferred(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred() {
      return this.xaEndTrueRollbackDeferred(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSIENT poaRequestDiscard(CompletionStatus var1, Throwable var2) {
      TRANSIENT var3 = new TRANSIENT(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.poaRequestDiscard", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSIENT poaRequestDiscard(CompletionStatus var1) {
      return this.poaRequestDiscard(var1, (Throwable)null);
   }

   public TRANSIENT poaRequestDiscard(Throwable var1) {
      return this.poaRequestDiscard(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSIENT poaRequestDiscard() {
      return this.poaRequestDiscard(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSIENT noUsableProfile3(CompletionStatus var1, Throwable var2) {
      TRANSIENT var3 = new TRANSIENT(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noUsableProfile3", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSIENT noUsableProfile3(CompletionStatus var1) {
      return this.noUsableProfile3(var1, (Throwable)null);
   }

   public TRANSIENT noUsableProfile3(Throwable var1) {
      return this.noUsableProfile3(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSIENT noUsableProfile3() {
      return this.noUsableProfile3(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSIENT requestCancelled(CompletionStatus var1, Throwable var2) {
      TRANSIENT var3 = new TRANSIENT(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.requestCancelled", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSIENT requestCancelled(CompletionStatus var1) {
      return this.requestCancelled(var1, (Throwable)null);
   }

   public TRANSIENT requestCancelled(Throwable var1) {
      return this.requestCancelled(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSIENT requestCancelled() {
      return this.requestCancelled(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSIENT poaDestroyed(CompletionStatus var1, Throwable var2) {
      TRANSIENT var3 = new TRANSIENT(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.poaDestroyed", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public TRANSIENT poaDestroyed(CompletionStatus var1) {
      return this.poaDestroyed(var1, (Throwable)null);
   }

   public TRANSIENT poaDestroyed(Throwable var1) {
      return this.poaDestroyed(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSIENT poaDestroyed() {
      return this.poaDestroyed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST unregisteredValueAsObjref(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.unregisteredValueAsObjref", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST unregisteredValueAsObjref(CompletionStatus var1) {
      return this.unregisteredValueAsObjref(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST unregisteredValueAsObjref(Throwable var1) {
      return this.unregisteredValueAsObjref(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST unregisteredValueAsObjref() {
      return this.unregisteredValueAsObjref(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST noObjectAdaptor(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.noObjectAdaptor", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST noObjectAdaptor(CompletionStatus var1) {
      return this.noObjectAdaptor(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST noObjectAdaptor(Throwable var1) {
      return this.noObjectAdaptor(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST noObjectAdaptor() {
      return this.noObjectAdaptor(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST bioNotAvailable(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.bioNotAvailable", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST bioNotAvailable(CompletionStatus var1) {
      return this.bioNotAvailable(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST bioNotAvailable(Throwable var1) {
      return this.bioNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST bioNotAvailable() {
      return this.bioNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST objectAdapterInactive(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.objectAdapterInactive", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST objectAdapterInactive(CompletionStatus var1) {
      return this.objectAdapterInactive(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST objectAdapterInactive(Throwable var1) {
      return this.objectAdapterInactive(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST objectAdapterInactive() {
      return this.objectAdapterInactive(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER adapterActivatorException(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      OBJ_ADAPTER var5 = new OBJ_ADAPTER(1330446337, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "OMG.adapterActivatorException", var6, OMGSystemException.class, var5);
      }

      return var5;
   }

   public OBJ_ADAPTER adapterActivatorException(CompletionStatus var1, Object var2, Object var3) {
      return this.adapterActivatorException(var1, (Throwable)null, var2, var3);
   }

   public OBJ_ADAPTER adapterActivatorException(Throwable var1, Object var2, Object var3) {
      return this.adapterActivatorException(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public OBJ_ADAPTER adapterActivatorException(Object var1, Object var2) {
      return this.adapterActivatorException(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public OBJ_ADAPTER badServantType(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badServantType", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER badServantType(CompletionStatus var1) {
      return this.badServantType(var1, (Throwable)null);
   }

   public OBJ_ADAPTER badServantType(Throwable var1) {
      return this.badServantType(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER badServantType() {
      return this.badServantType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER noDefaultServant(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noDefaultServant", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER noDefaultServant(CompletionStatus var1) {
      return this.noDefaultServant(var1, (Throwable)null);
   }

   public OBJ_ADAPTER noDefaultServant(Throwable var1) {
      return this.noDefaultServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER noDefaultServant() {
      return this.noDefaultServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER noServantManager(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1330446340, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.noServantManager", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER noServantManager(CompletionStatus var1) {
      return this.noServantManager(var1, (Throwable)null);
   }

   public OBJ_ADAPTER noServantManager(Throwable var1) {
      return this.noServantManager(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER noServantManager() {
      return this.noServantManager(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER badPolicyIncarnate(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1330446341, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.badPolicyIncarnate", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER badPolicyIncarnate(CompletionStatus var1) {
      return this.badPolicyIncarnate(var1, (Throwable)null);
   }

   public OBJ_ADAPTER badPolicyIncarnate(Throwable var1) {
      return this.badPolicyIncarnate(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER badPolicyIncarnate() {
      return this.badPolicyIncarnate(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER piExcCompEstablished(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1330446342, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.piExcCompEstablished", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER piExcCompEstablished(CompletionStatus var1) {
      return this.piExcCompEstablished(var1, (Throwable)null);
   }

   public OBJ_ADAPTER piExcCompEstablished(Throwable var1) {
      return this.piExcCompEstablished(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER piExcCompEstablished() {
      return this.piExcCompEstablished(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER nullServantReturned(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1330446343, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.nullServantReturned", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER nullServantReturned(CompletionStatus var1) {
      return this.nullServantReturned(var1, (Throwable)null);
   }

   public OBJ_ADAPTER nullServantReturned(Throwable var1) {
      return this.nullServantReturned(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER nullServantReturned() {
      return this.nullServantReturned(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownUserException(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1330446337, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "OMG.unknownUserException", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownUserException(CompletionStatus var1) {
      return this.unknownUserException(var1, (Throwable)null);
   }

   public UNKNOWN unknownUserException(Throwable var1) {
      return this.unknownUserException(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownUserException() {
      return this.unknownUserException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unsupportedSystemException(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1330446338, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.unsupportedSystemException", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unsupportedSystemException(CompletionStatus var1) {
      return this.unsupportedSystemException(var1, (Throwable)null);
   }

   public UNKNOWN unsupportedSystemException(Throwable var1) {
      return this.unsupportedSystemException(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unsupportedSystemException() {
      return this.unsupportedSystemException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN piUnknownUserException(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1330446339, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "OMG.piUnknownUserException", (Object[])var4, OMGSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN piUnknownUserException(CompletionStatus var1) {
      return this.piUnknownUserException(var1, (Throwable)null);
   }

   public UNKNOWN piUnknownUserException(Throwable var1) {
      return this.piUnknownUserException(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN piUnknownUserException() {
      return this.piUnknownUserException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
