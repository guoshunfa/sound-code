package java.sql;

import java.time.Instant;
import java.time.LocalDateTime;

public class Timestamp extends java.util.Date {
   private int nanos;
   static final long serialVersionUID = 2745179027874758501L;
   private static final int MILLIS_PER_SECOND = 1000;

   /** @deprecated */
   @Deprecated
   public Timestamp(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(var1, var2, var3, var4, var5, var6);
      if (var7 <= 999999999 && var7 >= 0) {
         this.nanos = var7;
      } else {
         throw new IllegalArgumentException("nanos > 999999999 or < 0");
      }
   }

   public Timestamp(long var1) {
      super(var1 / 1000L * 1000L);
      this.nanos = (int)(var1 % 1000L * 1000000L);
      if (this.nanos < 0) {
         this.nanos += 1000000000;
         super.setTime((var1 / 1000L - 1L) * 1000L);
      }

   }

   public void setTime(long var1) {
      super.setTime(var1 / 1000L * 1000L);
      this.nanos = (int)(var1 % 1000L * 1000000L);
      if (this.nanos < 0) {
         this.nanos += 1000000000;
         super.setTime((var1 / 1000L - 1L) * 1000L);
      }

   }

   public long getTime() {
      long var1 = super.getTime();
      return var1 + (long)(this.nanos / 1000000);
   }

   public static Timestamp valueOf(String var0) {
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      int var15 = 0;
      boolean var19 = false;
      boolean var20 = false;
      boolean var21 = false;
      String var22 = "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]";
      String var23 = "000000000";
      String var24 = "-";
      String var25 = ":";
      if (var0 == null) {
         throw new IllegalArgumentException("null string");
      } else {
         var0 = var0.trim();
         int var18 = var0.indexOf(32);
         if (var18 > 0) {
            String var6 = var0.substring(0, var18);
            String var7 = var0.substring(var18 + 1);
            int var16 = var6.indexOf(45);
            int var17 = var6.indexOf(45, var16 + 1);
            if (var7 == null) {
               throw new IllegalArgumentException(var22);
            } else {
               int var30 = var7.indexOf(58);
               int var31 = var7.indexOf(58, var30 + 1);
               int var32 = var7.indexOf(46, var31 + 1);
               boolean var26 = false;
               if (var16 > 0 && var17 > 0 && var17 < var6.length() - 1) {
                  String var27 = var6.substring(0, var16);
                  String var28 = var6.substring(var16 + 1, var17);
                  String var29 = var6.substring(var17 + 1);
                  if (var27.length() == 4 && var28.length() >= 1 && var28.length() <= 2 && var29.length() >= 1 && var29.length() <= 2) {
                     var9 = Integer.parseInt(var27);
                     var10 = Integer.parseInt(var28);
                     var11 = Integer.parseInt(var29);
                     if (var10 >= 1 && var10 <= 12 && var11 >= 1 && var11 <= 31) {
                        var26 = true;
                     }
                  }
               }

               if (!var26) {
                  throw new IllegalArgumentException(var22);
               } else if (var30 > 0 & var31 > 0 & var31 < var7.length() - 1) {
                  int var12 = Integer.parseInt(var7.substring(0, var30));
                  int var13 = Integer.parseInt(var7.substring(var30 + 1, var31));
                  int var14;
                  if (var32 > 0 & var32 < var7.length() - 1) {
                     var14 = Integer.parseInt(var7.substring(var31 + 1, var32));
                     String var8 = var7.substring(var32 + 1);
                     if (var8.length() > 9) {
                        throw new IllegalArgumentException(var22);
                     }

                     if (!Character.isDigit(var8.charAt(0))) {
                        throw new IllegalArgumentException(var22);
                     }

                     var8 = var8 + var23.substring(0, 9 - var8.length());
                     var15 = Integer.parseInt(var8);
                  } else {
                     if (var32 > 0) {
                        throw new IllegalArgumentException(var22);
                     }

                     var14 = Integer.parseInt(var7.substring(var31 + 1));
                  }

                  return new Timestamp(var9 - 1900, var10 - 1, var11, var12, var13, var14, var15);
               } else {
                  throw new IllegalArgumentException(var22);
               }
            }
         } else {
            throw new IllegalArgumentException(var22);
         }
      }
   }

