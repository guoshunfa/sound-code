package javax.lang.model.util;

import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.UnknownTypeException;

public abstract class AbstractTypeVisitor6<R, P> implements TypeVisitor<R, P> {
   protected AbstractTypeVisitor6() {
   }

   public final R visit(TypeMirror var1, P var2) {
      return var1.accept(this, var2);
   }

   public final R visit(TypeMirror var1) {
      return var1.accept(this, (Object)null);
   }

   public R visitUnion(UnionType var1, P var2) {
      return this.visitUnknown(var1, var2);
   }

   public R visitIntersection(IntersectionType var1, P var2) {
      return this.visitUnknown(var1, var2);
   }

   public R visitUnknown(TypeMirror var1, P var2) {
      throw new UnknownTypeException(var1, var2);
   }
}
