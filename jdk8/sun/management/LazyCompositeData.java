package sun.management;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularType;

public abstract class LazyCompositeData implements CompositeData, Serializable {
   private CompositeData compositeData;
   private static final long serialVersionUID = -2190411934472666714L;

   public boolean containsKey(String var1) {
      return this.compositeData().containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      return this.compositeData().containsValue(var1);
   }

   public boolean equals(Object var1) {
      return this.compositeData().equals(var1);
   }

   public Object get(String var1) {
      return this.compositeData().get(var1);
   }

   public Object[] getAll(String[] var1) {
      return this.compositeData().getAll(var1);
   }

   public CompositeType getCompositeType() {
      return this.compositeData().getCompositeType();
   }

   public int hashCode() {
      return this.compositeData().hashCode();
   }

   public String toString() {
      return this.compositeData().toString();
   }

   public Collection<?> values() {
      return this.compositeData().values();
   }

   private synchronized CompositeData compositeData() {
      if (this.compositeData != null) {
         return this.compositeData;
      } else {
         this.compositeData = this.getCompositeData();
         return this.compositeData;
      }
   }

   protected Object writeReplace() throws ObjectStreamException {
      return this.compositeData();
   }

   protected abstract CompositeData getCompositeData();

   static String getString(CompositeData var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null CompositeData");
      } else {
         return (String)var0.get(var1);
      }
   }

   static boolean getBoolean(CompositeData var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null CompositeData");
      } else {
         return (Boolean)var0.get(var1);
      }
   }

   static long getLong(CompositeData var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null CompositeData");
      } else {
         return (Long)var0.get(var1);
      }
   }

   static int getInt(CompositeData var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null CompositeData");
      } else {
         return (Integer)var0.get(var1);
      }
   }

   protected static boolean isTypeMatched(CompositeType var0, CompositeType var1) {
      if (var0 == var1) {
         return true;
      } else {
         Set var2 = var0.keySet();
         return !var1.keySet().containsAll(var2) ? false : var2.stream().allMatch((var2x) -> {
            return isTypeMatched(var0.getType(var2x), var1.getType(var2x));
         });
      }
   }

   protected static boolean isTypeMatched(TabularType var0, TabularType var1) {
      if (var0 == var1) {
         return true;
      } else {
         List var2 = var0.getIndexNames();
         List var3 = var1.getIndexNames();
         return !var2.equals(var3) ? false : isTypeMatched(var0.getRowType(), var1.getRowType());
      }
   }

   protected static boolean isTypeMatched(ArrayType<?> var0, ArrayType<?> var1) {
      if (var0 == var1) {
         return true;
      } else {
         int var2 = var0.getDimension();
         int var3 = var1.getDimension();
         return var2 != var3 ? false : isTypeMatched(var0.getElementOpenType(), var1.getElementOpenType());
      }
   }

   private static boolean isTypeMatched(OpenType<?> var0, OpenType<?> var1) {
      if (var0 instanceof CompositeType) {
         if (!(var1 instanceof CompositeType)) {
            return false;
         }

         if (!isTypeMatched((CompositeType)var0, (CompositeType)var1)) {
            return false;
         }
      } else if (var0 instanceof TabularType) {
         if (!(var1 instanceof TabularType)) {
            return false;
         }

         if (!isTypeMatched((TabularType)var0, (TabularType)var1)) {
            return false;
         }
      } else if (var0 instanceof ArrayType) {
         if (!(var1 instanceof ArrayType)) {
            return false;
         }

         if (!isTypeMatched((ArrayType)var0, (ArrayType)var1)) {
            return false;
         }
      } else if (!var0.equals(var1)) {
         return false;
      }

      return true;
   }
}
