package java.util.zip;

import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class ZipUtils {
   private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;

   public static final FileTime winTimeToFileTime(long var0) {
      return FileTime.from(var0 / 10L + -11644473600000000L, TimeUnit.MICROSECONDS);
   }

   public static final long fileTimeToWinTime(FileTime var0) {
      return (var0.to(TimeUnit.MICROSECONDS) - -11644473600000000L) * 10L;
   }

   public static final FileTime unixTimeToFileTime(long var0) {
      return FileTime.from(var0, TimeUnit.SECONDS);
   }

   public static final long fileTimeToUnixTime(FileTime var0) {
      return var0.to(TimeUnit.SECONDS);
   }

   private static long dosToJavaTime(long var0) {
      Date var2 = new Date((int)((var0 >> 25 & 127L) + 80L), (int)((var0 >> 21 & 15L) - 1L), (int)(var0 >> 16 & 31L), (int)(var0 >> 11 & 31L), (int)(var0 >> 5 & 63L), (int)(var0 << 1 & 62L));
      return var2.getTime();
   }

   public static long extendedDosToJavaTime(long var0) {
      long var2 = dosToJavaTime(var0);
      return var2 + (var0 >> 32);
   }

   private static long javaToDosTime(long var0) {
      Date var2 = new Date(var0);
      int var3 = var2.getYear() + 1900;
      return var3 < 1980 ? 2162688L : (long)(var3 - 1980 << 25 | var2.getMonth() + 1 << 21 | var2.getDate() << 16 | var2.getHours() << 11 | var2.getMinutes() << 5 | var2.getSeconds() >> 1);
   }

   public static long javaToExtendedDosTime(long var0) {
      if (var0 < 0L) {
         return 2162688L;
      } else {
         long var2 = javaToDosTime(var0);
         return var2 != 2162688L ? var2 + (var0 % 2000L << 32) : 2162688L;
      }
   }

   public static final int get16(byte[] var0, int var1) {
      return Byte.toUnsignedInt(var0[var1]) | Byte.toUnsignedInt(var0[var1 + 1]) << 8;
   }

   public static final long get32(byte[] var0, int var1) {
      return ((long)get16(var0, var1) | (long)get16(var0, var1 + 2) << 16) & 4294967295L;
   }

   public static final long get64(byte[] var0, int var1) {
      return get32(var0, var1) | get32(var0, var1 + 4) << 32;
   }
}
