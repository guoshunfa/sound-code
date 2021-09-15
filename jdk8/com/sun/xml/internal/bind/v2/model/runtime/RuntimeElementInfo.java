package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import java.lang.reflect.Type;
import javax.xml.bind.JAXBElement;

public interface RuntimeElementInfo extends ElementInfo<Type, Class>, RuntimeElement {
   RuntimeClassInfo getScope();

   RuntimeElementPropertyInfo getProperty();

   Class<? extends JAXBElement> getType();

   RuntimeNonElement getContentType();
}
