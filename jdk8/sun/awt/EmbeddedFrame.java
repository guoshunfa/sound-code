package sun.awt;

import java.applet.Applet;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.FramePeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

public abstract class EmbeddedFrame extends Frame implements KeyEventDispatcher, PropertyChangeListener {
   private boolean isCursorAllowed;
   private boolean supportsXEmbed;
   private KeyboardFocusManager appletKFM;
   private static final long serialVersionUID = 2967042741780317130L;
   protected static final boolean FORWARD = true;
   protected static final boolean BACKWARD = false;

   public boolean supportsXEmbed() {
      return this.supportsXEmbed && SunToolkit.needsXEmbed();
   }

   protected EmbeddedFrame(boolean var1) {
      this(0L, var1);
   }

   protected EmbeddedFrame() {
      this(0L);
   }

   /** @deprecated */
   @Deprecated
   protected EmbeddedFrame(int var1) {
      this((long)var1);
   }

   protected EmbeddedFrame(long var1) {
      this(var1, false);
   }

   protected EmbeddedFrame(long var1, boolean var3) {
      this.isCursorAllowed = true;
      this.supportsXEmbed = false;
      this.supportsXEmbed = var3;
      this.registerListeners();
   }

   public Container getParent() {
      return null;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (var1.getPropertyName().equals("managingFocus")) {
         if (var1.getNewValue() != Boolean.TRUE) {
            this.removeTraversingOutListeners((KeyboardFocusManager)var1.getSource());
            this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            if (this.isVisible()) {
               this.addTraversingOutListeners(this.appletKFM);
            }

         }
      }
   }

   private void addTraversingOutListeners(KeyboardFocusManager var1) {
      var1.addKeyEventDispatcher(this);
      var1.addPropertyChangeListener("managingFocus", this);
   }

   private void removeTraversingOutListeners(KeyboardFocusManager var1) {
      var1.removeKeyEventDispatcher(this);
      var1.removePropertyChangeListener("managingFocus", this);
   }

   public void registerListeners() {
      if (this.appletKFM != null) {
         this.removeTraversingOutListeners(this.appletKFM);
      }

      this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      if (this.isVisible()) {
         this.addTraversingOutListeners(this.appletKFM);
      }

   }

   public void show() {
      if (this.appletKFM != null) {
         this.addTraversingOutListeners(this.appletKFM);
      }

      super.show();
   }

   public void hide() {
      if (this.appletKFM != null) {
         this.removeTraversingOutListeners(this.appletKFM);
      }

      super.hide();
   }

