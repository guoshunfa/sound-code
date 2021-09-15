package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

class ForwardBuilder extends Builder {
   private static final Debug debug = Debug.getInstance("certpath");
   private final Set<X509Certificate> trustedCerts;
   private final Set<X500Principal> trustedSubjectDNs;
   private final Set<TrustAnchor> trustAnchors;
   private X509CertSelector eeSelector;
   private AdaptableX509CertSelector caSelector;
   private X509CertSelector caTargetSelector;
   TrustAnchor trustAnchor;
   private boolean searchAllCertStores = true;

   ForwardBuilder(PKIX.BuilderParams var1, boolean var2) {
      super(var1);
      this.trustAnchors = var1.trustAnchors();
      this.trustedCerts = new HashSet(this.trustAnchors.size());
      this.trustedSubjectDNs = new HashSet(this.trustAnchors.size());
      Iterator var3 = this.trustAnchors.iterator();

      while(var3.hasNext()) {
         TrustAnchor var4 = (TrustAnchor)var3.next();
         X509Certificate var5 = var4.getTrustedCert();
         if (var5 != null) {
            this.trustedCerts.add(var5);
            this.trustedSubjectDNs.add(var5.getSubjectX500Principal());
         } else {
            this.trustedSubjectDNs.add(var4.getCA());
         }
      }

      this.searchAllCertStores = var2;
   }

   Collection<X509Certificate> getMatchingCerts(State var1, List<CertStore> var2) throws CertStoreException, CertificateException, IOException {
      if (debug != null) {
         debug.println("ForwardBuilder.getMatchingCerts()...");
      }

      ForwardState var3 = (ForwardState)var1;
      ForwardBuilder.PKIXCertComparator var4 = new ForwardBuilder.PKIXCertComparator(this.trustedSubjectDNs, var3.cert);
      TreeSet var5 = new TreeSet(var4);
      if (var3.isInitial()) {
         this.getMatchingEECerts(var3, var2, var5);
      }

      this.getMatchingCACerts(var3, var2, var5);
      return var5;
   }

   private void getMatchingEECerts(ForwardState var1, List<CertStore> var2, Collection<X509Certificate> var3) throws IOException {
      if (debug != null) {
         debug.println("ForwardBuilder.getMatchingEECerts()...");
      }

      if (this.eeSelector == null) {
         this.eeSelector = (X509CertSelector)this.targetCertConstraints.clone();
         this.eeSelector.setCertificateValid(this.buildParams.date());
         if (this.buildParams.explicitPolicyRequired()) {
            this.eeSelector.setPolicy(this.getMatchingPolicies());
         }

         this.eeSelector.setBasicConstraints(-2);
      }

      this.addMatchingCerts(this.eeSelector, var2, var3, this.searchAllCertStores);
   }

   private void getMatchingCACerts(ForwardState var1, List<CertStore> var2, Collection<X509Certificate> var3) throws IOException {
      if (debug != null) {
         debug.println("ForwardBuilder.getMatchingCACerts()...");
      }

      int var4 = var3.size();
      Object var5 = null;
      if (var1.isInitial()) {
         if (this.targetCertConstraints.getBasicConstraints() == -2) {
            return;
         }

         if (debug != null) {
            debug.println("ForwardBuilder.getMatchingCACerts(): the target is a CA");
         }

         if (this.caTargetSelector == null) {
            this.caTargetSelector = (X509CertSelector)this.targetCertConstraints.clone();
            if (this.buildParams.explicitPolicyRequired()) {
               this.caTargetSelector.setPolicy(this.getMatchingPolicies());
            }
         }

         var5 = this.caTargetSelector;
      } else {
         if (this.caSelector == null) {
            this.caSelector = new AdaptableX509CertSelector();
            if (this.buildParams.explicitPolicyRequired()) {
               this.caSelector.setPolicy(this.getMatchingPolicies());
            }
         }

         this.caSelector.setSubject(var1.issuerDN);
         CertPathHelper.setPathToNames(this.caSelector, var1.subjectNamesTraversed);
         this.caSelector.setValidityPeriod(var1.cert.getNotBefore(), var1.cert.getNotAfter());
         var5 = this.caSelector;
      }

      ((X509CertSelector)var5).setBasicConstraints(-1);
      Iterator var6 = this.trustedCerts.iterator();

      while(var6.hasNext()) {
         X509Certificate var7 = (X509Certificate)var6.next();
         if (((X509CertSelector)var5).match(var7)) {
            if (debug != null) {
               debug.println("ForwardBuilder.getMatchingCACerts: found matching trust anchor.\n  SN: " + Debug.toHexString(var7.getSerialNumber()) + "\n  Subject: " + var7.getSubjectX500Principal() + "\n  Issuer: " + var7.getIssuerX500Principal());
            }

            if (var3.add(var7) && !this.searchAllCertStores) {
               return;
            }
         }
      }

      ((X509CertSelector)var5).setCertificateValid(this.buildParams.date());
      ((X509CertSelector)var5).setBasicConstraints(var1.traversedCACerts);
      if (!var1.isInitial() && this.buildParams.maxPathLength() != -1 && this.buildParams.maxPathLength() <= var1.traversedCACerts || !this.addMatchingCerts((X509CertSelector)var5, var2, var3, this.searchAllCertStores) || this.searchAllCertStores) {
         if (!var1.isInitial() && Builder.USE_AIA) {
            AuthorityInfoAccessExtension var8 = var1.cert.getAuthorityInfoAccessExtension();
            if (var8 != null) {
               this.getCerts(var8, var3);
            }
         }

         if (debug != null) {
            int var9 = var3.size() - var4;
            debug.println("ForwardBuilder.getMatchingCACerts: found " + var9 + " CA certs");
         }

      }
   }

