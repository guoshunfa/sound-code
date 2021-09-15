package java.lang.reflect;

public interface AnnotatedWildcardType extends AnnotatedType {
   AnnotatedType[] getAnnotatedLowerBounds();

   AnnotatedType[] getAnnotatedUpperBounds();
}
