package javax.lang.model.util;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public interface Elements {
   PackageElement getPackageElement(CharSequence var1);

   TypeElement getTypeElement(CharSequence var1);

   Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror var1);

   String getDocComment(Element var1);

   boolean isDeprecated(Element var1);

   Name getBinaryName(TypeElement var1);

   PackageElement getPackageOf(Element var1);

   List<? extends Element> getAllMembers(TypeElement var1);

   List<? extends AnnotationMirror> getAllAnnotationMirrors(Element var1);

   boolean hides(Element var1, Element var2);

   boolean overrides(ExecutableElement var1, ExecutableElement var2, TypeElement var3);

   String getConstantExpression(Object var1);

   void printElements(Writer var1, Element... var2);

   Name getName(CharSequence var1);

   boolean isFunctionalInterface(TypeElement var1);
}
