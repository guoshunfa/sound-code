package java.lang.annotation;

public class IncompleteAnnotationException extends RuntimeException {
   private static final long serialVersionUID = 8445097402741811912L;
   private Class<? extends Annotation> annotationType;
   private String elementName;

   public IncompleteAnnotationException(Class<? extends Annotation> var1, String var2) {
      super(var1.getName() + " missing element " + var2.toString());
      this.annotationType = var1;
      this.elementName = var2;
   }

   public Class<? extends Annotation> annotationType() {
      return this.annotationType;
   }

   public String elementName() {
      return this.elementName;
   }
}
