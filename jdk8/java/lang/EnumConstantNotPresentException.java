package java.lang;

public class EnumConstantNotPresentException extends RuntimeException {
   private static final long serialVersionUID = -6046998521960521108L;
   private Class<? extends Enum> enumType;
   private String constantName;

   public EnumConstantNotPresentException(Class<? extends Enum> var1, String var2) {
      super(var1.getName() + "." + var2);
      this.enumType = var1;
      this.constantName = var2;
   }

   public Class<? extends Enum> enumType() {
      return this.enumType;
   }

   public String constantName() {
      return this.constantName;
   }
}
