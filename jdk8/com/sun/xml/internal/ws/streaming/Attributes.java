package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;

public interface Attributes {
   int getLength();

   boolean isNamespaceDeclaration(int var1);

   QName getName(int var1);

   String getURI(int var1);

   String getLocalName(int var1);

   String getPrefix(int var1);

   String getValue(int var1);

   int getIndex(QName var1);

   int getIndex(String var1, String var2);

   int getIndex(String var1);

   String getValue(QName var1);

   String getValue(String var1, String var2);

   String getValue(String var1);
}
