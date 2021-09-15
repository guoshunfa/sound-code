package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Security;

public class GetBooleanSecurityPropertyAction implements PrivilegedAction<Boolean> {
   private String theProp;

   public GetBooleanSecurityPropertyAction(String var1) {
      this.theProp = var1;
   }

   public Boolean run() {
      boolean var1 = false;

      try {
         String var2 = Security.getProperty(this.theProp);
         var1 = var2 != null && var2.equalsIgnoreCase("true");
      } catch (NullPointerException var3) {
      }

      return var1;
   }
}
