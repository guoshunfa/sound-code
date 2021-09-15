package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import sun.util.ResourceBundleEnumeration;

public class PropertyResourceBundle extends ResourceBundle {
   private Map<String, Object> lookup;

   public PropertyResourceBundle(InputStream var1) throws IOException {
      Properties var2 = new Properties();
      var2.load(var1);
      this.lookup = new HashMap(var2);
   }

   public PropertyResourceBundle(Reader var1) throws IOException {
      Properties var2 = new Properties();
      var2.load(var1);
      this.lookup = new HashMap(var2);
   }

   public Object handleGetObject(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.lookup.get(var1);
      }
   }

   public Enumeration<String> getKeys() {
      ResourceBundle var1 = this.parent;
      return new ResourceBundleEnumeration(this.lookup.keySet(), var1 != null ? var1.getKeys() : null);
   }

   protected Set<String> handleKeySet() {
      return this.lookup.keySet();
   }
}
