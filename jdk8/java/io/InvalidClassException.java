package java.io;

public class InvalidClassException extends ObjectStreamException {
   private static final long serialVersionUID = -4333316296251054416L;
   public String classname;

   public InvalidClassException(String var1) {
      super(var1);
   }

   public InvalidClassException(String var1, String var2) {
      super(var2);
      this.classname = var1;
   }

   public String getMessage() {
      return this.classname == null ? super.getMessage() : this.classname + "; " + super.getMessage();
   }
}
