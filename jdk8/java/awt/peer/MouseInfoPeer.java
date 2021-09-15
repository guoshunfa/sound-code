package java.awt.peer;

import java.awt.Point;
import java.awt.Window;

public interface MouseInfoPeer {
   int fillPointWithCoords(Point var1);

   boolean isWindowUnderMouse(Window var1);
}
