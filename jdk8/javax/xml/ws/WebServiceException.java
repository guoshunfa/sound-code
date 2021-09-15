package javax.xml.ws;

public class WebServiceException extends RuntimeException {
   public WebServiceException() {
   }

   public WebServiceException(String message) {
      super(message);
   }

   public WebServiceException(String message, Throwable cause) {
      super(message, cause);
   }

   public WebServiceException(Throwable cause) {
      super(cause);
   }
}
