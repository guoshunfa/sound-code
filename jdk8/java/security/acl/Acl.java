package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

public interface Acl extends Owner {
   void setName(Principal var1, String var2) throws NotOwnerException;

   String getName();

   boolean addEntry(Principal var1, AclEntry var2) throws NotOwnerException;

   boolean removeEntry(Principal var1, AclEntry var2) throws NotOwnerException;

   Enumeration<Permission> getPermissions(Principal var1);

   Enumeration<AclEntry> entries();

   boolean checkPermission(Principal var1, Permission var2);

   String toString();
}
