package com.sun.xml.internal.bind.v2.model.core;

public interface ArrayInfo<T, C> extends NonElement<T, C> {
   NonElement<T, C> getItemType();
}
