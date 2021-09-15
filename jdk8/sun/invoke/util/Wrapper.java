package sun.invoke.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public enum Wrapper {
   BOOLEAN(Boolean.class, Boolean.TYPE, 'Z', false, new boolean[0], Wrapper.Format.unsigned(1)),
   BYTE(Byte.class, Byte.TYPE, 'B', (byte)0, new byte[0], Wrapper.Format.signed(8)),
   SHORT(Short.class, Short.TYPE, 'S', Short.valueOf((short)0), new short[0], Wrapper.Format.signed(16)),
   CHAR(Character.class, Character.TYPE, 'C', '\u0000', new char[0], Wrapper.Format.unsigned(16)),
   INT(Integer.class, Integer.TYPE, 'I', 0, new int[0], Wrapper.Format.signed(32)),
   LONG(Long.class, Long.TYPE, 'J', 0L, new long[0], Wrapper.Format.signed(64)),
   FLOAT(Float.class, Float.TYPE, 'F', 0.0F, new float[0], Wrapper.Format.floating(32)),
   DOUBLE(Double.class, Double.TYPE, 'D', 0.0D, new double[0], Wrapper.Format.floating(64)),
   OBJECT(Object.class, Object.class, 'L', (Object)null, new Object[0], Wrapper.Format.other(1)),
   VOID(Void.class, Void.TYPE, 'V', (Object)null, (Object)null, Wrapper.Format.other(0));

   private final Class<?> wrapperType;
   private final Class<?> primitiveType;
   private final char basicTypeChar;
   private final Object zero;
   private final Object emptyArray;
   private final int format;
   private final String wrapperSimpleName;
   private final String primitiveSimpleName;
   private static final Wrapper[] FROM_PRIM;
   private static final Wrapper[] FROM_WRAP;
   private static final Wrapper[] FROM_CHAR;

   private Wrapper(Class<?> var3, Class<?> var4, char var5, Object var6, Object var7, int var8) {
      this.wrapperType = var3;
      this.primitiveType = var4;
      this.basicTypeChar = var5;
      this.zero = var6;
      this.emptyArray = var7;
      this.format = var8;
      this.wrapperSimpleName = var3.getSimpleName();
      this.primitiveSimpleName = var4.getSimpleName();
   }

   public String detailString() {
      return this.wrapperSimpleName + Arrays.asList(this.wrapperType, this.primitiveType, this.basicTypeChar, this.zero, "0x" + Integer.toHexString(this.format));
   }

   public int bitWidth() {
      return this.format >> 2 & 1023;
   }

   public int stackSlots() {
      return this.format >> 0 & 3;
   }

   public boolean isSingleWord() {
      return (this.format & 1) != 0;
   }

   public boolean isDoubleWord() {
      return (this.format & 2) != 0;
   }

   public boolean isNumeric() {
      return (this.format & -4) != 0;
   }

   public boolean isIntegral() {
      return this.isNumeric() && this.format < 4225;
   }

   public boolean isSubwordOrInt() {
      return this.isIntegral() && this.isSingleWord();
   }

   public boolean isSigned() {
      return this.format < 0;
   }

   public boolean isUnsigned() {
      return this.format >= 5 && this.format < 4225;
   }

   public boolean isFloating() {
      return this.format >= 4225;
   }

   public boolean isOther() {
      return (this.format & -4) == 0;
   }

   public boolean isConvertibleFrom(Wrapper var1) {
      if (this == var1) {
         return true;
      } else if (this.compareTo(var1) < 0) {
         return false;
      } else {
         boolean var2 = (this.format & var1.format & -4096) != 0;
         if (!var2) {
            if (this.isOther()) {
               return true;
            } else {
               return var1.format == 65;
            }
         } else {
            assert this.isFloating() || this.isSigned();

            assert var1.isFloating() || var1.isSigned();

            return true;
         }
      }
   }

   private static boolean checkConvertibleFrom() {
      Wrapper[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Wrapper var3 = var0[var2];

         assert var3.isConvertibleFrom(var3);

         assert VOID.isConvertibleFrom(var3);

         if (var3 != VOID) {
            assert OBJECT.isConvertibleFrom(var3);

            assert !var3.isConvertibleFrom(VOID);
         }

         if (var3 != CHAR) {
            assert !CHAR.isConvertibleFrom(var3);

            assert var3.isConvertibleFrom(INT) || !var3.isConvertibleFrom(CHAR);
         }

         if (var3 != BOOLEAN) {
            assert !BOOLEAN.isConvertibleFrom(var3);

            assert var3 == VOID || var3 == OBJECT || !var3.isConvertibleFrom(BOOLEAN);
         }

         Wrapper[] var4;
         int var5;
         int var6;
         Wrapper var7;
         if (var3.isSigned()) {
            var4 = values();
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               if (var3 != var7) {
                  if (var7.isFloating()) {
                     assert !var3.isConvertibleFrom(var7);
                  } else if (var7.isSigned()) {
                     if (var3.compareTo(var7) < 0) {
                        assert !var3.isConvertibleFrom(var7);
                     } else {
                        assert var3.isConvertibleFrom(var7);
                     }
                  }
               }
            }
         }

         if (var3.isFloating()) {
            var4 = values();
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               if (var3 != var7) {
                  if (var7.isSigned()) {
                     assert var3.isConvertibleFrom(var7);
                  } else if (var7.isFloating()) {
                     if (var3.compareTo(var7) < 0) {
                        assert !var3.isConvertibleFrom(var7);
                     } else {
                        assert var3.isConvertibleFrom(var7);
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public Object zero() {
      return this.zero;
   }

   public <T> T zero(Class<T> var1) {
      return this.convert(this.zero, var1);
   }

   public static Wrapper forPrimitiveType(Class<?> var0) {
      Wrapper var1 = findPrimitiveType(var0);
      if (var1 != null) {
         return var1;
      } else if (var0.isPrimitive()) {
         throw new InternalError();
      } else {
         throw newIllegalArgumentException("not primitive: " + var0);
      }
   }

   static Wrapper findPrimitiveType(Class<?> var0) {
      Wrapper var1 = FROM_PRIM[hashPrim(var0)];
      return var1 != null && var1.primitiveType == var0 ? var1 : null;
   }

   public static Wrapper forWrapperType(Class<?> var0) {
      Wrapper var1 = findWrapperType(var0);
      if (var1 != null) {
         return var1;
      } else {
         Wrapper[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Wrapper var5 = var2[var4];
            if (var5.wrapperType == var0) {
               throw new InternalError();
            }
         }

         throw newIllegalArgumentException("not wrapper: " + var0);
      }
   }

   static Wrapper findWrapperType(Class<?> var0) {
      Wrapper var1 = FROM_WRAP[hashWrap(var0)];
      return var1 != null && var1.wrapperType == var0 ? var1 : null;
   }

   public static Wrapper forBasicType(char var0) {
      Wrapper var1 = FROM_CHAR[hashChar(var0)];
      if (var1 != null && var1.basicTypeChar == var0) {
         return var1;
      } else {
         Wrapper[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Wrapper var10000 = var2[var4];
            if (var1.basicTypeChar == var0) {
               throw new InternalError();
            }
         }

         throw newIllegalArgumentException("not basic type char: " + var0);
      }
   }

   public static Wrapper forBasicType(Class<?> var0) {
      return var0.isPrimitive() ? forPrimitiveType(var0) : OBJECT;
   }

   private static int hashPrim(Class<?> var0) {
      String var1 = var0.getName();
      return var1.length() < 3 ? 0 : (var1.charAt(0) + var1.charAt(2)) % 16;
   }

   private static int hashWrap(Class<?> var0) {
      String var1 = var0.getName();

      assert 10 == "java.lang.".length();

      return var1.length() < 13 ? 0 : (3 * var1.charAt(11) + var1.charAt(12)) % 16;
   }

   private static int hashChar(char var0) {
      return (var0 + (var0 >> 1)) % 16;
   }

   public Class<?> primitiveType() {
      return this.primitiveType;
   }

   public Class<?> wrapperType() {
      return this.wrapperType;
   }

   public <T> Class<T> wrapperType(Class<T> var1) {
      if (var1 == this.wrapperType) {
         return var1;
      } else if (var1 != this.primitiveType && this.wrapperType != Object.class && !var1.isInterface()) {
         throw newClassCastException(var1, this.primitiveType);
      } else {
         return forceType(this.wrapperType, var1);
      }
   }

   private static ClassCastException newClassCastException(Class<?> var0, Class<?> var1) {
      return new ClassCastException(var0 + " is not compatible with " + var1);
   }

   public static <T> Class<T> asWrapperType(Class<T> var0) {
      return var0.isPrimitive() ? forPrimitiveType(var0).wrapperType(var0) : var0;
   }

   public static <T> Class<T> asPrimitiveType(Class<T> var0) {
      Wrapper var1 = findWrapperType(var0);
      return var1 != null ? forceType(var1.primitiveType(), var0) : var0;
   }

   public static boolean isWrapperType(Class<?> var0) {
      return findWrapperType(var0) != null;
   }

   public static boolean isPrimitiveType(Class<?> var0) {
      return var0.isPrimitive();
   }

   public static char basicTypeChar(Class<?> var0) {
      return !var0.isPrimitive() ? 'L' : forPrimitiveType(var0).basicTypeChar();
   }

   public char basicTypeChar() {
      return this.basicTypeChar;
   }

   public String wrapperSimpleName() {
      return this.wrapperSimpleName;
   }

   public String primitiveSimpleName() {
      return this.primitiveSimpleName;
   }

   public <T> T cast(Object var1, Class<T> var2) {
      return this.convert(var1, var2, true);
   }

   public <T> T convert(Object var1, Class<T> var2) {
      return this.convert(var1, var2, false);
   }

   private <T> T convert(Object var1, Class<T> var2, boolean var3) {
      if (this == OBJECT) {
         assert !var2.isPrimitive();

         if (!var2.isInterface()) {
            var2.cast(var1);
         }

         return var1;
      } else {
         Class var4 = this.wrapperType(var2);
         if (var4.isInstance(var1)) {
            return var4.cast(var1);
         } else {
            Object var5;
            if (!var3) {
               Class var7 = var1.getClass();
               Wrapper var6 = findWrapperType(var7);
               if (var6 == null || !this.isConvertibleFrom(var6)) {
                  throw newClassCastException(var4, var7);
               }
            } else if (var1 == null) {
               var5 = this.zero;
               return var5;
            }

            var5 = this.wrap(var1);

            assert (var5 == null ? Void.class : var5.getClass()) == var4;

            return var5;
         }
      }
   }

   static <T> Class<T> forceType(Class<?> var0, Class<T> var1) {
      boolean var2 = var0 == var1 || var0.isPrimitive() && forPrimitiveType(var0) == findWrapperType(var1) || var1.isPrimitive() && forPrimitiveType(var1) == findWrapperType(var0) || var0 == Object.class && !var1.isPrimitive();
      if (!var2) {
         System.out.println(var0 + " <= " + var1);
      }

      assert var0 == var1 || var0.isPrimitive() && forPrimitiveType(var0) == findWrapperType(var1) || var1.isPrimitive() && forPrimitiveType(var1) == findWrapperType(var0) || var0 == Object.class && !var1.isPrimitive();

      return var0;
   }

   public Object wrap(Object var1) {
      switch(this.basicTypeChar) {
      case 'L':
         return var1;
      case 'V':
         return null;
      default:
         Number var2 = numberValue(var1);
         switch(this.basicTypeChar) {
         case 'B':
            return (byte)var2.intValue();
         case 'C':
            return (char)var2.intValue();
         case 'D':
            return var2.doubleValue();
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new InternalError("bad wrapper");
         case 'F':
            return var2.floatValue();
         case 'I':
            return var2.intValue();
         case 'J':
            return var2.longValue();
         case 'S':
            return (short)var2.intValue();
         case 'Z':
            return boolValue(var2.byteValue());
         }
      }
   }

   public Object wrap(int var1) {
      if (this.basicTypeChar == 'L') {
         return var1;
      } else {
         switch(this.basicTypeChar) {
         case 'B':
            return (byte)var1;
         case 'C':
            return (char)var1;
         case 'D':
            return (double)var1;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new InternalError("bad wrapper");
         case 'F':
            return (float)var1;
         case 'I':
            return var1;
         case 'J':
            return (long)var1;
         case 'L':
            throw newIllegalArgumentException("cannot wrap to object type");
         case 'S':
            return (short)var1;
         case 'V':
            return null;
         case 'Z':
            return boolValue((byte)var1);
         }
      }
   }

   private static Number numberValue(Object var0) {
      if (var0 instanceof Number) {
         return (Number)var0;
      } else if (var0 instanceof Character) {
         return Integer.valueOf((Character)var0);
      } else {
         return (Number)(var0 instanceof Boolean ? (Boolean)var0 ? 1 : 0 : (Number)var0);
      }
   }

   private static boolean boolValue(byte var0) {
      var0 = (byte)(var0 & 1);
      return var0 != 0;
   }

   private static RuntimeException newIllegalArgumentException(String var0, Object var1) {
      return newIllegalArgumentException(var0 + var1);
   }

   private static RuntimeException newIllegalArgumentException(String var0) {
      return new IllegalArgumentException(var0);
   }

   public Object makeArray(int var1) {
      return Array.newInstance(this.primitiveType, var1);
   }

   public Class<?> arrayType() {
      return this.emptyArray.getClass();
   }

   public void copyArrayUnboxing(Object[] var1, int var2, Object var3, int var4, int var5) {
      if (var3.getClass() != this.arrayType()) {
         this.arrayType().cast(var3);
      }

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var1[var6 + var2];
         var7 = this.convert(var7, this.primitiveType);
         Array.set(var3, var6 + var4, var7);
      }

   }

   public void copyArrayBoxing(Object var1, int var2, Object[] var3, int var4, int var5) {
      if (var1.getClass() != this.arrayType()) {
         this.arrayType().cast(var1);
      }

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = Array.get(var1, var6 + var2);

         assert var7.getClass() == this.wrapperType;

         var3[var6 + var4] = var7;
      }

   }

   static {
      assert checkConvertibleFrom();

      FROM_PRIM = new Wrapper[16];
      FROM_WRAP = new Wrapper[16];
      FROM_CHAR = new Wrapper[16];
      Wrapper[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Wrapper var3 = var0[var2];
         int var4 = hashPrim(var3.primitiveType);
         int var5 = hashWrap(var3.wrapperType);
         int var6 = hashChar(var3.basicTypeChar);

         assert FROM_PRIM[var4] == null;

         assert FROM_WRAP[var5] == null;

         assert FROM_CHAR[var6] == null;

         FROM_PRIM[var4] = var3;
         FROM_WRAP[var5] = var3;
         FROM_CHAR[var6] = var3;
      }

   }

   private abstract static class Format {
      static final int SLOT_SHIFT = 0;
      static final int SIZE_SHIFT = 2;
      static final int KIND_SHIFT = 12;
      static final int SIGNED = -4096;
      static final int UNSIGNED = 0;
      static final int FLOATING = 4096;
      static final int SLOT_MASK = 3;
      static final int SIZE_MASK = 1023;
      static final int INT = -3967;
      static final int SHORT = -4031;
      static final int BOOLEAN = 5;
      static final int CHAR = 65;
      static final int FLOAT = 4225;
      static final int VOID = 0;
      static final int NUM_MASK = -4;

      static int format(int var0, int var1, int var2) {
         assert var0 >> 12 << 12 == var0;

         assert (var1 & var1 - 1) == 0;

         if (!$assertionsDisabled) {
            if (var0 == -4096) {
               if (var1 <= 0) {
                  throw new AssertionError();
               }
            } else if (var0 == 0) {
               if (var1 <= 0) {
                  throw new AssertionError();
               }
            } else if (var0 != 4096 || var1 != 32 && var1 != 64) {
               throw new AssertionError();
            }
         }

         if (!$assertionsDisabled) {
            if (var2 == 2) {
               if (var1 != 64) {
                  throw new AssertionError();
               }
            } else if (var2 != 1 || var1 > 32) {
               throw new AssertionError();
            }
         }

         return var0 | var1 << 2 | var2 << 0;
      }

      static int signed(int var0) {
         return format(-4096, var0, var0 > 32 ? 2 : 1);
      }

      static int unsigned(int var0) {
         return format(0, var0, var0 > 32 ? 2 : 1);
      }

      static int floating(int var0) {
         return format(4096, var0, var0 > 32 ? 2 : 1);
      }

      static int other(int var0) {
         return var0 << 0;
      }
   }
}
