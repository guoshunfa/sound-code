package java.security;

import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.Debug;

public final class AccessController {
   private AccessController() {
   }

   @CallerSensitive
   public static native <T> T doPrivileged(PrivilegedAction<T> var0);

   @CallerSensitive
   public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> var0) {
      AccessControlContext var1 = getStackAccessControlContext();
      if (var1 == null) {
         return doPrivileged(var0);
      } else {
         DomainCombiner var2 = var1.getAssignedCombiner();
         return doPrivileged(var0, preserveCombiner(var2, Reflection.getCallerClass()));
      }
   }

   @CallerSensitive
   public static native <T> T doPrivileged(PrivilegedAction<T> var0, AccessControlContext var1);

   @CallerSensitive
   public static <T> T doPrivileged(PrivilegedAction<T> var0, AccessControlContext var1, Permission... var2) {
      AccessControlContext var3 = getContext();
      if (var2 == null) {
         throw new NullPointerException("null permissions parameter");
      } else {
         Class var4 = Reflection.getCallerClass();
         return doPrivileged(var0, createWrapper((DomainCombiner)null, var4, var3, var1, var2));
      }
   }

   @CallerSensitive
   public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> var0, AccessControlContext var1, Permission... var2) {
      AccessControlContext var3 = getContext();
      DomainCombiner var4 = var3.getCombiner();
      if (var4 == null && var1 != null) {
         var4 = var1.getCombiner();
      }

      if (var2 == null) {
         throw new NullPointerException("null permissions parameter");
      } else {
         Class var5 = Reflection.getCallerClass();
         return doPrivileged(var0, createWrapper(var4, var5, var3, var1, var2));
      }
   }

   @CallerSensitive
   public static native <T> T doPrivileged(PrivilegedExceptionAction<T> var0) throws PrivilegedActionException;

   @CallerSensitive
   public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> var0) throws PrivilegedActionException {
      AccessControlContext var1 = getStackAccessControlContext();
      if (var1 == null) {
         return doPrivileged(var0);
      } else {
         DomainCombiner var2 = var1.getAssignedCombiner();
         return doPrivileged(var0, preserveCombiner(var2, Reflection.getCallerClass()));
      }
   }

   private static AccessControlContext preserveCombiner(DomainCombiner var0, Class<?> var1) {
      return createWrapper(var0, var1, (AccessControlContext)null, (AccessControlContext)null, (Permission[])null);
   }

   private static AccessControlContext createWrapper(DomainCombiner var0, Class<?> var1, AccessControlContext var2, AccessControlContext var3, Permission[] var4) {
      ProtectionDomain var5 = getCallerPD(var1);
      if (var3 != null && !var3.isAuthorized() && System.getSecurityManager() != null && !var5.impliesCreateAccessControlContext()) {
         ProtectionDomain var6 = new ProtectionDomain((CodeSource)null, (PermissionCollection)null);
         return new AccessControlContext(new ProtectionDomain[]{var6});
      } else {
         return new AccessControlContext(var5, var0, var2, var3, var4);
      }
   }

   private static ProtectionDomain getCallerPD(final Class<?> var0) {
      ProtectionDomain var1 = (ProtectionDomain)doPrivileged(new PrivilegedAction<ProtectionDomain>() {
         public ProtectionDomain run() {
            return var0.getProtectionDomain();
         }
      });
      return var1;
   }

   @CallerSensitive
   public static native <T> T doPrivileged(PrivilegedExceptionAction<T> var0, AccessControlContext var1) throws PrivilegedActionException;

   @CallerSensitive
   public static <T> T doPrivileged(PrivilegedExceptionAction<T> var0, AccessControlContext var1, Permission... var2) throws PrivilegedActionException {
      AccessControlContext var3 = getContext();
      if (var2 == null) {
         throw new NullPointerException("null permissions parameter");
      } else {
         Class var4 = Reflection.getCallerClass();
         return doPrivileged(var0, createWrapper((DomainCombiner)null, var4, var3, var1, var2));
      }
   }

   @CallerSensitive
   public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> var0, AccessControlContext var1, Permission... var2) throws PrivilegedActionException {
      AccessControlContext var3 = getContext();
      DomainCombiner var4 = var3.getCombiner();
      if (var4 == null && var1 != null) {
         var4 = var1.getCombiner();
      }

      if (var2 == null) {
         throw new NullPointerException("null permissions parameter");
      } else {
         Class var5 = Reflection.getCallerClass();
         return doPrivileged(var0, createWrapper(var4, var5, var3, var1, var2));
      }
   }

   private static native AccessControlContext getStackAccessControlContext();

   static native AccessControlContext getInheritedAccessControlContext();

   public static AccessControlContext getContext() {
      AccessControlContext var0 = getStackAccessControlContext();
      return var0 == null ? new AccessControlContext((ProtectionDomain[])null, true) : var0.optimize();
   }

   public static void checkPermission(Permission var0) throws AccessControlException {
      if (var0 == null) {
         throw new NullPointerException("permission can't be null");
      } else {
         AccessControlContext var1 = getStackAccessControlContext();
         if (var1 != null) {
            AccessControlContext var4 = var1.optimize();
            var4.checkPermission(var0);
         } else {
            Debug var2 = AccessControlContext.getDebug();
            boolean var3 = false;
            if (var2 != null) {
               var3 = !Debug.isOn("codebase=");
               var3 &= !Debug.isOn("permission=") || Debug.isOn("permission=" + var0.getClass().getCanonicalName());
            }

            if (var3 && Debug.isOn("stack")) {
               Thread.dumpStack();
            }

            if (var3 && Debug.isOn("domain")) {
               var2.println("domain (context is null)");
            }

            if (var3) {
               var2.println("access allowed " + var0);
            }

         }
      }
   }
}
