package com.apple.eawt;

class _AppMiscHandlers {
   private static native void nativeOpenHelpViewer();

   private static native void nativeRequestActivation(boolean var0);

   private static native void nativeRequestUserAttention(boolean var0);

   private static native void nativeEnableSuddenTermination();

   private static native void nativeDisableSuddenTermination();

   static void openHelpViewer() {
      nativeOpenHelpViewer();
   }

   static void requestActivation(boolean var0) {
      nativeRequestActivation(var0);
   }

   static void requestUserAttention(boolean var0) {
      nativeRequestUserAttention(var0);
   }

   static void enableSuddenTermination() {
      nativeEnableSuddenTermination();
   }

   static void disableSuddenTermination() {
      nativeDisableSuddenTermination();
   }
}
