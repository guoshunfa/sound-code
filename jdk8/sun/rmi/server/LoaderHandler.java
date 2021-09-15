package sun.rmi.server;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.rmi.server.LogStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

public final class LoaderHandler {
   static final int logLevel = LogStream.parseLevel((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.loader.logLevel"))));
   static final Log loaderLog;
   private static String codebaseProperty;
   private static URL[] codebaseURLs;
   private static final Map<ClassLoader, Void> codebaseLoaders;
   private static final HashMap<LoaderHandler.LoaderKey, LoaderHandler.LoaderEntry> loaderTable;
   private static final ReferenceQueue<LoaderHandler.Loader> refQueue;
   private static final Map<String, Object[]> pathToURLsCache;

   private LoaderHandler() {
   }

   private static synchronized URL[] getDefaultCodebaseURLs() throws MalformedURLException {
      if (codebaseURLs == null) {
         if (codebaseProperty != null) {
            codebaseURLs = pathToURLs(codebaseProperty);
         } else {
            codebaseURLs = new URL[0];
         }
      }

      return codebaseURLs;
   }

   public static Class<?> loadClass(String var0, String var1, ClassLoader var2) throws MalformedURLException, ClassNotFoundException {
      if (loaderLog.isLoggable(Log.BRIEF)) {
         loaderLog.log(Log.BRIEF, "name = \"" + var1 + "\", codebase = \"" + (var0 != null ? var0 : "") + "\"" + (var2 != null ? ", defaultLoader = " + var2 : ""));
      }

      URL[] var3;
      if (var0 != null) {
         var3 = pathToURLs(var0);
      } else {
         var3 = getDefaultCodebaseURLs();
      }

      if (var2 != null) {
         try {
            Class var4 = loadClassForName(var1, false, var2);
            if (loaderLog.isLoggable(Log.VERBOSE)) {
               loaderLog.log(Log.VERBOSE, "class \"" + var1 + "\" found via defaultLoader, defined by " + var4.getClassLoader());
            }

            return var4;
         } catch (ClassNotFoundException var5) {
         }
      }

      return loadClass(var3, var1);
   }

   public static String getClassAnnotation(Class<?> var0) {
      String var1 = var0.getName();
      int var2 = var1.length();
      if (var2 > 0 && var1.charAt(0) == '[') {
         int var3;
         for(var3 = 1; var2 > var3 && var1.charAt(var3) == '['; ++var3) {
         }

         if (var2 > var3 && var1.charAt(var3) != 'L') {
            return null;
         }
      }

      ClassLoader var11 = var0.getClassLoader();
      if (var11 != null && !codebaseLoaders.containsKey(var11)) {
         String var4 = null;
         if (var11 instanceof LoaderHandler.Loader) {
            var4 = ((LoaderHandler.Loader)var11).getClassAnnotation();
         } else if (var11 instanceof URLClassLoader) {
            try {
               URL[] var5 = ((URLClassLoader)var11).getURLs();
               if (var5 != null) {
                  SecurityManager var6 = System.getSecurityManager();
                  if (var6 != null) {
                     Permissions var7 = new Permissions();

                     for(int var8 = 0; var8 < var5.length; ++var8) {
                        Permission var9 = var5[var8].openConnection().getPermission();
                        if (var9 != null && !var7.implies(var9)) {
                           var6.checkPermission(var9);
                           var7.add(var9);
                        }
                     }
                  }

                  var4 = urlsToPath(var5);
               }
            } catch (IOException | SecurityException var10) {
            }
         }

         return var4 != null ? var4 : codebaseProperty;
      } else {
         return codebaseProperty;
      }
   }

   public static ClassLoader getClassLoader(String var0) throws MalformedURLException {
      ClassLoader var1 = getRMIContextClassLoader();
      URL[] var2;
      if (var0 != null) {
         var2 = pathToURLs(var0);
      } else {
         var2 = getDefaultCodebaseURLs();
      }

      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(new RuntimePermission("getClassLoader"));
         LoaderHandler.Loader var4 = lookupLoader(var2, var1);
         if (var4 != null) {
            var4.checkPermissions();
         }

         return var4;
      } else {
         return var1;
      }
   }

   public static Object getSecurityContext(ClassLoader var0) {
      if (var0 instanceof LoaderHandler.Loader) {
         URL[] var1 = ((LoaderHandler.Loader)var0).getURLs();
         if (var1.length > 0) {
            return var1[0];
         }
      }

      return null;
   }

   public static void registerCodebaseLoader(ClassLoader var0) {
      codebaseLoaders.put(var0, (Object)null);
   }

   private static Class<?> loadClass(URL[] var0, String var1) throws ClassNotFoundException {
      ClassLoader var2 = getRMIContextClassLoader();
      if (loaderLog.isLoggable(Log.VERBOSE)) {
         loaderLog.log(Log.VERBOSE, "(thread context class loader: " + var2 + ")");
      }

      SecurityManager var3 = System.getSecurityManager();
      if (var3 == null) {
         try {
            Class var11 = Class.forName(var1, false, var2);
            if (loaderLog.isLoggable(Log.VERBOSE)) {
               loaderLog.log(Log.VERBOSE, "class \"" + var1 + "\" found via thread context class loader (no security manager: codebase disabled), defined by " + var11.getClassLoader());
            }

            return var11;
         } catch (ClassNotFoundException var8) {
            if (loaderLog.isLoggable(Log.BRIEF)) {
               loaderLog.log(Log.BRIEF, "class \"" + var1 + "\" not found via thread context class loader (no security manager: codebase disabled)", var8);
            }

            throw new ClassNotFoundException(var8.getMessage() + " (no security manager: RMI class loader disabled)", var8.getException());
         }
      } else {
         LoaderHandler.Loader var4 = lookupLoader(var0, var2);

         try {
            if (var4 != null) {
               var4.checkPermissions();
            }
         } catch (SecurityException var10) {
            try {
               Class var6 = loadClassForName(var1, false, var2);
               if (loaderLog.isLoggable(Log.VERBOSE)) {
                  loaderLog.log(Log.VERBOSE, "class \"" + var1 + "\" found via thread context class loader (access to codebase denied), defined by " + var6.getClassLoader());
               }

               return var6;
            } catch (ClassNotFoundException var7) {
               if (loaderLog.isLoggable(Log.BRIEF)) {
                  loaderLog.log(Log.BRIEF, "class \"" + var1 + "\" not found via thread context class loader (access to codebase denied)", var10);
               }

               throw new ClassNotFoundException("access to class loader denied", var10);
            }
         }

         try {
            Class var5 = loadClassForName(var1, false, var4);
            if (loaderLog.isLoggable(Log.VERBOSE)) {
               loaderLog.log(Log.VERBOSE, "class \"" + var1 + "\" found via codebase, defined by " + var5.getClassLoader());
            }

            return var5;
         } catch (ClassNotFoundException var9) {
            if (loaderLog.isLoggable(Log.BRIEF)) {
               loaderLog.log(Log.BRIEF, "class \"" + var1 + "\" not found via codebase", var9);
            }

            throw var9;
         }
      }
   }

   public static Class<?> loadProxyClass(String var0, String[] var1, ClassLoader var2) throws MalformedURLException, ClassNotFoundException {
      if (loaderLog.isLoggable(Log.BRIEF)) {
         loaderLog.log(Log.BRIEF, "interfaces = " + Arrays.asList(var1) + ", codebase = \"" + (var0 != null ? var0 : "") + "\"" + (var2 != null ? ", defaultLoader = " + var2 : ""));
      }

      ClassLoader var3 = getRMIContextClassLoader();
      if (loaderLog.isLoggable(Log.VERBOSE)) {
         loaderLog.log(Log.VERBOSE, "(thread context class loader: " + var3 + ")");
      }

      URL[] var4;
      if (var0 != null) {
         var4 = pathToURLs(var0);
      } else {
         var4 = getDefaultCodebaseURLs();
      }

      SecurityManager var5 = System.getSecurityManager();
      if (var5 == null) {
         try {
            Class var13 = loadProxyClass(var1, var2, var3, false);
            if (loaderLog.isLoggable(Log.VERBOSE)) {
               loaderLog.log(Log.VERBOSE, "(no security manager: codebase disabled) proxy class defined by " + var13.getClassLoader());
            }

            return var13;
         } catch (ClassNotFoundException var10) {
            if (loaderLog.isLoggable(Log.BRIEF)) {
               loaderLog.log(Log.BRIEF, "(no security manager: codebase disabled) proxy class resolution failed", var10);
            }

            throw new ClassNotFoundException(var10.getMessage() + " (no security manager: RMI class loader disabled)", var10.getException());
         }
      } else {
         LoaderHandler.Loader var6 = lookupLoader(var4, var3);

         try {
            if (var6 != null) {
               var6.checkPermissions();
            }
         } catch (SecurityException var12) {
            try {
               Class var8 = loadProxyClass(var1, var2, var3, false);
               if (loaderLog.isLoggable(Log.VERBOSE)) {
                  loaderLog.log(Log.VERBOSE, "(access to codebase denied) proxy class defined by " + var8.getClassLoader());
               }

               return var8;
            } catch (ClassNotFoundException var9) {
               if (loaderLog.isLoggable(Log.BRIEF)) {
                  loaderLog.log(Log.BRIEF, "(access to codebase denied) proxy class resolution failed", var12);
               }

               throw new ClassNotFoundException("access to class loader denied", var12);
            }
         }

         try {
            Class var7 = loadProxyClass(var1, var2, var6, true);
            if (loaderLog.isLoggable(Log.VERBOSE)) {
               loaderLog.log(Log.VERBOSE, "proxy class defined by " + var7.getClassLoader());
            }

            return var7;
         } catch (ClassNotFoundException var11) {
            if (loaderLog.isLoggable(Log.BRIEF)) {
               loaderLog.log(Log.BRIEF, "proxy class resolution failed", var11);
            }

            throw var11;
         }
      }
   }

   private static Class<?> loadProxyClass(String[] var0, ClassLoader var1, ClassLoader var2, boolean var3) throws ClassNotFoundException {
      ClassLoader var4 = null;
      Class[] var5 = new Class[var0.length];
      boolean[] var6 = new boolean[]{false};
      ClassLoader[] var7;
      int var8;
      if (var1 != null) {
         label62: {
            try {
               var4 = loadProxyInterfaces(var0, var1, var5, var6);
               if (loaderLog.isLoggable(Log.VERBOSE)) {
                  var7 = new ClassLoader[var5.length];

                  for(var8 = 0; var8 < var7.length; ++var8) {
                     var7[var8] = var5[var8].getClassLoader();
                  }

                  loaderLog.log(Log.VERBOSE, "proxy interfaces found via defaultLoader, defined by " + Arrays.asList(var7));
               }
            } catch (ClassNotFoundException var10) {
               break label62;
            }

            if (!var6[0]) {
               if (var3) {
                  try {
                     return Proxy.getProxyClass(var2, var5);
                  } catch (IllegalArgumentException var9) {
                  }
               }

               var4 = var1;
            }

            return loadProxyClass(var4, var5);
         }
      }

      var6[0] = false;
      var4 = loadProxyInterfaces(var0, var2, var5, var6);
      if (loaderLog.isLoggable(Log.VERBOSE)) {
         var7 = new ClassLoader[var5.length];

         for(var8 = 0; var8 < var7.length; ++var8) {
            var7[var8] = var5[var8].getClassLoader();
         }

         loaderLog.log(Log.VERBOSE, "proxy interfaces found via codebase, defined by " + Arrays.asList(var7));
      }

      if (!var6[0]) {
         var4 = var2;
      }

      return loadProxyClass(var4, var5);
   }

   private static Class<?> loadProxyClass(ClassLoader var0, Class<?>[] var1) throws ClassNotFoundException {
      try {
         return Proxy.getProxyClass(var0, var1);
      } catch (IllegalArgumentException var3) {
         throw new ClassNotFoundException("error creating dynamic proxy class", var3);
      }
   }

   private static ClassLoader loadProxyInterfaces(String[] var0, ClassLoader var1, Class<?>[] var2, boolean[] var3) throws ClassNotFoundException {
      ClassLoader var4 = null;

      for(int var5 = 0; var5 < var0.length; ++var5) {
         Class var6 = var2[var5] = loadClassForName(var0[var5], false, var1);
         if (!Modifier.isPublic(var6.getModifiers())) {
            ClassLoader var7 = var6.getClassLoader();
            if (loaderLog.isLoggable(Log.VERBOSE)) {
               loaderLog.log(Log.VERBOSE, "non-public interface \"" + var0[var5] + "\" defined by " + var7);
            }

            if (!var3[0]) {
               var4 = var7;
               var3[0] = true;
            } else if (var7 != var4) {
               throw new IllegalAccessError("non-public interfaces defined in different class loaders");
            }
         }
      }

      return var4;
   }

   private static URL[] pathToURLs(String var0) throws MalformedURLException {
      synchronized(pathToURLsCache) {
         Object[] var2 = (Object[])pathToURLsCache.get(var0);
         if (var2 != null) {
            return (URL[])((URL[])var2[0]);
         }
      }

      StringTokenizer var1 = new StringTokenizer(var0);
      URL[] var7 = new URL[var1.countTokens()];

      for(int var3 = 0; var1.hasMoreTokens(); ++var3) {
         var7[var3] = new URL(var1.nextToken());
      }

      synchronized(pathToURLsCache) {
         pathToURLsCache.put(var0, new Object[]{var7, new SoftReference(var0)});
         return var7;
      }
   }

   private static String urlsToPath(URL[] var0) {
      if (var0.length == 0) {
         return null;
      } else if (var0.length == 1) {
         return var0[0].toExternalForm();
      } else {
         StringBuffer var1 = new StringBuffer(var0[0].toExternalForm());

         for(int var2 = 1; var2 < var0.length; ++var2) {
            var1.append(' ');
            var1.append(var0[var2].toExternalForm());
         }

         return var1.toString();
      }
   }

   private static ClassLoader getRMIContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
   }

