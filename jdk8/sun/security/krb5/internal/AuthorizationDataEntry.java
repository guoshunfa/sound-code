package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorizationDataEntry implements Cloneable {
   public int adType;
   public byte[] adData;

   private AuthorizationDataEntry() {
   }

   public AuthorizationDataEntry(int var1, byte[] var2) {
      this.adType = var1;
      this.adData = var2;
   }

   public Object clone() {
      AuthorizationDataEntry var1 = new AuthorizationDataEntry();
      var1.adType = this.adType;
      if (this.adData != null) {
         var1.adData = new byte[this.adData.length];
         System.arraycopy(this.adData, 0, var1.adData, 0, this.adData.length);
      }

      return var1;
   }

   public AuthorizationDataEntry(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.adType = var2.getData().getBigInteger().intValue();
            var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) == 1) {
               this.adData = var2.getData().getOctetString();
               if (var1.getData().available() > 0) {
                  throw new Asn1Exception(906);
               }
            } else {
               throw new Asn1Exception(906);
            }
         } else {
            throw new Asn1Exception(906);
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(this.adType);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      var2.putOctetString(this.adData);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public void writeEntry(CCacheOutputStream var1) throws IOException {
      var1.write16(this.adType);
      var1.write32(this.adData.length);
      var1.write(this.adData, 0, this.adData.length);
   }

   public String toString() {
      return "adType=" + this.adType + " adData.length=" + this.adData.length;
   }
}
