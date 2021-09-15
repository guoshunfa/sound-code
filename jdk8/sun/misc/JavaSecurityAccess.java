package sun.misc;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;

public interface JavaSecurityAccess {
   <T> T doIntersectionPrivilege(PrivilegedAction<T> var1, AccessControlContext var2, AccessControlContext var3);

   <T> T doIntersectionPrivilege(PrivilegedAction<T> var1, AccessControlContext var2);
}
