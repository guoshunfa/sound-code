package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ShortSignature implements BaseType {
   private static final ShortSignature singleton = new ShortSignature();

   private ShortSignature() {
   }

   public static ShortSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitShortSignature(this);
   }
}
