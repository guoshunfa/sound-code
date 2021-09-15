package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ArrayTypeSignature implements FieldTypeSignature {
   private final TypeSignature componentType;

   private ArrayTypeSignature(TypeSignature var1) {
      this.componentType = var1;
   }

   public static ArrayTypeSignature make(TypeSignature var0) {
      return new ArrayTypeSignature(var0);
   }

   public TypeSignature getComponentType() {
      return this.componentType;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitArrayTypeSignature(this);
   }
}
