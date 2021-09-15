package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.Map;

public class PutAllAction implements PrivilegedAction<Void> {
   private final Provider provider;
   private final Map<?, ?> map;

   public PutAllAction(Provider var1, Map<?, ?> var2) {
      this.provider = var1;
      this.map = var2;
   }

   public Void run() {
      this.provider.putAll(this.map);
      return null;
   }
}
