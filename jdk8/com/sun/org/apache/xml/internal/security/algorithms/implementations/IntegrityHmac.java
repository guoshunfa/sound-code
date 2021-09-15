package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class IntegrityHmac extends SignatureAlgorithmSpi {
   private static Logger log = Logger.getLogger(IntegrityHmac.class.getName());
   private Mac macAlgorithm = null;
   private int HMACOutputLength = 0;
   private boolean HMACOutputLengthSet = false;

   public abstract String engineGetURI();

   abstract int getDigestLength();

   public IntegrityHmac() throws XMLSignatureException {
      String var1 = JCEMapper.translateURItoJCEID(this.engineGetURI());
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Created IntegrityHmacSHA1 using " + var1);
      }

      try {
         this.macAlgorithm = Mac.getInstance(var1);
      } catch (NoSuchAlgorithmException var4) {
         Object[] var3 = new Object[]{var1, var4.getLocalizedMessage()};
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", var3);
      }
   }

   protected void engineSetParameter(AlgorithmParameterSpec var1) throws XMLSignatureException {
      throw new XMLSignatureException("empty");
   }

   public void reset() {
      this.HMACOutputLength = 0;
      this.HMACOutputLengthSet = false;
      this.macAlgorithm.reset();
   }

   protected boolean engineVerify(byte[] var1) throws XMLSignatureException {
      try {
         if (this.HMACOutputLengthSet && this.HMACOutputLength < this.getDigestLength()) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "HMACOutputLength must not be less than " + this.getDigestLength());
            }

            Object[] var4 = new Object[]{String.valueOf(this.getDigestLength())};
            throw new XMLSignatureException("algorithms.HMACOutputLengthMin", var4);
         } else {
            byte[] var2 = this.macAlgorithm.doFinal();
            return MessageDigestAlgorithm.isEqual(var2, var1);
         }
      } catch (IllegalStateException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected void engineInitVerify(Key var1) throws XMLSignatureException {
      if (!(var1 instanceof SecretKey)) {
         String var2 = var1.getClass().getName();
         String var7 = SecretKey.class.getName();
         Object[] var4 = new Object[]{var2, var7};
         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", var4);
      } else {
         try {
            this.macAlgorithm.init(var1);
         } catch (InvalidKeyException var6) {
            Mac var3 = this.macAlgorithm;

            try {
               this.macAlgorithm = Mac.getInstance(this.macAlgorithm.getAlgorithm());
            } catch (Exception var5) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Exception when reinstantiating Mac:" + var5);
               }

               this.macAlgorithm = var3;
            }

            throw new XMLSignatureException("empty", var6);
         }
      }
   }

   protected byte[] engineSign() throws XMLSignatureException {
      try {
         if (this.HMACOutputLengthSet && this.HMACOutputLength < this.getDigestLength()) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "HMACOutputLength must not be less than " + this.getDigestLength());
            }

            Object[] var1 = new Object[]{String.valueOf(this.getDigestLength())};
            throw new XMLSignatureException("algorithms.HMACOutputLengthMin", var1);
         } else {
            return this.macAlgorithm.doFinal();
         }
      } catch (IllegalStateException var2) {
         throw new XMLSignatureException("empty", var2);
      }
   }

   protected void engineInitSign(Key var1) throws XMLSignatureException {
      if (!(var1 instanceof SecretKey)) {
         String var2 = var1.getClass().getName();
         String var3 = SecretKey.class.getName();
         Object[] var4 = new Object[]{var2, var3};
         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", var4);
      } else {
         try {
            this.macAlgorithm.init(var1);
         } catch (InvalidKeyException var5) {
            throw new XMLSignatureException("empty", var5);
         }
      }
   }

   protected void engineInitSign(Key var1, AlgorithmParameterSpec var2) throws XMLSignatureException {
      if (!(var1 instanceof SecretKey)) {
         String var3 = var1.getClass().getName();
         String var4 = SecretKey.class.getName();
         Object[] var5 = new Object[]{var3, var4};
         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", var5);
      } else {
         try {
            this.macAlgorithm.init(var1, var2);
         } catch (InvalidKeyException var6) {
            throw new XMLSignatureException("empty", var6);
         } catch (InvalidAlgorithmParameterException var7) {
            throw new XMLSignatureException("empty", var7);
         }
      }
   }

   protected void engineInitSign(Key var1, SecureRandom var2) throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
   }

   protected void engineUpdate(byte[] var1) throws XMLSignatureException {
      try {
         this.macAlgorithm.update(var1);
      } catch (IllegalStateException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected void engineUpdate(byte var1) throws XMLSignatureException {
      try {
         this.macAlgorithm.update(var1);
      } catch (IllegalStateException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   protected void engineUpdate(byte[] var1, int var2, int var3) throws XMLSignatureException {
      try {
         this.macAlgorithm.update(var1, var2, var3);
      } catch (IllegalStateException var5) {
         throw new XMLSignatureException("empty", var5);
      }
   }

   protected String engineGetJCEAlgorithmString() {
      return this.macAlgorithm.getAlgorithm();
   }

   protected String engineGetJCEProviderName() {
      return this.macAlgorithm.getProvider().getName();
   }

   protected void engineSetHMACOutputLength(int var1) {
      this.HMACOutputLength = var1;
      this.HMACOutputLengthSet = true;
   }

   protected void engineGetContextFromElement(Element var1) {
      super.engineGetContextFromElement(var1);
      if (var1 == null) {
         throw new IllegalArgumentException("element null");
      } else {
         Text var2 = XMLUtils.selectDsNodeText(var1.getFirstChild(), "HMACOutputLength", 0);
         if (var2 != null) {
            this.HMACOutputLength = Integer.parseInt(var2.getData());
            this.HMACOutputLengthSet = true;
         }

      }
   }

   public void engineAddContextToElement(Element var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null element");
      } else {
         if (this.HMACOutputLengthSet) {
            Document var2 = var1.getOwnerDocument();
            Element var3 = XMLUtils.createElementInSignatureSpace(var2, "HMACOutputLength");
            Text var4 = var2.createTextNode(Integer.valueOf(this.HMACOutputLength).toString());
            var3.appendChild(var4);
            XMLUtils.addReturnToElement(var1);
            var1.appendChild(var3);
            XMLUtils.addReturnToElement(var1);
         }

      }
   }

   public static class IntegrityHmacMD5 extends IntegrityHmac {
      public IntegrityHmacMD5() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
      }

      int getDigestLength() {
         return 128;
      }
   }

   public static class IntegrityHmacRIPEMD160 extends IntegrityHmac {
      public IntegrityHmacRIPEMD160() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
      }

      int getDigestLength() {
         return 160;
      }
   }

   public static class IntegrityHmacSHA512 extends IntegrityHmac {
      public IntegrityHmacSHA512() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
      }

      int getDigestLength() {
         return 512;
      }
   }

   public static class IntegrityHmacSHA384 extends IntegrityHmac {
      public IntegrityHmacSHA384() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
      }

      int getDigestLength() {
         return 384;
      }
   }

   public static class IntegrityHmacSHA256 extends IntegrityHmac {
      public IntegrityHmacSHA256() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
      }

      int getDigestLength() {
         return 256;
      }
   }

   public static class IntegrityHmacSHA1 extends IntegrityHmac {
      public IntegrityHmacSHA1() throws XMLSignatureException {
      }

      public String engineGetURI() {
         return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
      }

      int getDigestLength() {
         return 160;
      }
   }
}
