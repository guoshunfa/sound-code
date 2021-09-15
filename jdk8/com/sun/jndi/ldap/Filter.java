package com.sun.jndi.ldap;

import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;

final class Filter {
   private static final boolean dbg = false;
   private static int dbgIndent = 0;
   static final int LDAP_FILTER_AND = 160;
   static final int LDAP_FILTER_OR = 161;
   static final int LDAP_FILTER_NOT = 162;
   static final int LDAP_FILTER_EQUALITY = 163;
   static final int LDAP_FILTER_SUBSTRINGS = 164;
   static final int LDAP_FILTER_GE = 165;
   static final int LDAP_FILTER_LE = 166;
   static final int LDAP_FILTER_PRESENT = 135;
   static final int LDAP_FILTER_APPROX = 168;
   static final int LDAP_FILTER_EXT = 169;
   static final int LDAP_FILTER_EXT_RULE = 129;
   static final int LDAP_FILTER_EXT_TYPE = 130;
   static final int LDAP_FILTER_EXT_VAL = 131;
   static final int LDAP_FILTER_EXT_DN = 132;
   static final int LDAP_SUBSTRING_INITIAL = 128;
   static final int LDAP_SUBSTRING_ANY = 129;
   static final int LDAP_SUBSTRING_FINAL = 130;

   static void encodeFilterString(BerEncoder var0, String var1, boolean var2) throws IOException, NamingException {
      if (var1 != null && !var1.equals("")) {
         byte[] var3;
         if (var2) {
            var3 = var1.getBytes("UTF8");
         } else {
            var3 = var1.getBytes("8859_1");
         }

         int var4 = var3.length;
         encodeFilter(var0, var3, 0, var4);
      } else {
         throw new InvalidSearchFilterException("Empty filter");
      }
   }

   private static void encodeFilter(BerEncoder var0, byte[] var1, int var2, int var3) throws IOException, NamingException {
      if (var3 - var2 <= 0) {
         throw new InvalidSearchFilterException("Empty filter");
      } else {
         int var5 = 0;
         int[] var8 = new int[]{var2};

         do {
            if (var8[0] >= var3) {
               if (var5 != 0) {
                  throw new InvalidSearchFilterException("Unbalanced parenthesis");
               }

               return;
            }

            int var10002;
            switch(var1[var8[0]]) {
            case 32:
               var10002 = var8[0]++;
               break;
            case 40:
               var10002 = var8[0]++;
               ++var5;
               switch(var1[var8[0]]) {
               case 33:
                  encodeComplexFilter(var0, var1, 162, var8, var3);
                  --var5;
                  continue;
               case 38:
                  encodeComplexFilter(var0, var1, 160, var8, var3);
                  --var5;
                  continue;
               case 124:
                  encodeComplexFilter(var0, var1, 161, var8, var3);
                  --var5;
                  continue;
               default:
                  int var6 = 1;
                  boolean var7 = false;
                  int var4 = var8[0];

                  while(var4 < var3 && var6 > 0) {
                     if (!var7) {
                        if (var1[var4] == 40) {
                           ++var6;
                        } else if (var1[var4] == 41) {
                           --var6;
                        }
                     }

                     if (var1[var4] == 92 && !var7) {
                        var7 = true;
                     } else {
                        var7 = false;
                     }

                     if (var6 > 0) {
                        ++var4;
                     }
                  }

                  if (var6 != 0) {
                     throw new InvalidSearchFilterException("Unbalanced parenthesis");
                  }

                  encodeSimpleFilter(var0, var1, var8[0], var4);
                  var8[0] = var4 + 1;
                  --var5;
                  continue;
               }
            case 41:
               var0.endSeq();
               var10002 = var8[0]++;
               --var5;
               break;
            default:
               encodeSimpleFilter(var0, var1, var8[0], var3);
               var8[0] = var3;
            }
         } while(var5 >= 0);

         throw new InvalidSearchFilterException("Unbalanced parenthesis");
      }
   }

   private static int hexchar2int(byte var0) {
      if (var0 >= 48 && var0 <= 57) {
         return var0 - 48;
      } else if (var0 >= 65 && var0 <= 70) {
         return var0 - 65 + 10;
      } else {
         return var0 >= 97 && var0 <= 102 ? var0 - 97 + 10 : -1;
      }
   }

