package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MirroredTypesException extends RuntimeException {
   private static final long serialVersionUID = 269L;
   transient List<? extends TypeMirror> types;

   MirroredTypesException(String var1, TypeMirror var2) {
      super(var1);
      ArrayList var3 = new ArrayList();
      var3.add(var2);
      this.types = Collections.unmodifiableList(var3);
   }

   public MirroredTypesException(List<? extends TypeMirror> var1) {
      ArrayList var2;
      super("Attempt to access Class objects for TypeMirrors " + (var2 = new ArrayList(var1)).toString());
      this.types = Collections.unmodifiableList(var2);
   }

   public List<? extends TypeMirror> getTypeMirrors() {
      return this.types;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.types = null;
   }
}
