package java.sql;

import java.security.BasicPermission;

public final class SQLPermission extends BasicPermission {
   static final long serialVersionUID = -1439323187199563495L;

   public SQLPermission(String var1) {
      super(var1);
   }

   public SQLPermission(String var1, String var2) {
      super(var1, var2);
   }
}
