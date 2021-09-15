package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleElementVisitor6<R, P> extends AbstractElementVisitor6<R, P> {
   protected final R DEFAULT_VALUE;

   protected SimpleElementVisitor6() {
      this.DEFAULT_VALUE = null;
   }

   protected SimpleElementVisitor6(R var1) {
      this.DEFAULT_VALUE = var1;
   }

   protected R defaultAction(Element var1, P var2) {
      return this.DEFAULT_VALUE;
   }

   public R visitPackage(PackageElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitType(TypeElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariable(VariableElement var1, P var2) {
      return var1.getKind() != ElementKind.RESOURCE_VARIABLE ? this.defaultAction(var1, var2) : this.visitUnknown(var1, var2);
   }

   public R visitExecutable(ExecutableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitTypeParameter(TypeParameterElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
