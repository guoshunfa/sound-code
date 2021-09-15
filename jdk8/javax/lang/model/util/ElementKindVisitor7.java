package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ElementKindVisitor7<R, P> extends ElementKindVisitor6<R, P> {
   protected ElementKindVisitor7() {
      super((Object)null);
   }

   protected ElementKindVisitor7(R var1) {
      super(var1);
   }

   public R visitVariableAsResourceVariable(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
