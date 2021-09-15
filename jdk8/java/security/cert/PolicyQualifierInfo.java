package java.security.cert;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerValue;

public class PolicyQualifierInfo {
   private byte[] mEncoded;
   private String mId;
   private byte[] mData;
   private String pqiString;

   public PolicyQualifierInfo(byte[] var1) throws IOException {
      this.mEncoded = (byte[])var1.clone();
      DerValue var2 = new DerValue(this.mEncoded);
      if (var2.tag != 48) {
         throw new IOException("Invalid encoding for PolicyQualifierInfo");
      } else {
         this.mId = var2.data.getDerValue().getOID().toString();
         byte[] var3 = var2.data.toByteArray();
         if (var3 == null) {
            this.mData = null;
         } else {
            this.mData = new byte[var3.length];
            System.arraycopy(var3, 0, this.mData, 0, var3.length);
         }

      }
   }

   public final String getPolicyQualifierId() {
      return this.mId;
   }

   public final byte[] getEncoded() {
      return (byte[])this.mEncoded.clone();
   }

   public final byte[] getPolicyQualifier() {
      return this.mData == null ? null : (byte[])this.mData.clone();
   }

   public String toString() {
      if (this.pqiString != null) {
         return this.pqiString;
      } else {
         HexDumpEncoder var1 = new HexDumpEncoder();
         StringBuffer var2 = new StringBuffer();
         var2.append("PolicyQualifierInfo: [\n");
         var2.append("  qualifierID: " + this.mId + "\n");
         var2.append("  qualifier: " + (this.mData == null ? "null" : var1.encodeBuffer(this.mData)) + "\n");
         var2.append("]");
         this.pqiString = var2.toString();
         return this.pqiString;
      }
   }
}
