package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;

public class BadAttributeValueExpException extends Exception {
   private static final long serialVersionUID = -3105272988410493376L;
   private Object val;

   public BadAttributeValueExpException(Object var1) {
      this.val = var1 == null ? null : var1.toString();
   }

   public String toString() {
      return "BadAttributeValueException: " + this.val;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Object var3 = var2.get("val", (Object)null);
      if (var3 == null) {
         this.val = null;
      } else if (var3 instanceof String) {
         this.val = var3;
      } else if (System.getSecurityManager() != null && !(var3 instanceof Long) && !(var3 instanceof Integer) && !(var3 instanceof Float) && !(var3 instanceof Double) && !(var3 instanceof Byte) && !(var3 instanceof Short) && !(var3 instanceof Boolean)) {
         this.val = System.identityHashCode(var3) + "@" + var3.getClass().getName();
      } else {
         this.val = var3.toString();
      }

   }
}
