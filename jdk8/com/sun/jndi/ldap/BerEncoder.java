package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerEncoder extends Ber {
   private int curSeqIndex;
   private int[] seqOffset;
   private static final int INITIAL_SEQUENCES = 16;
   private static final int DEFAULT_BUFSIZE = 1024;
   private static final int BUF_GROWTH_FACTOR = 8;

   public BerEncoder() {
      this(1024);
   }

   public BerEncoder(int var1) {
      this.buf = new byte[var1];
      this.bufsize = var1;
      this.offset = 0;
      this.seqOffset = new int[16];
      this.curSeqIndex = 0;
   }

   public void reset() {
      while(this.offset > 0) {
         this.buf[--this.offset] = 0;
      }

      while(this.curSeqIndex > 0) {
         this.seqOffset[--this.curSeqIndex] = 0;
      }

   }

   public int getDataLen() {
      return this.offset;
   }

   public byte[] getBuf() {
      if (this.curSeqIndex != 0) {
         throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs.");
      } else {
         return this.buf;
      }
   }

   public byte[] getTrimmedBuf() {
      int var1 = this.getDataLen();
      byte[] var2 = new byte[var1];
      System.arraycopy(this.getBuf(), 0, var2, 0, var1);
      return var2;
   }

   public void beginSeq(int var1) {
      if (this.curSeqIndex >= this.seqOffset.length) {
         int[] var2 = new int[this.seqOffset.length * 2];

         for(int var3 = 0; var3 < this.seqOffset.length; ++var3) {
            var2[var3] = this.seqOffset[var3];
         }

         this.seqOffset = var2;
      }

      this.encodeByte(var1);
      this.seqOffset[this.curSeqIndex] = this.offset;
      this.ensureFreeBytes(3);
      this.offset += 3;
      ++this.curSeqIndex;
   }

   public void endSeq() throws Ber.EncodeException {
      --this.curSeqIndex;
      if (this.curSeqIndex < 0) {
         throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs.");
      } else {
         int var1 = this.seqOffset[this.curSeqIndex] + 3;
         int var2 = this.offset - var1;
         if (var2 <= 127) {
            this.shiftSeqData(var1, var2, -2);
            this.buf[this.seqOffset[this.curSeqIndex]] = (byte)var2;
         } else if (var2 <= 255) {
            this.shiftSeqData(var1, var2, -1);
            this.buf[this.seqOffset[this.curSeqIndex]] = -127;
            this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)var2;
         } else if (var2 <= 65535) {
            this.buf[this.seqOffset[this.curSeqIndex]] = -126;
            this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)(var2 >> 8);
            this.buf[this.seqOffset[this.curSeqIndex] + 2] = (byte)var2;
         } else {
            if (var2 > 16777215) {
               throw new Ber.EncodeException("SEQUENCE too long");
            }

            this.shiftSeqData(var1, var2, 1);
            this.buf[this.seqOffset[this.curSeqIndex]] = -125;
            this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)(var2 >> 16);
            this.buf[this.seqOffset[this.curSeqIndex] + 2] = (byte)(var2 >> 8);
            this.buf[this.seqOffset[this.curSeqIndex] + 3] = (byte)var2;
         }

      }
   }

   private void shiftSeqData(int var1, int var2, int var3) {
      if (var3 > 0) {
         this.ensureFreeBytes(var3);
      }

      System.arraycopy(this.buf, var1, this.buf, var1 + var3, var2);
      this.offset += var3;
   }

   public void encodeByte(int var1) {
      this.ensureFreeBytes(1);
      this.buf[this.offset++] = (byte)var1;
   }

   public void encodeInt(int var1) {
      this.encodeInt(var1, 2);
   }

   public void encodeInt(int var1, int var2) {
      int var3 = -8388608;

      int var4;
      for(var4 = 4; ((var1 & var3) == 0 || (var1 & var3) == var3) && var4 > 1; var1 <<= 8) {
         --var4;
      }

      this.encodeInt(var1, var2, var4);
   }

   private void encodeInt(int var1, int var2, int var3) {
      if (var3 > 4) {
         throw new IllegalArgumentException("BER encode error: INTEGER too long.");
      } else {
         this.ensureFreeBytes(2 + var3);
         this.buf[this.offset++] = (byte)var2;
         this.buf[this.offset++] = (byte)var3;

         for(int var4 = -16777216; var3-- > 0; var1 <<= 8) {
            this.buf[this.offset++] = (byte)((var1 & var4) >> 24);
         }

      }
   }

   public void encodeBoolean(boolean var1) {
      this.encodeBoolean(var1, 1);
   }

   public void encodeBoolean(boolean var1, int var2) {
      this.ensureFreeBytes(3);
      this.buf[this.offset++] = (byte)var2;
      this.buf[this.offset++] = 1;
      this.buf[this.offset++] = (byte)(var1 ? -1 : 0);
   }

   public void encodeString(String var1, boolean var2) throws Ber.EncodeException {
      this.encodeString(var1, 4, var2);
   }

   public void encodeString(String var1, int var2, boolean var3) throws Ber.EncodeException {
      this.encodeByte(var2);
      int var4 = 0;
      byte[] var6 = null;
      int var5;
      if (var1 == null) {
         var5 = 0;
      } else if (var3) {
         try {
            var6 = var1.getBytes("UTF8");
            var5 = var6.length;
         } catch (UnsupportedEncodingException var9) {
            throw new Ber.EncodeException("UTF8 not available on platform");
         }
      } else {
         try {
            var6 = var1.getBytes("8859_1");
            var5 = var6.length;
         } catch (UnsupportedEncodingException var8) {
            throw new Ber.EncodeException("8859_1 not available on platform");
         }
      }

      this.encodeLength(var5);
      this.ensureFreeBytes(var5);

      while(var4 < var5) {
         this.buf[this.offset++] = var6[var4++];
      }

   }

   public void encodeOctetString(byte[] var1, int var2, int var3, int var4) throws Ber.EncodeException {
      this.encodeByte(var2);
      this.encodeLength(var4);
      if (var4 > 0) {
         this.ensureFreeBytes(var4);
         System.arraycopy(var1, var3, this.buf, this.offset, var4);
         this.offset += var4;
      }

   }

   public void encodeOctetString(byte[] var1, int var2) throws Ber.EncodeException {
      this.encodeOctetString(var1, var2, 0, var1.length);
   }

   private void encodeLength(int var1) throws Ber.EncodeException {
      this.ensureFreeBytes(4);
      if (var1 < 128) {
         this.buf[this.offset++] = (byte)var1;
      } else if (var1 <= 255) {
         this.buf[this.offset++] = -127;
         this.buf[this.offset++] = (byte)var1;
      } else if (var1 <= 65535) {
         this.buf[this.offset++] = -126;
         this.buf[this.offset++] = (byte)(var1 >> 8);
         this.buf[this.offset++] = (byte)(var1 & 255);
      } else {
         if (var1 > 16777215) {
            throw new Ber.EncodeException("string too long");
         }

         this.buf[this.offset++] = -125;
         this.buf[this.offset++] = (byte)(var1 >> 16);
         this.buf[this.offset++] = (byte)(var1 >> 8);
         this.buf[this.offset++] = (byte)(var1 & 255);
      }

   }

   public void encodeStringArray(String[] var1, boolean var2) throws Ber.EncodeException {
      if (var1 != null) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            this.encodeString(var1[var3], var2);
         }

      }
   }

   private void ensureFreeBytes(int var1) {
      if (this.bufsize - this.offset < var1) {
         int var2 = this.bufsize * 8;
         if (var2 - this.offset < var1) {
            var2 += var1;
         }

         byte[] var3 = new byte[var2];
         System.arraycopy(this.buf, 0, var3, 0, this.offset);
         this.buf = var3;
         this.bufsize = var2;
      }

   }
}
