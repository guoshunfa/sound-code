package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

class AttributePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends SingleTypePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements AttributePropertyInfo<TypeT, ClassDeclT> {
   private final QName xmlName;
   private final boolean isRequired;

   AttributePropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> seed) {
      super(parent, seed);
      XmlAttribute att = (XmlAttribute)seed.readAnnotation(XmlAttribute.class);

      assert att != null;

      if (att.required()) {
         this.isRequired = true;
      } else {
         this.isRequired = this.nav().isPrimitive(this.getIndividualType());
      }

      this.xmlName = this.calcXmlName(att);
   }

   private QName calcXmlName(XmlAttribute att) {
      String uri = att.namespace();
      String local = att.name();
      if (local.equals("##default")) {
         local = NameConverter.standard.toVariableName(this.getName());
      }

      if (uri.equals("##default")) {
         XmlSchema xs = (XmlSchema)this.reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
         if (xs != null) {
            switch(xs.attributeFormDefault()) {
            case QUALIFIED:
               uri = this.parent.getTypeName().getNamespaceURI();
               if (uri.length() == 0) {
                  uri = this.parent.builder.defaultNsUri;
               }
               break;
            case UNQUALIFIED:
            case UNSET:
               uri = "";
            }
         } else {
            uri = "";
         }
      }

      return new QName(uri.intern(), local.intern());
   }

   public boolean isRequired() {
      return this.isRequired;
   }

   public final QName getXmlName() {
      return this.xmlName;
   }

   public final PropertyKind kind() {
      return PropertyKind.ATTRIBUTE;
   }
}
