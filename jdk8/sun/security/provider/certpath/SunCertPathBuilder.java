package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathChecker;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.PKIXExtensions;

public final class SunCertPathBuilder extends CertPathBuilderSpi {
   private static final Debug debug = Debug.getInstance("certpath");
   private PKIX.BuilderParams buildParams;
   private CertificateFactory cf;
   private boolean pathCompleted = false;
   private PolicyNode policyTreeResult;
   private TrustAnchor trustAnchor;
   private PublicKey finalPublicKey;

   public SunCertPathBuilder() throws CertPathBuilderException {
      try {
         this.cf = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var2) {
         throw new CertPathBuilderException(var2);
      }
   }

   public CertPathChecker engineGetRevocationChecker() {
      return new RevocationChecker();
   }

   public CertPathBuilderResult engineBuild(CertPathParameters var1) throws CertPathBuilderException, InvalidAlgorithmParameterException {
      if (debug != null) {
         debug.println("SunCertPathBuilder.engineBuild(" + var1 + ")");
      }

      this.buildParams = PKIX.checkBuilderParams(var1);
      return this.build();
   }

   private PKIXCertPathBuilderResult build() throws CertPathBuilderException {
      ArrayList var1 = new ArrayList();
      PKIXCertPathBuilderResult var2 = this.buildCertPath(false, var1);
      if (var2 == null) {
         if (debug != null) {
            debug.println("SunCertPathBuilder.engineBuild: 2nd pass; try building again searching all certstores");
         }

         var1.clear();
         var2 = this.buildCertPath(true, var1);
         if (var2 == null) {
            throw new SunCertPathBuilderException("unable to find valid certification path to requested target", new AdjacencyList(var1));
         }
      }

      return var2;
   }

   private PKIXCertPathBuilderResult buildCertPath(boolean var1, List<List<Vertex>> var2) throws CertPathBuilderException {
      this.pathCompleted = false;
      this.trustAnchor = null;
      this.finalPublicKey = null;
      this.policyTreeResult = null;
      LinkedList var3 = new LinkedList();

      try {
         this.buildForward(var2, var3, var1);
      } catch (IOException | GeneralSecurityException var6) {
         if (debug != null) {
            debug.println("SunCertPathBuilder.engineBuild() exception in build");
            var6.printStackTrace();
         }

         throw new SunCertPathBuilderException("unable to find valid certification path to requested target", var6, new AdjacencyList(var2));
      }

      try {
         if (this.pathCompleted) {
            if (debug != null) {
               debug.println("SunCertPathBuilder.engineBuild() pathCompleted");
            }

            Collections.reverse(var3);
            return new SunCertPathBuilderResult(this.cf.generateCertPath((List)var3), this.trustAnchor, this.policyTreeResult, this.finalPublicKey, new AdjacencyList(var2));
         } else {
            return null;
         }
      } catch (CertificateException var5) {
         if (debug != null) {
            debug.println("SunCertPathBuilder.engineBuild() exception in wrap-up");
            var5.printStackTrace();
         }

         throw new SunCertPathBuilderException("unable to find valid certification path to requested target", var5, new AdjacencyList(var2));
      }
   }

   private void buildForward(List<List<Vertex>> var1, LinkedList<X509Certificate> var2, boolean var3) throws GeneralSecurityException, IOException {
      if (debug != null) {
         debug.println("SunCertPathBuilder.buildForward()...");
      }

      ForwardState var4 = new ForwardState();
      var4.initState(this.buildParams.certPathCheckers());
      var1.clear();
      var1.add(new LinkedList());
      var4.untrustedChecker = new UntrustedChecker();
      this.depthFirstSearchForward(this.buildParams.targetSubject(), var4, new ForwardBuilder(this.buildParams, var3), var1, var2);
   }

