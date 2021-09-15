package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RetryType;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;
import org.omg.PortableInterceptor.ClientRequestInfo;

public final class ClientRequestInfoImpl extends RequestInfoImpl implements ClientRequestInfo {
   static final int CALL_SEND_REQUEST = 0;
   static final int CALL_SEND_POLL = 1;
   static final int CALL_RECEIVE_REPLY = 0;
   static final int CALL_RECEIVE_EXCEPTION = 1;
   static final int CALL_RECEIVE_OTHER = 2;
   private RetryType retryRequest;
   private int entryCount = 0;
   private Request request;
   private boolean diiInitiate;
   private CorbaMessageMediator messageMediator;
   private Object cachedTargetObject;
   private Object cachedEffectiveTargetObject;
   private Parameter[] cachedArguments;
   private TypeCode[] cachedExceptions;
   private String[] cachedContexts;
   private String[] cachedOperationContext;
   private String cachedReceivedExceptionId;
   private Any cachedResult;
   private Any cachedReceivedException;
   private TaggedProfile cachedEffectiveProfile;
   private HashMap cachedRequestServiceContexts;
   private HashMap cachedReplyServiceContexts;
   private HashMap cachedEffectiveComponents;
   protected boolean piCurrentPushed;
   protected static final int MID_TARGET = 14;
   protected static final int MID_EFFECTIVE_TARGET = 15;
   protected static final int MID_EFFECTIVE_PROFILE = 16;
   protected static final int MID_RECEIVED_EXCEPTION = 17;
   protected static final int MID_RECEIVED_EXCEPTION_ID = 18;
   protected static final int MID_GET_EFFECTIVE_COMPONENT = 19;
   protected static final int MID_GET_EFFECTIVE_COMPONENTS = 20;
   protected static final int MID_GET_REQUEST_POLICY = 21;
   protected static final int MID_ADD_REQUEST_SERVICE_CONTEXT = 22;
   private static final boolean[][] validCall = new boolean[][]{{true, true, true, true, true}, {true, true, true, true, true}, {true, false, true, false, false}, {true, false, true, true, true}, {true, false, true, true, true}, {true, false, true, true, true}, {false, false, true, false, false}, {true, true, true, true, true}, {true, false, true, true, true}, {false, false, true, true, true}, {false, false, false, false, true}, {true, true, true, true, true}, {true, false, true, true, true}, {false, false, true, true, true}, {true, true, true, true, true}, {true, true, true, true, true}, {true, true, true, true, true}, {false, false, false, true, false}, {false, false, false, true, false}, {true, false, true, true, true}, {true, false, true, true, true}, {true, false, true, true, true}, {true, false, false, false, false}};

   void reset() {
      super.reset();
      this.retryRequest = RetryType.NONE;
      this.request = null;
      this.diiInitiate = false;
      this.messageMediator = null;
      this.cachedTargetObject = null;
      this.cachedEffectiveTargetObject = null;
      this.cachedArguments = null;
      this.cachedExceptions = null;
      this.cachedContexts = null;
      this.cachedOperationContext = null;
      this.cachedReceivedExceptionId = null;
      this.cachedResult = null;
      this.cachedReceivedException = null;
      this.cachedEffectiveProfile = null;
      this.cachedRequestServiceContexts = null;
      this.cachedReplyServiceContexts = null;
      this.cachedEffectiveComponents = null;
      this.piCurrentPushed = false;
      this.startingPointCall = 0;
      this.endingPointCall = 0;
   }

   protected ClientRequestInfoImpl(ORB var1) {
      super(var1);
      this.startingPointCall = 0;
      this.endingPointCall = 0;
   }

   public Object target() {
      if (this.cachedTargetObject == null) {
         CorbaContactInfo var1 = (CorbaContactInfo)this.messageMediator.getContactInfo();
         this.cachedTargetObject = this.iorToObject(var1.getTargetIOR());
      }

      return this.cachedTargetObject;
   }

   public Object effective_target() {
      if (this.cachedEffectiveTargetObject == null) {
         CorbaContactInfo var1 = (CorbaContactInfo)this.messageMediator.getContactInfo();
         this.cachedEffectiveTargetObject = this.iorToObject(var1.getEffectiveTargetIOR());
      }

      return this.cachedEffectiveTargetObject;
   }

   public TaggedProfile effective_profile() {
      if (this.cachedEffectiveProfile == null) {
         CorbaContactInfo var1 = (CorbaContactInfo)this.messageMediator.getContactInfo();
         this.cachedEffectiveProfile = var1.getEffectiveProfile().getIOPProfile();
      }

      return this.cachedEffectiveProfile;
   }

