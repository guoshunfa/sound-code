package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
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
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SignatureECDSA extends SignatureAlgorithmSpi {
   private static Logger log = Logger.getLogger(SignatureECDSA.class.getName());
   private Signature signatureAlgorithm = null;

   public abstract String engineGetURI();

   public static byte[] convertASN1toXMLDSIG(byte[] var0) throws IOException {
      if (var0.length >= 8 && var0[0] == 48) {
         byte var1;
         if (var0[1] > 0) {
            var1 = 2;
         } else {
            if (var0[1] != -127) {
               throw new IOException("Invalid ASN.1 format of ECDSA signature");
            }

            var1 = 3;
         }

         byte var2 = var0[var1 + 1];

         int var3;
         for(var3 = var2; var3 > 0 && var0[var1 + 2 + var2 - var3] == 0; --var3) {
         }

         byte var4 = var0[var1 + 2 + var2 + 1];

         int var5;
         for(var5 = var4; var5 > 0 && var0[var1 + 2 + var2 + 2 + var4 - var5] == 0; --var5) {
         }

         int var6 = Math.max(var3, var5);
         if ((var0[var1 - 1] & 255) == var0.length - var1 && (var0[var1 - 1] & 255) == 2 + var2 + 2 + var4 && var0[var1] == 2 && var0[var1 + 2 + var2] == 2) {
            byte[] var7 = new byte[2 * var6];
            System.arraycopy(var0, var1 + 2 + var2 - var3, var7, var6 - var3, var3);
            System.arraycopy(var0, var1 + 2 + var2 + 2 + var4 - var5, var7, 2 * var6 - var5, var5);
            return var7;
         } else {
            throw new IOException("Invalid ASN.1 format of ECDSA signature");
         }
      } else {
         throw new IOException("Invalid ASN.1 format of ECDSA signature");
      }
   }

   public static byte[] convertXMLDSIGtoASN1(byte[] var0) throws IOException {
      int var1 = var0.length / 2;

      int var2;
      for(var2 = var1; var2 > 0 && var0[var1 - var2] == 0; --var2) {
      }

      int var3 = var2;
      if (var0[var1 - var2] < 0) {
         var3 = var2 + 1;
      }

      int var4;
      for(var4 = var1; var4 > 0 && var0[2 * var1 - var4] == 0; --var4) {
      }

      int var5 = var4;
      if (var0[2 * var1 - var4] < 0) {
         var5 = var4 + 1;
      }

      int var6 = 2 + var3 + 2 + var5;
      if (var6 > 255) {
         throw new IOException("Invalid XMLDSIG format of ECDSA signature");
      } else {
         byte var7;
         byte[] var8;
         if (var6 < 128) {
            var8 = new byte[4 + var3 + 2 + var5];
            var7 = 1;
         } else {
            var8 = new byte[5 + var3 + 2 + var5];
            var8[1] = -127;
            var7 = 2;
         }

         var8[0] = 48;
         int var9 = var7 + 1;
         var8[var7] = (byte)var6;
         var8[var9++] = 2;
         var8[var9++] = (byte)var3;
         System.arraycopy(var0, var1 - var2, var8, var9 + var3 - var2, var2);
         var9 += var3;
         var8[var9++] = 2;
         var8[var9++] = (byte)var5;
         System.arraycopy(var0, 2 * var1 - var4, var8, var9 + var5 - var4, var4);
         return var8;
      }
   }

   public SignatureECDSA() throws XMLSignatureException {
      String var1 = JCEMapper.translateURItoJCEID(this.engineGetURI());
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Created SignatureECDSA using " + var1);
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
         byte[] var2 = convertXMLDSIGtoASN1(var1);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Called ECDSA.verify() on " + Base64.encode(var1));
         }

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
      }
   }

   protected byte[] engineSign() throws XMLSignatureException {
      try {
         byte[] var1 = this.signatureAlgorithm.sign();
         return convertASN1toXMLDSIG(var1);
      } catch (SignatureException var2) {
         throw new XMLSignatureException("empty", var2);
      } catch (IOException var3) {
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
      throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
   }

   public static class SignatureECDSASHA512 extends SignatureECDSA {
      public SignatureECDSASHA512() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
      }
   }

   public static class SignatureECDSASHA384 extends SignatureECDSA {
      public SignatureECDSASHA384() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
      }
   }

   public static class SignatureECDSASHA256 extends SignatureECDSA {
      public SignatureECDSASHA256() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
      }
   }

   public static class SignatureECDSASHA1 extends SignatureECDSA {
      public SignatureECDSASHA1() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
      }
   }
}