   static byte[] unescapeFilterValue(byte[] var0, int var1, int var2) throws NamingException {
      boolean var3 = false;
      boolean var4 = false;
      int var7 = var2 - var1;
      byte[] var8 = new byte[var7];
      int var9 = 0;

      for(int var10 = var1; var10 < var2; ++var10) {
         byte var6 = var0[var10];
         if (var3) {
            int var5;
            if ((var5 = hexchar2int(var6)) < 0) {
               if (!var4) {
                  throw new InvalidSearchFilterException("invalid escape sequence: " + var0);
               }

               var3 = false;
               var8[var9++] = var6;
            } else if (var4) {
               var8[var9] = (byte)(var5 << 4);
               var4 = false;
            } else {
               int var10001 = var9++;
               var8[var10001] |= (byte)var5;
               var3 = false;
            }
         } else if (var6 != 92) {
            var8[var9++] = var6;
            var3 = false;
         } else {
            var3 = true;
            var4 = true;
         }
      }

      byte[] var11 = new byte[var9];
      System.arraycopy(var8, 0, var11, 0, var9);
      return var11;
   }

   private static int indexOf(byte[] var0, char var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3; ++var4) {
         if (var0[var4] == var1) {
            return var4;
         }
      }

      return -1;
   }

   private static int indexOf(byte[] var0, String var1, int var2, int var3) {
      int var4 = indexOf(var0, var1.charAt(0), var2, var3);
      if (var4 >= 0) {
         for(int var5 = 1; var5 < var1.length(); ++var5) {
            if (var0[var4 + var5] != var1.charAt(var5)) {
               return -1;
            }
         }
      }

      return var4;
   }

   private static int findUnescaped(byte[] var0, char var1, int var2, int var3) {
      while(var2 < var3) {
         int var4 = indexOf(var0, var1, var2, var3);
         int var6 = 0;

         for(int var5 = var4 - 1; var5 >= var2 && var0[var5] == 92; ++var6) {
            --var5;
         }

         if (var4 == var2 || var4 == -1 || var6 % 2 == 0) {
            return var4;
         }

         var2 = var4 + 1;
      }

      return -1;
   }

   private static void encodeSimpleFilter(BerEncoder var0, byte[] var1, int var2, int var3) throws IOException, NamingException {
      int var10;
      if ((var10 = indexOf(var1, '=', var2, var3)) == -1) {
         throw new InvalidSearchFilterException("Missing 'equals'");
      } else {
         int var6 = var10 + 1;
         int var9;
         short var11;
         switch(var1[var10 - 1]) {
         case 58:
            var11 = 169;
            var9 = var10 - 1;
            break;
         case 60:
            var11 = 166;
            var9 = var10 - 1;
            break;
         case 62:
            var11 = 165;
            var9 = var10 - 1;
            break;
         case 126:
            var11 = 168;
            var9 = var10 - 1;
            break;
         default:
            var9 = var10;
            var11 = 0;
         }

         int var12 = -1;
         int var13 = -1;
         boolean var14;
         int var15;
         if (var1[var2] >= 48 && var1[var2] <= 57 || var1[var2] >= 65 && var1[var2] <= 90 || var1[var2] >= 97 && var1[var2] <= 122) {
            var14 = var1[var2] >= 48 && var1[var2] <= 57;

            for(var15 = var2 + 1; var15 < var9; ++var15) {
               if (var1[var15] == 59) {
                  if (var14 && var1[var15 - 1] == 46) {
                     throw new InvalidSearchFilterException("invalid attribute description");
                  }

                  var12 = var15;
                  break;
               }

               if (var1[var15] == 58 && var11 == 169) {
                  if (var14 && var1[var15 - 1] == 46) {
                     throw new InvalidSearchFilterException("invalid attribute description");
                  }

                  var13 = var15;
                  break;
               }

               if (var14) {
                  if (var1[var15] == 46 && var1[var15 - 1] == 46 || var1[var15] != 46 && (var1[var15] < 48 || var1[var15] > 57)) {
                     throw new InvalidSearchFilterException("invalid attribute description");
                  }
               } else if (var1[var15] != 45 && var1[var15] != 95 && (var1[var15] < 48 || var1[var15] > 57) && (var1[var15] < 65 || var1[var15] > 90) && (var1[var15] < 97 || var1[var15] > 122)) {
                  throw new InvalidSearchFilterException("invalid attribute description");
               }
            }
         } else {
            if (var11 != 169 || var1[var2] != 58) {
               throw new InvalidSearchFilterException("invalid attribute description");
            }

            var13 = var2;
         }

         if (var12 > 0) {
            for(int var18 = var12 + 1; var18 < var9; ++var18) {
               if (var1[var18] == 59) {
                  if (var1[var18 - 1] == 59) {
                     throw new InvalidSearchFilterException("invalid attribute description");
                  }
               } else {
                  if (var1[var18] == 58 && var11 == 169) {
                     if (var1[var18 - 1] == 59) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                     }

                     var13 = var18;
                     break;
                  }

                  if (var1[var18] != 45 && var1[var18] != 95 && (var1[var18] < 48 || var1[var18] > 57) && (var1[var18] < 65 || var1[var18] > 90) && (var1[var18] < 97 || var1[var18] > 122)) {
                     throw new InvalidSearchFilterException("invalid attribute description");
                  }
               }
            }
         }

         if (var13 > 0) {
            var14 = false;

            for(var15 = var13 + 1; var15 < var9; ++var15) {
               if (var1[var15] == 58) {
                  throw new InvalidSearchFilterException("invalid attribute description");
               }

               if ((var1[var15] < 48 || var1[var15] > 57) && (var1[var15] < 65 || var1[var15] > 90) && (var1[var15] < 97 || var1[var15] > 122)) {
                  throw new InvalidSearchFilterException("invalid attribute description");
               }

               boolean var16 = var1[var15] >= 48 && var1[var15] <= 57;
               ++var15;

               for(int var17 = var15; var17 < var9; ++var15) {
                  if (var1[var17] == 58) {
                     if (var14) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                     }

                     if (var16 && var1[var17 - 1] == 46) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                     }

                     var14 = true;
                     break;
                  }

                  if (var16) {
                     if (var1[var17] == 46 && var1[var17 - 1] == 46 || var1[var17] != 46 && (var1[var17] < 48 || var1[var17] > 57)) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                     }
                  } else if (var1[var17] != 45 && var1[var17] != 95 && (var1[var17] < 48 || var1[var17] > 57) && (var1[var17] < 65 || var1[var17] > 90) && (var1[var17] < 97 || var1[var17] > 122)) {
                     throw new InvalidSearchFilterException("invalid attribute description");
                  }

                  ++var17;
               }
            }
         }

         if (var1[var9 - 1] != 46 && var1[var9 - 1] != 59 && var1[var9 - 1] != 58) {
            if (var9 == var10) {
               if (findUnescaped(var1, '*', var6, var3) == -1) {
                  var11 = 163;
               } else {
                  if (var1[var6] != 42 || var6 != var3 - 1) {
                     encodeSubstringFilter(var0, var1, var2, var9, var6, var3);
                     return;
                  }

                  var11 = 135;
               }
            }

            if (var11 == 135) {
               var0.encodeOctetString(var1, var11, var2, var9 - var2);
            } else if (var11 == 169) {
               encodeExtensibleMatch(var0, var1, var2, var9, var6, var3);
            } else {
               var0.beginSeq(var11);
               var0.encodeOctetString(var1, 4, var2, var9 - var2);
               var0.encodeOctetString(unescapeFilterValue(var1, var6, var3), 4);
               var0.endSeq();
            }

         } else {
            throw new InvalidSearchFilterException("invalid attribute description");
         }
      }
   }

   private static void encodeSubstringFilter(BerEncoder var0, byte[] var1, int var2, int var3, int var4, int var5) throws IOException, NamingException {
      var0.beginSeq(164);
      var0.encodeOctetString(var1, 4, var2, var3 - var2);
      var0.beginSeq(48);

      int var6;
      int var7;
      for(var7 = var4; (var6 = findUnescaped(var1, '*', var7, var5)) != -1; var7 = var6 + 1) {
         if (var7 == var4) {
            if (var7 < var6) {
               var0.encodeOctetString(unescapeFilterValue(var1, var7, var6), 128);
            }
         } else if (var7 < var6) {
            var0.encodeOctetString(unescapeFilterValue(var1, var7, var6), 129);
         }
      }

      if (var7 < var5) {
         var0.encodeOctetString(unescapeFilterValue(var1, var7, var5), 130);
      }

      var0.endSeq();
      var0.endSeq();
   }

   private static void encodeComplexFilter(BerEncoder var0, byte[] var1, int var2, int[] var3, int var4) throws IOException, NamingException {
      int var10002 = var3[0]++;
      var0.beginSeq(var2);
      int[] var5 = findRightParen(var1, var3, var4);
      encodeFilterList(var0, var1, var2, var5[0], var5[1]);
      var0.endSeq();
   }

   private static int[] findRightParen(byte[] var0, int[] var1, int var2) throws IOException, NamingException {
      int var3 = 1;
      boolean var4 = false;
      int var5 = var1[0];

      while(var5 < var2 && var3 > 0) {
         if (!var4) {
            if (var0[var5] == 40) {
               ++var3;
            } else if (var0[var5] == 41) {
               --var3;
            }
         }

         if (var0[var5] == 92 && !var4) {
            var4 = true;
         } else {
            var4 = false;
         }

         if (var3 > 0) {
            ++var5;
         }
      }

      if (var3 != 0) {
         throw new InvalidSearchFilterException("Unbalanced parenthesis");
      } else {
         int[] var6 = new int[]{var1[0], var5};
         var1[0] = var5 + 1;
         return var6;
      }
   }

   private static void encodeFilterList(BerEncoder var0, byte[] var1, int var2, int var3, int var4) throws IOException, NamingException {
      int[] var5 = new int[1];
      int var6 = 0;

      int var10002;
      for(var5[0] = var3; var5[0] < var4; var10002 = var5[0]++) {
         if (!Character.isSpaceChar((char)var1[var5[0]])) {
            if (var2 == 162 && var6 > 0) {
               throw new InvalidSearchFilterException("Filter (!) cannot be followed by more than one filters");
            }

            if (var1[var5[0]] != 40) {
               int[] var7 = findRightParen(var1, var5, var4);
               int var8 = var7[1] - var7[0];
               byte[] var9 = new byte[var8 + 2];
               System.arraycopy(var1, var7[0], var9, 1, var8);
               var9[0] = 40;
               var9[var8 + 1] = 41;
               encodeFilter(var0, var9, 0, var9.length);
               ++var6;
            }
         }
      }

   }

   private static void encodeExtensibleMatch(BerEncoder var0, byte[] var1, int var2, int var3, int var4, int var5) throws IOException, NamingException {
      boolean var6 = false;
      var0.beginSeq(169);
      int var7;
      if ((var7 = indexOf(var1, ':', var2, var3)) >= 0) {
         int var9;
         if ((var9 = indexOf(var1, ":dn", var7, var3)) >= 0) {
            var6 = true;
         }

         int var8;
         if ((var8 = indexOf(var1, ':', var7 + 1, var3)) >= 0 || var9 == -1) {
            if (var9 == var7) {
               var0.encodeOctetString(var1, 129, var8 + 1, var3 - (var8 + 1));
            } else if (var9 == var8 && var9 >= 0) {
               var0.encodeOctetString(var1, 129, var7 + 1, var8 - (var7 + 1));
            } else {
               var0.encodeOctetString(var1, 129, var7 + 1, var3 - (var7 + 1));
            }
         }

         if (var7 > var2) {
            var0.encodeOctetString(var1, 130, var2, var7 - var2);
         }
      } else {
         var0.encodeOctetString(var1, 130, var2, var3 - var2);
      }

      var0.encodeOctetString(unescapeFilterValue(var1, var4, var5), 131);
      var0.encodeBoolean(var6, 132);
      var0.endSeq();
   }

   private static void dprint(String var0) {
      dprint(var0, new byte[0], 0, 0);
   }

   private static void dprint(String var0, byte[] var1) {
      dprint(var0, var1, 0, var1.length);
   }

   private static void dprint(String var0, byte[] var1, int var2, int var3) {
      String var4 = "  ";

      for(int var5 = dbgIndent; var5-- > 0; var4 = var4 + "  ") {
      }

      var4 = var4 + var0;
      System.err.print(var4);

      for(int var6 = var2; var6 < var3; ++var6) {
         System.err.print((char)var1[var6]);
      }

      System.err.println();
   }
}
