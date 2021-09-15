package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ElementScanner7<R, P> extends ElementScanner6<R, P> {
   protected ElementScanner7() {
      super((Object)null);
   }

   protected ElementScanner7(R var1) {
      super(var1);
   }

   public R visitVariable(VariableElement var1, P var2) {
      return this.scan(var1.getEnclosedElements(), var2);
   }
}
