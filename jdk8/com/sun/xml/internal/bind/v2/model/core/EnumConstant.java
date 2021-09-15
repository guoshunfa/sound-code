package com.sun.xml.internal.bind.v2.model.core;

public interface EnumConstant<T, C> {
   EnumLeafInfo<T, C> getEnclosingClass();

   String getLexicalValue();

   String getName();
}
