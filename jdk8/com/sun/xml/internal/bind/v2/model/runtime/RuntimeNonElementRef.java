package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;

public interface RuntimeNonElementRef extends NonElementRef<Type, Class> {
   RuntimeNonElement getTarget();

   RuntimePropertyInfo getSource();

   Transducer getTransducer();
}
