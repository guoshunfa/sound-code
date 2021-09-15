package com.sun.org.apache.regexp.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class RETest {
   static final boolean showSuccesses = false;
   static final String NEW_LINE = System.getProperty("line.separator");
   REDebugCompiler compiler = new REDebugCompiler();
   int testCount = 0;
   int failures = 0;

   public static void main(String[] args) {
      try {
         if (!test(args)) {
            System.exit(1);
         }
      } catch (Exception var2) {
         var2.printStackTrace();
         System.exit(1);
      }

   }

   public static boolean test(String[] args) throws Exception {
      RETest test = new RETest();
      if (args.length == 2) {
         test.runInteractiveTests(args[1]);
      } else if (args.length == 1) {
         test.runAutomatedTests(args[0]);
      } else {
         System.out.println("Usage: RETest ([-i] [regex]) ([/path/to/testfile.txt])");
         System.out.println("By Default will run automated tests from file 'docs/RETest.txt' ...");
         System.out.println();
         test.runAutomatedTests("docs/RETest.txt");
      }

      return test.failures == 0;
   }

   void runInteractiveTests(String expr) {
      RE r = new RE();

      try {
         r.setProgram(this.compiler.compile(expr));
         this.say("" + NEW_LINE + "" + expr + "" + NEW_LINE + "");
         PrintWriter writer = new PrintWriter(System.out);
         this.compiler.dumpProgram(writer);
         writer.flush();
         boolean running = true;

         while(running) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> ");
            System.out.flush();
            String match = br.readLine();
            if (match != null) {
               if (r.match(match)) {
                  this.say("Match successful.");
               } else {
                  this.say("Match failed.");
               }

               this.showParens(r);
            } else {
               running = false;
               System.out.println();
            }
         }
      } catch (Exception var7) {
         this.say("Error: " + var7.toString());
         var7.printStackTrace();
      }

   }

   void die(String s) {
      this.say("FATAL ERROR: " + s);
      System.exit(-1);
   }

   void fail(StringBuffer log, String s) {
      System.out.print(log.toString());
      this.fail(s);
   }

   void fail(String s) {
      ++this.failures;
      this.say("" + NEW_LINE + "");
      this.say("*******************************************************");
      this.say("*********************  FAILURE!  **********************");
      this.say("*******************************************************");
      this.say("" + NEW_LINE + "");
      this.say(s);
      this.say("");
      if (this.compiler != null) {
         PrintWriter writer = new PrintWriter(System.out);
         this.compiler.dumpProgram(writer);
         writer.flush();
         this.say("" + NEW_LINE + "");
      }

   }

   void say(String s) {
      System.out.println(s);
   }

   void showParens(RE r) {
      for(int i = 0; i < r.getParenCount(); ++i) {
         this.say("$" + i + " = " + r.getParen(i));
      }

   }

   void runAutomatedTests(String testDocument) throws Exception {
      long ms = System.currentTimeMillis();
      this.testPrecompiledRE();
      this.testSplitAndGrep();
      this.testSubst();
      this.testOther();
      File testInput = new File(testDocument);
      if (!testInput.exists()) {
         throw new Exception("Could not find: " + testDocument);
      } else {
         BufferedReader br = new BufferedReader(new FileReader(testInput));

         try {
            while(br.ready()) {
               RETestCase testcase = this.getNextTestCase(br);
               if (testcase != null) {
                  testcase.runTest();
               }
            }
         } finally {
            br.close();
         }

         this.say(NEW_LINE + NEW_LINE + "Match time = " + (System.currentTimeMillis() - ms) + " ms.");
         if (this.failures > 0) {
            this.say("*************** THERE ARE FAILURES! *******************");
         }

         this.say("Tests complete.  " + this.testCount + " tests, " + this.failures + " failure(s).");
      }
   }

   void testOther() throws Exception {
      RE r = new RE("(a*)b");
      this.say("Serialized/deserialized (a*)b");
      ByteArrayOutputStream out = new ByteArrayOutputStream(128);
      (new ObjectOutputStream(out)).writeObject(r);
      ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
      r = (RE)(new ObjectInputStream(in)).readObject();
      if (!r.match("aaab")) {
         this.fail("Did not match 'aaab' with deserialized RE.");
      } else {
         this.say("aaaab = true");
         this.showParens(r);
      }

      out.reset();
      this.say("Deserialized (a*)b");
      (new ObjectOutputStream(out)).writeObject(r);
      in = new ByteArrayInputStream(out.toByteArray());
      r = (RE)(new ObjectInputStream(in)).readObject();
      if (r.getParenCount() != 0) {
         this.fail("Has parens after deserialization.");
      }

      if (!r.match("aaab")) {
         this.fail("Did not match 'aaab' with deserialized RE.");
      } else {
         this.say("aaaab = true");
         this.showParens(r);
      }

      r = new RE("abc(\\w*)");
      this.say("MATCH_CASEINDEPENDENT abc(\\w*)");
      r.setMatchFlags(1);
      this.say("abc(d*)");
      if (!r.match("abcddd")) {
         this.fail("Did not match 'abcddd'.");
      } else {
         this.say("abcddd = true");
         this.showParens(r);
      }

      if (!r.match("aBcDDdd")) {
         this.fail("Did not match 'aBcDDdd'.");
      } else {
         this.say("aBcDDdd = true");
         this.showParens(r);
      }

      if (!r.match("ABCDDDDD")) {
         this.fail("Did not match 'ABCDDDDD'.");
      } else {
         this.say("ABCDDDDD = true");
         this.showParens(r);
      }

      r = new RE("(A*)b\\1");
      r.setMatchFlags(1);
      if (!r.match("AaAaaaBAAAAAA")) {
         this.fail("Did not match 'AaAaaaBAAAAAA'.");
      } else {
         this.say("AaAaaaBAAAAAA = true");
         this.showParens(r);
      }

      r = new RE("[A-Z]*");
      r.setMatchFlags(1);
      if (!r.match("CaBgDe12")) {
         this.fail("Did not match 'CaBgDe12'.");
      } else {
         this.say("CaBgDe12 = true");
         this.showParens(r);
      }

      r = new RE("^abc$", 2);
      if (!r.match("\nabc")) {
         this.fail("\"\\nabc\" doesn't match \"^abc$\"");
      }

      if (!r.match("\rabc")) {
         this.fail("\"\\rabc\" doesn't match \"^abc$\"");
      }

      if (!r.match("\r\nabc")) {
         this.fail("\"\\r\\nabc\" doesn't match \"^abc$\"");
      }

      if (!r.match("\u0085abc")) {
         this.fail("\"\\u0085abc\" doesn't match \"^abc$\"");
      }

      if (!r.match("\u2028abc")) {
         this.fail("\"\\u2028abc\" doesn't match \"^abc$\"");
      }

      if (!r.match("\u2029abc")) {
         this.fail("\"\\u2029abc\" doesn't match \"^abc$\"");
      }

      r = new RE("^a.*b$", 2);
      if (r.match("a\nb")) {
         this.fail("\"a\\nb\" matches \"^a.*b$\"");
      }

      if (r.match("a\rb")) {
         this.fail("\"a\\rb\" matches \"^a.*b$\"");
      }

      if (r.match("a\r\nb")) {
         this.fail("\"a\\r\\nb\" matches \"^a.*b$\"");
      }

      if (r.match("a\u0085b")) {
         this.fail("\"a\\u0085b\" matches \"^a.*b$\"");
      }

      if (r.match("a\u2028b")) {
         this.fail("\"a\\u2028b\" matches \"^a.*b$\"");
      }

      if (r.match("a\u2029b")) {
         this.fail("\"a\\u2029b\" matches \"^a.*b$\"");
      }

   }

   private void testPrecompiledRE() {
      char[] re1Instructions = new char[]{'|', '\u0000', '\u001a', '|', '\u0000', '\r', 'A', '\u0001', '\u0004', 'a', '|', '\u0000', '\u0003', 'G', '\u0000', '\ufff6', '|', '\u0000', '\u0003', 'N', '\u0000', '\u0003', 'A', '\u0001', '\u0004', 'b', 'E', '\u0000', '\u0000'};
      REProgram re1 = new REProgram(re1Instructions);
      RE r = new RE(re1);
      this.say("a*b");
      boolean result = r.match("aaab");
      this.say("aaab = " + result);
      this.showParens(r);
      if (!result) {
         this.fail("\"aaab\" doesn't match to precompiled \"a*b\"");
      }

      result = r.match("b");
      this.say("b = " + result);
      this.showParens(r);
      if (!result) {
         this.fail("\"b\" doesn't match to precompiled \"a*b\"");
      }

      result = r.match("c");
      this.say("c = " + result);
      this.showParens(r);
      if (result) {
         this.fail("\"c\" matches to precompiled \"a*b\"");
      }

      result = r.match("ccccaaaaab");
      this.say("ccccaaaaab = " + result);
      this.showParens(r);
      if (!result) {
         this.fail("\"ccccaaaaab\" doesn't match to precompiled \"a*b\"");
      }

   }

   private void testSplitAndGrep() {
      String[] expected = new String[]{"xxxx", "xxxx", "yyyy", "zzz"};
      RE r = new RE("a*b");
      String[] s = r.split("xxxxaabxxxxbyyyyaaabzzz");

      int i;
      for(i = 0; i < expected.length && i < s.length; ++i) {
         this.assertEquals("Wrong splitted part", expected[i], s[i]);
      }

      this.assertEquals("Wrong number of splitted parts", expected.length, s.length);
      r = new RE("x+");
      expected = new String[]{"xxxx", "xxxx"};
      s = r.grep(s);

      for(i = 0; i < s.length; ++i) {
         this.say("s[" + i + "] = " + s[i]);
         this.assertEquals("Grep fails", expected[i], s[i]);
      }

      this.assertEquals("Wrong number of string found by grep", expected.length, s.length);
   }

   private void testSubst() {
      RE r = new RE("a*b");
      String expected = "-foo-garply-wacky-";
      String actual = r.subst("aaaabfooaaabgarplyaaabwackyb", "-");
      this.assertEquals("Wrong result of substitution in \"a*b\"", expected, actual);
      r = new RE("http://[\\.\\w\\-\\?/~_@&=%]+");
      actual = r.subst("visit us: http://www.apache.org!", "1234<a href=\"$0\">$0</a>", 2);
      this.assertEquals("Wrong subst() result", "visit us: 1234<a href=\"http://www.apache.org\">http://www.apache.org</a>!", actual);
      r = new RE("(.*?)=(.*)");
      actual = r.subst("variable=value", "$1_test_$212", 2);
      this.assertEquals("Wrong subst() result", "variable_test_value12", actual);
      r = new RE("^a$");
      actual = r.subst("a", "b", 2);
      this.assertEquals("Wrong subst() result", "b", actual);
      r = new RE("^a$", 2);
      actual = r.subst("\r\na\r\n", "b", 2);
      this.assertEquals("Wrong subst() result", "\r\nb\r\n", actual);
   }

   public void assertEquals(String message, String expected, String actual) {
      if (expected != null && !expected.equals(actual) || actual != null && !actual.equals(expected)) {
         this.fail(message + " (expected \"" + expected + "\", actual \"" + actual + "\")");
      }

   }

   public void assertEquals(String message, int expected, int actual) {
      if (expected != actual) {
         this.fail(message + " (expected \"" + expected + "\", actual \"" + actual + "\")");
      }

   }

   private boolean getExpectedResult(String yesno) {
      if ("NO".equals(yesno)) {
         return false;
      } else if ("YES".equals(yesno)) {
         return true;
      } else {
         this.die("Test script error!");
         return false;
      }
   }

   private String findNextTest(BufferedReader br) throws IOException {
      String number = "";

      while(br.ready()) {
         number = br.readLine();
         if (number == null) {
            break;
         }

         number = number.trim();
         if (number.startsWith("#")) {
            break;
         }

         if (!number.equals("")) {
            this.say("Script error.  Line = " + number);
            System.exit(-1);
         }
      }

      return number;
   }

   private RETestCase getNextTestCase(BufferedReader br) throws IOException {
      String tag = this.findNextTest(br);
      if (!br.ready()) {
         return null;
      } else {
         String expr = br.readLine();
         String matchAgainst = br.readLine();
         boolean badPattern = "ERR".equals(matchAgainst);
         boolean shouldMatch = false;
         int expectedParenCount = false;
         String[] expectedParens = null;
         if (!badPattern) {
            shouldMatch = this.getExpectedResult(br.readLine().trim());
            if (shouldMatch) {
               int expectedParenCount = Integer.parseInt(br.readLine().trim());
               expectedParens = new String[expectedParenCount];

               for(int i = 0; i < expectedParenCount; ++i) {
                  expectedParens[i] = br.readLine();
               }
            }
         }

         return new RETestCase(this, tag, expr, matchAgainst, badPattern, shouldMatch, expectedParens);
      }
   }
}
