package com.sun.jmx.snmp;

public class SnmpIpAddress extends SnmpOid {
   private static final long serialVersionUID = 7204629998270874474L;
   static final String name = "IpAddress";

   public SnmpIpAddress(byte[] var1) throws IllegalArgumentException {
      this.buildFromByteArray(var1);
   }

   public SnmpIpAddress(long var1) {
      int var3 = (int)var1;
      byte[] var4 = new byte[]{(byte)(var3 >>> 24 & 255), (byte)(var3 >>> 16 & 255), (byte)(var3 >>> 8 & 255), (byte)(var3 & 255)};
      this.buildFromByteArray(var4);
   }

   public SnmpIpAddress(String var1) throws IllegalArgumentException {
      super(var1);
      if (this.componentCount > 4 || this.components[0] > 255L || this.components[1] > 255L || this.components[2] > 255L || this.components[3] > 255L) {
         throw new IllegalArgumentException(var1);
      }
   }

   public SnmpIpAddress(long var1, long var3, long var5, long var7) {
      super(var1, var3, var5, var7);
      if (this.components[0] > 255L || this.components[1] > 255L || this.components[2] > 255L || this.components[3] > 255L) {
         throw new IllegalArgumentException();
      }
   }

   public byte[] byteValue() {
      byte[] var1 = new byte[]{(byte)((int)this.components[0]), (byte)((int)this.components[1]), (byte)((int)this.components[2]), (byte)((int)this.components[3])};
      return var1;
   }

   public String stringValue() {
      return this.toString();
   }

   public static SnmpOid toOid(long[] var0, int var1) throws SnmpStatusException {
      if (var1 + 4 <= var0.length) {
         try {
            return new SnmpOid(var0[var1], var0[var1 + 1], var0[var1 + 2], var0[var1 + 3]);
         } catch (IllegalArgumentException var3) {
            throw new SnmpStatusException(2);
         }
      } else {
         throw new SnmpStatusException(2);
      }
   }

   public static int nextOid(long[] var0, int var1) throws SnmpStatusException {
      if (var1 + 4 <= var0.length) {
         return var1 + 4;
      } else {
         throw new SnmpStatusException(2);
      }
   }

   public static void appendToOid(SnmpOid var0, SnmpOid var1) {
      if (var0.getLength() != 4) {
         throw new IllegalArgumentException();
      } else {
         var1.append(var0);
      }
   }

   public final String getTypeName() {
      return "IpAddress";
   }

   private void buildFromByteArray(byte[] var1) {
      if (var1.length != 4) {
         throw new IllegalArgumentException();
      } else {
         this.components = new long[4];
         this.componentCount = 4;
         this.components[0] = var1[0] >= 0 ? (long)var1[0] : (long)(var1[0] + 256);
         this.components[1] = var1[1] >= 0 ? (long)var1[1] : (long)(var1[1] + 256);
         this.components[2] = var1[2] >= 0 ? (long)var1[2] : (long)(var1[2] + 256);
         this.components[3] = var1[3] >= 0 ? (long)var1[3] : (long)(var1[3] + 256);
      }
   }
}