   private static LoaderHandler.Loader lookupLoader(final URL[] var0, final ClassLoader var1) {
      Class var4 = LoaderHandler.class;
      synchronized(LoaderHandler.class) {
         LoaderHandler.LoaderEntry var2;
         while((var2 = (LoaderHandler.LoaderEntry)refQueue.poll()) != null) {
            if (!var2.removed) {
               loaderTable.remove(var2.key);
            }
         }

         LoaderHandler.LoaderKey var5 = new LoaderHandler.LoaderKey(var0, var1);
         var2 = (LoaderHandler.LoaderEntry)loaderTable.get(var5);
         LoaderHandler.Loader var3;
         if (var2 == null || (var3 = (LoaderHandler.Loader)var2.get()) == null) {
            if (var2 != null) {
               loaderTable.remove(var5);
               var2.removed = true;
            }

            AccessControlContext var6 = getLoaderAccessControlContext(var0);
            var3 = (LoaderHandler.Loader)AccessController.doPrivileged(new PrivilegedAction<LoaderHandler.Loader>() {
               public LoaderHandler.Loader run() {
                  return new LoaderHandler.Loader(var0, var1);
               }
            }, var6);
            var2 = new LoaderHandler.LoaderEntry(var5, var3);
            loaderTable.put(var5, var2);
         }

         return var3;
      }
   }

