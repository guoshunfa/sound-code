package com.sun.xml.internal.bind.v2.model.nav;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;

final class ReflectionNavigator implements Navigator<Type, Class, Field, Method> {
   private static final ReflectionNavigator INSTANCE = new ReflectionNavigator();
   private static final TypeVisitor<Type, Class> baseClassFinder = new TypeVisitor<Type, Class>() {
      public Type onClass(Class c, Class sup) {
         if (sup == c) {
            return sup;
         } else {
            Type sc = c.getGenericSuperclass();
            Type r;
            if (sc != null) {
               r = (Type)this.visit(sc, sup);
               if (r != null) {
                  return r;
               }
            }

            Type[] var5 = c.getGenericInterfaces();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Type i = var5[var7];
               r = (Type)this.visit(i, sup);
               if (r != null) {
                  return r;
               }
            }

            return null;
         }
      }

      public Type onParameterizdType(ParameterizedType p, Class sup) {
         Class raw = (Class)p.getRawType();
         if (raw == sup) {
            return p;
         } else {
            Type r = raw.getGenericSuperclass();
            if (r != null) {
               r = (Type)this.visit(this.bind(r, raw, p), sup);
            }

            if (r != null) {
               return r;
            } else {
               Type[] var5 = raw.getGenericInterfaces();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  Type i = var5[var7];
                  r = (Type)this.visit(this.bind(i, raw, p), sup);
                  if (r != null) {
                     return r;
                  }
               }

               return null;
            }
         }
      }

      public Type onGenericArray(GenericArrayType g, Class sup) {
         return null;
      }

      public Type onVariable(TypeVariable v, Class sup) {
         return (Type)this.visit(v.getBounds()[0], sup);
      }

      public Type onWildcard(WildcardType w, Class sup) {
         return null;
      }

      private Type bind(Type t, GenericDeclaration decl, ParameterizedType args) {
         return (Type)ReflectionNavigator.binder.visit(t, new ReflectionNavigator.BinderArg(decl, args.getActualTypeArguments()));
      }
   };
   private static final TypeVisitor<Type, ReflectionNavigator.BinderArg> binder = new TypeVisitor<Type, ReflectionNavigator.BinderArg>() {
      public Type onClass(Class c, ReflectionNavigator.BinderArg args) {
         return c;
      }

      public Type onParameterizdType(ParameterizedType p, ReflectionNavigator.BinderArg args) {
         Type[] params = p.getActualTypeArguments();
         boolean different = false;

         for(int i = 0; i < params.length; ++i) {
            Type t = params[i];
            params[i] = (Type)this.visit(t, args);
            different |= t != params[i];
         }

         Type newOwner = p.getOwnerType();
         if (newOwner != null) {
            newOwner = (Type)this.visit(newOwner, args);
         }

         different |= p.getOwnerType() != newOwner;
         if (!different) {
            return p;
         } else {
            return new ParameterizedTypeImpl((Class)p.getRawType(), params, newOwner);
         }
      }

      public Type onGenericArray(GenericArrayType g, ReflectionNavigator.BinderArg types) {
         Type c = (Type)this.visit(g.getGenericComponentType(), types);
         return (Type)(c == g.getGenericComponentType() ? g : new GenericArrayTypeImpl(c));
      }

      public Type onVariable(TypeVariable v, ReflectionNavigator.BinderArg types) {
         return types.replace(v);
      }

      public Type onWildcard(WildcardType w, ReflectionNavigator.BinderArg types) {
         Type[] lb = w.getLowerBounds();
         Type[] ub = w.getUpperBounds();
         boolean diff = false;

         int i;
         Type t;
         for(i = 0; i < lb.length; ++i) {
            t = lb[i];
            lb[i] = (Type)this.visit(t, types);
            diff |= t != lb[i];
         }

         for(i = 0; i < ub.length; ++i) {
            t = ub[i];
            ub[i] = (Type)this.visit(t, types);
            diff |= t != ub[i];
         }

         if (!diff) {
            return w;
         } else {
            return new WildcardTypeImpl(lb, ub);
         }
      }
   };
   private static final TypeVisitor<Class, Void> eraser = new TypeVisitor<Class, Void>() {
      public Class onClass(Class c, Void v) {
         return c;
      }

      public Class onParameterizdType(ParameterizedType p, Void v) {
         return (Class)this.visit(p.getRawType(), (Object)null);
      }

      public Class onGenericArray(GenericArrayType g, Void v) {
         return Array.newInstance((Class)this.visit(g.getGenericComponentType(), (Object)null), 0).getClass();
      }

      public Class onVariable(TypeVariable tv, Void v) {
         return (Class)this.visit(tv.getBounds()[0], (Object)null);
      }

      public Class onWildcard(WildcardType w, Void v) {
         return (Class)this.visit(w.getUpperBounds()[0], (Object)null);
      }
   };

   static ReflectionNavigator getInstance() {
      return INSTANCE;
   }

   private ReflectionNavigator() {
   }

   public Class getSuperClass(Class clazz) {
      if (clazz == Object.class) {
         return null;
      } else {
         Class sc = clazz.getSuperclass();
         if (sc == null) {
            sc = Object.class;
         }

         return sc;
      }
   }

   public Type getBaseClass(Type t, Class sup) {
      return (Type)baseClassFinder.visit(t, sup);
   }

   public String getClassName(Class clazz) {
      return clazz.getName();
   }

   public String getTypeName(Type type) {
      if (type instanceof Class) {
         Class c = (Class)type;
         return c.isArray() ? this.getTypeName((Type)c.getComponentType()) + "[]" : c.getName();
      } else {
         return type.toString();
      }
   }

   public String getClassShortName(Class clazz) {
      return clazz.getSimpleName();
   }

   public Collection<? extends Field> getDeclaredFields(Class clazz) {
      return Arrays.asList(clazz.getDeclaredFields());
   }

   public Field getDeclaredField(Class clazz, String fieldName) {
      try {
         return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException var4) {
         return null;
      }
   }

   public Collection<? extends Method> getDeclaredMethods(Class clazz) {
      return Arrays.asList(clazz.getDeclaredMethods());
   }

   public Class getDeclaringClassForField(Field field) {
      return field.getDeclaringClass();
   }

   public Class getDeclaringClassForMethod(Method method) {
      return method.getDeclaringClass();
   }

   public Type getFieldType(Field field) {
      if (field.getType().isArray()) {
         Class c = field.getType().getComponentType();
         if (c.isPrimitive()) {
            return Array.newInstance(c, 0).getClass();
         }
      }

      return this.fix(field.getGenericType());
   }

   public String getFieldName(Field field) {
      return field.getName();
   }

   public String getMethodName(Method method) {
      return method.getName();
   }

   public Type getReturnType(Method method) {
      return this.fix(method.getGenericReturnType());
   }

   public Type[] getMethodParameters(Method method) {
      return method.getGenericParameterTypes();
   }

   public boolean isStaticMethod(Method method) {
      return Modifier.isStatic(method.getModifiers());
   }

   public boolean isFinalMethod(Method method) {
      return Modifier.isFinal(method.getModifiers());
   }

   public boolean isSubClassOf(Type sub, Type sup) {
      return this.erasure(sup).isAssignableFrom(this.erasure(sub));
   }

   public Class ref(Class c) {
      return c;
   }

   public Class use(Class c) {
      return c;
   }

   public Class asDecl(Type t) {
      return this.erasure(t);
   }

   public Class asDecl(Class c) {
      return c;
   }

   public <T> Class<T> erasure(Type t) {
      return (Class)eraser.visit(t, (Object)null);
   }

   public boolean isAbstract(Class clazz) {
      return Modifier.isAbstract(clazz.getModifiers());
   }

   public boolean isFinal(Class clazz) {
      return Modifier.isFinal(clazz.getModifiers());
   }

   public Type createParameterizedType(Class rawType, Type... arguments) {
      return new ParameterizedTypeImpl(rawType, arguments, (Type)null);
   }

   public boolean isArray(Type t) {
      if (t instanceof Class) {
         Class c = (Class)t;
         return c.isArray();
      } else {
         return t instanceof GenericArrayType;
      }
   }

   public boolean isArrayButNotByteArray(Type t) {
      if (!(t instanceof Class)) {
         if (t instanceof GenericArrayType) {
            t = ((GenericArrayType)t).getGenericComponentType();
            return t != Byte.TYPE;
         } else {
            return false;
         }
      } else {
         Class c = (Class)t;
         return c.isArray() && c != byte[].class;
      }
   }

   public Type getComponentType(Type t) {
      if (t instanceof Class) {
         Class c = (Class)t;
         return c.getComponentType();
      } else if (t instanceof GenericArrayType) {
         return ((GenericArrayType)t).getGenericComponentType();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Type getTypeArgument(Type type, int i) {
      if (type instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)type;
         return this.fix(p.getActualTypeArguments()[i]);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean isParameterizedType(Type type) {
      return type instanceof ParameterizedType;
   }

   public boolean isPrimitive(Type type) {
      if (type instanceof Class) {
         Class c = (Class)type;
         return c.isPrimitive();
      } else {
         return false;
      }
   }

   public Type getPrimitive(Class primitiveType) {
      assert primitiveType.isPrimitive();

      return primitiveType;
   }

   public Location getClassLocation(final Class clazz) {
      return new Location() {
         public String toString() {
            return clazz.getName();
         }
      };
   }

   public Location getFieldLocation(final Field field) {
      return new Location() {
         public String toString() {
            return field.toString();
         }
      };
   }

   public Location getMethodLocation(final Method method) {
      return new Location() {
         public String toString() {
            return method.toString();
         }
      };
   }

   public boolean hasDefaultConstructor(Class c) {
      try {
         c.getDeclaredConstructor();
         return true;
      } catch (NoSuchMethodException var3) {
         return false;
      }
   }

   public boolean isStaticField(Field field) {
      return Modifier.isStatic(field.getModifiers());
   }

   public boolean isPublicMethod(Method method) {
      return Modifier.isPublic(method.getModifiers());
   }

   public boolean isPublicField(Field field) {
      return Modifier.isPublic(field.getModifiers());
   }

   public boolean isEnum(Class c) {
      return Enum.class.isAssignableFrom(c);
   }

   public Field[] getEnumConstants(Class clazz) {
      try {
         Object[] values = clazz.getEnumConstants();
         Field[] fields = new Field[values.length];

         for(int i = 0; i < values.length; ++i) {
            fields[i] = clazz.getField(((Enum)values[i]).name());
         }

         return fields;
      } catch (NoSuchFieldException var5) {
         throw new NoSuchFieldError(var5.getMessage());
      }
   }

   public Type getVoidType() {
      return Void.class;
   }

   public String getPackageName(Class clazz) {
      String name = clazz.getName();
      int idx = name.lastIndexOf(46);
      return idx < 0 ? "" : name.substring(0, idx);
   }

   public Class loadObjectFactory(Class referencePoint, String pkg) {
      ClassLoader cl = SecureLoader.getClassClassLoader(referencePoint);
      if (cl == null) {
         cl = SecureLoader.getSystemClassLoader();
      }

      try {
         return cl.loadClass(pkg + ".ObjectFactory");
      } catch (ClassNotFoundException var5) {
         return null;
      }
   }

   public boolean isBridgeMethod(Method method) {
      return method.isBridge();
   }

   public boolean isOverriding(Method method, Class base) {
      String name = method.getName();

      for(Class[] params = method.getParameterTypes(); base != null; base = base.getSuperclass()) {
         try {
            if (base.getDeclaredMethod(name, params) != null) {
               return true;
            }
         } catch (NoSuchMethodException var6) {
         }
      }

      return false;
   }

   public boolean isInterface(Class clazz) {
      return clazz.isInterface();
   }

   public boolean isTransient(Field f) {
      return Modifier.isTransient(f.getModifiers());
   }

   public boolean isInnerClass(Class clazz) {
      return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers());
   }

   public boolean isSameType(Type t1, Type t2) {
      return t1.equals(t2);
   }

   private Type fix(Type t) {
      if (!(t instanceof GenericArrayType)) {
         return t;
      } else {
         GenericArrayType gat = (GenericArrayType)t;
         if (gat.getGenericComponentType() instanceof Class) {
            Class c = (Class)gat.getGenericComponentType();
            return Array.newInstance(c, 0).getClass();
         } else {
            return t;
         }
      }
   }

   private static class BinderArg {
      final TypeVariable[] params;
      final Type[] args;

      BinderArg(TypeVariable[] params, Type[] args) {
         this.params = params;
         this.args = args;

         assert params.length == args.length;

      }

      public BinderArg(GenericDeclaration decl, Type[] args) {
         this(decl.getTypeParameters(), args);
      }

      Type replace(TypeVariable v) {
         for(int i = 0; i < this.params.length; ++i) {
            if (this.params[i].equals(v)) {
               return this.args[i];
            }
         }

         return v;
      }
   }
}
