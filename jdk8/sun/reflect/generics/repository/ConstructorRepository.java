package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class ConstructorRepository extends GenericDeclRepository<MethodTypeSignature> {
   private Type[] paramTypes;
   private Type[] exceptionTypes;

   protected ConstructorRepository(String var1, GenericsFactory var2) {
      super(var1, var2);
   }

   protected MethodTypeSignature parse(String var1) {
      return SignatureParser.make().parseMethodSig(var1);
   }

   public static ConstructorRepository make(String var0, GenericsFactory var1) {
      return new ConstructorRepository(var0, var1);
   }

   public Type[] getParameterTypes() {
      if (this.paramTypes == null) {
         TypeSignature[] var1 = ((MethodTypeSignature)this.getTree()).getParameterTypes();
         Type[] var2 = new Type[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Reifier var4 = this.getReifier();
            var1[var3].accept(var4);
            var2[var3] = var4.getResult();
         }

         this.paramTypes = var2;
      }

      return (Type[])this.paramTypes.clone();
   }

   public Type[] getExceptionTypes() {
      if (this.exceptionTypes == null) {
         FieldTypeSignature[] var1 = ((MethodTypeSignature)this.getTree()).getExceptionTypes();
         Type[] var2 = new Type[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Reifier var4 = this.getReifier();
            var1[var3].accept(var4);
            var2[var3] = var4.getResult();
         }

         this.exceptionTypes = var2;
      }

      return (Type[])this.exceptionTypes.clone();
   }
}