   private static AccessControlContext getLoaderAccessControlContext(URL[] var0) {
      PermissionCollection var1 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
         public PermissionCollection run() {
            CodeSource var1 = new CodeSource((URL)null, (Certificate[])null);
            Policy var2 = Policy.getPolicy();
            return (PermissionCollection)(var2 != null ? var2.getPermissions(var1) : new Permissions());
         }
      });
      var1.add(new RuntimePermission("createClassLoader"));
      var1.add(new PropertyPermission("java.*", "read"));
      addPermissionsForURLs(var0, var1, true);
      ProtectionDomain var2 = new ProtectionDomain(new CodeSource(var0.length > 0 ? var0[0] : null, (Certificate[])null), var1);
      return new AccessControlContext(new ProtectionDomain[]{var2});
   }

   private static void addPermissionsForURLs(URL[] var0, PermissionCollection var1, boolean var2) {
      for(int var3 = 0; var3 < var0.length; ++var3) {
         URL var4 = var0[var3];

         try {
            URLConnection var5 = var4.openConnection();
            Permission var6 = var5.getPermission();
            if (var6 != null) {
               if (var6 instanceof FilePermission) {
                  String var7 = var6.getName();
                  int var8 = var7.lastIndexOf(File.separatorChar);
                  if (var8 != -1) {
                     var7 = var7.substring(0, var8 + 1);
                     if (var7.endsWith(File.separator)) {
                        var7 = var7 + "-";
                     }

                     FilePermission var9 = new FilePermission(var7, "read");
                     if (!var1.implies(var9)) {
                        var1.add(var9);
                     }

                     var1.add(new FilePermission(var7, "read"));
                  } else if (!var1.implies(var6)) {
                     var1.add(var6);
                  }
               } else {
                  if (!var1.implies(var6)) {
                     var1.add(var6);
                  }

                  if (var2) {
                     URL var11 = var4;

                     for(URLConnection var12 = var5; var12 instanceof JarURLConnection; var12 = var11.openConnection()) {
                        var11 = ((JarURLConnection)var12).getJarFileURL();
                     }

                     String var13 = var11.getHost();
                     if (var13 != null && var6.implies(new SocketPermission(var13, "resolve"))) {
                        SocketPermission var14 = new SocketPermission(var13, "connect,accept");
                        if (!var1.implies(var14)) {
                           var1.add(var14);
                        }
                     }
                  }
               }
            }
         } catch (IOException var10) {
         }
      }

   }

   private static Class<?> loadClassForName(String var0, boolean var1, ClassLoader var2) throws ClassNotFoundException {
      if (var2 == null) {
         ReflectUtil.checkPackageAccess(var0);
      }

      return Class.forName(var0, var1, var2);
   }

   static {
      loaderLog = Log.getLog("sun.rmi.loader", "loader", logLevel);
      codebaseProperty = null;
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.codebase")));
      if (var0 != null && var0.trim().length() > 0) {
         codebaseProperty = var0;
      }

      codebaseURLs = null;
      codebaseLoaders = Collections.synchronizedMap(new IdentityHashMap(5));

      for(ClassLoader var1 = ClassLoader.getSystemClassLoader(); var1 != null; var1 = var1.getParent()) {
         codebaseLoaders.put(var1, (Object)null);
      }

      loaderTable = new HashMap(5);
      refQueue = new ReferenceQueue();
      pathToURLsCache = new WeakHashMap(5);
   }

   private static class Loader extends URLClassLoader {
      private ClassLoader parent;
      private String annotation;
      private Permissions permissions;

      private Loader(URL[] var1, ClassLoader var2) {
         super(var1, var2);
         this.parent = var2;
         this.permissions = new Permissions();
         LoaderHandler.addPermissionsForURLs(var1, this.permissions, false);
         this.annotation = LoaderHandler.urlsToPath(var1);
      }

      public String getClassAnnotation() {
         return this.annotation;
      }

      private void checkPermissions() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            Enumeration var2 = this.permissions.elements();

            while(var2.hasMoreElements()) {
               var1.checkPermission((Permission)var2.nextElement());
            }
         }

      }

      protected PermissionCollection getPermissions(CodeSource var1) {
         PermissionCollection var2 = super.getPermissions(var1);
         return var2;
      }

      public String toString() {
         return super.toString() + "[\"" + this.annotation + "\"]";
      }

      protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
         if (this.parent == null) {
            ReflectUtil.checkPackageAccess(var1);
         }

         return super.loadClass(var1, var2);
      }

      // $FF: synthetic method
      Loader(URL[] var1, ClassLoader var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class LoaderEntry extends WeakReference<LoaderHandler.Loader> {
      public LoaderHandler.LoaderKey key;
      public boolean removed = false;

      public LoaderEntry(LoaderHandler.LoaderKey var1, LoaderHandler.Loader var2) {
         super(var2, LoaderHandler.refQueue);
         this.key = var1;
      }
   }

   private static class LoaderKey {
      private URL[] urls;
      private ClassLoader parent;
      private int hashValue;

      public LoaderKey(URL[] var1, ClassLoader var2) {
         this.urls = var1;
         this.parent = var2;
         if (var2 != null) {
            this.hashValue = var2.hashCode();
         }

         for(int var3 = 0; var3 < var1.length; ++var3) {
            this.hashValue ^= var1[var3].hashCode();
         }

      }

      public int hashCode() {
         return this.hashValue;
      }

      public boolean equals(Object var1) {
         if (var1 instanceof LoaderHandler.LoaderKey) {
            LoaderHandler.LoaderKey var2 = (LoaderHandler.LoaderKey)var1;
            if (this.parent != var2.parent) {
               return false;
            } else if (this.urls == var2.urls) {
               return true;
            } else if (this.urls.length != var2.urls.length) {
               return false;
            } else {
               for(int var3 = 0; var3 < this.urls.length; ++var3) {
                  if (!this.urls[var3].equals(var2.urls[var3])) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }
}
