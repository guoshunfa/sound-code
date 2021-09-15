package sun.security.provider;

import java.net.MalformedURLException;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PolicySpi;
import java.security.ProtectionDomain;
import java.security.URIParameter;

public final class PolicySpiFile extends PolicySpi {
   private PolicyFile pf;

   public PolicySpiFile(Policy.Parameters var1) {
      if (var1 == null) {
         this.pf = new PolicyFile();
      } else {
         if (!(var1 instanceof URIParameter)) {
            throw new IllegalArgumentException("Unrecognized policy parameter: " + var1);
         }

         URIParameter var2 = (URIParameter)var1;

         try {
            this.pf = new PolicyFile(var2.getURI().toURL());
         } catch (MalformedURLException var4) {
            throw new IllegalArgumentException("Invalid URIParameter", var4);
         }
      }

   }

   protected PermissionCollection engineGetPermissions(CodeSource var1) {
      return this.pf.getPermissions(var1);
   }

   protected PermissionCollection engineGetPermissions(ProtectionDomain var1) {
      return this.pf.getPermissions(var1);
   }

   protected boolean engineImplies(ProtectionDomain var1, Permission var2) {
      return this.pf.implies(var1, var2);
   }

   protected void engineRefresh() {
      this.pf.refresh();
   }
}
