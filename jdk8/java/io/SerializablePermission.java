package java.io;

import java.security.BasicPermission;

public final class SerializablePermission extends BasicPermission {
   private static final long serialVersionUID = 8537212141160296410L;
   private String actions;

   public SerializablePermission(String var1) {
      super(var1);
   }

   public SerializablePermission(String var1, String var2) {
      super(var1, var2);
   }
}
