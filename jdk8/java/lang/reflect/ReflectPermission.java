package java.lang.reflect;

import java.security.BasicPermission;

public final class ReflectPermission extends BasicPermission {
   private static final long serialVersionUID = 7412737110241507485L;

   public ReflectPermission(String var1) {
      super(var1);
   }

   public ReflectPermission(String var1, String var2) {
      super(var1, var2);
   }
}
