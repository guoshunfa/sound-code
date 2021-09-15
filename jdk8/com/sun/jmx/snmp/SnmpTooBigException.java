package com.sun.jmx.snmp;

public class SnmpTooBigException extends Exception {
   private static final long serialVersionUID = 4754796246674803969L;
   private int varBindCount;

   public SnmpTooBigException() {
      this.varBindCount = 0;
   }

   public SnmpTooBigException(int var1) {
      this.varBindCount = var1;
   }

   public int getVarBindCount() {
      return this.varBindCount;
   }
}
