package sun.reflect.misc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import sun.misc.IOUtils;

public final class MethodUtil extends SecureClassLoader {
   private static final String MISC_PKG = "sun.reflect.misc.";
   private static final String TRAMPOLINE = "sun.reflect.misc.Trampoline";
   private static final Method bounce = getTrampoline();

   private MethodUtil() {
   }

   public static Method getMethod(Class<?> var0, String var1, Class<?>[] var2) throws NoSuchMethodException {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getMethod(var1, var2);
   }

   public static Method[] getMethods(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getMethods();
   }

   public static Method[] getPublicMethods(Class<?> var0) {
      if (System.getSecurityManager() == null) {
         return var0.getMethods();
      } else {
         HashMap var1;
         for(var1 = new HashMap(); var0 != null; var0 = var0.getSuperclass()) {
            boolean var2 = getInternalPublicMethods(var0, var1);
            if (var2) {
               break;
            }

            getInterfaceMethods(var0, var1);
         }

         return (Method[])var1.values().toArray(new Method[var1.size()]);
      }
   }

   private static void getInterfaceMethods(Class<?> var0, Map<MethodUtil.Signature, Method> var1) {
      Class[] var2 = var0.getInterfaces();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Class var4 = var2[var3];
         boolean var5 = getInternalPublicMethods(var4, var1);
         if (!var5) {
            getInterfaceMethods(var4, var1);
         }
      }

   }

   private static boolean getInternalPublicMethods(Class<?> var0, Map<MethodUtil.Signature, Method> var1) {
      Method[] var2 = null;

      try {
         if (!Modifier.isPublic(var0.getModifiers())) {
            return false;
         }

         if (!ReflectUtil.isPackageAccessible(var0)) {
            return false;
         }

         var2 = var0.getMethods();
      } catch (SecurityException var6) {
         return false;
      }

      boolean var3 = true;

      int var4;
      Class var5;
      for(var4 = 0; var4 < var2.length; ++var4) {
         var5 = var2[var4].getDeclaringClass();
         if (!Modifier.isPublic(var5.getModifiers())) {
            var3 = false;
            break;
         }
      }

      if (var3) {
         for(var4 = 0; var4 < var2.length; ++var4) {
            addMethod(var1, var2[var4]);
         }
      } else {
         for(var4 = 0; var4 < var2.length; ++var4) {
            var5 = var2[var4].getDeclaringClass();
            if (var0.equals(var5)) {
               addMethod(var1, var2[var4]);
            }
         }
      }

      return var3;
   }

   private static void addMethod(Map<MethodUtil.Signature, Method> var0, Method var1) {
      MethodUtil.Signature var2 = new MethodUtil.Signature(var1);
      if (!var0.containsKey(var2)) {
         var0.put(var2, var1);
      } else if (!var1.getDeclaringClass().isInterface()) {
         Method var3 = (Method)var0.get(var2);
         if (var3.getDeclaringClass().isInterface()) {
            var0.put(var2, var1);
         }
      }

   }

   public static Object invoke(Method var0, Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
      try {
         return bounce.invoke((Object)null, var0, var1, var2);
      } catch (InvocationTargetException var5) {
         Throwable var4 = var5.getCause();
         if (var4 instanceof InvocationTargetException) {
            throw (InvocationTargetException)var4;
         } else if (var4 instanceof IllegalAccessException) {
            throw (IllegalAccessException)var4;
         } else if (var4 instanceof RuntimeException) {
            throw (RuntimeException)var4;
         } else if (var4 instanceof Error) {
            throw (Error)var4;
         } else {
            throw new Error("Unexpected invocation error", var4);
         }
      } catch (IllegalAccessException var6) {
         throw new Error("Unexpected invocation error", var6);
      }
   }

   private static Method getTrampoline() {
      try {
         return (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               Class var1 = MethodUtil.getTrampolineClass();
               Class[] var2 = new Class[]{Method.class, Object.class, Object[].class};
               Method var3 = var1.getDeclaredMethod("invoke", var2);
               var3.setAccessible(true);
               return var3;
            }
         });
      } catch (Exception var1) {
         throw new InternalError("bouncer cannot be found", var1);
      }
   }

   protected synchronized Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var1);
      Class var3 = this.findLoadedClass(var1);
      if (var3 == null) {
         try {
            var3 = this.findClass(var1);
         } catch (ClassNotFoundException var5) {
         }

         if (var3 == null) {
            var3 = this.getParent().loadClass(var1);
         }
      }

      if (var2) {
         this.resolveClass(var3);
      }

      return var3;
   }

   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      if (!var1.startsWith("sun.reflect.misc.")) {
         throw new ClassNotFoundException(var1);
      } else {
         String var2 = var1.replace('.', '/').concat(".class");
         URL var3 = this.getResource(var2);
         if (var3 != null) {
            try {
               return this.defineClass(var1, var3);
            } catch (IOException var5) {
               throw new ClassNotFoundException(var1, var5);
            }
         } else {
            throw new ClassNotFoundException(var1);
         }
      }
   }

   private Class<?> defineClass(String var1, URL var2) throws IOException {
      byte[] var3 = getBytes(var2);
      CodeSource var4 = new CodeSource((URL)null, (Certificate[])null);
      if (!var1.equals("sun.reflect.misc.Trampoline")) {
         throw new IOException("MethodUtil: bad name " + var1);
      } else {
         return this.defineClass(var1, var3, 0, var3.length, var4);
      }
   }

   private static byte[] getBytes(URL var0) throws IOException {
      URLConnection var1 = var0.openConnection();
      if (var1 instanceof HttpURLConnection) {
         HttpURLConnection var2 = (HttpURLConnection)var1;
         int var3 = var2.getResponseCode();
         if (var3 >= 400) {
            throw new IOException("open HTTP connection failed.");
         }
      }

      int var8 = var1.getContentLength();
      BufferedInputStream var9 = new BufferedInputStream(var1.getInputStream());

      byte[] var4;
      try {
         var4 = IOUtils.readFully(var9, var8, true);
      } finally {
         var9.close();
      }

      return var4;
   }

   protected PermissionCollection getPermissions(CodeSource var1) {
      PermissionCollection var2 = super.getPermissions(var1);
      var2.add(new AllPermission());
      return var2;
   }

   private static Class<?> getTrampolineClass() {
      try {
         return Class.forName("sun.reflect.misc.Trampoline", true, new MethodUtil());
      } catch (ClassNotFoundException var1) {
         return null;
      }
   }

   private static class Signature {
      private String methodName;
      private Class<?>[] argClasses;
      private volatile int hashCode = 0;

      Signature(Method var1) {
         this.methodName = var1.getName();
         this.argClasses = var1.getParameterTypes();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            MethodUtil.Signature var2 = (MethodUtil.Signature)var1;
            if (!this.methodName.equals(var2.methodName)) {
               return false;
            } else if (this.argClasses.length != var2.argClasses.length) {
               return false;
            } else {
               for(int var3 = 0; var3 < this.argClasses.length; ++var3) {
                  if (this.argClasses[var3] != var2.argClasses[var3]) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         if (this.hashCode == 0) {
            byte var1 = 17;
            int var3 = 37 * var1 + this.methodName.hashCode();
            if (this.argClasses != null) {
               for(int var2 = 0; var2 < this.argClasses.length; ++var2) {
                  var3 = 37 * var3 + (this.argClasses[var2] == null ? 0 : this.argClasses[var2].hashCode());
               }
            }

            this.hashCode = var3;
         }

         return this.hashCode;
      }
   }
}
