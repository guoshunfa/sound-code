package sun.lwawt.macosx;

import sun.java2d.SurfaceData;
import sun.lwawt.LWWindowPeer;

public class CPlatformLWView extends CPlatformView {
   public void initialize(LWWindowPeer var1, CPlatformResponder var2) {
      this.initializeBase(var1, var2);
   }

   public long getAWTView() {
      return 0L;
   }

   public boolean isOpaque() {
      return true;
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
   }

   public SurfaceData replaceSurfaceData() {
      return null;
   }

   public SurfaceData getSurfaceData() {
      return null;
   }

   public void dispose() {
   }

   public long getWindowLayerPtr() {
      return 0L;
   }
}
