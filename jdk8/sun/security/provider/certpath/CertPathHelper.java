package sun.security.provider.certpath;

import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Date;
import java.util.Set;
import sun.security.x509.GeneralNameInterface;

public abstract class CertPathHelper {
   protected static CertPathHelper instance;

   protected CertPathHelper() {
   }

   protected abstract void implSetPathToNames(X509CertSelector var1, Set<GeneralNameInterface> var2);

   protected abstract void implSetDateAndTime(X509CRLSelector var1, Date var2, long var3);

   static void setPathToNames(X509CertSelector var0, Set<GeneralNameInterface> var1) {
      instance.implSetPathToNames(var0, var1);
   }

   public static void setDateAndTime(X509CRLSelector var0, Date var1, long var2) {
      instance.implSetDateAndTime(var0, var1, var2);
   }
}
