package java.lang.management;

import java.security.BasicPermission;

public final class ManagementPermission extends BasicPermission {
   private static final long serialVersionUID = 1897496590799378737L;

   public ManagementPermission(String var1) {
      super(var1);
      if (!var1.equals("control") && !var1.equals("monitor")) {
         throw new IllegalArgumentException("name: " + var1);
      }
   }

   public ManagementPermission(String var1, String var2) throws IllegalArgumentException {
      super(var1);
      if (!var1.equals("control") && !var1.equals("monitor")) {
         throw new IllegalArgumentException("name: " + var1);
      } else if (var2 != null && var2.length() > 0) {
         throw new IllegalArgumentException("actions: " + var2);
      }
   }
}
