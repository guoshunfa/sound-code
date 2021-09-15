package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import sun.security.jca.GetInstance;

public class CertStore {
   private static final String CERTSTORE_TYPE = "certstore.type";
   private CertStoreSpi storeSpi;
   private Provider provider;
   private String type;
   private CertStoreParameters params;

   protected CertStore(CertStoreSpi var1, Provider var2, String var3, CertStoreParameters var4) {
      this.storeSpi = var1;
      this.provider = var2;
      this.type = var3;
      if (var4 != null) {
         this.params = (CertStoreParameters)var4.clone();
      }

   }

   public final Collection<? extends Certificate> getCertificates(CertSelector var1) throws CertStoreException {
      return this.storeSpi.engineGetCertificates(var1);
   }

   public final Collection<? extends CRL> getCRLs(CRLSelector var1) throws CertStoreException {
      return this.storeSpi.engineGetCRLs(var1);
   }

   public static CertStore getInstance(String var0, CertStoreParameters var1) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
      try {
         GetInstance.Instance var2 = GetInstance.getInstance("CertStore", CertStoreSpi.class, var0, (Object)var1);
         return new CertStore((CertStoreSpi)var2.impl, var2.provider, var0, var1);
      } catch (NoSuchAlgorithmException var3) {
         return handleException(var3);
      }
   }

   private static CertStore handleException(NoSuchAlgorithmException var0) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      Throwable var1 = var0.getCause();
      if (var1 instanceof InvalidAlgorithmParameterException) {
         throw (InvalidAlgorithmParameterException)var1;
      } else {
         throw var0;
      }
   }

   public static CertStore getInstance(String var0, CertStoreParameters var1, String var2) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
      try {
         GetInstance.Instance var3 = GetInstance.getInstance("CertStore", CertStoreSpi.class, var0, var1, (String)var2);
         return new CertStore((CertStoreSpi)var3.impl, var3.provider, var0, var1);
      } catch (NoSuchAlgorithmException var4) {
         return handleException(var4);
      }
   }

   public static CertStore getInstance(String var0, CertStoreParameters var1, Provider var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      try {
         GetInstance.Instance var3 = GetInstance.getInstance("CertStore", CertStoreSpi.class, var0, var1, (Provider)var2);
         return new CertStore((CertStoreSpi)var3.impl, var3.provider, var0, var1);
      } catch (NoSuchAlgorithmException var4) {
         return handleException(var4);
      }
   }

   public final CertStoreParameters getCertStoreParameters() {
      return this.params == null ? null : (CertStoreParameters)this.params.clone();
   }

   public final String getType() {
      return this.type;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public static final String getDefaultType() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("certstore.type");
         }
      });
      if (var0 == null) {
         var0 = "LDAP";
      }

      return var0;
   }
}
