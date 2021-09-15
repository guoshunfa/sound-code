package sun.security.provider.certpath;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
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
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.DistributionPointName;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.IssuingDistributionPointExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.RDN;
import sun.security.x509.ReasonFlags;
import sun.security.x509.SerialNumber;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public class DistributionPointFetcher {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final boolean[] ALL_REASONS = new boolean[]{true, true, true, true, true, true, true, true, true};

   private DistributionPointFetcher() {
   }

   public static Collection<X509CRL> getCRLs(X509CRLSelector var0, boolean var1, PublicKey var2, String var3, List<CertStore> var4, boolean[] var5, Set<TrustAnchor> var6, Date var7, String var8) throws CertStoreException {
      return getCRLs(var0, var1, var2, (X509Certificate)null, var3, var4, var5, var6, var7, var8);
   }

   public static Collection<X509CRL> getCRLs(X509CRLSelector var0, boolean var1, PublicKey var2, String var3, List<CertStore> var4, boolean[] var5, Set<TrustAnchor> var6, Date var7) throws CertStoreException {
      return getCRLs(var0, var1, var2, (X509Certificate)null, var3, var4, var5, var6, var7, "generic");
   }

   public static Collection<X509CRL> getCRLs(X509CRLSelector var0, boolean var1, PublicKey var2, X509Certificate var3, String var4, List<CertStore> var5, boolean[] var6, Set<TrustAnchor> var7, Date var8, String var9) throws CertStoreException {
      X509Certificate var10 = var0.getCertificateChecking();
      if (var10 == null) {
         return Collections.emptySet();
      } else {
         try {
            X509CertImpl var11 = X509CertImpl.toImpl(var10);
            if (debug != null) {
               debug.println("DistributionPointFetcher.getCRLs: Checking CRLDPs for " + var11.getSubjectX500Principal());
            }

            CRLDistributionPointsExtension var12 = var11.getCRLDistributionPointsExtension();
            if (var12 == null) {
               if (debug != null) {
                  debug.println("No CRLDP ext");
               }

               return Collections.emptySet();
            } else {
               List var13 = var12.get("points");
               HashSet var14 = new HashSet();
               Iterator var15 = var13.iterator();

               while(var15.hasNext() && !Arrays.equals(var6, ALL_REASONS)) {
                  DistributionPoint var16 = (DistributionPoint)var15.next();
                  Collection var17 = getCRLs(var0, var11, var16, var6, var1, var2, var3, var4, var5, var7, var8, var9);
                  var14.addAll(var17);
               }

               if (debug != null) {
                  debug.println("Returning " + var14.size() + " CRLs");
               }

               return var14;
            }
         } catch (IOException | CertificateException var18) {
            return Collections.emptySet();
         }
      }
   }

   private static Collection<X509CRL> getCRLs(X509CRLSelector var0, X509CertImpl var1, DistributionPoint var2, boolean[] var3, boolean var4, PublicKey var5, X509Certificate var6, String var7, List<CertStore> var8, Set<TrustAnchor> var9, Date var10, String var11) throws CertStoreException {
      GeneralNames var12 = var2.getFullName();
      if (var12 == null) {
         RDN var13 = var2.getRelativeName();
         if (var13 == null) {
            return Collections.emptySet();
         }

         try {
            GeneralNames var14 = var2.getCRLIssuer();
            if (var14 == null) {
               var12 = getFullNames((X500Name)var1.getIssuerDN(), var13);
            } else {
               if (var14.size() != 1) {
                  return Collections.emptySet();
               }

               var12 = getFullNames((X500Name)var14.get(0).getName(), var13);
            }
         } catch (IOException var20) {
            return Collections.emptySet();
         }
      }

      ArrayList var22 = new ArrayList();
      CertStoreException var23 = null;
      Iterator var15 = var12.iterator();

      while(var15.hasNext()) {
         try {
            GeneralName var16 = (GeneralName)var15.next();
            if (var16.getType() == 4) {
               X500Name var17 = (X500Name)var16.getName();
               var22.addAll(getCRLs(var17, var1.getIssuerX500Principal(), var8));
            } else if (var16.getType() == 6) {
               URIName var26 = (URIName)var16.getName();
               X509CRL var18 = getCRL(var26);
               if (var18 != null) {
                  var22.add(var18);
               }
            }
         } catch (CertStoreException var19) {
            var23 = var19;
         }
      }

      if (var22.isEmpty() && var23 != null) {
         throw var23;
      } else {
         ArrayList var24 = new ArrayList(2);
         Iterator var25 = var22.iterator();

         while(var25.hasNext()) {
            X509CRL var27 = (X509CRL)var25.next();

            try {
               var0.setIssuerNames((Collection)null);
               if (var0.match(var27) && verifyCRL(var1, var2, var27, var3, var4, var5, var6, var7, var9, var8, var10, var11)) {
                  var24.add(var27);
               }
            } catch (CRLException | IOException var21) {
               if (debug != null) {
                  debug.println("Exception verifying CRL: " + var21.getMessage());
                  var21.printStackTrace();
               }
            }
         }

         return var24;
      }
   }

   private static X509CRL getCRL(URIName var0) throws CertStoreException {
      URI var1 = var0.getURI();
      if (debug != null) {
         debug.println("Trying to fetch CRL from DP " + var1);
      }

      CertStore var2 = null;

      try {
         var2 = URICertStore.getInstance(new URICertStore.URICertStoreParameters(var1));
      } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException var4) {
         if (debug != null) {
            debug.println("Can't create URICertStore: " + var4.getMessage());
         }

         return null;
      }

      Collection var3 = var2.getCRLs((CRLSelector)null);
      return var3.isEmpty() ? null : (X509CRL)var3.iterator().next();
   }

   private static Collection<X509CRL> getCRLs(X500Name var0, X500Principal var1, List<CertStore> var2) throws CertStoreException {
      if (debug != null) {
         debug.println("Trying to fetch CRL from DP " + var0);
      }

      X509CRLSelector var3 = new X509CRLSelector();
      var3.addIssuer(var0.asX500Principal());
      var3.addIssuer(var1);
      ArrayList var4 = new ArrayList();
      PKIX.CertStoreTypeException var5 = null;
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         CertStore var7 = (CertStore)var6.next();

         try {
            Iterator var8 = var7.getCRLs(var3).iterator();

            while(var8.hasNext()) {
               CRL var9 = (CRL)var8.next();
               var4.add((X509CRL)var9);
            }
         } catch (CertStoreException var10) {
            if (debug != null) {
               debug.println("Exception while retrieving CRLs: " + var10);
               var10.printStackTrace();
            }

            var5 = new PKIX.CertStoreTypeException(var7.getType(), var10);
         }
      }

      if (var4.isEmpty() && var5 != null) {
         throw var5;
      } else {
         return var4;
      }
   }

   static boolean verifyCRL(X509CertImpl var0, DistributionPoint var1, X509CRL var2, boolean[] var3, boolean var4, PublicKey var5, X509Certificate var6, String var7, Set<TrustAnchor> var8, List<CertStore> var9, Date var10, String var11) throws CRLException, IOException {
      if (debug != null) {
         debug.println("DistributionPointFetcher.verifyCRL: checking revocation status for\n  SN: " + Debug.toHexString(var0.getSerialNumber()) + "\n  Subject: " + var0.getSubjectX500Principal() + "\n  Issuer: " + var0.getIssuerX500Principal());
      }

      boolean var12 = false;
      X509CRLImpl var13 = X509CRLImpl.toImpl(var2);
      IssuingDistributionPointExtension var14 = var13.getIssuingDistributionPointExtension();
      X500Name var15 = (X500Name)var0.getIssuerDN();
      X500Name var16 = (X500Name)var13.getIssuerDN();
      GeneralNames var17 = var1.getCRLIssuer();
      X500Name var18 = null;
      if (var17 != null) {
         if (var14 == null || ((Boolean)var14.get("indirect_crl")).equals(Boolean.FALSE)) {
            return false;
         }

         boolean var19 = false;
         Iterator var20 = var17.iterator();

         while(!var19 && var20.hasNext()) {
            GeneralNameInterface var21 = ((GeneralName)var20.next()).getName();
            if (var16.equals(var21)) {
               var18 = (X500Name)var21;
               var19 = true;
            }
         }

         if (!var19) {
            return false;
         }

         if (issues(var0, var13, var7)) {
            var5 = var0.getPublicKey();
         } else {
            var12 = true;
         }
      } else {
         if (!var16.equals(var15)) {
            if (debug != null) {
               debug.println("crl issuer does not equal cert issuer.\ncrl issuer: " + var16 + "\ncert issuer: " + var15);
            }

            return false;
         }

         KeyIdentifier var34 = var0.getAuthKeyId();
         KeyIdentifier var36 = var13.getAuthKeyId();
         if (var34 != null && var36 != null) {
            if (!var34.equals(var36)) {
               if (issues(var0, var13, var7)) {
                  var5 = var0.getPublicKey();
               } else {
                  var12 = true;
               }
            }
         } else if (issues(var0, var13, var7)) {
            var5 = var0.getPublicKey();
         }
      }

      if (!var12 && !var4) {
         return false;
      } else {
         boolean var42;
         Iterator var50;
         if (var14 != null) {
            DistributionPointName var35 = (DistributionPointName)var14.get("point");
            if (var35 != null) {
               GeneralNames var38 = var35.getFullName();
               if (var38 == null) {
                  RDN var39 = var35.getRelativeName();
                  if (var39 == null) {
                     if (debug != null) {
                        debug.println("IDP must be relative or full DN");
                     }

                     return false;
                  }

                  if (debug != null) {
                     debug.println("IDP relativeName:" + var39);
                  }

                  var38 = getFullNames(var16, var39);
               }

               if (var1.getFullName() == null && var1.getRelativeName() == null) {
                  boolean var44 = false;
                  Iterator var45 = var17.iterator();

                  while(!var44 && var45.hasNext()) {
                     GeneralNameInterface var47 = ((GeneralName)var45.next()).getName();

                     GeneralNameInterface var54;
                     for(var50 = var38.iterator(); !var44 && var50.hasNext(); var44 = var47.equals(var54)) {
                        var54 = ((GeneralName)var50.next()).getName();
                     }
                  }

                  if (!var44) {
                     return false;
                  }
               } else {
                  GeneralNames var40 = var1.getFullName();
                  if (var40 == null) {
                     RDN var22 = var1.getRelativeName();
                     if (var22 == null) {
                        if (debug != null) {
                           debug.println("DP must be relative or full DN");
                        }

                        return false;
                     }

                     if (debug != null) {
                        debug.println("DP relativeName:" + var22);
                     }

                     if (var12) {
                        if (var17.size() != 1) {
                           if (debug != null) {
                              debug.println("must only be one CRL issuer when relative name present");
                           }

                           return false;
                        }

                        var40 = getFullNames(var18, var22);
                     } else {
                        var40 = getFullNames(var15, var22);
                     }
                  }

                  var42 = false;
                  Iterator var23 = var38.iterator();

                  while(!var42 && var23.hasNext()) {
                     GeneralNameInterface var24 = ((GeneralName)var23.next()).getName();
                     if (debug != null) {
                        debug.println("idpName: " + var24);
                     }

                     GeneralNameInterface var26;
                     for(Iterator var25 = var40.iterator(); !var42 && var25.hasNext(); var42 = var24.equals(var26)) {
                        var26 = ((GeneralName)var25.next()).getName();
                        if (debug != null) {
                           debug.println("pointName: " + var26);
                        }
                     }
                  }

                  if (!var42) {
                     if (debug != null) {
                        debug.println("IDP name does not match DP name");
                     }

                     return false;
                  }
               }
            }

            Boolean var41 = (Boolean)var14.get("only_user_certs");
            if (var41.equals(Boolean.TRUE) && var0.getBasicConstraints() != -1) {
               if (debug != null) {
                  debug.println("cert must be a EE cert");
               }

               return false;
            }

            var41 = (Boolean)var14.get("only_ca_certs");
            if (var41.equals(Boolean.TRUE) && var0.getBasicConstraints() == -1) {
               if (debug != null) {
                  debug.println("cert must be a CA cert");
               }

               return false;
            }

            var41 = (Boolean)var14.get("only_attribute_certs");
            if (var41.equals(Boolean.TRUE)) {
               if (debug != null) {
                  debug.println("cert must not be an AA cert");
               }

               return false;
            }
         }

         boolean[] var37 = new boolean[9];
         ReasonFlags var43 = null;
         if (var14 != null) {
            var43 = (ReasonFlags)var14.get("reasons");
         }

         boolean[] var48 = var1.getReasonFlags();
         int var49;
         if (var43 != null) {
            if (var48 != null) {
               boolean[] var46 = var43.getFlags();

               for(var49 = 0; var49 < var37.length; ++var49) {
                  var37[var49] = var49 < var46.length && var46[var49] && var49 < var48.length && var48[var49];
               }
            } else {
               var37 = (boolean[])var43.getFlags().clone();
            }
         } else if (var14 == null || var43 == null) {
            if (var48 != null) {
               var37 = (boolean[])var48.clone();
            } else {
               Arrays.fill(var37, true);
            }
         }

         var42 = false;

         for(var49 = 0; var49 < var37.length && !var42; ++var49) {
            if (var37[var49] && (var49 >= var3.length || !var3[var49])) {
               var42 = true;
            }
         }

         if (!var42) {
            return false;
         } else {
            if (var12) {
               X509CertSelector var52 = new X509CertSelector();
               var52.setSubject(var16.asX500Principal());
               boolean[] var51 = new boolean[]{false, false, false, false, false, false, true};
               var52.setKeyUsage(var51);
               AuthorityKeyIdentifierExtension var56 = var13.getAuthKeyIdExtension();
               SerialNumber var27;
               if (var56 != null) {
                  byte[] var58 = var56.getEncodedKeyIdentifier();
                  if (var58 != null) {
                     var52.setSubjectKeyIdentifier(var58);
                  }

                  var27 = (SerialNumber)var56.get("serial_number");
                  if (var27 != null) {
                     var52.setSerialNumber(var27.getNumber());
                  }
               }

               HashSet var59 = new HashSet(var8);
               if (var5 != null) {
                  TrustAnchor var60;
                  if (var6 != null) {
                     var60 = new TrustAnchor(var6, (byte[])null);
                  } else {
                     X500Principal var28 = var0.getIssuerX500Principal();
                     var60 = new TrustAnchor(var28, var5, (byte[])null);
                  }

                  var59.add(var60);
               }

               var27 = null;

               PKIXBuilderParameters var62;
               try {
                  var62 = new PKIXBuilderParameters(var59, var52);
               } catch (InvalidAlgorithmParameterException var31) {
                  throw new CRLException(var31);
               }

               var62.setCertStores(var9);
               var62.setSigProvider(var7);
               var62.setDate(var10);

               try {
                  CertPathBuilder var61 = CertPathBuilder.getInstance("PKIX");
                  PKIXCertPathBuilderResult var29 = (PKIXCertPathBuilderResult)var61.build(var62);
                  var5 = var29.getPublicKey();
               } catch (GeneralSecurityException var30) {
                  throw new CRLException(var30);
               }
            }

            try {
               AlgorithmChecker.check(var5, var2, var11);
            } catch (CertPathValidatorException var33) {
               if (debug != null) {
                  debug.println("CRL signature algorithm check failed: " + var33);
               }

               return false;
            }

            try {
               var2.verify(var5, var7);
            } catch (GeneralSecurityException var32) {
               if (debug != null) {
                  debug.println("CRL signature failed to verify");
               }

               return false;
            }

            Set var55 = var2.getCriticalExtensionOIDs();
            if (var55 != null) {
               var55.remove(PKIXExtensions.IssuingDistributionPoint_Id.toString());
               if (!var55.isEmpty()) {
                  if (debug != null) {
                     debug.println("Unrecognized critical extension(s) in CRL: " + var55);
                     var50 = var55.iterator();

                     while(var50.hasNext()) {
                        String var57 = (String)var50.next();
                        debug.println(var57);
                     }
                  }

                  return false;
               }
            }

            for(int var53 = 0; var53 < var3.length; ++var53) {
               var3[var53] = var3[var53] || var53 < var37.length && var37[var53];
            }

            return true;
         }
      }
   }

   private static GeneralNames getFullNames(X500Name var0, RDN var1) throws IOException {
      ArrayList var2 = new ArrayList(var0.rdns());
      var2.add(var1);
      X500Name var3 = new X500Name((RDN[])var2.toArray(new RDN[0]));
      GeneralNames var4 = new GeneralNames();
      var4.add(new GeneralName(var3));
      return var4;
   }

   private static boolean issues(X509CertImpl var0, X509CRLImpl var1, String var2) throws IOException {
      boolean var3 = false;
      AdaptableX509CertSelector var4 = new AdaptableX509CertSelector();
      boolean[] var5 = var0.getKeyUsage();
      if (var5 != null) {
         var5[6] = true;
         var4.setKeyUsage(var5);
      }

      X500Principal var6 = var1.getIssuerX500Principal();
      var4.setSubject(var6);
      AuthorityKeyIdentifierExtension var7 = var1.getAuthKeyIdExtension();
      var4.setSkiAndSerialNumber(var7);
      var3 = var4.match(var0);
      if (var3 && (var7 == null || var0.getAuthorityKeyIdentifierExtension() == null)) {
         try {
            var1.verify(var0.getPublicKey(), var2);
            var3 = true;
         } catch (GeneralSecurityException var9) {
            var3 = false;
         }
      }

      return var3;
   }
}
