package com.sun.xml.internal.stream.buffer;

import org.xml.sax.Attributes;

public final class AttributesHolder implements Attributes {
   private static final int DEFAULT_CAPACITY = 8;
   private static final int ITEM_SIZE = 8;
   private static final int PREFIX = 0;
   private static final int URI = 1;
   private static final int LOCAL_NAME = 2;
   private static final int QNAME = 3;
   private static final int TYPE = 4;
   private static final int VALUE = 5;
   private int _attributeCount;
   private String[] _strings = new String[64];

   public final int getLength() {
      return this._attributeCount;
   }

   public final String getPrefix(int index) {
      return index >= 0 && index < this._attributeCount ? this._strings[(index << 3) + 0] : null;
   }

   public final String getLocalName(int index) {
      return index >= 0 && index < this._attributeCount ? this._strings[(index << 3) + 2] : null;
   }

   public final String getQName(int index) {
      return index >= 0 && index < this._attributeCount ? this._strings[(index << 3) + 3] : null;
   }

   public final String getType(int index) {
      return index >= 0 && index < this._attributeCount ? this._strings[(index << 3) + 4] : null;
   }

   public final String getURI(int index) {
      return index >= 0 && index < this._attributeCount ? this._strings[(index << 3) + 1] : null;
   }

   public final String getValue(int index) {
      return index >= 0 && index < this._attributeCount ? this._strings[(index << 3) + 5] : null;
   }

   public final int getIndex(String qName) {
      for(int i = 0; i < this._attributeCount; ++i) {
         if (qName.equals(this._strings[(i << 3) + 3])) {
            return i;
         }
      }

      return -1;
   }

   public final String getType(String qName) {
      int i = (this.getIndex(qName) << 3) + 4;
      return i >= 0 ? this._strings[i] : null;
   }

   public final String getValue(String qName) {
      int i = (this.getIndex(qName) << 3) + 5;
      return i >= 0 ? this._strings[i] : null;
   }

   public final int getIndex(String uri, String localName) {
      for(int i = 0; i < this._attributeCount; ++i) {
         if (localName.equals(this._strings[(i << 3) + 2]) && uri.equals(this._strings[(i << 3) + 1])) {
            return i;
         }
      }

      return -1;
   }

   public final String getType(String uri, String localName) {
      int i = (this.getIndex(uri, localName) << 3) + 4;
      return i >= 0 ? this._strings[i] : null;
   }

   public final String getValue(String uri, String localName) {
      int i = (this.getIndex(uri, localName) << 3) + 5;
      return i >= 0 ? this._strings[i] : null;
   }

   public final void clear() {
      if (this._attributeCount > 0) {
         for(int i = 0; i < this._attributeCount; ++i) {
            this._strings[(i << 3) + 5] = null;
         }

         this._attributeCount = 0;
      }

   }

   public final void addAttributeWithQName(String uri, String localName, String qName, String type, String value) {
      int i = this._attributeCount << 3;
      if (i == this._strings.length) {
         this.resize(i);
      }

      this._strings[i + 0] = null;
      this._strings[i + 1] = uri;
      this._strings[i + 2] = localName;
      this._strings[i + 3] = qName;
      this._strings[i + 4] = type;
      this._strings[i + 5] = value;
      ++this._attributeCount;
   }

   public final void addAttributeWithPrefix(String prefix, String uri, String localName, String type, String value) {
      int i = this._attributeCount << 3;
      if (i == this._strings.length) {
         this.resize(i);
      }

      this._strings[i + 0] = prefix;
      this._strings[i + 1] = uri;
      this._strings[i + 2] = localName;
      this._strings[i + 3] = null;
      this._strings[i + 4] = type;
      this._strings[i + 5] = value;
      ++this._attributeCount;
   }

   private void resize(int length) {
      int newLength = length * 2;
      String[] strings = new String[newLength];
      System.arraycopy(this._strings, 0, strings, 0, length);
      this._strings = strings;
   }
}
