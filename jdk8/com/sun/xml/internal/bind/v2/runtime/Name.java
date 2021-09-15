package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.QName;

public final class Name implements Comparable<Name> {
   public final String nsUri;
   public final String localName;
   public final short nsUriIndex;
   public final short localNameIndex;
   public final short qNameIndex;
   public final boolean isAttribute;

   Name(int qNameIndex, int nsUriIndex, String nsUri, int localIndex, String localName, boolean isAttribute) {
      this.qNameIndex = (short)qNameIndex;
      this.nsUri = nsUri;
      this.localName = localName;
      this.nsUriIndex = (short)nsUriIndex;
      this.localNameIndex = (short)localIndex;
      this.isAttribute = isAttribute;
   }

   public String toString() {
      return '{' + this.nsUri + '}' + this.localName;
   }

   public QName toQName() {
      return new QName(this.nsUri, this.localName);
   }

   public boolean equals(String nsUri, String localName) {
      return localName.equals(this.localName) && nsUri.equals(this.nsUri);
   }

   public int compareTo(Name that) {
      int r = this.nsUri.compareTo(that.nsUri);
      return r != 0 ? r : this.localName.compareTo(that.localName);
   }
}
