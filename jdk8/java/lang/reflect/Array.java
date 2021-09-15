package java.lang.reflect;

public final class Array {
   private Array() {
   }

   public static Object newInstance(Class<?> var0, int var1) throws NegativeArraySizeException {
      return newArray(var0, var1);
   }

   public static Object newInstance(Class<?> var0, int... var1) throws IllegalArgumentException, NegativeArraySizeException {
      return multiNewArray(var0, var1);
   }

   public static native int getLength(Object var0) throws IllegalArgumentException;

   public static native Object get(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native boolean getBoolean(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native byte getByte(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native char getChar(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native short getShort(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native int getInt(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native long getLong(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native float getFloat(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native double getDouble(Object var0, int var1) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void set(Object var0, int var1, Object var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setBoolean(Object var0, int var1, boolean var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setByte(Object var0, int var1, byte var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setChar(Object var0, int var1, char var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setShort(Object var0, int var1, short var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setInt(Object var0, int var1, int var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setLong(Object var0, int var1, long var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setFloat(Object var0, int var1, float var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   public static native void setDouble(Object var0, int var1, double var2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   private static native Object newArray(Class<?> var0, int var1) throws NegativeArraySizeException;

   private static native Object multiNewArray(Class<?> var0, int[] var1) throws IllegalArgumentException, NegativeArraySizeException;
}
