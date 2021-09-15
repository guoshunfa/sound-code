package java.io;

public class DataInputStream extends FilterInputStream implements DataInput {
   private byte[] bytearr = new byte[80];
   private char[] chararr = new char[80];
   private byte[] readBuffer = new byte[8];
   private char[] lineBuffer;

   public DataInputStream(InputStream var1) {
      super(var1);
   }

   public final int read(byte[] var1) throws IOException {
      return this.in.read(var1, 0, var1.length);
   }

   public final int read(byte[] var1, int var2, int var3) throws IOException {
      return this.in.read(var1, var2, var3);
   }

   public final void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   public final void readFully(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int var5;
         for(int var4 = 0; var4 < var3; var4 += var5) {
            var5 = this.in.read(var1, var2 + var4, var3 - var4);
            if (var5 < 0) {
               throw new EOFException();
            }
         }

      }
   }

   public final int skipBytes(int var1) throws IOException {
      int var2 = 0;

      int var4;
      for(boolean var3 = false; var2 < var1 && (var4 = (int)this.in.skip((long)(var1 - var2))) > 0; var2 += var4) {
      }

      return var2;
   }

   public final boolean readBoolean() throws IOException {
      int var1 = this.in.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1 != 0;
      }
   }

   public final byte readByte() throws IOException {
      int var1 = this.in.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }

   public final int readUnsignedByte() throws IOException {
      int var1 = this.in.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   public final short readShort() throws IOException {
      int var1 = this.in.read();
      int var2 = this.in.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (short)((var1 << 8) + (var2 << 0));
      }
   }

   public final int readUnsignedShort() throws IOException {
      int var1 = this.in.read();
      int var2 = this.in.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 8) + (var2 << 0);
      }
   }

   public final char readChar() throws IOException {
      int var1 = this.in.read();
      int var2 = this.in.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (char)((var1 << 8) + (var2 << 0));
      }
   }

   public final int readInt() throws IOException {
      int var1 = this.in.read();
      int var2 = this.in.read();
      int var3 = this.in.read();
      int var4 = this.in.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 24) + (var2 << 16) + (var3 << 8) + (var4 << 0);
      }
   }

   public final long readLong() throws IOException {
      this.readFully(this.readBuffer, 0, 8);
      return ((long)this.readBuffer[0] << 56) + ((long)(this.readBuffer[1] & 255) << 48) + ((long)(this.readBuffer[2] & 255) << 40) + ((long)(this.readBuffer[3] & 255) << 32) + ((long)(this.readBuffer[4] & 255) << 24) + (long)((this.readBuffer[5] & 255) << 16) + (long)((this.readBuffer[6] & 255) << 8) + (long)((this.readBuffer[7] & 255) << 0);
   }

   public final float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   public final double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   /** @deprecated */
   @Deprecated
   public final String readLine() throws IOException {
      char[] var1 = this.lineBuffer;
      if (var1 == null) {
         var1 = this.lineBuffer = new char[128];
      }

      int var2 = var1.length;
      int var3 = 0;

      while(true) {
         int var4;
         switch(var4 = this.in.read()) {
         case 13:
            int var5 = this.in.read();
            if (var5 != 10 && var5 != -1) {
               if (!(this.in instanceof PushbackInputStream)) {
                  this.in = new PushbackInputStream(this.in);
               }

               ((PushbackInputStream)this.in).unread(var5);
            }
         case -1:
         case 10:
            if (var4 == -1 && var3 == 0) {
               return null;
            }

            return String.copyValueOf(var1, 0, var3);
         }

         --var2;
         if (var2 < 0) {
            var1 = new char[var3 + 128];
            var2 = var1.length - var3 - 1;
            System.arraycopy(this.lineBuffer, 0, var1, 0, var3);
            this.lineBuffer = var1;
         }

         var1[var3++] = (char)var4;
      }
   }

   public final String readUTF() throws IOException {
      return readUTF(this);
   }

   public static final String readUTF(DataInput var0) throws IOException {
      int var1 = var0.readUnsignedShort();
      Object var2 = null;
      Object var3 = null;
      byte[] var9;
      char[] var10;
      if (var0 instanceof DataInputStream) {
         DataInputStream var4 = (DataInputStream)var0;
         if (var4.bytearr.length < var1) {
            var4.bytearr = new byte[var1 * 2];
            var4.chararr = new char[var1 * 2];
         }

         var10 = var4.chararr;
         var9 = var4.bytearr;
      } else {
         var9 = new byte[var1];
         var10 = new char[var1];
      }

      int var7 = 0;
      int var8 = 0;
      var0.readFully(var9, 0, var1);

      int var11;
      while(var7 < var1) {
         var11 = var9[var7] & 255;
         if (var11 > 127) {
            break;
         }

         ++var7;
         var10[var8++] = (char)var11;
      }

      while(var7 < var1) {
         var11 = var9[var7] & 255;
         byte var5;
         switch(var11 >> 4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            ++var7;
            var10[var8++] = (char)var11;
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw new UTFDataFormatException("malformed input around byte " + var7);
         case 12:
         case 13:
            var7 += 2;
            if (var7 > var1) {
               throw new UTFDataFormatException("malformed input: partial character at end");
            }

            var5 = var9[var7 - 1];
            if ((var5 & 192) != 128) {
               throw new UTFDataFormatException("malformed input around byte " + var7);
            }

            var10[var8++] = (char)((var11 & 31) << 6 | var5 & 63);
            break;
         case 14:
            var7 += 3;
            if (var7 > var1) {
               throw new UTFDataFormatException("malformed input: partial character at end");
            }

            var5 = var9[var7 - 2];
            byte var6 = var9[var7 - 1];
            if ((var5 & 192) != 128 || (var6 & 192) != 128) {
               throw new UTFDataFormatException("malformed input around byte " + (var7 - 1));
            }

            var10[var8++] = (char)((var11 & 15) << 12 | (var5 & 63) << 6 | (var6 & 63) << 0);
         }
      }

      return new String(var10, 0, var8);
   }
}
