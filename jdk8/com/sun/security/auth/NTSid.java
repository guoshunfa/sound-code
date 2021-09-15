package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSid implements Principal, Serializable {
   private static final long serialVersionUID = 4412290580770249885L;
   private String sid;

   public NTSid(String var1) {
      if (var1 == null) {
         MessageFormat var2 = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
         Object[] var3 = new Object[]{"stringSid"};
         throw new NullPointerException(var2.format(var3));
      } else if (var1.length() == 0) {
         throw new IllegalArgumentException(ResourcesMgr.getString("Invalid.NTSid.value", "sun.security.util.AuthResources"));
      } else {
         this.sid = new String(var1);
      }
   }

   public String getName() {
      return this.sid;
   }

   public String toString() {
      MessageFormat var1 = new MessageFormat(ResourcesMgr.getString("NTSid.name", "sun.security.util.AuthResources"));
      Object[] var2 = new Object[]{this.sid};
      return var1.format(var2);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof NTSid)) {
         return false;
      } else {
         NTSid var2 = (NTSid)var1;
         return this.sid.equals(var2.sid);
      }
   }

   public int hashCode() {
      return this.sid.hashCode();
   }
}
