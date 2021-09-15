package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;

public class SimpleAttributeSet implements MutableAttributeSet, Serializable, Cloneable {
   private static final long serialVersionUID = -6631553454711782652L;
   public static final AttributeSet EMPTY = new SimpleAttributeSet.EmptyAttributeSet();
   private transient LinkedHashMap<Object, Object> table = new LinkedHashMap(3);

   public SimpleAttributeSet() {
   }

   public SimpleAttributeSet(AttributeSet var1) {
      this.addAttributes(var1);
   }

   public boolean isEmpty() {
      return this.table.isEmpty();
   }

   public int getAttributeCount() {
      return this.table.size();
   }

   public boolean isDefined(Object var1) {
      return this.table.containsKey(var1);
   }

   public boolean isEqual(AttributeSet var1) {
      return this.getAttributeCount() == var1.getAttributeCount() && this.containsAttributes(var1);
   }

   public AttributeSet copyAttributes() {
      return (AttributeSet)this.clone();
   }

   public Enumeration<?> getAttributeNames() {
      return Collections.enumeration(this.table.keySet());
   }

   public Object getAttribute(Object var1) {
      Object var2 = this.table.get(var1);
      if (var2 == null) {
         AttributeSet var3 = this.getResolveParent();
         if (var3 != null) {
            var2 = var3.getAttribute(var1);
         }
      }

      return var2;
   }

   public boolean containsAttribute(Object var1, Object var2) {
      return var2.equals(this.getAttribute(var1));
   }

   public boolean containsAttributes(AttributeSet var1) {
      boolean var2 = true;

      Object var4;
      for(Enumeration var3 = var1.getAttributeNames(); var2 && var3.hasMoreElements(); var2 = var1.getAttribute(var4).equals(this.getAttribute(var4))) {
         var4 = var3.nextElement();
      }

      return var2;
   }

   public void addAttribute(Object var1, Object var2) {
      this.table.put(var1, var2);
   }

   public void addAttributes(AttributeSet var1) {
      Enumeration var2 = var1.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         this.addAttribute(var3, var1.getAttribute(var3));
      }

   }

   public void removeAttribute(Object var1) {
      this.table.remove(var1);
   }

   public void removeAttributes(Enumeration<?> var1) {
      while(var1.hasMoreElements()) {
         this.removeAttribute(var1.nextElement());
      }

   }

   public void removeAttributes(AttributeSet var1) {
      if (var1 == this) {
         this.table.clear();
      } else {
         Enumeration var2 = var1.getAttributeNames();

         while(var2.hasMoreElements()) {
            Object var3 = var2.nextElement();
            Object var4 = var1.getAttribute(var3);
            if (var4.equals(this.getAttribute(var3))) {
               this.removeAttribute(var3);
            }
         }
      }

   }

   public AttributeSet getResolveParent() {
      return (AttributeSet)this.table.get(StyleConstants.ResolveAttribute);
   }

   public void setResolveParent(AttributeSet var1) {
      this.addAttribute(StyleConstants.ResolveAttribute, var1);
   }

   public Object clone() {
      SimpleAttributeSet var1;
      try {
         var1 = (SimpleAttributeSet)super.clone();
         var1.table = (LinkedHashMap)this.table.clone();
      } catch (CloneNotSupportedException var3) {
         var1 = null;
      }

      return var1;
   }

   public int hashCode() {
      return this.table.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof AttributeSet) {
         AttributeSet var2 = (AttributeSet)var1;
         return this.isEqual(var2);
      } else {
         return false;
      }
   }

   public String toString() {
      String var1 = "";
      Enumeration var2 = this.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         Object var4 = this.getAttribute(var3);
         if (var4 instanceof AttributeSet) {
            var1 = var1 + var3 + "=**AttributeSet** ";
         } else {
            var1 = var1 + var3 + "=" + var4 + " ";
         }
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      StyleContext.writeAttributeSet(var1, this);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.table = new LinkedHashMap(3);
      StyleContext.readAttributeSet(var1, this);
   }

   static class EmptyAttributeSet implements AttributeSet, Serializable {
      static final long serialVersionUID = -8714803568785904228L;

      public int getAttributeCount() {
         return 0;
      }

      public boolean isDefined(Object var1) {
         return false;
      }

      public boolean isEqual(AttributeSet var1) {
         return var1.getAttributeCount() == 0;
      }

      public AttributeSet copyAttributes() {
         return this;
      }

      public Object getAttribute(Object var1) {
         return null;
      }

      public Enumeration getAttributeNames() {
         return Collections.emptyEnumeration();
      }

      public boolean containsAttribute(Object var1, Object var2) {
         return false;
      }

      public boolean containsAttributes(AttributeSet var1) {
         return var1.getAttributeCount() == 0;
      }

      public AttributeSet getResolveParent() {
         return null;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof AttributeSet && ((AttributeSet)var1).getAttributeCount() == 0;
         }
      }

      public int hashCode() {
         return 0;
      }
   }
}
