package javax.xml.ws;

public class ProtocolException extends WebServiceException {
   public ProtocolException() {
   }

   public ProtocolException(String message) {
      super(message);
   }

   public ProtocolException(String message, Throwable cause) {
      super(message, cause);
   }

   public ProtocolException(Throwable cause) {
      super(cause);
   }
}
