package com.sun.org.apache.xerces.internal.xs;

public interface XSObject {
   short getType();

   String getName();

   String getNamespace();

   XSNamespaceItem getNamespaceItem();
}
