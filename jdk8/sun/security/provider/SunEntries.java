package sun.security.provider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Map;
import sun.security.action.GetPropertyAction;

final class SunEntries {
   private static final boolean useLegacyDSA = Boolean.parseBoolean(GetPropertyAction.privilegedGetProperty("jdk.security.legacyDSAKeyPairGenerator"));
   private static final String PROP_EGD = "java.security.egd";
   private static final String PROP_RNDSOURCE = "securerandom.source";
   static final String URL_DEV_RANDOM = "file:/dev/random";
   static final String URL_DEV_URANDOM = "file:/dev/urandom";
   private static final String seedSource = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
      public String run() {
         String var1 = System.getProperty("java.security.egd", "");
         if (var1.length() != 0) {
            return var1;
         } else {
            var1 = Security.getProperty("securerandom.source");
            return var1 == null ? "" : var1;
         }
      }
   });

   private SunEntries() {
   }

   static void putEntries(Map<Object, Object> var0) {
      boolean var1 = NativePRNG.isAvailable();
      boolean var2 = seedSource.equals("file:/dev/urandom") || seedSource.equals("file:/dev/random");
      if (var1 && var2) {
         var0.put("SecureRandom.NativePRNG", "sun.security.provider.NativePRNG");
      }

      var0.put("SecureRandom.SHA1PRNG", "sun.security.provider.SecureRandom");
      if (var1 && !var2) {
         var0.put("SecureRandom.NativePRNG", "sun.security.provider.NativePRNG");
      }

      if (NativePRNG.Blocking.isAvailable()) {
         var0.put("SecureRandom.NativePRNGBlocking", "sun.security.provider.NativePRNG$Blocking");
      }

      if (NativePRNG.NonBlocking.isAvailable()) {
         var0.put("SecureRandom.NativePRNGNonBlocking", "sun.security.provider.NativePRNG$NonBlocking");
      }

      var0.put("Signature.SHA1withDSA", "sun.security.provider.DSA$SHA1withDSA");
      var0.put("Signature.NONEwithDSA", "sun.security.provider.DSA$RawDSA");
      var0.put("Alg.Alias.Signature.RawDSA", "NONEwithDSA");
      var0.put("Signature.SHA224withDSA", "sun.security.provider.DSA$SHA224withDSA");
      var0.put("Signature.SHA256withDSA", "sun.security.provider.DSA$SHA256withDSA");
      String var3 = "java.security.interfaces.DSAPublicKey|java.security.interfaces.DSAPrivateKey";
      var0.put("Signature.SHA1withDSA SupportedKeyClasses", var3);
      var0.put("Signature.NONEwithDSA SupportedKeyClasses", var3);
      var0.put("Signature.SHA224withDSA SupportedKeyClasses", var3);
      var0.put("Signature.SHA256withDSA SupportedKeyClasses", var3);
      var0.put("Alg.Alias.Signature.DSA", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.DSS", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.SHA/DSA", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.SHA-1/DSA", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.SHA1/DSA", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.SHAwithDSA", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.DSAWithSHA1", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.10040.4.3", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.1.2.840.10040.4.3", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.1.3.14.3.2.13", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.1.3.14.3.2.27", "SHA1withDSA");
      var0.put("Alg.Alias.Signature.OID.2.16.840.1.101.3.4.3.1", "SHA224withDSA");
      var0.put("Alg.Alias.Signature.2.16.840.1.101.3.4.3.1", "SHA224withDSA");
      var0.put("Alg.Alias.Signature.OID.2.16.840.1.101.3.4.3.2", "SHA256withDSA");
      var0.put("Alg.Alias.Signature.2.16.840.1.101.3.4.3.2", "SHA256withDSA");
      String var4 = "sun.security.provider.DSAKeyPairGenerator$";
      var4 = var4 + (useLegacyDSA ? "Legacy" : "Current");
      var0.put("KeyPairGenerator.DSA", var4);
      var0.put("Alg.Alias.KeyPairGenerator.OID.1.2.840.10040.4.1", "DSA");
      var0.put("Alg.Alias.KeyPairGenerator.1.2.840.10040.4.1", "DSA");
      var0.put("Alg.Alias.KeyPairGenerator.1.3.14.3.2.12", "DSA");
      var0.put("MessageDigest.MD2", "sun.security.provider.MD2");
      var0.put("MessageDigest.MD5", "sun.security.provider.MD5");
      var0.put("MessageDigest.SHA", "sun.security.provider.SHA");
      var0.put("Alg.Alias.MessageDigest.SHA-1", "SHA");
      var0.put("Alg.Alias.MessageDigest.SHA1", "SHA");
      var0.put("Alg.Alias.MessageDigest.1.3.14.3.2.26", "SHA");
      var0.put("Alg.Alias.MessageDigest.OID.1.3.14.3.2.26", "SHA");
      var0.put("MessageDigest.SHA-224", "sun.security.provider.SHA2$SHA224");
      var0.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.4", "SHA-224");
      var0.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.4", "SHA-224");
      var0.put("MessageDigest.SHA-256", "sun.security.provider.SHA2$SHA256");
      var0.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.1", "SHA-256");
      var0.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.1", "SHA-256");
      var0.put("MessageDigest.SHA-384", "sun.security.provider.SHA5$SHA384");
      var0.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.2", "SHA-384");
      var0.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.2", "SHA-384");
      var0.put("MessageDigest.SHA-512", "sun.security.provider.SHA5$SHA512");
      var0.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.3", "SHA-512");
      var0.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.3", "SHA-512");
      var0.put("AlgorithmParameterGenerator.DSA", "sun.security.provider.DSAParameterGenerator");
      var0.put("AlgorithmParameters.DSA", "sun.security.provider.DSAParameters");
      var0.put("Alg.Alias.AlgorithmParameters.OID.1.2.840.10040.4.1", "DSA");
      var0.put("Alg.Alias.AlgorithmParameters.1.2.840.10040.4.1", "DSA");
      var0.put("Alg.Alias.AlgorithmParameters.1.3.14.3.2.12", "DSA");
      var0.put("KeyFactory.DSA", "sun.security.provider.DSAKeyFactory");
      var0.put("Alg.Alias.KeyFactory.OID.1.2.840.10040.4.1", "DSA");
      var0.put("Alg.Alias.KeyFactory.1.2.840.10040.4.1", "DSA");
      var0.put("Alg.Alias.KeyFactory.1.3.14.3.2.12", "DSA");
      var0.put("CertificateFactory.X.509", "sun.security.provider.X509Factory");
      var0.put("Alg.Alias.CertificateFactory.X509", "X.509");
      var0.put("KeyStore.JKS", "sun.security.provider.JavaKeyStore$DualFormatJKS");
      var0.put("KeyStore.CaseExactJKS", "sun.security.provider.JavaKeyStore$CaseExactJKS");
      var0.put("KeyStore.DKS", "sun.security.provider.DomainKeyStore$DKS");
      var0.put("Policy.JavaPolicy", "sun.security.provider.PolicySpiFile");
      var0.put("Configuration.JavaLoginConfig", "sun.security.provider.ConfigFile$Spi");
      var0.put("CertPathBuilder.PKIX", "sun.security.provider.certpath.SunCertPathBuilder");
      var0.put("CertPathBuilder.PKIX ValidationAlgorithm", "RFC3280");
      var0.put("CertPathValidator.PKIX", "sun.security.provider.certpath.PKIXCertPathValidator");
      var0.put("CertPathValidator.PKIX ValidationAlgorithm", "RFC3280");
      var0.put("CertStore.LDAP", "sun.security.provider.certpath.ldap.LDAPCertStore");
      var0.put("CertStore.LDAP LDAPSchema", "RFC2587");
      var0.put("CertStore.Collection", "sun.security.provider.certpath.CollectionCertStore");
      var0.put("CertStore.com.sun.security.IndexedCollection", "sun.security.provider.certpath.IndexedCollectionCertStore");
      var0.put("Signature.NONEwithDSA KeySize", "1024");
      var0.put("Signature.SHA1withDSA KeySize", "1024");
      var0.put("Signature.SHA224withDSA KeySize", "2048");
      var0.put("Signature.SHA256withDSA KeySize", "2048");
      var0.put("KeyPairGenerator.DSA KeySize", "2048");
      var0.put("AlgorithmParameterGenerator.DSA KeySize", "2048");
      var0.put("Signature.SHA1withDSA ImplementedIn", "Software");
      var0.put("KeyPairGenerator.DSA ImplementedIn", "Software");
      var0.put("MessageDigest.MD5 ImplementedIn", "Software");
      var0.put("MessageDigest.SHA ImplementedIn", "Software");
      var0.put("AlgorithmParameterGenerator.DSA ImplementedIn", "Software");
      var0.put("AlgorithmParameters.DSA ImplementedIn", "Software");
      var0.put("KeyFactory.DSA ImplementedIn", "Software");
      var0.put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
      var0.put("CertificateFactory.X.509 ImplementedIn", "Software");
      var0.put("KeyStore.JKS ImplementedIn", "Software");
      var0.put("CertPathValidator.PKIX ImplementedIn", "Software");
      var0.put("CertPathBuilder.PKIX ImplementedIn", "Software");
      var0.put("CertStore.LDAP ImplementedIn", "Software");
      var0.put("CertStore.Collection ImplementedIn", "Software");
      var0.put("CertStore.com.sun.security.IndexedCollection ImplementedIn", "Software");
   }

   static String getSeedSource() {
      return seedSource;
   }

   static File getDeviceFile(URL var0) throws IOException {
      try {
         URI var1 = var0.toURI();
         if (var1.isOpaque()) {
            URI var2 = (new File(System.getProperty("user.dir"))).toURI();
            String var3 = var2.toString() + var1.toString().substring(5);
            return new File(URI.create(var3));
         } else {
            return new File(var1);
         }
      } catch (URISyntaxException var4) {
         return new File(var0.getPath());
      }
   }
}
