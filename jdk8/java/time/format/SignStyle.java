package java.time.format;

public enum SignStyle {
   NORMAL,
   ALWAYS,
   NEVER,
   NOT_NEGATIVE,
   EXCEEDS_PAD;

   boolean parse(boolean var1, boolean var2, boolean var3) {
      switch(this.ordinal()) {
      case 0:
         return !var1 || !var2;
      case 1:
      case 4:
         return true;
      case 2:
      case 3:
      default:
         return !var2 && !var3;
      }
   }
}
