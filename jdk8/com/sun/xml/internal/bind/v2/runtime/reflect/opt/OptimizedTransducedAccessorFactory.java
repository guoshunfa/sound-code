package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OptimizedTransducedAccessorFactory {
   private static final Logger logger = Util.getClassLogger();
   private static final String fieldTemplateName;
   private static final String methodTemplateName;
   private static final Map<Class, String> suffixMap;

   private OptimizedTransducedAccessorFactory() {
   }

   public static final TransducedAccessor get(RuntimePropertyInfo prop) {
      Accessor acc = prop.getAccessor();
      Class opt = null;
      TypeInfo<Type, Class> parent = prop.parent();
      if (!(parent instanceof RuntimeClassInfo)) {
         return null;
      } else {
         Class dc = (Class)((RuntimeClassInfo)parent).getClazz();
         String newClassName = ClassTailor.toVMClassName(dc) + "_JaxbXducedAccessor_" + prop.getName();
         if (acc instanceof Accessor.FieldReflection) {
            Accessor.FieldReflection racc = (Accessor.FieldReflection)acc;
            Field field = racc.f;
            int mods = field.getModifiers();
            if (Modifier.isPrivate(mods) || Modifier.isFinal(mods)) {
               return null;
            }

            Class<?> t = field.getType();
            if (t.isPrimitive()) {
               opt = AccessorInjector.prepare(dc, fieldTemplateName + (String)suffixMap.get(t), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(dc), "f_" + t.getName(), field.getName());
            }
         }

         if (acc.getClass() == Accessor.GetterSetterReflection.class) {
            label67: {
               Accessor.GetterSetterReflection gacc = (Accessor.GetterSetterReflection)acc;
               if (gacc.getter != null && gacc.setter != null) {
                  Class<?> t = gacc.getter.getReturnType();
                  if (!Modifier.isPrivate(gacc.getter.getModifiers()) && !Modifier.isPrivate(gacc.setter.getModifiers())) {
                     if (t.isPrimitive()) {
                        opt = AccessorInjector.prepare(dc, methodTemplateName + (String)suffixMap.get(t), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(dc), "get_" + t.getName(), gacc.getter.getName(), "set_" + t.getName(), gacc.setter.getName());
                     }
                     break label67;
                  }

                  return null;
               }

               return null;
            }
         }

         if (opt == null) {
            return null;
         } else {
            logger.log(Level.FINE, "Using optimized TransducedAccessor for " + prop.displayName());

            try {
               return (TransducedAccessor)opt.newInstance();
            } catch (InstantiationException var10) {
               logger.log(Level.INFO, (String)"failed to load an optimized TransducedAccessor", (Throwable)var10);
            } catch (IllegalAccessException var11) {
               logger.log(Level.INFO, (String)"failed to load an optimized TransducedAccessor", (Throwable)var11);
            } catch (SecurityException var12) {
               logger.log(Level.INFO, (String)"failed to load an optimized TransducedAccessor", (Throwable)var12);
            }

            return null;
         }
      }
   }

   static {
      String s = TransducedAccessor_field_Byte.class.getName();
      fieldTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
      s = TransducedAccessor_method_Byte.class.getName();
      methodTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
      suffixMap = new HashMap();
      suffixMap.put(Byte.TYPE, "Byte");
      suffixMap.put(Short.TYPE, "Short");
      suffixMap.put(Integer.TYPE, "Integer");
      suffixMap.put(Long.TYPE, "Long");
      suffixMap.put(Boolean.TYPE, "Boolean");
      suffixMap.put(Float.TYPE, "Float");
      suffixMap.put(Double.TYPE, "Double");
   }
}
