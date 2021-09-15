package com.sun.xml.internal.bind.v2.model.core;

public interface EnumLeafInfo<T, C> extends LeafInfo<T, C> {
   C getClazz();

   NonElement<T, C> getBaseType();

   Iterable<? extends EnumConstant> getConstants();
}
