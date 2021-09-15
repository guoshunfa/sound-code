package java.util.logging;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.WeakHashMap;
import sun.misc.JavaAWTAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public class LogManager {
   private static final LogManager manager;
   private volatile Properties props;
   private static final Level defaultLevel;
   private final Map<Object, Integer> listenerMap;
   private final LogManager.LoggerContext systemContext;
   private final LogManager.LoggerContext userContext;
   private volatile Logger rootLogger;
   private volatile boolean readPrimordialConfiguration;
   private boolean initializedGlobalHandlers;
   private boolean deathImminent;
   private boolean initializedCalled;
   private volatile boolean initializationDone;
   private WeakHashMap<Object, LogManager.LoggerContext> contextsMap;
   private final ReferenceQueue<Logger> loggerRefQueue;
   private static final int MAX_ITERATIONS = 400;
   private final Permission controlPermission;
   private static LoggingMXBean loggingMXBean;
   public static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";

   protected LogManager() {
      this(checkSubclassPermissions());
   }

   private LogManager(Void var1) {
      this.props = new Properties();
      this.listenerMap = new HashMap();
      this.systemContext = new LogManager.SystemLoggerContext();
      this.userContext = new LogManager.LoggerContext();
      this.initializedGlobalHandlers = true;
      this.initializedCalled = false;
      this.initializationDone = false;
      this.contextsMap = null;
      this.loggerRefQueue = new ReferenceQueue();
      this.controlPermission = new LoggingPermission("control", (String)null);

      try {
         Runtime.getRuntime().addShutdownHook(new LogManager.Cleaner());
      } catch (IllegalStateException var3) {
      }

   }

   private static Void checkSubclassPermissions() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("shutdownHooks"));
         var0.checkPermission(new RuntimePermission("setContextClassLoader"));
      }

      return null;
   }

   final void ensureLogManagerInitialized() {
      final LogManager var1 = this;
      if (!this.initializationDone && this == manager) {
         synchronized(this) {
            boolean var3 = this.initializedCalled;

            assert this.initializedCalled || !this.initializationDone : "Initialization can't be done if initialized has not been called!";

            if (!var3 && !this.initializationDone) {
               this.initializedCalled = true;

               try {
                  AccessController.doPrivileged(new PrivilegedAction<Object>() {
                     public Object run() {
                        assert LogManager.this.rootLogger == null;

                        assert LogManager.this.initializedCalled && !LogManager.this.initializationDone;

                        var1.readPrimordialConfiguration();
                        var1.rootLogger = var1.new RootLogger();
                        var1.addLogger(var1.rootLogger);
                        if (!var1.rootLogger.isLevelInitialized()) {
                           var1.rootLogger.setLevel(LogManager.defaultLevel);
                        }

                        Logger var1x = Logger.global;
                        var1.addLogger(var1x);
                        return null;
                     }
                  });
               } finally {
                  this.initializationDone = true;
               }

            }
         }
      }
   }

   public static LogManager getLogManager() {
      if (manager != null) {
         manager.ensureLogManagerInitialized();
      }

      return manager;
   }

   private void readPrimordialConfiguration() {
      if (!this.readPrimordialConfiguration) {
         synchronized(this) {
            if (!this.readPrimordialConfiguration) {
               if (System.out == null) {
                  return;
               }

               this.readPrimordialConfiguration = true;

               try {
                  AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                     public Void run() throws Exception {
                        LogManager.this.readConfiguration();
                        PlatformLogger.redirectPlatformLoggers();
                        return null;
                     }
                  });
               } catch (Exception var4) {
                  assert false : "Exception raised while reading logging configuration: " + var4;
               }
            }
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void addPropertyChangeListener(PropertyChangeListener var1) throws SecurityException {
      PropertyChangeListener var2 = (PropertyChangeListener)Objects.requireNonNull(var1);
      this.checkPermission();
      synchronized(this.listenerMap) {
         Integer var4 = (Integer)this.listenerMap.get(var2);
         var4 = var4 == null ? 1 : var4 + 1;
         this.listenerMap.put(var2, var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public void removePropertyChangeListener(PropertyChangeListener var1) throws SecurityException {
      this.checkPermission();
      if (var1 != null) {
         PropertyChangeListener var2 = var1;
         synchronized(this.listenerMap) {
            Integer var4 = (Integer)this.listenerMap.get(var2);
            if (var4 != null) {
               int var5 = var4;
               if (var5 == 1) {
                  this.listenerMap.remove(var2);
               } else {
                  assert var5 > 1;

                  this.listenerMap.put(var2, var5 - 1);
               }
            }
         }
      }

   }

   private LogManager.LoggerContext getUserContext() {
      LogManager.LoggerContext var1 = null;
      SecurityManager var2 = System.getSecurityManager();
      JavaAWTAccess var3 = SharedSecrets.getJavaAWTAccess();
      if (var2 != null && var3 != null) {
         Object var4 = var3.getAppletContext();
         if (var4 != null) {
            synchronized(var3) {
               if (this.contextsMap == null) {
                  this.contextsMap = new WeakHashMap();
               }

               var1 = (LogManager.LoggerContext)this.contextsMap.get(var4);
               if (var1 == null) {
                  var1 = new LogManager.LoggerContext();
                  this.contextsMap.put(var4, var1);
               }
            }
         }
      }

      return var1 != null ? var1 : this.userContext;
   }

   final LogManager.LoggerContext getSystemContext() {
      return this.systemContext;
   }

   private List<LogManager.LoggerContext> contexts() {
      ArrayList var1 = new ArrayList();
      var1.add(this.getSystemContext());
      var1.add(this.getUserContext());
      return var1;
   }

   Logger demandLogger(String var1, String var2, Class<?> var3) {
      Logger var4 = this.getLogger(var1);
      if (var4 == null) {
         Logger var5 = new Logger(var1, var2, var3, this, false);

         do {
            if (this.addLogger(var5)) {
               return var5;
            }

            var4 = this.getLogger(var1);
         } while(var4 == null);
      }

      return var4;
   }

   Logger demandSystemLogger(String var1, String var2) {
      final Logger var3 = this.getSystemContext().demandLogger(var1, var2);

      final Logger var4;
      do {
         if (this.addLogger(var3)) {
            var4 = var3;
         } else {
            var4 = this.getLogger(var1);
         }
      } while(var4 == null);

      if (var4 != var3 && var3.accessCheckedHandlers().length == 0) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Handler[] var1 = var4.accessCheckedHandlers();
               int var2 = var1.length;

               for(int var3x = 0; var3x < var2; ++var3x) {
                  Handler var4x = var1[var3x];
                  var3.addHandler(var4x);
               }

               return null;
            }
         });
      }

      return var3;
   }

   private void loadLoggerHandlers(final Logger var1, String var2, final String var3) {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            String[] var1x = LogManager.this.parseClassNames(var3);

            for(int var2 = 0; var2 < var1x.length; ++var2) {
               String var3x = var1x[var2];

               try {
                  Class var4 = ClassLoader.getSystemClassLoader().loadClass(var3x);
                  Handler var5 = (Handler)var4.newInstance();
                  String var6 = LogManager.this.getProperty(var3x + ".level");
                  if (var6 != null) {
                     Level var7 = Level.findLevel(var6);
                     if (var7 != null) {
                        var5.setLevel(var7);
                     } else {
                        System.err.println("Can't set level for " + var3x);
                     }
                  }

                  var1.addHandler(var5);
               } catch (Exception var8) {
                  System.err.println("Can't load log handler \"" + var3x + "\"");
                  System.err.println("" + var8);
                  var8.printStackTrace();
               }
            }

            return null;
         }
      });
   }

   final void drainLoggerRefQueueBounded() {
      for(int var1 = 0; var1 < 400 && this.loggerRefQueue != null; ++var1) {
         LogManager.LoggerWeakRef var2 = (LogManager.LoggerWeakRef)this.loggerRefQueue.poll();
         if (var2 == null) {
            break;
         }

         var2.dispose();
      }

   }

   public boolean addLogger(Logger var1) {
      String var2 = var1.getName();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.drainLoggerRefQueueBounded();
         LogManager.LoggerContext var3 = this.getUserContext();
         if (var3.addLocalLogger(var1)) {
            this.loadLoggerHandlers(var1, var2, var2 + ".handlers");
            return true;
         } else {
            return false;
         }
      }
   }

   private static void doSetLevel(final Logger var0, final Level var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 == null) {
         var0.setLevel(var1);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               var0.setLevel(var1);
               return null;
            }
         });
      }
   }

   private static void doSetParent(final Logger var0, final Logger var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 == null) {
         var0.setParent(var1);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               var0.setParent(var1);
               return null;
            }
         });
      }
   }

   public Logger getLogger(String var1) {
      return this.getUserContext().findLogger(var1);
   }

   public Enumeration<String> getLoggerNames() {
      return this.getUserContext().getLoggerNames();
   }

   public void readConfiguration() throws IOException, SecurityException {
      this.checkPermission();
      String var1 = System.getProperty("java.util.logging.config.class");
      if (var1 != null) {
         try {
            try {
               Class var19 = ClassLoader.getSystemClassLoader().loadClass(var1);
               var19.newInstance();
               return;
            } catch (ClassNotFoundException var16) {
               Class var21 = Thread.currentThread().getContextClassLoader().loadClass(var1);
               var21.newInstance();
               return;
            }
         } catch (Exception var18) {
            System.err.println("Logging configuration class \"" + var1 + "\" failed");
            System.err.println("" + var18);
         }
      }

      String var2 = System.getProperty("java.util.logging.config.file");
      if (var2 == null) {
         var2 = System.getProperty("java.home");
         if (var2 == null) {
            throw new Error("Can't find java.home ??");
         }

         File var3 = new File(var2, "lib");
         var3 = new File(var3, "logging.properties");
         var2 = var3.getCanonicalPath();
      }

      FileInputStream var20 = new FileInputStream(var2);
      Throwable var4 = null;

      try {
         BufferedInputStream var5 = new BufferedInputStream(var20);
         this.readConfiguration(var5);
      } catch (Throwable var15) {
         var4 = var15;
         throw var15;
      } finally {
         if (var20 != null) {
            if (var4 != null) {
               try {
                  var20.close();
               } catch (Throwable var14) {
                  var4.addSuppressed(var14);
               }
            } else {
               var20.close();
            }
         }

      }

   }

   public void reset() throws SecurityException {
      this.checkPermission();
      synchronized(this) {
         this.props = new Properties();
         this.initializedGlobalHandlers = true;
      }

      Iterator var1 = this.contexts().iterator();

      while(var1.hasNext()) {
         LogManager.LoggerContext var2 = (LogManager.LoggerContext)var1.next();
         Enumeration var3 = var2.getLoggerNames();

         while(var3.hasMoreElements()) {
            String var4 = (String)var3.nextElement();
            Logger var5 = var2.findLogger(var4);
            if (var5 != null) {
               this.resetLogger(var5);
            }
         }
      }

   }

   private void resetLogger(Logger var1) {
      Handler[] var2 = var1.getHandlers();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Handler var4 = var2[var3];
         var1.removeHandler(var4);

         try {
            var4.close();
         } catch (Exception var6) {
         }
      }

      String var7 = var1.getName();
      if (var7 != null && var7.equals("")) {
         var1.setLevel(defaultLevel);
      } else {
         var1.setLevel((Level)null);
      }

   }

   private String[] parseClassNames(String var1) {
      String var2 = this.getProperty(var1);
      if (var2 == null) {
         return new String[0];
      } else {
         var2 = var2.trim();
         int var3 = 0;
         ArrayList var4 = new ArrayList();

         while(var3 < var2.length()) {
            int var5;
            for(var5 = var3; var5 < var2.length() && !Character.isWhitespace(var2.charAt(var5)) && var2.charAt(var5) != ','; ++var5) {
            }

            String var6 = var2.substring(var3, var5);
            var3 = var5 + 1;
            var6 = var6.trim();
            if (var6.length() != 0) {
               var4.add(var6);
            }
         }

         return (String[])var4.toArray(new String[var4.size()]);
      }
   }

   public void readConfiguration(InputStream var1) throws IOException, SecurityException {
      this.checkPermission();
      this.reset();
      this.props.load(var1);
      String[] var2 = this.parseClassNames("config");

      for(int var3 = 0; var3 < var2.length; ++var3) {
         String var4 = var2[var3];

         try {
            Class var5 = ClassLoader.getSystemClassLoader().loadClass(var4);
            var5.newInstance();
         } catch (Exception var12) {
            System.err.println("Can't load config class \"" + var4 + "\"");
            System.err.println("" + var12);
         }
      }

      this.setLevelsOnExistingLoggers();
      HashMap var14 = null;
      synchronized(this.listenerMap) {
         if (!this.listenerMap.isEmpty()) {
            var14 = new HashMap(this.listenerMap);
         }
      }

      if (var14 != null) {
         assert LogManager.Beans.isBeansPresent();

         Object var15 = LogManager.Beans.newPropertyChangeEvent(LogManager.class, (String)null, (Object)null, (Object)null);
         Iterator var16 = var14.entrySet().iterator();

         while(var16.hasNext()) {
            Map.Entry var6 = (Map.Entry)var16.next();
            Object var7 = var6.getKey();
            int var8 = (Integer)var6.getValue();

            for(int var9 = 0; var9 < var8; ++var9) {
               LogManager.Beans.invokePropertyChange(var7, var15);
            }
         }
      }

      synchronized(this) {
         this.initializedGlobalHandlers = false;
      }
   }

   public String getProperty(String var1) {
      return this.props.getProperty(var1);
   }

   String getStringProperty(String var1, String var2) {
      String var3 = this.getProperty(var1);
      return var3 == null ? var2 : var3.trim();
   }

   int getIntProperty(String var1, int var2) {
      String var3 = this.getProperty(var1);
      if (var3 == null) {
         return var2;
      } else {
         try {
            return Integer.parseInt(var3.trim());
         } catch (Exception var5) {
            return var2;
         }
      }
   }

   boolean getBooleanProperty(String var1, boolean var2) {
      String var3 = this.getProperty(var1);
      if (var3 == null) {
         return var2;
      } else {
         var3 = var3.toLowerCase();
         if (!var3.equals("true") && !var3.equals("1")) {
            return !var3.equals("false") && !var3.equals("0") ? var2 : false;
         } else {
            return true;
         }
      }
   }

   Level getLevelProperty(String var1, Level var2) {
      String var3 = this.getProperty(var1);
      if (var3 == null) {
         return var2;
      } else {
         Level var4 = Level.findLevel(var3.trim());
         return var4 != null ? var4 : var2;
      }
   }

   Filter getFilterProperty(String var1, Filter var2) {
      String var3 = this.getProperty(var1);

      try {
         if (var3 != null) {
            Class var4 = ClassLoader.getSystemClassLoader().loadClass(var3);
            return (Filter)var4.newInstance();
         }
      } catch (Exception var5) {
      }

      return var2;
   }

   Formatter getFormatterProperty(String var1, Formatter var2) {
      String var3 = this.getProperty(var1);

      try {
         if (var3 != null) {
            Class var4 = ClassLoader.getSystemClassLoader().loadClass(var3);
            return (Formatter)var4.newInstance();
         }
      } catch (Exception var5) {
      }

      return var2;
   }

   private synchronized void initializeGlobalHandlers() {
      if (!this.initializedGlobalHandlers) {
         this.initializedGlobalHandlers = true;
         if (!this.deathImminent) {
            this.loadLoggerHandlers(this.rootLogger, (String)null, "handlers");
         }
      }
   }

   void checkPermission() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(this.controlPermission);
      }

   }

   public void checkAccess() throws SecurityException {
      this.checkPermission();
   }

   private synchronized void setLevelsOnExistingLoggers() {
      Enumeration var1 = this.props.propertyNames();

      while(true) {
         while(true) {
            String var2;
            do {
               if (!var1.hasMoreElements()) {
                  return;
               }

               var2 = (String)var1.nextElement();
            } while(!var2.endsWith(".level"));

            int var3 = var2.length() - 6;
            String var4 = var2.substring(0, var3);
            Level var5 = this.getLevelProperty(var2, (Level)null);
            if (var5 == null) {
               System.err.println("Bad level value for property: " + var2);
            } else {
               Iterator var6 = this.contexts().iterator();

               while(var6.hasNext()) {
                  LogManager.LoggerContext var7 = (LogManager.LoggerContext)var6.next();
                  Logger var8 = var7.findLogger(var4);
                  if (var8 != null) {
                     var8.setLevel(var5);
                  }
               }
            }
         }
      }
   }

   public static synchronized LoggingMXBean getLoggingMXBean() {
      if (loggingMXBean == null) {
         loggingMXBean = new Logging();
      }

      return loggingMXBean;
   }

   static {
      defaultLevel = Level.INFO;
      manager = (LogManager)AccessController.doPrivileged(new PrivilegedAction<LogManager>() {
         public LogManager run() {
            LogManager var1 = null;
            String var2 = null;

            try {
               var2 = System.getProperty("java.util.logging.manager");
               if (var2 != null) {
                  try {
                     Class var3 = ClassLoader.getSystemClassLoader().loadClass(var2);
                     var1 = (LogManager)var3.newInstance();
                  } catch (ClassNotFoundException var5) {
                     Class var4 = Thread.currentThread().getContextClassLoader().loadClass(var2);
                     var1 = (LogManager)var4.newInstance();
                  }
               }
            } catch (Exception var6) {
               System.err.println("Could not load Logmanager \"" + var2 + "\"");
               var6.printStackTrace();
            }

            if (var1 == null) {
               var1 = new LogManager();
            }

            return var1;
         }
      });
      loggingMXBean = null;
   }

   private static class Beans {
      private static final Class<?> propertyChangeListenerClass = getClass("java.beans.PropertyChangeListener");
      private static final Class<?> propertyChangeEventClass = getClass("java.beans.PropertyChangeEvent");
      private static final Method propertyChangeMethod;
      private static final Constructor<?> propertyEventCtor;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, LogManager.Beans.class.getClassLoader());
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Constructor<?> getConstructor(Class<?> var0, Class<?>... var1) {
         try {
            return var0 == null ? null : var0.getDeclaredConstructor(var1);
         } catch (NoSuchMethodException var3) {
            throw new AssertionError(var3);
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         try {
            return var0 == null ? null : var0.getMethod(var1, var2);
         } catch (NoSuchMethodException var4) {
            throw new AssertionError(var4);
         }
      }

      static boolean isBeansPresent() {
         return propertyChangeListenerClass != null && propertyChangeEventClass != null;
      }

      static Object newPropertyChangeEvent(Object var0, String var1, Object var2, Object var3) {
         try {
            return propertyEventCtor.newInstance(var0, var1, var2, var3);
         } catch (IllegalAccessException | InstantiationException var6) {
            throw new AssertionError(var6);
         } catch (InvocationTargetException var7) {
            Throwable var5 = var7.getCause();
            if (var5 instanceof Error) {
               throw (Error)var5;
            } else if (var5 instanceof RuntimeException) {
               throw (RuntimeException)var5;
            } else {
               throw new AssertionError(var7);
            }
         }
      }

      static void invokePropertyChange(Object var0, Object var1) {
         try {
            propertyChangeMethod.invoke(var0, var1);
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         } catch (InvocationTargetException var5) {
            Throwable var3 = var5.getCause();
            if (var3 instanceof Error) {
               throw (Error)var3;
            } else if (var3 instanceof RuntimeException) {
               throw (RuntimeException)var3;
            } else {
               throw new AssertionError(var5);
            }
         }
      }

      static {
         propertyChangeMethod = getMethod(propertyChangeListenerClass, "propertyChange", propertyChangeEventClass);
         propertyEventCtor = getConstructor(propertyChangeEventClass, Object.class, String.class, Object.class, Object.class);
      }
   }

   private final class RootLogger extends Logger {
      private RootLogger() {
         super("", (String)null, (Class)null, LogManager.this, true);
      }

      public void log(LogRecord var1) {
         LogManager.this.initializeGlobalHandlers();
         super.log(var1);
      }

      public void addHandler(Handler var1) {
         LogManager.this.initializeGlobalHandlers();
         super.addHandler(var1);
      }

      public void removeHandler(Handler var1) {
         LogManager.this.initializeGlobalHandlers();
         super.removeHandler(var1);
      }

      Handler[] accessCheckedHandlers() {
         LogManager.this.initializeGlobalHandlers();
         return super.accessCheckedHandlers();
      }

      // $FF: synthetic method
      RootLogger(Object var2) {
         this();
      }
   }

   private static class LogNode {
      HashMap<String, LogManager.LogNode> children;
      LogManager.LoggerWeakRef loggerRef;
      LogManager.LogNode parent;
      final LogManager.LoggerContext context;

      LogNode(LogManager.LogNode var1, LogManager.LoggerContext var2) {
         this.parent = var1;
         this.context = var2;
      }

      void walkAndSetParent(Logger var1) {
         if (this.children != null) {
            Iterator var2 = this.children.values().iterator();

            while(var2.hasNext()) {
               LogManager.LogNode var3 = (LogManager.LogNode)var2.next();
               LogManager.LoggerWeakRef var4 = var3.loggerRef;
               Logger var5 = var4 == null ? null : (Logger)var4.get();
               if (var5 == null) {
                  var3.walkAndSetParent(var1);
               } else {
                  LogManager.doSetParent(var5, var1);
               }
            }

         }
      }
   }

   final class LoggerWeakRef extends WeakReference<Logger> {
      private String name;
      private LogManager.LogNode node;
      private WeakReference<Logger> parentRef;
      private boolean disposed = false;

      LoggerWeakRef(Logger var2) {
         super(var2, LogManager.this.loggerRefQueue);
         this.name = var2.getName();
      }

      void dispose() {
         synchronized(this) {
            if (this.disposed) {
               return;
            }

            this.disposed = true;
         }

         LogManager.LogNode var1 = this.node;
         if (var1 != null) {
            synchronized(var1.context) {
               var1.context.removeLoggerRef(this.name, this);
               this.name = null;
               if (var1.loggerRef == this) {
                  var1.loggerRef = null;
               }

               this.node = null;
            }
         }

         if (this.parentRef != null) {
            Logger var2 = (Logger)this.parentRef.get();
            if (var2 != null) {
               var2.removeChildLogger(this);
            }

            this.parentRef = null;
         }

      }

      void setNode(LogManager.LogNode var1) {
         this.node = var1;
      }

      void setParentRef(WeakReference<Logger> var1) {
         this.parentRef = var1;
      }
   }

   final class SystemLoggerContext extends LogManager.LoggerContext {
      SystemLoggerContext() {
         super(null);
      }

      Logger demandLogger(String var1, String var2) {
         Logger var3 = this.findLogger(var1);
         if (var3 == null) {
            Logger var4 = new Logger(var1, var2, (Class)null, this.getOwner(), true);

            do {
               if (this.addLocalLogger(var4)) {
                  var3 = var4;
               } else {
                  var3 = this.findLogger(var1);
               }
            } while(var3 == null);
         }

         return var3;
      }
   }

   class LoggerContext {
      private final Hashtable<String, LogManager.LoggerWeakRef> namedLoggers;
      private final LogManager.LogNode root;

      private LoggerContext() {
         this.namedLoggers = new Hashtable();
         this.root = new LogManager.LogNode((LogManager.LogNode)null, this);
      }

      final boolean requiresDefaultLoggers() {
         boolean var1 = this.getOwner() == LogManager.manager;
         if (var1) {
            this.getOwner().ensureLogManagerInitialized();
         }

         return var1;
      }

      final LogManager getOwner() {
         return LogManager.this;
      }

      final Logger getRootLogger() {
         return this.getOwner().rootLogger;
      }

      final Logger getGlobalLogger() {
         Logger var1 = Logger.global;
         return var1;
      }

      Logger demandLogger(String var1, String var2) {
         LogManager var3 = this.getOwner();
         return var3.demandLogger(var1, var2, (Class)null);
      }

      private void ensureInitialized() {
         if (this.requiresDefaultLoggers()) {
            this.ensureDefaultLogger(this.getRootLogger());
            this.ensureDefaultLogger(this.getGlobalLogger());
         }

      }

      synchronized Logger findLogger(String var1) {
         this.ensureInitialized();
         LogManager.LoggerWeakRef var2 = (LogManager.LoggerWeakRef)this.namedLoggers.get(var1);
         if (var2 == null) {
            return null;
         } else {
            Logger var3 = (Logger)var2.get();
            if (var3 == null) {
               var2.dispose();
            }

            return var3;
         }
      }

      private void ensureAllDefaultLoggers(Logger var1) {
         if (this.requiresDefaultLoggers()) {
            String var2 = var1.getName();
            if (!var2.isEmpty()) {
               this.ensureDefaultLogger(this.getRootLogger());
               if (!"global".equals(var2)) {
                  this.ensureDefaultLogger(this.getGlobalLogger());
               }
            }
         }

      }

      private void ensureDefaultLogger(Logger var1) {
         if (!this.requiresDefaultLoggers() || var1 == null || var1 != Logger.global && var1 != LogManager.this.rootLogger) {
            assert var1 == null;

         } else {
            if (!this.namedLoggers.containsKey(var1.getName())) {
               this.addLocalLogger(var1, false);
            }

         }
      }

      boolean addLocalLogger(Logger var1) {
         return this.addLocalLogger(var1, this.requiresDefaultLoggers());
      }

      synchronized boolean addLocalLogger(Logger var1, boolean var2) {
         if (var2) {
            this.ensureAllDefaultLoggers(var1);
         }

         String var3 = var1.getName();
         if (var3 == null) {
            throw new NullPointerException();
         } else {
            LogManager.LoggerWeakRef var4 = (LogManager.LoggerWeakRef)this.namedLoggers.get(var3);
            if (var4 != null) {
               if (var4.get() != null) {
                  return false;
               }

               var4.dispose();
            }

            LogManager var5 = this.getOwner();
            var1.setLogManager(var5);
            var4 = var5.new LoggerWeakRef(var1);
            this.namedLoggers.put(var3, var4);
            Level var6 = var5.getLevelProperty(var3 + ".level", (Level)null);
            if (var6 != null && !var1.isLevelInitialized()) {
               LogManager.doSetLevel(var1, var6);
            }

            this.processParentHandlers(var1, var3);
            LogManager.LogNode var7 = this.getNode(var3);
            var7.loggerRef = var4;
            Logger var8 = null;

            for(LogManager.LogNode var9 = var7.parent; var9 != null; var9 = var9.parent) {
               LogManager.LoggerWeakRef var10 = var9.loggerRef;
               if (var10 != null) {
                  var8 = (Logger)var10.get();
                  if (var8 != null) {
                     break;
                  }
               }
            }

            if (var8 != null) {
               LogManager.doSetParent(var1, var8);
            }

            var7.walkAndSetParent(var1);
            var4.setNode(var7);
            return true;
         }
      }

      synchronized void removeLoggerRef(String var1, LogManager.LoggerWeakRef var2) {
         this.namedLoggers.remove(var1, var2);
      }

      synchronized Enumeration<String> getLoggerNames() {
         this.ensureInitialized();
         return this.namedLoggers.keys();
      }

      private void processParentHandlers(final Logger var1, final String var2) {
         final LogManager var3 = this.getOwner();
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               if (var1 != var3.rootLogger) {
                  boolean var1x = var3.getBooleanProperty(var2 + ".useParentHandlers", true);
                  if (!var1x) {
                     var1.setUseParentHandlers(false);
                  }
               }

               return null;
            }
         });
         int var4 = 1;

         while(true) {
            int var5 = var2.indexOf(".", var4);
            if (var5 < 0) {
               return;
            }

            String var6 = var2.substring(0, var5);
            if (var3.getProperty(var6 + ".level") != null || var3.getProperty(var6 + ".handlers") != null) {
               this.demandLogger(var6, (String)null);
            }

            var4 = var5 + 1;
         }
      }

      LogManager.LogNode getNode(String var1) {
         if (var1 != null && !var1.equals("")) {
            LogManager.LogNode var2;
            LogManager.LogNode var5;
            for(var2 = this.root; var1.length() > 0; var2 = var5) {
               int var3 = var1.indexOf(".");
               String var4;
               if (var3 > 0) {
                  var4 = var1.substring(0, var3);
                  var1 = var1.substring(var3 + 1);
               } else {
                  var4 = var1;
                  var1 = "";
               }

               if (var2.children == null) {
                  var2.children = new HashMap();
               }

               var5 = (LogManager.LogNode)var2.children.get(var4);
               if (var5 == null) {
                  var5 = new LogManager.LogNode(var2, this);
                  var2.children.put(var4, var5);
               }
            }

            return var2;
         } else {
            return this.root;
         }
      }

      // $FF: synthetic method
      LoggerContext(Object var2) {
         this();
      }
   }

   private class Cleaner extends Thread {
      private Cleaner() {
         this.setContextClassLoader((ClassLoader)null);
      }

      public void run() {
         LogManager var1 = LogManager.manager;
         synchronized(LogManager.this) {
            LogManager.this.deathImminent = true;
            LogManager.this.initializedGlobalHandlers = true;
         }

         LogManager.this.reset();
      }

      // $FF: synthetic method
      Cleaner(Object var2) {
         this();
      }
   }
}
