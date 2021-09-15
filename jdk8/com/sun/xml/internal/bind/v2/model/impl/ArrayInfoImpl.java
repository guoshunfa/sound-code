package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

public class ArrayInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends TypeInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ArrayInfo<TypeT, ClassDeclT>, Location {
   private final NonElement<TypeT, ClassDeclT> itemType;
   private final QName typeName;
   private final TypeT arrayType;

   public ArrayInfoImpl(ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> builder, Locatable upstream, TypeT arrayType) {
      super(builder, upstream);
      this.arrayType = arrayType;
      TypeT componentType = this.nav().getComponentType(arrayType);
      this.itemType = builder.getTypeInfo(componentType, this);
      QName n = this.itemType.getTypeName();
      if (n == null) {
         builder.reportError(new IllegalAnnotationException(Messages.ANONYMOUS_ARRAY_ITEM.format(this.nav().getTypeName(componentType)), this));
         n = new QName("#dummy");
      }

      this.typeName = ArrayInfoUtil.calcArrayTypeName(n);
   }

   public NonElement<TypeT, ClassDeclT> getItemType() {
      return this.itemType;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public boolean isSimpleType() {
      return false;
   }

   public TypeT getType() {
      return this.arrayType;
   }

   /** @deprecated */
   public final boolean canBeReferencedByIDREF() {
      return false;
   }

   public Location getLocation() {
      return this;
   }

   public String toString() {
      return this.nav().getTypeName(this.arrayType);
   }
}
