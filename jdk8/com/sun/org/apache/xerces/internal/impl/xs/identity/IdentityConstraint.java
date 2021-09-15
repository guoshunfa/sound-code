package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSIDCDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class IdentityConstraint implements XSIDCDefinition {
   protected short type;
   protected String fNamespace;
   protected String fIdentityConstraintName;
   protected String fElementName;
   protected Selector fSelector;
   protected int fFieldCount;
   protected Field[] fFields;
   protected XSAnnotationImpl[] fAnnotations = null;
   protected int fNumAnnotations;

   protected IdentityConstraint(String namespace, String identityConstraintName, String elemName) {
      this.fNamespace = namespace;
      this.fIdentityConstraintName = identityConstraintName;
      this.fElementName = elemName;
   }

   public String getIdentityConstraintName() {
      return this.fIdentityConstraintName;
   }

   public void setSelector(Selector selector) {
      this.fSelector = selector;
   }

   public Selector getSelector() {
      return this.fSelector;
   }

   public void addField(Field field) {
      if (this.fFields == null) {
         this.fFields = new Field[4];
      } else if (this.fFieldCount == this.fFields.length) {
         this.fFields = resize(this.fFields, this.fFieldCount * 2);
      }

      this.fFields[this.fFieldCount++] = field;
   }

   public int getFieldCount() {
      return this.fFieldCount;
   }

   public Field getFieldAt(int index) {
      return this.fFields[index];
   }

   public String getElementName() {
      return this.fElementName;
   }

   public String toString() {
      String s = super.toString();
      int index1 = s.lastIndexOf(36);
      if (index1 != -1) {
         return s.substring(index1 + 1);
      } else {
         int index2 = s.lastIndexOf(46);
         return index2 != -1 ? s.substring(index2 + 1) : s;
      }
   }

   public boolean equals(IdentityConstraint id) {
      boolean areEqual = this.fIdentityConstraintName.equals(id.fIdentityConstraintName);
      if (!areEqual) {
         return false;
      } else {
         areEqual = this.fSelector.toString().equals(id.fSelector.toString());
         if (!areEqual) {
            return false;
         } else {
            areEqual = this.fFieldCount == id.fFieldCount;
            if (!areEqual) {
               return false;
            } else {
               for(int i = 0; i < this.fFieldCount; ++i) {
                  if (!this.fFields[i].toString().equals(id.fFields[i].toString())) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   static final Field[] resize(Field[] oldArray, int newSize) {
      Field[] newArray = new Field[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
      return newArray;
   }

   public short getType() {
      return 10;
   }

   public String getName() {
      return this.fIdentityConstraintName;
   }

   public String getNamespace() {
      return this.fNamespace;
   }

   public short getCategory() {
      return this.type;
   }

   public String getSelectorStr() {
      return this.fSelector != null ? this.fSelector.toString() : null;
   }

   public StringList getFieldStrs() {
      String[] strs = new String[this.fFieldCount];

      for(int i = 0; i < this.fFieldCount; ++i) {
         strs[i] = this.fFields[i].toString();
      }

      return new StringListImpl(strs, this.fFieldCount);
   }

   public XSIDCDefinition getRefKey() {
      return null;
   }

   public XSObjectList getAnnotations() {
      return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
   }

   public XSNamespaceItem getNamespaceItem() {
      return null;
   }

   public void addAnnotation(XSAnnotationImpl annotation) {
      if (annotation != null) {
         if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
         } else if (this.fNumAnnotations == this.fAnnotations.length) {
            XSAnnotationImpl[] newArray = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, newArray, 0, this.fNumAnnotations);
            this.fAnnotations = newArray;
         }

         this.fAnnotations[this.fNumAnnotations++] = annotation;
      }
   }
}
