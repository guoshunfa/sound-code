package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.xml.sax.AttributeList;
import org.xml.sax.ext.Attributes2;

public final class AttributesProxy implements AttributeList, Attributes2 {
   private XMLAttributes fAttributes;

   public AttributesProxy(XMLAttributes attributes) {
      this.fAttributes = attributes;
   }

   public void setAttributes(XMLAttributes attributes) {
      this.fAttributes = attributes;
   }

   public XMLAttributes getAttributes() {
      return this.fAttributes;
   }

   public int getLength() {
      return this.fAttributes.getLength();
   }

   public String getQName(int index) {
      return this.fAttributes.getQName(index);
   }

   public String getURI(int index) {
      String uri = this.fAttributes.getURI(index);
      return uri != null ? uri : XMLSymbols.EMPTY_STRING;
   }

   public String getLocalName(int index) {
      return this.fAttributes.getLocalName(index);
   }

   public String getType(int i) {
      return this.fAttributes.getType(i);
   }

   public String getType(String name) {
      return this.fAttributes.getType(name);
   }

   public String getType(String uri, String localName) {
      return uri.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getType((String)null, localName) : this.fAttributes.getType(uri, localName);
   }

   public String getValue(int i) {
      return this.fAttributes.getValue(i);
   }

   public String getValue(String name) {
      return this.fAttributes.getValue(name);
   }

   public String getValue(String uri, String localName) {
      return uri.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getValue((String)null, localName) : this.fAttributes.getValue(uri, localName);
   }

   public int getIndex(String qName) {
      return this.fAttributes.getIndex(qName);
   }

   public int getIndex(String uri, String localPart) {
      return uri.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getIndex((String)null, localPart) : this.fAttributes.getIndex(uri, localPart);
   }

   public boolean isDeclared(int index) {
      if (index >= 0 && index < this.fAttributes.getLength()) {
         return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
      } else {
         throw new ArrayIndexOutOfBoundsException(index);
      }
   }

   public boolean isDeclared(String qName) {
      int index = this.getIndex(qName);
      if (index == -1) {
         throw new IllegalArgumentException(qName);
      } else {
         return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
      }
   }

   public boolean isDeclared(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      if (index == -1) {
         throw new IllegalArgumentException(localName);
      } else {
         return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
      }
   }

   public boolean isSpecified(int index) {
      if (index >= 0 && index < this.fAttributes.getLength()) {
         return this.fAttributes.isSpecified(index);
      } else {
         throw new ArrayIndexOutOfBoundsException(index);
      }
   }

   public boolean isSpecified(String qName) {
      int index = this.getIndex(qName);
      if (index == -1) {
         throw new IllegalArgumentException(qName);
      } else {
         return this.fAttributes.isSpecified(index);
      }
   }

   public boolean isSpecified(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      if (index == -1) {
         throw new IllegalArgumentException(localName);
      } else {
         return this.fAttributes.isSpecified(index);
      }
   }

   public String getName(int i) {
      return this.fAttributes.getQName(i);
   }
}
