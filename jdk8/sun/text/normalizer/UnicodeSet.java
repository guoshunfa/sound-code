package sun.text.normalizer;

import java.text.ParsePosition;
import java.util.Iterator;
import java.util.TreeSet;

public class UnicodeSet implements UnicodeMatcher {
   private static final int LOW = 0;
   private static final int HIGH = 1114112;
   public static final int MIN_VALUE = 0;
   public static final int MAX_VALUE = 1114111;
   private int len;
   private int[] list;
   private int[] rangeList;
   private int[] buffer;
   TreeSet<String> strings;
   private String pat;
   private static final int START_EXTRA = 16;
   private static final int GROW_EXTRA = 16;
   private static UnicodeSet[] INCLUSIONS = null;
   static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
   public static final int IGNORE_SPACE = 1;

   public UnicodeSet() {
      this.strings = new TreeSet();
      this.pat = null;
      this.list = new int[17];
      this.list[this.len++] = 1114112;
   }

   public UnicodeSet(int var1, int var2) {
      this();
      this.complement(var1, var2);
   }

   public UnicodeSet(String var1) {
      this();
      this.applyPattern((String)var1, (ParsePosition)null, (SymbolTable)null, 1);
   }

   public UnicodeSet set(UnicodeSet var1) {
      this.list = (int[])var1.list.clone();
      this.len = var1.len;
      this.pat = var1.pat;
      this.strings = (TreeSet)var1.strings.clone();
      return this;
   }

   public final UnicodeSet applyPattern(String var1) {
      return this.applyPattern((String)var1, (ParsePosition)null, (SymbolTable)null, 1);
   }

   private static void _appendToPat(StringBuffer var0, String var1, boolean var2) {
      for(int var3 = 0; var3 < var1.length(); var3 += UTF16.getCharCount(var3)) {
         _appendToPat(var0, UTF16.charAt(var1, var3), var2);
      }

   }

   private static void _appendToPat(StringBuffer var0, int var1, boolean var2) {
      if (!var2 || !Utility.isUnprintable(var1) || !Utility.escapeUnprintable(var0, var1)) {
         switch(var1) {
         case 36:
         case 38:
         case 45:
         case 58:
         case 91:
         case 92:
         case 93:
         case 94:
         case 123:
         case 125:
            var0.append('\\');
            break;
         default:
            if (UCharacterProperty.isRuleWhiteSpace(var1)) {
               var0.append('\\');
            }
         }

         UTF16.append(var0, var1);
      }
   }

   private StringBuffer _toPattern(StringBuffer var1, boolean var2) {
      if (this.pat == null) {
         return this._generatePattern(var1, var2, true);
      } else {
         int var4 = 0;
         int var3 = 0;

         while(true) {
            while(var3 < this.pat.length()) {
               int var5 = UTF16.charAt(this.pat, var3);
               var3 += UTF16.getCharCount(var5);
               if (var2 && Utility.isUnprintable(var5)) {
                  if (var4 % 2 == 1) {
                     var1.setLength(var1.length() - 1);
                  }

                  Utility.escapeUnprintable(var1, var5);
                  var4 = 0;
               } else {
                  UTF16.append(var1, var5);
                  if (var5 == 92) {
                     ++var4;
                  } else {
                     var4 = 0;
                  }
               }
            }

            return var1;
         }
      }
   }

   public StringBuffer _generatePattern(StringBuffer var1, boolean var2, boolean var3) {
      var1.append('[');
      int var4 = this.getRangeCount();
      int var5;
      int var6;
      int var7;
      if (var4 > 1 && this.getRangeStart(0) == 0 && this.getRangeEnd(var4 - 1) == 1114111) {
         var1.append('^');

         for(var5 = 1; var5 < var4; ++var5) {
            var6 = this.getRangeEnd(var5 - 1) + 1;
            var7 = this.getRangeStart(var5) - 1;
            _appendToPat(var1, var6, var2);
            if (var6 != var7) {
               if (var6 + 1 != var7) {
                  var1.append('-');
               }

               _appendToPat(var1, var7, var2);
            }
         }
      } else {
         for(var5 = 0; var5 < var4; ++var5) {
            var6 = this.getRangeStart(var5);
            var7 = this.getRangeEnd(var5);
            _appendToPat(var1, var6, var2);
            if (var6 != var7) {
               if (var6 + 1 != var7) {
                  var1.append('-');
               }

               _appendToPat(var1, var7, var2);
            }
         }
      }

      if (var3 && this.strings.size() > 0) {
         Iterator var8 = this.strings.iterator();

         while(var8.hasNext()) {
            var1.append('{');
            _appendToPat(var1, (String)var8.next(), var2);
            var1.append('}');
         }
      }

      return var1.append(']');
   }

