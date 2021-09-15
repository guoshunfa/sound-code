package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.AddressingDispositionHelper;
import com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_2;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.EmptyStackException;
import java.util.Iterator;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.OutputStreamFactory;

public class CorbaMessageMediatorImpl implements CorbaMessageMediator, CorbaProtocolHandler, MessageHandler {
   protected ORB orb;
   protected ORBUtilSystemException wrapper;
   protected InterceptorsSystemException interceptorWrapper;
   protected CorbaContactInfo contactInfo;
   protected CorbaConnection connection;
   protected short addrDisposition;
   protected CDROutputObject outputObject;
   protected CDRInputObject inputObject;
   protected Message messageHeader;
   protected RequestMessage requestHeader;
   protected LocateReplyOrReplyMessage replyHeader;
   protected String replyExceptionDetailMessage;
   protected IOR replyIOR;
   protected Integer requestIdInteger;
   protected Message dispatchHeader;
   protected ByteBuffer dispatchByteBuffer;
   protected byte streamFormatVersion;
   protected boolean streamFormatVersionSet;
   protected Request diiRequest;
   protected boolean cancelRequestAlreadySent;
   protected ProtocolHandler protocolHandler;
   protected boolean _executeReturnServantInResponseConstructor;
   protected boolean _executeRemoveThreadInfoInResponseConstructor;
   protected boolean _executePIInResponseConstructor;
   protected boolean isThreadDone;

   public CorbaMessageMediatorImpl(ORB var1, ContactInfo var2, Connection var3, GIOPVersion var4, IOR var5, int var6, short var7, String var8, boolean var9) {
      this(var1, var3);
      this.contactInfo = (CorbaContactInfo)var2;
      this.addrDisposition = var7;
      this.streamFormatVersion = this.getStreamFormatVersionForThisRequest(this.contactInfo.getEffectiveTargetIOR(), var4);
      this.streamFormatVersionSet = true;
      this.requestHeader = MessageBase.createRequest(this.orb, var4, ORBUtility.getEncodingVersion(var1, var5), var6, !var9, this.contactInfo.getEffectiveTargetIOR(), this.addrDisposition, var8, new ServiceContexts(var1), (Principal)null);
   }

   public CorbaMessageMediatorImpl(ORB var1, Connection var2) {
      this.streamFormatVersionSet = false;
      this.cancelRequestAlreadySent = false;
      this._executeReturnServantInResponseConstructor = false;
      this._executeRemoveThreadInfoInResponseConstructor = false;
      this._executePIInResponseConstructor = false;
      this.isThreadDone = false;
      this.orb = var1;
      this.connection = (CorbaConnection)var2;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.interceptorWrapper = InterceptorsSystemException.get(var1, "rpc.protocol");
   }

   public CorbaMessageMediatorImpl(ORB var1, CorbaConnection var2, Message var3, ByteBuffer var4) {
      this(var1, var2);
      this.dispatchHeader = var3;
      this.dispatchByteBuffer = var4;
   }

   public Broker getBroker() {
      return this.orb;
   }

