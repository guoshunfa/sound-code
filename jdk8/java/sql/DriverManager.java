package java.sql;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class DriverManager {
   private static final CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList();
   private static volatile int loginTimeout = 0;
   private static volatile PrintWriter logWriter = null;
   private static volatile PrintStream logStream = null;
   private static final Object logSync = new Object();
   static final SQLPermission SET_LOG_PERMISSION;
   static final SQLPermission DEREGISTER_DRIVER_PERMISSION;

   private DriverManager() {
   }

   public static PrintWriter getLogWriter() {
      return logWriter;
   }

   public static void setLogWriter(PrintWriter var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SET_LOG_PERMISSION);
      }

      logStream = null;
      logWriter = var0;
   }

   @CallerSensitive
   public static Connection getConnection(String var0, Properties var1) throws SQLException {
      return getConnection(var0, var1, Reflection.getCallerClass());
   }

   @CallerSensitive
   public static Connection getConnection(String var0, String var1, String var2) throws SQLException {
      Properties var3 = new Properties();
      if (var1 != null) {
         var3.put("user", var1);
      }

      if (var2 != null) {
         var3.put("password", var2);
      }

      return getConnection(var0, var3, Reflection.getCallerClass());
   }

   @CallerSensitive
   public static Connection getConnection(String var0) throws SQLException {
      Properties var1 = new Properties();
      return getConnection(var0, var1, Reflection.getCallerClass());
   }

   @CallerSensitive
   public static Driver getDriver(String var0) throws SQLException {
      println("DriverManager.getDriver(\"" + var0 + "\")");
      Class var1 = Reflection.getCallerClass();
      Iterator var2 = registeredDrivers.iterator();

      while(var2.hasNext()) {
         DriverInfo var3 = (DriverInfo)var2.next();
         if (isDriverAllowed(var3.driver, var1)) {
            try {
               if (var3.driver.acceptsURL(var0)) {
                  println("getDriver returning " + var3.driver.getClass().getName());
                  return var3.driver;
               }
            } catch (SQLException var5) {
            }
         } else {
            println("    skipping: " + var3.driver.getClass().getName());
         }
      }

      println("getDriver: no suitable driver");
      throw new SQLException("No suitable driver", "08001");
   }

   public static synchronized void registerDriver(Driver var0) throws SQLException {
      registerDriver(var0, (DriverAction)null);
   }

   public static synchronized void registerDriver(Driver var0, DriverAction var1) throws SQLException {
      if (var0 != null) {
         registeredDrivers.addIfAbsent(new DriverInfo(var0, var1));
         println("registerDriver: " + var0);
      } else {
         throw new NullPointerException();
      }
   }

   @CallerSensitive
   public static synchronized void deregisterDriver(Driver var0) throws SQLException {
      if (var0 != null) {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(DEREGISTER_DRIVER_PERMISSION);
         }

         println("DriverManager.deregisterDriver: " + var0);
         DriverInfo var2 = new DriverInfo(var0, (DriverAction)null);
         if (registeredDrivers.contains(var2)) {
            if (!isDriverAllowed(var0, Reflection.getCallerClass())) {
               throw new SecurityException();
            }

            DriverInfo var3 = (DriverInfo)registeredDrivers.get(registeredDrivers.indexOf(var2));
            if (var3.action() != null) {
               var3.action().deregister();
            }

            registeredDrivers.remove(var2);
         } else {
            println("    couldn't find driver to unload");
         }

      }
   }

   @CallerSensitive
   public static Enumeration<Driver> getDrivers() {
      Vector var0 = new Vector();
      Class var1 = Reflection.getCallerClass();
      Iterator var2 = registeredDrivers.iterator();

      while(var2.hasNext()) {
         DriverInfo var3 = (DriverInfo)var2.next();
         if (isDriverAllowed(var3.driver, var1)) {
            var0.addElement(var3.driver);
         } else {
            println("    skipping: " + var3.getClass().getName());
         }
      }

      return var0.elements();
   }

   public static void setLoginTimeout(int var0) {
      loginTimeout = var0;
   }

   public static int getLoginTimeout() {
      return loginTimeout;
   }

   /** @deprecated */
   @Deprecated
   public static void setLogStream(PrintStream var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SET_LOG_PERMISSION);
      }

      logStream = var0;
      if (var0 != null) {
         logWriter = new PrintWriter(var0);
      } else {
         logWriter = null;
      }

   }

   /** @deprecated */
   @Deprecated
   public static PrintStream getLogStream() {
      return logStream;
   }

   public static void println(String var0) {
      synchronized(logSync) {
         if (logWriter != null) {
            logWriter.println(var0);
            logWriter.flush();
         }

      }
   }

   private static boolean isDriverAllowed(Driver var0, Class<?> var1) {
      ClassLoader var2 = var1 != null ? var1.getClassLoader() : null;
      return isDriverAllowed(var0, var2);
   }

   private static boolean isDriverAllowed(Driver var0, ClassLoader var1) {
      boolean var2 = false;
      if (var0 != null) {
         Class var3 = null;

         try {
            var3 = Class.forName(var0.getClass().getName(), true, var1);
         } catch (Exception var5) {
            var2 = false;
         }

         var2 = var3 == var0.getClass();
      }

      return var2;
   }

   private static void loadInitialDrivers() {
      String var0;
      try {
         var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty("jdbc.drivers");
            }
         });
      } catch (Exception var8) {
         var0 = null;
      }

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            ServiceLoader var1 = ServiceLoader.load(Driver.class);
            Iterator var2 = var1.iterator();

            try {
               while(var2.hasNext()) {
                  var2.next();
               }
            } catch (Throwable var4) {
            }

            return null;
         }
      });
      println("DriverManager.initialize: jdbc.drivers = " + var0);
      if (var0 != null && !var0.equals("")) {
         String[] var1 = var0.split(":");
         println("number of Drivers:" + var1.length);
         String[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];

            try {
               println("DriverManager.Initialize: loading " + var5);
               Class.forName(var5, true, ClassLoader.getSystemClassLoader());
            } catch (Exception var7) {
               println("DriverManager.Initialize: load failed: " + var7);
            }
         }

      }
   }

   private static Connection getConnection(String var0, Properties var1, Class<?> var2) throws SQLException {
      ClassLoader var3 = var2 != null ? var2.getClassLoader() : null;
      Class var4 = DriverManager.class;
      synchronized(DriverManager.class) {
         if (var3 == null) {
            var3 = Thread.currentThread().getContextClassLoader();
         }
      }

      if (var0 == null) {
         throw new SQLException("The url cannot be null", "08001");
      } else {
         println("DriverManager.getConnection(\"" + var0 + "\")");
         SQLException var10 = null;
         Iterator var5 = registeredDrivers.iterator();

         while(true) {
            while(var5.hasNext()) {
               DriverInfo var6 = (DriverInfo)var5.next();
               if (isDriverAllowed(var6.driver, var3)) {
                  try {
                     println("    trying " + var6.driver.getClass().getName());
                     Connection var7 = var6.driver.connect(var0, var1);
                     if (var7 != null) {
                        println("getConnection returning " + var6.driver.getClass().getName());
                        return var7;
                     }
                  } catch (SQLException var8) {
                     if (var10 == null) {
                        var10 = var8;
                     }
                  }
               } else {
                  println("    skipping: " + var6.getClass().getName());
               }
            }

            if (var10 != null) {
               println("getConnection failed: " + var10);
               throw var10;
            }

            println("getConnection: no suitable driver found for " + var0);
            throw new SQLException("No suitable driver found for " + var0, "08001");
         }
      }
   }

   static {
      loadInitialDrivers();
      println("JDBC DriverManager initialized");
      SET_LOG_PERMISSION = new SQLPermission("setLog");
      DEREGISTER_DRIVER_PERMISSION = new SQLPermission("deregisterDriver");
   }
}
