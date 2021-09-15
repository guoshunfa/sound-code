package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KrbCredInfo {
   public EncryptionKey key;
   public PrincipalName pname;
   public TicketFlags flags;
   public KerberosTime authtime;
   public KerberosTime starttime;
   public KerberosTime endtime;
   public KerberosTime renewTill;
   public PrincipalName sname;
   public HostAddresses caddr;

   private KrbCredInfo() {
   }

   public KrbCredInfo(EncryptionKey var1, PrincipalName var2, TicketFlags var3, KerberosTime var4, KerberosTime var5, KerberosTime var6, KerberosTime var7, PrincipalName var8, HostAddresses var9) {
      this.key = var1;
      this.pname = var2;
      this.flags = var3;
      this.authtime = var4;
      this.starttime = var5;
      this.endtime = var6;
      this.renewTill = var7;
      this.sname = var8;
      this.caddr = var9;
   }

   public KrbCredInfo(DerValue var1) throws Asn1Exception, IOException, RealmException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         this.pname = null;
         this.flags = null;
         this.authtime = null;
         this.starttime = null;
         this.endtime = null;
         this.renewTill = null;
         this.sname = null;
         this.caddr = null;
         this.key = EncryptionKey.parse(var1.getData(), (byte)0, false);
         Realm var2 = null;
         Realm var3 = null;
         if (var1.getData().available() > 0) {
            var2 = Realm.parse(var1.getData(), (byte)1, true);
         }

         if (var1.getData().available() > 0) {
            this.pname = PrincipalName.parse(var1.getData(), (byte)2, true, var2);
         }

         if (var1.getData().available() > 0) {
            this.flags = TicketFlags.parse(var1.getData(), (byte)3, true);
         }

         if (var1.getData().available() > 0) {
            this.authtime = KerberosTime.parse(var1.getData(), (byte)4, true);
         }

         if (var1.getData().available() > 0) {
            this.starttime = KerberosTime.parse(var1.getData(), (byte)5, true);
         }

         if (var1.getData().available() > 0) {
            this.endtime = KerberosTime.parse(var1.getData(), (byte)6, true);
         }

         if (var1.getData().available() > 0) {
            this.renewTill = KerberosTime.parse(var1.getData(), (byte)7, true);
         }

         if (var1.getData().available() > 0) {
            var3 = Realm.parse(var1.getData(), (byte)8, true);
         }

         if (var1.getData().available() > 0) {
            this.sname = PrincipalName.parse(var1.getData(), (byte)9, true, var3);
         }

         if (var1.getData().available() > 0) {
            this.caddr = HostAddresses.parse(var1.getData(), (byte)10, true);
         }

         if (var1.getData().available() > 0) {
            throw new Asn1Exception(906);
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      Vector var1 = new Vector();
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)0), this.key.asn1Encode()));
      if (this.pname != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)1), this.pname.getRealm().asn1Encode()));
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)2), this.pname.asn1Encode()));
      }

      if (this.flags != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)3), this.flags.asn1Encode()));
      }

      if (this.authtime != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)4), this.authtime.asn1Encode()));
      }

      if (this.starttime != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)5), this.starttime.asn1Encode()));
      }

      if (this.endtime != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)6), this.endtime.asn1Encode()));
      }

      if (this.renewTill != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)7), this.renewTill.asn1Encode()));
      }

      if (this.sname != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)8), this.sname.getRealm().asn1Encode()));
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)9), this.sname.asn1Encode()));
      }

      if (this.caddr != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)10), this.caddr.asn1Encode()));
      }

      DerValue[] var2 = new DerValue[var1.size()];
      var1.copyInto(var2);
      DerOutputStream var3 = new DerOutputStream();
      var3.putSequence(var2);
      return var3.toByteArray();
   }

   public Object clone() {
      KrbCredInfo var1 = new KrbCredInfo();
      var1.key = (EncryptionKey)this.key.clone();
      if (this.pname != null) {
         var1.pname = (PrincipalName)this.pname.clone();
      }

      if (this.flags != null) {
         var1.flags = (TicketFlags)this.flags.clone();
      }

      var1.authtime = this.authtime;
      var1.starttime = this.starttime;
      var1.endtime = this.endtime;
      var1.renewTill = this.renewTill;
      if (this.sname != null) {
         var1.sname = (PrincipalName)this.sname.clone();
      }

      if (this.caddr != null) {
         var1.caddr = (HostAddresses)this.caddr.clone();
      }

      return var1;
   }
}