   public Any received_exception() {
      this.checkAccess(17);
      if (this.cachedReceivedException == null) {
         this.cachedReceivedException = this.exceptionToAny(this.exception);
      }

      return this.cachedReceivedException;
   }

   public String received_exception_id() {
      this.checkAccess(18);
      if (this.cachedReceivedExceptionId == null) {
         String var1 = null;
         if (this.exception == null) {
            throw this.wrapper.exceptionWasNull();
         }

         if (this.exception instanceof SystemException) {
            String var2 = this.exception.getClass().getName();
            var1 = ORBUtility.repositoryIdOf(var2);
         } else if (this.exception instanceof ApplicationException) {
            var1 = ((ApplicationException)this.exception).getId();
         }

         this.cachedReceivedExceptionId = var1;
      }

      return this.cachedReceivedExceptionId;
   }

   public TaggedComponent get_effective_component(int var1) {
      this.checkAccess(19);
      return this.get_effective_components(var1)[0];
   }

   public TaggedComponent[] get_effective_components(int var1) {
      this.checkAccess(20);
      Integer var2 = new Integer(var1);
      TaggedComponent[] var3 = null;
      boolean var4 = false;
      if (this.cachedEffectiveComponents == null) {
         this.cachedEffectiveComponents = new HashMap();
         var4 = true;
      } else {
         var3 = (TaggedComponent[])((TaggedComponent[])this.cachedEffectiveComponents.get(var2));
      }

      if (var3 == null && (var4 || !this.cachedEffectiveComponents.containsKey(var2))) {
         CorbaContactInfo var5 = (CorbaContactInfo)this.messageMediator.getContactInfo();
         IIOPProfileTemplate var6 = (IIOPProfileTemplate)var5.getEffectiveProfile().getTaggedProfileTemplate();
         var3 = var6.getIOPComponents(this.myORB, var1);
         this.cachedEffectiveComponents.put(var2, var3);
      }

      if (var3 != null && var3.length != 0) {
         return var3;
      } else {
         throw this.stdWrapper.invalidComponentId(var2);
      }
   }

   public Policy get_request_policy(int var1) {
      this.checkAccess(21);
      throw this.wrapper.piOrbNotPolicyBased();
   }

   public void add_request_service_context(ServiceContext var1, boolean var2) {
      this.checkAccess(22);
      if (this.cachedRequestServiceContexts == null) {
         this.cachedRequestServiceContexts = new HashMap();
      }

      this.addServiceContext(this.cachedRequestServiceContexts, this.messageMediator.getRequestServiceContexts(), var1, var2);
   }

   public int request_id() {
      return this.messageMediator.getRequestId();
   }

   public String operation() {
      return this.messageMediator.getOperationName();
   }

   public Parameter[] arguments() {
      this.checkAccess(2);
      if (this.cachedArguments == null) {
         if (this.request == null) {
            throw this.stdWrapper.piOperationNotSupported1();
         }

         this.cachedArguments = this.nvListToParameterArray(this.request.arguments());
      }

      return this.cachedArguments;
   }

   public TypeCode[] exceptions() {
      this.checkAccess(3);
      if (this.cachedExceptions == null) {
         if (this.request == null) {
            throw this.stdWrapper.piOperationNotSupported2();
         }

         ExceptionList var1 = this.request.exceptions();
         int var2 = var1.count();
         TypeCode[] var3 = new TypeCode[var2];

         try {
            for(int var4 = 0; var4 < var2; ++var4) {
               var3[var4] = var1.item(var4);
            }
         } catch (Exception var5) {
            throw this.wrapper.exceptionInExceptions((Throwable)var5);
         }

         this.cachedExceptions = var3;
      }

      return this.cachedExceptions;
   }

   public String[] contexts() {
      this.checkAccess(4);
      if (this.cachedContexts == null) {
         if (this.request == null) {
            throw this.stdWrapper.piOperationNotSupported3();
         }

         ContextList var1 = this.request.contexts();
         int var2 = var1.count();
         String[] var3 = new String[var2];

         try {
            for(int var4 = 0; var4 < var2; ++var4) {
               var3[var4] = var1.item(var4);
            }
         } catch (Exception var5) {
            throw this.wrapper.exceptionInContexts((Throwable)var5);
         }

         this.cachedContexts = var3;
      }

      return this.cachedContexts;
   }

