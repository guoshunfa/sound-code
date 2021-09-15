package sun.reflect.annotation;

public class TypeNotPresentExceptionProxy extends ExceptionProxy {
   private static final long serialVersionUID = 5565925172427947573L;
   String typeName;
   Throwable cause;

   public TypeNotPresentExceptionProxy(String var1, Throwable var2) {
      this.typeName = var1;
      this.cause = var2;
   }

   protected RuntimeException generateException() {
      return new TypeNotPresentException(this.typeName, this.cause);
   }
}
