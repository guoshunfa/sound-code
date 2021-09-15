package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BytecodeDescriptor {
   private BytecodeDescriptor() {
   }

   public static List<Class<?>> parseMethod(String var0, ClassLoader var1) {
      return parseMethod(var0, 0, var0.length(), var1);
   }

   static List<Class<?>> parseMethod(String var0, int var1, int var2, ClassLoader var3) {
      if (var3 == null) {
         var3 = ClassLoader.getSystemClassLoader();
      }

      String var4 = var0;
      int[] var5 = new int[]{var1};
      ArrayList var6 = new ArrayList();
      Class var7;
      if (var5[0] < var2 && var0.charAt(var5[0]) == '(') {
         int var10002;
         for(var10002 = var5[0]++; var5[0] < var2 && var4.charAt(var5[0]) != ')'; var6.add(var7)) {
            var7 = parseSig(var4, var5, var2, var3);
            if (var7 == null || var7 == Void.TYPE) {
               parseError(var4, "bad argument type");
            }
         }

         var10002 = var5[0]++;
      } else {
         parseError(var0, "not a method type");
      }

      var7 = parseSig(var4, var5, var2, var3);
      if (var7 == null || var5[0] != var2) {
         parseError(var4, "bad return type");
      }

      var6.add(var7);
      return var6;
   }

   private static void parseError(String var0, String var1) {
      throw new IllegalArgumentException("bad signature: " + var0 + ": " + var1);
   }

   private static Class<?> parseSig(String var0, int[] var1, int var2, ClassLoader var3) {
      if (var1[0] == var2) {
         return null;
      } else {
         int var10004 = var1[0];
         int var10001 = var1[0];
         var1[0] = var10004 + 1;
         char var4 = var0.charAt(var10001);
         if (var4 == 'L') {
            int var10 = var1[0];
            int var6 = var0.indexOf(59, var10);
            if (var6 < 0) {
               return null;
            } else {
               var1[0] = var6 + 1;
               String var7 = var0.substring(var10, var6).replace('/', '.');

               try {
                  return var3.loadClass(var7);
               } catch (ClassNotFoundException var9) {
                  throw new TypeNotPresentException(var7, var9);
               }
            }
         } else if (var4 == '[') {
            Class var5 = parseSig(var0, var1, var2, var3);
            if (var5 != null) {
               var5 = Array.newInstance(var5, 0).getClass();
            }

            return var5;
         } else {
            return Wrapper.forBasicType(var4).primitiveType();
         }
      }
   }

   public static String unparse(Class<?> var0) {
      StringBuilder var1 = new StringBuilder();
      unparseSig(var0, var1);
      return var1.toString();
   }

   public static String unparse(MethodType var0) {
      return unparseMethod(var0.returnType(), var0.parameterList());
   }

   public static String unparse(Object var0) {
      if (var0 instanceof Class) {
         return unparse((Class)var0);
      } else {
         return var0 instanceof MethodType ? unparse((MethodType)var0) : (String)var0;
      }
   }

   public static String unparseMethod(Class<?> var0, List<Class<?>> var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append('(');
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Class var4 = (Class)var3.next();
         unparseSig(var4, var2);
      }

      var2.append(')');
      unparseSig(var0, var2);
      return var2.toString();
   }

   private static void unparseSig(Class<?> var0, StringBuilder var1) {
      char var2 = Wrapper.forBasicType(var0).basicTypeChar();
      if (var2 != 'L') {
         var1.append(var2);
      } else {
         boolean var3 = !var0.isArray();
         if (var3) {
            var1.append('L');
         }

         var1.append(var0.getName().replace('.', '/'));
         if (var3) {
            var1.append(';');
         }
      }

   }
}
