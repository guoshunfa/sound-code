package sun.lwawt.macosx;

import java.awt.Font;
import java.awt.MenuComponent;
import java.awt.peer.MenuComponentPeer;

abstract class CMenuComponent extends CFRetainedResource implements MenuComponentPeer {
   private final MenuComponent target;

   CMenuComponent(MenuComponent var1) {
      super(0L, true);
      this.target = var1;
      this.setPtr(this.createModel());
   }

   final MenuComponent getTarget() {
      return this.target;
   }

   abstract long createModel();

   public final void dispose() {
      super.dispose();
      LWCToolkit.targetDisposedPeer(this.target, this);
   }

   public final void setFont(Font var1) {
   }
}
