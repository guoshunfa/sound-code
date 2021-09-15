package javax.management;

public class MBeanRegistrationException extends MBeanException {
   private static final long serialVersionUID = 4482382455277067805L;

   public MBeanRegistrationException(Exception var1) {
      super(var1);
   }

   public MBeanRegistrationException(Exception var1, String var2) {
      super(var1, var2);
   }
}
