package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import sun.security.action.GetBooleanAction;
import sun.security.util.Debug;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

public abstract class Builder {
   private static final Debug debug = Debug.getInstance("certpath");
   private Set<String> matchingPolicies;
   final PKIX.BuilderParams buildParams;
   final X509CertSelector targetCertConstraints;
   static final boolean USE_AIA = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("com.sun.security.enableAIAcaIssuers")));

   Builder(PKIX.BuilderParams var1) {
      this.buildParams = var1;
      this.targetCertConstraints = (X509CertSelector)var1.targetCertConstraints();
   }

   abstract Collection<X509Certificate> getMatchingCerts(State var1, List<CertStore> var2) throws CertStoreException, CertificateException, IOException;

   abstract void verifyCert(X509Certificate var1, State var2, List<X509Certificate> var3) throws GeneralSecurityException;

   abstract boolean isPathCompleted(X509Certificate var1);

   abstract void addCertToPath(X509Certificate var1, LinkedList<X509Certificate> var2);

   abstract void removeFinalCertFromPath(LinkedList<X509Certificate> var1);

   static int distance(GeneralNameInterface var0, GeneralNameInterface var1, int var2) {
      switch(var0.constrains(var1)) {
      case -1:
         if (debug != null) {
            debug.println("Builder.distance(): Names are different types");
         }

         return var2;
      case 0:
         return 0;
      case 1:
      case 2:
         return var1.subtreeDepth() - var0.subtreeDepth();
      case 3:
         if (debug != null) {
            debug.println("Builder.distance(): Names are same type but in different subtrees");
         }

         return var2;
      default:
         return var2;
      }
   }

   static int hops(GeneralNameInterface var0, GeneralNameInterface var1, int var2) {
      int var3 = var0.constrains(var1);
      switch(var3) {
      case -1:
         if (debug != null) {
            debug.println("Builder.hops(): Names are different types");
         }

         return var2;
      case 0:
         return 0;
      case 1:
         return var1.subtreeDepth() - var0.subtreeDepth();
      case 2:
         return var1.subtreeDepth() - var0.subtreeDepth();
      case 3:
         if (var0.getType() != 4) {
            if (debug != null) {
               debug.println("Builder.hops(): hopDistance not implemented for this name type");
            }

            return var2;
         } else {
            X500Name var4 = (X500Name)var0;
            X500Name var5 = (X500Name)var1;
            X500Name var6 = var4.commonAncestor(var5);
            if (var6 == null) {
               if (debug != null) {
                  debug.println("Builder.hops(): Names are in different namespaces");
               }

               return var2;
            }

            int var7 = var6.subtreeDepth();
            int var8 = var4.subtreeDepth();
            int var9 = var5.subtreeDepth();
            return var8 + var9 - 2 * var7;
         }
      default:
         return var2;
      }
   }

   static int targetDistance(NameConstraintsExtension var0, X509Certificate var1, GeneralNameInterface var2) throws IOException {
      if (var0 != null && !var0.verify(var1)) {
         throw new IOException("certificate does not satisfy existing name constraints");
      } else {
         X509CertImpl var3;
         try {
            var3 = X509CertImpl.toImpl(var1);
         } catch (CertificateException var13) {
            throw new IOException("Invalid certificate", var13);
         }

         X500Name var4 = X500Name.asX500Name(var3.getSubjectX500Principal());
         if (var4.equals(var2)) {
            return 0;
         } else {
            SubjectAlternativeNameExtension var5 = var3.getSubjectAlternativeNameExtension();
            if (var5 != null) {
               GeneralNames var6 = var5.get("subject_name");
               if (var6 != null) {
                  int var7 = 0;

                  for(int var8 = var6.size(); var7 < var8; ++var7) {
                     GeneralNameInterface var9 = var6.get(var7).getName();
                     if (var9.equals(var2)) {
                        return 0;
                     }
                  }
               }
            }

            NameConstraintsExtension var14 = var3.getNameConstraintsExtension();
            if (var14 == null) {
               return -1;
            } else {
               if (var0 != null) {
                  var0.merge(var14);
               } else {
                  var0 = (NameConstraintsExtension)var14.clone();
               }

               if (debug != null) {
                  debug.println("Builder.targetDistance() merged constraints: " + String.valueOf((Object)var0));
               }

               GeneralSubtrees var15 = var0.get("permitted_subtrees");
               GeneralSubtrees var16 = var0.get("excluded_subtrees");
               if (var15 != null) {
                  var15.reduce(var16);
               }

               if (debug != null) {
                  debug.println("Builder.targetDistance() reduced constraints: " + var15);
               }

               if (!var0.verify(var2)) {
                  throw new IOException("New certificate not allowed to sign certificate for target");
               } else if (var15 == null) {
                  return -1;
               } else {
                  int var17 = 0;

                  for(int var10 = var15.size(); var17 < var10; ++var17) {
                     GeneralNameInterface var11 = var15.get(var17).getName().getName();
                     int var12 = distance(var11, var2, -1);
                     if (var12 >= 0) {
                        return var12 + 1;
                     }
                  }

                  return -1;
               }
            }
         }
      }
   }

   Set<String> getMatchingPolicies() {
      if (this.matchingPolicies != null) {
         Set var1 = this.buildParams.initialPolicies();
         if (!var1.isEmpty() && !var1.contains("2.5.29.32.0") && this.buildParams.policyMappingInhibited()) {
            this.matchingPolicies = new HashSet(var1);
            this.matchingPolicies.add("2.5.29.32.0");
         } else {
            this.matchingPolicies = Collections.emptySet();
         }
      }

      return this.matchingPolicies;
   }

   boolean addMatchingCerts(X509CertSelector var1, Collection<CertStore> var2, Collection<X509Certificate> var3, boolean var4) {
      X509Certificate var5 = var1.getCertificate();
      if (var5 != null) {
         if (var1.match(var5) && !X509CertImpl.isSelfSigned(var5, this.buildParams.sigProvider())) {
            if (debug != null) {
               debug.println("Builder.addMatchingCerts: adding target cert\n  SN: " + Debug.toHexString(var5.getSerialNumber()) + "\n  Subject: " + var5.getSubjectX500Principal() + "\n  Issuer: " + var5.getIssuerX500Principal());
            }

            return var3.add(var5);
         } else {
            return false;
         }
      } else {
         boolean var6 = false;
         Iterator var7 = var2.iterator();

         while(var7.hasNext()) {
            CertStore var8 = (CertStore)var7.next();

            try {
               Collection var9 = var8.getCertificates(var1);
               Iterator var10 = var9.iterator();

               while(var10.hasNext()) {
                  Certificate var11 = (Certificate)var10.next();
                  if (!X509CertImpl.isSelfSigned((X509Certificate)var11, this.buildParams.sigProvider()) && var3.add((X509Certificate)var11)) {
                     var6 = true;
                  }
               }

               if (!var4 && var6) {
                  return true;
               }
            } catch (CertStoreException var12) {
               if (debug != null) {
                  debug.println("Builder.addMatchingCerts, non-fatal exception retrieving certs: " + var12);
                  var12.printStackTrace();
               }
            }
         }

         return var6;
      }
   }
}
