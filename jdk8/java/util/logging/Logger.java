package java.util.logging;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Logger {
   private static final Handler[] emptyHandlers = new Handler[0];
   private static final int offValue;
   static final String SYSTEM_LOGGER_RB_NAME = "sun.util.logging.resources.logging";
   private static final Logger.LoggerBundle SYSTEM_BUNDLE;
   private static final Logger.LoggerBundle NO_RESOURCE_BUNDLE;
   private volatile LogManager manager;
   private String name;
   private final CopyOnWriteArrayList<Handler> handlers;
   private volatile Logger.LoggerBundle loggerBundle;
   private volatile boolean useParentHandlers;
   private volatile Filter filter;
   private boolean anonymous;
   private ResourceBundle catalog;
   private String catalogName;
   private Locale catalogLocale;
   private static final Object treeLock;
   private volatile Logger parent;
   private ArrayList<LogManager.LoggerWeakRef> kids;
   private volatile Level levelObject;
   private volatile int levelValue;
   private WeakReference<ClassLoader> callersClassLoaderRef;
   private final boolean isSystemLogger;
   public static final String GLOBAL_LOGGER_NAME = "global";
   /** @deprecated */
   @Deprecated
   public static final Logger global;

   public static final Logger getGlobal() {
      LogManager.getLogManager();
      return global;
   }

   protected Logger(String var1, String var2) {
      this(var1, var2, (Class)null, LogManager.getLogManager(), false);
   }

   Logger(String var1, String var2, Class<?> var3, LogManager var4, boolean var5) {
      this.handlers = new CopyOnWriteArrayList();
      this.loggerBundle = NO_RESOURCE_BUNDLE;
      this.useParentHandlers = true;
      this.manager = var4;
      this.isSystemLogger = var5;
      this.setupResourceInfo(var2, var3);
      this.name = var1;
      this.levelValue = Level.INFO.intValue();
   }

   private void setCallersClassLoaderRef(Class<?> var1) {
      ClassLoader var2 = var1 != null ? var1.getClassLoader() : null;
      if (var2 != null) {
         this.callersClassLoaderRef = new WeakReference(var2);
      }

   }

   private ClassLoader getCallersClassLoader() {
      return this.callersClassLoaderRef != null ? (ClassLoader)this.callersClassLoaderRef.get() : null;
   }

   private Logger(String var1) {
      this.handlers = new CopyOnWriteArrayList();
      this.loggerBundle = NO_RESOURCE_BUNDLE;
      this.useParentHandlers = true;
      this.name = var1;
      this.isSystemLogger = true;
      this.levelValue = Level.INFO.intValue();
   }

   void setLogManager(LogManager var1) {
      this.manager = var1;
   }

   private void checkPermission() throws SecurityException {
      if (!this.anonymous) {
         if (this.manager == null) {
            this.manager = LogManager.getLogManager();
         }

         this.manager.checkPermission();
      }

   }

   private static Logger demandLogger(String var0, String var1, Class<?> var2) {
      LogManager var3 = LogManager.getLogManager();
      SecurityManager var4 = System.getSecurityManager();
      return var4 != null && !Logger.SystemLoggerHelper.disableCallerCheck && var2.getClassLoader() == null ? var3.demandSystemLogger(var0, var1) : var3.demandLogger(var0, var1, var2);
   }

   @CallerSensitive
   public static Logger getLogger(String var0) {
      return demandLogger(var0, (String)null, Reflection.getCallerClass());
   }

   @CallerSensitive
   public static Logger getLogger(String var0, String var1) {
      Class var2 = Reflection.getCallerClass();
      Logger var3 = demandLogger(var0, var1, var2);
      var3.setupResourceInfo(var1, var2);
      return var3;
   }

   static Logger getPlatformLogger(String var0) {
      LogManager var1 = LogManager.getLogManager();
      Logger var2 = var1.demandSystemLogger(var0, "sun.util.logging.resources.logging");
      return var2;
   }

   public static Logger getAnonymousLogger() {
      return getAnonymousLogger((String)null);
   }

   @CallerSensitive
   public static Logger getAnonymousLogger(String var0) {
      LogManager var1 = LogManager.getLogManager();
      var1.drainLoggerRefQueueBounded();
      Logger var2 = new Logger((String)null, var0, Reflection.getCallerClass(), var1, false);
      var2.anonymous = true;
      Logger var3 = var1.getLogger("");
      var2.doSetParent(var3);
      return var2;
   }

   public ResourceBundle getResourceBundle() {
      return this.findResourceBundle(this.getResourceBundleName(), true);
   }

   public String getResourceBundleName() {
      return this.loggerBundle.resourceBundleName;
   }

   public void setFilter(Filter var1) throws SecurityException {
      this.checkPermission();
      this.filter = var1;
   }

   public Filter getFilter() {
      return this.filter;
   }

   public void log(LogRecord var1) {
      if (this.isLoggable(var1.getLevel())) {
         Filter var2 = this.filter;
         if (var2 == null || var2.isLoggable(var1)) {
            for(Logger var3 = this; var3 != null; var3 = this.isSystemLogger ? var3.parent : var3.getParent()) {
               Handler[] var4 = this.isSystemLogger ? var3.accessCheckedHandlers() : var3.getHandlers();
               Handler[] var5 = var4;
               int var6 = var4.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  Handler var8 = var5[var7];
                  var8.publish(var1);
               }

               boolean var9 = this.isSystemLogger ? var3.useParentHandlers : var3.getUseParentHandlers();
               if (!var9) {
                  break;
               }
            }

         }
      }
   }

   private void doLog(LogRecord var1) {
      var1.setLoggerName(this.name);
      Logger.LoggerBundle var2 = this.getEffectiveLoggerBundle();
      ResourceBundle var3 = var2.userBundle;
      String var4 = var2.resourceBundleName;
      if (var4 != null && var3 != null) {
         var1.setResourceBundleName(var4);
         var1.setResourceBundle(var3);
      }

      this.log(var1);
   }

   public void log(Level var1, String var2) {
      if (this.isLoggable(var1)) {
         LogRecord var3 = new LogRecord(var1, var2);
         this.doLog(var3);
      }
   }

   public void log(Level var1, Supplier<String> var2) {
      if (this.isLoggable(var1)) {
         LogRecord var3 = new LogRecord(var1, (String)var2.get());
         this.doLog(var3);
      }
   }

   public void log(Level var1, String var2, Object var3) {
      if (this.isLoggable(var1)) {
         LogRecord var4 = new LogRecord(var1, var2);
         Object[] var5 = new Object[]{var3};
         var4.setParameters(var5);
         this.doLog(var4);
      }
   }

   public void log(Level var1, String var2, Object[] var3) {
      if (this.isLoggable(var1)) {
         LogRecord var4 = new LogRecord(var1, var2);
         var4.setParameters(var3);
         this.doLog(var4);
      }
   }

   public void log(Level var1, String var2, Throwable var3) {
      if (this.isLoggable(var1)) {
         LogRecord var4 = new LogRecord(var1, var2);
         var4.setThrown(var3);
         this.doLog(var4);
      }
   }

   public void log(Level var1, Throwable var2, Supplier<String> var3) {
      if (this.isLoggable(var1)) {
         LogRecord var4 = new LogRecord(var1, (String)var3.get());
         var4.setThrown(var2);
         this.doLog(var4);
      }
   }

   public void logp(Level var1, String var2, String var3, String var4) {
      if (this.isLoggable(var1)) {
         LogRecord var5 = new LogRecord(var1, var4);
         var5.setSourceClassName(var2);
         var5.setSourceMethodName(var3);
         this.doLog(var5);
      }
   }

   public void logp(Level var1, String var2, String var3, Supplier<String> var4) {
      if (this.isLoggable(var1)) {
         LogRecord var5 = new LogRecord(var1, (String)var4.get());
         var5.setSourceClassName(var2);
         var5.setSourceMethodName(var3);
         this.doLog(var5);
      }
   }

   public void logp(Level var1, String var2, String var3, String var4, Object var5) {
      if (this.isLoggable(var1)) {
         LogRecord var6 = new LogRecord(var1, var4);
         var6.setSourceClassName(var2);
         var6.setSourceMethodName(var3);
         Object[] var7 = new Object[]{var5};
         var6.setParameters(var7);
         this.doLog(var6);
      }
   }

   public void logp(Level var1, String var2, String var3, String var4, Object[] var5) {
      if (this.isLoggable(var1)) {
         LogRecord var6 = new LogRecord(var1, var4);
         var6.setSourceClassName(var2);
         var6.setSourceMethodName(var3);
         var6.setParameters(var5);
         this.doLog(var6);
      }
   }

   public void logp(Level var1, String var2, String var3, String var4, Throwable var5) {
      if (this.isLoggable(var1)) {
         LogRecord var6 = new LogRecord(var1, var4);
         var6.setSourceClassName(var2);
         var6.setSourceMethodName(var3);
         var6.setThrown(var5);
         this.doLog(var6);
      }
   }

   public void logp(Level var1, String var2, String var3, Throwable var4, Supplier<String> var5) {
      if (this.isLoggable(var1)) {
         LogRecord var6 = new LogRecord(var1, (String)var5.get());
         var6.setSourceClassName(var2);
         var6.setSourceMethodName(var3);
         var6.setThrown(var4);
         this.doLog(var6);
      }
   }

   private void doLog(LogRecord var1, String var2) {
      var1.setLoggerName(this.name);
      if (var2 != null) {
         var1.setResourceBundleName(var2);
         var1.setResourceBundle(this.findResourceBundle(var2, false));
      }

      this.log(var1);
   }

   private void doLog(LogRecord var1, ResourceBundle var2) {
      var1.setLoggerName(this.name);
      if (var2 != null) {
         var1.setResourceBundleName(var2.getBaseBundleName());
         var1.setResourceBundle(var2);
      }

      this.log(var1);
   }

   /** @deprecated */
   @Deprecated
   public void logrb(Level var1, String var2, String var3, String var4, String var5) {
      if (this.isLoggable(var1)) {
         LogRecord var6 = new LogRecord(var1, var5);
         var6.setSourceClassName(var2);
         var6.setSourceMethodName(var3);
         this.doLog(var6, var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Object var6) {
      if (this.isLoggable(var1)) {
         LogRecord var7 = new LogRecord(var1, var5);
         var7.setSourceClassName(var2);
         var7.setSourceMethodName(var3);
         Object[] var8 = new Object[]{var6};
         var7.setParameters(var8);
         this.doLog(var7, var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Object[] var6) {
      if (this.isLoggable(var1)) {
         LogRecord var7 = new LogRecord(var1, var5);
         var7.setSourceClassName(var2);
         var7.setSourceMethodName(var3);
         var7.setParameters(var6);
         this.doLog(var7, var4);
      }
   }

   public void logrb(Level var1, String var2, String var3, ResourceBundle var4, String var5, Object... var6) {
      if (this.isLoggable(var1)) {
         LogRecord var7 = new LogRecord(var1, var5);
         var7.setSourceClassName(var2);
         var7.setSourceMethodName(var3);
         if (var6 != null && var6.length != 0) {
            var7.setParameters(var6);
         }

         this.doLog(var7, var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Throwable var6) {
      if (this.isLoggable(var1)) {
         LogRecord var7 = new LogRecord(var1, var5);
         var7.setSourceClassName(var2);
         var7.setSourceMethodName(var3);
         var7.setThrown(var6);
         this.doLog(var7, var4);
      }
   }

   public void logrb(Level var1, String var2, String var3, ResourceBundle var4, String var5, Throwable var6) {
      if (this.isLoggable(var1)) {
         LogRecord var7 = new LogRecord(var1, var5);
         var7.setSourceClassName(var2);
         var7.setSourceMethodName(var3);
         var7.setThrown(var6);
         this.doLog(var7, var4);
      }
   }

   public void entering(String var1, String var2) {
      this.logp(Level.FINER, var1, var2, "ENTRY");
   }

   public void entering(String var1, String var2, Object var3) {
      this.logp(Level.FINER, var1, var2, "ENTRY {0}", var3);
   }

   public void entering(String var1, String var2, Object[] var3) {
      String var4 = "ENTRY";
      if (var3 == null) {
         this.logp(Level.FINER, var1, var2, var4);
      } else if (this.isLoggable(Level.FINER)) {
         for(int var5 = 0; var5 < var3.length; ++var5) {
            var4 = var4 + " {" + var5 + "}";
         }

         this.logp(Level.FINER, var1, var2, var4, var3);
      }
   }

   public void exiting(String var1, String var2) {
      this.logp(Level.FINER, var1, var2, "RETURN");
   }

   public void exiting(String var1, String var2, Object var3) {
      this.logp(Level.FINER, var1, var2, "RETURN {0}", var3);
   }

   public void throwing(String var1, String var2, Throwable var3) {
      if (this.isLoggable(Level.FINER)) {
         LogRecord var4 = new LogRecord(Level.FINER, "THROW");
         var4.setSourceClassName(var1);
         var4.setSourceMethodName(var2);
         var4.setThrown(var3);
         this.doLog(var4);
      }
   }

   public void severe(String var1) {
      this.log(Level.SEVERE, var1);
   }

   public void warning(String var1) {
      this.log(Level.WARNING, var1);
   }

   public void info(String var1) {
      this.log(Level.INFO, var1);
   }

   public void config(String var1) {
      this.log(Level.CONFIG, var1);
   }

   public void fine(String var1) {
      this.log(Level.FINE, var1);
   }

   public void finer(String var1) {
      this.log(Level.FINER, var1);
   }

   public void finest(String var1) {
      this.log(Level.FINEST, var1);
   }

   public void severe(Supplier<String> var1) {
      this.log(Level.SEVERE, var1);
   }

   public void warning(Supplier<String> var1) {
      this.log(Level.WARNING, var1);
   }

   public void info(Supplier<String> var1) {
      this.log(Level.INFO, var1);
   }

   public void config(Supplier<String> var1) {
      this.log(Level.CONFIG, var1);
   }

   public void fine(Supplier<String> var1) {
      this.log(Level.FINE, var1);
   }

   public void finer(Supplier<String> var1) {
      this.log(Level.FINER, var1);
   }

   public void finest(Supplier<String> var1) {
      this.log(Level.FINEST, var1);
   }

   public void setLevel(Level var1) throws SecurityException {
      this.checkPermission();
      synchronized(treeLock) {
         this.levelObject = var1;
         this.updateEffectiveLevel();
      }
   }

   final boolean isLevelInitialized() {
      return this.levelObject != null;
   }

   public Level getLevel() {
      return this.levelObject;
   }

   public boolean isLoggable(Level var1) {
      return var1.intValue() >= this.levelValue && this.levelValue != offValue;
   }

   public String getName() {
      return this.name;
   }

   public void addHandler(Handler var1) throws SecurityException {
      var1.getClass();
      this.checkPermission();
      this.handlers.add(var1);
   }

   public void removeHandler(Handler var1) throws SecurityException {
      this.checkPermission();
      if (var1 != null) {
         this.handlers.remove(var1);
      }
   }

   public Handler[] getHandlers() {
      return this.accessCheckedHandlers();
   }

   Handler[] accessCheckedHandlers() {
      return (Handler[])this.handlers.toArray(emptyHandlers);
   }

   public void setUseParentHandlers(boolean var1) {
      this.checkPermission();
      this.useParentHandlers = var1;
   }

   public boolean getUseParentHandlers() {
      return this.useParentHandlers;
   }

   private static ResourceBundle findSystemResourceBundle(final Locale var0) {
      return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
         public ResourceBundle run() {
            try {
               return ResourceBundle.getBundle("sun.util.logging.resources.logging", var0);
            } catch (MissingResourceException var2) {
               throw new InternalError(var2.toString());
            }
         }
      });
   }

   private synchronized ResourceBundle findResourceBundle(String var1, boolean var2) {
      if (var1 == null) {
         return null;
      } else {
         Locale var3 = Locale.getDefault();
         Logger.LoggerBundle var4 = this.loggerBundle;
         if (var4.userBundle != null && var1.equals(var4.resourceBundleName)) {
            return var4.userBundle;
         } else if (this.catalog != null && var3.equals(this.catalogLocale) && var1.equals(this.catalogName)) {
            return this.catalog;
         } else if (var1.equals("sun.util.logging.resources.logging")) {
            this.catalog = findSystemResourceBundle(var3);
            this.catalogName = var1;
            this.catalogLocale = var3;
            return this.catalog;
         } else {
            ClassLoader var5 = Thread.currentThread().getContextClassLoader();
            if (var5 == null) {
               var5 = ClassLoader.getSystemClassLoader();
            }

            try {
               this.catalog = ResourceBundle.getBundle(var1, var3, var5);
               this.catalogName = var1;
               this.catalogLocale = var3;
               return this.catalog;
            } catch (MissingResourceException var9) {
               if (var2) {
                  ClassLoader var6 = this.getCallersClassLoader();
                  if (var6 != null && var6 != var5) {
                     try {
                        this.catalog = ResourceBundle.getBundle(var1, var3, var6);
                        this.catalogName = var1;
                        this.catalogLocale = var3;
                        return this.catalog;
                     } catch (MissingResourceException var8) {
                        return null;
                     }
                  } else {
                     return null;
                  }
               } else {
                  return null;
               }
            }
         }
      }
   }

   private synchronized void setupResourceInfo(String var1, Class<?> var2) {
      Logger.LoggerBundle var3 = this.loggerBundle;
      if (var3.resourceBundleName != null) {
         if (!var3.resourceBundleName.equals(var1)) {
            throw new IllegalArgumentException(var3.resourceBundleName + " != " + var1);
         }
      } else if (var1 != null) {
         this.setCallersClassLoaderRef(var2);
         if (this.isSystemLogger && this.getCallersClassLoader() != null) {
            this.checkPermission();
         }

         if (this.findResourceBundle(var1, true) == null) {
            this.callersClassLoaderRef = null;
            throw new MissingResourceException("Can't find " + var1 + " bundle", var1, "");
         } else {
            assert var3.userBundle == null;

            this.loggerBundle = Logger.LoggerBundle.get(var1, (ResourceBundle)null);
         }
      }
   }

   public void setResourceBundle(ResourceBundle var1) {
      this.checkPermission();
      String var2 = var1.getBaseBundleName();
      if (var2 != null && !var2.isEmpty()) {
         synchronized(this) {
            Logger.LoggerBundle var4 = this.loggerBundle;
            boolean var5 = var4.resourceBundleName == null || var4.resourceBundleName.equals(var2);
            if (!var5) {
               throw new IllegalArgumentException("can't replace resource bundle");
            } else {
               this.loggerBundle = Logger.LoggerBundle.get(var2, var1);
            }
         }
      } else {
         throw new IllegalArgumentException("resource bundle must have a name");
      }
   }

   public Logger getParent() {
      return this.parent;
   }

   public void setParent(Logger var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.manager == null) {
            this.manager = LogManager.getLogManager();
         }

         this.manager.checkPermission();
         this.doSetParent(var1);
      }
   }

   private void doSetParent(Logger var1) {
      synchronized(treeLock) {
         LogManager.LoggerWeakRef var3 = null;
         if (this.parent != null) {
            for(Iterator var4 = this.parent.kids.iterator(); var4.hasNext(); var3 = null) {
               var3 = (LogManager.LoggerWeakRef)var4.next();
               Logger var5 = (Logger)var3.get();
               if (var5 == this) {
                  var4.remove();
                  break;
               }
            }
         }

         this.parent = var1;
         if (this.parent.kids == null) {
            this.parent.kids = new ArrayList(2);
         }

         if (var3 == null) {
            var3 = this.manager.new LoggerWeakRef(this);
         }

         var3.setParentRef(new WeakReference(this.parent));
         this.parent.kids.add(var3);
         this.updateEffectiveLevel();
      }
   }

   final void removeChildLogger(LogManager.LoggerWeakRef var1) {
      synchronized(treeLock) {
         Iterator var3 = this.kids.iterator();

         LogManager.LoggerWeakRef var4;
         do {
            if (!var3.hasNext()) {
               return;
            }

            var4 = (LogManager.LoggerWeakRef)var3.next();
         } while(var4 != var1);

         var3.remove();
      }
   }

   private void updateEffectiveLevel() {
      int var1;
      if (this.levelObject != null) {
         var1 = this.levelObject.intValue();
      } else if (this.parent != null) {
         var1 = this.parent.levelValue;
      } else {
         var1 = Level.INFO.intValue();
      }

      if (this.levelValue != var1) {
         this.levelValue = var1;
         if (this.kids != null) {
            for(int var2 = 0; var2 < this.kids.size(); ++var2) {
               LogManager.LoggerWeakRef var3 = (LogManager.LoggerWeakRef)this.kids.get(var2);
               Logger var4 = (Logger)var3.get();
               if (var4 != null) {
                  var4.updateEffectiveLevel();
               }
            }
         }

      }
   }

   private Logger.LoggerBundle getEffectiveLoggerBundle() {
      Logger.LoggerBundle var1 = this.loggerBundle;
      if (var1.isSystemBundle()) {
         return SYSTEM_BUNDLE;
      } else {
         ResourceBundle var2 = this.getResourceBundle();
         if (var2 != null && var2 == var1.userBundle) {
            return var1;
         } else if (var2 != null) {
            String var6 = this.getResourceBundleName();
            return Logger.LoggerBundle.get(var6, var2);
         } else {
            for(Logger var3 = this.parent; var3 != null; var3 = this.isSystemLogger ? var3.parent : var3.getParent()) {
               Logger.LoggerBundle var4 = var3.loggerBundle;
               if (var4.isSystemBundle()) {
                  return SYSTEM_BUNDLE;
               }

               if (var4.userBundle != null) {
                  return var4;
               }

               String var5 = this.isSystemLogger ? (var3.isSystemLogger ? var4.resourceBundleName : null) : var3.getResourceBundleName();
               if (var5 != null) {
                  return Logger.LoggerBundle.get(var5, this.findResourceBundle(var5, true));
               }
            }

            return NO_RESOURCE_BUNDLE;
         }
      }
   }

   static {
      offValue = Level.OFF.intValue();
      SYSTEM_BUNDLE = new Logger.LoggerBundle("sun.util.logging.resources.logging", (ResourceBundle)null);
      NO_RESOURCE_BUNDLE = new Logger.LoggerBundle((String)null, (ResourceBundle)null);
      treeLock = new Object();
      global = new Logger("global");
   }

   private static class SystemLoggerHelper {
      static boolean disableCallerCheck = getBooleanProperty("sun.util.logging.disableCallerCheck");

      private static boolean getBooleanProperty(final String var0) {
         String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty(var0);
            }
         });
         return Boolean.valueOf(var1);
      }
   }

   private static final class LoggerBundle {
      final String resourceBundleName;
      final ResourceBundle userBundle;

      private LoggerBundle(String var1, ResourceBundle var2) {
         this.resourceBundleName = var1;
         this.userBundle = var2;
      }

      boolean isSystemBundle() {
         return "sun.util.logging.resources.logging".equals(this.resourceBundleName);
      }

      static Logger.LoggerBundle get(String var0, ResourceBundle var1) {
         if (var0 == null && var1 == null) {
            return Logger.NO_RESOURCE_BUNDLE;
         } else {
            return "sun.util.logging.resources.logging".equals(var0) && var1 == null ? Logger.SYSTEM_BUNDLE : new Logger.LoggerBundle(var0, var1);
         }
      }

      // $FF: synthetic method
      LoggerBundle(String var1, ResourceBundle var2, Object var3) {
         this(var1, var2);
      }
   }
}
