package sun.misc;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public interface JavaSecurityProtectionDomainAccess {
   JavaSecurityProtectionDomainAccess.ProtectionDomainCache getProtectionDomainCache();

   boolean getStaticPermissionsField(ProtectionDomain var1);

   public interface ProtectionDomainCache {
      void put(ProtectionDomain var1, PermissionCollection var2);

      PermissionCollection get(ProtectionDomain var1);
   }
}
