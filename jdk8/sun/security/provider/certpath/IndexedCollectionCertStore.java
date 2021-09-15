package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

public class IndexedCollectionCertStore extends CertStoreSpi {
   private Map<X500Principal, Object> certSubjects;
   private Map<X500Principal, Object> crlIssuers;
   private Set<Certificate> otherCertificates;
   private Set<CRL> otherCRLs;

   public IndexedCollectionCertStore(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof CollectionCertStoreParameters)) {
         throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
      } else {
         Collection var2 = ((CollectionCertStoreParameters)var1).getCollection();
         if (var2 == null) {
            throw new InvalidAlgorithmParameterException("Collection must not be null");
         } else {
            this.buildIndex(var2);
         }
      }
   }

   private void buildIndex(Collection<?> var1) {
      this.certSubjects = new HashMap();
      this.crlIssuers = new HashMap();
      this.otherCertificates = null;
      this.otherCRLs = null;
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (var3 instanceof X509Certificate) {
            this.indexCertificate((X509Certificate)var3);
         } else if (var3 instanceof X509CRL) {
            this.indexCRL((X509CRL)var3);
         } else if (var3 instanceof Certificate) {
            if (this.otherCertificates == null) {
               this.otherCertificates = new HashSet();
            }

            this.otherCertificates.add((Certificate)var3);
         } else if (var3 instanceof CRL) {
            if (this.otherCRLs == null) {
               this.otherCRLs = new HashSet();
            }

            this.otherCRLs.add((CRL)var3);
         }
      }

      if (this.otherCertificates == null) {
         this.otherCertificates = Collections.emptySet();
      }

      if (this.otherCRLs == null) {
         this.otherCRLs = Collections.emptySet();
      }

   }

   private void indexCertificate(X509Certificate var1) {
      X500Principal var2 = var1.getSubjectX500Principal();
      Object var3 = this.certSubjects.put(var2, var1);
      if (var3 != null) {
         if (var3 instanceof X509Certificate) {
            if (var1.equals(var3)) {
               return;
            }

            ArrayList var4 = new ArrayList(2);
            var4.add(var1);
            var4.add((X509Certificate)var3);
            this.certSubjects.put(var2, var4);
         } else {
            List var5 = (List)var3;
            if (!var5.contains(var1)) {
               var5.add(var1);
            }

            this.certSubjects.put(var2, var5);
         }
      }

   }

   private void indexCRL(X509CRL var1) {
      X500Principal var2 = var1.getIssuerX500Principal();
      Object var3 = this.crlIssuers.put(var2, var1);
      if (var3 != null) {
         if (var3 instanceof X509CRL) {
            if (var1.equals(var3)) {
               return;
            }

            ArrayList var4 = new ArrayList(2);
            var4.add(var1);
            var4.add((X509CRL)var3);
            this.crlIssuers.put(var2, var4);
         } else {
            List var5 = (List)var3;
            if (!var5.contains(var1)) {
               var5.add(var1);
            }

            this.crlIssuers.put(var2, var5);
         }
      }

   }

   public Collection<? extends Certificate> engineGetCertificates(CertSelector var1) throws CertStoreException {
      HashSet var10;
      if (var1 == null) {
         var10 = new HashSet();
         this.matchX509Certs(new X509CertSelector(), var10);
         var10.addAll(this.otherCertificates);
         return var10;
      } else if (!(var1 instanceof X509CertSelector)) {
         var10 = new HashSet();
         this.matchX509Certs(var1, var10);
         Iterator var11 = this.otherCertificates.iterator();

         while(var11.hasNext()) {
            Certificate var12 = (Certificate)var11.next();
            if (var1.match(var12)) {
               var10.add(var12);
            }
         }

         return var10;
      } else if (this.certSubjects.isEmpty()) {
         return Collections.emptySet();
      } else {
         X509CertSelector var2 = (X509CertSelector)var1;
         X509Certificate var4 = var2.getCertificate();
         X500Principal var3;
         if (var4 != null) {
            var3 = var4.getSubjectX500Principal();
         } else {
            var3 = var2.getSubject();
         }

         if (var3 != null) {
            Object var13 = this.certSubjects.get(var3);
            if (var13 == null) {
               return Collections.emptySet();
            } else if (var13 instanceof X509Certificate) {
               X509Certificate var14 = (X509Certificate)var13;
               return var2.match(var14) ? Collections.singleton(var14) : Collections.emptySet();
            } else {
               List var6 = (List)var13;
               HashSet var7 = new HashSet(16);
               Iterator var8 = var6.iterator();

               while(var8.hasNext()) {
                  X509Certificate var9 = (X509Certificate)var8.next();
                  if (var2.match(var9)) {
                     var7.add(var9);
                  }
               }

               return var7;
            }
         } else {
            HashSet var5 = new HashSet(16);
            this.matchX509Certs(var2, var5);
            return var5;
         }
      }
   }

   private void matchX509Certs(CertSelector var1, Collection<Certificate> var2) {
      Iterator var3 = this.certSubjects.values().iterator();

      while(true) {
         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof X509Certificate) {
               X509Certificate var8 = (X509Certificate)var4;
               if (var1.match(var8)) {
                  var2.add(var8);
               }
            } else {
               List var5 = (List)var4;
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  X509Certificate var7 = (X509Certificate)var6.next();
                  if (var1.match(var7)) {
                     var2.add(var7);
                  }
               }
            }
         }

         return;
      }
   }

   public Collection<CRL> engineGetCRLs(CRLSelector var1) throws CertStoreException {
      HashSet var11;
      if (var1 == null) {
         var11 = new HashSet();
         this.matchX509CRLs(new X509CRLSelector(), var11);
         var11.addAll(this.otherCRLs);
         return var11;
      } else if (!(var1 instanceof X509CRLSelector)) {
         var11 = new HashSet();
         this.matchX509CRLs(var1, var11);
         Iterator var12 = this.otherCRLs.iterator();

         while(var12.hasNext()) {
            CRL var13 = (CRL)var12.next();
            if (var1.match(var13)) {
               var11.add(var13);
            }
         }

         return var11;
      } else if (this.crlIssuers.isEmpty()) {
         return Collections.emptySet();
      } else {
         X509CRLSelector var2 = (X509CRLSelector)var1;
         Collection var3 = var2.getIssuers();
         HashSet var4;
         if (var3 == null) {
            var4 = new HashSet(16);
            this.matchX509CRLs(var2, var4);
            return var4;
         } else {
            var4 = new HashSet(16);
            Iterator var5 = var3.iterator();

            while(true) {
               while(true) {
                  Object var7;
                  do {
                     if (!var5.hasNext()) {
                        return var4;
                     }

                     X500Principal var6 = (X500Principal)var5.next();
                     var7 = this.crlIssuers.get(var6);
                  } while(var7 == null);

                  if (var7 instanceof X509CRL) {
                     X509CRL var14 = (X509CRL)var7;
                     if (var2.match(var14)) {
                        var4.add(var14);
                     }
                  } else {
                     List var8 = (List)var7;
                     Iterator var9 = var8.iterator();

                     while(var9.hasNext()) {
                        X509CRL var10 = (X509CRL)var9.next();
                        if (var2.match(var10)) {
                           var4.add(var10);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void matchX509CRLs(CRLSelector var1, Collection<CRL> var2) {
      Iterator var3 = this.crlIssuers.values().iterator();

      while(true) {
         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof X509CRL) {
               X509CRL var8 = (X509CRL)var4;
               if (var1.match(var8)) {
                  var2.add(var8);
               }
            } else {
               List var5 = (List)var4;
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  X509CRL var7 = (X509CRL)var6.next();
                  if (var1.match(var7)) {
                     var2.add(var7);
                  }
               }
            }
         }

         return;
      }
   }
}
