package com.sun.jndi.ldap.sasl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

class SaslOutputStream extends FilterOutputStream {
   private static final boolean debug = false;
   private byte[] lenBuf = new byte[4];
   private int rawSendSize = 65536;
   private SaslClient sc;

   SaslOutputStream(SaslClient var1, OutputStream var2) throws SaslException {
      super(var2);
      this.sc = var1;
      String var3 = (String)var1.getNegotiatedProperty("javax.security.sasl.rawsendsize");
      if (var3 != null) {
         try {
            this.rawSendSize = Integer.parseInt(var3);
         } catch (NumberFormatException var5) {
            throw new SaslException("javax.security.sasl.rawsendsize property must be numeric string: " + var3);
         }
      }

   }

   public void write(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)var1};
      this.write(var2, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      for(int var7 = 0; var7 < var3; var7 += this.rawSendSize) {
         int var4 = var3 - var7 < this.rawSendSize ? var3 - var7 : this.rawSendSize;
         byte[] var5 = this.sc.wrap(var1, var2 + var7, var4);
         intToNetworkByteOrder(var5.length, this.lenBuf, 0, 4);
         this.out.write(this.lenBuf, 0, 4);
         this.out.write(var5, 0, var5.length);
      }

   }

   public void close() throws IOException {
      SaslException var1 = null;

      try {
         this.sc.dispose();
      } catch (SaslException var3) {
         var1 = var3;
      }

      super.close();
      if (var1 != null) {
         throw var1;
      }
   }

   private static void intToNetworkByteOrder(int var0, byte[] var1, int var2, int var3) {
      if (var3 > 4) {
         throw new IllegalArgumentException("Cannot handle more than 4 bytes");
      } else {
         for(int var4 = var3 - 1; var4 >= 0; --var4) {
            var1[var2 + var4] = (byte)(var0 & 255);
            var0 >>>= 8;
         }

      }
   }
}
