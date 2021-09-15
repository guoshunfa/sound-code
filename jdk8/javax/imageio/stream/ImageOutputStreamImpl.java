package javax.imageio.stream;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteOrder;

public abstract class ImageOutputStreamImpl extends ImageInputStreamImpl implements ImageOutputStream {
   public abstract void write(int var1) throws IOException;

   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public abstract void write(byte[] var1, int var2, int var3) throws IOException;

   public void writeBoolean(boolean var1) throws IOException {
      this.write(var1 ? 1 : 0);
   }

   public void writeByte(int var1) throws IOException {
      this.write(var1);
   }

   public void writeShort(int var1) throws IOException {
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         this.byteBuf[0] = (byte)(var1 >>> 8);
         this.byteBuf[1] = (byte)(var1 >>> 0);
      } else {
         this.byteBuf[0] = (byte)(var1 >>> 0);
         this.byteBuf[1] = (byte)(var1 >>> 8);
      }

      this.write(this.byteBuf, 0, 2);
   }

   public void writeChar(int var1) throws IOException {
      this.writeShort(var1);
   }

   public void writeInt(int var1) throws IOException {
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         this.byteBuf[0] = (byte)(var1 >>> 24);
         this.byteBuf[1] = (byte)(var1 >>> 16);
         this.byteBuf[2] = (byte)(var1 >>> 8);
         this.byteBuf[3] = (byte)(var1 >>> 0);
      } else {
         this.byteBuf[0] = (byte)(var1 >>> 0);
         this.byteBuf[1] = (byte)(var1 >>> 8);
         this.byteBuf[2] = (byte)(var1 >>> 16);
         this.byteBuf[3] = (byte)(var1 >>> 24);
      }

      this.write(this.byteBuf, 0, 4);
   }

   public void writeLong(long var1) throws IOException {
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         this.byteBuf[0] = (byte)((int)(var1 >>> 56));
         this.byteBuf[1] = (byte)((int)(var1 >>> 48));
         this.byteBuf[2] = (byte)((int)(var1 >>> 40));
         this.byteBuf[3] = (byte)((int)(var1 >>> 32));
         this.byteBuf[4] = (byte)((int)(var1 >>> 24));
         this.byteBuf[5] = (byte)((int)(var1 >>> 16));
         this.byteBuf[6] = (byte)((int)(var1 >>> 8));
         this.byteBuf[7] = (byte)((int)(var1 >>> 0));
      } else {
         this.byteBuf[0] = (byte)((int)(var1 >>> 0));
         this.byteBuf[1] = (byte)((int)(var1 >>> 8));
         this.byteBuf[2] = (byte)((int)(var1 >>> 16));
         this.byteBuf[3] = (byte)((int)(var1 >>> 24));
         this.byteBuf[4] = (byte)((int)(var1 >>> 32));
         this.byteBuf[5] = (byte)((int)(var1 >>> 40));
         this.byteBuf[6] = (byte)((int)(var1 >>> 48));
         this.byteBuf[7] = (byte)((int)(var1 >>> 56));
      }

      this.write(this.byteBuf, 0, 4);
      this.write(this.byteBuf, 4, 4);
   }

   public void writeFloat(float var1) throws IOException {
      this.writeInt(Float.floatToIntBits(var1));
   }

   public void writeDouble(double var1) throws IOException {
      this.writeLong(Double.doubleToLongBits(var1));
   }

   public void writeBytes(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.write((byte)var1.charAt(var3));
      }

   }

   public void writeChars(String var1) throws IOException {
      int var2 = var1.length();
      byte[] var3 = new byte[var2 * 2];
      int var4 = 0;
      int var5;
      char var6;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var5 = 0; var5 < var2; ++var5) {
            var6 = var1.charAt(var5);
            var3[var4++] = (byte)(var6 >>> 8);
            var3[var4++] = (byte)(var6 >>> 0);
         }
      } else {
         for(var5 = 0; var5 < var2; ++var5) {
            var6 = var1.charAt(var5);
            var3[var4++] = (byte)(var6 >>> 0);
            var3[var4++] = (byte)(var6 >>> 8);
         }
      }

      this.write(var3, 0, var2 * 2);
   }

   public void writeUTF(String var1) throws IOException {
      int var2 = var1.length();
      int var3 = 0;
      char[] var4 = new char[var2];
      byte var6 = 0;
      var1.getChars(0, var2, var4, 0);

      char var5;
      for(int var7 = 0; var7 < var2; ++var7) {
         var5 = var4[var7];
         if (var5 >= 1 && var5 <= 127) {
            ++var3;
         } else if (var5 > 2047) {
            var3 += 3;
         } else {
            var3 += 2;
         }
      }

      if (var3 > 65535) {
         throw new UTFDataFormatException("utflen > 65536!");
      } else {
         byte[] var10 = new byte[var3 + 2];
         int var9 = var6 + 1;
         var10[var6] = (byte)(var3 >>> 8 & 255);
         var10[var9++] = (byte)(var3 >>> 0 & 255);

         for(int var8 = 0; var8 < var2; ++var8) {
            var5 = var4[var8];
            if (var5 >= 1 && var5 <= 127) {
               var10[var9++] = (byte)var5;
            } else if (var5 > 2047) {
               var10[var9++] = (byte)(224 | var5 >> 12 & 15);
               var10[var9++] = (byte)(128 | var5 >> 6 & 63);
               var10[var9++] = (byte)(128 | var5 >> 0 & 63);
            } else {
               var10[var9++] = (byte)(192 | var5 >> 6 & 31);
               var10[var9++] = (byte)(128 | var5 >> 0 & 63);
            }
         }

         this.write(var10, 0, var3 + 2);
      }
   }

   public void writeShorts(short[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         byte[] var4 = new byte[var3 * 2];
         int var5 = 0;
         int var6;
         short var7;
         if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)(var7 >>> 8);
               var4[var5++] = (byte)(var7 >>> 0);
            }
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)(var7 >>> 0);
               var4[var5++] = (byte)(var7 >>> 8);
            }
         }

         this.write(var4, 0, var3 * 2);
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
      }
   }

   public void writeChars(char[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         byte[] var4 = new byte[var3 * 2];
         int var5 = 0;
         int var6;
         char var7;
         if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)(var7 >>> 8);
               var4[var5++] = (byte)(var7 >>> 0);
            }
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)(var7 >>> 0);
               var4[var5++] = (byte)(var7 >>> 8);
            }
         }

         this.write(var4, 0, var3 * 2);
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
      }
   }

   public void writeInts(int[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         byte[] var4 = new byte[var3 * 4];
         int var5 = 0;
         int var6;
         int var7;
         if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)(var7 >>> 24);
               var4[var5++] = (byte)(var7 >>> 16);
               var4[var5++] = (byte)(var7 >>> 8);
               var4[var5++] = (byte)(var7 >>> 0);
            }
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)(var7 >>> 0);
               var4[var5++] = (byte)(var7 >>> 8);
               var4[var5++] = (byte)(var7 >>> 16);
               var4[var5++] = (byte)(var7 >>> 24);
            }
         }

         this.write(var4, 0, var3 * 4);
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
      }
   }

   public void writeLongs(long[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         byte[] var4 = new byte[var3 * 8];
         int var5 = 0;
         int var6;
         long var7;
         if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)((int)(var7 >>> 56));
               var4[var5++] = (byte)((int)(var7 >>> 48));
               var4[var5++] = (byte)((int)(var7 >>> 40));
               var4[var5++] = (byte)((int)(var7 >>> 32));
               var4[var5++] = (byte)((int)(var7 >>> 24));
               var4[var5++] = (byte)((int)(var7 >>> 16));
               var4[var5++] = (byte)((int)(var7 >>> 8));
               var4[var5++] = (byte)((int)(var7 >>> 0));
            }
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = var1[var2 + var6];
               var4[var5++] = (byte)((int)(var7 >>> 0));
               var4[var5++] = (byte)((int)(var7 >>> 8));
               var4[var5++] = (byte)((int)(var7 >>> 16));
               var4[var5++] = (byte)((int)(var7 >>> 24));
               var4[var5++] = (byte)((int)(var7 >>> 32));
               var4[var5++] = (byte)((int)(var7 >>> 40));
               var4[var5++] = (byte)((int)(var7 >>> 48));
               var4[var5++] = (byte)((int)(var7 >>> 56));
            }
         }

         this.write(var4, 0, var3 * 8);
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
      }
   }

   public void writeFloats(float[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         byte[] var4 = new byte[var3 * 4];
         int var5 = 0;
         int var6;
         int var7;
         if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = Float.floatToIntBits(var1[var2 + var6]);
               var4[var5++] = (byte)(var7 >>> 24);
               var4[var5++] = (byte)(var7 >>> 16);
               var4[var5++] = (byte)(var7 >>> 8);
               var4[var5++] = (byte)(var7 >>> 0);
            }
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = Float.floatToIntBits(var1[var2 + var6]);
               var4[var5++] = (byte)(var7 >>> 0);
               var4[var5++] = (byte)(var7 >>> 8);
               var4[var5++] = (byte)(var7 >>> 16);
               var4[var5++] = (byte)(var7 >>> 24);
            }
         }

         this.write(var4, 0, var3 * 4);
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
      }
   }

   public void writeDoubles(double[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         byte[] var4 = new byte[var3 * 8];
         int var5 = 0;
         int var6;
         long var7;
         if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = Double.doubleToLongBits(var1[var2 + var6]);
               var4[var5++] = (byte)((int)(var7 >>> 56));
               var4[var5++] = (byte)((int)(var7 >>> 48));
               var4[var5++] = (byte)((int)(var7 >>> 40));
               var4[var5++] = (byte)((int)(var7 >>> 32));
               var4[var5++] = (byte)((int)(var7 >>> 24));
               var4[var5++] = (byte)((int)(var7 >>> 16));
               var4[var5++] = (byte)((int)(var7 >>> 8));
               var4[var5++] = (byte)((int)(var7 >>> 0));
            }
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var7 = Double.doubleToLongBits(var1[var2 + var6]);
               var4[var5++] = (byte)((int)(var7 >>> 0));
               var4[var5++] = (byte)((int)(var7 >>> 8));
               var4[var5++] = (byte)((int)(var7 >>> 16));
               var4[var5++] = (byte)((int)(var7 >>> 24));
               var4[var5++] = (byte)((int)(var7 >>> 32));
               var4[var5++] = (byte)((int)(var7 >>> 40));
               var4[var5++] = (byte)((int)(var7 >>> 48));
               var4[var5++] = (byte)((int)(var7 >>> 56));
            }
         }

         this.write(var4, 0, var3 * 8);
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
      }
   }

   public void writeBit(int var1) throws IOException {
      this.writeBits(1L & (long)var1, 1);
   }

   public void writeBits(long var1, int var3) throws IOException {
      this.checkClosed();
      if (var3 >= 0 && var3 <= 64) {
         if (var3 != 0) {
            int var4;
            int var5;
            int var6;
            int var7;
            if (this.getStreamPosition() > 0L || this.bitOffset > 0) {
               var4 = this.bitOffset;
               var5 = this.read();
               if (var5 != -1) {
                  this.seek(this.getStreamPosition() - 1L);
               } else {
                  var5 = 0;
               }

               if (var3 + var4 < 8) {
                  var6 = 8 - (var4 + var3);
                  var7 = -1 >>> 32 - var3;
                  var5 &= ~(var7 << var6);
                  var5 = (int)((long)var5 | (var1 & (long)var7) << var6);
                  this.write(var5);
                  this.seek(this.getStreamPosition() - 1L);
                  this.bitOffset = var4 + var3;
                  var3 = 0;
               } else {
                  var6 = 8 - var4;
                  var7 = -1 >>> 32 - var6;
                  var5 &= ~var7;
                  var5 = (int)((long)var5 | var1 >> var3 - var6 & (long)var7);
                  this.write(var5);
                  var3 -= var6;
               }
            }

            if (var3 > 7) {
               var4 = var3 % 8;

               for(var5 = var3 / 8; var5 > 0; --var5) {
                  var6 = (var5 - 1) * 8 + var4;
                  var7 = (int)(var6 == 0 ? var1 & 255L : var1 >> var6 & 255L);
                  this.write(var7);
               }

               var3 = var4;
            }

            if (var3 != 0) {
               boolean var8 = false;
               var4 = this.read();
               if (var4 != -1) {
                  this.seek(this.getStreamPosition() - 1L);
               } else {
                  var4 = 0;
               }

               var5 = 8 - var3;
               var6 = -1 >>> 32 - var3;
               var4 &= ~(var6 << var5);
               var4 = (int)((long)var4 | (var1 & (long)var6) << var5);
               this.write(var4);
               this.seek(this.getStreamPosition() - 1L);
               this.bitOffset = var3;
            }

         }
      } else {
         throw new IllegalArgumentException("Bad value for numBits!");
      }
   }

   protected final void flushBits() throws IOException {
      this.checkClosed();
      if (this.bitOffset != 0) {
         int var1 = this.bitOffset;
         int var2 = this.read();
         if (var2 < 0) {
            var2 = 0;
            this.bitOffset = 0;
         } else {
            this.seek(this.getStreamPosition() - 1L);
            var2 &= -1 << 8 - var1;
         }

         this.write(var2);
      }

   }
}
