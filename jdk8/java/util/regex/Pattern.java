package java.util.regex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Pattern implements Serializable {
   public static final int UNIX_LINES = 1;
   public static final int CASE_INSENSITIVE = 2;
   public static final int COMMENTS = 4;
   public static final int MULTILINE = 8;
   public static final int LITERAL = 16;
   public static final int DOTALL = 32;
   public static final int UNICODE_CASE = 64;
   public static final int CANON_EQ = 128;
   public static final int UNICODE_CHARACTER_CLASS = 256;
   private static final long serialVersionUID = 5073258162644648461L;
   private String pattern;
   private int flags;
   private transient volatile boolean compiled = false;
   private transient String normalizedPattern;
   transient Pattern.Node root;
   transient Pattern.Node matchRoot;
   transient int[] buffer;
   transient volatile Map<String, Integer> namedGroups;
   transient Pattern.GroupHead[] groupNodes;
   private transient int[] temp;
   transient int capturingGroupCount;
   transient int localCount;
   private transient int cursor;
   private transient int patternLength;
   private transient boolean hasSupplementary;
   static final int MAX_REPS = Integer.MAX_VALUE;
   static final int GREEDY = 0;
   static final int LAZY = 1;
   static final int POSSESSIVE = 2;
   static final int INDEPENDENT = 3;
   static Pattern.Node lookbehindEnd = new Pattern.Node() {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         return var2 == var1.lookbehindTo;
      }
   };
   static Pattern.Node accept = new Pattern.Node();
   static Pattern.Node lastAccept = new Pattern.LastNode();

   public static Pattern compile(String var0) {
      return new Pattern(var0, 0);
   }

   public static Pattern compile(String var0, int var1) {
      return new Pattern(var0, var1);
   }

   public String pattern() {
      return this.pattern;
   }

   public String toString() {
      return this.pattern;
   }

   public Matcher matcher(CharSequence var1) {
      if (!this.compiled) {
         synchronized(this) {
            if (!this.compiled) {
               this.compile();
            }
         }
      }

      Matcher var2 = new Matcher(this, var1);
      return var2;
   }

   public int flags() {
      return this.flags;
   }

   public static boolean matches(String var0, CharSequence var1) {
      Pattern var2 = compile(var0);
      Matcher var3 = var2.matcher(var1);
      return var3.matches();
   }

   public String[] split(CharSequence var1, int var2) {
      int var3 = 0;
      boolean var4 = var2 > 0;
      ArrayList var5 = new ArrayList();
      Matcher var6 = this.matcher(var1);

      while(true) {
         while(var6.find()) {
            String var7;
            if (var4 && var5.size() >= var2 - 1) {
               if (var5.size() == var2 - 1) {
                  var7 = var1.subSequence(var3, var1.length()).toString();
                  var5.add(var7);
                  var3 = var6.end();
               }
            } else if (var3 != 0 || var3 != var6.start() || var6.start() != var6.end()) {
               var7 = var1.subSequence(var3, var6.start()).toString();
               var5.add(var7);
               var3 = var6.end();
            }
         }

         if (var3 == 0) {
            return new String[]{var1.toString()};
         }

         if (!var4 || var5.size() < var2) {
            var5.add(var1.subSequence(var3, var1.length()).toString());
         }

         int var9 = var5.size();
         if (var2 == 0) {
            while(var9 > 0 && ((String)var5.get(var9 - 1)).equals("")) {
               --var9;
            }
         }

         String[] var8 = new String[var9];
         return (String[])var5.subList(0, var9).toArray(var8);
      }
   }

   public String[] split(CharSequence var1) {
      return this.split(var1, 0);
   }

   public static String quote(String var0) {
      int var1 = var0.indexOf("\\E");
      if (var1 == -1) {
         return "\\Q" + var0 + "\\E";
      } else {
         StringBuilder var2 = new StringBuilder(var0.length() * 2);
         var2.append("\\Q");
         boolean var4 = false;
         int var3 = 0;

         while((var1 = var0.indexOf("\\E", var3)) != -1) {
            var2.append(var0.substring(var3, var1));
            var3 = var1 + 2;
            var2.append("\\E\\\\E\\Q");
         }

         var2.append(var0.substring(var3, var0.length()));
         var2.append("\\E");
         return var2.toString();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.capturingGroupCount = 1;
      this.localCount = 0;
      this.compiled = false;
      if (this.pattern.length() == 0) {
         this.root = new Pattern.Start(lastAccept);
         this.matchRoot = lastAccept;
         this.compiled = true;
      }

   }

   private Pattern(String var1, int var2) {
      this.pattern = var1;
      this.flags = var2;
      if ((this.flags & 256) != 0) {
         this.flags |= 64;
      }

      this.capturingGroupCount = 1;
      this.localCount = 0;
      if (this.pattern.length() > 0) {
         this.compile();
      } else {
         this.root = new Pattern.Start(lastAccept);
         this.matchRoot = lastAccept;
      }

   }

   private void normalize() {
      boolean var1 = false;
      int var2 = -1;
      this.normalizedPattern = Normalizer.normalize(this.pattern, Normalizer.Form.NFD);
      this.patternLength = this.normalizedPattern.length();
      StringBuilder var3 = new StringBuilder(this.patternLength);

      int var5;
      for(int var4 = 0; var4 < this.patternLength; var4 += Character.charCount(var5)) {
         var5 = this.normalizedPattern.codePointAt(var4);
         if (Character.getType(var5) == 6 && var2 != -1) {
            StringBuilder var6 = new StringBuilder();
            var6.appendCodePoint(var2);
            var6.appendCodePoint(var5);

            while(Character.getType(var5) == 6) {
               var4 += Character.charCount(var5);
               if (var4 >= this.patternLength) {
                  break;
               }

               var5 = this.normalizedPattern.codePointAt(var4);
               var6.appendCodePoint(var5);
            }

            String var7 = this.produceEquivalentAlternation(var6.toString());
            var3.setLength(var3.length() - Character.charCount(var2));
            var3.append("(?:").append(var7).append(")");
         } else if (var5 == 91 && var2 != 92) {
            var4 = this.normalizeCharClass(var3, var4);
         } else {
            var3.appendCodePoint(var5);
         }

         var2 = var5;
      }

      this.normalizedPattern = var3.toString();
   }

   private int normalizeCharClass(StringBuilder var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      StringBuilder var4 = null;
      int var5 = -1;
      ++var2;
      if (var2 == this.normalizedPattern.length()) {
         throw this.error("Unclosed character class");
      } else {
         var3.append("[");

         while(true) {
            int var7 = this.normalizedPattern.codePointAt(var2);
            if (var7 == 93 && var5 != 92) {
               var3.append((char)var7);
               String var6;
               if (var4 != null) {
                  var6 = "(?:" + var3.toString() + var4.toString() + ")";
               } else {
                  var6 = var3.toString();
               }

               var1.append(var6);
               return var2;
            }

            if (Character.getType(var7) != 6) {
               var3.appendCodePoint(var7);
               ++var2;
            } else {
               StringBuilder var8 = new StringBuilder();
               var8.appendCodePoint(var5);

               while(Character.getType(var7) == 6) {
                  var8.appendCodePoint(var7);
                  var2 += Character.charCount(var7);
                  if (var2 >= this.normalizedPattern.length()) {
                     break;
                  }

                  var7 = this.normalizedPattern.codePointAt(var2);
               }

               String var9 = this.produceEquivalentAlternation(var8.toString());
               var3.setLength(var3.length() - Character.charCount(var5));
               if (var4 == null) {
                  var4 = new StringBuilder();
               }

               var4.append('|');
               var4.append(var9);
            }

            if (var2 == this.normalizedPattern.length()) {
               throw this.error("Unclosed character class");
            }

            var5 = var7;
         }
      }
   }

   private String produceEquivalentAlternation(String var1) {
      int var2 = countChars(var1, 0, 1);
      if (var1.length() == var2) {
         return var1;
      } else {
         String var3 = var1.substring(0, var2);
         String var4 = var1.substring(var2);
         String[] var5 = this.producePermutations(var4);
         StringBuilder var6 = new StringBuilder(var1);

         for(int var7 = 0; var7 < var5.length; ++var7) {
            String var8 = var3 + var5[var7];
            if (var7 > 0) {
               var6.append("|" + var8);
            }

            var8 = this.composeOneStep(var8);
            if (var8 != null) {
               var6.append("|" + this.produceEquivalentAlternation(var8));
            }
         }

         return var6.toString();
      }
   }

   private String[] producePermutations(String var1) {
      if (var1.length() == countChars(var1, 0, 1)) {
         return new String[]{var1};
      } else {
         int var2;
         int var3;
         String[] var16;
         if (var1.length() == countChars(var1, 0, 2)) {
            var2 = Character.codePointAt((CharSequence)var1, 0);
            var3 = Character.codePointAt((CharSequence)var1, Character.charCount(var2));
            if (this.getClass(var3) == this.getClass(var2)) {
               return new String[]{var1};
            } else {
               var16 = new String[]{var1, null};
               StringBuilder var17 = new StringBuilder(2);
               var17.appendCodePoint(var3);
               var17.appendCodePoint(var2);
               var16[1] = var17.toString();
               return var16;
            }
         } else {
            var2 = 1;
            var3 = countCodePoints(var1);

            for(int var4 = 1; var4 < var3; ++var4) {
               var2 *= var4 + 1;
            }

            var16 = new String[var2];
            int[] var5 = new int[var3];
            int var6 = 0;

            int var7;
            int var8;
            for(var7 = 0; var6 < var3; ++var6) {
               var8 = Character.codePointAt((CharSequence)var1, var7);
               var5[var6] = this.getClass(var8);
               var7 += Character.charCount(var8);
            }

            var6 = 0;
            var8 = 0;

            int var9;
            for(var9 = 0; var8 < var3; var9 += var7) {
               var7 = countChars(var1, var9, 1);
               boolean var10 = false;
               int var11 = var8 - 1;

               label54:
               while(true) {
                  if (var11 < 0) {
                     StringBuilder var19 = new StringBuilder(var1);
                     String var12 = var19.delete(var9, var9 + var7).toString();
                     String[] var13 = this.producePermutations(var12);
                     String var14 = var1.substring(var9, var9 + var7);
                     int var15 = 0;

                     while(true) {
                        if (var15 >= var13.length) {
                           break label54;
                        }

                        var16[var6++] = var14 + var13[var15];
                        ++var15;
                     }
                  }

                  if (var5[var11] == var5[var8]) {
                     break;
                  }

                  --var11;
               }

               ++var8;
            }

            String[] var18 = new String[var6];

            for(var9 = 0; var9 < var6; ++var9) {
               var18[var9] = var16[var9];
            }

            return var18;
         }
      }
   }

   private int getClass(int var1) {
      return sun.text.Normalizer.getCombiningClass(var1);
   }

   private String composeOneStep(String var1) {
      int var2 = countChars(var1, 0, 2);
      String var3 = var1.substring(0, var2);
      String var4 = Normalizer.normalize(var3, Normalizer.Form.NFC);
      if (var4.equals(var3)) {
         return null;
      } else {
         String var5 = var1.substring(var2);
         return var4 + var5;
      }
   }

   private void RemoveQEQuoting() {
      int var1 = this.patternLength;
      int var2 = 0;

      while(var2 < var1 - 1) {
         if (this.temp[var2] != 92) {
            ++var2;
         } else {
            if (this.temp[var2 + 1] == 81) {
               break;
            }

            var2 += 2;
         }
      }

      if (var2 < var1 - 1) {
         int var3 = var2;
         var2 += 2;
         int[] var4 = new int[var3 + 3 * (var1 - var2) + 2];
         System.arraycopy(this.temp, 0, var4, 0, var3);
         boolean var5 = true;
         boolean var6 = true;

         while(true) {
            while(var2 < var1) {
               int var7 = this.temp[var2++];
               if (ASCII.isAscii(var7) && !ASCII.isAlpha(var7)) {
                  if (ASCII.isDigit(var7)) {
                     if (var6) {
                        var4[var3++] = 92;
                        var4[var3++] = 120;
                        var4[var3++] = 51;
                     }

                     var4[var3++] = var7;
                  } else if (var7 != 92) {
                     if (var5) {
                        var4[var3++] = 92;
                     }

                     var4[var3++] = var7;
                  } else if (var5) {
                     if (this.temp[var2] == 69) {
                        ++var2;
                        var5 = false;
                     } else {
                        var4[var3++] = 92;
                        var4[var3++] = 92;
                     }
                  } else {
                     if (this.temp[var2] == 81) {
                        ++var2;
                        var5 = true;
                        var6 = true;
                        continue;
                     }

                     var4[var3++] = var7;
                     if (var2 != var1) {
                        var4[var3++] = this.temp[var2++];
                     }
                  }
               } else {
                  var4[var3++] = var7;
               }

               var6 = false;
            }

            this.patternLength = var3;
            this.temp = Arrays.copyOf(var4, var3 + 2);
            return;
         }
      }
   }

   private void compile() {
      if (this.has(128) && !this.has(16)) {
         this.normalize();
      } else {
         this.normalizedPattern = this.pattern;
      }

      this.patternLength = this.normalizedPattern.length();
      this.temp = new int[this.patternLength + 2];
      this.hasSupplementary = false;
      int var2 = 0;

      int var1;
      for(int var3 = 0; var3 < this.patternLength; var3 += Character.charCount(var1)) {
         var1 = this.normalizedPattern.codePointAt(var3);
         if (isSupplementary(var1)) {
            this.hasSupplementary = true;
         }

         this.temp[var2++] = var1;
      }

      this.patternLength = var2;
      if (!this.has(16)) {
         this.RemoveQEQuoting();
      }

      this.buffer = new int[32];
      this.groupNodes = new Pattern.GroupHead[10];
      this.namedGroups = null;
      if (this.has(16)) {
         this.matchRoot = this.newSlice(this.temp, this.patternLength, this.hasSupplementary);
         this.matchRoot.next = lastAccept;
      } else {
         this.matchRoot = this.expr(lastAccept);
         if (this.patternLength != this.cursor) {
            if (this.peek() == 41) {
               throw this.error("Unmatched closing ')'");
            }

            throw this.error("Unexpected internal error");
         }
      }

      if (this.matchRoot instanceof Pattern.Slice) {
         this.root = Pattern.BnM.optimize(this.matchRoot);
         if (this.root == this.matchRoot) {
            this.root = (Pattern.Node)(this.hasSupplementary ? new Pattern.StartS(this.matchRoot) : new Pattern.Start(this.matchRoot));
         }
      } else if (!(this.matchRoot instanceof Pattern.Begin) && !(this.matchRoot instanceof Pattern.First)) {
         this.root = (Pattern.Node)(this.hasSupplementary ? new Pattern.StartS(this.matchRoot) : new Pattern.Start(this.matchRoot));
      } else {
         this.root = this.matchRoot;
      }

      this.temp = null;
      this.buffer = null;
      this.groupNodes = null;
      this.patternLength = 0;
      this.compiled = true;
   }

   Map<String, Integer> namedGroups() {
      if (this.namedGroups == null) {
         this.namedGroups = new HashMap(2);
      }

      return this.namedGroups;
   }

   private static void printObjectTree(Pattern.Node var0) {
      while(var0 != null) {
         if (var0 instanceof Pattern.Prolog) {
            System.out.println((Object)var0);
            printObjectTree(((Pattern.Prolog)var0).loop);
            System.out.println("**** end contents prolog loop");
         } else if (var0 instanceof Pattern.Loop) {
            System.out.println((Object)var0);
            printObjectTree(((Pattern.Loop)var0).body);
            System.out.println("**** end contents Loop body");
         } else if (var0 instanceof Pattern.Curly) {
            System.out.println((Object)var0);
            printObjectTree(((Pattern.Curly)var0).atom);
            System.out.println("**** end contents Curly body");
         } else if (var0 instanceof Pattern.GroupCurly) {
            System.out.println((Object)var0);
            printObjectTree(((Pattern.GroupCurly)var0).atom);
            System.out.println("**** end contents GroupCurly body");
         } else {
            if (var0 instanceof Pattern.GroupTail) {
               System.out.println((Object)var0);
               System.out.println("Tail next is " + var0.next);
               return;
            }

            System.out.println((Object)var0);
         }

         var0 = var0.next;
         if (var0 != null) {
            System.out.println("->next:");
         }

         if (var0 == accept) {
            System.out.println("Accept Node");
            var0 = null;
         }
      }

   }

   private boolean has(int var1) {
      return (this.flags & var1) != 0;
   }

   private void accept(int var1, String var2) {
      int var3 = this.temp[this.cursor++];
      if (this.has(4)) {
         var3 = this.parsePastWhitespace(var3);
      }

      if (var1 != var3) {
         throw this.error(var2);
      }
   }

   private void mark(int var1) {
      this.temp[this.patternLength] = var1;
   }

   private int peek() {
      int var1 = this.temp[this.cursor];
      if (this.has(4)) {
         var1 = this.peekPastWhitespace(var1);
      }

      return var1;
   }

   private int read() {
      int var1 = this.temp[this.cursor++];
      if (this.has(4)) {
         var1 = this.parsePastWhitespace(var1);
      }

      return var1;
   }

   private int readEscaped() {
      int var1 = this.temp[this.cursor++];
      return var1;
   }

   private int next() {
      int var1 = this.temp[++this.cursor];
      if (this.has(4)) {
         var1 = this.peekPastWhitespace(var1);
      }

      return var1;
   }

   private int nextEscaped() {
      int var1 = this.temp[++this.cursor];
      return var1;
   }

   private int peekPastWhitespace(int var1) {
      while(ASCII.isSpace(var1) || var1 == 35) {
         while(ASCII.isSpace(var1)) {
            var1 = this.temp[++this.cursor];
         }

         if (var1 == 35) {
            var1 = this.peekPastLine();
         }
      }

      return var1;
   }

   private int parsePastWhitespace(int var1) {
      while(ASCII.isSpace(var1) || var1 == 35) {
         while(ASCII.isSpace(var1)) {
            var1 = this.temp[this.cursor++];
         }

         if (var1 == 35) {
            var1 = this.parsePastLine();
         }
      }

      return var1;
   }

   private int parsePastLine() {
      int var1;
      for(var1 = this.temp[this.cursor++]; var1 != 0 && !this.isLineSeparator(var1); var1 = this.temp[this.cursor++]) {
      }

      return var1;
   }

   private int peekPastLine() {
      int var1;
      for(var1 = this.temp[++this.cursor]; var1 != 0 && !this.isLineSeparator(var1); var1 = this.temp[++this.cursor]) {
      }

      return var1;
   }

   private boolean isLineSeparator(int var1) {
      if (this.has(1)) {
         return var1 == 10;
      } else {
         return var1 == 10 || var1 == 13 || (var1 | 1) == 8233 || var1 == 133;
      }
   }

   private int skip() {
      int var1 = this.cursor;
      int var2 = this.temp[var1 + 1];
      this.cursor = var1 + 2;
      return var2;
   }

   private void unread() {
      --this.cursor;
   }

   private PatternSyntaxException error(String var1) {
      return new PatternSyntaxException(var1, this.normalizedPattern, this.cursor - 1);
   }

   private boolean findSupplementary(int var1, int var2) {
      for(int var3 = var1; var3 < var2; ++var3) {
         if (isSupplementary(this.temp[var3])) {
            return true;
         }
      }

      return false;
   }

   private static final boolean isSupplementary(int var0) {
      return var0 >= 65536 || Character.isSurrogate((char)var0);
   }

   private Pattern.Node expr(Pattern.Node var1) {
      Object var2 = null;
      Pattern.Node var3 = null;
      Pattern.Branch var4 = null;
      Pattern.BranchConn var5 = null;

      while(true) {
         Pattern.Node var6 = this.sequence(var1);
         Pattern.Node var7 = this.root;
         if (var2 == null) {
            var2 = var6;
            var3 = var7;
         } else {
            if (var5 == null) {
               var5 = new Pattern.BranchConn();
               var5.next = var1;
            }

            if (var6 == var1) {
               var6 = null;
            } else {
               var7.next = var5;
            }

            if (var2 == var4) {
               var4.add(var6);
            } else {
               if (var2 == var1) {
                  var2 = null;
               } else {
                  var3.next = var5;
               }

               var2 = var4 = new Pattern.Branch((Pattern.Node)var2, var6, var5);
            }
         }

         if (this.peek() != 124) {
            return (Pattern.Node)var2;
         }

         this.next();
      }
   }

   private Pattern.Node sequence(Pattern.Node var1) {
      Pattern.Node var2 = null;
      Pattern.Node var3 = null;
      Object var4 = null;

      label75:
      while(true) {
         int var5 = this.peek();
         Pattern.Node var8;
         switch(var5) {
         case 0:
            if (this.cursor >= this.patternLength) {
               break label75;
            }
         default:
            var4 = this.atom();
            break;
         case 36:
            this.next();
            if (this.has(1)) {
               var4 = new Pattern.UnixDollar(this.has(8));
            } else {
               var4 = new Pattern.Dollar(this.has(8));
            }
            break;
         case 40:
            var8 = this.group0();
            if (var8 != null) {
               if (var2 == null) {
                  var2 = var8;
               } else {
                  var3.next = var8;
               }

               var3 = this.root;
            }
            continue;
         case 41:
         case 124:
            break label75;
         case 42:
         case 43:
         case 63:
            this.next();
            throw this.error("Dangling meta character '" + (char)var5 + "'");
         case 46:
            this.next();
            if (this.has(32)) {
               var4 = new Pattern.All();
            } else if (this.has(1)) {
               var4 = new Pattern.UnixDot();
            } else {
               var4 = new Pattern.Dot();
            }
            break;
         case 91:
            var4 = this.clazz(true);
            break;
         case 92:
            var5 = this.nextEscaped();
            if (var5 != 112 && var5 != 80) {
               this.unread();
               var4 = this.atom();
            } else {
               boolean var6 = true;
               boolean var7 = var5 == 80;
               var5 = this.next();
               if (var5 != 123) {
                  this.unread();
               } else {
                  var6 = false;
               }

               var4 = this.family(var6, var7);
            }
            break;
         case 93:
         case 125:
            var4 = this.atom();
            break;
         case 94:
            this.next();
            if (this.has(8)) {
               if (this.has(1)) {
                  var4 = new Pattern.UnixCaret();
               } else {
                  var4 = new Pattern.Caret();
               }
            } else {
               var4 = new Pattern.Begin();
            }
         }

         var8 = this.closure((Pattern.Node)var4);
         if (var2 == null) {
            var3 = var8;
            var2 = var8;
         } else {
            var3.next = var8;
            var3 = var8;
         }
      }

      if (var2 == null) {
         return var1;
      } else {
         var3.next = var1;
         this.root = var3;
         return var2;
      }
   }

   private Pattern.Node atom() {
      int var1 = 0;
      int var2 = -1;
      boolean var3 = false;
      int var4 = this.peek();

      while(true) {
         switch(var4) {
         case 0:
            if (this.cursor >= this.patternLength) {
               return (Pattern.Node)(var1 == 1 ? this.newSingle(this.buffer[0]) : this.newSlice(this.buffer, var1, var3));
            }
         default:
            var2 = this.cursor;
            this.append(var4, var1);
            ++var1;
            if (isSupplementary(var4)) {
               var3 = true;
            }

            var4 = this.next();
            break;
         case 36:
         case 40:
         case 41:
         case 46:
         case 91:
         case 94:
         case 124:
            return (Pattern.Node)(var1 == 1 ? this.newSingle(this.buffer[0]) : this.newSlice(this.buffer, var1, var3));
         case 42:
         case 43:
         case 63:
         case 123:
            if (var1 > 1) {
               this.cursor = var2;
               --var1;
            }

            return (Pattern.Node)(var1 == 1 ? this.newSingle(this.buffer[0]) : this.newSlice(this.buffer, var1, var3));
         case 92:
            var4 = this.nextEscaped();
            if (var4 != 112 && var4 != 80) {
               this.unread();
               var2 = this.cursor;
               var4 = this.escape(false, var1 == 0, false);
               if (var4 >= 0) {
                  this.append(var4, var1);
                  ++var1;
                  if (isSupplementary(var4)) {
                     var3 = true;
                  }

                  var4 = this.peek();
                  continue;
               }

               if (var1 == 0) {
                  return this.root;
               }

               this.cursor = var2;
               return (Pattern.Node)(var1 == 1 ? this.newSingle(this.buffer[0]) : this.newSlice(this.buffer, var1, var3));
            }

            if (var1 <= 0) {
               boolean var5 = var4 == 80;
               boolean var6 = true;
               var4 = this.next();
               if (var4 != 123) {
                  this.unread();
               } else {
                  var6 = false;
               }

               return this.family(var6, var5);
            }

            this.unread();
            return (Pattern.Node)(var1 == 1 ? this.newSingle(this.buffer[0]) : this.newSlice(this.buffer, var1, var3));
         }
      }
   }

   private void append(int var1, int var2) {
      if (var2 >= this.buffer.length) {
         int[] var3 = new int[var2 + var2];
         System.arraycopy(this.buffer, 0, var3, 0, var2);
         this.buffer = var3;
      }

      this.buffer[var2] = var1;
   }

   private Pattern.Node ref(int var1) {
      boolean var2 = false;

      while(!var2) {
         int var3 = this.peek();
         switch(var3) {
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            int var4 = var1 * 10 + (var3 - 48);
            if (this.capturingGroupCount - 1 < var4) {
               var2 = true;
            } else {
               var1 = var4;
               this.read();
            }
            break;
         default:
            var2 = true;
         }
      }

      if (this.has(2)) {
         return new Pattern.CIBackRef(var1, this.has(64));
      } else {
         return new Pattern.BackRef(var1);
      }
   }

   private int escape(boolean var1, boolean var2, boolean var3) {
      int var4 = this.skip();
      switch(var4) {
      case 48:
         return this.o();
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
         if (!var1) {
            if (var2) {
               this.root = this.ref(var4 - 48);
            }

            return -1;
         }
         break;
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 91:
      case 92:
      case 93:
      case 94:
      case 95:
      case 96:
      default:
         return var4;
      case 65:
         if (!var1) {
            if (var2) {
               this.root = new Pattern.Begin();
            }

            return -1;
         }
         break;
      case 66:
         if (!var1) {
            if (var2) {
               this.root = new Pattern.Bound(Pattern.Bound.NONE, this.has(256));
            }

            return -1;
         }
      case 67:
      case 69:
      case 70:
      case 73:
      case 74:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
      case 84:
      case 85:
      case 88:
      case 89:
      case 103:
      case 105:
      case 106:
      case 108:
      case 109:
      case 111:
      case 112:
      case 113:
      case 121:
         break;
      case 68:
         if (var2) {
            this.root = this.has(256) ? (new Pattern.Utype(UnicodeProp.DIGIT)).complement() : (new Pattern.Ctype(1024)).complement();
         }

         return -1;
      case 71:
         if (!var1) {
            if (var2) {
               this.root = new Pattern.LastMatch();
            }

            return -1;
         }
         break;
      case 72:
         if (var2) {
            this.root = (new Pattern.HorizWS()).complement();
         }

         return -1;
      case 82:
         if (!var1) {
            if (var2) {
               this.root = new Pattern.LineEnding();
            }

            return -1;
         }
         break;
      case 83:
         if (var2) {
            this.root = this.has(256) ? (new Pattern.Utype(UnicodeProp.WHITE_SPACE)).complement() : (new Pattern.Ctype(2048)).complement();
         }

         return -1;
      case 86:
         if (var2) {
            this.root = (new Pattern.VertWS()).complement();
         }

         return -1;
      case 87:
         if (var2) {
            this.root = this.has(256) ? (new Pattern.Utype(UnicodeProp.WORD)).complement() : (new Pattern.Ctype(67328)).complement();
         }

         return -1;
      case 90:
         if (!var1) {
            if (var2) {
               if (this.has(1)) {
                  this.root = new Pattern.UnixDollar(false);
               } else {
                  this.root = new Pattern.Dollar(false);
               }
            }

            return -1;
         }
         break;
      case 97:
         return 7;
      case 98:
         if (!var1) {
            if (var2) {
               this.root = new Pattern.Bound(Pattern.Bound.BOTH, this.has(256));
            }

            return -1;
         }
         break;
      case 99:
         return this.c();
      case 100:
         if (var2) {
            this.root = (Pattern.Node)(this.has(256) ? new Pattern.Utype(UnicodeProp.DIGIT) : new Pattern.Ctype(1024));
         }

         return -1;
      case 101:
         return 27;
      case 102:
         return 12;
      case 104:
         if (var2) {
            this.root = new Pattern.HorizWS();
         }

         return -1;
      case 107:
         if (!var1) {
            if (this.read() != 60) {
               throw this.error("\\k is not followed by '<' for named capturing group");
            }

            String var5 = this.groupname(this.read());
            if (!this.namedGroups().containsKey(var5)) {
               throw this.error("(named capturing group <" + var5 + "> does not exit");
            }

            if (var2) {
               if (this.has(2)) {
                  this.root = new Pattern.CIBackRef((Integer)this.namedGroups().get(var5), this.has(64));
               } else {
                  this.root = new Pattern.BackRef((Integer)this.namedGroups().get(var5));
               }
            }

            return -1;
         }
         break;
      case 110:
         return 10;
      case 114:
         return 13;
      case 115:
         if (var2) {
            this.root = (Pattern.Node)(this.has(256) ? new Pattern.Utype(UnicodeProp.WHITE_SPACE) : new Pattern.Ctype(2048));
         }

         return -1;
      case 116:
         return 9;
      case 117:
         return this.u();
      case 118:
         if (var3) {
            return 11;
         }

         if (var2) {
            this.root = new Pattern.VertWS();
         }

         return -1;
      case 119:
         if (var2) {
            this.root = (Pattern.Node)(this.has(256) ? new Pattern.Utype(UnicodeProp.WORD) : new Pattern.Ctype(67328));
         }

         return -1;
      case 120:
         return this.x();
      case 122:
         if (!var1) {
            if (var2) {
               this.root = new Pattern.End();
            }

            return -1;
         }
      }

      throw this.error("Illegal/unsupported escape sequence");
   }

   private Pattern.CharProperty clazz(boolean var1) {
      Pattern.CharProperty var2 = null;
      Pattern.CharProperty var3 = null;
      Pattern.BitClass var4 = new Pattern.BitClass();
      boolean var5 = true;
      boolean var6 = true;
      int var7 = this.next();

      while(true) {
         while(true) {
            switch(var7) {
            case 0:
               var6 = false;
               if (this.cursor >= this.patternLength) {
                  throw this.error("Unclosed character class");
               }
               break;
            case 38:
               var6 = false;
               var7 = this.next();
               if (var7 == 38) {
                  var7 = this.next();

                  Pattern.CharProperty var8;
                  for(var8 = null; var7 != 93 && var7 != 38; var7 = this.peek()) {
                     if (var7 == 91) {
                        if (var8 == null) {
                           var8 = this.clazz(true);
                        } else {
                           var8 = union(var8, this.clazz(true));
                        }
                     } else {
                        this.unread();
                        var8 = this.clazz(false);
                     }
                  }

                  if (var8 != null) {
                     var3 = var8;
                  }

                  if (var2 == null) {
                     if (var8 == null) {
                        throw this.error("Bad class syntax");
                     }

                     var2 = var8;
                  } else {
                     var2 = intersection(var2, var3);
                  }
                  continue;
               }

               this.unread();
               break;
            case 91:
               var6 = false;
               var3 = this.clazz(true);
               if (var2 == null) {
                  var2 = var3;
               } else {
                  var2 = union(var2, var3);
               }

               var7 = this.peek();
               continue;
            case 93:
               var6 = false;
               if (var2 != null) {
                  if (var1) {
                     this.next();
                  }

                  return var2;
               }
               break;
            case 94:
               if (var6 && this.temp[this.cursor - 1] == 91) {
                  var7 = this.next();
                  var5 = !var5;
                  continue;
               }
               break;
            default:
               var6 = false;
            }

            var3 = this.range(var4);
            if (var5) {
               if (var2 == null) {
                  var2 = var3;
               } else if (var2 != var3) {
                  var2 = union(var2, var3);
               }
            } else if (var2 == null) {
               var2 = var3.complement();
            } else if (var2 != var3) {
               var2 = setDifference(var2, var3);
            }

            var7 = this.peek();
         }
      }
   }

   private Pattern.CharProperty bitsOrSingle(Pattern.BitClass var1, int var2) {
      return (Pattern.CharProperty)(var2 >= 256 || this.has(2) && this.has(64) && (var2 == 255 || var2 == 181 || var2 == 73 || var2 == 105 || var2 == 83 || var2 == 115 || var2 == 75 || var2 == 107 || var2 == 197 || var2 == 229) ? this.newSingle(var2) : var1.add(var2, this.flags()));
   }

   private Pattern.CharProperty range(Pattern.BitClass var1) {
      int var2 = this.peek();
      if (var2 == 92) {
         var2 = this.nextEscaped();
         boolean var3;
         if (var2 == 112 || var2 == 80) {
            var3 = var2 == 80;
            boolean var6 = true;
            var2 = this.next();
            if (var2 != 123) {
               this.unread();
            } else {
               var6 = false;
            }

            return this.family(var6, var3);
         }

         var3 = this.temp[this.cursor + 1] == 45;
         this.unread();
         var2 = this.escape(true, true, var3);
         if (var2 == -1) {
            return (Pattern.CharProperty)this.root;
         }
      } else {
         this.next();
      }

      if (var2 >= 0) {
         if (this.peek() == 45) {
            int var5 = this.temp[this.cursor + 1];
            if (var5 == 91) {
               return this.bitsOrSingle(var1, var2);
            }

            if (var5 != 93) {
               this.next();
               int var4 = this.peek();
               if (var4 == 92) {
                  var4 = this.escape(true, false, true);
               } else {
                  this.next();
               }

               if (var4 < var2) {
                  throw this.error("Illegal character range");
               }

               if (this.has(2)) {
                  return this.caseInsensitiveRangeFor(var2, var4);
               }

               return rangeFor(var2, var4);
            }
         }

         return this.bitsOrSingle(var1, var2);
      } else {
         throw this.error("Unexpected character '" + (char)var2 + "'");
      }
   }

   private Pattern.CharProperty family(boolean var1, boolean var2) {
      this.next();
      Object var4 = null;
      String var3;
      int var5;
      if (var1) {
         var5 = this.temp[this.cursor];
         if (!Character.isSupplementaryCodePoint(var5)) {
            var3 = String.valueOf((char)var5);
         } else {
            var3 = new String(this.temp, this.cursor, 1);
         }

         this.read();
      } else {
         var5 = this.cursor;
         this.mark(125);

         while(true) {
            if (this.read() == 125) {
               this.mark(0);
               int var6 = this.cursor;
               if (var6 > this.patternLength) {
                  throw this.error("Unclosed character family");
               }

               if (var5 + 1 >= var6) {
                  throw this.error("Empty character family");
               }

               var3 = new String(this.temp, var5, var6 - var5 - 1);
               break;
            }
         }
      }

      var5 = var3.indexOf(61);
      if (var5 != -1) {
         String var7 = var3.substring(var5 + 1);
         var3 = var3.substring(0, var5).toLowerCase(Locale.ENGLISH);
         if (!"sc".equals(var3) && !"script".equals(var3)) {
            if (!"blk".equals(var3) && !"block".equals(var3)) {
               if (!"gc".equals(var3) && !"general_category".equals(var3)) {
                  throw this.error("Unknown Unicode property {name=<" + var3 + ">, value=<" + var7 + ">}");
               }

               var4 = this.charPropertyNodeFor(var7);
            } else {
               var4 = this.unicodeBlockPropertyFor(var7);
            }
         } else {
            var4 = this.unicodeScriptPropertyFor(var7);
         }
      } else if (var3.startsWith("In")) {
         var4 = this.unicodeBlockPropertyFor(var3.substring(2));
      } else {
         UnicodeProp var8;
         if (var3.startsWith("Is")) {
            var3 = var3.substring(2);
            var8 = UnicodeProp.forName(var3);
            if (var8 != null) {
               var4 = new Pattern.Utype(var8);
            }

            if (var4 == null) {
               var4 = Pattern.CharPropertyNames.charPropertyFor(var3);
            }

            if (var4 == null) {
               var4 = this.unicodeScriptPropertyFor(var3);
            }
         } else {
            if (this.has(256)) {
               var8 = UnicodeProp.forPOSIXName(var3);
               if (var8 != null) {
                  var4 = new Pattern.Utype(var8);
               }
            }

            if (var4 == null) {
               var4 = this.charPropertyNodeFor(var3);
            }
         }
      }

      if (var2) {
         if (var4 instanceof Pattern.Category || var4 instanceof Pattern.Block) {
            this.hasSupplementary = true;
         }

         var4 = ((Pattern.CharProperty)var4).complement();
      }

      return (Pattern.CharProperty)var4;
   }

   private Pattern.CharProperty unicodeScriptPropertyFor(String var1) {
      Character.UnicodeScript var2;
      try {
         var2 = Character.UnicodeScript.forName(var1);
      } catch (IllegalArgumentException var4) {
         throw this.error("Unknown character script name {" + var1 + "}");
      }

      return new Pattern.Script(var2);
   }

   private Pattern.CharProperty unicodeBlockPropertyFor(String var1) {
      Character.UnicodeBlock var2;
      try {
         var2 = Character.UnicodeBlock.forName(var1);
      } catch (IllegalArgumentException var4) {
         throw this.error("Unknown character block name {" + var1 + "}");
      }

      return new Pattern.Block(var2);
   }

   private Pattern.CharProperty charPropertyNodeFor(String var1) {
      Pattern.CharProperty var2 = Pattern.CharPropertyNames.charPropertyFor(var1);
      if (var2 == null) {
         throw this.error("Unknown character property name {" + var1 + "}");
      } else {
         return var2;
      }
   }

   private String groupname(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(Character.toChars(var1));

      while(ASCII.isLower(var1 = this.read()) || ASCII.isUpper(var1) || ASCII.isDigit(var1)) {
         var2.append(Character.toChars(var1));
      }

      if (var2.length() == 0) {
         throw this.error("named capturing group has 0 length name");
      } else if (var1 != 62) {
         throw this.error("named capturing group is missing trailing '>'");
      } else {
         return var2.toString();
      }
   }

   private Pattern.Node group0() {
      boolean var1 = false;
      Pattern.Node var2 = null;
      Pattern.Node var3 = null;
      int var4 = this.flags;
      this.root = null;
      int var5 = this.next();
      Object var12;
      Object var13;
      if (var5 == 63) {
         var5 = this.skip();
         switch(var5) {
         case 33:
         case 61:
            var2 = this.createGroup(true);
            var3 = this.root;
            var2.next = this.expr(var3);
            if (var5 == 61) {
               var12 = var13 = new Pattern.Pos(var2);
            } else {
               var12 = var13 = new Pattern.Neg(var2);
            }
            break;
         case 36:
         case 64:
            throw this.error("Unknown group type");
         case 58:
            var12 = this.createGroup(true);
            var13 = this.root;
            ((Pattern.Node)var12).next = this.expr((Pattern.Node)var13);
            break;
         case 60:
            var5 = this.read();
            if (!ASCII.isLower(var5) && !ASCII.isUpper(var5)) {
               int var14 = this.cursor;
               var2 = this.createGroup(true);
               var3 = this.root;
               var2.next = this.expr(var3);
               var3.next = lookbehindEnd;
               Pattern.TreeInfo var7 = new Pattern.TreeInfo();
               var2.study(var7);
               if (!var7.maxValid) {
                  throw this.error("Look-behind group does not have an obvious maximum length");
               }

               boolean var8 = this.findSupplementary(var14, this.patternLength);
               Object var10000;
               if (var5 == 61) {
                  var10000 = var8 ? new Pattern.BehindS(var2, var7.maxLength, var7.minLength) : new Pattern.Behind(var2, var7.maxLength, var7.minLength);
                  var13 = var10000;
                  var12 = var10000;
               } else {
                  if (var5 != 33) {
                     throw this.error("Unknown look-behind group");
                  }

                  var10000 = var8 ? new Pattern.NotBehindS(var2, var7.maxLength, var7.minLength) : new Pattern.NotBehind(var2, var7.maxLength, var7.minLength);
                  var13 = var10000;
                  var12 = var10000;
               }
            } else {
               String var6 = this.groupname(var5);
               if (this.namedGroups().containsKey(var6)) {
                  throw this.error("Named capturing group <" + var6 + "> is already defined");
               }

               var1 = true;
               var12 = this.createGroup(false);
               var13 = this.root;
               this.namedGroups().put(var6, this.capturingGroupCount - 1);
               ((Pattern.Node)var12).next = this.expr((Pattern.Node)var13);
            }
            break;
         case 62:
            var2 = this.createGroup(true);
            var3 = this.root;
            var2.next = this.expr(var3);
            var12 = var13 = new Pattern.Ques(var2, 3);
            break;
         default:
            this.unread();
            this.addFlag();
            var5 = this.read();
            if (var5 == 41) {
               return null;
            }

            if (var5 != 58) {
               throw this.error("Unknown inline modifier");
            }

            var12 = this.createGroup(true);
            var13 = this.root;
            ((Pattern.Node)var12).next = this.expr((Pattern.Node)var13);
         }
      } else {
         var1 = true;
         var12 = this.createGroup(false);
         var13 = this.root;
         ((Pattern.Node)var12).next = this.expr((Pattern.Node)var13);
      }

      this.accept(41, "Unclosed group");
      this.flags = var4;
      Pattern.Node var15 = this.closure((Pattern.Node)var12);
      if (var15 == var12) {
         this.root = (Pattern.Node)var13;
         return var15;
      } else if (var12 == var13) {
         this.root = var15;
         return var15;
      } else if (var15 instanceof Pattern.Ques) {
         Pattern.Ques var18 = (Pattern.Ques)var15;
         if (var18.type == 2) {
            this.root = var15;
            return var15;
         } else {
            ((Pattern.Node)var13).next = new Pattern.BranchConn();
            var3 = ((Pattern.Node)var13).next;
            Pattern.Branch var16;
            if (var18.type == 0) {
               var16 = new Pattern.Branch((Pattern.Node)var12, (Pattern.Node)null, var3);
            } else {
               var16 = new Pattern.Branch((Pattern.Node)null, (Pattern.Node)var12, var3);
            }

            this.root = var3;
            return var16;
         }
      } else if (var15 instanceof Pattern.Curly) {
         Pattern.Curly var17 = (Pattern.Curly)var15;
         if (var17.type == 2) {
            this.root = var15;
            return var15;
         } else {
            Pattern.TreeInfo var19 = new Pattern.TreeInfo();
            if (((Pattern.Node)var12).study(var19)) {
               Pattern.GroupTail var20 = (Pattern.GroupTail)var13;
               var2 = this.root = new Pattern.GroupCurly(((Pattern.Node)var12).next, var17.cmin, var17.cmax, var17.type, ((Pattern.GroupTail)var13).localIndex, ((Pattern.GroupTail)var13).groupIndex, var1);
               return var2;
            } else {
               int var9 = ((Pattern.GroupHead)var12).localIndex;
               Object var10;
               if (var17.type == 0) {
                  var10 = new Pattern.Loop(this.localCount, var9);
               } else {
                  var10 = new Pattern.LazyLoop(this.localCount, var9);
               }

               Pattern.Prolog var11 = new Pattern.Prolog((Pattern.Loop)var10);
               ++this.localCount;
               ((Pattern.Loop)var10).cmin = var17.cmin;
               ((Pattern.Loop)var10).cmax = var17.cmax;
               ((Pattern.Loop)var10).body = (Pattern.Node)var12;
               ((Pattern.Node)var13).next = (Pattern.Node)var10;
               this.root = (Pattern.Node)var10;
               return var11;
            }
         }
      } else {
         throw this.error("Internal logic error");
      }
   }

   private Pattern.Node createGroup(boolean var1) {
      int var2 = this.localCount++;
      int var3 = 0;
      if (!var1) {
         var3 = this.capturingGroupCount++;
      }

      Pattern.GroupHead var4 = new Pattern.GroupHead(var2);
      this.root = new Pattern.GroupTail(var2, var3);
      if (!var1 && var3 < 10) {
         this.groupNodes[var3] = var4;
      }

      return var4;
   }

   private void addFlag() {
      int var1 = this.peek();

      while(true) {
         switch(var1) {
         case 45:
            var1 = this.next();
            this.subFlag();
         default:
            return;
         case 85:
            this.flags |= 320;
            break;
         case 99:
            this.flags |= 128;
            break;
         case 100:
            this.flags |= 1;
            break;
         case 105:
            this.flags |= 2;
            break;
         case 109:
            this.flags |= 8;
            break;
         case 115:
            this.flags |= 32;
            break;
         case 117:
            this.flags |= 64;
            break;
         case 120:
            this.flags |= 4;
         }

         var1 = this.next();
      }
   }

   private void subFlag() {
      int var1 = this.peek();

      while(true) {
         switch(var1) {
         case 85:
            this.flags &= -321;
         default:
            return;
         case 99:
            this.flags &= -129;
            break;
         case 100:
            this.flags &= -2;
            break;
         case 105:
            this.flags &= -3;
            break;
         case 109:
            this.flags &= -9;
            break;
         case 115:
            this.flags &= -33;
            break;
         case 117:
            this.flags &= -65;
            break;
         case 120:
            this.flags &= -5;
         }

         var1 = this.next();
      }
   }

   private Pattern.Node closure(Pattern.Node var1) {
      int var3 = this.peek();
      switch(var3) {
      case 42:
         var3 = this.next();
         if (var3 == 63) {
            this.next();
            return new Pattern.Curly(var1, 0, Integer.MAX_VALUE, 1);
         } else {
            if (var3 == 43) {
               this.next();
               return new Pattern.Curly(var1, 0, Integer.MAX_VALUE, 2);
            }

            return new Pattern.Curly(var1, 0, Integer.MAX_VALUE, 0);
         }
      case 43:
         var3 = this.next();
         if (var3 == 63) {
            this.next();
            return new Pattern.Curly(var1, 1, Integer.MAX_VALUE, 1);
         } else {
            if (var3 == 43) {
               this.next();
               return new Pattern.Curly(var1, 1, Integer.MAX_VALUE, 2);
            }

            return new Pattern.Curly(var1, 1, Integer.MAX_VALUE, 0);
         }
      case 63:
         var3 = this.next();
         if (var3 == 63) {
            this.next();
            return new Pattern.Ques(var1, 1);
         } else {
            if (var3 == 43) {
               this.next();
               return new Pattern.Ques(var1, 2);
            }

            return new Pattern.Ques(var1, 0);
         }
      case 123:
         var3 = this.temp[this.cursor + 1];
         if (!ASCII.isDigit(var3)) {
            throw this.error("Illegal repetition");
         } else {
            this.skip();
            int var4 = 0;

            do {
               var4 = var4 * 10 + (var3 - 48);
            } while(ASCII.isDigit(var3 = this.read()));

            int var5 = var4;
            if (var3 == 44) {
               var3 = this.read();
               var5 = Integer.MAX_VALUE;
               if (var3 != 125) {
                  for(var5 = 0; ASCII.isDigit(var3); var3 = this.read()) {
                     var5 = var5 * 10 + (var3 - 48);
                  }
               }
            }

            if (var3 != 125) {
               throw this.error("Unclosed counted closure");
            } else if ((var4 | var5 | var5 - var4) < 0) {
               throw this.error("Illegal repetition range");
            } else {
               var3 = this.peek();
               Pattern.Curly var6;
               if (var3 == 63) {
                  this.next();
                  var6 = new Pattern.Curly(var1, var4, var5, 1);
               } else if (var3 == 43) {
                  this.next();
                  var6 = new Pattern.Curly(var1, var4, var5, 2);
               } else {
                  var6 = new Pattern.Curly(var1, var4, var5, 0);
               }

               return var6;
            }
         }
      default:
         return var1;
      }
   }

   private int c() {
      if (this.cursor < this.patternLength) {
         return this.read() ^ 64;
      } else {
         throw this.error("Illegal control escape sequence");
      }
   }

   private int o() {
      int var1 = this.read();
      if ((var1 - 48 | 55 - var1) >= 0) {
         int var2 = this.read();
         if ((var2 - 48 | 55 - var2) >= 0) {
            int var3 = this.read();
            if ((var3 - 48 | 55 - var3) >= 0 && (var1 - 48 | 51 - var1) >= 0) {
               return (var1 - 48) * 64 + (var2 - 48) * 8 + (var3 - 48);
            } else {
               this.unread();
               return (var1 - 48) * 8 + (var2 - 48);
            }
         } else {
            this.unread();
            return var1 - 48;
         }
      } else {
         throw this.error("Illegal octal escape sequence");
      }
   }

   private int x() {
      int var1 = this.read();
      int var2;
      if (ASCII.isHexDigit(var1)) {
         var2 = this.read();
         if (ASCII.isHexDigit(var2)) {
            return ASCII.toDigit(var1) * 16 + ASCII.toDigit(var2);
         }
      } else if (var1 == 123 && ASCII.isHexDigit(this.peek())) {
         var2 = 0;

         do {
            if (!ASCII.isHexDigit(var1 = this.read())) {
               if (var1 != 125) {
                  throw this.error("Unclosed hexadecimal escape sequence");
               }

               return var2;
            }

            var2 = (var2 << 4) + ASCII.toDigit(var1);
         } while(var2 <= 1114111);

         throw this.error("Hexadecimal codepoint is too big");
      }

      throw this.error("Illegal hexadecimal escape sequence");
   }

   private int cursor() {
      return this.cursor;
   }

   private void setcursor(int var1) {
      this.cursor = var1;
   }

   private int uxxxx() {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         int var3 = this.read();
         if (!ASCII.isHexDigit(var3)) {
            throw this.error("Illegal Unicode escape sequence");
         }

         var1 = var1 * 16 + ASCII.toDigit(var3);
      }

      return var1;
   }

   private int u() {
      int var1 = this.uxxxx();
      if (Character.isHighSurrogate((char)var1)) {
         int var2 = this.cursor();
         if (this.read() == 92 && this.read() == 117) {
            int var3 = this.uxxxx();
            if (Character.isLowSurrogate((char)var3)) {
               return Character.toCodePoint((char)var1, (char)var3);
            }
         }

         this.setcursor(var2);
      }

      return var1;
   }

   private static final int countChars(CharSequence var0, int var1, int var2) {
      if (var2 == 1 && !Character.isHighSurrogate(var0.charAt(var1))) {
         assert var1 >= 0 && var1 < var0.length();

         return 1;
      } else {
         int var3 = var0.length();
         int var4 = var1;
         int var5;
         if (var2 >= 0) {
            assert var1 >= 0 && var1 < var3;

            for(var5 = 0; var4 < var3 && var5 < var2; ++var5) {
               if (Character.isHighSurrogate(var0.charAt(var4++)) && var4 < var3 && Character.isLowSurrogate(var0.charAt(var4))) {
                  ++var4;
               }
            }

            return var4 - var1;
         } else {
            assert var1 >= 0 && var1 <= var3;

            if (var1 == 0) {
               return 0;
            } else {
               var5 = -var2;

               for(int var6 = 0; var4 > 0 && var6 < var5; ++var6) {
                  --var4;
                  if (Character.isLowSurrogate(var0.charAt(var4)) && var4 > 0 && Character.isHighSurrogate(var0.charAt(var4 - 1))) {
                     --var4;
                  }
               }

               return var1 - var4;
            }
         }
      }
   }

   private static final int countCodePoints(CharSequence var0) {
      int var1 = var0.length();
      int var2 = 0;
      int var3 = 0;

      while(var3 < var1) {
         ++var2;
         if (Character.isHighSurrogate(var0.charAt(var3++)) && var3 < var1 && Character.isLowSurrogate(var0.charAt(var3))) {
            ++var3;
         }
      }

      return var2;
   }

   private Pattern.CharProperty newSingle(int var1) {
      if (this.has(2)) {
         int var2;
         int var3;
         if (this.has(64)) {
            var3 = Character.toUpperCase(var1);
            var2 = Character.toLowerCase(var3);
            if (var3 != var2) {
               return new Pattern.SingleU(var2);
            }
         } else if (ASCII.isAscii(var1)) {
            var2 = ASCII.toLower(var1);
            var3 = ASCII.toUpper(var1);
            if (var2 != var3) {
               return new Pattern.SingleI(var2, var3);
            }
         }
      }

      return (Pattern.CharProperty)(isSupplementary(var1) ? new Pattern.SingleS(var1) : new Pattern.Single(var1));
   }

   private Pattern.Node newSlice(int[] var1, int var2, boolean var3) {
      int[] var4 = new int[var2];
      int var5;
      if (this.has(2)) {
         if (this.has(64)) {
            for(var5 = 0; var5 < var2; ++var5) {
               var4[var5] = Character.toLowerCase(Character.toUpperCase(var1[var5]));
            }

            return (Pattern.Node)(var3 ? new Pattern.SliceUS(var4) : new Pattern.SliceU(var4));
         } else {
            for(var5 = 0; var5 < var2; ++var5) {
               var4[var5] = ASCII.toLower(var1[var5]);
            }

            return (Pattern.Node)(var3 ? new Pattern.SliceIS(var4) : new Pattern.SliceI(var4));
         }
      } else {
         for(var5 = 0; var5 < var2; ++var5) {
            var4[var5] = var1[var5];
         }

         return (Pattern.Node)(var3 ? new Pattern.SliceS(var4) : new Pattern.Slice(var4));
      }
   }

   private static boolean inRange(int var0, int var1, int var2) {
      return var0 <= var1 && var1 <= var2;
   }

   private static Pattern.CharProperty rangeFor(final int var0, final int var1) {
      return new Pattern.CharProperty() {
         boolean isSatisfiedBy(int var1x) {
            return Pattern.inRange(var0, var1x, var1);
         }
      };
   }

   private Pattern.CharProperty caseInsensitiveRangeFor(final int var1, final int var2) {
      return this.has(64) ? new Pattern.CharProperty() {
         boolean isSatisfiedBy(int var1x) {
            if (Pattern.inRange(var1, var1x, var2)) {
               return true;
            } else {
               int var2x = Character.toUpperCase(var1x);
               return Pattern.inRange(var1, var2x, var2) || Pattern.inRange(var1, Character.toLowerCase(var2x), var2);
            }
         }
      } : new Pattern.CharProperty() {
         boolean isSatisfiedBy(int var1x) {
            return Pattern.inRange(var1, var1x, var2) || ASCII.isAscii(var1x) && (Pattern.inRange(var1, ASCII.toUpper(var1x), var2) || Pattern.inRange(var1, ASCII.toLower(var1x), var2));
         }
      };
   }

   private static Pattern.CharProperty union(final Pattern.CharProperty var0, final Pattern.CharProperty var1) {
      return new Pattern.CharProperty() {
         boolean isSatisfiedBy(int var1x) {
            return var0.isSatisfiedBy(var1x) || var1.isSatisfiedBy(var1x);
         }
      };
   }

   private static Pattern.CharProperty intersection(final Pattern.CharProperty var0, final Pattern.CharProperty var1) {
      return new Pattern.CharProperty() {
         boolean isSatisfiedBy(int var1x) {
            return var0.isSatisfiedBy(var1x) && var1.isSatisfiedBy(var1x);
         }
      };
   }

   private static Pattern.CharProperty setDifference(final Pattern.CharProperty var0, final Pattern.CharProperty var1) {
      return new Pattern.CharProperty() {
         boolean isSatisfiedBy(int var1x) {
            return !var1.isSatisfiedBy(var1x) && var0.isSatisfiedBy(var1x);
         }
      };
   }

   private static boolean hasBaseCharacter(Matcher var0, int var1, CharSequence var2) {
      int var3 = !var0.transparentBounds ? var0.from : 0;

      for(int var4 = var1; var4 >= var3; --var4) {
         int var5 = Character.codePointAt(var2, var4);
         if (Character.isLetterOrDigit(var5)) {
            return true;
         }

         if (Character.getType(var5) != 6) {
            return false;
         }
      }

      return false;
   }

   public Predicate<String> asPredicate() {
      return (var1) -> {
         return this.matcher(var1).find();
      };
   }

   public Stream<String> splitAsStream(final CharSequence var1) {
      class MatcherIterator implements Iterator<String> {
         private final Matcher matcher = Pattern.this.matcher(var1);
         private int current;
         private String nextElement;
         private int emptyElementCount;

         public String next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else if (this.emptyElementCount == 0) {
               String var1x = this.nextElement;
               this.nextElement = null;
               return var1x;
            } else {
               --this.emptyElementCount;
               return "";
            }
         }

         public boolean hasNext() {
            if (this.nextElement == null && this.emptyElementCount <= 0) {
               if (this.current == var1.length()) {
                  return false;
               } else {
                  while(this.matcher.find()) {
                     this.nextElement = var1.subSequence(this.current, this.matcher.start()).toString();
                     this.current = this.matcher.end();
                     if (!this.nextElement.isEmpty()) {
                        return true;
                     }

                     if (this.current > 0) {
                        ++this.emptyElementCount;
                     }
                  }

                  this.nextElement = var1.subSequence(this.current, var1.length()).toString();
                  this.current = var1.length();
                  if (!this.nextElement.isEmpty()) {
                     return true;
                  } else {
                     this.emptyElementCount = 0;
                     this.nextElement = null;
                     return false;
                  }
               }
            } else {
               return true;
            }
         }
      }

      return StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator)(new MatcherIterator()), 272), false);
   }

   private static class CharPropertyNames {
      private static final HashMap<String, Pattern.CharPropertyNames.CharPropertyFactory> map = new HashMap();

      static Pattern.CharProperty charPropertyFor(String var0) {
         Pattern.CharPropertyNames.CharPropertyFactory var1 = (Pattern.CharPropertyNames.CharPropertyFactory)map.get(var0);
         return var1 == null ? null : var1.make();
      }

      private static void defCategory(String var0, final int var1) {
         map.put(var0, new Pattern.CharPropertyNames.CharPropertyFactory() {
            Pattern.CharProperty make() {
               return new Pattern.Category(var1);
            }
         });
      }

      private static void defRange(String var0, final int var1, final int var2) {
         map.put(var0, new Pattern.CharPropertyNames.CharPropertyFactory() {
            Pattern.CharProperty make() {
               return Pattern.rangeFor(var1, var2);
            }
         });
      }

      private static void defCtype(String var0, final int var1) {
         map.put(var0, new Pattern.CharPropertyNames.CharPropertyFactory() {
            Pattern.CharProperty make() {
               return new Pattern.Ctype(var1);
            }
         });
      }

      private static void defClone(String var0, final Pattern.CharPropertyNames.CloneableProperty var1) {
         map.put(var0, new Pattern.CharPropertyNames.CharPropertyFactory() {
            Pattern.CharProperty make() {
               return var1.clone();
            }
         });
      }

      static {
         defCategory("Cn", 1);
         defCategory("Lu", 2);
         defCategory("Ll", 4);
         defCategory("Lt", 8);
         defCategory("Lm", 16);
         defCategory("Lo", 32);
         defCategory("Mn", 64);
         defCategory("Me", 128);
         defCategory("Mc", 256);
         defCategory("Nd", 512);
         defCategory("Nl", 1024);
         defCategory("No", 2048);
         defCategory("Zs", 4096);
         defCategory("Zl", 8192);
         defCategory("Zp", 16384);
         defCategory("Cc", 32768);
         defCategory("Cf", 65536);
         defCategory("Co", 262144);
         defCategory("Cs", 524288);
         defCategory("Pd", 1048576);
         defCategory("Ps", 2097152);
         defCategory("Pe", 4194304);
         defCategory("Pc", 8388608);
         defCategory("Po", 16777216);
         defCategory("Sm", 33554432);
         defCategory("Sc", 67108864);
         defCategory("Sk", 134217728);
         defCategory("So", 268435456);
         defCategory("Pi", 536870912);
         defCategory("Pf", 1073741824);
         defCategory("L", 62);
         defCategory("M", 448);
         defCategory("N", 3584);
         defCategory("Z", 28672);
         defCategory("C", 884736);
         defCategory("P", 1643118592);
         defCategory("S", 503316480);
         defCategory("LC", 14);
         defCategory("LD", 574);
         defRange("L1", 0, 255);
         map.put("all", new Pattern.CharPropertyNames.CharPropertyFactory() {
            Pattern.CharProperty make() {
               return new Pattern.All();
            }
         });
         defRange("ASCII", 0, 127);
         defCtype("Alnum", 1792);
         defCtype("Alpha", 768);
         defCtype("Blank", 16384);
         defCtype("Cntrl", 8192);
         defRange("Digit", 48, 57);
         defCtype("Graph", 5888);
         defRange("Lower", 97, 122);
         defRange("Print", 32, 126);
         defCtype("Punct", 4096);
         defCtype("Space", 2048);
         defRange("Upper", 65, 90);
         defCtype("XDigit", 32768);
         defClone("javaLowerCase", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isLowerCase(var1);
            }
         });
         defClone("javaUpperCase", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isUpperCase(var1);
            }
         });
         defClone("javaAlphabetic", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isAlphabetic(var1);
            }
         });
         defClone("javaIdeographic", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isIdeographic(var1);
            }
         });
         defClone("javaTitleCase", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isTitleCase(var1);
            }
         });
         defClone("javaDigit", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isDigit(var1);
            }
         });
         defClone("javaDefined", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isDefined(var1);
            }
         });
         defClone("javaLetter", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isLetter(var1);
            }
         });
         defClone("javaLetterOrDigit", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isLetterOrDigit(var1);
            }
         });
         defClone("javaJavaIdentifierStart", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isJavaIdentifierStart(var1);
            }
         });
         defClone("javaJavaIdentifierPart", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isJavaIdentifierPart(var1);
            }
         });
         defClone("javaUnicodeIdentifierStart", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isUnicodeIdentifierStart(var1);
            }
         });
         defClone("javaUnicodeIdentifierPart", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isUnicodeIdentifierPart(var1);
            }
         });
         defClone("javaIdentifierIgnorable", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isIdentifierIgnorable(var1);
            }
         });
         defClone("javaSpaceChar", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isSpaceChar(var1);
            }
         });
         defClone("javaWhitespace", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isWhitespace(var1);
            }
         });
         defClone("javaISOControl", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isISOControl(var1);
            }
         });
         defClone("javaMirrored", new Pattern.CharPropertyNames.CloneableProperty() {
            boolean isSatisfiedBy(int var1) {
               return Character.isMirrored(var1);
            }
         });
      }

      private abstract static class CloneableProperty extends Pattern.CharProperty implements Cloneable {
         private CloneableProperty() {
            super(null);
         }

         public Pattern.CharPropertyNames.CloneableProperty clone() {
            try {
               return (Pattern.CharPropertyNames.CloneableProperty)super.clone();
            } catch (CloneNotSupportedException var2) {
               throw new AssertionError(var2);
            }
         }

         // $FF: synthetic method
         CloneableProperty(Object var1) {
            this();
         }
      }

      private abstract static class CharPropertyFactory {
         private CharPropertyFactory() {
         }

         abstract Pattern.CharProperty make();

         // $FF: synthetic method
         CharPropertyFactory(Object var1) {
            this();
         }
      }
   }

   static final class BnMS extends Pattern.BnM {
      int lengthInChars;

      BnMS(int[] var1, int[] var2, int[] var3, Pattern.Node var4) {
         super(var1, var2, var3, var4);

         for(int var5 = 0; var5 < this.buffer.length; ++var5) {
            this.lengthInChars += Character.charCount(this.buffer[var5]);
         }

      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var4.length;
         int var6 = var1.to - this.lengthInChars;

         while(true) {
            label26:
            while(var2 <= var6) {
               int var8 = Pattern.countChars(var3, var2, var5);

               for(int var9 = var5 - 1; var8 > 0; --var9) {
                  int var7 = Character.codePointBefore(var3, var2 + var8);
                  if (var7 != var4[var9]) {
                     int var10 = Math.max(var9 + 1 - this.lastOcc[var7 & 127], this.optoSft[var9]);
                     var2 += Pattern.countChars(var3, var2, var10);
                     continue label26;
                  }

                  var8 -= Character.charCount(var7);
               }

               var1.first = var2;
               boolean var11 = this.next.match(var1, var2 + this.lengthInChars, var3);
               if (var11) {
                  var1.first = var2;
                  var1.groups[0] = var1.first;
                  var1.groups[1] = var1.last;
                  return true;
               }

               var2 += Pattern.countChars(var3, var2, 1);
            }

            var1.hitEnd = true;
            return false;
         }
      }
   }

   static class BnM extends Pattern.Node {
      int[] buffer;
      int[] lastOcc;
      int[] optoSft;

      static Pattern.Node optimize(Pattern.Node var0) {
         if (!(var0 instanceof Pattern.Slice)) {
            return var0;
         } else {
            int[] var1 = ((Pattern.Slice)var0).buffer;
            int var2 = var1.length;
            if (var2 < 4) {
               return var0;
            } else {
               int[] var6 = new int[128];
               int[] var7 = new int[var2];

               int var3;
               for(var3 = 0; var3 < var2; ++var3) {
                  var6[var1[var3] & 127] = var3 + 1;
               }

               label42:
               for(var3 = var2; var3 > 0; --var3) {
                  int var4;
                  for(var4 = var2 - 1; var4 >= var3; --var4) {
                     if (var1[var4] != var1[var4 - var3]) {
                        continue label42;
                     }

                     var7[var4 - 1] = var3;
                  }

                  while(var4 > 0) {
                     --var4;
                     var7[var4] = var3;
                  }
               }

               var7[var2 - 1] = 1;
               if (var0 instanceof Pattern.SliceS) {
                  return new Pattern.BnMS(var1, var6, var7, var0.next);
               } else {
                  return new Pattern.BnM(var1, var6, var7, var0.next);
               }
            }
         }
      }

      BnM(int[] var1, int[] var2, int[] var3, Pattern.Node var4) {
         this.buffer = var1;
         this.lastOcc = var2;
         this.optoSft = var3;
         this.next = var4;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var4.length;
         int var6 = var1.to - var5;

         while(true) {
            label26:
            while(var2 <= var6) {
               for(int var7 = var5 - 1; var7 >= 0; --var7) {
                  char var8 = var3.charAt(var2 + var7);
                  if (var8 != var4[var7]) {
                     var2 += Math.max(var7 + 1 - this.lastOcc[var8 & 127], this.optoSft[var7]);
                     continue label26;
                  }
               }

               var1.first = var2;
               boolean var9 = this.next.match(var1, var2 + var5, var3);
               if (var9) {
                  var1.first = var2;
                  var1.groups[0] = var1.first;
                  var1.groups[1] = var1.last;
                  return true;
               }

               ++var2;
            }

            var1.hitEnd = true;
            return false;
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.minLength += this.buffer.length;
         var1.maxValid = false;
         return this.next.study(var1);
      }
   }

   static final class Bound extends Pattern.Node {
      static int LEFT = 1;
      static int RIGHT = 2;
      static int BOTH = 3;
      static int NONE = 4;
      int type;
      boolean useUWORD;

      Bound(int var1, boolean var2) {
         this.type = var1;
         this.useUWORD = var2;
      }

      boolean isWord(int var1) {
         return this.useUWORD ? UnicodeProp.WORD.is(var1) : var1 == 95 || Character.isLetterOrDigit(var1);
      }

      int check(Matcher var1, int var2, CharSequence var3) {
         boolean var5 = false;
         int var6 = var1.from;
         int var7 = var1.to;
         if (var1.transparentBounds) {
            var6 = 0;
            var7 = var1.getTextLength();
         }

         int var4;
         if (var2 > var6) {
            var4 = Character.codePointBefore(var3, var2);
            var5 = this.isWord(var4) || Character.getType(var4) == 6 && Pattern.hasBaseCharacter(var1, var2 - 1, var3);
         }

         boolean var8 = false;
         if (var2 < var7) {
            var4 = Character.codePointAt(var3, var2);
            var8 = this.isWord(var4) || Character.getType(var4) == 6 && Pattern.hasBaseCharacter(var1, var2, var3);
         } else {
            var1.hitEnd = true;
            var1.requireEnd = true;
         }

         return var5 ^ var8 ? (var8 ? LEFT : RIGHT) : NONE;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         return (this.check(var1, var2, var3) & this.type) > 0 && this.next.match(var1, var2, var3);
      }
   }

   static final class NotBehindS extends Pattern.NotBehind {
      NotBehindS(Pattern.Node var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = Pattern.countChars(var3, var2, -this.rmax);
         int var5 = Pattern.countChars(var3, var2, -this.rmin);
         int var6 = var1.from;
         int var7 = var1.lookbehindTo;
         boolean var8 = false;
         int var9 = !var1.transparentBounds ? var1.from : 0;
         int var10 = Math.max(var2 - var4, var9);
         var1.lookbehindTo = var2;
         if (var1.transparentBounds) {
            var1.from = 0;
         }

         for(int var11 = var2 - var5; !var8 && var11 >= var10; var11 -= var11 > var10 ? Pattern.countChars(var3, var11, -1) : 1) {
            var8 = this.cond.match(var1, var11, var3);
         }

         var1.from = var6;
         var1.lookbehindTo = var7;
         return !var8 && this.next.match(var1, var2, var3);
      }
   }

   static class NotBehind extends Pattern.Node {
      Pattern.Node cond;
      int rmax;
      int rmin;

      NotBehind(Pattern.Node var1, int var2, int var3) {
         this.cond = var1;
         this.rmax = var2;
         this.rmin = var3;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.lookbehindTo;
         int var5 = var1.from;
         boolean var6 = false;
         int var7 = !var1.transparentBounds ? var1.from : 0;
         int var8 = Math.max(var2 - this.rmax, var7);
         var1.lookbehindTo = var2;
         if (var1.transparentBounds) {
            var1.from = 0;
         }

         for(int var9 = var2 - this.rmin; !var6 && var9 >= var8; --var9) {
            var6 = this.cond.match(var1, var9, var3);
         }

         var1.from = var5;
         var1.lookbehindTo = var4;
         return !var6 && this.next.match(var1, var2, var3);
      }
   }

   static final class BehindS extends Pattern.Behind {
      BehindS(Pattern.Node var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = Pattern.countChars(var3, var2, -this.rmax);
         int var5 = Pattern.countChars(var3, var2, -this.rmin);
         int var6 = var1.from;
         int var7 = !var1.transparentBounds ? var1.from : 0;
         boolean var8 = false;
         int var9 = Math.max(var2 - var4, var7);
         int var10 = var1.lookbehindTo;
         var1.lookbehindTo = var2;
         if (var1.transparentBounds) {
            var1.from = 0;
         }

         for(int var11 = var2 - var5; !var8 && var11 >= var9; var11 -= var11 > var9 ? Pattern.countChars(var3, var11, -1) : 1) {
            var8 = this.cond.match(var1, var11, var3);
         }

         var1.from = var6;
         var1.lookbehindTo = var10;
         return var8 && this.next.match(var1, var2, var3);
      }
   }

   static class Behind extends Pattern.Node {
      Pattern.Node cond;
      int rmax;
      int rmin;

      Behind(Pattern.Node var1, int var2, int var3) {
         this.cond = var1;
         this.rmax = var2;
         this.rmin = var3;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.from;
         boolean var5 = false;
         int var6 = !var1.transparentBounds ? var1.from : 0;
         int var7 = Math.max(var2 - this.rmax, var6);
         int var8 = var1.lookbehindTo;
         var1.lookbehindTo = var2;
         if (var1.transparentBounds) {
            var1.from = 0;
         }

         for(int var9 = var2 - this.rmin; !var5 && var9 >= var7; --var9) {
            var5 = this.cond.match(var1, var9, var3);
         }

         var1.from = var4;
         var1.lookbehindTo = var8;
         return var5 && this.next.match(var1, var2, var3);
      }
   }

   static final class Neg extends Pattern.Node {
      Pattern.Node cond;

      Neg(Pattern.Node var1) {
         this.cond = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.to;
         boolean var5 = false;
         if (var1.transparentBounds) {
            var1.to = var1.getTextLength();
         }

         try {
            if (var2 < var1.to) {
               var5 = !this.cond.match(var1, var2, var3);
            } else {
               var1.requireEnd = true;
               var5 = !this.cond.match(var1, var2, var3);
            }
         } finally {
            var1.to = var4;
         }

         return var5 && this.next.match(var1, var2, var3);
      }
   }

   static final class Pos extends Pattern.Node {
      Pattern.Node cond;

      Pos(Pattern.Node var1) {
         this.cond = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.to;
         boolean var5 = false;
         if (var1.transparentBounds) {
            var1.to = var1.getTextLength();
         }

         try {
            var5 = this.cond.match(var1, var2, var3);
         } finally {
            var1.to = var4;
         }

         return var5 && this.next.match(var1, var2, var3);
      }
   }

   static final class Conditional extends Pattern.Node {
      Pattern.Node cond;
      Pattern.Node yes;
      Pattern.Node not;

      Conditional(Pattern.Node var1, Pattern.Node var2, Pattern.Node var3) {
         this.cond = var1;
         this.yes = var2;
         this.not = var3;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         return this.cond.match(var1, var2, var3) ? this.yes.match(var1, var2, var3) : this.not.match(var1, var2, var3);
      }

      boolean study(Pattern.TreeInfo var1) {
         int var2 = var1.minLength;
         int var3 = var1.maxLength;
         boolean var4 = var1.maxValid;
         var1.reset();
         this.yes.study(var1);
         int var5 = var1.minLength;
         int var6 = var1.maxLength;
         boolean var7 = var1.maxValid;
         var1.reset();
         this.not.study(var1);
         var1.minLength = var2 + Math.min(var5, var1.minLength);
         var1.maxLength = var3 + Math.max(var6, var1.maxLength);
         var1.maxValid &= var4 & var7;
         var1.deterministic = false;
         return this.next.study(var1);
      }
   }

   static final class First extends Pattern.Node {
      Pattern.Node atom;

      First(Pattern.Node var1) {
         this.atom = Pattern.BnM.optimize(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (this.atom instanceof Pattern.BnM) {
            return this.atom.match(var1, var2, var3) && this.next.match(var1, var1.last, var3);
         } else {
            while(var2 <= var1.to) {
               if (this.atom.match(var1, var2, var3)) {
                  return this.next.match(var1, var1.last, var3);
               }

               var2 += Pattern.countChars(var3, var2, 1);
               ++var1.first;
            }

            var1.hitEnd = true;
            return false;
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         this.atom.study(var1);
         var1.maxValid = false;
         var1.deterministic = false;
         return this.next.study(var1);
      }
   }

   static class CIBackRef extends Pattern.Node {
      int groupIndex;
      boolean doUnicodeCase;

      CIBackRef(int var1, boolean var2) {
         this.groupIndex = var1 + var1;
         this.doUnicodeCase = var2;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.groups[this.groupIndex];
         int var5 = var1.groups[this.groupIndex + 1];
         int var6 = var5 - var4;
         if (var4 < 0) {
            return false;
         } else if (var2 + var6 > var1.to) {
            var1.hitEnd = true;
            return false;
         } else {
            int var7 = var2;

            for(int var8 = 0; var8 < var6; ++var8) {
               int var9 = Character.codePointAt(var3, var7);
               int var10 = Character.codePointAt(var3, var4);
               if (var9 != var10) {
                  if (this.doUnicodeCase) {
                     int var11 = Character.toUpperCase(var9);
                     int var12 = Character.toUpperCase(var10);
                     if (var11 != var12 && Character.toLowerCase(var11) != Character.toLowerCase(var12)) {
                        return false;
                     }
                  } else if (ASCII.toLower(var9) != ASCII.toLower(var10)) {
                     return false;
                  }
               }

               var7 += Character.charCount(var9);
               var4 += Character.charCount(var10);
            }

            return this.next.match(var1, var2 + var6, var3);
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.maxValid = false;
         return this.next.study(var1);
      }
   }

   static class BackRef extends Pattern.Node {
      int groupIndex;

      BackRef(int var1) {
         this.groupIndex = var1 + var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.groups[this.groupIndex];
         int var5 = var1.groups[this.groupIndex + 1];
         int var6 = var5 - var4;
         if (var4 < 0) {
            return false;
         } else if (var2 + var6 > var1.to) {
            var1.hitEnd = true;
            return false;
         } else {
            for(int var7 = 0; var7 < var6; ++var7) {
               if (var3.charAt(var2 + var7) != var3.charAt(var4 + var7)) {
                  return false;
               }
            }

            return this.next.match(var1, var2 + var6, var3);
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.maxValid = false;
         return this.next.study(var1);
      }
   }

   static final class LazyLoop extends Pattern.Loop {
      LazyLoop(int var1, int var2) {
         super(var1, var2);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 > var1.locals[this.beginIndex]) {
            int var4 = var1.locals[this.countIndex];
            boolean var5;
            if (var4 < this.cmin) {
               var1.locals[this.countIndex] = var4 + 1;
               var5 = this.body.match(var1, var2, var3);
               if (!var5) {
                  var1.locals[this.countIndex] = var4;
               }

               return var5;
            } else if (this.next.match(var1, var2, var3)) {
               return true;
            } else if (var4 < this.cmax) {
               var1.locals[this.countIndex] = var4 + 1;
               var5 = this.body.match(var1, var2, var3);
               if (!var5) {
                  var1.locals[this.countIndex] = var4;
               }

               return var5;
            } else {
               return false;
            }
         } else {
            return this.next.match(var1, var2, var3);
         }
      }

      boolean matchInit(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.locals[this.countIndex];
         boolean var5 = false;
         if (0 < this.cmin) {
            var1.locals[this.countIndex] = 1;
            var5 = this.body.match(var1, var2, var3);
         } else if (this.next.match(var1, var2, var3)) {
            var5 = true;
         } else if (0 < this.cmax) {
            var1.locals[this.countIndex] = 1;
            var5 = this.body.match(var1, var2, var3);
         }

         var1.locals[this.countIndex] = var4;
         return var5;
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.maxValid = false;
         var1.deterministic = false;
         return false;
      }
   }

   static class Loop extends Pattern.Node {
      Pattern.Node body;
      int countIndex;
      int beginIndex;
      int cmin;
      int cmax;

      Loop(int var1, int var2) {
         this.countIndex = var1;
         this.beginIndex = var2;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 > var1.locals[this.beginIndex]) {
            int var4 = var1.locals[this.countIndex];
            boolean var5;
            if (var4 < this.cmin) {
               var1.locals[this.countIndex] = var4 + 1;
               var5 = this.body.match(var1, var2, var3);
               if (!var5) {
                  var1.locals[this.countIndex] = var4;
               }

               return var5;
            }

            if (var4 < this.cmax) {
               var1.locals[this.countIndex] = var4 + 1;
               var5 = this.body.match(var1, var2, var3);
               if (var5) {
                  return true;
               }

               var1.locals[this.countIndex] = var4;
            }
         }

         return this.next.match(var1, var2, var3);
      }

      boolean matchInit(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.locals[this.countIndex];
         boolean var5 = false;
         if (0 < this.cmin) {
            var1.locals[this.countIndex] = 1;
            var5 = this.body.match(var1, var2, var3);
         } else if (0 < this.cmax) {
            var1.locals[this.countIndex] = 1;
            var5 = this.body.match(var1, var2, var3);
            if (!var5) {
               var5 = this.next.match(var1, var2, var3);
            }
         } else {
            var5 = this.next.match(var1, var2, var3);
         }

         var1.locals[this.countIndex] = var4;
         return var5;
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.maxValid = false;
         var1.deterministic = false;
         return false;
      }
   }

   static final class Prolog extends Pattern.Node {
      Pattern.Loop loop;

      Prolog(Pattern.Loop var1) {
         this.loop = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         return this.loop.matchInit(var1, var2, var3);
      }

      boolean study(Pattern.TreeInfo var1) {
         return this.loop.study(var1);
      }
   }

   static final class GroupTail extends Pattern.Node {
      int localIndex;
      int groupIndex;

      GroupTail(int var1, int var2) {
         this.localIndex = var1;
         this.groupIndex = var2 + var2;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.locals[this.localIndex];
         if (var4 >= 0) {
            int var5 = var1.groups[this.groupIndex];
            int var6 = var1.groups[this.groupIndex + 1];
            var1.groups[this.groupIndex] = var4;
            var1.groups[this.groupIndex + 1] = var2;
            if (this.next.match(var1, var2, var3)) {
               return true;
            } else {
               var1.groups[this.groupIndex] = var5;
               var1.groups[this.groupIndex + 1] = var6;
               return false;
            }
         } else {
            var1.last = var2;
            return true;
         }
      }
   }

   static final class GroupRef extends Pattern.Node {
      Pattern.GroupHead head;

      GroupRef(Pattern.GroupHead var1) {
         this.head = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         return this.head.matchRef(var1, var2, var3) && this.next.match(var1, var1.last, var3);
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.maxValid = false;
         var1.deterministic = false;
         return this.next.study(var1);
      }
   }

   static final class GroupHead extends Pattern.Node {
      int localIndex;

      GroupHead(int var1) {
         this.localIndex = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.locals[this.localIndex];
         var1.locals[this.localIndex] = var2;
         boolean var5 = this.next.match(var1, var2, var3);
         var1.locals[this.localIndex] = var4;
         return var5;
      }

      boolean matchRef(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.locals[this.localIndex];
         var1.locals[this.localIndex] = ~var2;
         boolean var5 = this.next.match(var1, var2, var3);
         var1.locals[this.localIndex] = var4;
         return var5;
      }
   }

   static final class Branch extends Pattern.Node {
      Pattern.Node[] atoms = new Pattern.Node[2];
      int size = 2;
      Pattern.Node conn;

      Branch(Pattern.Node var1, Pattern.Node var2, Pattern.Node var3) {
         this.conn = var3;
         this.atoms[0] = var1;
         this.atoms[1] = var2;
      }

      void add(Pattern.Node var1) {
         if (this.size >= this.atoms.length) {
            Pattern.Node[] var2 = new Pattern.Node[this.atoms.length * 2];
            System.arraycopy(this.atoms, 0, var2, 0, this.atoms.length);
            this.atoms = var2;
         }

         this.atoms[this.size++] = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         for(int var4 = 0; var4 < this.size; ++var4) {
            if (this.atoms[var4] == null) {
               if (this.conn.next.match(var1, var2, var3)) {
                  return true;
               }
            } else if (this.atoms[var4].match(var1, var2, var3)) {
               return true;
            }
         }

         return false;
      }

      boolean study(Pattern.TreeInfo var1) {
         int var2 = var1.minLength;
         int var3 = var1.maxLength;
         boolean var4 = var1.maxValid;
         int var5 = Integer.MAX_VALUE;
         int var6 = -1;

         for(int var7 = 0; var7 < this.size; ++var7) {
            var1.reset();
            if (this.atoms[var7] != null) {
               this.atoms[var7].study(var1);
            }

            var5 = Math.min(var5, var1.minLength);
            var6 = Math.max(var6, var1.maxLength);
            var4 &= var1.maxValid;
         }

         var2 += var5;
         var3 += var6;
         var1.reset();
         this.conn.next.study(var1);
         var1.minLength += var2;
         var1.maxLength += var3;
         var1.maxValid &= var4;
         var1.deterministic = false;
         return false;
      }
   }

   static final class BranchConn extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         return this.next.match(var1, var2, var3);
      }

      boolean study(Pattern.TreeInfo var1) {
         return var1.deterministic;
      }
   }

   static final class GroupCurly extends Pattern.Node {
      Pattern.Node atom;
      int type;
      int cmin;
      int cmax;
      int localIndex;
      int groupIndex;
      boolean capture;

      GroupCurly(Pattern.Node var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
         this.atom = var1;
         this.type = var4;
         this.cmin = var2;
         this.cmax = var3;
         this.localIndex = var5;
         this.groupIndex = var6;
         this.capture = var7;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = var1.groups;
         int[] var5 = var1.locals;
         int var6 = var5[this.localIndex];
         int var7 = 0;
         int var8 = 0;
         if (this.capture) {
            var7 = var4[this.groupIndex];
            var8 = var4[this.groupIndex + 1];
         }

         var5[this.localIndex] = -1;
         boolean var9 = true;

         for(int var10 = 0; var10 < this.cmin; ++var10) {
            if (!this.atom.match(var1, var2, var3)) {
               var9 = false;
               break;
            }

            if (this.capture) {
               var4[this.groupIndex] = var2;
               var4[this.groupIndex + 1] = var1.last;
            }

            var2 = var1.last;
         }

         if (var9) {
            if (this.type == 0) {
               var9 = this.match0(var1, var2, this.cmin, var3);
            } else if (this.type == 1) {
               var9 = this.match1(var1, var2, this.cmin, var3);
            } else {
               var9 = this.match2(var1, var2, this.cmin, var3);
            }
         }

         if (!var9) {
            var5[this.localIndex] = var6;
            if (this.capture) {
               var4[this.groupIndex] = var7;
               var4[this.groupIndex + 1] = var8;
            }
         }

         return var9;
      }

      boolean match0(Matcher var1, int var2, int var3, CharSequence var4) {
         int var5 = var3;
         int[] var6 = var1.groups;
         int var7 = 0;
         int var8 = 0;
         if (this.capture) {
            var7 = var6[this.groupIndex];
            var8 = var6[this.groupIndex + 1];
         }

         if (var3 < this.cmax && this.atom.match(var1, var2, var4)) {
            int var9 = var1.last - var2;
            if (var9 <= 0) {
               if (this.capture) {
                  var6[this.groupIndex] = var2;
                  var6[this.groupIndex + 1] = var2 + var9;
               }

               var2 += var9;
            } else {
               while(true) {
                  if (this.capture) {
                     var6[this.groupIndex] = var2;
                     var6[this.groupIndex + 1] = var2 + var9;
                  }

                  var2 += var9;
                  ++var3;
                  if (var3 >= this.cmax || !this.atom.match(var1, var2, var4)) {
                     break;
                  }

                  if (var2 + var9 != var1.last) {
                     if (this.match0(var1, var2, var3, var4)) {
                        return true;
                     }
                     break;
                  }
               }

               for(; var3 > var5; --var3) {
                  if (this.next.match(var1, var2, var4)) {
                     if (this.capture) {
                        var6[this.groupIndex + 1] = var2;
                        var6[this.groupIndex] = var2 - var9;
                     }

                     return true;
                  }

                  var2 -= var9;
                  if (this.capture) {
                     var6[this.groupIndex + 1] = var2;
                     var6[this.groupIndex] = var2 - var9;
                  }
               }
            }
         }

         if (this.capture) {
            var6[this.groupIndex] = var7;
            var6[this.groupIndex + 1] = var8;
         }

         return this.next.match(var1, var2, var4);
      }

      boolean match1(Matcher var1, int var2, int var3, CharSequence var4) {
         while(!this.next.match(var1, var2, var4)) {
            if (var3 >= this.cmax) {
               return false;
            }

            if (!this.atom.match(var1, var2, var4)) {
               return false;
            }

            if (var2 == var1.last) {
               return false;
            }

            if (this.capture) {
               var1.groups[this.groupIndex] = var2;
               var1.groups[this.groupIndex + 1] = var1.last;
            }

            var2 = var1.last;
            ++var3;
         }

         return true;
      }

      boolean match2(Matcher var1, int var2, int var3, CharSequence var4) {
         while(true) {
            if (var3 < this.cmax && this.atom.match(var1, var2, var4)) {
               if (this.capture) {
                  var1.groups[this.groupIndex] = var2;
                  var1.groups[this.groupIndex + 1] = var1.last;
               }

               if (var2 != var1.last) {
                  var2 = var1.last;
                  ++var3;
                  continue;
               }
            }

            return this.next.match(var1, var2, var4);
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         int var2 = var1.minLength;
         int var3 = var1.maxLength;
         boolean var4 = var1.maxValid;
         boolean var5 = var1.deterministic;
         var1.reset();
         this.atom.study(var1);
         int var6 = var1.minLength * this.cmin + var2;
         if (var6 < var2) {
            var6 = 268435455;
         }

         var1.minLength = var6;
         if (var4 & var1.maxValid) {
            var6 = var1.maxLength * this.cmax + var3;
            var1.maxLength = var6;
            if (var6 < var3) {
               var1.maxValid = false;
            }
         } else {
            var1.maxValid = false;
         }

         if (var1.deterministic && this.cmin == this.cmax) {
            var1.deterministic = var5;
         } else {
            var1.deterministic = false;
         }

         return this.next.study(var1);
      }
   }

   static final class Curly extends Pattern.Node {
      Pattern.Node atom;
      int type;
      int cmin;
      int cmax;

      Curly(Pattern.Node var1, int var2, int var3, int var4) {
         this.atom = var1;
         this.type = var4;
         this.cmin = var2;
         this.cmax = var3;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4;
         for(var4 = 0; var4 < this.cmin; ++var4) {
            if (!this.atom.match(var1, var2, var3)) {
               return false;
            }

            var2 = var1.last;
         }

         if (this.type == 0) {
            return this.match0(var1, var2, var4, var3);
         } else if (this.type == 1) {
            return this.match1(var1, var2, var4, var3);
         } else {
            return this.match2(var1, var2, var4, var3);
         }
      }

      boolean match0(Matcher var1, int var2, int var3, CharSequence var4) {
         if (var3 >= this.cmax) {
            return this.next.match(var1, var2, var4);
         } else {
            int var5 = var3;
            if (this.atom.match(var1, var2, var4)) {
               int var6 = var1.last - var2;
               if (var6 != 0) {
                  var2 = var1.last;
                  ++var3;

                  while(var3 < this.cmax && this.atom.match(var1, var2, var4)) {
                     if (var2 + var6 != var1.last) {
                        if (this.match0(var1, var1.last, var3 + 1, var4)) {
                           return true;
                        }
                        break;
                     }

                     var2 += var6;
                     ++var3;
                  }

                  while(var3 >= var5) {
                     if (this.next.match(var1, var2, var4)) {
                        return true;
                     }

                     var2 -= var6;
                     --var3;
                  }

                  return false;
               }
            }

            return this.next.match(var1, var2, var4);
         }
      }

      boolean match1(Matcher var1, int var2, int var3, CharSequence var4) {
         while(!this.next.match(var1, var2, var4)) {
            if (var3 >= this.cmax) {
               return false;
            }

            if (!this.atom.match(var1, var2, var4)) {
               return false;
            }

            if (var2 == var1.last) {
               return false;
            }

            var2 = var1.last;
            ++var3;
         }

         return true;
      }

      boolean match2(Matcher var1, int var2, int var3, CharSequence var4) {
         while(var3 < this.cmax && this.atom.match(var1, var2, var4) && var2 != var1.last) {
            var2 = var1.last;
            ++var3;
         }

         return this.next.match(var1, var2, var4);
      }

      boolean study(Pattern.TreeInfo var1) {
         int var2 = var1.minLength;
         int var3 = var1.maxLength;
         boolean var4 = var1.maxValid;
         boolean var5 = var1.deterministic;
         var1.reset();
         this.atom.study(var1);
         int var6 = var1.minLength * this.cmin + var2;
         if (var6 < var2) {
            var6 = 268435455;
         }

         var1.minLength = var6;
         if (var4 & var1.maxValid) {
            var6 = var1.maxLength * this.cmax + var3;
            var1.maxLength = var6;
            if (var6 < var3) {
               var1.maxValid = false;
            }
         } else {
            var1.maxValid = false;
         }

         if (var1.deterministic && this.cmin == this.cmax) {
            var1.deterministic = var5;
         } else {
            var1.deterministic = false;
         }

         return this.next.study(var1);
      }
   }

   static final class Ques extends Pattern.Node {
      Pattern.Node atom;
      int type;

      Ques(Pattern.Node var1, int var2) {
         this.atom = var1;
         this.type = var2;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         switch(this.type) {
         case 0:
            return this.atom.match(var1, var2, var3) && this.next.match(var1, var1.last, var3) || this.next.match(var1, var2, var3);
         case 1:
            return this.next.match(var1, var2, var3) || this.atom.match(var1, var2, var3) && this.next.match(var1, var1.last, var3);
         case 2:
            if (this.atom.match(var1, var2, var3)) {
               var2 = var1.last;
            }

            return this.next.match(var1, var2, var3);
         default:
            return this.atom.match(var1, var2, var3) && this.next.match(var1, var1.last, var3);
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         if (this.type != 3) {
            int var2 = var1.minLength;
            this.atom.study(var1);
            var1.minLength = var2;
            var1.deterministic = false;
            return this.next.study(var1);
         } else {
            this.atom.study(var1);
            return this.next.study(var1);
         }
      }
   }

   static final class UnixDot extends Pattern.CharProperty {
      UnixDot() {
         super(null);
      }

      boolean isSatisfiedBy(int var1) {
         return var1 != 10;
      }
   }

   static final class Dot extends Pattern.CharProperty {
      Dot() {
         super(null);
      }

      boolean isSatisfiedBy(int var1) {
         return var1 != 10 && var1 != 13 && (var1 | 1) != 8233 && var1 != 133;
      }
   }

   static final class All extends Pattern.CharProperty {
      All() {
         super(null);
      }

      boolean isSatisfiedBy(int var1) {
         return true;
      }
   }

   static final class SliceUS extends Pattern.SliceIS {
      SliceUS(int[] var1) {
         super(var1);
      }

      int toLower(int var1) {
         return Character.toLowerCase(Character.toUpperCase(var1));
      }
   }

   static class SliceIS extends Pattern.SliceNode {
      SliceIS(int[] var1) {
         super(var1);
      }

      int toLower(int var1) {
         return ASCII.toLower(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var2;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            if (var5 >= var1.to) {
               var1.hitEnd = true;
               return false;
            }

            int var7 = Character.codePointAt(var3, var5);
            if (var4[var6] != var7 && var4[var6] != this.toLower(var7)) {
               return false;
            }

            var5 += Character.charCount(var7);
            if (var5 > var1.to) {
               var1.hitEnd = true;
               return false;
            }
         }

         return this.next.match(var1, var5, var3);
      }
   }

   static final class SliceS extends Pattern.SliceNode {
      SliceS(int[] var1) {
         super(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var2;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            if (var5 >= var1.to) {
               var1.hitEnd = true;
               return false;
            }

            int var7 = Character.codePointAt(var3, var5);
            if (var4[var6] != var7) {
               return false;
            }

            var5 += Character.charCount(var7);
            if (var5 > var1.to) {
               var1.hitEnd = true;
               return false;
            }
         }

         return this.next.match(var1, var5, var3);
      }
   }

   static final class SliceU extends Pattern.SliceNode {
      SliceU(int[] var1) {
         super(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var2 + var6 >= var1.to) {
               var1.hitEnd = true;
               return false;
            }

            char var7 = var3.charAt(var2 + var6);
            if (var4[var6] != var7 && var4[var6] != Character.toLowerCase(Character.toUpperCase((int)var7))) {
               return false;
            }
         }

         return this.next.match(var1, var2 + var5, var3);
      }
   }

   static class SliceI extends Pattern.SliceNode {
      SliceI(int[] var1) {
         super(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var2 + var6 >= var1.to) {
               var1.hitEnd = true;
               return false;
            }

            char var7 = var3.charAt(var2 + var6);
            if (var4[var6] != var7 && var4[var6] != ASCII.toLower(var7)) {
               return false;
            }
         }

         return this.next.match(var1, var2 + var5, var3);
      }
   }

   static final class Slice extends Pattern.SliceNode {
      Slice(int[] var1) {
         super(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int[] var4 = this.buffer;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var2 + var6 >= var1.to) {
               var1.hitEnd = true;
               return false;
            }

            if (var4[var6] != var3.charAt(var2 + var6)) {
               return false;
            }
         }

         return this.next.match(var1, var2 + var5, var3);
      }
   }

   static class SliceNode extends Pattern.Node {
      int[] buffer;

      SliceNode(int[] var1) {
         this.buffer = var1;
      }

      boolean study(Pattern.TreeInfo var1) {
         var1.minLength += this.buffer.length;
         var1.maxLength += this.buffer.length;
         return this.next.study(var1);
      }
   }

   static final class HorizWS extends Pattern.BmpCharProperty {
      HorizWS() {
         super(null);
      }

      boolean isSatisfiedBy(int var1) {
         return var1 == 9 || var1 == 32 || var1 == 160 || var1 == 5760 || var1 == 6158 || var1 >= 8192 && var1 <= 8202 || var1 == 8239 || var1 == 8287 || var1 == 12288;
      }
   }

   static final class VertWS extends Pattern.BmpCharProperty {
      VertWS() {
         super(null);
      }

      boolean isSatisfiedBy(int var1) {
         return var1 >= 10 && var1 <= 13 || var1 == 133 || var1 == 8232 || var1 == 8233;
      }
   }

   static final class Ctype extends Pattern.BmpCharProperty {
      final int ctype;

      Ctype(int var1) {
         super(null);
         this.ctype = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return var1 < 128 && ASCII.isType(var1, this.ctype);
      }
   }

   static final class Utype extends Pattern.CharProperty {
      final UnicodeProp uprop;

      Utype(UnicodeProp var1) {
         super(null);
         this.uprop = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return this.uprop.is(var1);
      }
   }

   static final class Category extends Pattern.CharProperty {
      final int typeMask;

      Category(int var1) {
         super(null);
         this.typeMask = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return (this.typeMask & 1 << Character.getType(var1)) != 0;
      }
   }

   static final class Script extends Pattern.CharProperty {
      final Character.UnicodeScript script;

      Script(Character.UnicodeScript var1) {
         super(null);
         this.script = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return this.script == Character.UnicodeScript.of(var1);
      }
   }

   static final class Block extends Pattern.CharProperty {
      final Character.UnicodeBlock block;

      Block(Character.UnicodeBlock var1) {
         super(null);
         this.block = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return this.block == Character.UnicodeBlock.of(var1);
      }
   }

   static final class SingleU extends Pattern.CharProperty {
      final int lower;

      SingleU(int var1) {
         super(null);
         this.lower = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return this.lower == var1 || this.lower == Character.toLowerCase(Character.toUpperCase(var1));
      }
   }

   static final class SingleI extends Pattern.BmpCharProperty {
      final int lower;
      final int upper;

      SingleI(int var1, int var2) {
         super(null);
         this.lower = var1;
         this.upper = var2;
      }

      boolean isSatisfiedBy(int var1) {
         return var1 == this.lower || var1 == this.upper;
      }
   }

   static final class Single extends Pattern.BmpCharProperty {
      final int c;

      Single(int var1) {
         super(null);
         this.c = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return var1 == this.c;
      }
   }

   static final class SingleS extends Pattern.CharProperty {
      final int c;

      SingleS(int var1) {
         super(null);
         this.c = var1;
      }

      boolean isSatisfiedBy(int var1) {
         return var1 == this.c;
      }
   }

   private abstract static class BmpCharProperty extends Pattern.CharProperty {
      private BmpCharProperty() {
         super(null);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 >= var1.to) {
            var1.hitEnd = true;
            return false;
         } else {
            return this.isSatisfiedBy(var3.charAt(var2)) && this.next.match(var1, var2 + 1, var3);
         }
      }

      // $FF: synthetic method
      BmpCharProperty(Object var1) {
         this();
      }
   }

   private abstract static class CharProperty extends Pattern.Node {
      private CharProperty() {
      }

      abstract boolean isSatisfiedBy(int var1);

      Pattern.CharProperty complement() {
         return new Pattern.CharProperty() {
            boolean isSatisfiedBy(int var1) {
               return !CharProperty.this.isSatisfiedBy(var1);
            }
         };
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 >= var1.to) {
            var1.hitEnd = true;
            return false;
         } else {
            int var4 = Character.codePointAt(var3, var2);
            return this.isSatisfiedBy(var4) && this.next.match(var1, var2 + Character.charCount(var4), var3);
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         ++var1.minLength;
         ++var1.maxLength;
         return this.next.study(var1);
      }

      // $FF: synthetic method
      CharProperty(Object var1) {
         this();
      }
   }

   static final class LineEnding extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 < var1.to) {
            char var4 = var3.charAt(var2);
            if (var4 == '\n' || var4 == 11 || var4 == '\f' || var4 == 133 || var4 == 8232 || var4 == 8233) {
               return this.next.match(var1, var2 + 1, var3);
            }

            if (var4 == '\r') {
               ++var2;
               if (var2 < var1.to && var3.charAt(var2) == '\n') {
                  ++var2;
               }

               return this.next.match(var1, var2, var3);
            }
         } else {
            var1.hitEnd = true;
         }

         return false;
      }

      boolean study(Pattern.TreeInfo var1) {
         ++var1.minLength;
         var1.maxLength += 2;
         return this.next.study(var1);
      }
   }

   static final class UnixDollar extends Pattern.Node {
      boolean multiline;

      UnixDollar(boolean var1) {
         this.multiline = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.anchoringBounds ? var1.to : var1.getTextLength();
         if (var2 < var4) {
            char var5 = var3.charAt(var2);
            if (var5 != '\n') {
               return false;
            }

            if (!this.multiline && var2 != var4 - 1) {
               return false;
            }

            if (this.multiline) {
               return this.next.match(var1, var2, var3);
            }
         }

         var1.hitEnd = true;
         var1.requireEnd = true;
         return this.next.match(var1, var2, var3);
      }

      boolean study(Pattern.TreeInfo var1) {
         this.next.study(var1);
         return var1.deterministic;
      }
   }

   static final class Dollar extends Pattern.Node {
      boolean multiline;

      Dollar(boolean var1) {
         this.multiline = var1;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.anchoringBounds ? var1.to : var1.getTextLength();
         char var5;
         if (!this.multiline) {
            if (var2 < var4 - 2) {
               return false;
            }

            if (var2 == var4 - 2) {
               var5 = var3.charAt(var2);
               if (var5 != '\r') {
                  return false;
               }

               var5 = var3.charAt(var2 + 1);
               if (var5 != '\n') {
                  return false;
               }
            }
         }

         if (var2 < var4) {
            var5 = var3.charAt(var2);
            if (var5 == '\n') {
               if (var2 > 0 && var3.charAt(var2 - 1) == '\r') {
                  return false;
               }

               if (this.multiline) {
                  return this.next.match(var1, var2, var3);
               }
            } else {
               if (var5 != '\r' && var5 != 133 && (var5 | 1) != 8233) {
                  return false;
               }

               if (this.multiline) {
                  return this.next.match(var1, var2, var3);
               }
            }
         }

         var1.hitEnd = true;
         var1.requireEnd = true;
         return this.next.match(var1, var2, var3);
      }

      boolean study(Pattern.TreeInfo var1) {
         this.next.study(var1);
         return var1.deterministic;
      }
   }

   static final class LastMatch extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         return var2 != var1.oldLast ? false : this.next.match(var1, var2, var3);
      }
   }

   static final class UnixCaret extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.from;
         int var5 = var1.to;
         if (!var1.anchoringBounds) {
            var4 = 0;
            var5 = var1.getTextLength();
         }

         if (var2 == var5) {
            var1.hitEnd = true;
            return false;
         } else {
            if (var2 > var4) {
               char var6 = var3.charAt(var2 - 1);
               if (var6 != '\n') {
                  return false;
               }
            }

            return this.next.match(var1, var2, var3);
         }
      }
   }

   static final class Caret extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.from;
         int var5 = var1.to;
         if (!var1.anchoringBounds) {
            var4 = 0;
            var5 = var1.getTextLength();
         }

         if (var2 == var5) {
            var1.hitEnd = true;
            return false;
         } else {
            if (var2 > var4) {
               char var6 = var3.charAt(var2 - 1);
               if (var6 != '\n' && var6 != '\r' && (var6 | 1) != 8233 && var6 != 133) {
                  return false;
               }

               if (var6 == '\r' && var3.charAt(var2) == '\n') {
                  return false;
               }
            }

            return this.next.match(var1, var2, var3);
         }
      }
   }

   static final class End extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.anchoringBounds ? var1.to : var1.getTextLength();
         if (var2 == var4) {
            var1.hitEnd = true;
            return this.next.match(var1, var2, var3);
         } else {
            return false;
         }
      }
   }

   static final class Begin extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         int var4 = var1.anchoringBounds ? var1.from : 0;
         if (var2 == var4 && this.next.match(var1, var2, var3)) {
            var1.first = var2;
            var1.groups[0] = var2;
            var1.groups[1] = var1.last;
            return true;
         } else {
            return false;
         }
      }
   }

   static final class StartS extends Pattern.Start {
      StartS(Pattern.Node var1) {
         super(var1);
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 > var1.to - this.minLength) {
            var1.hitEnd = true;
            return false;
         } else {
            int var4 = var1.to - this.minLength;

            while(var2 <= var4) {
               if (this.next.match(var1, var2, var3)) {
                  var1.first = var2;
                  var1.groups[0] = var1.first;
                  var1.groups[1] = var1.last;
                  return true;
               }

               if (var2 == var4) {
                  break;
               }

               if (Character.isHighSurrogate(var3.charAt(var2++)) && var2 < var3.length() && Character.isLowSurrogate(var3.charAt(var2))) {
                  ++var2;
               }
            }

            var1.hitEnd = true;
            return false;
         }
      }
   }

   static class Start extends Pattern.Node {
      int minLength;

      Start(Pattern.Node var1) {
         this.next = var1;
         Pattern.TreeInfo var2 = new Pattern.TreeInfo();
         this.next.study(var2);
         this.minLength = var2.minLength;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var2 > var1.to - this.minLength) {
            var1.hitEnd = true;
            return false;
         } else {
            for(int var4 = var1.to - this.minLength; var2 <= var4; ++var2) {
               if (this.next.match(var1, var2, var3)) {
                  var1.first = var2;
                  var1.groups[0] = var1.first;
                  var1.groups[1] = var1.last;
                  return true;
               }
            }

            var1.hitEnd = true;
            return false;
         }
      }

      boolean study(Pattern.TreeInfo var1) {
         this.next.study(var1);
         var1.maxValid = false;
         var1.deterministic = false;
         return false;
      }
   }

   static class LastNode extends Pattern.Node {
      boolean match(Matcher var1, int var2, CharSequence var3) {
         if (var1.acceptMode == 1 && var2 != var1.to) {
            return false;
         } else {
            var1.last = var2;
            var1.groups[0] = var1.first;
            var1.groups[1] = var1.last;
            return true;
         }
      }
   }

   static class Node {
      Pattern.Node next;

      Node() {
         this.next = Pattern.accept;
      }

      boolean match(Matcher var1, int var2, CharSequence var3) {
         var1.last = var2;
         var1.groups[0] = var1.first;
         var1.groups[1] = var1.last;
         return true;
      }

      boolean study(Pattern.TreeInfo var1) {
         return this.next != null ? this.next.study(var1) : var1.deterministic;
      }
   }

   private static final class BitClass extends Pattern.BmpCharProperty {
      final boolean[] bits;

      BitClass() {
         super(null);
         this.bits = new boolean[256];
      }

      private BitClass(boolean[] var1) {
         super(null);
         this.bits = var1;
      }

      Pattern.BitClass add(int var1, int var2) {
         assert var1 >= 0 && var1 <= 255;

         if ((var2 & 2) != 0) {
            if (ASCII.isAscii(var1)) {
               this.bits[ASCII.toUpper(var1)] = true;
               this.bits[ASCII.toLower(var1)] = true;
            } else if ((var2 & 64) != 0) {
               this.bits[Character.toLowerCase(var1)] = true;
               this.bits[Character.toUpperCase(var1)] = true;
            }
         }

         this.bits[var1] = true;
         return this;
      }

      boolean isSatisfiedBy(int var1) {
         return var1 < 256 && this.bits[var1];
      }
   }

   static final class TreeInfo {
      int minLength;
      int maxLength;
      boolean maxValid;
      boolean deterministic;

      TreeInfo() {
         this.reset();
      }

      void reset() {
         this.minLength = 0;
         this.maxLength = 0;
         this.maxValid = true;
         this.deterministic = true;
      }
   }
}
