package java.lang.ref;

public class WeakReference<T> extends Reference<T> {
   public WeakReference(T var1) {
      super(var1);
   }

   public WeakReference(T var1, ReferenceQueue<? super T> var2) {
      super(var1, var2);
   }
}
