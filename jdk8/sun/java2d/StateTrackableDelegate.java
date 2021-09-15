package sun.java2d;

public final class StateTrackableDelegate implements StateTrackable {
   public static final StateTrackableDelegate UNTRACKABLE_DELEGATE;
   public static final StateTrackableDelegate IMMUTABLE_DELEGATE;
   private StateTrackable.State theState;
   StateTracker theTracker;
   private int numDynamicAgents;

   public static StateTrackableDelegate createInstance(StateTrackable.State var0) {
      switch(var0) {
      case UNTRACKABLE:
         return UNTRACKABLE_DELEGATE;
      case STABLE:
         return new StateTrackableDelegate(StateTrackable.State.STABLE);
      case DYNAMIC:
         return new StateTrackableDelegate(StateTrackable.State.DYNAMIC);
      case IMMUTABLE:
         return IMMUTABLE_DELEGATE;
      default:
         throw new InternalError("unknown state");
      }
   }

   private StateTrackableDelegate(StateTrackable.State var1) {
      this.theState = var1;
   }

   public StateTrackable.State getState() {
      return this.theState;
   }

   public synchronized StateTracker getStateTracker() {
      StateTracker var1 = this.theTracker;
      if (var1 == null) {
         switch(this.theState) {
         case UNTRACKABLE:
         case DYNAMIC:
            var1 = StateTracker.NEVER_CURRENT;
            break;
         case STABLE:
            var1 = new StateTracker() {
               public boolean isCurrent() {
                  return StateTrackableDelegate.this.theTracker == this;
               }
            };
            break;
         case IMMUTABLE:
            var1 = StateTracker.ALWAYS_CURRENT;
         }

         this.theTracker = var1;
      }

      return var1;
   }

   public synchronized void setImmutable() {
      if (this.theState != StateTrackable.State.UNTRACKABLE && this.theState != StateTrackable.State.DYNAMIC) {
         this.theState = StateTrackable.State.IMMUTABLE;
         this.theTracker = null;
      } else {
         throw new IllegalStateException("UNTRACKABLE or DYNAMIC objects cannot become IMMUTABLE");
      }
   }

   public synchronized void setUntrackable() {
      if (this.theState == StateTrackable.State.IMMUTABLE) {
         throw new IllegalStateException("IMMUTABLE objects cannot become UNTRACKABLE");
      } else {
         this.theState = StateTrackable.State.UNTRACKABLE;
         this.theTracker = null;
      }
   }

   public synchronized void addDynamicAgent() {
      if (this.theState == StateTrackable.State.IMMUTABLE) {
         throw new IllegalStateException("Cannot change state from IMMUTABLE");
      } else {
         ++this.numDynamicAgents;
         if (this.theState == StateTrackable.State.STABLE) {
            this.theState = StateTrackable.State.DYNAMIC;
            this.theTracker = null;
         }

      }
   }

   protected synchronized void removeDynamicAgent() {
      if (--this.numDynamicAgents == 0 && this.theState == StateTrackable.State.DYNAMIC) {
         this.theState = StateTrackable.State.STABLE;
         this.theTracker = null;
      }

   }

   public final void markDirty() {
      this.theTracker = null;
   }

   static {
      UNTRACKABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.UNTRACKABLE);
      IMMUTABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.IMMUTABLE);
   }
}
