package java.security.cert;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;

public class CertificateFactory {
   private String type;
   private Provider provider;
   private CertificateFactorySpi certFacSpi;

   protected CertificateFactory(CertificateFactorySpi var1, Provider var2, String var3) {
      this.certFacSpi = var1;
      this.provider = var2;
      this.type = var3;
   }

   public static final CertificateFactory getInstance(String var0) throws CertificateException {
      try {
         GetInstance.Instance var1 = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, var0);
         return new CertificateFactory((CertificateFactorySpi)var1.impl, var1.provider, var0);
      } catch (NoSuchAlgorithmException var2) {
         throw new CertificateException(var0 + " not found", var2);
      }
   }

   public static final CertificateFactory getInstance(String var0, String var1) throws CertificateException, NoSuchProviderException {
      try {
         GetInstance.Instance var2 = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, var0, var1);
         return new CertificateFactory((CertificateFactorySpi)var2.impl, var2.provider, var0);
      } catch (NoSuchAlgorithmException var3) {
         throw new CertificateException(var0 + " not found", var3);
      }
   }

   public static final CertificateFactory getInstance(String var0, Provider var1) throws CertificateException {
      try {
         GetInstance.Instance var2 = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, var0, var1);
         return new CertificateFactory((CertificateFactorySpi)var2.impl, var2.provider, var0);
      } catch (NoSuchAlgorithmException var3) {
         throw new CertificateException(var0 + " not found", var3);
      }
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final String getType() {
      return this.type;
   }

   public final Certificate generateCertificate(InputStream var1) throws CertificateException {
      return this.certFacSpi.engineGenerateCertificate(var1);
   }

   public final Iterator<String> getCertPathEncodings() {
      return this.certFacSpi.engineGetCertPathEncodings();
   }

   public final CertPath generateCertPath(InputStream var1) throws CertificateException {
      return this.certFacSpi.engineGenerateCertPath(var1);
   }

   public final CertPath generateCertPath(InputStream var1, String var2) throws CertificateException {
      return this.certFacSpi.engineGenerateCertPath(var1, var2);
   }

   public final CertPath generateCertPath(List<? extends Certificate> var1) throws CertificateException {
      return this.certFacSpi.engineGenerateCertPath(var1);
   }

   public final Collection<? extends Certificate> generateCertificates(InputStream var1) throws CertificateException {
      return this.certFacSpi.engineGenerateCertificates(var1);
   }

   public final CRL generateCRL(InputStream var1) throws CRLException {
      return this.certFacSpi.engineGenerateCRL(var1);
   }

   public final Collection<? extends CRL> generateCRLs(InputStream var1) throws CRLException {
      return this.certFacSpi.engineGenerateCRLs(var1);
   }
}