   public String[] operation_context() {
      this.checkAccess(5);
      if (this.cachedOperationContext == null) {
         if (this.request == null) {
            throw this.stdWrapper.piOperationNotSupported4();
         }

         Context var1 = this.request.ctx();
         NVList var2 = var1.get_values("", 15, "*");
         String[] var3 = new String[var2.count() * 2];
         if (var2 != null && var2.count() != 0) {
            int var4 = 0;

            for(int var5 = 0; var5 < var2.count(); ++var5) {
               NamedValue var6;
               try {
                  var6 = var2.item(var5);
               } catch (Exception var8) {
                  return (String[])null;
               }

               var3[var4] = var6.name();
               ++var4;
               var3[var4] = var6.value().extract_string();
               ++var4;
            }
         }

         this.cachedOperationContext = var3;
      }

      return this.cachedOperationContext;
   }

   public Any result() {
      this.checkAccess(6);
      if (this.cachedResult == null) {
         if (this.request == null) {
            throw this.stdWrapper.piOperationNotSupported5();
         }

         NamedValue var1 = this.request.result();
         if (var1 == null) {
            throw this.wrapper.piDiiResultIsNull();
         }

         this.cachedResult = var1.value();
      }

      return this.cachedResult;
   }

   public boolean response_expected() {
      return !this.messageMediator.isOneWay();
   }

   public Object forward_reference() {
      this.checkAccess(10);
      if (this.replyStatus != 3) {
         throw this.stdWrapper.invalidPiCall1();
      } else {
         IOR var1 = this.getLocatedIOR();
         return this.iorToObject(var1);
      }
   }

   private IOR getLocatedIOR() {
      CorbaContactInfoList var2 = (CorbaContactInfoList)this.messageMediator.getContactInfo().getContactInfoList();
      IOR var1 = var2.getEffectiveTargetIOR();
      return var1;
   }

   protected void setLocatedIOR(IOR var1) {
      ORB var2 = (ORB)this.messageMediator.getBroker();
      CorbaContactInfoListIterator var3 = (CorbaContactInfoListIterator)((CorbaInvocationInfo)var2.getInvocationInfo()).getContactInfoListIterator();
      var3.reportRedirect((CorbaContactInfo)this.messageMediator.getContactInfo(), var1);
   }

   public ServiceContext get_request_service_context(int var1) {
      this.checkAccess(12);
      if (this.cachedRequestServiceContexts == null) {
         this.cachedRequestServiceContexts = new HashMap();
      }

      return this.getServiceContext(this.cachedRequestServiceContexts, this.messageMediator.getRequestServiceContexts(), var1);
   }

   public ServiceContext get_reply_service_context(int var1) {
      this.checkAccess(13);
      if (this.cachedReplyServiceContexts == null) {
         this.cachedReplyServiceContexts = new HashMap();
      }

      try {
         ServiceContexts var2 = this.messageMediator.getReplyServiceContexts();
         if (var2 == null) {
            throw new NullPointerException();
         } else {
            return this.getServiceContext(this.cachedReplyServiceContexts, var2, var1);
         }
      } catch (NullPointerException var3) {
         throw this.stdWrapper.invalidServiceContextId((Throwable)var3);
      }
   }

   public Connection connection() {
      return (Connection)this.messageMediator.getConnection();
   }

   protected void setInfo(MessageMediator var1) {
      this.messageMediator = (CorbaMessageMediator)var1;
      this.messageMediator.setDIIInfo(this.request);
   }

   void setRetryRequest(RetryType var1) {
      this.retryRequest = var1;
   }

   RetryType getRetryRequest() {
      return this.retryRequest;
   }

   void incrementEntryCount() {
      ++this.entryCount;
   }

   void decrementEntryCount() {
      --this.entryCount;
   }

   int getEntryCount() {
      return this.entryCount;
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

   protected void setDIIRequest(Request var1) {
      this.request = var1;
   }

   protected void setDIIInitiate(boolean var1) {
      this.diiInitiate = var1;
   }

   protected boolean isDIIInitiate() {
      return this.diiInitiate;
   }

   protected void setPICurrentPushed(boolean var1) {
      this.piCurrentPushed = var1;
   }

   protected boolean isPICurrentPushed() {
      return this.piCurrentPushed;
   }

   protected void setException(Exception var1) {
      super.setException(var1);
      this.cachedReceivedException = null;
      this.cachedReceivedExceptionId = null;
   }

   protected boolean getIsOneWay() {
      return !this.response_expected();
   }

   protected void checkAccess(int var1) throws BAD_INV_ORDER {
      byte var2;
      var2 = 0;
      label24:
      switch(this.currentExecutionPoint) {
      case 0:
         switch(this.startingPointCall) {
         case 0:
            var2 = 0;
            break label24;
         case 1:
            var2 = 1;
         default:
            break label24;
         }
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
}
