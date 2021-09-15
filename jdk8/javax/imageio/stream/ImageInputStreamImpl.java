package javax.imageio.stream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Stack;
import javax.imageio.IIOException;

public abstract class ImageInputStreamImpl implements ImageInputStream {
   private Stack markByteStack = new Stack();
   private Stack markBitStack = new Stack();
   private boolean isClosed = false;
   private static final int BYTE_BUF_LENGTH = 8192;
   byte[] byteBuf = new byte[8192];
   protected ByteOrder byteOrder;
   protected long streamPos;
   protected int bitOffset;
   protected long flushedPos;

   public ImageInputStreamImpl() {
      this.byteOrder = ByteOrder.BIG_ENDIAN;
      this.flushedPos = 0L;
   }

   protected final void checkClosed() throws IOException {
      if (this.isClosed) {
         throw new IOException("closed");
      }
   }

   public void setByteOrder(ByteOrder var1) {
      this.byteOrder = var1;
   }

   public ByteOrder getByteOrder() {
      return this.byteOrder;
   }

   public abstract int read() throws IOException;

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public abstract int read(byte[] var1, int var2, int var3) throws IOException;

   public void readBytes(IIOByteBuffer var1, int var2) throws IOException {
      if (var2 < 0) {
         throw new IndexOutOfBoundsException("len < 0!");
      } else if (var1 == null) {
         throw new NullPointerException("buf == null!");
      } else {
         byte[] var3 = new byte[var2];
         var2 = this.read(var3, 0, var2);
         var1.setData(var3);
         var1.setOffset(0);
         var1.setLength(var2);
      }
   }

