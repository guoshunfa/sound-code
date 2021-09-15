package java.security;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;
import javax.crypto.spec.SecretKeySpec;

public class KeyRep implements Serializable {
   private static final long serialVersionUID = -4757683898830641853L;
   private static final String PKCS8 = "PKCS#8";
   private static final String X509 = "X.509";
   private static final String RAW = "RAW";
   private KeyRep.Type type;
   private String algorithm;
   private String format;
   private byte[] encoded;

   public KeyRep(KeyRep.Type var1, String var2, String var3, byte[] var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         this.type = var1;
         this.algorithm = var2;
         this.format = var3.toUpperCase(Locale.ENGLISH);
         this.encoded = (byte[])var4.clone();
      } else {
         throw new NullPointerException("invalid null input(s)");
      }
   }

   protected Object readResolve() throws ObjectStreamException {
      try {
         if (this.type == KeyRep.Type.SECRET && "RAW".equals(this.format)) {
            return new SecretKeySpec(this.encoded, this.algorithm);
         } else {
            KeyFactory var1;
            if (this.type == KeyRep.Type.PUBLIC && "X.509".equals(this.format)) {
               var1 = KeyFactory.getInstance(this.algorithm);
               return var1.generatePublic(new X509EncodedKeySpec(this.encoded));
            } else if (this.type == KeyRep.Type.PRIVATE && "PKCS#8".equals(this.format)) {
               var1 = KeyFactory.getInstance(this.algorithm);
               return var1.generatePrivate(new PKCS8EncodedKeySpec(this.encoded));
            } else {
               throw new NotSerializableException("unrecognized type/format combination: " + this.type + "/" + this.format);
            }
         }
      } catch (NotSerializableException var3) {
         throw var3;
      } catch (Exception var4) {
         NotSerializableException var2 = new NotSerializableException("java.security.Key: [" + this.type + "] [" + this.algorithm + "] [" + this.format + "]");
         var2.initCause(var4);
         throw var2;
      }
   }

   public static enum Type {
      SECRET,
      PUBLIC,
      PRIVATE;
   }
}
