package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Principal;

public class JMXPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -4184480100214577411L;
   private String name;

   public JMXPrincipal(String var1) {
      validate(var1);
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return "JMXPrincipal:  " + this.name;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof JMXPrincipal)) {
         return false;
      } else {
         JMXPrincipal var2 = (JMXPrincipal)var1;
         return this.getName().equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("name", (Object)null);

      try {
         validate(var3);
         this.name = var3;
      } catch (NullPointerException var5) {
         throw new InvalidObjectException(var5.getMessage());
      }
   }

   private static void validate(String var0) throws NullPointerException {
      if (var0 == null) {
         throw new NullPointerException("illegal null input");
      }
   }
}
