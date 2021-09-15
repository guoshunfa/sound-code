package com.sun.xml.internal.ws.spi.db;

public interface PropertySetter {
   Class getType();

   <A> A getAnnotation(Class<A> var1);

   void set(Object var1, Object var2);
}
