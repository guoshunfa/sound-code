package sun.security.action;

import java.security.PrivilegedAction;

public class GetLongAction implements PrivilegedAction<Long> {
   private String theProp;
   private long defaultVal;
   private boolean defaultSet = false;

   public GetLongAction(String var1) {
      this.theProp = var1;
   }

   public GetLongAction(String var1, long var2) {
      this.theProp = var1;
      this.defaultVal = var2;
      this.defaultSet = true;
   }

   public Long run() {
      Long var1 = Long.getLong(this.theProp);
      return var1 == null && this.defaultSet ? new Long(this.defaultVal) : var1;
   }
}
