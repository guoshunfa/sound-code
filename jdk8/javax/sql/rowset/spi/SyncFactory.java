package javax.sql.rowset.spi;

import com.sun.rowset.providers.RIOptimisticProvider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.sql.SQLPermission;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import sun.reflect.misc.ReflectUtil;

public class SyncFactory {
   public static final String ROWSET_SYNC_PROVIDER = "rowset.provider.classname";
   public static final String ROWSET_SYNC_VENDOR = "rowset.provider.vendor";
   public static final String ROWSET_SYNC_PROVIDER_VERSION = "rowset.provider.version";
   private static String ROWSET_PROPERTIES = "rowset.properties";
   private static final SQLPermission SET_SYNCFACTORY_PERMISSION = new SQLPermission("setSyncFactory");
   private static Context ic;
   private static volatile Logger rsLogger;
   private static Hashtable<String, SyncProvider> implementations;
   private static String colon = ":";
   private static String strFileSep = "/";
   private static boolean debug = false;
   private static int providerImplIndex = 0;
   private static boolean lazyJNDICtxRefresh = false;

   private SyncFactory() {
   }

   public static synchronized void registerProvider(String var0) throws SyncFactoryException {
      ProviderImpl var1 = new ProviderImpl();
      var1.setClassname(var0);
      initMapIfNecessary();
      implementations.put(var0, var1);
   }

   public static SyncFactory getSyncFactory() {
      return SyncFactory.SyncFactoryHolder.factory;
   }

   public static synchronized void unregisterProvider(String var0) throws SyncFactoryException {
      initMapIfNecessary();
      if (implementations.containsKey(var0)) {
         implementations.remove(var0);
      }

   }

   private static synchronized void initMapIfNecessary() throws SyncFactoryException {
      Properties var0 = new Properties();
      if (implementations == null) {
         implementations = new Hashtable();

         String var1;
         try {
            try {
               var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<String>() {
                  public String run() {
                     return System.getProperty("rowset.properties");
                  }
               }), (AccessControlContext)null, new PropertyPermission("rowset.properties", "read"));
            } catch (Exception var19) {
               System.out.println("errorget rowset.properties: " + var19);
               var1 = null;
            }

            if (var1 != null) {
               ROWSET_PROPERTIES = var1;
               FileInputStream var2 = new FileInputStream(ROWSET_PROPERTIES);
               Throwable var3 = null;

               try {
                  var0.load((InputStream)var2);
               } catch (Throwable var18) {
                  var3 = var18;
                  throw var18;
               } finally {
                  if (var2 != null) {
                     if (var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var16) {
                           var3.addSuppressed(var16);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }

               parseProperties(var0);
            }

            ROWSET_PROPERTIES = "javax" + strFileSep + "sql" + strFileSep + "rowset" + strFileSep + "rowset.properties";
            ClassLoader var24 = Thread.currentThread().getContextClassLoader();

            try {
               AccessController.doPrivileged(() -> {
                  InputStream var2 = var24 == null ? ClassLoader.getSystemResourceAsStream(ROWSET_PROPERTIES) : var24.getResourceAsStream(ROWSET_PROPERTIES);
                  Throwable var3 = null;

                  try {
                     if (var2 == null) {
                        throw new SyncFactoryException("Resource " + ROWSET_PROPERTIES + " not found");
                     }

                     var0.load(var2);
                  } catch (Throwable var12) {
                     var3 = var12;
                     throw var12;
                  } finally {
                     if (var2 != null) {
                        if (var3 != null) {
                           try {
                              var2.close();
                           } catch (Throwable var11) {
                              var3.addSuppressed(var11);
                           }
                        } else {
                           var2.close();
                        }
                     }

                  }

                  return null;
               });
            } catch (PrivilegedActionException var20) {
               Exception var4 = var20.getException();
               if (var4 instanceof SyncFactoryException) {
                  throw (SyncFactoryException)var4;
               }

               SyncFactoryException var5 = new SyncFactoryException();
               var5.initCause(var20.getException());
               throw var5;
            }

            parseProperties(var0);
         } catch (FileNotFoundException var22) {
            throw new SyncFactoryException("Cannot locate properties file: " + var22);
         } catch (IOException var23) {
            throw new SyncFactoryException("IOException: " + var23);
         }

         var0.clear();

         try {
            var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<String>() {
               public String run() {
                  return System.getProperty("rowset.provider.classname");
               }
            }), (AccessControlContext)null, new PropertyPermission("rowset.provider.classname", "read"));
         } catch (Exception var17) {
            var1 = null;
         }

