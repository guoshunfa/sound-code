package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Ref;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

public class SerialRef implements Ref, Serializable, Cloneable {
   private String baseTypeName;
   private Object object;
   private Ref reference;
   static final long serialVersionUID = -4727123500609662274L;

   public SerialRef(Ref var1) throws SerialException, SQLException {
      if (var1 == null) {
         throw new SQLException("Cannot instantiate a SerialRef object with a null Ref object");
      } else {
         this.reference = var1;
         this.object = var1;
         if (var1.getBaseTypeName() == null) {
            throw new SQLException("Cannot instantiate a SerialRef object that returns a null base type name");
         } else {
            this.baseTypeName = var1.getBaseTypeName();
         }
      }
   }

   public String getBaseTypeName() throws SerialException {
      return this.baseTypeName;
   }

   public Object getObject(Map<String, Class<?>> var1) throws SerialException {
      Hashtable var2 = new Hashtable(var1);
      if (this.object != null) {
         return var2.get(this.object);
      } else {
         throw new SerialException("The object is not set");
      }
   }

   public Object getObject() throws SerialException {
      if (this.reference != null) {
         try {
            return this.reference.getObject();
         } catch (SQLException var2) {
            throw new SerialException("SQLException: " + var2.getMessage());
         }
      } else if (this.object != null) {
         return this.object;
      } else {
         throw new SerialException("The object is not set");
      }
   }

   public void setObject(Object var1) throws SerialException {
      try {
         this.reference.setObject(var1);
      } catch (SQLException var3) {
         throw new SerialException("SQLException: " + var3.getMessage());
      }

      this.object = var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SerialRef)) {
         return false;
      } else {
         SerialRef var2 = (SerialRef)var1;
         return this.baseTypeName.equals(var2.baseTypeName) && this.object.equals(var2.object);
      }
   }

   public int hashCode() {
      return (31 + this.object.hashCode()) * 31 + this.baseTypeName.hashCode();
   }

   public Object clone() {
      try {
         SerialRef var1 = (SerialRef)super.clone();
         var1.reference = null;
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.object = var2.get("object", (Object)null);
      this.baseTypeName = (String)var2.get("baseTypeName", (Object)null);
      this.reference = (Ref)var2.get("reference", (Object)null);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("baseTypeName", this.baseTypeName);
      var2.put("object", this.object);
      var2.put("reference", this.reference instanceof Serializable ? this.reference : null);
      var1.writeFields();
   }
}
