package com.sun.jmx.snmp;

public class SnmpInt extends SnmpValue {
   private static final long serialVersionUID = -7163624758070343373L;
   static final String name = "Integer32";
   protected long value;

   public SnmpInt(int var1) throws IllegalArgumentException {
      this.value = 0L;
      if (!this.isInitValueValid(var1)) {
         throw new IllegalArgumentException();
      } else {
         this.value = (long)var1;
      }
   }

   public SnmpInt(Integer var1) throws IllegalArgumentException {
      this(var1);
   }

   public SnmpInt(long var1) throws IllegalArgumentException {
      this.value = 0L;
      if (!this.isInitValueValid(var1)) {
         throw new IllegalArgumentException();
      } else {
         this.value = var1;
      }
   }

   public SnmpInt(Long var1) throws IllegalArgumentException {
      this(var1);
   }

   public SnmpInt(Enumerated var1) throws IllegalArgumentException {
      this(var1.intValue());
   }

   public SnmpInt(boolean var1) {
      this.value = 0L;
      this.value = var1 ? 1L : 2L;
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
      SnmpInt var1 = null;

      try {
         var1 = (SnmpInt)super.clone();
         var1.value = this.value;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   public String getTypeName() {
      return "Integer32";
   }

   boolean isInitValueValid(int var1) {
      return var1 >= Integer.MIN_VALUE && var1 <= Integer.MAX_VALUE;
   }

   boolean isInitValueValid(long var1) {
      return var1 >= -2147483648L && var1 <= 2147483647L;
   }
}
