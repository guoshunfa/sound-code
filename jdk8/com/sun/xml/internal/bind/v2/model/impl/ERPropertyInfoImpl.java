package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

abstract class ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends PropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> {
   private final QName xmlName;
   private final boolean wrapperNillable;
   private final boolean wrapperRequired;

   public ERPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> classInfo, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> propertySeed) {
      super(classInfo, propertySeed);
      XmlElementWrapper e = (XmlElementWrapper)this.seed.readAnnotation(XmlElementWrapper.class);
      boolean nil = false;
      boolean required = false;
      if (!this.isCollection()) {
         this.xmlName = null;
         if (e != null) {
            classInfo.builder.reportError(new IllegalAnnotationException(Messages.XML_ELEMENT_WRAPPER_ON_NON_COLLECTION.format(this.nav().getClassName(this.parent.getClazz()) + '.' + this.seed.getName()), e));
         }
      } else if (e != null) {
         this.xmlName = this.calcXmlName(e);
         nil = e.nillable();
         required = e.required();
      } else {
         this.xmlName = null;
      }

      this.wrapperNillable = nil;
      this.wrapperRequired = required;
   }

   public final QName getXmlName() {
      return this.xmlName;
   }

   public final boolean isCollectionNillable() {
      return this.wrapperNillable;
   }

   public final boolean isCollectionRequired() {
      return this.wrapperRequired;
   }
}
