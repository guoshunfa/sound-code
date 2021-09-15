package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.PolicyNode;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.CertificatePoliciesExtension;
import sun.security.x509.CertificatePolicyMap;
import sun.security.x509.InhibitAnyPolicyExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.PolicyConstraintsExtension;
import sun.security.x509.PolicyInformation;
import sun.security.x509.PolicyMappingsExtension;
import sun.security.x509.X509CertImpl;

class PolicyChecker extends PKIXCertPathChecker {
   private final Set<String> initPolicies;
   private final int certPathLen;
   private final boolean expPolicyRequired;
   private final boolean polMappingInhibited;
   private final boolean anyPolicyInhibited;
   private final boolean rejectPolicyQualifiers;
   private PolicyNodeImpl rootNode;
   private int explicitPolicy;
   private int policyMapping;
   private int inhibitAnyPolicy;
   private int certIndex;
   private Set<String> supportedExts;
   private static final Debug debug = Debug.getInstance("certpath");
   static final String ANY_POLICY = "2.5.29.32.0";

   PolicyChecker(Set<String> var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6, PolicyNodeImpl var7) {
      if (var1.isEmpty()) {
         this.initPolicies = new HashSet(1);
         this.initPolicies.add("2.5.29.32.0");
      } else {
         this.initPolicies = new HashSet(var1);
      }

      this.certPathLen = var2;
      this.expPolicyRequired = var3;
      this.polMappingInhibited = var4;
      this.anyPolicyInhibited = var5;
      this.rejectPolicyQualifiers = var6;
      this.rootNode = var7;
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (var1) {
         throw new CertPathValidatorException("forward checking not supported");
      } else {
         this.certIndex = 1;
         this.explicitPolicy = this.expPolicyRequired ? 0 : this.certPathLen + 1;
         this.policyMapping = this.polMappingInhibited ? 0 : this.certPathLen + 1;
         this.inhibitAnyPolicy = this.anyPolicyInhibited ? 0 : this.certPathLen + 1;
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      if (this.supportedExts == null) {
         this.supportedExts = new HashSet(4);
         this.supportedExts.add(PKIXExtensions.CertificatePolicies_Id.toString());
         this.supportedExts.add(PKIXExtensions.PolicyMappings_Id.toString());
         this.supportedExts.add(PKIXExtensions.PolicyConstraints_Id.toString());
         this.supportedExts.add(PKIXExtensions.InhibitAnyPolicy_Id.toString());
         this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
      }

      return this.supportedExts;
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      this.checkPolicy((X509Certificate)var1);
      if (var2 != null && !var2.isEmpty()) {
         var2.remove(PKIXExtensions.CertificatePolicies_Id.toString());
         var2.remove(PKIXExtensions.PolicyMappings_Id.toString());
         var2.remove(PKIXExtensions.PolicyConstraints_Id.toString());
         var2.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
      }

   }

   private void checkPolicy(X509Certificate var1) throws CertPathValidatorException {
      String var2 = "certificate policies";
      if (debug != null) {
         debug.println("PolicyChecker.checkPolicy() ---checking " + var2 + "...");
         debug.println("PolicyChecker.checkPolicy() certIndex = " + this.certIndex);
         debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: explicitPolicy = " + this.explicitPolicy);
         debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyMapping = " + this.policyMapping);
         debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
         debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyTree = " + this.rootNode);
      }

      X509CertImpl var3 = null;

      try {
         var3 = X509CertImpl.toImpl(var1);
      } catch (CertificateException var5) {
         throw new CertPathValidatorException(var5);
      }

      boolean var4 = this.certIndex == this.certPathLen;
      this.rootNode = processPolicies(this.certIndex, this.initPolicies, this.explicitPolicy, this.policyMapping, this.inhibitAnyPolicy, this.rejectPolicyQualifiers, this.rootNode, var3, var4);
      if (!var4) {
         this.explicitPolicy = mergeExplicitPolicy(this.explicitPolicy, var3, var4);
         this.policyMapping = mergePolicyMapping(this.policyMapping, var3);
         this.inhibitAnyPolicy = mergeInhibitAnyPolicy(this.inhibitAnyPolicy, var3);
      }

      ++this.certIndex;
      if (debug != null) {
         debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: explicitPolicy = " + this.explicitPolicy);
         debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyMapping = " + this.policyMapping);
         debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
         debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyTree = " + this.rootNode);
         debug.println("PolicyChecker.checkPolicy() " + var2 + " verified");
      }

   }

   static int mergeExplicitPolicy(int var0, X509CertImpl var1, boolean var2) throws CertPathValidatorException {
      if (var0 > 0 && !X509CertImpl.isSelfIssued(var1)) {
         --var0;
      }

      try {
         PolicyConstraintsExtension var3 = var1.getPolicyConstraintsExtension();
         if (var3 == null) {
            return var0;
         } else {
            int var4 = var3.get("require");
            if (debug != null) {
               debug.println("PolicyChecker.mergeExplicitPolicy() require Index from cert = " + var4);
            }

            if (!var2) {
               if (var4 != -1 && (var0 == -1 || var4 < var0)) {
                  var0 = var4;
               }
            } else if (var4 == 0) {
               var0 = var4;
            }

            return var0;
         }
      } catch (IOException var5) {
         if (debug != null) {
            debug.println("PolicyChecker.mergeExplicitPolicy unexpected exception");
            var5.printStackTrace();
         }

         throw new CertPathValidatorException(var5);
      }
   }

   static int mergePolicyMapping(int var0, X509CertImpl var1) throws CertPathValidatorException {
      if (var0 > 0 && !X509CertImpl.isSelfIssued(var1)) {
         --var0;
      }

      try {
         PolicyConstraintsExtension var2 = var1.getPolicyConstraintsExtension();
         if (var2 == null) {
            return var0;
         } else {
            int var3 = var2.get("inhibit");
            if (debug != null) {
               debug.println("PolicyChecker.mergePolicyMapping() inhibit Index from cert = " + var3);
            }

            if (var3 != -1 && (var0 == -1 || var3 < var0)) {
               var0 = var3;
            }

            return var0;
         }
      } catch (IOException var4) {
         if (debug != null) {
            debug.println("PolicyChecker.mergePolicyMapping unexpected exception");
            var4.printStackTrace();
         }

         throw new CertPathValidatorException(var4);
      }
   }

   static int mergeInhibitAnyPolicy(int var0, X509CertImpl var1) throws CertPathValidatorException {
      if (var0 > 0 && !X509CertImpl.isSelfIssued(var1)) {
         --var0;
      }

      try {
         InhibitAnyPolicyExtension var2 = (InhibitAnyPolicyExtension)var1.getExtension(PKIXExtensions.InhibitAnyPolicy_Id);
         if (var2 == null) {
            return var0;
         } else {
            int var3 = var2.get("skip_certs");
            if (debug != null) {
               debug.println("PolicyChecker.mergeInhibitAnyPolicy() skipCerts Index from cert = " + var3);
            }

            if (var3 != -1 && var3 < var0) {
               var0 = var3;
            }

            return var0;
         }
      } catch (IOException var4) {
         if (debug != null) {
            debug.println("PolicyChecker.mergeInhibitAnyPolicy unexpected exception");
            var4.printStackTrace();
         }

         throw new CertPathValidatorException(var4);
      }
   }

   static PolicyNodeImpl processPolicies(int var0, Set<String> var1, int var2, int var3, int var4, boolean var5, PolicyNodeImpl var6, X509CertImpl var7, boolean var8) throws CertPathValidatorException {
      boolean var9 = false;
      PolicyNodeImpl var11 = null;
      Object var12 = new HashSet();
      if (var6 == null) {
         var11 = null;
      } else {
         var11 = var6.copyTree();
      }

      CertificatePoliciesExtension var13 = var7.getCertificatePoliciesExtension();
      if (var13 != null && var11 != null) {
         var9 = var13.isCritical();
         if (debug != null) {
            debug.println("PolicyChecker.processPolicies() policiesCritical = " + var9);
         }

         List var10;
         try {
            var10 = var13.get("policies");
         } catch (IOException var20) {
            throw new CertPathValidatorException("Exception while retrieving policyOIDs", var20);
         }

         if (debug != null) {
            debug.println("PolicyChecker.processPolicies() rejectPolicyQualifiers = " + var5);
         }

         boolean var14 = false;
         Iterator var15 = var10.iterator();

         while(var15.hasNext()) {
            PolicyInformation var16 = (PolicyInformation)var15.next();
            String var17 = var16.getPolicyIdentifier().getIdentifier().toString();
            if (var17.equals("2.5.29.32.0")) {
               var14 = true;
               var12 = var16.getPolicyQualifiers();
            } else {
               if (debug != null) {
                  debug.println("PolicyChecker.processPolicies() processing policy: " + var17);
               }

               Set var18 = var16.getPolicyQualifiers();
               if (!var18.isEmpty() && var5 && var9) {
                  throw new CertPathValidatorException("critical policy qualifiers present in certificate", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_POLICY);
               }

               boolean var19 = processParents(var0, var9, var5, var11, var17, var18, false);
               if (!var19) {
                  processParents(var0, var9, var5, var11, var17, var18, true);
               }
            }
         }

         if (var14 && (var4 > 0 || !var8 && X509CertImpl.isSelfIssued(var7))) {
            if (debug != null) {
               debug.println("PolicyChecker.processPolicies() processing policy: 2.5.29.32.0");
            }

            processParents(var0, var9, var5, var11, "2.5.29.32.0", (Set)var12, true);
         }

         var11.prune(var0);
         if (!var11.getChildren().hasNext()) {
            var11 = null;
         }
      } else if (var13 == null) {
         if (debug != null) {
            debug.println("PolicyChecker.processPolicies() no policies present in cert");
         }

         var11 = null;
      }

      if (var11 != null && !var8) {
         var11 = processPolicyMappings(var7, var0, var3, var11, var9, (Set)var12);
      }

      if (var11 != null && !var1.contains("2.5.29.32.0") && var13 != null) {
         var11 = removeInvalidNodes(var11, var0, var1, var13);
         if (var11 != null && var8) {
            var11 = rewriteLeafNodes(var0, var1, var11);
         }
      }

      if (var8) {
         var2 = mergeExplicitPolicy(var2, var7, var8);
      }

      if (var2 == 0 && var11 == null) {
         throw new CertPathValidatorException("non-null policy tree required and policy tree is null", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_POLICY);
      } else {
         return var11;
      }
   }

   private static PolicyNodeImpl rewriteLeafNodes(int var0, Set<String> var1, PolicyNodeImpl var2) {
      Set var3 = var2.getPolicyNodesValid(var0, "2.5.29.32.0");
      if (var3.isEmpty()) {
         return var2;
      } else {
         PolicyNodeImpl var4 = (PolicyNodeImpl)var3.iterator().next();
         PolicyNodeImpl var5 = (PolicyNodeImpl)var4.getParent();
         var5.deleteChild(var4);
         HashSet var6 = new HashSet(var1);
         Iterator var7 = var2.getPolicyNodes(var0).iterator();

         while(var7.hasNext()) {
            PolicyNodeImpl var8 = (PolicyNodeImpl)var7.next();
            var6.remove(var8.getValidPolicy());
         }

         if (var6.isEmpty()) {
            var2.prune(var0);
            if (!var2.getChildren().hasNext()) {
               var2 = null;
            }
         } else {
            boolean var13 = var4.isCritical();
            Set var14 = var4.getPolicyQualifiers();
            Iterator var9 = var6.iterator();

            while(var9.hasNext()) {
               String var10 = (String)var9.next();
               Set var11 = Collections.singleton(var10);
               new PolicyNodeImpl(var5, var10, var14, var13, var11, false);
            }
         }

         return var2;
      }
   }

   private static boolean processParents(int var0, boolean var1, boolean var2, PolicyNodeImpl var3, String var4, Set<PolicyQualifierInfo> var5, boolean var6) throws CertPathValidatorException {
      boolean var7 = false;
      if (debug != null) {
         debug.println("PolicyChecker.processParents(): matchAny = " + var6);
      }

      Set var8 = var3.getPolicyNodesExpected(var0 - 1, var4, var6);
      Iterator var9 = var8.iterator();

      while(true) {
         label34:
         while(var9.hasNext()) {
            PolicyNodeImpl var10 = (PolicyNodeImpl)var9.next();
            if (debug != null) {
               debug.println("PolicyChecker.processParents() found parent:\n" + var10.asString());
            }

            var7 = true;
            String var11 = var10.getValidPolicy();
            Object var12 = null;
            HashSet var13 = null;
            if (var4.equals("2.5.29.32.0")) {
               Set var14 = var10.getExpectedPolicies();
               Iterator var15 = var14.iterator();

               while(true) {
                  label45:
                  while(true) {
                     if (!var15.hasNext()) {
                        continue label34;
                     }

                     String var16 = (String)var15.next();
                     Iterator var17 = var10.getChildren();

                     while(var17.hasNext()) {
                        PolicyNodeImpl var18 = (PolicyNodeImpl)var17.next();
                        String var19 = var18.getValidPolicy();
                        if (var16.equals(var19)) {
                           if (debug != null) {
                              debug.println(var19 + " in parent's expected policy set already appears in child node");
                           }
                           continue label45;
                        }
                     }

                     HashSet var20 = new HashSet();
                     var20.add(var16);
                     new PolicyNodeImpl(var10, var16, var5, var1, var20, false);
                  }
               }
            } else {
               var13 = new HashSet();
               var13.add(var4);
               new PolicyNodeImpl(var10, var4, var5, var1, var13, false);
            }
         }

         return var7;
      }
   }

   private static PolicyNodeImpl processPolicyMappings(X509CertImpl var0, int var1, int var2, PolicyNodeImpl var3, boolean var4, Set<PolicyQualifierInfo> var5) throws CertPathValidatorException {
      PolicyMappingsExtension var6 = var0.getPolicyMappingsExtension();
      if (var6 == null) {
         return var3;
      } else {
         if (debug != null) {
            debug.println("PolicyChecker.processPolicyMappings() inside policyMapping check");
         }

         List var7 = null;

         try {
            var7 = var6.get("map");
         } catch (IOException var20) {
            if (debug != null) {
               debug.println("PolicyChecker.processPolicyMappings() mapping exception");
               var20.printStackTrace();
            }

            throw new CertPathValidatorException("Exception while checking mapping", var20);
         }

         boolean var8 = false;
         Iterator var9 = var7.iterator();

         label97:
         while(var9.hasNext()) {
            CertificatePolicyMap var10 = (CertificatePolicyMap)var9.next();
            String var11 = var10.getIssuerIdentifier().getIdentifier().toString();
            String var12 = var10.getSubjectIdentifier().getIdentifier().toString();
            if (debug != null) {
               debug.println("PolicyChecker.processPolicyMappings() issuerDomain = " + var11);
               debug.println("PolicyChecker.processPolicyMappings() subjectDomain = " + var12);
            }

            if (var11.equals("2.5.29.32.0")) {
               throw new CertPathValidatorException("encountered an issuerDomainPolicy of ANY_POLICY", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_POLICY);
            }

            if (var12.equals("2.5.29.32.0")) {
               throw new CertPathValidatorException("encountered a subjectDomainPolicy of ANY_POLICY", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_POLICY);
            }

            Set var13 = var3.getPolicyNodesValid(var1, var11);
            PolicyNodeImpl var16;
            if (var13.isEmpty()) {
               if (var2 > 0 || var2 == -1) {
                  Set var21 = var3.getPolicyNodesValid(var1, "2.5.29.32.0");
                  Iterator var22 = var21.iterator();

                  while(var22.hasNext()) {
                     var16 = (PolicyNodeImpl)var22.next();
                     PolicyNodeImpl var17 = (PolicyNodeImpl)var16.getParent();
                     HashSet var18 = new HashSet();
                     var18.add(var12);
                     new PolicyNodeImpl(var17, var11, var5, var4, var18, true);
                  }
               }
            } else {
               Iterator var14 = var13.iterator();

               while(true) {
                  while(true) {
                     if (!var14.hasNext()) {
                        continue label97;
                     }

                     PolicyNodeImpl var15 = (PolicyNodeImpl)var14.next();
                     if (var2 <= 0 && var2 != -1) {
                        if (var2 == 0) {
                           var16 = (PolicyNodeImpl)var15.getParent();
                           if (debug != null) {
                              debug.println("PolicyChecker.processPolicyMappings() before deleting: policy tree = " + var3);
                           }

                           var16.deleteChild(var15);
                           var8 = true;
                           if (debug != null) {
                              debug.println("PolicyChecker.processPolicyMappings() after deleting: policy tree = " + var3);
                           }
                        }
                     } else {
                        var15.addExpectedPolicy(var12);
                     }
                  }
               }
            }
         }

         if (var8) {
            var3.prune(var1);
            if (!var3.getChildren().hasNext()) {
               if (debug != null) {
                  debug.println("setting rootNode to null");
               }

               var3 = null;
            }
         }

         return var3;
      }
   }

   private static PolicyNodeImpl removeInvalidNodes(PolicyNodeImpl var0, int var1, Set<String> var2, CertificatePoliciesExtension var3) throws CertPathValidatorException {
      List var4 = null;

      try {
         var4 = var3.get("policies");
      } catch (IOException var13) {
         throw new CertPathValidatorException("Exception while retrieving policyOIDs", var13);
      }

      boolean var5 = false;
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         PolicyInformation var7 = (PolicyInformation)var6.next();
         String var8 = var7.getPolicyIdentifier().getIdentifier().toString();
         if (debug != null) {
            debug.println("PolicyChecker.processPolicies() processing policy second time: " + var8);
         }

         Set var9 = var0.getPolicyNodesValid(var1, var8);
         Iterator var10 = var9.iterator();

         while(var10.hasNext()) {
            PolicyNodeImpl var11 = (PolicyNodeImpl)var10.next();
            PolicyNodeImpl var12 = (PolicyNodeImpl)var11.getParent();
            if (var12.getValidPolicy().equals("2.5.29.32.0") && !var2.contains(var8) && !var8.equals("2.5.29.32.0")) {
               if (debug != null) {
                  debug.println("PolicyChecker.processPolicies() before deleting: policy tree = " + var0);
               }

               var12.deleteChild(var11);
               var5 = true;
               if (debug != null) {
                  debug.println("PolicyChecker.processPolicies() after deleting: policy tree = " + var0);
               }
            }
         }
      }

      if (var5) {
         var0.prune(var1);
         if (!var0.getChildren().hasNext()) {
            var0 = null;
         }
      }

      return var0;
   }

   PolicyNode getPolicyTree() {
      if (this.rootNode == null) {
         return null;
      } else {
         PolicyNodeImpl var1 = this.rootNode.copyTree();
         var1.setImmutable();
         return var1;
      }
   }
}
