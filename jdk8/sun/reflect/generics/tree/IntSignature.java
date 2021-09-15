package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class IntSignature implements BaseType {
   private static final IntSignature singleton = new IntSignature();

   private IntSignature() {
   }

   public static IntSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitIntSignature(this);
   }
}
