package sun.tracing;

class NullProbe extends ProbeSkeleton {
   public NullProbe(Class<?>[] var1) {
      super(var1);
   }

   public boolean isEnabled() {
      return false;
   }

   public void uncheckedTrigger(Object[] var1) {
   }
}
