package sun.text.bidi;

import java.text.Bidi;
import java.util.Arrays;

public final class BidiLine {
   static void setTrailingWSStart(BidiBase var0) {
      byte[] var1 = var0.dirProps;
      byte[] var2 = var0.levels;
      int var3 = var0.length;
      byte var4 = var0.paraLevel;
      if (BidiBase.NoContextRTL(var1[var3 - 1]) == 7) {
         var0.trailingWSStart = var3;
      } else {
         while(var3 > 0 && (BidiBase.DirPropFlagNC(var1[var3 - 1]) & BidiBase.MASK_WS) != 0) {
            --var3;
         }

         while(var3 > 0 && var2[var3 - 1] == var4) {
            --var3;
         }

         var0.trailingWSStart = var3;
      }
   }

   public static Bidi setLine(Bidi var0, BidiBase var1, Bidi var2, BidiBase var3, int var4, int var5) {
      BidiBase var7 = var3;
      int var6 = var3.length = var3.originalLength = var3.resultLength = var5 - var4;
      var3.text = new char[var6];
      System.arraycopy(var1.text, var4, var3.text, 0, var6);
      var3.paraLevel = var1.GetParaLevelAt(var4);
      var3.paraCount = var1.paraCount;
      var3.runs = new BidiRun[0];
      if (var1.controlCount > 0) {
         for(int var8 = var4; var8 < var5; ++var8) {
            if (BidiBase.IsBidiControlChar(var1.text[var8])) {
               ++var7.controlCount;
            }
         }

         var7.resultLength -= var7.controlCount;
      }

      var7.getDirPropsMemory(var6);
      var7.dirProps = var7.dirPropsMemory;
      System.arraycopy(var1.dirProps, var4, var7.dirProps, 0, var6);
      var7.getLevelsMemory(var6);
      var7.levels = var7.levelsMemory;
      System.arraycopy(var1.levels, var4, var7.levels, 0, var6);
      var7.runCount = -1;
      if (var1.direction != 2) {
         var7.direction = var1.direction;
         if (var1.trailingWSStart <= var4) {
            var7.trailingWSStart = 0;
         } else if (var1.trailingWSStart < var5) {
            var7.trailingWSStart = var1.trailingWSStart - var4;
         } else {
            var7.trailingWSStart = var6;
         }
      } else {
         byte[] var12 = var7.levels;
         setTrailingWSStart(var7);
         int var10 = var7.trailingWSStart;
         if (var10 == 0) {
            var7.direction = (byte)(var7.paraLevel & 1);
         } else {
            byte var11 = (byte)(var12[0] & 1);
            if (var10 < var6 && (var7.paraLevel & 1) != var11) {
               var7.direction = 2;
            } else {
               label71: {
                  for(int var9 = 1; var9 != var10; ++var9) {
                     if ((var12[var9] & 1) != var11) {
                        var7.direction = 2;
                        break label71;
                     }
                  }

                  var7.direction = var11;
               }
            }
         }

         switch(var7.direction) {
         case 0:
            var7.paraLevel = (byte)(var7.paraLevel + 1 & -2);
            var7.trailingWSStart = 0;
            break;
         case 1:
            var7.paraLevel = (byte)(var7.paraLevel | 1);
            var7.trailingWSStart = 0;
         }
      }

      var3.paraBidi = var1;
      return var2;
   }

   static byte getLevelAt(BidiBase var0, int var1) {
      return var0.direction == 2 && var1 < var0.trailingWSStart ? var0.levels[var1] : var0.GetParaLevelAt(var1);
   }

   static byte[] getLevels(BidiBase var0) {
      int var1 = var0.trailingWSStart;
      int var2 = var0.length;
      if (var1 != var2) {
         Arrays.fill(var0.levels, var1, var2, var0.paraLevel);
         var0.trailingWSStart = var2;
      }

      if (var2 < var0.levels.length) {
         byte[] var3 = new byte[var2];
         System.arraycopy(var0.levels, 0, var3, 0, var2);
         return var3;
      } else {
         return var0.levels;
      }
   }

   static BidiRun getLogicalRun(BidiBase var0, int var1) {
      BidiRun var2 = new BidiRun();
      getRuns(var0);
      int var4 = var0.runCount;
      int var5 = 0;
      int var6 = 0;
      BidiRun var3 = var0.runs[0];

      for(int var7 = 0; var7 < var4; ++var7) {
         var3 = var0.runs[var7];
         var6 = var3.start + var3.limit - var5;
         if (var1 >= var3.start && var1 < var6) {
            break;
         }

         var5 = var3.limit;
      }

      var2.start = var3.start;
      var2.limit = var6;
      var2.level = var3.level;
      return var2;
   }

   private static void getSingleRun(BidiBase var0, byte var1) {
      var0.runs = var0.simpleRuns;
      var0.runCount = 1;
      var0.runs[0] = new BidiRun(0, var0.length, var1);
   }

