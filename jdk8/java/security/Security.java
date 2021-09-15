package java.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.security.jca.GetInstance;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

public final class Security {
   private static final Debug sdebug = Debug.getInstance("properties");
   private static Properties props;
   private static final Map<String, Class<?>> spiMap;

   private static void initialize() {
      props = new Properties();
      boolean var0 = false;
      boolean var1 = false;
      File var2 = securityPropFile("java.security");
      if (var2.exists()) {
         BufferedInputStream var3 = null;

         try {
            FileInputStream var4 = new FileInputStream(var2);
            var3 = new BufferedInputStream(var4);
            props.load((InputStream)var3);
            var0 = true;
            if (sdebug != null) {
               sdebug.println("reading security properties file: " + var2);
            }
         } catch (IOException var31) {
            if (sdebug != null) {
               sdebug.println("unable to load security properties from " + var2);
               var31.printStackTrace();
            }
         } finally {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (IOException var27) {
                  if (sdebug != null) {
                     sdebug.println("unable to close input stream");
                  }
               }
            }

         }
      }

      if ("true".equalsIgnoreCase(props.getProperty("security.overridePropertiesFile"))) {
         String var33 = System.getProperty("java.security.properties");
         if (var33 != null && var33.startsWith("=")) {
            var1 = true;
            var33 = var33.substring(1);
         }

         if (var1) {
            props = new Properties();
            if (sdebug != null) {
               sdebug.println("overriding other security properties files!");
            }
         }

         if (var33 != null) {
            BufferedInputStream var34 = null;

            try {
               var33 = PropertyExpander.expand(var33);
               var2 = new File(var33);
               URL var5;
               if (var2.exists()) {
                  var5 = new URL("file:" + var2.getCanonicalPath());
               } else {
                  var5 = new URL(var33);
               }

               var34 = new BufferedInputStream(var5.openStream());
               props.load((InputStream)var34);
               var0 = true;
               if (sdebug != null) {
                  sdebug.println("reading security properties file: " + var5);
                  if (var1) {
                     sdebug.println("overriding other security properties files!");
                  }
               }
            } catch (Exception var29) {
               if (sdebug != null) {
                  sdebug.println("unable to load security properties from " + var33);
                  var29.printStackTrace();
               }
            } finally {
               if (var34 != null) {
                  try {
                     var34.close();
                  } catch (IOException var28) {
                     if (sdebug != null) {
                        sdebug.println("unable to close input stream");
                     }
                  }
               }

            }
         }
      }

