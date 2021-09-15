package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

interface PropertySeed<T, C, F, M> extends Locatable, AnnotationSource {
   String getName();

   T getRawType();
}
