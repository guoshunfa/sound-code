package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class DoubleSignature implements BaseType {
   private static final DoubleSignature singleton = new DoubleSignature();

   private DoubleSignature() {
   }

   public static DoubleSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitDoubleSignature(this);
   }
}
