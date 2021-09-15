package java.util.regex;

import java.util.Objects;

public final class Matcher implements MatchResult {
   Pattern parentPattern;
   int[] groups;
   int from;
   int to;
   int lookbehindTo;
   CharSequence text;
   static final int ENDANCHOR = 1;
   static final int NOANCHOR = 0;
   int acceptMode = 0;
   int first = -1;
   int last = 0;
   int oldLast = -1;
   int lastAppendPosition = 0;
   int[] locals;
   boolean hitEnd;
   boolean requireEnd;
   boolean transparentBounds = false;
   boolean anchoringBounds = true;

   Matcher() {
   }

   Matcher(Pattern var1, CharSequence var2) {
      this.parentPattern = var1;
      this.text = var2;
      int var3 = Math.max(var1.capturingGroupCount, 10);
      this.groups = new int[var3 * 2];
      this.locals = new int[var1.localCount];
      this.reset();
   }

   public Pattern pattern() {
      return this.parentPattern;
   }

   public MatchResult toMatchResult() {
      Matcher var1 = new Matcher(this.parentPattern, this.text.toString());
      var1.first = this.first;
      var1.last = this.last;
      var1.groups = (int[])this.groups.clone();
      return var1;
   }

   public Matcher usePattern(Pattern var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Pattern cannot be null");
      } else {
         this.parentPattern = var1;
         int var2 = Math.max(var1.capturingGroupCount, 10);
         this.groups = new int[var2 * 2];
         this.locals = new int[var1.localCount];

         int var3;
         for(var3 = 0; var3 < this.groups.length; ++var3) {
            this.groups[var3] = -1;
         }

         for(var3 = 0; var3 < this.locals.length; ++var3) {
            this.locals[var3] = -1;
         }

         return this;
      }
   }

   public Matcher reset() {
      this.first = -1;
      this.last = 0;
      this.oldLast = -1;

      int var1;
      for(var1 = 0; var1 < this.groups.length; ++var1) {
         this.groups[var1] = -1;
      }

      for(var1 = 0; var1 < this.locals.length; ++var1) {
         this.locals[var1] = -1;
      }

      this.lastAppendPosition = 0;
      this.from = 0;
      this.to = this.getTextLength();
      return this;
   }

   public Matcher reset(CharSequence var1) {
      this.text = var1;
      return this.reset();
   }

   public int start() {
      if (this.first < 0) {
         throw new IllegalStateException("No match available");
      } else {
         return this.first;
      }
   }

   public int start(int var1) {
      if (this.first < 0) {
         throw new IllegalStateException("No match available");
      } else if (var1 >= 0 && var1 <= this.groupCount()) {
         return this.groups[var1 * 2];
      } else {
         throw new IndexOutOfBoundsException("No group " + var1);
      }
   }

   public int start(String var1) {
      return this.groups[this.getMatchedGroupIndex(var1) * 2];
   }

   public int end() {
      if (this.first < 0) {
         throw new IllegalStateException("No match available");
      } else {
         return this.last;
      }
   }

   public int end(int var1) {
      if (this.first < 0) {
         throw new IllegalStateException("No match available");
      } else if (var1 >= 0 && var1 <= this.groupCount()) {
         return this.groups[var1 * 2 + 1];
      } else {
         throw new IndexOutOfBoundsException("No group " + var1);
      }
   }

   public int end(String var1) {
      return this.groups[this.getMatchedGroupIndex(var1) * 2 + 1];
   }

   public String group() {
      return this.group(0);
   }

   public String group(int var1) {
      if (this.first < 0) {
         throw new IllegalStateException("No match found");
      } else if (var1 >= 0 && var1 <= this.groupCount()) {
         return this.groups[var1 * 2] != -1 && this.groups[var1 * 2 + 1] != -1 ? this.getSubSequence(this.groups[var1 * 2], this.groups[var1 * 2 + 1]).toString() : null;
      } else {
         throw new IndexOutOfBoundsException("No group " + var1);
      }
   }

   public String group(String var1) {
      int var2 = this.getMatchedGroupIndex(var1);
      return this.groups[var2 * 2] != -1 && this.groups[var2 * 2 + 1] != -1 ? this.getSubSequence(this.groups[var2 * 2], this.groups[var2 * 2 + 1]).toString() : null;
   }

   public int groupCount() {
      return this.parentPattern.capturingGroupCount - 1;
   }

   public boolean matches() {
      return this.match(this.from, 1);
   }

   public boolean find() {
      int var1 = this.last;
      if (var1 == this.first) {
         ++var1;
      }

      if (var1 < this.from) {
         var1 = this.from;
      }

      if (var1 <= this.to) {
         return this.search(var1);
      } else {
         for(int var2 = 0; var2 < this.groups.length; ++var2) {
            this.groups[var2] = -1;
         }

         return false;
      }
   }

   public boolean find(int var1) {
      int var2 = this.getTextLength();
      if (var1 >= 0 && var1 <= var2) {
         this.reset();
         return this.search(var1);
      } else {
         throw new IndexOutOfBoundsException("Illegal start index");
      }
   }

   public boolean lookingAt() {
      return this.match(this.from, 0);
   }

   public static String quoteReplacement(String var0) {
      if (var0.indexOf(92) == -1 && var0.indexOf(36) == -1) {
         return var0;
      } else {
         StringBuilder var1 = new StringBuilder();

         for(int var2 = 0; var2 < var0.length(); ++var2) {
            char var3 = var0.charAt(var2);
            if (var3 == '\\' || var3 == '$') {
               var1.append('\\');
            }

            var1.append(var3);
         }

         return var1.toString();
      }
   }

   public Matcher appendReplacement(StringBuffer var1, String var2) {
      if (this.first < 0) {
         throw new IllegalStateException("No match available");
      } else {
         int var3 = 0;
         StringBuilder var4 = new StringBuilder();

         while(true) {
            while(true) {
               while(var3 < var2.length()) {
                  char var5 = var2.charAt(var3);
                  if (var5 != '\\') {
                     if (var5 == '$') {
                        ++var3;
                        if (var3 == var2.length()) {
                           throw new IllegalArgumentException("Illegal group reference: group index is missing");
                        }

                        var5 = var2.charAt(var3);
                        boolean var6 = true;
                        int var10;
                        if (var5 == '{') {
                           ++var3;

                           StringBuilder var7;
                           for(var7 = new StringBuilder(); var3 < var2.length(); ++var3) {
                              var5 = var2.charAt(var3);
                              if (!ASCII.isLower(var5) && !ASCII.isUpper(var5) && !ASCII.isDigit(var5)) {
                                 break;
                              }

                              var7.append(var5);
                           }

                           if (var7.length() == 0) {
                              throw new IllegalArgumentException("named capturing group has 0 length name");
                           }

                           if (var5 != '}') {
                              throw new IllegalArgumentException("named capturing group is missing trailing '}'");
                           }

                           String var8 = var7.toString();
                           if (ASCII.isDigit(var8.charAt(0))) {
                              throw new IllegalArgumentException("capturing group name {" + var8 + "} starts with digit character");
                           }

                           if (!this.parentPattern.namedGroups().containsKey(var8)) {
                              throw new IllegalArgumentException("No group with name {" + var8 + "}");
                           }

                           var10 = (Integer)this.parentPattern.namedGroups().get(var8);
                           ++var3;
                        } else {
                           var10 = var5 - 48;
                           if (var10 < 0 || var10 > 9) {
                              throw new IllegalArgumentException("Illegal group reference");
                           }

                           ++var3;
                           boolean var11 = false;

                           while(!var11 && var3 < var2.length()) {
                              int var12 = var2.charAt(var3) - 48;
                              if (var12 < 0 || var12 > 9) {
                                 break;
                              }

                              int var9 = var10 * 10 + var12;
                              if (this.groupCount() < var9) {
                                 var11 = true;
                              } else {
                                 var10 = var9;
                                 ++var3;
                              }
                           }
                        }

                        if (this.start(var10) != -1 && this.end(var10) != -1) {
                           var4.append(this.text, this.start(var10), this.end(var10));
                        }
                     } else {
                        var4.append(var5);
                        ++var3;
                     }
                  } else {
                     ++var3;
                     if (var3 == var2.length()) {
                        throw new IllegalArgumentException("character to be escaped is missing");
                     }

                     var5 = var2.charAt(var3);
                     var4.append(var5);
                     ++var3;
                  }
               }

               var1.append(this.text, this.lastAppendPosition, this.first);
               var1.append((CharSequence)var4);
               this.lastAppendPosition = this.last;
               return this;
            }
         }
      }
   }

   public StringBuffer appendTail(StringBuffer var1) {
      var1.append(this.text, this.lastAppendPosition, this.getTextLength());
      return var1;
   }

   public String replaceAll(String var1) {
      this.reset();
      boolean var2 = this.find();
      if (!var2) {
         return this.text.toString();
      } else {
         StringBuffer var3 = new StringBuffer();

         do {
            this.appendReplacement(var3, var1);
            var2 = this.find();
         } while(var2);

         this.appendTail(var3);
         return var3.toString();
      }
   }

   public String replaceFirst(String var1) {
      if (var1 == null) {
         throw new NullPointerException("replacement");
      } else {
         this.reset();
         if (!this.find()) {
            return this.text.toString();
         } else {
            StringBuffer var2 = new StringBuffer();
            this.appendReplacement(var2, var1);
            this.appendTail(var2);
            return var2.toString();
         }
      }
   }

   public Matcher region(int var1, int var2) {
      if (var1 >= 0 && var1 <= this.getTextLength()) {
         if (var2 >= 0 && var2 <= this.getTextLength()) {
            if (var1 > var2) {
               throw new IndexOutOfBoundsException("start > end");
            } else {
               this.reset();
               this.from = var1;
               this.to = var2;
               return this;
            }
         } else {
            throw new IndexOutOfBoundsException("end");
         }
      } else {
         throw new IndexOutOfBoundsException("start");
      }
   }

   public int regionStart() {
      return this.from;
   }

   public int regionEnd() {
      return this.to;
   }

   public boolean hasTransparentBounds() {
      return this.transparentBounds;
   }

   public Matcher useTransparentBounds(boolean var1) {
      this.transparentBounds = var1;
      return this;
   }

   public boolean hasAnchoringBounds() {
      return this.anchoringBounds;
   }

   public Matcher useAnchoringBounds(boolean var1) {
      this.anchoringBounds = var1;
      return this;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("java.util.regex.Matcher");
      var1.append("[pattern=" + this.pattern());
      var1.append(" region=");
      var1.append(this.regionStart() + "," + this.regionEnd());
      var1.append(" lastmatch=");
      if (this.first >= 0 && this.group() != null) {
         var1.append(this.group());
      }

      var1.append("]");
      return var1.toString();
   }

   public boolean hitEnd() {
      return this.hitEnd;
   }

   public boolean requireEnd() {
      return this.requireEnd;
   }

   boolean search(int var1) {
      this.hitEnd = false;
      this.requireEnd = false;
      var1 = var1 < 0 ? 0 : var1;
      this.first = var1;
      this.oldLast = this.oldLast < 0 ? var1 : this.oldLast;

      for(int var2 = 0; var2 < this.groups.length; ++var2) {
         this.groups[var2] = -1;
      }

      this.acceptMode = 0;
      boolean var3 = this.parentPattern.root.match(this, var1, this.text);
      if (!var3) {
         this.first = -1;
      }

      this.oldLast = this.last;
      return var3;
   }

   boolean match(int var1, int var2) {
      this.hitEnd = false;
      this.requireEnd = false;
      var1 = var1 < 0 ? 0 : var1;
      this.first = var1;
      this.oldLast = this.oldLast < 0 ? var1 : this.oldLast;

      for(int var3 = 0; var3 < this.groups.length; ++var3) {
         this.groups[var3] = -1;
      }

      this.acceptMode = var2;
      boolean var4 = this.parentPattern.matchRoot.match(this, var1, this.text);
      if (!var4) {
         this.first = -1;
      }

      this.oldLast = this.last;
      return var4;
   }

   int getTextLength() {
      return this.text.length();
   }

   CharSequence getSubSequence(int var1, int var2) {
      return this.text.subSequence(var1, var2);
   }

   char charAt(int var1) {
      return this.text.charAt(var1);
   }

   int getMatchedGroupIndex(String var1) {
      Objects.requireNonNull(var1, (String)"Group name");
      if (this.first < 0) {
         throw new IllegalStateException("No match found");
      } else if (!this.parentPattern.namedGroups().containsKey(var1)) {
         throw new IllegalArgumentException("No group with name <" + var1 + ">");
      } else {
         return (Integer)this.parentPattern.namedGroups().get(var1);
      }
   }
}
