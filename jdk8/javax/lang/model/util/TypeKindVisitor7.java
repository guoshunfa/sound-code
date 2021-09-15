package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.UnionType;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TypeKindVisitor7<R, P> extends TypeKindVisitor6<R, P> {
   protected TypeKindVisitor7() {
      super((Object)null);
   }

   protected TypeKindVisitor7(R var1) {
      super(var1);
   }

   public R visitUnion(UnionType var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