   private UnicodeSet add_unchecked(int var1, int var2) {
      if (var1 >= 0 && var1 <= 1114111) {
         if (var2 >= 0 && var2 <= 1114111) {
            if (var1 < var2) {
               this.add(this.range(var1, var2), 2, 0);
            } else if (var1 == var2) {
               this.add(var1);
            }

            return this;
         } else {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(var2, 6));
         }
      } else {
         throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(var1, 6));
      }
   }

   public final UnicodeSet add(int var1) {
      return this.add_unchecked(var1);
   }

   private final UnicodeSet add_unchecked(int var1) {
      if (var1 >= 0 && var1 <= 1114111) {
         int var2 = this.findCodePoint(var1);
         if ((var2 & 1) != 0) {
            return this;
         } else {
            if (var1 == this.list[var2] - 1) {
               this.list[var2] = var1;
               if (var1 == 1114111) {
                  this.ensureCapacity(this.len + 1);
                  this.list[this.len++] = 1114112;
               }

               if (var2 > 0 && var1 == this.list[var2 - 1]) {
                  System.arraycopy(this.list, var2 + 1, this.list, var2 - 1, this.len - var2 - 1);
                  this.len -= 2;
               }
            } else if (var2 > 0 && var1 == this.list[var2 - 1]) {
               int var10002 = this.list[var2 - 1]++;
            } else {
               if (this.len + 2 > this.list.length) {
                  int[] var3 = new int[this.len + 2 + 16];
                  if (var2 != 0) {
                     System.arraycopy(this.list, 0, var3, 0, var2);
                  }

                  System.arraycopy(this.list, var2, var3, var2 + 2, this.len - var2);
                  this.list = var3;
               } else {
                  System.arraycopy(this.list, var2, this.list, var2 + 2, this.len - var2);
               }

               this.list[var2] = var1;
               this.list[var2 + 1] = var1 + 1;
               this.len += 2;
            }

            this.pat = null;
            return this;
         }
      } else {
         throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(var1, 6));
      }
   }

   public final UnicodeSet add(String var1) {
      int var2 = getSingleCP(var1);
      if (var2 < 0) {
         this.strings.add(var1);
         this.pat = null;
      } else {
         this.add_unchecked(var2, var2);
      }

      return this;
   }

   private static int getSingleCP(String var0) {
      if (var0.length() < 1) {
         throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
      } else if (var0.length() > 2) {
         return -1;
      } else if (var0.length() == 1) {
         return var0.charAt(0);
      } else {
         int var1 = UTF16.charAt(var0, 0);
         return var1 > 65535 ? var1 : -1;
      }
   }

   public UnicodeSet complement(int var1, int var2) {
      if (var1 >= 0 && var1 <= 1114111) {
         if (var2 >= 0 && var2 <= 1114111) {
            if (var1 <= var2) {
               this.xor(this.range(var1, var2), 2, 0);
            }

            this.pat = null;
            return this;
         } else {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(var2, 6));
         }
      } else {
         throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(var1, 6));
      }
   }

   public UnicodeSet complement() {
      if (this.list[0] == 0) {
         System.arraycopy(this.list, 1, this.list, 0, this.len - 1);
         --this.len;
      } else {
         this.ensureCapacity(this.len + 1);
         System.arraycopy(this.list, 0, this.list, 1, this.len);
         this.list[0] = 0;
         ++this.len;
      }

      this.pat = null;
      return this;
   }

   public boolean contains(int var1) {
      if (var1 >= 0 && var1 <= 1114111) {
         int var2 = this.findCodePoint(var1);
         return (var2 & 1) != 0;
      } else {
         throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(var1, 6));
      }
   }

   private final int findCodePoint(int var1) {
      if (var1 < this.list[0]) {
         return 0;
      } else if (this.len >= 2 && var1 >= this.list[this.len - 2]) {
         return this.len - 1;
      } else {
         int var2 = 0;
         int var3 = this.len - 1;

         while(true) {
            int var4 = var2 + var3 >>> 1;
            if (var4 == var2) {
               return var3;
            }

            if (var1 < this.list[var4]) {
               var3 = var4;
            } else {
               var2 = var4;
            }
         }
      }
   }

   public UnicodeSet addAll(UnicodeSet var1) {
      this.add(var1.list, var1.len, 0);
      this.strings.addAll(var1.strings);
      return this;
   }

   public UnicodeSet retainAll(UnicodeSet var1) {
      this.retain(var1.list, var1.len, 0);
      this.strings.retainAll(var1.strings);
      return this;
   }

   public UnicodeSet removeAll(UnicodeSet var1) {
      this.retain(var1.list, var1.len, 2);
      this.strings.removeAll(var1.strings);
      return this;
   }

   public UnicodeSet clear() {
      this.list[0] = 1114112;
      this.len = 1;
      this.pat = null;
      this.strings.clear();
      return this;
   }

   public int getRangeCount() {
      return this.len / 2;
   }

   public int getRangeStart(int var1) {
      return this.list[var1 * 2];
   }

   public int getRangeEnd(int var1) {
      return this.list[var1 * 2 + 1] - 1;
   }

   UnicodeSet applyPattern(String var1, ParsePosition var2, SymbolTable var3, int var4) {
      boolean var5 = var2 == null;
      if (var5) {
         var2 = new ParsePosition(0);
      }

      StringBuffer var6 = new StringBuffer();
      RuleCharacterIterator var7 = new RuleCharacterIterator(var1, var3, var2);
      this.applyPattern(var7, var3, var6, var4);
      if (var7.inVariable()) {
         syntaxError(var7, "Extra chars in variable value");
      }

      this.pat = var6.toString();
      if (var5) {
         int var8 = var2.getIndex();
         if ((var4 & 1) != 0) {
            var8 = Utility.skipWhitespace(var1, var8);
         }

         if (var8 != var1.length()) {
            throw new IllegalArgumentException("Parse of \"" + var1 + "\" failed at " + var8);
         }
      }

      return this;
   }

   void applyPattern(RuleCharacterIterator var1, SymbolTable var2, StringBuffer var3, int var4) {
      int var5 = 3;
      if ((var4 & 1) != 0) {
         var5 |= 4;
      }

      StringBuffer var6 = new StringBuffer();
      StringBuffer var7 = null;
      boolean var8 = false;
      UnicodeSet var9 = null;
      Object var10 = null;
      byte var11 = 0;
      int var12 = 0;
      byte var13 = 0;
      char var14 = 0;
      boolean var15 = false;
      this.clear();

      while(var13 != 2 && !var1.atEnd()) {
         int var16 = 0;
         boolean var17 = false;
         UnicodeSet var18 = null;
         byte var19 = 0;
         if (resemblesPropertyPattern(var1, var5)) {
            var19 = 2;
         } else {
            var10 = var1.getPos(var10);
            var16 = var1.next(var5);
            var17 = var1.isEscaped();
            if (var16 == 91 && !var17) {
               if (var13 == 1) {
                  var1.setPos(var10);
                  var19 = 1;
               } else {
                  var13 = 1;
                  var6.append('[');
                  var10 = var1.getPos(var10);
                  var16 = var1.next(var5);
                  var17 = var1.isEscaped();
                  if (var16 == 94 && !var17) {
                     var15 = true;
                     var6.append('^');
                     var10 = var1.getPos(var10);
                     var16 = var1.next(var5);
                     var17 = var1.isEscaped();
                  }

                  if (var16 != 45) {
                     var1.setPos(var10);
                     continue;
                  }

                  var17 = true;
               }
            } else if (var2 != null) {
               UnicodeMatcher var20 = var2.lookupMatcher(var16);
               if (var20 != null) {
                  try {
                     var18 = (UnicodeSet)var20;
                     var19 = 3;
                  } catch (ClassCastException var22) {
                     syntaxError(var1, "Syntax error");
                  }
               }
            }
         }

         if (var19 != 0) {
            if (var11 == 1) {
               if (var14 != 0) {
                  syntaxError(var1, "Char expected after operator");
               }

               this.add_unchecked(var12, var12);
               _appendToPat(var6, var12, false);
               var14 = 0;
               boolean var23 = false;
            }

            if (var14 == '-' || var14 == '&') {
               var6.append(var14);
            }

            if (var18 == null) {
               if (var9 == null) {
                  var9 = new UnicodeSet();
               }

               var18 = var9;
            }

            switch(var19) {
            case 1:
               var18.applyPattern(var1, var2, var6, var4);
               break;
            case 2:
               var1.skipIgnored(var5);
               var18.applyPropertyPattern(var1, var6, var2);
               break;
            case 3:
               var18._toPattern(var6, false);
            }

            var8 = true;
            if (var13 == 0) {
               this.set(var18);
               var13 = 2;
               break;
            }

            switch(var14) {
            case '\u0000':
               this.addAll(var18);
               break;
            case '&':
               this.retainAll(var18);
               break;
            case '-':
               this.removeAll(var18);
            }

            var14 = 0;
            var11 = 2;
         } else {
            if (var13 == 0) {
               syntaxError(var1, "Missing '['");
            }

            if (!var17) {
               switch(var16) {
               case 36:
                  var10 = var1.getPos(var10);
                  var16 = var1.next(var5);
                  var17 = var1.isEscaped();
                  boolean var21 = var16 == 93 && !var17;
                  if (var2 == null && !var21) {
                     var16 = 36;
                     var1.setPos(var10);
                  } else {
                     if (var21 && var14 == 0) {
                        if (var11 == 1) {
                           this.add_unchecked(var12, var12);
                           _appendToPat(var6, var12, false);
                        }

                        this.add_unchecked(65535);
                        var8 = true;
                        var6.append('$').append(']');
                        var13 = 2;
                        continue;
                     }

                     syntaxError(var1, "Unquoted '$'");
                  }
                  break;
               case 38:
                  if (var11 == 2 && var14 == 0) {
                     var14 = (char)var16;
                     continue;
                  }

                  syntaxError(var1, "'&' not after set");
                  break;
               case 45:
                  if (var14 == 0) {
                     if (var11 != 0) {
                        var14 = (char)var16;
                        continue;
                     }

                     this.add_unchecked(var16, var16);
                     var16 = var1.next(var5);
                     var17 = var1.isEscaped();
                     if (var16 == 93 && !var17) {
                        var6.append("-]");
                        var13 = 2;
                        continue;
                     }
                  }

                  syntaxError(var1, "'-' not after char or set");
                  break;
               case 93:
                  if (var11 == 1) {
                     this.add_unchecked(var12, var12);
                     _appendToPat(var6, var12, false);
                  }

                  if (var14 == '-') {
                     this.add_unchecked(var14, var14);
                     var6.append(var14);
                  } else if (var14 == '&') {
                     syntaxError(var1, "Trailing '&'");
                  }

                  var6.append(']');
                  var13 = 2;
                  continue;
               case 94:
                  syntaxError(var1, "'^' not after '['");
                  break;
               case 123:
                  if (var14 != 0) {
                     syntaxError(var1, "Missing operand after operator");
                  }

                  if (var11 == 1) {
                     this.add_unchecked(var12, var12);
                     _appendToPat(var6, var12, false);
                  }

                  var11 = 0;
                  if (var7 == null) {
                     var7 = new StringBuffer();
                  } else {
                     var7.setLength(0);
                  }

                  boolean var24 = false;

                  while(!var1.atEnd()) {
                     var16 = var1.next(var5);
                     var17 = var1.isEscaped();
                     if (var16 == 125 && !var17) {
                        var24 = true;
                        break;
                     }

                     UTF16.append(var7, var16);
                  }

                  if (var7.length() < 1 || !var24) {
                     syntaxError(var1, "Invalid multicharacter string");
                  }

                  this.add(var7.toString());
                  var6.append('{');
                  _appendToPat(var6, var7.toString(), false);
                  var6.append('}');
                  continue;
               }
            }

            switch(var11) {
            case 0:
               var11 = 1;
               var12 = var16;
               break;
            case 1:
               if (var14 == '-') {
                  if (var12 >= var16) {
                     syntaxError(var1, "Invalid range");
                  }

                  this.add_unchecked(var12, var16);
                  _appendToPat(var6, var12, false);
                  var6.append(var14);
                  _appendToPat(var6, var16, false);
                  var14 = 0;
                  var11 = 0;
               } else {
                  this.add_unchecked(var12, var12);
                  _appendToPat(var6, var12, false);
                  var12 = var16;
               }
               break;
            case 2:
               if (var14 != 0) {
                  syntaxError(var1, "Set expected after operator");
               }

               var12 = var16;
               var11 = 1;
            }
         }
      }

      if (var13 != 2) {
         syntaxError(var1, "Missing ']'");
      }

      var1.skipIgnored(var5);
      if (var15) {
         this.complement();
      }

      if (var8) {
         var3.append(var6.toString());
      } else {
         this._generatePattern(var3, false, true);
      }

   }

   private static void syntaxError(RuleCharacterIterator var0, String var1) {
      throw new IllegalArgumentException("Error: " + var1 + " at \"" + Utility.escape(var0.toString()) + '"');
   }

   private void ensureCapacity(int var1) {
      if (var1 > this.list.length) {
         int[] var2 = new int[var1 + 16];
         System.arraycopy(this.list, 0, var2, 0, this.len);
         this.list = var2;
      }
   }

   private void ensureBufferCapacity(int var1) {
      if (this.buffer == null || var1 > this.buffer.length) {
         this.buffer = new int[var1 + 16];
      }
   }

   private int[] range(int var1, int var2) {
      if (this.rangeList == null) {
         this.rangeList = new int[]{var1, var2 + 1, 1114112};
      } else {
         this.rangeList[0] = var1;
         this.rangeList[1] = var2 + 1;
      }

      return this.rangeList;
   }

   private UnicodeSet xor(int[] var1, int var2, int var3) {
      this.ensureBufferCapacity(this.len + var2);
      byte var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var10 = var4 + 1;
      int var7 = this.list[var4];
      int var8;
      if (var3 != 1 && var3 != 2) {
         var8 = var1[var5++];
      } else {
         var8 = 0;
         if (var1[var5] == 0) {
            ++var5;
            var8 = var1[var5];
         }
      }

      while(true) {
         while(var7 >= var8) {
            if (var8 < var7) {
               this.buffer[var6++] = var8;
               var8 = var1[var5++];
            } else {
               if (var7 == 1114112) {
                  this.buffer[var6++] = 1114112;
                  this.len = var6;
                  int[] var9 = this.list;
                  this.list = this.buffer;
                  this.buffer = var9;
                  this.pat = null;
                  return this;
               }

               var7 = this.list[var10++];
               var8 = var1[var5++];
            }
         }

         this.buffer[var6++] = var7;
         var7 = this.list[var10++];
      }
   }

   private UnicodeSet add(int[] var1, int var2, int var3) {
      this.ensureBufferCapacity(this.len + var2);
      byte var4 = 0;
      byte var5 = 0;
      int var6 = 0;
      int var10 = var4 + 1;
      int var7 = this.list[var4];
      int var11 = var5 + 1;
      int var8 = var1[var5];

      label95:
      while(true) {
         switch(var3) {
         case 0:
            int var10000;
            if (var7 < var8) {
               if (var6 > 0 && var7 <= this.buffer[var6 - 1]) {
                  var10000 = this.list[var10];
                  --var6;
                  var7 = max(var10000, this.buffer[var6]);
               } else {
                  this.buffer[var6++] = var7;
                  var7 = this.list[var10];
               }

               ++var10;
               var3 ^= 1;
            } else if (var8 < var7) {
               if (var6 > 0 && var8 <= this.buffer[var6 - 1]) {
                  var10000 = var1[var11];
                  --var6;
                  var8 = max(var10000, this.buffer[var6]);
               } else {
                  this.buffer[var6++] = var8;
                  var8 = var1[var11];
               }

               ++var11;
               var3 ^= 2;
            } else {
               if (var7 == 1114112) {
                  break label95;
               }

               if (var6 > 0 && var7 <= this.buffer[var6 - 1]) {
                  var10000 = this.list[var10];
                  --var6;
                  var7 = max(var10000, this.buffer[var6]);
               } else {
                  this.buffer[var6++] = var7;
                  var7 = this.list[var10];
               }

               ++var10;
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
            break;
         case 1:
            if (var7 < var8) {
               this.buffer[var6++] = var7;
               var7 = this.list[var10++];
               var3 ^= 1;
            } else {
               if (var8 < var7) {
                  var8 = var1[var11++];
                  var3 ^= 2;
                  continue;
               }

               if (var7 == 1114112) {
                  break label95;
               }

               var7 = this.list[var10++];
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
            break;
         case 2:
            if (var8 < var7) {
               this.buffer[var6++] = var8;
               var8 = var1[var11++];
               var3 ^= 2;
            } else {
               if (var7 < var8) {
                  var7 = this.list[var10++];
                  var3 ^= 1;
                  continue;
               }

               if (var7 == 1114112) {
                  break label95;
               }

               var7 = this.list[var10++];
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
            break;
         case 3:
            if (var8 <= var7) {
               if (var7 == 1114112) {
                  break label95;
               }

               this.buffer[var6++] = var7;
            } else {
               if (var8 == 1114112) {
                  break label95;
               }

               this.buffer[var6++] = var8;
            }

            var7 = this.list[var10++];
            var3 ^= 1;
            var8 = var1[var11++];
            var3 ^= 2;
         }
      }

      this.buffer[var6++] = 1114112;
      this.len = var6;
      int[] var9 = this.list;
      this.list = this.buffer;
      this.buffer = var9;
      this.pat = null;
      return this;
   }

   private UnicodeSet retain(int[] var1, int var2, int var3) {
      this.ensureBufferCapacity(this.len + var2);
      byte var4 = 0;
      byte var5 = 0;
      int var6 = 0;
      int var10 = var4 + 1;
      int var7 = this.list[var4];
      int var11 = var5 + 1;
      int var8 = var1[var5];

      label64:
      while(true) {
         switch(var3) {
         case 0:
            if (var7 < var8) {
               var7 = this.list[var10++];
               var3 ^= 1;
            } else {
               if (var8 < var7) {
                  var8 = var1[var11++];
                  var3 ^= 2;
                  continue;
               }

               if (var7 == 1114112) {
                  break label64;
               }

               this.buffer[var6++] = var7;
               var7 = this.list[var10++];
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
            break;
         case 1:
            if (var7 < var8) {
               var7 = this.list[var10++];
               var3 ^= 1;
            } else {
               if (var8 < var7) {
                  this.buffer[var6++] = var8;
                  var8 = var1[var11++];
                  var3 ^= 2;
                  continue;
               }

               if (var7 == 1114112) {
                  break label64;
               }

               var7 = this.list[var10++];
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
            break;
         case 2:
            if (var8 < var7) {
               var8 = var1[var11++];
               var3 ^= 2;
            } else {
               if (var7 < var8) {
                  this.buffer[var6++] = var7;
                  var7 = this.list[var10++];
                  var3 ^= 1;
                  continue;
               }

               if (var7 == 1114112) {
                  break label64;
               }

               var7 = this.list[var10++];
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
            break;
         case 3:
            if (var7 < var8) {
               this.buffer[var6++] = var7;
               var7 = this.list[var10++];
               var3 ^= 1;
            } else if (var8 < var7) {
               this.buffer[var6++] = var8;
               var8 = var1[var11++];
               var3 ^= 2;
            } else {
               if (var7 == 1114112) {
                  break label64;
               }

               this.buffer[var6++] = var7;
               var7 = this.list[var10++];
               var3 ^= 1;
               var8 = var1[var11++];
               var3 ^= 2;
            }
         }
      }

      this.buffer[var6++] = 1114112;
      this.len = var6;
      int[] var9 = this.list;
      this.list = this.buffer;
      this.buffer = var9;
      this.pat = null;
      return this;
   }

   private static final int max(int var0, int var1) {
      return var0 > var1 ? var0 : var1;
   }

   private static synchronized UnicodeSet getInclusions(int var0) {
      if (INCLUSIONS == null) {
         INCLUSIONS = new UnicodeSet[9];
      }

      if (INCLUSIONS[var0] == null) {
         UnicodeSet var1 = new UnicodeSet();
         switch(var0) {
         case 2:
            UCharacterProperty.getInstance().upropsvec_addPropertyStarts(var1);
            INCLUSIONS[var0] = var1;
            break;
         default:
            throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + var0 + ")");
         }
      }

      return INCLUSIONS[var0];
   }

   private UnicodeSet applyFilter(UnicodeSet.Filter var1, int var2) {
      this.clear();
      int var3 = -1;
      UnicodeSet var4 = getInclusions(var2);
      int var5 = var4.getRangeCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4.getRangeStart(var6);
         int var8 = var4.getRangeEnd(var6);

         for(int var9 = var7; var9 <= var8; ++var9) {
            if (var1.contains(var9)) {
               if (var3 < 0) {
                  var3 = var9;
               }
            } else if (var3 >= 0) {
               this.add_unchecked(var3, var9 - 1);
               var3 = -1;
            }
         }
      }

      if (var3 >= 0) {
         this.add_unchecked(var3, 1114111);
      }

      return this;
   }

   private static String mungeCharName(String var0) {
      StringBuffer var1 = new StringBuffer();
      int var2 = 0;

      while(true) {
         int var3;
         while(true) {
            if (var2 >= var0.length()) {
               if (var1.length() != 0 && var1.charAt(var1.length() - 1) == ' ') {
                  var1.setLength(var1.length() - 1);
               }

               return var1.toString();
            }

            var3 = UTF16.charAt(var0, var2);
            var2 += UTF16.getCharCount(var3);
            if (!UCharacterProperty.isRuleWhiteSpace(var3)) {
               break;
            }

            if (var1.length() != 0 && var1.charAt(var1.length() - 1) != ' ') {
               var3 = 32;
               break;
            }
         }

         UTF16.append(var1, var3);
      }
   }

   public UnicodeSet applyPropertyAlias(String var1, String var2, SymbolTable var3) {
      if (var2.length() > 0 && var1.equals("Age")) {
         VersionInfo var4 = VersionInfo.getInstance(mungeCharName(var2));
         this.applyFilter(new UnicodeSet.VersionFilter(var4), 2);
         return this;
      } else {
         throw new IllegalArgumentException("Unsupported property: " + var1);
      }
   }

   private static boolean resemblesPropertyPattern(RuleCharacterIterator var0, int var1) {
      boolean var2 = false;
      var1 &= -3;
      Object var3 = var0.getPos((Object)null);
      int var4 = var0.next(var1);
      if (var4 == 91 || var4 == 92) {
         int var5 = var0.next(var1 & -5);
         var2 = var4 == 91 ? var5 == 58 : var5 == 78 || var5 == 112 || var5 == 80;
      }

      var0.setPos(var3);
      return var2;
   }

   private UnicodeSet applyPropertyPattern(String var1, ParsePosition var2, SymbolTable var3) {
      int var4 = var2.getIndex();
      if (var4 + 5 > var1.length()) {
         return null;
      } else {
         boolean var5 = false;
         boolean var6 = false;
         boolean var7 = false;
         if (var1.regionMatches(var4, "[:", 0, 2)) {
            var5 = true;
            var4 = Utility.skipWhitespace(var1, var4 + 2);
            if (var4 < var1.length() && var1.charAt(var4) == '^') {
               ++var4;
               var7 = true;
            }
         } else {
            if (!var1.regionMatches(true, var4, "\\p", 0, 2) && !var1.regionMatches(var4, "\\N", 0, 2)) {
               return null;
            }

            char var8 = var1.charAt(var4 + 1);
            var7 = var8 == 'P';
            var6 = var8 == 'N';
            var4 = Utility.skipWhitespace(var1, var4 + 2);
            if (var4 == var1.length() || var1.charAt(var4++) != '{') {
               return null;
            }
         }

         int var12 = var1.indexOf(var5 ? ":]" : "}", var4);
         if (var12 < 0) {
            return null;
         } else {
            int var9 = var1.indexOf(61, var4);
            String var10;
            String var11;
            if (var9 >= 0 && var9 < var12 && !var6) {
               var10 = var1.substring(var4, var9);
               var11 = var1.substring(var9 + 1, var12);
            } else {
               var10 = var1.substring(var4, var12);
               var11 = "";
               if (var6) {
                  var11 = var10;
                  var10 = "na";
               }
            }

            this.applyPropertyAlias(var10, var11, var3);
            if (var7) {
               this.complement();
            }

            var2.setIndex(var12 + (var5 ? 2 : 1));
            return this;
         }
      }
   }

   private void applyPropertyPattern(RuleCharacterIterator var1, StringBuffer var2, SymbolTable var3) {
      String var4 = var1.lookahead();
      ParsePosition var5 = new ParsePosition(0);
      this.applyPropertyPattern(var4, var5, var3);
      if (var5.getIndex() == 0) {
         syntaxError(var1, "Invalid property pattern");
      }

      var1.jumpahead(var5.getIndex());
      var2.append(var4.substring(0, var5.getIndex()));
   }

   private static class VersionFilter implements UnicodeSet.Filter {
      VersionInfo version;

      VersionFilter(VersionInfo var1) {
         this.version = var1;
      }

      public boolean contains(int var1) {
         VersionInfo var2 = UCharacter.getAge(var1);
         return var2 != UnicodeSet.NO_VERSION && var2.compareTo(this.version) <= 0;
      }
   }

   private interface Filter {
      boolean contains(int var1);
   }
}