   public String toString() {
      int var1 = super.getYear() + 1900;
      int var2 = super.getMonth() + 1;
      int var3 = super.getDate();
      int var4 = super.getHours();
      int var5 = super.getMinutes();
      int var6 = super.getSeconds();
      String var14 = "000000000";
      String var15 = "0000";
      String var7;
      if (var1 < 1000) {
         var7 = "" + var1;
         var7 = var15.substring(0, 4 - var7.length()) + var7;
      } else {
         var7 = "" + var1;
      }

      String var8;
      if (var2 < 10) {
         var8 = "0" + var2;
      } else {
         var8 = Integer.toString(var2);
      }

      String var9;
      if (var3 < 10) {
         var9 = "0" + var3;
      } else {
         var9 = Integer.toString(var3);
      }

      String var10;
      if (var4 < 10) {
         var10 = "0" + var4;
      } else {
         var10 = Integer.toString(var4);
      }

      String var11;
      if (var5 < 10) {
         var11 = "0" + var5;
      } else {
         var11 = Integer.toString(var5);
      }

      String var12;
      if (var6 < 10) {
         var12 = "0" + var6;
      } else {
         var12 = Integer.toString(var6);
      }

      String var13;
      if (this.nanos == 0) {
         var13 = "0";
      } else {
         var13 = Integer.toString(this.nanos);
         var13 = var14.substring(0, 9 - var13.length()) + var13;
         char[] var17 = new char[var13.length()];
         var13.getChars(0, var13.length(), var17, 0);

         int var18;
         for(var18 = 8; var17[var18] == '0'; --var18) {
         }

         var13 = new String(var17, 0, var18 + 1);
      }

      StringBuffer var16 = new StringBuffer(20 + var13.length());
      var16.append(var7);
      var16.append("-");
      var16.append(var8);
      var16.append("-");
      var16.append(var9);
      var16.append(" ");
      var16.append(var10);
      var16.append(":");
      var16.append(var11);
      var16.append(":");
      var16.append(var12);
      var16.append(".");
      var16.append(var13);
      return var16.toString();
   }

   public int getNanos() {
      return this.nanos;
   }

   public void setNanos(int var1) {
      if (var1 <= 999999999 && var1 >= 0) {
         this.nanos = var1;
      } else {
         throw new IllegalArgumentException("nanos > 999999999 or < 0");
      }
   }

   public boolean equals(Timestamp var1) {
      if (super.equals(var1)) {
         return this.nanos == var1.nanos;
      } else {
         return false;
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof Timestamp ? this.equals((Timestamp)var1) : false;
   }

   public boolean before(Timestamp var1) {
      return this.compareTo(var1) < 0;
   }

   public boolean after(Timestamp var1) {
      return this.compareTo(var1) > 0;
   }

   public int compareTo(Timestamp var1) {
      long var2 = this.getTime();
      long var4 = var1.getTime();
      int var6 = var2 < var4 ? -1 : (var2 == var4 ? 0 : 1);
      if (var6 == 0) {
         if (this.nanos > var1.nanos) {
            return 1;
         }

         if (this.nanos < var1.nanos) {
            return -1;
         }
      }

      return var6;
   }

   public int compareTo(java.util.Date var1) {
      if (var1 instanceof Timestamp) {
         return this.compareTo((Timestamp)var1);
      } else {
         Timestamp var2 = new Timestamp(var1.getTime());
         return this.compareTo(var2);
      }
   }

   public int hashCode() {
      return super.hashCode();
   }

   public static Timestamp valueOf(LocalDateTime var0) {
      return new Timestamp(var0.getYear() - 1900, var0.getMonthValue() - 1, var0.getDayOfMonth(), var0.getHour(), var0.getMinute(), var0.getSecond(), var0.getNano());
   }

   public LocalDateTime toLocalDateTime() {
      return LocalDateTime.of(this.getYear() + 1900, this.getMonth() + 1, this.getDate(), this.getHours(), this.getMinutes(), this.getSeconds(), this.getNanos());
   }

   public static Timestamp from(Instant var0) {
      try {
         Timestamp var1 = new Timestamp(var0.getEpochSecond() * 1000L);
         var1.nanos = var0.getNano();
         return var1;
      } catch (ArithmeticException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public Instant toInstant() {
      return Instant.ofEpochSecond(super.getTime() / 1000L, (long)this.nanos);
   }
}
