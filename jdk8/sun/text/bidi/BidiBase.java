package sun.text.bidi;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.util.Arrays;
import java.util.MissingResourceException;
import sun.text.normalizer.UBiDiProps;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UTF16;

public class BidiBase {
   public static final byte INTERNAL_LEVEL_DEFAULT_LTR = 126;
   public static final byte INTERNAL_LEVEL_DEFAULT_RTL = 127;
   public static final byte MAX_EXPLICIT_LEVEL = 61;
   public static final byte INTERNAL_LEVEL_OVERRIDE = -128;
   public static final int MAP_NOWHERE = -1;
   public static final byte MIXED = 2;
   public static final short DO_MIRRORING = 2;
   private static final short REORDER_DEFAULT = 0;
   private static final short REORDER_NUMBERS_SPECIAL = 1;
   private static final short REORDER_GROUP_NUMBERS_WITH_R = 2;
   private static final short REORDER_RUNS_ONLY = 3;
   private static final short REORDER_INVERSE_NUMBERS_AS_L = 4;
   private static final short REORDER_INVERSE_LIKE_DIRECT = 5;
   private static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6;
   private static final short REORDER_LAST_LOGICAL_TO_VISUAL = 1;
   private static final int OPTION_INSERT_MARKS = 1;
   private static final int OPTION_REMOVE_CONTROLS = 2;
   private static final int OPTION_STREAMING = 4;
   private static final byte L = 0;
   private static final byte R = 1;
   private static final byte EN = 2;
   private static final byte ES = 3;
   private static final byte ET = 4;
   private static final byte AN = 5;
   private static final byte CS = 6;
   static final byte B = 7;
   private static final byte S = 8;
   private static final byte WS = 9;
   private static final byte ON = 10;
   private static final byte LRE = 11;
   private static final byte LRO = 12;
   private static final byte AL = 13;
   private static final byte RLE = 14;
   private static final byte RLO = 15;
   private static final byte PDF = 16;
   private static final byte NSM = 17;
   private static final byte BN = 18;
   private static final int MASK_R_AL = 8194;
   private static final char CR = '\r';
   private static final char LF = '\n';
   static final int LRM_BEFORE = 1;
   static final int LRM_AFTER = 2;
   static final int RLM_BEFORE = 4;
   static final int RLM_AFTER = 8;
   BidiBase paraBidi;
   final UBiDiProps bdp;
   char[] text;
   int originalLength;
   public int length;
   int resultLength;
   boolean mayAllocateText;
   boolean mayAllocateRuns;
   byte[] dirPropsMemory;
   byte[] levelsMemory;
   byte[] dirProps;
   byte[] levels;
   boolean orderParagraphsLTR;
   byte paraLevel;
   byte defaultParaLevel;
   BidiBase.ImpTabPair impTabPair;
   byte direction;
   int flags;
   int lastArabicPos;
   int trailingWSStart;
   int paraCount;
   int[] parasMemory;
   int[] paras;
   int[] simpleParas;
   int runCount;
   BidiRun[] runsMemory;
   BidiRun[] runs;
   BidiRun[] simpleRuns;
   int[] logicalToVisualRunsMap;
   boolean isGoodLogicalToVisualRunsMap;
   BidiBase.InsertPoints insertPoints;
   int controlCount;
   static final byte CONTEXT_RTL_SHIFT = 6;
   static final byte CONTEXT_RTL = 64;
   static final int DirPropFlagMultiRuns = DirPropFlag((byte)31);
   static final int[] DirPropFlagLR = new int[]{DirPropFlag((byte)0), DirPropFlag((byte)1)};
   static final int[] DirPropFlagE = new int[]{DirPropFlag((byte)11), DirPropFlag((byte)14)};
   static final int[] DirPropFlagO = new int[]{DirPropFlag((byte)12), DirPropFlag((byte)15)};
   static final int MASK_LTR = DirPropFlag((byte)0) | DirPropFlag((byte)2) | DirPropFlag((byte)5) | DirPropFlag((byte)11) | DirPropFlag((byte)12);
   static final int MASK_RTL = DirPropFlag((byte)1) | DirPropFlag((byte)13) | DirPropFlag((byte)14) | DirPropFlag((byte)15);
   private static final int MASK_LRX = DirPropFlag((byte)11) | DirPropFlag((byte)12);
   private static final int MASK_RLX = DirPropFlag((byte)14) | DirPropFlag((byte)15);
   private static final int MASK_EXPLICIT;
   private static final int MASK_BN_EXPLICIT;
   private static final int MASK_B_S;
   static final int MASK_WS;
   private static final int MASK_N;
   private static final int MASK_POSSIBLE_N;
   static final int MASK_EMBEDDING;
   private static final int IMPTABPROPS_COLUMNS = 14;
   private static final int IMPTABPROPS_RES = 13;
   private static final short[] groupProp;
   private static final short _L = 0;
   private static final short _R = 1;
   private static final short _EN = 2;
   private static final short _AN = 3;
   private static final short _ON = 4;
   private static final short _S = 5;
   private static final short _B = 6;
   private static final short[][] impTabProps;
   private static final int IMPTABLEVELS_COLUMNS = 8;
   private static final int IMPTABLEVELS_RES = 7;
   private static final byte[][] impTabL_DEFAULT;
   private static final byte[][] impTabR_DEFAULT;
   private static final short[] impAct0;
   private static final BidiBase.ImpTabPair impTab_DEFAULT;
   private static final byte[][] impTabL_NUMBERS_SPECIAL;
   private static final BidiBase.ImpTabPair impTab_NUMBERS_SPECIAL;
   private static final byte[][] impTabL_GROUP_NUMBERS_WITH_R;
   private static final byte[][] impTabR_GROUP_NUMBERS_WITH_R;
   private static final BidiBase.ImpTabPair impTab_GROUP_NUMBERS_WITH_R;
   private static final byte[][] impTabL_INVERSE_NUMBERS_AS_L;
   private static final byte[][] impTabR_INVERSE_NUMBERS_AS_L;
   private static final BidiBase.ImpTabPair impTab_INVERSE_NUMBERS_AS_L;
   private static final byte[][] impTabR_INVERSE_LIKE_DIRECT;
   private static final short[] impAct1;
   private static final BidiBase.ImpTabPair impTab_INVERSE_LIKE_DIRECT;
   private static final byte[][] impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS;
   private static final byte[][] impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS;
   private static final short[] impAct2;
   private static final BidiBase.ImpTabPair impTab_INVERSE_LIKE_DIRECT_WITH_MARKS;
   private static final BidiBase.ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL;
   private static final byte[][] impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS;
   private static final BidiBase.ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS;
   static final int FIRSTALLOC = 10;
   private static final int INTERNAL_DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126;
   private static final int INTERMAL_DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127;

   static int DirPropFlag(byte var0) {
      return 1 << var0;
   }

   static byte NoContextRTL(byte var0) {
      return (byte)(var0 & -65);
   }

