package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBSafeBody {
   public byte[] userData = null;
   public KerberosTime timestamp;
   public Integer usec;
   public Integer seqNumber;
   public HostAddress sAddress;
   public HostAddress rAddress;

   public KRBSafeBody(byte[] var1, KerberosTime var2, Integer var3, Integer var4, HostAddress var5, HostAddress var6) {
      if (var1 != null) {
         this.userData = (byte[])var1.clone();
      }

      this.timestamp = var2;
      this.usec = var3;
      this.seqNumber = var4;
      this.sAddress = var5;
      this.rAddress = var6;
   }

   public KRBSafeBody(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.userData = var2.getData().getOctetString();
            this.timestamp = KerberosTime.parse(var1.getData(), (byte)1, true);
            if ((var1.getData().peekByte() & 31) == 2) {
               var2 = var1.getData().getDerValue();
               this.usec = new Integer(var2.getData().getBigInteger().intValue());
            }

            if ((var1.getData().peekByte() & 31) == 3) {
               var2 = var1.getData().getDerValue();
               this.seqNumber = new Integer(var2.getData().getBigInteger().intValue());
            }

            this.sAddress = HostAddress.parse(var1.getData(), (byte)4, false);
            if (var1.getData().available() > 0) {
               this.rAddress = HostAddress.parse(var1.getData(), (byte)5, true);
            }

            if (var1.getData().available() > 0) {
               throw new Asn1Exception(906);
            }
         } else {
            throw new Asn1Exception(906);
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putOctetString(this.userData);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      if (this.timestamp != null) {
         var1.write(DerValue.createTag((byte)-128, true, (byte)1), this.timestamp.asn1Encode());
      }

      if (this.usec != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf((long)this.usec));
         var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      }

      if (this.seqNumber != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
         var1.write(DerValue.createTag((byte)-128, true, (byte)3), var2);
      }

      var1.write(DerValue.createTag((byte)-128, true, (byte)4), this.sAddress.asn1Encode());
      if (this.rAddress != null) {
         var2 = new DerOutputStream();
      }

      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public static KRBSafeBody parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new KRBSafeBody(var4);
         }
      }
   }
}
