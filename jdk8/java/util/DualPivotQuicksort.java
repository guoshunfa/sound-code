package java.util;

final class DualPivotQuicksort {
   private static final int MAX_RUN_COUNT = 67;
   private static final int MAX_RUN_LENGTH = 33;
   private static final int QUICKSORT_THRESHOLD = 286;
   private static final int INSERTION_SORT_THRESHOLD = 47;
   private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 29;
   private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200;
   private static final int NUM_SHORT_VALUES = 65536;
   private static final int NUM_CHAR_VALUES = 65536;
   private static final int NUM_BYTE_VALUES = 256;

   private DualPivotQuicksort() {
   }

   static void sort(int[] var0, int var1, int var2, int[] var3, int var4, int var5) {
      if (var2 - var1 < 286) {
         sort(var0, var1, var2, true);
      } else {
         int[] var6 = new int[68];
         int var7 = 0;
         var6[0] = var1;

         int var9;
         int var10;
         int var11;
         for(int var8 = var1; var8 < var2; var6[var7] = var8) {
            if (var0[var8] < var0[var8 + 1]) {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] <= var0[var8]);
            } else if (var0[var8] <= var0[var8 + 1]) {
               var9 = 33;

               while(true) {
                  ++var8;
                  if (var8 > var2 || var0[var8 - 1] != var0[var8]) {
                     break;
                  }

                  --var9;
                  if (var9 == 0) {
                     sort(var0, var1, var2, true);
                     return;
                  }
               }
            } else {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] >= var0[var8]);

               var9 = var6[var7] - 1;
               var10 = var8;

