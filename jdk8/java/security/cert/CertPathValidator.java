package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;

public class CertPathValidator {
   private static final String CPV_TYPE = "certpathvalidator.type";
   private final CertPathValidatorSpi validatorSpi;
   private final Provider provider;
   private final String algorithm;

   protected CertPathValidator(CertPathValidatorSpi var1, Provider var2, String var3) {
      this.validatorSpi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   public static CertPathValidator getInstance(String var0) throws NoSuchAlgorithmException {
      GetInstance.Instance var1 = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, var0);
      return new CertPathValidator((CertPathValidatorSpi)var1.impl, var1.provider, var0);
   }

   public static CertPathValidator getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, var0, var1);
      return new CertPathValidator((CertPathValidatorSpi)var2.impl, var2.provider, var0);
   }

   public static CertPathValidator getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, var0, var1);
      return new CertPathValidator((CertPathValidatorSpi)var2.impl, var2.provider, var0);
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public final CertPathValidatorResult validate(CertPath var1, CertPathParameters var2) throws CertPathValidatorException, InvalidAlgorithmParameterException {
      return this.validatorSpi.engineValidate(var1, var2);
   }

   public static final String getDefaultType() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("certpathvalidator.type");
         }
      });
      return var0 == null ? "PKIX" : var0;
   }

   public final CertPathChecker getRevocationChecker() {
      return this.validatorSpi.engineGetRevocationChecker();
   }
}
