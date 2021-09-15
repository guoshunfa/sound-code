package javax.lang.model.util;

import java.util.Iterator;
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
public class ElementScanner6<R, P> extends AbstractElementVisitor6<R, P> {
   protected final R DEFAULT_VALUE;

   protected ElementScanner6() {
      this.DEFAULT_VALUE = null;
   }

   protected ElementScanner6(R var1) {
      this.DEFAULT_VALUE = var1;
   }

   public final R scan(Iterable<? extends Element> var1, P var2) {
      Object var3 = this.DEFAULT_VALUE;

      Element var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = this.scan(var5, var2)) {
         var5 = (Element)var4.next();
      }

      return var3;
   }

   public R scan(Element var1, P var2) {
      return var1.accept(this, var2);
   }

   public final R scan(Element var1) {
      return this.scan((Element)var1, (Object)null);
   }

   public R visitPackage(PackageElement var1, P var2) {
      return this.scan((Iterable)var1.getEnclosedElements(), var2);
   }

   public R visitType(TypeElement var1, P var2) {
      return this.scan((Iterable)var1.getEnclosedElements(), var2);
   }

   public R visitVariable(VariableElement var1, P var2) {
      return var1.getKind() != ElementKind.RESOURCE_VARIABLE ? this.scan((Iterable)var1.getEnclosedElements(), var2) : this.visitUnknown(var1, var2);
   }

   public R visitExecutable(ExecutableElement var1, P var2) {
      return this.scan((Iterable)var1.getParameters(), var2);
   }

   public R visitTypeParameter(TypeParameterElement var1, P var2) {
      return this.scan((Iterable)var1.getEnclosedElements(), var2);
   }
}
