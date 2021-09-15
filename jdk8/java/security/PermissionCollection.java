package java.security;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public abstract class PermissionCollection implements Serializable {
   private static final long serialVersionUID = -6727011328946861783L;
   private volatile boolean readOnly;

   public abstract void add(Permission var1);

   public abstract boolean implies(Permission var1);

   public abstract Enumeration<Permission> elements();

   public void setReadOnly() {
      this.readOnly = true;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public String toString() {
      Enumeration var1 = this.elements();
      StringBuilder var2 = new StringBuilder();
      var2.append(super.toString() + " (\n");

      while(var1.hasMoreElements()) {
         try {
            var2.append(" ");
            var2.append(((Permission)var1.nextElement()).toString());
            var2.append("\n");
         } catch (NoSuchElementException var4) {
         }
      }

      var2.append(")\n");
      return var2.toString();
   }
}
