package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class TransitedEncoding {
   public int trType;
   public byte[] contents;

   public TransitedEncoding(int var1, byte[] var2) {
      this.trType = var1;
      this.contents = var2;
   }

   public TransitedEncoding(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.trType = var2.getData().getBigInteger().intValue();
            var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) == 1) {
               this.contents = var2.getData().getOctetString();
               if (var2.getData().available() > 0) {
                  throw new Asn1Exception(906);
               }
            } else {
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
      var2.putInteger(BigInteger.valueOf((long)this.trType));
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      var2.putOctetString(this.contents);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public static TransitedEncoding parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new TransitedEncoding(var4);
         }
      }
   }
}
