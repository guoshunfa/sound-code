package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import sun.reflect.misc.MethodUtil;

final class ConvertingMethod {
   private static final String[] noStrings = new String[0];
   private final Method method;
   private final MXBeanMapping returnMapping;
   private final MXBeanMapping[] paramMappings;
   private final boolean paramConversionIsIdentity;

   static ConvertingMethod from(Method var0) {
      try {
         return new ConvertingMethod(var0);
      } catch (OpenDataException var3) {
         String var2 = "Method " + var0.getDeclaringClass().getName() + "." + var0.getName() + " has parameter or return type that cannot be translated into an open type";
         throw new IllegalArgumentException(var2, var3);
      }
   }

   Method getMethod() {
      return this.method;
   }

   Descriptor getDescriptor() {
      return Introspector.descriptorForElement(this.method);
   }

   Type getGenericReturnType() {
      return this.method.getGenericReturnType();
   }

   Type[] getGenericParameterTypes() {
      return this.method.getGenericParameterTypes();
   }

   String getName() {
      return this.method.getName();
   }

   OpenType<?> getOpenReturnType() {
      return this.returnMapping.getOpenType();
   }

   OpenType<?>[] getOpenParameterTypes() {
      OpenType[] var1 = new OpenType[this.paramMappings.length];

      for(int var2 = 0; var2 < this.paramMappings.length; ++var2) {
         var1[var2] = this.paramMappings[var2].getOpenType();
      }

      return var1;
   }

   void checkCallFromOpen() {
      try {
         MXBeanMapping[] var1 = this.paramMappings;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            MXBeanMapping var4 = var1[var3];
            var4.checkReconstructible();
         }

      } catch (InvalidObjectException var5) {
         throw new IllegalArgumentException(var5);
      }
   }

   void checkCallToOpen() {
      try {
         this.returnMapping.checkReconstructible();
      } catch (InvalidObjectException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   String[] getOpenSignature() {
      if (this.paramMappings.length == 0) {
         return noStrings;
      } else {
         String[] var1 = new String[this.paramMappings.length];

         for(int var2 = 0; var2 < this.paramMappings.length; ++var2) {
            var1[var2] = this.paramMappings[var2].getOpenClass().getName();
         }

         return var1;
      }
   }

   final Object toOpenReturnValue(MXBeanLookup var1, Object var2) throws OpenDataException {
      return this.returnMapping.toOpenValue(var2);
   }

   final Object fromOpenReturnValue(MXBeanLookup var1, Object var2) throws InvalidObjectException {
      return this.returnMapping.fromOpenValue(var2);
   }

   final Object[] toOpenParameters(MXBeanLookup var1, Object[] var2) throws OpenDataException {
      if (!this.paramConversionIsIdentity && var2 != null) {
         Object[] var3 = new Object[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = this.paramMappings[var4].toOpenValue(var2[var4]);
         }

         return var3;
      } else {
         return var2;
      }
   }

   final Object[] fromOpenParameters(Object[] var1) throws InvalidObjectException {
      if (!this.paramConversionIsIdentity && var1 != null) {
         Object[] var2 = new Object[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = this.paramMappings[var3].fromOpenValue(var1[var3]);
         }

         return var2;
      } else {
         return var1;
      }
   }

   final Object toOpenParameter(MXBeanLookup var1, Object var2, int var3) throws OpenDataException {
      return this.paramMappings[var3].toOpenValue(var2);
   }

   final Object fromOpenParameter(MXBeanLookup var1, Object var2, int var3) throws InvalidObjectException {
      return this.paramMappings[var3].fromOpenValue(var2);
   }

   Object invokeWithOpenReturn(MXBeanLookup var1, Object var2, Object[] var3) throws MBeanException, IllegalAccessException, InvocationTargetException {
      MXBeanLookup var4 = MXBeanLookup.getLookup();

      Object var5;
      try {
         MXBeanLookup.setLookup(var1);
         var5 = this.invokeWithOpenReturn(var2, var3);
      } finally {
         MXBeanLookup.setLookup(var4);
      }

      return var5;
   }

   private Object invokeWithOpenReturn(Object var1, Object[] var2) throws MBeanException, IllegalAccessException, InvocationTargetException {
      Object[] var3;
      try {
         var3 = this.fromOpenParameters(var2);
      } catch (InvalidObjectException var8) {
         String var5 = this.methodName() + ": cannot convert parameters from open values: " + var8;
         throw new MBeanException(var8, var5);
      }

      Object var4 = MethodUtil.invoke(this.method, var1, var3);

      try {
         return this.returnMapping.toOpenValue(var4);
      } catch (OpenDataException var7) {
         String var6 = this.methodName() + ": cannot convert return value to open value: " + var7;
         throw new MBeanException(var7, var6);
      }
   }

   private String methodName() {
      return this.method.getDeclaringClass() + "." + this.method.getName();
   }

   private ConvertingMethod(Method var1) throws OpenDataException {
      this.method = var1;
      MXBeanMappingFactory var2 = MXBeanMappingFactory.DEFAULT;
      this.returnMapping = var2.mappingForType(var1.getGenericReturnType(), var2);
      Type[] var3 = var1.getGenericParameterTypes();
      this.paramMappings = new MXBeanMapping[var3.length];
      boolean var4 = true;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         this.paramMappings[var5] = var2.mappingForType(var3[var5], var2);
         var4 &= DefaultMXBeanMappingFactory.isIdentity(this.paramMappings[var5]);
      }

      this.paramConversionIsIdentity = var4;
   }
}
