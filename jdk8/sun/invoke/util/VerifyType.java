package sun.invoke.util;

import java.lang.invoke.MethodType;
import sun.invoke.empty.Empty;

public class VerifyType {
   private VerifyType() {
   }

   public static boolean isNullConversion(Class<?> var0, Class<?> var1, boolean var2) {
      if (var0 == var1) {
         return true;
      } else {
         if (!var2) {
            if (var1.isInterface()) {
               var1 = Object.class;
            }

            if (var0.isInterface()) {
               var0 = Object.class;
            }

            if (var0 == var1) {
               return true;
            }
         }

         if (isNullType(var0)) {
            return !var1.isPrimitive();
         } else if (!var0.isPrimitive()) {
            return var1.isAssignableFrom(var0);
         } else if (!var1.isPrimitive()) {
            return false;
         } else {
            Wrapper var3 = Wrapper.forPrimitiveType(var0);
            if (var1 == Integer.TYPE) {
               return var3.isSubwordOrInt();
            } else {
               Wrapper var4 = Wrapper.forPrimitiveType(var1);
               if (!var3.isSubwordOrInt()) {
                  return false;
               } else if (!var4.isSubwordOrInt()) {
                  return false;
               } else if (!var4.isSigned() && var3.isSigned()) {
                  return false;
               } else {
                  return var4.bitWidth() > var3.bitWidth();
               }
            }
         }
      }
   }

   public static boolean isNullReferenceConversion(Class<?> var0, Class<?> var1) {
      assert !var1.isPrimitive();

      if (var1.isInterface()) {
         return true;
      } else {
         return isNullType(var0) ? true : var1.isAssignableFrom(var0);
      }
   }

   public static boolean isNullType(Class<?> var0) {
      if (var0 == Void.class) {
         return true;
      } else {
         return var0 == Empty.class;
      }
   }

   public static boolean isNullConversion(MethodType var0, MethodType var1, boolean var2) {
      if (var0 == var1) {
         return true;
      } else {
         int var3 = var0.parameterCount();
         if (var3 != var1.parameterCount()) {
            return false;
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               if (!isNullConversion(var0.parameterType(var4), var1.parameterType(var4), var2)) {
                  return false;
               }
            }

            return isNullConversion(var1.returnType(), var0.returnType(), var2);
         }
      }
   }

   public static int canPassUnchecked(Class<?> var0, Class<?> var1) {
      if (var0 == var1) {
         return 1;
      } else if (var1.isPrimitive()) {
         if (var1 == Void.TYPE) {
            return 1;
         } else if (var0 == Void.TYPE) {
            return 0;
         } else if (!var0.isPrimitive()) {
            return 0;
         } else {
            Wrapper var2 = Wrapper.forPrimitiveType(var0);
            Wrapper var3 = Wrapper.forPrimitiveType(var1);
            if (var2.isSubwordOrInt() && var3.isSubwordOrInt()) {
               if (var2.bitWidth() >= var3.bitWidth()) {
                  return -1;
               } else {
                  return !var3.isSigned() && var2.isSigned() ? -1 : 1;
               }
            } else if (var0 != Float.TYPE && var1 != Float.TYPE) {
               return 0;
            } else {
               return var0 != Double.TYPE && var1 != Double.TYPE ? 0 : -1;
            }
         }
      } else if (var0.isPrimitive()) {
         return 0;
      } else {
         return isNullReferenceConversion(var0, var1) ? 1 : -1;
      }
   }

   public static boolean isSpreadArgType(Class<?> var0) {
      return var0.isArray();
   }

   public static Class<?> spreadArgElementType(Class<?> var0, int var1) {
      return var0.getComponentType();
   }
}
