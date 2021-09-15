package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Type;
import java.util.Collection;

public interface RuntimePropertyInfo extends PropertyInfo<Type, Class> {
   Collection<? extends RuntimeTypeInfo> ref();

   Accessor getAccessor();

   boolean elementOnlyContent();

   Type getRawType();

   Type getIndividualType();
}
