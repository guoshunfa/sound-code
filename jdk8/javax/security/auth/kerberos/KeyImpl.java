package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.util.DerValue;

class KeyImpl implements SecretKey, Destroyable, Serializable {
   private static final long serialVersionUID = -7889313790214321193L;
   private transient byte[] keyBytes;
   private transient int keyType;
   private transient volatile boolean destroyed = false;

   public KeyImpl(byte[] var1, int var2) {
      this.keyBytes = (byte[])var1.clone();
      this.keyType = var2;
   }

   public KeyImpl(KerberosPrincipal var1, char[] var2, String var3) {
      try {
         PrincipalName var4 = new PrincipalName(var1.getName());
         EncryptionKey var5 = new EncryptionKey(var2, var4.getSalt(), var3);
         this.keyBytes = var5.getBytes();
         this.keyType = var5.getEType();
      } catch (KrbException var6) {
         throw new IllegalArgumentException(var6.getMessage());
      }
   }

   public final int getKeyType() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.keyType;
      }
   }

   public final String getAlgorithm() {
      return this.getAlgorithmName(this.keyType);
   }

   private String getAlgorithmName(int var1) {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         switch(var1) {
         case 0:
            return "NULL";
         case 1:
         case 3:
            return "DES";
         case 2:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 19:
         case 20:
         case 21:
         case 22:
         default:
            throw new IllegalArgumentException("Unsupported encryption type: " + var1);
         case 16:
            return "DESede";
         case 17:
            return "AES128";
         case 18:
            return "AES256";
         case 23:
            return "ArcFourHmac";
         }
      }
   }

   public final String getFormat() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return "RAW";
      }
   }

   public final byte[] getEncoded() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return (byte[])this.keyBytes.clone();
      }
   }

   public void destroy() throws DestroyFailedException {
      if (!this.destroyed) {
         this.destroyed = true;
         Arrays.fill((byte[])this.keyBytes, (byte)0);
      }

   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.destroyed) {
         throw new IOException("This key is no longer valid");
      } else {
         try {
            var1.writeObject((new EncryptionKey(this.keyType, this.keyBytes)).asn1Encode());
         } catch (Asn1Exception var3) {
            throw new IOException(var3.getMessage());
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      try {
         EncryptionKey var2 = new EncryptionKey(new DerValue((byte[])((byte[])var1.readObject())));
         this.keyType = var2.getEType();
         this.keyBytes = var2.getBytes();
      } catch (Asn1Exception var3) {
         throw new IOException(var3.getMessage());
      }
   }

   public String toString() {
      HexDumpEncoder var1 = new HexDumpEncoder();
      return "EncryptionKey: keyType=" + this.keyType + " keyBytes (hex dump)=" + (this.keyBytes != null && this.keyBytes.length != 0 ? '\n' + var1.encodeBuffer(this.keyBytes) + '\n' : " Empty Key");
   }

   public int hashCode() {
      byte var1 = 17;
      if (this.isDestroyed()) {
         return var1;
      } else {
         int var2 = 37 * var1 + Arrays.hashCode(this.keyBytes);
         return 37 * var2 + this.keyType;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KeyImpl)) {
         return false;
      } else {
         KeyImpl var2 = (KeyImpl)var1;
         if (!this.isDestroyed() && !var2.isDestroyed()) {
            return this.keyType == var2.getKeyType() && Arrays.equals(this.keyBytes, var2.getEncoded());
         } else {
            return false;
         }
      }
   }
}
