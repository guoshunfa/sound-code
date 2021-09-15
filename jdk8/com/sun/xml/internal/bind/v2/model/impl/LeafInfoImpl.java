package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

abstract class LeafInfoImpl<TypeT, ClassDeclT> implements LeafInfo<TypeT, ClassDeclT>, Location {
   private final TypeT type;
   private final QName typeName;

   protected LeafInfoImpl(TypeT type, QName typeName) {
      assert type != null;

      this.type = type;
      this.typeName = typeName;
   }

   public TypeT getType() {
      return this.type;
   }

   /** @deprecated */
   public final boolean canBeReferencedByIDREF() {
      return false;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public Locatable getUpstream() {
      return null;
   }

   public Location getLocation() {
      return this;
   }

   public boolean isSimpleType() {
      return true;
   }

   public String toString() {
      return this.type.toString();
   }
}
