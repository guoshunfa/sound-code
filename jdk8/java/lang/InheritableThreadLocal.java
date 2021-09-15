package java.lang;

public class InheritableThreadLocal<T> extends ThreadLocal<T> {
   protected T childValue(T var1) {
      return var1;
   }

   ThreadLocal.ThreadLocalMap getMap(Thread var1) {
      return var1.inheritableThreadLocals;
   }

   void createMap(Thread var1, T var2) {
      var1.inheritableThreadLocals = new ThreadLocal.ThreadLocalMap(this, var2);
   }
}