               while(true) {
                  ++var9;
                  --var10;
                  if (var9 >= var10) {
                     break;
                  }

                  var11 = var0[var9];
                  var0[var9] = var0[var10];
                  var0[var10] = var11;
               }
            }

            ++var7;
            if (var7 == 67) {
               sort(var0, var1, var2, true);
               return;
            }
         }

         if (var6[var7] == var2++) {
            ++var7;
            var6[var7] = var2;
         } else if (var7 == 1) {
            return;
         }

         byte var20 = 0;

         for(var9 = 1; (var9 <<= 1) < var7; var20 = (byte)(var20 ^ 1)) {
         }

         int var12 = var2 - var1;
         if (var3 == null || var5 < var12 || var4 + var12 > var3.length) {
            var3 = new int[var12];
            var4 = 0;
         }

         int[] var21;
         if (var20 == 0) {
            System.arraycopy(var0, var1, var3, var4, var12);
            var21 = var0;
            var11 = 0;
            var0 = var3;
            var10 = var4 - var1;
         } else {
            var21 = var3;
            var10 = 0;
            var11 = var4 - var1;
         }

         while(var7 > 1) {
            int var13 = 0;

            int var14;
            int var15;
            for(var14 = 0 + 2; var14 <= var7; var14 += 2) {
               var15 = var6[var14];
               int var16 = var6[var14 - 1];
               int var17 = var6[var14 - 2];
               int var18 = var17;

               for(int var19 = var16; var17 < var15; ++var17) {
                  if (var19 >= var15 || var18 < var16 && var0[var18 + var10] <= var0[var19 + var10]) {
                     var21[var17 + var11] = var0[var18++ + var10];
                  } else {
                     var21[var17 + var11] = var0[var19++ + var10];
                  }
               }

               ++var13;
               var6[var13] = var15;
            }

            if ((var7 & 1) != 0) {
               var14 = var2;
               var15 = var6[var7 - 1];

               while(true) {
                  --var14;
                  if (var14 < var15) {
                     ++var13;
                     var6[var13] = var2;
                     break;
                  }

                  var21[var14 + var11] = var0[var14 + var10];
               }
            }

            int[] var22 = var0;
            var0 = var21;
            var21 = var22;
            var15 = var10;
            var10 = var11;
            var11 = var15;
            var7 = var13;
         }

      }
   }

   private static void sort(int[] var0, int var1, int var2, boolean var3) {
      int var4 = var2 - var1 + 1;
      int var5;
      int var6;
      int var7;
      if (var4 < 47) {
         if (var3) {
            var5 = var1;

            for(var6 = var1; var5 < var2; var6 = var5) {
               var7 = var0[var5 + 1];

               while(var7 < var0[var6]) {
                  var0[var6 + 1] = var0[var6];
                  if (var6-- == var1) {
                     break;
                  }
               }

               var0[var6 + 1] = var7;
               ++var5;
            }

         } else {
            do {
               if (var1 >= var2) {
                  return;
               }

               ++var1;
            } while(var0[var1] >= var0[var1 - 1]);

            var5 = var1;

            label140:
            while(true) {
               ++var1;
               if (var1 > var2) {
                  var5 = var0[var2];

                  while(true) {
                     --var2;
                     if (var5 >= var0[var2]) {
                        var0[var2 + 1] = var5;
                        return;
                     }

                     var0[var2 + 1] = var0[var2];
                  }
               }

               var6 = var0[var5];
               var7 = var0[var1];
               if (var6 < var7) {
                  var7 = var6;
                  var6 = var0[var1];
               }

               while(true) {
                  --var5;
                  if (var6 >= var0[var5]) {
                     ++var5;
                     var0[var5 + 1] = var6;

                     while(true) {
                        --var5;
                        if (var7 >= var0[var5]) {
                           var0[var5 + 1] = var7;
                           ++var1;
                           var5 = var1;
                           continue label140;
                        }

                        var0[var5 + 1] = var0[var5];
                     }
                  }

                  var0[var5 + 2] = var0[var5];
               }
            }
         }
      } else {
         var5 = (var4 >> 3) + (var4 >> 6) + 1;
         var6 = var1 + var2 >>> 1;
         var7 = var6 - var5;
         int var8 = var7 - var5;
         int var9 = var6 + var5;
         int var10 = var9 + var5;
         int var11;
         if (var0[var7] < var0[var8]) {
            var11 = var0[var7];
            var0[var7] = var0[var8];
            var0[var8] = var11;
         }

         if (var0[var6] < var0[var7]) {
            var11 = var0[var6];
            var0[var6] = var0[var7];
            var0[var7] = var11;
            if (var11 < var0[var8]) {
               var0[var7] = var0[var8];
               var0[var8] = var11;
            }
         }

         if (var0[var9] < var0[var6]) {
            var11 = var0[var9];
            var0[var9] = var0[var6];
            var0[var6] = var11;
            if (var11 < var0[var7]) {
               var0[var6] = var0[var7];
               var0[var7] = var11;
               if (var11 < var0[var8]) {
                  var0[var7] = var0[var8];
                  var0[var8] = var11;
               }
            }
         }

         if (var0[var10] < var0[var9]) {
            var11 = var0[var10];
            var0[var10] = var0[var9];
            var0[var9] = var11;
            if (var11 < var0[var6]) {
               var0[var9] = var0[var6];
               var0[var6] = var11;
               if (var11 < var0[var7]) {
                  var0[var6] = var0[var7];
                  var0[var7] = var11;
                  if (var11 < var0[var8]) {
                     var0[var7] = var0[var8];
                     var0[var8] = var11;
                  }
               }
            }
         }

         var11 = var1;
         int var12 = var2;
         int var13;
         int var14;
         int var15;
         if (var0[var8] != var0[var7] && var0[var7] != var0[var6] && var0[var6] != var0[var9] && var0[var9] != var0[var10]) {
            var13 = var0[var7];
            var14 = var0[var9];
            var0[var7] = var0[var1];
            var0[var9] = var0[var2];

            while(true) {
               ++var11;
               if (var0[var11] >= var13) {
                  do {
                     --var12;
                  } while(var0[var12] > var14);

                  var15 = var11 - 1;

                  int var16;
                  label229:
                  while(true) {
                     while(true) {
                        ++var15;
                        if (var15 > var12) {
                           break label229;
                        }

                        var16 = var0[var15];
                        if (var16 < var13) {
                           var0[var15] = var0[var11];
                           var0[var11] = var16;
                           ++var11;
                        } else if (var16 > var14) {
                           while(var0[var12] > var14) {
                              if (var12-- == var15) {
                                 break label229;
                              }
                           }

                           if (var0[var12] < var13) {
                              var0[var15] = var0[var11];
                              var0[var11] = var0[var12];
                              ++var11;
                           } else {
                              var0[var15] = var0[var12];
                           }

                           var0[var12] = var16;
                           --var12;
                        }
                     }
                  }

                  var0[var1] = var0[var11 - 1];
                  var0[var11 - 1] = var13;
                  var0[var2] = var0[var12 + 1];
                  var0[var12 + 1] = var14;
                  sort(var0, var1, var11 - 2, var3);
                  sort(var0, var12 + 2, var2, false);
                  if (var11 < var8 && var10 < var12) {
                     while(var0[var11] == var13) {
                        ++var11;
                     }

                     while(var0[var12] == var14) {
                        --var12;
                     }

                     var15 = var11 - 1;

                     label199:
                     while(true) {
                        while(true) {
                           ++var15;
                           if (var15 > var12) {
                              break label199;
                           }

                           var16 = var0[var15];
                           if (var16 == var13) {
                              var0[var15] = var0[var11];
                              var0[var11] = var16;
                              ++var11;
                           } else if (var16 == var14) {
                              while(var0[var12] == var14) {
                                 if (var12-- == var15) {
                                    break label199;
                                 }
                              }

                              if (var0[var12] == var13) {
                                 var0[var15] = var0[var11];
                                 var0[var11] = var13;
                                 ++var11;
                              } else {
                                 var0[var15] = var0[var12];
                              }

                              var0[var12] = var16;
                              --var12;
                           }
                        }
                     }
                  }

                  sort(var0, var11, var12, false);
                  break;
               }
            }
         } else {
            var13 = var0[var6];

            for(var14 = var1; var14 <= var12; ++var14) {
               if (var0[var14] != var13) {
                  var15 = var0[var14];
                  if (var15 < var13) {
                     var0[var14] = var0[var11];
                     var0[var11] = var15;
                     ++var11;
                  } else {
                     while(var0[var12] > var13) {
                        --var12;
                     }

                     if (var0[var12] < var13) {
                        var0[var14] = var0[var11];
                        var0[var11] = var0[var12];
                        ++var11;
                     } else {
                        var0[var14] = var13;
                     }

                     var0[var12] = var15;
                     --var12;
                  }
               }
            }

            sort(var0, var1, var11 - 1, var3);
            sort(var0, var12 + 1, var2, false);
         }

      }
   }

   static void sort(long[] var0, int var1, int var2, long[] var3, int var4, int var5) {
      if (var2 - var1 < 286) {
         sort(var0, var1, var2, true);
      } else {
         int[] var6 = new int[68];
         int var7 = 0;
         var6[0] = var1;

         int var9;
         int var10;
         for(int var8 = var1; var8 < var2; var6[var7] = var8) {
            if (var0[var8] < var0[var8 + 1]) {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] <= var0[var8]);
            } else if (var0[var8] <= var0[var8 + 1]) {
               var9 = 33;

               while(true) {
                  ++var8;
                  if (var8 > var2 || var0[var8 - 1] != var0[var8]) {
                     break;
                  }

                  --var9;
                  if (var9 == 0) {
                     sort(var0, var1, var2, true);
                     return;
                  }
               }
            } else {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] >= var0[var8]);

               var9 = var6[var7] - 1;
               var10 = var8;

               while(true) {
                  ++var9;
                  --var10;
                  if (var9 >= var10) {
                     break;
                  }

                  long var11 = var0[var9];
                  var0[var9] = var0[var10];
                  var0[var10] = var11;
               }
            }

            ++var7;
            if (var7 == 67) {
               sort(var0, var1, var2, true);
               return;
            }
         }

         if (var6[var7] == var2++) {
            ++var7;
            var6[var7] = var2;
         } else if (var7 == 1) {
            return;
         }

         byte var20 = 0;

         for(var9 = 1; (var9 <<= 1) < var7; var20 = (byte)(var20 ^ 1)) {
         }

         int var12 = var2 - var1;
         if (var3 == null || var5 < var12 || var4 + var12 > var3.length) {
            var3 = new long[var12];
            var4 = 0;
         }

         long[] var21;
         int var22;
         if (var20 == 0) {
            System.arraycopy(var0, var1, var3, var4, var12);
            var21 = var0;
            var22 = 0;
            var0 = var3;
            var10 = var4 - var1;
         } else {
            var21 = var3;
            var10 = 0;
            var22 = var4 - var1;
         }

         while(var7 > 1) {
            int var13 = 0;

            int var14;
            int var15;
            for(var14 = 0 + 2; var14 <= var7; var14 += 2) {
               var15 = var6[var14];
               int var16 = var6[var14 - 1];
               int var17 = var6[var14 - 2];
               int var18 = var17;

               for(int var19 = var16; var17 < var15; ++var17) {
                  if (var19 >= var15 || var18 < var16 && var0[var18 + var10] <= var0[var19 + var10]) {
                     var21[var17 + var22] = var0[var18++ + var10];
                  } else {
                     var21[var17 + var22] = var0[var19++ + var10];
                  }
               }

               ++var13;
               var6[var13] = var15;
            }

            if ((var7 & 1) != 0) {
               var14 = var2;
               var15 = var6[var7 - 1];

               while(true) {
                  --var14;
                  if (var14 < var15) {
                     ++var13;
                     var6[var13] = var2;
                     break;
                  }

                  var21[var14 + var22] = var0[var14 + var10];
               }
            }

            long[] var23 = var0;
            var0 = var21;
            var21 = var23;
            var15 = var10;
            var10 = var22;
            var22 = var15;
            var7 = var13;
         }

      }
   }

   private static void sort(long[] var0, int var1, int var2, boolean var3) {
      int var4 = var2 - var1 + 1;
      int var5;
      int var6;
      if (var4 < 47) {
         if (var3) {
            var5 = var1;

            for(var6 = var1; var5 < var2; var6 = var5) {
               long var20 = var0[var5 + 1];

               while(var20 < var0[var6]) {
                  var0[var6 + 1] = var0[var6];
                  if (var6-- == var1) {
                     break;
                  }
               }

               var0[var6 + 1] = var20;
               ++var5;
            }

         } else {
            do {
               if (var1 >= var2) {
                  return;
               }

               ++var1;
            } while(var0[var1] >= var0[var1 - 1]);

            var5 = var1;

            label140:
            while(true) {
               ++var1;
               if (var1 > var2) {
                  long var21 = var0[var2];

                  while(true) {
                     --var2;
                     if (var21 >= var0[var2]) {
                        var0[var2 + 1] = var21;
                        return;
                     }

                     var0[var2 + 1] = var0[var2];
                  }
               }

               long var22 = var0[var5];
               long var23 = var0[var1];
               if (var22 < var23) {
                  var23 = var22;
                  var22 = var0[var1];
               }

               while(true) {
                  --var5;
                  if (var22 >= var0[var5]) {
                     ++var5;
                     var0[var5 + 1] = var22;

                     while(true) {
                        --var5;
                        if (var23 >= var0[var5]) {
                           var0[var5 + 1] = var23;
                           ++var1;
                           var5 = var1;
                           continue label140;
                        }

                        var0[var5 + 1] = var0[var5];
                     }
                  }

                  var0[var5 + 2] = var0[var5];
               }
            }
         }
      } else {
         var5 = (var4 >> 3) + (var4 >> 6) + 1;
         var6 = var1 + var2 >>> 1;
         int var7 = var6 - var5;
         int var8 = var7 - var5;
         int var9 = var6 + var5;
         int var10 = var9 + var5;
         long var11;
         if (var0[var7] < var0[var8]) {
            var11 = var0[var7];
            var0[var7] = var0[var8];
            var0[var8] = var11;
         }

         if (var0[var6] < var0[var7]) {
            var11 = var0[var6];
            var0[var6] = var0[var7];
            var0[var7] = var11;
            if (var11 < var0[var8]) {
               var0[var7] = var0[var8];
               var0[var8] = var11;
            }
         }

         if (var0[var9] < var0[var6]) {
            var11 = var0[var9];
            var0[var9] = var0[var6];
            var0[var6] = var11;
            if (var11 < var0[var7]) {
               var0[var6] = var0[var7];
               var0[var7] = var11;
               if (var11 < var0[var8]) {
                  var0[var7] = var0[var8];
                  var0[var8] = var11;
               }
            }
         }

         if (var0[var10] < var0[var9]) {
            var11 = var0[var10];
            var0[var10] = var0[var9];
            var0[var9] = var11;
            if (var11 < var0[var6]) {
               var0[var9] = var0[var6];
               var0[var6] = var11;
               if (var11 < var0[var7]) {
                  var0[var6] = var0[var7];
                  var0[var7] = var11;
                  if (var11 < var0[var8]) {
                     var0[var7] = var0[var8];
                     var0[var8] = var11;
                  }
               }
            }
         }

         int var24 = var1;
         int var12 = var2;
         long var13;
         if (var0[var8] != var0[var7] && var0[var7] != var0[var6] && var0[var6] != var0[var9] && var0[var9] != var0[var10]) {
            var13 = var0[var7];
            long var25 = var0[var9];
            var0[var7] = var0[var1];
            var0[var9] = var0[var2];

            while(true) {
               ++var24;
               if (var0[var24] >= var13) {
                  do {
                     --var12;
                  } while(var0[var12] > var25);

                  int var17 = var24 - 1;

                  long var18;
                  label229:
                  while(true) {
                     while(true) {
                        ++var17;
                        if (var17 > var12) {
                           break label229;
                        }

                        var18 = var0[var17];
                        if (var18 < var13) {
                           var0[var17] = var0[var24];
                           var0[var24] = var18;
                           ++var24;
                        } else if (var18 > var25) {
                           while(var0[var12] > var25) {
                              if (var12-- == var17) {
                                 break label229;
                              }
                           }

                           if (var0[var12] < var13) {
                              var0[var17] = var0[var24];
                              var0[var24] = var0[var12];
                              ++var24;
                           } else {
                              var0[var17] = var0[var12];
                           }

                           var0[var12] = var18;
                           --var12;
                        }
                     }
                  }

                  var0[var1] = var0[var24 - 1];
                  var0[var24 - 1] = var13;
                  var0[var2] = var0[var12 + 1];
                  var0[var12 + 1] = var25;
                  sort(var0, var1, var24 - 2, var3);
                  sort(var0, var12 + 2, var2, false);
                  if (var24 < var8 && var10 < var12) {
                     while(var0[var24] == var13) {
                        ++var24;
                     }

                     while(var0[var12] == var25) {
                        --var12;
                     }

                     var17 = var24 - 1;

                     label199:
                     while(true) {
                        while(true) {
                           ++var17;
                           if (var17 > var12) {
                              break label199;
                           }

                           var18 = var0[var17];
                           if (var18 == var13) {
                              var0[var17] = var0[var24];
                              var0[var24] = var18;
                              ++var24;
                           } else if (var18 == var25) {
                              while(var0[var12] == var25) {
                                 if (var12-- == var17) {
                                    break label199;
                                 }
                              }

                              if (var0[var12] == var13) {
                                 var0[var17] = var0[var24];
                                 var0[var24] = var13;
                                 ++var24;
                              } else {
                                 var0[var17] = var0[var12];
                              }

                              var0[var12] = var18;
                              --var12;
                           }
                        }
                     }
                  }

                  sort(var0, var24, var12, false);
                  break;
               }
            }
         } else {
            var13 = var0[var6];

            for(int var15 = var1; var15 <= var12; ++var15) {
               if (var0[var15] != var13) {
                  long var16 = var0[var15];
                  if (var16 < var13) {
                     var0[var15] = var0[var24];
                     var0[var24] = var16;
                     ++var24;
                  } else {
                     while(var0[var12] > var13) {
                        --var12;
                     }

                     if (var0[var12] < var13) {
                        var0[var15] = var0[var24];
                        var0[var24] = var0[var12];
                        ++var24;
                     } else {
                        var0[var15] = var13;
                     }

                     var0[var12] = var16;
                     --var12;
                  }
               }
            }

            sort(var0, var1, var24 - 1, var3);
            sort(var0, var12 + 1, var2, false);
         }

      }
   }

   static void sort(short[] var0, int var1, int var2, short[] var3, int var4, int var5) {
      if (var2 - var1 > 3200) {
         int[] var6 = new int[65536];
         int var7 = var1 - 1;

         while(true) {
            ++var7;
            if (var7 > var2) {
               var7 = 65536;
               int var8 = var2 + 1;

               while(var8 > var1) {
                  do {
                     --var7;
                  } while(var6[var7] == 0);

                  short var9 = (short)(var7 + -32768);
                  int var10 = var6[var7];

                  while(true) {
                     --var8;
                     var0[var8] = var9;
                     --var10;
                     if (var10 <= 0) {
                        break;
                     }
                  }
               }
               break;
            }

            ++var6[var0[var7] - -32768];
         }
      } else {
         doSort(var0, var1, var2, var3, var4, var5);
      }

   }

   private static void doSort(short[] var0, int var1, int var2, short[] var3, int var4, int var5) {
      if (var2 - var1 < 286) {
         sort(var0, var1, var2, true);
      } else {
         int[] var6 = new int[68];
         int var7 = 0;
         var6[0] = var1;

         int var9;
         int var10;
         for(int var8 = var1; var8 < var2; var6[var7] = var8) {
            if (var0[var8] < var0[var8 + 1]) {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] <= var0[var8]);
            } else if (var0[var8] <= var0[var8 + 1]) {
               var9 = 33;

               while(true) {
                  ++var8;
                  if (var8 > var2 || var0[var8 - 1] != var0[var8]) {
                     break;
                  }

                  --var9;
                  if (var9 == 0) {
                     sort(var0, var1, var2, true);
                     return;
                  }
               }
            } else {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] >= var0[var8]);

               var9 = var6[var7] - 1;
               var10 = var8;

               while(true) {
                  ++var9;
                  --var10;
                  if (var9 >= var10) {
                     break;
                  }

                  short var11 = var0[var9];
                  var0[var9] = var0[var10];
                  var0[var10] = var11;
               }
            }

            ++var7;
            if (var7 == 67) {
               sort(var0, var1, var2, true);
               return;
            }
         }

         if (var6[var7] == var2++) {
            ++var7;
            var6[var7] = var2;
         } else if (var7 == 1) {
            return;
         }

         byte var20 = 0;

         for(var9 = 1; (var9 <<= 1) < var7; var20 = (byte)(var20 ^ 1)) {
         }

         int var12 = var2 - var1;
         if (var3 == null || var5 < var12 || var4 + var12 > var3.length) {
            var3 = new short[var12];
            var4 = 0;
         }

         short[] var21;
         int var22;
         if (var20 == 0) {
            System.arraycopy(var0, var1, var3, var4, var12);
            var21 = var0;
            var22 = 0;
            var0 = var3;
            var10 = var4 - var1;
         } else {
            var21 = var3;
            var10 = 0;
            var22 = var4 - var1;
         }

         while(var7 > 1) {
            int var13 = 0;

            int var14;
            int var15;
            for(var14 = 0 + 2; var14 <= var7; var14 += 2) {
               var15 = var6[var14];
               int var16 = var6[var14 - 1];
               int var17 = var6[var14 - 2];
               int var18 = var17;

               for(int var19 = var16; var17 < var15; ++var17) {
                  if (var19 >= var15 || var18 < var16 && var0[var18 + var10] <= var0[var19 + var10]) {
                     var21[var17 + var22] = var0[var18++ + var10];
                  } else {
                     var21[var17 + var22] = var0[var19++ + var10];
                  }
               }

               ++var13;
               var6[var13] = var15;
            }

            if ((var7 & 1) != 0) {
               var14 = var2;
               var15 = var6[var7 - 1];

               while(true) {
                  --var14;
                  if (var14 < var15) {
                     ++var13;
                     var6[var13] = var2;
                     break;
                  }

                  var21[var14 + var22] = var0[var14 + var10];
               }
            }

            short[] var23 = var0;
            var0 = var21;
            var21 = var23;
            var15 = var10;
            var10 = var22;
            var22 = var15;
            var7 = var13;
         }

      }
   }

   private static void sort(short[] var0, int var1, int var2, boolean var3) {
      int var4 = var2 - var1 + 1;
      int var5;
      int var6;
      if (var4 < 47) {
         short var17;
         if (var3) {
            var5 = var1;

            for(var6 = var1; var5 < var2; var6 = var5) {
               var17 = var0[var5 + 1];

               while(var17 < var0[var6]) {
                  var0[var6 + 1] = var0[var6];
                  if (var6-- == var1) {
                     break;
                  }
               }

               var0[var6 + 1] = var17;
               ++var5;
            }

         } else {
            do {
               if (var1 >= var2) {
                  return;
               }

               ++var1;
            } while(var0[var1] >= var0[var1 - 1]);

            var5 = var1;

            label140:
            while(true) {
               ++var1;
               if (var1 > var2) {
                  short var18 = var0[var2];

                  while(true) {
                     --var2;
                     if (var18 >= var0[var2]) {
                        var0[var2 + 1] = var18;
                        return;
                     }

                     var0[var2 + 1] = var0[var2];
                  }
               }

               short var19 = var0[var5];
               var17 = var0[var1];
               if (var19 < var17) {
                  var17 = var19;
                  var19 = var0[var1];
               }

               while(true) {
                  --var5;
                  if (var19 >= var0[var5]) {
                     ++var5;
                     var0[var5 + 1] = var19;

                     while(true) {
                        --var5;
                        if (var17 >= var0[var5]) {
                           var0[var5 + 1] = var17;
                           ++var1;
                           var5 = var1;
                           continue label140;
                        }

                        var0[var5 + 1] = var0[var5];
                     }
                  }

                  var0[var5 + 2] = var0[var5];
               }
            }
         }
      } else {
         var5 = (var4 >> 3) + (var4 >> 6) + 1;
         var6 = var1 + var2 >>> 1;
         int var7 = var6 - var5;
         int var8 = var7 - var5;
         int var9 = var6 + var5;
         int var10 = var9 + var5;
         short var11;
         if (var0[var7] < var0[var8]) {
            var11 = var0[var7];
            var0[var7] = var0[var8];
            var0[var8] = var11;
         }

         if (var0[var6] < var0[var7]) {
            var11 = var0[var6];
            var0[var6] = var0[var7];
            var0[var7] = var11;
            if (var11 < var0[var8]) {
               var0[var7] = var0[var8];
               var0[var8] = var11;
            }
         }

         if (var0[var9] < var0[var6]) {
            var11 = var0[var9];
            var0[var9] = var0[var6];
            var0[var6] = var11;
            if (var11 < var0[var7]) {
               var0[var6] = var0[var7];
               var0[var7] = var11;
               if (var11 < var0[var8]) {
                  var0[var7] = var0[var8];
                  var0[var8] = var11;
               }
            }
         }

         if (var0[var10] < var0[var9]) {
            var11 = var0[var10];
            var0[var10] = var0[var9];
            var0[var9] = var11;
            if (var11 < var0[var6]) {
               var0[var9] = var0[var6];
               var0[var6] = var11;
               if (var11 < var0[var7]) {
                  var0[var6] = var0[var7];
                  var0[var7] = var11;
                  if (var11 < var0[var8]) {
                     var0[var7] = var0[var8];
                     var0[var8] = var11;
                  }
               }
            }
         }

         int var20 = var1;
         int var12 = var2;
         short var13;
         if (var0[var8] != var0[var7] && var0[var7] != var0[var6] && var0[var6] != var0[var9] && var0[var9] != var0[var10]) {
            var13 = var0[var7];
            short var21 = var0[var9];
            var0[var7] = var0[var1];
            var0[var9] = var0[var2];

            while(true) {
               ++var20;
               if (var0[var20] >= var13) {
                  do {
                     --var12;
                  } while(var0[var12] > var21);

                  int var22 = var20 - 1;

                  short var16;
                  label229:
                  while(true) {
                     while(true) {
                        ++var22;
                        if (var22 > var12) {
                           break label229;
                        }

                        var16 = var0[var22];
                        if (var16 < var13) {
                           var0[var22] = var0[var20];
                           var0[var20] = var16;
                           ++var20;
                        } else if (var16 > var21) {
                           while(var0[var12] > var21) {
                              if (var12-- == var22) {
                                 break label229;
                              }
                           }

                           if (var0[var12] < var13) {
                              var0[var22] = var0[var20];
                              var0[var20] = var0[var12];
                              ++var20;
                           } else {
                              var0[var22] = var0[var12];
                           }

                           var0[var12] = var16;
                           --var12;
                        }
                     }
                  }

                  var0[var1] = var0[var20 - 1];
                  var0[var20 - 1] = var13;
                  var0[var2] = var0[var12 + 1];
                  var0[var12 + 1] = var21;
                  sort(var0, var1, var20 - 2, var3);
                  sort(var0, var12 + 2, var2, false);
                  if (var20 < var8 && var10 < var12) {
                     while(var0[var20] == var13) {
                        ++var20;
                     }

                     while(var0[var12] == var21) {
                        --var12;
                     }

                     var22 = var20 - 1;

                     label199:
                     while(true) {
                        while(true) {
                           ++var22;
                           if (var22 > var12) {
                              break label199;
                           }

                           var16 = var0[var22];
                           if (var16 == var13) {
                              var0[var22] = var0[var20];
                              var0[var20] = var16;
                              ++var20;
                           } else if (var16 == var21) {
                              while(var0[var12] == var21) {
                                 if (var12-- == var22) {
                                    break label199;
                                 }
                              }

                              if (var0[var12] == var13) {
                                 var0[var22] = var0[var20];
                                 var0[var20] = var13;
                                 ++var20;
                              } else {
                                 var0[var22] = var0[var12];
                              }

                              var0[var12] = var16;
                              --var12;
                           }
                        }
                     }
                  }

                  sort(var0, var20, var12, false);
                  break;
               }
            }
         } else {
            var13 = var0[var6];

            for(int var14 = var1; var14 <= var12; ++var14) {
               if (var0[var14] != var13) {
                  short var15 = var0[var14];
                  if (var15 < var13) {
                     var0[var14] = var0[var20];
                     var0[var20] = var15;
                     ++var20;
                  } else {
                     while(var0[var12] > var13) {
                        --var12;
                     }

                     if (var0[var12] < var13) {
                        var0[var14] = var0[var20];
                        var0[var20] = var0[var12];
                        ++var20;
                     } else {
                        var0[var14] = var13;
                     }

                     var0[var12] = var15;
                     --var12;
                  }
               }
            }

            sort(var0, var1, var20 - 1, var3);
            sort(var0, var12 + 1, var2, false);
         }

      }
   }

   static void sort(char[] var0, int var1, int var2, char[] var3, int var4, int var5) {
      if (var2 - var1 > 3200) {
         int[] var6 = new int[65536];
         int var7 = var1 - 1;

         while(true) {
            ++var7;
            if (var7 > var2) {
               var7 = 65536;
               int var8 = var2 + 1;

               while(var8 > var1) {
                  do {
                     --var7;
                  } while(var6[var7] == 0);

                  char var9 = (char)var7;
                  int var10 = var6[var7];

                  while(true) {
                     --var8;
                     var0[var8] = var9;
                     --var10;
                     if (var10 <= 0) {
                        break;
                     }
                  }
               }
               break;
            }

            ++var6[var0[var7]];
         }
      } else {
         doSort(var0, var1, var2, var3, var4, var5);
      }

   }

   private static void doSort(char[] var0, int var1, int var2, char[] var3, int var4, int var5) {
      if (var2 - var1 < 286) {
         sort(var0, var1, var2, true);
      } else {
         int[] var6 = new int[68];
         int var7 = 0;
         var6[0] = var1;

         int var9;
         int var10;
         for(int var8 = var1; var8 < var2; var6[var7] = var8) {
            if (var0[var8] < var0[var8 + 1]) {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] <= var0[var8]);
            } else if (var0[var8] <= var0[var8 + 1]) {
               var9 = 33;

               while(true) {
                  ++var8;
                  if (var8 > var2 || var0[var8 - 1] != var0[var8]) {
                     break;
                  }

                  --var9;
                  if (var9 == 0) {
                     sort(var0, var1, var2, true);
                     return;
                  }
               }
            } else {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] >= var0[var8]);

               var9 = var6[var7] - 1;
               var10 = var8;

               while(true) {
                  ++var9;
                  --var10;
                  if (var9 >= var10) {
                     break;
                  }

                  char var11 = var0[var9];
                  var0[var9] = var0[var10];
                  var0[var10] = var11;
               }
            }

            ++var7;
            if (var7 == 67) {
               sort(var0, var1, var2, true);
               return;
            }
         }

         if (var6[var7] == var2++) {
            ++var7;
            var6[var7] = var2;
         } else if (var7 == 1) {
            return;
         }

         byte var20 = 0;

         for(var9 = 1; (var9 <<= 1) < var7; var20 = (byte)(var20 ^ 1)) {
         }

         int var12 = var2 - var1;
         if (var3 == null || var5 < var12 || var4 + var12 > var3.length) {
            var3 = new char[var12];
            var4 = 0;
         }

         char[] var21;
         int var22;
         if (var20 == 0) {
            System.arraycopy(var0, var1, var3, var4, var12);
            var21 = var0;
            var22 = 0;
            var0 = var3;
            var10 = var4 - var1;
         } else {
            var21 = var3;
            var10 = 0;
            var22 = var4 - var1;
         }

         while(var7 > 1) {
            int var13 = 0;

            int var14;
            int var15;
            for(var14 = 0 + 2; var14 <= var7; var14 += 2) {
               var15 = var6[var14];
               int var16 = var6[var14 - 1];
               int var17 = var6[var14 - 2];
               int var18 = var17;

               for(int var19 = var16; var17 < var15; ++var17) {
                  if (var19 >= var15 || var18 < var16 && var0[var18 + var10] <= var0[var19 + var10]) {
                     var21[var17 + var22] = var0[var18++ + var10];
                  } else {
                     var21[var17 + var22] = var0[var19++ + var10];
                  }
               }

               ++var13;
               var6[var13] = var15;
            }

            if ((var7 & 1) != 0) {
               var14 = var2;
               var15 = var6[var7 - 1];

               while(true) {
                  --var14;
                  if (var14 < var15) {
                     ++var13;
                     var6[var13] = var2;
                     break;
                  }

                  var21[var14 + var22] = var0[var14 + var10];
               }
            }

            char[] var23 = var0;
            var0 = var21;
            var21 = var23;
            var15 = var10;
            var10 = var22;
            var22 = var15;
            var7 = var13;
         }

      }
   }

   private static void sort(char[] var0, int var1, int var2, boolean var3) {
      int var4 = var2 - var1 + 1;
      int var5;
      int var6;
      if (var4 < 47) {
         char var17;
         if (var3) {
            var5 = var1;

            for(var6 = var1; var5 < var2; var6 = var5) {
               var17 = var0[var5 + 1];

               while(var17 < var0[var6]) {
                  var0[var6 + 1] = var0[var6];
                  if (var6-- == var1) {
                     break;
                  }
               }

               var0[var6 + 1] = var17;
               ++var5;
            }

         } else {
            do {
               if (var1 >= var2) {
                  return;
               }

               ++var1;
            } while(var0[var1] >= var0[var1 - 1]);

            var5 = var1;

            label140:
            while(true) {
               ++var1;
               if (var1 > var2) {
                  char var18 = var0[var2];

                  while(true) {
                     --var2;
                     if (var18 >= var0[var2]) {
                        var0[var2 + 1] = var18;
                        return;
                     }

                     var0[var2 + 1] = var0[var2];
                  }
               }

               char var19 = var0[var5];
               var17 = var0[var1];
               if (var19 < var17) {
                  var17 = var19;
                  var19 = var0[var1];
               }

               while(true) {
                  --var5;
                  if (var19 >= var0[var5]) {
                     ++var5;
                     var0[var5 + 1] = var19;

                     while(true) {
                        --var5;
                        if (var17 >= var0[var5]) {
                           var0[var5 + 1] = var17;
                           ++var1;
                           var5 = var1;
                           continue label140;
                        }

                        var0[var5 + 1] = var0[var5];
                     }
                  }

                  var0[var5 + 2] = var0[var5];
               }
            }
         }
      } else {
         var5 = (var4 >> 3) + (var4 >> 6) + 1;
         var6 = var1 + var2 >>> 1;
         int var7 = var6 - var5;
         int var8 = var7 - var5;
         int var9 = var6 + var5;
         int var10 = var9 + var5;
         char var11;
         if (var0[var7] < var0[var8]) {
            var11 = var0[var7];
            var0[var7] = var0[var8];
            var0[var8] = var11;
         }

         if (var0[var6] < var0[var7]) {
            var11 = var0[var6];
            var0[var6] = var0[var7];
            var0[var7] = var11;
            if (var11 < var0[var8]) {
               var0[var7] = var0[var8];
               var0[var8] = var11;
            }
         }

         if (var0[var9] < var0[var6]) {
            var11 = var0[var9];
            var0[var9] = var0[var6];
            var0[var6] = var11;
            if (var11 < var0[var7]) {
               var0[var6] = var0[var7];
               var0[var7] = var11;
               if (var11 < var0[var8]) {
                  var0[var7] = var0[var8];
                  var0[var8] = var11;
               }
            }
         }

         if (var0[var10] < var0[var9]) {
            var11 = var0[var10];
            var0[var10] = var0[var9];
            var0[var9] = var11;
            if (var11 < var0[var6]) {
               var0[var9] = var0[var6];
               var0[var6] = var11;
               if (var11 < var0[var7]) {
                  var0[var6] = var0[var7];
                  var0[var7] = var11;
                  if (var11 < var0[var8]) {
                     var0[var7] = var0[var8];
                     var0[var8] = var11;
                  }
               }
            }
         }

         int var20 = var1;
         int var12 = var2;
         char var13;
         if (var0[var8] != var0[var7] && var0[var7] != var0[var6] && var0[var6] != var0[var9] && var0[var9] != var0[var10]) {
            var13 = var0[var7];
            char var21 = var0[var9];
            var0[var7] = var0[var1];
            var0[var9] = var0[var2];

            while(true) {
               ++var20;
               if (var0[var20] >= var13) {
                  do {
                     --var12;
                  } while(var0[var12] > var21);

                  int var22 = var20 - 1;

                  char var16;
                  label229:
                  while(true) {
                     while(true) {
                        ++var22;
                        if (var22 > var12) {
                           break label229;
                        }

                        var16 = var0[var22];
                        if (var16 < var13) {
                           var0[var22] = var0[var20];
                           var0[var20] = var16;
                           ++var20;
                        } else if (var16 > var21) {
                           while(var0[var12] > var21) {
                              if (var12-- == var22) {
                                 break label229;
                              }
                           }

                           if (var0[var12] < var13) {
                              var0[var22] = var0[var20];
                              var0[var20] = var0[var12];
                              ++var20;
                           } else {
                              var0[var22] = var0[var12];
                           }

                           var0[var12] = var16;
                           --var12;
                        }
                     }
                  }

                  var0[var1] = var0[var20 - 1];
                  var0[var20 - 1] = var13;
                  var0[var2] = var0[var12 + 1];
                  var0[var12 + 1] = var21;
                  sort(var0, var1, var20 - 2, var3);
                  sort(var0, var12 + 2, var2, false);
                  if (var20 < var8 && var10 < var12) {
                     while(var0[var20] == var13) {
                        ++var20;
                     }

                     while(var0[var12] == var21) {
                        --var12;
                     }

                     var22 = var20 - 1;

                     label199:
                     while(true) {
                        while(true) {
                           ++var22;
                           if (var22 > var12) {
                              break label199;
                           }

                           var16 = var0[var22];
                           if (var16 == var13) {
                              var0[var22] = var0[var20];
                              var0[var20] = var16;
                              ++var20;
                           } else if (var16 == var21) {
                              while(var0[var12] == var21) {
                                 if (var12-- == var22) {
                                    break label199;
                                 }
                              }

                              if (var0[var12] == var13) {
                                 var0[var22] = var0[var20];
                                 var0[var20] = var13;
                                 ++var20;
                              } else {
                                 var0[var22] = var0[var12];
                              }

                              var0[var12] = var16;
                              --var12;
                           }
                        }
                     }
                  }

                  sort(var0, var20, var12, false);
                  break;
               }
            }
         } else {
            var13 = var0[var6];

            for(int var14 = var1; var14 <= var12; ++var14) {
               if (var0[var14] != var13) {
                  char var15 = var0[var14];
                  if (var15 < var13) {
                     var0[var14] = var0[var20];
                     var0[var20] = var15;
                     ++var20;
                  } else {
                     while(var0[var12] > var13) {
                        --var12;
                     }

                     if (var0[var12] < var13) {
                        var0[var14] = var0[var20];
                        var0[var20] = var0[var12];
                        ++var20;
                     } else {
                        var0[var14] = var13;
                     }

                     var0[var12] = var15;
                     --var12;
                  }
               }
            }

            sort(var0, var1, var20 - 1, var3);
            sort(var0, var12 + 1, var2, false);
         }

      }
   }

   static void sort(byte[] var0, int var1, int var2) {
      int var4;
      if (var2 - var1 > 29) {
         int[] var3 = new int[256];
         var4 = var1 - 1;

         while(true) {
            ++var4;
            if (var4 > var2) {
               var4 = 256;
               int var5 = var2 + 1;

               while(var5 > var1) {
                  do {
                     --var4;
                  } while(var3[var4] == 0);

                  byte var6 = (byte)(var4 + -128);
                  int var7 = var3[var4];

                  while(true) {
                     --var5;
                     var0[var5] = var6;
                     --var7;
                     if (var7 <= 0) {
                        break;
                     }
                  }
               }
               break;
            }

            ++var3[var0[var4] - -128];
         }
      } else {
         int var8 = var1;

         for(var4 = var1; var8 < var2; var4 = var8) {
            byte var9 = var0[var8 + 1];

            while(var9 < var0[var4]) {
               var0[var4 + 1] = var0[var4];
               if (var4-- == var1) {
                  break;
               }
            }

            var0[var4 + 1] = var9;
            ++var8;
         }
      }

   }

   static void sort(float[] var0, int var1, int var2, float[] var3, int var4, int var5) {
      while(var1 <= var2 && Float.isNaN(var0[var2])) {
         --var2;
      }

      int var6 = var2;

      while(true) {
         --var6;
         if (var6 < var1) {
            doSort(var0, var1, var2, var3, var4, var5);
            var6 = var2;

            int var10;
            while(var1 < var6) {
               var10 = var1 + var6 >>> 1;
               float var8 = var0[var10];
               if (var8 < 0.0F) {
                  var1 = var10 + 1;
               } else {
                  var6 = var10;
               }
            }

            while(var1 <= var2 && Float.floatToRawIntBits(var0[var1]) < 0) {
               ++var1;
            }

            var10 = var1;
            int var11 = var1 - 1;

            while(true) {
               ++var10;
               if (var10 > var2) {
                  break;
               }

               float var9 = var0[var10];
               if (var9 != 0.0F) {
                  break;
               }

               if (Float.floatToRawIntBits(var9) < 0) {
                  var0[var10] = 0.0F;
                  ++var11;
                  var0[var11] = -0.0F;
               }
            }

            return;
         }

         float var7 = var0[var6];
         if (var7 != var7) {
            var0[var6] = var0[var2];
            var0[var2] = var7;
            --var2;
         }
      }
   }

   private static void doSort(float[] var0, int var1, int var2, float[] var3, int var4, int var5) {
      if (var2 - var1 < 286) {
         sort(var0, var1, var2, true);
      } else {
         int[] var6 = new int[68];
         int var7 = 0;
         var6[0] = var1;

         int var9;
         int var10;
         for(int var8 = var1; var8 < var2; var6[var7] = var8) {
            if (var0[var8] < var0[var8 + 1]) {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] <= var0[var8]);
            } else if (var0[var8] <= var0[var8 + 1]) {
               var9 = 33;

               while(true) {
                  ++var8;
                  if (var8 > var2 || var0[var8 - 1] != var0[var8]) {
                     break;
                  }

                  --var9;
                  if (var9 == 0) {
                     sort(var0, var1, var2, true);
                     return;
                  }
               }
            } else {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] >= var0[var8]);

               var9 = var6[var7] - 1;
               var10 = var8;

               while(true) {
                  ++var9;
                  --var10;
                  if (var9 >= var10) {
                     break;
                  }

                  float var11 = var0[var9];
                  var0[var9] = var0[var10];
                  var0[var10] = var11;
               }
            }

            ++var7;
            if (var7 == 67) {
               sort(var0, var1, var2, true);
               return;
            }
         }

         if (var6[var7] == var2++) {
            ++var7;
            var6[var7] = var2;
         } else if (var7 == 1) {
            return;
         }

         byte var20 = 0;

         for(var9 = 1; (var9 <<= 1) < var7; var20 = (byte)(var20 ^ 1)) {
         }

         int var12 = var2 - var1;
         if (var3 == null || var5 < var12 || var4 + var12 > var3.length) {
            var3 = new float[var12];
            var4 = 0;
         }

         float[] var21;
         int var22;
         if (var20 == 0) {
            System.arraycopy(var0, var1, var3, var4, var12);
            var21 = var0;
            var22 = 0;
            var0 = var3;
            var10 = var4 - var1;
         } else {
            var21 = var3;
            var10 = 0;
            var22 = var4 - var1;
         }

         while(var7 > 1) {
            int var13 = 0;

            int var14;
            int var15;
            for(var14 = 0 + 2; var14 <= var7; var14 += 2) {
               var15 = var6[var14];
               int var16 = var6[var14 - 1];
               int var17 = var6[var14 - 2];
               int var18 = var17;

               for(int var19 = var16; var17 < var15; ++var17) {
                  if (var19 >= var15 || var18 < var16 && var0[var18 + var10] <= var0[var19 + var10]) {
                     var21[var17 + var22] = var0[var18++ + var10];
                  } else {
                     var21[var17 + var22] = var0[var19++ + var10];
                  }
               }

               ++var13;
               var6[var13] = var15;
            }

            if ((var7 & 1) != 0) {
               var14 = var2;
               var15 = var6[var7 - 1];

               while(true) {
                  --var14;
                  if (var14 < var15) {
                     ++var13;
                     var6[var13] = var2;
                     break;
                  }

                  var21[var14 + var22] = var0[var14 + var10];
               }
            }

            float[] var23 = var0;
            var0 = var21;
            var21 = var23;
            var15 = var10;
            var10 = var22;
            var22 = var15;
            var7 = var13;
         }

      }
   }

   private static void sort(float[] var0, int var1, int var2, boolean var3) {
      int var4 = var2 - var1 + 1;
      int var5;
      int var6;
      if (var4 < 47) {
         float var17;
         if (var3) {
            var5 = var1;

            for(var6 = var1; var5 < var2; var6 = var5) {
               var17 = var0[var5 + 1];

               while(var17 < var0[var6]) {
                  var0[var6 + 1] = var0[var6];
                  if (var6-- == var1) {
                     break;
                  }
               }

               var0[var6 + 1] = var17;
               ++var5;
            }

         } else {
            do {
               if (var1 >= var2) {
                  return;
               }

               ++var1;
            } while(var0[var1] >= var0[var1 - 1]);

            var5 = var1;

            label140:
            while(true) {
               ++var1;
               if (var1 > var2) {
                  float var18 = var0[var2];

                  while(true) {
                     --var2;
                     if (var18 >= var0[var2]) {
                        var0[var2 + 1] = var18;
                        return;
                     }

                     var0[var2 + 1] = var0[var2];
                  }
               }

               float var19 = var0[var5];
               var17 = var0[var1];
               if (var19 < var17) {
                  var17 = var19;
                  var19 = var0[var1];
               }

               while(true) {
                  --var5;
                  if (var19 >= var0[var5]) {
                     ++var5;
                     var0[var5 + 1] = var19;

                     while(true) {
                        --var5;
                        if (var17 >= var0[var5]) {
                           var0[var5 + 1] = var17;
                           ++var1;
                           var5 = var1;
                           continue label140;
                        }

                        var0[var5 + 1] = var0[var5];
                     }
                  }

                  var0[var5 + 2] = var0[var5];
               }
            }
         }
      } else {
         var5 = (var4 >> 3) + (var4 >> 6) + 1;
         var6 = var1 + var2 >>> 1;
         int var7 = var6 - var5;
         int var8 = var7 - var5;
         int var9 = var6 + var5;
         int var10 = var9 + var5;
         float var11;
         if (var0[var7] < var0[var8]) {
            var11 = var0[var7];
            var0[var7] = var0[var8];
            var0[var8] = var11;
         }

         if (var0[var6] < var0[var7]) {
            var11 = var0[var6];
            var0[var6] = var0[var7];
            var0[var7] = var11;
            if (var11 < var0[var8]) {
               var0[var7] = var0[var8];
               var0[var8] = var11;
            }
         }

         if (var0[var9] < var0[var6]) {
            var11 = var0[var9];
            var0[var9] = var0[var6];
            var0[var6] = var11;
            if (var11 < var0[var7]) {
               var0[var6] = var0[var7];
               var0[var7] = var11;
               if (var11 < var0[var8]) {
                  var0[var7] = var0[var8];
                  var0[var8] = var11;
               }
            }
         }

         if (var0[var10] < var0[var9]) {
            var11 = var0[var10];
            var0[var10] = var0[var9];
            var0[var9] = var11;
            if (var11 < var0[var6]) {
               var0[var9] = var0[var6];
               var0[var6] = var11;
               if (var11 < var0[var7]) {
                  var0[var6] = var0[var7];
                  var0[var7] = var11;
                  if (var11 < var0[var8]) {
                     var0[var7] = var0[var8];
                     var0[var8] = var11;
                  }
               }
            }
         }

         int var20 = var1;
         int var12 = var2;
         float var13;
         if (var0[var8] != var0[var7] && var0[var7] != var0[var6] && var0[var6] != var0[var9] && var0[var9] != var0[var10]) {
            var13 = var0[var7];
            float var21 = var0[var9];
            var0[var7] = var0[var1];
            var0[var9] = var0[var2];

            while(true) {
               ++var20;
               if (var0[var20] >= var13) {
                  do {
                     --var12;
                  } while(var0[var12] > var21);

                  int var22 = var20 - 1;

                  float var16;
                  label229:
                  while(true) {
                     while(true) {
                        ++var22;
                        if (var22 > var12) {
                           break label229;
                        }

                        var16 = var0[var22];
                        if (var16 < var13) {
                           var0[var22] = var0[var20];
                           var0[var20] = var16;
                           ++var20;
                        } else if (var16 > var21) {
                           while(var0[var12] > var21) {
                              if (var12-- == var22) {
                                 break label229;
                              }
                           }

                           if (var0[var12] < var13) {
                              var0[var22] = var0[var20];
                              var0[var20] = var0[var12];
                              ++var20;
                           } else {
                              var0[var22] = var0[var12];
                           }

                           var0[var12] = var16;
                           --var12;
                        }
                     }
                  }

                  var0[var1] = var0[var20 - 1];
                  var0[var20 - 1] = var13;
                  var0[var2] = var0[var12 + 1];
                  var0[var12 + 1] = var21;
                  sort(var0, var1, var20 - 2, var3);
                  sort(var0, var12 + 2, var2, false);
                  if (var20 < var8 && var10 < var12) {
                     while(var0[var20] == var13) {
                        ++var20;
                     }

                     while(var0[var12] == var21) {
                        --var12;
                     }

                     var22 = var20 - 1;

                     label199:
                     while(true) {
                        while(true) {
                           ++var22;
                           if (var22 > var12) {
                              break label199;
                           }

                           var16 = var0[var22];
                           if (var16 == var13) {
                              var0[var22] = var0[var20];
                              var0[var20] = var16;
                              ++var20;
                           } else if (var16 == var21) {
                              while(var0[var12] == var21) {
                                 if (var12-- == var22) {
                                    break label199;
                                 }
                              }

                              if (var0[var12] == var13) {
                                 var0[var22] = var0[var20];
                                 var0[var20] = var0[var12];
                                 ++var20;
                              } else {
                                 var0[var22] = var0[var12];
                              }

                              var0[var12] = var16;
                              --var12;
                           }
                        }
                     }
                  }

                  sort(var0, var20, var12, false);
                  break;
               }
            }
         } else {
            var13 = var0[var6];

            for(int var14 = var1; var14 <= var12; ++var14) {
               if (var0[var14] != var13) {
                  float var15 = var0[var14];
                  if (var15 < var13) {
                     var0[var14] = var0[var20];
                     var0[var20] = var15;
                     ++var20;
                  } else {
                     while(var0[var12] > var13) {
                        --var12;
                     }

                     if (var0[var12] < var13) {
                        var0[var14] = var0[var20];
                        var0[var20] = var0[var12];
                        ++var20;
                     } else {
                        var0[var14] = var0[var12];
                     }

                     var0[var12] = var15;
                     --var12;
                  }
               }
            }

            sort(var0, var1, var20 - 1, var3);
            sort(var0, var12 + 1, var2, false);
         }

      }
   }

   static void sort(double[] var0, int var1, int var2, double[] var3, int var4, int var5) {
      while(var1 <= var2 && Double.isNaN(var0[var2])) {
         --var2;
      }

      int var6 = var2;

      while(true) {
         --var6;
         if (var6 < var1) {
            doSort(var0, var1, var2, var3, var4, var5);
            var6 = var2;

            int var11;
            while(var1 < var6) {
               var11 = var1 + var6 >>> 1;
               double var8 = var0[var11];
               if (var8 < 0.0D) {
                  var1 = var11 + 1;
               } else {
                  var6 = var11;
               }
            }

            while(var1 <= var2 && Double.doubleToRawLongBits(var0[var1]) < 0L) {
               ++var1;
            }

            var11 = var1;
            int var12 = var1 - 1;

            while(true) {
               ++var11;
               if (var11 > var2) {
                  break;
               }

               double var9 = var0[var11];
               if (var9 != 0.0D) {
                  break;
               }

               if (Double.doubleToRawLongBits(var9) < 0L) {
                  var0[var11] = 0.0D;
                  ++var12;
                  var0[var12] = -0.0D;
               }
            }

            return;
         }

         double var7 = var0[var6];
         if (var7 != var7) {
            var0[var6] = var0[var2];
            var0[var2] = var7;
            --var2;
         }
      }
   }

   private static void doSort(double[] var0, int var1, int var2, double[] var3, int var4, int var5) {
      if (var2 - var1 < 286) {
         sort(var0, var1, var2, true);
      } else {
         int[] var6 = new int[68];
         int var7 = 0;
         var6[0] = var1;

         int var9;
         int var10;
         for(int var8 = var1; var8 < var2; var6[var7] = var8) {
            if (var0[var8] < var0[var8 + 1]) {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] <= var0[var8]);
            } else if (var0[var8] <= var0[var8 + 1]) {
               var9 = 33;

               while(true) {
                  ++var8;
                  if (var8 > var2 || var0[var8 - 1] != var0[var8]) {
                     break;
                  }

                  --var9;
                  if (var9 == 0) {
                     sort(var0, var1, var2, true);
                     return;
                  }
               }
            } else {
               do {
                  ++var8;
               } while(var8 <= var2 && var0[var8 - 1] >= var0[var8]);

               var9 = var6[var7] - 1;
               var10 = var8;

               while(true) {
                  ++var9;
                  --var10;
                  if (var9 >= var10) {
                     break;
                  }

                  double var11 = var0[var9];
                  var0[var9] = var0[var10];
                  var0[var10] = var11;
               }
            }

            ++var7;
            if (var7 == 67) {
               sort(var0, var1, var2, true);
               return;
            }
         }

         if (var6[var7] == var2++) {
            ++var7;
            var6[var7] = var2;
         } else if (var7 == 1) {
            return;
         }

         byte var20 = 0;

         for(var9 = 1; (var9 <<= 1) < var7; var20 = (byte)(var20 ^ 1)) {
         }

         int var12 = var2 - var1;
         if (var3 == null || var5 < var12 || var4 + var12 > var3.length) {
            var3 = new double[var12];
            var4 = 0;
         }

         double[] var21;
         int var22;
         if (var20 == 0) {
            System.arraycopy(var0, var1, var3, var4, var12);
            var21 = var0;
            var22 = 0;
            var0 = var3;
            var10 = var4 - var1;
         } else {
            var21 = var3;
            var10 = 0;
            var22 = var4 - var1;
         }

         while(var7 > 1) {
            int var13 = 0;

            int var14;
            int var15;
            for(var14 = 0 + 2; var14 <= var7; var14 += 2) {
               var15 = var6[var14];
               int var16 = var6[var14 - 1];
               int var17 = var6[var14 - 2];
               int var18 = var17;

               for(int var19 = var16; var17 < var15; ++var17) {
                  if (var19 >= var15 || var18 < var16 && var0[var18 + var10] <= var0[var19 + var10]) {
                     var21[var17 + var22] = var0[var18++ + var10];
                  } else {
                     var21[var17 + var22] = var0[var19++ + var10];
                  }
               }

               ++var13;
               var6[var13] = var15;
            }

            if ((var7 & 1) != 0) {
               var14 = var2;
               var15 = var6[var7 - 1];

               while(true) {
                  --var14;
                  if (var14 < var15) {
                     ++var13;
                     var6[var13] = var2;
                     break;
                  }

                  var21[var14 + var22] = var0[var14 + var10];
               }
            }

            double[] var23 = var0;
            var0 = var21;
            var21 = var23;
            var15 = var10;
            var10 = var22;
            var22 = var15;
            var7 = var13;
         }

      }
   }

   private static void sort(double[] var0, int var1, int var2, boolean var3) {
      int var4 = var2 - var1 + 1;
      int var5;
      int var6;
      if (var4 < 47) {
         if (var3) {
            var5 = var1;

            for(var6 = var1; var5 < var2; var6 = var5) {
               double var20 = var0[var5 + 1];

               while(var20 < var0[var6]) {
                  var0[var6 + 1] = var0[var6];
                  if (var6-- == var1) {
                     break;
                  }
               }

               var0[var6 + 1] = var20;
               ++var5;
            }

         } else {
            do {
               if (var1 >= var2) {
                  return;
               }

               ++var1;
            } while(var0[var1] >= var0[var1 - 1]);

            var5 = var1;

            label140:
            while(true) {
               ++var1;
               if (var1 > var2) {
                  double var21 = var0[var2];

                  while(true) {
                     --var2;
                     if (var21 >= var0[var2]) {
                        var0[var2 + 1] = var21;
                        return;
                     }

                     var0[var2 + 1] = var0[var2];
                  }
               }

               double var22 = var0[var5];
               double var23 = var0[var1];
               if (var22 < var23) {
                  var23 = var22;
                  var22 = var0[var1];
               }

               while(true) {
                  --var5;
                  if (var22 >= var0[var5]) {
                     ++var5;
                     var0[var5 + 1] = var22;

                     while(true) {
                        --var5;
                        if (var23 >= var0[var5]) {
                           var0[var5 + 1] = var23;
                           ++var1;
                           var5 = var1;
                           continue label140;
                        }

                        var0[var5 + 1] = var0[var5];
                     }
                  }

                  var0[var5 + 2] = var0[var5];
               }
            }
         }
      } else {
         var5 = (var4 >> 3) + (var4 >> 6) + 1;
         var6 = var1 + var2 >>> 1;
         int var7 = var6 - var5;
         int var8 = var7 - var5;
         int var9 = var6 + var5;
         int var10 = var9 + var5;
         double var11;
         if (var0[var7] < var0[var8]) {
            var11 = var0[var7];
            var0[var7] = var0[var8];
            var0[var8] = var11;
         }

         if (var0[var6] < var0[var7]) {
            var11 = var0[var6];
            var0[var6] = var0[var7];
            var0[var7] = var11;
            if (var11 < var0[var8]) {
               var0[var7] = var0[var8];
               var0[var8] = var11;
            }
         }

         if (var0[var9] < var0[var6]) {
            var11 = var0[var9];
            var0[var9] = var0[var6];
            var0[var6] = var11;
            if (var11 < var0[var7]) {
               var0[var6] = var0[var7];
               var0[var7] = var11;
               if (var11 < var0[var8]) {
                  var0[var7] = var0[var8];
                  var0[var8] = var11;
               }
            }
         }

         if (var0[var10] < var0[var9]) {
            var11 = var0[var10];
            var0[var10] = var0[var9];
            var0[var9] = var11;
            if (var11 < var0[var6]) {
               var0[var9] = var0[var6];
               var0[var6] = var11;
               if (var11 < var0[var7]) {
                  var0[var6] = var0[var7];
                  var0[var7] = var11;
                  if (var11 < var0[var8]) {
                     var0[var7] = var0[var8];
                     var0[var8] = var11;
                  }
               }
            }
         }

         int var24 = var1;
         int var12 = var2;
         double var13;
         if (var0[var8] != var0[var7] && var0[var7] != var0[var6] && var0[var6] != var0[var9] && var0[var9] != var0[var10]) {
            var13 = var0[var7];
            double var25 = var0[var9];
            var0[var7] = var0[var1];
            var0[var9] = var0[var2];

            while(true) {
               ++var24;
               if (var0[var24] >= var13) {
                  do {
                     --var12;
                  } while(var0[var12] > var25);

                  int var17 = var24 - 1;

                  double var18;
                  label229:
                  while(true) {
                     while(true) {
                        ++var17;
                        if (var17 > var12) {
                           break label229;
                        }

                        var18 = var0[var17];
                        if (var18 < var13) {
                           var0[var17] = var0[var24];
                           var0[var24] = var18;
                           ++var24;
                        } else if (var18 > var25) {
                           while(var0[var12] > var25) {
                              if (var12-- == var17) {
                                 break label229;
                              }
                           }

                           if (var0[var12] < var13) {
                              var0[var17] = var0[var24];
                              var0[var24] = var0[var12];
                              ++var24;
                           } else {
                              var0[var17] = var0[var12];
                           }

                           var0[var12] = var18;
                           --var12;
                        }
                     }
                  }

                  var0[var1] = var0[var24 - 1];
                  var0[var24 - 1] = var13;
                  var0[var2] = var0[var12 + 1];
                  var0[var12 + 1] = var25;
                  sort(var0, var1, var24 - 2, var3);
                  sort(var0, var12 + 2, var2, false);
                  if (var24 < var8 && var10 < var12) {
                     while(var0[var24] == var13) {
                        ++var24;
                     }

                     while(var0[var12] == var25) {
                        --var12;
                     }

                     var17 = var24 - 1;

                     label199:
                     while(true) {
                        while(true) {
                           ++var17;
                           if (var17 > var12) {
                              break label199;
                           }

                           var18 = var0[var17];
                           if (var18 == var13) {
                              var0[var17] = var0[var24];
                              var0[var24] = var18;
                              ++var24;
                           } else if (var18 == var25) {
                              while(var0[var12] == var25) {
                                 if (var12-- == var17) {
                                    break label199;
                                 }
                              }

                              if (var0[var12] == var13) {
                                 var0[var17] = var0[var24];
                                 var0[var24] = var0[var12];
                                 ++var24;
                              } else {
                                 var0[var17] = var0[var12];
                              }

                              var0[var12] = var18;
                              --var12;
                           }
                        }
                     }
                  }

                  sort(var0, var24, var12, false);
                  break;
               }
            }
         } else {
            var13 = var0[var6];

            for(int var15 = var1; var15 <= var12; ++var15) {
               if (var0[var15] != var13) {
                  double var16 = var0[var15];
                  if (var16 < var13) {
                     var0[var15] = var0[var24];
                     var0[var24] = var16;
                     ++var24;
                  } else {
                     while(var0[var12] > var13) {
                        --var12;
                     }

                     if (var0[var12] < var13) {
                        var0[var15] = var0[var24];
                        var0[var24] = var0[var12];
                        ++var24;
                     } else {
                        var0[var15] = var0[var12];
                     }

                     var0[var12] = var16;
                     --var12;
                  }
               }
            }

            sort(var0, var1, var24 - 1, var3);
            sort(var0, var12 + 1, var2, false);
         }

      }
   }
}
