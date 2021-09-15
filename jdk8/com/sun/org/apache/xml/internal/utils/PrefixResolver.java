package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.Node;

public interface PrefixResolver {
   String getNamespaceForPrefix(String var1);

   String getNamespaceForPrefix(String var1, Node var2);

   String getBaseIdentifier();

   boolean handlesNullPrefixes();
}
