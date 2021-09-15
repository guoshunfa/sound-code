package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;

class EnumLeafInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements EnumLeafInfo<T, C>, Element<T, C>, Iterable<EnumConstantImpl<T, C, F, M>> {
   final C clazz;
   NonElement<T, C> baseType;
   private final T type;
   private final QName typeName;
   private EnumConstantImpl<T, C, F, M> firstConstant;
   private QName elementName;
   protected boolean tokenStringType;

   public EnumLeafInfoImpl(ModelBuilder<T, C, F, M> builder, Locatable upstream, C clazz, T type) {
      super(builder, upstream);
      this.clazz = clazz;
      this.type = type;
      this.elementName = this.parseElementName(clazz);
      this.typeName = this.parseTypeName(clazz);
      XmlEnum xe = (XmlEnum)builder.reader.getClassAnnotation(XmlEnum.class, clazz, this);
      if (xe != null) {
         T base = builder.reader.getClassValue(xe, "value");
         this.baseType = builder.getTypeInfo(base, this);
      } else {
         this.baseType = builder.getTypeInfo(builder.nav.ref(String.class), this);
      }

   }

   protected void calcConstants() {
      EnumConstantImpl<T, C, F, M> last = null;
      Collection<? extends F> fields = this.nav().getDeclaredFields(this.clazz);
      Iterator var3 = fields.iterator();

      while(var3.hasNext()) {
         F f = var3.next();
         if (this.nav().isSameType(this.nav().getFieldType(f), this.nav().ref(String.class))) {
            XmlSchemaType schemaTypeAnnotation = (XmlSchemaType)this.builder.reader.getFieldAnnotation(XmlSchemaType.class, f, this);
            if (schemaTypeAnnotation != null && "token".equals(schemaTypeAnnotation.name())) {
               this.tokenStringType = true;
               break;
            }
         }
      }

      F[] constants = this.nav().getEnumConstants(this.clazz);

      for(int i = constants.length - 1; i >= 0; --i) {
         F constant = constants[i];
         String name = this.nav().getFieldName(constant);
         XmlEnumValue xev = (XmlEnumValue)this.builder.reader.getFieldAnnotation(XmlEnumValue.class, constant, this);
         String literal;
         if (xev == null) {
            literal = name;
         } else {
            literal = xev.value();
         }

         last = this.createEnumConstant(name, literal, constant, last);
      }

      this.firstConstant = last;
   }

   protected EnumConstantImpl<T, C, F, M> createEnumConstant(String name, String literal, F constant, EnumConstantImpl<T, C, F, M> last) {
      return new EnumConstantImpl(this, name, literal, last);
   }

   public T getType() {
      return this.type;
   }

   public boolean isToken() {
      return this.tokenStringType;
   }

   /** @deprecated */
   public final boolean canBeReferencedByIDREF() {
      return false;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public C getClazz() {
      return this.clazz;
   }

   public NonElement<T, C> getBaseType() {
      return this.baseType;
   }

   public boolean isSimpleType() {
      return true;
   }

   public Location getLocation() {
      return this.nav().getClassLocation(this.clazz);
   }

   public Iterable<? extends EnumConstantImpl<T, C, F, M>> getConstants() {
      if (this.firstConstant == null) {
         this.calcConstants();
      }

      return this;
   }

   public void link() {
      this.getConstants();
      super.link();
   }

   /** @deprecated */
   public Element<T, C> getSubstitutionHead() {
      return null;
   }

   public QName getElementName() {
      return this.elementName;
   }

   public boolean isElement() {
      return this.elementName != null;
   }

   public Element<T, C> asElement() {
      return this.isElement() ? this : null;
   }

   /** @deprecated */
   public ClassInfo<T, C> getScope() {
      return null;
   }

   public Iterator<EnumConstantImpl<T, C, F, M>> iterator() {
      return new Iterator<EnumConstantImpl<T, C, F, M>>() {
         private EnumConstantImpl<T, C, F, M> next;

         {
            this.next = EnumLeafInfoImpl.this.firstConstant;
         }

         public boolean hasNext() {
            return this.next != null;
         }

         public EnumConstantImpl<T, C, F, M> next() {
            EnumConstantImpl<T, C, F, M> r = this.next;
            this.next = this.next.next;
            return r;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }
}
