package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncAPRepPart {
   public KerberosTime ctime;
   public int cusec;
   EncryptionKey subKey;
   Integer seqNumber;

   public EncAPRepPart(KerberosTime var1, int var2, EncryptionKey var3, Integer var4) {
      this.ctime = var1;
      this.cusec = var2;
      this.subKey = var3;
      this.seqNumber = var4;
   }

   public EncAPRepPart(byte[] var1) throws Asn1Exception, IOException {
      this.init(new DerValue(var1));
   }

   public EncAPRepPart(DerValue var1) throws Asn1Exception, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException {
      if ((var1.getTag() & 31) == 27 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            this.ctime = KerberosTime.parse(var2.getData(), (byte)0, true);
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) == 1) {
               this.cusec = var3.getData().getBigInteger().intValue();
               if (var2.getData().available() > 0) {
                  this.subKey = EncryptionKey.parse(var2.getData(), (byte)2, true);
               } else {
                  this.subKey = null;
                  this.seqNumber = null;
               }

               if (var2.getData().available() > 0) {
                  var3 = var2.getData().getDerValue();
                  if ((var3.getTag() & 31) != 3) {
                     throw new Asn1Exception(906);
                  }

                  this.seqNumber = new Integer(var3.getData().getBigInteger().intValue());
               } else {
                  this.seqNumber = null;
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
      Vector var1 = new Vector();
      DerOutputStream var2 = new DerOutputStream();
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)0), this.ctime.asn1Encode()));
      var2.putInteger(BigInteger.valueOf((long)this.cusec));
      var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)1), var2.toByteArray()));
      if (this.subKey != null) {
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)2), this.subKey.asn1Encode()));
      }

      if (this.seqNumber != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf(this.seqNumber.longValue()));
         var1.addElement(new DerValue(DerValue.createTag((byte)-128, true, (byte)3), var2.toByteArray()));
      }

      DerValue[] var3 = new DerValue[var1.size()];
      var1.copyInto(var3);
      var2 = new DerOutputStream();
      var2.putSequence(var3);
      DerOutputStream var4 = new DerOutputStream();
      var4.write(DerValue.createTag((byte)64, true, (byte)27), var2);
      return var4.toByteArray();
   }

   public final EncryptionKey getSubKey() {
      return this.subKey;
   }

   public final Integer getSeqNumber() {
      return this.seqNumber;
   }
}
