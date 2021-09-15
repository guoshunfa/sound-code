package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import sun.security.util.AnchorCertificates;
import sun.security.util.ConstraintsParameters;
import sun.security.util.Debug;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public final class AlgorithmChecker extends PKIXCertPathChecker {
   private static final Debug debug = Debug.getInstance("certpath");
   private final AlgorithmConstraints constraints;
   private final PublicKey trustedPubKey;
   private final Date pkixdate;
   private PublicKey prevPubKey;
   private final Timestamp jarTimestamp;
   private final String variant;
   private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET;
   private static final Set<CryptoPrimitive> KU_PRIMITIVE_SET;
   private static final DisabledAlgorithmConstraints certPathDefaultConstraints;
   private static final boolean publicCALimits;
   private boolean trustedMatch;

   public AlgorithmChecker(TrustAnchor var1, String var2) {
      this(var1, certPathDefaultConstraints, (Date)null, (Timestamp)null, var2);
   }

   public AlgorithmChecker(AlgorithmConstraints var1, Timestamp var2, String var3) {
      this((TrustAnchor)null, var1, (Date)null, var2, var3);
   }

   public AlgorithmChecker(TrustAnchor var1, AlgorithmConstraints var2, Date var3, Timestamp var4, String var5) {
      this.trustedMatch = false;
      if (var1 != null) {
         if (var1.getTrustedCert() != null) {
            this.trustedPubKey = var1.getTrustedCert().getPublicKey();
            this.trustedMatch = checkFingerprint(var1.getTrustedCert());
            if (this.trustedMatch && debug != null) {
               debug.println("trustedMatch = true");
            }
         } else {
            this.trustedPubKey = var1.getCAPublicKey();
         }
      } else {
         this.trustedPubKey = null;
         if (debug != null) {
            debug.println("TrustAnchor is null, trustedMatch is false.");
         }
      }

      this.prevPubKey = this.trustedPubKey;
      this.constraints = (AlgorithmConstraints)(var2 == null ? certPathDefaultConstraints : var2);
      this.pkixdate = var4 != null ? var4.getTimestamp() : var3;
      this.jarTimestamp = var4;
      this.variant = var5 == null ? "generic" : var5;
   }

   public AlgorithmChecker(TrustAnchor var1, Date var2, String var3) {
      this(var1, certPathDefaultConstraints, var2, (Timestamp)null, var3);
   }

   private static boolean checkFingerprint(X509Certificate var0) {
      if (!publicCALimits) {
         return false;
      } else {
         if (debug != null) {
            debug.println("AlgorithmChecker.contains: " + var0.getSigAlgName());
         }

         return AnchorCertificates.contains(var0);
      }
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (!var1) {
         if (this.trustedPubKey != null) {
            this.prevPubKey = this.trustedPubKey;
         } else {
            this.prevPubKey = null;
         }

      } else {
         throw new CertPathValidatorException("forward checking not supported");
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      return null;
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      if (var1 instanceof X509Certificate && this.constraints != null) {
         boolean[] var3 = ((X509Certificate)var1).getKeyUsage();
         if (var3 != null && var3.length < 9) {
            throw new CertPathValidatorException("incorrect KeyUsage extension", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_KEY_USAGE);
         } else {
            X509CertImpl var4;
            AlgorithmId var5;
            try {
               var4 = X509CertImpl.toImpl((X509Certificate)var1);
               var5 = (AlgorithmId)var4.get("x509.algorithm");
            } catch (CertificateException var16) {
               throw new CertPathValidatorException(var16);
            }

            AlgorithmParameters var6 = var5.getParameters();
            PublicKey var7 = var1.getPublicKey();
            String var8 = var4.getSigAlgName();
            if (!this.constraints.permits(SIGNATURE_PRIMITIVE_SET, var8, var6)) {
               throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + var8, (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
            } else {
               Object var9 = KU_PRIMITIVE_SET;
               if (var3 != null) {
                  var9 = EnumSet.noneOf(CryptoPrimitive.class);
                  if (var3[0] || var3[1] || var3[5] || var3[6]) {
                     ((Set)var9).add(CryptoPrimitive.SIGNATURE);
                  }

                  if (var3[2]) {
                     ((Set)var9).add(CryptoPrimitive.KEY_ENCAPSULATION);
                  }

                  if (var3[3]) {
                     ((Set)var9).add(CryptoPrimitive.PUBLIC_KEY_ENCRYPTION);
                  }

                  if (var3[4]) {
                     ((Set)var9).add(CryptoPrimitive.KEY_AGREEMENT);
                  }

                  if (((Set)var9).isEmpty()) {
                     throw new CertPathValidatorException("incorrect KeyUsage extension bits", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_KEY_USAGE);
                  }
               }

               ConstraintsParameters var10 = new ConstraintsParameters((X509Certificate)var1, this.trustedMatch, this.pkixdate, this.jarTimestamp, this.variant);
               if (this.constraints instanceof DisabledAlgorithmConstraints) {
                  ((DisabledAlgorithmConstraints)this.constraints).permits(var8, var10);
               } else {
                  certPathDefaultConstraints.permits(var8, var10);
                  if (!this.constraints.permits((Set)var9, var7)) {
                     throw new CertPathValidatorException("Algorithm constraints check failed on key " + var7.getAlgorithm() + " with size of " + KeyUtil.getKeySize((Key)var7) + "bits", (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
                  }
               }

               if (this.prevPubKey == null) {
                  this.prevPubKey = var7;
               } else if (!this.constraints.permits(SIGNATURE_PRIMITIVE_SET, var8, this.prevPubKey, var6)) {
                  throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + var8, (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
               } else {
                  if (PKIX.isDSAPublicKeyWithoutParams(var7)) {
                     if (!(this.prevPubKey instanceof DSAPublicKey)) {
                        throw new CertPathValidatorException("Input key is not of a appropriate type for inheriting parameters");
                     }

                     DSAParams var11 = ((DSAPublicKey)this.prevPubKey).getParams();
                     if (var11 == null) {
                        throw new CertPathValidatorException("Key parameters missing from public key.");
                     }

                     try {
                        BigInteger var12 = ((DSAPublicKey)var7).getY();
                        KeyFactory var13 = KeyFactory.getInstance("DSA");
                        DSAPublicKeySpec var14 = new DSAPublicKeySpec(var12, var11.getP(), var11.getQ(), var11.getG());
                        var7 = var13.generatePublic(var14);
                     } catch (GeneralSecurityException var15) {
                        throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + var15.getMessage(), var15);
                     }
                  }

                  this.prevPubKey = var7;
               }
            }
         }
      }
   }

   void trySetTrustAnchor(TrustAnchor var1) {
      if (this.prevPubKey == null) {
         if (var1 == null) {
            throw new IllegalArgumentException("The trust anchor cannot be null");
         }

         if (var1.getTrustedCert() != null) {
            this.prevPubKey = var1.getTrustedCert().getPublicKey();
            this.trustedMatch = checkFingerprint(var1.getTrustedCert());
            if (this.trustedMatch && debug != null) {
               debug.println("trustedMatch = true");
            }
         } else {
            this.prevPubKey = var1.getCAPublicKey();
         }
      }

   }

   static void check(PublicKey var0, X509CRL var1, String var2) throws CertPathValidatorException {
      X509CRLImpl var3 = null;

      try {
         var3 = X509CRLImpl.toImpl(var1);
      } catch (CRLException var5) {
         throw new CertPathValidatorException(var5);
      }

      AlgorithmId var4 = var3.getSigAlgId();
      check(var0, var4, var2);
   }

   static void check(PublicKey var0, AlgorithmId var1, String var2) throws CertPathValidatorException {
      String var3 = var1.getName();
      AlgorithmParameters var4 = var1.getParameters();
      certPathDefaultConstraints.permits(new ConstraintsParameters(var3, var4, var0, var2));
   }

   static {
      SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
      KU_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE, CryptoPrimitive.KEY_ENCAPSULATION, CryptoPrimitive.PUBLIC_KEY_ENCRYPTION, CryptoPrimitive.KEY_AGREEMENT));
      certPathDefaultConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
      publicCALimits = certPathDefaultConstraints.checkProperty("jdkCA");
   }
}
