package sun.reflect.generics.reflectiveObjects;

import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;

public abstract class LazyReflectiveObjectGenerator {
   private final GenericsFactory factory;

   protected LazyReflectiveObjectGenerator(GenericsFactory var1) {
      this.factory = var1;
   }

   private GenericsFactory getFactory() {
      return this.factory;
   }

   protected Reifier getReifier() {
      return Reifier.make(this.getFactory());
   }
}
