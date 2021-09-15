package sun.security.krb5.internal.ccache;

import java.io.File;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public abstract class MemoryCredentialsCache extends CredentialsCache {
   private static CredentialsCache getCCacheInstance(PrincipalName var0) {
      return null;
   }

   private static CredentialsCache getCCacheInstance(PrincipalName var0, File var1) {
      return null;
   }

   public abstract boolean exists(String var1);

   public abstract void update(Credentials var1);

   public abstract void save() throws IOException, KrbException;

   public abstract Credentials[] getCredsList();

   public abstract Credentials getCreds(PrincipalName var1);

   public abstract PrincipalName getPrimaryPrincipal();
}
