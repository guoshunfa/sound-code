package sun.security.acl;

import java.security.Principal;

public class WorldGroupImpl extends GroupImpl {
   public WorldGroupImpl(String var1) {
      super(var1);
   }

   public boolean isMember(Principal var1) {
      return true;
   }
}
