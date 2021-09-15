package com.sun.jmx.snmp;

public abstract class SnmpUnsignedInt extends SnmpInt {
   public static final long MAX_VALUE = 4294967295L;
   static final String name = "Unsigned32";

   public SnmpUnsignedInt(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpUnsignedInt(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpUnsignedInt(long var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpUnsignedInt(Long var1) throws IllegalArgumentException {
      super(var1);
   }

   public String getTypeName() {
      return "Unsigned32";
   }

   boolean isInitValueValid(int var1) {
      return var1 >= 0 && (long)var1 <= 4294967295L;
   }

   boolean isInitValueValid(long var1) {
      return var1 >= 0L && var1 <= 4294967295L;
   }
}
