package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpPduRequestType;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;

public class SnmpInformRequest implements SnmpDefinitions {
   private static SnmpRequestCounter requestCounter = new SnmpRequestCounter();
   private SnmpVarBindList varBindList = null;
   int errorStatus = 0;
   int errorIndex = 0;
   SnmpVarBind[] internalVarBind = null;
   String reason = null;
   private transient SnmpAdaptorServer adaptor;
   private transient SnmpSession informSession;
   private SnmpInformHandler callback = null;
   SnmpPdu requestPdu;
   SnmpPduRequestType responsePdu;
   private static final int stBase = 1;
   public static final int stInProgress = 1;
   public static final int stWaitingToSend = 3;
   public static final int stWaitingForReply = 5;
   public static final int stReceivedReply = 9;
   public static final int stAborted = 16;
   public static final int stTimeout = 32;
   public static final int stInternalError = 64;
   public static final int stResultsAvailable = 128;
   public static final int stNeverUsed = 256;
   private int numTries = 0;
   private int timeout = 3000;
   private int reqState = 256;
   private long prevPollTime = 0L;
   private long nextPollTime = 0L;
   private long waitTimeForResponse;
   private Date debugDate = new Date();
   private int requestId = 0;
   private int port = 0;
   private InetAddress address = null;
   private String communityString = null;

   SnmpInformRequest(SnmpSession var1, SnmpAdaptorServer var2, InetAddress var3, String var4, int var5, SnmpInformHandler var6) throws SnmpStatusException {
      this.informSession = var1;
      this.adaptor = var2;
      this.address = var3;
      this.communityString = var4;
      this.port = var5;
      this.callback = var6;
      this.informSession.addInformRequest(this);
      this.setTimeout(this.adaptor.getTimeout());
   }

   public final synchronized int getRequestId() {
      return this.requestId;
   }

   synchronized InetAddress getAddress() {
      return this.address;
   }

   public final synchronized int getRequestStatus() {
      return this.reqState;
   }

   public final synchronized boolean isAborted() {
      return (this.reqState & 16) == 16;
   }

   public final synchronized boolean inProgress() {
      return (this.reqState & 1) == 1;
   }

   public final synchronized boolean isResultAvailable() {
      return this.reqState == 128;
   }

   public final synchronized int getErrorStatus() {
      return this.errorStatus;
   }

   public final synchronized int getErrorIndex() {
      return this.errorIndex;
   }

   public final int getMaxTries() {
      return this.adaptor.getMaxTries();
   }

   public final synchronized int getNumTries() {
      return this.numTries;
   }

   final synchronized void setTimeout(int var1) {
      this.timeout = var1;
   }

   public final synchronized long getAbsNextPollTime() {
      return this.nextPollTime;
   }

   public final synchronized long getAbsMaxTimeToWait() {
      return this.prevPollTime == 0L ? System.currentTimeMillis() : this.waitTimeForResponse;
   }

   public final synchronized SnmpVarBindList getResponseVarBindList() {
      return this.inProgress() ? null : this.varBindList;
   }

   public final boolean waitForCompletion(long var1) {
      if (!this.inProgress()) {
         return true;
      } else {
         if (this.informSession.thisSessionContext()) {
            SnmpInformHandler var3 = this.callback;
            this.callback = null;
            this.informSession.waitForResponse(this, var1);
            this.callback = var3;
         } else {
            synchronized(this) {
               SnmpInformHandler var4 = this.callback;

               try {
                  this.callback = null;
                  this.wait(var1);
               } catch (InterruptedException var7) {
               }

               this.callback = var4;
            }
         }

         return !this.inProgress();
      }
   }

   public final void cancelRequest() {
      this.errorStatus = 225;
      this.stopRequest();
      this.deleteRequest();
      this.notifyClient();
   }

   public final synchronized void notifyClient() {
      this.notifyAll();
   }

   protected void finalize() {
      this.callback = null;
      this.varBindList = null;
      this.internalVarBind = null;
      this.adaptor = null;
      this.informSession = null;
      this.requestPdu = null;
      this.responsePdu = null;
   }

