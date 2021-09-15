package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBPriv {
   public int pvno;
   public int msgType;
   public EncryptedData encPart;

   public KRBPriv(EncryptedData var1) {
      this.pvno = 5;
      this.msgType = 21;
      this.encPart = var1;
   }

   public KRBPriv(byte[] var1) throws Asn1Exception, KrbApErrException, IOException {
      this.init(new DerValue(var1));
   }

   public KRBPriv(DerValue var1) throws Asn1Exception, KrbApErrException, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, KrbApErrException, IOException {
      if ((var1.getTag() & 31) == 21 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) == 0) {
               this.pvno = var3.getData().getBigInteger().intValue();
               if (this.pvno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  var3 = var2.getData().getDerValue();
                  if ((var3.getTag() & 31) == 1) {
                     this.msgType = var3.getData().getBigInteger().intValue();
                     if (this.msgType != 21) {
                        throw new KrbApErrException(40);
                     } else {
                        this.encPart = EncryptedData.parse(var2.getData(), (byte)3, false);
                        if (var2.getData().available() > 0) {
                           throw new Asn1Exception(906);
                        }
                     }
                  } else {
                     throw new Asn1Exception(906);
                  }
               }
            } else {
               throw new Asn1Exception(906);
            }
         }
      } else {
         throw new Asn1Exception(906);
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.pvno));
      DerOutputStream var2 = new DerOutputStream();
      var2.write(DerValue.createTag((byte)-128, true, (byte)0), var1);
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.msgType));
      var2.write(DerValue.createTag((byte)-128, true, (byte)1), var1);
      var2.write(DerValue.createTag((byte)-128, true, (byte)3), this.encPart.asn1Encode());
      var1 = new DerOutputStream();
      var1.write((byte)48, (DerOutputStream)var2);
      var2 = new DerOutputStream();
      var2.write(DerValue.createTag((byte)64, true, (byte)21), var1);
      return var2.toByteArray();
   }
}
