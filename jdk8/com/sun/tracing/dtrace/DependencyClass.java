package com.sun.tracing.dtrace;

public enum DependencyClass {
   UNKNOWN(0),
   CPU(1),
   PLATFORM(2),
   GROUP(3),
   ISA(4),
   COMMON(5);

   private int encoding;

   public String toDisplayString() {
      return this.toString().substring(0, 1) + this.toString().substring(1).toLowerCase();
   }

   public int getEncoding() {
      return this.encoding;
   }

   private DependencyClass(int var3) {
      this.encoding = var3;
   }
}
