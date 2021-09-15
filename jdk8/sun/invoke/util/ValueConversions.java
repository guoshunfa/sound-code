package sun.invoke.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.EnumMap;

public class ValueConversions {
   private static final Class<?> THIS_CLASS = ValueConversions.class;
   private static final MethodHandles.Lookup IMPL_LOOKUP = MethodHandles.lookup();
   private static final ValueConversions.WrapperCache[] UNBOX_CONVERSIONS = newWrapperCaches(4);
   private static final Integer ZERO_INT = 0;
   private static final Integer ONE_INT = 1;
   private static final ValueConversions.WrapperCache[] BOX_CONVERSIONS = newWrapperCaches(1);
   private static final ValueConversions.WrapperCache[] CONSTANT_FUNCTIONS = newWrapperCaches(2);
   private static final MethodHandle CAST_REFERENCE;
   private static final MethodHandle IGNORE;
   private static final MethodHandle EMPTY;
   private static final ValueConversions.WrapperCache[] CONVERT_PRIMITIVE_FUNCTIONS;

   private static ValueConversions.WrapperCache[] newWrapperCaches(int var0) {
      ValueConversions.WrapperCache[] var1 = new ValueConversions.WrapperCache[var0];

      for(int var2 = 0; var2 < var0; ++var2) {
         var1[var2] = new ValueConversions.WrapperCache();
      }

      return var1;
   }

   static int unboxInteger(Integer var0) {
      return var0;
   }

   static int unboxInteger(Object var0, boolean var1) {
      return var0 instanceof Integer ? (Integer)var0 : primitiveConversion(Wrapper.INT, var0, var1).intValue();
   }

   static byte unboxByte(Byte var0) {
      return var0;
   }

   static byte unboxByte(Object var0, boolean var1) {
      return var0 instanceof Byte ? (Byte)var0 : primitiveConversion(Wrapper.BYTE, var0, var1).byteValue();
   }

   static short unboxShort(Short var0) {
      return var0;
   }

   static short unboxShort(Object var0, boolean var1) {
      return var0 instanceof Short ? (Short)var0 : primitiveConversion(Wrapper.SHORT, var0, var1).shortValue();
   }

   static boolean unboxBoolean(Boolean var0) {
      return var0;
   }

   static boolean unboxBoolean(Object var0, boolean var1) {
      if (var0 instanceof Boolean) {
         return (Boolean)var0;
      } else {
         return (primitiveConversion(Wrapper.BOOLEAN, var0, var1).intValue() & 1) != 0;
      }
   }

   static char unboxCharacter(Character var0) {
      return var0;
   }

   static char unboxCharacter(Object var0, boolean var1) {
      return var0 instanceof Character ? (Character)var0 : (char)primitiveConversion(Wrapper.CHAR, var0, var1).intValue();
   }

   static long unboxLong(Long var0) {
      return var0;
   }

   static long unboxLong(Object var0, boolean var1) {
      return var0 instanceof Long ? (Long)var0 : primitiveConversion(Wrapper.LONG, var0, var1).longValue();
   }

   static float unboxFloat(Float var0) {
      return var0;
   }

   static float unboxFloat(Object var0, boolean var1) {
      return var0 instanceof Float ? (Float)var0 : primitiveConversion(Wrapper.FLOAT, var0, var1).floatValue();
   }

   static double unboxDouble(Double var0) {
      return var0;
   }

   static double unboxDouble(Object var0, boolean var1) {
      return var0 instanceof Double ? (Double)var0 : primitiveConversion(Wrapper.DOUBLE, var0, var1).doubleValue();
   }

   private static MethodType unboxType(Wrapper var0, int var1) {
      return var1 == 0 ? MethodType.methodType(var0.primitiveType(), var0.wrapperType()) : MethodType.methodType(var0.primitiveType(), Object.class, Boolean.TYPE);
   }

