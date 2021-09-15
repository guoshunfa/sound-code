package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleTypeVisitor6<R, P> extends AbstractTypeVisitor6<R, P> {
   protected final R DEFAULT_VALUE;

   protected SimpleTypeVisitor6() {
      this.DEFAULT_VALUE = null;
   }

   protected SimpleTypeVisitor6(R var1) {
      this.DEFAULT_VALUE = var1;
   }

   protected R defaultAction(TypeMirror var1, P var2) {
      return this.DEFAULT_VALUE;
   }

   public R visitPrimitive(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitNull(NullType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitArray(ArrayType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitDeclared(DeclaredType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitError(ErrorType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitTypeVariable(TypeVariable var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitWildcard(WildcardType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitExecutable(ExecutableType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitNoType(NoType var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
