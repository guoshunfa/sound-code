package java.io;

import java.util.Objects;

public class UncheckedIOException extends RuntimeException {
   private static final long serialVersionUID = -8134305061645241065L;

   public UncheckedIOException(String var1, IOException var2) {
      super(var1, (Throwable)Objects.requireNonNull(var2));
   }

   public UncheckedIOException(IOException var1) {
      super((Throwable)Objects.requireNonNull(var1));
   }

   public IOException getCause() {
      return (IOException)super.getCause();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Throwable var2 = super.getCause();
      if (!(var2 instanceof IOException)) {
         throw new InvalidObjectException("Cause must be an IOException");
      }
   }
}
