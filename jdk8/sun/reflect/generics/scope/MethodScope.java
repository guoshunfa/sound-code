package sun.reflect.generics.scope;

import java.lang.reflect.Method;

public class MethodScope extends AbstractScope<Method> {
   private MethodScope(Method var1) {
      super(var1);
   }

   private Class<?> getEnclosingClass() {
      return ((Method)this.getRecvr()).getDeclaringClass();
   }

   protected Scope computeEnclosingScope() {
      return ClassScope.make(this.getEnclosingClass());
   }

   public static MethodScope make(Method var0) {
      return new MethodScope(var0);
   }
}
