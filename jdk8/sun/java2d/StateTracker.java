package sun.java2d;

public interface StateTracker {
   StateTracker ALWAYS_CURRENT = new StateTracker() {
      public boolean isCurrent() {
         return true;
      }
   };
   StateTracker NEVER_CURRENT = new StateTracker() {
      public boolean isCurrent() {
         return false;
      }
   };

   boolean isCurrent();
}
