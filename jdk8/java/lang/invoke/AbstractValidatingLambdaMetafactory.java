package java.lang.invoke;

import sun.invoke.util.Wrapper;

abstract class AbstractValidatingLambdaMetafactory {
   final Class<?> targetClass;
   final MethodType invokedType;
   final Class<?> samBase;
   final String samMethodName;
   final MethodType samMethodType;
   final MethodHandle implMethod;
   final MethodHandleInfo implInfo;
   final int implKind;
   final boolean implIsInstanceMethod;
   final Class<?> implDefiningClass;
   final MethodType implMethodType;
   final MethodType instantiatedMethodType;
   final boolean isSerializable;
   final Class<?>[] markerInterfaces;
   final MethodType[] additionalBridges;

   AbstractValidatingLambdaMetafactory(MethodHandles.Lookup var1, MethodType var2, String var3, MethodType var4, MethodHandle var5, MethodType var6, boolean var7, Class<?>[] var8, MethodType[] var9) throws LambdaConversionException {
      if ((var1.lookupModes() & 2) == 0) {
         throw new LambdaConversionException(String.format("Invalid caller: %s", var1.lookupClass().getName()));
      } else {
         this.targetClass = var1.lookupClass();
         this.invokedType = var2;
         this.samBase = var2.returnType();
         this.samMethodName = var3;
         this.samMethodType = var4;
         this.implMethod = var5;
         this.implInfo = var1.revealDirect(var5);
         this.implKind = this.implInfo.getReferenceKind();
         this.implIsInstanceMethod = this.implKind == 5 || this.implKind == 7 || this.implKind == 9;
         this.implDefiningClass = this.implInfo.getDeclaringClass();
         this.implMethodType = this.implInfo.getMethodType();
         this.instantiatedMethodType = var6;
         this.isSerializable = var7;
         this.markerInterfaces = var8;
         this.additionalBridges = var9;
         if (!this.samBase.isInterface()) {
            throw new LambdaConversionException(String.format("Functional interface %s is not an interface", this.samBase.getName()));
         } else {
            Class[] var10 = var8;
            int var11 = var8.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               Class var13 = var10[var12];
               if (!var13.isInterface()) {
                  throw new LambdaConversionException(String.format("Marker interface %s is not an interface", var13.getName()));
               }
            }

         }
      }
   }

   abstract CallSite buildCallSite() throws LambdaConversionException;

   void validateMetafactoryArgs() throws LambdaConversionException {
      switch(this.implKind) {
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
         int var1 = this.implMethodType.parameterCount();
         int var2 = this.implIsInstanceMethod ? 1 : 0;
         int var3 = this.invokedType.parameterCount();
         int var4 = this.samMethodType.parameterCount();
         int var5 = this.instantiatedMethodType.parameterCount();
         if (var1 + var2 != var3 + var4) {
            throw new LambdaConversionException(String.format("Incorrect number of parameters for %s method %s; %d captured parameters, %d functional interface method parameters, %d implementation parameters", this.implIsInstanceMethod ? "instance" : "static", this.implInfo, var3, var4, var1));
         } else if (var5 != var4) {
            throw new LambdaConversionException(String.format("Incorrect number of parameters for %s method %s; %d instantiated parameters, %d functional interface method parameters", this.implIsInstanceMethod ? "instance" : "static", this.implInfo, var5, var4));
         } else {
            MethodType[] var6 = this.additionalBridges;
            int var7 = var6.length;

            int var8;
            for(var8 = 0; var8 < var7; ++var8) {
               MethodType var9 = var6[var8];
               if (var9.parameterCount() != var4) {
                  throw new LambdaConversionException(String.format("Incorrect number of parameters for bridge signature %s; incompatible with %s", var9, this.samMethodType));
               }
            }

            byte var17;
            byte var18;
            if (this.implIsInstanceMethod) {
               Class var19;
               if (var3 == 0) {
                  var17 = 0;
                  var18 = 1;
                  var19 = this.instantiatedMethodType.parameterType(0);
               } else {
                  var17 = 1;
                  var18 = 0;
                  var19 = this.invokedType.parameterType(0);
               }

               if (!this.implDefiningClass.isAssignableFrom(var19)) {
                  throw new LambdaConversionException(String.format("Invalid receiver type %s; not a subtype of implementation type %s", var19, this.implDefiningClass));
               }

               Class var20 = this.implMethod.type().parameterType(0);
               if (var20 != this.implDefiningClass && !var20.isAssignableFrom(var19)) {
                  throw new LambdaConversionException(String.format("Invalid receiver type %s; not a subtype of implementation receiver type %s", var19, var20));
               }
            } else {
               var17 = 0;
               var18 = 0;
            }

            var8 = var3 - var17;

            Class var10;
            Class var11;
            int var21;
            for(var21 = 0; var21 < var8; ++var21) {
               var10 = this.implMethodType.parameterType(var21);
               var11 = this.invokedType.parameterType(var21 + var17);
               if (!var11.equals(var10)) {
                  throw new LambdaConversionException(String.format("Type mismatch in captured lambda parameter %d: expecting %s, found %s", var21, var11, var10));
               }
            }

            var21 = var18 - var8;

            Class var12;
            for(int var22 = var8; var22 < var1; ++var22) {
               var11 = this.implMethodType.parameterType(var22);
               var12 = this.instantiatedMethodType.parameterType(var22 + var21);
               if (!this.isAdaptableTo(var12, var11, true)) {
                  throw new LambdaConversionException(String.format("Type mismatch for lambda argument %d: %s is not convertible to %s", var22, var12, var11));
               }
            }

            var10 = this.instantiatedMethodType.returnType();
            var11 = this.implKind == 8 ? this.implDefiningClass : this.implMethodType.returnType();
            var12 = this.samMethodType.returnType();
            if (!this.isAdaptableToAsReturn(var11, var10)) {
               throw new LambdaConversionException(String.format("Type mismatch for lambda return: %s is not convertible to %s", var11, var10));
            } else if (!this.isAdaptableToAsReturnStrict(var10, var12)) {
               throw new LambdaConversionException(String.format("Type mismatch for lambda expected return: %s is not convertible to %s", var10, var12));
            } else {
               MethodType[] var13 = this.additionalBridges;
               int var14 = var13.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  MethodType var16 = var13[var15];
                  if (!this.isAdaptableToAsReturnStrict(var10, var16.returnType())) {
                     throw new LambdaConversionException(String.format("Type mismatch for lambda expected return: %s is not convertible to %s", var10, var16.returnType()));
                  }
               }

               return;
            }
         }
      default:
         throw new LambdaConversionException(String.format("Unsupported MethodHandle kind: %s", this.implInfo));
      }
   }

   private boolean isAdaptableTo(Class<?> var1, Class<?> var2, boolean var3) {
      if (var1.equals(var2)) {
         return true;
      } else {
         Wrapper var4;
         Wrapper var5;
         if (var1.isPrimitive()) {
            var4 = Wrapper.forPrimitiveType(var1);
            if (var2.isPrimitive()) {
               var5 = Wrapper.forPrimitiveType(var2);
               return var5.isConvertibleFrom(var4);
            } else {
               return var2.isAssignableFrom(var4.wrapperType());
            }
         } else if (var2.isPrimitive()) {
            if (Wrapper.isWrapperType(var1) && (var4 = Wrapper.forWrapperType(var1)).primitiveType().isPrimitive()) {
               var5 = Wrapper.forPrimitiveType(var2);
               return var5.isConvertibleFrom(var4);
            } else {
               return !var3;
            }
         } else {
            return !var3 || var2.isAssignableFrom(var1);
         }
      }
   }

   private boolean isAdaptableToAsReturn(Class<?> var1, Class<?> var2) {
      return var2.equals(Void.TYPE) || !var1.equals(Void.TYPE) && this.isAdaptableTo(var1, var2, false);
   }

   private boolean isAdaptableToAsReturnStrict(Class<?> var1, Class<?> var2) {
      return var1.equals(Void.TYPE) ? var2.equals(Void.TYPE) : this.isAdaptableTo(var1, var2, true);
   }
}
