package sun.security.provider.certpath;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import sun.security.util.Debug;

class PKIXMasterCertPathValidator {
   private static final Debug debug = Debug.getInstance("certpath");

   static void validate(CertPath var0, List<X509Certificate> var1, List<PKIXCertPathChecker> var2) throws CertPathValidatorException {
      int var3 = var1.size();
      if (debug != null) {
         debug.println("--------------------------------------------------------------");
         debug.println("Executing PKIX certification path validation algorithm.");
      }

      for(int var4 = 0; var4 < var3; ++var4) {
         X509Certificate var5 = (X509Certificate)var1.get(var4);
         if (debug != null) {
            debug.println("Checking cert" + (var4 + 1) + " - Subject: " + var5.getSubjectX500Principal());
         }

         Set var6 = var5.getCriticalExtensionOIDs();
         if (var6 == null) {
            var6 = Collections.emptySet();
         }

         if (debug != null && !var6.isEmpty()) {
            StringJoiner var7 = new StringJoiner(", ", "{", "}");
            Iterator var8 = var6.iterator();

            while(var8.hasNext()) {
               String var9 = (String)var8.next();
               var7.add(var9);
            }

            debug.println("Set of critical extensions: " + var7.toString());
         }

         for(int var11 = 0; var11 < var2.size(); ++var11) {
            PKIXCertPathChecker var12 = (PKIXCertPathChecker)var2.get(var11);
            if (debug != null) {
               debug.println("-Using checker" + (var11 + 1) + " ... [" + var12.getClass().getName() + "]");
            }

            if (var4 == 0) {
               var12.init(false);
            }

            try {
               var12.check(var5, var6);
               if (debug != null) {
                  debug.println("-checker" + (var11 + 1) + " validation succeeded");
               }
            } catch (CertPathValidatorException var10) {
               throw new CertPathValidatorException(var10.getMessage(), (Throwable)(var10.getCause() != null ? var10.getCause() : var10), var0, var3 - (var4 + 1), var10.getReason());
            }
         }

         if (!var6.isEmpty()) {
            throw new CertPathValidatorException("unrecognized critical extension(s)", (Throwable)null, var0, var3 - (var4 + 1), PKIXReason.UNRECOGNIZED_CRIT_EXT);
         }

         if (debug != null) {
            debug.println("\ncert" + (var4 + 1) + " validation succeeded.\n");
         }
      }

      if (debug != null) {
         debug.println("Cert path validation succeeded. (PKIX validation algorithm)");
         debug.println("--------------------------------------------------------------");
      }

   }
}
