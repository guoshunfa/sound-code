package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;

public class POASystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new POASystemException(var1);
      }
   };
   public static final int SERVANT_MANAGER_ALREADY_SET = 1398080489;
   public static final int DESTROY_DEADLOCK = 1398080490;
   public static final int SERVANT_ORB = 1398080489;
   public static final int BAD_SERVANT = 1398080490;
   public static final int ILLEGAL_FORWARD_REQUEST = 1398080491;
   public static final int BAD_TRANSACTION_CONTEXT = 1398080489;
   public static final int BAD_REPOSITORY_ID = 1398080490;
   public static final int INVOKESETUP = 1398080489;
   public static final int BAD_LOCALREPLYSTATUS = 1398080490;
   public static final int PERSISTENT_SERVERPORT_ERROR = 1398080491;
   public static final int SERVANT_DISPATCH = 1398080492;
   public static final int WRONG_CLIENTSC = 1398080493;
   public static final int CANT_CLONE_TEMPLATE = 1398080494;
   public static final int POACURRENT_UNBALANCED_STACK = 1398080495;
   public static final int POACURRENT_NULL_FIELD = 1398080496;
   public static final int POA_INTERNAL_GET_SERVANT_ERROR = 1398080497;
   public static final int MAKE_FACTORY_NOT_POA = 1398080498;
   public static final int DUPLICATE_ORB_VERSION_SC = 1398080499;
   public static final int PREINVOKE_CLONE_ERROR = 1398080500;
   public static final int PREINVOKE_POA_DESTROYED = 1398080501;
   public static final int PMF_CREATE_RETAIN = 1398080502;
   public static final int PMF_CREATE_NON_RETAIN = 1398080503;
   public static final int POLICY_MEDIATOR_BAD_POLICY_IN_FACTORY = 1398080504;
   public static final int SERVANT_TO_ID_OAA = 1398080505;
   public static final int SERVANT_TO_ID_SAA = 1398080506;
   public static final int SERVANT_TO_ID_WP = 1398080507;
   public static final int CANT_RESOLVE_ROOT_POA = 1398080508;
   public static final int SERVANT_MUST_BE_LOCAL = 1398080509;
   public static final int NO_PROFILES_IN_IOR = 1398080510;
   public static final int AOM_ENTRY_DEC_ZERO = 1398080511;
   public static final int ADD_POA_INACTIVE = 1398080512;
   public static final int ILLEGAL_POA_STATE_TRANS = 1398080513;
   public static final int UNEXPECTED_EXCEPTION = 1398080514;
   public static final int SINGLE_THREAD_NOT_SUPPORTED = 1398080489;
   public static final int METHOD_NOT_IMPLEMENTED = 1398080490;
   public static final int POA_LOOKUP_ERROR = 1398080489;
   public static final int POA_INACTIVE = 1398080490;
   public static final int POA_NO_SERVANT_MANAGER = 1398080491;
   public static final int POA_NO_DEFAULT_SERVANT = 1398080492;
   public static final int POA_SERVANT_NOT_UNIQUE = 1398080493;
   public static final int POA_WRONG_POLICY = 1398080494;
   public static final int FINDPOA_ERROR = 1398080495;
   public static final int POA_SERVANT_ACTIVATOR_LOOKUP_FAILED = 1398080497;
   public static final int POA_BAD_SERVANT_MANAGER = 1398080498;
   public static final int POA_SERVANT_LOCATOR_LOOKUP_FAILED = 1398080499;
   public static final int POA_UNKNOWN_POLICY = 1398080500;
   public static final int POA_NOT_FOUND = 1398080501;
   public static final int SERVANT_LOOKUP = 1398080502;
   public static final int LOCAL_SERVANT_LOOKUP = 1398080503;
   public static final int SERVANT_MANAGER_BAD_TYPE = 1398080504;
   public static final int DEFAULT_POA_NOT_POAIMPL = 1398080505;
   public static final int WRONG_POLICIES_FOR_THIS_OBJECT = 1398080506;
   public static final int THIS_OBJECT_SERVANT_NOT_ACTIVE = 1398080507;
   public static final int THIS_OBJECT_WRONG_POLICY = 1398080508;
   public static final int NO_CONTEXT = 1398080509;
   public static final int INCARNATE_RETURNED_NULL = 1398080510;
   public static final int JTS_INIT_ERROR = 1398080489;
   public static final int PERSISTENT_SERVERID_NOT_SET = 1398080490;
   public static final int PERSISTENT_SERVERPORT_NOT_SET = 1398080491;
   public static final int ORBD_ERROR = 1398080492;
   public static final int BOOTSTRAP_ERROR = 1398080493;
   public static final int POA_DISCARDING = 1398080489;
   public static final int OTSHOOKEXCEPTION = 1398080489;
   public static final int UNKNOWN_SERVER_EXCEPTION = 1398080490;
   public static final int UNKNOWN_SERVERAPP_EXCEPTION = 1398080491;
   public static final int UNKNOWN_LOCALINVOCATION_ERROR = 1398080492;
   public static final int ADAPTER_ACTIVATOR_NONEXISTENT = 1398080489;
   public static final int ADAPTER_ACTIVATOR_FAILED = 1398080490;
   public static final int BAD_SKELETON = 1398080491;
   public static final int NULL_SERVANT = 1398080492;
   public static final int ADAPTER_DESTROYED = 1398080493;

   public POASystemException(Logger var1) {
      super(var1);
   }

   public static POASystemException get(ORB var0, String var1) {
      POASystemException var2 = (POASystemException)var0.getLogWrapper(var1, "POA", factory);
      return var2;
   }

   public static POASystemException get(String var0) {
      POASystemException var1 = (POASystemException)ORB.staticGetLogWrapper(var0, "POA", factory);
      return var1;
   }

   public BAD_INV_ORDER servantManagerAlreadySet(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantManagerAlreadySet", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER servantManagerAlreadySet(CompletionStatus var1) {
      return this.servantManagerAlreadySet(var1, (Throwable)null);
   }

   public BAD_INV_ORDER servantManagerAlreadySet(Throwable var1) {
      return this.servantManagerAlreadySet(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER servantManagerAlreadySet() {
      return this.servantManagerAlreadySet(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER destroyDeadlock(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.destroyDeadlock", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER destroyDeadlock(CompletionStatus var1) {
      return this.destroyDeadlock(var1, (Throwable)null);
   }

   public BAD_INV_ORDER destroyDeadlock(Throwable var1) {
      return this.destroyDeadlock(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER destroyDeadlock() {
      return this.destroyDeadlock(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION servantOrb(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantOrb", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION servantOrb(CompletionStatus var1) {
      return this.servantOrb(var1, (Throwable)null);
   }

   public BAD_OPERATION servantOrb(Throwable var1) {
      return this.servantOrb(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION servantOrb() {
      return this.servantOrb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION badServant(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.badServant", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION badServant(CompletionStatus var1) {
      return this.badServant(var1, (Throwable)null);
   }

   public BAD_OPERATION badServant(Throwable var1) {
      return this.badServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION badServant() {
      return this.badServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION illegalForwardRequest(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398080491, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.illegalForwardRequest", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION illegalForwardRequest(CompletionStatus var1) {
      return this.illegalForwardRequest(var1, (Throwable)null);
   }

   public BAD_OPERATION illegalForwardRequest(Throwable var1) {
      return this.illegalForwardRequest(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION illegalForwardRequest() {
      return this.illegalForwardRequest(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM badTransactionContext(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.badTransactionContext", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM badTransactionContext(CompletionStatus var1) {
      return this.badTransactionContext(var1, (Throwable)null);
   }

   public BAD_PARAM badTransactionContext(Throwable var1) {
      return this.badTransactionContext(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM badTransactionContext() {
      return this.badTransactionContext(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM badRepositoryId(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.badRepositoryId", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM badRepositoryId(CompletionStatus var1) {
      return this.badRepositoryId(var1, (Throwable)null);
   }

   public BAD_PARAM badRepositoryId(Throwable var1) {
      return this.badRepositoryId(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM badRepositoryId() {
      return this.badRepositoryId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL invokesetup(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.invokesetup", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL invokesetup(CompletionStatus var1) {
      return this.invokesetup(var1, (Throwable)null);
   }

   public INTERNAL invokesetup(Throwable var1) {
      return this.invokesetup(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL invokesetup() {
      return this.invokesetup(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badLocalreplystatus(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.badLocalreplystatus", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badLocalreplystatus(CompletionStatus var1) {
      return this.badLocalreplystatus(var1, (Throwable)null);
   }

   public INTERNAL badLocalreplystatus(Throwable var1) {
      return this.badLocalreplystatus(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badLocalreplystatus() {
      return this.badLocalreplystatus(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL persistentServerportError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080491, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.persistentServerportError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL persistentServerportError(CompletionStatus var1) {
      return this.persistentServerportError(var1, (Throwable)null);
   }

   public INTERNAL persistentServerportError(Throwable var1) {
      return this.persistentServerportError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL persistentServerportError() {
      return this.persistentServerportError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL servantDispatch(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080492, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantDispatch", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL servantDispatch(CompletionStatus var1) {
      return this.servantDispatch(var1, (Throwable)null);
   }

   public INTERNAL servantDispatch(Throwable var1) {
      return this.servantDispatch(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL servantDispatch() {
      return this.servantDispatch(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL wrongClientsc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080493, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.wrongClientsc", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL wrongClientsc(CompletionStatus var1) {
      return this.wrongClientsc(var1, (Throwable)null);
   }

   public INTERNAL wrongClientsc(Throwable var1) {
      return this.wrongClientsc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL wrongClientsc() {
      return this.wrongClientsc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL cantCloneTemplate(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080494, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.cantCloneTemplate", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL cantCloneTemplate(CompletionStatus var1) {
      return this.cantCloneTemplate(var1, (Throwable)null);
   }

   public INTERNAL cantCloneTemplate(Throwable var1) {
      return this.cantCloneTemplate(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL cantCloneTemplate() {
      return this.cantCloneTemplate(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL poacurrentUnbalancedStack(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080495, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poacurrentUnbalancedStack", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL poacurrentUnbalancedStack(CompletionStatus var1) {
      return this.poacurrentUnbalancedStack(var1, (Throwable)null);
   }

   public INTERNAL poacurrentUnbalancedStack(Throwable var1) {
      return this.poacurrentUnbalancedStack(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL poacurrentUnbalancedStack() {
      return this.poacurrentUnbalancedStack(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL poacurrentNullField(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080496, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poacurrentNullField", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL poacurrentNullField(CompletionStatus var1) {
      return this.poacurrentNullField(var1, (Throwable)null);
   }

   public INTERNAL poacurrentNullField(Throwable var1) {
      return this.poacurrentNullField(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL poacurrentNullField() {
      return this.poacurrentNullField(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL poaInternalGetServantError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080497, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaInternalGetServantError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL poaInternalGetServantError(CompletionStatus var1) {
      return this.poaInternalGetServantError(var1, (Throwable)null);
   }

   public INTERNAL poaInternalGetServantError(Throwable var1) {
      return this.poaInternalGetServantError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL poaInternalGetServantError() {
      return this.poaInternalGetServantError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL makeFactoryNotPoa(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080498, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "POA.makeFactoryNotPoa", var5, POASystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL makeFactoryNotPoa(CompletionStatus var1, Object var2) {
      return this.makeFactoryNotPoa(var1, (Throwable)null, var2);
   }

   public INTERNAL makeFactoryNotPoa(Throwable var1, Object var2) {
      return this.makeFactoryNotPoa(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL makeFactoryNotPoa(Object var1) {
      return this.makeFactoryNotPoa(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL duplicateOrbVersionSc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080499, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.duplicateOrbVersionSc", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL duplicateOrbVersionSc(CompletionStatus var1) {
      return this.duplicateOrbVersionSc(var1, (Throwable)null);
   }

   public INTERNAL duplicateOrbVersionSc(Throwable var1) {
      return this.duplicateOrbVersionSc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL duplicateOrbVersionSc() {
      return this.duplicateOrbVersionSc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL preinvokeCloneError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080500, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.preinvokeCloneError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL preinvokeCloneError(CompletionStatus var1) {
      return this.preinvokeCloneError(var1, (Throwable)null);
   }

   public INTERNAL preinvokeCloneError(Throwable var1) {
      return this.preinvokeCloneError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL preinvokeCloneError() {
      return this.preinvokeCloneError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL preinvokePoaDestroyed(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080501, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.preinvokePoaDestroyed", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL preinvokePoaDestroyed(CompletionStatus var1) {
      return this.preinvokePoaDestroyed(var1, (Throwable)null);
   }

   public INTERNAL preinvokePoaDestroyed(Throwable var1) {
      return this.preinvokePoaDestroyed(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL preinvokePoaDestroyed() {
      return this.preinvokePoaDestroyed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL pmfCreateRetain(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080502, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.pmfCreateRetain", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL pmfCreateRetain(CompletionStatus var1) {
      return this.pmfCreateRetain(var1, (Throwable)null);
   }

   public INTERNAL pmfCreateRetain(Throwable var1) {
      return this.pmfCreateRetain(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL pmfCreateRetain() {
      return this.pmfCreateRetain(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL pmfCreateNonRetain(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080503, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.pmfCreateNonRetain", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL pmfCreateNonRetain(CompletionStatus var1) {
      return this.pmfCreateNonRetain(var1, (Throwable)null);
   }

   public INTERNAL pmfCreateNonRetain(Throwable var1) {
      return this.pmfCreateNonRetain(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL pmfCreateNonRetain() {
      return this.pmfCreateNonRetain(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL policyMediatorBadPolicyInFactory(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080504, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.policyMediatorBadPolicyInFactory", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL policyMediatorBadPolicyInFactory(CompletionStatus var1) {
      return this.policyMediatorBadPolicyInFactory(var1, (Throwable)null);
   }

   public INTERNAL policyMediatorBadPolicyInFactory(Throwable var1) {
      return this.policyMediatorBadPolicyInFactory(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL policyMediatorBadPolicyInFactory() {
      return this.policyMediatorBadPolicyInFactory(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL servantToIdOaa(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080505, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantToIdOaa", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL servantToIdOaa(CompletionStatus var1) {
      return this.servantToIdOaa(var1, (Throwable)null);
   }

   public INTERNAL servantToIdOaa(Throwable var1) {
      return this.servantToIdOaa(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL servantToIdOaa() {
      return this.servantToIdOaa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL servantToIdSaa(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080506, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantToIdSaa", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL servantToIdSaa(CompletionStatus var1) {
      return this.servantToIdSaa(var1, (Throwable)null);
   }

   public INTERNAL servantToIdSaa(Throwable var1) {
      return this.servantToIdSaa(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL servantToIdSaa() {
      return this.servantToIdSaa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL servantToIdWp(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080507, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantToIdWp", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL servantToIdWp(CompletionStatus var1) {
      return this.servantToIdWp(var1, (Throwable)null);
   }

   public INTERNAL servantToIdWp(Throwable var1) {
      return this.servantToIdWp(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL servantToIdWp() {
      return this.servantToIdWp(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL cantResolveRootPoa(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080508, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.cantResolveRootPoa", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL cantResolveRootPoa(CompletionStatus var1) {
      return this.cantResolveRootPoa(var1, (Throwable)null);
   }

   public INTERNAL cantResolveRootPoa(Throwable var1) {
      return this.cantResolveRootPoa(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL cantResolveRootPoa() {
      return this.cantResolveRootPoa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL servantMustBeLocal(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080509, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantMustBeLocal", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL servantMustBeLocal(CompletionStatus var1) {
      return this.servantMustBeLocal(var1, (Throwable)null);
   }

   public INTERNAL servantMustBeLocal(Throwable var1) {
      return this.servantMustBeLocal(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL servantMustBeLocal() {
      return this.servantMustBeLocal(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL noProfilesInIor(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080510, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.noProfilesInIor", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL noProfilesInIor(CompletionStatus var1) {
      return this.noProfilesInIor(var1, (Throwable)null);
   }

   public INTERNAL noProfilesInIor(Throwable var1) {
      return this.noProfilesInIor(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL noProfilesInIor() {
      return this.noProfilesInIor(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL aomEntryDecZero(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080511, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.aomEntryDecZero", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL aomEntryDecZero(CompletionStatus var1) {
      return this.aomEntryDecZero(var1, (Throwable)null);
   }

   public INTERNAL aomEntryDecZero(Throwable var1) {
      return this.aomEntryDecZero(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL aomEntryDecZero() {
      return this.aomEntryDecZero(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL addPoaInactive(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080512, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.addPoaInactive", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL addPoaInactive(CompletionStatus var1) {
      return this.addPoaInactive(var1, (Throwable)null);
   }

   public INTERNAL addPoaInactive(Throwable var1) {
      return this.addPoaInactive(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL addPoaInactive() {
      return this.addPoaInactive(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL illegalPoaStateTrans(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080513, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.illegalPoaStateTrans", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL illegalPoaStateTrans(CompletionStatus var1) {
      return this.illegalPoaStateTrans(var1, (Throwable)null);
   }

   public INTERNAL illegalPoaStateTrans(Throwable var1) {
      return this.illegalPoaStateTrans(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL illegalPoaStateTrans() {
      return this.illegalPoaStateTrans(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unexpectedException(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398080514, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "POA.unexpectedException", var5, POASystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL unexpectedException(CompletionStatus var1, Object var2) {
      return this.unexpectedException(var1, (Throwable)null, var2);
   }

   public INTERNAL unexpectedException(Throwable var1, Object var2) {
      return this.unexpectedException(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL unexpectedException(Object var1) {
      return this.unexpectedException(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public NO_IMPLEMENT singleThreadNotSupported(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.singleThreadNotSupported", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT singleThreadNotSupported(CompletionStatus var1) {
      return this.singleThreadNotSupported(var1, (Throwable)null);
   }

   public NO_IMPLEMENT singleThreadNotSupported(Throwable var1) {
      return this.singleThreadNotSupported(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT singleThreadNotSupported() {
      return this.singleThreadNotSupported(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT methodNotImplemented(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.methodNotImplemented", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT methodNotImplemented(CompletionStatus var1) {
      return this.methodNotImplemented(var1, (Throwable)null);
   }

   public NO_IMPLEMENT methodNotImplemented(Throwable var1) {
      return this.methodNotImplemented(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT methodNotImplemented() {
      return this.methodNotImplemented(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaLookupError(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaLookupError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaLookupError(CompletionStatus var1) {
      return this.poaLookupError(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaLookupError(Throwable var1) {
      return this.poaLookupError(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaLookupError() {
      return this.poaLookupError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaInactive(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "POA.poaInactive", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaInactive(CompletionStatus var1) {
      return this.poaInactive(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaInactive(Throwable var1) {
      return this.poaInactive(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaInactive() {
      return this.poaInactive(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaNoServantManager(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080491, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaNoServantManager", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaNoServantManager(CompletionStatus var1) {
      return this.poaNoServantManager(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaNoServantManager(Throwable var1) {
      return this.poaNoServantManager(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaNoServantManager() {
      return this.poaNoServantManager(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaNoDefaultServant(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080492, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaNoDefaultServant", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaNoDefaultServant(CompletionStatus var1) {
      return this.poaNoDefaultServant(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaNoDefaultServant(Throwable var1) {
      return this.poaNoDefaultServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaNoDefaultServant() {
      return this.poaNoDefaultServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaServantNotUnique(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080493, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaServantNotUnique", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaServantNotUnique(CompletionStatus var1) {
      return this.poaServantNotUnique(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaServantNotUnique(Throwable var1) {
      return this.poaServantNotUnique(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaServantNotUnique() {
      return this.poaServantNotUnique(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaWrongPolicy(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080494, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaWrongPolicy", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaWrongPolicy(CompletionStatus var1) {
      return this.poaWrongPolicy(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaWrongPolicy(Throwable var1) {
      return this.poaWrongPolicy(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaWrongPolicy() {
      return this.poaWrongPolicy(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER findpoaError(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080495, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.findpoaError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER findpoaError(CompletionStatus var1) {
      return this.findpoaError(var1, (Throwable)null);
   }

   public OBJ_ADAPTER findpoaError(Throwable var1) {
      return this.findpoaError(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER findpoaError() {
      return this.findpoaError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaServantActivatorLookupFailed(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080497, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaServantActivatorLookupFailed", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaServantActivatorLookupFailed(CompletionStatus var1) {
      return this.poaServantActivatorLookupFailed(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaServantActivatorLookupFailed(Throwable var1) {
      return this.poaServantActivatorLookupFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaServantActivatorLookupFailed() {
      return this.poaServantActivatorLookupFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaBadServantManager(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080498, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaBadServantManager", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaBadServantManager(CompletionStatus var1) {
      return this.poaBadServantManager(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaBadServantManager(Throwable var1) {
      return this.poaBadServantManager(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaBadServantManager() {
      return this.poaBadServantManager(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaServantLocatorLookupFailed(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080499, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaServantLocatorLookupFailed", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaServantLocatorLookupFailed(CompletionStatus var1) {
      return this.poaServantLocatorLookupFailed(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaServantLocatorLookupFailed(Throwable var1) {
      return this.poaServantLocatorLookupFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaServantLocatorLookupFailed() {
      return this.poaServantLocatorLookupFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaUnknownPolicy(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080500, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaUnknownPolicy", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaUnknownPolicy(CompletionStatus var1) {
      return this.poaUnknownPolicy(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaUnknownPolicy(Throwable var1) {
      return this.poaUnknownPolicy(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaUnknownPolicy() {
      return this.poaUnknownPolicy(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER poaNotFound(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080501, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.poaNotFound", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER poaNotFound(CompletionStatus var1) {
      return this.poaNotFound(var1, (Throwable)null);
   }

   public OBJ_ADAPTER poaNotFound(Throwable var1) {
      return this.poaNotFound(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER poaNotFound() {
      return this.poaNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER servantLookup(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080502, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantLookup", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER servantLookup(CompletionStatus var1) {
      return this.servantLookup(var1, (Throwable)null);
   }

   public OBJ_ADAPTER servantLookup(Throwable var1) {
      return this.servantLookup(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER servantLookup() {
      return this.servantLookup(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER localServantLookup(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080503, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.localServantLookup", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER localServantLookup(CompletionStatus var1) {
      return this.localServantLookup(var1, (Throwable)null);
   }

   public OBJ_ADAPTER localServantLookup(Throwable var1) {
      return this.localServantLookup(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER localServantLookup() {
      return this.localServantLookup(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER servantManagerBadType(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080504, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.servantManagerBadType", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER servantManagerBadType(CompletionStatus var1) {
      return this.servantManagerBadType(var1, (Throwable)null);
   }

   public OBJ_ADAPTER servantManagerBadType(Throwable var1) {
      return this.servantManagerBadType(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER servantManagerBadType() {
      return this.servantManagerBadType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER defaultPoaNotPoaimpl(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080505, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.defaultPoaNotPoaimpl", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER defaultPoaNotPoaimpl(CompletionStatus var1) {
      return this.defaultPoaNotPoaimpl(var1, (Throwable)null);
   }

   public OBJ_ADAPTER defaultPoaNotPoaimpl(Throwable var1) {
      return this.defaultPoaNotPoaimpl(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER defaultPoaNotPoaimpl() {
      return this.defaultPoaNotPoaimpl(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER wrongPoliciesForThisObject(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080506, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.wrongPoliciesForThisObject", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER wrongPoliciesForThisObject(CompletionStatus var1) {
      return this.wrongPoliciesForThisObject(var1, (Throwable)null);
   }

   public OBJ_ADAPTER wrongPoliciesForThisObject(Throwable var1) {
      return this.wrongPoliciesForThisObject(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER wrongPoliciesForThisObject() {
      return this.wrongPoliciesForThisObject(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER thisObjectServantNotActive(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080507, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.thisObjectServantNotActive", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER thisObjectServantNotActive(CompletionStatus var1) {
      return this.thisObjectServantNotActive(var1, (Throwable)null);
   }

   public OBJ_ADAPTER thisObjectServantNotActive(Throwable var1) {
      return this.thisObjectServantNotActive(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER thisObjectServantNotActive() {
      return this.thisObjectServantNotActive(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER thisObjectWrongPolicy(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080508, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.thisObjectWrongPolicy", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER thisObjectWrongPolicy(CompletionStatus var1) {
      return this.thisObjectWrongPolicy(var1, (Throwable)null);
   }

   public OBJ_ADAPTER thisObjectWrongPolicy(Throwable var1) {
      return this.thisObjectWrongPolicy(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER thisObjectWrongPolicy() {
      return this.thisObjectWrongPolicy(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER noContext(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080509, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "POA.noContext", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER noContext(CompletionStatus var1) {
      return this.noContext(var1, (Throwable)null);
   }

   public OBJ_ADAPTER noContext(Throwable var1) {
      return this.noContext(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER noContext() {
      return this.noContext(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER incarnateReturnedNull(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398080510, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.incarnateReturnedNull", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER incarnateReturnedNull(CompletionStatus var1) {
      return this.incarnateReturnedNull(var1, (Throwable)null);
   }

   public OBJ_ADAPTER incarnateReturnedNull(Throwable var1) {
      return this.incarnateReturnedNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER incarnateReturnedNull() {
      return this.incarnateReturnedNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE jtsInitError(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.jtsInitError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE jtsInitError(CompletionStatus var1) {
      return this.jtsInitError(var1, (Throwable)null);
   }

   public INITIALIZE jtsInitError(Throwable var1) {
      return this.jtsInitError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE jtsInitError() {
      return this.jtsInitError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE persistentServeridNotSet(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.persistentServeridNotSet", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE persistentServeridNotSet(CompletionStatus var1) {
      return this.persistentServeridNotSet(var1, (Throwable)null);
   }

   public INITIALIZE persistentServeridNotSet(Throwable var1) {
      return this.persistentServeridNotSet(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE persistentServeridNotSet() {
      return this.persistentServeridNotSet(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE persistentServerportNotSet(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080491, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.persistentServerportNotSet", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE persistentServerportNotSet(CompletionStatus var1) {
      return this.persistentServerportNotSet(var1, (Throwable)null);
   }

   public INITIALIZE persistentServerportNotSet(Throwable var1) {
      return this.persistentServerportNotSet(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE persistentServerportNotSet() {
      return this.persistentServerportNotSet(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE orbdError(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080492, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.orbdError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE orbdError(CompletionStatus var1) {
      return this.orbdError(var1, (Throwable)null);
   }

   public INITIALIZE orbdError(Throwable var1) {
      return this.orbdError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE orbdError() {
      return this.orbdError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE bootstrapError(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080493, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.bootstrapError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE bootstrapError(CompletionStatus var1) {
      return this.bootstrapError(var1, (Throwable)null);
   }

   public INITIALIZE bootstrapError(Throwable var1) {
      return this.bootstrapError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE bootstrapError() {
      return this.bootstrapError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSIENT poaDiscarding(CompletionStatus var1, Throwable var2) {
      TRANSIENT var3 = new TRANSIENT(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "POA.poaDiscarding", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public TRANSIENT poaDiscarding(CompletionStatus var1) {
      return this.poaDiscarding(var1, (Throwable)null);
   }

   public TRANSIENT poaDiscarding(Throwable var1) {
      return this.poaDiscarding(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSIENT poaDiscarding() {
      return this.poaDiscarding(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN otshookexception(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.otshookexception", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN otshookexception(CompletionStatus var1) {
      return this.otshookexception(var1, (Throwable)null);
   }

   public UNKNOWN otshookexception(Throwable var1) {
      return this.otshookexception(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN otshookexception() {
      return this.otshookexception(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownServerException(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.unknownServerException", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownServerException(CompletionStatus var1) {
      return this.unknownServerException(var1, (Throwable)null);
   }

   public UNKNOWN unknownServerException(Throwable var1) {
      return this.unknownServerException(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownServerException() {
      return this.unknownServerException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownServerappException(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080491, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.unknownServerappException", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownServerappException(CompletionStatus var1) {
      return this.unknownServerappException(var1, (Throwable)null);
   }

   public UNKNOWN unknownServerappException(Throwable var1) {
      return this.unknownServerappException(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownServerappException() {
      return this.unknownServerappException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownLocalinvocationError(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080492, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.unknownLocalinvocationError", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownLocalinvocationError(CompletionStatus var1) {
      return this.unknownLocalinvocationError(var1, (Throwable)null);
   }

   public UNKNOWN unknownLocalinvocationError(Throwable var1) {
      return this.unknownLocalinvocationError(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownLocalinvocationError() {
      return this.unknownLocalinvocationError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST adapterActivatorNonexistent(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398080489, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.adapterActivatorNonexistent", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST adapterActivatorNonexistent(CompletionStatus var1) {
      return this.adapterActivatorNonexistent(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST adapterActivatorNonexistent(Throwable var1) {
      return this.adapterActivatorNonexistent(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST adapterActivatorNonexistent() {
      return this.adapterActivatorNonexistent(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST adapterActivatorFailed(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398080490, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.adapterActivatorFailed", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST adapterActivatorFailed(CompletionStatus var1) {
      return this.adapterActivatorFailed(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST adapterActivatorFailed(Throwable var1) {
      return this.adapterActivatorFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST adapterActivatorFailed() {
      return this.adapterActivatorFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badSkeleton(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398080491, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.badSkeleton", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST badSkeleton(CompletionStatus var1) {
      return this.badSkeleton(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badSkeleton(Throwable var1) {
      return this.badSkeleton(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST badSkeleton() {
      return this.badSkeleton(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST nullServant(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398080492, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "POA.nullServant", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST nullServant(CompletionStatus var1) {
      return this.nullServant(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST nullServant(Throwable var1) {
      return this.nullServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST nullServant() {
      return this.nullServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST adapterDestroyed(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398080493, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "POA.adapterDestroyed", (Object[])var4, POASystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST adapterDestroyed(CompletionStatus var1) {
      return this.adapterDestroyed(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST adapterDestroyed(Throwable var1) {
      return this.adapterDestroyed(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST adapterDestroyed() {
      return this.adapterDestroyed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
