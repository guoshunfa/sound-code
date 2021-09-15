package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.IntersectionType;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleTypeVisitor8<R, P> extends SimpleTypeVisitor7<R, P> {
   protected SimpleTypeVisitor8() {
      super((Object)null);
   }

   protected SimpleTypeVisitor8(R var1) {
      super(var1);
   }

   public R visitIntersection(IntersectionType var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
