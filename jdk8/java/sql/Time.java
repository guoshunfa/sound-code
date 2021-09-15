package java.sql;

import java.time.Instant;
import java.time.LocalTime;

public class Time extends java.util.Date {
   static final long serialVersionUID = 8397324403548013681L;

   /** @deprecated */
   @Deprecated
   public Time(int var1, int var2, int var3) {
      super(70, 0, 1, var1, var2, var3);
   }

   public Time(long var1) {
      super(var1);
   }

   public void setTime(long var1) {
      super.setTime(var1);
   }

   public static Time valueOf(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else {
         int var4 = var0.indexOf(58);
         int var5 = var0.indexOf(58, var4 + 1);
         if (var4 > 0 & var5 > 0 & var5 < var0.length() - 1) {
            int var1 = Integer.parseInt(var0.substring(0, var4));
            int var2 = Integer.parseInt(var0.substring(var4 + 1, var5));
            int var3 = Integer.parseInt(var0.substring(var5 + 1));
            return new Time(var1, var2, var3);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public String toString() {
      int var1 = super.getHours();
      int var2 = super.getMinutes();
      int var3 = super.getSeconds();
      String var4;
      if (var1 < 10) {
         var4 = "0" + var1;
      } else {
         var4 = Integer.toString(var1);
      }

      String var5;
      if (var2 < 10) {
         var5 = "0" + var2;
      } else {
         var5 = Integer.toString(var2);
      }

      String var6;
      if (var3 < 10) {
         var6 = "0" + var3;
      } else {
         var6 = Integer.toString(var3);
      }

      return var4 + ":" + var5 + ":" + var6;
   }

   /** @deprecated */
   @Deprecated
   public int getYear() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public int getMonth() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public int getDay() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public int getDate() {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public void setYear(int var1) {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public void setMonth(int var1) {
      throw new IllegalArgumentException();
   }

   /** @deprecated */
   @Deprecated
   public void setDate(int var1) {
      throw new IllegalArgumentException();
   }

   public static Time valueOf(LocalTime var0) {
      return new Time(var0.getHour(), var0.getMinute(), var0.getSecond());
   }

   public LocalTime toLocalTime() {
      return LocalTime.of(this.getHours(), this.getMinutes(), this.getSeconds());
   }

   public Instant toInstant() {
      throw new UnsupportedOperationException();
   }
}