   private boolean getCerts(AuthorityInfoAccessExtension var1, Collection<X509Certificate> var2) {
      if (!Builder.USE_AIA) {
         return false;
      } else {
         List var3 = var1.getAccessDescriptions();
         if (var3 != null && !var3.isEmpty()) {
            boolean var4 = false;
            Iterator var5 = var3.iterator();

            while(true) {
               CertStore var7;
               do {
                  if (!var5.hasNext()) {
                     return var4;
                  }

                  AccessDescription var6 = (AccessDescription)var5.next();
                  var7 = URICertStore.getInstance(var6);
               } while(var7 == null);

               try {
                  if (var2.addAll(var7.getCertificates(this.caSelector))) {
                     var4 = true;
                     if (!this.searchAllCertStores) {
                        return true;
                     }
                  }
               } catch (CertStoreException var9) {
                  if (debug != null) {
                     debug.println("exception getting certs from CertStore:");
                     var9.printStackTrace();
                  }
               }
            }
         } else {
            return false;
         }
      }
   }

   void verifyCert(X509Certificate var1, State var2, List<X509Certificate> var3) throws GeneralSecurityException {
      if (debug != null) {
         debug.println("ForwardBuilder.verifyCert(SN: " + Debug.toHexString(var1.getSerialNumber()) + "\n  Issuer: " + var1.getIssuerX500Principal() + ")\n  Subject: " + var1.getSubjectX500Principal() + ")");
      }

      ForwardState var4 = (ForwardState)var2;
      var4.untrustedChecker.check(var1, Collections.emptySet());
      if (var3 != null) {
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            X509Certificate var6 = (X509Certificate)var5.next();
            if (var1.equals(var6)) {
               if (debug != null) {
                  debug.println("loop detected!!");
               }

               throw new CertPathValidatorException("loop detected");
            }
         }
      }

      boolean var10 = this.trustedCerts.contains(var1);
      if (!var10) {
         Set var11 = var1.getCriticalExtensionOIDs();
         if (var11 == null) {
            var11 = Collections.emptySet();
         }

         Iterator var7 = var4.forwardCheckers.iterator();

         PKIXCertPathChecker var8;
         while(var7.hasNext()) {
            var8 = (PKIXCertPathChecker)var7.next();
            var8.check(var1, var11);
         }

         var7 = this.buildParams.certPathCheckers().iterator();

         while(var7.hasNext()) {
            var8 = (PKIXCertPathChecker)var7.next();
            if (!var8.isForwardCheckingSupported()) {
               Set var9 = var8.getSupportedExtensions();
               if (var9 != null) {
                  var11.removeAll(var9);
               }
            }
         }

         if (!var11.isEmpty()) {
            var11.remove(PKIXExtensions.BasicConstraints_Id.toString());
            var11.remove(PKIXExtensions.NameConstraints_Id.toString());
            var11.remove(PKIXExtensions.CertificatePolicies_Id.toString());
            var11.remove(PKIXExtensions.PolicyMappings_Id.toString());
            var11.remove(PKIXExtensions.PolicyConstraints_Id.toString());
            var11.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
            var11.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
            var11.remove(PKIXExtensions.KeyUsage_Id.toString());
            var11.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
            if (!var11.isEmpty()) {
               throw new CertPathValidatorException("Unrecognized critical extension(s)", (Throwable)null, (CertPath)null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
            }
         }
      }

      if (!var4.isInitial()) {
         if (!var10) {
            if (var1.getBasicConstraints() == -1) {
               throw new CertificateException("cert is NOT a CA cert");
            }

            KeyChecker.verifyCAKeyUsage(var1);
         }

         if (!var4.keyParamsNeeded()) {
            var4.cert.verify(var1.getPublicKey(), this.buildParams.sigProvider());
         }

      }
   }

