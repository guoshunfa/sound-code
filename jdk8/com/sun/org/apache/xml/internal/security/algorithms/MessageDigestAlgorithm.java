package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import org.w3c.dom.Document;

public class MessageDigestAlgorithm extends Algorithm {
   public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#md5";
   public static final String ALGO_ID_DIGEST_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
   public static final String ALGO_ID_DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
   public static final String ALGO_ID_DIGEST_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
   public static final String ALGO_ID_DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
   public static final String ALGO_ID_DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
   private final MessageDigest algorithm;

   private MessageDigestAlgorithm(Document var1, String var2) throws XMLSignatureException {
      super(var1, var2);
      this.algorithm = getDigestInstance(var2);
   }

   public static MessageDigestAlgorithm getInstance(Document var0, String var1) throws XMLSignatureException {
      return new MessageDigestAlgorithm(var0, var1);
   }

   private static MessageDigest getDigestInstance(String var0) throws XMLSignatureException {
      String var1 = JCEMapper.translateURItoJCEID(var0);
      if (var1 == null) {
         Object[] var8 = new Object[]{var0};
         throw new XMLSignatureException("algorithms.NoSuchMap", var8);
      } else {
         String var3 = JCEMapper.getProviderId();

         Object[] var5;
         try {
            MessageDigest var2;
            if (var3 == null) {
               var2 = MessageDigest.getInstance(var1);
            } else {
               var2 = MessageDigest.getInstance(var1, var3);
            }

            return var2;
         } catch (NoSuchAlgorithmException var6) {
            var5 = new Object[]{var1, var6.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var5);
         } catch (NoSuchProviderException var7) {
            var5 = new Object[]{var1, var7.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var5);
         }
      }
   }

   public MessageDigest getAlgorithm() {
      return this.algorithm;
   }

   public static boolean isEqual(byte[] var0, byte[] var1) {
      return MessageDigest.isEqual(var0, var1);
   }

   public byte[] digest() {
      return this.algorithm.digest();
   }

   public byte[] digest(byte[] var1) {
      return this.algorithm.digest(var1);
   }

   public int digest(byte[] var1, int var2, int var3) throws DigestException {
      return this.algorithm.digest(var1, var2, var3);
   }

   public String getJCEAlgorithmString() {
      return this.algorithm.getAlgorithm();
   }

   public Provider getJCEProvider() {
      return this.algorithm.getProvider();
   }

   public int getDigestLength() {
      return this.algorithm.getDigestLength();
   }

   public void reset() {
      this.algorithm.reset();
   }

   public void update(byte[] var1) {
      this.algorithm.update(var1);
   }

   public void update(byte var1) {
      this.algorithm.update(var1);
   }

   public void update(byte[] var1, int var2, int var3) {
      this.algorithm.update(var1, var2, var3);
   }

   public String getBaseNamespace() {
      return "http://www.w3.org/2000/09/xmldsig#";
   }

   public String getBaseLocalName() {
      return "DigestMethod";
   }
}
