package sun.lwawt.macosx;

import java.awt.Insets;
import sun.lwawt.PlatformComponent;
import sun.lwawt.PlatformWindow;

class CPlatformComponent extends CFRetainedResource implements PlatformComponent {
   private volatile PlatformWindow platformWindow;

   CPlatformComponent() {
      super(0L, true);
   }

   public long getPointer() {
      return this.ptr;
   }

   public void initialize(PlatformWindow var1) {
      this.platformWindow = var1;
      this.setPtr(this.nativeCreateComponent(var1.getLayerPtr()));
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      Insets var5 = this.platformWindow.getPeer().getInsets();
      this.execute((var6) -> {
         this.nativeSetBounds(var6, var1 - var5.left, var2 - var5.top, var3, var4);
      });
   }

   public void dispose() {
      super.dispose();
   }

   private native long nativeCreateComponent(long var1);

   private native void nativeSetBounds(long var1, int var3, int var4, int var5, int var6);
}
