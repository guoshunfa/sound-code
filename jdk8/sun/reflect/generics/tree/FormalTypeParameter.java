package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FormalTypeParameter implements TypeTree {
   private final String name;
   private final FieldTypeSignature[] bounds;

   private FormalTypeParameter(String var1, FieldTypeSignature[] var2) {
      this.name = var1;
      this.bounds = var2;
   }

   public static FormalTypeParameter make(String var0, FieldTypeSignature[] var1) {
      return new FormalTypeParameter(var0, var1);
   }

   public FieldTypeSignature[] getBounds() {
      return this.bounds;
   }

   public String getName() {
      return this.name;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitFormalTypeParameter(this);
   }
}
