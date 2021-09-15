package javax.xml.crypto;

import java.security.Key;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public abstract class KeySelector {
   protected KeySelector() {
   }

   public abstract KeySelectorResult select(KeyInfo var1, KeySelector.Purpose var2, AlgorithmMethod var3, XMLCryptoContext var4) throws KeySelectorException;

   public static KeySelector singletonKeySelector(Key var0) {
      return new KeySelector.SingletonKeySelector(var0);
   }

   private static class SingletonKeySelector extends KeySelector {
      private final Key key;

      SingletonKeySelector(Key var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.key = var1;
         }
      }

      public KeySelectorResult select(KeyInfo var1, KeySelector.Purpose var2, AlgorithmMethod var3, XMLCryptoContext var4) throws KeySelectorException {
         return new KeySelectorResult() {
            public Key getKey() {
               return SingletonKeySelector.this.key;
            }
         };
      }
   }

   public static class Purpose {
      private final String name;
      public static final KeySelector.Purpose SIGN = new KeySelector.Purpose("sign");
      public static final KeySelector.Purpose VERIFY = new KeySelector.Purpose("verify");
      public static final KeySelector.Purpose ENCRYPT = new KeySelector.Purpose("encrypt");
      public static final KeySelector.Purpose DECRYPT = new KeySelector.Purpose("decrypt");

      private Purpose(String var1) {
         this.name = var1;
      }

      public String toString() {
         return this.name;
      }
   }
}
