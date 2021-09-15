package java.lang;

public class ClassNotFoundException extends ReflectiveOperationException {
   private static final long serialVersionUID = 9176873029745254542L;
   private Throwable ex;

   public ClassNotFoundException() {
      super((Throwable)null);
   }

   public ClassNotFoundException(String var1) {
      super(var1, (Throwable)null);
   }

   public ClassNotFoundException(String var1, Throwable var2) {
      super(var1, (Throwable)null);
      this.ex = var2;
   }

   public Throwable getException() {
      return this.ex;
   }

   public Throwable getCause() {
      return this.ex;
   }
}
