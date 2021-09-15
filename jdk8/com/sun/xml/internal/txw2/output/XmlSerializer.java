package com.sun.xml.internal.txw2.output;

public interface XmlSerializer {
   void startDocument();

   void beginStartTag(String var1, String var2, String var3);

   void writeAttribute(String var1, String var2, String var3, StringBuilder var4);

   void writeXmlns(String var1, String var2);

   void endStartTag(String var1, String var2, String var3);

   void endTag();

   void text(StringBuilder var1);

   void cdata(StringBuilder var1);

   void comment(StringBuilder var1);

   void endDocument();

   void flush();
}
