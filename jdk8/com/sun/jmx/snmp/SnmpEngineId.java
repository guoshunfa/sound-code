package com.sun.jmx.snmp;

import com.sun.jmx.snmp.internal.SnmpTools;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SnmpEngineId implements Serializable {
   private static final long serialVersionUID = 5434729655830763317L;
   byte[] engineId = null;
   String hexString = null;
   String humanString = null;

   SnmpEngineId(String var1) {
      this.engineId = SnmpTools.ascii2binary(var1);
      this.hexString = var1.toLowerCase();
   }

   SnmpEngineId(byte[] var1) {
      this.engineId = var1;
      this.hexString = SnmpTools.binary2ascii(var1).toLowerCase();
   }

   public String getReadableId() {
      return this.humanString;
   }

   public String toString() {
      return this.hexString;
   }

   public byte[] getBytes() {
      return this.engineId;
   }

   void setStringValue(String var1) {
      this.humanString = var1;
   }

   static void validateId(String var0) throws IllegalArgumentException {
      byte[] var1 = SnmpTools.ascii2binary(var0);
      validateId(var1);
   }

   static void validateId(byte[] var0) throws IllegalArgumentException {
      if (var0.length < 5) {
         throw new IllegalArgumentException("Id size lower than 5 bytes.");
      } else if (var0.length > 32) {
         throw new IllegalArgumentException("Id size greater than 32 bytes.");
      } else if ((var0[0] & 128) == 0 && var0.length != 12) {
         throw new IllegalArgumentException("Very first bit = 0 and length != 12 octets");
      } else {
         byte[] var1 = new byte[var0.length];
         if (Arrays.equals(var1, var0)) {
            throw new IllegalArgumentException("Zeroed Id.");
         } else {
            byte[] var2 = new byte[var0.length];
            Arrays.fill(var2, (byte)-1);
            if (Arrays.equals(var2, var0)) {
               throw new IllegalArgumentException("0xFF Id.");
            }
         }
      }
   }

   public static SnmpEngineId createEngineId(byte[] var0) throws IllegalArgumentException {
      if (var0 != null && var0.length != 0) {
         validateId(var0);
         return new SnmpEngineId(var0);
      } else {
         return null;
      }
   }

   public static SnmpEngineId createEngineId() {
      Object var0 = null;
      byte[] var1 = new byte[13];
      byte var2 = 42;
      long var3 = 255L;
      long var5 = System.currentTimeMillis();
      var1[0] = (byte)((var2 & -16777216) >> 24);
      var1[0] = (byte)(var1[0] | 128);
      var1[1] = (byte)((var2 & 16711680) >> 16);
      var1[2] = (byte)((var2 & '\uff00') >> 8);
      var1[3] = (byte)(var2 & 255);
      var1[4] = 5;
      var1[5] = (byte)((int)((var5 & var3 << 56) >>> 56));
      var1[6] = (byte)((int)((var5 & var3 << 48) >>> 48));
      var1[7] = (byte)((int)((var5 & var3 << 40) >>> 40));
      var1[8] = (byte)((int)((var5 & var3 << 32) >>> 32));
      var1[9] = (byte)((int)((var5 & var3 << 24) >>> 24));
      var1[10] = (byte)((int)((var5 & var3 << 16) >>> 16));
      var1[11] = (byte)((int)((var5 & var3 << 8) >>> 8));
      var1[12] = (byte)((int)(var5 & var3));
      return new SnmpEngineId(var1);
   }

   public SnmpOid toOid() {
      long[] var1 = new long[this.engineId.length + 1];
      var1[0] = (long)this.engineId.length;

      for(int var2 = 1; var2 <= this.engineId.length; ++var2) {
         var1[var2] = (long)(this.engineId[var2 - 1] & 255);
      }

      return new SnmpOid(var1);
   }

   public static SnmpEngineId createEngineId(String var0) throws IllegalArgumentException, UnknownHostException {
      return createEngineId(var0, (String)null);
   }

   public static SnmpEngineId createEngineId(String var0, String var1) throws IllegalArgumentException, UnknownHostException {
      if (var0 == null) {
         return null;
      } else if (!var0.startsWith("0x") && !var0.startsWith("0X")) {
         var1 = var1 == null ? ":" : var1;
         StringTokenizer var2 = new StringTokenizer(var0, var1, true);
         String var3 = null;
         String var4 = null;
         String var5 = null;
         int var6 = 161;
         int var7 = 42;
         InetAddress var8 = null;
         SnmpEngineId var9 = null;

         try {
            try {
               var3 = var2.nextToken();
            } catch (NoSuchElementException var15) {
               throw new IllegalArgumentException("Passed string is invalid : [" + var0 + "]");
            }

            if (!var3.equals(var1)) {
               var8 = InetAddress.getByName(var3);

               try {
                  var2.nextToken();
               } catch (NoSuchElementException var14) {
                  var9 = createEngineId(var8, var6, var7);
                  var9.setStringValue(var0);
                  return var9;
               }
            } else {
               var8 = InetAddress.getLocalHost();
            }

            try {
               var4 = var2.nextToken();
            } catch (NoSuchElementException var13) {
               var9 = createEngineId(var8, var6, var7);
               var9.setStringValue(var0);
               return var9;
            }

            if (!var4.equals(var1)) {
               var6 = Integer.parseInt(var4);

               try {
                  var2.nextToken();
               } catch (NoSuchElementException var12) {
                  var9 = createEngineId(var8, var6, var7);
                  var9.setStringValue(var0);
                  return var9;
               }
            }

            try {
               var5 = var2.nextToken();
            } catch (NoSuchElementException var11) {
               var9 = createEngineId(var8, var6, var7);
               var9.setStringValue(var0);
               return var9;
            }

            if (!var5.equals(var1)) {
               var7 = Integer.parseInt(var5);
            }

            var9 = createEngineId(var8, var6, var7);
            var9.setStringValue(var0);
            return var9;
         } catch (Exception var16) {
            throw new IllegalArgumentException("Passed string is invalid : [" + var0 + "]. Check that the used separator [" + var1 + "] is compatible with IPv6 address format.");
         }
      } else {
         validateId(var0);
         return new SnmpEngineId(var0);
      }
   }

   public static SnmpEngineId createEngineId(int var0) throws UnknownHostException {
      byte var1 = 42;
      InetAddress var2 = null;
      var2 = InetAddress.getLocalHost();
      return createEngineId(var2, var0, var1);
   }

   public static SnmpEngineId createEngineId(InetAddress var0, int var1) throws IllegalArgumentException {
      byte var2 = 42;
      if (var0 == null) {
         throw new IllegalArgumentException("InetAddress is null.");
      } else {
         return createEngineId(var0, var1, var2);
      }
   }

   public static SnmpEngineId createEngineId(int var0, int var1) throws UnknownHostException {
      InetAddress var2 = null;
      var2 = InetAddress.getLocalHost();
      return createEngineId(var2, var0, var1);
   }

   public static SnmpEngineId createEngineId(InetAddress var0, int var1, int var2) {
      if (var0 == null) {
         throw new IllegalArgumentException("InetAddress is null.");
      } else {
         byte[] var3 = var0.getAddress();
         byte[] var4 = new byte[9 + var3.length];
         var4[0] = (byte)((var2 & -16777216) >> 24);
         var4[0] = (byte)(var4[0] | 128);
         var4[1] = (byte)((var2 & 16711680) >> 16);
         var4[2] = (byte)((var2 & '\uff00') >> 8);
         var4[3] = (byte)(var2 & 255);
         var4[4] = 5;
         if (var3.length == 4) {
            var4[4] = 1;
         }

         if (var3.length == 16) {
            var4[4] = 2;
         }

         for(int var5 = 0; var5 < var3.length; ++var5) {
            var4[var5 + 5] = var3[var5];
         }

         var4[5 + var3.length] = (byte)((var1 & -16777216) >> 24);
         var4[6 + var3.length] = (byte)((var1 & 16711680) >> 16);
         var4[7 + var3.length] = (byte)((var1 & '\uff00') >> 8);
         var4[8 + var3.length] = (byte)(var1 & 255);
         return new SnmpEngineId(var4);
      }
   }

   public static SnmpEngineId createEngineId(int var0, InetAddress var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("InetAddress is null.");
      } else {
         byte[] var2 = var1.getAddress();
         byte[] var3 = new byte[5 + var2.length];
         var3[0] = (byte)((var0 & -16777216) >> 24);
         var3[0] = (byte)(var3[0] | 128);
         var3[1] = (byte)((var0 & 16711680) >> 16);
         var3[2] = (byte)((var0 & '\uff00') >> 8);
         var3[3] = (byte)(var0 & 255);
         if (var2.length == 4) {
            var3[4] = 1;
         }

         if (var2.length == 16) {
            var3[4] = 2;
         }

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4 + 5] = var2[var4];
         }

         return new SnmpEngineId(var3);
      }
   }

   public static SnmpEngineId createEngineId(InetAddress var0) {
      return createEngineId(42, var0);
   }

   public boolean equals(Object var1) {
      return !(var1 instanceof SnmpEngineId) ? false : this.hexString.equals(((SnmpEngineId)var1).toString());
   }

   public int hashCode() {
      return this.hexString.hashCode();
   }
}
