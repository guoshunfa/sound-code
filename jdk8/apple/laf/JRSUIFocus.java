package apple.laf;

public class JRSUIFocus {
   private static final int SUCCESS = 0;
   private static final int NULL_PTR = -1;
   private static final int NULL_CG_REF = -2;
   public static final int RING_ONLY = 0;
   public static final int RING_ABOVE = 1;
   public static final int RING_BELOW = 2;
   final long cgContext;

   private static native int beginNativeFocus(long var0, int var2);

   private static native int endNativeFocus(long var0);

   public JRSUIFocus(long var1) {
      this.cgContext = var1;
   }

   public void beginFocus(int var1) {
      testForFailure(beginNativeFocus(this.cgContext, var1));
   }

   public void endFocus() {
      testForFailure(endNativeFocus(this.cgContext));
   }

   static void testForFailure(int var0) {
      if (var0 != 0) {
         switch(var0) {
         case -2:
            throw new RuntimeException("Null CG reference in native JRSUI");
         case -1:
            throw new RuntimeException("Null pointer exception in native JRSUI");
         default:
            throw new RuntimeException("JRSUI draw focus problem: " + var0);
         }
      }
   }
}
