package java.util.logging;

import java.security.BasicPermission;

public final class LoggingPermission extends BasicPermission {
   private static final long serialVersionUID = 63564341580231582L;

   public LoggingPermission(String var1, String var2) throws IllegalArgumentException {
      super(var1);
      if (!var1.equals("control")) {
         throw new IllegalArgumentException("name: " + var1);
      } else if (var2 != null && var2.length() > 0) {
         throw new IllegalArgumentException("actions: " + var2);
      }
   }
}