   private static MethodHandle unbox(Wrapper var0, int var1) {
      ValueConversions.WrapperCache var2 = UNBOX_CONVERSIONS[var1];
      MethodHandle var3 = var2.get(var0);
      if (var3 != null) {
         return var3;
      } else {
         switch(var0) {
         case OBJECT:
         case VOID:
            throw new IllegalArgumentException("unbox " + var0);
         default:
            String var4 = "unbox" + var0.wrapperSimpleName();
            MethodType var5 = unboxType(var0, var1);

            try {
               var3 = IMPL_LOOKUP.findStatic(THIS_CLASS, var4, var5);
            } catch (ReflectiveOperationException var7) {
               var3 = null;
            }

            if (var3 != null) {
               if (var1 > 0) {
                  boolean var6 = var1 != 2;
                  var3 = MethodHandles.insertArguments(var3, 1, var6);
               }

               if (var1 == 1) {
                  var3 = var3.asType(unboxType(var0, 0));
               }

               return var2.put(var0, var3);
            } else {
               throw new IllegalArgumentException("cannot find unbox adapter for " + var0 + (var1 <= 1 ? " (exact)" : (var1 == 3 ? " (cast)" : "")));
            }
         }
      }
   }

   public static MethodHandle unboxExact(Wrapper var0) {
      return unbox(var0, 0);
   }

   public static MethodHandle unboxExact(Wrapper var0, boolean var1) {
      return unbox(var0, var1 ? 0 : 1);
   }

   public static MethodHandle unboxWiden(Wrapper var0) {
      return unbox(var0, 2);
   }

   public static MethodHandle unboxCast(Wrapper var0) {
      return unbox(var0, 3);
   }

   public static Number primitiveConversion(Wrapper var0, Object var1, boolean var2) {
      if (var1 == null) {
         return !var2 ? null : ZERO_INT;
      } else {
         Object var3;
         if (var1 instanceof Number) {
            var3 = (Number)var1;
         } else if (var1 instanceof Boolean) {
            var3 = (Boolean)var1 ? ONE_INT : ZERO_INT;
         } else if (var1 instanceof Character) {
            var3 = Integer.valueOf((Character)var1);
         } else {
            var3 = (Number)var1;
         }

         Wrapper var4 = Wrapper.findWrapperType(var1.getClass());
         return (Number)(var4 != null && (var2 || var0.isConvertibleFrom(var4)) ? var3 : (Number)var0.wrapperType().cast(var1));
      }
   }

   public static int widenSubword(Object var0) {
      if (var0 instanceof Integer) {
         return (Integer)var0;
      } else if (var0 instanceof Boolean) {
         return fromBoolean((Boolean)var0);
      } else if (var0 instanceof Character) {
         return (Character)var0;
      } else if (var0 instanceof Short) {
         return (Short)var0;
      } else {
         return var0 instanceof Byte ? (Byte)var0 : (Integer)var0;
      }
   }

   static Integer boxInteger(int var0) {
      return var0;
   }

   static Byte boxByte(byte var0) {
      return var0;
   }

   static Short boxShort(short var0) {
      return var0;
   }

   static Boolean boxBoolean(boolean var0) {
      return var0;
   }

   static Character boxCharacter(char var0) {
      return var0;
   }

   static Long boxLong(long var0) {
      return var0;
   }

   static Float boxFloat(float var0) {
      return var0;
   }

   static Double boxDouble(double var0) {
      return var0;
   }

   private static MethodType boxType(Wrapper var0) {
      Class var1 = var0.wrapperType();
      return MethodType.methodType(var1, var0.primitiveType());
   }

