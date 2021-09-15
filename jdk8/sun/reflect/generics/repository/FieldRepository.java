package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class FieldRepository extends AbstractRepository<TypeSignature> {
   private Type genericType;

   protected FieldRepository(String var1, GenericsFactory var2) {
      super(var1, var2);
   }

   protected TypeSignature parse(String var1) {
      return SignatureParser.make().parseTypeSig(var1);
   }

   public static FieldRepository make(String var0, GenericsFactory var1) {
      return new FieldRepository(var0, var1);
   }

   public Type getGenericType() {
      if (this.genericType == null) {
         Reifier var1 = this.getReifier();
         ((TypeSignature)this.getTree()).accept(var1);
         this.genericType = var1.getResult();
      }

      return this.genericType;
   }
}
