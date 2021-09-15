package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;

public class CertPathBuilder {
   private static final String CPB_TYPE = "certpathbuilder.type";
   private final CertPathBuilderSpi builderSpi;
   private final Provider provider;
   private final String algorithm;

   protected CertPathBuilder(CertPathBuilderSpi var1, Provider var2, String var3) {
      this.builderSpi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   public static CertPathBuilder getInstance(String var0) throws NoSuchAlgorithmException {
      GetInstance.Instance var1 = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, var0);
      return new CertPathBuilder((CertPathBuilderSpi)var1.impl, var1.provider, var0);
   }

   public static CertPathBuilder getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, var0, var1);
      return new CertPathBuilder((CertPathBuilderSpi)var2.impl, var2.provider, var0);
   }

   public static CertPathBuilder getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, var0, var1);
      return new CertPathBuilder((CertPathBuilderSpi)var2.impl, var2.provider, var0);
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public final CertPathBuilderResult build(CertPathParameters var1) throws CertPathBuilderException, InvalidAlgorithmParameterException {
      return this.builderSpi.engineBuild(var1);
   }

   public static final String getDefaultType() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("certpathbuilder.type");
         }
      });
      return var0 == null ? "PKIX" : var0;
   }

   public final CertPathChecker getRevocationChecker() {
      return this.builderSpi.engineGetRevocationChecker();
   }
}
