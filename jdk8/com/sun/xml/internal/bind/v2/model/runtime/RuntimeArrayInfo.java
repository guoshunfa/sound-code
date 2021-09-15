package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import java.lang.reflect.Type;

public interface RuntimeArrayInfo extends ArrayInfo<Type, Class>, RuntimeNonElement {
   Class getType();

   RuntimeNonElement getItemType();
}