   private static void reorderLine(BidiBase var0, byte var1, byte var2) {
      if (var2 > (var1 | 1)) {
         ++var1;
         BidiRun[] var3 = var0.runs;
         byte[] var5 = var0.levels;
         int var9 = var0.runCount;
         if (var0.trailingWSStart < var0.length) {
            --var9;
         }

         label70:
         while(true) {
            --var2;
            BidiRun var4;
            int var6;
            if (var2 < var1) {
               if ((var1 & 1) == 0) {
                  var6 = 0;
                  if (var0.trailingWSStart == var0.length) {
                     --var9;
                  }

                  while(var6 < var9) {
                     var4 = var3[var6];
                     var3[var6] = var3[var9];
                     var3[var9] = var4;
                     ++var6;
                     --var9;
                  }
               }

               return;
            }

            var6 = 0;

            while(true) {
               while(var6 >= var9 || var5[var3[var6].start] >= var2) {
                  if (var6 >= var9) {
                     continue label70;
                  }

                  int var8 = var6;

                  do {
                     ++var8;
                  } while(var8 < var9 && var5[var3[var8].start] >= var2);

                  for(int var7 = var8 - 1; var6 < var7; --var7) {
                     var4 = var3[var6];
                     var3[var6] = var3[var7];
                     var3[var7] = var4;
                     ++var6;
                  }

                  if (var8 == var9) {
                     continue label70;
                  }

                  var6 = var8 + 1;
               }

               ++var6;
            }
         }
      }
   }

   static int getRunFromLogicalIndex(BidiBase var0, int var1) {
      BidiRun[] var2 = var0.runs;
      int var3 = var0.runCount;
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         int var6 = var2[var5].limit - var4;
         int var7 = var2[var5].start;
         if (var1 >= var7 && var1 < var7 + var6) {
            return var5;
         }

         var4 += var6;
      }

