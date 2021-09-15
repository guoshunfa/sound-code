package java.awt;

import sun.util.logging.PlatformLogger;

abstract class AttributeValue {
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.AttributeValue");
   private final int value;
   private final String[] names;

   protected AttributeValue(int var1, String[] var2) {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         log.finest("value = " + var1 + ", names = " + var2);
      }

      if (log.isLoggable(PlatformLogger.Level.FINER) && (var1 < 0 || var2 == null || var1 >= var2.length)) {
         log.finer("Assertion failed");
      }

      this.value = var1;
      this.names = var2;
   }

   public int hashCode() {
      return this.value;
   }

   public String toString() {
      return this.names[this.value];
   }
}
