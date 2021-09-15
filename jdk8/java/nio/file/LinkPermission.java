package java.nio.file;

import java.security.BasicPermission;

public final class LinkPermission extends BasicPermission {
   static final long serialVersionUID = -1441492453772213220L;

   private void checkName(String var1) {
      if (!var1.equals("hard") && !var1.equals("symbolic")) {
         throw new IllegalArgumentException("name: " + var1);
      }
   }

   public LinkPermission(String var1) {
      super(var1);
      this.checkName(var1);
   }

   public LinkPermission(String var1, String var2) {
      super(var1);
      this.checkName(var1);
      if (var2 != null && var2.length() > 0) {
         throw new IllegalArgumentException("actions: " + var2);
      }
   }
}
