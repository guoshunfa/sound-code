package java.util;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.ZoneId;
import sun.security.action.GetPropertyAction;
import sun.util.calendar.ZoneInfo;
import sun.util.calendar.ZoneInfoFile;
import sun.util.locale.provider.TimeZoneNameUtility;

public abstract class TimeZone implements Serializable, Cloneable {
   public static final int SHORT = 0;
   public static final int LONG = 1;
   private static final int ONE_MINUTE = 60000;
   private static final int ONE_HOUR = 3600000;
   private static final int ONE_DAY = 86400000;
   static final long serialVersionUID = 3581463369166924961L;
   static final TimeZone NO_TIMEZONE = null;
   private String ID;
   private static volatile TimeZone defaultTimeZone;
   static final String GMT_ID = "GMT";
   private static final int GMT_ID_LENGTH = 3;
   private static volatile TimeZone mainAppContextDefault;

   public abstract int getOffset(int var1, int var2, int var3, int var4, int var5, int var6);

   public int getOffset(long var1) {
      return this.inDaylightTime(new Date(var1)) ? this.getRawOffset() + this.getDSTSavings() : this.getRawOffset();
   }

   int getOffsets(long var1, int[] var3) {
      int var4 = this.getRawOffset();
      int var5 = 0;
      if (this.inDaylightTime(new Date(var1))) {
         var5 = this.getDSTSavings();
      }

      if (var3 != null) {
         var3[0] = var4;
         var3[1] = var5;
      }

      return var4 + var5;
   }

   public abstract void setRawOffset(int var1);

   public abstract int getRawOffset();

   public String getID() {
      return this.ID;
   }

