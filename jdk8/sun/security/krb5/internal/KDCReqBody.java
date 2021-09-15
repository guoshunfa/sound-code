package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCReqBody {
   public KDCOptions kdcOptions;
   public PrincipalName cname;
   public PrincipalName sname;
   public KerberosTime from;
   public KerberosTime till;
   public KerberosTime rtime;
   public HostAddresses addresses;
   private int nonce;
   private int[] eType = null;
   private EncryptedData encAuthorizationData;
   private Ticket[] additionalTickets;

   public KDCReqBody(KDCOptions var1, PrincipalName var2, PrincipalName var3, KerberosTime var4, KerberosTime var5, KerberosTime var6, int var7, int[] var8, HostAddresses var9, EncryptedData var10, Ticket[] var11) throws IOException {
      this.kdcOptions = var1;
      this.cname = var2;
      this.sname = var3;
      this.from = var4;
      this.till = var5;
      this.rtime = var6;
      this.nonce = var7;
      if (var8 != null) {
         this.eType = (int[])var8.clone();
      }

      this.addresses = var9;
      this.encAuthorizationData = var10;
      if (var11 != null) {
         this.additionalTickets = new Ticket[var11.length];

         for(int var12 = 0; var12 < var11.length; ++var12) {
            if (var11[var12] == null) {
               throw new IOException("Cannot create a KDCReqBody");
            }

            this.additionalTickets[var12] = (Ticket)var11[var12].clone();
         }
      }

   }

   public KDCReqBody(DerValue var1, int var2) throws Asn1Exception, RealmException, KrbException, IOException {
      this.addresses = null;
      this.encAuthorizationData = null;
      this.additionalTickets = null;
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         this.kdcOptions = KDCOptions.parse(var1.getData(), (byte)0, false);
         this.cname = PrincipalName.parse(var1.getData(), (byte)1, true, new Realm("PLACEHOLDER"));
         if (var2 != 10 && this.cname != null) {
            throw new Asn1Exception(906);
         } else {
            Realm var5 = Realm.parse(var1.getData(), (byte)2, false);
            if (this.cname != null) {
               this.cname = new PrincipalName(this.cname.getNameType(), this.cname.getNameStrings(), var5);
            }

            this.sname = PrincipalName.parse(var1.getData(), (byte)3, true, var5);
            this.from = KerberosTime.parse(var1.getData(), (byte)4, true);
            this.till = KerberosTime.parse(var1.getData(), (byte)5, false);
            this.rtime = KerberosTime.parse(var1.getData(), (byte)6, true);
            DerValue var3 = var1.getData().getDerValue();
            if ((var3.getTag() & 31) != 7) {
               throw new Asn1Exception(906);
            } else {
               this.nonce = var3.getData().getBigInteger().intValue();
               var3 = var1.getData().getDerValue();
               Vector var6 = new Vector();
               if ((var3.getTag() & 31) != 8) {
                  throw new Asn1Exception(906);
               } else {
                  DerValue var4 = var3.getData().getDerValue();
                  if (var4.getTag() != 48) {
                     throw new Asn1Exception(906);
                  } else {
                     while(var4.getData().available() > 0) {
                        var6.addElement(var4.getData().getBigInteger().intValue());
                     }

                     this.eType = new int[var6.size()];

                     for(int var7 = 0; var7 < var6.size(); ++var7) {
                        this.eType[var7] = (Integer)var6.elementAt(var7);
                     }

                     if (var1.getData().available() > 0) {
                        this.addresses = HostAddresses.parse(var1.getData(), (byte)9, true);
                     }

                     if (var1.getData().available() > 0) {
                        this.encAuthorizationData = EncryptedData.parse(var1.getData(), (byte)10, true);
                     }

                     if (var1.getData().available() > 0) {
                        Vector var8 = new Vector();
                        var3 = var1.getData().getDerValue();
                        if ((var3.getTag() & 31) != 11) {
                           throw new Asn1Exception(906);
                        }

                        var4 = var3.getData().getDerValue();
                        if (var4.getTag() != 48) {
                           throw new Asn1Exception(906);
                        }

                        while(var4.getData().available() > 0) {
                           var8.addElement(new Ticket(var4.getData().getDerValue()));
                        }

                        if (var8.size() > 0) {
                           this.additionalTickets = new Ticket[var8.size()];
                           var8.copyInto(this.additionalTickets);
                        }
                     }

                     if (var1.getData().available() > 0) {
                        throw new Asn1Exception(906);
                     }
                  }
               }
            }
         }
      }
   }

   public byte[] asn1Encode(int var1) throws Asn1Exception, IOException {
      Vector var2 = new Vector();
      var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)0), this.kdcOptions.asn1Encode()));
      if (var1 == 10 && this.cname != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)1), this.cname.asn1Encode()));
      }

      if (this.sname != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)2), this.sname.getRealm().asn1Encode()));
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)3), this.sname.asn1Encode()));
      } else if (this.cname != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)2), this.cname.getRealm().asn1Encode()));
      }

      if (this.from != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)4), this.from.asn1Encode()));
      }

      var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)5), this.till.asn1Encode()));
      if (this.rtime != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)6), this.rtime.asn1Encode()));
      }

      DerOutputStream var3 = new DerOutputStream();
      var3.putInteger(BigInteger.valueOf((long)this.nonce));
      var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)7), var3.toByteArray()));
      var3 = new DerOutputStream();

      for(int var4 = 0; var4 < this.eType.length; ++var4) {
         var3.putInteger(BigInteger.valueOf((long)this.eType[var4]));
      }

      DerOutputStream var6 = new DerOutputStream();
      var6.write((byte)48, (DerOutputStream)var3);
      var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)8), var6.toByteArray()));
      if (this.addresses != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)9), this.addresses.asn1Encode()));
      }

      if (this.encAuthorizationData != null) {
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)10), this.encAuthorizationData.asn1Encode()));
      }

      if (this.additionalTickets != null && this.additionalTickets.length > 0) {
         var3 = new DerOutputStream();

         for(int var5 = 0; var5 < this.additionalTickets.length; ++var5) {
            var3.write(this.additionalTickets[var5].asn1Encode());
         }

         DerOutputStream var7 = new DerOutputStream();
         var7.write((byte)48, (DerOutputStream)var3);
         var2.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)11), var7.toByteArray()));
      }

      DerValue[] var8 = new DerValue[var2.size()];
      var2.copyInto(var8);
      var3 = new DerOutputStream();
      var3.putSequence(var8);
      return var3.toByteArray();
   }

   public int getNonce() {
      return this.nonce;
   }
}
