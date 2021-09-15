package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

class ForwardState implements State {
   private static final Debug debug = Debug.getInstance("certpath");
   X500Principal issuerDN;
   X509CertImpl cert;
   HashSet<GeneralNameInterface> subjectNamesTraversed;
   int traversedCACerts;
   private boolean init = true;
   UntrustedChecker untrustedChecker;
   ArrayList<PKIXCertPathChecker> forwardCheckers;
   boolean keyParamsNeededFlag = false;

   public boolean isInitial() {
      return this.init;
   }

   public boolean keyParamsNeeded() {
      return this.keyParamsNeededFlag;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("State [");
      var1.append("\n  issuerDN of last cert: ").append((Object)this.issuerDN);
      var1.append("\n  traversedCACerts: ").append(this.traversedCACerts);
      var1.append("\n  init: ").append(String.valueOf(this.init));
      var1.append("\n  keyParamsNeeded: ").append(String.valueOf(this.keyParamsNeededFlag));
      var1.append("\n  subjectNamesTraversed: \n").append((Object)this.subjectNamesTraversed);
      var1.append("]\n");
      return var1.toString();
   }

   public void initState(List<PKIXCertPathChecker> var1) throws CertPathValidatorException {
      this.subjectNamesTraversed = new HashSet();
      this.traversedCACerts = 0;
      this.forwardCheckers = new ArrayList();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         PKIXCertPathChecker var3 = (PKIXCertPathChecker)var2.next();
         if (var3.isForwardCheckingSupported()) {
            var3.init(true);
            this.forwardCheckers.add(var3);
         }
      }

      this.init = true;
   }

   public void updateState(X509Certificate var1) throws CertificateException, IOException, CertPathValidatorException {
      if (var1 != null) {
         X509CertImpl var2 = X509CertImpl.toImpl(var1);
         if (PKIX.isDSAPublicKeyWithoutParams(var2.getPublicKey())) {
            this.keyParamsNeededFlag = true;
         }

         this.cert = var2;
         this.issuerDN = var1.getIssuerX500Principal();
         if (!X509CertImpl.isSelfIssued(var1) && !this.init && var1.getBasicConstraints() != -1) {
            ++this.traversedCACerts;
         }

         if (this.init || !X509CertImpl.isSelfIssued(var1)) {
            X500Principal var3 = var1.getSubjectX500Principal();
            this.subjectNamesTraversed.add(X500Name.asX500Name(var3));

            try {
               SubjectAlternativeNameExtension var4 = var2.getSubjectAlternativeNameExtension();
               if (var4 != null) {
                  GeneralNames var5 = var4.get("subject_name");
                  Iterator var6 = var5.names().iterator();

                  while(var6.hasNext()) {
                     GeneralName var7 = (GeneralName)var6.next();
                     this.subjectNamesTraversed.add(var7.getName());
                  }
               }
            } catch (IOException var8) {
               if (debug != null) {
                  debug.println("ForwardState.updateState() unexpected exception");
                  var8.printStackTrace();
               }

               throw new CertPathValidatorException(var8);
            }
         }

         this.init = false;
      }
   }

   public Object clone() {
      try {
         ForwardState var1 = (ForwardState)super.clone();
         var1.forwardCheckers = (ArrayList)this.forwardCheckers.clone();
         ListIterator var2 = var1.forwardCheckers.listIterator();

         while(var2.hasNext()) {
            PKIXCertPathChecker var3 = (PKIXCertPathChecker)var2.next();
            if (var3 instanceof Cloneable) {
               var2.set((PKIXCertPathChecker)var3.clone());
            }
         }

         var1.subjectNamesTraversed = (HashSet)this.subjectNamesTraversed.clone();
         return var1;
      } catch (CloneNotSupportedException var4) {
         throw new InternalError(var4.toString(), var4);
      }
   }
}
