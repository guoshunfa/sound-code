package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import java.lang.reflect.Type;

public interface RuntimeAttributePropertyInfo extends AttributePropertyInfo<Type, Class>, RuntimePropertyInfo, RuntimeNonElementRef {
   RuntimeNonElement getTarget();
}
