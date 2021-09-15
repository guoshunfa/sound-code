package java.util;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InvalidPropertiesFormatException extends IOException {
   private static final long serialVersionUID = 7763056076009360219L;

   public InvalidPropertiesFormatException(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.initCause(var1);
   }

   public InvalidPropertiesFormatException(String var1) {
      super(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws NotSerializableException {
      throw new NotSerializableException("Not serializable.");
   }

   private void readObject(ObjectInputStream var1) throws NotSerializableException {
      throw new NotSerializableException("Not serializable.");
   }
}