   static int DirPropFlagNC(byte var0) {
      return 1 << (var0 & -65);
   }

   static final int DirPropFlagLR(byte var0) {
      return DirPropFlagLR[var0 & 1];
   }

   static final int DirPropFlagE(byte var0) {
      return DirPropFlagE[var0 & 1];
   }

   static final int DirPropFlagO(byte var0) {
      return DirPropFlagO[var0 & 1];
   }

   private static byte GetLRFromLevel(byte var0) {
      return (byte)(var0 & 1);
   }

   private static boolean IsDefaultLevel(byte var0) {
      return (var0 & 126) == 126;
   }

   byte GetParaLevelAt(int var1) {
      return this.defaultParaLevel != 0 ? (byte)(this.dirProps[var1] >> 6) : this.paraLevel;
   }

   static boolean IsBidiControlChar(int var0) {
      return (var0 & -4) == 8204 || var0 >= 8234 && var0 <= 8238;
   }

   public void verifyValidPara() {
      if (this != this.paraBidi) {
         throw new IllegalStateException("");
      }
   }

   public void verifyValidParaOrLine() {
      BidiBase var1 = this.paraBidi;
      if (this != var1) {
         if (var1 == null || var1 != var1.paraBidi) {
            throw new IllegalStateException();
         }
      }
   }

   public void verifyRange(int var1, int var2, int var3) {
      if (var1 < var2 || var1 >= var3) {
         throw new IllegalArgumentException("Value " + var1 + " is out of range " + var2 + " to " + var3);
      }
   }

   public void verifyIndex(int var1, int var2, int var3) {
      if (var1 < var2 || var1 >= var3) {
         throw new ArrayIndexOutOfBoundsException("Index " + var1 + " is out of range " + var2 + " to " + var3);
      }
   }

