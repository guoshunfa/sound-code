package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCRep {
   public PrincipalName cname;
   public Ticket ticket;
   public EncryptedData encPart;
   public EncKDCRepPart encKDCRepPart;
   private int pvno;
   private int msgType;
   public PAData[] pAData = null;
   private boolean DEBUG;

   public KDCRep(PAData[] var1, PrincipalName var2, Ticket var3, EncryptedData var4, int var5) throws IOException {
      this.DEBUG = Krb5.DEBUG;
      this.pvno = 5;
      this.msgType = var5;
      if (var1 != null) {
         this.pAData = new PAData[var1.length];

         for(int var6 = 0; var6 < var1.length; ++var6) {
            if (var1[var6] == null) {
               throw new IOException("Cannot create a KDCRep");
            }

            this.pAData[var6] = (PAData)var1[var6].clone();
         }
      }

      this.cname = var2;
      this.ticket = var3;
      this.encPart = var4;
   }

   public KDCRep() {
      this.DEBUG = Krb5.DEBUG;
   }

   public KDCRep(byte[] var1, int var2) throws Asn1Exception, KrbApErrException, RealmException, IOException {
      this.DEBUG = Krb5.DEBUG;
      this.init(new DerValue(var1), var2);
   }

   public KDCRep(DerValue var1, int var2) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.DEBUG = Krb5.DEBUG;
      this.init(var1, var2);
   }

   protected void init(DerValue var1, int var2) throws Asn1Exception, RealmException, IOException, KrbApErrException {
      if ((var1.getTag() & 31) != var2) {
         if (this.DEBUG) {
            System.out.println(">>> KDCRep: init() encoding tag is " + var1.getTag() + " req type is " + var2);
         }

         throw new Asn1Exception(906);
      } else {
         DerValue var3 = var1.getData().getDerValue();
         if (var3.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            if ((var4.getTag() & 31) == 0) {
               this.pvno = var4.getData().getBigInteger().intValue();
               if (this.pvno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  var4 = var3.getData().getDerValue();
                  if ((var4.getTag() & 31) == 1) {
                     this.msgType = var4.getData().getBigInteger().intValue();
                     if (this.msgType != var2) {
                        throw new KrbApErrException(40);
                     } else {
                        if ((var3.getData().peekByte() & 31) == 2) {
                           var4 = var3.getData().getDerValue();
                           DerValue[] var5 = var4.getData().getSequence(1);
                           this.pAData = new PAData[var5.length];

                           for(int var6 = 0; var6 < var5.length; ++var6) {
                              this.pAData[var6] = new PAData(var5[var6]);
                           }
                        } else {
                           this.pAData = null;
                        }

                        Realm var7 = Realm.parse(var3.getData(), (byte)3, false);
                        this.cname = PrincipalName.parse(var3.getData(), (byte)4, false, var7);
                        this.ticket = Ticket.parse(var3.getData(), (byte)5, false);
                        this.encPart = EncryptedData.parse(var3.getData(), (byte)6, false);
                        if (var3.getData().available() > 0) {
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
      if (this.pAData != null && this.pAData.length > 0) {
         DerOutputStream var3 = new DerOutputStream();

         for(int var4 = 0; var4 < this.pAData.length; ++var4) {
            var3.write(this.pAData[var4].asn1Encode());
         }

         var2 = new DerOutputStream();
         var2.write((byte)48, (DerOutputStream)var3);
         var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      }

      var1.write(DerValue.createTag((byte)-128, true, (byte)3), this.cname.getRealm().asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)4), this.cname.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)5), this.ticket.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)6), this.encPart.asn1Encode());
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }
}
