package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBCred {
   public Ticket[] tickets = null;
   public EncryptedData encPart;
   private int pvno;
   private int msgType;

   public KRBCred(Ticket[] var1, EncryptedData var2) throws IOException {
      this.pvno = 5;
      this.msgType = 22;
      if (var1 != null) {
         this.tickets = new Ticket[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == null) {
               throw new IOException("Cannot create a KRBCred");
            }

            this.tickets[var3] = (Ticket)var1[var3].clone();
         }
      }

      this.encPart = var2;
   }

   public KRBCred(byte[] var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(new DerValue(var1));
   }

   public KRBCred(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      if ((var1.getTag() & 31) == 22 && var1.isApplication() && var1.isConstructed()) {
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
                     if (this.msgType != 22) {
                        throw new KrbApErrException(40);
                     } else {
                        var3 = var2.getData().getDerValue();
                        if ((var3.getTag() & 31) != 2) {
                           throw new Asn1Exception(906);
                        } else {
                           DerValue var4 = var3.getData().getDerValue();
                           if (var4.getTag() != 48) {
                              throw new Asn1Exception(906);
                           } else {
                              Vector var5 = new Vector();

                              while(var4.getData().available() > 0) {
                                 var5.addElement(new Ticket(var4.getData().getDerValue()));
                              }

                              if (var5.size() > 0) {
                                 this.tickets = new Ticket[var5.size()];
                                 var5.copyInto(this.tickets);
                              }

                              this.encPart = EncryptedData.parse(var2.getData(), (byte)3, false);
                              if (var2.getData().available() > 0) {
                                 throw new Asn1Exception(906);
                              }
                           }
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
      var1.putInteger(BigInteger.valueOf((long)this.pvno));
      DerOutputStream var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)-128, true, (byte)0), var1);
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.msgType));
      var3.write(DerValue.createTag((byte)-128, true, (byte)1), var1);
      var1 = new DerOutputStream();

      for(int var4 = 0; var4 < this.tickets.length; ++var4) {
         var1.write(this.tickets[var4].asn1Encode());
      }

      DerOutputStream var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      var3.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      var3.write(DerValue.createTag((byte)-128, true, (byte)3), this.encPart.asn1Encode());
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var3);
      var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)64, true, (byte)22), var2);
      return var3.toByteArray();
   }
}
