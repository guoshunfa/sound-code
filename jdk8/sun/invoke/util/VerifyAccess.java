package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.reflect.Reflection;

public class VerifyAccess {
   private static final int PACKAGE_ONLY = 0;
   private static final int PACKAGE_ALLOWED = 8;
   private static final int PROTECTED_OR_PACKAGE_ALLOWED = 12;
   private static final int ALL_ACCESS_MODES = 7;
   private static final boolean ALLOW_NESTMATE_ACCESS = false;

   private VerifyAccess() {
   }

   public static boolean isMemberAccessible(Class<?> var0, Class<?> var1, int var2, Class<?> var3, int var4) {
      if (var4 == 0) {
         return false;
      } else {
         assert (var4 & 1) != 0 && (var4 & -16) == 0;

         if (!isClassAccessible(var0, var3, var4)) {
            return false;
         } else if (var1 == var3 && (var4 & 2) != 0) {
            return true;
         } else {
            switch(var2 & 7) {
            case 0:
               assert !var1.isInterface();

               return (var4 & 8) != 0 && isSamePackage(var1, var3);
            case 1:
               return true;
            case 2:
               return false;
            case 3:
            default:
               throw new IllegalArgumentException("bad modifiers: " + Modifier.toString(var2));
            case 4:
               assert !var1.isInterface();

               if ((var4 & 12) != 0 && isSamePackage(var1, var3)) {
                  return true;
               } else if ((var4 & 4) == 0) {
                  return false;
               } else if ((var2 & 8) != 0 && !isRelatedClass(var0, var3)) {
                  return false;
               } else {
                  return (var4 & 4) != 0 && isSubClass(var3, var1);
               }
            }
         }
      }
   }

   static boolean isRelatedClass(Class<?> var0, Class<?> var1) {
      return var0 == var1 || isSubClass(var0, var1) || isSubClass(var1, var0);
   }

   static boolean isSubClass(Class<?> var0, Class<?> var1) {
      return var1.isAssignableFrom(var0) && !var0.isInterface();
   }

   static int getClassModifiers(Class<?> var0) {
      return !var0.isArray() && !var0.isPrimitive() ? Reflection.getClassAccessFlags(var0) : var0.getModifiers();
   }

   public static boolean isClassAccessible(Class<?> var0, Class<?> var1, int var2) {
      if (var2 == 0) {
         return false;
      } else {
         assert (var2 & 1) != 0 && (var2 & -16) == 0;

         int var3 = getClassModifiers(var0);
         if (Modifier.isPublic(var3)) {
            return true;
         } else {
            return (var2 & 8) != 0 && isSamePackage(var1, var0);
         }
      }
   }

   public static boolean isTypeVisible(Class<?> var0, Class<?> var1) {
      if (var0 == var1) {
         return true;
      } else {
         while(var0.isArray()) {
            var0 = var0.getComponentType();
         }

         if (!var0.isPrimitive() && var0 != Object.class) {
            ClassLoader var2 = var0.getClassLoader();
            final ClassLoader var3 = var1.getClassLoader();
            if (var2 == var3) {
               return true;
            } else if (var3 == null && var2 != null) {
               return false;
            } else if (var2 == null && var0.getName().startsWith("java.")) {
               return true;
            } else {
               final String var4 = var0.getName();
               Class var5 = (Class)AccessController.doPrivileged(new PrivilegedAction<Class>() {
                  public Class<?> run() {
                     try {
                        return Class.forName(var4, false, var3);
                     } catch (LinkageError | ClassNotFoundException var2) {
                        return null;
                     }
                  }
               });
               return var0 == var5;
            }
         } else {
            return true;
         }
      }
   }

   public static boolean isTypeVisible(MethodType var0, Class<?> var1) {
      int var2 = -1;

      for(int var3 = var0.parameterCount(); var2 < var3; ++var2) {
         Class var4 = var2 < 0 ? var0.returnType() : var0.parameterType(var2);
         if (!isTypeVisible(var4, var1)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isSamePackage(Class<?> var0, Class<?> var1) {
      assert !var0.isArray() && !var1.isArray();

      if (var0 == var1) {
         return true;
      } else if (var0.getClassLoader() != var1.getClassLoader()) {
         return false;
      } else {
         String var2 = var0.getName();
         String var3 = var1.getName();
         int var4 = var2.lastIndexOf(46);
         if (var4 != var3.lastIndexOf(46)) {
            return false;
         } else {
            for(int var5 = 0; var5 < var4; ++var5) {
               if (var2.charAt(var5) != var3.charAt(var5)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static String getPackageName(Class<?> var0) {
      assert !var0.isArray();

      String var1 = var0.getName();
      int var2 = var1.lastIndexOf(46);
      return var2 < 0 ? "" : var1.substring(0, var2);
   }

   public static boolean isSamePackageMember(Class<?> var0, Class<?> var1) {
      if (var0 == var1) {
         return true;
      } else if (!isSamePackage(var0, var1)) {
         return false;
      } else {
         return getOutermostEnclosingClass(var0) == getOutermostEnclosingClass(var1);
      }
   }

   private static Class<?> getOutermostEnclosingClass(Class<?> var0) {
      Class var1 = var0;

      for(Class var2 = var0; (var2 = var2.getEnclosingClass()) != null; var1 = var2) {
      }

      return var1;
   }

   private static boolean loadersAreRelated(ClassLoader var0, ClassLoader var1, boolean var2) {
      if (var0 == var1 || var0 == null || var1 == null && !var2) {
         return true;
      } else {
         ClassLoader var3;
         for(var3 = var1; var3 != null; var3 = var3.getParent()) {
            if (var3 == var0) {
               return true;
            }
         }

         if (var2) {
            return false;
         } else {
            for(var3 = var0; var3 != null; var3 = var3.getParent()) {
               if (var3 == var1) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static boolean classLoaderIsAncestor(Class<?> var0, Class<?> var1) {
      return loadersAreRelated(var0.getClassLoader(), var1.getClassLoader(), true);
   }
}
