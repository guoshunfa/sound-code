package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignatureDSA extends SignatureAlgorithmSpi {
   private static Logger log = Logger.getLogger(SignatureDSA.class.getName());
   private Signature signatureAlgorithm = null;
   private int size;

   protected String engineGetURI() {
      return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
   }

   public SignatureDSA() throws XMLSignatureException {
      String var1 = JCEMapper.translateURItoJCEID(this.engineGetURI());
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Created SignatureDSA using " + var1);
      }

      String var2 = JCEMapper.getProviderId();

      Object[] var4;
      try {
         if (var2 == null) {
            this.signatureAlgorithm = Signature.getInstance(var1);
         } else {
            this.signatureAlgorithm = Signature.getInstance(var1, var2);
         }

      } catch (NoSuchAlgorithmException var5) {
         var4 = new Object[]{var1, var5.getLocalizedMessage()};
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var4);
      } catch (NoSuchProviderException var6) {
         var4 = new Object[]{var1, var6.getLocalizedMessage()};
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var4);
      }
   }

   protected void engineSetParameter(AlgorithmParameterSpec var1) throws XMLSignatureException {
      try {
         this.signatureAlgorithm.setParameter(var1);
      } catch (InvalidAlgorithmParameterException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected boolean engineVerify(byte[] var1) throws XMLSignatureException {
      try {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Called DSA.verify() on " + Base64.encode(var1));
         }

         byte[] var2 = JavaUtils.convertDsaXMLDSIGtoASN1(var1, this.size / 8);
         return this.signatureAlgorithm.verify(var2);
      } catch (SignatureException var3) {
         throw new XMLSignatureException("empty", var3);
      } catch (IOException var4) {
         throw new XMLSignatureException("empty", var4);
      }
   }

   protected void engineInitVerify(Key var1) throws XMLSignatureException {
      if (!(var1 instanceof PublicKey)) {
         String var2 = var1.getClass().getName();
         String var7 = PublicKey.class.getName();
         Object[] var4 = new Object[]{var2, var7};
         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", var4);
      } else {
         try {
            this.signatureAlgorithm.initVerify((PublicKey)var1);
         } catch (InvalidKeyException var6) {
            Signature var3 = this.signatureAlgorithm;

            try {
               this.signatureAlgorithm = Signature.getInstance(this.signatureAlgorithm.getAlgorithm());
            } catch (Exception var5) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Exception when reinstantiating Signature:" + var5);
               }

               this.signatureAlgorithm = var3;
            }

            throw new XMLSignatureException("empty", var6);
         }

         this.size = ((DSAKey)var1).getParams().getQ().bitLength();
      }
   }

   protected byte[] engineSign() throws XMLSignatureException {
      try {
         byte[] var1 = this.signatureAlgorithm.sign();
         return JavaUtils.convertDsaASN1toXMLDSIG(var1, this.size / 8);
      } catch (IOException var2) {
         throw new XMLSignatureException("empty", var2);
      } catch (SignatureException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected void engineInitSign(Key var1, SecureRandom var2) throws XMLSignatureException {
      if (!(var1 instanceof PrivateKey)) {
         String var3 = var1.getClass().getName();
         String var4 = PrivateKey.class.getName();
         Object[] var5 = new Object[]{var3, var4};
         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", var5);
      } else {
         try {
            this.signatureAlgorithm.initSign((PrivateKey)var1, var2);
         } catch (InvalidKeyException var6) {
            throw new XMLSignatureException("empty", var6);
         }

         this.size = ((DSAKey)var1).getParams().getQ().bitLength();
      }
   }

   protected void engineInitSign(Key var1) throws XMLSignatureException {
      if (!(var1 instanceof PrivateKey)) {
         String var2 = var1.getClass().getName();
         String var3 = PrivateKey.class.getName();
         Object[] var4 = new Object[]{var2, var3};
         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", var4);
      } else {
         try {
            this.signatureAlgorithm.initSign((PrivateKey)var1);
         } catch (InvalidKeyException var5) {
            throw new XMLSignatureException("empty", var5);
         }

         this.size = ((DSAKey)var1).getParams().getQ().bitLength();
      }
   }

   protected void engineUpdate(byte[] var1) throws XMLSignatureException {
      try {
         this.signatureAlgorithm.update(var1);
      } catch (SignatureException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected void engineUpdate(byte var1) throws XMLSignatureException {
      try {
         this.signatureAlgorithm.update(var1);
      } catch (SignatureException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected void engineUpdate(byte[] var1, int var2, int var3) throws XMLSignatureException {
      try {
         this.signatureAlgorithm.update(var1, var2, var3);
      } catch (SignatureException var5) {
         throw new XMLSignatureException("empty", var5);
      }
   }

   protected String engineGetJCEAlgorithmString() {
      return this.signatureAlgorithm.getAlgorithm();
   }

   protected String engineGetJCEProviderName() {
      return this.signatureAlgorithm.getProvider().getName();
   }

   protected void engineSetHMACOutputLength(int var1) throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
   }

   protected void engineInitSign(Key var1, AlgorithmParameterSpec var2) throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnDSA");
   }

   public static class SHA256 extends SignatureDSA {
      public SHA256() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
      }
   }
}
