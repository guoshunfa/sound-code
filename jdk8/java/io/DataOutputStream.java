package java.io;

public class DataOutputStream extends FilterOutputStream implements DataOutput {
   protected int written;
   private byte[] bytearr = null;
   private byte[] writeBuffer = new byte[8];

   public DataOutputStream(OutputStream var1) {
      super(var1);
   }

   private void incCount(int var1) {
      int var2 = this.written + var1;
      if (var2 < 0) {
         var2 = Integer.MAX_VALUE;
      }

      this.written = var2;
   }

   public synchronized void write(int var1) throws IOException {
      this.out.write(var1);
      this.incCount(1);
   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      this.incCount(var3);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public final void writeBoolean(boolean var1) throws IOException {
      this.out.write(var1 ? 1 : 0);
      this.incCount(1);
   }

   public final void writeByte(int var1) throws IOException {
      this.out.write(var1);
      this.incCount(1);
   }

   public final void writeShort(int var1) throws IOException {
      this.out.write(var1 >>> 8 & 255);
      this.out.write(var1 >>> 0 & 255);
      this.incCount(2);
   }

   public final void writeChar(int var1) throws IOException {
      this.out.write(var1 >>> 8 & 255);
      this.out.write(var1 >>> 0 & 255);
      this.incCount(2);
   }

   public final void writeInt(int var1) throws IOException {
      this.out.write(var1 >>> 24 & 255);
      this.out.write(var1 >>> 16 & 255);
      this.out.write(var1 >>> 8 & 255);
      this.out.write(var1 >>> 0 & 255);
      this.incCount(4);
   }

   public final void writeLong(long var1) throws IOException {
      this.writeBuffer[0] = (byte)((int)(var1 >>> 56));
      this.writeBuffer[1] = (byte)((int)(var1 >>> 48));
      this.writeBuffer[2] = (byte)((int)(var1 >>> 40));
      this.writeBuffer[3] = (byte)((int)(var1 >>> 32));
      this.writeBuffer[4] = (byte)((int)(var1 >>> 24));
      this.writeBuffer[5] = (byte)((int)(var1 >>> 16));
      this.writeBuffer[6] = (byte)((int)(var1 >>> 8));
      this.writeBuffer[7] = (byte)((int)(var1 >>> 0));
      this.out.write(this.writeBuffer, 0, 8);
      this.incCount(8);
   }

   public final void writeFloat(float var1) throws IOException {
      this.writeInt(Float.floatToIntBits(var1));
   }

   public final void writeDouble(double var1) throws IOException {
      this.writeLong(Double.doubleToLongBits(var1));
   }

   public final void writeBytes(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.out.write((byte)var1.charAt(var3));
      }

      this.incCount(var2);
   }

   public final void writeChars(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1.charAt(var3);
         this.out.write(var4 >>> 8 & 255);
         this.out.write(var4 >>> 0 & 255);
      }

      this.incCount(var2 * 2);
   }

   public final void writeUTF(String var1) throws IOException {
      writeUTF(var1, this);
   }

   static int writeUTF(String var0, DataOutput var1) throws IOException {
      int var2 = var0.length();
      int var3 = 0;
      byte var5 = 0;

      char var4;
      for(int var6 = 0; var6 < var2; ++var6) {
         var4 = var0.charAt(var6);
         if (var4 >= 1 && var4 <= 127) {
            ++var3;
         } else if (var4 > 2047) {
            var3 += 3;
         } else {
            var3 += 2;
         }
      }

      if (var3 > 65535) {
         throw new UTFDataFormatException("encoded string too long: " + var3 + " bytes");
      } else {
         Object var9 = null;
         byte[] var10;
         if (var1 instanceof DataOutputStream) {
            DataOutputStream var7 = (DataOutputStream)var1;
            if (var7.bytearr == null || var7.bytearr.length < var3 + 2) {
               var7.bytearr = new byte[var3 * 2 + 2];
            }

            var10 = var7.bytearr;
         } else {
            var10 = new byte[var3 + 2];
         }

         int var8 = var5 + 1;
         var10[var5] = (byte)(var3 >>> 8 & 255);
         var10[var8++] = (byte)(var3 >>> 0 & 255);
         boolean var11 = false;

         int var12;
         for(var12 = 0; var12 < var2; ++var12) {
            var4 = var0.charAt(var12);
            if (var4 < 1 || var4 > 127) {
               break;
            }

            var10[var8++] = (byte)var4;
         }

         for(; var12 < var2; ++var12) {
            var4 = var0.charAt(var12);
            if (var4 >= 1 && var4 <= 127) {
               var10[var8++] = (byte)var4;
            } else if (var4 > 2047) {
               var10[var8++] = (byte)(224 | var4 >> 12 & 15);
               var10[var8++] = (byte)(128 | var4 >> 6 & 63);
               var10[var8++] = (byte)(128 | var4 >> 0 & 63);
            } else {
               var10[var8++] = (byte)(192 | var4 >> 6 & 31);
               var10[var8++] = (byte)(128 | var4 >> 0 & 63);
            }
         }

         var1.write(var10, 0, var3 + 2);
         return var3 + 2;
      }
   }

   public final int size() {
      return this.written;
   }
}