   public BidiBase(int var1, int var2) {
      this.dirPropsMemory = new byte[1];
      this.levelsMemory = new byte[1];
      this.parasMemory = new int[1];
      this.simpleParas = new int[]{0};
      this.runsMemory = new BidiRun[0];
      this.simpleRuns = new BidiRun[]{new BidiRun()};
      this.insertPoints = new BidiBase.InsertPoints();
      if (var1 >= 0 && var2 >= 0) {
         try {
            this.bdp = UBiDiProps.getSingleton();
         } catch (IOException var4) {
            throw new MissingResourceException(var4.getMessage(), "(BidiProps)", "");
         }

         if (var1 > 0) {
            this.getInitialDirPropsMemory(var1);
            this.getInitialLevelsMemory(var1);
         } else {
            this.mayAllocateText = true;
         }

         if (var2 > 0) {
            if (var2 > 1) {
               this.getInitialRunsMemory(var2);
            }
         } else {
            this.mayAllocateRuns = true;
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   private Object getMemory(String var1, Object var2, Class<?> var3, boolean var4, int var5) {
      int var6 = Array.getLength(var2);
      if (var5 == var6) {
         return var2;
      } else if (!var4) {
         if (var5 <= var6) {
            return var2;
         } else {
            throw new OutOfMemoryError("Failed to allocate memory for " + var1);
         }
      } else {
         try {
            return Array.newInstance(var3, var5);
         } catch (Exception var8) {
            throw new OutOfMemoryError("Failed to allocate memory for " + var1);
         }
      }
   }

   private void getDirPropsMemory(boolean var1, int var2) {
      Object var3 = this.getMemory("DirProps", this.dirPropsMemory, Byte.TYPE, var1, var2);
      this.dirPropsMemory = (byte[])((byte[])var3);
   }

   void getDirPropsMemory(int var1) {
      this.getDirPropsMemory(this.mayAllocateText, var1);
   }

   private void getLevelsMemory(boolean var1, int var2) {
      Object var3 = this.getMemory("Levels", this.levelsMemory, Byte.TYPE, var1, var2);
      this.levelsMemory = (byte[])((byte[])var3);
   }

   void getLevelsMemory(int var1) {
      this.getLevelsMemory(this.mayAllocateText, var1);
   }

   private void getRunsMemory(boolean var1, int var2) {
      Object var3 = this.getMemory("Runs", this.runsMemory, BidiRun.class, var1, var2);
      this.runsMemory = (BidiRun[])((BidiRun[])var3);
   }

   void getRunsMemory(int var1) {
      this.getRunsMemory(this.mayAllocateRuns, var1);
   }

   private void getInitialDirPropsMemory(int var1) {
      this.getDirPropsMemory(true, var1);
   }

   private void getInitialLevelsMemory(int var1) {
      this.getLevelsMemory(true, var1);
   }

   private void getInitialParasMemory(int var1) {
      Object var2 = this.getMemory("Paras", this.parasMemory, Integer.TYPE, true, var1);
      this.parasMemory = (int[])((int[])var2);
   }

   private void getInitialRunsMemory(int var1) {
      this.getRunsMemory(true, var1);
   }

   private void getDirProps() {
      boolean var1 = false;
      this.flags = 0;
      int var6 = 0;
      boolean var7 = IsDefaultLevel(this.paraLevel);
      this.lastArabicPos = -1;
      this.controlCount = 0;
      int var12 = 0;
      boolean var14 = false;
      boolean var15 = false;
      byte var11;
      int var13;
      if (var7) {
         var6 = (this.paraLevel & 1) != 0 ? 64 : 0;
         var13 = var6;
         var11 = 1;
      } else {
         var11 = 0;
         var13 = 0;
      }

      int var16 = 0;

      while(true) {
         int var3;
         byte[] var10000;
         label101:
         do {
            label97:
            while(var16 < this.originalLength) {
               int var2 = var16;
               int var4 = UTF16.charAt(this.text, 0, this.originalLength, var16);
               var16 += Character.charCount(var4);
               var3 = var16 - 1;
               byte var5 = (byte)this.bdp.getClass(var4);
               this.flags |= DirPropFlag(var5);
               this.dirProps[var3] = (byte)(var5 | var13);
               if (var3 > var2) {
                  this.flags |= DirPropFlag((byte)18);

                  do {
                     --var3;
                     this.dirProps[var3] = (byte)(18 | var13);
                  } while(var3 > var2);
               }

               if (var11 == 1) {
                  if (var5 == 0) {
                     var11 = 2;
                     continue label101;
                  }

                  if (var5 == 1 || var5 == 13) {
                     var11 = 2;
                     if (var13 != 0) {
                        continue;
                     }

                     var13 = 64;
                     var3 = var12;

                     while(true) {
                        if (var3 >= var16) {
                           continue label97;
                        }

                        var10000 = this.dirProps;
                        var10000[var3] = (byte)(var10000[var3] | 64);
                        ++var3;
                     }
                  }
               }

               if (var5 == 0) {
                  var14 = false;
               } else if (var5 == 1) {
                  var14 = true;
               } else if (var5 == 13) {
                  var14 = true;
                  this.lastArabicPos = var16 - 1;
               } else if (var5 == 7 && var16 < this.originalLength) {
                  if (var4 != 13 || this.text[var16] != '\n') {
                     ++this.paraCount;
                  }

                  if (var7) {
                     var11 = 1;
                     var12 = var16;
                     var13 = var6;
                  }
               }
            }

            if (var7) {
               this.paraLevel = this.GetParaLevelAt(0);
            }

            this.flags |= DirPropFlagLR(this.paraLevel);
            if (this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0) {
               this.flags |= DirPropFlag((byte)0);
            }

            return;
         } while(var13 == 0);

         var13 = 0;

         for(var3 = var12; var3 < var16; ++var3) {
            var10000 = this.dirProps;
            var10000[var3] &= -65;
         }
      }
   }

   private byte directionFromFlags() {
      if ((this.flags & MASK_RTL) != 0 || (this.flags & DirPropFlag((byte)5)) != 0 && (this.flags & MASK_POSSIBLE_N) != 0) {
         return (byte)((this.flags & MASK_LTR) == 0 ? 1 : 2);
      } else {
         return 0;
      }
   }

   private byte resolveExplicitLevels() {
      boolean var1 = false;
      byte var3 = this.GetParaLevelAt(0);
      int var5 = 0;
      byte var4 = this.directionFromFlags();
      if (var4 == 2 || this.paraCount != 1) {
         int var12;
         if (this.paraCount == 1 && (this.flags & MASK_EXPLICIT) == 0) {
            for(var12 = 0; var12 < this.length; ++var12) {
               this.levels[var12] = var3;
            }
         } else {
            byte var6 = var3;
            byte var8 = 0;
            byte[] var9 = new byte[61];
            int var10 = 0;
            int var11 = 0;
            this.flags = 0;

            for(var12 = 0; var12 < this.length; ++var12) {
               byte var2 = NoContextRTL(this.dirProps[var12]);
               byte var7;
               switch(var2) {
               case 7:
                  var8 = 0;
                  var10 = 0;
                  var11 = 0;
                  var3 = this.GetParaLevelAt(var12);
                  if (var12 + 1 < this.length) {
                     var6 = this.GetParaLevelAt(var12 + 1);
                     if (this.text[var12] != '\r' || this.text[var12 + 1] != '\n') {
                        this.paras[var5++] = var12 + 1;
                     }
                  }

                  this.flags |= DirPropFlag((byte)7);
                  break;
               case 8:
               case 9:
               case 10:
               case 13:
               case 17:
               default:
                  if (var3 != var6) {
                     var3 = var6;
                     if ((var6 & -128) != 0) {
                        this.flags |= DirPropFlagO(var6) | DirPropFlagMultiRuns;
                     } else {
                        this.flags |= DirPropFlagE(var6) | DirPropFlagMultiRuns;
                     }
                  }

                  if ((var3 & -128) == 0) {
                     this.flags |= DirPropFlag(var2);
                  }
                  break;
               case 11:
               case 12:
                  var7 = (byte)(var6 + 2 & 126);
                  if (var7 <= 61) {
                     var9[var8] = var6;
                     ++var8;
                     var6 = var7;
                     if (var2 == 12) {
                        var6 = (byte)(var7 | -128);
                     }
                  } else if ((var6 & 127) == 61) {
                     ++var11;
                  } else {
                     ++var10;
                  }

                  this.flags |= DirPropFlag((byte)18);
                  break;
               case 14:
               case 15:
                  var7 = (byte)((var6 & 127) + 1 | 1);
                  if (var7 <= 61) {
                     var9[var8] = var6;
                     ++var8;
                     var6 = var7;
                     if (var2 == 15) {
                        var6 = (byte)(var7 | -128);
                     }
                  } else {
                     ++var11;
                  }

                  this.flags |= DirPropFlag((byte)18);
                  break;
               case 16:
                  if (var11 > 0) {
                     --var11;
                  } else if (var10 > 0 && (var6 & 127) != 61) {
                     --var10;
                  } else if (var8 > 0) {
                     --var8;
                     var6 = var9[var8];
                  }

                  this.flags |= DirPropFlag((byte)18);
                  break;
               case 18:
                  this.flags |= DirPropFlag((byte)18);
               }

               this.levels[var12] = var3;
            }

            if ((this.flags & MASK_EMBEDDING) != 0) {
               this.flags |= DirPropFlagLR(this.paraLevel);
            }

            if (this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0) {
               this.flags |= DirPropFlag((byte)0);
            }

            var4 = this.directionFromFlags();
         }
      }

      return var4;
   }

   private byte checkExplicitLevels() {
      this.flags = 0;
      int var4 = 0;

      for(int var2 = 0; var2 < this.length; ++var2) {
         if (this.levels[var2] == 0) {
            this.levels[var2] = this.paraLevel;
         }

         if (61 < (this.levels[var2] & 127)) {
            if ((this.levels[var2] & -128) != 0) {
               this.levels[var2] = (byte)(this.paraLevel | -128);
            } else {
               this.levels[var2] = this.paraLevel;
            }
         }

         byte var3 = this.levels[var2];
         byte var1 = NoContextRTL(this.dirProps[var2]);
         if ((var3 & -128) != 0) {
            var3 = (byte)(var3 & 127);
            this.flags |= DirPropFlagO(var3);
         } else {
            this.flags |= DirPropFlagE(var3) | DirPropFlag(var1);
         }

         if (var3 < this.GetParaLevelAt(var2) && (0 != var3 || var1 != 7) || 61 < var3) {
            throw new IllegalArgumentException("level " + var3 + " out of bounds at index " + var2);
         }

         if (var1 == 7 && var2 + 1 < this.length && (this.text[var2] != '\r' || this.text[var2 + 1] != '\n')) {
            this.paras[var4++] = var2 + 1;
         }
      }

      if ((this.flags & MASK_EMBEDDING) != 0) {
         this.flags |= DirPropFlagLR(this.paraLevel);
      }

      return this.directionFromFlags();
   }

   private static short GetStateProps(short var0) {
      return (short)(var0 & 31);
   }

   private static short GetActionProps(short var0) {
      return (short)(var0 >> 5);
   }

   private static short GetState(byte var0) {
      return (short)(var0 & 15);
   }

   private static short GetAction(byte var0) {
      return (short)(var0 >> 4);
   }

   private void addPoint(int var1, int var2) {
      BidiBase.Point var3 = new BidiBase.Point();
      int var4 = this.insertPoints.points.length;
      if (var4 == 0) {
         this.insertPoints.points = new BidiBase.Point[10];
         var4 = 10;
      }

      if (this.insertPoints.size >= var4) {
         BidiBase.Point[] var5 = this.insertPoints.points;
         this.insertPoints.points = new BidiBase.Point[var4 * 2];
         System.arraycopy(var5, 0, this.insertPoints.points, 0, var4);
      }

      var3.pos = var1;
      var3.flag = var2;
      this.insertPoints.points[this.insertPoints.size] = var3;
      ++this.insertPoints.size;
   }

   private void processPropertySeq(BidiBase.LevState var1, short var2, int var3, int var4) {
      byte[][] var6 = var1.impTab;
      short[] var7 = var1.impAct;
      int var12 = var3;
      short var8 = var1.state;
      byte var5 = var6[var8][var2];
      var1.state = GetState(var5);
      short var9 = var7[GetAction(var5)];
      byte var11 = var6[var1.state][7];
      byte var10;
      int var13;
      if (var9 != 0) {
         byte[] var10000;
         label156:
         switch(var9) {
         case 1:
            var1.startON = var3;
            break;
         case 2:
            var3 = var1.startON;
            break;
         case 3:
            if (var1.startL2EN >= 0) {
               this.addPoint(var1.startL2EN, 1);
            }

            var1.startL2EN = -1;
            if (this.insertPoints.points.length != 0 && this.insertPoints.size > this.insertPoints.confirmed) {
               for(var13 = var1.lastStrongRTL + 1; var13 < var12; ++var13) {
                  this.levels[var13] = (byte)(this.levels[var13] - 2 & -2);
               }

               this.insertPoints.confirmed = this.insertPoints.size;
               var1.lastStrongRTL = -1;
               if (var2 == 5) {
                  this.addPoint(var12, 1);
                  this.insertPoints.confirmed = this.insertPoints.size;
               }
            } else {
               var1.lastStrongRTL = -1;
               var10 = var6[var8][7];
               if ((var10 & 1) != 0 && var1.startON > 0) {
                  var3 = var1.startON;
               }

               if (var2 == 5) {
                  this.addPoint(var3, 1);
                  this.insertPoints.confirmed = this.insertPoints.size;
               }
            }
            break;
         case 4:
            if (this.insertPoints.points.length > 0) {
               this.insertPoints.size = this.insertPoints.confirmed;
            }

            var1.startON = -1;
            var1.startL2EN = -1;
            var1.lastStrongRTL = var4 - 1;
            break;
         case 5:
            if (var2 == 3 && NoContextRTL(this.dirProps[var3]) == 5) {
               if (var1.startL2EN == -1) {
                  var1.lastStrongRTL = var4 - 1;
               } else {
                  if (var1.startL2EN >= 0) {
                     this.addPoint(var1.startL2EN, 1);
                     var1.startL2EN = -2;
                  }

                  this.addPoint(var3, 1);
               }
            } else if (var1.startL2EN == -1) {
               var1.startL2EN = var3;
            }
            break;
         case 6:
            var1.lastStrongRTL = var4 - 1;
            var1.startON = -1;
            break;
         case 7:
            for(var13 = var3 - 1; var13 >= 0 && (this.levels[var13] & 1) == 0; --var13) {
            }

            if (var13 >= 0) {
               this.addPoint(var13, 4);
               this.insertPoints.confirmed = this.insertPoints.size;
            }

            var1.startON = var3;
            break;
         case 8:
            this.addPoint(var3, 1);
            this.addPoint(var3, 2);
            break;
         case 9:
            this.insertPoints.size = this.insertPoints.confirmed;
            if (var2 == 5) {
               this.addPoint(var3, 4);
               this.insertPoints.confirmed = this.insertPoints.size;
            }
            break;
         case 10:
            var10 = (byte)(var1.runLevel + var11);

            for(var13 = var1.startON; var13 < var12; ++var13) {
               if (this.levels[var13] < var10) {
                  this.levels[var13] = var10;
               }
            }

            this.insertPoints.confirmed = this.insertPoints.size;
            var1.startON = var12;
            break;
         case 11:
            var10 = var1.runLevel;
            var13 = var3 - 1;

            while(true) {
               if (var13 < var1.startON) {
                  break label156;
               }

               if (this.levels[var13] == var10 + 3) {
                  while(this.levels[var13] == var10 + 3) {
                     var10000 = this.levels;
                     int var10001 = var13--;
                     var10000[var10001] = (byte)(var10000[var10001] - 2);
                  }

                  while(this.levels[var13] == var10) {
                     --var13;
                  }
               }

               if (this.levels[var13] == var10 + 2) {
                  this.levels[var13] = var10;
               } else {
                  this.levels[var13] = (byte)(var10 + 1);
               }

               --var13;
            }
         case 12:
            var10 = (byte)(var1.runLevel + 1);
            var13 = var3 - 1;

            while(true) {
               if (var13 < var1.startON) {
                  break label156;
               }

               if (this.levels[var13] > var10) {
                  var10000 = this.levels;
                  var10000[var13] = (byte)(var10000[var13] - 2);
               }

               --var13;
            }
         default:
            throw new IllegalStateException("Internal ICU error in processPropertySeq");
         }
      }

      if (var11 != 0 || var3 < var12) {
         var10 = (byte)(var1.runLevel + var11);

         for(var13 = var3; var13 < var4; ++var13) {
            this.levels[var13] = var10;
         }
      }

   }

   private void resolveImplicitLevels(int var1, int var2, short var3, short var4) {
      BidiBase.LevState var5 = new BidiBase.LevState();
      boolean var15 = true;
      boolean var16 = true;
      var5.startL2EN = -1;
      var5.lastStrongRTL = -1;
      var5.state = 0;
      var5.runLevel = this.levels[var1];
      var5.impTab = this.impTabPair.imptab[var5.runLevel & 1];
      var5.impAct = this.impTabPair.impact[var5.runLevel & 1];
      this.processPropertySeq(var5, var3, var1, var1);
      short var10;
      if (this.dirProps[var1] == 17) {
         var10 = (short)(1 + var3);
      } else {
         var10 = 0;
      }

      int var7 = var1;
      int var8 = 0;

      for(int var6 = var1; var6 <= var2; ++var6) {
         short var12;
         if (var6 >= var2) {
            var12 = var4;
         } else {
            short var17 = (short)NoContextRTL(this.dirProps[var6]);
            var12 = groupProp[var17];
         }

         short var9 = var10;
         short var14 = impTabProps[var10][var12];
         var10 = GetStateProps(var14);
         short var11 = GetActionProps(var14);
         if (var6 == var2 && var11 == 0) {
            var11 = 1;
         }

         if (var11 != 0) {
            short var13 = impTabProps[var9][13];
            switch(var11) {
            case 1:
               this.processPropertySeq(var5, var13, var7, var6);
               var7 = var6;
               break;
            case 2:
               var8 = var6;
               break;
            case 3:
               this.processPropertySeq(var5, var13, var7, var8);
               this.processPropertySeq(var5, (short)4, var8, var6);
               var7 = var6;
               break;
            case 4:
               this.processPropertySeq(var5, var13, var7, var8);
               var7 = var8;
               var8 = var6;
               break;
            default:
               throw new IllegalStateException("Internal ICU error in resolveImplicitLevels");
            }
         }
      }

      this.processPropertySeq(var5, var4, var2, var2);
   }

   private void adjustWSLevels() {
      if ((this.flags & MASK_WS) != 0) {
         int var1 = this.trailingWSStart;

         while(true) {
            while(var1 > 0) {
               int var2;
               label35:
               while(true) {
                  while(true) {
                     if (var1 <= 0) {
                        break label35;
                     }

                     --var1;
                     if (((var2 = DirPropFlagNC(this.dirProps[var1])) & MASK_WS) == 0) {
                        break label35;
                     }

                     if (this.orderParagraphsLTR && (var2 & DirPropFlag((byte)7)) != 0) {
                        this.levels[var1] = 0;
                     } else {
                        this.levels[var1] = this.GetParaLevelAt(var1);
                     }
                  }
               }

               while(var1 > 0) {
                  --var1;
                  var2 = DirPropFlagNC(this.dirProps[var1]);
                  if ((var2 & MASK_BN_EXPLICIT) != 0) {
                     this.levels[var1] = this.levels[var1 + 1];
                  } else {
                     if (this.orderParagraphsLTR && (var2 & DirPropFlag((byte)7)) != 0) {
                        this.levels[var1] = 0;
                        break;
                     }

                     if ((var2 & MASK_B_S) != 0) {
                        this.levels[var1] = this.GetParaLevelAt(var1);
                        break;
                     }
                  }
               }
            }

            return;
         }
      }
   }

   private int Bidi_Min(int var1, int var2) {
      return var1 < var2 ? var1 : var2;
   }

   private int Bidi_Abs(int var1) {
      return var1 >= 0 ? var1 : -var1;
   }

   void setPara(String var1, byte var2, byte[] var3) {
      if (var1 == null) {
         this.setPara(new char[0], var2, var3);
      } else {
         this.setPara(var1.toCharArray(), var2, var3);
      }

   }

   public void setPara(char[] var1, byte var2, byte[] var3) {
      if (var2 < 126) {
         this.verifyRange(var2, 0, 62);
      }

      if (var1 == null) {
         var1 = new char[0];
      }

      this.paraBidi = null;
      this.text = var1;
      this.length = this.originalLength = this.resultLength = this.text.length;
      this.paraLevel = var2;
      this.direction = 0;
      this.paraCount = 1;
      this.dirProps = new byte[0];
      this.levels = new byte[0];
      this.runs = new BidiRun[0];
      this.isGoodLogicalToVisualRunsMap = false;
      this.insertPoints.size = 0;
      this.insertPoints.confirmed = 0;
      if (IsDefaultLevel(var2)) {
         this.defaultParaLevel = var2;
      } else {
         this.defaultParaLevel = 0;
      }

      if (this.length == 0) {
         if (IsDefaultLevel(var2)) {
            this.paraLevel = (byte)(this.paraLevel & 1);
            this.defaultParaLevel = 0;
         }

         if ((this.paraLevel & 1) != 0) {
            this.flags = DirPropFlag((byte)1);
            this.direction = 1;
         } else {
            this.flags = DirPropFlag((byte)0);
            this.direction = 0;
         }

         this.runCount = 0;
         this.paraCount = 0;
         this.paraBidi = this;
      } else {
         this.runCount = -1;
         this.getDirPropsMemory(this.length);
         this.dirProps = this.dirPropsMemory;
         this.getDirProps();
         this.trailingWSStart = this.length;
         if (this.paraCount > 1) {
            this.getInitialParasMemory(this.paraCount);
            this.paras = this.parasMemory;
            this.paras[this.paraCount - 1] = this.length;
         } else {
            this.paras = this.simpleParas;
            this.simpleParas[0] = this.length;
         }

         if (var3 == null) {
            this.getLevelsMemory(this.length);
            this.levels = this.levelsMemory;
            this.direction = this.resolveExplicitLevels();
         } else {
            this.levels = var3;
            this.direction = this.checkExplicitLevels();
         }

         switch(this.direction) {
         case 0:
            var2 = (byte)(var2 + 1 & -2);
            this.trailingWSStart = 0;
            break;
         case 1:
            var2 = (byte)(var2 | 1);
            this.trailingWSStart = 0;
            break;
         default:
            this.impTabPair = impTab_DEFAULT;
            if (var3 == null && this.paraCount <= 1 && (this.flags & DirPropFlagMultiRuns) == 0) {
               this.resolveImplicitLevels(0, this.length, (short)GetLRFromLevel(this.GetParaLevelAt(0)), (short)GetLRFromLevel(this.GetParaLevelAt(this.length - 1)));
            } else {
               int var5 = 0;
               byte var6 = this.GetParaLevelAt(0);
               byte var7 = this.levels[0];
               short var9;
               if (var6 < var7) {
                  var9 = (short)GetLRFromLevel(var7);
               } else {
                  var9 = (short)GetLRFromLevel(var6);
               }

               do {
                  int var4 = var5;
                  var6 = var7;
                  short var8;
                  if (var5 > 0 && NoContextRTL(this.dirProps[var5 - 1]) == 7) {
                     var8 = (short)GetLRFromLevel(this.GetParaLevelAt(var5));
                  } else {
                     var8 = var9;
                  }

                  do {
                     ++var5;
                  } while(var5 < this.length && this.levels[var5] == var6);

                  if (var5 < this.length) {
                     var7 = this.levels[var5];
                  } else {
                     var7 = this.GetParaLevelAt(this.length - 1);
                  }

                  if ((var6 & 127) < (var7 & 127)) {
                     var9 = (short)GetLRFromLevel(var7);
                  } else {
                     var9 = (short)GetLRFromLevel(var6);
                  }

                  if ((var6 & -128) == 0) {
                     this.resolveImplicitLevels(var4, var5, var8, var9);
                  } else {
                     do {
                        byte[] var10000 = this.levels;
                        int var10001 = var4++;
                        var10000[var10001] = (byte)(var10000[var10001] & 127);
                     } while(var4 < var5);
                  }
               } while(var5 < this.length);
            }

            this.adjustWSLevels();
         }

         this.resultLength += this.insertPoints.size;
         this.paraBidi = this;
      }
   }

   public void setPara(AttributedCharacterIterator var1) {
      char var3 = var1.first();
      Boolean var4 = (Boolean)var1.getAttribute(BidiBase.TextAttributeConstants.RUN_DIRECTION);
      Object var5 = var1.getAttribute(BidiBase.TextAttributeConstants.NUMERIC_SHAPING);
      int var2;
      if (var4 == null) {
         var2 = 126;
      } else {
         var2 = var4.equals(BidiBase.TextAttributeConstants.RUN_DIRECTION_LTR) ? 0 : 1;
      }

      byte[] var6 = null;
      int var7 = var1.getEndIndex() - var1.getBeginIndex();
      byte[] var8 = new byte[var7];
      char[] var9 = new char[var7];

      for(int var10 = 0; var3 != '\uffff'; ++var10) {
         var9[var10] = var3;
         Integer var11 = (Integer)var1.getAttribute(BidiBase.TextAttributeConstants.BIDI_EMBEDDING);
         if (var11 != null) {
            byte var12 = var11.byteValue();
            if (var12 != 0) {
               if (var12 < 0) {
                  var6 = var8;
                  var8[var10] = (byte)(0 - var12 | -128);
               } else {
                  var6 = var8;
                  var8[var10] = var12;
               }
            }
         }

         var3 = var1.next();
      }

      if (var5 != null) {
         BidiBase.NumericShapings.shape(var5, var9, 0, var7);
      }

      this.setPara((char[])var9, (byte)var2, var6);
   }

   private void orderParagraphsLTR(boolean var1) {
      this.orderParagraphsLTR = var1;
   }

   private byte getDirection() {
      this.verifyValidParaOrLine();
      return this.direction;
   }

   public int getLength() {
      this.verifyValidParaOrLine();
      return this.originalLength;
   }

   public byte getParaLevel() {
      this.verifyValidParaOrLine();
      return this.paraLevel;
   }

   public int getParagraphIndex(int var1) {
      this.verifyValidParaOrLine();
      BidiBase var2 = this.paraBidi;
      this.verifyRange(var1, 0, var2.length);

      int var3;
      for(var3 = 0; var1 >= var2.paras[var3]; ++var3) {
      }

      return var3;
   }

   public Bidi setLine(Bidi var1, BidiBase var2, Bidi var3, BidiBase var4, int var5, int var6) {
      this.verifyValidPara();
      this.verifyRange(var5, 0, var6);
      this.verifyRange(var6, 0, this.length + 1);
      return BidiLine.setLine(var1, this, var3, var4, var5, var6);
   }

   public byte getLevelAt(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         this.verifyValidParaOrLine();
         this.verifyRange(var1, 0, this.length);
         return BidiLine.getLevelAt(this, var1);
      } else {
         return (byte)this.getBaseLevel();
      }
   }

   private byte[] getLevels() {
      this.verifyValidParaOrLine();
      return this.length <= 0 ? new byte[0] : BidiLine.getLevels(this);
   }

   public int countRuns() {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      return this.runCount;
   }

   private int[] getVisualMap() {
      this.countRuns();
      return this.resultLength <= 0 ? new int[0] : BidiLine.getVisualMap(this);
   }

   private static int[] reorderVisual(byte[] var0) {
      return BidiLine.reorderVisual(var0);
   }

   public BidiBase(char[] var1, int var2, byte[] var3, int var4, int var5, int var6) {
      this(0, 0);
      byte var7;
      switch(var6) {
      case -2:
         var7 = 126;
         break;
      case -1:
         var7 = 127;
         break;
      case 0:
      default:
         var7 = 0;
         break;
      case 1:
         var7 = 1;
      }

      byte[] var8;
      if (var3 == null) {
         var8 = null;
      } else {
         var8 = new byte[var5];

         for(int var10 = 0; var10 < var5; ++var10) {
            byte var9 = var3[var10 + var4];
            if (var9 < 0) {
               var9 = (byte)(-var9 | -128);
            } else if (var9 == 0) {
               var9 = var7;
               if (var7 > 61) {
                  var9 = (byte)(var7 & 1);
               }
            }

            var8[var10] = var9;
         }
      }

      if (var2 == 0 && var4 == 0 && var5 == var1.length) {
         this.setPara(var1, var7, var8);
      } else {
         char[] var11 = new char[var5];
         System.arraycopy(var1, var2, var11, 0, var5);
         this.setPara(var11, var7, var8);
      }

   }

   public boolean isMixed() {
      return !this.isLeftToRight() && !this.isRightToLeft();
   }

   public boolean isLeftToRight() {
      return this.getDirection() == 0 && (this.paraLevel & 1) == 0;
   }

   public boolean isRightToLeft() {
      return this.getDirection() == 1 && (this.paraLevel & 1) == 1;
   }

   public boolean baseIsLeftToRight() {
      return this.getParaLevel() == 0;
   }

   public int getBaseLevel() {
      return this.getParaLevel();
   }

   private void getLogicalToVisualRunsMap() {
      if (!this.isGoodLogicalToVisualRunsMap) {
         int var1 = this.countRuns();
         if (this.logicalToVisualRunsMap == null || this.logicalToVisualRunsMap.length < var1) {
            this.logicalToVisualRunsMap = new int[var1];
         }

         long[] var3 = new long[var1];

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            var3[var2] = ((long)this.runs[var2].start << 32) + (long)var2;
         }

         Arrays.sort(var3);

         for(var2 = 0; var2 < var1; ++var2) {
            this.logicalToVisualRunsMap[var2] = (int)(var3[var2] & -1L);
         }

         Object var4 = null;
         this.isGoodLogicalToVisualRunsMap = true;
      }
   }

   public int getRunLevel(int var1) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      if (var1 >= 0 && var1 < this.runCount) {
         this.getLogicalToVisualRunsMap();
         return this.runs[this.logicalToVisualRunsMap[var1]].level;
      } else {
         return this.getParaLevel();
      }
   }

