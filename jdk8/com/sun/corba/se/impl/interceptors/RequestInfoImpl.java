package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.legacy.interceptor.RequestInfoExt;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.ServiceContextHelper;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.RequestInfo;
import sun.corba.OutputStreamFactory;
import sun.corba.SharedSecrets;

public abstract class RequestInfoImpl extends LocalObject implements RequestInfo, RequestInfoExt {
   protected ORB myORB;
   protected InterceptorsSystemException wrapper;
   protected OMGSystemException stdWrapper;
   protected int flowStackIndex = 0;
   protected int startingPointCall;
   protected int intermediatePointCall;
   protected int endingPointCall;
   protected short replyStatus = -1;
   protected static final short UNINITIALIZED = -1;
   protected int currentExecutionPoint;
   protected static final int EXECUTION_POINT_STARTING = 0;
   protected static final int EXECUTION_POINT_INTERMEDIATE = 1;
   protected static final int EXECUTION_POINT_ENDING = 2;
   protected boolean alreadyExecuted;
   protected Connection connection;
   protected ServiceContexts serviceContexts;
   protected ForwardRequest forwardRequest;
   protected IOR forwardRequestIOR;
   protected SlotTable slotTable;
   protected Exception exception;
   protected static final int MID_REQUEST_ID = 0;
   protected static final int MID_OPERATION = 1;
   protected static final int MID_ARGUMENTS = 2;
   protected static final int MID_EXCEPTIONS = 3;
   protected static final int MID_CONTEXTS = 4;
   protected static final int MID_OPERATION_CONTEXT = 5;
   protected static final int MID_RESULT = 6;
   protected static final int MID_RESPONSE_EXPECTED = 7;
   protected static final int MID_SYNC_SCOPE = 8;
   protected static final int MID_REPLY_STATUS = 9;
   protected static final int MID_FORWARD_REFERENCE = 10;
   protected static final int MID_GET_SLOT = 11;
   protected static final int MID_GET_REQUEST_SERVICE_CONTEXT = 12;
   protected static final int MID_GET_REPLY_SERVICE_CONTEXT = 13;
   protected static final int MID_RI_LAST = 13;

   void reset() {
      this.flowStackIndex = 0;
      this.startingPointCall = 0;
      this.intermediatePointCall = 0;
      this.endingPointCall = 0;
      this.setReplyStatus((short)-1);
      this.currentExecutionPoint = 0;
      this.alreadyExecuted = false;
      this.connection = null;
      this.serviceContexts = null;
      this.forwardRequest = null;
      this.forwardRequestIOR = null;
      this.exception = null;
   }

   public RequestInfoImpl(ORB var1) {
      this.myORB = var1;
      this.wrapper = InterceptorsSystemException.get(var1, "rpc.protocol");
      this.stdWrapper = OMGSystemException.get(var1, "rpc.protocol");
      PICurrent var2 = (PICurrent)((PICurrent)var1.getPIHandler().getPICurrent());
      this.slotTable = var2.getSlotTable();
   }

   public abstract int request_id();

   public abstract String operation();

   public abstract Parameter[] arguments();

   public abstract TypeCode[] exceptions();

   public abstract String[] contexts();

   public abstract String[] operation_context();

   public abstract Any result();

   public abstract boolean response_expected();

   public short sync_scope() {
      this.checkAccess(8);
      return 1;
   }

   public short reply_status() {
      this.checkAccess(9);
      return this.replyStatus;
   }

   public abstract Object forward_reference();

   public Any get_slot(int var1) throws InvalidSlot {
      return this.slotTable.get_slot(var1);
   }

   public abstract ServiceContext get_request_service_context(int var1);

   public abstract ServiceContext get_reply_service_context(int var1);

   public Connection connection() {
      return this.connection;
   }

