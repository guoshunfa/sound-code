package sun.lwawt.macosx;

import java.awt.Dimension;
import java.awt.peer.SystemTrayPeer;

public class CSystemTray implements SystemTrayPeer {
   CSystemTray() {
   }

   public Dimension getTrayIconSize() {
      return new Dimension(20, 20);
   }
}
