package com.sun.jmx.snmp;

public class SnmpStringFixed extends SnmpString {
   private static final long serialVersionUID = -9120939046874646063L;

   public SnmpStringFixed(byte[] var1) {
      super(var1);
   }

   public SnmpStringFixed(Byte[] var1) {
      super(var1);
   }

   public SnmpStringFixed(String var1) {
      super(var1);
   }

   public SnmpStringFixed(int var1, byte[] var2) throws IllegalArgumentException {
      if (var1 > 0 && var2 != null) {
         int var3 = Math.min(var1, var2.length);
         this.value = new byte[var1];

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            this.value[var4] = var2[var4];
         }

         for(var4 = var3; var4 < var1; ++var4) {
            this.value[var4] = 0;
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public SnmpStringFixed(int var1, Byte[] var2) throws IllegalArgumentException {
      if (var1 > 0 && var2 != null) {
         int var3 = Math.min(var1, var2.length);
         this.value = new byte[var1];

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            this.value[var4] = var2[var4];
         }

         for(var4 = var3; var4 < var1; ++var4) {
            this.value[var4] = 0;
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public SnmpStringFixed(int var1, String var2) throws IllegalArgumentException {
      if (var1 > 0 && var2 != null) {
         byte[] var3 = var2.getBytes();
         int var4 = Math.min(var1, var3.length);
         this.value = new byte[var1];

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            this.value[var5] = var3[var5];
         }

         for(var5 = var4; var5 < var1; ++var5) {
            this.value[var5] = 0;
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public static SnmpOid toOid(int var0, long[] var1, int var2) throws SnmpStatusException {
      try {
         long[] var3 = new long[var0];

         for(int var4 = 0; var4 < var0; ++var4) {
            var3[var4] = var1[var2 + var4];
         }

         return new SnmpOid(var3);
      } catch (IndexOutOfBoundsException var5) {
         throw new SnmpStatusException(2);
      }
   }

   public static int nextOid(int var0, long[] var1, int var2) throws SnmpStatusException {
      int var3 = var2 + var0;
      if (var3 > var1.length) {
         throw new SnmpStatusException(2);
      } else {
         return var3;
      }
   }

   public static void appendToOid(int var0, SnmpOid var1, SnmpOid var2) {
      var2.append(var1);
   }
}
