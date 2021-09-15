package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

public class ReflectAnnotationReader implements MetadataReader {
   public Annotation[] getAnnotations(Method m) {
      return m.getAnnotations();
   }

   public Annotation[][] getParameterAnnotations(final Method method) {
      return (Annotation[][])AccessController.doPrivileged(new PrivilegedAction<Annotation[][]>() {
         public Annotation[][] run() {
            return method.getParameterAnnotations();
         }
      });
   }

   public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
      return (Annotation)AccessController.doPrivileged(new PrivilegedAction<A>() {
         public A run() {
            return m.getAnnotation(annType);
         }
      });
   }

   public <A extends Annotation> A getAnnotation(final Class<A> annType, final Class<?> cls) {
      return (Annotation)AccessController.doPrivileged(new PrivilegedAction<A>() {
         public A run() {
            return cls.getAnnotation(annType);
         }
      });
   }

   public Annotation[] getAnnotations(final Class<?> cls) {
      return (Annotation[])AccessController.doPrivileged(new PrivilegedAction<Annotation[]>() {
         public Annotation[] run() {
            return cls.getAnnotations();
         }
      });
   }

   public void getProperties(Map<String, Object> prop, Class<?> cls) {
   }

   public void getProperties(Map<String, Object> prop, Method method) {
   }

   public void getProperties(Map<String, Object> prop, Method method, int pos) {
   }
}
