package javax.xml.bind;

public class MarshalException extends JAXBException {
   public MarshalException(String message) {
      this(message, (String)null, (Throwable)null);
   }

   public MarshalException(String message, String errorCode) {
      this(message, errorCode, (Throwable)null);
   }

   public MarshalException(Throwable exception) {
      this((String)null, (String)null, exception);
   }

   public MarshalException(String message, Throwable exception) {
      this(message, (String)null, exception);
   }

   public MarshalException(String message, String errorCode, Throwable exception) {
      super(message, errorCode, exception);
   }
}
