package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

abstract class TypeVisitor<T, P> {
   public final T visit(Type t, P param) {
      assert t != null;

      if (t instanceof Class) {
         return this.onClass((Class)t, param);
      } else if (t instanceof ParameterizedType) {
         return this.onParameterizdType((ParameterizedType)t, param);
      } else if (t instanceof GenericArrayType) {
         return this.onGenericArray((GenericArrayType)t, param);
      } else if (t instanceof WildcardType) {
         return this.onWildcard((WildcardType)t, param);
      } else if (t instanceof TypeVariable) {
         return this.onVariable((TypeVariable)t, param);
      } else {
         assert false;

         throw new IllegalArgumentException();
      }
   }

   protected abstract T onClass(Class var1, P var2);

   protected abstract T onParameterizdType(ParameterizedType var1, P var2);

   protected abstract T onGenericArray(GenericArrayType var1, P var2);

   protected abstract T onVariable(TypeVariable var1, P var2);

   protected abstract T onWildcard(WildcardType var1, P var2);
}
