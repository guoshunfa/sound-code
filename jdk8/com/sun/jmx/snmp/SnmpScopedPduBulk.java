package com.sun.jmx.snmp;

public class SnmpScopedPduBulk extends SnmpScopedPduPacket implements SnmpPduBulkType {
   private static final long serialVersionUID = -1648623646227038885L;
   int nonRepeaters;
   int maxRepetitions;

   public SnmpScopedPduBulk() {
      this.type = 165;
      this.version = 3;
   }

   public void setMaxRepetitions(int var1) {
      this.maxRepetitions = var1;
   }

   public void setNonRepeaters(int var1) {
      this.nonRepeaters = var1;
   }

   public int getMaxRepetitions() {
      return this.maxRepetitions;
   }

   public int getNonRepeaters() {
      return this.nonRepeaters;
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
