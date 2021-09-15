package java.lang.ref;

class FinalReference<T> extends Reference<T> {
   public FinalReference(T var1, ReferenceQueue<? super T> var2) {
      super(var1, var2);
   }
}
