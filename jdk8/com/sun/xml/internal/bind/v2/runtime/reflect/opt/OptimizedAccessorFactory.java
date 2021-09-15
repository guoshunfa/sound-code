package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OptimizedAccessorFactory {
   private static final Logger logger = Util.getClassLogger();
   private static final String fieldTemplateName;
   private static final String methodTemplateName;

   private OptimizedAccessorFactory() {
   }

   public static final <B, V> Accessor<B, V> get(Method getter, Method setter) {
      if (getter.getParameterTypes().length != 0) {
         return null;
      } else {
         Class<?>[] sparams = setter.getParameterTypes();
         if (sparams.length != 1) {
            return null;
         } else if (sparams[0] != getter.getReturnType()) {
            return null;
         } else if (setter.getReturnType() != Void.TYPE) {
            return null;
         } else if (getter.getDeclaringClass() != setter.getDeclaringClass()) {
            return null;
         } else if (!Modifier.isPrivate(getter.getModifiers()) && !Modifier.isPrivate(setter.getModifiers())) {
            Class t = sparams[0];
            String typeName = t.getName().replace('.', '_');
            String compName;
            if (t.isArray()) {
               typeName = "AOf_";

               for(compName = t.getComponentType().getName().replace('.', '_'); compName.startsWith("[L"); typeName = typeName + "AOf_") {
                  compName = compName.substring(2);
               }

               typeName = typeName + compName;
            }

            compName = ClassTailor.toVMClassName(getter.getDeclaringClass()) + "$JaxbAccessorM_" + getter.getName() + '_' + setter.getName() + '_' + typeName;
            Class opt;
            if (t.isPrimitive()) {
               opt = AccessorInjector.prepare(getter.getDeclaringClass(), methodTemplateName + ((Class)RuntimeUtil.primitiveToBox.get(t)).getSimpleName(), compName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(getter.getDeclaringClass()), "get_" + t.getName(), getter.getName(), "set_" + t.getName(), setter.getName());
            } else {
               opt = AccessorInjector.prepare(getter.getDeclaringClass(), methodTemplateName + "Ref", compName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(getter.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(t), "()" + ClassTailor.toVMTypeName(Ref.class), "()" + ClassTailor.toVMTypeName(t), '(' + ClassTailor.toVMTypeName(Ref.class) + ")V", '(' + ClassTailor.toVMTypeName(t) + ")V", "get_ref", getter.getName(), "set_ref", setter.getName());
            }

            if (opt == null) {
               return null;
            } else {
               Accessor<B, V> acc = instanciate(opt);
               if (acc != null && logger.isLoggable(Level.FINE)) {
                  logger.log(Level.FINE, "Using optimized Accessor for {0} and {1}", new Object[]{getter, setter});
               }

               return acc;
            }
         } else {
            return null;
         }
      }
   }

   public static final <B, V> Accessor<B, V> get(Field field) {
      int mods = field.getModifiers();
      if (!Modifier.isPrivate(mods) && !Modifier.isFinal(mods)) {
         String newClassName = ClassTailor.toVMClassName(field.getDeclaringClass()) + "$JaxbAccessorF_" + field.getName();
         Class opt;
         if (field.getType().isPrimitive()) {
            opt = AccessorInjector.prepare(field.getDeclaringClass(), fieldTemplateName + ((Class)RuntimeUtil.primitiveToBox.get(field.getType())).getSimpleName(), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(field.getDeclaringClass()), "f_" + field.getType().getName(), field.getName());
         } else {
            opt = AccessorInjector.prepare(field.getDeclaringClass(), fieldTemplateName + "Ref", newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(field.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(field.getType()), ClassTailor.toVMTypeName(Ref.class), ClassTailor.toVMTypeName(field.getType()), "f_ref", field.getName());
         }

         if (opt == null) {
            return null;
         } else {
            Accessor<B, V> acc = instanciate(opt);
            if (acc != null && logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, (String)"Using optimized Accessor for {0}", (Object)field);
            }

            return acc;
         }
      } else {
         return null;
      }
   }

   private static <B, V> Accessor<B, V> instanciate(Class opt) {
      try {
         return (Accessor)opt.newInstance();
      } catch (InstantiationException var2) {
         logger.log(Level.INFO, (String)"failed to load an optimized Accessor", (Throwable)var2);
      } catch (IllegalAccessException var3) {
         logger.log(Level.INFO, (String)"failed to load an optimized Accessor", (Throwable)var3);
      } catch (SecurityException var4) {
         logger.log(Level.INFO, (String)"failed to load an optimized Accessor", (Throwable)var4);
      }

      return null;
   }

   static {
      String s = FieldAccessor_Byte.class.getName();
      fieldTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
      s = MethodAccessor_Byte.class.getName();
      methodTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
   }
}
