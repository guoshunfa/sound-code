package sun.reflect.generics.tree;

import java.util.List;
import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ClassTypeSignature implements FieldTypeSignature {
   private final List<SimpleClassTypeSignature> path;

   private ClassTypeSignature(List<SimpleClassTypeSignature> var1) {
      this.path = var1;
   }

   public static ClassTypeSignature make(List<SimpleClassTypeSignature> var0) {
      return new ClassTypeSignature(var0);
   }

   public List<SimpleClassTypeSignature> getPath() {
      return this.path;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitClassTypeSignature(this);
   }
}
