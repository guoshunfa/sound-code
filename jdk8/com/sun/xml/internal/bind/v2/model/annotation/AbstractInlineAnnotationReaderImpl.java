package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;

public abstract class AbstractInlineAnnotationReaderImpl<T, C, F, M> implements AnnotationReader<T, C, F, M> {
   private ErrorHandler errorHandler;

   public void setErrorHandler(ErrorHandler errorHandler) {
      if (errorHandler == null) {
         throw new IllegalArgumentException();
      } else {
         this.errorHandler = errorHandler;
      }
   }

   public final ErrorHandler getErrorHandler() {
      assert this.errorHandler != null : "error handler must be set before use";

      return this.errorHandler;
   }

   public final <A extends Annotation> A getMethodAnnotation(Class<A> annotation, M getter, M setter, Locatable srcPos) {
      A a1 = getter == null ? null : this.getMethodAnnotation(annotation, getter, srcPos);
      A a2 = setter == null ? null : this.getMethodAnnotation(annotation, setter, srcPos);
      if (a1 == null) {
         return a2 == null ? null : a2;
      } else if (a2 == null) {
         return a1;
      } else {
         this.getErrorHandler().error(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(annotation.getName(), this.fullName(getter), this.fullName(setter)), a1, a2));
         return a1;
      }
   }

   public boolean hasMethodAnnotation(Class<? extends Annotation> annotation, String propertyName, M getter, M setter, Locatable srcPos) {
      boolean x = getter != null && this.hasMethodAnnotation(annotation, getter);
      boolean y = setter != null && this.hasMethodAnnotation(annotation, setter);
      if (x && y) {
         this.getMethodAnnotation(annotation, getter, setter, srcPos);
      }

      return x || y;
   }

   protected abstract String fullName(M var1);
}
