package sun.reflect.generics.scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ClassScope extends AbstractScope<Class<?>> implements Scope {
   private ClassScope(Class<?> var1) {
      super(var1);
   }

   protected Scope computeEnclosingScope() {
      Class var1 = (Class)this.getRecvr();
      Method var2 = var1.getEnclosingMethod();
      if (var2 != null) {
         return MethodScope.make(var2);
      } else {
         Constructor var3 = var1.getEnclosingConstructor();
         if (var3 != null) {
            return ConstructorScope.make(var3);
         } else {
            Class var4 = var1.getEnclosingClass();
            return (Scope)(var4 != null ? make(var4) : DummyScope.make());
         }
      }
   }

   public static ClassScope make(Class<?> var0) {
      return new ClassScope(var0);
   }
}
