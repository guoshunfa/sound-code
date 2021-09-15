package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.MethodTypeSignature;

public interface Visitor<T> extends TypeTreeVisitor<T> {
   void visitClassSignature(ClassSignature var1);

   void visitMethodTypeSignature(MethodTypeSignature var1);
}
