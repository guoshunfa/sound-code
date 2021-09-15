package com.sun.beans;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class TypeResolver {
   private static final WeakCache<Type, Map<Type, Type>> CACHE = new WeakCache();

   public static Type resolveInClass(Class<?> var0, Type var1) {
      return resolve(getActualType(var0), var1);
   }

   public static Type[] resolveInClass(Class<?> var0, Type[] var1) {
      return resolve(getActualType(var0), var1);
   }

   public static Type resolve(Type var0, Type var1) {
      if (var1 instanceof Class) {
         return var1;
      } else if (var1 instanceof GenericArrayType) {
         Type var9 = ((GenericArrayType)var1).getGenericComponentType();
         var9 = resolve(var0, var9);
         return (Type)(var9 instanceof Class ? Array.newInstance((Class)var9, 0).getClass() : GenericArrayTypeImpl.make(var9));
      } else {
         Type[] var10;
         if (var1 instanceof ParameterizedType) {
            ParameterizedType var8 = (ParameterizedType)var1;
            var10 = resolve(var0, var8.getActualTypeArguments());
            return ParameterizedTypeImpl.make((Class)var8.getRawType(), var10, var8.getOwnerType());
         } else if (var1 instanceof WildcardType) {
            WildcardType var7 = (WildcardType)var1;
            var10 = resolve(var0, var7.getUpperBounds());
            Type[] var4 = resolve(var0, var7.getLowerBounds());
            return new WildcardTypeImpl(var10, var4);
         } else if (var1 instanceof TypeVariable) {
            Object var2;
            synchronized(CACHE) {
               var2 = (Map)CACHE.get(var0);
               if (var2 == null) {
                  var2 = new HashMap();
                  prepare((Map)var2, var0);
                  CACHE.put(var0, var2);
               }
            }

            Type var3 = (Type)((Map)var2).get(var1);
            if (var3 != null && !var3.equals(var1)) {
               var3 = fixGenericArray(var3);
               return resolve(var0, var3);
            } else {
               return var1;
            }
         } else {
            throw new IllegalArgumentException("Bad Type kind: " + var1.getClass());
         }
      }
   }

   public static Type[] resolve(Type var0, Type[] var1) {
      int var2 = var1.length;
      Type[] var3 = new Type[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = resolve(var0, var1[var4]);
      }

      return var3;
   }

   public static Class<?> erase(Type var0) {
      if (var0 instanceof Class) {
         return (Class)var0;
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var5 = (ParameterizedType)var0;
         return (Class)var5.getRawType();
      } else {
         Type[] var2;
         if (var0 instanceof TypeVariable) {
            TypeVariable var4 = (TypeVariable)var0;
            var2 = var4.getBounds();
            return 0 < var2.length ? erase(var2[0]) : Object.class;
         } else if (var0 instanceof WildcardType) {
            WildcardType var3 = (WildcardType)var0;
            var2 = var3.getUpperBounds();
            return 0 < var2.length ? erase(var2[0]) : Object.class;
         } else if (var0 instanceof GenericArrayType) {
            GenericArrayType var1 = (GenericArrayType)var0;
            return Array.newInstance(erase(var1.getGenericComponentType()), 0).getClass();
         } else {
            throw new IllegalArgumentException("Unknown Type kind: " + var0.getClass());
         }
      }
   }

   public static Class[] erase(Type[] var0) {
      int var1 = var0.length;
      Class[] var2 = new Class[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = erase(var0[var3]);
      }

      return var2;
   }

   private static void prepare(Map<Type, Type> var0, Type var1) {
      Class var2 = (Class)((Class)(var1 instanceof Class ? var1 : ((ParameterizedType)var1).getRawType()));
      TypeVariable[] var3 = var2.getTypeParameters();
      Object var4 = var1 instanceof Class ? var3 : ((ParameterizedType)var1).getActualTypeArguments();

      assert var3.length == ((Object[])var4).length;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var0.put(var3[var5], ((Object[])var4)[var5]);
      }

      Type var10 = var2.getGenericSuperclass();
      if (var10 != null) {
         prepare(var0, var10);
      }

      Type[] var6 = var2.getGenericInterfaces();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Type var9 = var6[var8];
         prepare(var0, var9);
      }

      if (var1 instanceof Class && var3.length > 0) {
         Iterator var11 = var0.entrySet().iterator();

         while(var11.hasNext()) {
            Map.Entry var12 = (Map.Entry)var11.next();
            var12.setValue(erase((Type)var12.getValue()));
         }
      }

   }

   private static Type fixGenericArray(Type var0) {
      if (var0 instanceof GenericArrayType) {
         Type var1 = ((GenericArrayType)var0).getGenericComponentType();
         var1 = fixGenericArray(var1);
         if (var1 instanceof Class) {
            return Array.newInstance((Class)var1, 0).getClass();
         }
      }

      return var0;
   }

   private static Type getActualType(Class<?> var0) {
      TypeVariable[] var1 = var0.getTypeParameters();
      return (Type)(var1.length == 0 ? var0 : ParameterizedTypeImpl.make(var0, var1, var0.getEnclosingClass()));
   }
}
