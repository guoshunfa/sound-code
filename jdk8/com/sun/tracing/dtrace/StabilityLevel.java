package com.sun.tracing.dtrace;

public enum StabilityLevel {
   INTERNAL(0),
   PRIVATE(1),
   OBSOLETE(2),
   EXTERNAL(3),
   UNSTABLE(4),
   EVOLVING(5),
   STABLE(6),
   STANDARD(7);

   private int encoding;

   String toDisplayString() {
      return this.toString().substring(0, 1) + this.toString().substring(1).toLowerCase();
   }

   public int getEncoding() {
      return this.encoding;
   }

   private StabilityLevel(int var3) {
      this.encoding = var3;
   }
}
