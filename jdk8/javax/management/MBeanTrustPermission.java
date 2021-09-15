package javax.management;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;

public class MBeanTrustPermission extends BasicPermission {
   private static final long serialVersionUID = -2952178077029018140L;

   public MBeanTrustPermission(String var1) {
      this(var1, (String)null);
   }

   public MBeanTrustPermission(String var1, String var2) {
      super(var1, var2);
      validate(var1, var2);
   }

   private static void validate(String var0, String var1) {
      if (var1 != null && var1.length() > 0) {
         throw new IllegalArgumentException("MBeanTrustPermission actions must be null: " + var1);
      } else if (!var0.equals("register") && !var0.equals("*")) {
         throw new IllegalArgumentException("MBeanTrustPermission: Unknown target name [" + var0 + "]");
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         validate(super.getName(), super.getActions());
      } catch (IllegalArgumentException var3) {
         throw new InvalidObjectException(var3.getMessage());
      }
   }
}
