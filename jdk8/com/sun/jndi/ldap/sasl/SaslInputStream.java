package com.sun.jndi.ldap.sasl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class SaslInputStream extends InputStream {
   private static final boolean debug = false;
   private byte[] saslBuffer;
   private byte[] lenBuf = new byte[4];
   private byte[] buf = new byte[0];
   private int bufPos = 0;
   private InputStream in;
   private SaslClient sc;
   private int recvMaxBufSize = 65536;

   SaslInputStream(SaslClient var1, InputStream var2) throws SaslException {
      this.in = var2;
      this.sc = var1;
      String var3 = (String)var1.getNegotiatedProperty("javax.security.sasl.maxbuffer");
      if (var3 != null) {
         try {
            this.recvMaxBufSize = Integer.parseInt(var3);
         } catch (NumberFormatException var5) {
            throw new SaslException("javax.security.sasl.maxbuffer property must be numeric string: " + var3);
         }
      }

      this.saslBuffer = new byte[this.recvMaxBufSize];
   }

   public int read() throws IOException {
      byte[] var1 = new byte[1];
      int var2 = this.read(var1, 0, 1);
      return var2 > 0 ? var1[0] : -1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4;
      if (this.bufPos >= this.buf.length) {
         for(var4 = this.fill(); var4 == 0; var4 = this.fill()) {
         }

         if (var4 == -1) {
            return -1;
         }
      }

      var4 = this.buf.length - this.bufPos;
      if (var3 > var4) {
         System.arraycopy(this.buf, this.bufPos, var1, var2, var4);
         this.bufPos = this.buf.length;
         return var4;
      } else {
         System.arraycopy(this.buf, this.bufPos, var1, var2, var3);
         this.bufPos += var3;
         return var3;
      }
   }

   private int fill() throws IOException {
      int var1 = this.readFully(this.lenBuf, 4);
      if (var1 != 4) {
         return -1;
      } else {
         int var2 = networkByteOrderToInt(this.lenBuf, 0, 4);
         if (var2 > this.recvMaxBufSize) {
            throw new IOException(var2 + "exceeds the negotiated receive buffer size limit:" + this.recvMaxBufSize);
         } else {
            var1 = this.readFully(this.saslBuffer, var2);
            if (var1 != var2) {
               throw new EOFException("Expecting to read " + var2 + " bytes but got " + var1 + " bytes before EOF");
            } else {
               this.buf = this.sc.unwrap(this.saslBuffer, 0, var2);
               this.bufPos = 0;
               return this.buf.length;
            }
         }
      }
   }

   private int readFully(byte[] var1, int var2) throws IOException {
      int var3;
      int var4;
      for(var4 = 0; var2 > 0; var2 -= var3) {
         var3 = this.in.read(var1, var4, var2);
         if (var3 == -1) {
            return var4 == 0 ? -1 : var4;
         }

         var4 += var3;
      }

      return var4;
   }

   public int available() throws IOException {
      return this.buf.length - this.bufPos;
   }

   public void close() throws IOException {
      SaslException var1 = null;

      try {
         this.sc.dispose();
      } catch (SaslException var3) {
         var1 = var3;
      }

      this.in.close();
      if (var1 != null) {
         throw var1;
      }
   }

   private static int networkByteOrderToInt(byte[] var0, int var1, int var2) {
      if (var2 > 4) {
         throw new IllegalArgumentException("Cannot handle more than 4 bytes");
      } else {
         int var3 = 0;

         for(int var4 = 0; var4 < var2; ++var4) {
            var3 <<= 8;
            var3 |= var0[var1 + var4] & 255;
         }

         return var3;
      }
   }
}
