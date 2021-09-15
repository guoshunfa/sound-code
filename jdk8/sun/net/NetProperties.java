package sun.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public class NetProperties {
   private static Properties props = new Properties();

   private NetProperties() {
   }

   private static void loadDefaultProperties() {
      String var0 = System.getProperty("java.home");
      if (var0 == null) {
         throw new Error("Can't find java.home ??");
      } else {
         try {
            File var1 = new File(var0, "lib");
            var1 = new File(var1, "net.properties");
            var0 = var1.getCanonicalPath();
            FileInputStream var2 = new FileInputStream(var0);
            BufferedInputStream var3 = new BufferedInputStream(var2);
            props.load((InputStream)var3);
            var3.close();
         } catch (Exception var4) {
         }

      }
   }

   public static String get(String var0) {
      String var1 = props.getProperty(var0);

      try {
         return System.getProperty(var0, var1);
      } catch (IllegalArgumentException var3) {
      } catch (NullPointerException var4) {
      }

      return null;
   }

   public static Integer getInteger(String var0, int var1) {
      String var2 = null;

      try {
         var2 = System.getProperty(var0, props.getProperty(var0));
      } catch (IllegalArgumentException var4) {
      } catch (NullPointerException var5) {
      }

      if (var2 != null) {
         try {
            return Integer.decode(var2);
         } catch (NumberFormatException var6) {
         }
      }

      return new Integer(var1);
   }

   public static Boolean getBoolean(String var0) {
      String var1 = null;

      try {
         var1 = System.getProperty(var0, props.getProperty(var0));
      } catch (IllegalArgumentException var3) {
      } catch (NullPointerException var4) {
      }

      if (var1 != null) {
         try {
            return Boolean.valueOf(var1);
         } catch (NumberFormatException var5) {
         }
      }

      return null;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            NetProperties.loadDefaultProperties();
            return null;
         }
      });
   }
}
