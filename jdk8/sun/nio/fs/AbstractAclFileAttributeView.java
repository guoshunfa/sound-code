package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractAclFileAttributeView implements AclFileAttributeView, DynamicFileAttributeView {
   private static final String OWNER_NAME = "owner";
   private static final String ACL_NAME = "acl";

   public final String name() {
      return "acl";
   }

   public final void setAttribute(String var1, Object var2) throws IOException {
      if (var1.equals("owner")) {
         this.setOwner((UserPrincipal)var2);
      } else if (var1.equals("acl")) {
         this.setAcl((List)var2);
      } else {
         throw new IllegalArgumentException("'" + this.name() + ":" + var1 + "' not recognized");
      }
   }

   public final Map<String, Object> readAttributes(String[] var1) throws IOException {
      boolean var2 = false;
      boolean var3 = false;
      String[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         if (var7.equals("*")) {
            var3 = true;
            var2 = true;
         } else if (var7.equals("acl")) {
            var2 = true;
         } else {
            if (!var7.equals("owner")) {
               throw new IllegalArgumentException("'" + this.name() + ":" + var7 + "' not recognized");
            }

            var3 = true;
         }
      }

      HashMap var8 = new HashMap(2);
      if (var2) {
         var8.put("acl", this.getAcl());
      }

      if (var3) {
         var8.put("owner", this.getOwner());
      }

      return Collections.unmodifiableMap(var8);
   }
}
