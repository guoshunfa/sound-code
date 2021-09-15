package com.sun.jmx.snmp.IPAcl;

import java.io.IOException;

class ParserTokenManager implements ParserConstants {
   static final long[] jjbitVec0 = new long[]{0L, 0L, -1L, -1L};
   static final int[] jjnextStates = new int[]{18, 19, 21, 28, 29, 39, 23, 24, 26, 27, 41, 42, 7, 8, 10, 18, 20, 21, 44, 46, 13, 1, 2, 4, 37, 28, 38, 26, 27, 37, 28, 38, 15, 16};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, "access", "acl", "=", "communities", "enterprise", "hosts", "{", "managers", "-", "}", "read-only", "read-write", "trap", "inform", "trap-community", "inform-community", "trap-num", null, null, null, null, null, null, null, null, null, null, null, null, ",", ".", "!", "/"};
   public static final String[] lexStateNames = new String[]{"DEFAULT"};
   static final long[] jjtoToken = new long[]{1067601362817L};
   static final long[] jjtoSkip = new long[]{126L};
   private ASCII_CharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   protected char curChar;
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   private final int jjStopStringLiteralDfa_0(int var1, long var2) {
      switch(var1) {
      case 0:
         if ((var2 & 32768L) != 0L) {
            return 0;
         } else if ((var2 & 16666624L) != 0L) {
            this.jjmatchedKind = 31;
            return 47;
         } else {
            if ((var2 & 3456L) != 0L) {
               this.jjmatchedKind = 31;
               return 48;
            }

            return -1;
         }
      case 1:
         if ((var2 & 16669696L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 1;
            return 49;
         } else {
            if ((var2 & 384L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 1;
               return 50;
            }

            return -1;
         }
      case 2:
         if ((var2 & 16669696L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 2;
            return 49;
         } else if ((var2 & 256L) != 0L) {
            return 49;
         } else {
            if ((var2 & 128L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 2;
               return 50;
            }

            return -1;
         }
      case 3:
         if ((var2 & 5659648L) != 0L) {
            if (this.jjmatchedPos != 3) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 3;
            }

            return 49;
         } else if ((var2 & 11010048L) != 0L) {
            return 49;
         } else {
            if ((var2 & 128L) != 0L) {
               if (this.jjmatchedPos != 3) {
                  this.jjmatchedKind = 31;
                  this.jjmatchedPos = 3;
               }

               return 50;
            }

            return -1;
         }
      case 4:
         if ((var2 & 10485760L) != 0L) {
            return 51;
         } else if ((var2 & 393216L) != 0L) {
            if (this.jjmatchedPos < 3) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 3;
            }

            return 51;
         } else if ((var2 & 4096L) != 0L) {
            return 49;
         } else {
            if ((var2 & 5262464L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 4;
               return 49;
            }

            return -1;
         }
      case 5:
         if ((var2 & 5243008L) != 0L) {
            return 49;
         } else if ((var2 & 19456L) != 0L) {
            if (this.jjmatchedPos != 5) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 5;
            }

            return 49;
         } else {
            if ((var2 & 10878976L) != 0L) {
               if (this.jjmatchedPos != 5) {
                  this.jjmatchedKind = 31;
                  this.jjmatchedPos = 5;
               }

               return 51;
            }

            return -1;
         }
      case 6:
         if ((var2 & 4194304L) != 0L) {
            return 51;
         } else if ((var2 & 19456L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 6;
            return 49;
         } else {
            if ((var2 & 10878976L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 6;
               return 51;
            }

            return -1;
         }
      case 7:
         if ((var2 & 6684672L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 7;
            return 51;
         } else if ((var2 & 8388608L) != 0L) {
            return 51;
         } else if ((var2 & 16384L) != 0L) {
            return 49;
         } else {
            if ((var2 & 3072L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 7;
               return 49;
            }

            return -1;
         }
      case 8:
         if ((var2 & 131072L) != 0L) {
            return 51;
         } else if ((var2 & 3072L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 8;
            return 49;
         } else {
            if ((var2 & 6553600L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 8;
               return 51;
            }

            return -1;
         }
      case 9:
         if ((var2 & 262144L) != 0L) {
            return 51;
         } else if ((var2 & 2048L) != 0L) {
            return 49;
         } else if ((var2 & 6291456L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 9;
            return 51;
         } else {
            if ((var2 & 1024L) != 0L) {
               this.jjmatchedKind = 31;
               this.jjmatchedPos = 9;
               return 49;
            }

            return -1;
         }
      case 10:
         if ((var2 & 6291456L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 10;
            return 51;
         } else {
            if ((var2 & 1024L) != 0L) {
               return 49;
            }

            return -1;
         }
      case 11:
         if ((var2 & 6291456L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 11;
            return 51;
         }

         return -1;
      case 12:
         if ((var2 & 6291456L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 12;
            return 51;
         }

         return -1;
      case 13:
         if ((var2 & 4194304L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 13;
            return 51;
         } else {
            if ((var2 & 2097152L) != 0L) {
               return 51;
            }

            return -1;
         }
      case 14:
         if ((var2 & 4194304L) != 0L) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 14;
            return 51;
         }

         return -1;
      default:
         return -1;
      }
   }

   private final int jjStartNfa_0(int var1, long var2) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(var1, var2), var1 + 1);
   }

   private final int jjStopAtPos(int var1, int var2) {
      this.jjmatchedKind = var2;
      this.jjmatchedPos = var1;
      return var1 + 1;
   }

   private final int jjStartNfaWithStates_0(int var1, int var2, int var3) {
      this.jjmatchedKind = var2;
      this.jjmatchedPos = var1;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return var1 + 1;
      }

      return this.jjMoveNfa_0(var3, var1 + 1);
   }

   private final int jjMoveStringLiteralDfa0_0() {
      switch(this.curChar) {
      case '!':
         return this.jjStopAtPos(0, 38);
      case ',':
         return this.jjStopAtPos(0, 36);
      case '-':
         return this.jjStartNfaWithStates_0(0, 15, 0);
      case '.':
         return this.jjStopAtPos(0, 37);
      case '/':
         return this.jjStopAtPos(0, 39);
      case '=':
         return this.jjStopAtPos(0, 9);
      case 'a':
         return this.jjMoveStringLiteralDfa1_0(384L);
      case 'c':
         return this.jjMoveStringLiteralDfa1_0(1024L);
      case 'e':
         return this.jjMoveStringLiteralDfa1_0(2048L);
      case 'h':
         return this.jjMoveStringLiteralDfa1_0(4096L);
      case 'i':
         return this.jjMoveStringLiteralDfa1_0(5242880L);
      case 'm':
         return this.jjMoveStringLiteralDfa1_0(16384L);
      case 'r':
         return this.jjMoveStringLiteralDfa1_0(393216L);
      case 't':
         return this.jjMoveStringLiteralDfa1_0(11010048L);
      case '{':
         return this.jjStopAtPos(0, 13);
      case '}':
         return this.jjStopAtPos(0, 16);
      default:
         return this.jjMoveNfa_0(5, 0);
      }
   }

   private final int jjMoveStringLiteralDfa1_0(long var1) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_0(0, var1);
         return 1;
      }

      switch(this.curChar) {
      case 'a':
         return this.jjMoveStringLiteralDfa2_0(var1, 16384L);
      case 'b':
      case 'd':
      case 'f':
      case 'g':
      case 'h':
      case 'i':
      case 'j':
      case 'k':
      case 'l':
      case 'm':
      case 'p':
      case 'q':
      default:
         return this.jjStartNfa_0(0, var1);
      case 'c':
         return this.jjMoveStringLiteralDfa2_0(var1, 384L);
      case 'e':
         return this.jjMoveStringLiteralDfa2_0(var1, 393216L);
      case 'n':
         return this.jjMoveStringLiteralDfa2_0(var1, 5244928L);
      case 'o':
         return this.jjMoveStringLiteralDfa2_0(var1, 5120L);
      case 'r':
         return this.jjMoveStringLiteralDfa2_0(var1, 11010048L);
      }
   }

   private final int jjMoveStringLiteralDfa2_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(0, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(1, var3);
            return 2;
         }

         switch(this.curChar) {
         case 'a':
            return this.jjMoveStringLiteralDfa3_0(var3, 11403264L);
         case 'c':
            return this.jjMoveStringLiteralDfa3_0(var3, 128L);
         case 'f':
            return this.jjMoveStringLiteralDfa3_0(var3, 5242880L);
         case 'l':
            if ((var3 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 8, 49);
            }
         case 'b':
         case 'd':
         case 'e':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         default:
            return this.jjStartNfa_0(1, var3);
         case 'm':
            return this.jjMoveStringLiteralDfa3_0(var3, 1024L);
         case 'n':
            return this.jjMoveStringLiteralDfa3_0(var3, 16384L);
         case 's':
            return this.jjMoveStringLiteralDfa3_0(var3, 4096L);
         case 't':
            return this.jjMoveStringLiteralDfa3_0(var3, 2048L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa3_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(1, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(2, var3);
            return 3;
         }

         switch(this.curChar) {
         case 'a':
            return this.jjMoveStringLiteralDfa4_0(var3, 16384L);
         case 'b':
         case 'c':
         case 'f':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'n':
         case 'q':
         case 'r':
         case 's':
         default:
            return this.jjStartNfa_0(2, var3);
         case 'd':
            return this.jjMoveStringLiteralDfa4_0(var3, 393216L);
         case 'e':
            return this.jjMoveStringLiteralDfa4_0(var3, 2176L);
         case 'm':
            return this.jjMoveStringLiteralDfa4_0(var3, 1024L);
         case 'o':
            return this.jjMoveStringLiteralDfa4_0(var3, 5242880L);
         case 'p':
            if ((var3 & 524288L) != 0L) {
               this.jjmatchedKind = 19;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(var3, 10485760L);
         case 't':
            return this.jjMoveStringLiteralDfa4_0(var3, 4096L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa4_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(2, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(3, var3);
            return 4;
         }

         switch(this.curChar) {
         case '-':
            return this.jjMoveStringLiteralDfa5_0(var3, 10878976L);
         case 'g':
            return this.jjMoveStringLiteralDfa5_0(var3, 16384L);
         case 'r':
            return this.jjMoveStringLiteralDfa5_0(var3, 5244928L);
         case 's':
            if ((var3 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 12, 49);
            }

            return this.jjMoveStringLiteralDfa5_0(var3, 128L);
         case 'u':
            return this.jjMoveStringLiteralDfa5_0(var3, 1024L);
         default:
            return this.jjStartNfa_0(3, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa5_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(3, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(4, var3);
            return 5;
         }

         switch(this.curChar) {
         case 'c':
            return this.jjMoveStringLiteralDfa6_0(var3, 2097152L);
         case 'e':
            return this.jjMoveStringLiteralDfa6_0(var3, 16384L);
         case 'm':
            if ((var3 & 1048576L) != 0L) {
               this.jjmatchedKind = 20;
               this.jjmatchedPos = 5;
            }

            return this.jjMoveStringLiteralDfa6_0(var3, 4194304L);
         case 'n':
            return this.jjMoveStringLiteralDfa6_0(var3, 8389632L);
         case 'o':
            return this.jjMoveStringLiteralDfa6_0(var3, 131072L);
         case 'p':
            return this.jjMoveStringLiteralDfa6_0(var3, 2048L);
         case 's':
            if ((var3 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 7, 49);
            }
         case 'd':
         case 'f':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'q':
         case 'r':
         case 't':
         case 'u':
         case 'v':
         default:
            return this.jjStartNfa_0(4, var3);
         case 'w':
            return this.jjMoveStringLiteralDfa6_0(var3, 262144L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa6_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(4, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(5, var3);
            return 6;
         }

         switch(this.curChar) {
         case '-':
            return this.jjMoveStringLiteralDfa7_0(var3, 4194304L);
         case 'i':
            return this.jjMoveStringLiteralDfa7_0(var3, 1024L);
         case 'n':
            return this.jjMoveStringLiteralDfa7_0(var3, 131072L);
         case 'o':
            return this.jjMoveStringLiteralDfa7_0(var3, 2097152L);
         case 'r':
            return this.jjMoveStringLiteralDfa7_0(var3, 280576L);
         case 'u':
            return this.jjMoveStringLiteralDfa7_0(var3, 8388608L);
         default:
            return this.jjStartNfa_0(5, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa7_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(5, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(6, var3);
            return 7;
         }

         switch(this.curChar) {
         case 'c':
            return this.jjMoveStringLiteralDfa8_0(var3, 4194304L);
         case 'i':
            return this.jjMoveStringLiteralDfa8_0(var3, 264192L);
         case 'l':
            return this.jjMoveStringLiteralDfa8_0(var3, 131072L);
         case 'm':
            if ((var3 & 8388608L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 23, 51);
            }

            return this.jjMoveStringLiteralDfa8_0(var3, 2097152L);
         case 's':
            if ((var3 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 14, 49);
            }
         case 'd':
         case 'e':
         case 'f':
         case 'g':
         case 'h':
         case 'j':
         case 'k':
         case 'n':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         default:
            return this.jjStartNfa_0(6, var3);
         case 't':
            return this.jjMoveStringLiteralDfa8_0(var3, 1024L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa8_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(6, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(7, var3);
            return 8;
         }

         switch(this.curChar) {
         case 'i':
            return this.jjMoveStringLiteralDfa9_0(var3, 1024L);
         case 'm':
            return this.jjMoveStringLiteralDfa9_0(var3, 2097152L);
         case 'o':
            return this.jjMoveStringLiteralDfa9_0(var3, 4194304L);
         case 's':
            return this.jjMoveStringLiteralDfa9_0(var3, 2048L);
         case 't':
            return this.jjMoveStringLiteralDfa9_0(var3, 262144L);
         case 'y':
            if ((var3 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 17, 51);
            }
         case 'j':
         case 'k':
         case 'l':
         case 'n':
         case 'p':
         case 'q':
         case 'r':
         case 'u':
         case 'v':
         case 'w':
         case 'x':
         default:
            return this.jjStartNfa_0(7, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa9_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(7, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(8, var3);
            return 9;
         }

         switch(this.curChar) {
         case 'e':
            if ((var3 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 11, 49);
            } else {
               if ((var3 & 262144L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 18, 51);
               }

               return this.jjMoveStringLiteralDfa10_0(var3, 1024L);
            }
         case 'm':
            return this.jjMoveStringLiteralDfa10_0(var3, 4194304L);
         case 'u':
            return this.jjMoveStringLiteralDfa10_0(var3, 2097152L);
         default:
            return this.jjStartNfa_0(8, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa10_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(8, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(9, var3);
            return 10;
         }

         switch(this.curChar) {
         case 'm':
            return this.jjMoveStringLiteralDfa11_0(var3, 4194304L);
         case 'n':
            return this.jjMoveStringLiteralDfa11_0(var3, 2097152L);
         case 's':
            if ((var3 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 10, 49);
            }
         default:
            return this.jjStartNfa_0(9, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa11_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(9, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(10, var3);
            return 11;
         }

         switch(this.curChar) {
         case 'i':
            return this.jjMoveStringLiteralDfa12_0(var3, 2097152L);
         case 'u':
            return this.jjMoveStringLiteralDfa12_0(var3, 4194304L);
         default:
            return this.jjStartNfa_0(10, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa12_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(10, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(11, var3);
            return 12;
         }

         switch(this.curChar) {
         case 'n':
            return this.jjMoveStringLiteralDfa13_0(var3, 4194304L);
         case 't':
            return this.jjMoveStringLiteralDfa13_0(var3, 2097152L);
         default:
            return this.jjStartNfa_0(11, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa13_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(11, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(12, var3);
            return 13;
         }

         switch(this.curChar) {
         case 'i':
            return this.jjMoveStringLiteralDfa14_0(var3, 4194304L);
         case 'y':
            if ((var3 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 21, 51);
            }
         default:
            return this.jjStartNfa_0(12, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa14_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(12, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(13, var3);
            return 14;
         }

         switch(this.curChar) {
         case 't':
            return this.jjMoveStringLiteralDfa15_0(var3, 4194304L);
         default:
            return this.jjStartNfa_0(13, var3);
         }
      }
   }

   private final int jjMoveStringLiteralDfa15_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(13, var1);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(14, var3);
            return 15;
         }

         switch(this.curChar) {
         case 'y':
            if ((var3 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(15, 22, 51);
            }
         default:
            return this.jjStartNfa_0(14, var3);
         }
      }
   }

   private final void jjCheckNAdd(int var1) {
      if (this.jjrounds[var1] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = var1;
         this.jjrounds[var1] = this.jjround;
      }

   }

   private final void jjAddStates(int var1, int var2) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[var1];
      } while(var1++ != var2);

   }

   private final void jjCheckNAddTwoStates(int var1, int var2) {
      this.jjCheckNAdd(var1);
      this.jjCheckNAdd(var2);
   }

   private final void jjCheckNAddStates(int var1, int var2) {
      do {
         this.jjCheckNAdd(jjnextStates[var1]);
      } while(var1++ != var2);

   }

   private final void jjCheckNAddStates(int var1) {
      this.jjCheckNAdd(jjnextStates[var1]);
      this.jjCheckNAdd(jjnextStates[var1 + 1]);
   }

   private final int jjMoveNfa_0(int var1, int var2) {
      int var4 = 0;
      this.jjnewStateCnt = 47;
      int var5 = 1;
      this.jjstateSet[0] = var1;
      int var7 = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long var12;
         if (this.curChar < '@') {
            var12 = 1L << this.curChar;

            do {
               --var5;
               switch(this.jjstateSet[var5]) {
               case 0:
                  if (this.curChar == '-') {
                     this.jjCheckNAddStates(21, 23);
                  }
                  break;
               case 1:
                  if ((-9217L & var12) != 0L) {
                     this.jjCheckNAddStates(21, 23);
                  }
                  break;
               case 2:
                  if ((9216L & var12) != 0L && var7 > 5) {
                     var7 = 5;
                  }
                  break;
               case 3:
                  if (this.curChar == '\n' && var7 > 5) {
                     var7 = 5;
                  }
                  break;
               case 4:
                  if (this.curChar == '\r') {
                     this.jjstateSet[this.jjnewStateCnt++] = 3;
                  }
                  break;
               case 5:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddStates(6, 9);
                  } else if (this.curChar == ':') {
                     this.jjAddStates(10, 11);
                  } else if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(15, 16);
                  } else if (this.curChar == '#') {
                     this.jjCheckNAddStates(12, 14);
                  } else if (this.curChar == '-') {
                     this.jjstateSet[this.jjnewStateCnt++] = 0;
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(15, 17);
                  }

                  if ((287667426198290432L & var12) != 0L) {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddTwoStates(12, 13);
                  } else if (this.curChar == '0') {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddStates(18, 20);
                  }
                  break;
               case 6:
                  if (this.curChar == '#') {
                     this.jjCheckNAddStates(12, 14);
                  }
                  break;
               case 7:
                  if ((-9217L & var12) != 0L) {
                     this.jjCheckNAddStates(12, 14);
                  }
                  break;
               case 8:
                  if ((9216L & var12) != 0L && var7 > 6) {
                     var7 = 6;
                  }
                  break;
               case 9:
                  if (this.curChar == '\n' && var7 > 6) {
                     var7 = 6;
                  }
                  break;
               case 10:
                  if (this.curChar == '\r') {
                     this.jjstateSet[this.jjnewStateCnt++] = 9;
                  }
                  break;
               case 11:
                  if ((287667426198290432L & var12) != 0L) {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddTwoStates(12, 13);
                  }
                  break;
               case 12:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddTwoStates(12, 13);
                  }
               case 13:
               case 44:
               default:
                  break;
               case 14:
                  if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(15, 16);
                  }
                  break;
               case 15:
                  if ((-17179869185L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(15, 16);
                  }
                  break;
               case 16:
                  if (this.curChar == '"' && var7 > 35) {
                     var7 = 35;
                  }
                  break;
               case 17:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(15, 17);
                  }
                  break;
               case 18:
                  if ((287984085547089920L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }
                  break;
               case 19:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }
                  break;
               case 20:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }
                  break;
               case 21:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }
                  break;
               case 22:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddStates(6, 9);
                  }
                  break;
               case 23:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 24:
                  if (this.curChar == ':') {
                     this.jjCheckNAddTwoStates(23, 25);
                  }
                  break;
               case 25:
               case 41:
                  if (this.curChar == ':' && var7 > 28) {
                     var7 = 28;
                  }
                  break;
               case 26:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(26, 27);
                  }
                  break;
               case 27:
                  if (this.curChar == ':') {
                     this.jjCheckNAddStates(3, 5);
                  }
                  break;
               case 28:
               case 42:
                  if (this.curChar == ':') {
                     this.jjCheckNAddTwoStates(29, 36);
                  }
                  break;
               case 29:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(29, 30);
                  }
                  break;
               case 30:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(31);
                  }
                  break;
               case 31:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(31, 32);
                  }
                  break;
               case 32:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(33);
                  }
                  break;
               case 33:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(33, 34);
                  }
                  break;
               case 34:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(35);
                  }
                  break;
               case 35:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAdd(35);
                  }
                  break;
               case 36:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAddStates(24, 26);
                  }
                  break;
               case 37:
                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(37, 28);
                  }
                  break;
               case 38:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAdd(38);
                  }
                  break;
               case 39:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAddStates(27, 31);
                  }
                  break;
               case 40:
                  if (this.curChar == ':') {
                     this.jjAddStates(10, 11);
                  }
                  break;
               case 43:
                  if (this.curChar == '0') {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddStates(18, 20);
                  }
                  break;
               case 45:
                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddTwoStates(45, 13);
                  }
                  break;
               case 46:
                  if ((71776119061217280L & var12) != 0L) {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddTwoStates(46, 13);
                  }
                  break;
               case 47:
                  if ((287984085547089920L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }
                  break;
               case 48:
                  if ((287984085547089920L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  } else if (this.curChar == ':') {
                     this.jjCheckNAddStates(3, 5);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  } else if (this.curChar == ':') {
                     this.jjCheckNAddTwoStates(23, 25);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(26, 27);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 49:
                  if ((287984085547089920L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }
                  break;
               case 50:
                  if ((287984085547089920L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  } else if (this.curChar == ':') {
                     this.jjCheckNAddStates(3, 5);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  } else if (this.curChar == ':') {
                     this.jjCheckNAddTwoStates(23, 25);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(26, 27);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 51:
                  if ((287984085547089920L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((287948901175001088L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }
               }
            } while(var5 != var4);
         } else if (this.curChar < 128) {
            var12 = 1L << (this.curChar & 63);

            do {
               --var5;
               switch(this.jjstateSet[var5]) {
               case 1:
                  this.jjAddStates(21, 23);
               case 2:
               case 3:
               case 4:
               case 6:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               case 14:
               case 16:
               case 24:
               case 25:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
               case 40:
               case 41:
               case 42:
               case 43:
               case 46:
               default:
                  break;
               case 5:
                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(15, 17);
                  }

                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddStates(6, 9);
                  }
                  break;
               case 7:
                  this.jjAddStates(12, 14);
                  break;
               case 13:
                  if ((17592186048512L & var12) != 0L && var7 > 24) {
                     var7 = 24;
                  }
                  break;
               case 15:
                  this.jjAddStates(32, 33);
                  break;
               case 17:
                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(15, 17);
                  }
                  break;
               case 18:
                  if ((576460745995190270L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }
                  break;
               case 19:
                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }
                  break;
               case 20:
                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }
                  break;
               case 21:
                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }
                  break;
               case 22:
                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddStates(6, 9);
                  }
                  break;
               case 23:
                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 26:
                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(26, 27);
                  }
                  break;
               case 36:
                  if ((541165879422L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAddStates(24, 26);
                  }
                  break;
               case 37:
                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(37, 28);
                  }
                  break;
               case 38:
                  if ((541165879422L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAdd(38);
                  }
                  break;
               case 39:
                  if ((541165879422L & var12) != 0L) {
                     if (var7 > 28) {
                        var7 = 28;
                     }

                     this.jjCheckNAddStates(27, 31);
                  }
                  break;
               case 44:
                  if ((72057594054705152L & var12) != 0L) {
                     this.jjCheckNAdd(45);
                  }
                  break;
               case 45:
                  if ((541165879422L & var12) != 0L) {
                     if (var7 > 24) {
                        var7 = 24;
                     }

                     this.jjCheckNAddTwoStates(45, 13);
                  }
                  break;
               case 47:
                  if ((576460745995190270L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }
                  break;
               case 48:
                  if ((576460745995190270L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }

                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(26, 27);
                  }

                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 49:
                  if ((576460745995190270L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }
                  break;
               case 50:
                  if ((576460745995190270L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAddStates(0, 2);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(20);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }

                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(26, 27);
                  }

                  if ((541165879422L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 51:
                  if ((576460745995190270L & var12) != 0L) {
                     this.jjCheckNAddTwoStates(18, 19);
                  }

                  if ((576460743847706622L & var12) != 0L) {
                     if (var7 > 31) {
                        var7 = 31;
                     }

                     this.jjCheckNAdd(19);
                  }
               }
            } while(var5 != var4);
         } else {
            int var8 = (this.curChar & 255) >> 6;
            long var9 = 1L << (this.curChar & 63);

            do {
               --var5;
               switch(this.jjstateSet[var5]) {
               case 1:
                  if ((jjbitVec0[var8] & var9) != 0L) {
                     this.jjAddStates(21, 23);
                  }
                  break;
               case 7:
                  if ((jjbitVec0[var8] & var9) != 0L) {
                     this.jjAddStates(12, 14);
                  }
                  break;
               case 15:
                  if ((jjbitVec0[var8] & var9) != 0L) {
                     this.jjAddStates(32, 33);
                  }
               }
            } while(var5 != var4);
         }

         if (var7 != Integer.MAX_VALUE) {
            this.jjmatchedKind = var7;
            this.jjmatchedPos = var2;
            var7 = Integer.MAX_VALUE;
         }

         ++var2;
         if ((var5 = this.jjnewStateCnt) == (var4 = 47 - (this.jjnewStateCnt = var4))) {
            return var2;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var11) {
            return var2;
         }
      }
   }

   public ParserTokenManager(ASCII_CharStream var1) {
      this.jjrounds = new int[47];
      this.jjstateSet = new int[94];
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = var1;
   }

   public ParserTokenManager(ASCII_CharStream var1, int var2) {
      this(var1);
      this.SwitchTo(var2);
   }

   public void ReInit(ASCII_CharStream var1) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = var1;
      this.ReInitRounds();
   }

   private final void ReInitRounds() {
      this.jjround = -2147483647;

      for(int var1 = 47; var1-- > 0; this.jjrounds[var1] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(ASCII_CharStream var1, int var2) {
      this.ReInit(var1);
      this.SwitchTo(var2);
   }

   public void SwitchTo(int var1) {
      if (var1 < 1 && var1 >= 0) {
         this.curLexState = var1;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + var1 + ". State unchanged.", 2);
      }
   }

   private final Token jjFillToken() {
      Token var1 = Token.newToken(this.jjmatchedKind);
      var1.kind = this.jjmatchedKind;
      String var2 = jjstrLiteralImages[this.jjmatchedKind];
      var1.image = var2 == null ? this.input_stream.GetImage() : var2;
      var1.beginLine = this.input_stream.getBeginLine();
      var1.beginColumn = this.input_stream.getBeginColumn();
      var1.endLine = this.input_stream.getEndLine();
      var1.endColumn = this.input_stream.getEndColumn();
      return var1;
   }

   public final Token getNextToken() {
      Object var2 = null;
      boolean var4 = false;

      while(true) {
         Token var3;
         try {
            this.curChar = this.input_stream.BeginToken();
         } catch (IOException var10) {
            this.jjmatchedKind = 0;
            var3 = this.jjFillToken();
            return var3;
         }

         try {
            this.input_stream.backup(0);

            while(this.curChar <= ' ' && (4294977024L & 1L << this.curChar) != 0L) {
               this.curChar = this.input_stream.BeginToken();
            }
         } catch (IOException var12) {
            continue;
         }

         this.jjmatchedKind = Integer.MAX_VALUE;
         this.jjmatchedPos = 0;
         int var13 = this.jjMoveStringLiteralDfa0_0();
         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int var5 = this.input_stream.getEndLine();
            int var6 = this.input_stream.getEndColumn();
            String var7 = null;
            boolean var8 = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var11) {
               var8 = true;
               var7 = var13 <= 1 ? "" : this.input_stream.GetImage();
               if (this.curChar != '\n' && this.curChar != '\r') {
                  ++var6;
               } else {
                  ++var5;
                  var6 = 0;
               }
            }

            if (!var8) {
               this.input_stream.backup(1);
               var7 = var13 <= 1 ? "" : this.input_stream.GetImage();
            }

            throw new TokenMgrError(var8, this.curLexState, var5, var6, var7, this.curChar, 0);
         }

         if (this.jjmatchedPos + 1 < var13) {
            this.input_stream.backup(var13 - this.jjmatchedPos - 1);
         }

         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            var3 = this.jjFillToken();
            return var3;
         }
      }
   }
}
