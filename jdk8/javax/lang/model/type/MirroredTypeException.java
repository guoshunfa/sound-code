package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MirroredTypeException extends MirroredTypesException {
   private static final long serialVersionUID = 269L;
   private transient TypeMirror type;

   public MirroredTypeException(TypeMirror var1) {
      super("Attempt to access Class object for TypeMirror " + var1.toString(), var1);
      this.type = var1;
   }

   public TypeMirror getTypeMirror() {
      return this.type;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.type = null;
      this.types = null;
   }
}