      throw new IllegalStateException("Internal ICU error in getRunFromLogicalIndex");
   }

   static void getRuns(BidiBase var0) {
      if (var0.runCount < 0) {
         int var1;
         int var2;
         if (var0.direction != 2) {
            getSingleRun(var0, var0.paraLevel);
         } else {
            var1 = var0.length;
            byte[] var3 = var0.levels;
            byte var6 = 126;
            var2 = var0.trailingWSStart;
            int var5 = 0;

            int var4;
            for(var4 = 0; var4 < var2; ++var4) {
               if (var3[var4] != var6) {
                  ++var5;
                  var6 = var3[var4];
               }
            }

            if (var5 == 1 && var2 == var1) {
               getSingleRun(var0, var3[0]);
            } else {
               byte var10 = 62;
               byte var11 = 0;
               if (var2 < var1) {
                  ++var5;
               }

               var0.getRunsMemory(var5);
               BidiRun[] var7 = var0.runsMemory;
               int var8 = 0;
               var4 = 0;

               do {
                  int var9 = var4;
                  var6 = var3[var4];
                  if (var6 < var10) {
                     var10 = var6;
                  }

                  if (var6 > var11) {
                     var11 = var6;
                  }

                  do {
                     ++var4;
                  } while(var4 < var2 && var3[var4] == var6);

                  var7[var8] = new BidiRun(var9, var4 - var9, var6);
                  ++var8;
               } while(var4 < var2);

               if (var2 < var1) {
                  var7[var8] = new BidiRun(var2, var1 - var2, var0.paraLevel);
                  if (var0.paraLevel < var10) {
                     var10 = var0.paraLevel;
                  }
               }

               var0.runs = var7;
               var0.runCount = var5;
               reorderLine(var0, var10, var11);
               var2 = 0;

               for(var4 = 0; var4 < var5; ++var4) {
                  var7[var4].level = var3[var7[var4].start];
                  var2 = var7[var4].limit += var2;
               }

               if (var8 < var5) {
                  int var12 = (var0.paraLevel & 1) != 0 ? 0 : var8;
                  var7[var12].level = var0.paraLevel;
               }
            }
         }

         if (var0.insertPoints.size > 0) {
            for(int var14 = 0; var14 < var0.insertPoints.size; ++var14) {
               BidiBase.Point var13 = var0.insertPoints.points[var14];
               var2 = getRunFromLogicalIndex(var0, var13.pos);
               BidiRun var10000 = var0.runs[var2];
               var10000.insertRemove |= var13.flag;
            }
         }

         if (var0.controlCount > 0) {
            for(var2 = 0; var2 < var0.length; ++var2) {
               char var15 = var0.text[var2];
               if (BidiBase.IsBidiControlChar(var15)) {
                  var1 = getRunFromLogicalIndex(var0, var2);
                  --var0.runs[var1].insertRemove;
               }
            }
         }

      }
   }

   static int[] prepareReorder(byte[] var0, byte[] var1, byte[] var2) {
      if (var0 != null && var0.length > 0) {
         byte var5 = 62;
         byte var6 = 0;
         int var3 = var0.length;

         while(var3 > 0) {
            --var3;
            byte var4 = var0[var3];
            if (var4 > 62) {
               return null;
            }

            if (var4 < var5) {
               var5 = var4;
            }

            if (var4 > var6) {
               var6 = var4;
            }
         }

         var1[0] = var5;
         var2[0] = var6;
         int[] var7 = new int[var0.length];

         for(var3 = var0.length; var3 > 0; var7[var3] = var3) {
            --var3;
         }

         return var7;
      } else {
         return null;
      }
   }

   static int[] reorderVisual(byte[] var0) {
      byte[] var1 = new byte[1];
      byte[] var2 = new byte[1];
      int[] var9 = prepareReorder(var0, var1, var2);
      if (var9 == null) {
         return null;
      } else {
         byte var7 = var1[0];
         byte var8 = var2[0];
         if (var7 == var8 && (var7 & 1) == 0) {
            return var9;
         } else {
            var7 = (byte)(var7 | 1);

            do {
               int var3 = 0;

               label58:
               while(true) {
                  while(var3 >= var0.length || var0[var3] >= var8) {
                     if (var3 >= var0.length) {
                        break label58;
                     }

                     int var5 = var3;

                     do {
                        ++var5;
                     } while(var5 < var0.length && var0[var5] >= var8);

                     for(int var4 = var5 - 1; var3 < var4; --var4) {
                        int var6 = var9[var3];
                        var9[var3] = var9[var4];
                        var9[var4] = var6;
                        ++var3;
                     }

                     if (var5 == var0.length) {
                        break label58;
                     }

                     var3 = var5 + 1;
                  }

                  ++var3;
               }

               --var8;
            } while(var8 >= var7);

            return var9;
         }
      }
   }

   static int[] getVisualMap(BidiBase var0) {
      BidiRun[] var1 = var0.runs;
      int var5 = var0.length > var0.resultLength ? var0.length : var0.resultLength;
      int[] var6 = new int[var5];
      int var3 = 0;
      int var7 = 0;

      int var2;
      int var4;
      int var8;
      for(var8 = 0; var8 < var0.runCount; ++var8) {
         var2 = var1[var8].start;
         var4 = var1[var8].limit;
         if (var1[var8].isEvenRun()) {
            do {
               var6[var7++] = var2++;
               ++var3;
            } while(var3 < var4);
         } else {
            var2 += var4 - var3;

            do {
               int var10001 = var7++;
               --var2;
               var6[var10001] = var2;
               ++var3;
            } while(var3 < var4);
         }
      }

      int var9;
      int var10;
      int var11;
      int var12;
      int var13;
      if (var0.insertPoints.size > 0) {
         var8 = 0;
         var9 = var0.runCount;
         var1 = var0.runs;

         for(var11 = 0; var11 < var9; ++var11) {
            var10 = var1[var11].insertRemove;
            if ((var10 & 5) > 0) {
               ++var8;
            }

            if ((var10 & 10) > 0) {
               ++var8;
            }
         }

         var13 = var0.resultLength;

         for(var11 = var9 - 1; var11 >= 0 && var8 > 0; --var11) {
            var10 = var1[var11].insertRemove;
            if ((var10 & 10) > 0) {
               --var13;
               var6[var13] = -1;
               --var8;
            }

            var3 = var11 > 0 ? var1[var11 - 1].limit : 0;

            for(var12 = var1[var11].limit - 1; var12 >= var3 && var8 > 0; --var12) {
               --var13;
               var6[var13] = var6[var12];
            }

            if ((var10 & 5) > 0) {
               --var13;
               var6[var13] = -1;
               --var8;
            }
         }
      } else if (var0.controlCount > 0) {
         var8 = var0.runCount;
         var1 = var0.runs;
         var3 = 0;
         int var14 = 0;

         for(var12 = 0; var12 < var8; var3 += var11) {
            var11 = var1[var12].limit - var3;
            var10 = var1[var12].insertRemove;
            if (var10 == 0 && var14 == var3) {
               var14 += var11;
            } else if (var10 == 0) {
               var4 = var1[var12].limit;

               for(var13 = var3; var13 < var4; ++var13) {
                  var6[var14++] = var6[var13];
               }
            } else {
               var2 = var1[var12].start;
               boolean var17 = var1[var12].isEvenRun();
               var9 = var2 + var11 - 1;

               for(var13 = 0; var13 < var11; ++var13) {
                  int var15 = var17 ? var2 + var13 : var9 - var13;
                  char var16 = var0.text[var15];
                  if (!BidiBase.IsBidiControlChar(var16)) {
                     var6[var14++] = var15;
                  }
               }
            }

            ++var12;
         }
      }

      if (var5 == var0.resultLength) {
         return var6;
      } else {
         int[] var18 = new int[var0.resultLength];
         System.arraycopy(var6, 0, var18, 0, var0.resultLength);
         return var18;
      }
   }
}
