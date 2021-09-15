package javax.sound.sampled;

import java.security.BasicPermission;

public class AudioPermission extends BasicPermission {
   public AudioPermission(String var1) {
      super(var1);
   }

   public AudioPermission(String var1, String var2) {
      super(var1, var2);
   }
}
