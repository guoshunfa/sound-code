package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.SerialNumber;

public class CertId {
   private static final boolean debug = false;
   private static final AlgorithmId SHA1_ALGID;
   private final AlgorithmId hashAlgId;
   private final byte[] issuerNameHash;
   private final byte[] issuerKeyHash;
   private final SerialNumber certSerialNumber;
   private int myhash;

   public CertId(X509Certificate var1, SerialNumber var2) throws IOException {
      this(var1.getSubjectX500Principal(), var1.getPublicKey(), var2);
   }

   public CertId(X500Principal var1, PublicKey var2, SerialNumber var3) throws IOException {
      this.myhash = -1;
      MessageDigest var4 = null;

      try {
         var4 = MessageDigest.getInstance("SHA1");
      } catch (NoSuchAlgorithmException var9) {
         throw new IOException("Unable to create CertId", var9);
      }

      this.hashAlgId = SHA1_ALGID;
      var4.update(var1.getEncoded());
      this.issuerNameHash = var4.digest();
      byte[] var5 = var2.getEncoded();
      DerValue var6 = new DerValue(var5);
      DerValue[] var7 = new DerValue[]{var6.data.getDerValue(), var6.data.getDerValue()};
      byte[] var8 = var7[1].getBitString();
      var4.update(var8);
      this.issuerKeyHash = var4.digest();
      this.certSerialNumber = var3;
   }

   public CertId(DerInputStream var1) throws IOException {
      this.myhash = -1;
      this.hashAlgId = AlgorithmId.parse(var1.getDerValue());
      this.issuerNameHash = var1.getOctetString();
      this.issuerKeyHash = var1.getOctetString();
      this.certSerialNumber = new SerialNumber(var1);
   }

   public AlgorithmId getHashAlgorithm() {
      return this.hashAlgId;
   }

   public byte[] getIssuerNameHash() {
      return this.issuerNameHash;
   }

   public byte[] getIssuerKeyHash() {
      return this.issuerKeyHash;
   }

   public BigInteger getSerialNumber() {
      return this.certSerialNumber.getNumber();
   }

   public void encode(DerOutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      this.hashAlgId.encode(var2);
      var2.putOctetString(this.issuerNameHash);
      var2.putOctetString(this.issuerKeyHash);
      this.certSerialNumber.encode(var2);
      var1.write((byte)48, (DerOutputStream)var2);
   }

   public int hashCode() {
      if (this.myhash == -1) {
         this.myhash = this.hashAlgId.hashCode();

         int var1;
         for(var1 = 0; var1 < this.issuerNameHash.length; ++var1) {
            this.myhash += this.issuerNameHash[var1] * var1;
         }

         for(var1 = 0; var1 < this.issuerKeyHash.length; ++var1) {
            this.myhash += this.issuerKeyHash[var1] * var1;
         }

         this.myhash += this.certSerialNumber.getNumber().hashCode();
      }

      return this.myhash;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && var1 instanceof CertId) {
         CertId var2 = (CertId)var1;
         return this.hashAlgId.equals(var2.getHashAlgorithm()) && Arrays.equals(this.issuerNameHash, var2.getIssuerNameHash()) && Arrays.equals(this.issuerKeyHash, var2.getIssuerKeyHash()) && this.certSerialNumber.getNumber().equals(var2.getSerialNumber());
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("CertId \n");
      var1.append("Algorithm: " + this.hashAlgId.toString() + "\n");
      var1.append("issuerNameHash \n");
      HexDumpEncoder var2 = new HexDumpEncoder();
      var1.append(var2.encode(this.issuerNameHash));
      var1.append("\nissuerKeyHash: \n");
      var1.append(var2.encode(this.issuerKeyHash));
      var1.append("\n" + this.certSerialNumber.toString());
      return var1.toString();
   }

   static {
      SHA1_ALGID = new AlgorithmId(AlgorithmId.SHA_oid);
   }
}
