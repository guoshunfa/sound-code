package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;

public class AccessibleObject implements AnnotatedElement {
   private static final Permission ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
   boolean override;
   static final ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged((PrivilegedAction)(new ReflectionFactory.GetReflectionFactoryAction()));
   volatile Object securityCheckCache;

   public static void setAccessible(AccessibleObject[] var0, boolean var1) throws SecurityException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(ACCESS_PERMISSION);
      }

      for(int var3 = 0; var3 < var0.length; ++var3) {
         setAccessible0(var0[var3], var1);
      }

   }

   public void setAccessible(boolean var1) throws SecurityException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(ACCESS_PERMISSION);
      }

      setAccessible0(this, var1);
   }

   private static void setAccessible0(AccessibleObject var0, boolean var1) throws SecurityException {
      if (var0 instanceof Constructor && var1) {
         Constructor var2 = (Constructor)var0;
         if (var2.getDeclaringClass() == Class.class) {
            throw new SecurityException("Cannot make a java.lang.Class constructor accessible");
         }
      }

      var0.override = var1;
   }

   public boolean isAccessible() {
      return this.override;
   }

   protected AccessibleObject() {
   }

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      throw new AssertionError("All subclasses should override this method");
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> var1) {
      return AnnotatedElement.super.isAnnotationPresent(var1);
   }

   public <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
      throw new AssertionError("All subclasses should override this method");
   }

   public Annotation[] getAnnotations() {
      return this.getDeclaredAnnotations();
   }

   public <T extends Annotation> T getDeclaredAnnotation(Class<T> var1) {
      return this.getAnnotation(var1);
   }

   public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> var1) {
      return this.getAnnotationsByType(var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      throw new AssertionError("All subclasses should override this method");
   }

   void checkAccess(Class<?> var1, Class<?> var2, Object var3, int var4) throws IllegalAccessException {
      if (var1 != var2) {
         Object var5 = this.securityCheckCache;
         Class var6 = var2;
         if (var3 != null && Modifier.isProtected(var4) && (var6 = var3.getClass()) != var2) {
            if (var5 instanceof Class[]) {
               Class[] var7 = (Class[])((Class[])var5);
               if (var7[1] == var6 && var7[0] == var1) {
                  return;
               }
            }
         } else if (var5 == var1) {
            return;
         }

         this.slowCheckMemberAccess(var1, var2, var3, var4, var6);
      }
   }

   void slowCheckMemberAccess(Class<?> var1, Class<?> var2, Object var3, int var4, Class<?> var5) throws IllegalAccessException {
      Reflection.ensureMemberAccess(var1, var2, var3, var4);
      Object var6 = var5 == var2 ? var1 : new Class[]{var1, var5};
      this.securityCheckCache = var6;
   }
}
