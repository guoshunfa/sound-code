package sun.security.validator;

import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.UntrustedChecker;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.NetscapeCertTypeExtension;
import sun.security.x509.X509CertImpl;

public final class SimpleValidator extends Validator {
   static final String OID_BASIC_CONSTRAINTS = "2.5.29.19";
   static final String OID_NETSCAPE_CERT_TYPE = "2.16.840.1.113730.1.1";
   static final String OID_KEY_USAGE = "2.5.29.15";
   static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
   static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
   static final ObjectIdentifier OBJID_NETSCAPE_CERT_TYPE;
   private static final String NSCT_SSL_CA = "ssl_ca";
   private static final String NSCT_CODE_SIGNING_CA = "object_signing_ca";
   private final Map<X500Principal, List<X509Certificate>> trustedX500Principals;
   private final Collection<X509Certificate> trustedCerts;

   SimpleValidator(String var1, Collection<X509Certificate> var2) {
      super("Simple", var1);
      this.trustedCerts = var2;
      this.trustedX500Principals = new HashMap();

      X509Certificate var4;
      Object var6;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); ((List)var6).add(var4)) {
         var4 = (X509Certificate)var3.next();
         X500Principal var5 = var4.getSubjectX500Principal();
         var6 = (List)this.trustedX500Principals.get(var5);
         if (var6 == null) {
            var6 = new ArrayList(2);
            this.trustedX500Principals.put(var5, var6);
         }
      }

   }

   public Collection<X509Certificate> getTrustedCertificates() {
      return this.trustedCerts;
   }

   X509Certificate[] engineValidate(X509Certificate[] var1, Collection<X509Certificate> var2, AlgorithmConstraints var3, Object var4) throws CertificateException {
      if (var1 != null && var1.length != 0) {
         var1 = this.buildTrustedChain(var1);
         Date var5 = this.validationDate;
         if (var5 == null) {
            var5 = new Date();
         }

         UntrustedChecker var6 = new UntrustedChecker();
         X509Certificate var7 = var1[var1.length - 1];

         try {
            var6.check(var7);
         } catch (CertPathValidatorException var18) {
            throw new ValidatorException("Untrusted certificate: " + var7.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, var7, var18);
         }

         TrustAnchor var8 = new TrustAnchor(var7, (byte[])null);
         AlgorithmChecker var9 = new AlgorithmChecker(var8, this.variant);
         AlgorithmChecker var10 = null;
         if (var3 != null) {
            var10 = new AlgorithmChecker(var8, var3, (Date)null, (Timestamp)null, this.variant);
         }

         int var11 = var1.length - 1;

         for(int var12 = var1.length - 2; var12 >= 0; --var12) {
            X509Certificate var13 = var1[var12 + 1];
            X509Certificate var14 = var1[var12];

            try {
               var6.check(var14, Collections.emptySet());
            } catch (CertPathValidatorException var17) {
               throw new ValidatorException("Untrusted certificate: " + var14.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, var14, var17);
            }

            try {
               var9.check(var14, Collections.emptySet());
               if (var10 != null) {
                  var10.check(var14, Collections.emptySet());
               }
            } catch (CertPathValidatorException var19) {
               throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED, var14, var19);
            }

            if (!this.variant.equals("code signing") && !this.variant.equals("jce signing")) {
               var14.checkValidity(var5);
            }

            if (!var14.getIssuerX500Principal().equals(var13.getSubjectX500Principal())) {
               throw new ValidatorException(ValidatorException.T_NAME_CHAINING, var14);
            }

            try {
               var14.verify(var13.getPublicKey());
            } catch (GeneralSecurityException var16) {
               throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR, var14, var16);
            }

            if (var12 != 0) {
               var11 = this.checkExtensions(var14, var11);
            }
         }

         return var1;
      } else {
         throw new CertificateException("null or zero-length certificate chain");
      }
   }

   private int checkExtensions(X509Certificate var1, int var2) throws CertificateException {
      Set var3 = var1.getCriticalExtensionOIDs();
      if (var3 == null) {
         var3 = Collections.emptySet();
      }

      int var4 = this.checkBasicConstraints(var1, var3, var2);
      this.checkKeyUsage(var1, var3);
      this.checkNetscapeCertType(var1, var3);
      if (!var3.isEmpty()) {
         throw new ValidatorException("Certificate contains unknown critical extensions: " + var3, ValidatorException.T_CA_EXTENSIONS, var1);
      } else {
         return var4;
      }
   }

   private void checkNetscapeCertType(X509Certificate var1, Set<String> var2) throws CertificateException {
      if (!this.variant.equals("generic")) {
         if (!this.variant.equals("tls client") && !this.variant.equals("tls server")) {
            if (!this.variant.equals("code signing") && !this.variant.equals("jce signing")) {
               throw new CertificateException("Unknown variant " + this.variant);
            }

            if (!getNetscapeCertTypeBit(var1, "object_signing_ca")) {
               throw new ValidatorException("Invalid Netscape CertType extension for code signing CA certificate", ValidatorException.T_CA_EXTENSIONS, var1);
            }

            var2.remove("2.16.840.1.113730.1.1");
         } else {
            if (!getNetscapeCertTypeBit(var1, "ssl_ca")) {
               throw new ValidatorException("Invalid Netscape CertType extension for SSL CA certificate", ValidatorException.T_CA_EXTENSIONS, var1);
            }

            var2.remove("2.16.840.1.113730.1.1");
         }
      }

   }

   static boolean getNetscapeCertTypeBit(X509Certificate var0, String var1) {
      try {
         NetscapeCertTypeExtension var2;
         if (var0 instanceof X509CertImpl) {
            X509CertImpl var3 = (X509CertImpl)var0;
            ObjectIdentifier var4 = OBJID_NETSCAPE_CERT_TYPE;
            var2 = (NetscapeCertTypeExtension)var3.getExtension(var4);
            if (var2 == null) {
               return true;
            }
         } else {
            byte[] var7 = var0.getExtensionValue("2.16.840.1.113730.1.1");
            if (var7 == null) {
               return true;
            }

            DerInputStream var9 = new DerInputStream(var7);
            byte[] var5 = var9.getOctetString();
            var5 = (new DerValue(var5)).getUnalignedBitString().toByteArray();
            var2 = new NetscapeCertTypeExtension(var5);
         }

         Boolean var8 = var2.get(var1);
         return var8;
      } catch (IOException var6) {
         return false;
      }
   }

   private int checkBasicConstraints(X509Certificate var1, Set<String> var2, int var3) throws CertificateException {
      var2.remove("2.5.29.19");
      int var4 = var1.getBasicConstraints();
      if (var4 < 0) {
         throw new ValidatorException("End user tried to act as a CA", ValidatorException.T_CA_EXTENSIONS, var1);
      } else {
         if (!X509CertImpl.isSelfIssued(var1)) {
            if (var3 <= 0) {
               throw new ValidatorException("Violated path length constraints", ValidatorException.T_CA_EXTENSIONS, var1);
            }

            --var3;
         }

         if (var3 > var4) {
            var3 = var4;
         }

         return var3;
      }
   }

   private void checkKeyUsage(X509Certificate var1, Set<String> var2) throws CertificateException {
      var2.remove("2.5.29.15");
      var2.remove("2.5.29.37");
      boolean[] var3 = var1.getKeyUsage();
      if (var3 != null && (var3.length < 6 || !var3[5])) {
         throw new ValidatorException("Wrong key usage: expected keyCertSign", ValidatorException.T_CA_EXTENSIONS, var1);
      }
   }

   private X509Certificate[] buildTrustedChain(X509Certificate[] var1) throws CertificateException {
      ArrayList var2 = new ArrayList(var1.length);

      for(int var3 = 0; var3 < var1.length; ++var3) {
         X509Certificate var4 = var1[var3];
         X509Certificate var5 = this.getTrustedCertificate(var4);
         if (var5 != null) {
            var2.add(var5);
            return (X509Certificate[])var2.toArray(CHAIN0);
         }

         var2.add(var4);
      }

      X509Certificate var8 = var1[var1.length - 1];
      X500Principal var9 = var8.getSubjectX500Principal();
      X500Principal var10 = var8.getIssuerX500Principal();
      List var6 = (List)this.trustedX500Principals.get(var10);
      if (var6 != null) {
         X509Certificate var7 = (X509Certificate)var6.iterator().next();
         var2.add(var7);
         return (X509Certificate[])var2.toArray(CHAIN0);
      } else {
         throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
      }
   }

   private X509Certificate getTrustedCertificate(X509Certificate var1) {
      X500Principal var2 = var1.getSubjectX500Principal();
      List var3 = (List)this.trustedX500Principals.get(var2);
      if (var3 == null) {
         return null;
      } else {
         X500Principal var4 = var1.getIssuerX500Principal();
         PublicKey var5 = var1.getPublicKey();
         Iterator var6 = var3.iterator();

         X509Certificate var7;
         do {
            if (!var6.hasNext()) {
               return null;
            }

            var7 = (X509Certificate)var6.next();
            if (var7.equals(var1)) {
               return var1;
            }
         } while(!var7.getIssuerX500Principal().equals(var4) || !var7.getPublicKey().equals(var5));

         return var7;
      }
   }

   static {
      OBJID_NETSCAPE_CERT_TYPE = NetscapeCertTypeExtension.NetscapeCertType_Id;
   }
}
