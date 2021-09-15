package java.net;

import java.io.Closeable;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.JavaNetAccess;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;
import sun.net.www.protocol.file.FileURLConnection;

public class URLClassLoader extends SecureClassLoader implements Closeable {
   private final URLClassPath ucp;
   private final AccessControlContext acc;
   private WeakHashMap<Closeable, Void> closeables = new WeakHashMap();

   public URLClassLoader(URL[] var1, ClassLoader var2) {
      super(var2);
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkCreateClassLoader();
      }

      this.acc = AccessController.getContext();
      this.ucp = new URLClassPath(var1, this.acc);
   }

   URLClassLoader(URL[] var1, ClassLoader var2, AccessControlContext var3) {
      super(var2);
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var4.checkCreateClassLoader();
      }

      this.acc = var3;
      this.ucp = new URLClassPath(var1, var3);
   }

   public URLClassLoader(URL[] var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkCreateClassLoader();
      }

      this.acc = AccessController.getContext();
      this.ucp = new URLClassPath(var1, this.acc);
   }

   URLClassLoader(URL[] var1, AccessControlContext var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkCreateClassLoader();
      }

      this.acc = var2;
      this.ucp = new URLClassPath(var1, var2);
   }

   public URLClassLoader(URL[] var1, ClassLoader var2, URLStreamHandlerFactory var3) {
      super(var2);
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var4.checkCreateClassLoader();
      }

      this.acc = AccessController.getContext();
      this.ucp = new URLClassPath(var1, var3, this.acc);
   }

   public InputStream getResourceAsStream(String var1) {
      URL var2 = this.getResource(var1);

      try {
         if (var2 == null) {
            return null;
         } else {
            URLConnection var3 = var2.openConnection();
            InputStream var4 = var3.getInputStream();
            if (var3 instanceof JarURLConnection) {
               JarURLConnection var5 = (JarURLConnection)var3;
               JarFile var6 = var5.getJarFile();
               synchronized(this.closeables) {
                  if (!this.closeables.containsKey(var6)) {
                     this.closeables.put(var6, (Object)null);
                  }
               }
            } else if (var3 instanceof FileURLConnection) {
               synchronized(this.closeables) {
                  this.closeables.put(var4, (Object)null);
               }
            }

            return var4;
         }
      } catch (IOException var12) {
         return null;
      }
   }

   public void close() throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("closeClassLoader"));
      }

      List var2 = this.ucp.closeLoaders();
      synchronized(this.closeables) {
         Set var4 = this.closeables.keySet();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Closeable var6 = (Closeable)var5.next();

            try {
               var6.close();
            } catch (IOException var9) {
               var2.add(var9);
            }
         }

         this.closeables.clear();
      }

      if (!var2.isEmpty()) {
         IOException var3 = (IOException)var2.remove(0);
         Iterator var11 = var2.iterator();

         while(var11.hasNext()) {
            IOException var12 = (IOException)var11.next();
            var3.addSuppressed(var12);
         }

         throw var3;
      }
   }

   protected void addURL(URL var1) {
      this.ucp.addURL(var1);
   }

   public URL[] getURLs() {
      return this.ucp.getURLs();
   }

   protected Class<?> findClass(final String var1) throws ClassNotFoundException {
      Class var2;
      try {
         var2 = (Class)AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
            public Class<?> run() throws ClassNotFoundException {
               String var1x = var1.replace('.', '/').concat(".class");
               Resource var2 = URLClassLoader.this.ucp.getResource(var1x, false);
               if (var2 != null) {
                  try {
                     return URLClassLoader.this.defineClass(var1, var2);
                  } catch (IOException var4) {
                     throw new ClassNotFoundException(var1, var4);
                  }
               } else {
                  return null;
               }
            }
         }, this.acc);
      } catch (PrivilegedActionException var4) {
         throw (ClassNotFoundException)var4.getException();
      }

      if (var2 == null) {
         throw new ClassNotFoundException(var1);
      } else {
         return var2;
      }
   }

   private Package getAndVerifyPackage(String var1, Manifest var2, URL var3) {
      Package var4 = this.getPackage(var1);
      if (var4 != null) {
         if (var4.isSealed()) {
            if (!var4.isSealed(var3)) {
               throw new SecurityException("sealing violation: package " + var1 + " is sealed");
            }
         } else if (var2 != null && this.isSealed(var1, var2)) {
            throw new SecurityException("sealing violation: can't seal package " + var1 + ": already loaded");
         }
      }

      return var4;
   }

   private void definePackageInternal(String var1, Manifest var2, URL var3) {
      if (this.getAndVerifyPackage(var1, var2, var3) == null) {
         try {
            if (var2 != null) {
               this.definePackage(var1, var2, var3);
            } else {
               this.definePackage(var1, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
            }
         } catch (IllegalArgumentException var5) {
            if (this.getAndVerifyPackage(var1, var2, var3) == null) {
               throw new AssertionError("Cannot find package " + var1);
            }
         }
      }

   }

   private Class<?> defineClass(String var1, Resource var2) throws IOException {
      long var3 = System.nanoTime();
      int var5 = var1.lastIndexOf(46);
      URL var6 = var2.getCodeSourceURL();
      if (var5 != -1) {
         String var7 = var1.substring(0, var5);
         Manifest var8 = var2.getManifest();
         this.definePackageInternal(var7, var8, var6);
      }

      ByteBuffer var11 = var2.getByteBuffer();
      if (var11 != null) {
         CodeSigner[] var13 = var2.getCodeSigners();
         CodeSource var14 = new CodeSource(var6, var13);
         PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(var3);
         return this.defineClass(var1, var11, var14);
      } else {
         byte[] var12 = var2.getBytes();
         CodeSigner[] var9 = var2.getCodeSigners();
         CodeSource var10 = new CodeSource(var6, var9);
         PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(var3);
         return this.defineClass(var1, var12, 0, var12.length, var10);
      }
   }

   protected Package definePackage(String var1, Manifest var2, URL var3) throws IllegalArgumentException {
      String var4 = var1.replace('.', '/').concat("/");
      String var5 = null;
      String var6 = null;
      String var7 = null;
      String var8 = null;
      String var9 = null;
      String var10 = null;
      String var11 = null;
      URL var12 = null;
      Attributes var13 = var2.getAttributes(var4);
      if (var13 != null) {
         var5 = var13.getValue(Attributes.Name.SPECIFICATION_TITLE);
         var6 = var13.getValue(Attributes.Name.SPECIFICATION_VERSION);
         var7 = var13.getValue(Attributes.Name.SPECIFICATION_VENDOR);
         var8 = var13.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
         var9 = var13.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
         var10 = var13.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
         var11 = var13.getValue(Attributes.Name.SEALED);
      }

      var13 = var2.getMainAttributes();
      if (var13 != null) {
         if (var5 == null) {
            var5 = var13.getValue(Attributes.Name.SPECIFICATION_TITLE);
         }

         if (var6 == null) {
            var6 = var13.getValue(Attributes.Name.SPECIFICATION_VERSION);
         }

         if (var7 == null) {
            var7 = var13.getValue(Attributes.Name.SPECIFICATION_VENDOR);
         }

         if (var8 == null) {
            var8 = var13.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
         }

         if (var9 == null) {
            var9 = var13.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
         }

         if (var10 == null) {
            var10 = var13.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
         }

         if (var11 == null) {
            var11 = var13.getValue(Attributes.Name.SEALED);
         }
      }

      if ("true".equalsIgnoreCase(var11)) {
         var12 = var3;
      }

      return this.definePackage(var1, var5, var6, var7, var8, var9, var10, var12);
   }

   private boolean isSealed(String var1, Manifest var2) {
      String var3 = var1.replace('.', '/').concat("/");
      Attributes var4 = var2.getAttributes(var3);
      String var5 = null;
      if (var4 != null) {
         var5 = var4.getValue(Attributes.Name.SEALED);
      }

      if (var5 == null && (var4 = var2.getMainAttributes()) != null) {
         var5 = var4.getValue(Attributes.Name.SEALED);
      }

      return "true".equalsIgnoreCase(var5);
   }

   public URL findResource(final String var1) {
      URL var2 = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>() {
         public URL run() {
            return URLClassLoader.this.ucp.findResource(var1, true);
         }
      }, this.acc);
      return var2 != null ? this.ucp.checkURL(var2) : null;
   }

   public Enumeration<URL> findResources(String var1) throws IOException {
      final Enumeration var2 = this.ucp.findResources(var1, true);
      return new Enumeration<URL>() {
         private URL url = null;

         private boolean next() {
            if (this.url != null) {
               return true;
            } else {
               do {
                  URL var1 = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>() {
                     public URL run() {
                        return !var2.hasMoreElements() ? null : (URL)var2.nextElement();
                     }
                  }, URLClassLoader.this.acc);
                  if (var1 == null) {
                     break;
                  }

                  this.url = URLClassLoader.this.ucp.checkURL(var1);
               } while(this.url == null);

               return this.url != null;
            }
         }

         public URL nextElement() {
            if (!this.next()) {
               throw new NoSuchElementException();
            } else {
               URL var1 = this.url;
               this.url = null;
               return var1;
            }
         }

         public boolean hasMoreElements() {
            return this.next();
         }
      };
   }

   protected PermissionCollection getPermissions(CodeSource var1) {
      PermissionCollection var2 = super.getPermissions(var1);
      URL var3 = var1.getLocation();

      final Object var4;
      URLConnection var5;
      try {
         var5 = var3.openConnection();
         var4 = var5.getPermission();
      } catch (IOException var8) {
         var4 = null;
         var5 = null;
      }

      String var6;
      if (var4 instanceof FilePermission) {
         var6 = ((Permission)var4).getName();
         if (var6.endsWith(File.separator)) {
            var6 = var6 + "-";
            var4 = new FilePermission(var6, "read");
         }
      } else if (var4 == null && var3.getProtocol().equals("file")) {
         var6 = var3.getFile().replace('/', File.separatorChar);
         var6 = ParseUtil.decode(var6);
         if (var6.endsWith(File.separator)) {
            var6 = var6 + "-";
         }

         var4 = new FilePermission(var6, "read");
      } else {
         URL var9 = var3;
         if (var5 instanceof JarURLConnection) {
            var9 = ((JarURLConnection)var5).getJarFileURL();
         }

         String var7 = var9.getHost();
         if (var7 != null && var7.length() > 0) {
            var4 = new SocketPermission(var7, "connect,accept");
         }
      }

      if (var4 != null) {
         final SecurityManager var10 = System.getSecurityManager();
         if (var10 != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() throws SecurityException {
                  var10.checkPermission((Permission)var4);
                  return null;
               }
            }, this.acc);
         }

         var2.add((Permission)var4);
      }

      return var2;
   }

   public static URLClassLoader newInstance(final URL[] var0, final ClassLoader var1) {
      final AccessControlContext var2 = AccessController.getContext();
      URLClassLoader var3 = (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
         public URLClassLoader run() {
            return new FactoryURLClassLoader(var0, var1, var2);
         }
      });
      return var3;
   }

   public static URLClassLoader newInstance(final URL[] var0) {
      final AccessControlContext var1 = AccessController.getContext();
      URLClassLoader var2 = (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
         public URLClassLoader run() {
            return new FactoryURLClassLoader(var0, var1);
         }
      });
      return var2;
   }

   static {
      SharedSecrets.setJavaNetAccess(new JavaNetAccess() {
         public URLClassPath getURLClassPath(URLClassLoader var1) {
            return var1.ucp;
         }

         public String getOriginalHostName(InetAddress var1) {
            return var1.holder.getOriginalHostName();
         }
      });
      ClassLoader.registerAsParallelCapable();
   }
}