   boolean isPathCompleted(X509Certificate var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.trustAnchors.iterator();

      TrustAnchor var4;
      X500Principal var5;
      PublicKey var6;
      while(var3.hasNext()) {
         var4 = (TrustAnchor)var3.next();
         if (var4.getTrustedCert() != null) {
            if (var1.equals(var4.getTrustedCert())) {
               this.trustAnchor = var4;
               return true;
            }
         } else {
            var5 = var4.getCA();
            var6 = var4.getCAPublicKey();
            if (var5 != null && var6 != null && var5.equals(var1.getSubjectX500Principal()) && var6.equals(var1.getPublicKey())) {
               this.trustAnchor = var4;
               return true;
            }

            var2.add(var4);
         }
      }

      var3 = var2.iterator();

      while(true) {
         do {
            do {
               do {
                  if (!var3.hasNext()) {
                     return false;
                  }

                  var4 = (TrustAnchor)var3.next();
                  var5 = var4.getCA();
                  var6 = var4.getCAPublicKey();
               } while(var5 == null);
            } while(!var5.equals(var1.getIssuerX500Principal()));
         } while(PKIX.isDSAPublicKeyWithoutParams(var6));

         try {
            var1.verify(var6, this.buildParams.sigProvider());
            break;
         } catch (InvalidKeyException var8) {
            if (debug != null) {
               debug.println("ForwardBuilder.isPathCompleted() invalid DSA key found");
            }
         } catch (GeneralSecurityException var9) {
            if (debug != null) {
               debug.println("ForwardBuilder.isPathCompleted() unexpected exception");
               var9.printStackTrace();
            }
         }
      }

      this.trustAnchor = var4;
      return true;
   }

   void addCertToPath(X509Certificate var1, LinkedList<X509Certificate> var2) {
      var2.addFirst(var1);
   }

   void removeFinalCertFromPath(LinkedList<X509Certificate> var1) {
      var1.removeFirst();
   }

   static class PKIXCertComparator implements Comparator<X509Certificate> {
      static final String METHOD_NME = "PKIXCertComparator.compare()";
      private final Set<X500Principal> trustedSubjectDNs;
      private final X509CertSelector certSkidSelector;

      PKIXCertComparator(Set<X500Principal> var1, X509CertImpl var2) throws IOException {
         this.trustedSubjectDNs = var1;
         this.certSkidSelector = this.getSelector(var2);
      }

      private X509CertSelector getSelector(X509CertImpl var1) throws IOException {
         if (var1 != null) {
            AuthorityKeyIdentifierExtension var2 = var1.getAuthorityKeyIdentifierExtension();
            if (var2 != null) {
               byte[] var3 = var2.getEncodedKeyIdentifier();
               if (var3 != null) {
                  X509CertSelector var4 = new X509CertSelector();
                  var4.setSubjectKeyIdentifier(var3);
                  return var4;
               }
            }
         }

         return null;
      }

