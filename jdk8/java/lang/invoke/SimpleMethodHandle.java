package java.lang.invoke;

final class SimpleMethodHandle extends BoundMethodHandle {
   static final BoundMethodHandle.SpeciesData SPECIES_DATA;

   private SimpleMethodHandle(MethodType var1, LambdaForm var2) {
      super(var1, var2);
   }

   static BoundMethodHandle make(MethodType var0, LambdaForm var1) {
      return new SimpleMethodHandle(var0, var1);
   }

   public BoundMethodHandle.SpeciesData speciesData() {
      return SPECIES_DATA;
   }

   BoundMethodHandle copyWith(MethodType var1, LambdaForm var2) {
      return make(var1, var2);
   }

   String internalProperties() {
      return "\n& Class=" + this.getClass().getSimpleName();
   }

   public int fieldCount() {
      return 0;
   }

   final BoundMethodHandle copyWithExtendL(MethodType var1, LambdaForm var2, Object var3) {
      return BoundMethodHandle.bindSingle(var1, var2, var3);
   }

   final BoundMethodHandle copyWithExtendI(MethodType var1, LambdaForm var2, int var3) {
      try {
         return SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(var1, var2, var3);
      } catch (Throwable var5) {
         throw MethodHandleStatics.uncaughtException(var5);
      }
   }

   final BoundMethodHandle copyWithExtendJ(MethodType var1, LambdaForm var2, long var3) {
      try {
         return SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(var1, var2, var3);
      } catch (Throwable var6) {
         throw MethodHandleStatics.uncaughtException(var6);
      }
   }

   final BoundMethodHandle copyWithExtendF(MethodType var1, LambdaForm var2, float var3) {
      try {
         return SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(var1, var2, var3);
      } catch (Throwable var5) {
         throw MethodHandleStatics.uncaughtException(var5);
      }
   }

   final BoundMethodHandle copyWithExtendD(MethodType var1, LambdaForm var2, double var3) {
      try {
         return SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(var1, var2, var3);
      } catch (Throwable var6) {
         throw MethodHandleStatics.uncaughtException(var6);
      }
   }

   static {
      SPECIES_DATA = BoundMethodHandle.SpeciesData.EMPTY;
   }
}
