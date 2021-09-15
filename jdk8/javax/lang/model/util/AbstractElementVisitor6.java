package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.UnknownElementException;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractElementVisitor6<R, P> implements ElementVisitor<R, P> {
   protected AbstractElementVisitor6() {
   }

   public final R visit(Element var1, P var2) {
      return var1.accept(this, var2);
   }

   public final R visit(Element var1) {
      return var1.accept(this, (Object)null);
   }

   public R visitUnknown(Element var1, P var2) {
      throw new UnknownElementException(var1, var2);
   }
}
