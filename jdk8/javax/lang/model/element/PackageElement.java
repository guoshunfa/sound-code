package javax.lang.model.element;

import java.util.List;

public interface PackageElement extends Element, QualifiedNameable {
   Name getQualifiedName();

   Name getSimpleName();

   List<? extends Element> getEnclosedElements();

   boolean isUnnamed();

   Element getEnclosingElement();
}
