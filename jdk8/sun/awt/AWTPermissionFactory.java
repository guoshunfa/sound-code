package sun.awt;

import java.awt.AWTPermission;
import sun.security.util.PermissionFactory;

public class AWTPermissionFactory implements PermissionFactory<AWTPermission> {
   public AWTPermission newPermission(String var1) {
      return new AWTPermission(var1);
   }
}
