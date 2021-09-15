package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidGroupPrincipal extends NTSid {
   private static final long serialVersionUID = -1373347438636198229L;

   public NTSidGroupPrincipal(String var1) {
      super(var1);
   }

   public String toString() {
      MessageFormat var1 = new MessageFormat(ResourcesMgr.getString("NTSidGroupPrincipal.name", "sun.security.util.AuthResources"));
      Object[] var2 = new Object[]{this.getName()};
      return var1.format(var2);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof NTSidGroupPrincipal) ? false : super.equals(var1);
      }
   }
}
