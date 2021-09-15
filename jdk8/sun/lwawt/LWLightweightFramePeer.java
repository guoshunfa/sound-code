package sun.lwawt;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import sun.awt.CausedFocusEvent;
import sun.awt.LightweightFrame;
import sun.awt.OverrideNativeWindowHandle;
import sun.swing.JLightweightFrame;
import sun.swing.SwingAccessor;

public class LWLightweightFramePeer extends LWWindowPeer implements OverrideNativeWindowHandle {
   private volatile long overriddenWindowHandle = 0L;

   public LWLightweightFramePeer(LightweightFrame var1, PlatformComponent var2, PlatformWindow var3) {
      super(var1, var2, var3, LWWindowPeer.PeerType.LW_FRAME);
   }

   private LightweightFrame getLwTarget() {
      return (LightweightFrame)this.getTarget();
   }

   public Graphics getGraphics() {
      return this.getLwTarget().getGraphics();
   }

   protected void setVisibleImpl(boolean var1) {
   }

   public boolean requestWindowFocus(CausedFocusEvent.Cause var1) {
      if (!this.focusAllowedFor()) {
         return false;
      } else if (this.getPlatformWindow().rejectFocusRequest(var1)) {
         return false;
      } else {
         Window var2 = LWKeyboardFocusManagerPeer.getInstance().getCurrentFocusedWindow();
         this.changeFocusedWindow(true, var2);
         return true;
      }
   }

   public Point getLocationOnScreen() {
      Rectangle var1 = this.getBounds();
      return new Point(var1.x, var1.y);
   }

   public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
      this.setBounds(var1, var2, var3, var4, var5, true, false);
   }

   public void addDropTarget(DropTarget var1) {
      this.getLwTarget().addDropTarget(var1);
   }

   public void removeDropTarget(DropTarget var1) {
      this.getLwTarget().removeDropTarget(var1);
   }

   public void grab() {
      this.getLwTarget().grabFocus();
   }

   public void ungrab() {
      this.getLwTarget().ungrabFocus();
   }

   public void updateCursorImmediately() {
      SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame)this.getLwTarget());
   }

   public void overrideWindowHandle(long var1) {
      this.overriddenWindowHandle = var1;
   }

   public long getOverriddenWindowHandle() {
      return this.overriddenWindowHandle;
   }
}
