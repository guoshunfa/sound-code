package sun.security.timestamp;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Extension;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public class TSRequest {
   private int version = 1;
   private AlgorithmId hashAlgorithmId = null;
   private byte[] hashValue;
   private String policyId = null;
   private BigInteger nonce = null;
   private boolean returnCertificate = false;
   private X509Extension[] extensions = null;

   public TSRequest(String var1, byte[] var2, MessageDigest var3) throws NoSuchAlgorithmException {
      this.policyId = var1;
      this.hashAlgorithmId = AlgorithmId.get(var3.getAlgorithm());
      this.hashValue = var3.digest(var2);
   }

   public byte[] getHashedMessage() {
      return (byte[])this.hashValue.clone();
   }

   public void setVersion(int var1) {
      this.version = var1;
   }

   public void setPolicyId(String var1) {
      this.policyId = var1;
   }

   public void setNonce(BigInteger var1) {
      this.nonce = var1;
   }

   public void requestCertificate(boolean var1) {
      this.returnCertificate = var1;
   }

   public void setExtensions(X509Extension[] var1) {
      this.extensions = var1;
   }

   public byte[] encode() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putInteger(this.version);
      DerOutputStream var2 = new DerOutputStream();
      this.hashAlgorithmId.encode(var2);
      var2.putOctetString(this.hashValue);
      var1.write((byte)48, (DerOutputStream)var2);
      if (this.policyId != null) {
         var1.putOID(new ObjectIdentifier(this.policyId));
      }

      if (this.nonce != null) {
         var1.putInteger(this.nonce);
      }

      if (this.returnCertificate) {
         var1.putBoolean(true);
      }

      DerOutputStream var3 = new DerOutputStream();
      var3.write((byte)48, (DerOutputStream)var1);
      return var3.toByteArray();
   }
}
