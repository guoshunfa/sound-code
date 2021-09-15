package com.sun.org.apache.xml.internal.security.keys.storage;

import com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.implementations.SingleCertificateResolver;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageResolver {
   private static Logger log = Logger.getLogger(StorageResolver.class.getName());
   private List<StorageResolverSpi> storageResolvers = null;

   public StorageResolver() {
   }

   public StorageResolver(StorageResolverSpi var1) {
      this.add(var1);
   }

   public void add(StorageResolverSpi var1) {
      if (this.storageResolvers == null) {
         this.storageResolvers = new ArrayList();
      }

      this.storageResolvers.add(var1);
   }

   public StorageResolver(KeyStore var1) {
      this.add(var1);
   }

   public void add(KeyStore var1) {
      try {
         this.add((StorageResolverSpi)(new KeyStoreResolver(var1)));
      } catch (StorageResolverException var3) {
         log.log(Level.SEVERE, (String)"Could not add KeyStore because of: ", (Throwable)var3);
      }

   }

   public StorageResolver(X509Certificate var1) {
      this.add(var1);
   }

   public void add(X509Certificate var1) {
      this.add((StorageResolverSpi)(new SingleCertificateResolver(var1)));
   }

   public Iterator<Certificate> getIterator() {
      return new StorageResolver.StorageResolverIterator(this.storageResolvers.iterator());
   }

   static class StorageResolverIterator implements Iterator<Certificate> {
      Iterator<StorageResolverSpi> resolvers = null;
      Iterator<Certificate> currentResolver = null;

      public StorageResolverIterator(Iterator<StorageResolverSpi> var1) {
         this.resolvers = var1;
         this.currentResolver = this.findNextResolver();
      }

      public boolean hasNext() {
         if (this.currentResolver == null) {
            return false;
         } else if (this.currentResolver.hasNext()) {
            return true;
         } else {
            this.currentResolver = this.findNextResolver();
            return this.currentResolver != null;
         }
      }

      public Certificate next() {
         if (this.hasNext()) {
            return (Certificate)this.currentResolver.next();
         } else {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove keys from KeyStore");
      }

      private Iterator<Certificate> findNextResolver() {
         while(true) {
            if (this.resolvers.hasNext()) {
               StorageResolverSpi var1 = (StorageResolverSpi)this.resolvers.next();
               Iterator var2 = var1.getIterator();
               if (!var2.hasNext()) {
                  continue;
               }

               return var2;
            }

            return null;
         }
      }
   }
}
