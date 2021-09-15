package com.sun.org.apache.xerces.internal.impl;

public class XML11NamespaceBinder extends XMLNamespaceBinder {
   protected boolean prefixBoundToNullURI(String uri, String localpart) {
      return false;
   }
}
