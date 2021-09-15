package javax.lang.model.element;

public interface ElementVisitor<R, P> {
   R visit(Element var1, P var2);

   R visit(Element var1);

   R visitPackage(PackageElement var1, P var2);

   R visitType(TypeElement var1, P var2);

   R visitVariable(VariableElement var1, P var2);

   R visitExecutable(ExecutableElement var1, P var2);

   R visitTypeParameter(TypeParameterElement var1, P var2);

   R visitUnknown(Element var1, P var2);
}
