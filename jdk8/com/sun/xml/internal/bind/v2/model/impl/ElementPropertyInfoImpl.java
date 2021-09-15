package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

class ElementPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ElementPropertyInfo<TypeT, ClassDeclT> {
   private List<TypeRefImpl<TypeT, ClassDeclT>> types;
   private final List<TypeInfo<TypeT, ClassDeclT>> ref = new AbstractList<TypeInfo<TypeT, ClassDeclT>>() {
      public TypeInfo<TypeT, ClassDeclT> get(int index) {
         return ((TypeRefImpl)ElementPropertyInfoImpl.this.getTypes().get(index)).getTarget();
      }

      public int size() {
         return ElementPropertyInfoImpl.this.getTypes().size();
      }
   };
   private Boolean isRequired;
   private final boolean isValueList;

   ElementPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> propertySeed) {
      super(parent, propertySeed);
      this.isValueList = this.seed.hasAnnotation(XmlList.class);
   }

   public List<? extends TypeRefImpl<TypeT, ClassDeclT>> getTypes() {
      if (this.types == null) {
         this.types = new FinalArrayList();
         XmlElement[] ann = null;
         XmlElement xe = (XmlElement)this.seed.readAnnotation(XmlElement.class);
         XmlElements xes = (XmlElements)this.seed.readAnnotation(XmlElements.class);
         if (xe != null && xes != null) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), xe.annotationType().getName(), xes.annotationType().getName()), xe, xes));
         }

         this.isRequired = true;
         if (xe != null) {
            ann = new XmlElement[]{xe};
         } else if (xes != null) {
            ann = xes.value();
         }

         if (ann == null) {
            TypeT t = this.getIndividualType();
            if (!this.nav().isPrimitive(t) || this.isCollection()) {
               this.isRequired = false;
            }

            this.types.add(this.createTypeRef(this.calcXmlName((XmlElement)null), t, this.isCollection(), (String)null));
         } else {
            XmlElement[] var4 = ann;
            int var5 = ann.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               XmlElement item = var4[var6];
               QName name = this.calcXmlName(item);
               TypeT type = this.reader().getClassValue(item, "type");
               if (this.nav().isSameType(type, this.nav().ref(XmlElement.DEFAULT.class))) {
                  type = this.getIndividualType();
               }

               if ((!this.nav().isPrimitive(type) || this.isCollection()) && !item.required()) {
                  this.isRequired = false;
               }

               this.types.add(this.createTypeRef(name, type, item.nillable(), this.getDefaultValue(item.defaultValue())));
            }
         }

         this.types = Collections.unmodifiableList(this.types);

         assert !this.types.contains((Object)null);
      }

      return this.types;
   }

   private String getDefaultValue(String value) {
      return value.equals("\u0000") ? null : value;
   }

   protected TypeRefImpl<TypeT, ClassDeclT> createTypeRef(QName name, TypeT type, boolean isNillable, String defaultValue) {
      return new TypeRefImpl(this, name, type, isNillable, defaultValue);
   }

   public boolean isValueList() {
      return this.isValueList;
   }

   public boolean isRequired() {
      if (this.isRequired == null) {
         this.getTypes();
      }

      return this.isRequired;
   }

   public List<? extends TypeInfo<TypeT, ClassDeclT>> ref() {
      return this.ref;
   }

   public final PropertyKind kind() {
      return PropertyKind.ELEMENT;
   }

   protected void link() {
      super.link();
      Iterator var1 = this.getTypes().iterator();

      TypeRefImpl ref;
      while(var1.hasNext()) {
         ref = (TypeRefImpl)var1.next();
         ref.link();
      }

      if (this.isValueList()) {
         if (this.id() != ID.IDREF) {
            var1 = this.types.iterator();

            while(var1.hasNext()) {
               ref = (TypeRefImpl)var1.next();
               if (!ref.getTarget().isSimpleType()) {
                  this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_NEEDS_SIMPLETYPE.format(this.nav().getTypeName(ref.getTarget().getType())), this));
                  break;
               }
            }
         }

         if (!this.isCollection()) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_ON_SINGLE_PROPERTY.format(), this));
         }
      }

   }
}
