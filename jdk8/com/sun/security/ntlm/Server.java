package com.sun.security.ntlm;

import java.util.Arrays;
import java.util.Locale;

public abstract class Server extends NTLM {
   private final String domain;
   private final boolean allVersion;

   public Server(String var1, String var2) throws NTLMException {
      super(var1);
      if (var2 == null) {
         throw new NTLMException(6, "domain cannot be null");
      } else {
         this.allVersion = var1 == null;
         this.domain = var2;
         this.debug("NTLM Server: (t,version) = (%s,%s)\n", new Object[]{var2, var1});
      }
   }

   public byte[] type2(byte[] var1, byte[] var2) throws NTLMException {
      if (var2 == null) {
         throw new NTLMException(6, "nonce cannot be null");
      } else {
         this.debug("NTLM Server: Type 1 received\n", new Object[0]);
         if (var1 != null) {
            this.debug(var1);
         }

         NTLM.Writer var3 = new NTLM.Writer(2, 32);
         int var4 = 590341;
         var3.writeSecurityBuffer(12, this.domain, true);
         var3.writeInt(20, var4);
         var3.writeBytes(24, var2);
         this.debug("NTLM Server: Type 2 created\n", new Object[0]);
         this.debug(var3.getBytes());
         return var3.getBytes();
      }
   }

   public String[] verify(byte[] var1, byte[] var2) throws NTLMException {
      if (var1 != null && var2 != null) {
         this.debug("NTLM Server: Type 3 received\n", new Object[0]);
         if (var1 != null) {
            this.debug(var1);
         }

         NTLM.Reader var3 = new NTLM.Reader(var1);
         String var4 = var3.readSecurityBuffer(36, true);
         String var5 = var3.readSecurityBuffer(44, true);
         String var6 = var3.readSecurityBuffer(28, true);
         boolean var7 = false;
         char[] var8 = this.getPassword(var6, var4);
         if (var8 == null) {
            throw new NTLMException(3, "Unknown user");
         } else {
            byte[] var9 = var3.readSecurityBuffer(12);
            byte[] var10 = var3.readSecurityBuffer(20);
            byte[] var11;
            byte[] var12;
            byte[] var13;
            if (!var7 && (this.allVersion || this.v == Version.NTLM)) {
               if (var9.length > 0) {
                  var11 = getP1(var8);
                  var12 = this.calcLMHash(var11);
                  var13 = this.calcResponse(var12, var2);
                  if (Arrays.equals(var13, var9)) {
                     var7 = true;
                  }
               }

               if (var10.length > 0) {
                  var11 = getP2(var8);
                  var12 = this.calcNTHash(var11);
                  var13 = this.calcResponse(var12, var2);
                  if (Arrays.equals(var13, var10)) {
                     var7 = true;
                  }
               }

               this.debug("NTLM Server: verify using NTLM: " + var7 + "\n", new Object[0]);
            }

            byte[] var14;
            if (!var7 && (this.allVersion || this.v == Version.NTLM2)) {
               var11 = getP2(var8);
               var12 = this.calcNTHash(var11);
               var13 = Arrays.copyOf((byte[])var9, 8);
               var14 = this.ntlm2NTLM(var12, var13, var2);
               if (Arrays.equals(var10, var14)) {
                  var7 = true;
               }

               this.debug("NTLM Server: verify using NTLM2: " + var7 + "\n", new Object[0]);
            }

            if (!var7 && (this.allVersion || this.v == Version.NTLMv2)) {
               var11 = getP2(var8);
               var12 = this.calcNTHash(var11);
               if (var9.length > 0) {
                  var13 = Arrays.copyOfRange((byte[])var9, 16, var9.length);
                  var14 = this.calcV2(var12, var4.toUpperCase(Locale.US) + var6, var13, var2);
                  if (Arrays.equals(var14, var9)) {
                     var7 = true;
                  }
               }

               if (var10.length > 0) {
                  var13 = Arrays.copyOfRange((byte[])var10, 16, var10.length);
                  var14 = this.calcV2(var12, var4.toUpperCase(Locale.US) + var6, var13, var2);
                  if (Arrays.equals(var14, var10)) {
                     var7 = true;
                  }
               }

               this.debug("NTLM Server: verify using NTLMv2: " + var7 + "\n", new Object[0]);
            }

            if (!var7) {
               throw new NTLMException(4, "None of LM and NTLM verified");
            } else {
               return new String[]{var4, var5, var6};
            }
         }
      } else {
         throw new NTLMException(6, "type1 or nonce cannot be null");
      }
   }

   public abstract char[] getPassword(String var1, String var2);
}
