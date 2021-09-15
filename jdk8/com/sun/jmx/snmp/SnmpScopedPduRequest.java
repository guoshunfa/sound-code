package com.sun.jmx.snmp;

public class SnmpScopedPduRequest extends SnmpScopedPduPacket implements SnmpPduRequestType {
   private static final long serialVersionUID = 6463060973056773680L;
   int errorStatus = 0;
   int errorIndex = 0;

   public void setErrorIndex(int var1) {
      this.errorIndex = var1;
   }

   public void setErrorStatus(int var1) {
      this.errorStatus = var1;
   }

   public int getErrorIndex() {
      return this.errorIndex;
   }

   public int getErrorStatus() {
      return this.errorStatus;
   }

   public SnmpPdu getResponsePdu() {
      SnmpScopedPduRequest var1 = new SnmpScopedPduRequest();
      var1.address = this.address;
      var1.port = this.port;
      var1.version = this.version;
      var1.requestId = this.requestId;
      var1.msgId = this.msgId;
      var1.msgMaxSize = this.msgMaxSize;
      var1.msgFlags = this.msgFlags;
      var1.msgSecurityModel = this.msgSecurityModel;
      var1.contextEngineId = this.contextEngineId;
      var1.contextName = this.contextName;
      var1.securityParameters = this.securityParameters;
      var1.type = 162;
      var1.errorStatus = 0;
      var1.errorIndex = 0;
      return var1;
   }
}
