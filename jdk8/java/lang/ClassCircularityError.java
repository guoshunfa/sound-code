package java.lang;

public class ClassCircularityError extends LinkageError {
   private static final long serialVersionUID = 1054362542914539689L;

   public ClassCircularityError() {
   }

   public ClassCircularityError(String var1) {
      super(var1);
   }
}
