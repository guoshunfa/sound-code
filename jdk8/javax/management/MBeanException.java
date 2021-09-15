package javax.management;

public class MBeanException extends JMException {
   private static final long serialVersionUID = 4066342430588744142L;
   private Exception exception;

   public MBeanException(Exception var1) {
      this.exception = var1;
   }

   public MBeanException(Exception var1, String var2) {
      super(var2);
      this.exception = var1;
   }

   public Exception getTargetException() {
      return this.exception;
   }

   public Throwable getCause() {
      return this.exception;
   }
}
