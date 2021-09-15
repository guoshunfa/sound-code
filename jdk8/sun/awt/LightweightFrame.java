package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.peer.FramePeer;
import java.util.List;

public abstract class LightweightFrame extends Frame {
   private int hostX;
   private int hostY;
   private int hostW;
   private int hostH;

   public LightweightFrame() {
      this.setUndecorated(true);
      this.setResizable(true);
      this.setEnabled(true);
   }

   public final Container getParent() {
      return null;
   }

   public Graphics getGraphics() {
      return null;
   }

   public final boolean isResizable() {
      return true;
   }

   public final void setTitle(String var1) {
   }

   public final void setIconImage(Image var1) {
   }

   public final void setIconImages(List<? extends Image> var1) {
   }

   public final void setMenuBar(MenuBar var1) {
   }

   public final void setResizable(boolean var1) {
   }

   public final void remove(MenuComponent var1) {
   }

   public final void toFront() {
   }

   public final void toBack() {
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.getPeer() == null) {
            SunToolkit var2 = (SunToolkit)Toolkit.getDefaultToolkit();

            try {
               this.setPeer(var2.createLightweightFrame(this));
            } catch (Exception var5) {
               throw new RuntimeException(var5);
            }
         }

         super.addNotify();
      }
   }

   private void setPeer(FramePeer var1) {
      AWTAccessor.getComponentAccessor().setPeer(this, var1);
   }

   public void emulateActivation(boolean var1) {
      ((FramePeer)this.getPeer()).emulateActivation(var1);
   }

   public abstract void grabFocus();

   public abstract void ungrabFocus();

   public abstract int getScaleFactor();

   public abstract void notifyDisplayChanged(int var1);

   public Rectangle getHostBounds() {
      return this.hostX == 0 && this.hostY == 0 && this.hostW == 0 && this.hostH == 0 ? this.getBounds() : new Rectangle(this.hostX, this.hostY, this.hostW, this.hostH);
   }

   public void setHostBounds(int var1, int var2, int var3, int var4) {
      this.hostX = var1;
      this.hostY = var2;
      this.hostW = var3;
      this.hostH = var4;
   }

   public abstract <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5);

   public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException;

   public abstract void addDropTarget(DropTarget var1);

   public abstract void removeDropTarget(DropTarget var1);
}
