package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.util.ArrayList;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableServer.DynamicImplementation;
import org.omg.PortableServer.Servant;

public final class ServerRequestInfoImpl extends RequestInfoImpl implements ServerRequestInfo {
   static final int CALL_RECEIVE_REQUEST_SERVICE_CONTEXT = 0;
   static final int CALL_RECEIVE_REQUEST = 0;
   static final int CALL_INTERMEDIATE_NONE = 1;
   static final int CALL_SEND_REPLY = 0;
   static final int CALL_SEND_EXCEPTION = 1;
   static final int CALL_SEND_OTHER = 2;
   private boolean forwardRequestRaisedInEnding;
   private CorbaMessageMediator request;
   private Object servant;
   private byte[] objectId;
   private ObjectKeyTemplate oktemp;
   private byte[] adapterId;
   private String[] adapterName;
   private ArrayList addReplyServiceContextQueue;
   private ReplyMessage replyMessage;
   private String targetMostDerivedInterface;
   private NVList dsiArguments;
   private Any dsiResult;
   private Any dsiException;
   private boolean isDynamic;
   private ObjectAdapter objectAdapter;
   private int serverRequestId;
   private Parameter[] cachedArguments;
   private Any cachedSendingException;
   private HashMap cachedRequestServiceContexts;
   private HashMap cachedReplyServiceContexts;
   protected static final int MID_SENDING_EXCEPTION = 14;
   protected static final int MID_OBJECT_ID = 15;
   protected static final int MID_ADAPTER_ID = 16;
   protected static final int MID_TARGET_MOST_DERIVED_INTERFACE = 17;
   protected static final int MID_GET_SERVER_POLICY = 18;
   protected static final int MID_SET_SLOT = 19;
   protected static final int MID_TARGET_IS_A = 20;
   protected static final int MID_ADD_REPLY_SERVICE_CONTEXT = 21;
   protected static final int MID_SERVER_ID = 22;
   protected static final int MID_ORB_ID = 23;
   protected static final int MID_ADAPTER_NAME = 24;
   private static final boolean[][] validCall = new boolean[][]{{true, true, true, true, true}, {true, true, true, true, true}, {false, true, true, false, false}, {false, true, true, true, true}, {false, true, true, true, true}, {false, true, true, false, false}, {false, false, true, false, false}, {true, true, true, true, true}, {true, true, true, true, true}, {false, false, true, true, true}, {false, false, false, false, true}, {true, true, true, true, true}, {true, true, true, true, true}, {false, false, true, true, true}, {false, false, false, true, false}, {false, true, true, true, true}, {false, true, true, true, true}, {false, true, false, false, false}, {true, true, true, true, true}, {true, true, true, true, true}, {false, true, false, false, false}, {true, true, true, true, true}, {false, true, true, true, true}, {false, true, true, true, true}, {false, true, true, true, true}};

   void reset() {
      super.reset();
      this.forwardRequestRaisedInEnding = false;
      this.request = null;
      this.servant = null;
      this.objectId = null;
      this.oktemp = null;
      this.adapterId = null;
      this.adapterName = null;
      this.addReplyServiceContextQueue = null;
      this.replyMessage = null;
      this.targetMostDerivedInterface = null;
      this.dsiArguments = null;
      this.dsiResult = null;
      this.dsiException = null;
      this.isDynamic = false;
      this.objectAdapter = null;
      this.serverRequestId = this.myORB.getPIHandler().allocateServerRequestId();
      this.cachedArguments = null;
      this.cachedSendingException = null;
      this.cachedRequestServiceContexts = null;
      this.cachedReplyServiceContexts = null;
      this.startingPointCall = 0;
      this.intermediatePointCall = 0;
      this.endingPointCall = 0;
   }

   ServerRequestInfoImpl(ORB var1) {
      super(var1);
      this.startingPointCall = 0;
      this.intermediatePointCall = 0;
      this.endingPointCall = 0;
      this.serverRequestId = var1.getPIHandler().allocateServerRequestId();
   }

   public Any sending_exception() {
      this.checkAccess(14);
      if (this.cachedSendingException == null) {
         Any var1 = null;
         if (this.dsiException != null) {
            var1 = this.dsiException;
         } else {
            if (this.exception == null) {
               throw this.wrapper.exceptionUnavailable();
            }

            var1 = this.exceptionToAny(this.exception);
         }

         this.cachedSendingException = var1;
      }

      return this.cachedSendingException;
   }

   public byte[] object_id() {
      this.checkAccess(15);
      if (this.objectId == null) {
         throw this.stdWrapper.piOperationNotSupported6();
      } else {
         return this.objectId;
      }
   }

