package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class MethodTypeSignature implements Signature {
   private final FormalTypeParameter[] formalTypeParams;
   private final TypeSignature[] parameterTypes;
   private final ReturnType returnType;
   private final FieldTypeSignature[] exceptionTypes;

   private MethodTypeSignature(FormalTypeParameter[] var1, TypeSignature[] var2, ReturnType var3, FieldTypeSignature[] var4) {
      this.formalTypeParams = var1;
      this.parameterTypes = var2;
      this.returnType = var3;
      this.exceptionTypes = var4;
   }

   public static MethodTypeSignature make(FormalTypeParameter[] var0, TypeSignature[] var1, ReturnType var2, FieldTypeSignature[] var3) {
      return new MethodTypeSignature(var0, var1, var2, var3);
   }

   public FormalTypeParameter[] getFormalTypeParameters() {
      return this.formalTypeParams;
   }

   public TypeSignature[] getParameterTypes() {
      return this.parameterTypes;
   }

   public ReturnType getReturnType() {
      return this.returnType;
   }

   public FieldTypeSignature[] getExceptionTypes() {
      return this.exceptionTypes;
   }

   public void accept(Visitor<?> var1) {
      var1.visitMethodTypeSignature(this);
   }
}
