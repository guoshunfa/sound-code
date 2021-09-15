package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.IntersectionType;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypeKindVisitor8<R, P> extends TypeKindVisitor7<R, P> {
   protected TypeKindVisitor8() {
      super((Object)null);
   }

   protected TypeKindVisitor8(R var1) {
      super(var1);
   }

   public R visitIntersection(IntersectionType var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
