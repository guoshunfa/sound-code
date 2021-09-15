package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.UNKNOWN;

public class NamingSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new NamingSystemException(var1);
      }
   };
   public static final int TRANSIENT_NAME_SERVER_BAD_PORT = 1398080088;
   public static final int TRANSIENT_NAME_SERVER_BAD_HOST = 1398080089;
   public static final int OBJECT_IS_NULL = 1398080090;
   public static final int INS_BAD_ADDRESS = 1398080091;
   public static final int BIND_UPDATE_CONTEXT_FAILED = 1398080088;
   public static final int BIND_FAILURE = 1398080089;
   public static final int RESOLVE_CONVERSION_FAILURE = 1398080090;
   public static final int RESOLVE_FAILURE = 1398080091;
   public static final int UNBIND_FAILURE = 1398080092;
   public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC_SYS = 1398080138;
   public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC = 1398080139;
   public static final int NAMING_CTX_REBIND_ALREADY_BOUND = 1398080088;
   public static final int NAMING_CTX_REBINDCTX_ALREADY_BOUND = 1398080089;
   public static final int NAMING_CTX_BAD_BINDINGTYPE = 1398080090;
   public static final int NAMING_CTX_RESOLVE_CANNOT_NARROW_TO_CTX = 1398080091;
   public static final int NAMING_CTX_BINDING_ITERATOR_CREATE = 1398080092;
   public static final int TRANS_NC_BIND_ALREADY_BOUND = 1398080188;
   public static final int TRANS_NC_LIST_GOT_EXC = 1398080189;
   public static final int TRANS_NC_NEWCTX_GOT_EXC = 1398080190;
   public static final int TRANS_NC_DESTROY_GOT_EXC = 1398080191;
   public static final int INS_BAD_SCHEME_NAME = 1398080193;
   public static final int INS_BAD_SCHEME_SPECIFIC_PART = 1398080195;
   public static final int INS_OTHER = 1398080196;

   public NamingSystemException(Logger var1) {
      super(var1);
   }

   public static NamingSystemException get(ORB var0, String var1) {
      NamingSystemException var2 = (NamingSystemException)var0.getLogWrapper(var1, "NAMING", factory);
      return var2;
   }

   public static NamingSystemException get(String var0) {
      NamingSystemException var1 = (NamingSystemException)ORB.staticGetLogWrapper(var0, "NAMING", factory);
      return var1;
   }

   public BAD_PARAM transientNameServerBadPort(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080088, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transientNameServerBadPort", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM transientNameServerBadPort(CompletionStatus var1) {
      return this.transientNameServerBadPort(var1, (Throwable)null);
   }

   public BAD_PARAM transientNameServerBadPort(Throwable var1) {
      return this.transientNameServerBadPort(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM transientNameServerBadPort() {
      return this.transientNameServerBadPort(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM transientNameServerBadHost(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080089, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transientNameServerBadHost", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM transientNameServerBadHost(CompletionStatus var1) {
      return this.transientNameServerBadHost(var1, (Throwable)null);
   }

   public BAD_PARAM transientNameServerBadHost(Throwable var1) {
      return this.transientNameServerBadHost(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM transientNameServerBadHost() {
      return this.transientNameServerBadHost(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM objectIsNull(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080090, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.objectIsNull", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM objectIsNull(CompletionStatus var1) {
      return this.objectIsNull(var1, (Throwable)null);
   }

   public BAD_PARAM objectIsNull(Throwable var1) {
      return this.objectIsNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM objectIsNull() {
      return this.objectIsNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM insBadAddress(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398080091, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.insBadAddress", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM insBadAddress(CompletionStatus var1) {
      return this.insBadAddress(var1, (Throwable)null);
   }

   public BAD_PARAM insBadAddress(Throwable var1) {
      return this.insBadAddress(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM insBadAddress() {
      return this.insBadAddress(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN bindUpdateContextFailed(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080088, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.bindUpdateContextFailed", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN bindUpdateContextFailed(CompletionStatus var1) {
      return this.bindUpdateContextFailed(var1, (Throwable)null);
   }

   public UNKNOWN bindUpdateContextFailed(Throwable var1) {
      return this.bindUpdateContextFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN bindUpdateContextFailed() {
      return this.bindUpdateContextFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN bindFailure(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080089, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.bindFailure", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN bindFailure(CompletionStatus var1) {
      return this.bindFailure(var1, (Throwable)null);
   }

   public UNKNOWN bindFailure(Throwable var1) {
      return this.bindFailure(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN bindFailure() {
      return this.bindFailure(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN resolveConversionFailure(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080090, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.resolveConversionFailure", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN resolveConversionFailure(CompletionStatus var1) {
      return this.resolveConversionFailure(var1, (Throwable)null);
   }

   public UNKNOWN resolveConversionFailure(Throwable var1) {
      return this.resolveConversionFailure(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN resolveConversionFailure() {
      return this.resolveConversionFailure(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN resolveFailure(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080091, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.resolveFailure", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN resolveFailure(CompletionStatus var1) {
      return this.resolveFailure(var1, (Throwable)null);
   }

   public UNKNOWN resolveFailure(Throwable var1) {
      return this.resolveFailure(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN resolveFailure() {
      return this.resolveFailure(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unbindFailure(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398080092, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.unbindFailure", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unbindFailure(CompletionStatus var1) {
      return this.unbindFailure(var1, (Throwable)null);
   }

   public UNKNOWN unbindFailure(Throwable var1) {
      return this.unbindFailure(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unbindFailure() {
      return this.unbindFailure(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE transNsCannotCreateInitialNcSys(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080138, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transNsCannotCreateInitialNcSys", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE transNsCannotCreateInitialNcSys(CompletionStatus var1) {
      return this.transNsCannotCreateInitialNcSys(var1, (Throwable)null);
   }

   public INITIALIZE transNsCannotCreateInitialNcSys(Throwable var1) {
      return this.transNsCannotCreateInitialNcSys(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE transNsCannotCreateInitialNcSys() {
      return this.transNsCannotCreateInitialNcSys(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE transNsCannotCreateInitialNc(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398080139, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transNsCannotCreateInitialNc", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE transNsCannotCreateInitialNc(CompletionStatus var1) {
      return this.transNsCannotCreateInitialNc(var1, (Throwable)null);
   }

   public INITIALIZE transNsCannotCreateInitialNc(Throwable var1) {
      return this.transNsCannotCreateInitialNc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE transNsCannotCreateInitialNc() {
      return this.transNsCannotCreateInitialNc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL namingCtxRebindAlreadyBound(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080088, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.namingCtxRebindAlreadyBound", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL namingCtxRebindAlreadyBound(CompletionStatus var1) {
      return this.namingCtxRebindAlreadyBound(var1, (Throwable)null);
   }

   public INTERNAL namingCtxRebindAlreadyBound(Throwable var1) {
      return this.namingCtxRebindAlreadyBound(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL namingCtxRebindAlreadyBound() {
      return this.namingCtxRebindAlreadyBound(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL namingCtxRebindctxAlreadyBound(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080089, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.namingCtxRebindctxAlreadyBound", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL namingCtxRebindctxAlreadyBound(CompletionStatus var1) {
      return this.namingCtxRebindctxAlreadyBound(var1, (Throwable)null);
   }

   public INTERNAL namingCtxRebindctxAlreadyBound(Throwable var1) {
      return this.namingCtxRebindctxAlreadyBound(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL namingCtxRebindctxAlreadyBound() {
      return this.namingCtxRebindctxAlreadyBound(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL namingCtxBadBindingtype(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080090, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.namingCtxBadBindingtype", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL namingCtxBadBindingtype(CompletionStatus var1) {
      return this.namingCtxBadBindingtype(var1, (Throwable)null);
   }

   public INTERNAL namingCtxBadBindingtype(Throwable var1) {
      return this.namingCtxBadBindingtype(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL namingCtxBadBindingtype() {
      return this.namingCtxBadBindingtype(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL namingCtxResolveCannotNarrowToCtx(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080091, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.namingCtxResolveCannotNarrowToCtx", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL namingCtxResolveCannotNarrowToCtx(CompletionStatus var1) {
      return this.namingCtxResolveCannotNarrowToCtx(var1, (Throwable)null);
   }

   public INTERNAL namingCtxResolveCannotNarrowToCtx(Throwable var1) {
      return this.namingCtxResolveCannotNarrowToCtx(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL namingCtxResolveCannotNarrowToCtx() {
      return this.namingCtxResolveCannotNarrowToCtx(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL namingCtxBindingIteratorCreate(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080092, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.namingCtxBindingIteratorCreate", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL namingCtxBindingIteratorCreate(CompletionStatus var1) {
      return this.namingCtxBindingIteratorCreate(var1, (Throwable)null);
   }

   public INTERNAL namingCtxBindingIteratorCreate(Throwable var1) {
      return this.namingCtxBindingIteratorCreate(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL namingCtxBindingIteratorCreate() {
      return this.namingCtxBindingIteratorCreate(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL transNcBindAlreadyBound(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080188, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transNcBindAlreadyBound", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL transNcBindAlreadyBound(CompletionStatus var1) {
      return this.transNcBindAlreadyBound(var1, (Throwable)null);
   }

   public INTERNAL transNcBindAlreadyBound(Throwable var1) {
      return this.transNcBindAlreadyBound(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL transNcBindAlreadyBound() {
      return this.transNcBindAlreadyBound(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL transNcListGotExc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080189, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transNcListGotExc", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL transNcListGotExc(CompletionStatus var1) {
      return this.transNcListGotExc(var1, (Throwable)null);
   }

   public INTERNAL transNcListGotExc(Throwable var1) {
      return this.transNcListGotExc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL transNcListGotExc() {
      return this.transNcListGotExc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL transNcNewctxGotExc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080190, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transNcNewctxGotExc", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL transNcNewctxGotExc(CompletionStatus var1) {
      return this.transNcNewctxGotExc(var1, (Throwable)null);
   }

   public INTERNAL transNcNewctxGotExc(Throwable var1) {
      return this.transNcNewctxGotExc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL transNcNewctxGotExc() {
      return this.transNcNewctxGotExc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL transNcDestroyGotExc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080191, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.transNcDestroyGotExc", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL transNcDestroyGotExc(CompletionStatus var1) {
      return this.transNcDestroyGotExc(var1, (Throwable)null);
   }

   public INTERNAL transNcDestroyGotExc(Throwable var1) {
      return this.transNcDestroyGotExc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL transNcDestroyGotExc() {
      return this.transNcDestroyGotExc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL insBadSchemeName(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080193, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.insBadSchemeName", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL insBadSchemeName(CompletionStatus var1) {
      return this.insBadSchemeName(var1, (Throwable)null);
   }

   public INTERNAL insBadSchemeName(Throwable var1) {
      return this.insBadSchemeName(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL insBadSchemeName() {
      return this.insBadSchemeName(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL insBadSchemeSpecificPart(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080195, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.insBadSchemeSpecificPart", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL insBadSchemeSpecificPart(CompletionStatus var1) {
      return this.insBadSchemeSpecificPart(var1, (Throwable)null);
   }

   public INTERNAL insBadSchemeSpecificPart(Throwable var1) {
      return this.insBadSchemeSpecificPart(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL insBadSchemeSpecificPart() {
      return this.insBadSchemeSpecificPart(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL insOther(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398080196, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "NAMING.insOther", (Object[])var4, NamingSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL insOther(CompletionStatus var1) {
      return this.insOther(var1, (Throwable)null);
   }

   public INTERNAL insOther(Throwable var1) {
      return this.insOther(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL insOther() {
      return this.insOther(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
