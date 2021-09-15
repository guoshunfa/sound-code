package org.xml.sax.ext;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class Attributes2Impl extends AttributesImpl implements Attributes2 {
   private boolean[] declared;
   private boolean[] specified;

   public Attributes2Impl() {
      this.specified = null;
      this.declared = null;
   }

   public Attributes2Impl(Attributes atts) {
      super(atts);
   }

   public boolean isDeclared(int index) {
      if (index >= 0 && index < this.getLength()) {
         return this.declared[index];
      } else {
         throw new ArrayIndexOutOfBoundsException("No attribute at index: " + index);
      }
   }

   public boolean isDeclared(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      if (index < 0) {
         throw new IllegalArgumentException("No such attribute: local=" + localName + ", namespace=" + uri);
      } else {
         return this.declared[index];
      }
   }

   public boolean isDeclared(String qName) {
      int index = this.getIndex(qName);
      if (index < 0) {
         throw new IllegalArgumentException("No such attribute: " + qName);
      } else {
         return this.declared[index];
      }
   }

   public boolean isSpecified(int index) {
      if (index >= 0 && index < this.getLength()) {
         return this.specified[index];
      } else {
         throw new ArrayIndexOutOfBoundsException("No attribute at index: " + index);
      }
   }

   public boolean isSpecified(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      if (index < 0) {
         throw new IllegalArgumentException("No such attribute: local=" + localName + ", namespace=" + uri);
      } else {
         return this.specified[index];
      }
   }

   public boolean isSpecified(String qName) {
      int index = this.getIndex(qName);
      if (index < 0) {
         throw new IllegalArgumentException("No such attribute: " + qName);
      } else {
         return this.specified[index];
      }
   }

   public void setAttributes(Attributes atts) {
      int length = atts.getLength();
      super.setAttributes(atts);
      this.declared = new boolean[length];
      this.specified = new boolean[length];
      if (atts instanceof Attributes2) {
         Attributes2 a2 = (Attributes2)atts;

         for(int i = 0; i < length; ++i) {
            this.declared[i] = a2.isDeclared(i);
            this.specified[i] = a2.isSpecified(i);
         }
      } else {
         for(int i = 0; i < length; ++i) {
            this.declared[i] = !"CDATA".equals(atts.getType(i));
            this.specified[i] = true;
         }
      }

   }

   public void addAttribute(String uri, String localName, String qName, String type, String value) {
      super.addAttribute(uri, localName, qName, type, value);
      int length = this.getLength();
      if (this.specified == null) {
         this.specified = new boolean[length];
         this.declared = new boolean[length];
      } else if (length > this.specified.length) {
         boolean[] newFlags = new boolean[length];
         System.arraycopy(this.declared, 0, newFlags, 0, this.declared.length);
         this.declared = newFlags;
         newFlags = new boolean[length];
         System.arraycopy(this.specified, 0, newFlags, 0, this.specified.length);
         this.specified = newFlags;
      }

      this.specified[length - 1] = true;
      this.declared[length - 1] = !"CDATA".equals(type);
   }

   public void removeAttribute(int index) {
      int origMax = this.getLength() - 1;
      super.removeAttribute(index);
      if (index != origMax) {
         System.arraycopy(this.declared, index + 1, this.declared, index, origMax - index);
         System.arraycopy(this.specified, index + 1, this.specified, index, origMax - index);
      }

   }

   public void setDeclared(int index, boolean value) {
      if (index >= 0 && index < this.getLength()) {
         this.declared[index] = value;
      } else {
         throw new ArrayIndexOutOfBoundsException("No attribute at index: " + index);
      }
   }

   public void setSpecified(int index, boolean value) {
      if (index >= 0 && index < this.getLength()) {
         this.specified[index] = value;
      } else {
         throw new ArrayIndexOutOfBoundsException("No attribute at index: " + index);
      }
   }
}
