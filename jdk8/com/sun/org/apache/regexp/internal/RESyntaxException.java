package com.sun.org.apache.regexp.internal;

public class RESyntaxException extends RuntimeException {
   public RESyntaxException(String s) {
      super("Syntax error: " + s);
   }
}
