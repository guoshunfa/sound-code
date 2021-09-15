package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationSource {
   <A extends Annotation> A readAnnotation(Class<A> var1);

   boolean hasAnnotation(Class<? extends Annotation> var1);
}
