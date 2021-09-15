package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Authenticator {
   public int authenticator_vno;
   public PrincipalName cname;
   Checksum cksum;
   public int cusec;
   public KerberosTime ctime;
   EncryptionKey subKey;
   Integer seqNumber;
   public AuthorizationData authorizationData;

   public Authenticator(PrincipalName var1, Checksum var2, int var3, KerberosTime var4, EncryptionKey var5, Integer var6, AuthorizationData var7) {
      this.authenticator_vno = 5;
      this.cname = var1;
      this.cksum = var2;
      this.cusec = var3;
      this.ctime = var4;
      this.subKey = var5;
      this.seqNumber = var6;
      this.authorizationData = var7;
   }

   public Authenticator(byte[] var1) throws Asn1Exception, IOException, KrbApErrException, RealmException {
      this.init(new DerValue(var1));
   }

   public Authenticator(DerValue var1) throws Asn1Exception, IOException, KrbApErrException, RealmException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, KrbApErrException, RealmException {
      if ((var1.getTag() & 31) == 2 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) != 0) {
               throw new Asn1Exception(906);
            } else {
               this.authenticator_vno = var3.getData().getBigInteger().intValue();
               if (this.authenticator_vno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  Realm var4 = Realm.parse(var2.getData(), (byte)1, false);
                  this.cname = PrincipalName.parse(var2.getData(), (byte)2, false, var4);
                  this.cksum = Checksum.parse(var2.getData(), (byte)3, true);
                  var3 = var2.getData().getDerValue();
                  if ((var3.getTag() & 31) == 4) {
                     this.cusec = var3.getData().getBigInteger().intValue();
                     this.ctime = KerberosTime.parse(var2.getData(), (byte)5, false);
                     if (var2.getData().available() > 0) {
                        this.subKey = EncryptionKey.parse(var2.getData(), (byte)6, true);
                     } else {
                        this.subKey = null;
                        this.seqNumber = null;
                        this.authorizationData = null;
                     }

                     if (var2.getData().available() > 0) {
                        if ((var2.getData().peekByte() & 31) == 7) {
                           var3 = var2.getData().getDerValue();
                           if ((var3.getTag() & 31) == 7) {
                              this.seqNumber = new Integer(var3.getData().getBigInteger().intValue());
                           }
                        }
                     } else {
                        this.seqNumber = null;
                        this.authorizationData = null;
                     }

                     if (var2.getData().available() > 0) {
                        this.authorizationData = AuthorizationData.parse(var2.getData(), (byte)8, true);
                     } else {
                        this.authorizationData = null;
                     }

                     if (var2.getData().available() > 0) {
                        throw new Asn1Exception(906);
                     }
                  } else {
                     throw new Asn1Exception(906);
                  }
               }
            }
         }
      } else {
         throw new Asn1Exception(906);
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      Vector var1 = new Vector();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(BigInteger.valueOf((long)this.authenticator_vno));
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)0), var2.toByteArray()));
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)1), this.cname.getRealm().asn1Encode()));
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)2), this.cname.asn1Encode()));
      if (this.cksum != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)3), this.cksum.asn1Encode()));
      }

      var2 = new DerOutputStream();
      var2.putInteger(BigInteger.valueOf((long)this.cusec));
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)4), var2.toByteArray()));
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)5), this.ctime.asn1Encode()));
      if (this.subKey != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)6), this.subKey.asn1Encode()));
      }

      if (this.seqNumber != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)7), var2.toByteArray()));
      }

      if (this.authorizationData != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)8), this.authorizationData.asn1Encode()));
      }

      DerValue[] var3 = new DerValue[var1.size()];
      var1.copyInto(var3);
      var2 = new DerOutputStream();
      var2.putSequence(var3);
      DerOutputStream var4 = new DerOutputStream();
      var4.write(DerValue.createTag((byte)64, true, (byte)2), var2);
      return var4.toByteArray();
   }

   public final Checksum getChecksum() {
      return this.cksum;
   }

   public final Integer getSeqNumber() {
      return this.seqNumber;
   }

   public final EncryptionKey getSubKey() {
      return this.subKey;
   }
}
