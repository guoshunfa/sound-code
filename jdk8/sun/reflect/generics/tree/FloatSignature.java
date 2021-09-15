package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FloatSignature implements BaseType {
   private static final FloatSignature singleton = new FloatSignature();

   private FloatSignature() {
   }

   public static FloatSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitFloatSignature(this);
   }
}
