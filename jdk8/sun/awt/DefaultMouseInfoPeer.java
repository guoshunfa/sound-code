package sun.awt;

import java.awt.Point;
import java.awt.Window;
import java.awt.peer.MouseInfoPeer;

public class DefaultMouseInfoPeer implements MouseInfoPeer {
   DefaultMouseInfoPeer() {
   }

   public native int fillPointWithCoords(Point var1);

   public native boolean isWindowUnderMouse(Window var1);
}
