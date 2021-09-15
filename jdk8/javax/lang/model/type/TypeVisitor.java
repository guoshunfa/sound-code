package javax.lang.model.type;

public interface TypeVisitor<R, P> {
   R visit(TypeMirror var1, P var2);

   R visit(TypeMirror var1);

   R visitPrimitive(PrimitiveType var1, P var2);

   R visitNull(NullType var1, P var2);

   R visitArray(ArrayType var1, P var2);

   R visitDeclared(DeclaredType var1, P var2);

   R visitError(ErrorType var1, P var2);

   R visitTypeVariable(TypeVariable var1, P var2);

   R visitWildcard(WildcardType var1, P var2);

   R visitExecutable(ExecutableType var1, P var2);

   R visitNoType(NoType var1, P var2);

   R visitUnknown(TypeMirror var1, P var2);

   R visitUnion(UnionType var1, P var2);

   R visitIntersection(IntersectionType var1, P var2);
}
