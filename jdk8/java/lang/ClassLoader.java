package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import sun.misc.CompoundEnumeration;
import sun.misc.Launcher;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.misc.URLClassPath;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public abstract class ClassLoader {
   private final ClassLoader parent;
   private final ConcurrentHashMap<String, Object> parallelLockMap;
   private final Map<String, Certificate[]> package2certs;
   private static final Certificate[] nocerts;
   private final Vector<Class<?>> classes;
   private final ProtectionDomain defaultDomain;
   private final Set<ProtectionDomain> domains;
   private final HashMap<String, Package> packages;
   private static ClassLoader scl;
   private static boolean sclSet;
   private static Vector<String> loadedLibraryNames;
   private static Vector<ClassLoader.NativeLibrary> systemNativeLibraries;
   private Vector<ClassLoader.NativeLibrary> nativeLibraries;
   private static Stack<ClassLoader.NativeLibrary> nativeLibraryContext;
   private static String[] usr_paths;
   private static String[] sys_paths;
   final Object assertionLock;
   private boolean defaultAssertionStatus;
   private Map<String, Boolean> packageAssertionStatus;
   Map<String, Boolean> classAssertionStatus;

   private static native void registerNatives();

   void addClass(Class<?> var1) {
      this.classes.addElement(var1);
   }

   private static Void checkCreateClassLoader() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkCreateClassLoader();
      }

      return null;
   }

   private ClassLoader(Void var1, ClassLoader var2) {
      this.classes = new Vector();
      this.defaultDomain = new ProtectionDomain(new CodeSource((URL)null, (Certificate[])null), (PermissionCollection)null, this, (Principal[])null);
      this.packages = new HashMap();
      this.nativeLibraries = new Vector();
      this.defaultAssertionStatus = false;
      this.packageAssertionStatus = null;
      this.classAssertionStatus = null;
      this.parent = var2;
      if (ClassLoader.ParallelLoaders.isRegistered(this.getClass())) {
         this.parallelLockMap = new ConcurrentHashMap();
         this.package2certs = new ConcurrentHashMap();
         this.domains = Collections.synchronizedSet(new HashSet());
         this.assertionLock = new Object();
      } else {
         this.parallelLockMap = null;
         this.package2certs = new Hashtable();
         this.domains = new HashSet();
         this.assertionLock = this;
      }

   }

   protected ClassLoader(ClassLoader var1) {
      this(checkCreateClassLoader(), var1);
   }

   protected ClassLoader() {
      this(checkCreateClassLoader(), getSystemClassLoader());
   }

   public Class<?> loadClass(String var1) throws ClassNotFoundException {
      return this.loadClass(var1, false);
   }

   protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      synchronized(this.getClassLoadingLock(var1)) {
         Class var4 = this.findLoadedClass(var1);
         if (var4 == null) {
            long var5 = System.nanoTime();

            try {
               if (this.parent != null) {
                  var4 = this.parent.loadClass(var1, false);
               } else {
                  var4 = this.findBootstrapClassOrNull(var1);
               }
            } catch (ClassNotFoundException var10) {
            }

            if (var4 == null) {
               long var7 = System.nanoTime();
               var4 = this.findClass(var1);
               PerfCounter.getParentDelegationTime().addTime(var7 - var5);
               PerfCounter.getFindClassTime().addElapsedTimeFrom(var7);
               PerfCounter.getFindClasses().increment();
            }
         }

         if (var2) {
            this.resolveClass(var4);
         }

         return var4;
      }
   }

   protected Object getClassLoadingLock(String var1) {
      Object var2 = this;
      if (this.parallelLockMap != null) {
         Object var3 = new Object();
         var2 = this.parallelLockMap.putIfAbsent(var1, var3);
         if (var2 == null) {
            var2 = var3;
         }
      }

      return var2;
   }

   private Class<?> loadClassInternal(String var1) throws ClassNotFoundException {
      if (this.parallelLockMap == null) {
         synchronized(this) {
            return this.loadClass(var1);
         }
      } else {
         return this.loadClass(var1);
      }
   }

   private void checkPackageAccess(Class<?> var1, ProtectionDomain var2) {
      final SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         final int var5;
         if (ReflectUtil.isNonPublicProxyClass(var1)) {
            Class[] var8 = var1.getInterfaces();
            var5 = var8.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Class var7 = var8[var6];
               this.checkPackageAccess(var7, var2);
            }

            return;
         }

         final String var4 = var1.getName();
         var5 = var4.lastIndexOf(46);
         if (var5 != -1) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  var3.checkPackageAccess(var4.substring(0, var5));
                  return null;
               }
            }, new AccessControlContext(new ProtectionDomain[]{var2}));
         }
      }

      this.domains.add(var2);
   }

   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      throw new ClassNotFoundException(var1);
   }

   /** @deprecated */
   @Deprecated
   protected final Class<?> defineClass(byte[] var1, int var2, int var3) throws ClassFormatError {
      return this.defineClass((String)null, var1, var2, var3, (ProtectionDomain)null);
   }

   protected final Class<?> defineClass(String var1, byte[] var2, int var3, int var4) throws ClassFormatError {
      return this.defineClass(var1, var2, var3, var4, (ProtectionDomain)null);
   }

   private ProtectionDomain preDefineClass(String var1, ProtectionDomain var2) {
      if (!this.checkName(var1)) {
         throw new NoClassDefFoundError("IllegalName: " + var1);
      } else if (var1 != null && var1.startsWith("java.")) {
         throw new SecurityException("Prohibited package name: " + var1.substring(0, var1.lastIndexOf(46)));
      } else {
         if (var2 == null) {
            var2 = this.defaultDomain;
         }

         if (var1 != null) {
            this.checkCerts(var1, var2.getCodeSource());
         }

         return var2;
      }
   }

   private String defineClassSourceLocation(ProtectionDomain var1) {
      CodeSource var2 = var1.getCodeSource();
      String var3 = null;
      if (var2 != null && var2.getLocation() != null) {
         var3 = var2.getLocation().toString();
      }

      return var3;
   }

   private void postDefineClass(Class<?> var1, ProtectionDomain var2) {
      if (var2.getCodeSource() != null) {
         Certificate[] var3 = var2.getCodeSource().getCertificates();
         if (var3 != null) {
            this.setSigners(var1, var3);
         }
      }

   }

   protected final Class<?> defineClass(String var1, byte[] var2, int var3, int var4, ProtectionDomain var5) throws ClassFormatError {
      var5 = this.preDefineClass(var1, var5);
      String var6 = this.defineClassSourceLocation(var5);
      Class var7 = this.defineClass1(var1, var2, var3, var4, var5, var6);
      this.postDefineClass(var7, var5);
      return var7;
   }

   protected final Class<?> defineClass(String var1, ByteBuffer var2, ProtectionDomain var3) throws ClassFormatError {
      int var4 = var2.remaining();
      if (!var2.isDirect()) {
         if (var2.hasArray()) {
            return this.defineClass(var1, var2.array(), var2.position() + var2.arrayOffset(), var4, var3);
         } else {
            byte[] var7 = new byte[var4];
            var2.get(var7);
            return this.defineClass(var1, var7, 0, var4, var3);
         }
      } else {
         var3 = this.preDefineClass(var1, var3);
         String var5 = this.defineClassSourceLocation(var3);
         Class var6 = this.defineClass2(var1, var2, var2.position(), var4, var3, var5);
         this.postDefineClass(var6, var3);
         return var6;
      }
   }

   private native Class<?> defineClass0(String var1, byte[] var2, int var3, int var4, ProtectionDomain var5);

   private native Class<?> defineClass1(String var1, byte[] var2, int var3, int var4, ProtectionDomain var5, String var6);

   private native Class<?> defineClass2(String var1, ByteBuffer var2, int var3, int var4, ProtectionDomain var5, String var6);

   private boolean checkName(String var1) {
      if (var1 != null && var1.length() != 0) {
         return var1.indexOf(47) == -1 && (VM.allowArraySyntax() || var1.charAt(0) != '[');
      } else {
         return true;
      }
   }

   private void checkCerts(String var1, CodeSource var2) {
      int var3 = var1.lastIndexOf(46);
      String var4 = var3 == -1 ? "" : var1.substring(0, var3);
      Certificate[] var5 = null;
      if (var2 != null) {
         var5 = var2.getCertificates();
      }

      Certificate[] var6 = null;
      if (this.parallelLockMap == null) {
         synchronized(this) {
            var6 = (Certificate[])this.package2certs.get(var4);
            if (var6 == null) {
               this.package2certs.put(var4, var5 == null ? nocerts : var5);
            }
         }
      } else {
         var6 = (Certificate[])((ConcurrentHashMap)this.package2certs).putIfAbsent(var4, var5 == null ? nocerts : var5);
      }

      if (var6 != null && !this.compareCerts(var6, var5)) {
         throw new SecurityException("class \"" + var1 + "\"'s signer information does not match signer information of other classes in the same package");
      }
   }

   private boolean compareCerts(Certificate[] var1, Certificate[] var2) {
      if (var2 != null && var2.length != 0) {
         if (var2.length != var1.length) {
            return false;
         } else {
            boolean var3;
            int var4;
            int var5;
            for(var4 = 0; var4 < var2.length; ++var4) {
               var3 = false;

               for(var5 = 0; var5 < var1.length; ++var5) {
                  if (var2[var4].equals(var1[var5])) {
                     var3 = true;
                     break;
                  }
               }

               if (!var3) {
                  return false;
               }
            }

            for(var4 = 0; var4 < var1.length; ++var4) {
               var3 = false;

               for(var5 = 0; var5 < var2.length; ++var5) {
                  if (var1[var4].equals(var2[var5])) {
                     var3 = true;
                     break;
                  }
               }

               if (!var3) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var1.length == 0;
      }
   }

   protected final void resolveClass(Class<?> var1) {
      this.resolveClass0(var1);
   }

   private native void resolveClass0(Class<?> var1);

   protected final Class<?> findSystemClass(String var1) throws ClassNotFoundException {
      ClassLoader var2 = getSystemClassLoader();
      if (var2 == null) {
         if (!this.checkName(var1)) {
            throw new ClassNotFoundException(var1);
         } else {
            Class var3 = this.findBootstrapClass(var1);
            if (var3 == null) {
               throw new ClassNotFoundException(var1);
            } else {
               return var3;
            }
         }
      } else {
         return var2.loadClass(var1);
      }
   }

   private Class<?> findBootstrapClassOrNull(String var1) {
      return !this.checkName(var1) ? null : this.findBootstrapClass(var1);
   }

   private native Class<?> findBootstrapClass(String var1);

   protected final Class<?> findLoadedClass(String var1) {
      return !this.checkName(var1) ? null : this.findLoadedClass0(var1);
   }

   private final native Class<?> findLoadedClass0(String var1);

   protected final void setSigners(Class<?> var1, Object[] var2) {
      var1.setSigners(var2);
   }

   public URL getResource(String var1) {
      URL var2;
      if (this.parent != null) {
         var2 = this.parent.getResource(var1);
      } else {
         var2 = getBootstrapResource(var1);
      }

      if (var2 == null) {
         var2 = this.findResource(var1);
      }

      return var2;
   }

   public Enumeration<URL> getResources(String var1) throws IOException {
      Enumeration[] var2 = (Enumeration[])(new Enumeration[2]);
      if (this.parent != null) {
         var2[0] = this.parent.getResources(var1);
      } else {
         var2[0] = getBootstrapResources(var1);
      }

      var2[1] = this.findResources(var1);
      return new CompoundEnumeration(var2);
   }

   protected URL findResource(String var1) {
      return null;
   }

   protected Enumeration<URL> findResources(String var1) throws IOException {
      return Collections.emptyEnumeration();
   }

   @CallerSensitive
   protected static boolean registerAsParallelCapable() {
      Class var0 = Reflection.getCallerClass().asSubclass(ClassLoader.class);
      return ClassLoader.ParallelLoaders.register(var0);
   }

   public static URL getSystemResource(String var0) {
      ClassLoader var1 = getSystemClassLoader();
      return var1 == null ? getBootstrapResource(var0) : var1.getResource(var0);
   }

   public static Enumeration<URL> getSystemResources(String var0) throws IOException {
      ClassLoader var1 = getSystemClassLoader();
      return var1 == null ? getBootstrapResources(var0) : var1.getResources(var0);
   }

   private static URL getBootstrapResource(String var0) {
      URLClassPath var1 = getBootstrapClassPath();
      Resource var2 = var1.getResource(var0);
      return var2 != null ? var2.getURL() : null;
   }

   private static Enumeration<URL> getBootstrapResources(String var0) throws IOException {
      final Enumeration var1 = getBootstrapClassPath().getResources(var0);
      return new Enumeration<URL>() {
         public URL nextElement() {
            return ((Resource)var1.nextElement()).getURL();
         }

         public boolean hasMoreElements() {
            return var1.hasMoreElements();
         }
      };
   }

   static URLClassPath getBootstrapClassPath() {
      return Launcher.getBootstrapClassPath();
   }

   public InputStream getResourceAsStream(String var1) {
      URL var2 = this.getResource(var1);

      try {
         return var2 != null ? var2.openStream() : null;
      } catch (IOException var4) {
         return null;
      }
   }

   public static InputStream getSystemResourceAsStream(String var0) {
      URL var1 = getSystemResource(var0);

      try {
         return var1 != null ? var1.openStream() : null;
      } catch (IOException var3) {
         return null;
      }
   }

   @CallerSensitive
   public final ClassLoader getParent() {
      if (this.parent == null) {
         return null;
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            checkClassLoaderPermission(this.parent, Reflection.getCallerClass());
         }

         return this.parent;
      }
   }

   @CallerSensitive
   public static ClassLoader getSystemClassLoader() {
      initSystemClassLoader();
      if (scl == null) {
         return null;
      } else {
         SecurityManager var0 = System.getSecurityManager();
         if (var0 != null) {
            checkClassLoaderPermission(scl, Reflection.getCallerClass());
         }

         return scl;
      }
   }

   private static synchronized void initSystemClassLoader() {
      if (!sclSet) {
         if (scl != null) {
            throw new IllegalStateException("recursive invocation");
         }

         Launcher var0 = Launcher.getLauncher();
         if (var0 != null) {
            Throwable var1 = null;
            scl = var0.getClassLoader();

            try {
               scl = (ClassLoader)AccessController.doPrivileged((PrivilegedExceptionAction)(new SystemClassLoaderAction(scl)));
            } catch (PrivilegedActionException var3) {
               var1 = var3.getCause();
               if (var1 instanceof InvocationTargetException) {
                  var1 = var1.getCause();
               }
            }

            if (var1 != null) {
               if (var1 instanceof Error) {
                  throw (Error)var1;
               }

               throw new Error(var1);
            }
         }

         sclSet = true;
      }

   }

   boolean isAncestor(ClassLoader var1) {
      ClassLoader var2 = this;

      do {
         var2 = var2.parent;
         if (var1 == var2) {
            return true;
         }
      } while(var2 != null);

      return false;
   }

   private static boolean needsClassLoaderPermissionCheck(ClassLoader var0, ClassLoader var1) {
      if (var0 == var1) {
         return false;
      } else if (var0 == null) {
         return false;
      } else {
         return !var1.isAncestor(var0);
      }
   }

   static ClassLoader getClassLoader(Class<?> var0) {
      return var0 == null ? null : var0.getClassLoader0();
   }

   static void checkClassLoaderPermission(ClassLoader var0, Class<?> var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         ClassLoader var3 = getClassLoader(var1);
         if (needsClassLoaderPermissionCheck(var3, var0)) {
            var2.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
         }
      }

   }

   protected Package definePackage(String var1, String var2, String var3, String var4, String var5, String var6, String var7, URL var8) throws IllegalArgumentException {
      synchronized(this.packages) {
         Package var10 = this.getPackage(var1);
         if (var10 != null) {
            throw new IllegalArgumentException(var1);
         } else {
            var10 = new Package(var1, var2, var3, var4, var5, var6, var7, var8, this);
            this.packages.put(var1, var10);
            return var10;
         }
      }
   }

   protected Package getPackage(String var1) {
      Package var2;
      synchronized(this.packages) {
         var2 = (Package)this.packages.get(var1);
      }

      if (var2 == null) {
         if (this.parent != null) {
            var2 = this.parent.getPackage(var1);
         } else {
            var2 = Package.getSystemPackage(var1);
         }

         if (var2 != null) {
            synchronized(this.packages) {
               Package var4 = (Package)this.packages.get(var1);
               if (var4 == null) {
                  this.packages.put(var1, var2);
               } else {
                  var2 = var4;
               }
            }
         }
      }

      return var2;
   }

   protected Package[] getPackages() {
      HashMap var1;
      synchronized(this.packages) {
         var1 = new HashMap(this.packages);
      }

      Package[] var2;
      if (this.parent != null) {
         var2 = this.parent.getPackages();
      } else {
         var2 = Package.getSystemPackages();
      }

      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            String var4 = var2[var3].getName();
            if (var1.get(var4) == null) {
               var1.put(var4, var2[var3]);
            }
         }
      }

      return (Package[])var1.values().toArray(new Package[var1.size()]);
   }

   protected String findLibrary(String var1) {
      return null;
   }

   private static String[] initializePath(String var0) {
      String var1 = System.getProperty(var0, "");
      String var2 = File.pathSeparator;
      int var3 = var1.length();
      int var4 = var1.indexOf(var2);

      int var6;
      for(var6 = 0; var4 >= 0; var4 = var1.indexOf(var2, var4 + 1)) {
         ++var6;
      }

      String[] var7 = new String[var6 + 1];
      var4 = 0;
      var6 = 0;

      for(int var5 = var1.indexOf(var2); var5 >= 0; var5 = var1.indexOf(var2, var4)) {
         if (var5 - var4 > 0) {
            var7[var6++] = var1.substring(var4, var5);
         } else if (var5 - var4 == 0) {
            var7[var6++] = ".";
         }

         var4 = var5 + 1;
      }

      var7[var6] = var1.substring(var4, var3);
      return var7;
   }

   static void loadLibrary(Class<?> var0, String var1, boolean var2) {
      ClassLoader var3 = var0 == null ? null : var0.getClassLoader();
      if (sys_paths == null) {
         usr_paths = initializePath("java.library.path");
         sys_paths = initializePath("sun.boot.library.path");
      }

      if (var2) {
         if (!loadLibrary0(var0, new File(var1))) {
            throw new UnsatisfiedLinkError("Can't load library: " + var1);
         }
      } else {
         File var5;
         if (var3 != null) {
            String var4 = var3.findLibrary(var1);
            if (var4 != null) {
               var5 = new File(var4);
               if (!var5.isAbsolute()) {
                  throw new UnsatisfiedLinkError("ClassLoader.findLibrary failed to return an absolute path: " + var4);
               }

               if (loadLibrary0(var0, var5)) {
                  return;
               }

               throw new UnsatisfiedLinkError("Can't load " + var4);
            }
         }

         int var6;
         for(var6 = 0; var6 < sys_paths.length; ++var6) {
            var5 = new File(sys_paths[var6], System.mapLibraryName(var1));
            if (loadLibrary0(var0, var5)) {
               return;
            }

            var5 = ClassLoaderHelper.mapAlternativeName(var5);
            if (var5 != null && loadLibrary0(var0, var5)) {
               return;
            }
         }

         if (var3 != null) {
            for(var6 = 0; var6 < usr_paths.length; ++var6) {
               var5 = new File(usr_paths[var6], System.mapLibraryName(var1));
               if (loadLibrary0(var0, var5)) {
                  return;
               }

               var5 = ClassLoaderHelper.mapAlternativeName(var5);
               if (var5 != null && loadLibrary0(var0, var5)) {
                  return;
               }
            }
         }

         throw new UnsatisfiedLinkError("no " + var1 + " in java.library.path");
      }
   }

   private static native String findBuiltinLib(String var0);

   private static boolean loadLibrary0(Class<?> var0, final File var1) {
      String var2 = findBuiltinLib(var1.getName());
      boolean var3 = var2 != null;
      if (!var3) {
         boolean var4 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               return var1.exists() ? Boolean.TRUE : null;
            }
         }) != null;
         if (!var4) {
            return false;
         }

         try {
            var2 = var1.getCanonicalPath();
         } catch (IOException var20) {
            return false;
         }
      }

      ClassLoader var23 = var0 == null ? null : var0.getClassLoader();
      Vector var5 = var23 != null ? var23.nativeLibraries : systemNativeLibraries;
      synchronized(var5) {
         int var7 = var5.size();

         for(int var8 = 0; var8 < var7; ++var8) {
            ClassLoader.NativeLibrary var9 = (ClassLoader.NativeLibrary)var5.elementAt(var8);
            if (var2.equals(var9.name)) {
               return true;
            }
         }

         boolean var10000;
         synchronized(loadedLibraryNames) {
            if (loadedLibraryNames.contains(var2)) {
               throw new UnsatisfiedLinkError("Native Library " + var2 + " already loaded in another classloader");
            }

            int var24 = nativeLibraryContext.size();

            for(int var10 = 0; var10 < var24; ++var10) {
               ClassLoader.NativeLibrary var11 = (ClassLoader.NativeLibrary)nativeLibraryContext.elementAt(var10);
               if (var2.equals(var11.name)) {
                  if (var23 == var11.fromClass.getClassLoader()) {
                     var10000 = true;
                     return var10000;
                  }

                  throw new UnsatisfiedLinkError("Native Library " + var2 + " is being loaded in another classloader");
               }
            }

            ClassLoader.NativeLibrary var25 = new ClassLoader.NativeLibrary(var0, var2, var3);
            nativeLibraryContext.push(var25);

            try {
               var25.load(var2, var3);
            } finally {
               nativeLibraryContext.pop();
            }

            if (!var25.loaded) {
               var10000 = false;
               return var10000;
            }

            loadedLibraryNames.addElement(var2);
            var5.addElement(var25);
            var10000 = true;
         }

         return var10000;
      }
   }

   static long findNative(ClassLoader var0, String var1) {
      Vector var2 = var0 != null ? var0.nativeLibraries : systemNativeLibraries;
      synchronized(var2) {
         int var4 = var2.size();

         for(int var5 = 0; var5 < var4; ++var5) {
            ClassLoader.NativeLibrary var6 = (ClassLoader.NativeLibrary)var2.elementAt(var5);
            long var7 = var6.find(var1);
            if (var7 != 0L) {
               return var7;
            }
         }

         return 0L;
      }
   }

   public void setDefaultAssertionStatus(boolean var1) {
      synchronized(this.assertionLock) {
         if (this.classAssertionStatus == null) {
            this.initializeJavaAssertionMaps();
         }

         this.defaultAssertionStatus = var1;
      }
   }

   public void setPackageAssertionStatus(String var1, boolean var2) {
      synchronized(this.assertionLock) {
         if (this.packageAssertionStatus == null) {
            this.initializeJavaAssertionMaps();
         }

         this.packageAssertionStatus.put(var1, var2);
      }
   }

   public void setClassAssertionStatus(String var1, boolean var2) {
      synchronized(this.assertionLock) {
         if (this.classAssertionStatus == null) {
            this.initializeJavaAssertionMaps();
         }

         this.classAssertionStatus.put(var1, var2);
      }
   }

   public void clearAssertionStatus() {
      synchronized(this.assertionLock) {
         this.classAssertionStatus = new HashMap();
         this.packageAssertionStatus = new HashMap();
         this.defaultAssertionStatus = false;
      }
   }

   boolean desiredAssertionStatus(String var1) {
      synchronized(this.assertionLock) {
         Boolean var3 = (Boolean)this.classAssertionStatus.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            int var4 = var1.lastIndexOf(".");
            if (var4 < 0) {
               var3 = (Boolean)this.packageAssertionStatus.get((Object)null);
               if (var3 != null) {
                  return var3;
               }
            }

            while(var4 > 0) {
               var1 = var1.substring(0, var4);
               var3 = (Boolean)this.packageAssertionStatus.get(var1);
               if (var3 != null) {
                  return var3;
               }

               var4 = var1.lastIndexOf(".", var4 - 1);
            }

            return this.defaultAssertionStatus;
         }
      }
   }

   private void initializeJavaAssertionMaps() {
      this.classAssertionStatus = new HashMap();
      this.packageAssertionStatus = new HashMap();
      AssertionStatusDirectives var1 = retrieveDirectives();

      int var2;
      for(var2 = 0; var2 < var1.classes.length; ++var2) {
         this.classAssertionStatus.put(var1.classes[var2], var1.classEnabled[var2]);
      }

      for(var2 = 0; var2 < var1.packages.length; ++var2) {
         this.packageAssertionStatus.put(var1.packages[var2], var1.packageEnabled[var2]);
      }

      this.defaultAssertionStatus = var1.deflt;
   }

   private static native AssertionStatusDirectives retrieveDirectives();

   static {
      registerNatives();
      nocerts = new Certificate[0];
      loadedLibraryNames = new Vector();
      systemNativeLibraries = new Vector();
      nativeLibraryContext = new Stack();
   }

   static class NativeLibrary {
      long handle;
      private int jniVersion;
      private final Class<?> fromClass;
      String name;
      boolean isBuiltin;
      boolean loaded;

      native void load(String var1, boolean var2);

      native long find(String var1);

      native void unload(String var1, boolean var2);

      public NativeLibrary(Class<?> var1, String var2, boolean var3) {
         this.name = var2;
         this.fromClass = var1;
         this.isBuiltin = var3;
      }

      protected void finalize() {
         synchronized(ClassLoader.loadedLibraryNames) {
            if (this.fromClass.getClassLoader() != null && this.loaded) {
               int var2 = ClassLoader.loadedLibraryNames.size();

               for(int var3 = 0; var3 < var2; ++var3) {
                  if (this.name.equals(ClassLoader.loadedLibraryNames.elementAt(var3))) {
                     ClassLoader.loadedLibraryNames.removeElementAt(var3);
                     break;
                  }
               }

               ClassLoader.nativeLibraryContext.push(this);

               try {
                  this.unload(this.name, this.isBuiltin);
               } finally {
                  ClassLoader.nativeLibraryContext.pop();
               }
            }

         }
      }

      static Class<?> getFromClass() {
         return ((ClassLoader.NativeLibrary)ClassLoader.nativeLibraryContext.peek()).fromClass;
      }
   }

   private static class ParallelLoaders {
      private static final Set<Class<? extends ClassLoader>> loaderTypes = Collections.newSetFromMap(new WeakHashMap());

      static boolean register(Class<? extends ClassLoader> var0) {
         synchronized(loaderTypes) {
            if (loaderTypes.contains(var0.getSuperclass())) {
               loaderTypes.add(var0);
               return true;
            } else {
               return false;
            }
         }
      }

      static boolean isRegistered(Class<? extends ClassLoader> var0) {
         synchronized(loaderTypes) {
            return loaderTypes.contains(var0);
         }
      }

      static {
         synchronized(loaderTypes) {
            loaderTypes.add(ClassLoader.class);
         }
      }
   }
}
