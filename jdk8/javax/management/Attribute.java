package javax.management;

import java.io.Serializable;

public class Attribute implements Serializable {
   private static final long serialVersionUID = 2484220110589082382L;
   private String name;
   private Object value = null;

   public Attribute(String var1, Object var2) {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null "));
      } else {
         this.name = var1;
         this.value = var2;
      }
   }

   public String getName() {
      return this.name;
   }

   public Object getValue() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Attribute)) {
         return false;
      } else {
         Attribute var2 = (Attribute)var1;
         if (this.value == null) {
            return var2.getValue() == null ? this.name.equals(var2.getName()) : false;
         } else {
            return this.name.equals(var2.getName()) && this.value.equals(var2.getValue());
         }
      }
   }

   public int hashCode() {
      return this.name.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
   }

   public String toString() {
      return this.getName() + " = " + this.getValue();
   }
}
