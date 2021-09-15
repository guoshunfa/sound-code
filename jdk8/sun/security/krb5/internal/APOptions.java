package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class APOptions extends KerberosFlags {
   public APOptions() {
      super(32);
   }

   public APOptions(int var1) throws Asn1Exception {
      super(32);
      this.set(var1, true);
   }

   public APOptions(int var1, byte[] var2) throws Asn1Exception {
      super(var1, var2);
      if (var1 > var2.length * 8 || var1 > 32) {
         throw new Asn1Exception(502);
      }
   }

   public APOptions(boolean[] var1) throws Asn1Exception {
      super(var1);
      if (var1.length > 32) {
         throw new Asn1Exception(502);
      }
   }

   public APOptions(DerValue var1) throws IOException, Asn1Exception {
      this(var1.getUnalignedBitString(true).toBooleanArray());
   }

   public static APOptions parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new APOptions(var4);
         }
      }
   }
}
