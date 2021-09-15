package com.sun.security.auth;

import java.security.CodeSource;
import java.security.PermissionCollection;
import javax.security.auth.Policy;
import javax.security.auth.Subject;
import jdk.Exported;
import sun.security.provider.AuthPolicyFile;

/** @deprecated */
@Exported(false)
@Deprecated
public class PolicyFile extends Policy {
   private final AuthPolicyFile apf = new AuthPolicyFile();

   public void refresh() {
      this.apf.refresh();
   }

   public PermissionCollection getPermissions(Subject var1, CodeSource var2) {
      return this.apf.getPermissions(var1, var2);
   }
}
