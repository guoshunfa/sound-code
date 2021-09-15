package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface BuiltinLeafInfo<T, C> extends LeafInfo<T, C> {
   QName getTypeName();
}
