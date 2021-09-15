package sun.security.action;

import java.security.PrivilegedAction;

public class GetIntegerAction implements PrivilegedAction<Integer> {
   private String theProp;
   private int defaultVal;
   private boolean defaultSet = false;

   public GetIntegerAction(String var1) {
      this.theProp = var1;
   }

   public GetIntegerAction(String var1, int var2) {
      this.theProp = var1;
      this.defaultVal = var2;
      this.defaultSet = true;
   }

   public Integer run() {
      Integer var1 = Integer.getInteger(this.theProp);
      return var1 == null && this.defaultSet ? new Integer(this.defaultVal) : var1;
   }
}
