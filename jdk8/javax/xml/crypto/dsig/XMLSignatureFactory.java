package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import sun.security.jca.GetInstance;

public abstract class XMLSignatureFactory {
   private String mechanismType;
   private Provider provider;

   protected XMLSignatureFactory() {
   }

   public static XMLSignatureFactory getInstance(String var0) {
      if (var0 == null) {
         throw new NullPointerException("mechanismType cannot be null");
      } else {
         GetInstance.Instance var1;
         try {
            var1 = GetInstance.getInstance((String)"XMLSignatureFactory", (Class)null, (String)var0);
         } catch (NoSuchAlgorithmException var3) {
            throw new NoSuchMechanismException(var3);
         }

         XMLSignatureFactory var2 = (XMLSignatureFactory)var1.impl;
         var2.mechanismType = var0;
         var2.provider = var1.provider;
         return var2;
      }
   }

   public static XMLSignatureFactory getInstance(String var0, Provider var1) {
      if (var0 == null) {
         throw new NullPointerException("mechanismType cannot be null");
      } else if (var1 == null) {
         throw new NullPointerException("provider cannot be null");
      } else {
         GetInstance.Instance var2;
         try {
            var2 = GetInstance.getInstance("XMLSignatureFactory", (Class)null, var0, (Provider)var1);
         } catch (NoSuchAlgorithmException var4) {
            throw new NoSuchMechanismException(var4);
         }

         XMLSignatureFactory var3 = (XMLSignatureFactory)var2.impl;
         var3.mechanismType = var0;
         var3.provider = var2.provider;
         return var3;
      }
   }

   public static XMLSignatureFactory getInstance(String var0, String var1) throws NoSuchProviderException {
      if (var0 == null) {
         throw new NullPointerException("mechanismType cannot be null");
      } else if (var1 == null) {
         throw new NullPointerException("provider cannot be null");
      } else if (var1.length() == 0) {
         throw new NoSuchProviderException();
      } else {
         GetInstance.Instance var2;
         try {
            var2 = GetInstance.getInstance("XMLSignatureFactory", (Class)null, var0, (String)var1);
         } catch (NoSuchAlgorithmException var4) {
            throw new NoSuchMechanismException(var4);
         }

         XMLSignatureFactory var3 = (XMLSignatureFactory)var2.impl;
         var3.mechanismType = var0;
         var3.provider = var2.provider;
         return var3;
      }
   }

   public static XMLSignatureFactory getInstance() {
      return getInstance("DOM");
   }

   public final String getMechanismType() {
      return this.mechanismType;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public abstract XMLSignature newXMLSignature(SignedInfo var1, KeyInfo var2);

   public abstract XMLSignature newXMLSignature(SignedInfo var1, KeyInfo var2, List var3, String var4, String var5);

   public abstract Reference newReference(String var1, DigestMethod var2);

   public abstract Reference newReference(String var1, DigestMethod var2, List var3, String var4, String var5);

   public abstract Reference newReference(String var1, DigestMethod var2, List var3, String var4, String var5, byte[] var6);

   public abstract Reference newReference(String var1, DigestMethod var2, List var3, Data var4, List var5, String var6, String var7);

   public abstract SignedInfo newSignedInfo(CanonicalizationMethod var1, SignatureMethod var2, List var3);

   public abstract SignedInfo newSignedInfo(CanonicalizationMethod var1, SignatureMethod var2, List var3, String var4);

   public abstract XMLObject newXMLObject(List var1, String var2, String var3, String var4);

   public abstract Manifest newManifest(List var1);

   public abstract Manifest newManifest(List var1, String var2);

   public abstract SignatureProperty newSignatureProperty(List var1, String var2, String var3);

   public abstract SignatureProperties newSignatureProperties(List var1, String var2);

   public abstract DigestMethod newDigestMethod(String var1, DigestMethodParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public abstract SignatureMethod newSignatureMethod(String var1, SignatureMethodParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public abstract Transform newTransform(String var1, TransformParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public abstract Transform newTransform(String var1, XMLStructure var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public abstract CanonicalizationMethod newCanonicalizationMethod(String var1, C14NMethodParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public abstract CanonicalizationMethod newCanonicalizationMethod(String var1, XMLStructure var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public final KeyInfoFactory getKeyInfoFactory() {
      return KeyInfoFactory.getInstance(this.getMechanismType(), this.getProvider());
   }

   public abstract XMLSignature unmarshalXMLSignature(XMLValidateContext var1) throws MarshalException;

   public abstract XMLSignature unmarshalXMLSignature(XMLStructure var1) throws MarshalException;

   public abstract boolean isFeatureSupported(String var1);

   public abstract URIDereferencer getURIDereferencer();
}
