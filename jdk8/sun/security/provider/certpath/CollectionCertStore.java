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
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

public class CollectionCertStore extends CertStoreSpi {
   private Collection<?> coll;

   public CollectionCertStore(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof CollectionCertStoreParameters)) {
         throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
      } else {
         this.coll = ((CollectionCertStoreParameters)var1).getCollection();
      }
   }

   public Collection<Certificate> engineGetCertificates(CertSelector var1) throws CertStoreException {
      if (this.coll == null) {
         throw new CertStoreException("Collection is null");
      } else {
         int var2 = 0;

         while(var2 < 10) {
            try {
               HashSet var3 = new HashSet();
               Iterator var4;
               Object var5;
               if (var1 != null) {
                  var4 = this.coll.iterator();

                  while(var4.hasNext()) {
                     var5 = var4.next();
                     if (var5 instanceof Certificate && var1.match((Certificate)var5)) {
                        var3.add((Certificate)var5);
                     }
                  }
               } else {
                  var4 = this.coll.iterator();

                  while(var4.hasNext()) {
                     var5 = var4.next();
                     if (var5 instanceof Certificate) {
                        var3.add((Certificate)var5);
                     }
                  }
               }

               return var3;
            } catch (ConcurrentModificationException var6) {
               ++var2;
            }
         }

         throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
      }
   }

   public Collection<CRL> engineGetCRLs(CRLSelector var1) throws CertStoreException {
      if (this.coll == null) {
         throw new CertStoreException("Collection is null");
      } else {
         int var2 = 0;

         while(var2 < 10) {
            try {
               HashSet var3 = new HashSet();
               Iterator var4;
               Object var5;
               if (var1 != null) {
                  var4 = this.coll.iterator();

                  while(var4.hasNext()) {
                     var5 = var4.next();
                     if (var5 instanceof CRL && var1.match((CRL)var5)) {
                        var3.add((CRL)var5);
                     }
                  }
               } else {
                  var4 = this.coll.iterator();

                  while(var4.hasNext()) {
                     var5 = var4.next();
                     if (var5 instanceof CRL) {
                        var3.add((CRL)var5);
                     }
                  }
               }

               return var3;
            } catch (ConcurrentModificationException var6) {
               ++var2;
            }
         }

         throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
      }
   }
}
