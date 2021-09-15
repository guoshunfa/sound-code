package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public abstract class MXBeanMapping {
   private final Type javaType;
   private final OpenType<?> openType;
   private final Class<?> openClass;

   protected MXBeanMapping(Type var1, OpenType<?> var2) {
      if (var1 != null && var2 != null) {
         this.javaType = var1;
         this.openType = var2;
         this.openClass = makeOpenClass(var1, var2);
      } else {
         throw new NullPointerException("Null argument");
      }
   }

   public final Type getJavaType() {
      return this.javaType;
   }

   public final OpenType<?> getOpenType() {
      return this.openType;
   }

   public final Class<?> getOpenClass() {
      return this.openClass;
   }

   private static Class<?> makeOpenClass(Type var0, OpenType<?> var1) {
      if (var0 instanceof Class && ((Class)var0).isPrimitive()) {
         return (Class)var0;
      } else {
         try {
            String var2 = var1.getClassName();
            return Class.forName(var2, false, MXBeanMapping.class.getClassLoader());
         } catch (ClassNotFoundException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public abstract Object fromOpenValue(Object var1) throws InvalidObjectException;

   public abstract Object toOpenValue(Object var1) throws OpenDataException;

   public void checkReconstructible() throws InvalidObjectException {
   }
}
