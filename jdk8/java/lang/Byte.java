package java.lang;

public final class Byte extends Number implements Comparable<Byte> {
   public static final byte MIN_VALUE = -128;
   public static final byte MAX_VALUE = 127;
   public static final Class<Byte> TYPE = Class.getPrimitiveClass("byte");
   private final byte value;
   public static final int SIZE = 8;
   public static final int BYTES = 1;
   private static final long serialVersionUID = -7183698231559129828L;

   public static String toString(byte var0) {
      return Integer.toString(var0, 10);
   }

   public static Byte valueOf(byte var0) {
      return Byte.ByteCache.cache[var0 + 128];
   }

   public static byte parseByte(String var0, int var1) throws NumberFormatException {
      int var2 = Integer.parseInt(var0, var1);
      if (var2 >= -128 && var2 <= 127) {
         return (byte)var2;
      } else {
         throw new NumberFormatException("Value out of range. Value:\"" + var0 + "\" Radix:" + var1);
      }
   }

   public static byte parseByte(String var0) throws NumberFormatException {
      return parseByte(var0, 10);
   }

   public static Byte valueOf(String var0, int var1) throws NumberFormatException {
      return parseByte(var0, var1);
   }

   public static Byte valueOf(String var0) throws NumberFormatException {
      return valueOf(var0, 10);
   }

   public static Byte decode(String var0) throws NumberFormatException {
      int var1 = Integer.decode(var0);
      if (var1 >= -128 && var1 <= 127) {
         return (byte)var1;
      } else {
         throw new NumberFormatException("Value " + var1 + " out of range from input " + var0);
      }
   }

   public Byte(byte var1) {
      this.value = var1;
   }

   public Byte(String var1) throws NumberFormatException {
      this.value = parseByte(var1, 10);
   }

   public byte byteValue() {
      return this.value;
   }

   public short shortValue() {
      return (short)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public String toString() {
      return Integer.toString(this.value);
   }

   public int hashCode() {
      return hashCode(this.value);
   }

   public static int hashCode(byte var0) {
      return var0;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Byte) {
         return this.value == (Byte)var1;
      } else {
         return false;
      }
   }

   public int compareTo(Byte var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(byte var0, byte var1) {
      return var0 - var1;
   }

   public static int toUnsignedInt(byte var0) {
      return var0 & 255;
   }

   public static long toUnsignedLong(byte var0) {
      return (long)var0 & 255L;
   }

   private static class ByteCache {
      static final Byte[] cache = new Byte[256];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new Byte((byte)(var0 - 128));
         }

      }
   }
}