      public int compare(X509Certificate var1, X509Certificate var2) {
         if (var1.equals(var2)) {
            return 0;
         } else {
            if (this.certSkidSelector != null) {
               if (this.certSkidSelector.match(var1)) {
                  return -1;
               }

               if (this.certSkidSelector.match(var2)) {
                  return 1;
               }
            }

            X500Principal var3 = var1.getIssuerX500Principal();
            X500Principal var4 = var2.getIssuerX500Principal();
            X500Name var5 = X500Name.asX500Name(var3);
            X500Name var6 = X500Name.asX500Name(var4);
            if (ForwardBuilder.debug != null) {
               ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Issuer:  " + var3);
               ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Issuer:  " + var4);
            }

            if (ForwardBuilder.debug != null) {
               ForwardBuilder.debug.println("PKIXCertComparator.compare() MATCH TRUSTED SUBJECT TEST...");
            }

            boolean var7 = this.trustedSubjectDNs.contains(var3);
            boolean var8 = this.trustedSubjectDNs.contains(var4);
            if (ForwardBuilder.debug != null) {
               ForwardBuilder.debug.println("PKIXCertComparator.compare() m1: " + var7);
               ForwardBuilder.debug.println("PKIXCertComparator.compare() m2: " + var8);
            }

            if (var7 && var8) {
               return -1;
            } else if (var7) {
               return -1;
            } else if (var8) {
               return 1;
            } else {
               if (ForwardBuilder.debug != null) {
                  ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING DESCENDANT TEST...");
               }

               Iterator var9 = this.trustedSubjectDNs.iterator();

               while(true) {
                  X500Principal var10;
                  X500Name var11;
                  int var17;
                  int var18;
                  if (var9.hasNext()) {
                     var10 = (X500Principal)var9.next();
                     var11 = X500Name.asX500Name(var10);
                     var17 = Builder.distance(var11, var5, -1);
                     var18 = Builder.distance(var11, var6, -1);
                     if (ForwardBuilder.debug != null) {
                        ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + var17);
                        ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + var18);
                     }

                     if (var17 <= 0 && var18 <= 0) {
                        continue;
                     }

                     if (var17 == var18) {
                        return -1;
                     }

                     if (var17 > 0 && var18 <= 0) {
                        return -1;
                     }

                     if (var17 <= 0 && var18 > 0) {
                        return 1;
                     }

                     if (var17 < var18) {
                        return -1;
                     }

                     return 1;
                  }

                  if (ForwardBuilder.debug != null) {
                     ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING ANCESTOR TEST...");
                  }

                  var9 = this.trustedSubjectDNs.iterator();

                  while(true) {
                     if (var9.hasNext()) {
                        var10 = (X500Principal)var9.next();
                        var11 = X500Name.asX500Name(var10);
                        var17 = Builder.distance(var11, var5, Integer.MAX_VALUE);
                        var18 = Builder.distance(var11, var6, Integer.MAX_VALUE);
                        if (ForwardBuilder.debug != null) {
                           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + var17);
                           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + var18);
                        }

                        if (var17 >= 0 && var18 >= 0) {
                           continue;
                        }

                        if (var17 == var18) {
                           return -1;
                        }

                        if (var17 < 0 && var18 >= 0) {
                           return -1;
                        }

                        if (var17 >= 0 && var18 < 0) {
                           return 1;
                        }

                        if (var17 > var18) {
                           return -1;
                        }

                        return 1;
                     }

                     if (ForwardBuilder.debug != null) {
                        ForwardBuilder.debug.println("PKIXCertComparator.compare() SAME NAMESPACE AS TRUSTED TEST...");
                     }

                     var9 = this.trustedSubjectDNs.iterator();

                     int var14;
                     int var15;
                     do {
                        X500Name var12;
                        X500Name var13;
                        do {
                           if (!var9.hasNext()) {
                              if (ForwardBuilder.debug != null) {
                                 ForwardBuilder.debug.println("PKIXCertComparator.compare() CERT ISSUER/SUBJECT COMPARISON TEST...");
                              }

                              X500Principal var16 = var1.getSubjectX500Principal();
                              var10 = var2.getSubjectX500Principal();
                              var11 = X500Name.asX500Name(var16);
                              var12 = X500Name.asX500Name(var10);
                              if (ForwardBuilder.debug != null) {
                                 ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Subject: " + var16);
                                 ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Subject: " + var10);
                              }

                              var18 = Builder.distance(var11, var5, Integer.MAX_VALUE);
                              var14 = Builder.distance(var12, var6, Integer.MAX_VALUE);
                              if (ForwardBuilder.debug != null) {
                                 ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI1: " + var18);
                                 ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI2: " + var14);
                              }

                              if (var14 > var18) {
                                 return -1;
                              }

                              if (var14 < var18) {
                                 return 1;
                              }

                              if (ForwardBuilder.debug != null) {
                                 ForwardBuilder.debug.println("PKIXCertComparator.compare() no tests matched; RETURN 0");
                              }

                              return -1;
                           }

                           var10 = (X500Principal)var9.next();
                           var11 = X500Name.asX500Name(var10);
                           var12 = var11.commonAncestor(var5);
                           var13 = var11.commonAncestor(var6);
                           if (ForwardBuilder.debug != null) {
                              ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo1: " + String.valueOf((Object)var12));
                              ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo2: " + String.valueOf((Object)var13));
                           }
                        } while(var12 == null && var13 == null);

                        if (var12 == null || var13 == null) {
                           if (var12 == null) {
                              return 1;
                           }

                           return -1;
                        }

                        var14 = Builder.hops(var11, var5, Integer.MAX_VALUE);
                        var15 = Builder.hops(var11, var6, Integer.MAX_VALUE);
                        if (ForwardBuilder.debug != null) {
                           ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto1: " + var14);
                           ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto2: " + var15);
                        }
                     } while(var14 == var15);

                     if (var14 > var15) {
                        return 1;
                     }

                     return -1;
                  }
               }
            }
         }
      }
   }
}
