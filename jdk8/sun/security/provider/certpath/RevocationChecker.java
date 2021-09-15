package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLReason;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.Extension;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNames;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CertImpl;

class RevocationChecker extends PKIXRevocationChecker {
   private static final Debug debug = Debug.getInstance("certpath");
   private TrustAnchor anchor;
   private PKIX.ValidatorParams params;
   private boolean onlyEE;
   private boolean softFail;
   private boolean crlDP;
   private URI responderURI;
   private X509Certificate responderCert;
   private List<CertStore> certStores;
   private Map<X509Certificate, byte[]> ocspResponses;
   private List<Extension> ocspExtensions;
   private final boolean legacy;
   private LinkedList<CertPathValidatorException> softFailExceptions = new LinkedList();
   private OCSPResponse.IssuerInfo issuerInfo;
   private PublicKey prevPubKey;
   private boolean crlSignFlag;
   private int certIndex;
   private RevocationChecker.Mode mode;
   private static final long MAX_CLOCK_SKEW = 900000L;
   private static final String HEX_DIGITS = "0123456789ABCDEFabcdef";
   private static final boolean[] ALL_REASONS = new boolean[]{true, true, true, true, true, true, true, true, true};
   private static final boolean[] CRL_SIGN_USAGE = new boolean[]{false, false, false, false, false, false, true};

   RevocationChecker() {
      this.mode = RevocationChecker.Mode.PREFER_OCSP;
      this.legacy = false;
   }

   RevocationChecker(TrustAnchor var1, PKIX.ValidatorParams var2) throws CertPathValidatorException {
      this.mode = RevocationChecker.Mode.PREFER_OCSP;
      this.legacy = true;
      this.init(var1, var2);
   }

   void init(TrustAnchor var1, PKIX.ValidatorParams var2) throws CertPathValidatorException {
      RevocationChecker.RevocationProperties var3 = getRevocationProperties();
      URI var4 = this.getOcspResponder();
      this.responderURI = var4 == null ? toURI(var3.ocspUrl) : var4;
      X509Certificate var5 = this.getOcspResponderCert();
      this.responderCert = var5 == null ? getResponderCert(var3, var2.trustAnchors(), var2.certStores()) : var5;
      Set var6 = this.getOptions();
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         PKIXRevocationChecker.Option var8 = (PKIXRevocationChecker.Option)var7.next();
         switch(var8) {
         case ONLY_END_ENTITY:
         case PREFER_CRLS:
         case SOFT_FAIL:
         case NO_FALLBACK:
            break;
         default:
            throw new CertPathValidatorException("Unrecognized revocation parameter option: " + var8);
         }
      }

      this.softFail = var6.contains(PKIXRevocationChecker.Option.SOFT_FAIL);
      if (this.legacy) {
         this.mode = var3.ocspEnabled ? RevocationChecker.Mode.PREFER_OCSP : RevocationChecker.Mode.ONLY_CRLS;
         this.onlyEE = var3.onlyEE;
      } else {
         if (var6.contains(PKIXRevocationChecker.Option.NO_FALLBACK)) {
            if (var6.contains(PKIXRevocationChecker.Option.PREFER_CRLS)) {
               this.mode = RevocationChecker.Mode.ONLY_CRLS;
            } else {
               this.mode = RevocationChecker.Mode.ONLY_OCSP;
            }
         } else if (var6.contains(PKIXRevocationChecker.Option.PREFER_CRLS)) {
            this.mode = RevocationChecker.Mode.PREFER_CRLS;
         }

         this.onlyEE = var6.contains(PKIXRevocationChecker.Option.ONLY_END_ENTITY);
      }

      if (this.legacy) {
         this.crlDP = var3.crlDPEnabled;
      } else {
         this.crlDP = true;
      }

      this.ocspResponses = this.getOcspResponses();
      this.ocspExtensions = this.getOcspExtensions();
      this.anchor = var1;
      this.params = var2;
      this.certStores = new ArrayList(var2.certStores());

