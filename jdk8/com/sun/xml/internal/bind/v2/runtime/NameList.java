package com.sun.xml.internal.bind.v2.runtime;

public final class NameList {
   public final String[] namespaceURIs;
   public final boolean[] nsUriCannotBeDefaulted;
   public final String[] localNames;
   public final int numberOfElementNames;
   public final int numberOfAttributeNames;

   public NameList(String[] namespaceURIs, boolean[] nsUriCannotBeDefaulted, String[] localNames, int numberElementNames, int numberAttributeNames) {
      this.namespaceURIs = namespaceURIs;
      this.nsUriCannotBeDefaulted = nsUriCannotBeDefaulted;
      this.localNames = localNames;
      this.numberOfElementNames = numberElementNames;
      this.numberOfAttributeNames = numberAttributeNames;
   }
}
