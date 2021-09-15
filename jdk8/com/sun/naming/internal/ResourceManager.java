package com.sun.naming.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public final class ResourceManager {
   private static final String PROVIDER_RESOURCE_FILE_NAME = "jndiprovider.properties";
   private static final String APP_RESOURCE_FILE_NAME = "jndi.properties";
   private static final String JRELIB_PROPERTY_FILE_NAME = "jndi.properties";
   private static final String DISABLE_APP_RESOURCE_FILES = "com.sun.naming.disable.app.resource.files";
   private static final String[] listProperties = new String[]{"java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.factory.control"};
   private static final VersionHelper helper = VersionHelper.getVersionHelper();
   private static final WeakHashMap<Object, Hashtable<? super String, Object>> propertiesCache = new WeakHashMap(11);
   private static final WeakHashMap<ClassLoader, Map<String, List<NamedWeakReference<Object>>>> factoryCache = new WeakHashMap(11);
   private static final WeakHashMap<ClassLoader, Map<String, WeakReference<Object>>> urlFactoryCache = new WeakHashMap(11);
   private static final WeakReference<Object> NO_FACTORY = new WeakReference((Object)null);

   private ResourceManager() {
   }

   public static Hashtable<?, ?> getInitialEnvironment(Hashtable<?, ?> var0) throws NamingException {
      String[] var1 = VersionHelper.PROPS;
      if (var0 == null) {
         var0 = new Hashtable(11);
      }

      Object var2 = var0.get("java.naming.applet");
      String[] var3 = helper.getJndiProperties();

      for(int var4 = 0; var4 < var1.length; ++var4) {
         Object var5 = var0.get(var1[var4]);
         if (var5 == null) {
            if (var2 != null) {
               var5 = ResourceManager.AppletParameter.get(var2, var1[var4]);
            }

            if (var5 == null) {
               var5 = var3 != null ? var3[var4] : helper.getJndiProperty(var4);
            }

            if (var5 != null) {
               var0.put(var1[var4], var5);
            }
         }
      }

      String var6 = (String)var0.get("com.sun.naming.disable.app.resource.files");
      if (var6 != null && var6.equalsIgnoreCase("true")) {
         return var0;
      } else {
         mergeTables(var0, getApplicationResources());
         return var0;
      }
   }

   public static String getProperty(String var0, Hashtable<?, ?> var1, Context var2, boolean var3) throws NamingException {
      String var4 = var1 != null ? (String)var1.get(var0) : null;
      if (var2 == null || var4 != null && !var3) {
         return var4;
      } else {
         String var5 = (String)getProviderResource(var2).get(var0);
         if (var4 == null) {
            return var5;
         } else {
            return var5 != null && var3 ? var4 + ":" + var5 : var4;
         }
      }
   }

   public static FactoryEnumeration getFactories(String var0, Hashtable<?, ?> var1, Context var2) throws NamingException {
      String var3 = getProperty(var0, var1, var2, true);
      if (var3 == null) {
         return null;
      } else {
         ClassLoader var4 = helper.getContextClassLoader();
         Object var5 = null;
         synchronized(factoryCache) {
            var5 = (Map)factoryCache.get(var4);
            if (var5 == null) {
               var5 = new HashMap(11);
               factoryCache.put(var4, var5);
            }
         }

         synchronized(var5) {
            List var7 = (List)((Map)var5).get(var3);
            if (var7 != null) {
               return var7.size() == 0 ? null : new FactoryEnumeration(var7, var4);
            } else {
               StringTokenizer var8 = new StringTokenizer(var3, ":");
               ArrayList var15 = new ArrayList(5);

               while(var8.hasMoreTokens()) {
                  try {
                     String var9 = var8.nextToken();
                     Class var10 = helper.loadClass(var9, var4);
                     var15.add(new NamedWeakReference(var10, var9));
                  } catch (Exception var12) {
                  }
               }

               ((Map)var5).put(var3, var15);
               return new FactoryEnumeration(var15, var4);
            }
         }
      }
   }

   public static Object getFactory(String var0, Hashtable<?, ?> var1, Context var2, String var3, String var4) throws NamingException {
      String var5 = getProperty(var0, var1, var2, true);
      if (var5 != null) {
         var5 = var5 + ":" + var4;
      } else {
         var5 = var4;
      }

      ClassLoader var6 = helper.getContextClassLoader();
      String var7 = var3 + " " + var5;
      Object var8 = null;
      synchronized(urlFactoryCache) {
         var8 = (Map)urlFactoryCache.get(var6);
         if (var8 == null) {
            var8 = new HashMap(11);
            urlFactoryCache.put(var6, var8);
         }
      }

      synchronized(var8) {
         Object var10 = null;
         WeakReference var11 = (WeakReference)((Map)var8).get(var7);
         if (var11 == NO_FACTORY) {
            return null;
         } else {
            if (var11 != null) {
               var10 = var11.get();
               if (var10 != null) {
                  return var10;
               }
            }

            StringTokenizer var12 = new StringTokenizer(var5, ":");

            while(var10 == null && var12.hasMoreTokens()) {
               String var13 = var12.nextToken() + var3;

               NamingException var15;
               try {
                  var10 = helper.loadClass(var13, var6).newInstance();
               } catch (InstantiationException var17) {
                  var15 = new NamingException("Cannot instantiate " + var13);
                  var15.setRootCause(var17);
                  throw var15;
               } catch (IllegalAccessException var18) {
                  var15 = new NamingException("Cannot access " + var13);
                  var15.setRootCause(var18);
                  throw var15;
               } catch (Exception var19) {
               }
            }

            ((Map)var8).put(var7, var10 != null ? new WeakReference(var10) : NO_FACTORY);
            return var10;
         }
      }
   }

   private static Hashtable<? super String, Object> getProviderResource(Object var0) throws NamingException {
      if (var0 == null) {
         return new Hashtable(1);
      } else {
         synchronized(propertiesCache) {
            Class var2 = var0.getClass();
            Hashtable var3 = (Hashtable)propertiesCache.get(var2);
            if (var3 != null) {
               return var3;
            } else {
               Properties var10 = new Properties();
               InputStream var4 = helper.getResourceAsStream(var2, "jndiprovider.properties");
               if (var4 != null) {
                  try {
                     ((Properties)var10).load(var4);
                  } catch (IOException var8) {
                     ConfigurationException var6 = new ConfigurationException("Error reading provider resource file for " + var2);
                     var6.setRootCause(var8);
                     throw var6;
                  }
               }

               propertiesCache.put(var2, var10);
               return var10;
            }
         }
      }
   }

   private static Hashtable<? super String, Object> getApplicationResources() throws NamingException {
      ClassLoader var0 = helper.getContextClassLoader();
      synchronized(propertiesCache) {
         Object var2 = (Hashtable)propertiesCache.get(var0);
         if (var2 != null) {
            return (Hashtable)var2;
         } else {
            try {
               NamingEnumeration var3 = helper.getResources(var0, "jndi.properties");

               try {
                  while(var3.hasMore()) {
                     Properties var30 = new Properties();
                     InputStream var5 = (InputStream)var3.next();

                     try {
                        var30.load(var5);
                     } finally {
                        var5.close();
                     }

                     if (var2 == null) {
                        var2 = var30;
                     } else {
                        mergeTables((Hashtable)var2, var30);
                     }
                  }
               } finally {
                  while(var3.hasMore()) {
                     ((InputStream)var3.next()).close();
                  }

               }

               InputStream var31 = helper.getJavaHomeLibStream("jndi.properties");
               if (var31 != null) {
                  try {
                     Properties var32 = new Properties();
                     var32.load(var31);
                     if (var2 == null) {
                        var2 = var32;
                     } else {
                        mergeTables((Hashtable)var2, var32);
                     }
                  } finally {
                     var31.close();
                  }
               }
            } catch (IOException var28) {
               ConfigurationException var4 = new ConfigurationException("Error reading application resource file");
               var4.setRootCause(var28);
               throw var4;
            }

            if (var2 == null) {
               var2 = new Hashtable(11);
            }

            propertiesCache.put(var0, var2);
            return (Hashtable)var2;
         }
      }
   }

   private static void mergeTables(Hashtable<? super String, Object> var0, Hashtable<? super String, Object> var1) {
      Iterator var2 = var1.keySet().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         String var4 = (String)var3;
         Object var5 = var0.get(var4);
         if (var5 == null) {
            var0.put(var4, var1.get(var4));
         } else if (isListProperty(var4)) {
            String var6 = (String)var1.get(var4);
            var0.put(var4, (String)var5 + ":" + var6);
         }
      }

   }

   private static boolean isListProperty(String var0) {
      var0 = var0.intern();

      for(int var1 = 0; var1 < listProperties.length; ++var1) {
         if (var0 == listProperties[var1]) {
            return true;
         }
      }

      return false;
   }

   private static class AppletParameter {
      private static final Class<?> clazz = getClass("java.applet.Applet");
      private static final Method getMethod;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, (ClassLoader)null);
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         if (var0 != null) {
            try {
               return var0.getMethod(var1, var2);
            } catch (NoSuchMethodException var4) {
               throw new AssertionError(var4);
            }
         } else {
            return null;
         }
      }

      static Object get(Object var0, String var1) {
         if (clazz != null && clazz.isInstance(var0)) {
            try {
               return getMethod.invoke(var0, var1);
            } catch (IllegalAccessException | InvocationTargetException var3) {
               throw new AssertionError(var3);
            }
         } else {
            throw new ClassCastException(var0.getClass().getName());
         }
      }

      static {
         getMethod = getMethod(clazz, "getParameter", String.class);
      }
   }
}
