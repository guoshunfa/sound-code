package sun.java2d;

public interface StateTrackable {
   StateTrackable.State getState();

   StateTracker getStateTracker();

   public static enum State {
      IMMUTABLE,
      STABLE,
      DYNAMIC,
      UNTRACKABLE;
   }
}
