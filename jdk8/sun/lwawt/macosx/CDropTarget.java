package sun.lwawt.macosx;

import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.awt.peer.ComponentPeer;
import sun.lwawt.LWComponentPeer;

public final class CDropTarget {
   Component fComponent;
   ComponentPeer fPeer;
   DropTarget fDropTarget;
   private long fNativeDropTarget;

   public static CDropTarget createDropTarget(DropTarget var0, Component var1, ComponentPeer var2) {
      return new CDropTarget(var0, var1, var2);
   }

   private CDropTarget(DropTarget var1, Component var2, ComponentPeer var3) {
      this.fDropTarget = var1;
      this.fComponent = var2;
      this.fPeer = var3;
      long var4 = CPlatformWindow.getNativeViewPtr(((LWComponentPeer)var3).getPlatformWindow());
      if (var4 != 0L) {
         this.fNativeDropTarget = this.createNativeDropTarget(var1, var2, var3, var4);
         if (this.fNativeDropTarget == 0L) {
            throw new IllegalStateException("CDropTarget.createNativeDropTarget() failed.");
         }
      }
   }

   public DropTarget getDropTarget() {
      return this.fDropTarget;
   }

   public void dispose() {
      if (this.fNativeDropTarget != 0L) {
         this.releaseNativeDropTarget(this.fNativeDropTarget);
         this.fNativeDropTarget = 0L;
      }

   }

   protected native long createNativeDropTarget(DropTarget var1, Component var2, ComponentPeer var3, long var4);

   protected native void releaseNativeDropTarget(long var1);
}
