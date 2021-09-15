package sun.lwawt.macosx;

final class CWrapper {
   private CWrapper() {
   }

   static final class NSView {
      static native void addSubview(long var0, long var2);

      static native void removeFromSuperview(long var0);

      static native void setFrame(long var0, int var2, int var3, int var4, int var5);

      static native long window(long var0);

      static native void setHidden(long var0, boolean var2);

      static native void setToolTip(long var0, String var2);
   }

   static final class NSWindow {
      static final int NSWindowAbove = 1;
      static final int NSWindowBelow = -1;
      static final int NSWindowOut = 0;
      static final int MAX_WINDOW_LEVELS = 3;
      static final int NSNormalWindowLevel = 0;
      static final int NSFloatingWindowLevel = 1;
      static final int NSPopUpMenuWindowLevel = 2;

      static native void setLevel(long var0, int var2);

      static native void makeKeyAndOrderFront(long var0);

      static native void makeKeyWindow(long var0);

      static native void makeMainWindow(long var0);

      static native boolean canBecomeMainWindow(long var0);

      static native boolean isKeyWindow(long var0);

      static native void orderFront(long var0);

      static native void orderFrontRegardless(long var0);

      static native void orderWindow(long var0, int var2, long var3);

      static native void orderOut(long var0);

      static native void close(long var0);

      static native void addChildWindow(long var0, long var2, int var4);

      static native void removeChildWindow(long var0, long var2);

      static native void setAlphaValue(long var0, float var2);

      static native void setOpaque(long var0, boolean var2);

      static native void setBackgroundColor(long var0, int var2);

      static native void miniaturize(long var0);

      static native void deminiaturize(long var0);

      static native boolean isZoomed(long var0);

      static native void zoom(long var0);

      static native void makeFirstResponder(long var0, long var2);
   }
}
