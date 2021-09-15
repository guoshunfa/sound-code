package com.sun.beans;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl implements WildcardType {
   private final Type[] upperBounds;
   private final Type[] lowerBounds;

   WildcardTypeImpl(Type[] var1, Type[] var2) {
      this.upperBounds = var1;
      this.lowerBounds = var2;
   }

   public Type[] getUpperBounds() {
      return (Type[])this.upperBounds.clone();
   }

   public Type[] getLowerBounds() {
      return (Type[])this.lowerBounds.clone();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof WildcardType)) {
         return false;
      } else {
         WildcardType var2 = (WildcardType)var1;
         return Arrays.equals((Object[])this.upperBounds, (Object[])var2.getUpperBounds()) && Arrays.equals((Object[])this.lowerBounds, (Object[])var2.getLowerBounds());
      }
   }

   public int hashCode() {
      return Arrays.hashCode((Object[])this.upperBounds) ^ Arrays.hashCode((Object[])this.lowerBounds);
   }

   public String toString() {
      StringBuilder var1;
      Type[] var2;
      if (this.lowerBounds.length == 0) {
         if (this.upperBounds.length == 0 || Object.class == this.upperBounds[0]) {
            return "?";
         }

         var2 = this.upperBounds;
         var1 = new StringBuilder("? extends ");
      } else {
         var2 = this.lowerBounds;
         var1 = new StringBuilder("? super ");
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 > 0) {
            var1.append(" & ");
         }

         var1.append(var2[var3] instanceof Class ? ((Class)var2[var3]).getName() : var2[var3].toString());
      }

      return var1.toString();
   }
}
