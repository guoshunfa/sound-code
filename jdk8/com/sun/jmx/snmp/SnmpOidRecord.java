package com.sun.jmx.snmp;

public class SnmpOidRecord {
   private String name;
   private String oid;
   private String type;

   public SnmpOidRecord(String var1, String var2, String var3) {
      this.name = var1;
      this.oid = var2;
      this.type = var3;
   }

   public String getName() {
      return this.name;
   }

   public String getOid() {
      return this.oid;
   }

   public String getType() {
      return this.type;
   }
}