         if (var1 != null) {
            int var25 = 0;
            if (var1.indexOf(colon) > 0) {
               for(StringTokenizer var26 = new StringTokenizer(var1, colon); var26.hasMoreElements(); ++var25) {
                  var0.put("rowset.provider.classname." + var25, var26.nextToken());
               }
            } else {
               var0.put("rowset.provider.classname", var1);
            }

            parseProperties(var0);
         }
      }

   }

   private static void parseProperties(Properties var0) {
      ProviderImpl var1 = null;
      String var2 = null;
      String[] var3 = null;
      Enumeration var4 = var0.propertyNames();

      while(var4.hasMoreElements()) {
         String var5 = (String)var4.nextElement();
         int var6 = var5.length();
         if (var5.startsWith("rowset.provider.classname")) {
            var1 = new ProviderImpl();
            var1.setIndex(providerImplIndex++);
            if (var6 == "rowset.provider.classname".length()) {
               var3 = getPropertyNames(false);
            } else {
               var3 = getPropertyNames(true, var5.substring(var6 - 1));
            }

            var2 = var0.getProperty(var3[0]);
            var1.setClassname(var2);
            var1.setVendor(var0.getProperty(var3[1]));
            var1.setVersion(var0.getProperty(var3[2]));
            implementations.put(var2, var1);
         }
      }

   }

   private static String[] getPropertyNames(boolean var0) {
      return getPropertyNames(var0, (String)null);
   }

   private static String[] getPropertyNames(boolean var0, String var1) {
      String var2 = ".";
      String[] var3 = new String[]{"rowset.provider.classname", "rowset.provider.vendor", "rowset.provider.version"};
      if (!var0) {
         return var3;
      } else {
         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = var3[var4] + var2 + var1;
         }

         return var3;
      }
   }

   private static void showImpl(ProviderImpl var0) {
      System.out.println("Provider implementation:");
      System.out.println("Classname: " + var0.getClassname());
      System.out.println("Vendor: " + var0.getVendor());
      System.out.println("Version: " + var0.getVersion());
      System.out.println("Impl index: " + var0.getIndex());
   }

   public static SyncProvider getInstance(String var0) throws SyncFactoryException {
      if (var0 == null) {
         throw new SyncFactoryException("The providerID cannot be null");
      } else {
         initMapIfNecessary();
         initJNDIContext();
         ProviderImpl var1 = (ProviderImpl)implementations.get(var0);
         if (var1 == null) {
            return new RIOptimisticProvider();
         } else {
            try {
               ReflectUtil.checkPackageAccess(var0);
            } catch (AccessControlException var7) {
               SyncFactoryException var3 = new SyncFactoryException();
               var3.initCause(var7);
               throw var3;
            }

            Class var2 = null;

            try {
               ClassLoader var8 = Thread.currentThread().getContextClassLoader();
               var2 = Class.forName(var0, true, var8);
               return (SyncProvider)(var2 != null ? (SyncProvider)var2.newInstance() : new RIOptimisticProvider());
            } catch (IllegalAccessException var4) {
               throw new SyncFactoryException("IllegalAccessException: " + var4.getMessage());
            } catch (InstantiationException var5) {
               throw new SyncFactoryException("InstantiationException: " + var5.getMessage());
            } catch (ClassNotFoundException var6) {
               throw new SyncFactoryException("ClassNotFoundException: " + var6.getMessage());
            }
         }
      }
   }

   public static Enumeration<SyncProvider> getRegisteredProviders() throws SyncFactoryException {
      initMapIfNecessary();
      return implementations.elements();
   }

   public static void setLogger(Logger var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SET_SYNCFACTORY_PERMISSION);
      }

      if (var0 == null) {
         throw new NullPointerException("You must provide a Logger");
      } else {
         rsLogger = var0;
      }
   }

   public static void setLogger(Logger var0, Level var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(SET_SYNCFACTORY_PERMISSION);
      }

      if (var0 == null) {
         throw new NullPointerException("You must provide a Logger");
      } else {
         var0.setLevel(var1);
         rsLogger = var0;
      }
   }

   public static Logger getLogger() throws SyncFactoryException {
      Logger var0 = rsLogger;
      if (var0 == null) {
         throw new SyncFactoryException("(SyncFactory) : No logger has been set");
      } else {
         return var0;
      }
   }

   public static synchronized void setJNDIContext(Context var0) throws SyncFactoryException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SET_SYNCFACTORY_PERMISSION);
      }

      if (var0 == null) {
         throw new SyncFactoryException("Invalid JNDI context supplied");
      } else {
         ic = var0;
      }
   }

   private static synchronized void initJNDIContext() throws SyncFactoryException {
      if (ic != null && !lazyJNDICtxRefresh) {
         try {
            parseProperties(parseJNDIContext());
            lazyJNDICtxRefresh = true;
         } catch (NamingException var1) {
            var1.printStackTrace();
            throw new SyncFactoryException("SPI: NamingException: " + var1.getExplanation());
         } catch (Exception var2) {
            var2.printStackTrace();
            throw new SyncFactoryException("SPI: Exception: " + var2.getMessage());
         }
      }

   }

   private static Properties parseJNDIContext() throws NamingException {
      NamingEnumeration var0 = ic.listBindings("");
      Properties var1 = new Properties();
      enumerateBindings(var0, var1);
      return var1;
   }

   private static void enumerateBindings(NamingEnumeration<?> var0, Properties var1) throws NamingException {
      boolean var2 = false;

      try {
         Binding var3 = null;
         Object var4 = null;
         String var5 = null;

         while(var0.hasMore()) {
            var3 = (Binding)var0.next();
            var5 = var3.getName();
            var4 = var3.getObject();
            if (!(ic.lookup(var5) instanceof Context) && ic.lookup(var5) instanceof SyncProvider) {
               var2 = true;
            }

            if (var2) {
               SyncProvider var6 = (SyncProvider)var4;
               var1.put("rowset.provider.classname", var6.getProviderID());
               var2 = false;
            }
         }
      } catch (NotContextException var7) {
         var0.next();
         enumerateBindings(var0, var1);
      }

   }

   // $FF: synthetic method
   SyncFactory(Object var1) {
      this();
   }

   private static class SyncFactoryHolder {
      static final SyncFactory factory = new SyncFactory();
   }
}
