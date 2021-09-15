package com.sun.xml.internal.ws.api.databinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public interface MetadataReader {
   Annotation[] getAnnotations(Method var1);

   Annotation[][] getParameterAnnotations(Method var1);

   <A extends Annotation> A getAnnotation(Class<A> var1, Method var2);

   <A extends Annotation> A getAnnotation(Class<A> var1, Class<?> var2);

   Annotation[] getAnnotations(Class<?> var1);

   void getProperties(Map<String, Object> var1, Class<?> var2);

   void getProperties(Map<String, Object> var1, Method var2);

   void getProperties(Map<String, Object> var1, Method var2, int var3);
}
