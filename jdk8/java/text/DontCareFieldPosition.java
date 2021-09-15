package java.text;

class DontCareFieldPosition extends FieldPosition {
   static final FieldPosition INSTANCE = new DontCareFieldPosition();
   private final Format.FieldDelegate noDelegate = new Format.FieldDelegate() {
      public void formatted(Format.Field var1, Object var2, int var3, int var4, StringBuffer var5) {
      }

      public void formatted(int var1, Format.Field var2, Object var3, int var4, int var5, StringBuffer var6) {
      }
   };

   private DontCareFieldPosition() {
      super(0);
   }

   Format.FieldDelegate getFieldDelegate() {
      return this.noDelegate;
   }
}
