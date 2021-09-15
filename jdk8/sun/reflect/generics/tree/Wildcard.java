package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class Wildcard implements TypeArgument {
   private FieldTypeSignature[] upperBounds;
   private FieldTypeSignature[] lowerBounds;
   private static final FieldTypeSignature[] emptyBounds = new FieldTypeSignature[0];

   private Wildcard(FieldTypeSignature[] var1, FieldTypeSignature[] var2) {
      this.upperBounds = var1;
      this.lowerBounds = var2;
   }

   public static Wildcard make(FieldTypeSignature[] var0, FieldTypeSignature[] var1) {
      return new Wildcard(var0, var1);
   }

   public FieldTypeSignature[] getUpperBounds() {
      return this.upperBounds;
   }

   public FieldTypeSignature[] getLowerBounds() {
      return this.lowerBounds.length == 1 && this.lowerBounds[0] == BottomSignature.make() ? emptyBounds : this.lowerBounds;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitWildcard(this);
   }
}
