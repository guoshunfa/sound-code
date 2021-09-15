package com.sun.xml.internal.bind.v2.model.core;

import java.util.Collection;
import java.util.Set;
import javax.xml.namespace.QName;

public interface ReferencePropertyInfo<T, C> extends PropertyInfo<T, C> {
   Set<? extends Element<T, C>> getElements();

   Collection<? extends TypeInfo<T, C>> ref();

   QName getXmlName();

   boolean isCollectionNillable();

   boolean isCollectionRequired();

   boolean isMixed();

   WildcardMode getWildcard();

   C getDOMHandler();

   boolean isRequired();

   Adapter<T, C> getAdapter();
}
