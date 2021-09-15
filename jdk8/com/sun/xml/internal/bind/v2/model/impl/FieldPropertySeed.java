package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;

class FieldPropertySeed<TypeT, ClassDeclT, FieldT, MethodT> implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> {
   protected final FieldT field;
   private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;

   FieldPropertySeed(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> classInfo, FieldT field) {
      this.parent = classInfo;
      this.field = field;
   }

   public <A extends Annotation> A readAnnotation(Class<A> a) {
      return this.parent.reader().getFieldAnnotation(a, this.field, this);
   }

   public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
      return this.parent.reader().hasFieldAnnotation(annotationType, this.field);
   }

   public String getName() {
      return this.parent.nav().getFieldName(this.field);
   }

   public TypeT getRawType() {
      return this.parent.nav().getFieldType(this.field);
   }

   public Locatable getUpstream() {
      return this.parent;
   }

   public Location getLocation() {
      return this.parent.nav().getFieldLocation(this.field);
   }
}
