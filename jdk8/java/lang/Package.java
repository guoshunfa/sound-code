package java.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Package implements AnnotatedElement {
   private static Map<String, Package> pkgs = new HashMap(31);
   private static Map<String, URL> urls = new HashMap(10);
   private static Map<String, Manifest> mans = new HashMap(10);
   private final String pkgName;
   private final String specTitle;
   private final String specVersion;
   private final String specVendor;
   private final String implTitle;
   private final String implVersion;
   private final String implVendor;
   private final URL sealBase;
   private final transient ClassLoader loader;
   private transient Class<?> packageInfo;

   public String getName() {
      return this.pkgName;
   }

   public String getSpecificationTitle() {
      return this.specTitle;
   }

   public String getSpecificationVersion() {
      return this.specVersion;
   }

   public String getSpecificationVendor() {
      return this.specVendor;
   }

   public String getImplementationTitle() {
      return this.implTitle;
   }

   public String getImplementationVersion() {
      return this.implVersion;
   }

   public String getImplementationVendor() {
      return this.implVendor;
   }

   public boolean isSealed() {
      return this.sealBase != null;
   }

   public boolean isSealed(URL var1) {
      return var1.equals(this.sealBase);
   }

   public boolean isCompatibleWith(String var1) throws NumberFormatException {
      if (this.specVersion != null && this.specVersion.length() >= 1) {
         String[] var2 = this.specVersion.split("\\.", -1);
         int[] var3 = new int[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = Integer.parseInt(var2[var4]);
            if (var3[var4] < 0) {
               throw NumberFormatException.forInputString("" + var3[var4]);
            }
         }

         String[] var10 = var1.split("\\.", -1);
         int[] var5 = new int[var10.length];

         int var6;
         for(var6 = 0; var6 < var10.length; ++var6) {
            var5[var6] = Integer.parseInt(var10[var6]);
            if (var5[var6] < 0) {
               throw NumberFormatException.forInputString("" + var5[var6]);
            }
         }

         var6 = Math.max(var5.length, var3.length);

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var7 < var5.length ? var5[var7] : 0;
            int var9 = var7 < var3.length ? var3[var7] : 0;
            if (var9 < var8) {
               return false;
            }

            if (var9 > var8) {
               return true;
            }
         }

         return true;
      } else {
         throw new NumberFormatException("Empty version string");
      }
   }

   @CallerSensitive
   public static Package getPackage(String var0) {
      ClassLoader var1 = ClassLoader.getClassLoader(Reflection.getCallerClass());
      return var1 != null ? var1.getPackage(var0) : getSystemPackage(var0);
   }

   @CallerSensitive
   public static Package[] getPackages() {
      ClassLoader var0 = ClassLoader.getClassLoader(Reflection.getCallerClass());
      return var0 != null ? var0.getPackages() : getSystemPackages();
   }

   static Package getPackage(Class<?> var0) {
      String var1 = var0.getName();
      int var2 = var1.lastIndexOf(46);
      if (var2 != -1) {
         var1 = var1.substring(0, var2);
         ClassLoader var3 = var0.getClassLoader();
         return var3 != null ? var3.getPackage(var1) : getSystemPackage(var1);
      } else {
         return null;
      }
   }

   public int hashCode() {
      return this.pkgName.hashCode();
   }

   public String toString() {
      String var1 = this.specTitle;
      String var2 = this.specVersion;
      if (var1 != null && var1.length() > 0) {
         var1 = ", " + var1;
      } else {
         var1 = "";
      }

      if (var2 != null && var2.length() > 0) {
         var2 = ", version " + var2;
      } else {
         var2 = "";
      }

      return "package " + this.pkgName + var1 + var2;
   }

   private Class<?> getPackageInfo() {
      if (this.packageInfo == null) {
         try {
            this.packageInfo = Class.forName(this.pkgName + ".package-info", false, this.loader);
         } catch (ClassNotFoundException var2) {
            class PackageInfoProxy {
            }

            this.packageInfo = PackageInfoProxy.class;
         }
      }

      return this.packageInfo;
   }

   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      return this.getPackageInfo().getAnnotation(var1);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> var1) {
      return AnnotatedElement.super.isAnnotationPresent(var1);
   }

   public <A extends Annotation> A[] getAnnotationsByType(Class<A> var1) {
      return this.getPackageInfo().getAnnotationsByType(var1);
   }

   public Annotation[] getAnnotations() {
      return this.getPackageInfo().getAnnotations();
   }

   public <A extends Annotation> A getDeclaredAnnotation(Class<A> var1) {
      return this.getPackageInfo().getDeclaredAnnotation(var1);
   }

   public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> var1) {
      return this.getPackageInfo().getDeclaredAnnotationsByType(var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      return this.getPackageInfo().getDeclaredAnnotations();
   }

   Package(String var1, String var2, String var3, String var4, String var5, String var6, String var7, URL var8, ClassLoader var9) {
      this.pkgName = var1;
      this.implTitle = var5;
      this.implVersion = var6;
      this.implVendor = var7;
      this.specTitle = var2;
      this.specVersion = var3;
      this.specVendor = var4;
      this.sealBase = var8;
      this.loader = var9;
   }

   private Package(String var1, Manifest var2, URL var3, ClassLoader var4) {
      String var5 = var1.replace('.', '/').concat("/");
      String var6 = null;
      String var7 = null;
      String var8 = null;
      String var9 = null;
      String var10 = null;
      String var11 = null;
      String var12 = null;
      URL var13 = null;
      Attributes var14 = var2.getAttributes(var5);
      if (var14 != null) {
         var7 = var14.getValue(Attributes.Name.SPECIFICATION_TITLE);
         var8 = var14.getValue(Attributes.Name.SPECIFICATION_VERSION);
         var9 = var14.getValue(Attributes.Name.SPECIFICATION_VENDOR);
         var10 = var14.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
         var11 = var14.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
         var12 = var14.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
         var6 = var14.getValue(Attributes.Name.SEALED);
      }

      var14 = var2.getMainAttributes();
      if (var14 != null) {
         if (var7 == null) {
            var7 = var14.getValue(Attributes.Name.SPECIFICATION_TITLE);
         }

         if (var8 == null) {
            var8 = var14.getValue(Attributes.Name.SPECIFICATION_VERSION);
         }

         if (var9 == null) {
            var9 = var14.getValue(Attributes.Name.SPECIFICATION_VENDOR);
         }

         if (var10 == null) {
            var10 = var14.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
         }

         if (var11 == null) {
            var11 = var14.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
         }

         if (var12 == null) {
            var12 = var14.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
         }

         if (var6 == null) {
            var6 = var14.getValue(Attributes.Name.SEALED);
         }
      }

      if ("true".equalsIgnoreCase(var6)) {
         var13 = var3;
      }

      this.pkgName = var1;
      this.specTitle = var7;
      this.specVersion = var8;
      this.specVendor = var9;
      this.implTitle = var10;
      this.implVersion = var11;
      this.implVendor = var12;
      this.sealBase = var13;
      this.loader = var4;
   }

   static Package getSystemPackage(String var0) {
      synchronized(pkgs) {
         Package var2 = (Package)pkgs.get(var0);
         if (var2 == null) {
            var0 = var0.replace('.', '/').concat("/");
            String var3 = getSystemPackage0(var0);
            if (var3 != null) {
               var2 = defineSystemPackage(var0, var3);
            }
         }

         return var2;
      }
   }

   static Package[] getSystemPackages() {
      String[] var0 = getSystemPackages0();
      synchronized(pkgs) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            defineSystemPackage(var0[var2], getSystemPackage0(var0[var2]));
         }

         return (Package[])pkgs.values().toArray(new Package[pkgs.size()]);
      }
   }

   private static Package defineSystemPackage(final String var0, final String var1) {
      return (Package)AccessController.doPrivileged(new PrivilegedAction<Package>() {
         public Package run() {
            String var1x = var0;
            URL var2 = (URL)Package.urls.get(var1);
            if (var2 == null) {
               File var3 = new File(var1);

               try {
                  var2 = ParseUtil.fileToEncodedURL(var3);
               } catch (MalformedURLException var5) {
               }

               if (var2 != null) {
                  Package.urls.put(var1, var2);
                  if (var3.isFile()) {
                     Package.mans.put(var1, Package.loadManifest(var1));
                  }
               }
            }

            var1x = var1x.substring(0, var1x.length() - 1).replace('/', '.');
            Manifest var4 = (Manifest)Package.mans.get(var1);
            Package var6;
            if (var4 != null) {
               var6 = new Package(var1x, var4, var2, (ClassLoader)null);
            } else {
               var6 = new Package(var1x, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null, (ClassLoader)null);
            }

            Package.pkgs.put(var1x, var6);
            return var6;
         }
      });
   }

   private static Manifest loadManifest(String var0) {
      try {
         FileInputStream var1 = new FileInputStream(var0);
         Throwable var2 = null;

         Object var5;
         try {
            JarInputStream var3 = new JarInputStream(var1, false);
            Throwable var4 = null;

            try {
               var5 = var3.getManifest();
            } catch (Throwable var30) {
               var5 = var30;
               var4 = var30;
               throw var30;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var29) {
                        var4.addSuppressed(var29);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Throwable var32) {
            var2 = var32;
            throw var32;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var28) {
                     var2.addSuppressed(var28);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return (Manifest)var5;
      } catch (IOException var34) {
         return null;
      }
   }

   private static native String getSystemPackage0(String var0);

   private static native String[] getSystemPackages0();

   // $FF: synthetic method
   Package(String var1, Manifest var2, URL var3, ClassLoader var4, Object var5) {
      this(var1, var2, var3, var4);
   }
}
