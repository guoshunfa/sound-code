package javax.management.modelmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class InvalidTargetObjectTypeException extends Exception {
   private static final long oldSerialVersionUID = 3711724570458346634L;
   private static final long newSerialVersionUID = 1190536278266811217L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("msgStr", String.class), new ObjectStreamField("relatedExcept", Exception.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("exception", Exception.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   Exception exception;

   public InvalidTargetObjectTypeException() {
      super("InvalidTargetObjectTypeException: ");
      this.exception = null;
   }

   public InvalidTargetObjectTypeException(String var1) {
      super("InvalidTargetObjectTypeException: " + var1);
      this.exception = null;
   }

   public InvalidTargetObjectTypeException(Exception var1, String var2) {
      super("InvalidTargetObjectTypeException: " + var2 + (var1 != null ? "\n\t triggered by:" + var1.toString() : ""));
      this.exception = var1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.exception = (Exception)var2.get("relatedExcept", (Object)null);
         if (var2.defaulted("relatedExcept")) {
            throw new NullPointerException("relatedExcept");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("relatedExcept", this.exception);
         var2.put("msgStr", this.exception != null ? this.exception.getMessage() : "");
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = 3711724570458346634L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 1190536278266811217L;
      }

   }
}