   public static String snmpErrorToString(int var0) {
      switch(var0) {
      case 0:
         return "noError";
      case 1:
         return "tooBig";
      case 2:
         return "noSuchName";
      case 3:
         return "badValue";
      case 4:
         return "readOnly";
      case 5:
         return "genErr";
      case 6:
         return "noAccess";
      case 7:
         return "wrongType";
      case 8:
         return "wrongLength";
      case 9:
         return "wrongEncoding";
      case 10:
         return "wrongValue";
      case 11:
         return "noCreation";
      case 12:
         return "inconsistentValue";
      case 13:
         return "resourceUnavailable";
      case 14:
         return "commitFailed";
      case 15:
         return "undoFailed";
      case 16:
         return "authorizationError";
      case 17:
         return "notWritable";
      case 18:
         return "inconsistentName";
      case 224:
         return "reqTimeout";
      case 225:
         return "reqAborted";
      case 226:
         return "rspDecodingError";
      case 227:
         return "reqEncodingError";
      case 228:
         return "reqPacketOverflow";
      case 229:
         return "rspEndOfTable";
      case 230:
         return "reqRefireAfterVbFix";
      case 231:
         return "reqHandleTooBig";
      case 232:
         return "reqTooBigImpossible";
      case 240:
         return "reqInternalError";
      case 241:
         return "reqSocketIOError";
      case 242:
         return "reqUnknownError";
      case 243:
         return "wrongSnmpVersion";
      case 244:
         return "snmpUnknownPrincipal";
      case 245:
         return "snmpAuthNotSupported";
      case 246:
         return "snmpPrivNotSupported";
      case 247:
         return "snmpUsmBadEngineId";
      case 248:
         return "snmpUsmInvalidTimeliness";
      case 249:
         return "snmpBadSecurityLevel";
      default:
         return "Unknown Error = " + var0;
      }
   }

   synchronized void start(SnmpVarBindList var1) throws SnmpStatusException {
      if (this.inProgress()) {
         throw new SnmpStatusException("Inform request already in progress.");
      } else {
         this.setVarBindList(var1);
         this.initializeAndFire();
      }
   }

   private synchronized void initializeAndFire() {
      this.requestPdu = null;
      this.responsePdu = null;
      this.reason = null;
      this.startRequest(System.currentTimeMillis());
      this.setErrorStatusAndIndex(0, 0);
   }

   private synchronized void startRequest(long var1) {
      this.nextPollTime = var1;
      this.prevPollTime = 0L;
      this.schedulePoll();
   }

   private void schedulePoll() {
      this.numTries = 0;
      this.initNewRequest();
      this.setRequestStatus(3);
      this.informSession.getSnmpQManager().addRequest(this);
   }

