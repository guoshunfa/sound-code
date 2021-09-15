package sun.security.pkcs12;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import sun.security.pkcs.ParsingException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

class MacData {
   private String digestAlgorithmName;
   private AlgorithmParameters digestAlgorithmParams;
   private byte[] digest;
   private byte[] macSalt;
   private int iterations;
   private byte[] encoded = null;

   MacData(DerInputStream var1) throws IOException, ParsingException {
      DerValue[] var2 = var1.getSequence(2);
      DerInputStream var3 = new DerInputStream(var2[0].toByteArray());
      DerValue[] var4 = var3.getSequence(2);
      AlgorithmId var5 = AlgorithmId.parse(var4[0]);
      this.digestAlgorithmName = var5.getName();
      this.digestAlgorithmParams = var5.getParameters();
      this.digest = var4[1].getOctetString();
      this.macSalt = var2[1].getOctetString();
      if (var2.length > 2) {
         this.iterations = var2[2].getInteger();
      } else {
         this.iterations = 1;
      }

   }

   MacData(String var1, byte[] var2, byte[] var3, int var4) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new NullPointerException("the algName parameter must be non-null");
      } else {
         AlgorithmId var5 = AlgorithmId.get(var1);
         this.digestAlgorithmName = var5.getName();
         this.digestAlgorithmParams = var5.getParameters();
         if (var2 == null) {
            throw new NullPointerException("the digest parameter must be non-null");
         } else if (var2.length == 0) {
            throw new IllegalArgumentException("the digest parameter must not be empty");
         } else {
            this.digest = (byte[])var2.clone();
            this.macSalt = var3;
            this.iterations = var4;
            this.encoded = null;
         }
      }
   }

   MacData(AlgorithmParameters var1, byte[] var2, byte[] var3, int var4) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new NullPointerException("the algParams parameter must be non-null");
      } else {
         AlgorithmId var5 = AlgorithmId.get(var1);
         this.digestAlgorithmName = var5.getName();
         this.digestAlgorithmParams = var5.getParameters();
         if (var2 == null) {
            throw new NullPointerException("the digest parameter must be non-null");
         } else if (var2.length == 0) {
            throw new IllegalArgumentException("the digest parameter must not be empty");
         } else {
            this.digest = (byte[])var2.clone();
            this.macSalt = var3;
            this.iterations = var4;
            this.encoded = null;
         }
      }
   }

   String getDigestAlgName() {
      return this.digestAlgorithmName;
   }

   byte[] getSalt() {
      return this.macSalt;
   }

   int getIterations() {
      return this.iterations;
   }

   byte[] getDigest() {
      return this.digest;
   }

   public byte[] getEncoded() throws NoSuchAlgorithmException, IOException {
      if (this.encoded != null) {
         return (byte[])this.encoded.clone();
      } else {
         DerOutputStream var1 = new DerOutputStream();
         DerOutputStream var2 = new DerOutputStream();
         DerOutputStream var3 = new DerOutputStream();
         AlgorithmId var4 = AlgorithmId.get(this.digestAlgorithmName);
         var4.encode(var3);
         var3.putOctetString(this.digest);
         var2.write((byte)48, (DerOutputStream)var3);
         var2.putOctetString(this.macSalt);
         var2.putInteger(this.iterations);
         var1.write((byte)48, (DerOutputStream)var2);
         this.encoded = var1.toByteArray();
         return (byte[])this.encoded.clone();
      }
   }
}
