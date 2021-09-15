package java.lang;

public class TypeNotPresentException extends RuntimeException {
   private static final long serialVersionUID = -5101214195716534496L;
   private String typeName;

   public TypeNotPresentException(String var1, Throwable var2) {
      super("Type " + var1 + " not present", var2);
      this.typeName = var1;
   }

   public String typeName() {
      return this.typeName;
   }
}
