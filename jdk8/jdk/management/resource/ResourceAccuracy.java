package jdk.management.resource;

public enum ResourceAccuracy {
   LOW,
   MEDIUM,
   HIGH,
   HIGHEST;

   public ResourceAccuracy improve() {
      if (this.equals(LOW)) {
         return MEDIUM;
      } else {
         return this.equals(MEDIUM) ? HIGH : HIGHEST;
      }
   }
}