   public static MethodHandle boxExact(Wrapper var0) {
      ValueConversions.WrapperCache var1 = BOX_CONVERSIONS[0];
      MethodHandle var2 = var1.get(var0);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = "box" + var0.wrapperSimpleName();
         MethodType var4 = boxType(var0);

         try {
            var2 = IMPL_LOOKUP.findStatic(THIS_CLASS, var3, var4);
         } catch (ReflectiveOperationException var6) {
            var2 = null;
         }

         if (var2 != null) {
            return var1.put(var0, var2);
         } else {
            throw new IllegalArgumentException("cannot find box adapter for " + var0);
         }
      }
   }

   static void ignore(Object var0) {
   }

   static void empty() {
   }

   static Object zeroObject() {
      return null;
   }

   static int zeroInteger() {
      return 0;
   }

   static long zeroLong() {
      return 0L;
   }

   static float zeroFloat() {
      return 0.0F;
   }

   static double zeroDouble() {
      return 0.0D;
   }

   public static MethodHandle zeroConstantFunction(Wrapper var0) {
      ValueConversions.WrapperCache var1 = CONSTANT_FUNCTIONS[0];
      MethodHandle var2 = var1.get(var0);
      if (var2 != null) {
         return var2;
      } else {
         MethodType var3 = MethodType.methodType(var0.primitiveType());
         switch(var0) {
         case OBJECT:
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
            try {
               var2 = IMPL_LOOKUP.findStatic(THIS_CLASS, "zero" + var0.wrapperSimpleName(), var3);
            } catch (ReflectiveOperationException var5) {
               var2 = null;
            }
            break;
         case VOID:
            var2 = EMPTY;
         }

         if (var2 != null) {
            return var1.put(var0, var2);
         } else if (var0.isSubwordOrInt() && var0 != Wrapper.INT) {
            var2 = MethodHandles.explicitCastArguments(zeroConstantFunction(Wrapper.INT), var3);
            return var1.put(var0, var2);
         } else {
            throw new IllegalArgumentException("cannot find zero constant for " + var0);
         }
      }
   }

   public static MethodHandle ignore() {
      return IGNORE;
   }

   public static MethodHandle cast() {
      return CAST_REFERENCE;
   }

   static float doubleToFloat(double var0) {
      return (float)var0;
   }

   static long doubleToLong(double var0) {
      return (long)var0;
   }

   static int doubleToInt(double var0) {
      return (int)var0;
   }

   static short doubleToShort(double var0) {
      return (short)((int)var0);
   }

   static char doubleToChar(double var0) {
      return (char)((int)var0);
   }

   static byte doubleToByte(double var0) {
      return (byte)((int)var0);
   }

   static boolean doubleToBoolean(double var0) {
      return toBoolean((byte)((int)var0));
   }

   static double floatToDouble(float var0) {
      return (double)var0;
   }

   static long floatToLong(float var0) {
      return (long)var0;
   }

   static int floatToInt(float var0) {
      return (int)var0;
   }

   static short floatToShort(float var0) {
      return (short)((int)var0);
   }

   static char floatToChar(float var0) {
      return (char)((int)var0);
   }

   static byte floatToByte(float var0) {
      return (byte)((int)var0);
   }

   static boolean floatToBoolean(float var0) {
      return toBoolean((byte)((int)var0));
   }

   static double longToDouble(long var0) {
      return (double)var0;
   }

   static float longToFloat(long var0) {
      return (float)var0;
   }

   static int longToInt(long var0) {
      return (int)var0;
   }

   static short longToShort(long var0) {
      return (short)((int)var0);
   }

   static char longToChar(long var0) {
      return (char)((int)var0);
   }

   static byte longToByte(long var0) {
      return (byte)((int)var0);
   }

   static boolean longToBoolean(long var0) {
      return toBoolean((byte)((int)var0));
   }

   static double intToDouble(int var0) {
      return (double)var0;
   }

   static float intToFloat(int var0) {
      return (float)var0;
   }

   static long intToLong(int var0) {
      return (long)var0;
   }

   static short intToShort(int var0) {
      return (short)var0;
   }

   static char intToChar(int var0) {
      return (char)var0;
   }

   static byte intToByte(int var0) {
      return (byte)var0;
   }

   static boolean intToBoolean(int var0) {
      return toBoolean((byte)var0);
   }

   static double shortToDouble(short var0) {
      return (double)var0;
   }

   static float shortToFloat(short var0) {
      return (float)var0;
   }

   static long shortToLong(short var0) {
      return (long)var0;
   }

   static int shortToInt(short var0) {
      return var0;
   }

   static char shortToChar(short var0) {
      return (char)var0;
   }

   static byte shortToByte(short var0) {
      return (byte)var0;
   }

   static boolean shortToBoolean(short var0) {
      return toBoolean((byte)var0);
   }

   static double charToDouble(char var0) {
      return (double)var0;
   }

   static float charToFloat(char var0) {
      return (float)var0;
   }

   static long charToLong(char var0) {
      return (long)var0;
   }

   static int charToInt(char var0) {
      return var0;
   }

   static short charToShort(char var0) {
      return (short)var0;
   }

   static byte charToByte(char var0) {
      return (byte)var0;
   }

   static boolean charToBoolean(char var0) {
      return toBoolean((byte)var0);
   }

   static double byteToDouble(byte var0) {
      return (double)var0;
   }

   static float byteToFloat(byte var0) {
      return (float)var0;
   }

   static long byteToLong(byte var0) {
      return (long)var0;
   }

   static int byteToInt(byte var0) {
      return var0;
   }

   static short byteToShort(byte var0) {
      return (short)var0;
   }

   static char byteToChar(byte var0) {
      return (char)var0;
   }

   static boolean byteToBoolean(byte var0) {
      return toBoolean(var0);
   }

   static double booleanToDouble(boolean var0) {
      return (double)fromBoolean(var0);
   }

   static float booleanToFloat(boolean var0) {
      return (float)fromBoolean(var0);
   }

   static long booleanToLong(boolean var0) {
      return (long)fromBoolean(var0);
   }

   static int booleanToInt(boolean var0) {
      return fromBoolean(var0);
   }

   static short booleanToShort(boolean var0) {
      return (short)fromBoolean(var0);
   }

   static char booleanToChar(boolean var0) {
      return (char)fromBoolean(var0);
   }

   static byte booleanToByte(boolean var0) {
      return fromBoolean(var0);
   }

   static boolean toBoolean(byte var0) {
      return (var0 & 1) != 0;
   }

   static byte fromBoolean(boolean var0) {
      return (byte)(var0 ? 1 : 0);
   }

   public static MethodHandle convertPrimitive(Wrapper var0, Wrapper var1) {
      ValueConversions.WrapperCache var2 = CONVERT_PRIMITIVE_FUNCTIONS[var0.ordinal()];
      MethodHandle var3 = var2.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         Class var4 = var0.primitiveType();
         Class var5 = var1.primitiveType();
         MethodType var6 = MethodType.methodType(var5, var4);
         if (var0 == var1) {
            var3 = MethodHandles.identity(var4);
         } else {
            assert var4.isPrimitive() && var5.isPrimitive();

            try {
               var3 = IMPL_LOOKUP.findStatic(THIS_CLASS, var4.getSimpleName() + "To" + capitalize(var5.getSimpleName()), var6);
            } catch (ReflectiveOperationException var8) {
               var3 = null;
            }
         }

         if (var3 != null) {
            assert var3.type() == var6 : var3;

            return var2.put(var1, var3);
         } else {
            throw new IllegalArgumentException("cannot find primitive conversion function for " + var4.getSimpleName() + " -> " + var5.getSimpleName());
         }
      }
   }

   public static MethodHandle convertPrimitive(Class<?> var0, Class<?> var1) {
      return convertPrimitive(Wrapper.forPrimitiveType(var0), Wrapper.forPrimitiveType(var1));
   }

   private static String capitalize(String var0) {
      return Character.toUpperCase(var0.charAt(0)) + var0.substring(1);
   }

   private static InternalError newInternalError(String var0, Throwable var1) {
      return new InternalError(var0, var1);
   }

   private static InternalError newInternalError(Throwable var0) {
      return new InternalError(var0);
   }

   static {
      try {
         MethodType var0 = MethodType.genericMethodType(1);
         MethodType var1 = var0.changeReturnType(Void.TYPE);
         CAST_REFERENCE = IMPL_LOOKUP.findVirtual(Class.class, "cast", var0);
         IGNORE = IMPL_LOOKUP.findStatic(THIS_CLASS, "ignore", var1);
         EMPTY = IMPL_LOOKUP.findStatic(THIS_CLASS, "empty", var1.dropParameterTypes(0, 1));
      } catch (IllegalAccessException | NoSuchMethodException var2) {
         throw newInternalError("uncaught exception", var2);
      }

      CONVERT_PRIMITIVE_FUNCTIONS = newWrapperCaches(Wrapper.values().length);
   }

   private static class WrapperCache {
      private final EnumMap<Wrapper, MethodHandle> map;

      private WrapperCache() {
         this.map = new EnumMap(Wrapper.class);
      }

      public MethodHandle get(Wrapper var1) {
         return (MethodHandle)this.map.get(var1);
      }

      public synchronized MethodHandle put(Wrapper var1, MethodHandle var2) {
         MethodHandle var3 = (MethodHandle)this.map.putIfAbsent(var1, var2);
         return var3 != null ? var3 : var2;
      }

      // $FF: synthetic method
      WrapperCache(Object var1) {
         this();
      }
   }
}
