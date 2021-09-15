package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKDCRepPart {
   public EncryptionKey key;
   public LastReq lastReq;
   public int nonce;
   public KerberosTime keyExpiration;
   public TicketFlags flags;
   public KerberosTime authtime;
   public KerberosTime starttime;
   public KerberosTime endtime;
   public KerberosTime renewTill;
   public PrincipalName sname;
   public HostAddresses caddr;
   public int msgType;

   public EncKDCRepPart(EncryptionKey var1, LastReq var2, int var3, KerberosTime var4, TicketFlags var5, KerberosTime var6, KerberosTime var7, KerberosTime var8, KerberosTime var9, PrincipalName var10, HostAddresses var11, int var12) {
      this.key = var1;
      this.lastReq = var2;
      this.nonce = var3;
      this.keyExpiration = var4;
      this.flags = var5;
      this.authtime = var6;
      this.starttime = var7;
      this.endtime = var8;
      this.renewTill = var9;
      this.sname = var10;
      this.caddr = var11;
      this.msgType = var12;
   }

   public EncKDCRepPart() {
   }

   public EncKDCRepPart(byte[] var1, int var2) throws Asn1Exception, IOException, RealmException {
      this.init(new DerValue(var1), var2);
   }

   public EncKDCRepPart(DerValue var1, int var2) throws Asn1Exception, IOException, RealmException {
      this.init(var1, var2);
   }

   protected void init(DerValue var1, int var2) throws Asn1Exception, IOException, RealmException {
      this.msgType = var1.getTag() & 31;
      if (this.msgType != 25 && this.msgType != 26) {
         throw new Asn1Exception(906);
      } else {
         DerValue var3 = var1.getData().getDerValue();
         if (var3.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            this.key = EncryptionKey.parse(var3.getData(), (byte)0, false);
            this.lastReq = LastReq.parse(var3.getData(), (byte)1, false);
            DerValue var4 = var3.getData().getDerValue();
            if ((var4.getTag() & 31) == 2) {
               this.nonce = var4.getData().getBigInteger().intValue();
               this.keyExpiration = KerberosTime.parse(var3.getData(), (byte)3, true);
               this.flags = TicketFlags.parse(var3.getData(), (byte)4, false);
               this.authtime = KerberosTime.parse(var3.getData(), (byte)5, false);
               this.starttime = KerberosTime.parse(var3.getData(), (byte)6, true);
               this.endtime = KerberosTime.parse(var3.getData(), (byte)7, false);
               this.renewTill = KerberosTime.parse(var3.getData(), (byte)8, true);
               Realm var5 = Realm.parse(var3.getData(), (byte)9, false);
               this.sname = PrincipalName.parse(var3.getData(), (byte)10, false, var5);
               if (var3.getData().available() > 0) {
                  this.caddr = HostAddresses.parse(var3.getData(), (byte)11, true);
               }

            } else {
               throw new Asn1Exception(906);
            }
         }
      }
   }

   public byte[] asn1Encode(int var1) throws Asn1Exception, IOException {
      DerOutputStream var2 = new DerOutputStream();
      DerOutputStream var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)-128, true, (byte)0), this.key.asn1Encode());
      var3.write(DerValue.createTag((byte)-128, true, (byte)1), this.lastReq.asn1Encode());
      var2.putInteger(BigInteger.valueOf((long)this.nonce));
      var3.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      if (this.keyExpiration != null) {
         var3.write(DerValue.createTag((byte)-128, true, (byte)3), this.keyExpiration.asn1Encode());
      }

      var3.write(DerValue.createTag((byte)-128, true, (byte)4), this.flags.asn1Encode());
      var3.write(DerValue.createTag((byte)-128, true, (byte)5), this.authtime.asn1Encode());
      if (this.starttime != null) {
         var3.write(DerValue.createTag((byte)-128, true, (byte)6), this.starttime.asn1Encode());
      }

      var3.write(DerValue.createTag((byte)-128, true, (byte)7), this.endtime.asn1Encode());
      if (this.renewTill != null) {
         var3.write(DerValue.createTag((byte)-128, true, (byte)8), this.renewTill.asn1Encode());
      }

      var3.write(DerValue.createTag((byte)-128, true, (byte)9), this.sname.getRealm().asn1Encode());
      var3.write(DerValue.createTag((byte)-128, true, (byte)10), this.sname.asn1Encode());
      if (this.caddr != null) {
         var3.write(DerValue.createTag((byte)-128, true, (byte)11), this.caddr.asn1Encode());
      }

      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var3);
      var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)64, true, (byte)this.msgType), var2);
      return var3.toByteArray();
   }
}