   private void insertApplicationException(ApplicationException var1, Any var2) throws UNKNOWN {
      try {
         RepositoryId var3 = RepositoryId.cache.getId(var1.getId());
         String var4 = var3.getClassName();
         String var5 = var4 + "Helper";
         Class var6 = SharedSecrets.getJavaCorbaAccess().loadClass(var5);
         Class[] var7 = new Class[]{InputStream.class};
         Method var8 = var6.getMethod("read", var7);
         InputStream var9 = var1.getInputStream();
         var9.mark(0);
         UserException var10 = null;

         try {
            java.lang.Object[] var11 = new java.lang.Object[]{var9};
            var10 = (UserException)var8.invoke((java.lang.Object)null, var11);
         } finally {
            try {
               var9.reset();
            } catch (IOException var23) {
               throw this.wrapper.markAndResetFailed((Throwable)var23);
            }
         }

         this.insertUserException(var10, var2);
      } catch (ClassNotFoundException var25) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var25);
      } catch (NoSuchMethodException var26) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var26);
      } catch (SecurityException var27) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var27);
      } catch (IllegalAccessException var28) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var28);
      } catch (IllegalArgumentException var29) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var29);
      } catch (InvocationTargetException var30) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var30);
      }
   }

   private void insertUserException(UserException var1, Any var2) throws UNKNOWN {
      try {
         if (var1 != null) {
            Class var3 = var1.getClass();
            String var4 = var3.getName();
            String var5 = var4 + "Helper";
            Class var6 = SharedSecrets.getJavaCorbaAccess().loadClass(var5);
            Class[] var7 = new Class[]{Any.class, var3};
            Method var8 = var6.getMethod("insert", var7);
            java.lang.Object[] var9 = new java.lang.Object[]{var2, var1};
            var8.invoke((java.lang.Object)null, var9);
         }

      } catch (ClassNotFoundException var10) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var10);
      } catch (NoSuchMethodException var11) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var11);
      } catch (SecurityException var12) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var12);
      } catch (IllegalAccessException var13) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var13);
      } catch (IllegalArgumentException var14) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var14);
      } catch (InvocationTargetException var15) {
         throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, var15);
      }
   }

   protected Parameter[] nvListToParameterArray(NVList var1) {
      int var2 = var1.count();
      Parameter[] var3 = new Parameter[var2];

      try {
         for(int var4 = 0; var4 < var2; ++var4) {
            Parameter var5 = new Parameter();
            var3[var4] = var5;
            NamedValue var6 = var1.item(var4);
            var3[var4].argument = var6.value();
            var3[var4].mode = ParameterMode.from_int(var6.flags() - 1);
         }

         return var3;
      } catch (Exception var7) {
         throw this.wrapper.exceptionInArguments((Throwable)var7);
      }
   }

   protected Any exceptionToAny(Exception var1) {
      Any var2 = this.myORB.create_any();
      if (var1 == null) {
         throw this.wrapper.exceptionWasNull2();
      } else {
         if (var1 instanceof SystemException) {
            ORBUtility.insertSystemException((SystemException)var1, var2);
         } else if (var1 instanceof ApplicationException) {
            try {
               ApplicationException var3 = (ApplicationException)var1;
               this.insertApplicationException(var3, var2);
            } catch (UNKNOWN var5) {
               ORBUtility.insertSystemException(var5, var2);
            }
         } else if (var1 instanceof UserException) {
            try {
               UserException var6 = (UserException)var1;
               this.insertUserException(var6, var2);
            } catch (UNKNOWN var4) {
               ORBUtility.insertSystemException(var4, var2);
            }
         }

         return var2;
      }
   }

   protected ServiceContext getServiceContext(HashMap var1, ServiceContexts var2, int var3) {
      ServiceContext var4 = null;
      Integer var5 = new Integer(var3);
      var4 = (ServiceContext)var1.get(var5);
      if (var4 == null) {
         com.sun.corba.se.spi.servicecontext.ServiceContext var6 = var2.get(var3);
         if (var6 == null) {
            throw this.stdWrapper.invalidServiceContextId();
         }

         EncapsOutputStream var7 = OutputStreamFactory.newEncapsOutputStream(this.myORB);
         var6.write(var7, GIOPVersion.V1_2);
         InputStream var8 = var7.create_input_stream();
         var4 = ServiceContextHelper.read(var8);
         var1.put(var5, var4);
      }

      return var4;
   }

   protected void addServiceContext(HashMap var1, ServiceContexts var2, ServiceContext var3, boolean var4) {
      boolean var5 = false;
      EncapsOutputStream var6 = OutputStreamFactory.newEncapsOutputStream(this.myORB);
      InputStream var7 = null;
      UnknownServiceContext var8 = null;
      ServiceContextHelper.write(var6, var3);
      var7 = var6.create_input_stream();
      var8 = new UnknownServiceContext(var7.read_long(), (org.omg.CORBA_2_3.portable.InputStream)var7);
      int var9 = var8.getId();
      if (var2.get(var9) != null) {
         if (!var4) {
            throw this.stdWrapper.serviceContextAddFailed(new Integer(var9));
         }

         var2.delete(var9);
      }

      var2.put(var8);
      var1.put(new Integer(var9), var3);
   }

   protected void setFlowStackIndex(int var1) {
      this.flowStackIndex = var1;
   }

   protected int getFlowStackIndex() {
      return this.flowStackIndex;
   }

   protected void setEndingPointCall(int var1) {
      this.endingPointCall = var1;
   }

   protected int getEndingPointCall() {
      return this.endingPointCall;
   }

   protected void setIntermediatePointCall(int var1) {
      this.intermediatePointCall = var1;
   }

   protected int getIntermediatePointCall() {
      return this.intermediatePointCall;
   }

   protected void setStartingPointCall(int var1) {
      this.startingPointCall = var1;
   }

   protected int getStartingPointCall() {
      return this.startingPointCall;
   }

   protected boolean getAlreadyExecuted() {
      return this.alreadyExecuted;
   }

   protected void setAlreadyExecuted(boolean var1) {
      this.alreadyExecuted = var1;
   }

   protected void setReplyStatus(short var1) {
      this.replyStatus = var1;
   }

   protected short getReplyStatus() {
      return this.replyStatus;
   }

   protected void setForwardRequest(ForwardRequest var1) {
      this.forwardRequest = var1;
      this.forwardRequestIOR = null;
   }

   protected void setForwardRequest(IOR var1) {
      this.forwardRequestIOR = var1;
      this.forwardRequest = null;
   }

   protected ForwardRequest getForwardRequestException() {
      if (this.forwardRequest == null && this.forwardRequestIOR != null) {
         Object var1 = this.iorToObject(this.forwardRequestIOR);
         this.forwardRequest = new ForwardRequest(var1);
      }

      return this.forwardRequest;
   }

   protected IOR getForwardRequestIOR() {
      if (this.forwardRequestIOR == null && this.forwardRequest != null) {
         this.forwardRequestIOR = ORBUtility.getIOR(this.forwardRequest.forward);
      }

      return this.forwardRequestIOR;
   }

   protected void setException(Exception var1) {
      this.exception = var1;
   }

   Exception getException() {
      return this.exception;
   }

   protected void setCurrentExecutionPoint(int var1) {
      this.currentExecutionPoint = var1;
   }

   protected abstract void checkAccess(int var1) throws BAD_INV_ORDER;

   void setSlotTable(SlotTable var1) {
      this.slotTable = var1;
   }

   protected Object iorToObject(IOR var1) {
      return ORBUtility.makeObjectReference(var1);
   }
}
