package com.sun.java.browser.dom;

public class DOMUnsupportedException extends Exception {
   private Throwable ex;
   private String msg;

   public DOMUnsupportedException() {
      this((Exception)null, (String)null);
   }

   public DOMUnsupportedException(String var1) {
      this((Exception)null, var1);
   }

   public DOMUnsupportedException(Exception var1) {
      this(var1, (String)null);
   }

   public DOMUnsupportedException(Exception var1, String var2) {
      this.ex = var1;
      this.msg = var2;
   }

   public String getMessage() {
      return this.msg;
   }

   public Throwable getCause() {
      return this.ex;
   }
}
