package javax.accessibility;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class AccessibleBundle {
   private static Hashtable table = new Hashtable();
   private final String defaultResourceBundleName = "com.sun.accessibility.internal.resources.accessibility";
   protected String key = null;

   protected String toDisplayString(String var1, Locale var2) {
      this.loadResourceBundle(var1, var2);
      Object var3 = table.get(var2);
      if (var3 != null && var3 instanceof Hashtable) {
         Hashtable var4 = (Hashtable)var3;
         var3 = var4.get(this.key);
         if (var3 != null && var3 instanceof String) {
            return (String)var3;
         }
      }

      return this.key;
   }

   public String toDisplayString(Locale var1) {
      return this.toDisplayString("com.sun.accessibility.internal.resources.accessibility", var1);
   }

   public String toDisplayString() {
      return this.toDisplayString(Locale.getDefault());
   }

   public String toString() {
      return this.toDisplayString();
   }

   private void loadResourceBundle(String var1, Locale var2) {
      if (!table.contains(var2)) {
         try {
            Hashtable var3 = new Hashtable();
            ResourceBundle var4 = ResourceBundle.getBundle(var1, var2);
            Enumeration var5 = var4.getKeys();

            while(var5.hasMoreElements()) {
               String var6 = (String)var5.nextElement();
               var3.put(var6, var4.getObject(var6));
            }

            table.put(var2, var3);
         } catch (MissingResourceException var7) {
            System.err.println("loadResourceBundle: " + var7);
            return;
         }
      }

   }
}
