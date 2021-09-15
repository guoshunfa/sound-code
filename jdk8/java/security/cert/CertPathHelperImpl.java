package java.security.cert;

import java.util.Date;
import java.util.Set;
import sun.security.provider.certpath.CertPathHelper;
import sun.security.x509.GeneralNameInterface;

class CertPathHelperImpl extends CertPathHelper {
   private CertPathHelperImpl() {
   }

   static synchronized void initialize() {
      if (CertPathHelper.instance == null) {
         CertPathHelper.instance = new CertPathHelperImpl();
      }

   }

   protected void implSetPathToNames(X509CertSelector var1, Set<GeneralNameInterface> var2) {
      var1.setPathToNamesInternal(var2);
   }

   protected void implSetDateAndTime(X509CRLSelector var1, Date var2, long var3) {
      var1.setDateAndTime(var2, var3);
   }
}
