package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import java.lang.annotation.Annotation;

public interface AnnotationReader<T, C, F, M> {
   void setErrorHandler(ErrorHandler var1);

   <A extends Annotation> A getFieldAnnotation(Class<A> var1, F var2, Locatable var3);

   boolean hasFieldAnnotation(Class<? extends Annotation> var1, F var2);

   boolean hasClassAnnotation(C var1, Class<? extends Annotation> var2);

   Annotation[] getAllFieldAnnotations(F var1, Locatable var2);

   <A extends Annotation> A getMethodAnnotation(Class<A> var1, M var2, M var3, Locatable var4);

   boolean hasMethodAnnotation(Class<? extends Annotation> var1, String var2, M var3, M var4, Locatable var5);

   Annotation[] getAllMethodAnnotations(M var1, Locatable var2);

   <A extends Annotation> A getMethodAnnotation(Class<A> var1, M var2, Locatable var3);

   boolean hasMethodAnnotation(Class<? extends Annotation> var1, M var2);

   @Nullable
   <A extends Annotation> A getMethodParameterAnnotation(Class<A> var1, M var2, int var3, Locatable var4);

   @Nullable
   <A extends Annotation> A getClassAnnotation(Class<A> var1, C var2, Locatable var3);

   @Nullable
   <A extends Annotation> A getPackageAnnotation(Class<A> var1, C var2, Locatable var3);

   T getClassValue(Annotation var1, String var2);

   T[] getClassArrayValue(Annotation var1, String var2);
}
