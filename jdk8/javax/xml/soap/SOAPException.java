package javax.xml.soap;

public class SOAPException extends Exception {
   private Throwable cause;

   public SOAPException() {
      this.cause = null;
   }

   public SOAPException(String reason) {
      super(reason);
      this.cause = null;
   }

   public SOAPException(String reason, Throwable cause) {
      super(reason);
      this.initCause(cause);
   }

   public SOAPException(Throwable cause) {
      super(cause.toString());
      this.initCause(cause);
   }

   public String getMessage() {
      String message = super.getMessage();
      return message == null && this.cause != null ? this.cause.getMessage() : message;
   }

   public Throwable getCause() {
      return this.cause;
   }

   public synchronized Throwable initCause(Throwable cause) {
      if (this.cause != null) {
         throw new IllegalStateException("Can't override cause");
      } else if (cause == this) {
         throw new IllegalArgumentException("Self-causation not permitted");
      } else {
         this.cause = cause;
         return this;
      }
   }
}
