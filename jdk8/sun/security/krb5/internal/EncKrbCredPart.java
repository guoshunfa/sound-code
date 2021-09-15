package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.RealmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKrbCredPart {
   public KrbCredInfo[] ticketInfo = null;
   public KerberosTime timeStamp;
   private Integer nonce;
   private Integer usec;
   private HostAddress sAddress;
   private HostAddresses rAddress;

   public EncKrbCredPart(KrbCredInfo[] var1, KerberosTime var2, Integer var3, Integer var4, HostAddress var5, HostAddresses var6) throws IOException {
      if (var1 != null) {
         this.ticketInfo = new KrbCredInfo[var1.length];

         for(int var7 = 0; var7 < var1.length; ++var7) {
            if (var1[var7] == null) {
               throw new IOException("Cannot create a EncKrbCredPart");
            }

            this.ticketInfo[var7] = (KrbCredInfo)var1[var7].clone();
         }
      }

      this.timeStamp = var2;
      this.usec = var3;
      this.nonce = var4;
      this.sAddress = var5;
      this.rAddress = var6;
   }

   public EncKrbCredPart(byte[] var1) throws Asn1Exception, IOException, RealmException {
      this.init(new DerValue(var1));
   }

   public EncKrbCredPart(DerValue var1) throws Asn1Exception, IOException, RealmException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, RealmException {
      this.nonce = null;
      this.timeStamp = null;
      this.usec = null;
      this.sAddress = null;
      this.rAddress = null;
      if ((var1.getTag() & 31) == 29 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) != 0) {
               throw new Asn1Exception(906);
            } else {
               DerValue[] var4 = var3.getData().getSequence(1);
               this.ticketInfo = new KrbCredInfo[var4.length];

               for(int var5 = 0; var5 < var4.length; ++var5) {
                  this.ticketInfo[var5] = new KrbCredInfo(var4[var5]);
               }

               if (var2.getData().available() > 0 && ((byte)var2.getData().peekByte() & 31) == 1) {
                  var3 = var2.getData().getDerValue();
                  this.nonce = new Integer(var3.getData().getBigInteger().intValue());
               }

               if (var2.getData().available() > 0) {
                  this.timeStamp = KerberosTime.parse(var2.getData(), (byte)2, true);
               }

               if (var2.getData().available() > 0 && ((byte)var2.getData().peekByte() & 31) == 3) {
                  var3 = var2.getData().getDerValue();
                  this.usec = new Integer(var3.getData().getBigInteger().intValue());
               }

               if (var2.getData().available() > 0) {
                  this.sAddress = HostAddress.parse(var2.getData(), (byte)4, true);
               }

               if (var2.getData().available() > 0) {
                  this.rAddress = HostAddresses.parse(var2.getData(), (byte)5, true);
               }

               if (var2.getData().available() > 0) {
                  throw new Asn1Exception(906);
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
      DerValue[] var3 = new DerValue[this.ticketInfo.length];

      for(int var4 = 0; var4 < this.ticketInfo.length; ++var4) {
         var3[var4] = new DerValue(this.ticketInfo[var4].asn1Encode());
      }

      var2.putSequence(var3);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      if (this.nonce != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf((long)this.nonce));
         var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      }

      if (this.timeStamp != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)2), this.timeStamp.asn1Encode());
      }

      if (this.usec != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf((long)this.usec));
         var1.write(DerValue.createTag((byte)-128, true, (byte)3), var2);
      }

      if (this.sAddress != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)4), this.sAddress.asn1Encode());
      }

      if (this.rAddress != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)5), this.rAddress.asn1Encode());
      }

      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      var1 = new DerOutputStream();
      var1.write(DerValue.createTag((byte)64, true, (byte)29), var2);
      return var1.toByteArray();
   }
}
