package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Config;
import sun.security.krb5.KrbException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KerberosTime {
   private final long kerberosTime;
   private final int microSeconds;
   private static long initMilli = System.currentTimeMillis();
   private static long initMicro = System.nanoTime() / 1000L;
   private static boolean DEBUG;

   private KerberosTime(long var1, int var3) {
      this.kerberosTime = var1;
      this.microSeconds = var3;
   }

   public KerberosTime(long var1) {
      this(var1, 0);
   }

   public KerberosTime(String var1) throws Asn1Exception {
      this(toKerberosTime(var1), 0);
   }

   private static long toKerberosTime(String var0) throws Asn1Exception {
      if (var0.length() != 15) {
         throw new Asn1Exception(900);
      } else if (var0.charAt(14) != 'Z') {
         throw new Asn1Exception(900);
      } else {
         int var1 = Integer.parseInt(var0.substring(0, 4));
         Calendar var2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         var2.clear();
         var2.set(var1, Integer.parseInt(var0.substring(4, 6)) - 1, Integer.parseInt(var0.substring(6, 8)), Integer.parseInt(var0.substring(8, 10)), Integer.parseInt(var0.substring(10, 12)), Integer.parseInt(var0.substring(12, 14)));
         return var2.getTimeInMillis();
      }
   }

   public KerberosTime(Date var1) {
      this(var1.getTime(), 0);
   }

   public static KerberosTime now() {
      long var0 = System.currentTimeMillis();
      long var2 = System.nanoTime() / 1000L;
      long var4 = var2 - initMicro;
      long var6 = initMilli + var4 / 1000L;
      if (var6 - var0 <= 100L && var0 - var6 <= 100L) {
         return new KerberosTime(var6, (int)(var4 % 1000L));
      } else {
         if (DEBUG) {
            System.out.println("System time adjusted");
         }

         initMilli = var0;
         initMicro = var2;
         return new KerberosTime(var0, 0);
      }
   }

   public String toGeneralizedTimeString() {
      Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      var1.clear();
      var1.setTimeInMillis(this.kerberosTime);
      return String.format("%04d%02d%02d%02d%02d%02dZ", var1.get(1), var1.get(2) + 1, var1.get(5), var1.get(11), var1.get(12), var1.get(13));
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putGeneralizedTime(this.toDate());
      return var1.toByteArray();
   }

   public long getTime() {
      return this.kerberosTime;
   }

   public Date toDate() {
      return new Date(this.kerberosTime);
   }

   public int getMicroSeconds() {
      Long var1 = new Long(this.kerberosTime % 1000L * 1000L);
      return var1.intValue() + this.microSeconds;
   }

   public KerberosTime withMicroSeconds(int var1) {
      return new KerberosTime(this.kerberosTime - this.kerberosTime % 1000L + (long)var1 / 1000L, var1 % 1000);
   }

   private boolean inClockSkew(int var1) {
      return Math.abs(this.kerberosTime - System.currentTimeMillis()) <= (long)var1 * 1000L;
   }

   public boolean inClockSkew() {
      return this.inClockSkew(getDefaultSkew());
   }

   public boolean greaterThanWRTClockSkew(KerberosTime var1, int var2) {
      return this.kerberosTime - var1.kerberosTime > (long)var2 * 1000L;
   }

   public boolean greaterThanWRTClockSkew(KerberosTime var1) {
      return this.greaterThanWRTClockSkew(var1, getDefaultSkew());
   }

   public boolean greaterThan(KerberosTime var1) {
      return this.kerberosTime > var1.kerberosTime || this.kerberosTime == var1.kerberosTime && this.microSeconds > var1.microSeconds;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KerberosTime)) {
         return false;
      } else {
         return this.kerberosTime == ((KerberosTime)var1).kerberosTime && this.microSeconds == ((KerberosTime)var1).microSeconds;
      }
   }

   public int hashCode() {
      int var1 = 629 + (int)(this.kerberosTime ^ this.kerberosTime >>> 32);
      return var1 * 17 + this.microSeconds;
   }

   public boolean isZero() {
      return this.kerberosTime == 0L && this.microSeconds == 0;
   }

   public int getSeconds() {
      Long var1 = new Long(this.kerberosTime / 1000L);
      return var1.intValue();
   }

   public static KerberosTime parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            Date var5 = var4.getGeneralizedTime();
            return new KerberosTime(var5.getTime(), 0);
         }
      }
   }

   public static int getDefaultSkew() {
      int var0 = 300;

      try {
         if ((var0 = Config.getInstance().getIntValue("libdefaults", "clockskew")) == Integer.MIN_VALUE) {
            var0 = 300;
         }
      } catch (KrbException var2) {
         if (DEBUG) {
            System.out.println("Exception in getting clockskew from Configuration using default value " + var2.getMessage());
         }
      }

      return var0;
   }

   public String toString() {
      return this.toGeneralizedTimeString();
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
