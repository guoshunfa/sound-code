package java.security;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GuardedObject implements Serializable {
   private static final long serialVersionUID = -5240450096227834308L;
   private Object object;
   private Guard guard;

   public GuardedObject(Object var1, Guard var2) {
      this.guard = var2;
      this.object = var1;
   }

   public Object getObject() throws SecurityException {
      if (this.guard != null) {
         this.guard.checkGuard(this.object);
      }

      return this.object;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.guard != null) {
         this.guard.checkGuard(this.object);
      }

      var1.defaultWriteObject();
   }
}
