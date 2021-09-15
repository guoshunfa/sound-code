package java.security.acl;

import java.security.Principal;

public interface Owner {
   boolean addOwner(Principal var1, Principal var2) throws NotOwnerException;

   boolean deleteOwner(Principal var1, Principal var2) throws NotOwnerException, LastOwnerException;

   boolean isOwner(Principal var1);
}
