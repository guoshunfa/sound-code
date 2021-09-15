package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class SerialStruct implements Struct, Serializable, Cloneable {
   private String SQLTypeName;
   private Object[] attribs;
   static final long serialVersionUID = -8322445504027483372L;

   public SerialStruct(Struct var1, Map<String, Class<?>> var2) throws SerialException {
      try {
         this.SQLTypeName = var1.getSQLTypeName();
         System.out.println("SQLTypeName: " + this.SQLTypeName);
         this.attribs = var1.getAttributes(var2);
         this.mapToSerial(var2);
      } catch (SQLException var4) {
         throw new SerialException(var4.getMessage());
      }
   }

   public SerialStruct(SQLData var1, Map<String, Class<?>> var2) throws SerialException {
      try {
         this.SQLTypeName = var1.getSQLTypeName();
         Vector var3 = new Vector();
         var1.writeSQL(new SQLOutputImpl(var3, var2));
         this.attribs = var3.toArray();
      } catch (SQLException var4) {
         throw new SerialException(var4.getMessage());
      }
   }

   public String getSQLTypeName() throws SerialException {
      return this.SQLTypeName;
   }

   public Object[] getAttributes() throws SerialException {
      Object[] var1 = this.attribs;
      return var1 == null ? null : Arrays.copyOf(var1, var1.length);
   }

   public Object[] getAttributes(Map<String, Class<?>> var1) throws SerialException {
      Object[] var2 = this.attribs;
      return var2 == null ? null : Arrays.copyOf(var2, var2.length);
   }

   private void mapToSerial(Map<String, Class<?>> var1) throws SerialException {
      try {
         for(int var2 = 0; var2 < this.attribs.length; ++var2) {
            if (this.attribs[var2] instanceof Struct) {
               this.attribs[var2] = new SerialStruct((Struct)this.attribs[var2], var1);
            } else if (this.attribs[var2] instanceof SQLData) {
               this.attribs[var2] = new SerialStruct((SQLData)this.attribs[var2], var1);
            } else if (this.attribs[var2] instanceof Blob) {
               this.attribs[var2] = new SerialBlob((Blob)this.attribs[var2]);
            } else if (this.attribs[var2] instanceof Clob) {
               this.attribs[var2] = new SerialClob((Clob)this.attribs[var2]);
            } else if (this.attribs[var2] instanceof Ref) {
               this.attribs[var2] = new SerialRef((Ref)this.attribs[var2]);
            } else if (this.attribs[var2] instanceof Array) {
               this.attribs[var2] = new SerialArray((Array)this.attribs[var2], var1);
            }
         }

      } catch (SQLException var3) {
         throw new SerialException(var3.getMessage());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SerialStruct)) {
         return false;
      } else {
         SerialStruct var2 = (SerialStruct)var1;
         return this.SQLTypeName.equals(var2.SQLTypeName) && Arrays.equals(this.attribs, var2.attribs);
      }
   }

   public int hashCode() {
      return (31 + Arrays.hashCode(this.attribs)) * 31 * 31 + this.SQLTypeName.hashCode();
   }

   public Object clone() {
      try {
         SerialStruct var1 = (SerialStruct)super.clone();
         var1.attribs = Arrays.copyOf(this.attribs, this.attribs.length);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Object[] var3 = (Object[])((Object[])var2.get("attribs", (Object)null));
      this.attribs = var3 == null ? null : (Object[])var3.clone();
      this.SQLTypeName = (String)var2.get("SQLTypeName", (Object)null);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("attribs", this.attribs);
      var2.put("SQLTypeName", this.SQLTypeName);
      var1.writeFields();
   }
}
