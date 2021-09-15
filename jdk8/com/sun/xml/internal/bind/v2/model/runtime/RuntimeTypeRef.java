package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import java.lang.reflect.Type;

public interface RuntimeTypeRef extends TypeRef<Type, Class>, RuntimeNonElementRef {
   RuntimeNonElement getTarget();

   RuntimePropertyInfo getSource();
}
