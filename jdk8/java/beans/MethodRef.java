package java.beans;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

final class MethodRef {
   private String signature;
   private SoftReference<Method> methodRef;
   private WeakReference<Class<?>> typeRef;

   void set(Method var1) {
      if (var1 == null) {
         this.signature = null;
         this.methodRef = null;
         this.typeRef = null;
      } else {
         this.signature = var1.toGenericString();
         this.methodRef = new SoftReference(var1);
         this.typeRef = new WeakReference(var1.getDeclaringClass());
      }

   }

   boolean isSet() {
      return this.methodRef != null;
   }

   Method get() {
      if (this.methodRef == null) {
         return null;
      } else {
         Method var1 = (Method)this.methodRef.get();
         if (var1 == null) {
            var1 = find((Class)this.typeRef.get(), this.signature);
            if (var1 == null) {
               this.signature = null;
               this.methodRef = null;
               this.typeRef = null;
               return null;
            }

            this.methodRef = new SoftReference(var1);
         }

         return ReflectUtil.isPackageAccessible(var1.getDeclaringClass()) ? var1 : null;
      }
   }

   private static Method find(Class<?> var0, String var1) {
      if (var0 != null) {
         Method[] var2 = var0.getMethods();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Method var5 = var2[var4];
            if (var0.equals(var5.getDeclaringClass()) && var5.toGenericString().equals(var1)) {
               return var5;
            }
         }
      }

      return null;
   }
}
