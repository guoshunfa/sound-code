package sun.lwawt.macosx;

import java.awt.AWTKeyStroke;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import sun.awt.EmbeddedFrame;
import sun.lwawt.LWWindowPeer;

public class CViewEmbeddedFrame extends EmbeddedFrame {
   private final long nsViewPtr;
   private boolean isActive = false;

   public CViewEmbeddedFrame(long var1) {
      this.nsViewPtr = var1;
   }

   public void addNotify() {
      if (this.getPeer() == null) {
         LWCToolkit var1 = (LWCToolkit)Toolkit.getDefaultToolkit();
         this.setPeer(var1.createEmbeddedFrame(this));
      }

      super.addNotify();
   }

   public long getEmbedderHandle() {
      return this.nsViewPtr;
   }

   public void registerAccelerator(AWTKeyStroke var1) {
   }

   public void unregisterAccelerator(AWTKeyStroke var1) {
   }

   public boolean isParentWindowActive() {
      return this.isActive;
   }

   public void synthesizeWindowActivation(boolean var1) {
      if (this.isActive != var1) {
         this.isActive = var1;
         ((LWWindowPeer)this.getPeer()).notifyActivation(var1, (LWWindowPeer)null);
      }

   }

   public void validateWithBounds(int var1, int var2, final int var3, final int var4) {
      try {
         LWCToolkit.invokeAndWait((Runnable)(new Runnable() {
            public void run() {
               ((LWWindowPeer)CViewEmbeddedFrame.this.getPeer()).setBoundsPrivate(0, 0, var3, var4);
               CViewEmbeddedFrame.this.validate();
               CViewEmbeddedFrame.this.setVisible(true);
            }
         }), this);
      } catch (InvocationTargetException var6) {
      }

   }
}
