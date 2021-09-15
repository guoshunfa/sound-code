package sun.util.resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.ResourceBundleEnumeration;

public abstract class OpenListResourceBundle extends ResourceBundle {
   private volatile Map<String, Object> lookup = null;
   private volatile Set<String> keyset;

   protected OpenListResourceBundle() {
   }

   protected Object handleGetObject(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.loadLookupTablesIfNecessary();
         return this.lookup.get(var1);
      }
   }

   public Enumeration<String> getKeys() {
      ResourceBundle var1 = this.parent;
      return new ResourceBundleEnumeration(this.handleKeySet(), var1 != null ? var1.getKeys() : null);
   }

   protected Set<String> handleKeySet() {
      this.loadLookupTablesIfNecessary();
      return this.lookup.keySet();
   }

   public Set<String> keySet() {
      if (this.keyset != null) {
         return this.keyset;
      } else {
         Set var1 = this.createSet();
         var1.addAll(this.handleKeySet());
         if (this.parent != null) {
            var1.addAll(this.parent.keySet());
         }

         synchronized(this) {
            if (this.keyset == null) {
               this.keyset = var1;
            }
         }

         return this.keyset;
      }
   }

   protected abstract Object[][] getContents();

   void loadLookupTablesIfNecessary() {
      if (this.lookup == null) {
         this.loadLookup();
      }

   }

   private void loadLookup() {
      Object[][] var1 = this.getContents();
      Map var2 = this.createMap(var1.length);

      for(int var3 = 0; var3 < var1.length; ++var3) {
         String var4 = (String)var1[var3][0];
         Object var5 = var1[var3][1];
         if (var4 == null || var5 == null) {
            throw new NullPointerException();
         }

         var2.put(var4, var5);
      }

      synchronized(this) {
         if (this.lookup == null) {
            this.lookup = var2;
         }

      }
   }

   protected <K, V> Map<K, V> createMap(int var1) {
      return new HashMap(var1);
   }

   protected <E> Set<E> createSet() {
      return new HashSet();
   }
}
