package com.sun.org.apache.xerces.internal.xs.datatypes;

import com.sun.org.apache.xerces.internal.xni.QName;

public interface XSQName {
   QName getXNIQName();

   javax.xml.namespace.QName getJAXPQName();
}
