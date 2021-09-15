package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class EncryptedPrivateKeyInfo {
   private AlgorithmId algid;
   private byte[] encryptedData;
   private byte[] encoded;

   public EncryptedPrivateKeyInfo(byte[] var1) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("encoding must not be null");
      } else {
         DerValue var2 = new DerValue(var1);
         DerValue[] var3 = new DerValue[]{var2.data.getDerValue(), var2.data.getDerValue()};
         if (var2.data.available() != 0) {
            throw new IOException("overrun, bytes = " + var2.data.available());
         } else {
            this.algid = AlgorithmId.parse(var3[0]);
            if (var3[0].data.available() != 0) {
               throw new IOException("encryptionAlgorithm field overrun");
            } else {
               this.encryptedData = var3[1].getOctetString();
               if (var3[1].data.available() != 0) {
                  throw new IOException("encryptedData field overrun");
               } else {
                  this.encoded = (byte[])var1.clone();
               }
            }
         }
      }
   }

   public EncryptedPrivateKeyInfo(AlgorithmId var1, byte[] var2) {
      this.algid = var1;
      this.encryptedData = (byte[])var2.clone();
   }

   public AlgorithmId getAlgorithm() {
      return this.algid;
   }

   public byte[] getEncryptedData() {
      return (byte[])this.encryptedData.clone();
   }

   public byte[] getEncoded() throws IOException {
      if (this.encoded != null) {
         return (byte[])this.encoded.clone();
      } else {
         DerOutputStream var1 = new DerOutputStream();
         DerOutputStream var2 = new DerOutputStream();
         this.algid.encode(var2);
         var2.putOctetString(this.encryptedData);
         var1.write((byte)48, (DerOutputStream)var2);
         this.encoded = var1.toByteArray();
         return (byte[])this.encoded.clone();
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof EncryptedPrivateKeyInfo)) {
         return false;
      } else {
         try {
            byte[] var2 = this.getEncoded();
            byte[] var3 = ((EncryptedPrivateKeyInfo)var1).getEncoded();
            if (var2.length != var3.length) {
               return false;
            } else {
               for(int var4 = 0; var4 < var2.length; ++var4) {
                  if (var2[var4] != var3[var4]) {
                     return false;
                  }
               }

               return true;
            }
         } catch (IOException var5) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.encryptedData.length; ++var2) {
         var1 += this.encryptedData[var2] * var2;
      }

      return var1;
   }
}
