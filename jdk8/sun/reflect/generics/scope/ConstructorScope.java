package sun.reflect.generics.scope;

import java.lang.reflect.Constructor;

public class ConstructorScope extends AbstractScope<Constructor<?>> {
   private ConstructorScope(Constructor<?> var1) {
      super(var1);
   }

   private Class<?> getEnclosingClass() {
      return ((Constructor)this.getRecvr()).getDeclaringClass();
   }

   protected Scope computeEnclosingScope() {
      return ClassScope.make(this.getEnclosingClass());
   }

   public static ConstructorScope make(Constructor<?> var0) {
      return new ConstructorScope(var0);
   }
}