      if (!var0) {
         initializeStatic();
         if (sdebug != null) {
            sdebug.println("unable to load security properties -- using defaults");
         }
      }

   }

   private static void initializeStatic() {
      props.put("security.provider.1", "sun.security.provider.Sun");
      props.put("security.provider.2", "sun.security.rsa.SunRsaSign");
      props.put("security.provider.3", "com.sun.net.ssl.internal.ssl.Provider");
      props.put("security.provider.4", "com.sun.crypto.provider.SunJCE");
      props.put("security.provider.5", "sun.security.jgss.SunProvider");
      props.put("security.provider.6", "com.sun.security.sasl.Provider");
   }

   private Security() {
   }

   private static File securityPropFile(String var0) {
      String var1 = File.separator;
      return new File(System.getProperty("java.home") + var1 + "lib" + var1 + "security" + var1 + var0);
   }

   private static Security.ProviderProperty getProviderProperty(String var0) {
      Object var1 = null;
      List var2 = Providers.getProviderList().providers();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         String var4 = null;
         Provider var5 = (Provider)var2.get(var3);
         String var6 = var5.getProperty(var0);
         if (var6 == null) {
            Enumeration var7 = var5.keys();

            while(var7.hasMoreElements() && var6 == null) {
               var4 = (String)var7.nextElement();
               if (var0.equalsIgnoreCase(var4)) {
                  var6 = var5.getProperty(var4);
                  break;
               }
            }
         }

         if (var6 != null) {
            Security.ProviderProperty var8 = new Security.ProviderProperty();
            var8.className = var6;
            var8.provider = var5;
            return var8;
         }
      }

      return (Security.ProviderProperty)var1;
   }

   private static String getProviderProperty(String var0, Provider var1) {
      String var2 = var1.getProperty(var0);
      if (var2 == null) {
         Enumeration var3 = var1.keys();

         while(var3.hasMoreElements() && var2 == null) {
            String var4 = (String)var3.nextElement();
            if (var0.equalsIgnoreCase(var4)) {
               var2 = var1.getProperty(var4);
               break;
            }
         }
      }

      return var2;
   }

   /** @deprecated */
   @Deprecated
   public static String getAlgorithmProperty(String var0, String var1) {
      Security.ProviderProperty var2 = getProviderProperty("Alg." + var1 + "." + var0);
      return var2 != null ? var2.className : null;
   }

   public static synchronized int insertProviderAt(Provider var0, int var1) {
      String var2 = var0.getName();
      checkInsertProvider(var2);
      ProviderList var3 = Providers.getFullProviderList();
      ProviderList var4 = ProviderList.insertAt(var3, var0, var1 - 1);
      if (var3 == var4) {
         return -1;
      } else {
         Providers.setProviderList(var4);
         return var4.getIndex(var2) + 1;
      }
   }

   public static int addProvider(Provider var0) {
      return insertProviderAt(var0, 0);
   }

   public static synchronized void removeProvider(String var0) {
      check("removeProvider." + var0);
      ProviderList var1 = Providers.getFullProviderList();
      ProviderList var2 = ProviderList.remove(var1, var0);
      Providers.setProviderList(var2);
   }

   public static Provider[] getProviders() {
      return Providers.getFullProviderList().toArray();
   }

   public static Provider getProvider(String var0) {
      return Providers.getProviderList().getProvider(var0);
   }

   public static Provider[] getProviders(String var0) {
      String var1 = null;
      String var2 = null;
      int var3 = var0.indexOf(58);
      if (var3 == -1) {
         var1 = var0;
         var2 = "";
      } else {
         var1 = var0.substring(0, var3);
         var2 = var0.substring(var3 + 1);
      }

      Hashtable var4 = new Hashtable(1);
      var4.put(var1, var2);
      return getProviders((Map)var4);
   }

   public static Provider[] getProviders(Map<String, String> var0) {
      Provider[] var1 = getProviders();
      Set var2 = var0.keySet();
      LinkedHashSet var3 = new LinkedHashSet(5);
      if (var2 != null && var1 != null) {
         boolean var4 = true;
         Iterator var5 = var2.iterator();

         label50:
         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            String var7 = (String)var0.get(var6);
            LinkedHashSet var8 = getAllQualifyingCandidates(var6, var7, var1);
            if (var4) {
               var3 = var8;
               var4 = false;
            }

            if (var8 != null && !var8.isEmpty()) {
               Iterator var9 = var3.iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     continue label50;
                  }

                  Provider var10 = (Provider)var9.next();
                  if (!var8.contains(var10)) {
                     var9.remove();
                  }
               }
            }

            var3 = null;
            break;
         }

         if (var3 != null && !var3.isEmpty()) {
            Object[] var11 = var3.toArray();
            Provider[] var12 = new Provider[var11.length];

            for(int var13 = 0; var13 < var12.length; ++var13) {
               var12[var13] = (Provider)var11[var13];
            }

            return var12;
         } else {
            return null;
         }
      } else {
         return var1;
      }
   }

   private static Class<?> getSpiClass(String var0) {
      Class var1 = (Class)spiMap.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         try {
            var1 = Class.forName("java.security." + var0 + "Spi");
            spiMap.put(var0, var1);
            return var1;
         } catch (ClassNotFoundException var3) {
            throw new AssertionError("Spi class not found", var3);
         }
      }
   }

   static Object[] getImpl(String var0, String var1, String var2) throws NoSuchAlgorithmException, NoSuchProviderException {
      return var2 == null ? GetInstance.getInstance(var1, getSpiClass(var1), var0).toArray() : GetInstance.getInstance(var1, getSpiClass(var1), var0, var2).toArray();
   }

   static Object[] getImpl(String var0, String var1, String var2, Object var3) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
      return var2 == null ? GetInstance.getInstance(var1, getSpiClass(var1), var0, var3).toArray() : GetInstance.getInstance(var1, getSpiClass(var1), var0, var3, var2).toArray();
   }

   static Object[] getImpl(String var0, String var1, Provider var2) throws NoSuchAlgorithmException {
      return GetInstance.getInstance(var1, getSpiClass(var1), var0, var2).toArray();
   }

   static Object[] getImpl(String var0, String var1, Provider var2, Object var3) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      return GetInstance.getInstance(var1, getSpiClass(var1), var0, var3, var2).toArray();
   }

   public static String getProperty(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new SecurityPermission("getProperty." + var0));
      }

      String var2 = props.getProperty(var0);
      if (var2 != null) {
         var2 = var2.trim();
      }

      return var2;
   }

   public static void setProperty(String var0, String var1) {
      check("setProperty." + var0);
      props.put(var0, var1);
      invalidateSMCache(var0);
   }

   private static void invalidateSMCache(String var0) {
      final boolean var1 = var0.equals("package.access");
      boolean var2 = var0.equals("package.definition");
      if (var1 || var2) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  Class var1x = Class.forName("java.lang.SecurityManager", false, (ClassLoader)null);
                  Field var2 = null;
                  boolean var3 = false;
                  if (var1) {
                     var2 = var1x.getDeclaredField("packageAccessValid");
                     var3 = var2.isAccessible();
                     var2.setAccessible(true);
                  } else {
                     var2 = var1x.getDeclaredField("packageDefinitionValid");
                     var3 = var2.isAccessible();
                     var2.setAccessible(true);
                  }

                  var2.setBoolean(var2, false);
                  var2.setAccessible(var3);
               } catch (Exception var4) {
               }

               return null;
            }
         });
      }

   }

   private static void check(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSecurityAccess(var0);
      }

   }

   private static void checkInsertProvider(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            var1.checkSecurityAccess("insertProvider");
         } catch (SecurityException var5) {
            try {
               var1.checkSecurityAccess("insertProvider." + var0);
            } catch (SecurityException var4) {
               var5.addSuppressed(var4);
               throw var5;
            }
         }
      }

   }

   private static LinkedHashSet<Provider> getAllQualifyingCandidates(String var0, String var1, Provider[] var2) {
      String[] var3 = getFilterComponents(var0, var1);
      String var4 = var3[0];
      String var5 = var3[1];
      String var6 = var3[2];
      return getProvidersNotUsingCache(var4, var5, var6, var1, var2);
   }

   private static LinkedHashSet<Provider> getProvidersNotUsingCache(String var0, String var1, String var2, String var3, Provider[] var4) {
      LinkedHashSet var5 = new LinkedHashSet(5);

      for(int var6 = 0; var6 < var4.length; ++var6) {
         if (isCriterionSatisfied(var4[var6], var0, var1, var2, var3)) {
            var5.add(var4[var6]);
         }
      }

      return var5;
   }

   private static boolean isCriterionSatisfied(Provider var0, String var1, String var2, String var3, String var4) {
      String var5 = var1 + '.' + var2;
      if (var3 != null) {
         var5 = var5 + ' ' + var3;
      }

      String var6 = getProviderProperty(var5, var0);
      if (var6 == null) {
         String var7 = getProviderProperty("Alg.Alias." + var1 + "." + var2, var0);
         if (var7 != null) {
            var5 = var1 + "." + var7;
            if (var3 != null) {
               var5 = var5 + ' ' + var3;
            }

            var6 = getProviderProperty(var5, var0);
         }

         if (var6 == null) {
            return false;
         }
      }

      if (var3 == null) {
         return true;
      } else {
         return isStandardAttr(var3) ? isConstraintSatisfied(var3, var4, var6) : var4.equalsIgnoreCase(var6);
      }
   }

   private static boolean isStandardAttr(String var0) {
      if (var0.equalsIgnoreCase("KeySize")) {
         return true;
      } else {
         return var0.equalsIgnoreCase("ImplementedIn");
      }
   }

   private static boolean isConstraintSatisfied(String var0, String var1, String var2) {
      if (var0.equalsIgnoreCase("KeySize")) {
         int var3 = Integer.parseInt(var1);
         int var4 = Integer.parseInt(var2);
         return var3 <= var4;
      } else {
         return var0.equalsIgnoreCase("ImplementedIn") ? var1.equalsIgnoreCase(var2) : false;
      }
   }

   static String[] getFilterComponents(String var0, String var1) {
      int var2 = var0.indexOf(46);
      if (var2 < 0) {
         throw new InvalidParameterException("Invalid filter");
      } else {
         String var3 = var0.substring(0, var2);
         String var4 = null;
         String var5 = null;
         if (var1.length() == 0) {
            var4 = var0.substring(var2 + 1).trim();
            if (var4.length() == 0) {
               throw new InvalidParameterException("Invalid filter");
            }
         } else {
            int var6 = var0.indexOf(32);
            if (var6 == -1) {
               throw new InvalidParameterException("Invalid filter");
            }

            var5 = var0.substring(var6 + 1).trim();
            if (var5.length() == 0) {
               throw new InvalidParameterException("Invalid filter");
            }

            if (var6 < var2 || var2 == var6 - 1) {
               throw new InvalidParameterException("Invalid filter");
            }

            var4 = var0.substring(var2 + 1, var6);
         }

         String[] var7 = new String[]{var3, var4, var5};
         return var7;
      }
   }

   public static Set<String> getAlgorithms(String var0) {
      if (var0 != null && var0.length() != 0 && !var0.endsWith(".")) {
         HashSet var1 = new HashSet();
         Provider[] var2 = getProviders();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Enumeration var4 = var2[var3].keys();

            while(var4.hasMoreElements()) {
               String var5 = ((String)var4.nextElement()).toUpperCase(Locale.ENGLISH);
               if (var5.startsWith(var0.toUpperCase(Locale.ENGLISH)) && var5.indexOf(" ") < 0) {
                  var1.add(var5.substring(var0.length() + 1));
               }
            }
         }

         return Collections.unmodifiableSet(var1);
      } else {
         return Collections.emptySet();
      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Security.initialize();
            return null;
         }
      });
      spiMap = new ConcurrentHashMap();
   }

   private static class ProviderProperty {
      String className;
      Provider provider;

      private ProviderProperty() {
      }

      // $FF: synthetic method
      ProviderProperty(Object var1) {
         this();
      }
   }
}
