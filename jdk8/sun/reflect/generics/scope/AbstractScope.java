package sun.reflect.generics.scope;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public abstract class AbstractScope<D extends GenericDeclaration> implements Scope {
   private final D recvr;
   private volatile Scope enclosingScope;

   protected AbstractScope(D var1) {
      this.recvr = var1;
   }

   protected D getRecvr() {
      return this.recvr;
   }

   protected abstract Scope computeEnclosingScope();

   protected Scope getEnclosingScope() {
      Scope var1 = this.enclosingScope;
      if (var1 == null) {
         var1 = this.computeEnclosingScope();
         this.enclosingScope = var1;
      }

      return var1;
   }

   public TypeVariable<?> lookup(String var1) {
      TypeVariable[] var2 = this.getRecvr().getTypeParameters();
      TypeVariable[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TypeVariable var6 = var3[var5];
         if (var6.getName().equals(var1)) {
            return var6;
         }
      }

      return this.getEnclosingScope().lookup(var1);
   }
}
