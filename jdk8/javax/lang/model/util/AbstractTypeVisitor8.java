package javax.lang.model.util;

import javax.lang.model.type.IntersectionType;

public abstract class AbstractTypeVisitor8<R, P> extends AbstractTypeVisitor7<R, P> {
   protected AbstractTypeVisitor8() {
   }

   public abstract R visitIntersection(IntersectionType var1, P var2);
}
