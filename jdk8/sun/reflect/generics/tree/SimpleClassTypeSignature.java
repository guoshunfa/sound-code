package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class SimpleClassTypeSignature implements FieldTypeSignature {
   private final boolean dollar;
   private final String name;
   private final TypeArgument[] typeArgs;

   private SimpleClassTypeSignature(String var1, boolean var2, TypeArgument[] var3) {
      this.name = var1;
      this.dollar = var2;
      this.typeArgs = var3;
   }

   public static SimpleClassTypeSignature make(String var0, boolean var1, TypeArgument[] var2) {
      return new SimpleClassTypeSignature(var0, var1, var2);
   }

   public boolean getDollar() {
      return this.dollar;
   }

   public String getName() {
      return this.name;
   }

   public TypeArgument[] getTypeArguments() {
      return this.typeArgs;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitSimpleClassTypeSignature(this);
   }
}