   void action() {
      if (this.inProgress()) {
         while(true) {
            try {
               if (this.numTries == 0) {
                  this.invokeOnReady();
               } else if (this.numTries < this.getMaxTries()) {
                  this.invokeOnRetry();
               } else {
                  this.invokeOnTimeout();
               }

               return;
            } catch (OutOfMemoryError var2) {
               ++this.numTries;
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "action", "Inform request hit out of memory situation...");
               }

               Thread.yield();
            }
         }
      }
   }

   private void invokeOnReady() {
      if (this.requestPdu == null) {
         this.requestPdu = this.constructPduPacket();
      }

      if (this.requestPdu != null && !this.sendPdu()) {
         this.queueResponse();
      }

   }

   private void invokeOnRetry() {
      this.invokeOnReady();
   }

   private void invokeOnTimeout() {
      this.errorStatus = 224;
      this.queueResponse();
   }

   private void queueResponse() {
      this.informSession.addResponse(this);
   }

   synchronized SnmpPdu constructPduPacket() {
      SnmpPduRequest var1 = null;
      Exception var2 = null;

      try {
         var1 = new SnmpPduRequest();
         var1.port = this.port;
         var1.type = 166;
         var1.version = 1;
         var1.community = this.communityString.getBytes("8859_1");
         var1.requestId = this.getRequestId();
         var1.varBindList = this.internalVarBind;
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "constructPduPacket", "Packet built");
         }
      } catch (Exception var4) {
         var2 = var4;
         this.errorStatus = 242;
         this.reason = var4.getMessage();
      }

      if (var2 != null) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "constructPduPacket", (String)"Got unexpected exception", (Throwable)var2);
         }

         var1 = null;
         this.queueResponse();
      }

      return var1;
   }

   boolean sendPdu() {
      try {
         this.responsePdu = null;
         SnmpPduFactory var1 = this.adaptor.getPduFactory();
         SnmpMessage var2 = (SnmpMessage)var1.encodeSnmpPdu((SnmpPduPacket)this.requestPdu, this.adaptor.getBufferSize());
         if (var2 == null) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "pdu factory returned a null value");
            }

            throw new SnmpStatusException(242);
         }

         int var3 = this.adaptor.getBufferSize();
         byte[] var4 = new byte[var3];
         int var5 = var2.encodeMessage(var4);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "sendPdu", "Dump : \n" + var2.printMessage());
         }

         this.sendPduPacket(var4, var5);
         return true;
      } catch (SnmpTooBigException var6) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", (String)"Got unexpected exception", (Throwable)var6);
         }

         this.setErrorStatusAndIndex(228, var6.getVarBindCount());
         this.requestPdu = null;
         this.reason = var6.getMessage();
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Packet Overflow while building inform request");
         }
      } catch (IOException var7) {
         this.setErrorStatusAndIndex(241, 0);
         this.reason = var7.getMessage();
      } catch (Exception var8) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", (String)"Got unexpected exception", (Throwable)var8);
         }

         this.setErrorStatusAndIndex(242, 0);
         this.reason = var8.getMessage();
      }

      return false;
   }

   final void sendPduPacket(byte[] var1, int var2) throws IOException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "sendPduPacket", "Send to peer. Peer/Port : " + this.address.getHostName() + "/" + this.port + ". Length = " + var2 + "\nDump : \n" + SnmpMessage.dumpHexBuffer(var1, 0, var2));
      }

      SnmpSocket var3 = this.informSession.getSocket();
      synchronized(var3) {
         var3.sendPacket(var1, var2, this.address, this.port);
         this.setRequestSentTime(System.currentTimeMillis());
      }
   }

   final void processResponse() {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "processResponse", "errstatus = " + this.errorStatus);
      }

      if (!this.inProgress()) {
         this.responsePdu = null;
      } else if (this.errorStatus >= 240) {
         this.handleInternalError("Internal Error...");
      } else {
         try {
            this.parsePduPacket(this.responsePdu);
            switch(this.errorStatus) {
            case 0:
               this.handleSuccess();
               return;
            case 224:
               this.handleTimeout();
               return;
            case 230:
               this.initializeAndFire();
               return;
            case 231:
               this.setErrorStatusAndIndex(1, 0);
               this.handleError("Cannot handle too-big situation...");
               return;
            case 240:
               this.handleInternalError("Unknown internal error.  deal with it later!");
               return;
            default:
               this.handleError("Error status set in packet...!!");
            }
         } catch (Exception var2) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "processResponse", (String)"Got unexpected exception", (Throwable)var2);
            }

            this.reason = var2.getMessage();
            this.handleInternalError(this.reason);
         }
      }
   }

   synchronized void parsePduPacket(SnmpPduRequestType var1) {
      if (var1 != null) {
         this.errorStatus = var1.getErrorStatus();
         this.errorIndex = var1.getErrorIndex();
         if (this.errorStatus == 0) {
            this.updateInternalVarBindWithResult(((SnmpPdu)var1).varBindList);
         } else {
            if (this.errorStatus != 0) {
               --this.errorIndex;
            }

            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "parsePduPacket", "received inform response. ErrorStatus/ErrorIndex = " + this.errorStatus + "/" + this.errorIndex);
            }

         }
      }
   }

   private void handleSuccess() {
      this.setRequestStatus(128);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "handleSuccess", "Invoking user defined callback...");
      }

      this.deleteRequest();
      this.notifyClient();
      this.requestPdu = null;
      this.internalVarBind = null;

      try {
         if (this.callback != null) {
            this.callback.processSnmpPollData(this, this.errorStatus, this.errorIndex, this.getVarBindList());
         }
      } catch (Exception var2) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleSuccess", (String)"Exception generated by user callback", (Throwable)var2);
         }
      } catch (OutOfMemoryError var3) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleSuccess", (String)"OutOfMemory Error generated by user callback", (Throwable)var3);
         }

         Thread.yield();
      }

   }

   private void handleTimeout() {
      this.setRequestStatus(32);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "Snmp error/index = " + snmpErrorToString(this.errorStatus) + "/" + this.errorIndex + ". Invoking timeout user defined callback...");
      }

      this.deleteRequest();
      this.notifyClient();
      this.requestPdu = null;
      this.responsePdu = null;
      this.internalVarBind = null;

      try {
         if (this.callback != null) {
            this.callback.processSnmpPollTimeout(this);
         }
      } catch (Exception var2) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", (String)"Exception generated by user callback", (Throwable)var2);
         }
      } catch (OutOfMemoryError var3) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", (String)"OutOfMemory Error generated by user callback", (Throwable)var3);
         }

         Thread.yield();
      }

   }

   private void handleError(String var1) {
      this.setRequestStatus(128);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "Snmp error/index = " + snmpErrorToString(this.errorStatus) + "/" + this.errorIndex + ". Invoking error user defined callback...\n" + this.getVarBindList());
      }

      this.deleteRequest();
      this.notifyClient();
      this.requestPdu = null;
      this.responsePdu = null;
      this.internalVarBind = null;

      try {
         if (this.callback != null) {
            this.callback.processSnmpPollData(this, this.getErrorStatus(), this.getErrorIndex(), this.getVarBindList());
         }
      } catch (Exception var3) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", (String)"Exception generated by user callback", (Throwable)var3);
         }
      } catch (OutOfMemoryError var4) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", (String)"OutOfMemory Error generated by user callback", (Throwable)var4);
         }

         Thread.yield();
      }

   }

   private void handleInternalError(String var1) {
      this.setRequestStatus(64);
      if (this.reason == null) {
         this.reason = var1;
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "Snmp error/index = " + snmpErrorToString(this.errorStatus) + "/" + this.errorIndex + ". Invoking internal error user defined callback...\n" + this.getVarBindList());
      }

      this.deleteRequest();
      this.notifyClient();
      this.requestPdu = null;
      this.responsePdu = null;
      this.internalVarBind = null;

      try {
         if (this.callback != null) {
            this.callback.processSnmpInternalError(this, this.reason);
         }
      } catch (Exception var3) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", (String)"Exception generated by user callback", (Throwable)var3);
         }
      } catch (OutOfMemoryError var4) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", (String)"OutOfMemory Error generated by user callback", (Throwable)var4);
         }

         Thread.yield();
      }

   }

   void updateInternalVarBindWithResult(SnmpVarBind[] var1) {
      if (var1 != null && var1.length != 0) {
         int var2 = 0;

         for(int var3 = 0; var3 < this.internalVarBind.length && var2 < var1.length; ++var3) {
            SnmpVarBind var4 = this.internalVarBind[var3];
            if (var4 != null) {
               SnmpVarBind var5 = var1[var2];
               var4.setSnmpValue(var5.getSnmpValue());
               ++var2;
            }
         }

      }
   }

   final void invokeOnResponse(Object var1) {
      if (var1 != null) {
         if (!(var1 instanceof SnmpPduRequestType)) {
            return;
         }

         this.responsePdu = (SnmpPduRequestType)var1;
      }

      this.setRequestStatus(9);
      this.queueResponse();
   }

   private void stopRequest() {
      synchronized(this) {
         this.setRequestStatus(16);
      }

      this.informSession.getSnmpQManager().removeRequest(this);
      synchronized(this) {
         this.requestId = 0;
      }
   }

   final synchronized void deleteRequest() {
      this.informSession.removeInformRequest(this);
   }

   final synchronized SnmpVarBindList getVarBindList() {
      return this.varBindList;
   }

   final synchronized void setVarBindList(SnmpVarBindList var1) {
      this.varBindList = var1;
      if (this.internalVarBind == null || this.internalVarBind.length != this.varBindList.size()) {
         this.internalVarBind = new SnmpVarBind[this.varBindList.size()];
      }

      this.varBindList.copyInto(this.internalVarBind);
   }

   final synchronized void setErrorStatusAndIndex(int var1, int var2) {
      this.errorStatus = var1;
      this.errorIndex = var2;
   }

   final synchronized void setPrevPollTime(long var1) {
      this.prevPollTime = var1;
   }

   final void setRequestSentTime(long var1) {
      ++this.numTries;
      this.setPrevPollTime(var1);
      this.waitTimeForResponse = this.prevPollTime + (long)(this.timeout * this.numTries);
      this.setRequestStatus(5);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "setRequestSentTime", "Inform request Successfully sent");
      }

      this.informSession.getSnmpQManager().addWaiting(this);
   }

   final synchronized void initNewRequest() {
      this.requestId = requestCounter.getNewId();
   }

   long timeRemainingForAction(long var1) {
      switch(this.reqState) {
      case 3:
         return this.nextPollTime - var1;
      case 5:
         return this.waitTimeForResponse - var1;
      default:
         return -1L;
      }
   }

   static String statusDescription(int var0) {
      switch(var0) {
      case 3:
         return "Waiting to send.";
      case 5:
         return "Waiting for reply.";
      case 9:
         return "Response arrived.";
      case 16:
         return "Aborted by user.";
      case 32:
         return "Timeout Occured.";
      case 64:
         return "Internal error.";
      case 128:
         return "Results available";
      case 256:
         return "Inform request in createAndWait state";
      default:
         return "Unknown inform request state.";
      }
   }

   final synchronized void setRequestStatus(int var1) {
      this.reqState = var1;
   }

   public synchronized String toString() {
      StringBuffer var1 = new StringBuffer(300);
      var1.append(this.tostring());
      var1.append("\nPeer/Port : " + this.address.getHostName() + "/" + this.port);
      return var1.toString();
   }

   private synchronized String tostring() {
      StringBuffer var1 = new StringBuffer("InformRequestId = " + this.requestId);
      var1.append("   Status = " + statusDescription(this.reqState));
      var1.append("  Timeout/MaxTries/NumTries = " + this.timeout * this.numTries + "/" + this.getMaxTries() + "/" + this.numTries);
      if (this.prevPollTime > 0L) {
         this.debugDate.setTime(this.prevPollTime);
         var1.append("\nPrevPolled = " + this.debugDate.toString());
      } else {
         var1.append("\nNeverPolled");
      }

      var1.append(" / RemainingTime(millis) = " + this.timeRemainingForAction(System.currentTimeMillis()));
      return var1.toString();
   }
}
