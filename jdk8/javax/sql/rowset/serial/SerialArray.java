package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;

public class SerialArray implements Array, Serializable, Cloneable {
   private Object[] elements;
   private int baseType;
   private String baseTypeName;
   private int len;
   static final long serialVersionUID = -8466174297270688520L;

   public SerialArray(Array var1, Map<String, Class<?>> var2) throws SerialException, SQLException {
      if (var1 != null && var2 != null) {
         if ((this.elements = (Object[])((Object[])var1.getArray())) == null) {
            throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
         } else {
            this.elements = (Object[])((Object[])var1.getArray(var2));
            this.baseType = var1.getBaseType();
            this.baseTypeName = var1.getBaseTypeName();
            this.len = this.elements.length;
            int var3;
            switch(this.baseType) {
            case 70:
               for(var3 = 0; var3 < this.len; ++var3) {
                  this.elements[var3] = new SerialDatalink((URL)this.elements[var3]);
               }

               return;
            case 2000:
               for(var3 = 0; var3 < this.len; ++var3) {
                  this.elements[var3] = new SerialJavaObject(this.elements[var3]);
               }

               return;
            case 2002:
               for(var3 = 0; var3 < this.len; ++var3) {
                  this.elements[var3] = new SerialStruct((Struct)this.elements[var3], var2);
               }

               return;
            case 2003:
               for(var3 = 0; var3 < this.len; ++var3) {
                  this.elements[var3] = new SerialArray((Array)this.elements[var3], var2);
               }

               return;
            case 2004:
               for(var3 = 0; var3 < this.len; ++var3) {
                  this.elements[var3] = new SerialBlob((Blob)this.elements[var3]);
               }

               return;
            case 2005:
               for(var3 = 0; var3 < this.len; ++var3) {
                  this.elements[var3] = new SerialClob((Clob)this.elements[var3]);
               }
            }

         }
      } else {
         throw new SQLException("Cannot instantiate a SerialArray object with null parameters");
      }
   }

   public void free() throws SQLException {
      if (this.elements != null) {
         this.elements = null;
         this.baseTypeName = null;
      }

   }

   public SerialArray(Array var1) throws SerialException, SQLException {
      if (var1 == null) {
         throw new SQLException("Cannot instantiate a SerialArray object with a null Array object");
      } else if ((this.elements = (Object[])((Object[])var1.getArray())) == null) {
         throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
      } else {
         this.baseType = var1.getBaseType();
         this.baseTypeName = var1.getBaseTypeName();
         this.len = this.elements.length;
         int var2;
         switch(this.baseType) {
         case 70:
            for(var2 = 0; var2 < this.len; ++var2) {
               this.elements[var2] = new SerialDatalink((URL)this.elements[var2]);
            }

            return;
         case 2000:
            for(var2 = 0; var2 < this.len; ++var2) {
               this.elements[var2] = new SerialJavaObject(this.elements[var2]);
            }

            return;
         case 2004:
            for(var2 = 0; var2 < this.len; ++var2) {
               this.elements[var2] = new SerialBlob((Blob)this.elements[var2]);
            }

            return;
         case 2005:
            for(var2 = 0; var2 < this.len; ++var2) {
               this.elements[var2] = new SerialClob((Clob)this.elements[var2]);
            }
         }

      }
   }

   public Object getArray() throws SerialException {
      this.isValid();
      Object[] var1 = new Object[this.len];
      System.arraycopy(this.elements, 0, var1, 0, this.len);
      return var1;
   }

   public Object getArray(Map<String, Class<?>> var1) throws SerialException {
      this.isValid();
      Object[] var2 = new Object[this.len];
      System.arraycopy(this.elements, 0, var2, 0, this.len);
      return var2;
   }

   public Object getArray(long var1, int var3) throws SerialException {
      this.isValid();
      Object[] var4 = new Object[var3];
      System.arraycopy(this.elements, (int)var1, var4, 0, var3);
      return var4;
   }

   public Object getArray(long var1, int var3, Map<String, Class<?>> var4) throws SerialException {
      this.isValid();
      Object[] var5 = new Object[var3];
      System.arraycopy(this.elements, (int)var1, var5, 0, var3);
      return var5;
   }

   public int getBaseType() throws SerialException {
      this.isValid();
      return this.baseType;
   }

   public String getBaseTypeName() throws SerialException {
      this.isValid();
      return this.baseTypeName;
   }

   public ResultSet getResultSet(long var1, int var3) throws SerialException {
      SerialException var4 = new SerialException();
      var4.initCause(new UnsupportedOperationException());
      throw var4;
   }

   public ResultSet getResultSet(Map<String, Class<?>> var1) throws SerialException {
      SerialException var2 = new SerialException();
      var2.initCause(new UnsupportedOperationException());
      throw var2;
   }

   public ResultSet getResultSet() throws SerialException {
      SerialException var1 = new SerialException();
      var1.initCause(new UnsupportedOperationException());
      throw var1;
   }

   public ResultSet getResultSet(long var1, int var3, Map<String, Class<?>> var4) throws SerialException {
      SerialException var5 = new SerialException();
      var5.initCause(new UnsupportedOperationException());
      throw var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SerialArray)) {
         return false;
      } else {
         SerialArray var2 = (SerialArray)var1;
         return this.baseType == var2.baseType && this.baseTypeName.equals(var2.baseTypeName) && Arrays.equals(this.elements, var2.elements);
      }
   }

   public int hashCode() {
      return (((31 + Arrays.hashCode(this.elements)) * 31 + this.len) * 31 + this.baseType) * 31 + this.baseTypeName.hashCode();
   }

   public Object clone() {
      try {
         SerialArray var1 = (SerialArray)super.clone();
         var1.elements = this.elements != null ? Arrays.copyOf(this.elements, this.len) : null;
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Object[] var3 = (Object[])((Object[])var2.get("elements", (Object)null));
      if (var3 == null) {
         throw new InvalidObjectException("elements is null and should not be!");
      } else {
         this.elements = (Object[])var3.clone();
         this.len = var2.get("len", (int)0);
         if (this.elements.length != this.len) {
            throw new InvalidObjectException("elements is not the expected size");
         } else {
            this.baseType = var2.get("baseType", (int)0);
            this.baseTypeName = (String)var2.get("baseTypeName", (Object)null);
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("elements", this.elements);
      var2.put("len", this.len);
      var2.put("baseType", this.baseType);
      var2.put("baseTypeName", this.baseTypeName);
      var1.writeFields();
   }

   private void isValid() throws SerialException {
      if (this.elements == null) {
         throw new SerialException("Error: You cannot call a method on a SerialArray instance once free() has been called.");
      }
   }
}
