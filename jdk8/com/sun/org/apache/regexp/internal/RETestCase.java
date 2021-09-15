package com.sun.org.apache.regexp.internal;

import java.io.StringBufferInputStream;
import java.io.StringReader;

final class RETestCase {
   private final StringBuffer log = new StringBuffer();
   private final int number;
   private final String tag;
   private final String pattern;
   private final String toMatch;
   private final boolean badPattern;
   private final boolean shouldMatch;
   private final String[] parens;
   private final RETest test;
   private RE regexp;

   public RETestCase(RETest test, String tag, String pattern, String toMatch, boolean badPattern, boolean shouldMatch, String[] parens) {
      this.number = ++test.testCount;
      this.test = test;
      this.tag = tag;
      this.pattern = pattern;
      this.toMatch = toMatch;
      this.badPattern = badPattern;
      this.shouldMatch = shouldMatch;
      if (parens != null) {
         this.parens = new String[parens.length];

         for(int i = 0; i < parens.length; ++i) {
            this.parens[i] = parens[i];
         }
      } else {
         this.parens = null;
      }

   }

   public void runTest() {
      this.test.say(this.tag + "(" + this.number + "): " + this.pattern);
      if (this.testCreation()) {
         this.testMatch();
      }

   }

   boolean testCreation() {
      try {
         this.regexp = new RE();
         this.regexp.setProgram(this.test.compiler.compile(this.pattern));
         if (this.badPattern) {
            this.test.fail(this.log, "Was expected to be an error, but wasn't.");
            return false;
         }

         return true;
      } catch (Exception var3) {
         if (this.badPattern) {
            this.log.append("   Match: ERR\n");
            this.success("Produces an error (" + var3.toString() + "), as expected.");
            return false;
         }

         String message = var3.getMessage() == null ? var3.toString() : var3.getMessage();
         this.test.fail(this.log, "Produces an unexpected exception \"" + message + "\"");
         var3.printStackTrace();
      } catch (Error var4) {
         this.test.fail(this.log, "Compiler threw fatal error \"" + var4.getMessage() + "\"");
         var4.printStackTrace();
      }

      return false;
   }

   private void testMatch() {
      this.log.append("   Match against: '" + this.toMatch + "'\n");

      try {
         boolean result = this.regexp.match(this.toMatch);
         this.log.append("   Matched: " + (result ? "YES" : "NO") + "\n");
         if (this.checkResult(result) && (!this.shouldMatch || this.checkParens())) {
            this.log.append("   Match using StringCharacterIterator\n");
            if (!this.tryMatchUsingCI(new StringCharacterIterator(this.toMatch))) {
               return;
            }

            this.log.append("   Match using CharacterArrayCharacterIterator\n");
            if (!this.tryMatchUsingCI(new CharacterArrayCharacterIterator(this.toMatch.toCharArray(), 0, this.toMatch.length()))) {
               return;
            }

            this.log.append("   Match using StreamCharacterIterator\n");
            if (!this.tryMatchUsingCI(new StreamCharacterIterator(new StringBufferInputStream(this.toMatch)))) {
               return;
            }

            this.log.append("   Match using ReaderCharacterIterator\n");
            if (!this.tryMatchUsingCI(new ReaderCharacterIterator(new StringReader(this.toMatch)))) {
               return;
            }
         }
      } catch (Exception var2) {
         this.test.fail(this.log, "Matcher threw exception: " + var2.toString());
         var2.printStackTrace();
      } catch (Error var3) {
         this.test.fail(this.log, "Matcher threw fatal error \"" + var3.getMessage() + "\"");
         var3.printStackTrace();
      }

   }

   private boolean checkResult(boolean result) {
      if (result == this.shouldMatch) {
         this.success((this.shouldMatch ? "Matched" : "Did not match") + " \"" + this.toMatch + "\", as expected:");
         return true;
      } else {
         if (this.shouldMatch) {
            this.test.fail(this.log, "Did not match \"" + this.toMatch + "\", when expected to.");
         } else {
            this.test.fail(this.log, "Matched \"" + this.toMatch + "\", when not expected to.");
         }

         return false;
      }
   }

   private boolean checkParens() {
      this.log.append("   Paren count: " + this.regexp.getParenCount() + "\n");
      if (!this.assertEquals(this.log, "Wrong number of parens", this.parens.length, this.regexp.getParenCount())) {
         return false;
      } else {
         for(int p = 0; p < this.regexp.getParenCount(); ++p) {
            this.log.append("   Paren " + p + ": " + this.regexp.getParen(p) + "\n");
            if ((!"null".equals(this.parens[p]) || this.regexp.getParen(p) != null) && !this.assertEquals(this.log, "Wrong register " + p, this.parens[p], this.regexp.getParen(p))) {
               return false;
            }
         }

         return true;
      }
   }

   boolean tryMatchUsingCI(CharacterIterator matchAgainst) {
      try {
         boolean result = this.regexp.match((CharacterIterator)matchAgainst, 0);
         this.log.append("   Match: " + (result ? "YES" : "NO") + "\n");
         return this.checkResult(result) && (!this.shouldMatch || this.checkParens());
      } catch (Exception var3) {
         this.test.fail(this.log, "Matcher threw exception: " + var3.toString());
         var3.printStackTrace();
      } catch (Error var4) {
         this.test.fail(this.log, "Matcher threw fatal error \"" + var4.getMessage() + "\"");
         var4.printStackTrace();
      }

      return false;
   }

   public boolean assertEquals(StringBuffer log, String message, String expected, String actual) {
      if ((expected == null || expected.equals(actual)) && (actual == null || actual.equals(expected))) {
         return true;
      } else {
         this.test.fail(log, message + " (expected \"" + expected + "\", actual \"" + actual + "\")");
         return false;
      }
   }

   public boolean assertEquals(StringBuffer log, String message, int expected, int actual) {
      if (expected != actual) {
         this.test.fail(log, message + " (expected \"" + expected + "\", actual \"" + actual + "\")");
         return false;
      } else {
         return true;
      }
   }

   void success(String s) {
   }
}
