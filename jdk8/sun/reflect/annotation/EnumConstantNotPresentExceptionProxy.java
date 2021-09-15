package sun.reflect.annotation;

public class EnumConstantNotPresentExceptionProxy extends ExceptionProxy {
   private static final long serialVersionUID = -604662101303187330L;
   Class<? extends Enum<?>> enumType;
   String constName;

   public EnumConstantNotPresentExceptionProxy(Class<? extends Enum<?>> var1, String var2) {
      this.enumType = var1;
      this.constName = var2;
   }

   protected RuntimeException generateException() {
      return new EnumConstantNotPresentException(this.enumType, this.constName);
   }
}
