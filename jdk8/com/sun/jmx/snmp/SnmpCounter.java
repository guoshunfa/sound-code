package com.sun.jmx.snmp;

public class SnmpCounter extends SnmpUnsignedInt {
   private static final long serialVersionUID = 4655264728839396879L;
   static final String name = "Counter32";

   public SnmpCounter(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpCounter(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpCounter(long var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpCounter(Long var1) throws IllegalArgumentException {
      super(var1);
   }

   public final String getTypeName() {
      return "Counter32";
   }
}
