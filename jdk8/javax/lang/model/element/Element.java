package javax.lang.model.element;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.type.TypeMirror;

public interface Element extends AnnotatedConstruct {
   TypeMirror asType();

   ElementKind getKind();

   Set<Modifier> getModifiers();

   Name getSimpleName();

   Element getEnclosingElement();

   List<? extends Element> getEnclosedElements();

   boolean equals(Object var1);

   int hashCode();

   List<? extends AnnotationMirror> getAnnotationMirrors();

   <A extends Annotation> A getAnnotation(Class<A> var1);

   <R, P> R accept(ElementVisitor<R, P> var1, P var2);
}
