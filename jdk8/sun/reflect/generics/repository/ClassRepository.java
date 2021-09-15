package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class ClassRepository extends GenericDeclRepository<ClassSignature> {
   public static final ClassRepository NONE = make("Ljava/lang/Object;", (GenericsFactory)null);
   private volatile Type superclass;
   private volatile Type[] superInterfaces;

   private ClassRepository(String var1, GenericsFactory var2) {
      super(var1, var2);
   }

   protected ClassSignature parse(String var1) {
      return SignatureParser.make().parseClassSig(var1);
   }

   public static ClassRepository make(String var0, GenericsFactory var1) {
      return new ClassRepository(var0, var1);
   }

   public Type getSuperclass() {
      Type var1 = this.superclass;
      if (var1 == null) {
         Reifier var2 = this.getReifier();
         ((ClassSignature)this.getTree()).getSuperclass().accept(var2);
         var1 = var2.getResult();
         this.superclass = var1;
      }

      return var1;
   }

   public Type[] getSuperInterfaces() {
      Type[] var1 = this.superInterfaces;
      if (var1 == null) {
         ClassTypeSignature[] var2 = ((ClassSignature)this.getTree()).getSuperInterfaces();
         var1 = new Type[var2.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Reifier var4 = this.getReifier();
            var2[var3].accept(var4);
            var1[var3] = var4.getResult();
         }

         this.superInterfaces = var1;
      }

      return (Type[])var1.clone();
   }
}
