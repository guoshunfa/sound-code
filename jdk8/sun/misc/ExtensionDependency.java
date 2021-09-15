package sun.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

public class ExtensionDependency {
   private static Vector<ExtensionInstallationProvider> providers;
   static final boolean DEBUG = false;

   public static synchronized void addExtensionInstallationProvider(ExtensionInstallationProvider var0) {
      if (providers == null) {
         providers = new Vector();
      }

      providers.add(var0);
   }

   public static synchronized void removeExtensionInstallationProvider(ExtensionInstallationProvider var0) {
      providers.remove(var0);
   }

   public static boolean checkExtensionsDependencies(JarFile var0) {
      if (providers == null) {
         return true;
      } else {
         try {
            ExtensionDependency var1 = new ExtensionDependency();
            return var1.checkExtensions(var0);
         } catch (ExtensionInstallationException var2) {
            debug(var2.getMessage());
            return false;
         }
      }
   }

   protected boolean checkExtensions(JarFile var1) throws ExtensionInstallationException {
      Manifest var2;
      try {
         var2 = var1.getManifest();
      } catch (IOException var9) {
         return false;
      }

      if (var2 == null) {
         return true;
      } else {
         boolean var3 = true;
         Attributes var4 = var2.getMainAttributes();
         if (var4 != null) {
            String var5 = var4.getValue(Attributes.Name.EXTENSION_LIST);
            if (var5 != null) {
               StringTokenizer var6 = new StringTokenizer(var5);

               while(var6.hasMoreTokens()) {
                  String var7 = var6.nextToken();
                  debug("The file " + var1.getName() + " appears to depend on " + var7);
                  String var8 = var7 + "-" + Attributes.Name.EXTENSION_NAME.toString();
                  if (var4.getValue(var8) == null) {
                     debug("The jar file " + var1.getName() + " appers to depend on " + var7 + " but does not define the " + var8 + " attribute in its manifest ");
                  } else if (!this.checkExtension(var7, var4)) {
                     debug("Failed installing " + var7);
                     var3 = false;
                  }
               }
            } else {
               debug("No dependencies for " + var1.getName());
            }
         }

         return var3;
      }
   }

   protected synchronized boolean checkExtension(String var1, Attributes var2) throws ExtensionInstallationException {
      debug("Checking extension " + var1);
      if (this.checkExtensionAgainstInstalled(var1, var2)) {
         return true;
      } else {
         debug("Extension not currently installed ");
         ExtensionInfo var3 = new ExtensionInfo(var1, var2);
         return this.installExtension(var3, (ExtensionInfo)null);
      }
   }

   boolean checkExtensionAgainstInstalled(String var1, Attributes var2) throws ExtensionInstallationException {
      File var3 = this.checkExtensionExists(var1);
      if (var3 != null) {
         try {
            if (this.checkExtensionAgainst(var1, var2, var3)) {
               return true;
            }
         } catch (FileNotFoundException var7) {
            this.debugException(var7);
         } catch (IOException var8) {
            this.debugException(var8);
         }

         return false;
      } else {
         File[] var4;
         try {
            var4 = this.getInstalledExtensions();
         } catch (IOException var11) {
            this.debugException(var11);
            return false;
         }

         for(int var5 = 0; var5 < var4.length; ++var5) {
            try {
               if (this.checkExtensionAgainst(var1, var2, var4[var5])) {
                  return true;
               }
            } catch (FileNotFoundException var9) {
               this.debugException(var9);
            } catch (IOException var10) {
               this.debugException(var10);
            }
         }

         return false;
      }
   }

   protected boolean checkExtensionAgainst(String var1, Attributes var2, final File var3) throws IOException, FileNotFoundException, ExtensionInstallationException {
      debug("Checking extension " + var1 + " against " + var3.getName());

      Manifest var4;
      try {
         var4 = (Manifest)AccessController.doPrivileged(new PrivilegedExceptionAction<Manifest>() {
            public Manifest run() throws IOException, FileNotFoundException {
               if (!var3.exists()) {
                  throw new FileNotFoundException(var3.getName());
               } else {
                  JarFile var1 = new JarFile(var3);
                  return var1.getManifest();
               }
            }
         });
      } catch (PrivilegedActionException var9) {
         if (var9.getException() instanceof FileNotFoundException) {
            throw (FileNotFoundException)var9.getException();
         }

         throw (IOException)var9.getException();
      }

      ExtensionInfo var5 = new ExtensionInfo(var1, var2);
      debug("Requested Extension : " + var5);
      boolean var6 = true;
      ExtensionInfo var7 = null;
      if (var4 != null) {
         Attributes var8 = var4.getMainAttributes();
         if (var8 != null) {
            var7 = new ExtensionInfo((String)null, var8);
            debug("Extension Installed " + var7);
            int var10 = var7.isCompatibleWith(var5);
            switch(var10) {
            case 0:
               debug("Extensions are compatible");
               return true;
            case 4:
               debug("Extensions are incompatible");
               return false;
            default:
               debug("Extensions require an upgrade or vendor switch");
               return this.installExtension(var5, var7);
            }
         }
      }

      return false;
   }

