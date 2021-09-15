package com.sun.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Iterator;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;

final class SSLSecurity {
   private SSLSecurity() {
   }

   private static Provider.Service getService(String var0, String var1) {
      ProviderList var2 = Providers.getProviderList();
      Iterator var3 = var2.providers().iterator();

      Provider.Service var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         Provider var4 = (Provider)var3.next();
         var5 = var4.getService(var0, var1);
      } while(var5 == null);

      return var5;
   }

   private static Object[] getImpl1(String var0, String var1, Provider.Service var2) throws NoSuchAlgorithmException {
      Provider var3 = var2.getProvider();
      String var4 = var2.getClassName();

      Class var5;
      try {
         ClassLoader var6 = var3.getClass().getClassLoader();
         if (var6 == null) {
            var5 = Class.forName(var4);
         } else {
            var5 = var6.loadClass(var4);
         }
      } catch (ClassNotFoundException var9) {
         throw new NoSuchAlgorithmException("Class " + var4 + " configured for " + var1 + " not found: " + var9.getMessage());
      } catch (SecurityException var10) {
         throw new NoSuchAlgorithmException("Class " + var4 + " configured for " + var1 + " cannot be accessed: " + var10.getMessage());
      }

      try {
         Object var8 = null;
         Class var12;
         if ((var12 = Class.forName("javax.net.ssl." + var1 + "Spi")) != null && checkSuperclass(var5, var12)) {
            if (var1.equals("SSLContext")) {
               var8 = new SSLContextSpiWrapper(var0, var3);
            } else if (var1.equals("TrustManagerFactory")) {
               var8 = new TrustManagerFactorySpiWrapper(var0, var3);
            } else {
               if (!var1.equals("KeyManagerFactory")) {
                  throw new IllegalStateException("Class " + var5.getName() + " unknown engineType wrapper:" + var1);
               }

               var8 = new KeyManagerFactorySpiWrapper(var0, var3);
            }
         } else {
            Class var13;
            if ((var13 = Class.forName("com.sun.net.ssl." + var1 + "Spi")) != null && checkSuperclass(var5, var13)) {
               var8 = var2.newInstance((Object)null);
            }
         }

         if (var8 != null) {
            return new Object[]{var8, var3};
         } else {
            throw new NoSuchAlgorithmException("Couldn't locate correct object or wrapper: " + var1 + " " + var0);
         }
      } catch (ClassNotFoundException var11) {
         IllegalStateException var7 = new IllegalStateException("Engine Class Not Found for " + var1);
         var7.initCause(var11);
         throw var7;
      }
   }

   static Object[] getImpl(String var0, String var1, String var2) throws NoSuchAlgorithmException, NoSuchProviderException {
      Provider.Service var3;
      if (var2 != null) {
         ProviderList var4 = Providers.getProviderList();
         Provider var5 = var4.getProvider(var2);
         if (var5 == null) {
            throw new NoSuchProviderException("No such provider: " + var2);
         }

         var3 = var5.getService(var1, var0);
      } else {
         var3 = getService(var1, var0);
      }

      if (var3 == null) {
         throw new NoSuchAlgorithmException("Algorithm " + var0 + " not available");
      } else {
         return getImpl1(var0, var1, var3);
      }
   }

   static Object[] getImpl(String var0, String var1, Provider var2) throws NoSuchAlgorithmException {
      Provider.Service var3 = var2.getService(var1, var0);
      if (var3 == null) {
         throw new NoSuchAlgorithmException("No such algorithm: " + var0);
      } else {
         return getImpl1(var0, var1, var3);
      }
   }

   private static boolean checkSuperclass(Class<?> var0, Class<?> var1) {
      if (var0 != null && var1 != null) {
         do {
            if (var0.equals(var1)) {
               return true;
            }

            var0 = var0.getSuperclass();
         } while(var0 != null);

         return false;
      } else {
         return false;
      }
   }

   static Object[] truncateArray(Object[] var0, Object[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = var0[var2];
      }

      return var1;
   }
}
