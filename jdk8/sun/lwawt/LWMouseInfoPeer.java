package sun.lwawt;

import java.awt.Point;
import java.awt.Window;
import java.awt.peer.MouseInfoPeer;
import sun.awt.AWTAccessor;

public class LWMouseInfoPeer implements MouseInfoPeer {
   public int fillPointWithCoords(Point var1) {
      LWCursorManager var2 = LWToolkit.getLWToolkit().getCursorManager();
      Point var3 = var2.getCursorPosition();
      var1.x = var3.x;
      var1.y = var3.y;
      return 0;
   }

   public boolean isWindowUnderMouse(Window var1) {
      if (var1 == null) {
         return false;
      } else {
         LWWindowPeer var2 = (LWWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var1);
         if (var2 == null) {
            return false;
         } else {
            return LWToolkit.getLWToolkit().getPlatformWindowUnderMouse() == var2.getPlatformWindow();
         }
      }
   }
}
