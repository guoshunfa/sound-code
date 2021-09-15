package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

public final class ObjectStreamClassUtil_1_3 {
   private static Comparator compareClassByName = new ObjectStreamClassUtil_1_3.CompareClassByName();
   private static Comparator compareMemberByName = new ObjectStreamClassUtil_1_3.CompareMemberByName();
   private static Method hasStaticInitializerMethod = null;

   public static long computeSerialVersionUID(Class var0) {
      long var1 = ObjectStreamClass.getSerialVersionUID(var0);
      if (var1 == 0L) {
         return var1;
      } else {
         var1 = getSerialVersion(var1, var0);
         return var1;
      }
   }

   private static Long getSerialVersion(final long var0, final Class var2) {
      return (Long)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            long var1;
            try {
               Field var3 = var2.getDeclaredField("serialVersionUID");
               int var4 = var3.getModifiers();
               if (Modifier.isStatic(var4) && Modifier.isFinal(var4) && Modifier.isPrivate(var4)) {
                  var1 = var0;
               } else {
                  var1 = ObjectStreamClassUtil_1_3._computeSerialVersionUID(var2);
               }
            } catch (NoSuchFieldException var5) {
               var1 = ObjectStreamClassUtil_1_3._computeSerialVersionUID(var2);
            }

            return new Long(var1);
         }
      });
   }

   public static long computeStructuralUID(boolean var0, Class<?> var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(512);
      long var3 = 0L;

      try {
         if (!Serializable.class.isAssignableFrom(var1) || var1.isInterface()) {
            return 0L;
         }

         if (Externalizable.class.isAssignableFrom(var1)) {
            return 1L;
         }

         MessageDigest var5 = MessageDigest.getInstance("SHA");
         DigestOutputStream var6 = new DigestOutputStream(var2, var5);
         DataOutputStream var7 = new DataOutputStream(var6);
         Class var8 = var1.getSuperclass();
         if (var8 != null && var8 != Object.class) {
            boolean var9 = false;
            Class[] var10 = new Class[]{ObjectOutputStream.class};
            Method var11 = getDeclaredMethod(var8, "writeObject", var10, 2, 8);
            if (var11 != null) {
               var9 = true;
            }

            var7.writeLong(computeStructuralUID(var9, var8));
         }

         if (var0) {
            var7.writeInt(2);
         } else {
            var7.writeInt(1);
         }

         Field[] var15 = getDeclaredFields(var1);
         Arrays.sort(var15, compareMemberByName);

         int var12;
         for(int var16 = 0; var16 < var15.length; ++var16) {
            Field var18 = var15[var16];
            var12 = var18.getModifiers();
            if (!Modifier.isTransient(var12) && !Modifier.isStatic(var12)) {
               var7.writeUTF(var18.getName());
               var7.writeUTF(getSignature(var18.getType()));
            }
         }

         var7.flush();
         byte[] var17 = var5.digest();
         int var19 = Math.min(8, var17.length);

         for(var12 = var19; var12 > 0; --var12) {
            var3 += (long)(var17[var12] & 255) << var12 * 8;
         }
      } catch (IOException var13) {
         var3 = -1L;
      } catch (NoSuchAlgorithmException var14) {
         throw new SecurityException(var14.getMessage());
      }

      return var3;
   }

   private static long _computeSerialVersionUID(Class var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(512);
      long var2 = 0L;

      try {
         MessageDigest var4 = MessageDigest.getInstance("SHA");
         DigestOutputStream var5 = new DigestOutputStream(var1, var4);
         DataOutputStream var6 = new DataOutputStream(var5);
         var6.writeUTF(var0.getName());
         int var7 = var0.getModifiers();
         var7 &= 1553;
         Method[] var8 = var0.getDeclaredMethods();
         if ((var7 & 512) != 0) {
            var7 &= -1025;
            if (var8.length > 0) {
               var7 |= 1024;
            }
         }

         var6.writeInt(var7);
         int var10;
         if (!var0.isArray()) {
            Class[] var9 = var0.getInterfaces();
            Arrays.sort(var9, compareClassByName);

            for(var10 = 0; var10 < var9.length; ++var10) {
               var6.writeUTF(var9[var10].getName());
            }
         }

         Field[] var17 = var0.getDeclaredFields();
         Arrays.sort(var17, compareMemberByName);

         int var12;
         for(var10 = 0; var10 < var17.length; ++var10) {
            Field var11 = var17[var10];
            var12 = var11.getModifiers();
            if (!Modifier.isPrivate(var12) || !Modifier.isTransient(var12) && !Modifier.isStatic(var12)) {
               var6.writeUTF(var11.getName());
               var6.writeInt(var12);
               var6.writeUTF(getSignature(var11.getType()));
            }
         }

         if (hasStaticInitializer(var0)) {
            var6.writeUTF("<clinit>");
            var6.writeInt(8);
            var6.writeUTF("()V");
         }

         ObjectStreamClassUtil_1_3.MethodSignature[] var18 = ObjectStreamClassUtil_1_3.MethodSignature.removePrivateAndSort(var0.getDeclaredConstructors());

         String var14;
         for(int var19 = 0; var19 < var18.length; ++var19) {
            ObjectStreamClassUtil_1_3.MethodSignature var21 = var18[var19];
            String var13 = "<init>";
            var14 = var21.signature;
            var14 = var14.replace('/', '.');
            var6.writeUTF(var13);
            var6.writeInt(var21.member.getModifiers());
            var6.writeUTF(var14);
         }

         ObjectStreamClassUtil_1_3.MethodSignature[] var20 = ObjectStreamClassUtil_1_3.MethodSignature.removePrivateAndSort(var8);

         for(var12 = 0; var12 < var20.length; ++var12) {
            ObjectStreamClassUtil_1_3.MethodSignature var23 = var20[var12];
            var14 = var23.signature;
            var14 = var14.replace('/', '.');
            var6.writeUTF(var23.member.getName());
            var6.writeInt(var23.member.getModifiers());
            var6.writeUTF(var14);
         }

         var6.flush();
         byte[] var22 = var4.digest();

         for(int var24 = 0; var24 < Math.min(8, var22.length); ++var24) {
            var2 += (long)(var22[var24] & 255) << var24 * 8;
         }
      } catch (IOException var15) {
         var2 = -1L;
      } catch (NoSuchAlgorithmException var16) {
         throw new SecurityException(var16.getMessage());
      }

      return var2;
   }

   private static String getSignature(Class var0) {
      String var1 = null;
      if (var0.isArray()) {
         Class var2 = var0;

         int var3;
         for(var3 = 0; var2.isArray(); var2 = var2.getComponentType()) {
            ++var3;
         }

         StringBuffer var4 = new StringBuffer();

         for(int var5 = 0; var5 < var3; ++var5) {
            var4.append("[");
         }

         var4.append(getSignature(var2));
         var1 = var4.toString();
      } else if (var0.isPrimitive()) {
         if (var0 == Integer.TYPE) {
            var1 = "I";
         } else if (var0 == Byte.TYPE) {
            var1 = "B";
         } else if (var0 == Long.TYPE) {
            var1 = "J";
         } else if (var0 == Float.TYPE) {
            var1 = "F";
         } else if (var0 == Double.TYPE) {
            var1 = "D";
         } else if (var0 == Short.TYPE) {
            var1 = "S";
         } else if (var0 == Character.TYPE) {
            var1 = "C";
         } else if (var0 == Boolean.TYPE) {
            var1 = "Z";
         } else if (var0 == Void.TYPE) {
            var1 = "V";
         }
      } else {
         var1 = "L" + var0.getName().replace('.', '/') + ";";
      }

      return var1;
   }

   private static String getSignature(Method var0) {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");
      Class[] var2 = var0.getParameterTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.append(getSignature(var2[var3]));
      }

      var1.append(")");
      var1.append(getSignature(var0.getReturnType()));
      return var1.toString();
   }

   private static String getSignature(Constructor var0) {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");
      Class[] var2 = var0.getParameterTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.append(getSignature(var2[var3]));
      }

      var1.append(")V");
      return var1.toString();
   }

   private static Field[] getDeclaredFields(final Class var0) {
      return (Field[])((Field[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return var0.getDeclaredFields();
         }
      }));
   }

   private static boolean hasStaticInitializer(Class var0) {
      if (hasStaticInitializerMethod == null) {
         Class var1 = null;

         try {
            if (var1 == null) {
               var1 = java.io.ObjectStreamClass.class;
            }

            hasStaticInitializerMethod = var1.getDeclaredMethod("hasStaticInitializer", Class.class);
         } catch (NoSuchMethodException var4) {
         }

         if (hasStaticInitializerMethod == null) {
            throw new InternalError("Can't find hasStaticInitializer method on " + var1.getName());
         }

         hasStaticInitializerMethod.setAccessible(true);
      }

      try {
         Boolean var5 = (Boolean)hasStaticInitializerMethod.invoke((Object)null, var0);
         return var5;
      } catch (Exception var3) {
         throw new InternalError("Error invoking hasStaticInitializer: " + var3);
      }
   }

   private static Method getDeclaredMethod(final Class var0, final String var1, final Class[] var2, final int var3, final int var4) {
      return (Method)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Method var1x = null;

            try {
               var1x = var0.getDeclaredMethod(var1, var2);
               int var2x = var1x.getModifiers();
               if ((var2x & var4) != 0 || (var2x & var3) != var3) {
                  var1x = null;
               }
            } catch (NoSuchMethodException var3x) {
            }

            return var1x;
         }
      });
   }

   private static class MethodSignature implements Comparator {
      Member member;
      String signature;

      static ObjectStreamClassUtil_1_3.MethodSignature[] removePrivateAndSort(Member[] var0) {
         int var1 = 0;

         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (!Modifier.isPrivate(var0[var2].getModifiers())) {
               ++var1;
            }
         }

         ObjectStreamClassUtil_1_3.MethodSignature[] var5 = new ObjectStreamClassUtil_1_3.MethodSignature[var1];
         int var3 = 0;

         for(int var4 = 0; var4 < var0.length; ++var4) {
            if (!Modifier.isPrivate(var0[var4].getModifiers())) {
               var5[var3] = new ObjectStreamClassUtil_1_3.MethodSignature(var0[var4]);
               ++var3;
            }
         }

         if (var3 > 0) {
            Arrays.sort(var5, var5[0]);
         }

         return var5;
      }

      public int compare(Object var1, Object var2) {
         if (var1 == var2) {
            return 0;
         } else {
            ObjectStreamClassUtil_1_3.MethodSignature var3 = (ObjectStreamClassUtil_1_3.MethodSignature)var1;
            ObjectStreamClassUtil_1_3.MethodSignature var4 = (ObjectStreamClassUtil_1_3.MethodSignature)var2;
            int var5;
            if (this.isConstructor()) {
               var5 = var3.signature.compareTo(var4.signature);
            } else {
               var5 = var3.member.getName().compareTo(var4.member.getName());
               if (var5 == 0) {
                  var5 = var3.signature.compareTo(var4.signature);
               }
            }

            return var5;
         }
      }

      private final boolean isConstructor() {
         return this.member instanceof Constructor;
      }

      private MethodSignature(Member var1) {
         this.member = var1;
         if (this.isConstructor()) {
            this.signature = ObjectStreamClassUtil_1_3.getSignature((Constructor)var1);
         } else {
            this.signature = ObjectStreamClassUtil_1_3.getSignature((Method)var1);
         }

      }
   }

   private static class CompareMemberByName implements Comparator {
      private CompareMemberByName() {
      }

      public int compare(Object var1, Object var2) {
         String var3 = ((Member)var1).getName();
         String var4 = ((Member)var2).getName();
         if (var1 instanceof Method) {
            var3 = var3 + ObjectStreamClassUtil_1_3.getSignature((Method)var1);
            var4 = var4 + ObjectStreamClassUtil_1_3.getSignature((Method)var2);
         } else if (var1 instanceof Constructor) {
            var3 = var3 + ObjectStreamClassUtil_1_3.getSignature((Constructor)var1);
            var4 = var4 + ObjectStreamClassUtil_1_3.getSignature((Constructor)var2);
         }

         return var3.compareTo(var4);
      }

      // $FF: synthetic method
      CompareMemberByName(Object var1) {
         this();
      }
   }

   private static class CompareClassByName implements Comparator {
      private CompareClassByName() {
      }

      public int compare(Object var1, Object var2) {
         Class var3 = (Class)var1;
         Class var4 = (Class)var2;
         return var3.getName().compareTo(var4.getName());
      }

      // $FF: synthetic method
      CompareClassByName(Object var1) {
         this();
      }
   }
}