   protected boolean installExtension(ExtensionInfo var1, ExtensionInfo var2) throws ExtensionInstallationException {
      Vector var3;
      synchronized(providers) {
         Vector var5 = (Vector)providers.clone();
         var3 = var5;
      }

      Enumeration var4 = var3.elements();

      ExtensionInstallationProvider var8;
      do {
         if (!var4.hasMoreElements()) {
            debug(var1.name + " installation failed");
            return false;
         }

         var8 = (ExtensionInstallationProvider)var4.nextElement();
      } while(var8 == null || !var8.installExtension(var1, var2));

      debug(var1.name + " installation successful");
      Launcher.ExtClassLoader var6 = (Launcher.ExtClassLoader)Launcher.getLauncher().getClassLoader().getParent();
      this.addNewExtensionsToClassLoader(var6);
      return true;
   }

   private File checkExtensionExists(final String var1) {
      final String[] var3 = new String[]{".jar", ".zip"};
      return (File)AccessController.doPrivileged(new PrivilegedAction<File>() {
         public File run() {
            try {
               File[] var2 = ExtensionDependency.getExtDirs();

               for(int var3x = 0; var3x < var2.length; ++var3x) {
                  for(int var4 = 0; var4 < var3.length; ++var4) {
                     File var1x;
                     if (var1.toLowerCase().endsWith(var3[var4])) {
                        var1x = new File(var2[var3x], var1);
                     } else {
                        var1x = new File(var2[var3x], var1 + var3[var4]);
                     }

                     ExtensionDependency.debug("checkExtensionExists:fileName " + var1x.getName());
                     if (var1x.exists()) {
                        return var1x;
                     }
                  }
               }

               return null;
            } catch (Exception var5) {
               ExtensionDependency.this.debugException(var5);
               return null;
            }
         }
      });
   }

   private static File[] getExtDirs() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.ext.dirs")));
      File[] var1;
      if (var0 != null) {
         StringTokenizer var2 = new StringTokenizer(var0, File.pathSeparator);
         int var3 = var2.countTokens();
         debug("getExtDirs count " + var3);
         var1 = new File[var3];

         for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4] = new File(var2.nextToken());
            debug("getExtDirs dirs[" + var4 + "] " + var1[var4]);
         }
      } else {
         var1 = new File[0];
         debug("getExtDirs dirs " + var1);
      }

      debug("getExtDirs dirs.length " + var1.length);
      return var1;
   }

   private static File[] getExtFiles(File[] var0) throws IOException {
      Vector var1 = new Vector();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         String[] var3 = var0[var2].list(new JarFilter());
         if (var3 != null) {
            debug("getExtFiles files.length " + var3.length);

            for(int var4 = 0; var4 < var3.length; ++var4) {
               File var5 = new File(var0[var2], var3[var4]);
               var1.add(var5);
               debug("getExtFiles f[" + var4 + "] " + var5);
            }
         }
      }

      File[] var6 = new File[var1.size()];
      var1.copyInto(var6);
      debug("getExtFiles ua.length " + var6.length);
      return var6;
   }

   private File[] getInstalledExtensions() throws IOException {
      return (File[])AccessController.doPrivileged(new PrivilegedAction<File[]>() {
         public File[] run() {
            try {
               return ExtensionDependency.getExtFiles(ExtensionDependency.getExtDirs());
            } catch (IOException var2) {
               ExtensionDependency.debug("Cannot get list of installed extensions");
               ExtensionDependency.this.debugException(var2);
               return new File[0];
            }
         }
      });
   }

   private Boolean addNewExtensionsToClassLoader(Launcher.ExtClassLoader var1) {
      try {
         File[] var2 = this.getInstalledExtensions();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            final File var4 = var2[var3];
            URL var5 = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>() {
               public URL run() {
                  try {
                     return ParseUtil.fileToEncodedURL(var4);
                  } catch (MalformedURLException var2) {
                     ExtensionDependency.this.debugException(var2);
                     return null;
                  }
               }
            });
            if (var5 != null) {
               URL[] var6 = var1.getURLs();
               boolean var7 = false;

               for(int var8 = 0; var8 < var6.length; ++var8) {
                  debug("URL[" + var8 + "] is " + var6[var8] + " looking for " + var5);
                  if (var6[var8].toString().compareToIgnoreCase(var5.toString()) == 0) {
                     var7 = true;
                     debug("Found !");
                  }
               }

               if (!var7) {
                  debug("Not Found ! adding to the classloader " + var5);
                  var1.addExtURL(var5);
               }
            }
         }
      } catch (MalformedURLException var9) {
         var9.printStackTrace();
      } catch (IOException var10) {
         var10.printStackTrace();
      }

      return Boolean.TRUE;
   }

   private static void debug(String var0) {
   }

   private void debugException(Throwable var1) {
   }
}
