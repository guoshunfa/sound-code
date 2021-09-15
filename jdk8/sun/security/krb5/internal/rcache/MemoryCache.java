package sun.security.krb5.internal.rcache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.ReplayCache;

public class MemoryCache extends ReplayCache {
   private static final int lifespan = KerberosTime.getDefaultSkew();
   private static final boolean DEBUG;
   private final Map<String, AuthList> content = new HashMap();

   public synchronized void checkAndStore(KerberosTime var1, AuthTimeWithHash var2) throws KrbApErrException {
      String var3 = var2.client + "|" + var2.server;
      AuthList var4 = (AuthList)this.content.get(var3);
      if (DEBUG) {
         System.out.println("MemoryCache: add " + var2 + " to " + var3);
      }

      if (var4 == null) {
         var4 = new AuthList(lifespan);
         var4.put(var2, var1);
         if (!var4.isEmpty()) {
            this.content.put(var3, var4);
         }
      } else {
         if (DEBUG) {
            System.out.println("MemoryCache: Existing AuthList:\n" + var4);
         }

         var4.put(var2, var1);
         if (var4.isEmpty()) {
            this.content.remove(var3);
         }
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.content.values().iterator();

      while(var2.hasNext()) {
         AuthList var3 = (AuthList)var2.next();
         var1.append(var3.toString());
      }

      return var1.toString();
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
