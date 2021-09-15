package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

class ParameterizedTypeImpl implements ParameterizedType {
   private Type[] actualTypeArguments;
   private Class<?> rawType;
   private Type ownerType;

   ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
      this.actualTypeArguments = actualTypeArguments;
      this.rawType = rawType;
      if (ownerType != null) {
         this.ownerType = ownerType;
      } else {
         this.ownerType = rawType.getDeclaringClass();
      }

      this.validateConstructorArguments();
   }

   private void validateConstructorArguments() {
      TypeVariable[] formals = this.rawType.getTypeParameters();
      if (formals.length != this.actualTypeArguments.length) {
         throw new MalformedParameterizedTypeException();
      }
   }

   public Type[] getActualTypeArguments() {
      return (Type[])this.actualTypeArguments.clone();
   }

   public Class<?> getRawType() {
      return this.rawType;
   }

   public Type getOwnerType() {
      return this.ownerType;
   }

   public boolean equals(Object o) {
      if (o instanceof ParameterizedType) {
         ParameterizedType that = (ParameterizedType)o;
         if (this == that) {
            return true;
         } else {
            boolean var10000;
            label39: {
               Type thatOwner = that.getOwnerType();
               Type thatRawType = that.getRawType();
               if (this.ownerType == null) {
                  if (thatOwner != null) {
                     break label39;
                  }
               } else if (!this.ownerType.equals(thatOwner)) {
                  break label39;
               }

               if (this.rawType == null) {
                  if (thatRawType != null) {
                     break label39;
                  }
               } else if (!this.rawType.equals(thatRawType)) {
                  break label39;
               }

               if (Arrays.equals((Object[])this.actualTypeArguments, (Object[])that.getActualTypeArguments())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode((Object[])this.actualTypeArguments) ^ (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ (this.rawType == null ? 0 : this.rawType.hashCode());
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      if (this.ownerType != null) {
         if (this.ownerType instanceof Class) {
            sb.append(((Class)this.ownerType).getName());
         } else {
            sb.append(this.ownerType.toString());
         }

         sb.append(".");
         if (this.ownerType instanceof ParameterizedTypeImpl) {
            sb.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
         } else {
            sb.append(this.rawType.getName());
         }
      } else {
         sb.append(this.rawType.getName());
      }

      if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
         sb.append("<");
         boolean first = true;
         Type[] var3 = this.actualTypeArguments;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Type t = var3[var5];
            if (!first) {
               sb.append(", ");
            }

            if (t instanceof Class) {
               sb.append(((Class)t).getName());
            } else {
               sb.append(t.toString());
            }

            first = false;
         }

         sb.append(">");
      }

      return sb.toString();
   }
}
