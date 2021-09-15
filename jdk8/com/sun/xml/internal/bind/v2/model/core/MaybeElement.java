package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface MaybeElement<T, C> extends NonElement<T, C> {
   boolean isElement();

   QName getElementName();

   Element<T, C> asElement();
}
