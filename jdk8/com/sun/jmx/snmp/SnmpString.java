package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SnmpString extends SnmpValue {
   private static final long serialVersionUID = -7011986973225194188L;
   static final String name = "String";
   protected byte[] value = null;

   public SnmpString() {
      this.value = new byte[0];
   }

   public SnmpString(byte[] var1) {
      this.value = (byte[])var1.clone();
   }

   public SnmpString(Byte[] var1) {
      this.value = new byte[var1.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.value[var2] = var1[var2];
      }

   }

   public SnmpString(String var1) {
      this.value = var1.getBytes();
   }

   public SnmpString(InetAddress var1) {
      this.value = var1.getAddress();
   }

   public InetAddress inetAddressValue() throws UnknownHostException {
      return InetAddress.getByAddress(this.value);
   }

   public static String BinToChar(String var0) {
      char[] var1 = new char[var0.length() / 8];
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1[var3] = (char)Integer.parseInt(var0.substring(8 * var3, 8 * var3 + 8), 2);
      }

      return new String(var1);
   }

   public static String HexToChar(String var0) {
      char[] var1 = new char[var0.length() / 2];
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1[var3] = (char)Integer.parseInt(var0.substring(2 * var3, 2 * var3 + 2), 16);
      }

      return new String(var1);
   }

   public byte[] byteValue() {
      return (byte[])this.value.clone();
   }

   public Byte[] toByte() {
      Byte[] var1 = new Byte[this.value.length];

      for(int var2 = 0; var2 < this.value.length; ++var2) {
         var1[var2] = new Byte(this.value[var2]);
      }

      return var1;
   }

   public String toString() {
      return new String(this.value);
   }

   public SnmpOid toOid() {
      long[] var1 = new long[this.value.length];

      for(int var2 = 0; var2 < this.value.length; ++var2) {
         var1[var2] = (long)(this.value[var2] & 255);
      }

      return new SnmpOid(var1);
   }

   public static SnmpOid toOid(long[] var0, int var1) throws SnmpStatusException {
      try {
         if (var0[var1] > 2147483647L) {
            throw new SnmpStatusException(2);
         } else {
            int var2 = (int)var0[var1++];
            long[] var3 = new long[var2];

            for(int var4 = 0; var4 < var2; ++var4) {
               var3[var4] = var0[var1 + var4];
            }

            return new SnmpOid(var3);
         }
      } catch (IndexOutOfBoundsException var5) {
         throw new SnmpStatusException(2);
      }
   }

   public static int nextOid(long[] var0, int var1) throws SnmpStatusException {
      try {
         if (var0[var1] > 2147483647L) {
            throw new SnmpStatusException(2);
         } else {
            int var2 = (int)var0[var1++];
            var1 += var2;
            if (var1 <= var0.length) {
               return var1;
            } else {
               throw new SnmpStatusException(2);
            }
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new SnmpStatusException(2);
      }
   }

   public static void appendToOid(SnmpOid var0, SnmpOid var1) {
      var1.append((long)var0.getLength());
      var1.append(var0);
   }

   public final synchronized SnmpValue duplicate() {
      return (SnmpValue)this.clone();
   }

   public synchronized Object clone() {
      SnmpString var1 = null;

      try {
         var1 = (SnmpString)super.clone();
         var1.value = new byte[this.value.length];
         System.arraycopy(this.value, 0, var1.value, 0, this.value.length);
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   public String getTypeName() {
      return "String";
   }
}
