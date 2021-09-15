package com.sun.security.ntlm;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public final class Client extends NTLM {
   private final String hostname;
   private final String username;
   private String domain;
   private byte[] pw1;
   private byte[] pw2;

   public Client(String var1, String var2, String var3, String var4, char[] var5) throws NTLMException {
      super(var1);
      if (var3 != null && var5 != null) {
         this.hostname = var2;
         this.username = var3;
         this.domain = var4 == null ? "" : var4;
         this.pw1 = getP1(var5);
         this.pw2 = getP2(var5);
         this.debug("NTLM Client: (h,u,t,version(v)) = (%s,%s,%s,%s(%s))\n", new Object[]{var2, var3, var4, var1, this.v.toString()});
      } else {
         throw new NTLMException(6, "username/password cannot be null");
      }
   }

   public byte[] type1() {
      NTLM.Writer var1 = new NTLM.Writer(1, 32);
      int var2 = 33287;
      if (this.v != Version.NTLM) {
         var2 |= 524288;
      }

      var1.writeInt(12, var2);
      this.debug("NTLM Client: Type 1 created\n", new Object[0]);
      this.debug(var1.getBytes());
      return var1.getBytes();
   }

   public byte[] type3(byte[] var1, byte[] var2) throws NTLMException {
      if (var1 == null || this.v != Version.NTLM && var2 == null) {
         throw new NTLMException(6, "type2 and nonce cannot be null");
      } else {
         this.debug("NTLM Client: Type 2 received\n", new Object[0]);
         this.debug(var1);
         NTLM.Reader var3 = new NTLM.Reader(var1);
         byte[] var4 = var3.readBytes(24, 8);
         int var5 = var3.readInt(20);
         boolean var6 = (var5 & 1) == 1;
         int var7 = 557568 | var5 & 3;
         NTLM.Writer var8 = new NTLM.Writer(3, 64);
         byte[] var9 = null;
         byte[] var10 = null;
         var8.writeSecurityBuffer(28, this.domain, var6);
         var8.writeSecurityBuffer(36, this.username, var6);
         var8.writeSecurityBuffer(44, this.hostname, var6);
         byte[] var11;
         byte[] var12;
         if (this.v == Version.NTLM) {
            var11 = this.calcLMHash(this.pw1);
            var12 = this.calcNTHash(this.pw2);
            if (this.writeLM) {
               var9 = this.calcResponse(var11, var4);
            }

            if (this.writeNTLM) {
               var10 = this.calcResponse(var12, var4);
            }
         } else if (this.v == Version.NTLM2) {
            var11 = this.calcNTHash(this.pw2);
            var9 = ntlm2LM(var2);
            var10 = this.ntlm2NTLM(var11, var2, var4);
         } else {
            var11 = this.calcNTHash(this.pw2);
            if (this.writeLM) {
               var9 = this.calcV2(var11, this.username.toUpperCase(Locale.US) + this.domain, var2, var4);
            }

            if (this.writeNTLM) {
               var12 = (var5 & 8388608) != 0 ? var3.readSecurityBuffer(40) : new byte[0];
               byte[] var13 = new byte[32 + var12.length];
               System.arraycopy(new byte[]{1, 1, 0, 0, 0, 0, 0, 0}, 0, var13, 0, 8);
               byte[] var14 = BigInteger.valueOf((new Date()).getTime()).add(new BigInteger("11644473600000")).multiply(BigInteger.valueOf(10000L)).toByteArray();

               for(int var15 = 0; var15 < var14.length; ++var15) {
                  var13[8 + var14.length - var15 - 1] = var14[var15];
               }

               System.arraycopy(var2, 0, var13, 16, 8);
               System.arraycopy(new byte[]{0, 0, 0, 0}, 0, var13, 24, 4);
               System.arraycopy(var12, 0, var13, 28, var12.length);
               System.arraycopy(new byte[]{0, 0, 0, 0}, 0, var13, 28 + var12.length, 4);
               var10 = this.calcV2(var11, this.username.toUpperCase(Locale.US) + this.domain, var13, var4);
            }
         }

         var8.writeSecurityBuffer(12, var9);
         var8.writeSecurityBuffer(20, var10);
         var8.writeSecurityBuffer(52, new byte[0]);
         var8.writeInt(60, var7);
         this.debug("NTLM Client: Type 3 created\n", new Object[0]);
         this.debug(var8.getBytes());
         return var8.getBytes();
      }
   }

   public String getDomain() {
      return this.domain;
   }

   public void dispose() {
      Arrays.fill((byte[])this.pw1, (byte)0);
      Arrays.fill((byte[])this.pw2, (byte)0);
   }
}
