package javax.management.openmbean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class CompositeDataSupport implements CompositeData, Serializable {
   static final long serialVersionUID = 8003518976613702244L;
   private final SortedMap<String, Object> contents;
   private final CompositeType compositeType;

   public CompositeDataSupport(CompositeType var1, String[] var2, Object[] var3) throws OpenDataException {
      this(makeMap(var2, var3), var1);
   }

   private static SortedMap<String, Object> makeMap(String[] var0, Object[] var1) throws OpenDataException {
      if (var0 != null && var1 != null) {
         if (var0.length != 0 && var1.length != 0) {
            if (var0.length != var1.length) {
               throw new IllegalArgumentException("Different lengths: itemNames[" + var0.length + "], itemValues[" + var1.length + "]");
            } else {
               TreeMap var2 = new TreeMap();

               for(int var3 = 0; var3 < var0.length; ++var3) {
                  String var4 = var0[var3];
                  if (var4 == null || var4.equals("")) {
                     throw new IllegalArgumentException("Null or empty item name");
                  }

                  if (var2.containsKey(var4)) {
                     throw new OpenDataException("Duplicate item name " + var4);
                  }

                  var2.put(var0[var3], var1[var3]);
               }

               return var2;
            }
         } else {
            throw new IllegalArgumentException("Empty itemNames or itemValues");
         }
      } else {
         throw new IllegalArgumentException("Null itemNames or itemValues");
      }
   }

   public CompositeDataSupport(CompositeType var1, Map<String, ?> var2) throws OpenDataException {
      this(makeMap(var2), var1);
   }

   private static SortedMap<String, Object> makeMap(Map<String, ?> var0) {
      if (var0 != null && !var0.isEmpty()) {
         TreeMap var1 = new TreeMap();
         Iterator var2 = var0.keySet().iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            if (var3 == null || var3.equals("")) {
               throw new IllegalArgumentException("Null or empty item name");
            }

            if (!(var3 instanceof String)) {
               throw new ArrayStoreException("Item name is not string: " + var3);
            }

            var1.put((String)var3, var0.get(var3));
         }

         return var1;
      } else {
         throw new IllegalArgumentException("Null or empty items map");
      }
   }

   private CompositeDataSupport(SortedMap<String, Object> var1, CompositeType var2) throws OpenDataException {
      if (var2 == null) {
         throw new IllegalArgumentException("Argument compositeType cannot be null.");
      } else {
         Set var3 = var2.keySet();
         Set var4 = var1.keySet();
         if (!var3.equals(var4)) {
            TreeSet var5 = new TreeSet(var3);
            var5.removeAll(var4);
            TreeSet var6 = new TreeSet(var4);
            var6.removeAll(var3);
            if (!var5.isEmpty() || !var6.isEmpty()) {
               throw new OpenDataException("Item names do not match CompositeType: names in items but not in CompositeType: " + var6 + "; names in CompositeType but not in items: " + var5);
            }
         }

         Iterator var9 = var3.iterator();

         while(var9.hasNext()) {
            String var10 = (String)var9.next();
            Object var7 = var1.get(var10);
            if (var7 != null) {
               OpenType var8 = var2.getType(var10);
               if (!var8.isValue(var7)) {
                  throw new OpenDataException("Argument value of wrong type for item " + var10 + ": value " + var7 + ", type " + var8);
               }
            }
         }

         this.compositeType = var2;
         this.contents = var1;
      }
   }

   public CompositeType getCompositeType() {
      return this.compositeType;
   }

   public Object get(String var1) {
      if (var1 != null && !var1.trim().equals("")) {
         if (!this.contents.containsKey(var1.trim())) {
            throw new InvalidKeyException("Argument key=\"" + var1.trim() + "\" is not an existing item name for this CompositeData instance.");
         } else {
            return this.contents.get(var1.trim());
         }
      } else {
         throw new IllegalArgumentException("Argument key cannot be a null or empty String.");
      }
   }

   public Object[] getAll(String[] var1) {
      if (var1 != null && var1.length != 0) {
         Object[] var2 = new Object[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = this.get(var1[var3]);
         }

         return var2;
      } else {
         return new Object[0];
      }
   }

   public boolean containsKey(String var1) {
      return var1 != null && !var1.trim().equals("") ? this.contents.containsKey(var1) : false;
   }

   public boolean containsValue(Object var1) {
      return this.contents.containsValue(var1);
   }

   public Collection<?> values() {
      return Collections.unmodifiableCollection(this.contents.values());
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CompositeData)) {
         return false;
      } else {
         CompositeData var2 = (CompositeData)var1;
         if (!this.getCompositeType().equals(var2.getCompositeType())) {
            return false;
         } else if (this.contents.size() != var2.values().size()) {
            return false;
         } else {
            Iterator var3 = this.contents.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               Object var5 = var4.getValue();
               Object var6 = var2.get((String)var4.getKey());
               if (var5 != var6) {
                  if (var5 == null) {
                     return false;
                  }

                  boolean var7 = var5.getClass().isArray() ? Arrays.deepEquals(new Object[]{var5}, new Object[]{var6}) : var5.equals(var6);
                  if (!var7) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int var1 = this.compositeType.hashCode();
      Iterator var2 = this.contents.values().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (var3 instanceof Object[]) {
            var1 += Arrays.deepHashCode((Object[])((Object[])var3));
         } else if (var3 instanceof byte[]) {
            var1 += Arrays.hashCode((byte[])((byte[])var3));
         } else if (var3 instanceof short[]) {
            var1 += Arrays.hashCode((short[])((short[])var3));
         } else if (var3 instanceof int[]) {
            var1 += Arrays.hashCode((int[])((int[])var3));
         } else if (var3 instanceof long[]) {
            var1 += Arrays.hashCode((long[])((long[])var3));
         } else if (var3 instanceof char[]) {
            var1 += Arrays.hashCode((char[])((char[])var3));
         } else if (var3 instanceof float[]) {
            var1 += Arrays.hashCode((float[])((float[])var3));
         } else if (var3 instanceof double[]) {
            var1 += Arrays.hashCode((double[])((double[])var3));
         } else if (var3 instanceof boolean[]) {
            var1 += Arrays.hashCode((boolean[])((boolean[])var3));
         } else if (var3 != null) {
            var1 += var3.hashCode();
         }
      }

      return var1;
   }

   public String toString() {
      return this.getClass().getName() + "(compositeType=" + this.compositeType.toString() + ",contents=" + this.contentString() + ")";
   }

   private String contentString() {
      StringBuilder var1 = new StringBuilder("{");
      String var2 = "";

      for(Iterator var3 = this.contents.entrySet().iterator(); var3.hasNext(); var2 = ", ") {
         Map.Entry var4 = (Map.Entry)var3.next();
         var1.append(var2).append((String)var4.getKey()).append("=");
         String var5 = Arrays.deepToString(new Object[]{var4.getValue()});
         var1.append(var5.substring(1, var5.length() - 1));
      }

      var1.append("}");
      return var1.toString();
   }
}
