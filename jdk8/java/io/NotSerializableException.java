package java.io;

public class NotSerializableException extends ObjectStreamException {
   private static final long serialVersionUID = 2906642554793891381L;

   public NotSerializableException(String var1) {
      super(var1);
   }

   public NotSerializableException() {
   }
}
