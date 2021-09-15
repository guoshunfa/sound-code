package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureAlgorithm extends Algorithm {
   private static Logger log = Logger.getLogger(SignatureAlgorithm.class.getName());
   private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash = new ConcurrentHashMap();
   private final SignatureAlgorithmSpi signatureAlgorithm;
   private final String algorithmURI;

   public SignatureAlgorithm(Document var1, String var2) throws XMLSecurityException {
      super(var1, var2);
      this.algorithmURI = var2;
      this.signatureAlgorithm = getSignatureAlgorithmSpi(var2);
      this.signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
   }

   public SignatureAlgorithm(Document var1, String var2, int var3) throws XMLSecurityException {
      super(var1, var2);
      this.algorithmURI = var2;
      this.signatureAlgorithm = getSignatureAlgorithmSpi(var2);
      this.signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
      this.signatureAlgorithm.engineSetHMACOutputLength(var3);
      ((IntegrityHmac)this.signatureAlgorithm).engineAddContextToElement(this.constructionElement);
   }

   public SignatureAlgorithm(Element var1, String var2) throws XMLSecurityException {
      this(var1, var2, false);
   }

   public SignatureAlgorithm(Element var1, String var2, boolean var3) throws XMLSecurityException {
      super(var1, var2);
      this.algorithmURI = this.getURI();
      Attr var4 = var1.getAttributeNodeNS((String)null, "Id");
      if (var4 != null) {
         var1.setIdAttributeNode(var4, true);
      }

      if (!var3 || !"http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(this.algorithmURI) && !"http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(this.algorithmURI)) {
         this.signatureAlgorithm = getSignatureAlgorithmSpi(this.algorithmURI);
         this.signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
      } else {
         Object[] var5 = new Object[]{this.algorithmURI};
         throw new XMLSecurityException("signature.signatureAlgorithm", var5);
      }
   }

   private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String var0) throws XMLSignatureException {
      Object[] var2;
      try {
         Class var1 = (Class)algorithmHash.get(var0);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Create URI \"" + var0 + "\" class \"" + var1 + "\"");
         }

         return (SignatureAlgorithmSpi)var1.newInstance();
      } catch (IllegalAccessException var3) {
         var2 = new Object[]{var0, var3.getMessage()};
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var2, var3);
      } catch (InstantiationException var4) {
         var2 = new Object[]{var0, var4.getMessage()};
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var2, var4);
      } catch (NullPointerException var5) {
         var2 = new Object[]{var0, var5.getMessage()};
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var2, var5);
      }
   }

   public byte[] sign() throws XMLSignatureException {
      return this.signatureAlgorithm.engineSign();
   }

   public String getJCEAlgorithmString() {
      return this.signatureAlgorithm.engineGetJCEAlgorithmString();
   }

   public String getJCEProviderName() {
      return this.signatureAlgorithm.engineGetJCEProviderName();
   }

   public void update(byte[] var1) throws XMLSignatureException {
      this.signatureAlgorithm.engineUpdate(var1);
   }

   public void update(byte var1) throws XMLSignatureException {
      this.signatureAlgorithm.engineUpdate(var1);
   }

   public void update(byte[] var1, int var2, int var3) throws XMLSignatureException {
      this.signatureAlgorithm.engineUpdate(var1, var2, var3);
   }

   public void initSign(Key var1) throws XMLSignatureException {
      this.signatureAlgorithm.engineInitSign(var1);
   }

   public void initSign(Key var1, SecureRandom var2) throws XMLSignatureException {
      this.signatureAlgorithm.engineInitSign(var1, var2);
   }

   public void initSign(Key var1, AlgorithmParameterSpec var2) throws XMLSignatureException {
      this.signatureAlgorithm.engineInitSign(var1, var2);
   }

   public void setParameter(AlgorithmParameterSpec var1) throws XMLSignatureException {
      this.signatureAlgorithm.engineSetParameter(var1);
   }

   public void initVerify(Key var1) throws XMLSignatureException {
      this.signatureAlgorithm.engineInitVerify(var1);
   }

   public boolean verify(byte[] var1) throws XMLSignatureException {
      return this.signatureAlgorithm.engineVerify(var1);
   }

   public final String getURI() {
      return this.constructionElement.getAttributeNS((String)null, "Algorithm");
   }

   public static void register(String var0, String var1) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
      JavaUtils.checkRegisterPermission();
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Try to register " + var0 + " " + var1);
      }

      Class var2 = (Class)algorithmHash.get(var0);
      if (var2 != null) {
         Object[] var6 = new Object[]{var0, var2};
         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", var6);
      } else {
         try {
            Class var3 = ClassLoaderUtils.loadClass(var1, SignatureAlgorithm.class);
            algorithmHash.put(var0, var3);
         } catch (NullPointerException var5) {
            Object[] var4 = new Object[]{var0, var5.getMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var4, var5);
         }
      }
   }

   public static void register(String var0, Class<? extends SignatureAlgorithmSpi> var1) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
      JavaUtils.checkRegisterPermission();
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Try to register " + var0 + " " + var1);
      }

      Class var2 = (Class)algorithmHash.get(var0);
      if (var2 != null) {
         Object[] var3 = new Object[]{var0, var2};
         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", var3);
      } else {
         algorithmHash.put(var0, var1);
      }
   }

   public static void registerDefaultAlgorithms() {
      algorithmHash.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", SignatureDSA.class);
      algorithmHash.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", SignatureDSA.SHA256.class);
      algorithmHash.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", SignatureBaseRSA.SignatureRSASHA1.class);
      algorithmHash.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", IntegrityHmac.IntegrityHmacSHA1.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", SignatureBaseRSA.SignatureRSAMD5.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", SignatureBaseRSA.SignatureRSARIPEMD160.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", SignatureBaseRSA.SignatureRSASHA256.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", SignatureBaseRSA.SignatureRSASHA384.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", SignatureBaseRSA.SignatureRSASHA512.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", SignatureECDSA.SignatureECDSASHA1.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", SignatureECDSA.SignatureECDSASHA256.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", SignatureECDSA.SignatureECDSASHA384.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", SignatureECDSA.SignatureECDSASHA512.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", IntegrityHmac.IntegrityHmacMD5.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", IntegrityHmac.IntegrityHmacRIPEMD160.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", IntegrityHmac.IntegrityHmacSHA256.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", IntegrityHmac.IntegrityHmacSHA384.class);
      algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", IntegrityHmac.IntegrityHmacSHA512.class);
   }

   public String getBaseNamespace() {
      return "http://www.w3.org/2000/09/xmldsig#";
   }

   public String getBaseLocalName() {
      return "SignatureMethod";
   }
}
