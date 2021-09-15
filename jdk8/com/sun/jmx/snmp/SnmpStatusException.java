package com.sun.jmx.snmp;

public class SnmpStatusException extends Exception implements SnmpDefinitions {
   private static final long serialVersionUID = 5809485694133115675L;
   public static final int noSuchName = 2;
   public static final int badValue = 3;
   public static final int readOnly = 4;
   public static final int noAccess = 6;
   public static final int noSuchInstance = 224;
   public static final int noSuchObject = 225;
   private int errorStatus = 0;
   private int errorIndex = -1;

   public SnmpStatusException(int var1) {
      this.errorStatus = var1;
   }

   public SnmpStatusException(int var1, int var2) {
      this.errorStatus = var1;
      this.errorIndex = var2;
   }

   public SnmpStatusException(String var1) {
      super(var1);
   }

   public SnmpStatusException(SnmpStatusException var1, int var2) {
      super(var1.getMessage());
      this.errorStatus = var1.errorStatus;
      this.errorIndex = var2;
   }

   public int getStatus() {
      return this.errorStatus;
   }

   public int getErrorIndex() {
      return this.errorIndex;
   }
}
