package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public interface TypeTreeVisitor<T> {
   T getResult();

   void visitFormalTypeParameter(FormalTypeParameter var1);

   void visitClassTypeSignature(ClassTypeSignature var1);

   void visitArrayTypeSignature(ArrayTypeSignature var1);

   void visitTypeVariableSignature(TypeVariableSignature var1);

   void visitWildcard(Wildcard var1);

   void visitSimpleClassTypeSignature(SimpleClassTypeSignature var1);

   void visitBottomSignature(BottomSignature var1);

   void visitByteSignature(ByteSignature var1);

   void visitBooleanSignature(BooleanSignature var1);

   void visitShortSignature(ShortSignature var1);

   void visitCharSignature(CharSignature var1);

   void visitIntSignature(IntSignature var1);

   void visitLongSignature(LongSignature var1);

   void visitFloatSignature(FloatSignature var1);

   void visitDoubleSignature(DoubleSignature var1);

   void visitVoidDescriptor(VoidDescriptor var1);
}
