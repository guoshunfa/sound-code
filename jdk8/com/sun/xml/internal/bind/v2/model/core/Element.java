package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface Element<T, C> extends TypeInfo<T, C> {
   QName getElementName();

   Element<T, C> getSubstitutionHead();

   ClassInfo<T, C> getScope();
}
