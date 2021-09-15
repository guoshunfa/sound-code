package com.sun.xml.internal.ws.spi.db;

public interface PropertyGetter {
   Class getType();

   <A> A getAnnotation(Class<A> var1);

   Object get(Object var1);
}
