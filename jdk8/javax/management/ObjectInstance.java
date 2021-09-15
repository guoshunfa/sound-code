package javax.management;

import java.io.Serializable;

public class ObjectInstance implements Serializable {
   private static final long serialVersionUID = -4099952623687795850L;
   private ObjectName name;
   private String className;

   public ObjectInstance(String var1, String var2) throws MalformedObjectNameException {
      this(new ObjectName(var1), var2);
   }

   public ObjectInstance(ObjectName var1, String var2) {
      if (var1.isPattern()) {
         IllegalArgumentException var3 = new IllegalArgumentException("Invalid name->" + var1.toString());
         throw new RuntimeOperationsException(var3);
      } else {
         this.name = var1;
         this.className = var2;
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectInstance)) {
         return false;
      } else {
         ObjectInstance var2 = (ObjectInstance)var1;
         if (!this.name.equals(var2.getObjectName())) {
            return false;
         } else if (this.className == null) {
            return var2.getClassName() == null;
         } else {
            return this.className.equals(var2.getClassName());
         }
      }
   }

   public int hashCode() {
      int var1 = this.className == null ? 0 : this.className.hashCode();
      return this.name.hashCode() ^ var1;
   }

   public ObjectName getObjectName() {
      return this.name;
   }

   public String getClassName() {
      return this.className;
   }

   public String toString() {
      return this.getClassName() + "[" + this.getObjectName() + "]";
   }
}
