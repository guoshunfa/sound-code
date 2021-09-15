package sun.lwawt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.peer.CanvasPeer;
import javax.swing.JComponent;

class LWCanvasPeer<T extends Component, D extends JComponent> extends LWComponentPeer<T, D> implements CanvasPeer {
   LWCanvasPeer(T var1, PlatformComponent var2) {
      super(var1, var2);
   }

   public final GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration var1) {
      return var1;
   }

   public final Dimension getPreferredSize() {
      return this.getMinimumSize();
   }

   public final Dimension getMinimumSize() {
      return this.getBounds().getSize();
   }
}
