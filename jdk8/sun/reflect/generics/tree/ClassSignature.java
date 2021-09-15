package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class ClassSignature implements Signature {
   private final FormalTypeParameter[] formalTypeParams;
   private final ClassTypeSignature superclass;
   private final ClassTypeSignature[] superInterfaces;

   private ClassSignature(FormalTypeParameter[] var1, ClassTypeSignature var2, ClassTypeSignature[] var3) {
      this.formalTypeParams = var1;
      this.superclass = var2;
      this.superInterfaces = var3;
   }

   public static ClassSignature make(FormalTypeParameter[] var0, ClassTypeSignature var1, ClassTypeSignature[] var2) {
      return new ClassSignature(var0, var1, var2);
   }

   public FormalTypeParameter[] getFormalTypeParameters() {
      return this.formalTypeParams;
   }

   public ClassTypeSignature getSuperclass() {
      return this.superclass;
   }

   public ClassTypeSignature[] getSuperInterfaces() {
      return this.superInterfaces;
   }

   public void accept(Visitor<?> var1) {
      var1.visitClassSignature(this);
   }
}