   public boolean dispatchKeyEvent(KeyEvent var1) {
      Container var2 = AWTAccessor.getKeyboardFocusManagerAccessor().getCurrentFocusCycleRoot();
      if (this != var2) {
         return false;
      } else if (var1.getID() == 400) {
         return false;
      } else if (this.getFocusTraversalKeysEnabled() && !var1.isConsumed()) {
         AWTKeyStroke var3 = AWTKeyStroke.getAWTKeyStrokeForEvent(var1);
         Component var5 = var1.getComponent();
         Set var4 = this.getFocusTraversalKeys(0);
         Component var6;
         if (var4.contains(var3)) {
            var6 = this.getFocusTraversalPolicy().getLastComponent(this);
            if ((var5 == var6 || var6 == null) && this.traverseOut(true)) {
               var1.consume();
               return true;
            }
         }

         var4 = this.getFocusTraversalKeys(1);
         if (var4.contains(var3)) {
            var6 = this.getFocusTraversalPolicy().getFirstComponent(this);
            if ((var5 == var6 || var6 == null) && this.traverseOut(false)) {
               var1.consume();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean traverseIn(boolean var1) {
      Component var2 = null;
      if (var1) {
         var2 = this.getFocusTraversalPolicy().getFirstComponent(this);
      } else {
         var2 = this.getFocusTraversalPolicy().getLastComponent(this);
      }

      if (var2 != null) {
         AWTAccessor.getKeyboardFocusManagerAccessor().setMostRecentFocusOwner(this, var2);
         this.synthesizeWindowActivation(true);
      }

      return null != var2;
   }

   protected boolean traverseOut(boolean var1) {
      return false;
   }

   public void setTitle(String var1) {
   }

   public void setIconImage(Image var1) {
   }

   public void setIconImages(List<? extends Image> var1) {
   }

   public void setMenuBar(MenuBar var1) {
   }

   public void setResizable(boolean var1) {
   }

   public void remove(MenuComponent var1) {
   }

   public boolean isResizable() {
      return true;
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.getPeer() == null) {
            this.setPeer(new EmbeddedFrame.NullEmbeddedFramePeer());
         }

         super.addNotify();
      }
   }

   public void setCursorAllowed(boolean var1) {
      this.isCursorAllowed = var1;
      this.getPeer().updateCursorImmediately();
   }

   public boolean isCursorAllowed() {
      return this.isCursorAllowed;
   }

   public Cursor getCursor() {
      return this.isCursorAllowed ? super.getCursor() : Cursor.getPredefinedCursor(0);
   }

   protected void setPeer(ComponentPeer var1) {
      AWTAccessor.getComponentAccessor().setPeer(this, var1);
   }

   public void synthesizeWindowActivation(boolean var1) {
   }

   protected void setLocationPrivate(int var1, int var2) {
      Dimension var3 = this.getSize();
      this.setBoundsPrivate(var1, var2, var3.width, var3.height);
   }

   protected Point getLocationPrivate() {
      Rectangle var1 = this.getBoundsPrivate();
      return new Point(var1.x, var1.y);
   }

   protected void setBoundsPrivate(int var1, int var2, int var3, int var4) {
      FramePeer var5 = (FramePeer)this.getPeer();
      if (var5 != null) {
         var5.setBoundsPrivate(var1, var2, var3, var4);
      }

   }

   protected Rectangle getBoundsPrivate() {
      FramePeer var1 = (FramePeer)this.getPeer();
      return var1 != null ? var1.getBoundsPrivate() : this.getBounds();
   }

   public void toFront() {
   }

   public void toBack() {
   }

   public abstract void registerAccelerator(AWTKeyStroke var1);

   public abstract void unregisterAccelerator(AWTKeyStroke var1);

   public static Applet getAppletIfAncestorOf(Component var0) {
      Container var1 = var0.getParent();

      Applet var2;
      for(var2 = null; var1 != null && !(var1 instanceof EmbeddedFrame); var1 = var1.getParent()) {
         if (var1 instanceof Applet) {
            var2 = (Applet)var1;
         }
      }

      return var1 == null ? null : var2;
   }

   public void notifyModalBlocked(Dialog var1, boolean var2) {
   }

   private static class NullEmbeddedFramePeer extends NullComponentPeer implements FramePeer {
      private NullEmbeddedFramePeer() {
      }

      public void setTitle(String var1) {
      }

      public void setIconImage(Image var1) {
      }

      public void updateIconImages() {
      }

      public void setMenuBar(MenuBar var1) {
      }

      public void setResizable(boolean var1) {
      }

      public void setState(int var1) {
      }

      public int getState() {
         return 0;
      }

      public void setMaximizedBounds(Rectangle var1) {
      }

      public void toFront() {
      }

      public void toBack() {
      }

      public void updateFocusableWindowState() {
      }

      public void updateAlwaysOnTop() {
      }

      public void updateAlwaysOnTopState() {
      }

      public Component getGlobalHeavyweightFocusOwner() {
         return null;
      }

      public void setBoundsPrivate(int var1, int var2, int var3, int var4) {
         this.setBounds(var1, var2, var3, var4, 3);
      }

      public Rectangle getBoundsPrivate() {
         return this.getBounds();
      }

      public void setModalBlocked(Dialog var1, boolean var2) {
      }

      public void restack() {
         throw new UnsupportedOperationException();
      }

      public boolean isRestackSupported() {
         return false;
      }

      public boolean requestWindowFocus() {
         return false;
      }

      public void updateMinimumSize() {
      }

      public void setOpacity(float var1) {
      }

      public void setOpaque(boolean var1) {
      }

      public void updateWindow() {
      }

      public void repositionSecurityWarning() {
      }

      public void emulateActivation(boolean var1) {
      }

      // $FF: synthetic method
      NullEmbeddedFramePeer(Object var1) {
         this();
      }
   }
}
