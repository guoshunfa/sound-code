package sun.reflect.generics.repository;

import java.lang.reflect.TypeVariable;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.Signature;
import sun.reflect.generics.visitor.Reifier;

public abstract class GenericDeclRepository<S extends Signature> extends AbstractRepository<S> {
   private volatile TypeVariable<?>[] typeParams;

   protected GenericDeclRepository(String var1, GenericsFactory var2) {
      super(var1, var2);
   }

   public TypeVariable<?>[] getTypeParameters() {
      TypeVariable[] var1 = this.typeParams;
      if (var1 == null) {
         FormalTypeParameter[] var2 = ((Signature)this.getTree()).getFormalTypeParameters();
         var1 = new TypeVariable[var2.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Reifier var4 = this.getReifier();
            var2[var3].accept(var4);
            var1[var3] = (TypeVariable)var4.getResult();
         }

         this.typeParams = var1;
      }

      return (TypeVariable[])var1.clone();
   }
}
