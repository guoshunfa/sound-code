package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

class MapPropertyInfoImpl<T, C, F, M> extends PropertyInfoImpl<T, C, F, M> implements MapPropertyInfo<T, C> {
   private final QName xmlName;
   private boolean nil;
   private final T keyType;
   private final T valueType;
   private NonElement<T, C> keyTypeInfo;
   private NonElement<T, C> valueTypeInfo;

   public MapPropertyInfoImpl(ClassInfoImpl<T, C, F, M> ci, PropertySeed<T, C, F, M> seed) {
      super(ci, seed);
      XmlElementWrapper xe = (XmlElementWrapper)seed.readAnnotation(XmlElementWrapper.class);
      this.xmlName = this.calcXmlName(xe);
      this.nil = xe != null && xe.nillable();
      T raw = this.getRawType();
      T bt = this.nav().getBaseClass(raw, this.nav().asDecl(Map.class));

      assert bt != null;

      if (this.nav().isParameterizedType(bt)) {
         this.keyType = this.nav().getTypeArgument(bt, 0);
         this.valueType = this.nav().getTypeArgument(bt, 1);
      } else {
         this.keyType = this.valueType = this.nav().ref(Object.class);
      }

   }

   public Collection<? extends TypeInfo<T, C>> ref() {
      return Arrays.asList(this.getKeyType(), this.getValueType());
   }

   public final PropertyKind kind() {
      return PropertyKind.MAP;
   }

   public QName getXmlName() {
      return this.xmlName;
   }

   public boolean isCollectionNillable() {
      return this.nil;
   }

   public NonElement<T, C> getKeyType() {
      if (this.keyTypeInfo == null) {
         this.keyTypeInfo = this.getTarget(this.keyType);
      }

      return this.keyTypeInfo;
   }

   public NonElement<T, C> getValueType() {
      if (this.valueTypeInfo == null) {
         this.valueTypeInfo = this.getTarget(this.valueType);
      }

      return this.valueTypeInfo;
   }

   public NonElement<T, C> getTarget(T type) {
      assert this.parent.builder != null : "this method must be called during the build stage";

      return this.parent.builder.getTypeInfo(type, this);
   }
}