   private void checkForNullTemplate() {
      if (this.oktemp == null) {
         throw this.stdWrapper.piOperationNotSupported7();
      }
   }

   public String server_id() {
      this.checkAccess(22);
      this.checkForNullTemplate();
      return Integer.toString(this.oktemp.getServerId());
   }

   public String orb_id() {
      this.checkAccess(23);
      return this.myORB.getORBData().getORBId();
   }

   public synchronized String[] adapter_name() {
      this.checkAccess(24);
      if (this.adapterName == null) {
         this.checkForNullTemplate();
         ObjectAdapterId var1 = this.oktemp.getObjectAdapterId();
         this.adapterName = var1.getAdapterName();
      }

      return this.adapterName;
   }

   public synchronized byte[] adapter_id() {
      this.checkAccess(16);
      if (this.adapterId == null) {
         this.checkForNullTemplate();
         this.adapterId = this.oktemp.getAdapterId();
      }

      return this.adapterId;
   }

   public String target_most_derived_interface() {
      this.checkAccess(17);
      return this.targetMostDerivedInterface;
   }

   public Policy get_server_policy(int var1) {
      Policy var2 = null;
      if (this.objectAdapter != null) {
         var2 = this.objectAdapter.getEffectivePolicy(var1);
      }

      return var2;
   }

   public void set_slot(int var1, Any var2) throws InvalidSlot {
      this.slotTable.set_slot(var1, var2);
   }

   public boolean target_is_a(String var1) {
      this.checkAccess(20);
      boolean var2 = false;
      if (this.servant instanceof Servant) {
         var2 = ((Servant)this.servant)._is_a(var1);
      } else {
         if (!StubAdapter.isStub(this.servant)) {
            throw this.wrapper.servantInvalid();
         }

         var2 = ((org.omg.CORBA.Object)this.servant)._is_a(var1);
      }

      return var2;
   }

   public void add_reply_service_context(ServiceContext var1, boolean var2) {
      if (this.currentExecutionPoint == 2) {
         ServiceContexts var3 = this.replyMessage.getServiceContexts();
         if (var3 == null) {
            var3 = new ServiceContexts(this.myORB);
            this.replyMessage.setServiceContexts(var3);
         }

         if (this.cachedReplyServiceContexts == null) {
            this.cachedReplyServiceContexts = new HashMap();
         }

         this.addServiceContext(this.cachedReplyServiceContexts, var3, var1, var2);
      }

      ServerRequestInfoImpl.AddReplyServiceContextCommand var4 = new ServerRequestInfoImpl.AddReplyServiceContextCommand();
      var4.service_context = var1;
      var4.replace = var2;
      if (this.addReplyServiceContextQueue == null) {
         this.addReplyServiceContextQueue = new ArrayList();
      }

      this.enqueue(var4);
   }

   public int request_id() {
      return this.serverRequestId;
   }

   public String operation() {
      return this.request.getOperationName();
   }

   public Parameter[] arguments() {
      this.checkAccess(2);
      if (this.cachedArguments == null) {
         if (!this.isDynamic) {
            throw this.stdWrapper.piOperationNotSupported1();
         }

         if (this.dsiArguments == null) {
            throw this.stdWrapper.piOperationNotSupported8();
         }

         this.cachedArguments = this.nvListToParameterArray(this.dsiArguments);
      }

      return this.cachedArguments;
   }

   public TypeCode[] exceptions() {
      this.checkAccess(3);
      throw this.stdWrapper.piOperationNotSupported2();
   }

   public String[] contexts() {
      this.checkAccess(4);
      throw this.stdWrapper.piOperationNotSupported3();
   }

   public String[] operation_context() {
      this.checkAccess(5);
      throw this.stdWrapper.piOperationNotSupported4();
   }

   public Any result() {
      this.checkAccess(6);
      if (!this.isDynamic) {
         throw this.stdWrapper.piOperationNotSupported5();
      } else if (this.dsiResult == null) {
         throw this.wrapper.piDsiResultIsNull();
      } else {
         return this.dsiResult;
      }
   }

   public boolean response_expected() {
      return !this.request.isOneWay();
   }

   public org.omg.CORBA.Object forward_reference() {
      this.checkAccess(10);
      if (this.replyStatus != 3) {
         throw this.stdWrapper.invalidPiCall1();
      } else {
         return this.getForwardRequestException().forward;
      }
   }

