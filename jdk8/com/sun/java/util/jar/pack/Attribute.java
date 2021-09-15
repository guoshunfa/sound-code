package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Attribute implements Comparable<Attribute> {
   Attribute.Layout def;
   byte[] bytes;
   Object fixups;
   private static final Map<List<Attribute>, List<Attribute>> canonLists = new HashMap();
   private static final Map<Attribute.Layout, Attribute> attributes = new HashMap();
   private static final Map<Attribute.Layout, Attribute> standardDefs = new HashMap();
   static final byte EK_INT = 1;
   static final byte EK_BCI = 2;
   static final byte EK_BCO = 3;
   static final byte EK_FLAG = 4;
   static final byte EK_REPL = 5;
   static final byte EK_REF = 6;
   static final byte EK_UN = 7;
   static final byte EK_CASE = 8;
   static final byte EK_CALL = 9;
   static final byte EK_CBLE = 10;
   static final byte EF_SIGN = 1;
   static final byte EF_DELTA = 2;
   static final byte EF_NULL = 4;
   static final byte EF_BACK = 8;
   static final int NO_BAND_INDEX = -1;

   public String name() {
      return this.def.name();
   }

   public Attribute.Layout layout() {
      return this.def;
   }

   public byte[] bytes() {
      return this.bytes;
   }

   public int size() {
      return this.bytes.length;
   }

   public ConstantPool.Entry getNameRef() {
      return this.def.getNameRef();
   }

   private Attribute(Attribute var1) {
      this.def = var1.def;
      this.bytes = var1.bytes;
      this.fixups = var1.fixups;
   }

   public Attribute(Attribute.Layout var1, byte[] var2, Object var3) {
      this.def = var1;
      this.bytes = var2;
      this.fixups = var3;
      Fixups.setBytes(var3, var2);
   }

   public Attribute(Attribute.Layout var1, byte[] var2) {
      this(var1, var2, (Object)null);
   }

   public Attribute addContent(byte[] var1, Object var2) {
      assert this.isCanonical();

      if (var1.length == 0 && var2 == null) {
         return this;
      } else {
         Attribute var3 = new Attribute(this);
         var3.bytes = var1;
         var3.fixups = var2;
         Fixups.setBytes(var2, var1);
         return var3;
      }
   }

   public Attribute addContent(byte[] var1) {
      return this.addContent(var1, (Object)null);
   }

   public void finishRefs(ConstantPool.Index var1) {
      if (this.fixups != null) {
         Fixups.finishRefs(this.fixups, this.bytes, var1);
         this.fixups = null;
      }

   }

   public boolean isCanonical() {
      return this == this.def.canon;
   }

   public int compareTo(Attribute var1) {
      return this.def.compareTo(var1.def);
   }

   public static List<Attribute> getCanonList(List<Attribute> var0) {
      synchronized(canonLists) {
         List var2 = (List)canonLists.get(var0);
         if (var2 == null) {
            ArrayList var5 = new ArrayList(var0.size());
            var5.addAll(var0);
            var2 = Collections.unmodifiableList(var5);
            canonLists.put(var0, var2);
         }

         return var2;
      }
   }

   public static Attribute find(int var0, String var1, String var2) {
      Attribute.Layout var3 = Attribute.Layout.makeKey(var0, var1, var2);
      synchronized(attributes) {
         Attribute var5 = (Attribute)attributes.get(var3);
         if (var5 == null) {
            var5 = (new Attribute.Layout(var0, var1, var2)).canonicalInstance();
            attributes.put(var3, var5);
         }

         return var5;
      }
   }

   public static Attribute.Layout keyForLookup(int var0, String var1) {
      return Attribute.Layout.makeKey(var0, var1);
   }

   public static Attribute lookup(Map<Attribute.Layout, Attribute> var0, int var1, String var2) {
      if (var0 == null) {
         var0 = standardDefs;
      }

      return (Attribute)var0.get(Attribute.Layout.makeKey(var1, var2));
   }

   public static Attribute define(Map<Attribute.Layout, Attribute> var0, int var1, String var2, String var3) {
      Attribute var4 = find(var1, var2, var3);
      var0.put(Attribute.Layout.makeKey(var1, var2), var4);
      return var4;
   }

   public static String contextName(int var0) {
      switch(var0) {
      case 0:
         return "class";
      case 1:
         return "field";
      case 2:
         return "method";
      case 3:
         return "code";
      default:
         return null;
      }
   }

   void visitRefs(Attribute.Holder var1, int var2, final Collection<ConstantPool.Entry> var3) {
      if (var2 == 0) {
         var3.add(this.getNameRef());
      }

      if (this.bytes.length != 0) {
         if (this.def.hasRefs) {
            if (this.fixups != null) {
               Fixups.visitRefs(this.fixups, var3);
            } else {
               this.def.parse(var1, this.bytes, 0, this.bytes.length, new Attribute.ValueStream() {
                  public void putInt(int var1, int var2) {
                  }

                  public void putRef(int var1, ConstantPool.Entry var2) {
                     var3.add(var2);
                  }

                  public int encodeBCI(int var1) {
                     return var1;
                  }
               });
            }
         }
      }
   }

   public void parse(Attribute.Holder var1, byte[] var2, int var3, int var4, Attribute.ValueStream var5) {
      this.def.parse(var1, var2, var3, var4, var5);
   }

   public Object unparse(Attribute.ValueStream var1, ByteArrayOutputStream var2) {
      return this.def.unparse(var1, var2);
   }

   public String toString() {
      return this.def + "{" + (this.bytes == null ? -1 : this.size()) + "}" + (this.fixups == null ? "" : this.fixups.toString());
   }

   public static String normalizeLayoutString(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      int var3 = var0.length();

      while(true) {
         while(true) {
            char var4;
            do {
               if (var2 >= var3) {
                  String var8 = var1.toString();
                  return var8;
               }

               var4 = var0.charAt(var2++);
            } while(var4 <= ' ');

            int var5;
            int var6;
            if (var4 == '#') {
               var5 = var0.indexOf(10, var2);
               var6 = var0.indexOf(13, var2);
               if (var5 < 0) {
                  var5 = var3;
               }

               if (var6 < 0) {
                  var6 = var3;
               }

               var2 = Math.min(var5, var6);
            } else if (var4 == '\\') {
               var1.append((int)var0.charAt(var2++));
            } else if (var4 == '0' && var0.startsWith("0x", var2 - 1)) {
               var5 = var2 - 1;

               for(var6 = var5 + 2; var6 < var3; ++var6) {
                  char var7 = var0.charAt(var6);
                  if ((var7 < '0' || var7 > '9') && (var7 < 'a' || var7 > 'f')) {
                     break;
                  }
               }

               if (var6 > var5) {
                  String var9 = var0.substring(var5, var6);
                  var1.append((Object)Integer.decode(var9));
                  var2 = var6;
               } else {
                  var1.append(var4);
               }
            } else {
               var1.append(var4);
            }
         }
      }
   }

   static Attribute.Layout.Element[] tokenizeLayout(Attribute.Layout var0, int var1, String var2) {
      ArrayList var3 = new ArrayList(var2.length());
      tokenizeLayout(var0, var1, var2, var3);
      Attribute.Layout.Element[] var4 = new Attribute.Layout.Element[var3.size()];
      var3.toArray(var4);
      return var4;
   }

   static void tokenizeLayout(Attribute.Layout var0, int var1, String var2, List<Attribute.Layout.Element> var3) {
      boolean var4 = false;
      int var5 = var2.length();
      int var6 = 0;

      while(true) {
         while(var6 < var5) {
            int var7;
            Attribute.Layout.Element var9;
            byte var10;
            var7 = var6;
            var9 = var0.new Element();
            int var8;
            int var14;
            label174:
            switch(var2.charAt(var6++)) {
            case '(':
               var10 = 9;
               var9.removeBand();
               var6 = var2.indexOf(41, var6);
               String var24 = var2.substring(var7 + 1, var6++);
               int var26 = Integer.parseInt(var24);
               var14 = var1 + var26;
               if (!(var26 + "").equals(var24) || var0.elems == null || var14 < 0 || var14 >= var0.elems.length) {
                  var6 = -var6;
                  continue;
               }

               Attribute.Layout.Element var27 = var0.elems[var14];

               assert var27.kind == 10;

               var9.value = var14;
               var9.body = new Attribute.Layout.Element[]{var27};
               if (var26 <= 0) {
                  var9.flags = (byte)(var9.flags | 8);
                  var27.flags = (byte)(var27.flags | 8);
               }
               break;
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'C':
            case 'D':
            case 'E':
            case 'G':
            case 'J':
            case 'L':
            case 'M':
            case 'Q':
            case 'U':
            default:
               var6 = -var6;
               continue;
            case 'B':
            case 'H':
            case 'I':
            case 'V':
               var10 = 1;
               --var6;
               var6 = tokenizeUInt(var9, var2, var6);
               break;
            case 'F':
               var10 = 4;
               var6 = tokenizeUInt(var9, var2, var6);
               break;
            case 'K':
               var10 = 6;
               switch(var2.charAt(var6++)) {
               case 'D':
                  var9.refKind = 6;
                  break label174;
               case 'E':
               case 'G':
               case 'H':
               case 'K':
               case 'N':
               case 'O':
               case 'P':
               case 'R':
               default:
                  var6 = -var6;
                  continue;
               case 'F':
                  var9.refKind = 4;
                  break label174;
               case 'I':
                  var9.refKind = 3;
                  break label174;
               case 'J':
                  var9.refKind = 5;
                  break label174;
               case 'L':
                  var9.refKind = 51;
                  break label174;
               case 'M':
                  var9.refKind = 15;
                  break label174;
               case 'Q':
                  var9.refKind = 53;
                  break label174;
               case 'S':
                  var9.refKind = 8;
                  break label174;
               case 'T':
                  var9.refKind = 16;
                  break label174;
               }
            case 'N':
               var10 = 5;
               var6 = tokenizeUInt(var9, var2, var6);
               if (var2.charAt(var6++) != '[') {
                  var6 = -var6;
                  continue;
               }

               var8 = var6;
               var6 = skipBody(var2, var6);
               var9.body = tokenizeLayout(var0, var1, var2.substring(var8, var6++));
               break;
            case 'O':
               var10 = 3;
               var9.flags = (byte)(var9.flags | 2);
               if (!var4) {
                  var6 = -var6;
                  continue;
               }

               var6 = tokenizeSInt(var9, var2, var6);
               break;
            case 'P':
               var10 = 2;
               if (var2.charAt(var6++) == 'O') {
                  var9.flags = (byte)(var9.flags | 2);
                  if (!var4) {
                     var6 = -var6;
                     continue;
                  }

                  ++var6;
               }

               --var6;
               var6 = tokenizeUInt(var9, var2, var6);
               break;
            case 'R':
               var10 = 6;
               switch(var2.charAt(var6++)) {
               case 'B':
                  var9.refKind = 17;
                  break label174;
               case 'C':
                  var9.refKind = 7;
                  break label174;
               case 'D':
                  var9.refKind = 12;
                  break label174;
               case 'E':
               case 'G':
               case 'H':
               case 'J':
               case 'K':
               case 'L':
               case 'O':
               case 'P':
               case 'R':
               case 'T':
               case 'V':
               case 'W':
               case 'X':
               default:
                  var6 = -var6;
                  continue;
               case 'F':
                  var9.refKind = 9;
                  break label174;
               case 'I':
                  var9.refKind = 11;
                  break label174;
               case 'M':
                  var9.refKind = 10;
                  break label174;
               case 'N':
                  var9.refKind = 52;
                  break label174;
               case 'Q':
                  var9.refKind = 50;
                  break label174;
               case 'S':
                  var9.refKind = 13;
                  break label174;
               case 'U':
                  var9.refKind = 1;
                  break label174;
               case 'Y':
                  var9.refKind = 18;
                  break label174;
               }
            case 'S':
               var10 = 1;
               --var6;
               var6 = tokenizeSInt(var9, var2, var6);
               break;
            case 'T':
               var10 = 7;
               var6 = tokenizeSInt(var9, var2, var6);
               ArrayList var11 = new ArrayList();

               int var12;
               label160:
               while(true) {
                  label158:
                  while(var2.charAt(var6++) == '(') {
                     var12 = var6;
                     var6 = var2.indexOf(41, var6);
                     String var13 = var2.substring(var12, var6++);
                     var14 = var13.length();
                     if (var2.charAt(var6++) != '[') {
                        var6 = -var6;
                        break label160;
                     }

                     if (var2.charAt(var6) == ']') {
                        var8 = var6;
                     } else {
                        var8 = var6;
                        var6 = skipBody(var2, var6);
                     }

                     Attribute.Layout.Element[] var15 = tokenizeLayout(var0, var1, var2.substring(var8, var6++));
                     if (var14 == 0) {
                        Attribute.Layout.Element var28 = var0.new Element();
                        var28.body = var15;
                        var28.kind = 8;
                        var28.removeBand();
                        var11.add(var28);
                        break label160;
                     }

                     boolean var16 = true;
                     int var17 = 0;

                     while(true) {
                        int var18 = var13.indexOf(44, var17);
                        if (var18 < 0) {
                           var18 = var14;
                        }

                        String var19 = var13.substring(var17, var18);
                        if (var19.length() == 0) {
                           var19 = "empty";
                        }

                        int var22 = findCaseDash(var19, 0);
                        int var20;
                        int var21;
                        if (var22 >= 0) {
                           var20 = parseIntBefore(var19, var22);
                           var21 = parseIntAfter(var19, var22);
                           if (var20 >= var21) {
                              var6 = -var6;
                              break;
                           }
                        } else {
                           var20 = var21 = Integer.parseInt(var19);
                        }

                        while(true) {
                           Attribute.Layout.Element var23 = var0.new Element();
                           var23.body = var15;
                           var23.kind = 8;
                           var23.removeBand();
                           if (!var16) {
                              var23.flags = (byte)(var23.flags | 8);
                           }

                           var16 = false;
                           var23.value = var20;
                           var11.add(var23);
                           if (var20 == var21) {
                              if (var18 == var14) {
                                 continue label158;
                              }

                              var17 = var18 + 1;
                              break;
                           }

                           ++var20;
                        }
                     }
                  }

                  var6 = -var6;
                  break;
               }

               var9.body = new Attribute.Layout.Element[var11.size()];
               var11.toArray(var9.body);
               var9.kind = var10;

               for(var12 = 0; var12 < var9.body.length - 1; ++var12) {
                  Attribute.Layout.Element var25 = var9.body[var12];
                  if (matchCase(var9, var25.value) != var25) {
                     var6 = -var6;
                     break;
                  }
               }
            }

            if (var10 == 6) {
               if (var2.charAt(var6++) == 'N') {
                  var9.flags = (byte)(var9.flags | 4);
                  ++var6;
               }

               --var6;
               var6 = tokenizeUInt(var9, var2, var6);
               var0.hasRefs = true;
            }

            var4 = var10 == 2;
            var9.kind = var10;
            var9.layout = var2.substring(var7, var6);
            var3.add(var9);
         }

         return;
      }
   }

   static String[] splitBodies(String var0) {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         if (var0.charAt(var2++) != '[') {
            var0.charAt(-var2);
         }

         int var3 = var2;
         var2 = skipBody(var0, var2);
         var1.add(var0.substring(var3, var2));
      }

      String[] var4 = new String[var1.size()];
      var1.toArray(var4);
      return var4;
   }

   private static int skipBody(String var0, int var1) {
      assert var0.charAt(var1 - 1) == '[';

      if (var0.charAt(var1) == ']') {
         return -var1;
      } else {
         int var2 = 1;

         while(var2 > 0) {
            switch(var0.charAt(var1++)) {
            case '[':
               ++var2;
               break;
            case ']':
               --var2;
            }
         }

         --var1;

         assert var0.charAt(var1) == ']';

         return var1;
      }
   }

   private static int tokenizeUInt(Attribute.Layout.Element var0, String var1, int var2) {
      switch(var1.charAt(var2++)) {
      case 'B':
         var0.len = 1;
         break;
      case 'H':
         var0.len = 2;
         break;
      case 'I':
         var0.len = 4;
         break;
      case 'V':
         var0.len = 0;
         break;
      default:
         return -var2;
      }

      return var2;
   }

   private static int tokenizeSInt(Attribute.Layout.Element var0, String var1, int var2) {
      if (var1.charAt(var2) == 'S') {
         var0.flags = (byte)(var0.flags | 1);
         ++var2;
      }

      return tokenizeUInt(var0, var1, var2);
   }

   private static boolean isDigit(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   static int findCaseDash(String var0, int var1) {
      if (var1 <= 0) {
         var1 = 1;
      }

      int var2 = var0.length() - 2;

      while(true) {
         int var3 = var0.indexOf(45, var1);
         if (var3 < 0 || var3 > var2) {
            return -1;
         }

         if (isDigit(var0.charAt(var3 - 1))) {
            char var4 = var0.charAt(var3 + 1);
            if (var4 == '-' && var3 + 2 < var0.length()) {
               var4 = var0.charAt(var3 + 2);
            }

            if (isDigit(var4)) {
               return var3;
            }
         }

         var1 = var3 + 1;
      }
   }

   static int parseIntBefore(String var0, int var1) {
      int var3;
      for(var3 = var1; var3 > 0 && isDigit(var0.charAt(var3 - 1)); --var3) {
      }

      if (var3 == var1) {
         return Integer.parseInt("empty");
      } else {
         if (var3 >= 1 && var0.charAt(var3 - 1) == '-') {
            --var3;
         }

         assert var3 == 0 || !isDigit(var0.charAt(var3 - 1));

         return Integer.parseInt(var0.substring(var3, var1));
      }
   }

   static int parseIntAfter(String var0, int var1) {
      int var2 = var1 + 1;
      int var3 = var2;
      int var4 = var0.length();
      if (var2 < var4 && var0.charAt(var2) == '-') {
         var3 = var2 + 1;
      }

      while(var3 < var4 && isDigit(var0.charAt(var3))) {
         ++var3;
      }

      return var2 == var3 ? Integer.parseInt("empty") : Integer.parseInt(var0.substring(var2, var3));
   }

   static String expandCaseDashNotation(String var0) {
      int var1 = findCaseDash(var0, 0);
      if (var1 < 0) {
         return var0;
      } else {
         StringBuilder var2 = new StringBuilder(var0.length() * 3);
         int var3 = 0;

         do {
            var2.append(var0.substring(var3, var1));
            var3 = var1 + 1;
            int var4 = parseIntBefore(var0, var1);
            int var5 = parseIntAfter(var0, var1);

            assert var4 < var5;

            var2.append(",");

            for(int var6 = var4 + 1; var6 < var5; ++var6) {
               var2.append(var6);
               var2.append(",");
            }

            var1 = findCaseDash(var0, var3);
         } while(var1 >= 0);

         var2.append(var0.substring(var3));
         return var2.toString();
      }
   }

   static int parseUsing(Attribute.Layout.Element[] var0, Attribute.Holder var1, byte[] var2, int var3, int var4, Attribute.ValueStream var5) {
      int var6 = 0;
      int var7 = 0;
      int var8 = var3 + var4;
      int[] var9 = new int[]{0};

      label94:
      for(int var10 = 0; var10 < var0.length; ++var10) {
         Attribute.Layout.Element var11 = var0[var10];
         int var12 = var11.bandIndex;
         int var13;
         int var14;
         int var15;
         switch(var11.kind) {
         case 1:
            var3 = parseInt(var11, var2, var3, var9);
            var13 = var9[0];
            var5.putInt(var12, var13);
            break;
         case 2:
            var3 = parseInt(var11, var2, var3, var9);
            var14 = var9[0];
            var15 = var5.encodeBCI(var14);
            if (!var11.flagTest((byte)2)) {
               var13 = var15;
            } else {
               var13 = var15 - var7;
            }

            var6 = var14;
            var7 = var15;
            var5.putInt(var12, var13);
            break;
         case 3:
            assert var11.flagTest((byte)2);

            var3 = parseInt(var11, var2, var3, var9);
            var14 = var6 + var9[0];
            var15 = var5.encodeBCI(var14);
            var13 = var15 - var7;
            var6 = var14;
            var7 = var15;
            var5.putInt(var12, var13);
            break;
         case 4:
            var3 = parseInt(var11, var2, var3, var9);
            var13 = var9[0];
            var5.putInt(var12, var13);
            break;
         case 5:
            var3 = parseInt(var11, var2, var3, var9);
            var13 = var9[0];
            var5.putInt(var12, var13);
            int var22 = 0;

            while(true) {
               if (var22 >= var13) {
                  continue label94;
               }

               var3 = parseUsing(var11.body, var1, var2, var3, var8 - var3, var5);
               ++var22;
            }
         case 6:
            var3 = parseInt(var11, var2, var3, var9);
            int var17 = var9[0];
            Object var18;
            if (var17 == 0) {
               var18 = null;
            } else {
               ConstantPool.Entry[] var19 = var1.getCPMap();
               var18 = var17 >= 0 && var17 < var19.length ? var19[var17] : null;
               byte var20 = var11.refKind;
               String var21;
               if (var18 != null && var20 == 13 && ((ConstantPool.Entry)var18).getTag() == 1) {
                  var21 = ((ConstantPool.Entry)var18).stringValue();
                  var18 = ConstantPool.getSignatureEntry(var21);
               }

               var21 = var18 == null ? "invalid CP index" : "type=" + ConstantPool.tagName(((ConstantPool.Entry)var18).tag);
               if (var18 == null || !((ConstantPool.Entry)var18).tagMatches(var20)) {
                  throw new IllegalArgumentException("Bad constant, expected type=" + ConstantPool.tagName(var20) + " got " + var21);
               }
            }

            var5.putRef(var12, (ConstantPool.Entry)var18);
            break;
         case 7:
            var3 = parseInt(var11, var2, var3, var9);
            var13 = var9[0];
            var5.putInt(var12, var13);
            Attribute.Layout.Element var16 = matchCase(var11, var13);
            var3 = parseUsing(var16.body, var1, var2, var3, var8 - var3, var5);
            break;
         case 8:
         default:
            assert false;
            break;
         case 9:
            assert var11.body.length == 1;

            assert var11.body[0].kind == 10;

            if (var11.flagTest((byte)8)) {
               var5.noteBackCall(var11.value);
            }

            var3 = parseUsing(var11.body[0].body, var1, var2, var3, var8 - var3, var5);
         }
      }

      return var3;
   }

   static Attribute.Layout.Element matchCase(Attribute.Layout.Element var0, int var1) {
      assert var0.kind == 7;

      int var2 = var0.body.length - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         Attribute.Layout.Element var4 = var0.body[var3];

         assert var4.kind == 8;

         if (var1 == var4.value) {
            return var4;
         }
      }

      return var0.body[var2];
   }

   private static int parseInt(Attribute.Layout.Element var0, byte[] var1, int var2, int[] var3) {
      int var4 = 0;
      int var5 = var0.len * 8;
      int var6 = var5;

      while(true) {
         var6 -= 8;
         if (var6 < 0) {
            if (var5 < 32 && var0.flagTest((byte)1)) {
               var6 = 32 - var5;
               var4 = var4 << var6 >> var6;
            }

            var3[0] = var4;
            return var2;
         }

         var4 += (var1[var2++] & 255) << var6;
      }
   }

   static void unparseUsing(Attribute.Layout.Element[] var0, Object[] var1, Attribute.ValueStream var2, ByteArrayOutputStream var3) {
      int var4 = 0;
      int var5 = 0;

      label72:
      for(int var6 = 0; var6 < var0.length; ++var6) {
         Attribute.Layout.Element var7 = var0[var6];
         int var8 = var7.bandIndex;
         int var9;
         int var10;
         int var11;
         switch(var7.kind) {
         case 1:
            var9 = var2.getInt(var8);
            unparseInt(var7, var9, var3);
            break;
         case 2:
            var9 = var2.getInt(var8);
            if (!var7.flagTest((byte)2)) {
               var11 = var9;
            } else {
               var11 = var5 + var9;
            }

            assert var4 == var2.decodeBCI(var5);

            var10 = var2.decodeBCI(var11);
            unparseInt(var7, var10, var3);
            var4 = var10;
            var5 = var11;
            break;
         case 3:
            var9 = var2.getInt(var8);

            assert var7.flagTest((byte)2);

            assert var4 == var2.decodeBCI(var5);

            var11 = var5 + var9;
            var10 = var2.decodeBCI(var11);
            unparseInt(var7, var10 - var4, var3);
            var4 = var10;
            var5 = var11;
            break;
         case 4:
            var9 = var2.getInt(var8);
            unparseInt(var7, var9, var3);
            break;
         case 5:
            var9 = var2.getInt(var8);
            unparseInt(var7, var9, var3);
            int var15 = 0;

            while(true) {
               if (var15 >= var9) {
                  continue label72;
               }

               unparseUsing(var7.body, var1, var2, var3);
               ++var15;
            }
         case 6:
            ConstantPool.Entry var13 = var2.getRef(var8);
            byte var14;
            if (var13 != null) {
               var1[0] = Fixups.addRefWithLoc(var1[0], var3.size(), var13);
               var14 = 0;
            } else {
               var14 = 0;
            }

            unparseInt(var7, var14, var3);
            break;
         case 7:
            var9 = var2.getInt(var8);
            unparseInt(var7, var9, var3);
            Attribute.Layout.Element var12 = matchCase(var7, var9);
            unparseUsing(var12.body, var1, var2, var3);
            break;
         case 8:
         default:
            assert false;
            break;
         case 9:
            assert var7.body.length == 1;

            assert var7.body[0].kind == 10;

            unparseUsing(var7.body[0].body, var1, var2, var3);
         }
      }

   }

   private static void unparseInt(Attribute.Layout.Element var0, int var1, ByteArrayOutputStream var2) {
      int var3 = var0.len * 8;
      if (var3 != 0) {
         int var4;
         if (var3 < 32) {
            var4 = 32 - var3;
            int var5;
            if (var0.flagTest((byte)1)) {
               var5 = var1 << var4 >> var4;
            } else {
               var5 = var1 << var4 >>> var4;
            }

            if (var5 != var1) {
               throw new InternalError("cannot code in " + var0.len + " bytes: " + var1);
            }
         }

         var4 = var3;

         while(true) {
            var4 -= 8;
            if (var4 < 0) {
               return;
            }

            var2.write((byte)(var1 >>> var4));
         }
      }
   }

   static {
      Map var0 = standardDefs;
      define(var0, 0, "Signature", "RSH");
      define(var0, 0, "Synthetic", "");
      define(var0, 0, "Deprecated", "");
      define(var0, 0, "SourceFile", "RUH");
      define(var0, 0, "EnclosingMethod", "RCHRDNH");
      define(var0, 0, "InnerClasses", "NH[RCHRCNHRUNHFH]");
      define(var0, 0, "BootstrapMethods", "NH[RMHNH[KLH]]");
      define(var0, 1, "Signature", "RSH");
      define(var0, 1, "Synthetic", "");
      define(var0, 1, "Deprecated", "");
      define(var0, 1, "ConstantValue", "KQH");
      define(var0, 2, "Signature", "RSH");
      define(var0, 2, "Synthetic", "");
      define(var0, 2, "Deprecated", "");
      define(var0, 2, "Exceptions", "NH[RCH]");
      define(var0, 2, "MethodParameters", "NB[RUNHFH]");
      define(var0, 3, "StackMapTable", "[NH[(1)]][TB(64-127)[(2)](247)[(1)(2)](248-251)[(1)](252)[(1)(2)](253)[(1)(2)(2)](254)[(1)(2)(2)(2)](255)[(1)NH[(2)]NH[(2)]]()[]][H][TB(7)[RCH](8)[PH]()[]]");
      define(var0, 3, "LineNumberTable", "NH[PHH]");
      define(var0, 3, "LocalVariableTable", "NH[PHOHRUHRSHH]");
      define(var0, 3, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
      String[] var8 = new String[]{normalizeLayoutString("\n  # parameter_annotations :=\n  [ NB[(1)] ]     # forward call to annotations"), normalizeLayoutString("\n  # annotations :=\n  [ NH[(1)] ]     # forward call to annotation\n  "), normalizeLayoutString("\n  # annotation :=\n  [RSH\n    NH[RUH (1)]   # forward call to value\n    ]"), normalizeLayoutString("\n  # value :=\n  [TB # Callable 2 encodes one tagged value.\n    (\\B,\\C,\\I,\\S,\\Z)[KIH]\n    (\\D)[KDH]\n    (\\F)[KFH]\n    (\\J)[KJH]\n    (\\c)[RSH]\n    (\\e)[RSH RUH]\n    (\\s)[RUH]\n    (\\[)[NH[(0)]] # backward self-call to value\n    (\\@)[RSH NH[RUH (0)]] # backward self-call to value\n    ()[] ]")};
      String[] var1 = new String[]{normalizeLayoutString("\n # type-annotations :=\n  [ NH[(1)(2)(3)] ]     # forward call to type-annotations"), normalizeLayoutString("\n  # type-annotation :=\n  [TB\n    (0-1) [B] # {CLASS, METHOD}_TYPE_PARAMETER\n    (16) [FH] # CLASS_EXTENDS\n    (17-18) [BB] # {CLASS, METHOD}_TYPE_PARAMETER_BOUND\n    (19-21) [] # FIELD, METHOD_RETURN, METHOD_RECEIVER\n    (22) [B] # METHOD_FORMAL_PARAMETER\n    (23) [H] # THROWS\n    (64-65) [NH[PHOHH]] # LOCAL_VARIABLE, RESOURCE_VARIABLE\n    (66) [H] # EXCEPTION_PARAMETER\n    (67-70) [PH] # INSTANCEOF, NEW, {CONSTRUCTOR, METHOD}_REFERENCE_RECEIVER\n    (71-75) [PHB] # CAST, {CONSTRUCTOR,METHOD}_INVOCATION_TYPE_ARGUMENT, {CONSTRUCTOR, METHOD}_REFERENCE_TYPE_ARGUMENT\n    ()[] ]"), normalizeLayoutString("\n # type-path\n [ NB[BB] ]")};
      Map var2 = standardDefs;
      String var3 = var8[3];
      String var4 = var8[1] + var8[2] + var8[3];
      String var5 = var8[0] + var4;
      String var6 = var1[0] + var1[1] + var1[2] + var8[2] + var8[3];

      for(int var7 = 0; var7 < 4; ++var7) {
         if (var7 != 3) {
            define(var2, var7, "RuntimeVisibleAnnotations", var4);
            define(var2, var7, "RuntimeInvisibleAnnotations", var4);
            if (var7 == 2) {
               define(var2, var7, "RuntimeVisibleParameterAnnotations", var5);
               define(var2, var7, "RuntimeInvisibleParameterAnnotations", var5);
               define(var2, var7, "AnnotationDefault", var3);
            }
         }

         define(var2, var7, "RuntimeVisibleTypeAnnotations", var6);
         define(var2, var7, "RuntimeInvisibleTypeAnnotations", var6);
      }

      assert expandCaseDashNotation("1-5").equals("1,2,3,4,5");

      assert expandCaseDashNotation("-2--1").equals("-2,-1");

      assert expandCaseDashNotation("-2-1").equals("-2,-1,0,1");

      assert expandCaseDashNotation("-1-0").equals("-1,0");

   }

   public static class FormatException extends IOException {
      private static final long serialVersionUID = -2542243830788066513L;
      private int ctype;
      private String name;
      String layout;

      public FormatException(String var1, int var2, String var3, String var4) {
         super(Constants.ATTR_CONTEXT_NAME[var2] + " attribute \"" + var3 + "\"" + (var1 == null ? "" : ": " + var1));
         this.ctype = var2;
         this.name = var3;
         this.layout = var4;
      }

      public FormatException(String var1, int var2, String var3) {
         this(var1, var2, var3, (String)null);
      }
   }

   public static class Layout implements Comparable<Attribute.Layout> {
      int ctype;
      String name;
      boolean hasRefs;
      String layout;
      int bandCount;
      Attribute.Layout.Element[] elems;
      Attribute canon;
      private static final Attribute.Layout.Element[] noElems = new Attribute.Layout.Element[0];

      public int ctype() {
         return this.ctype;
      }

      public String name() {
         return this.name;
      }

      public String layout() {
         return this.layout;
      }

      public Attribute canonicalInstance() {
         return this.canon;
      }

      public ConstantPool.Entry getNameRef() {
         return ConstantPool.getUtf8Entry(this.name());
      }

      public boolean isEmpty() {
         return this.layout.isEmpty();
      }

      public Layout(int var1, String var2, String var3) {
         this.ctype = var1;
         this.name = var2.intern();
         this.layout = var3.intern();

         assert var1 < 4;

         boolean var4 = var3.startsWith("[");

         try {
            if (!var4) {
               this.elems = Attribute.tokenizeLayout(this, -1, var3);
            } else {
               String[] var5 = Attribute.splitBodies(var3);
               Attribute.Layout.Element[] var6 = new Attribute.Layout.Element[var5.length];
               this.elems = var6;

               int var7;
               Attribute.Layout.Element var8;
               for(var7 = 0; var7 < var6.length; ++var7) {
                  var8 = new Attribute.Layout.Element();
                  var8.kind = 10;
                  var8.removeBand();
                  var8.bandIndex = -1;
                  var8.layout = var5[var7];
                  var6[var7] = var8;
               }

               for(var7 = 0; var7 < var6.length; ++var7) {
                  var8 = var6[var7];
                  var8.body = Attribute.tokenizeLayout(this, var7, var5[var7]);
               }
            }
         } catch (StringIndexOutOfBoundsException var9) {
            throw new RuntimeException("Bad attribute layout: " + var3, var9);
         }

         this.canon = new Attribute(this, Constants.noBytes);
      }

      private Layout() {
      }

      static Attribute.Layout makeKey(int var0, String var1, String var2) {
         Attribute.Layout var3 = new Attribute.Layout();
         var3.ctype = var0;
         var3.name = var1.intern();
         var3.layout = var2.intern();

         assert var0 < 4;

         return var3;
      }

      static Attribute.Layout makeKey(int var0, String var1) {
         return makeKey(var0, var1, "");
      }

      public Attribute addContent(byte[] var1, Object var2) {
         return this.canon.addContent(var1, var2);
      }

      public Attribute addContent(byte[] var1) {
         return this.canon.addContent(var1, (Object)null);
      }

      public boolean equals(Object var1) {
         return var1 != null && var1.getClass() == Attribute.Layout.class && this.equals((Attribute.Layout)var1);
      }

      public boolean equals(Attribute.Layout var1) {
         return this.name.equals(var1.name) && this.layout.equals(var1.layout) && this.ctype == var1.ctype;
      }

      public int hashCode() {
         return ((17 + this.name.hashCode()) * 37 + this.layout.hashCode()) * 37 + this.ctype;
      }

      public int compareTo(Attribute.Layout var1) {
         int var2 = this.name.compareTo(var1.name);
         if (var2 != 0) {
            return var2;
         } else {
            var2 = this.layout.compareTo(var1.layout);
            return var2 != 0 ? var2 : this.ctype - var1.ctype;
         }
      }

      public String toString() {
         String var1 = Attribute.contextName(this.ctype) + "." + this.name + "[" + this.layout + "]";

         assert (var1 = this.stringForDebug()) != null;

         return var1;
      }

      private String stringForDebug() {
         return Attribute.contextName(this.ctype) + "." + this.name + Arrays.asList(this.elems);
      }

      public boolean hasCallables() {
         return this.elems.length > 0 && this.elems[0].kind == 10;
      }

      public Attribute.Layout.Element[] getCallables() {
         if (this.hasCallables()) {
            Attribute.Layout.Element[] var1 = (Attribute.Layout.Element[])Arrays.copyOf((Object[])this.elems, this.elems.length);
            return var1;
         } else {
            return noElems;
         }
      }

      public Attribute.Layout.Element[] getEntryPoint() {
         if (this.hasCallables()) {
            return this.elems[0].body;
         } else {
            Attribute.Layout.Element[] var1 = (Attribute.Layout.Element[])Arrays.copyOf((Object[])this.elems, this.elems.length);
            return var1;
         }
      }

      public void parse(Attribute.Holder var1, byte[] var2, int var3, int var4, Attribute.ValueStream var5) {
         int var6 = Attribute.parseUsing(this.getEntryPoint(), var1, var2, var3, var4, var5);
         if (var6 != var3 + var4) {
            throw new InternalError("layout parsed " + (var6 - var3) + " out of " + var4 + " bytes");
         }
      }

      public Object unparse(Attribute.ValueStream var1, ByteArrayOutputStream var2) {
         Object[] var3 = new Object[]{null};
         Attribute.unparseUsing(this.getEntryPoint(), var3, var1, var2);
         return var3[0];
      }

      public String layoutForClassVersion(Package.Version var1) {
         return var1.lessThan(Constants.JAVA6_MAX_CLASS_VERSION) ? Attribute.expandCaseDashNotation(this.layout) : this.layout;
      }

      public class Element {
         String layout;
         byte flags;
         byte kind;
         byte len;
         byte refKind;
         int bandIndex;
         int value;
         Attribute.Layout.Element[] body;

         boolean flagTest(byte var1) {
            return (this.flags & var1) != 0;
         }

         Element() {
            this.bandIndex = Layout.this.bandCount++;
         }

         void removeBand() {
            --Layout.this.bandCount;

            assert this.bandIndex == Layout.this.bandCount;

            this.bandIndex = -1;
         }

         public boolean hasBand() {
            return this.bandIndex >= 0;
         }

         public String toString() {
            String var1 = this.layout;

            assert (var1 = this.stringForDebug()) != null;

            return var1;
         }

         private String stringForDebug() {
            Attribute.Layout.Element[] var1 = this.body;
            switch(this.kind) {
            case 8:
               if (this.flagTest((byte)8)) {
                  var1 = null;
               }
               break;
            case 9:
               var1 = null;
            }

            return this.layout + (!this.hasBand() ? "" : "#" + this.bandIndex) + "<" + (this.flags == 0 ? "" : "" + this.flags) + this.kind + this.len + (this.refKind == 0 ? "" : "" + this.refKind) + ">" + (this.value == 0 ? "" : "(" + this.value + ")") + (var1 == null ? "" : "" + Arrays.asList(var1));
         }
      }
   }

   public abstract static class ValueStream {
      public int getInt(int var1) {
         throw this.undef();
      }

      public void putInt(int var1, int var2) {
         throw this.undef();
      }

      public ConstantPool.Entry getRef(int var1) {
         throw this.undef();
      }

      public void putRef(int var1, ConstantPool.Entry var2) {
         throw this.undef();
      }

      public int decodeBCI(int var1) {
         throw this.undef();
      }

      public int encodeBCI(int var1) {
         throw this.undef();
      }

      public void noteBackCall(int var1) {
      }

      private RuntimeException undef() {
         return new UnsupportedOperationException("ValueStream method");
      }
   }

   public abstract static class Holder {
      protected int flags;
      protected List<Attribute> attributes;
      static final List<Attribute> noAttributes = Arrays.asList();

      protected abstract ConstantPool.Entry[] getCPMap();

      public int attributeSize() {
         return this.attributes == null ? 0 : this.attributes.size();
      }

      public void trimToSize() {
         if (this.attributes != null) {
            if (this.attributes.isEmpty()) {
               this.attributes = null;
            } else {
               if (this.attributes instanceof ArrayList) {
                  ArrayList var1 = (ArrayList)this.attributes;
                  var1.trimToSize();
                  boolean var2 = true;
                  Iterator var3 = var1.iterator();

                  while(var3.hasNext()) {
                     Attribute var4 = (Attribute)var3.next();
                     if (!var4.isCanonical()) {
                        var2 = false;
                     }

                     if (var4.fixups != null) {
                        assert !var4.isCanonical();

                        var4.fixups = Fixups.trimToSize(var4.fixups);
                     }
                  }

                  if (var2) {
                     this.attributes = Attribute.getCanonList(var1);
                  }
               }

            }
         }
      }

      public void addAttribute(Attribute var1) {
         if (this.attributes == null) {
            this.attributes = new ArrayList(3);
         } else if (!(this.attributes instanceof ArrayList)) {
            this.attributes = new ArrayList(this.attributes);
         }

         this.attributes.add(var1);
      }

      public Attribute removeAttribute(Attribute var1) {
         if (this.attributes == null) {
            return null;
         } else if (!this.attributes.contains(var1)) {
            return null;
         } else {
            if (!(this.attributes instanceof ArrayList)) {
               this.attributes = new ArrayList(this.attributes);
            }

            this.attributes.remove(var1);
            return var1;
         }
      }

      public Attribute getAttribute(int var1) {
         return (Attribute)this.attributes.get(var1);
      }

      protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
         if (this.attributes != null) {
            Iterator var3 = this.attributes.iterator();

            while(var3.hasNext()) {
               Attribute var4 = (Attribute)var3.next();
               var4.visitRefs(this, var1, var2);
            }

         }
      }

      public List<Attribute> getAttributes() {
         return this.attributes == null ? noAttributes : this.attributes;
      }

      public void setAttributes(List<Attribute> var1) {
         if (var1.isEmpty()) {
            this.attributes = null;
         } else {
            this.attributes = var1;
         }

      }

      public Attribute getAttribute(String var1) {
         if (this.attributes == null) {
            return null;
         } else {
            Iterator var2 = this.attributes.iterator();

            Attribute var3;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (Attribute)var2.next();
            } while(!var3.name().equals(var1));

            return var3;
         }
      }

      public Attribute getAttribute(Attribute.Layout var1) {
         if (this.attributes == null) {
            return null;
         } else {
            Iterator var2 = this.attributes.iterator();

            Attribute var3;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (Attribute)var2.next();
            } while(var3.layout() != var1);

            return var3;
         }
      }

      public Attribute removeAttribute(String var1) {
         return this.removeAttribute(this.getAttribute(var1));
      }

      public Attribute removeAttribute(Attribute.Layout var1) {
         return this.removeAttribute(this.getAttribute(var1));
      }

      public void strip(String var1) {
         this.removeAttribute(this.getAttribute(var1));
      }
   }
}
