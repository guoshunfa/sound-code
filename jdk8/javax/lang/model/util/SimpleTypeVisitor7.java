package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.UnionType;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleTypeVisitor7<R, P> extends SimpleTypeVisitor6<R, P> {
   protected SimpleTypeVisitor7() {
      super((Object)null);
   }

   protected SimpleTypeVisitor7(R var1) {
      super(var1);
   }

   public R visitUnion(UnionType var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
