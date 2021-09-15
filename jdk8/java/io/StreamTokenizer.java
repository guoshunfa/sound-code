package java.io;

import java.util.Arrays;

public class StreamTokenizer {
   private Reader reader;
   private InputStream input;
   private char[] buf;
   private int peekc;
   private static final int NEED_CHAR = Integer.MAX_VALUE;
   private static final int SKIP_LF = 2147483646;
   private boolean pushedBack;
   private boolean forceLower;
   private int LINENO;
   private boolean eolIsSignificantP;
   private boolean slashSlashCommentsP;
   private boolean slashStarCommentsP;
   private byte[] ctype;
   private static final byte CT_WHITESPACE = 1;
   private static final byte CT_DIGIT = 2;
   private static final byte CT_ALPHA = 4;
   private static final byte CT_QUOTE = 8;
   private static final byte CT_COMMENT = 16;
   public int ttype;
   public static final int TT_EOF = -1;
   public static final int TT_EOL = 10;
   public static final int TT_NUMBER = -2;
   public static final int TT_WORD = -3;
   private static final int TT_NOTHING = -4;
   public String sval;
   public double nval;

   private StreamTokenizer() {
      this.reader = null;
      this.input = null;
      this.buf = new char[20];
      this.peekc = Integer.MAX_VALUE;
      this.LINENO = 1;
      this.eolIsSignificantP = false;
      this.slashSlashCommentsP = false;
      this.slashStarCommentsP = false;
      this.ctype = new byte[256];
      this.ttype = -4;
      this.wordChars(97, 122);
      this.wordChars(65, 90);
      this.wordChars(160, 255);
      this.whitespaceChars(0, 32);
      this.commentChar(47);
      this.quoteChar(34);
      this.quoteChar(39);
      this.parseNumbers();
   }

