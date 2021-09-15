package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Config;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.util.KerberosFlags;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class KDCOptions extends KerberosFlags {
   private static final int KDC_OPT_PROXIABLE = 268435456;
   private static final int KDC_OPT_RENEWABLE_OK = 16;
   private static final int KDC_OPT_FORWARDABLE = 1073741824;
   public static final int RESERVED = 0;
   public static final int FORWARDABLE = 1;
   public static final int FORWARDED = 2;
   public static final int PROXIABLE = 3;
   public static final int PROXY = 4;
   public static final int ALLOW_POSTDATE = 5;
   public static final int POSTDATED = 6;
   public static final int UNUSED7 = 7;
   public static final int RENEWABLE = 8;
   public static final int UNUSED9 = 9;
   public static final int UNUSED10 = 10;
   public static final int UNUSED11 = 11;
   public static final int CNAME_IN_ADDL_TKT = 14;
   public static final int RENEWABLE_OK = 27;
   public static final int ENC_TKT_IN_SKEY = 28;
   public static final int RENEW = 30;
   public static final int VALIDATE = 31;
   private static final String[] names = new String[]{"RESERVED", "FORWARDABLE", "FORWARDED", "PROXIABLE", "PROXY", "ALLOW_POSTDATE", "POSTDATED", "UNUSED7", "RENEWABLE", "UNUSED9", "UNUSED10", "UNUSED11", null, null, "CNAME_IN_ADDL_TKT", null, null, null, null, null, null, null, null, null, null, null, null, "RENEWABLE_OK", "ENC_TKT_IN_SKEY", null, "RENEW", "VALIDATE"};
   private boolean DEBUG;

   public static KDCOptions with(int... var0) {
      KDCOptions var1 = new KDCOptions();
      int[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1.set(var5, true);
      }

      return var1;
   }

   public KDCOptions() {
      super(32);
      this.DEBUG = Krb5.DEBUG;
      this.setDefault();
   }

   public KDCOptions(int var1, byte[] var2) throws Asn1Exception {
      super(var1, var2);
      this.DEBUG = Krb5.DEBUG;
      if (var1 > var2.length * 8 || var1 > 32) {
         throw new Asn1Exception(502);
      }
   }

   public KDCOptions(boolean[] var1) throws Asn1Exception {
      super(var1);
      this.DEBUG = Krb5.DEBUG;
      if (var1.length > 32) {
         throw new Asn1Exception(502);
      }
   }

   public KDCOptions(DerValue var1) throws Asn1Exception, IOException {
      this(var1.getUnalignedBitString(true).toBooleanArray());
   }

   public KDCOptions(byte[] var1) {
      super(var1.length * 8, var1);
      this.DEBUG = Krb5.DEBUG;
   }

   public static KDCOptions parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new KDCOptions(var4);
         }
      }
   }

   public void set(int var1, boolean var2) throws ArrayIndexOutOfBoundsException {
      super.set(var1, var2);
   }

   public boolean get(int var1) throws ArrayIndexOutOfBoundsException {
      return super.get(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("KDCOptions: ");

      for(int var2 = 0; var2 < 32; ++var2) {
         if (this.get(var2)) {
            if (names[var2] != null) {
               var1.append(names[var2]).append(",");
            } else {
               var1.append(var2).append(",");
            }
         }
      }

      return var1.toString();
   }

   private void setDefault() {
      try {
         Config var1 = Config.getInstance();
         int var2 = var1.getIntValue("libdefaults", "kdc_default_options");
         if ((var2 & 16) == 16) {
            this.set(27, true);
         } else if (var1.getBooleanValue("libdefaults", "renewable")) {
            this.set(27, true);
         }

         if ((var2 & 268435456) == 268435456) {
            this.set(3, true);
         } else if (var1.getBooleanValue("libdefaults", "proxiable")) {
            this.set(3, true);
         }

         if ((var2 & 1073741824) == 1073741824) {
            this.set(1, true);
         } else if (var1.getBooleanValue("libdefaults", "forwardable")) {
            this.set(1, true);
         }
      } catch (KrbException var3) {
         if (this.DEBUG) {
            System.out.println("Exception in getting default values for KDC Options from the configuration ");
            var3.printStackTrace();
         }
      }

   }
}
