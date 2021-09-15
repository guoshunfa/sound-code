package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTNumericCredential {
   private long impersonationToken;

   public NTNumericCredential(long var1) {
      this.impersonationToken = var1;
   }

   public long getToken() {
      return this.impersonationToken;
   }

   public String toString() {
      MessageFormat var1 = new MessageFormat(ResourcesMgr.getString("NTNumericCredential.name", "sun.security.util.AuthResources"));
      Object[] var2 = new Object[]{Long.toString(this.impersonationToken)};
      return var1.format(var2);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof NTNumericCredential)) {
         return false;
      } else {
         NTNumericCredential var2 = (NTNumericCredential)var1;
         return this.impersonationToken == var2.getToken();
      }
   }

   public int hashCode() {
      return (int)this.impersonationToken;
   }
}
