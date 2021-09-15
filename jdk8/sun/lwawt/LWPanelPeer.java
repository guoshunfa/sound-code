package sun.lwawt;

import java.awt.Panel;
import java.awt.peer.PanelPeer;
import javax.swing.JPanel;

final class LWPanelPeer extends LWContainerPeer<Panel, JPanel> implements PanelPeer {
   LWPanelPeer(Panel var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JPanel createDelegate() {
      return new JPanel();
   }
}
