package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.io.InvalidObjectException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataInvocationHandler;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultMXBeanMappingFactory extends MXBeanMappingFactory {
   private static final DefaultMXBeanMappingFactory.Mappings mappings = new DefaultMXBeanMappingFactory.Mappings();
   private static final List<MXBeanMapping> permanentMappings = Util.newList();
   private static final String[] keyArray;
   private static final String[] keyValueArray;
   private static final Map<Type, Type> inProgress;

   static boolean isIdentity(MXBeanMapping var0) {
      return var0 instanceof DefaultMXBeanMappingFactory.NonNullMXBeanMapping && ((DefaultMXBeanMappingFactory.NonNullMXBeanMapping)var0).isIdentity();
   }

   private static synchronized MXBeanMapping getMapping(Type var0) {
      WeakReference var1 = (WeakReference)mappings.get(var0);
      return var1 == null ? null : (MXBeanMapping)var1.get();
   }

   private static synchronized void putMapping(Type var0, MXBeanMapping var1) {
      WeakReference var2 = new WeakReference(var1);
      mappings.put(var0, var2);
   }

   private static synchronized void putPermanentMapping(Type var0, MXBeanMapping var1) {
      putMapping(var0, var1);
      permanentMappings.add(var1);
   }

   public synchronized MXBeanMapping mappingForType(Type var1, MXBeanMappingFactory var2) throws OpenDataException {
      if (inProgress.containsKey(var1)) {
         throw new OpenDataException("Recursive data structure, including " + MXBeanIntrospector.typeName(var1));
      } else {
         MXBeanMapping var3 = getMapping(var1);
         if (var3 != null) {
            return var3;
         } else {
            inProgress.put(var1, var1);

            try {
               var3 = this.makeMapping(var1, var2);
            } catch (OpenDataException var8) {
               throw openDataException("Cannot convert type: " + MXBeanIntrospector.typeName(var1), var8);
            } finally {
               inProgress.remove(var1);
            }

            putMapping(var1, var3);
            return var3;
         }
      }
   }

   private MXBeanMapping makeMapping(Type var1, MXBeanMappingFactory var2) throws OpenDataException {
      if (var1 instanceof GenericArrayType) {
         Type var5 = ((GenericArrayType)var1).getGenericComponentType();
         return this.makeArrayOrCollectionMapping(var1, var5, var2);
      } else if (var1 instanceof Class) {
         Class var3 = (Class)var1;
         if (var3.isEnum()) {
            return makeEnumMapping(var3, ElementType.class);
         } else if (var3.isArray()) {
            Class var4 = var3.getComponentType();
            return this.makeArrayOrCollectionMapping(var3, var4, var2);
         } else {
            return JMX.isMXBeanInterface(var3) ? makeMXBeanRefMapping(var3) : this.makeCompositeMapping(var3, var2);
         }
      } else if (var1 instanceof ParameterizedType) {
         return this.makeParameterizedTypeMapping((ParameterizedType)var1, var2);
      } else {
         throw new OpenDataException("Cannot map type: " + var1);
      }
   }

   private static <T extends Enum<T>> MXBeanMapping makeEnumMapping(Class<?> var0, Class<T> var1) {
      ReflectUtil.checkPackageAccess(var0);
      return new DefaultMXBeanMappingFactory.EnumMapping((Class)Util.cast(var0));
   }

   private MXBeanMapping makeArrayOrCollectionMapping(Type var1, Type var2, MXBeanMappingFactory var3) throws OpenDataException {
      MXBeanMapping var4 = var3.mappingForType(var2, var3);
      OpenType var5 = var4.getOpenType();
      ArrayType var6 = ArrayType.getArrayType(var5);
      Class var7 = var4.getOpenClass();
      String var9;
      if (var7.isArray()) {
         var9 = "[" + var7.getName();
      } else {
         var9 = "[L" + var7.getName() + ";";
      }

      Class var8;
      try {
         var8 = Class.forName(var9);
      } catch (ClassNotFoundException var11) {
         throw openDataException("Cannot obtain array class", var11);
      }

      if (var1 instanceof ParameterizedType) {
         return new DefaultMXBeanMappingFactory.CollectionMapping(var1, var6, var8, var4);
      } else {
         return (MXBeanMapping)(isIdentity(var4) ? new DefaultMXBeanMappingFactory.IdentityMapping(var1, var6) : new DefaultMXBeanMappingFactory.ArrayMapping(var1, var6, var8, var4));
      }
   }

   private MXBeanMapping makeTabularMapping(Type var1, boolean var2, Type var3, Type var4, MXBeanMappingFactory var5) throws OpenDataException {
      String var6 = MXBeanIntrospector.typeName(var1);
      MXBeanMapping var7 = var5.mappingForType(var3, var5);
      MXBeanMapping var8 = var5.mappingForType(var4, var5);
      OpenType var9 = var7.getOpenType();
      OpenType var10 = var8.getOpenType();
      CompositeType var11 = new CompositeType(var6, var6, keyValueArray, keyValueArray, new OpenType[]{var9, var10});
      TabularType var12 = new TabularType(var6, var6, var11, keyArray);
      return new DefaultMXBeanMappingFactory.TabularMapping(var1, var2, var12, var7, var8);
   }

   private MXBeanMapping makeParameterizedTypeMapping(ParameterizedType var1, MXBeanMappingFactory var2) throws OpenDataException {
      Type var3 = var1.getRawType();
      if (var3 instanceof Class) {
         Class var4 = (Class)var3;
         if (var4 == List.class || var4 == Set.class || var4 == SortedSet.class) {
            Type[] var7 = var1.getActualTypeArguments();

            assert var7.length == 1;

            if (var4 == SortedSet.class) {
               mustBeComparable(var4, var7[0]);
            }

            return this.makeArrayOrCollectionMapping(var1, var7[0], var2);
         }

         boolean var5 = var4 == SortedMap.class;
         if (var4 == Map.class || var5) {
            Type[] var6 = var1.getActualTypeArguments();

            assert var6.length == 2;

            if (var5) {
               mustBeComparable(var4, var6[0]);
            }

            return this.makeTabularMapping(var1, var5, var6[0], var6[1], var2);
         }
      }

      throw new OpenDataException("Cannot convert type: " + var1);
   }

   private static MXBeanMapping makeMXBeanRefMapping(Type var0) throws OpenDataException {
      return new DefaultMXBeanMappingFactory.MXBeanRefMapping(var0);
   }

   private MXBeanMapping makeCompositeMapping(Class<?> var1, MXBeanMappingFactory var2) throws OpenDataException {
      boolean var3 = var1.getName().equals("com.sun.management.GcInfo") && var1.getClassLoader() == null;
      ReflectUtil.checkPackageAccess(var1);
      List var4 = MBeanAnalyzer.eliminateCovariantMethods(Arrays.asList(var1.getMethods()));
      SortedMap var5 = Util.newSortedMap();
      Iterator var6 = var4.iterator();

      Method var7;
      Method var9;
      do {
         String var8;
         do {
            do {
               if (!var6.hasNext()) {
                  int var15 = var5.size();
                  if (var15 == 0) {
                     throw new OpenDataException("Can't map " + var1.getName() + " to an open data type");
                  }

                  Method[] var16 = new Method[var15];
                  String[] var17 = new String[var15];
                  OpenType[] var18 = new OpenType[var15];
                  int var19 = 0;

                  for(Iterator var11 = var5.entrySet().iterator(); var11.hasNext(); ++var19) {
                     Map.Entry var12 = (Map.Entry)var11.next();
                     var17[var19] = (String)var12.getKey();
                     Method var13 = (Method)var12.getValue();
                     var16[var19] = var13;
                     Type var14 = var13.getGenericReturnType();
                     var18[var19] = var2.mappingForType(var14, var2).getOpenType();
                  }

                  CompositeType var20 = new CompositeType(var1.getName(), var1.getName(), var17, var17, var18);
                  return new DefaultMXBeanMappingFactory.CompositeMapping(var1, var20, var17, var16, var2);
               }

               var7 = (Method)var6.next();
               var8 = propertyName(var7);
            } while(var8 == null);
         } while(var3 && var8.equals("CompositeType"));

         var9 = (Method)var5.put(decapitalize(var8), var7);
      } while(var9 == null);

      String var10 = "Class " + var1.getName() + " has method name clash: " + var9.getName() + ", " + var7.getName();
      throw new OpenDataException(var10);
   }

   static InvalidObjectException invalidObjectException(String var0, Throwable var1) {
      return (InvalidObjectException)EnvHelp.initCause(new InvalidObjectException(var0), var1);
   }

   static InvalidObjectException invalidObjectException(Throwable var0) {
      return invalidObjectException(var0.getMessage(), var0);
   }

   static OpenDataException openDataException(String var0, Throwable var1) {
      return (OpenDataException)EnvHelp.initCause(new OpenDataException(var0), var1);
   }

   static OpenDataException openDataException(Throwable var0) {
      return openDataException(var0.getMessage(), var0);
   }

   static void mustBeComparable(Class<?> var0, Type var1) throws OpenDataException {
      if (!(var1 instanceof Class) || !Comparable.class.isAssignableFrom((Class)var1)) {
         String var2 = "Parameter class " + var1 + " of " + var0.getName() + " does not implement " + Comparable.class.getName();
         throw new OpenDataException(var2);
      }
   }

   public static String decapitalize(String var0) {
      if (var0 != null && var0.length() != 0) {
         int var1 = Character.offsetByCodePoints(var0, 0, 1);
         return var1 < var0.length() && Character.isUpperCase(var0.codePointAt(var1)) ? var0 : var0.substring(0, var1).toLowerCase() + var0.substring(var1);
      } else {
         return var0;
      }
   }

   static String capitalize(String var0) {
      if (var0 != null && var0.length() != 0) {
         int var1 = var0.offsetByCodePoints(0, 1);
         return var0.substring(0, var1).toUpperCase() + var0.substring(var1);
      } else {
         return var0;
      }
   }

   public static String propertyName(Method var0) {
      String var1 = null;
      String var2 = var0.getName();
      if (var2.startsWith("get")) {
         var1 = var2.substring(3);
      } else if (var2.startsWith("is") && var0.getReturnType() == Boolean.TYPE) {
         var1 = var2.substring(2);
      }

      return var1 != null && var1.length() != 0 && var0.getParameterTypes().length <= 0 && var0.getReturnType() != Void.TYPE && !var2.equals("getClass") ? var1 : null;
   }

   static {
      OpenType[] var0 = new OpenType[]{SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID};

      for(int var1 = 0; var1 < var0.length; ++var1) {
         OpenType var2 = var0[var1];

         Class var3;
         try {
            var3 = Class.forName(var2.getClassName(), false, ObjectName.class.getClassLoader());
         } catch (ClassNotFoundException var11) {
            throw new Error(var11);
         }

         DefaultMXBeanMappingFactory.IdentityMapping var4 = new DefaultMXBeanMappingFactory.IdentityMapping(var3, var2);
         putPermanentMapping(var3, var4);
         if (var3.getName().startsWith("java.lang.")) {
            try {
               Field var5 = var3.getField("TYPE");
               Class var6 = (Class)var5.get((Object)null);
               DefaultMXBeanMappingFactory.IdentityMapping var7 = new DefaultMXBeanMappingFactory.IdentityMapping(var6, var2);
               putPermanentMapping(var6, var7);
               if (var6 != Void.TYPE) {
                  Class var8 = Array.newInstance(var6, 0).getClass();
                  ArrayType var9 = ArrayType.getPrimitiveArrayType(var8);
                  DefaultMXBeanMappingFactory.IdentityMapping var10 = new DefaultMXBeanMappingFactory.IdentityMapping(var8, var9);
                  putPermanentMapping(var8, var10);
               }
            } catch (NoSuchFieldException var12) {
            } catch (IllegalAccessException var13) {
               assert false;
            }
         }
      }

      keyArray = new String[]{"key"};
      keyValueArray = new String[]{"key", "value"};
      inProgress = Util.newIdentityHashMap();
   }

   private static final class CompositeBuilderViaProxy extends DefaultMXBeanMappingFactory.CompositeBuilder {
      CompositeBuilderViaProxy(Class<?> var1, String[] var2) {
         super(var1, var2);
      }

      String applicable(Method[] var1) {
         Class var2 = this.getTargetClass();
         if (!var2.isInterface()) {
            return "not an interface";
         } else {
            Set var3 = Util.newSet(Arrays.asList(var2.getMethods()));
            var3.removeAll(Arrays.asList(var1));
            String var4 = null;
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Method var6 = (Method)var5.next();
               String var7 = var6.getName();
               Class[] var8 = var6.getParameterTypes();

               try {
                  Method var9 = Object.class.getMethod(var7, var8);
                  if (!Modifier.isPublic(var9.getModifiers())) {
                     var4 = var7;
                  }
               } catch (NoSuchMethodException var10) {
                  var4 = var7;
               }
            }

            return var4 != null ? "contains methods other than getters (" + var4 + ")" : null;
         }
      }

      final Object fromCompositeData(CompositeData var1, String[] var2, MXBeanMapping[] var3) {
         Class var4 = this.getTargetClass();
         return Proxy.newProxyInstance(var4.getClassLoader(), new Class[]{var4}, new CompositeDataInvocationHandler(var1));
      }
   }

   private static final class CompositeBuilderViaConstructor extends DefaultMXBeanMappingFactory.CompositeBuilder {
      private List<DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.Constr> annotatedConstructors;

      CompositeBuilderViaConstructor(Class<?> var1, String[] var2) {
         super(var1, var2);
      }

      String applicable(Method[] var1) throws InvalidObjectException {
         if (!DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.AnnotationHelper.isAvailable()) {
            return "@ConstructorProperties annotation not available";
         } else {
            Class var2 = this.getTargetClass();
            Constructor[] var3 = var2.getConstructors();
            List var4 = Util.newList();
            Constructor[] var5 = var3;
            int var6 = var3.length;

            int var7;
            for(var7 = 0; var7 < var6; ++var7) {
               Constructor var8 = var5[var7];
               if (Modifier.isPublic(var8.getModifiers()) && DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.AnnotationHelper.getPropertyNames(var8) != null) {
                  var4.add(var8);
               }
            }

            if (var4.isEmpty()) {
               return "no constructor has @ConstructorProperties annotation";
            } else {
               this.annotatedConstructors = Util.newList();
               Map var20 = Util.newMap();
               String[] var21 = this.getItemNames();

               for(var7 = 0; var7 < var21.length; ++var7) {
                  var20.put(var21[var7], var7);
               }

               Set var22 = Util.newSet();
               Iterator var23 = var4.iterator();

               String var15;
               BitSet var29;
               while(var23.hasNext()) {
                  Constructor var9 = (Constructor)var23.next();
                  String[] var10 = DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.AnnotationHelper.getPropertyNames(var9);
                  Type[] var11 = var9.getGenericParameterTypes();
                  if (var11.length != var10.length) {
                     String var27 = "Number of constructor params does not match @ConstructorProperties annotation: " + var9;
                     throw new InvalidObjectException(var27);
                  }

                  int[] var12 = new int[var1.length];

                  for(int var13 = 0; var13 < var1.length; ++var13) {
                     var12[var13] = -1;
                  }

                  var29 = new BitSet();

                  for(int var14 = 0; var14 < var10.length; ++var14) {
                     var15 = var10[var14];
                     if (!var20.containsKey(var15)) {
                        String var34 = "@ConstructorProperties includes name " + var15 + " which does not correspond to a property";
                        Iterator var36 = var20.keySet().iterator();

                        while(var36.hasNext()) {
                           String var37 = (String)var36.next();
                           if (var37.equalsIgnoreCase(var15)) {
                              var34 = var34 + " (differs only in case from property " + var37 + ")";
                           }
                        }

                        var34 = var34 + ": " + var9;
                        throw new InvalidObjectException(var34);
                     }

                     int var16 = (Integer)var20.get(var15);
                     var12[var16] = var14;
                     if (var29.get(var16)) {
                        String var35 = "@ConstructorProperties contains property " + var15 + " more than once: " + var9;
                        throw new InvalidObjectException(var35);
                     }

                     var29.set(var16);
                     Method var17 = var1[var16];
                     Type var18 = var17.getGenericReturnType();
                     if (!var18.equals(var11[var14])) {
                        String var19 = "@ConstructorProperties gives property " + var15 + " of type " + var18 + " for parameter  of type " + var11[var14] + ": " + var9;
                        throw new InvalidObjectException(var19);
                     }
                  }

                  if (!var22.add(var29)) {
                     String var31 = "More than one constructor has a @ConstructorProperties annotation with this set of names: " + Arrays.toString((Object[])var10);
                     throw new InvalidObjectException(var31);
                  }

                  DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.Constr var30 = new DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.Constr(var9, var12, var29);
                  this.annotatedConstructors.add(var30);
               }

               var23 = var22.iterator();

               while(var23.hasNext()) {
                  BitSet var24 = (BitSet)var23.next();
                  boolean var25 = false;
                  Iterator var26 = var22.iterator();

                  while(var26.hasNext()) {
                     BitSet var28 = (BitSet)var26.next();
                     if (var24 == var28) {
                        var25 = true;
                     } else if (var25) {
                        var29 = new BitSet();
                        var29.or(var24);
                        var29.or(var28);
                        if (!var22.contains(var29)) {
                           TreeSet var32 = new TreeSet();

                           for(int var33 = var29.nextSetBit(0); var33 >= 0; var33 = var29.nextSetBit(var33 + 1)) {
                              var32.add(var21[var33]);
                           }

                           var15 = "Constructors with @ConstructorProperties annotation  would be ambiguous for these items: " + var32;
                           throw new InvalidObjectException(var15);
                        }
                     }
                  }
               }

               return null;
            }
         }
      }

      final Object fromCompositeData(CompositeData var1, String[] var2, MXBeanMapping[] var3) throws InvalidObjectException {
         CompositeType var4 = var1.getCompositeType();
         BitSet var5 = new BitSet();

         for(int var6 = 0; var6 < var2.length; ++var6) {
            if (var4.getType(var2[var6]) != null) {
               var5.set(var6);
            }
         }

         DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.Constr var13 = null;
         Iterator var7 = this.annotatedConstructors.iterator();

         while(true) {
            DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.Constr var8;
            do {
               do {
                  if (!var7.hasNext()) {
                     if (var13 == null) {
                        String var15 = "No constructor has a @ConstructorProperties for this set of items: " + var4.keySet();
                        throw new InvalidObjectException(var15);
                     }

                     Object[] var14 = new Object[var13.presentParams.cardinality()];

                     for(int var16 = 0; var16 < var2.length; ++var16) {
                        if (var13.presentParams.get(var16)) {
                           Object var9 = var1.get(var2[var16]);
                           Object var10 = var3[var16].fromOpenValue(var9);
                           int var11 = var13.paramIndexes[var16];
                           if (var11 >= 0) {
                              var14[var11] = var10;
                           }
                        }
                     }

                     try {
                        ReflectUtil.checkPackageAccess(var13.constructor.getDeclaringClass());
                        return var13.constructor.newInstance(var14);
                     } catch (Exception var12) {
                        String var17 = "Exception constructing " + this.getTargetClass().getName();
                        throw DefaultMXBeanMappingFactory.invalidObjectException(var17, var12);
                     }
                  }

                  var8 = (DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor.Constr)var7.next();
               } while(!subset(var8.presentParams, var5));
            } while(var13 != null && !subset(var13.presentParams, var8.presentParams));

            var13 = var8;
         }
      }

      private static boolean subset(BitSet var0, BitSet var1) {
         BitSet var2 = (BitSet)var0.clone();
         var2.andNot(var1);
         return var2.isEmpty();
      }

      private static class Constr {
         final Constructor<?> constructor;
         final int[] paramIndexes;
         final BitSet presentParams;

         Constr(Constructor<?> var1, int[] var2, BitSet var3) {
            this.constructor = var1;
            this.paramIndexes = var2;
            this.presentParams = var3;
         }
      }

      static class AnnotationHelper {
         private static Class<? extends Annotation> constructorPropertiesClass;
         private static Method valueMethod;

         private static void findConstructorPropertiesClass() {
            try {
               constructorPropertiesClass = Class.forName("java.beans.ConstructorProperties", false, DefaultMXBeanMappingFactory.class.getClassLoader());
               valueMethod = constructorPropertiesClass.getMethod("value");
            } catch (ClassNotFoundException var1) {
            } catch (NoSuchMethodException var2) {
               throw new InternalError(var2);
            }

         }

         static boolean isAvailable() {
            return constructorPropertiesClass != null;
         }

         static String[] getPropertyNames(Constructor<?> var0) {
            if (!isAvailable()) {
               return null;
            } else {
               Annotation var1 = var0.getAnnotation(constructorPropertiesClass);
               if (var1 == null) {
                  return null;
               } else {
                  try {
                     return (String[])((String[])valueMethod.invoke(var1));
                  } catch (InvocationTargetException var3) {
                     throw new InternalError(var3);
                  } catch (IllegalAccessException var4) {
                     throw new InternalError(var4);
                  }
               }
            }
         }

         static {
            findConstructorPropertiesClass();
         }
      }
   }

   private static class CompositeBuilderViaSetters extends DefaultMXBeanMappingFactory.CompositeBuilder {
      private Method[] setters;

      CompositeBuilderViaSetters(Class<?> var1, String[] var2) {
         super(var1, var2);
      }

      String applicable(Method[] var1) {
         try {
            Constructor var2 = this.getTargetClass().getConstructor();
         } catch (Exception var10) {
            return "does not have a public no-arg constructor";
         }

         Method[] var12 = new Method[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Method var4 = var1[var3];
            Class var5 = var4.getReturnType();
            String var6 = DefaultMXBeanMappingFactory.propertyName(var4);
            String var7 = "set" + var6;

            Method var8;
            try {
               var8 = this.getTargetClass().getMethod(var7, var5);
               if (var8.getReturnType() != Void.TYPE) {
                  throw new Exception();
               }
            } catch (Exception var11) {
               return "not all getters have corresponding setters (" + var4 + ")";
            }

            var12[var3] = var8;
         }

         this.setters = var12;
         return null;
      }

      Object fromCompositeData(CompositeData var1, String[] var2, MXBeanMapping[] var3) throws InvalidObjectException {
         try {
            Class var5 = this.getTargetClass();
            ReflectUtil.checkPackageAccess(var5);
            Object var4 = var5.newInstance();

            for(int var6 = 0; var6 < var2.length; ++var6) {
               if (var1.containsKey(var2[var6])) {
                  Object var7 = var1.get(var2[var6]);
                  Object var8 = var3[var6].fromOpenValue(var7);
                  MethodUtil.invoke(this.setters[var6], var4, new Object[]{var8});
               }
            }

            return var4;
         } catch (Exception var9) {
            throw DefaultMXBeanMappingFactory.invalidObjectException(var9);
         }
      }
   }

   private static class CompositeBuilderCheckGetters extends DefaultMXBeanMappingFactory.CompositeBuilder {
      private final MXBeanMapping[] getterConverters;
      private Throwable possibleCause;

      CompositeBuilderCheckGetters(Class<?> var1, String[] var2, MXBeanMapping[] var3) {
         super(var1, var2);
         this.getterConverters = var3;
      }

      String applicable(Method[] var1) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            try {
               this.getterConverters[var2].checkReconstructible();
            } catch (InvalidObjectException var4) {
               this.possibleCause = var4;
               return "method " + var1[var2].getName() + " returns type that cannot be mapped back from OpenData";
            }
         }

         return "";
      }

      Throwable possibleCause() {
         return this.possibleCause;
      }

      final Object fromCompositeData(CompositeData var1, String[] var2, MXBeanMapping[] var3) {
         throw new Error();
      }
   }

   private static final class CompositeBuilderViaFrom extends DefaultMXBeanMappingFactory.CompositeBuilder {
      private Method fromMethod;

      CompositeBuilderViaFrom(Class<?> var1, String[] var2) {
         super(var1, var2);
      }

      String applicable(Method[] var1) throws InvalidObjectException {
         Class var2 = this.getTargetClass();

         try {
            Method var3 = var2.getMethod("from", CompositeData.class);
            if (!Modifier.isStatic(var3.getModifiers())) {
               throw new InvalidObjectException("Method from(CompositeData) is not static");
            } else if (var3.getReturnType() != this.getTargetClass()) {
               String var4 = "Method from(CompositeData) returns " + MXBeanIntrospector.typeName(var3.getReturnType()) + " not " + MXBeanIntrospector.typeName(var2);
               throw new InvalidObjectException(var4);
            } else {
               this.fromMethod = var3;
               return null;
            }
         } catch (InvalidObjectException var5) {
            throw var5;
         } catch (Exception var6) {
            return "no method from(CompositeData)";
         }
      }

      final Object fromCompositeData(CompositeData var1, String[] var2, MXBeanMapping[] var3) throws InvalidObjectException {
         try {
            return MethodUtil.invoke(this.fromMethod, (Object)null, new Object[]{var1});
         } catch (Exception var6) {
            throw DefaultMXBeanMappingFactory.invalidObjectException("Failed to invoke from(CompositeData)", var6);
         }
      }
   }

   private abstract static class CompositeBuilder {
      private final Class<?> targetClass;
      private final String[] itemNames;

      CompositeBuilder(Class<?> var1, String[] var2) {
         this.targetClass = var1;
         this.itemNames = var2;
      }

      Class<?> getTargetClass() {
         return this.targetClass;
      }

      String[] getItemNames() {
         return this.itemNames;
      }

      abstract String applicable(Method[] var1) throws InvalidObjectException;

      Throwable possibleCause() {
         return null;
      }

      abstract Object fromCompositeData(CompositeData var1, String[] var2, MXBeanMapping[] var3) throws InvalidObjectException;
   }

   private final class CompositeMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      private final String[] itemNames;
      private final Method[] getters;
      private final MXBeanMapping[] getterMappings;
      private DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder;

      CompositeMapping(Class<?> var2, CompositeType var3, String[] var4, Method[] var5, MXBeanMappingFactory var6) throws OpenDataException {
         super(var2, var3);

         assert var4.length == var5.length;

         this.itemNames = var4;
         this.getters = var5;
         this.getterMappings = new MXBeanMapping[var5.length];

         for(int var7 = 0; var7 < var5.length; ++var7) {
            Type var8 = var5[var7].getGenericReturnType();
            this.getterMappings[var7] = var6.mappingForType(var8, var6);
         }

      }

      final Object toNonNullOpenValue(Object var1) throws OpenDataException {
         CompositeType var2 = (CompositeType)this.getOpenType();
         if (var1 instanceof CompositeDataView) {
            return ((CompositeDataView)var1).toCompositeData(var2);
         } else if (var1 == null) {
            return null;
         } else {
            Object[] var3 = new Object[this.getters.length];

            for(int var4 = 0; var4 < this.getters.length; ++var4) {
               try {
                  Object var5 = MethodUtil.invoke(this.getters[var4], var1, (Object[])null);
                  var3[var4] = this.getterMappings[var4].toOpenValue(var5);
               } catch (Exception var6) {
                  throw DefaultMXBeanMappingFactory.openDataException("Error calling getter for " + this.itemNames[var4] + ": " + var6, var6);
               }
            }

            return new CompositeDataSupport(var2, this.itemNames, var3);
         }
      }

      private synchronized void makeCompositeBuilder() throws InvalidObjectException {
         if (this.compositeBuilder == null) {
            Class var1 = (Class)this.getJavaType();
            DefaultMXBeanMappingFactory.CompositeBuilder[][] var2 = new DefaultMXBeanMappingFactory.CompositeBuilder[][]{{new DefaultMXBeanMappingFactory.CompositeBuilderViaFrom(var1, this.itemNames)}, {new DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor(var1, this.itemNames)}, {new DefaultMXBeanMappingFactory.CompositeBuilderCheckGetters(var1, this.itemNames, this.getterMappings), new DefaultMXBeanMappingFactory.CompositeBuilderViaSetters(var1, this.itemNames), new DefaultMXBeanMappingFactory.CompositeBuilderViaProxy(var1, this.itemNames)}};
            DefaultMXBeanMappingFactory.CompositeBuilder var3 = null;
            StringBuilder var4 = new StringBuilder();
            Throwable var5 = null;
            DefaultMXBeanMappingFactory.CompositeBuilder[][] var6 = var2;
            int var7 = var2.length;
            int var8 = 0;

            label51:
            while(var8 < var7) {
               DefaultMXBeanMappingFactory.CompositeBuilder[] var9 = var6[var8];
               int var10 = 0;

               while(true) {
                  label47: {
                     if (var10 < var9.length) {
                        DefaultMXBeanMappingFactory.CompositeBuilder var11 = var9[var10];
                        String var12 = var11.applicable(this.getters);
                        if (var12 == null) {
                           var3 = var11;
                           break label51;
                        }

                        Throwable var13 = var11.possibleCause();
                        if (var13 != null) {
                           var5 = var13;
                        }

                        if (var12.length() <= 0) {
                           break label47;
                        }

                        if (var4.length() > 0) {
                           var4.append("; ");
                        }

                        var4.append(var12);
                        if (var10 != 0) {
                           break label47;
                        }
                     }

                     ++var8;
                     break;
                  }

                  ++var10;
               }
            }

            if (var3 == null) {
               String var14 = "Do not know how to make a " + var1.getName() + " from a CompositeData: " + var4;
               if (var5 != null) {
                  var14 = var14 + ". Remaining exceptions show a POSSIBLE cause.";
               }

               throw DefaultMXBeanMappingFactory.invalidObjectException(var14, var5);
            } else {
               this.compositeBuilder = var3;
            }
         }
      }

      public void checkReconstructible() throws InvalidObjectException {
         this.makeCompositeBuilder();
      }

      final Object fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         this.makeCompositeBuilder();
         return this.compositeBuilder.fromCompositeData((CompositeData)var1, this.itemNames, this.getterMappings);
      }
   }

   private static final class TabularMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      private final boolean sortedMap;
      private final MXBeanMapping keyMapping;
      private final MXBeanMapping valueMapping;

      TabularMapping(Type var1, boolean var2, TabularType var3, MXBeanMapping var4, MXBeanMapping var5) {
         super(var1, var3);
         this.sortedMap = var2;
         this.keyMapping = var4;
         this.valueMapping = var5;
      }

      final Object toNonNullOpenValue(Object var1) throws OpenDataException {
         Map var2 = (Map)Util.cast(var1);
         if (var2 instanceof SortedMap) {
            Comparator var3 = ((SortedMap)var2).comparator();
            if (var3 != null) {
               String var12 = "Cannot convert SortedMap with non-null comparator: " + var3;
               throw DefaultMXBeanMappingFactory.openDataException(var12, new IllegalArgumentException(var12));
            }
         }

         TabularType var11 = (TabularType)this.getOpenType();
         TabularDataSupport var4 = new TabularDataSupport(var11);
         CompositeType var5 = var11.getRowType();
         Iterator var6 = var2.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            Object var8 = this.keyMapping.toOpenValue(var7.getKey());
            Object var9 = this.valueMapping.toOpenValue(var7.getValue());
            CompositeDataSupport var10 = new CompositeDataSupport(var5, DefaultMXBeanMappingFactory.keyValueArray, new Object[]{var8, var9});
            var4.put(var10);
         }

         return var4;
      }

      final Object fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         TabularData var2 = (TabularData)var1;
         Collection var3 = (Collection)Util.cast(var2.values());
         Object var4 = this.sortedMap ? Util.newSortedMap() : Util.newInsertionOrderMap();
         Iterator var5 = var3.iterator();

         Object var7;
         Object var8;
         do {
            if (!var5.hasNext()) {
               return var4;
            }

            CompositeData var6 = (CompositeData)var5.next();
            var7 = this.keyMapping.fromOpenValue(var6.get("key"));
            var8 = this.valueMapping.fromOpenValue(var6.get("value"));
         } while(((Map)var4).put(var7, var8) == null);

         String var9 = "Duplicate entry in TabularData: key=" + var7;
         throw new InvalidObjectException(var9);
      }

      public void checkReconstructible() throws InvalidObjectException {
         this.keyMapping.checkReconstructible();
         this.valueMapping.checkReconstructible();
      }
   }

   private static final class MXBeanRefMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      MXBeanRefMapping(Type var1) {
         super(var1, SimpleType.OBJECTNAME);
      }

      final Object toNonNullOpenValue(Object var1) throws OpenDataException {
         MXBeanLookup var2 = this.lookupNotNull(OpenDataException.class);
         ObjectName var3 = var2.mxbeanToObjectName(var1);
         if (var3 == null) {
            throw new OpenDataException("No name for object: " + var1);
         } else {
            return var3;
         }
      }

      final Object fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         MXBeanLookup var2 = this.lookupNotNull(InvalidObjectException.class);
         ObjectName var3 = (ObjectName)var1;
         Object var4 = var2.objectNameToMXBean(var3, (Class)this.getJavaType());
         if (var4 == null) {
            String var5 = "No MXBean for name: " + var3;
            throw new InvalidObjectException(var5);
         } else {
            return var4;
         }
      }

      private <T extends Exception> MXBeanLookup lookupNotNull(Class<T> var1) throws T {
         MXBeanLookup var2 = MXBeanLookup.getLookup();
         if (var2 == null) {
            Exception var4;
            try {
               Constructor var5 = var1.getConstructor(String.class);
               var4 = (Exception)var5.newInstance("Cannot convert MXBean interface in this context");
            } catch (Exception var6) {
               throw new RuntimeException(var6);
            }

            throw var4;
         } else {
            return var2;
         }
      }
   }

   private static final class CollectionMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      private final Class<? extends Collection<?>> collectionClass;
      private final MXBeanMapping elementMapping;

      CollectionMapping(Type var1, ArrayType<?> var2, Class<?> var3, MXBeanMapping var4) {
         super(var1, var2);
         this.elementMapping = var4;
         Type var5 = ((ParameterizedType)var1).getRawType();
         Class var6 = (Class)var5;
         Class var7;
         if (var6 == List.class) {
            var7 = ArrayList.class;
         } else if (var6 == Set.class) {
            var7 = HashSet.class;
         } else if (var6 == SortedSet.class) {
            var7 = TreeSet.class;
         } else {
            assert false;

            var7 = null;
         }

         this.collectionClass = (Class)Util.cast(var7);
      }

      final Object toNonNullOpenValue(Object var1) throws OpenDataException {
         Collection var2 = (Collection)var1;
         if (var2 instanceof SortedSet) {
            Comparator var3 = ((SortedSet)var2).comparator();
            if (var3 != null) {
               String var8 = "Cannot convert SortedSet with non-null comparator: " + var3;
               throw DefaultMXBeanMappingFactory.openDataException(var8, new IllegalArgumentException(var8));
            }
         }

         Object[] var7 = (Object[])((Object[])Array.newInstance(this.getOpenClass().getComponentType(), var2.size()));
         int var4 = 0;

         Object var6;
         for(Iterator var5 = var2.iterator(); var5.hasNext(); var7[var4++] = this.elementMapping.toOpenValue(var6)) {
            var6 = var5.next();
         }

         return var7;
      }

      final Object fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         Object[] var2 = (Object[])((Object[])var1);

         Collection var3;
         try {
            var3 = (Collection)Util.cast(this.collectionClass.newInstance());
         } catch (Exception var10) {
            throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot create collection", var10);
         }

         Object[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Object var7 = var4[var6];
            Object var8 = this.elementMapping.fromOpenValue(var7);
            if (!var3.add(var8)) {
               String var9 = "Could not add " + var7 + " to " + this.collectionClass.getName() + " (duplicate set element?)";
               throw new InvalidObjectException(var9);
            }
         }

         return var3;
      }

      public void checkReconstructible() throws InvalidObjectException {
         this.elementMapping.checkReconstructible();
      }
   }

   private static final class ArrayMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      private final MXBeanMapping elementMapping;

      ArrayMapping(Type var1, ArrayType<?> var2, Class<?> var3, MXBeanMapping var4) {
         super(var1, var2);
         this.elementMapping = var4;
      }

      final Object toNonNullOpenValue(Object var1) throws OpenDataException {
         Object[] var2 = (Object[])((Object[])var1);
         int var3 = var2.length;
         Object[] var4 = (Object[])((Object[])Array.newInstance(this.getOpenClass().getComponentType(), var3));

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = this.elementMapping.toOpenValue(var2[var5]);
         }

         return var4;
      }

      final Object fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         Object[] var2 = (Object[])((Object[])var1);
         Type var3 = this.getJavaType();
         Object var5;
         if (var3 instanceof GenericArrayType) {
            var5 = ((GenericArrayType)var3).getGenericComponentType();
         } else {
            if (!(var3 instanceof Class) || !((Class)var3).isArray()) {
               throw new IllegalArgumentException("Not an array: " + var3);
            }

            var5 = ((Class)var3).getComponentType();
         }

         Object[] var4 = (Object[])((Object[])Array.newInstance((Class)var5, var2.length));

         for(int var6 = 0; var6 < var2.length; ++var6) {
            var4[var6] = this.elementMapping.fromOpenValue(var2[var6]);
         }

         return var4;
      }

      public void checkReconstructible() throws InvalidObjectException {
         this.elementMapping.checkReconstructible();
      }
   }

   private static final class EnumMapping<T extends Enum<T>> extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      private final Class<T> enumClass;

      EnumMapping(Class<T> var1) {
         super(var1, SimpleType.STRING);
         this.enumClass = var1;
      }

      final Object toNonNullOpenValue(Object var1) {
         return ((Enum)var1).name();
      }

      final T fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         try {
            return Enum.valueOf(this.enumClass, (String)var1);
         } catch (Exception var3) {
            throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot convert to enum: " + var1, var3);
         }
      }
   }

   private static final class IdentityMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping {
      IdentityMapping(Type var1, OpenType<?> var2) {
         super(var1, var2);
      }

      boolean isIdentity() {
         return true;
      }

      Object fromNonNullOpenValue(Object var1) throws InvalidObjectException {
         return var1;
      }

      Object toNonNullOpenValue(Object var1) throws OpenDataException {
         return var1;
      }
   }

   private static final class Mappings extends WeakHashMap<Type, WeakReference<MXBeanMapping>> {
      private Mappings() {
      }

      // $FF: synthetic method
      Mappings(Object var1) {
         this();
      }
   }

   abstract static class NonNullMXBeanMapping extends MXBeanMapping {
      NonNullMXBeanMapping(Type var1, OpenType<?> var2) {
         super(var1, var2);
      }

      public final Object fromOpenValue(Object var1) throws InvalidObjectException {
         return var1 == null ? null : this.fromNonNullOpenValue(var1);
      }

      public final Object toOpenValue(Object var1) throws OpenDataException {
         return var1 == null ? null : this.toNonNullOpenValue(var1);
      }

      abstract Object fromNonNullOpenValue(Object var1) throws InvalidObjectException;

      abstract Object toNonNullOpenValue(Object var1) throws OpenDataException;

      boolean isIdentity() {
         return false;
      }
   }
}
