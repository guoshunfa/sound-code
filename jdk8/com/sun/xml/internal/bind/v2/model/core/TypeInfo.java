package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

public interface TypeInfo<T, C> extends Locatable {
   T getType();

   boolean canBeReferencedByIDREF();
}
