package sun.reflect.generics.visitor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import sun.reflect.generics.factory.GenericsFactory;
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
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public class Reifier implements TypeTreeVisitor<Type> {
   private Type resultType;
   private GenericsFactory factory;

   private Reifier(GenericsFactory var1) {
      this.factory = var1;
   }

   private GenericsFactory getFactory() {
      return this.factory;
   }

   public static Reifier make(GenericsFactory var0) {
      return new Reifier(var0);
   }

   private Type[] reifyTypeArguments(TypeArgument[] var1) {
      Type[] var2 = new Type[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var1[var3].accept(this);
         var2[var3] = this.resultType;
      }

      return var2;
   }

   public Type getResult() {
      assert this.resultType != null;

      return this.resultType;
   }

   public void visitFormalTypeParameter(FormalTypeParameter var1) {
      this.resultType = this.getFactory().makeTypeVariable(var1.getName(), var1.getBounds());
   }

   public void visitClassTypeSignature(ClassTypeSignature var1) {
      List var2 = var1.getPath();

      assert !var2.isEmpty();

      Iterator var3 = var2.iterator();
      SimpleClassTypeSignature var4 = (SimpleClassTypeSignature)var3.next();
      StringBuilder var5 = new StringBuilder(var4.getName());
      boolean var6 = var4.getDollar();

      while(var3.hasNext() && var4.getTypeArguments().length == 0) {
         var4 = (SimpleClassTypeSignature)var3.next();
         var6 = var4.getDollar();
         var5.append(var6 ? "$" : ".").append(var4.getName());
      }

      assert !var3.hasNext() || var4.getTypeArguments().length > 0;

      Type var7 = this.getFactory().makeNamedType(var5.toString());
      if (var4.getTypeArguments().length == 0) {
         assert !var3.hasNext();

         this.resultType = var7;
      } else {
         assert var4.getTypeArguments().length > 0;

         Type[] var8 = this.reifyTypeArguments(var4.getTypeArguments());
         ParameterizedType var9 = this.getFactory().makeParameterizedType(var7, var8, (Type)null);

         for(var6 = false; var3.hasNext(); var9 = this.getFactory().makeParameterizedType(var7, var8, var9)) {
            var4 = (SimpleClassTypeSignature)var3.next();
            var6 = var4.getDollar();
            var5.append(var6 ? "$" : ".").append(var4.getName());
            var7 = this.getFactory().makeNamedType(var5.toString());
            var8 = this.reifyTypeArguments(var4.getTypeArguments());
         }

         this.resultType = var9;
      }

   }

   public void visitArrayTypeSignature(ArrayTypeSignature var1) {
      var1.getComponentType().accept(this);
      Type var2 = this.resultType;
      this.resultType = this.getFactory().makeArrayType(var2);
   }

   public void visitTypeVariableSignature(TypeVariableSignature var1) {
      this.resultType = this.getFactory().findTypeVariable(var1.getIdentifier());
   }

   public void visitWildcard(Wildcard var1) {
      this.resultType = this.getFactory().makeWildcard(var1.getUpperBounds(), var1.getLowerBounds());
   }

   public void visitSimpleClassTypeSignature(SimpleClassTypeSignature var1) {
      this.resultType = this.getFactory().makeNamedType(var1.getName());
   }

   public void visitBottomSignature(BottomSignature var1) {
   }

   public void visitByteSignature(ByteSignature var1) {
      this.resultType = this.getFactory().makeByte();
   }

   public void visitBooleanSignature(BooleanSignature var1) {
      this.resultType = this.getFactory().makeBool();
   }

   public void visitShortSignature(ShortSignature var1) {
      this.resultType = this.getFactory().makeShort();
   }

   public void visitCharSignature(CharSignature var1) {
      this.resultType = this.getFactory().makeChar();
   }

   public void visitIntSignature(IntSignature var1) {
      this.resultType = this.getFactory().makeInt();
   }

   public void visitLongSignature(LongSignature var1) {
      this.resultType = this.getFactory().makeLong();
   }

   public void visitFloatSignature(FloatSignature var1) {
      this.resultType = this.getFactory().makeFloat();
   }

   public void visitDoubleSignature(DoubleSignature var1) {
      this.resultType = this.getFactory().makeDouble();
   }

   public void visitVoidDescriptor(VoidDescriptor var1) {
      this.resultType = this.getFactory().makeVoid();
   }
}
