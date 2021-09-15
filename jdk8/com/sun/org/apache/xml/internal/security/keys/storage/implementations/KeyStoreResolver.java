package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class KeyStoreResolver extends StorageResolverSpi {
   private KeyStore keyStore = null;

   public KeyStoreResolver(KeyStore var1) throws StorageResolverException {
      this.keyStore = var1;

      try {
         var1.aliases();
      } catch (KeyStoreException var3) {
         throw new StorageResolverException("generic.EmptyMessage", var3);
      }
   }

   public Iterator<Certificate> getIterator() {
      return new KeyStoreResolver.KeyStoreIterator(this.keyStore);
   }

   static class KeyStoreIterator implements Iterator<Certificate> {
      KeyStore keyStore = null;
      Enumeration<String> aliases = null;
      Certificate nextCert = null;

      public KeyStoreIterator(KeyStore var1) {
         try {
            this.keyStore = var1;
            this.aliases = this.keyStore.aliases();
         } catch (KeyStoreException var3) {
            this.aliases = new Enumeration<String>() {
               public boolean hasMoreElements() {
                  return false;
               }

               public String nextElement() {
                  return null;
               }
            };
         }

      }

      public boolean hasNext() {
         if (this.nextCert == null) {
            this.nextCert = this.findNextCert();
         }

         return this.nextCert != null;
      }

      public Certificate next() {
         if (this.nextCert == null) {
            this.nextCert = this.findNextCert();
            if (this.nextCert == null) {
               throw new NoSuchElementException();
            }
         }

         Certificate var1 = this.nextCert;
         this.nextCert = null;
         return var1;
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove keys from KeyStore");
      }

      private Certificate findNextCert() {
         while(true) {
            if (this.aliases.hasMoreElements()) {
               String var1 = (String)this.aliases.nextElement();

               try {
                  Certificate var2 = this.keyStore.getCertificate(var1);
                  if (var2 == null) {
                     continue;
                  }

                  return var2;
               } catch (KeyStoreException var3) {
                  return null;
               }
            }

            return null;
         }
      }
   }
}