   public ServiceContext get_request_service_context(int var1) {
      this.checkAccess(12);
      if (this.cachedRequestServiceContexts == null) {
         this.cachedRequestServiceContexts = new HashMap();
      }

      return this.getServiceContext(this.cachedRequestServiceContexts, this.request.getRequestServiceContexts(), var1);
   }

   public ServiceContext get_reply_service_context(int var1) {
      this.checkAccess(13);
      if (this.cachedReplyServiceContexts == null) {
         this.cachedReplyServiceContexts = new HashMap();
      }

      return this.getServiceContext(this.cachedReplyServiceContexts, this.replyMessage.getServiceContexts(), var1);
   }

   private void enqueue(ServerRequestInfoImpl.AddReplyServiceContextCommand var1) {
      int var2 = this.addReplyServiceContextQueue.size();
      boolean var3 = false;

      for(int var4 = 0; var4 < var2; ++var4) {
         ServerRequestInfoImpl.AddReplyServiceContextCommand var5 = (ServerRequestInfoImpl.AddReplyServiceContextCommand)this.addReplyServiceContextQueue.get(var4);
         if (var5.service_context.context_id == var1.service_context.context_id) {
            var3 = true;
            if (!var1.replace) {
               throw this.stdWrapper.serviceContextAddFailed(new Integer(var5.service_context.context_id));
            }

            this.addReplyServiceContextQueue.set(var4, var1);
            break;
         }
      }

      if (!var3) {
         this.addReplyServiceContextQueue.add(var1);
      }

   }

   protected void setCurrentExecutionPoint(int var1) {
      super.setCurrentExecutionPoint(var1);
      if (var1 == 2 && this.addReplyServiceContextQueue != null) {
         int var2 = this.addReplyServiceContextQueue.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            ServerRequestInfoImpl.AddReplyServiceContextCommand var4 = (ServerRequestInfoImpl.AddReplyServiceContextCommand)this.addReplyServiceContextQueue.get(var3);

            try {
               this.add_reply_service_context(var4.service_context, var4.replace);
            } catch (BAD_INV_ORDER var6) {
            }
         }
      }

   }

   protected void setInfo(CorbaMessageMediator var1, ObjectAdapter var2, byte[] var3, ObjectKeyTemplate var4) {
      this.request = var1;
      this.objectId = var3;
      this.oktemp = var4;
      this.objectAdapter = var2;
      this.connection = (Connection)var1.getConnection();
   }

   protected void setDSIArguments(NVList var1) {
      this.dsiArguments = var1;
   }

   protected void setDSIException(Any var1) {
      this.dsiException = var1;
      this.cachedSendingException = null;
   }

   protected void setDSIResult(Any var1) {
      this.dsiResult = var1;
   }

   protected void setException(Exception var1) {
      super.setException(var1);
      this.dsiException = null;
      this.cachedSendingException = null;
   }

   protected void setInfo(Object var1, String var2) {
      this.servant = var1;
      this.targetMostDerivedInterface = var2;
      this.isDynamic = var1 instanceof DynamicImplementation || var1 instanceof org.omg.CORBA.DynamicImplementation;
   }

   void setReplyMessage(ReplyMessage var1) {
      this.replyMessage = var1;
   }

   protected void setReplyStatus(short var1) {
      super.setReplyStatus(var1);
      switch(var1) {
      case 0:
         this.endingPointCall = 0;
         break;
      case 1:
      case 2:
         this.endingPointCall = 1;
         break;
      case 3:
      case 4:
         this.endingPointCall = 2;
      }

   }

   void releaseServant() {
      this.servant = null;
   }

   void setForwardRequestRaisedInEnding() {
      this.forwardRequestRaisedInEnding = true;
   }

   boolean isForwardRequestRaisedInEnding() {
      return this.forwardRequestRaisedInEnding;
   }

   boolean isDynamic() {
      return this.isDynamic;
   }

   protected void checkAccess(int var1) {
      byte var2 = 0;
      switch(this.currentExecutionPoint) {
      case 0:
         var2 = 0;
         break;
      case 1:
         var2 = 1;
         break;
      case 2:
         switch(this.endingPointCall) {
         case 0:
            var2 = 2;
            break;
         case 1:
            var2 = 3;
            break;
         case 2:
            var2 = 4;
         }
      }

      if (!validCall[var1][var2]) {
         throw this.stdWrapper.invalidPiCall2();
      }
   }

   private class AddReplyServiceContextCommand {
      ServiceContext service_context;
      boolean replace;

      private AddReplyServiceContextCommand() {
      }

      // $FF: synthetic method
      AddReplyServiceContextCommand(Object var2) {
         this();
      }
   }
}