   public boolean readBoolean() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1 != 0;
      }
   }

   public byte readByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }

   public int readUnsignedByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   public short readShort() throws IOException {
      if (this.read(this.byteBuf, 0, 2) != 2) {
         throw new EOFException();
      } else {
         return this.byteOrder == ByteOrder.BIG_ENDIAN ? (short)((this.byteBuf[0] & 255) << 8 | (this.byteBuf[1] & 255) << 0) : (short)((this.byteBuf[1] & 255) << 8 | (this.byteBuf[0] & 255) << 0);
      }
   }

   public int readUnsignedShort() throws IOException {
      return this.readShort() & '\uffff';
   }

   public char readChar() throws IOException {
      return (char)this.readShort();
   }

   public int readInt() throws IOException {
      if (this.read(this.byteBuf, 0, 4) != 4) {
         throw new EOFException();
      } else {
         return this.byteOrder == ByteOrder.BIG_ENDIAN ? (this.byteBuf[0] & 255) << 24 | (this.byteBuf[1] & 255) << 16 | (this.byteBuf[2] & 255) << 8 | (this.byteBuf[3] & 255) << 0 : (this.byteBuf[3] & 255) << 24 | (this.byteBuf[2] & 255) << 16 | (this.byteBuf[1] & 255) << 8 | (this.byteBuf[0] & 255) << 0;
      }
   }

   public long readUnsignedInt() throws IOException {
      return (long)this.readInt() & 4294967295L;
   }

   public long readLong() throws IOException {
      int var1 = this.readInt();
      int var2 = this.readInt();
      return this.byteOrder == ByteOrder.BIG_ENDIAN ? ((long)var1 << 32) + ((long)var2 & 4294967295L) : ((long)var2 << 32) + ((long)var1 & 4294967295L);
   }

   public float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   public double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   public String readLine() throws IOException {
      StringBuffer var1 = new StringBuffer();
      int var2 = -1;
      boolean var3 = false;

      while(!var3) {
         switch(var2 = this.read()) {
         case -1:
         case 10:
            var3 = true;
            break;
         case 13:
            var3 = true;
            long var4 = this.getStreamPosition();
            if (this.read() != 10) {
               this.seek(var4);
            }
            break;
         default:
            var1.append((char)var2);
         }
      }

      if (var2 == -1 && var1.length() == 0) {
         return null;
      } else {
         return var1.toString();
      }
   }

   public String readUTF() throws IOException {
      this.bitOffset = 0;
      ByteOrder var1 = this.getByteOrder();
      this.setByteOrder(ByteOrder.BIG_ENDIAN);

      String var2;
      try {
         var2 = DataInputStream.readUTF(this);
      } catch (IOException var4) {
         this.setByteOrder(var1);
         throw var4;
      }

      this.setByteOrder(var1);
      return var2;
   }

   public void readFully(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = this.read(var1, var2, var3);
            if (var4 == -1) {
               throw new EOFException();
            }

            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!");
      }
   }

   public void readFully(byte[] var1) throws IOException {
      this.readFully((byte[])var1, 0, var1.length);
   }

   public void readFully(short[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = Math.min(var3, this.byteBuf.length / 2);
            this.readFully((byte[])this.byteBuf, 0, var4 * 2);
            this.toShorts(this.byteBuf, var1, var2, var4);
            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
      }
   }

   public void readFully(char[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = Math.min(var3, this.byteBuf.length / 2);
            this.readFully((byte[])this.byteBuf, 0, var4 * 2);
            this.toChars(this.byteBuf, var1, var2, var4);
            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
      }
   }

   public void readFully(int[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = Math.min(var3, this.byteBuf.length / 4);
            this.readFully((byte[])this.byteBuf, 0, var4 * 4);
            this.toInts(this.byteBuf, var1, var2, var4);
            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
      }
   }

   public void readFully(long[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = Math.min(var3, this.byteBuf.length / 8);
            this.readFully((byte[])this.byteBuf, 0, var4 * 8);
            this.toLongs(this.byteBuf, var1, var2, var4);
            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
      }
   }

   public void readFully(float[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = Math.min(var3, this.byteBuf.length / 4);
            this.readFully((byte[])this.byteBuf, 0, var4 * 4);
            this.toFloats(this.byteBuf, var1, var2, var4);
            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
      }
   }

   public void readFully(double[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         while(var3 > 0) {
            int var4 = Math.min(var3, this.byteBuf.length / 8);
            this.readFully((byte[])this.byteBuf, 0, var4 * 8);
            this.toDoubles(this.byteBuf, var1, var2, var4);
            var2 += var4;
            var3 -= var4;
         }

      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
      }
   }

   private void toShorts(byte[] var1, short[] var2, int var3, int var4) {
      int var5 = 0;
      int var6;
      byte var7;
      int var8;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5];
            var8 = var1[var5 + 1] & 255;
            var2[var3 + var6] = (short)(var7 << 8 | var8);
            var5 += 2;
         }
      } else {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5 + 1];
            var8 = var1[var5] & 255;
            var2[var3 + var6] = (short)(var7 << 8 | var8);
            var5 += 2;
         }
      }

   }

   private void toChars(byte[] var1, char[] var2, int var3, int var4) {
      int var5 = 0;
      int var6;
      byte var7;
      int var8;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5];
            var8 = var1[var5 + 1] & 255;
            var2[var3 + var6] = (char)(var7 << 8 | var8);
            var5 += 2;
         }
      } else {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5 + 1];
            var8 = var1[var5] & 255;
            var2[var3 + var6] = (char)(var7 << 8 | var8);
            var5 += 2;
         }
      }

   }

   private void toInts(byte[] var1, int[] var2, int var3, int var4) {
      int var5 = 0;
      int var6;
      byte var7;
      int var8;
      int var9;
      int var10;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5];
            var8 = var1[var5 + 1] & 255;
            var9 = var1[var5 + 2] & 255;
            var10 = var1[var5 + 3] & 255;
            var2[var3 + var6] = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var5 += 4;
         }
      } else {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5 + 3];
            var8 = var1[var5 + 2] & 255;
            var9 = var1[var5 + 1] & 255;
            var10 = var1[var5] & 255;
            var2[var3 + var6] = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var5 += 4;
         }
      }

   }

   private void toLongs(byte[] var1, long[] var2, int var3, int var4) {
      int var5 = 0;
      int var6;
      byte var7;
      int var8;
      int var9;
      int var10;
      byte var11;
      int var12;
      int var13;
      int var14;
      int var15;
      int var16;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5];
            var8 = var1[var5 + 1] & 255;
            var9 = var1[var5 + 2] & 255;
            var10 = var1[var5 + 3] & 255;
            var11 = var1[var5 + 4];
            var12 = var1[var5 + 5] & 255;
            var13 = var1[var5 + 6] & 255;
            var14 = var1[var5 + 7] & 255;
            var15 = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var16 = var11 << 24 | var12 << 16 | var13 << 8 | var14;
            var2[var3 + var6] = (long)var15 << 32 | (long)var16 & 4294967295L;
            var5 += 8;
         }
      } else {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5 + 7];
            var8 = var1[var5 + 6] & 255;
            var9 = var1[var5 + 5] & 255;
            var10 = var1[var5 + 4] & 255;
            var11 = var1[var5 + 3];
            var12 = var1[var5 + 2] & 255;
            var13 = var1[var5 + 1] & 255;
            var14 = var1[var5] & 255;
            var15 = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var16 = var11 << 24 | var12 << 16 | var13 << 8 | var14;
            var2[var3 + var6] = (long)var15 << 32 | (long)var16 & 4294967295L;
            var5 += 8;
         }
      }

   }

   private void toFloats(byte[] var1, float[] var2, int var3, int var4) {
      int var5 = 0;
      int var6;
      byte var7;
      int var8;
      int var9;
      int var10;
      int var11;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5];
            var8 = var1[var5 + 1] & 255;
            var9 = var1[var5 + 2] & 255;
            var10 = var1[var5 + 3] & 255;
            var11 = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var2[var3 + var6] = Float.intBitsToFloat(var11);
            var5 += 4;
         }
      } else {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5 + 3];
            var8 = var1[var5 + 2] & 255;
            var9 = var1[var5 + 1] & 255;
            var10 = var1[var5 + 0] & 255;
            var11 = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var2[var3 + var6] = Float.intBitsToFloat(var11);
            var5 += 4;
         }
      }

   }

   private void toDoubles(byte[] var1, double[] var2, int var3, int var4) {
      int var5 = 0;
      int var6;
      byte var7;
      int var8;
      int var9;
      int var10;
      byte var11;
      int var12;
      int var13;
      int var14;
      int var15;
      int var16;
      long var17;
      if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5];
            var8 = var1[var5 + 1] & 255;
            var9 = var1[var5 + 2] & 255;
            var10 = var1[var5 + 3] & 255;
            var11 = var1[var5 + 4];
            var12 = var1[var5 + 5] & 255;
            var13 = var1[var5 + 6] & 255;
            var14 = var1[var5 + 7] & 255;
            var15 = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var16 = var11 << 24 | var12 << 16 | var13 << 8 | var14;
            var17 = (long)var15 << 32 | (long)var16 & 4294967295L;
            var2[var3 + var6] = Double.longBitsToDouble(var17);
            var5 += 8;
         }
      } else {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var1[var5 + 7];
            var8 = var1[var5 + 6] & 255;
            var9 = var1[var5 + 5] & 255;
            var10 = var1[var5 + 4] & 255;
            var11 = var1[var5 + 3];
            var12 = var1[var5 + 2] & 255;
            var13 = var1[var5 + 1] & 255;
            var14 = var1[var5] & 255;
            var15 = var7 << 24 | var8 << 16 | var9 << 8 | var10;
            var16 = var11 << 24 | var12 << 16 | var13 << 8 | var14;
            var17 = (long)var15 << 32 | (long)var16 & 4294967295L;
            var2[var3 + var6] = Double.longBitsToDouble(var17);
            var5 += 8;
         }
      }

   }

   public long getStreamPosition() throws IOException {
      this.checkClosed();
      return this.streamPos;
   }

   public int getBitOffset() throws IOException {
      this.checkClosed();
      return this.bitOffset;
   }

   public void setBitOffset(int var1) throws IOException {
      this.checkClosed();
      if (var1 >= 0 && var1 <= 7) {
         this.bitOffset = var1;
      } else {
         throw new IllegalArgumentException("bitOffset must be betwwen 0 and 7!");
      }
   }

   public int readBit() throws IOException {
      this.checkClosed();
      int var1 = this.bitOffset + 1 & 7;
      int var2 = this.read();
      if (var2 == -1) {
         throw new EOFException();
      } else {
         if (var1 != 0) {
            this.seek(this.getStreamPosition() - 1L);
            var2 >>= 8 - var1;
         }

         this.bitOffset = var1;
         return var2 & 1;
      }
   }

   public long readBits(int var1) throws IOException {
      this.checkClosed();
      if (var1 >= 0 && var1 <= 64) {
         if (var1 == 0) {
            return 0L;
         } else {
            int var2 = var1 + this.bitOffset;
            int var3 = this.bitOffset + var1 & 7;

            long var4;
            for(var4 = 0L; var2 > 0; var2 -= 8) {
               int var6 = this.read();
               if (var6 == -1) {
                  throw new EOFException();
               }

               var4 <<= 8;
               var4 |= (long)var6;
            }

            if (var3 != 0) {
               this.seek(this.getStreamPosition() - 1L);
            }

            this.bitOffset = var3;
            var4 >>>= -var2;
            var4 &= -1L >>> 64 - var1;
            return var4;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public long length() {
      return -1L;
   }

   public int skipBytes(int var1) throws IOException {
      long var2 = this.getStreamPosition();
      this.seek(var2 + (long)var1);
      return (int)(this.getStreamPosition() - var2);
   }

   public long skipBytes(long var1) throws IOException {
      long var3 = this.getStreamPosition();
      this.seek(var3 + var1);
      return this.getStreamPosition() - var3;
   }

   public void seek(long var1) throws IOException {
      this.checkClosed();
      if (var1 < this.flushedPos) {
         throw new IndexOutOfBoundsException("pos < flushedPos!");
      } else {
         this.streamPos = var1;
         this.bitOffset = 0;
      }
   }

   public void mark() {
      try {
         this.markByteStack.push(this.getStreamPosition());
         this.markBitStack.push(this.getBitOffset());
      } catch (IOException var2) {
      }

   }

   public void reset() throws IOException {
      if (!this.markByteStack.empty()) {
         long var1 = (Long)this.markByteStack.pop();
         if (var1 < this.flushedPos) {
            throw new IIOException("Previous marked position has been discarded!");
         } else {
            this.seek(var1);
            int var3 = (Integer)this.markBitStack.pop();
            this.setBitOffset(var3);
         }
      }
   }

   public void flushBefore(long var1) throws IOException {
      this.checkClosed();
      if (var1 < this.flushedPos) {
         throw new IndexOutOfBoundsException("pos < flushedPos!");
      } else if (var1 > this.getStreamPosition()) {
         throw new IndexOutOfBoundsException("pos > getStreamPosition()!");
      } else {
         this.flushedPos = var1;
      }
   }

   public void flush() throws IOException {
      this.flushBefore(this.getStreamPosition());
   }

   public long getFlushedPosition() {
      return this.flushedPos;
   }

   public boolean isCached() {
      return false;
   }

   public boolean isCachedMemory() {
      return false;
   }

   public boolean isCachedFile() {
      return false;
   }

   public void close() throws IOException {
      this.checkClosed();
      this.isClosed = true;
   }

   protected void finalize() throws Throwable {
      if (!this.isClosed) {
         try {
            this.close();
         } catch (IOException var2) {
         }
      }

      super.finalize();
   }
}
