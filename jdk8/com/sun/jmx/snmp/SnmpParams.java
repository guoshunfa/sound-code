package com.sun.jmx.snmp;

public abstract class SnmpParams implements SnmpDefinitions {
   private int protocolVersion = 0;

   SnmpParams(int var1) {
      this.protocolVersion = var1;
   }

   SnmpParams() {
   }

   public abstract boolean allowSnmpSets();

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public void setProtocolVersion(int var1) {
      this.protocolVersion = var1;
   }
}
