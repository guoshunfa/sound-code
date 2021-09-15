package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl implements WildcardType {
   private final Type[] ub;
   private final Type[] lb;

   public WildcardTypeImpl(Type[] ub, Type[] lb) {
      this.ub = ub;
      this.lb = lb;
   }

   public Type[] getUpperBounds() {
      return this.ub;
   }

   public Type[] getLowerBounds() {
      return this.lb;
   }

   public int hashCode() {
      return Arrays.hashCode((Object[])this.lb) ^ Arrays.hashCode((Object[])this.ub);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof WildcardType)) {
         return false;
      } else {
         WildcardType that = (WildcardType)obj;
         return Arrays.equals((Object[])that.getLowerBounds(), (Object[])this.lb) && Arrays.equals((Object[])that.getUpperBounds(), (Object[])this.ub);
      }
   }
}