   /** @deprecated */
   @Deprecated
   public StreamTokenizer(InputStream var1) {
      this();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.input = var1;
      }
   }

   public StreamTokenizer(Reader var1) {
      this();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.reader = var1;
      }
   }

   public void resetSyntax() {
      int var1 = this.ctype.length;

      while(true) {
         --var1;
         if (var1 < 0) {
            return;
         }

         this.ctype[var1] = 0;
      }
   }

   public void wordChars(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 >= this.ctype.length) {
         var2 = this.ctype.length - 1;
      }

      while(var1 <= var2) {
         byte[] var10000 = this.ctype;
         int var10001 = var1++;
         var10000[var10001] = (byte)(var10000[var10001] | 4);
      }

   }

   public void whitespaceChars(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 >= this.ctype.length) {
         var2 = this.ctype.length - 1;
      }

      while(var1 <= var2) {
         this.ctype[var1++] = 1;
      }

   }

   public void ordinaryChars(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 >= this.ctype.length) {
         var2 = this.ctype.length - 1;
      }

      while(var1 <= var2) {
         this.ctype[var1++] = 0;
      }

   }

   public void ordinaryChar(int var1) {
      if (var1 >= 0 && var1 < this.ctype.length) {
         this.ctype[var1] = 0;
      }

   }

   public void commentChar(int var1) {
      if (var1 >= 0 && var1 < this.ctype.length) {
         this.ctype[var1] = 16;
      }

   }

   public void quoteChar(int var1) {
      if (var1 >= 0 && var1 < this.ctype.length) {
         this.ctype[var1] = 8;
      }

   }

   public void parseNumbers() {
      byte[] var10000;
      for(int var1 = 48; var1 <= 57; ++var1) {
         var10000 = this.ctype;
         var10000[var1] = (byte)(var10000[var1] | 2);
      }

      var10000 = this.ctype;
      var10000[46] = (byte)(var10000[46] | 2);
      var10000 = this.ctype;
      var10000[45] = (byte)(var10000[45] | 2);
   }

   public void eolIsSignificant(boolean var1) {
      this.eolIsSignificantP = var1;
   }

   public void slashStarComments(boolean var1) {
      this.slashStarCommentsP = var1;
   }

   public void slashSlashComments(boolean var1) {
      this.slashSlashCommentsP = var1;
   }

   public void lowerCaseMode(boolean var1) {
      this.forceLower = var1;
   }

   private int read() throws IOException {
      if (this.reader != null) {
         return this.reader.read();
      } else if (this.input != null) {
         return this.input.read();
      } else {
         throw new IllegalStateException();
      }
   }

   public int nextToken() throws IOException {
      if (this.pushedBack) {
         this.pushedBack = false;
         return this.ttype;
      } else {
         byte[] var1 = this.ctype;
         this.sval = null;
         int var2 = this.peekc;
         if (var2 < 0) {
            var2 = Integer.MAX_VALUE;
         }

         if (var2 == 2147483646) {
            var2 = this.read();
            if (var2 < 0) {
               return this.ttype = -1;
            }

            if (var2 == 10) {
               var2 = Integer.MAX_VALUE;
            }
         }

         if (var2 == Integer.MAX_VALUE) {
            var2 = this.read();
            if (var2 < 0) {
               return this.ttype = -1;
            }
         }

         this.ttype = var2;
         this.peekc = Integer.MAX_VALUE;

         byte var3;
         for(var3 = var2 < 256 ? var1[var2] : 4; (var3 & 1) != 0; var3 = var2 < 256 ? var1[var2] : 4) {
            if (var2 == 13) {
               ++this.LINENO;
               if (this.eolIsSignificantP) {
                  this.peekc = 2147483646;
                  return this.ttype = 10;
               }

               var2 = this.read();
               if (var2 == 10) {
                  var2 = this.read();
               }
            } else {
               if (var2 == 10) {
                  ++this.LINENO;
                  if (this.eolIsSignificantP) {
                     return this.ttype = 10;
                  }
               }

               var2 = this.read();
            }

            if (var2 < 0) {
               return this.ttype = -1;
            }
         }

         int var7;
         if ((var3 & 2) != 0) {
            boolean var11 = false;
            if (var2 == 45) {
               var2 = this.read();
               if (var2 != 46 && (var2 < 48 || var2 > 57)) {
                  this.peekc = var2;
                  return this.ttype = 45;
               }

               var11 = true;
            }

            double var12 = 0.0D;
            var7 = 0;
            byte var8 = 0;

            while(true) {
               if (var2 == 46 && var8 == 0) {
                  var8 = 1;
               } else {
                  if (48 > var2 || var2 > 57) {
                     this.peekc = var2;
                     if (var7 != 0) {
                        double var9 = 10.0D;
                        --var7;

                        while(var7 > 0) {
                           var9 *= 10.0D;
                           --var7;
                        }

                        var12 /= var9;
                     }

                     this.nval = var11 ? -var12 : var12;
                     return this.ttype = -2;
                  }

                  var12 = var12 * 10.0D + (double)(var2 - 48);
                  var7 += var8;
               }

               var2 = this.read();
            }
         } else {
            int var4;
            if ((var3 & 4) != 0) {
               var4 = 0;

               do {
                  if (var4 >= this.buf.length) {
                     this.buf = Arrays.copyOf(this.buf, this.buf.length * 2);
                  }

                  this.buf[var4++] = (char)var2;
                  var2 = this.read();
                  var3 = var2 < 0 ? 1 : (var2 < 256 ? var1[var2] : 4);
               } while((var3 & 6) != 0);

               this.peekc = var2;
               this.sval = String.copyValueOf(this.buf, 0, var4);
               if (this.forceLower) {
                  this.sval = this.sval.toLowerCase();
               }

               return this.ttype = -3;
            } else if ((var3 & 8) != 0) {
               this.ttype = var2;
               var4 = 0;

               int var5;
               for(var5 = this.read(); var5 >= 0 && var5 != this.ttype && var5 != 10 && var5 != 13; this.buf[var4++] = (char)var2) {
                  if (var5 == 92) {
                     var2 = this.read();
                     int var6 = var2;
                     if (var2 >= 48 && var2 <= 55) {
                        var2 -= 48;
                        var7 = this.read();
                        if (48 <= var7 && var7 <= 55) {
                           var2 = (var2 << 3) + (var7 - 48);
                           var7 = this.read();
                           if (48 <= var7 && var7 <= 55 && var6 <= 51) {
                              var2 = (var2 << 3) + (var7 - 48);
                              var5 = this.read();
                           } else {
                              var5 = var7;
                           }
                        } else {
                           var5 = var7;
                        }
                     } else {
                        switch(var2) {
                        case 97:
                           var2 = 7;
                           break;
                        case 98:
                           var2 = 8;
                        case 99:
                        case 100:
                        case 101:
                        case 103:
                        case 104:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 111:
                        case 112:
                        case 113:
                        case 115:
                        case 117:
                        default:
                           break;
                        case 102:
                           var2 = 12;
                           break;
                        case 110:
                           var2 = 10;
                           break;
                        case 114:
                           var2 = 13;
                           break;
                        case 116:
                           var2 = 9;
                           break;
                        case 118:
                           var2 = 11;
                        }

                        var5 = this.read();
                     }
                  } else {
                     var2 = var5;
                     var5 = this.read();
                  }

                  if (var4 >= this.buf.length) {
                     this.buf = Arrays.copyOf(this.buf, this.buf.length * 2);
                  }
               }

               this.peekc = var5 == this.ttype ? Integer.MAX_VALUE : var5;
               this.sval = String.copyValueOf(this.buf, 0, var4);
               return this.ttype;
            } else if (var2 != 47 || !this.slashSlashCommentsP && !this.slashStarCommentsP) {
               if ((var3 & 16) == 0) {
                  return this.ttype = var2;
               } else {
                  while((var2 = this.read()) != 10 && var2 != 13 && var2 >= 0) {
                  }

                  this.peekc = var2;
                  return this.nextToken();
               }
            } else {
               var2 = this.read();
               if (var2 == 42 && this.slashStarCommentsP) {
                  for(var4 = 0; (var2 = this.read()) != 47 || var4 != 42; var4 = var2) {
                     if (var2 == 13) {
                        ++this.LINENO;
                        var2 = this.read();
                        if (var2 == 10) {
                           var2 = this.read();
                        }
                     } else if (var2 == 10) {
                        ++this.LINENO;
                        var2 = this.read();
                     }

                     if (var2 < 0) {
                        return this.ttype = -1;
                     }
                  }

                  return this.nextToken();
               } else if (var2 == 47 && this.slashSlashCommentsP) {
                  while((var2 = this.read()) != 10 && var2 != 13 && var2 >= 0) {
                  }

                  this.peekc = var2;
                  return this.nextToken();
               } else if ((var1[47] & 16) == 0) {
                  this.peekc = var2;
                  return this.ttype = 47;
               } else {
                  while((var2 = this.read()) != 10 && var2 != 13 && var2 >= 0) {
                  }

                  this.peekc = var2;
                  return this.nextToken();
               }
            }
         }
      }
   }

   public void pushBack() {
      if (this.ttype != -4) {
         this.pushedBack = true;
      }

   }

   public int lineno() {
      return this.LINENO;
   }

   public String toString() {
      String var1;
      switch(this.ttype) {
      case -4:
         var1 = "NOTHING";
         break;
      case -3:
         var1 = this.sval;
         break;
      case -2:
         var1 = "n=" + this.nval;
         break;
      case -1:
         var1 = "EOF";
         break;
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      default:
         if (this.ttype < 256 && (this.ctype[this.ttype] & 8) != 0) {
            var1 = this.sval;
         } else {
            char[] var2 = new char[3];
            var2[0] = var2[2] = '\'';
            var2[1] = (char)this.ttype;
            var1 = new String(var2);
         }
         break;
      case 10:
         var1 = "EOL";
      }

      return "Token[" + var1 + "], line " + this.LINENO;
   }
}
