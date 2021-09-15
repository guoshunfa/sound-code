package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;

class NumericValueExp extends QueryEval implements ValueExp {
   private static final long oldSerialVersionUID = -6227876276058904000L;
   private static final long newSerialVersionUID = -4679739485102359104L;
   private static final ObjectStreamField[] oldSerialPersistentFields;
   private static final ObjectStreamField[] newSerialPersistentFields;
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private Number val = 0.0D;
   private static boolean compat;

   public NumericValueExp() {
   }

   NumericValueExp(Number var1) {
      this.val = var1;
   }

   public double doubleValue() {
      return !(this.val instanceof Long) && !(this.val instanceof Integer) ? this.val.doubleValue() : (double)this.val.longValue();
   }

   public long longValue() {
      return !(this.val instanceof Long) && !(this.val instanceof Integer) ? (long)this.val.doubleValue() : this.val.longValue();
   }

   public boolean isLong() {
      return this.val instanceof Long || this.val instanceof Integer;
   }

   public String toString() {
      if (this.val == null) {
         return "null";
      } else if (!(this.val instanceof Long) && !(this.val instanceof Integer)) {
         double var1 = this.val.doubleValue();
         if (Double.isInfinite(var1)) {
            return var1 > 0.0D ? "(1.0 / 0.0)" : "(-1.0 / 0.0)";
         } else {
            return Double.isNaN(var1) ? "(0.0 / 0.0)" : Double.toString(var1);
         }
      } else {
         return Long.toString(this.val.longValue());
      }
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      return this;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var7 = var1.readFields();
         double var2 = var7.get("doubleVal", 0.0D);
         if (var7.defaulted("doubleVal")) {
            throw new NullPointerException("doubleVal");
         }

         long var4 = var7.get("longVal", 0L);
         if (var7.defaulted("longVal")) {
            throw new NullPointerException("longVal");
         }

         boolean var6 = var7.get("valIsLong", false);
         if (var7.defaulted("valIsLong")) {
            throw new NullPointerException("valIsLong");
         }

         if (var6) {
            this.val = var4;
         } else {
            this.val = var2;
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("doubleVal", this.doubleValue());
         var2.put("longVal", this.longValue());
         var2.put("valIsLong", this.isLong());
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   /** @deprecated */
   @Deprecated
   public void setMBeanServer(MBeanServer var1) {
      super.setMBeanServer(var1);
   }

   static {
      oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("longVal", Long.TYPE), new ObjectStreamField("doubleVal", Double.TYPE), new ObjectStreamField("valIsLong", Boolean.TYPE)};
      newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("val", Number.class)};
      compat = false;

      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = -6227876276058904000L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -4679739485102359104L;
      }

   }
}
