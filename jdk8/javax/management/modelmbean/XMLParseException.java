package javax.management.modelmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class XMLParseException extends Exception {
   private static final long oldSerialVersionUID = -7780049316655891976L;
   private static final long newSerialVersionUID = 3176664577895105181L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("msgStr", String.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[0];
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;

   public XMLParseException() {
      super("XML Parse Exception.");
   }

   public XMLParseException(String var1) {
      super("XML Parse Exception: " + var1);
   }

   public XMLParseException(Exception var1, String var2) {
      super("XML Parse Exception: " + var2 + ":" + var1.toString());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("msgStr", this.getMessage());
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
         serialVersionUID = -7780049316655891976L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 3176664577895105181L;
      }

   }
}
