package java.lang.invoke;

import java.io.Serializable;
import java.util.Arrays;

public class LambdaMetafactory {
   public static final int FLAG_SERIALIZABLE = 1;
   public static final int FLAG_MARKERS = 2;
   public static final int FLAG_BRIDGES = 4;
   private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
   private static final MethodType[] EMPTY_MT_ARRAY = new MethodType[0];

   public static CallSite metafactory(MethodHandles.Lookup var0, String var1, MethodType var2, MethodType var3, MethodHandle var4, MethodType var5) throws LambdaConversionException {
      InnerClassLambdaMetafactory var6 = new InnerClassLambdaMetafactory(var0, var2, var1, var3, var4, var5, false, EMPTY_CLASS_ARRAY, EMPTY_MT_ARRAY);
      var6.validateMetafactoryArgs();
      return var6.buildCallSite();
   }

   public static CallSite altMetafactory(MethodHandles.Lookup var0, String var1, MethodType var2, Object... var3) throws LambdaConversionException {
      MethodType var4 = (MethodType)var3[0];
      MethodHandle var5 = (MethodHandle)var3[1];
      MethodType var6 = (MethodType)var3[2];
      int var7 = (Integer)var3[3];
      int var10 = 4;
      Class[] var8;
      int var11;
      if ((var7 & 2) != 0) {
         var11 = (Integer)var3[var10++];
         var8 = new Class[var11];
         System.arraycopy(var3, var10, var8, 0, var11);
         var10 += var11;
      } else {
         var8 = EMPTY_CLASS_ARRAY;
      }

      MethodType[] var9;
      if ((var7 & 4) != 0) {
         var11 = (Integer)var3[var10++];
         var9 = new MethodType[var11];
         System.arraycopy(var3, var10, var9, 0, var11);
         int var10000 = var10 + var11;
      } else {
         var9 = EMPTY_MT_ARRAY;
      }

      boolean var17 = (var7 & 1) != 0;
      if (var17) {
         boolean var12 = Serializable.class.isAssignableFrom(var2.returnType());
         Class[] var13 = var8;
         int var14 = var8.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            Class var16 = var13[var15];
            var12 |= Serializable.class.isAssignableFrom(var16);
         }

         if (!var12) {
            var8 = (Class[])Arrays.copyOf((Object[])var8, var8.length + 1);
            var8[var8.length - 1] = Serializable.class;
         }
      }

      InnerClassLambdaMetafactory var18 = new InnerClassLambdaMetafactory(var0, var2, var1, var4, var5, var6, var17, var8, var9);
      var18.validateMetafactoryArgs();
      return var18.buildCallSite();
   }
}
