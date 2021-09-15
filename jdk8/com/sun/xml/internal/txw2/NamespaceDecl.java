package com.sun.xml.internal.txw2;

final class NamespaceDecl {
   final String uri;
   boolean requirePrefix;
   final String dummyPrefix;
   final char uniqueId;
   String prefix;
   boolean declared;
   NamespaceDecl next;

   NamespaceDecl(char uniqueId, String uri, String prefix, boolean requirePrefix) {
      this.dummyPrefix = (new StringBuilder(2)).append('\u0000').append(uniqueId).toString();
      this.uri = uri;
      this.prefix = prefix;
      this.requirePrefix = requirePrefix;
      this.uniqueId = uniqueId;
   }
}
