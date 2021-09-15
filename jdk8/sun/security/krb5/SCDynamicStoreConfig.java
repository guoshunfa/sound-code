package sun.security.krb5;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import sun.security.krb5.internal.Krb5;

public class SCDynamicStoreConfig {
   private static boolean DEBUG;

   private static native void installNotificationCallback();

   private static native Hashtable<String, Object> getKerberosConfig();

   private static Vector<String> unwrapHost(Collection<Hashtable<String, String>> var0) {
      Vector var1 = new Vector();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Hashtable var3 = (Hashtable)var2.next();
         var1.add(var3.get("host"));
      }

      return var1;
   }

   private static Hashtable<String, Object> convertRealmConfigs(Hashtable<String, ?> var0) {
      Hashtable var1 = new Hashtable();

      String var3;
      Hashtable var5;
      for(Iterator var2 = var0.keySet().iterator(); var2.hasNext(); var1.put(var3, var5)) {
         var3 = (String)var2.next();
         Hashtable var4 = (Hashtable)var0.get(var3);
         var5 = new Hashtable();
         Collection var6 = (Collection)var4.get("kdc");
         if (var6 != null) {
            var5.put("kdc", unwrapHost(var6));
         }

         Collection var7 = (Collection)var4.get("kadmin");
         if (var7 != null) {
            var5.put("admin_server", unwrapHost(var7));
         }
      }

      return var1;
   }

   public static Hashtable<String, Object> getConfig() throws IOException {
      Hashtable var0 = getKerberosConfig();
      if (var0 == null) {
         throw new IOException("Could not load configuration from SCDynamicStore");
      } else {
         if (DEBUG) {
            System.out.println("Raw map from JNI: " + var0);
         }

         return convertNativeConfig(var0);
      }
   }

   private static Hashtable<String, Object> convertNativeConfig(Hashtable<String, Object> var0) {
      Hashtable var1 = (Hashtable)var0.get("realms");
      if (var1 != null) {
         var0.remove("realms");
         Hashtable var2 = convertRealmConfigs(var1);
         var0.put("realms", var2);
      }

      WrapAllStringInVector(var0);
      if (DEBUG) {
         System.out.println("stanzaTable : " + var0);
      }

      return var0;
   }

   private static void WrapAllStringInVector(Hashtable<String, Object> var0) {
      Iterator var1 = var0.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         Object var3 = var0.get(var2);
         if (var3 instanceof Hashtable) {
            WrapAllStringInVector((Hashtable)var3);
         } else if (var3 instanceof String) {
            Vector var4 = new Vector();
            var4.add((String)var3);
            var0.put(var2, var4);
         }
      }

   }

   static {
      DEBUG = Krb5.DEBUG;
      boolean var0 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String var1 = System.getProperty("os.name");
            if (var1.contains("OS X")) {
               System.loadLibrary("osx");
               return true;
            } else {
               return false;
            }
         }
      });
      if (var0) {
         installNotificationCallback();
      }

   }
}
