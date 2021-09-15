package com.sun.jmx.snmp;

public class SnmpPduRequest extends SnmpPduPacket implements SnmpPduRequestType {
   private static final long serialVersionUID = 2218754017025258979L;
   public int errorStatus = 0;
   public int errorIndex = 0;

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
