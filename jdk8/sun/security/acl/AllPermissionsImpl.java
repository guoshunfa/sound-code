package sun.security.acl;

import java.security.acl.Permission;

public class AllPermissionsImpl extends PermissionImpl {
   public AllPermissionsImpl(String var1) {
      super(var1);
   }

   public boolean equals(Permission var1) {
      return true;
   }
}
