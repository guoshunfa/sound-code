package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerDecoder extends Ber {
   private int origOffset;

   public BerDecoder(byte[] var1, int var2, int var3) {
      this.buf = var1;
      this.bufsize = var3;
      this.origOffset = var2;
      this.reset();
   }

   public void reset() {
      this.offset = this.origOffset;
   }

   public int getParsePosition() {
      return this.offset;
   }

   public int parseLength() throws Ber.DecodeException {
      int var1 = this.parseByte();
      if ((var1 & 128) != 128) {
         return var1;
      } else {
         var1 &= 127;
         if (var1 == 0) {
            throw new Ber.DecodeException("Indefinite length not supported");
         } else if (var1 > 4) {
            throw new Ber.DecodeException("encoding too long");
         } else if (this.bufsize - this.offset < var1) {
            throw new Ber.DecodeException("Insufficient data");
         } else {
            int var2 = 0;

            for(int var3 = 0; var3 < var1; ++var3) {
               var2 = (var2 << 8) + (this.buf[this.offset++] & 255);
            }

            if (var2 < 0) {
               throw new Ber.DecodeException("Invalid length bytes");
            } else {
               return var2;
            }
         }
      }
   }

   public int parseSeq(int[] var1) throws Ber.DecodeException {
      int var2 = this.parseByte();
      int var3 = this.parseLength();
      if (var1 != null) {
         var1[0] = var3;
      }

      return var2;
   }

   void seek(int var1) throws Ber.DecodeException {
      if (this.offset + var1 <= this.bufsize && this.offset + var1 >= 0) {
         this.offset += var1;
      } else {
         throw new Ber.DecodeException("array index out of bounds");
      }
   }

   public int parseByte() throws Ber.DecodeException {
      if (this.bufsize - this.offset < 1) {
         throw new Ber.DecodeException("Insufficient data");
      } else {
         return this.buf[this.offset++] & 255;
      }
   }

   public int peekByte() throws Ber.DecodeException {
      if (this.bufsize - this.offset < 1) {
         throw new Ber.DecodeException("Insufficient data");
      } else {
         return this.buf[this.offset] & 255;
      }
   }

   public boolean parseBoolean() throws Ber.DecodeException {
      return this.parseIntWithTag(1) != 0;
   }

   public int parseEnumeration() throws Ber.DecodeException {
      return this.parseIntWithTag(10);
   }

   public int parseInt() throws Ber.DecodeException {
      return this.parseIntWithTag(2);
   }

   private int parseIntWithTag(int var1) throws Ber.DecodeException {
      if (this.parseByte() != var1) {
         throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(this.buf[this.offset - 1] & 255) + " (expected tag " + Integer.toString(var1) + ")");
      } else {
         int var2 = this.parseLength();
         if (var2 > 4) {
            throw new Ber.DecodeException("INTEGER too long");
         } else if (var2 > this.bufsize - this.offset) {
            throw new Ber.DecodeException("Insufficient data");
         } else {
            byte var3 = this.buf[this.offset++];
            boolean var4 = false;
            int var6 = var3 & 127;

            for(int var5 = 1; var5 < var2; ++var5) {
               var6 <<= 8;
               var6 |= this.buf[this.offset++] & 255;
            }

            if ((var3 & 128) == 128) {
               var6 = -var6;
            }

            return var6;
         }
      }
   }

   public String parseString(boolean var1) throws Ber.DecodeException {
      return this.parseStringWithTag(4, var1, (int[])null);
   }

   public String parseStringWithTag(int var1, boolean var2, int[] var3) throws Ber.DecodeException {
      int var5 = this.offset;
      int var4;
      if ((var4 = this.parseByte()) != var1) {
         throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString((byte)var4) + " (expected tag " + var1 + ")");
      } else {
         int var6 = this.parseLength();
         if (var6 > this.bufsize - this.offset) {
            throw new Ber.DecodeException("Insufficient data");
         } else {
            String var7;
            if (var6 == 0) {
               var7 = "";
            } else {
               byte[] var8 = new byte[var6];
               System.arraycopy(this.buf, this.offset, var8, 0, var6);
               if (var2) {
                  try {
                     var7 = new String(var8, "UTF8");
                  } catch (UnsupportedEncodingException var11) {
                     throw new Ber.DecodeException("UTF8 not available on platform");
                  }
               } else {
                  try {
                     var7 = new String(var8, "8859_1");
                  } catch (UnsupportedEncodingException var10) {
                     throw new Ber.DecodeException("8859_1 not available on platform");
                  }
               }

               this.offset += var6;
            }

            if (var3 != null) {
               var3[0] = this.offset - var5;
            }

            return var7;
         }
      }
   }

   public byte[] parseOctetString(int var1, int[] var2) throws Ber.DecodeException {
      int var3 = this.offset;
      int var4;
      if ((var4 = this.parseByte()) != var1) {
         throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(var4) + " (expected tag " + Integer.toString(var1) + ")");
      } else {
         int var5 = this.parseLength();
         if (var5 > this.bufsize - this.offset) {
            throw new Ber.DecodeException("Insufficient data");
         } else {
            byte[] var6 = new byte[var5];
            if (var5 > 0) {
               System.arraycopy(this.buf, this.offset, var6, 0, var5);
               this.offset += var5;
            }

            if (var2 != null) {
               var2[0] = this.offset - var3;
            }

            return var6;
         }
      }
   }

   public int bytesLeft() {
      return this.bufsize - this.offset;
   }
}
