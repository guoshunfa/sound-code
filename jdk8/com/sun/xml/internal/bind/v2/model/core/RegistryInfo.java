package com.sun.xml.internal.bind.v2.model.core;

import java.util.Set;

public interface RegistryInfo<T, C> {
   Set<TypeInfo<T, C>> getReferences();

   C getClazz();
}
