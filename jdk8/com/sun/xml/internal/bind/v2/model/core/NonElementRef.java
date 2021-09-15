package com.sun.xml.internal.bind.v2.model.core;

public interface NonElementRef<T, C> {
   NonElement<T, C> getTarget();

   PropertyInfo<T, C> getSource();
}
