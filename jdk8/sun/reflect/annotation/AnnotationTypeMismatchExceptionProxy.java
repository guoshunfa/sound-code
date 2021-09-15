package sun.reflect.annotation;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Method;

class AnnotationTypeMismatchExceptionProxy extends ExceptionProxy {
   private static final long serialVersionUID = 7844069490309503934L;
   private Method member;
   private String foundType;

   AnnotationTypeMismatchExceptionProxy(String var1) {
      this.foundType = var1;
   }

   AnnotationTypeMismatchExceptionProxy setMember(Method var1) {
      this.member = var1;
      return this;
   }

   protected RuntimeException generateException() {
      return new AnnotationTypeMismatchException(this.member, this.foundType);
   }
}
