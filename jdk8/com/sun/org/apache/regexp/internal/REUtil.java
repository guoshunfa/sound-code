package com.sun.org.apache.regexp.internal;

public class REUtil {
   private static final String complexPrefix = "complex:";

   public static RE createRE(String expression, int matchFlags) throws RESyntaxException {
      return expression.startsWith("complex:") ? new RE(expression.substring("complex:".length()), matchFlags) : new RE(RE.simplePatternToFullRegularExpression(expression), matchFlags);
   }

   public static RE createRE(String expression) throws RESyntaxException {
      return createRE(expression, 0);
   }
}
