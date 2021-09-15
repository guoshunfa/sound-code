package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncKrbPrivPart {
   public byte[] userData = null;
   public KerberosTime timestamp;
   public Integer usec;
   public Integer seqNumber;
   public HostAddress sAddress;
   public HostAddress rAddress;

   public EncKrbPrivPart(byte[] var1, KerberosTime var2, Integer var3, Integer var4, HostAddress var5, HostAddress var6) {
      if (var1 != null) {
         this.userData = (byte[])var1.clone();
      }

      this.timestamp = var2;
      this.usec = var3;
      this.seqNumber = var4;
      this.sAddress = var5;
      this.rAddress = var6;
   }

   public EncKrbPrivPart(byte[] var1) throws Asn1Exception, IOException {
      this.init(new DerValue(var1));
   }

   public EncKrbPrivPart(DerValue var1) throws Asn1Exception, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException {
      if ((var1.getTag() & 31) == 28 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) == 0) {
               this.userData = var3.getData().getOctetString();
               this.timestamp = KerberosTime.parse(var2.getData(), (byte)1, true);
               if ((var2.getData().peekByte() & 31) == 2) {
                  var3 = var2.getData().getDerValue();
                  this.usec = new Integer(var3.getData().getBigInteger().intValue());
               } else {
                  this.usec = null;
               }

               if ((var2.getData().peekByte() & 31) == 3) {
                  var3 = var2.getData().getDerValue();
                  this.seqNumber = new Integer(var3.getData().getBigInteger().intValue());
               } else {
                  this.seqNumber = null;
               }

               this.sAddress = HostAddress.parse(var2.getData(), (byte)4, false);
               if (var2.getData().available() > 0) {
                  this.rAddress = HostAddress.parse(var2.getData(), (byte)5, true);
               }

               if (var2.getData().available() > 0) {
                  throw new Asn1Exception(906);
               }
            } else {
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
      var1.putOctetString(this.userData);
      var2.write(DerValue.createTag((byte)-128, true, (byte)0), var1);
      if (this.timestamp != null) {
         var2.write(DerValue.createTag((byte)-128, true, (byte)1), this.timestamp.asn1Encode());
      }

      if (this.usec != null) {
         var1 = new DerOutputStream();
         var1.putInteger(BigInteger.valueOf((long)this.usec));
         var2.write(DerValue.createTag((byte)-128, true, (byte)2), var1);
      }

      if (this.seqNumber != null) {
         var1 = new DerOutputStream();
         var1.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
         var2.write(DerValue.createTag((byte)-128, true, (byte)3), var1);
      }

      var2.write(DerValue.createTag((byte)-128, true, (byte)4), this.sAddress.asn1Encode());
      if (this.rAddress != null) {
         var2.write(DerValue.createTag((byte)-128, true, (byte)5), this.rAddress.asn1Encode());
      }

      var1 = new DerOutputStream();
      var1.write((byte)48, (DerOutputStream)var2);
      var2 = new DerOutputStream();
      var2.write(DerValue.createTag((byte)64, true, (byte)28), var1);
      return var2.toByteArray();
   }
}
