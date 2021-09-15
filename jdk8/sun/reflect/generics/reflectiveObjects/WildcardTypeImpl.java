package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class WildcardTypeImpl extends LazyReflectiveObjectGenerator implements WildcardType {
   private Type[] upperBounds;
   private Type[] lowerBounds;
   private FieldTypeSignature[] upperBoundASTs;
   private FieldTypeSignature[] lowerBoundASTs;

   private WildcardTypeImpl(FieldTypeSignature[] var1, FieldTypeSignature[] var2, GenericsFactory var3) {
      super(var3);
      this.upperBoundASTs = var1;
      this.lowerBoundASTs = var2;
   }

   public static WildcardTypeImpl make(FieldTypeSignature[] var0, FieldTypeSignature[] var1, GenericsFactory var2) {
      return new WildcardTypeImpl(var0, var1, var2);
   }

   private FieldTypeSignature[] getUpperBoundASTs() {
      assert this.upperBounds == null;

      return this.upperBoundASTs;
   }

   private FieldTypeSignature[] getLowerBoundASTs() {
      assert this.lowerBounds == null;

      return this.lowerBoundASTs;
   }

   public Type[] getUpperBounds() {
      if (this.upperBounds == null) {
         FieldTypeSignature[] var1 = this.getUpperBoundASTs();
         Type[] var2 = new Type[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Reifier var4 = this.getReifier();
            var1[var3].accept(var4);
            var2[var3] = var4.getResult();
         }

         this.upperBounds = var2;
      }

      return (Type[])this.upperBounds.clone();
   }

   public Type[] getLowerBounds() {
      if (this.lowerBounds == null) {
         FieldTypeSignature[] var1 = this.getLowerBoundASTs();
         Type[] var2 = new Type[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Reifier var4 = this.getReifier();
            var1[var3].accept(var4);
            var2[var3] = var4.getResult();
         }

         this.lowerBounds = var2;
      }

      return (Type[])this.lowerBounds.clone();
   }

   public String toString() {
      Type[] var1 = this.getLowerBounds();
      Type[] var2 = var1;
      StringBuilder var3 = new StringBuilder();
      if (var1.length > 0) {
         var3.append("? super ");
      } else {
         Type[] var4 = this.getUpperBounds();
         if (var4.length <= 0 || var4[0].equals(Object.class)) {
            return "?";
         }

         var2 = var4;
         var3.append("? extends ");
      }

      assert var2.length > 0;

      boolean var9 = true;
      Type[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Type var8 = var5[var7];
         if (!var9) {
            var3.append(" & ");
         }

         var9 = false;
         var3.append(var8.getTypeName());
      }

      return var3.toString();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof WildcardType)) {
         return false;
      } else {
         WildcardType var2 = (WildcardType)var1;
         return Arrays.equals((Object[])this.getLowerBounds(), (Object[])var2.getLowerBounds()) && Arrays.equals((Object[])this.getUpperBounds(), (Object[])var2.getUpperBounds());
      }
   }

   public int hashCode() {
      Type[] var1 = this.getLowerBounds();
      Type[] var2 = this.getUpperBounds();
      return Arrays.hashCode((Object[])var1) ^ Arrays.hashCode((Object[])var2);
   }
}
