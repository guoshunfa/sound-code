package javax.net.ssl;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;

public class TrustManagerFactory {
   private Provider provider;
   private TrustManagerFactorySpi factorySpi;
   private String algorithm;

   public static final String getDefaultAlgorithm() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("ssl.TrustManagerFactory.algorithm");
         }
      });
      if (var0 == null) {
         var0 = "SunX509";
      }

      return var0;
   }

   protected TrustManagerFactory(TrustManagerFactorySpi var1, Provider var2, String var3) {
      this.factorySpi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public static final TrustManagerFactory getInstance(String var0) throws NoSuchAlgorithmException {
      GetInstance.Instance var1 = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, var0);
      return new TrustManagerFactory((TrustManagerFactorySpi)var1.impl, var1.provider, var0);
   }

   public static final TrustManagerFactory getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, var0, var1);
      return new TrustManagerFactory((TrustManagerFactorySpi)var2.impl, var2.provider, var0);
   }

   public static final TrustManagerFactory getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, var0, var1);
      return new TrustManagerFactory((TrustManagerFactorySpi)var2.impl, var2.provider, var0);
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final void init(KeyStore var1) throws KeyStoreException {
      this.factorySpi.engineInit(var1);
   }

   public final void init(ManagerFactoryParameters var1) throws InvalidAlgorithmParameterException {
      this.factorySpi.engineInit(var1);
   }

   public final TrustManager[] getTrustManagers() {
      return this.factorySpi.engineGetTrustManagers();
   }
}
