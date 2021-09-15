package javax.management.openmbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TabularType extends OpenType<TabularData> {
   static final long serialVersionUID = 6554071860220659261L;
   private CompositeType rowType;
   private List<String> indexNames;
   private transient Integer myHashCode = null;
   private transient String myToString = null;

   public TabularType(String var1, String var2, CompositeType var3, String[] var4) throws OpenDataException {
      super(TabularData.class.getName(), var1, var2, false);
      if (var3 == null) {
         throw new IllegalArgumentException("Argument rowType cannot be null.");
      } else {
         checkForNullElement(var4, "indexNames");
         checkForEmptyString(var4, "indexNames");

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (!var3.containsKey(var4[var5])) {
               throw new OpenDataException("Argument's element value indexNames[" + var5 + "]=\"" + var4[var5] + "\" is not a valid item name for rowType.");
            }
         }

         this.rowType = var3;
         ArrayList var7 = new ArrayList(var4.length + 1);

         for(int var6 = 0; var6 < var4.length; ++var6) {
            var7.add(var4[var6]);
         }

         this.indexNames = Collections.unmodifiableList(var7);
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

   public CompositeType getRowType() {
      return this.rowType;
   }

   public List<String> getIndexNames() {
      return this.indexNames;
   }

   public boolean isValue(Object var1) {
      if (!(var1 instanceof TabularData)) {
         return false;
      } else {
         TabularData var2 = (TabularData)var1;
         TabularType var3 = var2.getTabularType();
         return this.isAssignableFrom(var3);
      }
   }

   boolean isAssignableFrom(OpenType<?> var1) {
      if (!(var1 instanceof TabularType)) {
         return false;
      } else {
         TabularType var2 = (TabularType)var1;
         return this.getTypeName().equals(var2.getTypeName()) && this.getIndexNames().equals(var2.getIndexNames()) ? this.getRowType().isAssignableFrom(var2.getRowType()) : false;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         TabularType var2;
         try {
            var2 = (TabularType)var1;
         } catch (ClassCastException var4) {
            return false;
         }

         if (!this.getTypeName().equals(var2.getTypeName())) {
            return false;
         } else if (!this.rowType.equals(var2.rowType)) {
            return false;
         } else {
            return this.indexNames.equals(var2.indexNames);
         }
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         byte var1 = 0;
         int var4 = var1 + this.getTypeName().hashCode();
         var4 += this.rowType.hashCode();

         String var3;
         for(Iterator var2 = this.indexNames.iterator(); var2.hasNext(); var4 += var3.hashCode()) {
            var3 = (String)var2.next();
         }

         this.myHashCode = var4;
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         StringBuilder var1 = (new StringBuilder()).append(this.getClass().getName()).append("(name=").append(this.getTypeName()).append(",rowType=").append(this.rowType.toString()).append(",indexNames=(");
         String var2 = "";

         for(Iterator var3 = this.indexNames.iterator(); var3.hasNext(); var2 = ",") {
            String var4 = (String)var3.next();
            var1.append(var2).append(var4);
         }

         var1.append("))");
         this.myToString = var1.toString();
      }

      return this.myToString;
   }
}
