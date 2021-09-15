package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

class RuntimeReferencePropertyInfoImpl extends ReferencePropertyInfoImpl<Type, Class, Field, Method> implements RuntimeReferencePropertyInfo {
   private final Accessor acc;

   public RuntimeReferencePropertyInfoImpl(RuntimeClassInfoImpl classInfo, PropertySeed<Type, Class, Field, Method> seed) {
      super(classInfo, seed);
      Accessor rawAcc = ((RuntimeClassInfoImpl.RuntimePropertySeed)seed).getAccessor();
      if (this.getAdapter() != null && !this.isCollection()) {
         rawAcc = rawAcc.adapt(this.getAdapter());
      }

      this.acc = rawAcc;
   }

   public Set<? extends RuntimeElement> getElements() {
      return super.getElements();
   }

   public Set<? extends RuntimeElement> ref() {
      return super.ref();
   }

   public Accessor getAccessor() {
      return this.acc;
   }

   public boolean elementOnlyContent() {
      return !this.isMixed();
   }
}