   public void setID(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.ID = var1;
      }
   }

   public final String getDisplayName() {
      return this.getDisplayName(false, 1, Locale.getDefault(Locale.Category.DISPLAY));
   }

   public final String getDisplayName(Locale var1) {
      return this.getDisplayName(false, 1, var1);
   }

   public final String getDisplayName(boolean var1, int var2) {
      return this.getDisplayName(var1, var2, Locale.getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayName(boolean var1, int var2, Locale var3) {
      if (var2 != 0 && var2 != 1) {
         throw new IllegalArgumentException("Illegal style: " + var2);
      } else {
         String var4 = this.getID();
         String var5 = TimeZoneNameUtility.retrieveDisplayName(var4, var1, var2, var3);
         if (var5 != null) {
            return var5;
         } else {
            if (var4.startsWith("GMT") && var4.length() > 3) {
               char var6 = var4.charAt(3);
               if (var6 == '+' || var6 == '-') {
                  return var4;
               }
            }

            int var7 = this.getRawOffset();
            if (var1) {
               var7 += this.getDSTSavings();
            }

            return ZoneInfoFile.toCustomID(var7);
         }
      }
   }

   private static String[] getDisplayNames(String var0, Locale var1) {
      return TimeZoneNameUtility.retrieveDisplayNames(var0, var1);
   }

   public int getDSTSavings() {
      return this.useDaylightTime() ? 3600000 : 0;
   }

   public abstract boolean useDaylightTime();

   public boolean observesDaylightTime() {
      return this.useDaylightTime() || this.inDaylightTime(new Date());
   }

   public abstract boolean inDaylightTime(Date var1);

   public static synchronized TimeZone getTimeZone(String var0) {
      return getTimeZone(var0, true);
   }

   public static TimeZone getTimeZone(ZoneId var0) {
      String var1 = var0.getId();
      char var2 = var1.charAt(0);
      if (var2 != '+' && var2 != '-') {
         if (var2 == 'Z' && var1.length() == 1) {
            var1 = "UTC";
         }
      } else {
         var1 = "GMT" + var1;
      }

      return getTimeZone(var1, true);
   }

   public ZoneId toZoneId() {
      String var1 = this.getID();
      if (ZoneInfoFile.useOldMapping() && var1.length() == 3) {
         if ("EST".equals(var1)) {
            return ZoneId.of("America/New_York");
         }

         if ("MST".equals(var1)) {
            return ZoneId.of("America/Denver");
         }

         if ("HST".equals(var1)) {
            return ZoneId.of("America/Honolulu");
         }
      }

      return ZoneId.of(var1, ZoneId.SHORT_IDS);
   }

   private static TimeZone getTimeZone(String var0, boolean var1) {
      Object var2 = ZoneInfo.getTimeZone(var0);
      if (var2 == null) {
         var2 = parseCustomTimeZone(var0);
         if (var2 == null && var1) {
            var2 = new ZoneInfo("GMT", 0);
         }
      }

      return (TimeZone)var2;
   }

   public static synchronized String[] getAvailableIDs(int var0) {
      return ZoneInfo.getAvailableIDs(var0);
   }

   public static synchronized String[] getAvailableIDs() {
      return ZoneInfo.getAvailableIDs();
   }

   private static native String getSystemTimeZoneID(String var0);

   private static native String getSystemGMTOffsetID();

   public static TimeZone getDefault() {
      return (TimeZone)getDefaultRef().clone();
   }

   static TimeZone getDefaultRef() {
      TimeZone var0 = defaultTimeZone;
      if (var0 == null) {
         var0 = setDefaultZone();

         assert var0 != null;
      }

      return var0;
   }

   private static synchronized TimeZone setDefaultZone() {
      final String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.timezone")));
      String var2;
      if (var1 == null || var1.isEmpty()) {
         var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home")));

         try {
            var1 = getSystemTimeZoneID(var2);
            if (var1 == null) {
               var1 = "GMT";
            }
         } catch (NullPointerException var4) {
            var1 = "GMT";
         }
      }

      TimeZone var0 = getTimeZone(var1, false);
      if (var0 == null) {
         var2 = getSystemGMTOffsetID();
         if (var2 != null) {
            var1 = var2;
         }

         var0 = getTimeZone(var1, true);
      }

      assert var0 != null;

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.setProperty("user.timezone", var1);
            return null;
         }
      });
      defaultTimeZone = var0;
      return var0;
   }

   public static void setDefault(TimeZone var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new PropertyPermission("user.timezone", "write"));
      }

      defaultTimeZone = var0;
   }

   public boolean hasSameRules(TimeZone var1) {
      return var1 != null && this.getRawOffset() == var1.getRawOffset() && this.useDaylightTime() == var1.useDaylightTime();
   }

   public Object clone() {
      try {
         TimeZone var1 = (TimeZone)super.clone();
         var1.ID = this.ID;
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   private static final TimeZone parseCustomTimeZone(String var0) {
      int var1;
      if ((var1 = var0.length()) >= 5 && var0.indexOf("GMT") == 0) {
         ZoneInfo var2 = ZoneInfoFile.getZoneInfo(var0);
         if (var2 != null) {
            return var2;
         } else {
            byte var3 = 3;
            boolean var4 = false;
            int var11 = var3 + 1;
            char var5 = var0.charAt(var3);
            if (var5 == '-') {
               var4 = true;
            } else if (var5 != '+') {
               return null;
            }

            int var6 = 0;
            int var7 = 0;
            int var8 = 0;
            int var9 = 0;

            while(true) {
               while(var11 < var1) {
                  var5 = var0.charAt(var11++);
                  if (var5 != ':') {
                     if (var5 < '0' || var5 > '9') {
                        return null;
                     }

                     var7 = var7 * 10 + (var5 - 48);
                     ++var9;
                  } else {
                     if (var8 > 0) {
                        return null;
                     }

                     if (var9 > 2) {
                        return null;
                     }

                     var6 = var7;
                     ++var8;
                     var7 = 0;
                     var9 = 0;
                  }
               }

               if (var11 != var1) {
                  return null;
               }

               if (var8 == 0) {
                  if (var9 <= 2) {
                     var6 = var7;
                     var7 = 0;
                  } else {
                     var6 = var7 / 100;
                     var7 %= 100;
                  }
               } else if (var9 != 2) {
                  return null;
               }

               if (var6 <= 23 && var7 <= 59) {
                  int var10 = (var6 * 60 + var7) * 60 * 1000;
                  if (var10 == 0) {
                     var2 = ZoneInfoFile.getZoneInfo("GMT");
                     if (var4) {
                        var2.setID("GMT-00:00");
                     } else {
                        var2.setID("GMT+00:00");
                     }
                  } else {
                     var2 = ZoneInfoFile.getCustomTimeZone(var0, var4 ? -var10 : var10);
                  }

                  return var2;
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }
}