      try {
         this.certStores.add(CertStore.getInstance("Collection", new CollectionCertStoreParameters(var2.certificates())));
      } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException var9) {
         if (debug != null) {
            debug.println("RevocationChecker: error creating Collection CertStore: " + var9);
         }
      }

   }

   private static URI toURI(String var0) throws CertPathValidatorException {
      try {
         return var0 != null ? new URI(var0) : null;
      } catch (URISyntaxException var2) {
         throw new CertPathValidatorException("cannot parse ocsp.responderURL property", var2);
      }
   }

   private static RevocationChecker.RevocationProperties getRevocationProperties() {
      return (RevocationChecker.RevocationProperties)AccessController.doPrivileged(new PrivilegedAction<RevocationChecker.RevocationProperties>() {
         public RevocationChecker.RevocationProperties run() {
            RevocationChecker.RevocationProperties var1 = new RevocationChecker.RevocationProperties();
            String var2 = Security.getProperty("com.sun.security.onlyCheckRevocationOfEECert");
            var1.onlyEE = var2 != null && var2.equalsIgnoreCase("true");
            String var3 = Security.getProperty("ocsp.enable");
            var1.ocspEnabled = var3 != null && var3.equalsIgnoreCase("true");
            var1.ocspUrl = Security.getProperty("ocsp.responderURL");
            var1.ocspSubject = Security.getProperty("ocsp.responderCertSubjectName");
            var1.ocspIssuer = Security.getProperty("ocsp.responderCertIssuerName");
            var1.ocspSerial = Security.getProperty("ocsp.responderCertSerialNumber");
            var1.crlDPEnabled = Boolean.getBoolean("com.sun.security.enableCRLDP");
            return var1;
         }
      });
   }

   private static X509Certificate getResponderCert(RevocationChecker.RevocationProperties var0, Set<TrustAnchor> var1, List<CertStore> var2) throws CertPathValidatorException {
      if (var0.ocspSubject != null) {
         return getResponderCert(var0.ocspSubject, var1, var2);
      } else if (var0.ocspIssuer != null && var0.ocspSerial != null) {
         return getResponderCert(var0.ocspIssuer, var0.ocspSerial, var1, var2);
      } else if (var0.ocspIssuer == null && var0.ocspSerial == null) {
         return null;
      } else {
         throw new CertPathValidatorException("Must specify both ocsp.responderCertIssuerName and ocsp.responderCertSerialNumber properties");
      }
   }

   private static X509Certificate getResponderCert(String var0, Set<TrustAnchor> var1, List<CertStore> var2) throws CertPathValidatorException {
      X509CertSelector var3 = new X509CertSelector();

      try {
         var3.setSubject(new X500Principal(var0));
      } catch (IllegalArgumentException var5) {
         throw new CertPathValidatorException("cannot parse ocsp.responderCertSubjectName property", var5);
      }

      return getResponderCert(var3, var1, var2);
   }

   private static X509Certificate getResponderCert(String var0, String var1, Set<TrustAnchor> var2, List<CertStore> var3) throws CertPathValidatorException {
      X509CertSelector var4 = new X509CertSelector();

      try {
         var4.setIssuer(new X500Principal(var0));
      } catch (IllegalArgumentException var7) {
         throw new CertPathValidatorException("cannot parse ocsp.responderCertIssuerName property", var7);
      }

      try {
         var4.setSerialNumber(new BigInteger(stripOutSeparators(var1), 16));
      } catch (NumberFormatException var6) {
         throw new CertPathValidatorException("cannot parse ocsp.responderCertSerialNumber property", var6);
      }

      return getResponderCert(var4, var2, var3);
   }

   private static X509Certificate getResponderCert(X509CertSelector var0, Set<TrustAnchor> var1, List<CertStore> var2) throws CertPathValidatorException {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         TrustAnchor var4 = (TrustAnchor)var3.next();
         X509Certificate var5 = var4.getTrustedCert();
         if (var5 != null && var0.match(var5)) {
            return var5;
         }
      }

      var3 = var2.iterator();

      while(var3.hasNext()) {
         CertStore var7 = (CertStore)var3.next();

         try {
            Collection var8 = var7.getCertificates(var0);
            if (!var8.isEmpty()) {
               return (X509Certificate)var8.iterator().next();
            }
         } catch (CertStoreException var6) {
            if (debug != null) {
               debug.println("CertStore exception:" + var6);
            }
         }
      }

      throw new CertPathValidatorException("Cannot find the responder's certificate (set using the OCSP security properties).");
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (var1) {
         throw new CertPathValidatorException("forward checking not supported");
      } else {
         if (this.anchor != null) {
            this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor);
            this.prevPubKey = this.issuerInfo.getPublicKey();
         }

         this.crlSignFlag = true;
         if (this.params != null && this.params.certPath() != null) {
            this.certIndex = this.params.certPath().getCertificates().size() - 1;
         } else {
            this.certIndex = -1;
         }

         this.softFailExceptions.clear();
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      return null;
   }

   public List<CertPathValidatorException> getSoftFailExceptions() {
      return Collections.unmodifiableList(this.softFailExceptions);
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      this.check((X509Certificate)var1, var2, this.prevPubKey, this.crlSignFlag);
   }

   private void check(X509Certificate var1, Collection<String> var2, PublicKey var3, boolean var4) throws CertPathValidatorException {
      if (debug != null) {
         debug.println("RevocationChecker.check: checking cert\n  SN: " + Debug.toHexString(var1.getSerialNumber()) + "\n  Subject: " + var1.getSubjectX500Principal() + "\n  Issuer: " + var1.getIssuerX500Principal());
      }

      try {
         if (this.onlyEE && var1.getBasicConstraints() != -1) {
            if (debug != null) {
               debug.println("Skipping revocation check; cert is not an end entity cert");
            }

         } else {
            switch(this.mode) {
            case PREFER_OCSP:
            case ONLY_OCSP:
               this.checkOCSP(var1, var2);
               return;
            case PREFER_CRLS:
            case ONLY_CRLS:
               this.checkCRLs(var1, var2, (Set)null, var3, var4);
               return;
            default:
            }
         }
      } catch (CertPathValidatorException var14) {
         if (var14.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
            throw var14;
         } else {
            boolean var6 = this.isSoftFailException(var14);
            if (var6) {
               if (this.mode == RevocationChecker.Mode.ONLY_OCSP || this.mode == RevocationChecker.Mode.ONLY_CRLS) {
                  return;
               }
            } else if (this.mode == RevocationChecker.Mode.ONLY_OCSP || this.mode == RevocationChecker.Mode.ONLY_CRLS) {
               throw var14;
            }

            if (debug != null) {
               debug.println("RevocationChecker.check() " + var14.getMessage());
               debug.println("RevocationChecker.check() preparing to failover");
            }

            try {
               switch(this.mode) {
               case PREFER_OCSP:
                  this.checkCRLs(var1, var2, (Set)null, var3, var4);
                  return;
               case PREFER_CRLS:
                  this.checkOCSP(var1, var2);
               }
            } catch (CertPathValidatorException var13) {
               if (debug != null) {
                  debug.println("RevocationChecker.check() failover failed");
                  debug.println("RevocationChecker.check() " + var13.getMessage());
               }

               if (var13.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                  throw var13;
               }

               if (!this.isSoftFailException(var13)) {
                  var14.addSuppressed(var13);
                  throw var14;
               }

               if (!var6) {
                  throw var14;
               }
            }

         }
      } finally {
         this.updateState(var1);
      }
   }

   private boolean isSoftFailException(CertPathValidatorException var1) {
      if (this.softFail && var1.getReason() == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
         CertPathValidatorException var2 = new CertPathValidatorException(var1.getMessage(), var1.getCause(), this.params.certPath(), this.certIndex, var1.getReason());
         this.softFailExceptions.addFirst(var2);
         return true;
      } else {
         return false;
      }
   }

   private void updateState(X509Certificate var1) throws CertPathValidatorException {
      this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor, var1);
      PublicKey var2 = var1.getPublicKey();
      if (PKIX.isDSAPublicKeyWithoutParams(var2)) {
         var2 = BasicChecker.makeInheritedParamsKey(var2, this.prevPubKey);
      }

      this.prevPubKey = var2;
      this.crlSignFlag = certCanSignCrl(var1);
      if (this.certIndex > 0) {
         --this.certIndex;
      }

   }

   private void checkCRLs(X509Certificate var1, Collection<String> var2, Set<X509Certificate> var3, PublicKey var4, boolean var5) throws CertPathValidatorException {
      this.checkCRLs(var1, var4, (X509Certificate)null, var5, true, var3, this.params.trustAnchors());
   }

   static boolean isCausedByNetworkIssue(String var0, CertStoreException var1) {
      Throwable var3 = var1.getCause();
      byte var5 = -1;
      switch(var0.hashCode()) {
      case 84300:
         if (var0.equals("URI")) {
            var5 = 2;
         }
         break;
      case 2331559:
         if (var0.equals("LDAP")) {
            var5 = 0;
         }
         break;
      case 133315663:
         if (var0.equals("SSLServer")) {
            var5 = 1;
         }
      }

      boolean var2;
      switch(var5) {
      case 0:
         if (var3 != null) {
            String var6 = var3.getClass().getName();
            var2 = var6.equals("javax.naming.ServiceUnavailableException") || var6.equals("javax.naming.CommunicationException");
         } else {
            var2 = false;
         }
         break;
      case 1:
         var2 = var3 != null && var3 instanceof IOException;
         break;
      case 2:
         var2 = var3 != null && var3 instanceof IOException;
         break;
      default:
         return false;
      }

      return var2;
   }

   private void checkCRLs(X509Certificate var1, PublicKey var2, X509Certificate var3, boolean var4, boolean var5, Set<X509Certificate> var6, Set<TrustAnchor> var7) throws CertPathValidatorException {
      if (debug != null) {
         debug.println("RevocationChecker.checkCRLs() ---checking revocation status ...");
      }

      if (var6 != null && var6.contains(var1)) {
         if (debug != null) {
            debug.println("RevocationChecker.checkCRLs() circular dependency");
         }

         throw new CertPathValidatorException("Could not determine revocation status", (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      } else {
         HashSet var8 = new HashSet();
         HashSet var9 = new HashSet();
         X509CRLSelector var10 = new X509CRLSelector();
         var10.setCertificateChecking(var1);
         CertPathHelper.setDateAndTime(var10, this.params.date(), 900000L);
         CertPathValidatorException var11 = null;
         Iterator var12 = this.certStores.iterator();

         while(var12.hasNext()) {
            CertStore var13 = (CertStore)var12.next();

            try {
               Iterator var14 = var13.getCRLs(var10).iterator();

               while(var14.hasNext()) {
                  CRL var15 = (CRL)var14.next();
                  var8.add((X509CRL)var15);
               }
            } catch (CertStoreException var18) {
               if (debug != null) {
                  debug.println("RevocationChecker.checkCRLs() CertStoreException: " + var18.getMessage());
               }

               if (var11 == null && isCausedByNetworkIssue(var13.getType(), var18)) {
                  var11 = new CertPathValidatorException("Unable to determine revocation status due to network error", var18, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
               }
            }
         }

         if (debug != null) {
            debug.println("RevocationChecker.checkCRLs() possible crls.size() = " + var8.size());
         }

         boolean[] var19 = new boolean[9];
         if (!var8.isEmpty()) {
            var9.addAll(this.verifyPossibleCRLs(var8, var1, var2, var4, var19, var7));
         }

         if (debug != null) {
            debug.println("RevocationChecker.checkCRLs() approved crls.size() = " + var9.size());
         }

         if (!var9.isEmpty() && Arrays.equals(var19, ALL_REASONS)) {
            this.checkApprovedCRLs(var1, var9);
         } else {
            try {
               if (this.crlDP) {
                  var9.addAll(DistributionPointFetcher.getCRLs(var10, var4, var2, var3, this.params.sigProvider(), this.certStores, var19, var7, (Date)null, this.params.variant()));
               }
            } catch (CertStoreException var17) {
               if (var17 instanceof PKIX.CertStoreTypeException) {
                  PKIX.CertStoreTypeException var20 = (PKIX.CertStoreTypeException)var17;
                  if (isCausedByNetworkIssue(var20.getType(), var17)) {
                     throw new CertPathValidatorException("Unable to determine revocation status due to network error", var17, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
                  }
               }

               throw new CertPathValidatorException(var17);
            }

            if (var9.isEmpty() || !Arrays.equals(var19, ALL_REASONS)) {
               if (var5) {
                  try {
                     this.verifyWithSeparateSigningKey(var1, var2, var4, var6);
                     return;
                  } catch (CertPathValidatorException var16) {
                     if (var11 != null) {
                        throw var11;
                     }

                     throw var16;
                  }
               }

               if (var11 != null) {
                  throw var11;
               }

               throw new CertPathValidatorException("Could not determine revocation status", (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
            }

            this.checkApprovedCRLs(var1, var9);
         }

      }
   }

   private void checkApprovedCRLs(X509Certificate var1, Set<X509CRL> var2) throws CertPathValidatorException {
      if (debug != null) {
         BigInteger var3 = var1.getSerialNumber();
         debug.println("RevocationChecker.checkApprovedCRLs() starting the final sweep...");
         debug.println("RevocationChecker.checkApprovedCRLs() cert SN: " + var3.toString());
      }

      CRLReason var12 = CRLReason.UNSPECIFIED;
      X509CRLEntryImpl var4 = null;
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         X509CRL var6 = (X509CRL)var5.next();
         X509CRLEntry var7 = var6.getRevokedCertificate(var1);
         if (var7 != null) {
            try {
               var4 = X509CRLEntryImpl.toImpl(var7);
            } catch (CRLException var11) {
               throw new CertPathValidatorException(var11);
            }

            if (debug != null) {
               debug.println("RevocationChecker.checkApprovedCRLs() CRL entry: " + var4.toString());
            }

            Set var8 = var4.getCriticalExtensionOIDs();
            if (var8 != null && !var8.isEmpty()) {
               var8.remove(PKIXExtensions.ReasonCode_Id.toString());
               var8.remove(PKIXExtensions.CertificateIssuer_Id.toString());
               if (!var8.isEmpty()) {
                  throw new CertPathValidatorException("Unrecognized critical extension(s) in revoked CRL entry");
               }
            }

            var12 = var4.getRevocationReason();
            if (var12 == null) {
               var12 = CRLReason.UNSPECIFIED;
            }

            Date var9 = var4.getRevocationDate();
            if (var9.before(this.params.date())) {
               CertificateRevokedException var10 = new CertificateRevokedException(var9, var12, var6.getIssuerX500Principal(), var4.getExtensions());
               throw new CertPathValidatorException(var10.getMessage(), var10, (CertPath)null, -1, CertPathValidatorException.BasicReason.REVOKED);
            }
         }
      }

   }

   private void checkOCSP(X509Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      X509CertImpl var3 = null;

      try {
         var3 = X509CertImpl.toImpl(var1);
      } catch (CertificateException var10) {
         throw new CertPathValidatorException(var10);
      }

      OCSPResponse var4 = null;
      CertId var5 = null;

      try {
         var5 = new CertId(this.issuerInfo.getName(), this.issuerInfo.getPublicKey(), var3.getSerialNumberObject());
         byte[] var6 = (byte[])this.ocspResponses.get(var1);
         if (var6 != null) {
            if (debug != null) {
               debug.println("Found cached OCSP response");
            }

            var4 = new OCSPResponse(var6);
            byte[] var7 = null;
            Iterator var8 = this.ocspExtensions.iterator();

            while(var8.hasNext()) {
               Extension var9 = (Extension)var8.next();
               if (var9.getId().equals("1.3.6.1.5.5.7.48.1.2")) {
                  var7 = var9.getValue();
               }
            }

            var4.verify(Collections.singletonList(var5), this.issuerInfo, this.responderCert, this.params.date(), var7, this.params.variant());
         } else {
            URI var13 = this.responderURI != null ? this.responderURI : OCSP.getResponderURI(var3);
            if (var13 == null) {
               throw new CertPathValidatorException("Certificate does not specify OCSP responder", (Throwable)null, (CertPath)null, -1);
            }

            var4 = OCSP.check((List)Collections.singletonList(var5), (URI)var13, (OCSPResponse.IssuerInfo)this.issuerInfo, this.responderCert, (Date)null, this.ocspExtensions, this.params.variant());
         }
      } catch (IOException var11) {
         throw new CertPathValidatorException("Unable to determine revocation status due to network error", var11, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      }

      OCSPResponse.SingleResponse var12 = var4.getSingleResponse(var5);
      OCSP.RevocationStatus.CertStatus var14 = var12.getCertStatus();
      if (var14 == OCSP.RevocationStatus.CertStatus.REVOKED) {
         Date var15 = var12.getRevocationTime();
         if (var15.before(this.params.date())) {
            CertificateRevokedException var16 = new CertificateRevokedException(var15, var12.getRevocationReason(), var4.getSignerCertificate().getSubjectX500Principal(), var12.getSingleExtensions());
            throw new CertPathValidatorException(var16.getMessage(), var16, (CertPath)null, -1, CertPathValidatorException.BasicReason.REVOKED);
         }
      } else if (var14 == OCSP.RevocationStatus.CertStatus.UNKNOWN) {
         throw new CertPathValidatorException("Certificate's revocation status is unknown", (Throwable)null, this.params.certPath(), -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      }

   }

   private static String stripOutSeparators(String var0) {
      char[] var1 = var0.toCharArray();
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if ("0123456789ABCDEFabcdef".indexOf(var1[var3]) != -1) {
            var2.append(var1[var3]);
         }
      }

      return var2.toString();
   }

   static boolean certCanSignCrl(X509Certificate var0) {
      boolean[] var1 = var0.getKeyUsage();
      return var1 != null ? var1[6] : false;
   }

   private Collection<X509CRL> verifyPossibleCRLs(Set<X509CRL> var1, X509Certificate var2, PublicKey var3, boolean var4, boolean[] var5, Set<TrustAnchor> var6) throws CertPathValidatorException {
      try {
         X509CertImpl var7 = X509CertImpl.toImpl(var2);
         if (debug != null) {
            debug.println("RevocationChecker.verifyPossibleCRLs: Checking CRLDPs for " + var7.getSubjectX500Principal());
         }

         CRLDistributionPointsExtension var8 = var7.getCRLDistributionPointsExtension();
         List var9 = null;
         if (var8 == null) {
            X500Name var10 = (X500Name)var7.getIssuerDN();
            DistributionPoint var11 = new DistributionPoint((new GeneralNames()).add(new GeneralName(var10)), (boolean[])null, (GeneralNames)null);
            var9 = Collections.singletonList(var11);
         } else {
            var9 = var8.get("points");
         }

         HashSet var16 = new HashSet();
         Iterator var17 = var9.iterator();

         while(var17.hasNext()) {
            DistributionPoint var12 = (DistributionPoint)var17.next();
            Iterator var13 = var1.iterator();

            while(var13.hasNext()) {
               X509CRL var14 = (X509CRL)var13.next();
               if (DistributionPointFetcher.verifyCRL(var7, var12, var14, var5, var4, var3, (X509Certificate)null, this.params.sigProvider(), var6, this.certStores, this.params.date(), this.params.variant())) {
                  var16.add(var14);
               }
            }

            if (Arrays.equals(var5, ALL_REASONS)) {
               break;
            }
         }

         return var16;
      } catch (CRLException | IOException | CertificateException var15) {
         if (debug != null) {
            debug.println("Exception while verifying CRL: " + var15.getMessage());
            var15.printStackTrace();
         }

         return Collections.emptySet();
      }
   }

   private void verifyWithSeparateSigningKey(X509Certificate var1, PublicKey var2, boolean var3, Set<X509Certificate> var4) throws CertPathValidatorException {
      String var5 = "revocation status";
      if (debug != null) {
         debug.println("RevocationChecker.verifyWithSeparateSigningKey() ---checking " + var5 + "...");
      }

      if (var4 != null && var4.contains(var1)) {
         if (debug != null) {
            debug.println("RevocationChecker.verifyWithSeparateSigningKey() circular dependency");
         }

         throw new CertPathValidatorException("Could not determine revocation status", (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      } else {
         if (!var3) {
            this.buildToNewKey(var1, (PublicKey)null, var4);
         } else {
            this.buildToNewKey(var1, var2, var4);
         }

      }
   }

   private void buildToNewKey(X509Certificate var1, PublicKey var2, Set<X509Certificate> var3) throws CertPathValidatorException {
      if (debug != null) {
         debug.println("RevocationChecker.buildToNewKey() starting work");
      }

      HashSet var4 = new HashSet();
      if (var2 != null) {
         var4.add(var2);
      }

      RevocationChecker.RejectKeySelector var5 = new RevocationChecker.RejectKeySelector(var4);
      var5.setSubject(var1.getIssuerX500Principal());
      var5.setKeyUsage(CRL_SIGN_USAGE);
      Set var6 = this.anchor == null ? this.params.trustAnchors() : Collections.singleton(this.anchor);

      PKIXBuilderParameters var7;
      try {
         var7 = new PKIXBuilderParameters(var6, var5);
      } catch (InvalidAlgorithmParameterException var18) {
         throw new RuntimeException(var18);
      }

      var7.setInitialPolicies(this.params.initialPolicies());
      var7.setCertStores(this.certStores);
      var7.setExplicitPolicyRequired(this.params.explicitPolicyRequired());
      var7.setPolicyMappingInhibited(this.params.policyMappingInhibited());
      var7.setAnyPolicyInhibited(this.params.anyPolicyInhibited());
      var7.setDate(this.params.date());
      var7.setCertPathCheckers(this.params.getPKIXParameters().getCertPathCheckers());
      var7.setSigProvider(this.params.sigProvider());
      var7.setRevocationEnabled(false);
      X509CertImpl var8;
      if (Builder.USE_AIA) {
         var8 = null;

         try {
            var8 = X509CertImpl.toImpl(var1);
         } catch (CertificateException var23) {
            if (debug != null) {
               debug.println("RevocationChecker.buildToNewKey: error decoding cert: " + var23);
            }
         }

         AuthorityInfoAccessExtension var9 = null;
         if (var8 != null) {
            var9 = var8.getAuthorityInfoAccessExtension();
         }

         if (var9 != null) {
            List var10 = var9.getAccessDescriptions();
            if (var10 != null) {
               Iterator var11 = var10.iterator();

               while(var11.hasNext()) {
                  AccessDescription var12 = (AccessDescription)var11.next();
                  CertStore var13 = URICertStore.getInstance(var12);
                  if (var13 != null) {
                     if (debug != null) {
                        debug.println("adding AIAext CertStore");
                     }

                     var7.addCertStore(var13);
                  }
               }
            }
         }
      }

      var8 = null;

      CertPathBuilder var24;
      try {
         var24 = CertPathBuilder.getInstance("PKIX");
      } catch (NoSuchAlgorithmException var17) {
         throw new CertPathValidatorException(var17);
      }

      while(true) {
         while(true) {
            try {
               if (debug != null) {
                  debug.println("RevocationChecker.buildToNewKey() about to try build ...");
               }

               PKIXCertPathBuilderResult var25 = (PKIXCertPathBuilderResult)var24.build(var7);
               if (debug != null) {
                  debug.println("RevocationChecker.buildToNewKey() about to check revocation ...");
               }

               if (var3 == null) {
                  var3 = new HashSet();
               }

               ((Set)var3).add(var1);
               TrustAnchor var26 = var25.getTrustAnchor();
               PublicKey var27 = var26.getCAPublicKey();
               if (var27 == null) {
                  var27 = var26.getTrustedCert().getPublicKey();
               }

               boolean var28 = true;
               List var29 = var25.getCertPath().getCertificates();

               X509Certificate var15;
               try {
                  for(int var14 = var29.size() - 1; var14 >= 0; --var14) {
                     var15 = (X509Certificate)var29.get(var14);
                     if (debug != null) {
                        debug.println("RevocationChecker.buildToNewKey() index " + var14 + " checking " + var15);
                     }

                     this.checkCRLs(var15, var27, (X509Certificate)null, var28, true, (Set)var3, var6);
                     var28 = certCanSignCrl(var15);
                     var27 = var15.getPublicKey();
                  }
               } catch (CertPathValidatorException var20) {
                  var4.add(var25.getPublicKey());
                  continue;
               }

               if (debug != null) {
                  debug.println("RevocationChecker.buildToNewKey() got key " + var25.getPublicKey());
               }

               PublicKey var30 = var25.getPublicKey();
               var15 = var29.isEmpty() ? null : (X509Certificate)var29.get(0);

               try {
                  this.checkCRLs(var1, var30, var15, true, false, (Set)null, this.params.trustAnchors());
                  return;
               } catch (CertPathValidatorException var19) {
                  if (var19.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                     throw var19;
                  }

                  var4.add(var30);
               }
            } catch (InvalidAlgorithmParameterException var21) {
               throw new CertPathValidatorException(var21);
            } catch (CertPathBuilderException var22) {
               throw new CertPathValidatorException("Could not determine revocation status", (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
            }
         }
      }
   }

   public RevocationChecker clone() {
      RevocationChecker var1 = (RevocationChecker)super.clone();
      var1.softFailExceptions = new LinkedList(this.softFailExceptions);
      return var1;
   }

   private static class RejectKeySelector extends X509CertSelector {
      private final Set<PublicKey> badKeySet;

      RejectKeySelector(Set<PublicKey> var1) {
         this.badKeySet = var1;
      }

      public boolean match(Certificate var1) {
         if (!super.match(var1)) {
            return false;
         } else if (this.badKeySet.contains(var1.getPublicKey())) {
            if (RevocationChecker.debug != null) {
               RevocationChecker.debug.println("RejectKeySelector.match: bad key");
            }

            return false;
         } else {
            if (RevocationChecker.debug != null) {
               RevocationChecker.debug.println("RejectKeySelector.match: returning true");
            }

            return true;
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("RejectKeySelector: [\n");
         var1.append(super.toString());
         var1.append((Object)this.badKeySet);
         var1.append("]");
         return var1.toString();
      }
   }

   private static class RevocationProperties {
      boolean onlyEE;
      boolean ocspEnabled;
      boolean crlDPEnabled;
      String ocspUrl;
      String ocspSubject;
      String ocspIssuer;
      String ocspSerial;

      private RevocationProperties() {
      }

      // $FF: synthetic method
      RevocationProperties(Object var1) {
         this();
      }
   }

   private static enum Mode {
      PREFER_OCSP,
      PREFER_CRLS,
      ONLY_CRLS,
      ONLY_OCSP;
   }
}
