package sun.security.jca;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

final class ProviderConfig {
   private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
   private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
   private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
   private static final int MAX_LOAD_TRIES = 30;
   private static final Class[] CL_STRING = new Class[]{String.class};
   private final String className;
   private final String argument;
   private int tries;
   private volatile Provider provider;
   private boolean isLoading;

   ProviderConfig(String var1, String var2) {
      if (var1.equals("sun.security.pkcs11.SunPKCS11") && var2.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg")) {
         this.checkSunPKCS11Solaris();
      }

      this.className = var1;
      this.argument = expand(var2);
   }

   ProviderConfig(String var1) {
      this(var1, "");
   }

   ProviderConfig(Provider var1) {
      this.className = var1.getClass().getName();
      this.argument = "";
      this.provider = var1;
   }

   private void checkSunPKCS11Solaris() {
      Boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            File var1 = new File("/usr/lib/libpkcs11.so");
            if (!var1.exists()) {
               return Boolean.FALSE;
            } else {
               return "false".equalsIgnoreCase(System.getProperty("sun.security.pkcs11.enable-solaris")) ? Boolean.FALSE : Boolean.TRUE;
            }
         }
      });
      if (var1 == Boolean.FALSE) {
         this.tries = 30;
      }

   }

   private boolean hasArgument() {
      return this.argument.length() != 0;
   }

   private boolean shouldLoad() {
      return this.tries < 30;
   }

   private void disableLoad() {
      this.tries = 30;
   }

   boolean isLoaded() {
      return this.provider != null;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ProviderConfig)) {
         return false;
      } else {
         ProviderConfig var2 = (ProviderConfig)var1;
         return this.className.equals(var2.className) && this.argument.equals(var2.argument);
      }
   }

   public int hashCode() {
      return this.className.hashCode() + this.argument.hashCode();
   }

   public String toString() {
      return this.hasArgument() ? this.className + "('" + this.argument + "')" : this.className;
   }

   synchronized Provider getProvider() {
      Provider var1 = this.provider;
      if (var1 != null) {
         return var1;
      } else if (!this.shouldLoad()) {
         return null;
      } else if (this.isLoading) {
         if (debug != null) {
            debug.println("Recursion loading provider: " + this);
            (new Exception("Call trace")).printStackTrace();
         }

         return null;
      } else {
         try {
            this.isLoading = true;
            ++this.tries;
            var1 = this.doLoadProvider();
         } finally {
            this.isLoading = false;
         }

         this.provider = var1;
         return var1;
      }
   }

   private Provider doLoadProvider() {
      return (Provider)AccessController.doPrivileged(new PrivilegedAction<Provider>() {
         public Provider run() {
            if (ProviderConfig.debug != null) {
               ProviderConfig.debug.println("Loading provider: " + ProviderConfig.this);
            }

            try {
               ClassLoader var1 = ClassLoader.getSystemClassLoader();
               Class var7;
               if (var1 != null) {
                  var7 = var1.loadClass(ProviderConfig.this.className);
               } else {
                  var7 = Class.forName(ProviderConfig.this.className);
               }

               Object var3;
               if (!ProviderConfig.this.hasArgument()) {
                  var3 = var7.newInstance();
               } else {
                  Constructor var4 = var7.getConstructor(ProviderConfig.CL_STRING);
                  var3 = var4.newInstance(ProviderConfig.this.argument);
               }

               if (var3 instanceof Provider) {
                  if (ProviderConfig.debug != null) {
                     ProviderConfig.debug.println("Loaded provider " + var3);
                  }

                  return (Provider)var3;
               } else {
                  if (ProviderConfig.debug != null) {
                     ProviderConfig.debug.println(ProviderConfig.this.className + " is not a provider");
                  }

                  ProviderConfig.this.disableLoad();
                  return null;
               }
            } catch (Exception var5) {
               Object var2;
               if (var5 instanceof InvocationTargetException) {
                  var2 = ((InvocationTargetException)var5).getCause();
               } else {
                  var2 = var5;
               }

               if (ProviderConfig.debug != null) {
                  ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
                  ((Throwable)var2).printStackTrace();
               }

               if (var2 instanceof ProviderException) {
                  throw (ProviderException)var2;
               } else {
                  if (var2 instanceof UnsupportedOperationException) {
                     ProviderConfig.this.disableLoad();
                  }

                  return null;
               }
            } catch (ExceptionInInitializerError var6) {
               if (ProviderConfig.debug != null) {
                  ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
                  var6.printStackTrace();
               }

               ProviderConfig.this.disableLoad();
               return null;
            }
         }
      });
   }

   private static String expand(final String var0) {
      return !var0.contains("${") ? var0 : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            try {
               return PropertyExpander.expand(var0);
            } catch (GeneralSecurityException var2) {
               throw new ProviderException(var2);
            }
         }
      });
   }
}
