package sun.security.rsa;

import java.util.Map;

public final class SunRsaSignEntries {
   private SunRsaSignEntries() {
   }

   public static void putEntries(Map<Object, Object> var0) {
      var0.put("KeyFactory.RSA", "sun.security.rsa.RSAKeyFactory");
      var0.put("KeyPairGenerator.RSA", "sun.security.rsa.RSAKeyPairGenerator");
      var0.put("Signature.MD2withRSA", "sun.security.rsa.RSASignature$MD2withRSA");
      var0.put("Signature.MD5withRSA", "sun.security.rsa.RSASignature$MD5withRSA");
      var0.put("Signature.SHA1withRSA", "sun.security.rsa.RSASignature$SHA1withRSA");
      var0.put("Signature.SHA224withRSA", "sun.security.rsa.RSASignature$SHA224withRSA");
      var0.put("Signature.SHA256withRSA", "sun.security.rsa.RSASignature$SHA256withRSA");
      var0.put("Signature.SHA384withRSA", "sun.security.rsa.RSASignature$SHA384withRSA");
      var0.put("Signature.SHA512withRSA", "sun.security.rsa.RSASignature$SHA512withRSA");
      String var1 = "java.security.interfaces.RSAPublicKey|java.security.interfaces.RSAPrivateKey";
      var0.put("Signature.MD2withRSA SupportedKeyClasses", var1);
      var0.put("Signature.MD5withRSA SupportedKeyClasses", var1);
      var0.put("Signature.SHA1withRSA SupportedKeyClasses", var1);
      var0.put("Signature.SHA224withRSA SupportedKeyClasses", var1);
      var0.put("Signature.SHA256withRSA SupportedKeyClasses", var1);
      var0.put("Signature.SHA384withRSA SupportedKeyClasses", var1);
      var0.put("Signature.SHA512withRSA SupportedKeyClasses", var1);
      var0.put("Alg.Alias.KeyFactory.1.2.840.113549.1.1", "RSA");
      var0.put("Alg.Alias.KeyFactory.OID.1.2.840.113549.1.1", "RSA");
      var0.put("Alg.Alias.KeyPairGenerator.1.2.840.113549.1.1", "RSA");
      var0.put("Alg.Alias.KeyPairGenerator.OID.1.2.840.113549.1.1", "RSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.2", "MD2withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.2", "MD2withRSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.4", "MD5withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.4", "MD5withRSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.5", "SHA1withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.5", "SHA1withRSA");
      var0.put("Alg.Alias.Signature.1.3.14.3.2.29", "SHA1withRSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.14", "SHA224withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.14", "SHA224withRSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.11", "SHA256withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.11", "SHA256withRSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.12", "SHA384withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.12", "SHA384withRSA");
      var0.put("Alg.Alias.Signature.1.2.840.113549.1.1.13", "SHA512withRSA");
      var0.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.13", "SHA512withRSA");
   }
}
