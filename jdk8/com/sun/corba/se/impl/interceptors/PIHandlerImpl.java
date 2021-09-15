package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RetryType;
import java.util.HashMap;
import java.util.Stack;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public class PIHandlerImpl implements PIHandler {
   boolean printPushPopEnabled = false;
   int pushLevel = 0;
   private ORB orb;
   InterceptorsSystemException wrapper;
   ORBUtilSystemException orbutilWrapper;
   OMGSystemException omgWrapper;
   private int serverRequestIdCounter = 0;
   CodecFactory codecFactory = null;
   String[] arguments = null;
   private InterceptorList interceptorList;
   private boolean hasIORInterceptors;
   private boolean hasClientInterceptors;
   private boolean hasServerInterceptors;
   private InterceptorInvoker interceptorInvoker;
   private PICurrent current;
   private HashMap policyFactoryTable;
   private static final short[] REPLY_MESSAGE_TO_PI_REPLY_STATUS = new short[]{0, 2, 1, 3, 3, 4};
   private ThreadLocal threadLocalClientRequestInfoStack = new ThreadLocal() {
      protected Object initialValue() {
         return PIHandlerImpl.this.new RequestInfoStack();
      }
   };
   private ThreadLocal threadLocalServerRequestInfoStack = new ThreadLocal() {
      protected Object initialValue() {
         return PIHandlerImpl.this.new RequestInfoStack();
      }
   };

   private void printPush() {
      if (this.printPushPopEnabled) {
         this.printSpaces(this.pushLevel);
         ++this.pushLevel;
         System.out.println("PUSH");
      }
   }

   private void printPop() {
      if (this.printPushPopEnabled) {
         --this.pushLevel;
         this.printSpaces(this.pushLevel);
         System.out.println("POP");
      }
   }

   private void printSpaces(int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         System.out.print(" ");
      }

   }

   public void close() {
      this.orb = null;
      this.wrapper = null;
      this.orbutilWrapper = null;
      this.omgWrapper = null;
      this.codecFactory = null;
      this.arguments = null;
      this.interceptorList = null;
      this.interceptorInvoker = null;
      this.current = null;
      this.policyFactoryTable = null;
      this.threadLocalClientRequestInfoStack = null;
      this.threadLocalServerRequestInfoStack = null;
   }

   public PIHandlerImpl(ORB var1, String[] var2) {
      this.orb = var1;
      this.wrapper = InterceptorsSystemException.get(var1, "rpc.protocol");
      this.orbutilWrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.omgWrapper = OMGSystemException.get(var1, "rpc.protocol");
      this.arguments = var2;
      this.codecFactory = new CodecFactoryImpl(var1);
      this.interceptorList = new InterceptorList(this.wrapper);
      this.current = new PICurrent(var1);
      this.interceptorInvoker = new InterceptorInvoker(var1, this.interceptorList, this.current);
      var1.getLocalResolver().register("PICurrent", ClosureFactory.makeConstant(this.current));
      var1.getLocalResolver().register("CodecFactory", ClosureFactory.makeConstant(this.codecFactory));
   }

   public void initialize() {
      if (this.orb.getORBData().getORBInitializers() != null) {
         ORBInitInfoImpl var1 = this.createORBInitInfo();
         this.current.setORBInitializing(true);
         this.preInitORBInitializers(var1);
         this.postInitORBInitializers(var1);
         this.interceptorList.sortInterceptors();
         this.current.setORBInitializing(false);
         var1.setStage(2);
         this.hasIORInterceptors = this.interceptorList.hasInterceptorsOfType(2);
         this.hasClientInterceptors = true;
         this.hasServerInterceptors = this.interceptorList.hasInterceptorsOfType(1);
         this.interceptorInvoker.setEnabled(true);
      }

   }

   public void destroyInterceptors() {
      this.interceptorList.destroyAll();
   }

   public void objectAdapterCreated(ObjectAdapter var1) {
      if (this.hasIORInterceptors) {
         this.interceptorInvoker.objectAdapterCreated(var1);
      }
   }

   public void adapterManagerStateChanged(int var1, short var2) {
      if (this.hasIORInterceptors) {
         this.interceptorInvoker.adapterManagerStateChanged(var1, var2);
      }
   }

   public void adapterStateChanged(ObjectReferenceTemplate[] var1, short var2) {
      if (this.hasIORInterceptors) {
         this.interceptorInvoker.adapterStateChanged(var1, var2);
      }
   }

   public void disableInterceptorsThisThread() {
      if (this.hasClientInterceptors) {
         PIHandlerImpl.RequestInfoStack var1 = (PIHandlerImpl.RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
         ++var1.disableCount;
      }
   }

   public void enableInterceptorsThisThread() {
      if (this.hasClientInterceptors) {
         PIHandlerImpl.RequestInfoStack var1 = (PIHandlerImpl.RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
         --var1.disableCount;
      }
   }

   public void invokeClientPIStartingPoint() throws RemarshalException {
      if (this.hasClientInterceptors) {
         if (this.isClientPIEnabledForThisThread()) {
            ClientRequestInfoImpl var1 = this.peekClientRequestInfoImplStack();
            this.interceptorInvoker.invokeClientInterceptorStartingPoint(var1);
            short var2 = var1.getReplyStatus();
            if (var2 != 1 && var2 != 3) {
               if (var2 != -1) {
                  throw this.wrapper.replyStatusNotInit();
               }
            } else {
               Exception var3 = this.invokeClientPIEndingPoint(this.convertPIReplyStatusToReplyMessage(var2), var1.getException());
               if (var3 == null) {
               }

               if (var3 instanceof SystemException) {
                  throw (SystemException)var3;
               }

               if (var3 instanceof RemarshalException) {
                  throw (RemarshalException)var3;
               }

               if (var3 instanceof UserException || var3 instanceof ApplicationException) {
                  throw this.wrapper.exceptionInvalid();
               }
            }

         }
      }
   }

   public Exception makeCompletedClientRequest(int var1, Exception var2) {
      return this.handleClientPIEndingPoint(var1, var2, false);
   }

   public Exception invokeClientPIEndingPoint(int var1, Exception var2) {
      return this.handleClientPIEndingPoint(var1, var2, true);
   }

   public Exception handleClientPIEndingPoint(int var1, Exception var2, boolean var3) {
      if (!this.hasClientInterceptors) {
         return (Exception)var2;
      } else if (!this.isClientPIEnabledForThisThread()) {
         return (Exception)var2;
      } else {
         short var4 = REPLY_MESSAGE_TO_PI_REPLY_STATUS[var1];
         ClientRequestInfoImpl var5 = this.peekClientRequestInfoImplStack();
         var5.setReplyStatus(var4);
         var5.setException((Exception)var2);
         if (var3) {
            this.interceptorInvoker.invokeClientInterceptorEndingPoint(var5);
            var4 = var5.getReplyStatus();
         }

         if (var4 != 3 && var4 != 4) {
            if (var4 == 1 || var4 == 2) {
               var2 = var5.getException();
            }
         } else {
            var5.reset();
            if (var3) {
               var5.setRetryRequest(RetryType.AFTER_RESPONSE);
            } else {
               var5.setRetryRequest(RetryType.BEFORE_RESPONSE);
            }

            var2 = new RemarshalException();
         }

         return (Exception)var2;
      }
   }

   public void initiateClientPIRequest(boolean var1) {
      if (this.hasClientInterceptors) {
         if (this.isClientPIEnabledForThisThread()) {
            PIHandlerImpl.RequestInfoStack var2 = (PIHandlerImpl.RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
            ClientRequestInfoImpl var3 = null;
            if (!var2.empty()) {
               var3 = (ClientRequestInfoImpl)var2.peek();
            }

            if (!var1 && var3 != null && var3.isDIIInitiate()) {
               var3.setDIIInitiate(false);
            } else {
               if (var3 == null || !var3.getRetryRequest().isRetry()) {
                  var3 = new ClientRequestInfoImpl(this.orb);
                  var2.push(var3);
                  this.printPush();
               }

               var3.setRetryRequest(RetryType.NONE);
               var3.incrementEntryCount();
               var3.setReplyStatus((short)-1);
               if (var1) {
                  var3.setDIIInitiate(true);
               }
            }

         }
      }
   }

   public void cleanupClientPIRequest() {
      if (this.hasClientInterceptors) {
         if (this.isClientPIEnabledForThisThread()) {
            ClientRequestInfoImpl var1 = this.peekClientRequestInfoImplStack();
            RetryType var2 = var1.getRetryRequest();
            if (!var2.equals(RetryType.BEFORE_RESPONSE)) {
               short var3 = var1.getReplyStatus();
               if (var3 == -1) {
                  this.invokeClientPIEndingPoint(2, this.wrapper.unknownRequestInvoke(CompletionStatus.COMPLETED_MAYBE));
               }
            }

            var1.decrementEntryCount();
            if (var1.getEntryCount() == 0 && !var1.getRetryRequest().isRetry()) {
               PIHandlerImpl.RequestInfoStack var4 = (PIHandlerImpl.RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
               var4.pop();
               this.printPop();
            }

         }
      }
   }

   public void setClientPIInfo(CorbaMessageMediator var1) {
      if (this.hasClientInterceptors) {
         if (this.isClientPIEnabledForThisThread()) {
            this.peekClientRequestInfoImplStack().setInfo(var1);
         }
      }
   }

   public void setClientPIInfo(RequestImpl var1) {
      if (this.hasClientInterceptors) {
         if (this.isClientPIEnabledForThisThread()) {
            this.peekClientRequestInfoImplStack().setDIIRequest(var1);
         }
      }
   }

   public void invokeServerPIStartingPoint() {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var1 = this.peekServerRequestInfoImplStack();
         this.interceptorInvoker.invokeServerInterceptorStartingPoint(var1);
         this.serverPIHandleExceptions(var1);
      }
   }

   public void invokeServerPIIntermediatePoint() {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var1 = this.peekServerRequestInfoImplStack();
         this.interceptorInvoker.invokeServerInterceptorIntermediatePoint(var1);
         var1.releaseServant();
         this.serverPIHandleExceptions(var1);
      }
   }

   public void invokeServerPIEndingPoint(ReplyMessage var1) {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var2 = this.peekServerRequestInfoImplStack();
         var2.setReplyMessage(var1);
         var2.setCurrentExecutionPoint(2);
         if (!var2.getAlreadyExecuted()) {
            int var3 = var1.getReplyStatus();
            short var4 = REPLY_MESSAGE_TO_PI_REPLY_STATUS[var3];
            if (var4 == 3 || var4 == 4) {
               var2.setForwardRequest(var1.getIOR());
            }

            Exception var5 = var2.getException();
            if (!var2.isDynamic() && var4 == 2) {
               var2.setException(this.omgWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE));
            }

            var2.setReplyStatus(var4);
            this.interceptorInvoker.invokeServerInterceptorEndingPoint(var2);
            short var6 = var2.getReplyStatus();
            Exception var7 = var2.getException();
            if (var6 == 1 && var7 != var5) {
               throw (SystemException)var7;
            }

            if (var6 == 3) {
               if (var4 != 3) {
                  IOR var8 = var2.getForwardRequestIOR();
                  throw new ForwardException(this.orb, var8);
               }

               if (var2.isForwardRequestRaisedInEnding()) {
                  var1.setIOR(var2.getForwardRequestIOR());
               }
            }
         }

      }
   }

   public void setServerPIInfo(Exception var1) {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var2 = this.peekServerRequestInfoImplStack();
         var2.setException(var1);
      }
   }

   public void setServerPIInfo(NVList var1) {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var2 = this.peekServerRequestInfoImplStack();
         var2.setDSIArguments(var1);
      }
   }

   public void setServerPIExceptionInfo(Any var1) {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var2 = this.peekServerRequestInfoImplStack();
         var2.setDSIException(var1);
      }
   }

   public void setServerPIInfo(Any var1) {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var2 = this.peekServerRequestInfoImplStack();
         var2.setDSIResult(var1);
      }
   }

   public void initializeServerPIInfo(CorbaMessageMediator var1, ObjectAdapter var2, byte[] var3, ObjectKeyTemplate var4) {
      if (this.hasServerInterceptors) {
         PIHandlerImpl.RequestInfoStack var5 = (PIHandlerImpl.RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
         ServerRequestInfoImpl var6 = new ServerRequestInfoImpl(this.orb);
         var5.push(var6);
         this.printPush();
         var1.setExecutePIInResponseConstructor(true);
         var6.setInfo(var1, var2, var3, var4);
      }
   }

   public void setServerPIInfo(Object var1, String var2) {
      if (this.hasServerInterceptors) {
         ServerRequestInfoImpl var3 = this.peekServerRequestInfoImplStack();
         var3.setInfo(var1, var2);
      }
   }

   public void cleanupServerPIRequest() {
      if (this.hasServerInterceptors) {
         PIHandlerImpl.RequestInfoStack var1 = (PIHandlerImpl.RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
         var1.pop();
         this.printPop();
      }
   }

   private void serverPIHandleExceptions(ServerRequestInfoImpl var1) {
      int var2 = var1.getEndingPointCall();
      if (var2 == 1) {
         throw (SystemException)var1.getException();
      } else if (var2 == 2 && var1.getForwardRequestException() != null) {
         IOR var3 = var1.getForwardRequestIOR();
         throw new ForwardException(this.orb, var3);
      }
   }

   private int convertPIReplyStatusToReplyMessage(short var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; ++var3) {
         if (REPLY_MESSAGE_TO_PI_REPLY_STATUS[var3] == var1) {
            var2 = var3;
            break;
         }
      }

      return var2;
   }

   private ClientRequestInfoImpl peekClientRequestInfoImplStack() {
      PIHandlerImpl.RequestInfoStack var1 = (PIHandlerImpl.RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
      ClientRequestInfoImpl var2 = null;
      if (!var1.empty()) {
         var2 = (ClientRequestInfoImpl)var1.peek();
         return var2;
      } else {
         throw this.wrapper.clientInfoStackNull();
      }
   }

   private ServerRequestInfoImpl peekServerRequestInfoImplStack() {
      PIHandlerImpl.RequestInfoStack var1 = (PIHandlerImpl.RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
      ServerRequestInfoImpl var2 = null;
      if (!var1.empty()) {
         var2 = (ServerRequestInfoImpl)var1.peek();
         return var2;
      } else {
         throw this.wrapper.serverInfoStackNull();
      }
   }

   private boolean isClientPIEnabledForThisThread() {
      PIHandlerImpl.RequestInfoStack var1 = (PIHandlerImpl.RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
      return var1.disableCount == 0;
   }

   private void preInitORBInitializers(ORBInitInfoImpl var1) {
      var1.setStage(0);

      for(int var2 = 0; var2 < this.orb.getORBData().getORBInitializers().length; ++var2) {
         ORBInitializer var3 = this.orb.getORBData().getORBInitializers()[var2];
         if (var3 != null) {
            try {
               var3.pre_init(var1);
            } catch (Exception var5) {
            }
         }
      }

   }

   private void postInitORBInitializers(ORBInitInfoImpl var1) {
      var1.setStage(1);

      for(int var2 = 0; var2 < this.orb.getORBData().getORBInitializers().length; ++var2) {
         ORBInitializer var3 = this.orb.getORBData().getORBInitializers()[var2];
         if (var3 != null) {
            try {
               var3.post_init(var1);
            } catch (Exception var5) {
            }
         }
      }

   }

   private ORBInitInfoImpl createORBInitInfo() {
      ORBInitInfoImpl var1 = null;
      String var2 = this.orb.getORBData().getORBId();
      var1 = new ORBInitInfoImpl(this.orb, this.arguments, var2, this.codecFactory);
      return var1;
   }

   public void register_interceptor(Interceptor var1, int var2) throws DuplicateName {
      if (var2 < 3 && var2 >= 0) {
         String var3 = var1.name();
         if (var3 == null) {
            throw this.wrapper.nameNull();
         } else {
            this.interceptorList.register_interceptor(var1, var2);
         }
      } else {
         throw this.wrapper.typeOutOfRange(new Integer(var2));
      }
   }

   public Current getPICurrent() {
      return this.current;
   }

   private void nullParam() throws BAD_PARAM {
      throw this.orbutilWrapper.nullParam();
   }

   public Policy create_policy(int var1, Any var2) throws PolicyError {
      if (var2 == null) {
         this.nullParam();
      }

      if (this.policyFactoryTable == null) {
         throw new PolicyError("There is no PolicyFactory Registered for type " + var1, (short)0);
      } else {
         PolicyFactory var3 = (PolicyFactory)this.policyFactoryTable.get(new Integer(var1));
         if (var3 == null) {
            throw new PolicyError(" Could Not Find PolicyFactory for the Type " + var1, (short)0);
         } else {
            Policy var4 = var3.create_policy(var1, var2);
            return var4;
         }
      }
   }

   public void registerPolicyFactory(int var1, PolicyFactory var2) {
      if (this.policyFactoryTable == null) {
         this.policyFactoryTable = new HashMap();
      }

      Integer var3 = new Integer(var1);
      Object var4 = this.policyFactoryTable.get(var3);
      if (var4 == null) {
         this.policyFactoryTable.put(var3, var2);
      } else {
         throw this.omgWrapper.policyFactoryRegFailed(new Integer(var1));
      }
   }

   public synchronized int allocateServerRequestId() {
      return this.serverRequestIdCounter++;
   }

   private final class RequestInfoStack extends Stack {
      public int disableCount;

      private RequestInfoStack() {
         this.disableCount = 0;
      }

      // $FF: synthetic method
      RequestInfoStack(Object var2) {
         this();
      }
   }
}
