package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidUserPrincipal extends NTSid {
   private static final long serialVersionUID = -5573239889517749525L;

   public NTSidUserPrincipal(String var1) {
      super(var1);
   }

   public String toString() {
      MessageFormat var1 = new MessageFormat(ResourcesMgr.getString("NTSidUserPrincipal.name", "sun.security.util.AuthResources"));
      Object[] var2 = new Object[]{this.getName()};
      return var1.format(var2);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof NTSidUserPrincipal) ? false : super.equals(var1);
      }
   }
}
