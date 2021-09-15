package javax.xml.bind;

public class DataBindingException extends RuntimeException {
   public DataBindingException(String message, Throwable cause) {
      super(message, cause);
   }

   public DataBindingException(Throwable cause) {
      super(cause);
   }
}
