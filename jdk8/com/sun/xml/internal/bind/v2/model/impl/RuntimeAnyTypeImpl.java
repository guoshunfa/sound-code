package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;

final class RuntimeAnyTypeImpl extends AnyTypeImpl<Type, Class> implements RuntimeNonElement {
   static final RuntimeNonElement theInstance = new RuntimeAnyTypeImpl();

   private RuntimeAnyTypeImpl() {
      super(Utils.REFLECTION_NAVIGATOR);
   }

   public <V> Transducer<V> getTransducer() {
      return null;
   }
}
