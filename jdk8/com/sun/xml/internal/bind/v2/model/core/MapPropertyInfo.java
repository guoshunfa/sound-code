package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface MapPropertyInfo<T, C> extends PropertyInfo<T, C> {
   QName getXmlName();

   boolean isCollectionNillable();

   NonElement<T, C> getKeyType();

   NonElement<T, C> getValueType();
}