   private void depthFirstSearchForward(X500Principal var1, ForwardState var2, ForwardBuilder var3, List<List<Vertex>> var4, LinkedList<X509Certificate> var5) throws GeneralSecurityException, IOException {
      if (debug != null) {
         debug.println("SunCertPathBuilder.depthFirstSearchForward(" + var1 + ", " + var2.toString() + ")");
      }

      Collection var6 = var3.getMatchingCerts(var2, this.buildParams.certStores());
      List var7 = addVertices(var6, var4);
      if (debug != null) {
         debug.println("SunCertPathBuilder.depthFirstSearchForward(): certs.size=" + var7.size());
      }

      Iterator var8 = var7.iterator();

      label175:
      while(var8.hasNext()) {
         Vertex var9 = (Vertex)var8.next();
         ForwardState var10 = (ForwardState)var2.clone();
         X509Certificate var11 = var9.getCertificate();

         try {
            var3.verifyCert(var11, var10, var5);
         } catch (GeneralSecurityException var27) {
            if (debug != null) {
               debug.println("SunCertPathBuilder.depthFirstSearchForward(): validation failed: " + var27);
               var27.printStackTrace();
            }

            var9.setThrowable(var27);
            continue;
         }

         if (!var3.isPathCompleted(var11)) {
            var3.addCertToPath(var11, var5);
            var10.updateState(var11);
            var4.add(new LinkedList());
            var9.setIndex(var4.size() - 1);
            this.depthFirstSearchForward(var11.getIssuerX500Principal(), var10, var3, var4, var5);
            if (this.pathCompleted) {
               return;
            }

            if (debug != null) {
               debug.println("SunCertPathBuilder.depthFirstSearchForward(): backtracking");
            }

            var3.removeFinalCertFromPath(var5);
         } else {
            if (debug != null) {
               debug.println("SunCertPathBuilder.depthFirstSearchForward(): commencing final verification");
            }

            ArrayList var12 = new ArrayList(var5);
            if (var3.trustAnchor.getTrustedCert() == null) {
               var12.add(0, var11);
            }

            Set var13 = Collections.singleton("2.5.29.32.0");
            PolicyNodeImpl var14 = new PolicyNodeImpl((PolicyNodeImpl)null, "2.5.29.32.0", (Set)null, false, var13, false);
            ArrayList var15 = new ArrayList();
            PolicyChecker var16 = new PolicyChecker(this.buildParams.initialPolicies(), var12.size(), this.buildParams.explicitPolicyRequired(), this.buildParams.policyMappingInhibited(), this.buildParams.anyPolicyInhibited(), this.buildParams.policyQualifiersRejected(), var14);
            var15.add(var16);
            var15.add(new AlgorithmChecker(var3.trustAnchor, this.buildParams.date(), this.buildParams.variant()));
            BasicChecker var17 = null;
            if (var10.keyParamsNeeded()) {
               PublicKey var18 = var11.getPublicKey();
               if (var3.trustAnchor.getTrustedCert() == null) {
                  var18 = var3.trustAnchor.getCAPublicKey();
                  if (debug != null) {
                     debug.println("SunCertPathBuilder.depthFirstSearchForward using buildParams public key: " + var18.toString());
                  }
               }

               TrustAnchor var19 = new TrustAnchor(var11.getSubjectX500Principal(), var18, (byte[])null);
               var17 = new BasicChecker(var19, this.buildParams.date(), this.buildParams.sigProvider(), true);
               var15.add(var17);
            }

            this.buildParams.setCertPath(this.cf.generateCertPath((List)var12));
            boolean var28 = false;
            List var29 = this.buildParams.certPathCheckers();
            Iterator var20 = var29.iterator();

            while(var20.hasNext()) {
               PKIXCertPathChecker var21 = (PKIXCertPathChecker)var20.next();
               if (var21 instanceof PKIXRevocationChecker) {
                  if (var28) {
                     throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
                  }

                  var28 = true;
                  if (var21 instanceof RevocationChecker) {
                     ((RevocationChecker)var21).init(var3.trustAnchor, this.buildParams);
                  }
               }
            }

            if (this.buildParams.revocationEnabled() && !var28) {
               var15.add(new RevocationChecker(var3.trustAnchor, this.buildParams));
            }

            var15.addAll(var29);

            for(int var30 = 0; var30 < var12.size(); ++var30) {
               X509Certificate var32 = (X509Certificate)var12.get(var30);
               if (debug != null) {
                  debug.println("current subject = " + var32.getSubjectX500Principal());
               }

               Set var22 = var32.getCriticalExtensionOIDs();
               if (var22 == null) {
                  var22 = Collections.emptySet();
               }

               Iterator var23 = var15.iterator();

               PKIXCertPathChecker var24;
               while(var23.hasNext()) {
                  var24 = (PKIXCertPathChecker)var23.next();
                  if (!var24.isForwardCheckingSupported()) {
                     if (var30 == 0) {
                        var24.init(false);
                        if (var24 instanceof AlgorithmChecker) {
                           ((AlgorithmChecker)var24).trySetTrustAnchor(var3.trustAnchor);
                        }
                     }

                     try {
                        var24.check(var32, var22);
                     } catch (CertPathValidatorException var26) {
                        if (debug != null) {
                           debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification failed: " + var26);
                        }

                        if (this.buildParams.targetCertConstraints().match(var32) && var26.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                           throw var26;
                        }

                        var9.setThrowable(var26);
                        continue label175;
                     }
                  }
               }

               var23 = this.buildParams.certPathCheckers().iterator();

               while(var23.hasNext()) {
                  var24 = (PKIXCertPathChecker)var23.next();
                  if (var24.isForwardCheckingSupported()) {
                     Set var25 = var24.getSupportedExtensions();
                     if (var25 != null) {
                        var22.removeAll(var25);
                     }
                  }
               }

               if (!var22.isEmpty()) {
                  var22.remove(PKIXExtensions.BasicConstraints_Id.toString());
                  var22.remove(PKIXExtensions.NameConstraints_Id.toString());
                  var22.remove(PKIXExtensions.CertificatePolicies_Id.toString());
                  var22.remove(PKIXExtensions.PolicyMappings_Id.toString());
                  var22.remove(PKIXExtensions.PolicyConstraints_Id.toString());
                  var22.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
                  var22.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
                  var22.remove(PKIXExtensions.KeyUsage_Id.toString());
                  var22.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
                  if (!var22.isEmpty()) {
                     throw new CertPathValidatorException("unrecognized critical extension(s)", (Throwable)null, (CertPath)null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
                  }
               }
            }

            if (debug != null) {
               debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification succeeded - path completed!");
            }

            this.pathCompleted = true;
            if (var3.trustAnchor.getTrustedCert() == null) {
               var3.addCertToPath(var11, var5);
            }

            this.trustAnchor = var3.trustAnchor;
            if (var17 != null) {
               this.finalPublicKey = var17.getPublicKey();
            } else {
               Object var31;
               if (var5.isEmpty()) {
                  var31 = var3.trustAnchor.getTrustedCert();
               } else {
                  var31 = (Certificate)var5.getLast();
               }

               this.finalPublicKey = ((Certificate)var31).getPublicKey();
            }

            this.policyTreeResult = var16.getPolicyTree();
            return;
         }
      }

   }

   private static List<Vertex> addVertices(Collection<X509Certificate> var0, List<List<Vertex>> var1) {
      List var2 = (List)var1.get(var1.size() - 1);
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         X509Certificate var4 = (X509Certificate)var3.next();
         Vertex var5 = new Vertex(var4);
         var2.add(var5);
      }

      return var2;
   }

   private static boolean anchorIsTarget(TrustAnchor var0, CertSelector var1) {
      X509Certificate var2 = var0.getTrustedCert();
      return var2 != null ? var1.match(var2) : false;
   }
}
