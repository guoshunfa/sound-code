package sun.applet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.NoSuchElementException;
import sun.awt.AppContext;
import sun.misc.IOUtils;
import sun.net.www.ParseUtil;

public class AppletClassLoader extends URLClassLoader {
   private URL base;
   private CodeSource codesource;
   private AccessControlContext acc;
   private boolean exceptionStatus = false;
   private final Object threadGroupSynchronizer = new Object();
   private final Object grabReleaseSynchronizer = new Object();
   private boolean codebaseLookup = true;
   private volatile boolean allowRecursiveDirectoryRead = true;
   private Object syncResourceAsStream = new Object();
   private Object syncResourceAsStreamFromJar = new Object();
   private boolean resourceAsStreamInCall = false;
   private boolean resourceAsStreamFromJarInCall = false;
   private AppletThreadGroup threadGroup;
   private AppContext appContext;
   int usageCount = 0;
   private HashMap jdk11AppletInfo = new HashMap();
   private HashMap jdk12AppletInfo = new HashMap();
   private static AppletMessageHandler mh = new AppletMessageHandler("appletclassloader");

   protected AppletClassLoader(URL var1) {
      super(new URL[0]);
      this.base = var1;
      this.codesource = new CodeSource(var1, (Certificate[])null);
      this.acc = AccessController.getContext();
   }

   public void disableRecursiveDirectoryRead() {
      this.allowRecursiveDirectoryRead = false;
   }

   void setCodebaseLookup(boolean var1) {
      this.codebaseLookup = var1;
   }

   URL getBaseURL() {
      return this.base;
   }

   public URL[] getURLs() {
      URL[] var1 = super.getURLs();
      URL[] var2 = new URL[var1.length + 1];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      var2[var2.length - 1] = this.base;
      return var2;
   }

   protected void addJar(String var1) throws IOException {
      URL var2;
      try {
         var2 = new URL(this.base, var1);
      } catch (MalformedURLException var4) {
         throw new IllegalArgumentException("name");
      }

      this.addURL(var2);
   }

   public synchronized Class loadClass(String var1, boolean var2) throws ClassNotFoundException {
      int var3 = var1.lastIndexOf(46);
      if (var3 != -1) {
         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            var4.checkPackageAccess(var1.substring(0, var3));
         }
      }

