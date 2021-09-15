package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.UnknownAnnotationValueException;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractAnnotationValueVisitor6<R, P> implements AnnotationValueVisitor<R, P> {
   protected AbstractAnnotationValueVisitor6() {
   }

   public final R visit(AnnotationValue var1, P var2) {
      return var1.accept(this, var2);
   }

   public final R visit(AnnotationValue var1) {
      return var1.accept(this, (Object)null);
   }

   public R visitUnknown(AnnotationValue var1, P var2) {
      throw new UnknownAnnotationValueException(var1, var2);
   }
}
