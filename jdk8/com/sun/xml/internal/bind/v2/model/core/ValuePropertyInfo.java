package com.sun.xml.internal.bind.v2.model.core;

public interface ValuePropertyInfo<T, C> extends PropertyInfo<T, C>, NonElementRef<T, C> {
   Adapter<T, C> getAdapter();
}
