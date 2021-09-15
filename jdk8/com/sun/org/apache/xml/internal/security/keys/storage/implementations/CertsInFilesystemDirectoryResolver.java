package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CertsInFilesystemDirectoryResolver extends StorageResolverSpi {
   private static Logger log = Logger.getLogger(CertsInFilesystemDirectoryResolver.class.getName());
   private String merlinsCertificatesDir = null;
   private List<X509Certificate> certs = new ArrayList();

   public CertsInFilesystemDirectoryResolver(String var1) throws StorageResolverException {
      this.merlinsCertificatesDir = var1;
      this.readCertsFromHarddrive();
   }

   private void readCertsFromHarddrive() throws StorageResolverException {
      File var1 = new File(this.merlinsCertificatesDir);
      ArrayList var2 = new ArrayList();
      String[] var3 = var1.list();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         String var5 = var3[var4];
         if (var5.endsWith(".crt")) {
            var2.add(var3[var4]);
         }
      }

      CertificateFactory var33 = null;

      try {
         var33 = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var26) {
         throw new StorageResolverException("empty", var26);
      }

      if (var33 == null) {
         throw new StorageResolverException("empty");
      } else {
         for(int var34 = 0; var34 < var2.size(); ++var34) {
            String var6 = var1.getAbsolutePath() + File.separator + (String)var2.get(var34);
            File var7 = new File(var6);
            boolean var8 = false;
            String var9 = null;
            FileInputStream var10 = null;

            try {
               var10 = new FileInputStream(var7);
               X509Certificate var11 = (X509Certificate)var33.generateCertificate(var10);
               var11.checkValidity();
               this.certs.add(var11);
               var9 = var11.getSubjectX500Principal().getName();
               var8 = true;
            } catch (FileNotFoundException var28) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)("Could not add certificate from file " + var6), (Throwable)var28);
               }
            } catch (CertificateNotYetValidException var29) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)("Could not add certificate from file " + var6), (Throwable)var29);
               }
            } catch (CertificateExpiredException var30) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)("Could not add certificate from file " + var6), (Throwable)var30);
               }
            } catch (CertificateException var31) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)("Could not add certificate from file " + var6), (Throwable)var31);
               }
            } finally {
               try {
                  if (var10 != null) {
                     var10.close();
                  }
               } catch (IOException var27) {
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, (String)("Could not add certificate from file " + var6), (Throwable)var27);
                  }
               }

            }

            if (var8 && log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Added certificate: " + var9);
            }
         }

      }
   }

   public Iterator<Certificate> getIterator() {
      return new CertsInFilesystemDirectoryResolver.FilesystemIterator(this.certs);
   }

   public static void main(String[] var0) throws Exception {
      CertsInFilesystemDirectoryResolver var1 = new CertsInFilesystemDirectoryResolver("data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs");
      Iterator var2 = var1.getIterator();

      while(var2.hasNext()) {
         X509Certificate var3 = (X509Certificate)var2.next();
         byte[] var4 = XMLX509SKI.getSKIBytesFromCert(var3);
         System.out.println();
         System.out.println("Base64(SKI())=                 \"" + Base64.encode(var4) + "\"");
         System.out.println("cert.getSerialNumber()=        \"" + var3.getSerialNumber().toString() + "\"");
         System.out.println("cert.getSubjectX500Principal().getName()= \"" + var3.getSubjectX500Principal().getName() + "\"");
         System.out.println("cert.getIssuerX500Principal().getName()=  \"" + var3.getIssuerX500Principal().getName() + "\"");
      }

   }

   private static class FilesystemIterator implements Iterator<Certificate> {
      List<X509Certificate> certs = null;
      int i;

      public FilesystemIterator(List<X509Certificate> var1) {
         this.certs = var1;
         this.i = 0;
      }

      public boolean hasNext() {
         return this.i < this.certs.size();
      }

      public Certificate next() {
         return (Certificate)this.certs.get(this.i++);
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove keys from KeyStore");
      }
   }
}
