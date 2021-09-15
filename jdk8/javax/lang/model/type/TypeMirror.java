package javax.lang.model.type;

import javax.lang.model.AnnotatedConstruct;

public interface TypeMirror extends AnnotatedConstruct {
   TypeKind getKind();

   boolean equals(Object var1);

   int hashCode();

   String toString();

   <R, P> R accept(TypeVisitor<R, P> var1, P var2);
}
