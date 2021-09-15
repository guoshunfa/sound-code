package com.sun.jmx.snmp;

public class SnmpPduBulk extends SnmpPduPacket implements SnmpPduBulkType {
   private static final long serialVersionUID = -7431306775883371046L;
   public int nonRepeaters;
   public int maxRepetitions;

   public SnmpPduBulk() {
      this.type = 165;
      this.version = 1;
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
      SnmpPduRequest var1 = new SnmpPduRequest();
      var1.address = this.address;
      var1.port = this.port;
      var1.version = this.version;
      var1.community = this.community;
      var1.type = 162;
      var1.requestId = this.requestId;
      var1.errorStatus = 0;
      var1.errorIndex = 0;
      return var1;
   }
}
