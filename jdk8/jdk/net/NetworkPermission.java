package jdk.net;

import java.security.BasicPermission;
import jdk.Exported;

@Exported
public final class NetworkPermission extends BasicPermission {
   private static final long serialVersionUID = -2012939586906722291L;

   public NetworkPermission(String var1) {
      super(var1);
   }

   public NetworkPermission(String var1, String var2) {
      super(var1, var2);
   }
}
