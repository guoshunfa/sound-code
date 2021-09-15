package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ByteSignature implements BaseType {
   private static final ByteSignature singleton = new ByteSignature();

   private ByteSignature() {
   }

   public static ByteSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitByteSignature(this);
   }
}
