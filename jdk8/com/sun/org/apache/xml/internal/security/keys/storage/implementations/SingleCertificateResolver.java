package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleCertificateResolver extends StorageResolverSpi {
   private X509Certificate certificate = null;

   public SingleCertificateResolver(X509Certificate var1) {
      this.certificate = var1;
   }

   public Iterator<Certificate> getIterator() {
      return new SingleCertificateResolver.InternalIterator(this.certificate);
   }

   static class InternalIterator implements Iterator<Certificate> {
      boolean alreadyReturned = false;
      X509Certificate certificate = null;

      public InternalIterator(X509Certificate var1) {
         this.certificate = var1;
      }

      public boolean hasNext() {
         return !this.alreadyReturned;
      }

      public Certificate next() {
         if (this.alreadyReturned) {
            throw new NoSuchElementException();
         } else {
            this.alreadyReturned = true;
            return this.certificate;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove keys from KeyStore");
      }
   }
}
