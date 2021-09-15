package java.util.prefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public abstract class Preferences {
   private static final PreferencesFactory factory = factory();
   public static final int MAX_KEY_LENGTH = 80;
   public static final int MAX_VALUE_LENGTH = 8192;
   public static final int MAX_NAME_LENGTH = 80;
   private static Permission prefsPerm = new RuntimePermission("preferences");

   private static PreferencesFactory factory() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty("java.util.prefs.PreferencesFactory");
         }
      });
      if (var0 != null) {
         try {
            return (PreferencesFactory)Class.forName(var0, false, ClassLoader.getSystemClassLoader()).newInstance();
         } catch (Exception var4) {
            try {
               SecurityManager var2 = System.getSecurityManager();
               if (var2 != null) {
                  var2.checkPermission(new AllPermission());
               }

               return (PreferencesFactory)Class.forName(var0, false, Thread.currentThread().getContextClassLoader()).newInstance();
            } catch (Exception var3) {
               throw new InternalError("Can't instantiate Preferences factory " + var0, var3);
            }
         }
      } else {
         return (PreferencesFactory)AccessController.doPrivileged(new PrivilegedAction<PreferencesFactory>() {
            public PreferencesFactory run() {
               return Preferences.factory1();
            }
         });
      }
   }

   private static PreferencesFactory factory1() {
      Iterator var0 = ServiceLoader.load(PreferencesFactory.class, ClassLoader.getSystemClassLoader()).iterator();

      while(true) {
         if (var0.hasNext()) {
            try {
               return (PreferencesFactory)var0.next();
            } catch (ServiceConfigurationError var5) {
               if (var5.getCause() instanceof SecurityException) {
                  continue;
               }

               throw var5;
            }
         }

         String var1 = System.getProperty("os.name");
         String var2;
         if (var1.startsWith("Windows")) {
            var2 = "java.util.prefs.WindowsPreferencesFactory";
         } else if (var1.contains("OS X")) {
            var2 = "java.util.prefs.MacOSXPreferencesFactory";
         } else {
            var2 = "java.util.prefs.FileSystemPreferencesFactory";
         }

         try {
            return (PreferencesFactory)Class.forName(var2, false, Preferences.class.getClassLoader()).newInstance();
         } catch (Exception var4) {
            throw new InternalError("Can't instantiate platform default Preferences factory " + var2, var4);
         }
      }
   }

   public static Preferences userNodeForPackage(Class<?> var0) {
      return userRoot().node(nodeName(var0));
   }

   public static Preferences systemNodeForPackage(Class<?> var0) {
      return systemRoot().node(nodeName(var0));
   }

   private static String nodeName(Class<?> var0) {
      if (var0.isArray()) {
         throw new IllegalArgumentException("Arrays have no associated preferences node.");
      } else {
         String var1 = var0.getName();
         int var2 = var1.lastIndexOf(46);
         if (var2 < 0) {
            return "/<unnamed>";
         } else {
            String var3 = var1.substring(0, var2);
            return "/" + var3.replace('.', '/');
         }
      }
   }

   public static Preferences userRoot() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(prefsPerm);
      }

      return factory.userRoot();
   }

   public static Preferences systemRoot() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(prefsPerm);
      }

      return factory.systemRoot();
   }

   protected Preferences() {
   }

   public abstract void put(String var1, String var2);

   public abstract String get(String var1, String var2);

   public abstract void remove(String var1);

   public abstract void clear() throws BackingStoreException;

   public abstract void putInt(String var1, int var2);

   public abstract int getInt(String var1, int var2);

   public abstract void putLong(String var1, long var2);

   public abstract long getLong(String var1, long var2);

   public abstract void putBoolean(String var1, boolean var2);

   public abstract boolean getBoolean(String var1, boolean var2);

   public abstract void putFloat(String var1, float var2);

   public abstract float getFloat(String var1, float var2);

   public abstract void putDouble(String var1, double var2);

   public abstract double getDouble(String var1, double var2);

   public abstract void putByteArray(String var1, byte[] var2);

   public abstract byte[] getByteArray(String var1, byte[] var2);

   public abstract String[] keys() throws BackingStoreException;

   public abstract String[] childrenNames() throws BackingStoreException;

   public abstract Preferences parent();

   public abstract Preferences node(String var1);

   public abstract boolean nodeExists(String var1) throws BackingStoreException;

   public abstract void removeNode() throws BackingStoreException;

   public abstract String name();

   public abstract String absolutePath();

   public abstract boolean isUserNode();

   public abstract String toString();

   public abstract void flush() throws BackingStoreException;

   public abstract void sync() throws BackingStoreException;

   public abstract void addPreferenceChangeListener(PreferenceChangeListener var1);

   public abstract void removePreferenceChangeListener(PreferenceChangeListener var1);

   public abstract void addNodeChangeListener(NodeChangeListener var1);

   public abstract void removeNodeChangeListener(NodeChangeListener var1);

   public abstract void exportNode(OutputStream var1) throws IOException, BackingStoreException;

   public abstract void exportSubtree(OutputStream var1) throws IOException, BackingStoreException;

   public static void importPreferences(InputStream var0) throws IOException, InvalidPreferencesFormatException {
      XmlSupport.importPreferences(var0);
   }
}
