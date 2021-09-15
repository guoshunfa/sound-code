package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class UnsafeFieldAccessorFactory {
   static FieldAccessor newFieldAccessor(Field var0, boolean var1) {
      Class var2 = var0.getType();
      boolean var3 = Modifier.isStatic(var0.getModifiers());
      boolean var4 = Modifier.isFinal(var0.getModifiers());
      boolean var5 = Modifier.isVolatile(var0.getModifiers());
      boolean var6 = var4 || var5;
      boolean var7 = var4 && (var3 || !var1);
      if (var3) {
         UnsafeFieldAccessorImpl.unsafe.ensureClassInitialized(var0.getDeclaringClass());
         if (!var6) {
            if (var2 == Boolean.TYPE) {
               return new UnsafeStaticBooleanFieldAccessorImpl(var0);
            } else if (var2 == Byte.TYPE) {
               return new UnsafeStaticByteFieldAccessorImpl(var0);
            } else if (var2 == Short.TYPE) {
               return new UnsafeStaticShortFieldAccessorImpl(var0);
            } else if (var2 == Character.TYPE) {
               return new UnsafeStaticCharacterFieldAccessorImpl(var0);
            } else if (var2 == Integer.TYPE) {
               return new UnsafeStaticIntegerFieldAccessorImpl(var0);
            } else if (var2 == Long.TYPE) {
               return new UnsafeStaticLongFieldAccessorImpl(var0);
            } else if (var2 == Float.TYPE) {
               return new UnsafeStaticFloatFieldAccessorImpl(var0);
            } else {
               return (FieldAccessor)(var2 == Double.TYPE ? new UnsafeStaticDoubleFieldAccessorImpl(var0) : new UnsafeStaticObjectFieldAccessorImpl(var0));
            }
         } else if (var2 == Boolean.TYPE) {
            return new UnsafeQualifiedStaticBooleanFieldAccessorImpl(var0, var7);
         } else if (var2 == Byte.TYPE) {
            return new UnsafeQualifiedStaticByteFieldAccessorImpl(var0, var7);
         } else if (var2 == Short.TYPE) {
            return new UnsafeQualifiedStaticShortFieldAccessorImpl(var0, var7);
         } else if (var2 == Character.TYPE) {
            return new UnsafeQualifiedStaticCharacterFieldAccessorImpl(var0, var7);
         } else if (var2 == Integer.TYPE) {
            return new UnsafeQualifiedStaticIntegerFieldAccessorImpl(var0, var7);
         } else if (var2 == Long.TYPE) {
            return new UnsafeQualifiedStaticLongFieldAccessorImpl(var0, var7);
         } else if (var2 == Float.TYPE) {
            return new UnsafeQualifiedStaticFloatFieldAccessorImpl(var0, var7);
         } else {
            return (FieldAccessor)(var2 == Double.TYPE ? new UnsafeQualifiedStaticDoubleFieldAccessorImpl(var0, var7) : new UnsafeQualifiedStaticObjectFieldAccessorImpl(var0, var7));
         }
      } else if (!var6) {
         if (var2 == Boolean.TYPE) {
            return new UnsafeBooleanFieldAccessorImpl(var0);
         } else if (var2 == Byte.TYPE) {
            return new UnsafeByteFieldAccessorImpl(var0);
         } else if (var2 == Short.TYPE) {
            return new UnsafeShortFieldAccessorImpl(var0);
         } else if (var2 == Character.TYPE) {
            return new UnsafeCharacterFieldAccessorImpl(var0);
         } else if (var2 == Integer.TYPE) {
            return new UnsafeIntegerFieldAccessorImpl(var0);
         } else if (var2 == Long.TYPE) {
            return new UnsafeLongFieldAccessorImpl(var0);
         } else if (var2 == Float.TYPE) {
            return new UnsafeFloatFieldAccessorImpl(var0);
         } else {
            return (FieldAccessor)(var2 == Double.TYPE ? new UnsafeDoubleFieldAccessorImpl(var0) : new UnsafeObjectFieldAccessorImpl(var0));
         }
      } else if (var2 == Boolean.TYPE) {
         return new UnsafeQualifiedBooleanFieldAccessorImpl(var0, var7);
      } else if (var2 == Byte.TYPE) {
         return new UnsafeQualifiedByteFieldAccessorImpl(var0, var7);
      } else if (var2 == Short.TYPE) {
         return new UnsafeQualifiedShortFieldAccessorImpl(var0, var7);
      } else if (var2 == Character.TYPE) {
         return new UnsafeQualifiedCharacterFieldAccessorImpl(var0, var7);
      } else if (var2 == Integer.TYPE) {
         return new UnsafeQualifiedIntegerFieldAccessorImpl(var0, var7);
      } else if (var2 == Long.TYPE) {
         return new UnsafeQualifiedLongFieldAccessorImpl(var0, var7);
      } else if (var2 == Float.TYPE) {
         return new UnsafeQualifiedFloatFieldAccessorImpl(var0, var7);
      } else {
         return (FieldAccessor)(var2 == Double.TYPE ? new UnsafeQualifiedDoubleFieldAccessorImpl(var0, var7) : new UnsafeQualifiedObjectFieldAccessorImpl(var0, var7));
      }
   }
}
