package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javax.xml.namespace.QName;

public interface StructureLoaderBuilder {
   QName TEXT_HANDLER = new QName("\u0000", "text");
   QName CATCH_ALL = new QName("\u0000", "catchAll");

   void buildChildElementUnmarshallers(UnmarshallerChain var1, QNameMap<ChildLoader> var2);
}
