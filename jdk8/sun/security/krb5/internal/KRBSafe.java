package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBSafe {
   public int pvno;
   public int msgType;
   public KRBSafeBody safeBody;
   public Checksum cksum;

   public KRBSafe(KRBSafeBody var1, Checksum var2) {
      this.pvno = 5;
      this.msgType = 20;
      this.safeBody = var1;
      this.cksum = var2;
   }

   public KRBSafe(byte[] var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(new DerValue(var1));
   }

   public KRBSafe(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      if ((var1.getTag() & 31) == 20 && var1.isApplication() && var1.isConstructed()) {
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
                     if (this.msgType != 20) {
                        throw new KrbApErrException(40);
                     } else {
                        this.safeBody = KRBSafeBody.parse(var2.getData(), (byte)2, false);
                        this.cksum = Checksum.parse(var2.getData(), (byte)3, false);
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
      DerOutputStream var2 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.pvno));
      var2.write(DerValue.createTag((byte)-128, true, (byte)0), var1);
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.msgType));
      var2.write(DerValue.createTag((byte)-128, true, (byte)1), var1);
      var2.write(DerValue.createTag((byte)-128, true, (byte)2), this.safeBody.asn1Encode());
      var2.write(DerValue.createTag((byte)-128, true, (byte)3), this.cksum.asn1Encode());
      var1 = new DerOutputStream();
      var1.write((byte)48, (DerOutputStream)var2);
      var2 = new DerOutputStream();
      var2.write(DerValue.createTag((byte)64, true, (byte)20), var1);
      return var2.toByteArray();
   }
}
