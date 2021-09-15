package com.sun.xml.internal.ws.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

final class FieldSignature {
   static String vms(Type t) {
      if (t instanceof Class && ((Class)t).isPrimitive()) {
         Class c = (Class)t;
         if (c == Integer.TYPE) {
            return "I";
         }

         if (c == Void.TYPE) {
            return "V";
         }

         if (c == Boolean.TYPE) {
            return "Z";
         }

         if (c == Byte.TYPE) {
            return "B";
         }

         if (c == Character.TYPE) {
            return "C";
         }

         if (c == Short.TYPE) {
            return "S";
         }

         if (c == Double.TYPE) {
            return "D";
         }

         if (c == Float.TYPE) {
            return "F";
         }

         if (c == Long.TYPE) {
            return "J";
         }
      } else {
         if (t instanceof Class && ((Class)t).isArray()) {
            return "[" + vms(((Class)t).getComponentType());
         }

         if (t instanceof Class || t instanceof ParameterizedType) {
            return "L" + fqcn(t) + ";";
         }

         if (t instanceof GenericArrayType) {
            return "[" + vms(((GenericArrayType)t).getGenericComponentType());
         }

         if (t instanceof TypeVariable) {
            return "Ljava/lang/Object;";
         }

         if (t instanceof WildcardType) {
            WildcardType w = (WildcardType)t;
            if (w.getLowerBounds().length > 0) {
               return "-" + vms(w.getLowerBounds()[0]);
            }

            if (w.getUpperBounds().length > 0) {
               Type wt = w.getUpperBounds()[0];
               if (wt.equals(Object.class)) {
                  return "*";
               }

               return "+" + vms(wt);
            }
         }
      }

      throw new IllegalArgumentException("Illegal vms arg " + t);
   }

   private static String fqcn(Type t) {
      if (t instanceof Class) {
         Class c = (Class)t;
         return c.getDeclaringClass() == null ? c.getName().replace('.', '/') : fqcn(c.getDeclaringClass()) + "$" + c.getSimpleName();
      } else if (t instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)t;
         if (p.getOwnerType() == null) {
            return fqcn(p.getRawType()) + args(p);
         } else {
            assert p.getRawType() instanceof Class;

            return fqcn(p.getOwnerType()) + "." + ((Class)p.getRawType()).getSimpleName() + args(p);
         }
      } else {
         throw new IllegalArgumentException("Illegal fqcn arg = " + t);
      }
   }

   private static String args(ParameterizedType p) {
      StringBuilder sig = new StringBuilder("<");
      Type[] var2 = p.getActualTypeArguments();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Type t = var2[var4];
         sig.append(vms(t));
      }

      return sig.append(">").toString();
   }
}
