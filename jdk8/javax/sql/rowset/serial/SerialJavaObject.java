package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;
import javax.sql.rowset.RowSetWarning;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class SerialJavaObject implements Serializable, Cloneable {
   private Object obj;
   private transient Field[] fields;
   static final long serialVersionUID = -1465795139032831023L;
   Vector<RowSetWarning> chain;

   public SerialJavaObject(Object var1) throws SerialException {
      Class var2 = var1.getClass();
      if (!(var1 instanceof Serializable)) {
         this.setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable"));
      }

      this.fields = var2.getFields();
      if (hasStaticFields(this.fields)) {
         throw new SerialException("Located static fields in object instance. Cannot serialize");
      } else {
         this.obj = var1;
      }
   }

   public Object getObject() throws SerialException {
      return this.obj;
   }

   @CallerSensitive
   public Field[] getFields() throws SerialException {
      if (this.fields != null) {
         Class var1 = this.obj.getClass();
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            Class var3 = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(var3.getClassLoader(), var1.getClassLoader())) {
               ReflectUtil.checkPackageAccess(var1);
            }
         }

         return var1.getFields();
      } else {
         throw new SerialException("SerialJavaObject does not contain a serialized object instance");
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof SerialJavaObject) {
         SerialJavaObject var2 = (SerialJavaObject)var1;
         return this.obj.equals(var2.obj);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 + this.obj.hashCode();
   }

   public Object clone() {
      try {
         SerialJavaObject var1 = (SerialJavaObject)super.clone();
         var1.fields = (Field[])Arrays.copyOf((Object[])this.fields, this.fields.length);
         if (this.chain != null) {
            var1.chain = new Vector(this.chain);
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void setWarning(RowSetWarning var1) {
      if (this.chain == null) {
         this.chain = new Vector();
      }

      this.chain.add(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Vector var3 = (Vector)var2.get("chain", (Object)null);
      if (var3 != null) {
         this.chain = new Vector(var3);
      }

      this.obj = var2.get("obj", (Object)null);
      if (this.obj != null) {
         this.fields = this.obj.getClass().getFields();
         if (hasStaticFields(this.fields)) {
            throw new IOException("Located static fields in object instance. Cannot serialize");
         }
      } else {
         throw new IOException("Object cannot be null!");
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("obj", this.obj);
      var2.put("chain", this.chain);
      var1.writeFields();
   }

   private static boolean hasStaticFields(Field[] var0) {
      Field[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Field var4 = var1[var3];
         if (var4.getModifiers() == 8) {
            return true;
         }
      }

      return false;
   }
}
