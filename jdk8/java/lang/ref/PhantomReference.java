package java.lang.ref;

public class PhantomReference<T> extends Reference<T> {
   public T get() {
      return null;
   }

   public PhantomReference(T var1, ReferenceQueue<? super T> var2) {
      super(var1, var2);
   }
}
