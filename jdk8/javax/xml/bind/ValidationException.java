package javax.xml.bind;

public class ValidationException extends JAXBException {
   public ValidationException(String message) {
      this(message, (String)null, (Throwable)null);
   }

   public ValidationException(String message, String errorCode) {
      this(message, errorCode, (Throwable)null);
   }

   public ValidationException(Throwable exception) {
      this((String)null, (String)null, exception);
   }

   public ValidationException(String message, Throwable exception) {
      this(message, (String)null, exception);
   }

   public ValidationException(String message, String errorCode, Throwable exception) {
      super(message, errorCode, exception);
   }
}
