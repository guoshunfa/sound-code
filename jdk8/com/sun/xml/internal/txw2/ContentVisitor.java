package com.sun.xml.internal.txw2;

interface ContentVisitor {
   void onStartDocument();

   void onEndDocument();

   void onEndTag();

   void onPcdata(StringBuilder var1);

   void onCdata(StringBuilder var1);

   void onStartTag(String var1, String var2, Attribute var3, NamespaceDecl var4);

   void onComment(StringBuilder var1);
}
