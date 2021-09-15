package javax.xml.transform;

public class TransformerFactoryConfigurationError extends Error {
   private static final long serialVersionUID = -6527718720676281516L;
   private Exception exception;

   public TransformerFactoryConfigurationError() {
      this.exception = null;
   }

   public TransformerFactoryConfigurationError(String msg) {
      super(msg);
      this.exception = null;
   }

   public TransformerFactoryConfigurationError(Exception e) {
      super(e.toString());
      this.exception = e;
   }

   public TransformerFactoryConfigurationError(Exception e, String msg) {
      super(msg);
      this.exception = e;
   }

   public String getMessage() {
      String message = super.getMessage();
      return message == null && this.exception != null ? this.exception.getMessage() : message;
   }

   public Exception getException() {
      return this.exception;
   }

   public Throwable getCause() {
      return this.exception;
   }
}
