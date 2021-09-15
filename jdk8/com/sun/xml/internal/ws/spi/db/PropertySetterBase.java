package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertySetterBase implements PropertySetter {
   protected Class type;

   public Class getType() {
      return this.type;
   }

   public static boolean setterPattern(Method method) {
      return method.getName().startsWith("set") && method.getName().length() > 3 && method.getReturnType().equals(Void.TYPE) && method.getParameterTypes() != null && method.getParameterTypes().length == 1;
   }
}
