package java.nio.file;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public final class DirectoryIteratorException extends ConcurrentModificationException {
   private static final long serialVersionUID = -6012699886086212874L;

   public DirectoryIteratorException(IOException var1) {
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
