package javax.print.attribute;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class HashAttributeSet implements AttributeSet, Serializable {
   private static final long serialVersionUID = 5311560590283707917L;
   private Class myInterface;
   private transient HashMap attrMap;

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Attribute[] var2 = this.toArray();
      var1.writeInt(var2.length);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.writeObject(var2[var3]);
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.attrMap = new HashMap();
      int var2 = var1.readInt();

      for(int var4 = 0; var4 < var2; ++var4) {
         Attribute var3 = (Attribute)var1.readObject();
         this.add(var3);
      }

   }

   public HashAttributeSet() {
      this(Attribute.class);
   }

   public HashAttributeSet(Attribute var1) {
      this(var1, Attribute.class);
   }

   public HashAttributeSet(Attribute[] var1) {
      this(var1, Attribute.class);
   }

   public HashAttributeSet(AttributeSet var1) {
      this(var1, Attribute.class);
   }

   protected HashAttributeSet(Class<?> var1) {
      this.attrMap = new HashMap();
      if (var1 == null) {
         throw new NullPointerException("null interface");
      } else {
         this.myInterface = var1;
      }
   }

   protected HashAttributeSet(Attribute var1, Class<?> var2) {
      this.attrMap = new HashMap();
      if (var2 == null) {
         throw new NullPointerException("null interface");
      } else {
         this.myInterface = var2;
         this.add(var1);
      }
   }

   protected HashAttributeSet(Attribute[] var1, Class<?> var2) {
      this.attrMap = new HashMap();
      if (var2 == null) {
         throw new NullPointerException("null interface");
      } else {
         this.myInterface = var2;
         int var3 = var1 == null ? 0 : var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.add(var1[var4]);
         }

      }
   }

   protected HashAttributeSet(AttributeSet var1, Class<?> var2) {
      this.attrMap = new HashMap();
      this.myInterface = var2;
      if (var1 != null) {
         Attribute[] var3 = var1.toArray();
         int var4 = var3 == null ? 0 : var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            this.add(var3[var5]);
         }
      }

   }

   public Attribute get(Class<?> var1) {
      return (Attribute)this.attrMap.get(AttributeSetUtilities.verifyAttributeCategory(var1, Attribute.class));
   }

   public boolean add(Attribute var1) {
      Object var2 = this.attrMap.put(var1.getCategory(), AttributeSetUtilities.verifyAttributeValue(var1, this.myInterface));
      return !var1.equals(var2);
   }

   public boolean remove(Class<?> var1) {
      return var1 != null && AttributeSetUtilities.verifyAttributeCategory(var1, Attribute.class) != null && this.attrMap.remove(var1) != null;
   }

   public boolean remove(Attribute var1) {
      return var1 != null && this.attrMap.remove(var1.getCategory()) != null;
   }

   public boolean containsKey(Class<?> var1) {
      return var1 != null && AttributeSetUtilities.verifyAttributeCategory(var1, Attribute.class) != null && this.attrMap.get(var1) != null;
   }

   public boolean containsValue(Attribute var1) {
      return var1 != null && var1 instanceof Attribute && var1.equals(this.attrMap.get(var1.getCategory()));
   }

   public boolean addAll(AttributeSet var1) {
      Attribute[] var2 = var1.toArray();
      boolean var3 = false;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         Attribute var5 = AttributeSetUtilities.verifyAttributeValue(var2[var4], this.myInterface);
         Object var6 = this.attrMap.put(var5.getCategory(), var5);
         var3 = !var5.equals(var6) || var3;
      }

      return var3;
   }

   public int size() {
      return this.attrMap.size();
   }

   public Attribute[] toArray() {
      Attribute[] var1 = new Attribute[this.size()];
      this.attrMap.values().toArray(var1);
      return var1;
   }

   public void clear() {
      this.attrMap.clear();
   }

   public boolean isEmpty() {
      return this.attrMap.isEmpty();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof AttributeSet) {
         AttributeSet var2 = (AttributeSet)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            Attribute[] var3 = this.toArray();

            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (!var2.containsValue(var3[var4])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = 0;
      Attribute[] var2 = this.toArray();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1 += var2[var3].hashCode();
      }

      return var1;
   }
}
