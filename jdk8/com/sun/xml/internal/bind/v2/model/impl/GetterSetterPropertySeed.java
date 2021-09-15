package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.beans.Introspector;
import java.lang.annotation.Annotation;

class GetterSetterPropertySeed<TypeT, ClassDeclT, FieldT, MethodT> implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> {
   protected final MethodT getter;
   protected final MethodT setter;
   private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;

   GetterSetterPropertySeed(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent, MethodT getter, MethodT setter) {
      this.parent = parent;
      this.getter = getter;
      this.setter = setter;
      if (getter == null && setter == null) {
         throw new IllegalArgumentException();
      }
   }

   public TypeT getRawType() {
      return this.getter != null ? this.parent.nav().getReturnType(this.getter) : this.parent.nav().getMethodParameters(this.setter)[0];
   }

   public <A extends Annotation> A readAnnotation(Class<A> annotation) {
      return this.parent.reader().getMethodAnnotation(annotation, this.getter, this.setter, this);
   }

   public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
      return this.parent.reader().hasMethodAnnotation(annotationType, this.getName(), this.getter, this.setter, this);
   }

   public String getName() {
      return this.getter != null ? this.getName(this.getter) : this.getName(this.setter);
   }

   private String getName(MethodT m) {
      String seed = this.parent.nav().getMethodName(m);
      String lseed = seed.toLowerCase();
      if (!lseed.startsWith("get") && !lseed.startsWith("set")) {
         return lseed.startsWith("is") ? camelize(seed.substring(2)) : seed;
      } else {
         return camelize(seed.substring(3));
      }
   }

   private static String camelize(String s) {
      return Introspector.decapitalize(s);
   }

   public Locatable getUpstream() {
      return this.parent;
   }

   public Location getLocation() {
      return this.getter != null ? this.parent.nav().getMethodLocation(this.getter) : this.parent.nav().getMethodLocation(this.setter);
   }
}