   public int getRunStart(int var1) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      if (this.runCount == 1) {
         return 0;
      } else if (var1 == this.runCount) {
         return this.length;
      } else {
         this.verifyIndex(var1, 0, this.runCount);
         this.getLogicalToVisualRunsMap();
         return this.runs[this.logicalToVisualRunsMap[var1]].start;
      }
   }

   public int getRunLimit(int var1) {
      this.verifyValidParaOrLine();
      BidiLine.getRuns(this);
      if (this.runCount == 1) {
         return this.length;
      } else {
         this.verifyIndex(var1, 0, this.runCount);
         this.getLogicalToVisualRunsMap();
         int var2 = this.logicalToVisualRunsMap[var1];
         int var3 = var2 == 0 ? this.runs[var2].limit : this.runs[var2].limit - this.runs[var2 - 1].limit;
         return this.runs[var2].start + var3;
      }
   }

   public static boolean requiresBidi(char[] var0, int var1, int var2) {
      if (0 <= var1 && var1 <= var2 && var2 <= var0.length) {
         for(int var4 = var1; var4 < var2; ++var4) {
            if (Character.isHighSurrogate(var0[var4]) && var4 < var2 - 1 && Character.isLowSurrogate(var0[var4 + 1])) {
               if ((1 << UCharacter.getDirection(Character.codePointAt(var0, var4)) & '\ue022') != 0) {
                  return true;
               }
            } else if ((1 << UCharacter.getDirection(var0[var4]) & '\ue022') != 0) {
               return true;
            }
         }

         return false;
      } else {
         throw new IllegalArgumentException("Value start " + var1 + " is out of range 0 to " + var2);
      }
   }

   public static void reorderVisually(byte[] var0, int var1, Object[] var2, int var3, int var4) {
      if (0 <= var1 && var0.length > var1) {
         if (0 <= var3 && var2.length > var3) {
            if (0 <= var4 && var2.length >= var3 + var4) {
               byte[] var5 = new byte[var4];
               System.arraycopy(var0, var1, var5, 0, var4);
               int[] var6 = reorderVisual(var5);
               Object[] var7 = new Object[var4];
               System.arraycopy(var2, var3, var7, 0, var4);

               for(int var8 = 0; var8 < var4; ++var8) {
                  var2[var3 + var8] = var7[var6[var8]];
               }

            } else {
               throw new IllegalArgumentException("Value count " + var1 + " is out of range 0 to " + (var2.length - var3));
            }
         } else {
            throw new IllegalArgumentException("Value objectStart " + var1 + " is out of range 0 to " + (var2.length - 1));
         }
      } else {
         throw new IllegalArgumentException("Value levelStart " + var1 + " is out of range 0 to " + (var0.length - 1));
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.getClass().getName());
      var1.append("[dir: ");
      var1.append((int)this.direction);
      var1.append(" baselevel: ");
      var1.append((int)this.paraLevel);
      var1.append(" length: ");
      var1.append(this.length);
      var1.append(" runs: ");
      int var2;
      if (this.levels == null) {
         var1.append("none");
      } else {
         var1.append('[');
         var1.append((int)this.levels[0]);

         for(var2 = 1; var2 < this.levels.length; ++var2) {
            var1.append(' ');
            var1.append((int)this.levels[var2]);
         }

         var1.append(']');
      }

      var1.append(" text: [0x");
      var1.append(Integer.toHexString(this.text[0]));

      for(var2 = 1; var2 < this.text.length; ++var2) {
         var1.append(" 0x");
         var1.append(Integer.toHexString(this.text[var2]));
      }

      var1.append("]]");
      return var1.toString();
   }

   static {
      MASK_EXPLICIT = MASK_LRX | MASK_RLX | DirPropFlag((byte)16);
      MASK_BN_EXPLICIT = DirPropFlag((byte)18) | MASK_EXPLICIT;
      MASK_B_S = DirPropFlag((byte)7) | DirPropFlag((byte)8);
      MASK_WS = MASK_B_S | DirPropFlag((byte)9) | MASK_BN_EXPLICIT;
      MASK_N = DirPropFlag((byte)10) | MASK_WS;
      MASK_POSSIBLE_N = DirPropFlag((byte)6) | DirPropFlag((byte)3) | DirPropFlag((byte)4) | MASK_N;
      MASK_EMBEDDING = DirPropFlag((byte)17) | MASK_POSSIBLE_N;
      groupProp = new short[]{0, 1, 2, 7, 8, 3, 9, 6, 5, 4, 4, 10, 10, 12, 10, 10, 10, 11, 10};
      impTabProps = new short[][]{{1, 2, 4, 5, 7, 15, 17, 7, 9, 7, 0, 7, 3, 4}, {1, 34, 36, 37, 39, 47, 49, 39, 41, 39, 1, 1, 35, 0}, {33, 2, 36, 37, 39, 47, 49, 39, 41, 39, 2, 2, 35, 1}, {33, 34, 38, 38, 40, 48, 49, 40, 40, 40, 3, 3, 3, 1}, {33, 34, 4, 37, 39, 47, 49, 74, 11, 74, 4, 4, 35, 2}, {33, 34, 36, 5, 39, 47, 49, 39, 41, 76, 5, 5, 35, 3}, {33, 34, 6, 6, 40, 48, 49, 40, 40, 77, 6, 6, 35, 3}, {33, 34, 36, 37, 7, 47, 49, 7, 78, 7, 7, 7, 35, 4}, {33, 34, 38, 38, 8, 48, 49, 8, 8, 8, 8, 8, 35, 4}, {33, 34, 4, 37, 7, 47, 49, 7, 9, 7, 9, 9, 35, 4}, {97, 98, 4, 101, 135, 111, 113, 135, 142, 135, 10, 135, 99, 2}, {33, 34, 4, 37, 39, 47, 49, 39, 11, 39, 11, 11, 35, 2}, {97, 98, 100, 5, 135, 111, 113, 135, 142, 135, 12, 135, 99, 3}, {97, 98, 6, 6, 136, 112, 113, 136, 136, 136, 13, 136, 99, 3}, {33, 34, 132, 37, 7, 47, 49, 7, 14, 7, 14, 14, 35, 4}, {33, 34, 36, 37, 39, 15, 49, 39, 41, 39, 15, 39, 35, 5}, {33, 34, 38, 38, 40, 16, 49, 40, 40, 40, 16, 40, 35, 5}, {33, 34, 36, 37, 39, 47, 17, 39, 41, 39, 17, 39, 35, 6}};
      impTabL_DEFAULT = new byte[][]{{0, 1, 0, 2, 0, 0, 0, 0}, {0, 1, 3, 3, 20, 20, 0, 1}, {0, 1, 0, 2, 21, 21, 0, 2}, {0, 1, 3, 3, 20, 20, 0, 2}, {32, 1, 3, 3, 4, 4, 32, 1}, {32, 1, 32, 2, 5, 5, 32, 1}};
      impTabR_DEFAULT = new byte[][]{{1, 0, 2, 2, 0, 0, 0, 0}, {1, 0, 1, 3, 20, 20, 0, 1}, {1, 0, 2, 2, 0, 0, 0, 1}, {1, 0, 1, 3, 5, 5, 0, 1}, {33, 0, 33, 3, 4, 4, 0, 0}, {1, 0, 1, 3, 5, 5, 0, 0}};
      impAct0 = new short[]{0, 1, 2, 3, 4, 5, 6};
      impTab_DEFAULT = new BidiBase.ImpTabPair(impTabL_DEFAULT, impTabR_DEFAULT, impAct0, impAct0);
      impTabL_NUMBERS_SPECIAL = new byte[][]{{0, 2, 1, 1, 0, 0, 0, 0}, {0, 2, 1, 1, 0, 0, 0, 2}, {0, 2, 4, 4, 19, 0, 0, 1}, {32, 2, 4, 4, 3, 3, 32, 1}, {0, 2, 4, 4, 19, 19, 0, 2}};
      impTab_NUMBERS_SPECIAL = new BidiBase.ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_DEFAULT, impAct0, impAct0);
      impTabL_GROUP_NUMBERS_WITH_R = new byte[][]{{0, 3, 17, 17, 0, 0, 0, 0}, {32, 3, 1, 1, 2, 32, 32, 2}, {32, 3, 1, 1, 2, 32, 32, 1}, {0, 3, 5, 5, 20, 0, 0, 1}, {32, 3, 5, 5, 4, 32, 32, 1}, {0, 3, 5, 5, 20, 0, 0, 2}};
      impTabR_GROUP_NUMBERS_WITH_R = new byte[][]{{2, 0, 1, 1, 0, 0, 0, 0}, {2, 0, 1, 1, 0, 0, 0, 1}, {2, 0, 20, 20, 19, 0, 0, 1}, {34, 0, 4, 4, 3, 0, 0, 0}, {34, 0, 4, 4, 3, 0, 0, 1}};
      impTab_GROUP_NUMBERS_WITH_R = new BidiBase.ImpTabPair(impTabL_GROUP_NUMBERS_WITH_R, impTabR_GROUP_NUMBERS_WITH_R, impAct0, impAct0);
      impTabL_INVERSE_NUMBERS_AS_L = new byte[][]{{0, 1, 0, 0, 0, 0, 0, 0}, {0, 1, 0, 0, 20, 20, 0, 1}, {0, 1, 0, 0, 21, 21, 0, 2}, {0, 1, 0, 0, 20, 20, 0, 2}, {32, 1, 32, 32, 4, 4, 32, 1}, {32, 1, 32, 32, 5, 5, 32, 1}};
      impTabR_INVERSE_NUMBERS_AS_L = new byte[][]{{1, 0, 1, 1, 0, 0, 0, 0}, {1, 0, 1, 1, 20, 20, 0, 1}, {1, 0, 1, 1, 0, 0, 0, 1}, {1, 0, 1, 1, 5, 5, 0, 1}, {33, 0, 33, 33, 4, 4, 0, 0}, {1, 0, 1, 1, 5, 5, 0, 0}};
      impTab_INVERSE_NUMBERS_AS_L = new BidiBase.ImpTabPair(impTabL_INVERSE_NUMBERS_AS_L, impTabR_INVERSE_NUMBERS_AS_L, impAct0, impAct0);
      impTabR_INVERSE_LIKE_DIRECT = new byte[][]{{1, 0, 2, 2, 0, 0, 0, 0}, {1, 0, 1, 2, 19, 19, 0, 1}, {1, 0, 2, 2, 0, 0, 0, 1}, {33, 48, 6, 4, 3, 3, 48, 0}, {33, 48, 6, 4, 5, 5, 48, 3}, {33, 48, 6, 4, 5, 5, 48, 2}, {33, 48, 6, 4, 3, 3, 48, 1}};
      impAct1 = new short[]{0, 1, 11, 12};
      impTab_INVERSE_LIKE_DIRECT = new BidiBase.ImpTabPair(impTabL_DEFAULT, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
      impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS = new byte[][]{{0, 99, 0, 1, 0, 0, 0, 0}, {0, 99, 0, 1, 18, 48, 0, 4}, {32, 99, 32, 1, 2, 48, 32, 3}, {0, 99, 85, 86, 20, 48, 0, 3}, {48, 67, 85, 86, 4, 48, 48, 3}, {48, 67, 5, 86, 20, 48, 48, 4}, {48, 67, 85, 6, 20, 48, 48, 4}};
      impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS = new byte[][]{{19, 0, 1, 1, 0, 0, 0, 0}, {35, 0, 1, 1, 2, 64, 0, 1}, {35, 0, 1, 1, 2, 64, 0, 0}, {3, 0, 3, 54, 20, 64, 0, 1}, {83, 64, 5, 54, 4, 64, 64, 0}, {83, 64, 5, 54, 4, 64, 64, 1}, {83, 64, 6, 6, 4, 64, 64, 3}};
      impAct2 = new short[]{0, 1, 7, 8, 9, 10};
      impTab_INVERSE_LIKE_DIRECT_WITH_MARKS = new BidiBase.ImpTabPair(impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
      impTab_INVERSE_FOR_NUMBERS_SPECIAL = new BidiBase.ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
      impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new byte[][]{{0, 98, 1, 1, 0, 0, 0, 0}, {0, 98, 1, 1, 0, 48, 0, 4}, {0, 98, 84, 84, 19, 48, 0, 3}, {48, 66, 84, 84, 3, 48, 48, 3}, {48, 66, 4, 4, 19, 48, 48, 4}};
      impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new BidiBase.ImpTabPair(impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
   }

   private static class NumericShapings {
      private static final Class<?> clazz = getClass("java.awt.font.NumericShaper");
      private static final Method shapeMethod;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, (ClassLoader)null);
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         if (var0 != null) {
            try {
               return var0.getMethod(var1, var2);
            } catch (NoSuchMethodException var4) {
               throw new AssertionError(var4);
            }
         } else {
            return null;
         }
      }

      static void shape(Object var0, char[] var1, int var2, int var3) {
         if (shapeMethod == null) {
            throw new AssertionError("Should not get here");
         } else {
            try {
               shapeMethod.invoke(var0, var1, var2, var3);
            } catch (InvocationTargetException var6) {
               Throwable var5 = var6.getCause();
               if (var5 instanceof RuntimeException) {
                  throw (RuntimeException)var5;
               } else {
                  throw new AssertionError(var6);
               }
            } catch (IllegalAccessException var7) {
               throw new AssertionError(var7);
            }
         }
      }

      static {
         shapeMethod = getMethod(clazz, "shape", char[].class, Integer.TYPE, Integer.TYPE);
      }
   }

   private static class TextAttributeConstants {
      private static final Class<?> clazz = getClass("java.awt.font.TextAttribute");
      static final AttributedCharacterIterator.Attribute RUN_DIRECTION = getTextAttribute("RUN_DIRECTION");
      static final AttributedCharacterIterator.Attribute NUMERIC_SHAPING = getTextAttribute("NUMERIC_SHAPING");
      static final AttributedCharacterIterator.Attribute BIDI_EMBEDDING = getTextAttribute("BIDI_EMBEDDING");
      static final Boolean RUN_DIRECTION_LTR;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, (ClassLoader)null);
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Object getStaticField(Class<?> var0, String var1) {
         try {
            Field var2 = var0.getField(var1);
            return var2.get((Object)null);
         } catch (IllegalAccessException | NoSuchFieldException var3) {
            throw new AssertionError(var3);
         }
      }

      private static AttributedCharacterIterator.Attribute getTextAttribute(String var0) {
         return clazz == null ? new AttributedCharacterIterator.Attribute(var0) {
         } : (AttributedCharacterIterator.Attribute)getStaticField(clazz, var0);
      }

      static {
         RUN_DIRECTION_LTR = clazz == null ? Boolean.FALSE : (Boolean)getStaticField(clazz, "RUN_DIRECTION_LTR");
      }
   }

   private class LevState {
      byte[][] impTab;
      short[] impAct;
      int startON;
      int startL2EN;
      int lastStrongRTL;
      short state;
      byte runLevel;

      private LevState() {
      }

      // $FF: synthetic method
      LevState(Object var2) {
         this();
      }
   }

   private static class ImpTabPair {
      byte[][][] imptab;
      short[][] impact;

      ImpTabPair(byte[][] var1, byte[][] var2, short[] var3, short[] var4) {
         this.imptab = new byte[][][]{var1, var2};
         this.impact = new short[][]{var3, var4};
      }
   }

   class InsertPoints {
      int size;
      int confirmed;
      BidiBase.Point[] points = new BidiBase.Point[0];
   }

   class Point {
      int pos;
      int flag;
   }
}
