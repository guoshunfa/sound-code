package javax.sql.rowset;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.reflect.misc.ReflectUtil;

public class RowSetProvider {
   private static final String ROWSET_DEBUG_PROPERTY = "javax.sql.rowset.RowSetProvider.debug";
   private static final String ROWSET_FACTORY_IMPL = "com.sun.rowset.RowSetFactoryImpl";
   private static final String ROWSET_FACTORY_NAME = "javax.sql.rowset.RowSetFactory";
   private static boolean debug = true;

   protected RowSetProvider() {
   }

   public static RowSetFactory newFactory() throws SQLException {
      RowSetFactory var0 = null;
      String var1 = null;

      try {
         trace("Checking for Rowset System Property...");
         var1 = getSystemProperty("javax.sql.rowset.RowSetFactory");
         if (var1 != null) {
            trace("Found system property, value=" + var1);
            var0 = (RowSetFactory)ReflectUtil.newInstance(getFactoryClass(var1, (ClassLoader)null, true));
         }
      } catch (Exception var3) {
         throw new SQLException("RowSetFactory: " + var1 + " could not be instantiated: ", var3);
      }

      if (var0 == null) {
         var0 = loadViaServiceLoader();
         var0 = var0 == null ? newFactory("com.sun.rowset.RowSetFactoryImpl", (ClassLoader)null) : var0;
      }

      return var0;
   }

   public static RowSetFactory newFactory(String var0, ClassLoader var1) throws SQLException {
      trace("***In newInstance()");
      if (var0 == null) {
         throw new SQLException("Error: factoryClassName cannot be null");
      } else {
         try {
            ReflectUtil.checkPackageAccess(var0);
         } catch (AccessControlException var6) {
            throw new SQLException("Access Exception", var6);
         }

         try {
            Class var2 = getFactoryClass(var0, var1, false);
            RowSetFactory var3 = (RowSetFactory)var2.newInstance();
            if (debug) {
               trace("Created new instance of " + var2 + " using ClassLoader: " + var1);
            }

            return var3;
         } catch (ClassNotFoundException var4) {
            throw new SQLException("Provider " + var0 + " not found", var4);
         } catch (Exception var5) {
            throw new SQLException("Provider " + var0 + " could not be instantiated: " + var5, var5);
         }
      }
   }

   private static ClassLoader getContextClassLoader() throws SecurityException {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            ClassLoader var1 = null;
            var1 = Thread.currentThread().getContextClassLoader();
            if (var1 == null) {
               var1 = ClassLoader.getSystemClassLoader();
            }

            return var1;
         }
      });
   }

   private static Class<?> getFactoryClass(String var0, ClassLoader var1, boolean var2) throws ClassNotFoundException {
      try {
         if (var1 == null) {
            var1 = getContextClassLoader();
            if (var1 == null) {
               throw new ClassNotFoundException();
            } else {
               return var1.loadClass(var0);
            }
         } else {
            return var1.loadClass(var0);
         }
      } catch (ClassNotFoundException var4) {
         if (var2) {
            return Class.forName(var0, true, RowSetFactory.class.getClassLoader());
         } else {
            throw var4;
         }
      }
   }

   private static RowSetFactory loadViaServiceLoader() throws SQLException {
      RowSetFactory var0 = null;

      try {
         trace("***in loadViaServiceLoader():");
         Iterator var1 = ServiceLoader.load(RowSetFactory.class).iterator();
         if (var1.hasNext()) {
            RowSetFactory var2 = (RowSetFactory)var1.next();
            trace(" Loading done by the java.util.ServiceLoader :" + var2.getClass().getName());
            var0 = var2;
         }

         return var0;
      } catch (ServiceConfigurationError var3) {
         throw new SQLException("RowSetFactory: Error locating RowSetFactory using Service Loader API: " + var3, var3);
      }
   }

   private static String getSystemProperty(final String var0) {
      String var1 = null;

      try {
         var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty(var0);
            }
         }), (AccessControlContext)null, new PropertyPermission(var0, "read"));
      } catch (SecurityException var3) {
         trace("error getting " + var0 + ":  " + var3);
         if (debug) {
            var3.printStackTrace();
         }
      }

      return var1;
   }

   private static void trace(String var0) {
      if (debug) {
         System.err.println("###RowSets: " + var0);
      }

   }

   static {
      String var0 = getSystemProperty("javax.sql.rowset.RowSetProvider.debug");
      debug = var0 != null && !"false".equals(var0);
   }
}
