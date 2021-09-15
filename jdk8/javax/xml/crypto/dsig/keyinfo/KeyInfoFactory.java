package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import sun.security.jca.GetInstance;

public abstract class KeyInfoFactory {
   private String mechanismType;
   private Provider provider;

   protected KeyInfoFactory() {
   }

   public static KeyInfoFactory getInstance(String var0) {
      if (var0 == null) {
         throw new NullPointerException("mechanismType cannot be null");
      } else {
         GetInstance.Instance var1;
         try {
            var1 = GetInstance.getInstance((String)"KeyInfoFactory", (Class)null, (String)var0);
         } catch (NoSuchAlgorithmException var3) {
            throw new NoSuchMechanismException(var3);
         }

         KeyInfoFactory var2 = (KeyInfoFactory)var1.impl;
         var2.mechanismType = var0;
         var2.provider = var1.provider;
         return var2;
      }
   }

   public static KeyInfoFactory getInstance(String var0, Provider var1) {
      if (var0 == null) {
         throw new NullPointerException("mechanismType cannot be null");
      } else if (var1 == null) {
         throw new NullPointerException("provider cannot be null");
      } else {
         GetInstance.Instance var2;
         try {
            var2 = GetInstance.getInstance("KeyInfoFactory", (Class)null, var0, (Provider)var1);
         } catch (NoSuchAlgorithmException var4) {
            throw new NoSuchMechanismException(var4);
         }

         KeyInfoFactory var3 = (KeyInfoFactory)var2.impl;
         var3.mechanismType = var0;
         var3.provider = var2.provider;
         return var3;
      }
   }

   public static KeyInfoFactory getInstance(String var0, String var1) throws NoSuchProviderException {
      if (var0 == null) {
         throw new NullPointerException("mechanismType cannot be null");
      } else if (var1 == null) {
         throw new NullPointerException("provider cannot be null");
      } else if (var1.length() == 0) {
         throw new NoSuchProviderException();
      } else {
         GetInstance.Instance var2;
         try {
            var2 = GetInstance.getInstance("KeyInfoFactory", (Class)null, var0, (String)var1);
         } catch (NoSuchAlgorithmException var4) {
            throw new NoSuchMechanismException(var4);
         }

         KeyInfoFactory var3 = (KeyInfoFactory)var2.impl;
         var3.mechanismType = var0;
         var3.provider = var2.provider;
         return var3;
      }
   }

   public static KeyInfoFactory getInstance() {
      return getInstance("DOM");
   }

   public final String getMechanismType() {
      return this.mechanismType;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public abstract KeyInfo newKeyInfo(List var1);

   public abstract KeyInfo newKeyInfo(List var1, String var2);

   public abstract KeyName newKeyName(String var1);

   public abstract KeyValue newKeyValue(PublicKey var1) throws KeyException;

   public abstract PGPData newPGPData(byte[] var1);

   public abstract PGPData newPGPData(byte[] var1, byte[] var2, List var3);

   public abstract PGPData newPGPData(byte[] var1, List var2);

   public abstract RetrievalMethod newRetrievalMethod(String var1);

   public abstract RetrievalMethod newRetrievalMethod(String var1, String var2, List var3);

   public abstract X509Data newX509Data(List var1);

   public abstract X509IssuerSerial newX509IssuerSerial(String var1, BigInteger var2);

   public abstract boolean isFeatureSupported(String var1);

   public abstract URIDereferencer getURIDereferencer();

   public abstract KeyInfo unmarshalKeyInfo(XMLStructure var1) throws MarshalException;
}
