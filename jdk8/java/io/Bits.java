package java.io;

class Bits {
   static boolean getBoolean(byte[] var0, int var1) {
      return var0[var1] != 0;
   }

   static char getChar(byte[] var0, int var1) {
      return (char)((var0[var1 + 1] & 255) + (var0[var1] << 8));
   }

   static short getShort(byte[] var0, int var1) {
      return (short)((var0[var1 + 1] & 255) + (var0[var1] << 8));
   }

   static int getInt(byte[] var0, int var1) {
      return (var0[var1 + 3] & 255) + ((var0[var1 + 2] & 255) << 8) + ((var0[var1 + 1] & 255) << 16) + (var0[var1] << 24);
   }

   static float getFloat(byte[] var0, int var1) {
      return Float.intBitsToFloat(getInt(var0, var1));
   }

   static long getLong(byte[] var0, int var1) {
      return ((long)var0[var1 + 7] & 255L) + (((long)var0[var1 + 6] & 255L) << 8) + (((long)var0[var1 + 5] & 255L) << 16) + (((long)var0[var1 + 4] & 255L) << 24) + (((long)var0[var1 + 3] & 255L) << 32) + (((long)var0[var1 + 2] & 255L) << 40) + (((long)var0[var1 + 1] & 255L) << 48) + ((long)var0[var1] << 56);
   }

   static double getDouble(byte[] var0, int var1) {
      return Double.longBitsToDouble(getLong(var0, var1));
   }

   static void putBoolean(byte[] var0, int var1, boolean var2) {
      var0[var1] = (byte)(var2 ? 1 : 0);
   }

   static void putChar(byte[] var0, int var1, char var2) {
      var0[var1 + 1] = (byte)var2;
      var0[var1] = (byte)(var2 >>> 8);
   }

   static void putShort(byte[] var0, int var1, short var2) {
      var0[var1 + 1] = (byte)var2;
      var0[var1] = (byte)(var2 >>> 8);
   }

   static void putInt(byte[] var0, int var1, int var2) {
      var0[var1 + 3] = (byte)var2;
      var0[var1 + 2] = (byte)(var2 >>> 8);
      var0[var1 + 1] = (byte)(var2 >>> 16);
      var0[var1] = (byte)(var2 >>> 24);
   }

   static void putFloat(byte[] var0, int var1, float var2) {
      putInt(var0, var1, Float.floatToIntBits(var2));
   }

   static void putLong(byte[] var0, int var1, long var2) {
      var0[var1 + 7] = (byte)((int)var2);
      var0[var1 + 6] = (byte)((int)(var2 >>> 8));
      var0[var1 + 5] = (byte)((int)(var2 >>> 16));
      var0[var1 + 4] = (byte)((int)(var2 >>> 24));
      var0[var1 + 3] = (byte)((int)(var2 >>> 32));
      var0[var1 + 2] = (byte)((int)(var2 >>> 40));
      var0[var1 + 1] = (byte)((int)(var2 >>> 48));
      var0[var1] = (byte)((int)(var2 >>> 56));
   }

   static void putDouble(byte[] var0, int var1, double var2) {
      putLong(var0, var1, Double.doubleToLongBits(var2));
   }
}
