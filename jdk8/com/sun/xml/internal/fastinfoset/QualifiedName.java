package com.sun.xml.internal.fastinfoset;

import javax.xml.namespace.QName;

public class QualifiedName {
   public String prefix;
   public String namespaceName;
   public String localName;
   public String qName;
   public int index;
   public int prefixIndex;
   public int namespaceNameIndex;
   public int localNameIndex;
   public int attributeId;
   public int attributeHash;
   private QName qNameObject;

   public QualifiedName() {
   }

   public QualifiedName(String prefix, String namespaceName, String localName, String qName) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = qName;
      this.index = -1;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
   }

   public void set(String prefix, String namespaceName, String localName, String qName) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = qName;
      this.index = -1;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
      this.qNameObject = null;
   }

   public QualifiedName(String prefix, String namespaceName, String localName, String qName, int index) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = qName;
      this.index = index;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
   }

   public final QualifiedName set(String prefix, String namespaceName, String localName, String qName, int index) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = qName;
      this.index = index;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
      this.qNameObject = null;
      return this;
   }

   public QualifiedName(String prefix, String namespaceName, String localName, String qName, int index, int prefixIndex, int namespaceNameIndex, int localNameIndex) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = qName;
      this.index = index;
      this.prefixIndex = prefixIndex + 1;
      this.namespaceNameIndex = namespaceNameIndex + 1;
      this.localNameIndex = localNameIndex;
   }

   public final QualifiedName set(String prefix, String namespaceName, String localName, String qName, int index, int prefixIndex, int namespaceNameIndex, int localNameIndex) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = qName;
      this.index = index;
      this.prefixIndex = prefixIndex + 1;
      this.namespaceNameIndex = namespaceNameIndex + 1;
      this.localNameIndex = localNameIndex;
      this.qNameObject = null;
      return this;
   }

   public QualifiedName(String prefix, String namespaceName, String localName) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = this.createQNameString(prefix, localName);
      this.index = -1;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
   }

   public final QualifiedName set(String prefix, String namespaceName, String localName) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = this.createQNameString(prefix, localName);
      this.index = -1;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
      this.qNameObject = null;
      return this;
   }

   public QualifiedName(String prefix, String namespaceName, String localName, int prefixIndex, int namespaceNameIndex, int localNameIndex, char[] charBuffer) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      if (charBuffer != null) {
         int l1 = prefix.length();
         int l2 = localName.length();
         int total = l1 + l2 + 1;
         if (total < charBuffer.length) {
            prefix.getChars(0, l1, charBuffer, 0);
            charBuffer[l1] = ':';
            localName.getChars(0, l2, charBuffer, l1 + 1);
            this.qName = new String(charBuffer, 0, total);
         } else {
            this.qName = this.createQNameString(prefix, localName);
         }
      } else {
         this.qName = this.localName;
      }

      this.prefixIndex = prefixIndex + 1;
      this.namespaceNameIndex = namespaceNameIndex + 1;
      this.localNameIndex = localNameIndex;
      this.index = -1;
   }

   public final QualifiedName set(String prefix, String namespaceName, String localName, int prefixIndex, int namespaceNameIndex, int localNameIndex, char[] charBuffer) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      if (charBuffer != null) {
         int l1 = prefix.length();
         int l2 = localName.length();
         int total = l1 + l2 + 1;
         if (total < charBuffer.length) {
            prefix.getChars(0, l1, charBuffer, 0);
            charBuffer[l1] = ':';
            localName.getChars(0, l2, charBuffer, l1 + 1);
            this.qName = new String(charBuffer, 0, total);
         } else {
            this.qName = this.createQNameString(prefix, localName);
         }
      } else {
         this.qName = this.localName;
      }

      this.prefixIndex = prefixIndex + 1;
      this.namespaceNameIndex = namespaceNameIndex + 1;
      this.localNameIndex = localNameIndex;
      this.index = -1;
      this.qNameObject = null;
      return this;
   }

   public QualifiedName(String prefix, String namespaceName, String localName, int index) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = this.createQNameString(prefix, localName);
      this.index = index;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
   }

   public final QualifiedName set(String prefix, String namespaceName, String localName, int index) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = this.createQNameString(prefix, localName);
      this.index = index;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
      this.qNameObject = null;
      return this;
   }

   public QualifiedName(String prefix, String namespaceName, String localName, int index, int prefixIndex, int namespaceNameIndex, int localNameIndex) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = this.createQNameString(prefix, localName);
      this.index = index;
      this.prefixIndex = prefixIndex + 1;
      this.namespaceNameIndex = namespaceNameIndex + 1;
      this.localNameIndex = localNameIndex;
   }

   public final QualifiedName set(String prefix, String namespaceName, String localName, int index, int prefixIndex, int namespaceNameIndex, int localNameIndex) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = localName;
      this.qName = this.createQNameString(prefix, localName);
      this.index = index;
      this.prefixIndex = prefixIndex + 1;
      this.namespaceNameIndex = namespaceNameIndex + 1;
      this.localNameIndex = localNameIndex;
      this.qNameObject = null;
      return this;
   }

   public QualifiedName(String prefix, String namespaceName) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = "";
      this.qName = "";
      this.index = -1;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
   }

   public final QualifiedName set(String prefix, String namespaceName) {
      this.prefix = prefix;
      this.namespaceName = namespaceName;
      this.localName = "";
      this.qName = "";
      this.index = -1;
      this.prefixIndex = 0;
      this.namespaceNameIndex = 0;
      this.localNameIndex = -1;
      this.qNameObject = null;
      return this;
   }

   public final QName getQName() {
      if (this.qNameObject == null) {
         this.qNameObject = new QName(this.namespaceName, this.localName, this.prefix);
      }

      return this.qNameObject;
   }

   public final String getQNameString() {
      return this.qName != "" ? this.qName : (this.qName = this.createQNameString(this.prefix, this.localName));
   }

   public final void createAttributeValues(int size) {
      this.attributeId = this.localNameIndex | this.namespaceNameIndex << 20;
      this.attributeHash = this.localNameIndex % size;
   }

   private final String createQNameString(String p, String l) {
      if (p != null && p.length() > 0) {
         StringBuffer b = new StringBuffer(p);
         b.append(':');
         b.append(l);
         return b.toString();
      } else {
         return l;
      }
   }
}
