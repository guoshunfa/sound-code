package sun.text.normalizer;

import java.util.HashMap;

public final class VersionInfo {
   private int m_version_;
   private static final HashMap<Integer, Object> MAP_ = new HashMap();
   private static final String INVALID_VERSION_NUMBER_ = "Invalid version number: Version number may be negative or greater than 255";

   public static VersionInfo getInstance(String var0) {
      int var1 = var0.length();
      int[] var2 = new int[]{0, 0, 0, 0};
      int var3 = 0;

      int var4;
      for(var4 = 0; var3 < 4 && var4 < var1; ++var4) {
         char var6 = var0.charAt(var4);
         if (var6 == '.') {
            ++var3;
         } else {
            var6 = (char)(var6 - 48);
            if (var6 < 0 || var6 > '\t') {
               throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
            }

            var2[var3] *= 10;
            var2[var3] += var6;
         }
      }

      if (var4 != var1) {
         throw new IllegalArgumentException("Invalid version number: String '" + var0 + "' exceeds version format");
      } else {
         for(int var5 = 0; var5 < 4; ++var5) {
            if (var2[var5] < 0 || var2[var5] > 255) {
               throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
            }
         }

         return getInstance(var2[0], var2[1], var2[2], var2[3]);
      }
   }

   public static VersionInfo getInstance(int var0, int var1, int var2, int var3) {
      if (var0 >= 0 && var0 <= 255 && var1 >= 0 && var1 <= 255 && var2 >= 0 && var2 <= 255 && var3 >= 0 && var3 <= 255) {
         int var4 = getInt(var0, var1, var2, var3);
         Integer var5 = var4;
         Object var6 = MAP_.get(var5);
         if (var6 == null) {
            var6 = new VersionInfo(var4);
            MAP_.put(var5, var6);
         }

         return (VersionInfo)var6;
      } else {
         throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
      }
   }

   public int compareTo(VersionInfo var1) {
      return this.m_version_ - var1.m_version_;
   }

   private VersionInfo(int var1) {
      this.m_version_ = var1;
   }

   private static int getInt(int var0, int var1, int var2, int var3) {
      return var0 << 24 | var1 << 16 | var2 << 8 | var3;
   }
}
