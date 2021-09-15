package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class APReq {
   public int pvno;
   public int msgType;
   public APOptions apOptions;
   public Ticket ticket;
   public EncryptedData authenticator;

   public APReq(APOptions var1, Ticket var2, EncryptedData var3) {
      this.pvno = 5;
      this.msgType = 14;
      this.apOptions = var1;
      this.ticket = var2;
      this.authenticator = var3;
   }

   public APReq(byte[] var1) throws Asn1Exception, IOException, KrbApErrException, RealmException {
      this.init(new DerValue(var1));
   }

   public APReq(DerValue var1) throws Asn1Exception, IOException, KrbApErrException, RealmException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, KrbApErrException, RealmException {
      if ((var1.getTag() & 31) == 14 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) != 0) {
               throw new Asn1Exception(906);
            } else {
               this.pvno = var3.getData().getBigInteger().intValue();
               if (this.pvno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  var3 = var2.getData().getDerValue();
                  if ((var3.getTag() & 31) != 1) {
                     throw new Asn1Exception(906);
                  } else {
                     this.msgType = var3.getData().getBigInteger().intValue();
                     if (this.msgType != 14) {
                        throw new KrbApErrException(40);
                     } else {
                        this.apOptions = APOptions.parse(var2.getData(), (byte)2, false);
                        this.ticket = Ticket.parse(var2.getData(), (byte)3, false);
                        this.authenticator = EncryptedData.parse(var2.getData(), (byte)4, false);
                        if (var2.getData().available() > 0) {
                           throw new Asn1Exception(906);
                        }
                     }
                  }
               }
            }
         }
      } else {
         throw new Asn1Exception(906);
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(BigInteger.valueOf((long)this.pvno));
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      var2.putInteger(BigInteger.valueOf((long)this.msgType));
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var1.write(DerValue.createTag((byte)-128, true, (byte)2), this.apOptions.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)3), this.ticket.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)4), this.authenticator.asn1Encode());
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      DerOutputStream var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)64, true, (byte)14), var2);
      return var3.toByteArray();
   }
}
