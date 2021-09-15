package java.util;

import sun.util.ResourceBundleEnumeration;

public abstract class ListResourceBundle extends ResourceBundle {
   private Map<String, Object> lookup = null;

   public final Object handleGetObject(String var1) {
      if (this.lookup == null) {
         this.loadLookup();
      }

      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.lookup.get(var1);
      }
   }

   public Enumeration<String> getKeys() {
      if (this.lookup == null) {
         this.loadLookup();
      }

      ResourceBundle var1 = this.parent;
      return new ResourceBundleEnumeration(this.lookup.keySet(), var1 != null ? var1.getKeys() : null);
   }

   protected Set<String> handleKeySet() {
      if (this.lookup == null) {
         this.loadLookup();
      }

      return this.lookup.keySet();
   }

   protected abstract Object[][] getContents();

   private synchronized void loadLookup() {
      if (this.lookup == null) {
         Object[][] var1 = this.getContents();
         HashMap var2 = new HashMap(var1.length);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            String var4 = (String)var1[var3][0];
            Object var5 = var1[var3][1];
            if (var4 == null || var5 == null) {
               throw new NullPointerException();
            }

            var2.put(var4, var5);
         }

         this.lookup = var2;
      }
   }
}
