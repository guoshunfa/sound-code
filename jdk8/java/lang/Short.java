package java.lang;

public final class Short extends Number implements Comparable<Short> {
   public static final short MIN_VALUE = -32768;
   public static final short MAX_VALUE = 32767;
   public static final Class<Short> TYPE = Class.getPrimitiveClass("short");
   private final short value;
   public static final int SIZE = 16;
   public static final int BYTES = 2;
   private static final long serialVersionUID = 7515723908773894738L;

   public static String toString(short var0) {
      return Integer.toString(var0, 10);
   }

   public static short parseShort(String var0, int var1) throws NumberFormatException {
      int var2 = Integer.parseInt(var0, var1);
      if (var2 >= -32768 && var2 <= 32767) {
         return (short)var2;
      } else {
         throw new NumberFormatException("Value out of range. Value:\"" + var0 + "\" Radix:" + var1);
      }
   }

   public static short parseShort(String var0) throws NumberFormatException {
      return parseShort(var0, 10);
   }

   public static Short valueOf(String var0, int var1) throws NumberFormatException {
      return parseShort(var0, var1);
   }

   public static Short valueOf(String var0) throws NumberFormatException {
      return valueOf(var0, 10);
   }

   public static Short valueOf(short var0) {
      return var0 >= -128 && var0 <= 127 ? Short.ShortCache.cache[var0 + 128] : new Short(var0);
   }

   public static Short decode(String var0) throws NumberFormatException {
      int var1 = Integer.decode(var0);
      if (var1 >= -32768 && var1 <= 32767) {
         return (short)var1;
      } else {
         throw new NumberFormatException("Value " + var1 + " out of range from input " + var0);
      }
   }

   public Short(short var1) {
      this.value = var1;
   }

   public Short(String var1) throws NumberFormatException {
      this.value = parseShort(var1, 10);
   }

   public byte byteValue() {
      return (byte)this.value;
   }

   public short shortValue() {
      return this.value;
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

   public static int hashCode(short var0) {
      return var0;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Short) {
         return this.value == (Short)var1;
      } else {
         return false;
      }
   }

   public int compareTo(Short var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(short var0, short var1) {
      return var0 - var1;
   }

   public static short reverseBytes(short var0) {
      return (short)((var0 & '\uff00') >> 8 | var0 << 8);
   }

   public static int toUnsignedInt(short var0) {
      return var0 & '\uffff';
   }

   public static long toUnsignedLong(short var0) {
      return (long)var0 & 65535L;
   }

   private static class ShortCache {
      static final Short[] cache = new Short[256];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new Short((short)(var0 - 128));
         }

      }
   }
}
