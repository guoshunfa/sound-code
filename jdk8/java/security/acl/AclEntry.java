package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

public interface AclEntry extends Cloneable {
   boolean setPrincipal(Principal var1);

   Principal getPrincipal();

   void setNegativePermissions();

   boolean isNegative();

   boolean addPermission(Permission var1);

   boolean removePermission(Permission var1);

   boolean checkPermission(Permission var1);

   Enumeration<Permission> permissions();

   String toString();

   Object clone();
}
