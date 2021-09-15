package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Ticket implements Cloneable {
   public int tkt_vno;
   public PrincipalName sname;
   public EncryptedData encPart;

   private Ticket() {
   }

   public Object clone() {
      Ticket var1 = new Ticket();
      var1.sname = (PrincipalName)this.sname.clone();
      var1.encPart = (EncryptedData)this.encPart.clone();
      var1.tkt_vno = this.tkt_vno;
      return var1;
   }

   public Ticket(PrincipalName var1, EncryptedData var2) {
      this.tkt_vno = 5;
      this.sname = var1;
      this.encPart = var2;
   }

   public Ticket(byte[] var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(new DerValue(var1));
   }

   public Ticket(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      if ((var1.getTag() & 31) == 1 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) != 0) {
               throw new Asn1Exception(906);
            } else {
               this.tkt_vno = var3.getData().getBigInteger().intValue();
               if (this.tkt_vno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  Realm var4 = Realm.parse(var2.getData(), (byte)1, false);
                  this.sname = PrincipalName.parse(var2.getData(), (byte)2, false, var4);
                  this.encPart = EncryptedData.parse(var2.getData(), (byte)3, false);
                  if (var2.getData().available() > 0) {
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
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      DerValue[] var3 = new DerValue[4];
      var2.putInteger(BigInteger.valueOf((long)this.tkt_vno));
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), this.sname.getRealm().asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)2), this.sname.asn1Encode());
      var1.write(DerValue.createTag((byte)-128, true, (byte)3), this.encPart.asn1Encode());
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      DerOutputStream var4 = new DerOutputStream();
      var4.write(DerValue.createTag((byte)64, true, (byte)1), var2);
      return var4.toByteArray();
   }

   public static Ticket parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException, RealmException, KrbApErrException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new Ticket(var4);
         }
      }
   }
}
