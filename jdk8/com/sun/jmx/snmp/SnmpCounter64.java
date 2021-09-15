package com.sun.jmx.snmp;

public class SnmpCounter64 extends SnmpValue {
   private static final long serialVersionUID = 8784850650494679937L;
   static final String name = "Counter64";
   private long value;

   public SnmpCounter64(long var1) throws IllegalArgumentException {
      this.value = 0L;
      if (var1 >= 0L && var1 <= Long.MAX_VALUE) {
         this.value = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public SnmpCounter64(Long var1) throws IllegalArgumentException {
      this(var1);
   }

   public long longValue() {
      return this.value;
   }

   public Long toLong() {
      return new Long(this.value);
   }

   public int intValue() {
      return (int)this.value;
   }

   public Integer toInteger() {
      return new Integer((int)this.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }

   public SnmpOid toOid() {
      return new SnmpOid(this.value);
   }

   public static SnmpOid toOid(long[] var0, int var1) throws SnmpStatusException {
      try {
         return new SnmpOid(var0[var1]);
      } catch (IndexOutOfBoundsException var3) {
         throw new SnmpStatusException(2);
      }
   }

   public static int nextOid(long[] var0, int var1) throws SnmpStatusException {
      if (var1 >= var0.length) {
         throw new SnmpStatusException(2);
      } else {
         return var1 + 1;
      }
   }

   public static void appendToOid(SnmpOid var0, SnmpOid var1) {
      if (var0.getLength() != 1) {
         throw new IllegalArgumentException();
      } else {
         var1.append(var0);
      }
   }

   public final synchronized SnmpValue duplicate() {
      return (SnmpValue)this.clone();
   }

   public final synchronized Object clone() {
      SnmpCounter64 var1 = null;

      try {
         var1 = (SnmpCounter64)super.clone();
         var1.value = this.value;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   public final String getTypeName() {
      return "Counter64";
   }
}
