package sun.nio.fs;

class MacOSXNativeDispatcher extends BsdNativeDispatcher {
   static final int kCFStringNormalizationFormC = 2;
   static final int kCFStringNormalizationFormD = 0;

   private MacOSXNativeDispatcher() {
   }

   static native char[] normalizepath(char[] var0, int var1);
}
