package javax.management.openmbean;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class CompositeType extends OpenType<CompositeData> {
   static final long serialVersionUID = -5366242454346948798L;
   private TreeMap<String, String> nameToDescription;
   private TreeMap<String, OpenType<?>> nameToType;
   private transient Integer myHashCode = null;
   private transient String myToString = null;
   private transient Set<String> myNamesSet = null;

   public CompositeType(String var1, String var2, String[] var3, String[] var4, OpenType<?>[] var5) throws OpenDataException {
      super(CompositeData.class.getName(), var1, var2, false);
      checkForNullElement(var3, "itemNames");
      checkForNullElement(var4, "itemDescriptions");
      checkForNullElement(var5, "itemTypes");
      checkForEmptyString(var3, "itemNames");
      checkForEmptyString(var4, "itemDescriptions");
      if (var3.length == var4.length && var3.length == var5.length) {
         this.nameToDescription = new TreeMap();
         this.nameToType = new TreeMap();

         for(int var7 = 0; var7 < var3.length; ++var7) {
            String var6 = var3[var7].trim();
            if (this.nameToDescription.containsKey(var6)) {
               throw new OpenDataException("Argument's element itemNames[" + var7 + "]=\"" + var3[var7] + "\" duplicates a previous item names.");
            }

            this.nameToDescription.put(var6, var4[var7].trim());
            this.nameToType.put(var6, var5[var7]);
         }

      } else {
         throw new IllegalArgumentException("Array arguments itemNames[], itemDescriptions[] and itemTypes[] should be of same length (got " + var3.length + ", " + var4.length + " and " + var5.length + ").");
      }
   }

   private static void checkForNullElement(Object[] var0, String var1) {
      if (var0 != null && var0.length != 0) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var0[var2] == null) {
               throw new IllegalArgumentException("Argument's element " + var1 + "[" + var2 + "] cannot be null.");
            }
         }

      } else {
         throw new IllegalArgumentException("Argument " + var1 + "[] cannot be null or empty.");
      }
   }

   private static void checkForEmptyString(String[] var0, String var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2].trim().equals("")) {
            throw new IllegalArgumentException("Argument's element " + var1 + "[" + var2 + "] cannot be an empty string.");
         }
      }

   }

   public boolean containsKey(String var1) {
      return var1 == null ? false : this.nameToDescription.containsKey(var1);
   }

   public String getDescription(String var1) {
      return var1 == null ? null : (String)this.nameToDescription.get(var1);
   }

   public OpenType<?> getType(String var1) {
      return var1 == null ? null : (OpenType)this.nameToType.get(var1);
   }

   public Set<String> keySet() {
      if (this.myNamesSet == null) {
         this.myNamesSet = Collections.unmodifiableSet(this.nameToDescription.keySet());
      }

      return this.myNamesSet;
   }

   public boolean isValue(Object var1) {
      if (!(var1 instanceof CompositeData)) {
         return false;
      } else {
         CompositeData var2 = (CompositeData)var1;
         CompositeType var3 = var2.getCompositeType();
         return this.isAssignableFrom(var3);
      }
   }

   boolean isAssignableFrom(OpenType<?> var1) {
      if (!(var1 instanceof CompositeType)) {
         return false;
      } else {
         CompositeType var2 = (CompositeType)var1;
         if (!var2.getTypeName().equals(this.getTypeName())) {
            return false;
         } else {
            Iterator var3 = this.keySet().iterator();

            OpenType var5;
            OpenType var6;
            do {
               if (!var3.hasNext()) {
                  return true;
               }

               String var4 = (String)var3.next();
               var5 = var2.getType(var4);
               var6 = this.getType(var4);
            } while(var5 != null && var6.isAssignableFrom(var5));

            return false;
         }
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         CompositeType var2;
         try {
            var2 = (CompositeType)var1;
         } catch (ClassCastException var4) {
            return false;
         }

         if (!this.getTypeName().equals(var2.getTypeName())) {
            return false;
         } else {
            return this.nameToType.equals(var2.nameToType);
         }
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         byte var1 = 0;
         int var4 = var1 + this.getTypeName().hashCode();

         String var3;
         for(Iterator var2 = this.nameToDescription.keySet().iterator(); var2.hasNext(); var4 += ((OpenType)this.nameToType.get(var3)).hashCode()) {
            var3 = (String)var2.next();
            var4 += var3.hashCode();
         }

         this.myHashCode = var4;
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.getClass().getName());
         var1.append("(name=");
         var1.append(this.getTypeName());
         var1.append(",items=(");
         int var2 = 0;

         for(Iterator var3 = this.nameToType.keySet().iterator(); var3.hasNext(); ++var2) {
            String var4 = (String)var3.next();
            if (var2 > 0) {
               var1.append(",");
            }

            var1.append("(itemName=");
            var1.append(var4);
            var1.append(",itemType=");
            var1.append(((OpenType)this.nameToType.get(var4)).toString() + ")");
         }

         var1.append("))");
         this.myToString = var1.toString();
      }

      return this.myToString;
   }
}
