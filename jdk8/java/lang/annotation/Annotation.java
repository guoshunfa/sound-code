package java.lang.annotation;

public interface Annotation {
   boolean equals(Object var1);

   int hashCode();

   String toString();

   Class<? extends Annotation> annotationType();
}
