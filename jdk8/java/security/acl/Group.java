package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

public interface Group extends Principal {
   boolean addMember(Principal var1);

   boolean removeMember(Principal var1);

   boolean isMember(Principal var1);

   Enumeration<? extends Principal> members();
}
