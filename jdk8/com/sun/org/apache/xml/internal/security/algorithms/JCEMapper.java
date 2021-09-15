package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

public class JCEMapper {
   private static Logger log = Logger.getLogger(JCEMapper.class.getName());
   private static Map<String, JCEMapper.Algorithm> algorithmsMap = new ConcurrentHashMap();
   private static String providerName = null;

   public static void register(String var0, JCEMapper.Algorithm var1) {
      JavaUtils.checkRegisterPermission();
      algorithmsMap.put(var0, var1);
   }

   public static void registerDefaultAlgorithms() {
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#md5", new JCEMapper.Algorithm("", "MD5", "MessageDigest"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#ripemd160", new JCEMapper.Algorithm("", "RIPEMD160", "MessageDigest"));
      algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#sha1", new JCEMapper.Algorithm("", "SHA-1", "MessageDigest"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha256", new JCEMapper.Algorithm("", "SHA-256", "MessageDigest"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#sha384", new JCEMapper.Algorithm("", "SHA-384", "MessageDigest"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha512", new JCEMapper.Algorithm("", "SHA-512", "MessageDigest"));
      algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", new JCEMapper.Algorithm("", "SHA1withDSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", new JCEMapper.Algorithm("", "SHA256withDSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", new JCEMapper.Algorithm("", "MD5withRSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", new JCEMapper.Algorithm("", "RIPEMD160withRSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", new JCEMapper.Algorithm("", "SHA1withRSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", new JCEMapper.Algorithm("", "SHA256withRSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", new JCEMapper.Algorithm("", "SHA384withRSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", new JCEMapper.Algorithm("", "SHA512withRSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", new JCEMapper.Algorithm("", "SHA1withECDSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", new JCEMapper.Algorithm("", "SHA256withECDSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", new JCEMapper.Algorithm("", "SHA384withECDSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", new JCEMapper.Algorithm("", "SHA512withECDSA", "Signature"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", new JCEMapper.Algorithm("", "HmacMD5", "Mac"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", new JCEMapper.Algorithm("", "HMACRIPEMD160", "Mac"));
      algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", new JCEMapper.Algorithm("", "HmacSHA1", "Mac"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", new JCEMapper.Algorithm("", "HmacSHA256", "Mac"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", new JCEMapper.Algorithm("", "HmacSHA384", "Mac"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", new JCEMapper.Algorithm("", "HmacSHA512", "Mac"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", new JCEMapper.Algorithm("DESede", "DESede/CBC/ISO10126Padding", "BlockEncryption", 192));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes128-cbc", new JCEMapper.Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 128));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes192-cbc", new JCEMapper.Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 192));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes256-cbc", new JCEMapper.Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 256));
      algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes128-gcm", new JCEMapper.Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 128));
      algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes192-gcm", new JCEMapper.Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 192));
      algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes256-gcm", new JCEMapper.Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 256));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#rsa-1_5", new JCEMapper.Algorithm("RSA", "RSA/ECB/PKCS1Padding", "KeyTransport"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p", new JCEMapper.Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport"));
      algorithmsMap.put("http://www.w3.org/2009/xmlenc11#rsa-oaep", new JCEMapper.Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#dh", new JCEMapper.Algorithm("", "", "KeyAgreement"));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-tripledes", new JCEMapper.Algorithm("DESede", "DESedeWrap", "SymmetricKeyWrap", 192));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes128", new JCEMapper.Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 128));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes192", new JCEMapper.Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 192));
      algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes256", new JCEMapper.Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 256));
   }

   public static String translateURItoJCEID(String var0) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Request for URI " + var0);
      }

      JCEMapper.Algorithm var1 = (JCEMapper.Algorithm)algorithmsMap.get(var0);
      return var1 != null ? var1.jceName : null;
   }

   public static String getAlgorithmClassFromURI(String var0) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Request for URI " + var0);
      }

      JCEMapper.Algorithm var1 = (JCEMapper.Algorithm)algorithmsMap.get(var0);
      return var1 != null ? var1.algorithmClass : null;
   }

   public static int getKeyLengthFromURI(String var0) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Request for URI " + var0);
      }

      JCEMapper.Algorithm var1 = (JCEMapper.Algorithm)algorithmsMap.get(var0);
      return var1 != null ? var1.keyLength : 0;
   }

   public static String getJCEKeyAlgorithmFromURI(String var0) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Request for URI " + var0);
      }

      JCEMapper.Algorithm var1 = (JCEMapper.Algorithm)algorithmsMap.get(var0);
      return var1 != null ? var1.requiredKey : null;
   }

   public static String getProviderId() {
      return providerName;
   }

   public static void setProviderId(String var0) {
      JavaUtils.checkRegisterPermission();
      providerName = var0;
   }

   public static class Algorithm {
      final String requiredKey;
      final String jceName;
      final String algorithmClass;
      final int keyLength;

      public Algorithm(Element var1) {
         this.requiredKey = var1.getAttribute("RequiredKey");
         this.jceName = var1.getAttribute("JCEName");
         this.algorithmClass = var1.getAttribute("AlgorithmClass");
         if (var1.hasAttribute("KeyLength")) {
            this.keyLength = Integer.parseInt(var1.getAttribute("KeyLength"));
         } else {
            this.keyLength = 0;
         }

      }

      public Algorithm(String var1, String var2) {
         this(var1, var2, (String)null, 0);
      }

      public Algorithm(String var1, String var2, String var3) {
         this(var1, var2, var3, 0);
      }

      public Algorithm(String var1, String var2, int var3) {
         this(var1, var2, (String)null, var3);
      }

      public Algorithm(String var1, String var2, String var3, int var4) {
         this.requiredKey = var1;
         this.jceName = var2;
         this.algorithmClass = var3;
         this.keyLength = var4;
      }
   }
}
