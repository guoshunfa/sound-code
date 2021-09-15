package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import java.lang.reflect.Type;

public interface RuntimeValuePropertyInfo extends ValuePropertyInfo<Type, Class>, RuntimePropertyInfo, RuntimeNonElementRef {
   RuntimeNonElement getTarget();
}
