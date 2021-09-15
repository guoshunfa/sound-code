package java.util.regex;

public interface MatchResult {
   int start();

   int start(int var1);

   int end();

   int end(int var1);

   String group();

   String group(int var1);

   int groupCount();
}
