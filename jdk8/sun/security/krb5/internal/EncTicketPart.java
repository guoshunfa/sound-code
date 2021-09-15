package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncTicketPart {
   public TicketFlags flags;
   public EncryptionKey key;
   public PrincipalName cname;
   public TransitedEncoding transited;
   public KerberosTime authtime;
   public KerberosTime starttime;
   public KerberosTime endtime;
   public KerberosTime renewTill;
   public HostAddresses caddr;
   public AuthorizationData authorizationData;

   public EncTicketPart(TicketFlags var1, EncryptionKey var2, PrincipalName var3, TransitedEncoding var4, KerberosTime var5, KerberosTime var6, KerberosTime var7, KerberosTime var8, HostAddresses var9, AuthorizationData var10) {
      this.flags = var1;
      this.key = var2;
      this.cname = var3;
      this.transited = var4;
      this.authtime = var5;
      this.starttime = var6;
      this.endtime = var7;
      this.renewTill = var8;
      this.caddr = var9;
      this.authorizationData = var10;
   }

   public EncTicketPart(byte[] var1) throws Asn1Exception, KrbException, IOException {
      this.init(new DerValue(var1));
   }

   public EncTicketPart(DerValue var1) throws Asn1Exception, KrbException, IOException {
      this.init(var1);
   }

   private static String getHexBytes(byte[] var0, int var1) throws IOException {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < var1; ++var3) {
         int var4 = var0[var3] >> 4 & 15;
         int var5 = var0[var3] & 15;
         var2.append(Integer.toHexString(var4));
         var2.append(Integer.toHexString(var5));
         var2.append(' ');
      }

      return var2.toString();
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, RealmException {
      this.renewTill = null;
      this.caddr = null;
      this.authorizationData = null;
      if ((var1.getTag() & 31) == 3 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            this.flags = TicketFlags.parse(var2.getData(), (byte)0, false);
            this.key = EncryptionKey.parse(var2.getData(), (byte)1, false);
            Realm var4 = Realm.parse(var2.getData(), (byte)2, false);
            this.cname = PrincipalName.parse(var2.getData(), (byte)3, false, var4);
            this.transited = TransitedEncoding.parse(var2.getData(), (byte)4, false);
            this.authtime = KerberosTime.parse(var2.getData(), (byte)5, false);
            this.starttime = KerberosTime.parse(var2.getData(), (byte)6, true);
            this.endtime = KerberosTime.parse(var2.getData(), (byte)7, false);
            if (var2.getData().available() > 0) {
               this.renewTill = KerberosTime.parse(var2.getData(), (byte)8, true);
            }

            if (var2.getData().available() > 0) {
               this.caddr = HostAddresses.parse(var2.getData(), (byte)9, true);
            }

            if (var2.getData().available() > 0) {
               this.authorizationData = AuthorizationData.parse(var2.getData(), (byte)10, true);
            }

            if (var2.getData().available() > 0) {
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
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), this.flags.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), this.key.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)2), this.cname.getRealm().asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)3), this.cname.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)4), this.transited.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)5), this.authtime.asn1Encode());
      if (this.starttime != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)6), this.starttime.asn1Encode());
      }

      var1.write(DerValue.createTag((byte)-128, true, (byte)7), this.endtime.asn1Encode());
      if (this.renewTill != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)8), this.renewTill.asn1Encode());
      }

      if (this.caddr != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)9), this.caddr.asn1Encode());
      }

      if (this.authorizationData != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)10), this.authorizationData.asn1Encode());
      }

      var2.write((byte)48, (DerOutputStream)var1);
      var1 = new DerOutputStream();
      var1.write(DerValue.createTag((byte)64, true, (byte)3), var2);
      return var1.toByteArray();
   }
}
