package sun.lwawt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.ComponentPeer;
import sun.awt.AWTAccessor;
import sun.awt.RepaintArea;

final class LWRepaintArea extends RepaintArea {
   protected void updateComponent(Component var1, Graphics var2) {
      if (var1 != null) {
         super.updateComponent(var1, var2);
         LWComponentPeer.flushOnscreenGraphics();
      }

   }

   protected void paintComponent(Component var1, Graphics var2) {
      if (var1 != null) {
         ComponentPeer var3 = AWTAccessor.getComponentAccessor().getPeer(var1);
         if (var3 != null) {
            ((LWComponentPeer)var3).paintPeer(var2);
         }

         super.paintComponent(var1, var2);
         LWComponentPeer.flushOnscreenGraphics();
      }

   }
}
