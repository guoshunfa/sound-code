package com.sun.jmx.mbeanserver;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;

class MXBeanIntrospector extends MBeanIntrospector<ConvertingMethod> {
   private static final MXBeanIntrospector instance = new MXBeanIntrospector();
   private final MBeanIntrospector.PerInterfaceMap<ConvertingMethod> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
   private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();

   static MXBeanIntrospector getInstance() {
      return instance;
   }

   MBeanIntrospector.PerInterfaceMap<ConvertingMethod> getPerInterfaceMap() {
      return this.perInterfaceMap;
   }

   MBeanIntrospector.MBeanInfoMap getMBeanInfoMap() {
      return mbeanInfoMap;
   }

   MBeanAnalyzer<ConvertingMethod> getAnalyzer(Class<?> var1) throws NotCompliantMBeanException {
      return MBeanAnalyzer.analyzer(var1, this);
   }

   boolean isMXBean() {
      return true;
   }

   ConvertingMethod mFrom(Method var1) {
      return ConvertingMethod.from(var1);
   }

   String getName(ConvertingMethod var1) {
      return var1.getName();
   }

   Type getGenericReturnType(ConvertingMethod var1) {
      return var1.getGenericReturnType();
   }

   Type[] getGenericParameterTypes(ConvertingMethod var1) {
      return var1.getGenericParameterTypes();
   }

   String[] getSignature(ConvertingMethod var1) {
      return var1.getOpenSignature();
   }

   void checkMethod(ConvertingMethod var1) {
      var1.checkCallFromOpen();
   }

   Object invokeM2(ConvertingMethod var1, Object var2, Object[] var3, Object var4) throws InvocationTargetException, IllegalAccessException, MBeanException {
      return var1.invokeWithOpenReturn((MXBeanLookup)var4, var2, var3);
   }

   boolean validParameter(ConvertingMethod var1, Object var2, int var3, Object var4) {
      if (var2 != null) {
         Object var8;
         try {
            var8 = var1.fromOpenParameter((MXBeanLookup)var4, var2, var3);
         } catch (Exception var7) {
            return true;
         }

         return isValidParameter(var1.getMethod(), var8, var3);
      } else {
         Type var5 = var1.getGenericParameterTypes()[var3];
         return !(var5 instanceof Class) || !((Class)var5).isPrimitive();
      }
   }

   MBeanAttributeInfo getMBeanAttributeInfo(String var1, ConvertingMethod var2, ConvertingMethod var3) {
      boolean var4 = var2 != null;
      boolean var5 = var3 != null;
      boolean var6 = var4 && this.getName(var2).startsWith("is");
      OpenType var8;
      Type var9;
      if (var4) {
         var8 = var2.getOpenReturnType();
         var9 = var2.getGenericReturnType();
      } else {
         var8 = var3.getOpenParameterTypes()[0];
         var9 = var3.getGenericParameterTypes()[0];
      }

      Object var10 = typeDescriptor(var8, var9);
      if (var4) {
         var10 = ImmutableDescriptor.union((Descriptor)var10, var2.getDescriptor());
      }

      if (var5) {
         var10 = ImmutableDescriptor.union((Descriptor)var10, var3.getDescriptor());
      }

      Object var11;
      if (canUseOpenInfo(var9)) {
         var11 = new OpenMBeanAttributeInfoSupport(var1, var1, var8, var4, var5, var6, (Descriptor)var10);
      } else {
         var11 = new MBeanAttributeInfo(var1, originalTypeString(var9), var1, var4, var5, var6, (Descriptor)var10);
      }

      return (MBeanAttributeInfo)var11;
   }

   MBeanOperationInfo getMBeanOperationInfo(String var1, ConvertingMethod var2) {
      Method var3 = var2.getMethod();
      OpenType var6 = var2.getOpenReturnType();
      Type var7 = var2.getGenericReturnType();
      OpenType[] var8 = var2.getOpenParameterTypes();
      Type[] var9 = var2.getGenericParameterTypes();
      MBeanParameterInfo[] var10 = new MBeanParameterInfo[var8.length];
      boolean var11 = canUseOpenInfo(var7);
      boolean var12 = true;
      Annotation[][] var13 = var3.getParameterAnnotations();

      for(int var14 = 0; var14 < var8.length; ++var14) {
         String var15 = "p" + var14;
         OpenType var17 = var8[var14];
         Type var18 = var9[var14];
         Descriptor var19 = typeDescriptor(var17, var18);
         ImmutableDescriptor var24 = ImmutableDescriptor.union(var19, Introspector.descriptorForAnnotations(var13[var14]));
         Object var20;
         if (canUseOpenInfo(var18)) {
            var20 = new OpenMBeanParameterInfoSupport(var15, var15, var17, var24);
         } else {
            var12 = false;
            var20 = new MBeanParameterInfo(var15, originalTypeString(var18), var15, var24);
         }

         var10[var14] = (MBeanParameterInfo)var20;
      }

      Descriptor var21 = typeDescriptor(var6, var7);
      ImmutableDescriptor var22 = ImmutableDescriptor.union(var21, Introspector.descriptorForElement(var3));
      Object var23;
      if (var11 && var12) {
         OpenMBeanParameterInfo[] var16 = new OpenMBeanParameterInfo[var10.length];
         System.arraycopy(var10, 0, var16, 0, var10.length);
         var23 = new OpenMBeanOperationInfoSupport(var1, var1, var16, var6, 3, var22);
      } else {
         var23 = new MBeanOperationInfo(var1, var1, var10, var11 ? var6.getClassName() : originalTypeString(var7), 3, var22);
      }

      return (MBeanOperationInfo)var23;
   }

   Descriptor getBasicMBeanDescriptor() {
      return new ImmutableDescriptor(new String[]{"mxbean=true", "immutableInfo=true"});
   }

   Descriptor getMBeanDescriptor(Class<?> var1) {
      return ImmutableDescriptor.EMPTY_DESCRIPTOR;
   }

   private static Descriptor typeDescriptor(OpenType<?> var0, Type var1) {
      return new ImmutableDescriptor(new String[]{"openType", "originalType"}, new Object[]{var0, originalTypeString(var1)});
   }

   private static boolean canUseOpenInfo(Type var0) {
      if (var0 instanceof GenericArrayType) {
         return canUseOpenInfo(((GenericArrayType)var0).getGenericComponentType());
      } else if (var0 instanceof Class && ((Class)var0).isArray()) {
         return canUseOpenInfo(((Class)var0).getComponentType());
      } else {
         return !(var0 instanceof Class) || !((Class)var0).isPrimitive();
      }
   }

   private static String originalTypeString(Type var0) {
      return var0 instanceof Class ? ((Class)var0).getName() : typeName(var0);
   }

   static String typeName(Type var0) {
      if (var0 instanceof Class) {
         Class var9 = (Class)var0;
         return var9.isArray() ? typeName(var9.getComponentType()) + "[]" : var9.getName();
      } else if (var0 instanceof GenericArrayType) {
         GenericArrayType var8 = (GenericArrayType)var0;
         return typeName(var8.getGenericComponentType()) + "[]";
      } else if (!(var0 instanceof ParameterizedType)) {
         return "???";
      } else {
         ParameterizedType var1 = (ParameterizedType)var0;
         StringBuilder var2 = new StringBuilder();
         var2.append(typeName(var1.getRawType())).append("<");
         String var3 = "";
         Type[] var4 = var1.getActualTypeArguments();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Type var7 = var4[var6];
            var2.append(var3).append(typeName(var7));
            var3 = ", ";
         }

         return var2.append(">").toString();
      }
   }
}
