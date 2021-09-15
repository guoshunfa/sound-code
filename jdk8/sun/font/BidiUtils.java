package sun.font;

import java.text.Bidi;

public final class BidiUtils {
   static final char NUMLEVELS = '>';

   public static void getLevels(Bidi var0, byte[] var1, int var2) {
      int var3 = var2 + var0.getLength();
      if (var2 >= 0 && var3 <= var1.length) {
         int var4 = var0.getRunCount();
         int var5 = var2;

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var2 + var0.getRunLimit(var6);

            for(byte var8 = (byte)var0.getRunLevel(var6); var5 < var7; var1[var5++] = var8) {
            }
         }

      } else {
         throw new IndexOutOfBoundsException("levels.length = " + var1.length + " start: " + var2 + " limit: " + var3);
      }
   }

   public static byte[] getLevels(Bidi var0) {
      byte[] var1 = new byte[var0.getLength()];
      getLevels(var0, var1, 0);
      return var1;
   }

   public static int[] createVisualToLogicalMap(byte[] var0) {
      int var1 = var0.length;
      int[] var2 = new int[var1];
      byte var3 = 63;
      byte var4 = 0;

      int var5;
      for(var5 = 0; var5 < var1; ++var5) {
         var2[var5] = var5;
         byte var6 = var0[var5];
         if (var6 > var4) {
            var4 = var6;
         }

         if ((var6 & 1) != 0 && var6 < var3) {
            var3 = var6;
         }
      }

      label58:
      for(; var4 >= var3; --var4) {
         var5 = 0;

         while(true) {
            while(var5 >= var1 || var0[var5] >= var4) {
               int var9 = var5++;
               if (var9 == var0.length) {
                  continue label58;
               }

               while(var5 < var1 && var0[var5] >= var4) {
                  ++var5;
               }

               for(int var7 = var5 - 1; var9 < var7; --var7) {
                  int var8 = var2[var9];
                  var2[var9] = var2[var7];
                  var2[var7] = var8;
                  ++var9;
               }
            }

            ++var5;
         }
      }

      return var2;
   }

   public static int[] createInverseMap(int[] var0) {
      if (var0 == null) {
         return null;
      } else {
         int[] var1 = new int[var0.length];

         for(int var2 = 0; var2 < var0.length; var1[var0[var2]] = var2++) {
         }

         return var1;
      }
   }

   public static int[] createContiguousOrder(int[] var0) {
      return var0 != null ? computeContiguousOrder(var0, 0, var0.length) : null;
   }

   private static int[] computeContiguousOrder(int[] var0, int var1, int var2) {
      int[] var3 = new int[var2 - var1];

      int var4;
      for(var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = var4 + var1;
      }

      for(var4 = 0; var4 < var3.length - 1; ++var4) {
         int var5 = var4;
         int var6 = var0[var3[var4]];

         int var7;
         for(var7 = var4; var7 < var3.length; ++var7) {
            if (var0[var3[var7]] < var6) {
               var5 = var7;
               var6 = var0[var3[var7]];
            }
         }

         var7 = var3[var4];
         var3[var4] = var3[var5];
         var3[var5] = var7;
      }

      if (var1 != 0) {
         for(var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] -= var1;
         }
      }

      for(var4 = 0; var4 < var3.length && var3[var4] == var4; ++var4) {
      }

      if (var4 == var3.length) {
         return null;
      } else {
         return createInverseMap(var3);
      }
   }

   public static int[] createNormalizedMap(int[] var0, byte[] var1, int var2, int var3) {
      if (var0 != null) {
         if (var2 == 0 && var3 == var0.length) {
            return var0;
         } else {
            boolean var4;
            boolean var5;
            byte var6;
            if (var1 == null) {
               var6 = 0;
               var4 = true;
               var5 = true;
            } else if (var1[var2] == var1[var3 - 1]) {
               var6 = var1[var2];
               var5 = (var6 & 1) == 0;

               int var7;
               for(var7 = var2; var7 < var3 && var1[var7] >= var6; ++var7) {
                  if (var5) {
                     var5 = var1[var7] == var6;
                  }
               }

               var4 = var7 == var3;
            } else {
               var4 = false;
               var6 = 0;
               var5 = false;
            }

            if (!var4) {
               return computeContiguousOrder(var0, var2, var3);
            } else if (var5) {
               return null;
            } else {
               int[] var10 = new int[var3 - var2];
               int var8;
               if ((var6 & 1) != 0) {
                  var8 = var0[var3 - 1];
               } else {
                  var8 = var0[var2];
               }

               if (var8 == 0) {
                  System.arraycopy(var0, var2, var10, 0, var3 - var2);
               } else {
                  for(int var9 = 0; var9 < var10.length; ++var9) {
                     var10[var9] = var0[var9 + var2] - var8;
                  }
               }

               return var10;
            }
         }
      } else {
         return null;
      }
   }

   public static void reorderVisually(byte[] var0, Object[] var1) {
      int var2 = var0.length;
      byte var3 = 63;
      byte var4 = 0;

      int var5;
      for(var5 = 0; var5 < var2; ++var5) {
         byte var6 = var0[var5];
         if (var6 > var4) {
            var4 = var6;
         }

         if ((var6 & 1) != 0 && var6 < var3) {
            var3 = var6;
         }
      }

      label58:
      for(; var4 >= var3; --var4) {
         var5 = 0;

         while(true) {
            while(var5 >= var2 || var0[var5] >= var4) {
               int var9 = var5++;
               if (var9 == var0.length) {
                  continue label58;
               }

               while(var5 < var2 && var0[var5] >= var4) {
                  ++var5;
               }

               for(int var7 = var5 - 1; var9 < var7; --var7) {
                  Object var8 = var1[var9];
                  var1[var9] = var1[var7];
                  var1[var7] = var8;
                  ++var9;
               }
            }

            ++var5;
         }
      }

   }
}
