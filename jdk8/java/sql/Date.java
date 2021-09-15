package java.sql;

import java.time.Instant;
import java.time.LocalDate;

public class Date extends java.util.Date {
   static final long serialVersionUID = 1511598038487230103L;

   /** @deprecated */
   @Deprecated
   public Date(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public Date(long var1) {
      super(var1);
   }

   public void setTime(long var1) {
      super.setTime(var1);
   }

   public static Date valueOf(String var0) {
      Date var8 = null;
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else {
         int var6 = var0.indexOf(45);
         int var7 = var0.indexOf(45, var6 + 1);
         if (var6 > 0 && var7 > 0 && var7 < var0.length() - 1) {
            String var9 = var0.substring(0, var6);
            String var10 = var0.substring(var6 + 1, var7);
            String var11 = var0.substring(var7 + 1);
            if (var9.length() == 4 && var10.length() >= 1 && var10.length() <= 2 && var11.length() >= 1 && var11.length() <= 2) {
               int var12 = Integer.parseInt(var9);
               int var13 = Integer.parseInt(var10);
               int var14 = Integer.parseInt(var11);
               if (var13 >= 1 && var13 <= 12 && var14 >= 1 && var14 <= 31) {
                  var8 = new Date(var12 - 1900, var13 - 1, var14);
               }
            }
         }

         if (var8 == null) {
            throw new IllegalArgumentException();
         } else {
            return var8;
         }
      }
   }

   public String toString() {
      int var1 = super.getYear() + 1900;
      int var2 = super.getMonth() + 1;
      int var3 = super.getDate();
      char[] var4 = "2000-00-00".toCharArray();
      var4[0] = Character.forDigit(var1 / 1000, 10);
      var4[1] = Character.forDigit(var1 / 100 % 10, 10);
      var4[2] = Character.forDigit(var1 / 10 % 10, 10);
      var4[3] = Character.forDigit(var1 % 10, 10);
      var4[5] = Character.forDigit(var2 / 10, 10);
      var4[6] = Character.forDigit(var2 % 10, 10);
      var4[8] = Character.forDigit(var3 / 10, 10);
      var4[9] = Character.forDigit(var3 % 10, 10);
      return new String(var4);
   }

   /** @deprecated */
   @Deprecated
   public int getHours() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public int getMinutes() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public int getSeconds() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public void setHours(int var1) {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public void setMinutes(int var1) {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public void setSeconds(int var1) {
      throw new IllegalArgumentException();
   }

   public static Date valueOf(LocalDate var0) {
      return new Date(var0.getYear() - 1900, var0.getMonthValue() - 1, var0.getDayOfMonth());
   }

   public LocalDate toLocalDate() {
      return LocalDate.of(this.getYear() + 1900, this.getMonth() + 1, this.getDate());
   }

   public Instant toInstant() {
      throw new UnsupportedOperationException();
   }
}