      try {
         return super.loadClass(var1, var2);
      } catch (ClassNotFoundException var5) {
         throw var5;
      } catch (RuntimeException var6) {
         throw var6;
      } catch (Error var7) {
         throw var7;
      }
   }

   protected Class findClass(String var1) throws ClassNotFoundException {
      int var2 = var1.indexOf(";");
      String var3 = "";
      if (var2 != -1) {
         var3 = var1.substring(var2, var1.length());
         var1 = var1.substring(0, var2);
      }

      try {
         return super.findClass(var1);
      } catch (ClassNotFoundException var8) {
         if (!this.codebaseLookup) {
            throw new ClassNotFoundException(var1);
         } else {
            String var4 = ParseUtil.encodePath(var1.replace('.', '/'), false);
            final String var5 = var4 + ".class" + var3;

            try {
               byte[] var6 = (byte[])((byte[])AccessController.doPrivileged(new PrivilegedExceptionAction() {
                  public Object run() throws IOException {
                     try {
                        URL var1 = new URL(AppletClassLoader.this.base, var5);
                        return AppletClassLoader.this.base.getProtocol().equals(var1.getProtocol()) && AppletClassLoader.this.base.getHost().equals(var1.getHost()) && AppletClassLoader.this.base.getPort() == var1.getPort() ? AppletClassLoader.getBytes(var1) : null;
                     } catch (Exception var2) {
                        return null;
                     }
                  }
               }, this.acc));
               if (var6 != null) {
                  return this.defineClass(var1, var6, 0, var6.length, this.codesource);
               } else {
                  throw new ClassNotFoundException(var1);
               }
            } catch (PrivilegedActionException var7) {
               throw new ClassNotFoundException(var1, var7.getException());
            }
         }
      }
   }

   protected PermissionCollection getPermissions(CodeSource var1) {
      PermissionCollection var2 = super.getPermissions(var1);
      URL var3 = var1.getLocation();
      String var4 = null;

      Permission var5;
      try {
         var5 = var3.openConnection().getPermission();
      } catch (IOException var12) {
         var5 = null;
      }

      if (var5 instanceof FilePermission) {
         var4 = var5.getName();
      } else if (var5 == null && var3.getProtocol().equals("file")) {
         var4 = var3.getFile().replace('/', File.separatorChar);
         var4 = ParseUtil.decode(var4);
      }

      if (var4 != null) {
         if (!var4.endsWith(File.separator)) {
            int var7 = var4.lastIndexOf(File.separatorChar);
            if (var7 != -1) {
               var4 = var4.substring(0, var7 + 1) + "-";
               var2.add(new FilePermission(var4, "read"));
            }
         }

         File var13 = new File(var4);
         boolean var8 = var13.isDirectory();
         if (this.allowRecursiveDirectoryRead && (var8 || var4.toLowerCase().endsWith(".jar") || var4.toLowerCase().endsWith(".zip"))) {
            Permission var9;
            try {
               var9 = this.base.openConnection().getPermission();
            } catch (IOException var11) {
               var9 = null;
            }

            String var10;
            if (var9 instanceof FilePermission) {
               var10 = var9.getName();
               if (var10.endsWith(File.separator)) {
                  var10 = var10 + "-";
               }

               var2.add(new FilePermission(var10, "read"));
            } else if (var9 == null && this.base.getProtocol().equals("file")) {
               var10 = this.base.getFile().replace('/', File.separatorChar);
               var10 = ParseUtil.decode(var10);
               if (var10.endsWith(File.separator)) {
                  var10 = var10 + "-";
               }

               var2.add(new FilePermission(var10, "read"));
            }
         }
      }

      return var2;
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

   public InputStream getResourceAsStream(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name");
      } else {
         try {
            InputStream var2 = null;
            synchronized(this.syncResourceAsStream) {
               this.resourceAsStreamInCall = true;
               var2 = super.getResourceAsStream(var1);
               this.resourceAsStreamInCall = false;
            }

            if (this.codebaseLookup && var2 == null) {
               URL var3 = new URL(this.base, ParseUtil.encodePath(var1, false));
               var2 = var3.openStream();
            }

            return var2;
         } catch (Exception var6) {
            return null;
         }
      }
   }

   public InputStream getResourceAsStreamFromJar(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name");
      } else {
         try {
            InputStream var2 = null;
            synchronized(this.syncResourceAsStreamFromJar) {
               this.resourceAsStreamFromJarInCall = true;
               var2 = super.getResourceAsStream(var1);
               this.resourceAsStreamFromJarInCall = false;
            }

            return var2;
         } catch (Exception var6) {
            return null;
         }
      }
   }

   public URL findResource(String var1) {
      URL var2 = super.findResource(var1);
      if (var1.startsWith("META-INF/")) {
         return var2;
      } else if (!this.codebaseLookup) {
         return var2;
      } else {
         if (var2 == null) {
            boolean var3 = false;
            synchronized(this.syncResourceAsStreamFromJar) {
               var3 = this.resourceAsStreamFromJarInCall;
            }

            if (var3) {
               return null;
            }

            boolean var4 = false;
            synchronized(this.syncResourceAsStream) {
               var4 = this.resourceAsStreamInCall;
            }

            if (!var4) {
               try {
                  var2 = new URL(this.base, ParseUtil.encodePath(var1, false));
                  if (!this.resourceExists(var2)) {
                     var2 = null;
                  }
               } catch (Exception var7) {
                  var2 = null;
               }
            }
         }

         return var2;
      }
   }

   private boolean resourceExists(URL var1) {
      boolean var2 = true;

      try {
         URLConnection var3 = var1.openConnection();
         if (var3 instanceof HttpURLConnection) {
            HttpURLConnection var4 = (HttpURLConnection)var3;
            var4.setRequestMethod("HEAD");
            int var5 = var4.getResponseCode();
            if (var5 == 200) {
               return true;
            }

            if (var5 >= 400) {
               return false;
            }
         } else {
            InputStream var7 = var3.getInputStream();
            var7.close();
         }
      } catch (Exception var6) {
         var2 = false;
      }

      return var2;
   }

   public Enumeration findResources(String var1) throws IOException {
      final Enumeration var2 = super.findResources(var1);
      if (var1.startsWith("META-INF/")) {
         return var2;
      } else if (!this.codebaseLookup) {
         return var2;
      } else {
         final URL var3 = new URL(this.base, ParseUtil.encodePath(var1, false));
         if (!this.resourceExists(var3)) {
            var3 = null;
         }

         return new Enumeration() {
            private boolean done;

            public Object nextElement() {
               if (!this.done) {
                  if (var2.hasMoreElements()) {
                     return var2.nextElement();
                  }

                  this.done = true;
                  if (var3 != null) {
                     return var3;
                  }
               }

               throw new NoSuchElementException();
            }

            public boolean hasMoreElements() {
               return !this.done && (var2.hasMoreElements() || var3 != null);
            }
         };
      }
   }

   Class loadCode(String var1) throws ClassNotFoundException {
      var1 = var1.replace('/', '.');
      var1 = var1.replace(File.separatorChar, '.');
      String var2 = null;
      int var3 = var1.indexOf(";");
      if (var3 != -1) {
         var2 = var1.substring(var3, var1.length());
         var1 = var1.substring(0, var3);
      }

      String var4 = var1;
      if (var1.endsWith(".class") || var1.endsWith(".java")) {
         var1 = var1.substring(0, var1.lastIndexOf(46));
      }

      try {
         if (var2 != null) {
            var1 = var1 + var2;
         }

         return this.loadClass(var1);
      } catch (ClassNotFoundException var6) {
         if (var2 != null) {
            var4 = var1 + var2;
         }

         return this.loadClass(var4);
      }
   }

   public ThreadGroup getThreadGroup() {
      synchronized(this.threadGroupSynchronizer) {
         if (this.threadGroup == null || this.threadGroup.isDestroyed()) {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  AppletClassLoader.this.threadGroup = new AppletThreadGroup(AppletClassLoader.this.base + "-threadGroup");
                  AppContextCreator var1 = new AppContextCreator(AppletClassLoader.this.threadGroup);
                  var1.setContextClassLoader(AppletClassLoader.this);
                  var1.start();

                  try {
                     synchronized(var1.syncObject) {
                        while(!var1.created) {
                           var1.syncObject.wait();
                        }
                     }
                  } catch (InterruptedException var5) {
                  }

                  AppletClassLoader.this.appContext = var1.appContext;
                  return null;
               }
            });
         }

         return this.threadGroup;
      }
   }

   public AppContext getAppContext() {
      return this.appContext;
   }

   public void grab() {
      synchronized(this.grabReleaseSynchronizer) {
         ++this.usageCount;
      }

      this.getThreadGroup();
   }

   protected void setExceptionStatus() {
      this.exceptionStatus = true;
   }

   public boolean getExceptionStatus() {
      return this.exceptionStatus;
   }

   protected void release() {
      AppContext var1 = null;
      synchronized(this.grabReleaseSynchronizer) {
         if (this.usageCount > 1) {
            --this.usageCount;
         } else {
            synchronized(this.threadGroupSynchronizer) {
               var1 = this.resetAppContext();
            }
         }
      }

      if (var1 != null) {
         try {
            var1.dispose();
         } catch (IllegalThreadStateException var6) {
         }
      }

   }

   protected AppContext resetAppContext() {
      AppContext var1 = null;
      synchronized(this.threadGroupSynchronizer) {
         var1 = this.appContext;
         this.usageCount = 0;
         this.appContext = null;
         this.threadGroup = null;
         return var1;
      }
   }

   void setJDK11Target(Class var1, boolean var2) {
      this.jdk11AppletInfo.put(var1.toString(), var2);
   }

   void setJDK12Target(Class var1, boolean var2) {
      this.jdk12AppletInfo.put(var1.toString(), var2);
   }

   Boolean isJDK11Target(Class var1) {
      return (Boolean)this.jdk11AppletInfo.get(var1.toString());
   }

   Boolean isJDK12Target(Class var1) {
      return (Boolean)this.jdk12AppletInfo.get(var1.toString());
   }

   private static void printError(String var0, Throwable var1) {
      String var2 = null;
      if (var1 == null) {
         var2 = mh.getMessage("filenotfound", (Object)var0);
      } else if (var1 instanceof IOException) {
         var2 = mh.getMessage("fileioexception", (Object)var0);
      } else if (var1 instanceof ClassFormatError) {
         var2 = mh.getMessage("fileformat", (Object)var0);
      } else if (var1 instanceof ThreadDeath) {
         var2 = mh.getMessage("filedeath", (Object)var0);
      } else if (var1 instanceof Error) {
         var2 = mh.getMessage("fileerror", var1.toString(), var0);
      }

      if (var2 != null) {
         System.err.println(var2);
      }

   }
}
