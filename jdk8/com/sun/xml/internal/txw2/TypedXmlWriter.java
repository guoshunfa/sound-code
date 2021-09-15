package com.sun.xml.internal.txw2;

import javax.xml.namespace.QName;

public interface TypedXmlWriter {
   void commit();

   void commit(boolean var1);

   void block();

   Document getDocument();

   void _attribute(String var1, Object var2);

   void _attribute(String var1, String var2, Object var3);

   void _attribute(QName var1, Object var2);

   void _namespace(String var1);

   void _namespace(String var1, String var2);

   void _namespace(String var1, boolean var2);

   void _pcdata(Object var1);

   void _cdata(Object var1);

   void _comment(Object var1) throws UnsupportedOperationException;

   <T extends TypedXmlWriter> T _element(String var1, Class<T> var2);

   <T extends TypedXmlWriter> T _element(String var1, String var2, Class<T> var3);

   <T extends TypedXmlWriter> T _element(QName var1, Class<T> var2);

   <T extends TypedXmlWriter> T _element(Class<T> var1);

   <T extends TypedXmlWriter> T _cast(Class<T> var1);
}
