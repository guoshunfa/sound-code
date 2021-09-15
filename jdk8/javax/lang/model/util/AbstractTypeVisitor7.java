package javax.lang.model.util;

import javax.lang.model.type.UnionType;

public abstract class AbstractTypeVisitor7<R, P> extends AbstractTypeVisitor6<R, P> {
   protected AbstractTypeVisitor7() {
   }

   public abstract R visitUnion(UnionType var1, P var2);
}
