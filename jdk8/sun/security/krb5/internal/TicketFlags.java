package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class TicketFlags extends KerberosFlags {
   public TicketFlags() {
      super(32);
   }

   public TicketFlags(boolean[] var1) throws Asn1Exception {
      super(var1);
      if (var1.length > 32) {
         throw new Asn1Exception(502);
      }
   }

   public TicketFlags(int var1, byte[] var2) throws Asn1Exception {
      super(var1, var2);
      if (var1 > var2.length * 8 || var1 > 32) {
         throw new Asn1Exception(502);
      }
   }

   public TicketFlags(DerValue var1) throws IOException, Asn1Exception {
      this(var1.getUnalignedBitString(true).toBooleanArray());
   }

   public static TicketFlags parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new TicketFlags(var4);
         }
      }
   }

   public Object clone() {
      try {
         return new TicketFlags(this.toBooleanArray());
      } catch (Exception var2) {
         return null;
      }
   }

   public boolean match(LoginOptions var1) {
      boolean var2 = false;
      if (this.get(1) == var1.get(1) && this.get(3) == var1.get(3) && this.get(8) == var1.get(8)) {
         var2 = true;
      }

      return var2;
   }

   public boolean match(TicketFlags var1) {
      boolean var2 = true;

      for(int var3 = 0; var3 <= 31; ++var3) {
         if (this.get(var3) != var1.get(var3)) {
            return false;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      boolean[] var2 = this.toBooleanArray();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3]) {
            switch(var3) {
            case 0:
               var1.append("RESERVED;");
               break;
            case 1:
               var1.append("FORWARDABLE;");
               break;
            case 2:
               var1.append("FORWARDED;");
               break;
            case 3:
               var1.append("PROXIABLE;");
               break;
            case 4:
               var1.append("PROXY;");
               break;
            case 5:
               var1.append("MAY-POSTDATE;");
               break;
            case 6:
               var1.append("POSTDATED;");
               break;
            case 7:
               var1.append("INVALID;");
               break;
            case 8:
               var1.append("RENEWABLE;");
               break;
            case 9:
               var1.append("INITIAL;");
               break;
            case 10:
               var1.append("PRE-AUTHENT;");
               break;
            case 11:
               var1.append("HW-AUTHENT;");
            }
         }
      }

      String var4 = var1.toString();
      if (var4.length() > 0) {
         var4 = var4.substring(0, var4.length() - 1);
      }

      return var4;
   }
}
