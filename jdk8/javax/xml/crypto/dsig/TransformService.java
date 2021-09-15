package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import sun.security.jca.GetInstance;

public abstract class TransformService implements Transform {
   private String algorithm;
   private String mechanism;
   private Provider provider;

   protected TransformService() {
   }

   public static TransformService getInstance(String var0, String var1) throws NoSuchAlgorithmException {
      if (var1 != null && var0 != null) {
         boolean var2 = false;
         if (var1.equals("DOM")) {
            var2 = true;
         }

         List var3 = GetInstance.getServices("TransformService", var0);
         Iterator var4 = var3.iterator();

         Provider.Service var5;
         String var6;
         do {
            if (!var4.hasNext()) {
               throw new NoSuchAlgorithmException(var0 + " algorithm and " + var1 + " mechanism not available");
            }

            var5 = (Provider.Service)var4.next();
            var6 = var5.getAttribute("MechanismType");
         } while((var6 != null || !var2) && (var6 == null || !var6.equals(var1)));

         GetInstance.Instance var7 = GetInstance.getInstance(var5, (Class)null);
         TransformService var8 = (TransformService)var7.impl;
         var8.algorithm = var0;
         var8.mechanism = var1;
         var8.provider = var7.provider;
         return var8;
      } else {
         throw new NullPointerException();
      }
   }

   public static TransformService getInstance(String var0, String var1, Provider var2) throws NoSuchAlgorithmException {
      if (var1 != null && var0 != null && var2 != null) {
         boolean var3 = false;
         if (var1.equals("DOM")) {
            var3 = true;
         }

         Provider.Service var4 = GetInstance.getService("TransformService", var0, var2);
         String var5 = var4.getAttribute("MechanismType");
         if ((var5 != null || !var3) && (var5 == null || !var5.equals(var1))) {
            throw new NoSuchAlgorithmException(var0 + " algorithm and " + var1 + " mechanism not available");
         } else {
            GetInstance.Instance var6 = GetInstance.getInstance(var4, (Class)null);
            TransformService var7 = (TransformService)var6.impl;
            var7.algorithm = var0;
            var7.mechanism = var1;
            var7.provider = var6.provider;
            return var7;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public static TransformService getInstance(String var0, String var1, String var2) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var1 != null && var0 != null && var2 != null) {
         if (var2.length() == 0) {
            throw new NoSuchProviderException();
         } else {
            boolean var3 = false;
            if (var1.equals("DOM")) {
               var3 = true;
            }

            Provider.Service var4 = GetInstance.getService("TransformService", var0, var2);
            String var5 = var4.getAttribute("MechanismType");
            if ((var5 != null || !var3) && (var5 == null || !var5.equals(var1))) {
               throw new NoSuchAlgorithmException(var0 + " algorithm and " + var1 + " mechanism not available");
            } else {
               GetInstance.Instance var6 = GetInstance.getInstance(var4, (Class)null);
               TransformService var7 = (TransformService)var6.impl;
               var7.algorithm = var0;
               var7.mechanism = var1;
               var7.provider = var6.provider;
               return var7;
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public final String getMechanismType() {
      return this.mechanism;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public abstract void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException;

   public abstract void marshalParams(XMLStructure var1, XMLCryptoContext var2) throws MarshalException;

   public abstract void init(XMLStructure var1, XMLCryptoContext var2) throws InvalidAlgorithmParameterException;

   private static class MechanismMapEntry implements Map.Entry<String, String> {
      private final String mechanism;
      private final String algorithm;
      private final String key;

      MechanismMapEntry(String var1, String var2) {
         this.algorithm = var1;
         this.mechanism = var2;
         this.key = "TransformService." + var1 + " MechanismType";
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            boolean var10000;
            label38: {
               label27: {
                  Map.Entry var2 = (Map.Entry)var1;
                  if (this.getKey() == null) {
                     if (var2.getKey() != null) {
                        break label27;
                     }
                  } else if (!this.getKey().equals(var2.getKey())) {
                     break label27;
                  }

                  if (this.getValue() == null) {
                     if (var2.getValue() == null) {
                        break label38;
                     }
                  } else if (this.getValue().equals(var2.getValue())) {
                     break label38;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.mechanism;
      }

      public String setValue(String var1) {
         throw new UnsupportedOperationException();
      }

      public int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }
   }
}
