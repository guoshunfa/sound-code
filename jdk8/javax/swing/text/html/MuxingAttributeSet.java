package javax.swing.text.html;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

class MuxingAttributeSet implements AttributeSet, Serializable {
   private AttributeSet[] attrs;

   public MuxingAttributeSet(AttributeSet[] var1) {
      this.attrs = var1;
   }

   protected MuxingAttributeSet() {
   }

   protected synchronized void setAttributes(AttributeSet[] var1) {
      this.attrs = var1;
   }

   protected synchronized AttributeSet[] getAttributes() {
      return this.attrs;
   }

   protected synchronized void insertAttributeSetAt(AttributeSet var1, int var2) {
      int var3 = this.attrs.length;
      AttributeSet[] var4 = new AttributeSet[var3 + 1];
      if (var2 < var3) {
         if (var2 > 0) {
            System.arraycopy(this.attrs, 0, var4, 0, var2);
            System.arraycopy(this.attrs, var2, var4, var2 + 1, var3 - var2);
         } else {
            System.arraycopy(this.attrs, 0, var4, 1, var3);
         }
      } else {
         System.arraycopy(this.attrs, 0, var4, 0, var3);
      }

      var4[var2] = var1;
      this.attrs = var4;
   }

   protected synchronized void removeAttributeSetAt(int var1) {
      int var2 = this.attrs.length;
      AttributeSet[] var3 = new AttributeSet[var2 - 1];
      if (var2 > 0) {
         if (var1 == 0) {
            System.arraycopy(this.attrs, 1, var3, 0, var2 - 1);
         } else if (var1 < var2 - 1) {
            System.arraycopy(this.attrs, 0, var3, 0, var1);
            System.arraycopy(this.attrs, var1 + 1, var3, var1, var2 - var1 - 1);
         } else {
            System.arraycopy(this.attrs, 0, var3, 0, var2 - 1);
         }
      }

      this.attrs = var3;
   }

   public int getAttributeCount() {
      AttributeSet[] var1 = this.getAttributes();
      int var2 = 0;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2 += var1[var3].getAttributeCount();
      }

      return var2;
   }

   public boolean isDefined(Object var1) {
      AttributeSet[] var2 = this.getAttributes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].isDefined(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isEqual(AttributeSet var1) {
      return this.getAttributeCount() == var1.getAttributeCount() && this.containsAttributes(var1);
   }

   public AttributeSet copyAttributes() {
      AttributeSet[] var1 = this.getAttributes();
      SimpleAttributeSet var2 = new SimpleAttributeSet();
      boolean var3 = false;

      for(int var4 = var1.length - 1; var4 >= 0; --var4) {
         var2.addAttributes(var1[var4]);
      }

      return var2;
   }

   public Object getAttribute(Object var1) {
      AttributeSet[] var2 = this.getAttributes();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4].getAttribute(var1);
         if (var5 != null) {
            return var5;
         }
      }

      return null;
   }

   public Enumeration getAttributeNames() {
      return new MuxingAttributeSet.MuxingAttributeNameEnumeration();
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

   public AttributeSet getResolveParent() {
      return null;
   }

   private class MuxingAttributeNameEnumeration implements Enumeration {
      private int attrIndex;
      private Enumeration currentEnum;

      MuxingAttributeNameEnumeration() {
         this.updateEnum();
      }

      public boolean hasMoreElements() {
         return this.currentEnum == null ? false : this.currentEnum.hasMoreElements();
      }

      public Object nextElement() {
         if (this.currentEnum == null) {
            throw new NoSuchElementException("No more names");
         } else {
            Object var1 = this.currentEnum.nextElement();
            if (!this.currentEnum.hasMoreElements()) {
               this.updateEnum();
            }

            return var1;
         }
      }

      void updateEnum() {
         AttributeSet[] var1 = MuxingAttributeSet.this.getAttributes();
         this.currentEnum = null;

         while(this.currentEnum == null && this.attrIndex < var1.length) {
            this.currentEnum = var1[this.attrIndex++].getAttributeNames();
            if (!this.currentEnum.hasMoreElements()) {
               this.currentEnum = null;
            }
         }

      }
   }
}
