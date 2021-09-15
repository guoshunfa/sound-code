package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertyGetterBase implements PropertyGetter {
   protected Class type;

   public Class getType() {
      return this.type;
   }

   public static boolean getterPattern(Method method) {
      if (!method.getReturnType().equals(Void.TYPE) && (method.getParameterTypes() == null || method.getParameterTypes().length == 0)) {
         if (method.getName().startsWith("get") && method.getName().length() > 3) {
            return true;
         }

         if (method.getReturnType().equals(Boolean.TYPE) && method.getName().startsWith("is") && method.getName().length() > 2) {
            return true;
         }
      }

      return false;
   }
}
