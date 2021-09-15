package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleElementVisitor7<R, P> extends SimpleElementVisitor6<R, P> {
   protected SimpleElementVisitor7() {
      super((Object)null);
   }

   protected SimpleElementVisitor7(R var1) {
      super(var1);
   }

   public R visitVariable(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
