package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public interface XPointerSchema extends XMLComponent, XMLDocumentFilter {
   void setXPointerSchemaName(String var1);

   String getXpointerSchemaName();

   void setParent(Object var1);

   Object getParent();

   void setXPointerSchemaPointer(String var1);

   String getXPointerSchemaPointer();

   boolean isSubResourceIndentified();

   void reset();
}
