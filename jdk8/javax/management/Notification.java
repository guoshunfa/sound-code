package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.EventObject;

public class Notification extends EventObject {
   private static final long oldSerialVersionUID = 1716977971058914352L;
   private static final long newSerialVersionUID = -7516092053498031989L;
   private static final ObjectStreamField[] oldSerialPersistentFields;
   private static final ObjectStreamField[] newSerialPersistentFields;
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat;
   private String type;
   private long sequenceNumber;
   private long timeStamp;
   private Object userData = null;
   private String message = "";
   protected Object source = null;

   public Notification(String var1, Object var2, long var3) {
      super(var2);
      this.source = var2;
      this.type = var1;
      this.sequenceNumber = var3;
      this.timeStamp = (new Date()).getTime();
   }

   public Notification(String var1, Object var2, long var3, String var5) {
      super(var2);
      this.source = var2;
      this.type = var1;
      this.sequenceNumber = var3;
      this.timeStamp = (new Date()).getTime();
      this.message = var5;
   }

   public Notification(String var1, Object var2, long var3, long var5) {
      super(var2);
      this.source = var2;
      this.type = var1;
      this.sequenceNumber = var3;
      this.timeStamp = var5;
   }

   public Notification(String var1, Object var2, long var3, long var5, String var7) {
      super(var2);
      this.source = var2;
      this.type = var1;
      this.sequenceNumber = var3;
      this.timeStamp = var5;
      this.message = var7;
   }

   public void setSource(Object var1) {
      super.source = var1;
      this.source = var1;
   }

   public long getSequenceNumber() {
      return this.sequenceNumber;
   }

   public void setSequenceNumber(long var1) {
      this.sequenceNumber = var1;
   }

   public String getType() {
      return this.type;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
   }

   public String getMessage() {
      return this.message;
   }

   public Object getUserData() {
      return this.userData;
   }

   public void setUserData(Object var1) {
      this.userData = var1;
   }

   public String toString() {
      return super.toString() + "[type=" + this.type + "][message=" + this.message + "]";
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      super.source = this.source;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("type", this.type);
         var2.put("sequenceNumber", this.sequenceNumber);
         var2.put("timeStamp", this.timeStamp);
         var2.put("userData", this.userData);
         var2.put("message", this.message);
         var2.put("source", this.source);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", Long.TYPE), new ObjectStreamField("source", Object.class), new ObjectStreamField("sourceObjectName", ObjectName.class), new ObjectStreamField("timeStamp", Long.TYPE), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class)};
      newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", Long.TYPE), new ObjectStreamField("source", Object.class), new ObjectStreamField("timeStamp", Long.TYPE), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class)};
      compat = false;

      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = 1716977971058914352L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -7516092053498031989L;
      }

   }
}