   public ContactInfo getContactInfo() {
      return this.contactInfo;
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void initializeMessage() {
      this.getRequestHeader().write(this.outputObject);
   }

   public void finishSendingRequest() {
      this.outputObject.finishSendingMessage();
   }

   public InputObject waitForResponse() {
      return this.getRequestHeader().isResponseExpected() ? this.connection.waitForResponse(this) : null;
   }

   public void setOutputObject(OutputObject var1) {
      this.outputObject = (CDROutputObject)var1;
   }

   public OutputObject getOutputObject() {
      return this.outputObject;
   }

   public void setInputObject(InputObject var1) {
      this.inputObject = (CDRInputObject)var1;
   }

   public InputObject getInputObject() {
      return this.inputObject;
   }

   public void setReplyHeader(LocateReplyOrReplyMessage var1) {
      this.replyHeader = var1;
      this.replyIOR = var1.getIOR();
   }

   public LocateReplyMessage getLocateReplyHeader() {
      return (LocateReplyMessage)this.replyHeader;
   }

   public ReplyMessage getReplyHeader() {
      return (ReplyMessage)this.replyHeader;
   }

   public void setReplyExceptionDetailMessage(String var1) {
      this.replyExceptionDetailMessage = var1;
   }

   public RequestMessage getRequestHeader() {
      return this.requestHeader;
   }

   public GIOPVersion getGIOPVersion() {
      return this.messageHeader != null ? this.messageHeader.getGIOPVersion() : this.getRequestHeader().getGIOPVersion();
   }

   public byte getEncodingVersion() {
      return this.messageHeader != null ? this.messageHeader.getEncodingVersion() : this.getRequestHeader().getEncodingVersion();
   }

   public int getRequestId() {
      return this.getRequestHeader().getRequestId();
   }

   public Integer getRequestIdInteger() {
      if (this.requestIdInteger == null) {
         this.requestIdInteger = new Integer(this.getRequestHeader().getRequestId());
      }

      return this.requestIdInteger;
   }

   public boolean isOneWay() {
      return !this.getRequestHeader().isResponseExpected();
   }

   public short getAddrDisposition() {
      return this.addrDisposition;
   }

   public String getOperationName() {
      return this.getRequestHeader().getOperation();
   }

   public ServiceContexts getRequestServiceContexts() {
      return this.getRequestHeader().getServiceContexts();
   }

   public ServiceContexts getReplyServiceContexts() {
      return this.getReplyHeader().getServiceContexts();
   }

   public void sendCancelRequestIfFinalFragmentNotSent() {
      if (!this.sentFullMessage() && this.sentFragment() && !this.cancelRequestAlreadySent) {
         try {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".sendCancelRequestIfFinalFragmentNotSent->: " + this.opAndId(this));
            }

            this.connection.sendCancelRequestWithLock(this.getGIOPVersion(), this.getRequestId());
            this.cancelRequestAlreadySent = true;
         } catch (IOException var5) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".sendCancelRequestIfFinalFragmentNotSent: !ERROR : " + this.opAndId(this), var5);
            }

            throw this.interceptorWrapper.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_MAYBE, var5);
         } finally {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".sendCancelRequestIfFinalFragmentNotSent<-: " + this.opAndId(this));
            }

         }
      }

   }

   public boolean sentFullMessage() {
      return this.outputObject.getBufferManager().sentFullMessage();
   }

   public boolean sentFragment() {
      return this.outputObject.getBufferManager().sentFragment();
   }

   public void setDIIInfo(Request var1) {
      this.diiRequest = var1;
   }

   public boolean isDIIRequest() {
      return this.diiRequest != null;
   }

   public Exception unmarshalDIIUserException(String var1, InputStream var2) {
      if (!this.isDIIRequest()) {
         return null;
      } else {
         ExceptionList var3 = this.diiRequest.exceptions();

         try {
            for(int var4 = 0; var4 < var3.count(); ++var4) {
               TypeCode var5 = var3.item(var4);
               if (var5.id().equals(var1)) {
                  Any var6 = this.orb.create_any();
                  var6.read_value(var2, var5);
                  return new UnknownUserException(var6);
               }
            }
         } catch (Exception var7) {
            throw this.wrapper.unexpectedDiiException((Throwable)var7);
         }

         return this.wrapper.unknownCorbaExc(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public void setDIIException(Exception var1) {
      this.diiRequest.env().exception(var1);
   }

   public void handleDIIReply(InputStream var1) {
      if (this.isDIIRequest()) {
         ((RequestImpl)this.diiRequest).unmarshalReply(var1);
      }
   }

   public Message getDispatchHeader() {
      return this.dispatchHeader;
   }

   public void setDispatchHeader(Message var1) {
      this.dispatchHeader = var1;
   }

   public ByteBuffer getDispatchBuffer() {
      return this.dispatchByteBuffer;
   }

   public void setDispatchBuffer(ByteBuffer var1) {
      this.dispatchByteBuffer = var1;
   }

   public int getThreadPoolToUse() {
      int var1 = 0;
      Message var2 = this.getDispatchHeader();
      if (var2 != null) {
         var1 = var2.getThreadPoolToUse();
      }

      return var1;
   }

   public byte getStreamFormatVersion() {
      return this.streamFormatVersionSet ? this.streamFormatVersion : this.getStreamFormatVersionForReply();
   }

   public byte getStreamFormatVersionForReply() {
      ServiceContexts var1 = this.getRequestServiceContexts();
      MaxStreamFormatVersionServiceContext var2 = (MaxStreamFormatVersionServiceContext)var1.get(17);
      if (var2 != null) {
         byte var3 = ORBUtility.getMaxStreamFormatVersion();
         byte var4 = var2.getMaximumStreamFormatVersion();
         return (byte)Math.min(var3, var4);
      } else {
         return (byte)(this.getGIOPVersion().lessThan(GIOPVersion.V1_3) ? 1 : 2);
      }
   }

   public boolean isSystemExceptionReply() {
      return this.replyHeader.getReplyStatus() == 2;
   }

   public boolean isUserExceptionReply() {
      return this.replyHeader.getReplyStatus() == 1;
   }

   public boolean isLocationForwardReply() {
      return this.replyHeader.getReplyStatus() == 3 || this.replyHeader.getReplyStatus() == 4;
   }

   public boolean isDifferentAddrDispositionRequestedReply() {
      return this.replyHeader.getReplyStatus() == 5;
   }

   public short getAddrDispositionReply() {
      return this.replyHeader.getAddrDisposition();
   }

   public IOR getForwardedIOR() {
      return this.replyHeader.getIOR();
   }

   public SystemException getSystemExceptionReply() {
      return this.replyHeader.getSystemException(this.replyExceptionDetailMessage);
   }

   public ObjectKey getObjectKey() {
      return this.getRequestHeader().getObjectKey();
   }

   public void setProtocolHandler(CorbaProtocolHandler var1) {
      throw this.wrapper.methodShouldNotBeCalled();
   }

   public CorbaProtocolHandler getProtocolHandler() {
      return this;
   }

   public OutputStream createReply() {
      this.getProtocolHandler().createResponse(this, (ServiceContexts)null);
      return (org.omg.CORBA_2_3.portable.OutputStream)this.getOutputObject();
   }

   public OutputStream createExceptionReply() {
      this.getProtocolHandler().createUserExceptionResponse(this, (ServiceContexts)null);
      return (org.omg.CORBA_2_3.portable.OutputStream)this.getOutputObject();
   }

   public boolean executeReturnServantInResponseConstructor() {
      return this._executeReturnServantInResponseConstructor;
   }

   public void setExecuteReturnServantInResponseConstructor(boolean var1) {
      this._executeReturnServantInResponseConstructor = var1;
   }

   public boolean executeRemoveThreadInfoInResponseConstructor() {
      return this._executeRemoveThreadInfoInResponseConstructor;
   }

   public void setExecuteRemoveThreadInfoInResponseConstructor(boolean var1) {
      this._executeRemoveThreadInfoInResponseConstructor = var1;
   }

   public boolean executePIInResponseConstructor() {
      return this._executePIInResponseConstructor;
   }

   public void setExecutePIInResponseConstructor(boolean var1) {
      this._executePIInResponseConstructor = var1;
   }

   private byte getStreamFormatVersionForThisRequest(IOR var1, GIOPVersion var2) {
      byte var3 = ORBUtility.getMaxStreamFormatVersion();
      IOR var4 = this.contactInfo.getEffectiveTargetIOR();
      IIOPProfileTemplate var5 = (IIOPProfileTemplate)var4.getProfile().getTaggedProfileTemplate();
      Iterator var6 = var5.iteratorById(38);
      if (!var6.hasNext()) {
         return (byte)(var2.lessThan(GIOPVersion.V1_3) ? 1 : 2);
      } else {
         byte var7 = ((MaxStreamFormatVersionComponent)var6.next()).getMaxStreamFormatVersion();
         return (byte)Math.min(var3, var7);
      }
   }

   public boolean handleRequest(MessageMediator var1) {
      try {
         this.dispatchHeader.callback(this);
      } catch (IOException var3) {
      }

      return this.isThreadDone;
   }

   private void setWorkThenPoolOrResumeSelect(Message var1) {
      if (this.getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
         this.resumeSelect(var1);
      } else {
         this.isThreadDone = true;
         this.orb.getTransportManager().getSelector(0).unregisterForEvent(this.getConnection().getEventHandler());
         this.orb.getTransportManager().getSelector(0).registerForEvent(this.getConnection().getEventHandler());
      }

   }

   private void setWorkThenReadOrResumeSelect(Message var1) {
      if (this.getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
         this.resumeSelect(var1);
      } else {
         this.isThreadDone = false;
      }

   }

   private void resumeSelect(Message var1) {
      if (this.transportDebug()) {
         this.dprint(".resumeSelect:->");
         String var2 = "?";
         if (var1 instanceof RequestMessage) {
            var2 = (new Integer(((RequestMessage)var1).getRequestId())).toString();
         } else if (var1 instanceof ReplyMessage) {
            var2 = (new Integer(((ReplyMessage)var1).getRequestId())).toString();
         } else if (var1 instanceof FragmentMessage_1_2) {
            var2 = (new Integer(((FragmentMessage_1_2)var1).getRequestId())).toString();
         }

         this.dprint(".resumeSelect: id/" + var2 + " " + this.getConnection());
      }

      EventHandler var3 = this.getConnection().getEventHandler();
      this.orb.getTransportManager().getSelector(0).registerInterestOps(var3);
      if (this.transportDebug()) {
         this.dprint(".resumeSelect:<-");
      }

   }

   private void setInputObject() {
      if (this.getConnection().getContactInfo() != null) {
         this.inputObject = (CDRInputObject)this.getConnection().getContactInfo().createInputObject(this.orb, this);
      } else {
         if (this.getConnection().getAcceptor() == null) {
            throw new RuntimeException("CorbaMessageMediatorImpl.setInputObject");
         }

         this.inputObject = (CDRInputObject)this.getConnection().getAcceptor().createInputObject(this.orb, this);
      }

      this.inputObject.setMessageMediator(this);
      this.setInputObject(this.inputObject);
   }

   private void signalResponseReceived() {
      this.connection.getResponseWaitingRoom().responseReceived(this.inputObject);
   }

   public void handleInput(Message var1) throws IOException {
      try {
         this.messageHeader = var1;
         if (this.transportDebug()) {
            this.dprint(".handleInput->: " + MessageBase.typeToString(var1.getType()));
         }

         this.setWorkThenReadOrResumeSelect(var1);
         switch(var1.getType()) {
         case 5:
            if (this.transportDebug()) {
               this.dprint(".handleInput: CloseConnection: purging");
            }

            this.connection.purgeCalls(this.wrapper.connectionRebind(), true, false);
            break;
         case 6:
            if (this.transportDebug()) {
               this.dprint(".handleInput: MessageError: purging");
            }

            this.connection.purgeCalls(this.wrapper.recvMsgError(), true, false);
            break;
         default:
            if (this.transportDebug()) {
               this.dprint(".handleInput: ERROR: " + MessageBase.typeToString(var1.getType()));
            }

            throw this.wrapper.badGiopRequestType();
         }

         this.releaseByteBufferToPool();
      } finally {
         if (this.transportDebug()) {
            this.dprint(".handleInput<-: " + MessageBase.typeToString(var1.getType()));
         }

      }

   }

   public void handleInput(RequestMessage_1_0 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.0->: " + var1);
         }

         try {
            this.messageHeader = this.requestHeader = var1;
            this.setInputObject();
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }

         this.getProtocolHandler().handleRequest((RequestMessage)var1, this);
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.0: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.0<-: " + var1);
         }

      }

   }

   public void handleInput(RequestMessage_1_1 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.1->: " + var1);
         }

         try {
            this.messageHeader = this.requestHeader = var1;
            this.setInputObject();
            this.connection.serverRequest_1_1_Put(this);
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }

         this.getProtocolHandler().handleRequest((RequestMessage)var1, this);
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.1: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.1<-: " + var1);
         }

      }

   }

   public void handleInput(RequestMessage_1_2 var1) throws IOException {
      try {
         try {
            this.messageHeader = this.requestHeader = var1;
            var1.unmarshalRequestID(this.dispatchByteBuffer);
            this.setInputObject();
            if (this.transportDebug()) {
               this.dprint(".REQUEST 1.2->: id/" + var1.getRequestId() + ": " + var1);
            }

            this.connection.serverRequestMapPut(var1.getRequestId(), this);
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }

         this.getProtocolHandler().handleRequest((RequestMessage)var1, this);
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.2: id/" + var1.getRequestId() + ": !!ERROR!!: " + var1, var11);
         }
      } finally {
         this.connection.serverRequestMapRemove(var1.getRequestId());
         if (this.transportDebug()) {
            this.dprint(".REQUEST 1.2<-: id/" + var1.getRequestId() + ": " + var1);
         }

      }

   }

   public void handleInput(ReplyMessage_1_0 var1) throws IOException {
      try {
         try {
            if (this.transportDebug()) {
               this.dprint(".REPLY 1.0->: " + var1);
            }

            this.messageHeader = this.replyHeader = var1;
            this.setInputObject();
            this.inputObject.unmarshalHeader();
            this.signalResponseReceived();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.0: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.0<-: " + var1);
         }

      }

   }

   public void handleInput(ReplyMessage_1_1 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.1->: " + var1);
         }

         this.messageHeader = this.replyHeader = var1;
         this.setInputObject();
         if (var1.moreFragmentsToFollow()) {
            this.connection.clientReply_1_1_Put(this);
            this.setWorkThenPoolOrResumeSelect(var1);
            this.inputObject.unmarshalHeader();
            this.signalResponseReceived();
         } else {
            this.inputObject.unmarshalHeader();
            this.signalResponseReceived();
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var6) {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.1: !!ERROR!!: " + var1);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.1<-: " + var1);
         }

      }

   }

   public void handleInput(ReplyMessage_1_2 var1) throws IOException {
      try {
         try {
            this.messageHeader = this.replyHeader = var1;
            var1.unmarshalRequestID(this.dispatchByteBuffer);
            if (this.transportDebug()) {
               this.dprint(".REPLY 1.2->: id/" + var1.getRequestId() + ": more?: " + var1.moreFragmentsToFollow() + ": " + var1);
            }

            this.setInputObject();
            this.signalResponseReceived();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.2: id/" + var1.getRequestId() + ": !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".REPLY 1.2<-: id/" + var1.getRequestId() + ": " + var1);
         }

      }

   }

   public void handleInput(LocateRequestMessage_1_0 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.0->: " + var1);
         }

         try {
            this.messageHeader = var1;
            this.setInputObject();
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }

         this.getProtocolHandler().handleRequest((LocateRequestMessage)var1, this);
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.0: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.0<-: " + var1);
         }

      }

   }

   public void handleInput(LocateRequestMessage_1_1 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.1->: " + var1);
         }

         try {
            this.messageHeader = var1;
            this.setInputObject();
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }

         this.getProtocolHandler().handleRequest((LocateRequestMessage)var1, this);
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.1: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.1<-:" + var1);
         }

      }

   }

   public void handleInput(LocateRequestMessage_1_2 var1) throws IOException {
      try {
         try {
            this.messageHeader = var1;
            var1.unmarshalRequestID(this.dispatchByteBuffer);
            this.setInputObject();
            if (this.transportDebug()) {
               this.dprint(".LOCATE_REQUEST 1.2->: id/" + var1.getRequestId() + ": " + var1);
            }

            if (var1.moreFragmentsToFollow()) {
               this.connection.serverRequestMapPut(var1.getRequestId(), this);
            }
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }

         this.getProtocolHandler().handleRequest((LocateRequestMessage)var1, this);
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.2: id/" + var1.getRequestId() + ": !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REQUEST 1.2<-: id/" + var1.getRequestId() + ": " + var1);
         }

      }

   }

   public void handleInput(LocateReplyMessage_1_0 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.0->:" + var1);
         }

         try {
            this.messageHeader = var1;
            this.setInputObject();
            this.inputObject.unmarshalHeader();
            this.signalResponseReceived();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.0: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.0<-: " + var1);
         }

      }

   }

   public void handleInput(LocateReplyMessage_1_1 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.1->: " + var1);
         }

         try {
            this.messageHeader = var1;
            this.setInputObject();
            this.inputObject.unmarshalHeader();
            this.signalResponseReceived();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.1: !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.1<-: " + var1);
         }

      }

   }

   public void handleInput(LocateReplyMessage_1_2 var1) throws IOException {
      try {
         try {
            this.messageHeader = var1;
            var1.unmarshalRequestID(this.dispatchByteBuffer);
            this.setInputObject();
            if (this.transportDebug()) {
               this.dprint(".LOCATE_REPLY 1.2->: id/" + var1.getRequestId() + ": " + var1);
            }

            this.signalResponseReceived();
         } finally {
            this.setWorkThenPoolOrResumeSelect(var1);
         }
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.2: id/" + var1.getRequestId() + ": !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".LOCATE_REPLY 1.2<-: id/" + var1.getRequestId() + ": " + var1);
         }

      }

   }

   public void handleInput(FragmentMessage_1_1 var1) throws IOException {
      try {
         if (this.transportDebug()) {
            this.dprint(".FRAGMENT 1.1->: more?: " + var1.moreFragmentsToFollow() + ": " + var1);
         }

         try {
            this.messageHeader = var1;
            MessageMediator var2 = null;
            CDRInputObject var3 = null;
            if (this.connection.isServer()) {
               var2 = this.connection.serverRequest_1_1_Get();
            } else {
               var2 = this.connection.clientReply_1_1_Get();
            }

            if (var2 != null) {
               var3 = (CDRInputObject)var2.getInputObject();
            }

            if (var3 != null) {
               var3.getBufferManager().processFragment(this.dispatchByteBuffer, var1);
               if (!var1.moreFragmentsToFollow()) {
                  if (this.connection.isServer()) {
                     this.connection.serverRequest_1_1_Remove();
                  } else {
                     this.connection.clientReply_1_1_Remove();
                  }

                  return;
               }

               return;
            }

            if (this.transportDebug()) {
               this.dprint(".FRAGMENT 1.1: ++++DISCARDING++++: " + var1);
            }

            this.releaseByteBufferToPool();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var13) {
         if (this.transportDebug()) {
            this.dprint(".FRAGMENT 1.1: !!ERROR!!: " + var1, var13);
         }

         return;
      } finally {
         if (this.transportDebug()) {
            this.dprint(".FRAGMENT 1.1<-: " + var1);
         }

      }

   }

   public void handleInput(FragmentMessage_1_2 var1) throws IOException {
      try {
         try {
            this.messageHeader = var1;
            var1.unmarshalRequestID(this.dispatchByteBuffer);
            if (this.transportDebug()) {
               this.dprint(".FRAGMENT 1.2->: id/" + var1.getRequestId() + ": more?: " + var1.moreFragmentsToFollow() + ": " + var1);
            }

            Object var2 = null;
            InputObject var3 = null;
            if (this.connection.isServer()) {
               var2 = this.connection.serverRequestMapGet(var1.getRequestId());
            } else {
               var2 = this.connection.clientRequestMapGet(var1.getRequestId());
            }

            if (var2 != null) {
               var3 = ((MessageMediator)var2).getInputObject();
            }

            if (var3 != null) {
               ((CDRInputObject)var3).getBufferManager().processFragment(this.dispatchByteBuffer, var1);
               if (!this.connection.isServer()) {
               }

               return;
            }

            if (this.transportDebug()) {
               this.dprint(".FRAGMENT 1.2: id/" + var1.getRequestId() + ": ++++DISCARDING++++: " + var1);
            }

            this.releaseByteBufferToPool();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var13) {
         if (this.transportDebug()) {
            this.dprint(".FRAGMENT 1.2: id/" + var1.getRequestId() + ": !!ERROR!!: " + var1, var13);
         }

         return;
      } finally {
         if (this.transportDebug()) {
            this.dprint(".FRAGMENT 1.2<-: id/" + var1.getRequestId() + ": " + var1);
         }

      }

   }

   public void handleInput(CancelRequestMessage var1) throws IOException {
      try {
         try {
            this.messageHeader = var1;
            this.setInputObject();
            this.inputObject.unmarshalHeader();
            if (this.transportDebug()) {
               this.dprint(".CANCEL->: id/" + var1.getRequestId() + ": " + var1.getGIOPVersion() + ": " + var1);
            }

            this.processCancelRequest(var1.getRequestId());
            this.releaseByteBufferToPool();
         } finally {
            this.setWorkThenReadOrResumeSelect(var1);
         }
      } catch (Throwable var11) {
         if (this.transportDebug()) {
            this.dprint(".CANCEL: id/" + var1.getRequestId() + ": !!ERROR!!: " + var1, var11);
         }
      } finally {
         if (this.transportDebug()) {
            this.dprint(".CANCEL<-: id/" + var1.getRequestId() + ": " + var1.getGIOPVersion() + ": " + var1);
         }

      }

   }

   private void throwNotImplemented() {
      this.isThreadDone = false;
      this.throwNotImplemented("");
   }

   private void throwNotImplemented(String var1) {
      throw new RuntimeException("CorbaMessageMediatorImpl: not implemented " + var1);
   }

   private void dprint(String var1, Throwable var2) {
      this.dprint(var1);
      var2.printStackTrace(System.out);
   }

   private void dprint(String var1) {
      ORBUtility.dprint("CorbaMessageMediatorImpl", var1);
   }

   protected String opAndId(CorbaMessageMediator var1) {
      return ORBUtility.operationNameAndRequestId(var1);
   }

   private boolean transportDebug() {
      return this.orb.transportDebugFlag;
   }

   private final void processCancelRequest(int var1) {
      if (this.connection.isServer()) {
         Object var2 = this.connection.serverRequestMapGet(var1);
         int var3;
         if (var2 == null) {
            var2 = this.connection.serverRequest_1_1_Get();
            if (var2 == null) {
               return;
            }

            var3 = ((CorbaMessageMediator)var2).getRequestId();
            if (var3 != var1) {
               return;
            }

            if (var3 == 0) {
               return;
            }
         } else {
            var3 = ((CorbaMessageMediator)var2).getRequestId();
         }

         RequestMessage var4 = ((CorbaMessageMediator)var2).getRequestHeader();
         if (var4.getType() != 0) {
            this.wrapper.badMessageTypeForCancel();
         }

         BufferManagerReadStream var5 = (BufferManagerReadStream)((CDRInputObject)((MessageMediator)var2).getInputObject()).getBufferManager();
         var5.cancelProcessing(var1);
      }
   }

   public void handleRequest(RequestMessage var1, CorbaMessageMediator var2) {
      try {
         this.beginRequest(var2);

         try {
            this.handleRequestRequest(var2);
            if (var2.isOneWay()) {
               return;
            }
         } catch (Throwable var8) {
            if (var2.isOneWay()) {
               return;
            }

            this.handleThrowableDuringServerDispatch(var2, var8, CompletionStatus.COMPLETED_MAYBE);
         }

         this.sendResponse(var2);
      } catch (Throwable var9) {
         this.dispatchError(var2, "RequestMessage", var9);
      } finally {
         this.endRequest(var2);
      }

   }

   public void handleRequest(LocateRequestMessage var1, CorbaMessageMediator var2) {
      try {
         this.beginRequest(var2);

         try {
            this.handleLocateRequest(var2);
         } catch (Throwable var8) {
            this.handleThrowableDuringServerDispatch(var2, var8, CompletionStatus.COMPLETED_MAYBE);
         }

         this.sendResponse(var2);
      } catch (Throwable var9) {
         this.dispatchError(var2, "LocateRequestMessage", var9);
      } finally {
         this.endRequest(var2);
      }

   }

   private void beginRequest(CorbaMessageMediator var1) {
      ORB var2 = (ORB)var1.getBroker();
      if (var2.subcontractDebugFlag) {
         this.dprint(".handleRequest->:");
      }

      this.connection.serverRequestProcessingBegins();
   }

   private void dispatchError(CorbaMessageMediator var1, String var2, Throwable var3) {
      if (this.orb.subcontractDebugFlag) {
         this.dprint(".handleRequest: " + this.opAndId(var1) + ": !!ERROR!!: " + var2, var3);
      }

   }

   private void sendResponse(CorbaMessageMediator var1) {
      if (this.orb.subcontractDebugFlag) {
         this.dprint(".handleRequest: " + this.opAndId(var1) + ": sending response");
      }

      CDROutputObject var2 = (CDROutputObject)var1.getOutputObject();
      if (var2 != null) {
         var2.finishSendingMessage();
      }

   }

   private void endRequest(CorbaMessageMediator var1) {
      ORB var2 = (ORB)var1.getBroker();
      if (var2.subcontractDebugFlag) {
         this.dprint(".handleRequest<-: " + this.opAndId(var1));
      }

      try {
         OutputObject var3 = var1.getOutputObject();
         if (var3 != null) {
            var3.close();
         }

         InputObject var4 = var1.getInputObject();
         if (var4 != null) {
            var4.close();
         }
      } catch (IOException var8) {
         if (var2.subcontractDebugFlag) {
            this.dprint(".endRequest: IOException:" + var8.getMessage(), var8);
         }
      } finally {
         ((CorbaConnection)var1.getConnection()).serverRequestProcessingEnds();
      }

   }

   protected void handleRequestRequest(CorbaMessageMediator var1) {
      ((CDRInputObject)var1.getInputObject()).unmarshalHeader();
      ORB var2 = (ORB)var1.getBroker();
      synchronized(var2) {
         var2.checkShutdownState();
      }

      ObjectKey var3 = var1.getObjectKey();
      if (var2.subcontractDebugFlag) {
         ObjectKeyTemplate var4 = var3.getTemplate();
         this.dprint(".handleRequest: " + this.opAndId(var1) + ": dispatching to scid: " + var4.getSubcontractId());
      }

      CorbaServerRequestDispatcher var10 = var3.getServerRequestDispatcher(var2);
      if (var2.subcontractDebugFlag) {
         this.dprint(".handleRequest: " + this.opAndId(var1) + ": dispatching to sc: " + var10);
      }

      if (var10 == null) {
         throw this.wrapper.noServerScInDispatch();
      } else {
         try {
            var2.startingDispatch();
            var10.dispatch(var1);
         } finally {
            var2.finishedDispatch();
         }

      }
   }

   protected void handleLocateRequest(CorbaMessageMediator var1) {
      ORB var2 = (ORB)var1.getBroker();
      LocateRequestMessage var3 = (LocateRequestMessage)var1.getDispatchHeader();
      IOR var4 = null;
      LocateReplyMessage var5 = null;
      short var6 = -1;

      try {
         ((CDRInputObject)var1.getInputObject()).unmarshalHeader();
         CorbaServerRequestDispatcher var7 = var3.getObjectKey().getServerRequestDispatcher(var2);
         if (var7 == null) {
            return;
         }

         var4 = var7.locate(var3.getObjectKey());
         if (var4 == null) {
            var5 = MessageBase.createLocateReply(var2, var3.getGIOPVersion(), var3.getEncodingVersion(), var3.getRequestId(), 1, (IOR)null);
         } else {
            var5 = MessageBase.createLocateReply(var2, var3.getGIOPVersion(), var3.getEncodingVersion(), var3.getRequestId(), 2, var4);
         }
      } catch (AddressingDispositionException var8) {
         var5 = MessageBase.createLocateReply(var2, var3.getGIOPVersion(), var3.getEncodingVersion(), var3.getRequestId(), 5, (IOR)null);
         var6 = var8.expectedAddrDisp();
      } catch (RequestCanceledException var9) {
         return;
      } catch (Exception var10) {
         var5 = MessageBase.createLocateReply(var2, var3.getGIOPVersion(), var3.getEncodingVersion(), var3.getRequestId(), 0, (IOR)null);
      }

      CDROutputObject var11 = this.createAppropriateOutputObject(var1, var3, var5);
      var1.setOutputObject(var11);
      var11.setMessageMediator(var1);
      var5.write(var11);
      if (var4 != null) {
         var4.write(var11);
      }

      if (var6 != -1) {
         AddressingDispositionHelper.write(var11, var6);
      }

   }

   private CDROutputObject createAppropriateOutputObject(CorbaMessageMediator var1, Message var2, LocateReplyMessage var3) {
      CDROutputObject var4;
      if (var2.getGIOPVersion().lessThan(GIOPVersion.V1_2)) {
         var4 = OutputStreamFactory.newCDROutputObject((ORB)var1.getBroker(), this, GIOPVersion.V1_0, (CorbaConnection)var1.getConnection(), var3, (byte)1);
      } else {
         var4 = OutputStreamFactory.newCDROutputObject((ORB)var1.getBroker(), var1, var3, (byte)1);
      }

      return var4;
   }

   public void handleThrowableDuringServerDispatch(CorbaMessageMediator var1, Throwable var2, CompletionStatus var3) {
      if (((ORB)var1.getBroker()).subcontractDebugFlag) {
         this.dprint(".handleThrowableDuringServerDispatch: " + this.opAndId(var1) + ": " + var2);
      }

      this.handleThrowableDuringServerDispatch(var1, var2, var3, 1);
   }

   protected void handleThrowableDuringServerDispatch(CorbaMessageMediator var1, Throwable var2, CompletionStatus var3, int var4) {
      if (var4 > 10) {
         if (((ORB)var1.getBroker()).subcontractDebugFlag) {
            this.dprint(".handleThrowableDuringServerDispatch: " + this.opAndId(var1) + ": cannot handle: " + var2);
         }

         RuntimeException var8 = new RuntimeException("handleThrowableDuringServerDispatch: cannot create response.");
         var8.initCause(var2);
         throw var8;
      } else {
         try {
            if (var2 instanceof ForwardException) {
               ForwardException var7 = (ForwardException)var2;
               this.createLocationForward(var1, var7.getIOR(), (ServiceContexts)null);
            } else if (var2 instanceof AddressingDispositionException) {
               this.handleAddressingDisposition(var1, (AddressingDispositionException)var2);
            } else {
               SystemException var5 = this.convertThrowableToSystemException(var2, var3);
               this.createSystemExceptionResponse(var1, var5, (ServiceContexts)null);
            }
         } catch (Throwable var6) {
            this.handleThrowableDuringServerDispatch(var1, var6, var3, var4 + 1);
         }
      }
   }

   protected SystemException convertThrowableToSystemException(Throwable var1, CompletionStatus var2) {
      if (var1 instanceof SystemException) {
         return (SystemException)var1;
      } else {
         return (SystemException)(var1 instanceof RequestCanceledException ? this.wrapper.requestCanceled(var1) : this.wrapper.runtimeexception(CompletionStatus.COMPLETED_MAYBE, var1));
      }
   }

   protected void handleAddressingDisposition(CorbaMessageMediator var1, AddressingDispositionException var2) {
      boolean var3 = true;
      CDROutputObject var5;
      switch(var1.getRequestHeader().getType()) {
      case 0:
         ReplyMessage var4 = MessageBase.createReply((ORB)var1.getBroker(), var1.getGIOPVersion(), var1.getEncodingVersion(), var1.getRequestId(), 5, (ServiceContexts)null, (IOR)null);
         var5 = OutputStreamFactory.newCDROutputObject((ORB)var1.getBroker(), this, var1.getGIOPVersion(), (CorbaConnection)var1.getConnection(), var4, (byte)1);
         var1.setOutputObject(var5);
         var5.setMessageMediator(var1);
         var4.write(var5);
         AddressingDispositionHelper.write(var5, var2.expectedAddrDisp());
         return;
      case 3:
         LocateReplyMessage var6 = MessageBase.createLocateReply((ORB)var1.getBroker(), var1.getGIOPVersion(), var1.getEncodingVersion(), var1.getRequestId(), 5, (IOR)null);
         short var8 = var2.expectedAddrDisp();
         var5 = this.createAppropriateOutputObject(var1, var1.getRequestHeader(), var6);
         var1.setOutputObject(var5);
         var5.setMessageMediator(var1);
         var6.write(var5);
         Object var7 = null;
         if (var7 != null) {
            ((IOR)var7).write(var5);
         }

         if (var8 != -1) {
            AddressingDispositionHelper.write(var5, var8);
         }

         return;
      default:
      }
   }

   public CorbaMessageMediator createResponse(CorbaMessageMediator var1, ServiceContexts var2) {
      return this.createResponseHelper(var1, this.getServiceContextsForReply(var1, (ServiceContexts)null));
   }

   public CorbaMessageMediator createUserExceptionResponse(CorbaMessageMediator var1, ServiceContexts var2) {
      return this.createResponseHelper(var1, this.getServiceContextsForReply(var1, (ServiceContexts)null), true);
   }

   public CorbaMessageMediator createUnknownExceptionResponse(CorbaMessageMediator var1, UnknownException var2) {
      ServiceContexts var3 = null;
      UNKNOWN var4 = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
      var3 = new ServiceContexts((ORB)var1.getBroker());
      UEInfoServiceContext var5 = new UEInfoServiceContext(var4);
      var3.put(var5);
      return this.createSystemExceptionResponse(var1, var4, var3);
   }

   public CorbaMessageMediator createSystemExceptionResponse(CorbaMessageMediator var1, SystemException var2, ServiceContexts var3) {
      if (var1.getConnection() != null) {
         CorbaMessageMediatorImpl var4 = (CorbaMessageMediatorImpl)((CorbaConnection)var1.getConnection()).serverRequestMapGet(var1.getRequestId());
         OutputObject var5 = null;
         if (var4 != null) {
            var5 = var4.getOutputObject();
         }

         if (var5 != null && var4.sentFragment() && !var4.sentFullMessage()) {
            return var4;
         }
      }

      if (var1.executePIInResponseConstructor()) {
         ((ORB)var1.getBroker()).getPIHandler().setServerPIInfo((Exception)var2);
      }

      if (((ORB)var1.getBroker()).subcontractDebugFlag && var2 != null) {
         this.dprint(".createSystemExceptionResponse: " + this.opAndId(var1), var2);
      }

      ServiceContexts var6 = this.getServiceContextsForReply(var1, var3);
      this.addExceptionDetailMessage(var1, var2, var6);
      CorbaMessageMediator var7 = this.createResponseHelper(var1, var6, false);
      ORBUtility.writeSystemException(var2, (org.omg.CORBA_2_3.portable.OutputStream)var7.getOutputObject());
      return var7;
   }

   private void addExceptionDetailMessage(CorbaMessageMediator var1, SystemException var2, ServiceContexts var3) {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();
      PrintWriter var5 = new PrintWriter(var4);
      var2.printStackTrace(var5);
      var5.flush();
      EncapsOutputStream var6 = OutputStreamFactory.newEncapsOutputStream((ORB)var1.getBroker());
      var6.putEndian();
      var6.write_wstring(var4.toString());
      UnknownServiceContext var7 = new UnknownServiceContext(14, var6.toByteArray());
      var3.put(var7);
   }

   public CorbaMessageMediator createLocationForward(CorbaMessageMediator var1, IOR var2, ServiceContexts var3) {
      ReplyMessage var4 = MessageBase.createReply((ORB)var1.getBroker(), var1.getGIOPVersion(), var1.getEncodingVersion(), var1.getRequestId(), 3, this.getServiceContextsForReply(var1, var3), var2);
      return this.createResponseHelper(var1, var4, var2);
   }

   protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator var1, ServiceContexts var2) {
      ReplyMessage var3 = MessageBase.createReply((ORB)var1.getBroker(), var1.getGIOPVersion(), var1.getEncodingVersion(), var1.getRequestId(), 0, var2, (IOR)null);
      return this.createResponseHelper(var1, var3, (IOR)null);
   }

   protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator var1, ServiceContexts var2, boolean var3) {
      ReplyMessage var4 = MessageBase.createReply((ORB)var1.getBroker(), var1.getGIOPVersion(), var1.getEncodingVersion(), var1.getRequestId(), var3 ? 1 : 2, var2, (IOR)null);
      return this.createResponseHelper(var1, var4, (IOR)null);
   }

   protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator var1, ReplyMessage var2, IOR var3) {
      this.runServantPostInvoke(var1);
      this.runInterceptors(var1, var2);
      this.runRemoveThreadInfo(var1);
      if (((ORB)var1.getBroker()).subcontractDebugFlag) {
         this.dprint(".createResponseHelper: " + this.opAndId(var1) + ": " + var2);
      }

      var1.setReplyHeader(var2);
      Object var4;
      if (var1.getConnection() == null) {
         var4 = OutputStreamFactory.newCDROutputObject(this.orb, var1, var1.getReplyHeader(), var1.getStreamFormatVersion(), 0);
      } else {
         var4 = var1.getConnection().getAcceptor().createOutputObject(var1.getBroker(), var1);
      }

      var1.setOutputObject((OutputObject)var4);
      var1.getOutputObject().setMessageMediator(var1);
      var2.write((org.omg.CORBA_2_3.portable.OutputStream)var1.getOutputObject());
      if (var2.getIOR() != null) {
         var2.getIOR().write((org.omg.CORBA_2_3.portable.OutputStream)var1.getOutputObject());
      }

      return var1;
   }

   protected void runServantPostInvoke(CorbaMessageMediator var1) {
      ORB var2 = null;
      if (var1.executeReturnServantInResponseConstructor()) {
         var1.setExecuteReturnServantInResponseConstructor(false);
         var1.setExecuteRemoveThreadInfoInResponseConstructor(true);

         try {
            var2 = (ORB)var1.getBroker();
            OAInvocationInfo var3 = var2.peekInvocationInfo();
            ObjectAdapter var4 = var3.oa();

            try {
               var4.returnServant();
            } catch (Throwable var10) {
               this.wrapper.unexpectedException(var10);
               if (var10 instanceof Error) {
                  throw (Error)var10;
               }

               if (var10 instanceof RuntimeException) {
                  throw (RuntimeException)var10;
               }
            } finally {
               var4.exit();
            }
         } catch (EmptyStackException var12) {
            throw this.wrapper.emptyStackRunServantPostInvoke((Throwable)var12);
         }
      }

   }

   protected void runInterceptors(CorbaMessageMediator var1, ReplyMessage var2) {
      if (var1.executePIInResponseConstructor()) {
         ((ORB)var1.getBroker()).getPIHandler().invokeServerPIEndingPoint(var2);
         ((ORB)var1.getBroker()).getPIHandler().cleanupServerPIRequest();
         var1.setExecutePIInResponseConstructor(false);
      }

   }

   protected void runRemoveThreadInfo(CorbaMessageMediator var1) {
      if (var1.executeRemoveThreadInfoInResponseConstructor()) {
         var1.setExecuteRemoveThreadInfoInResponseConstructor(false);
         ((ORB)var1.getBroker()).popInvocationInfo();
      }

   }

   protected ServiceContexts getServiceContextsForReply(CorbaMessageMediator var1, ServiceContexts var2) {
      CorbaConnection var3 = (CorbaConnection)var1.getConnection();
      if (((ORB)var1.getBroker()).subcontractDebugFlag) {
         this.dprint(".getServiceContextsForReply: " + this.opAndId(var1) + ": " + var3);
      }

      if (var2 == null) {
         var2 = new ServiceContexts((ORB)var1.getBroker());
      }

      if (var3 != null && !var3.isPostInitialContexts()) {
         var3.setPostInitialContexts();
         SendingContextServiceContext var4 = new SendingContextServiceContext(((ORB)var1.getBroker()).getFVDCodeBaseIOR());
         if (var2.get(var4.getId()) != null) {
            throw this.wrapper.duplicateSendingContextServiceContext();
         }

         var2.put(var4);
         if (((ORB)var1.getBroker()).subcontractDebugFlag) {
            this.dprint(".getServiceContextsForReply: " + this.opAndId(var1) + ": added SendingContextServiceContext");
         }
      }

      ORBVersionServiceContext var5 = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
      if (var2.get(var5.getId()) != null) {
         throw this.wrapper.duplicateOrbVersionServiceContext();
      } else {
         var2.put(var5);
         if (((ORB)var1.getBroker()).subcontractDebugFlag) {
            this.dprint(".getServiceContextsForReply: " + this.opAndId(var1) + ": added ORB version service context");
         }

         return var2;
      }
   }

   private void releaseByteBufferToPool() {
      if (this.dispatchByteBuffer != null) {
         this.orb.getByteBufferPool().releaseByteBuffer(this.dispatchByteBuffer);
         if (this.transportDebug()) {
            int var1 = System.identityHashCode(this.dispatchByteBuffer);
            StringBuffer var2 = new StringBuffer();
            var2.append(".handleInput: releasing ByteBuffer (" + var1 + ") to ByteBufferPool");
            this.dprint(var2.toString());
         }
      }

   }
}
