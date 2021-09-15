package java.lang.ref;

public class SoftReference<T> extends Reference<T> {
   private static long clock;
   private long timestamp;

   public SoftReference(T var1) {
      super(var1);
      this.timestamp = clock;
   }

   public SoftReference(T var1, ReferenceQueue<? super T> var2) {
      super(var1, var2);
      this.timestamp = clock;
   }

   public T get() {
      Object var1 = super.get();
      if (var1 != null && this.timestamp != clock) {
         this.timestamp = clock;
      }

      return var1;
   }
}
