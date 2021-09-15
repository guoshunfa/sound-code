package java.security;

public final class SecurityPermission extends BasicPermission {
   private static final long serialVersionUID = 5236109936224050470L;

   public SecurityPermission(String var1) {
      super(var1);
   }

   public SecurityPermission(String var1, String var2) {
      super(var1, var2);
   }
}
