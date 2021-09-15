package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.FramePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class Frame extends Window implements MenuContainer {
   /** @deprecated */
   @Deprecated
   public static final int DEFAULT_CURSOR = 0;
   /** @deprecated */
   @Deprecated
   public static final int CROSSHAIR_CURSOR = 1;
   /** @deprecated */
   @Deprecated
   public static final int TEXT_CURSOR = 2;
   /** @deprecated */
   @Deprecated
   public static final int WAIT_CURSOR = 3;
   /** @deprecated */
   @Deprecated
   public static final int SW_RESIZE_CURSOR = 4;
   /** @deprecated */
   @Deprecated
   public static final int SE_RESIZE_CURSOR = 5;
   /** @deprecated */
   @Deprecated
   public static final int NW_RESIZE_CURSOR = 6;
   /** @deprecated */
   @Deprecated
   public static final int NE_RESIZE_CURSOR = 7;
   /** @deprecated */
   @Deprecated
   public static final int N_RESIZE_CURSOR = 8;
   /** @deprecated */
   @Deprecated
   public static final int S_RESIZE_CURSOR = 9;
   /** @deprecated */
   @Deprecated
   public static final int W_RESIZE_CURSOR = 10;
   /** @deprecated */
   @Deprecated
   public static final int E_RESIZE_CURSOR = 11;
   /** @deprecated */
   @Deprecated
   public static final int HAND_CURSOR = 12;
   /** @deprecated */
   @Deprecated
   public static final int MOVE_CURSOR = 13;
   public static final int NORMAL = 0;
   public static final int ICONIFIED = 1;
   public static final int MAXIMIZED_HORIZ = 2;
   public static final int MAXIMIZED_VERT = 4;
   public static final int MAXIMIZED_BOTH = 6;
   Rectangle maximizedBounds;
   String title;
   MenuBar menuBar;
   boolean resizable;
   boolean undecorated;
   boolean mbManagement;
   private int state;
   Vector<Window> ownedWindows;
   private static final String base = "frame";
   private static int nameCounter = 0;
   private static final long serialVersionUID = 2673458971256075116L;
   private int frameSerializedDataVersion;

   public Frame() throws HeadlessException {
      this("");
   }

   public Frame(GraphicsConfiguration var1) {
      this("", var1);
   }

   public Frame(String var1) throws HeadlessException {
      this.title = "Untitled";
      this.resizable = true;
      this.undecorated = false;
      this.mbManagement = false;
      this.state = 0;
      this.frameSerializedDataVersion = 1;
      this.init(var1, (GraphicsConfiguration)null);
   }

   public Frame(String var1, GraphicsConfiguration var2) {
      super(var2);
      this.title = "Untitled";
      this.resizable = true;
      this.undecorated = false;
      this.mbManagement = false;
      this.state = 0;
      this.frameSerializedDataVersion = 1;
      this.init(var1, var2);
   }

   private void init(String var1, GraphicsConfiguration var2) {
      this.title = var1;
      SunToolkit.checkAndSetPolicy(this);
   }

   String constructComponentName() {
      Class var1 = Frame.class;
      synchronized(Frame.class) {
         return "frame" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createFrame(this);
         }

         FramePeer var2 = (FramePeer)this.peer;
         MenuBar var3 = this.menuBar;
         if (var3 != null) {
            this.mbManagement = true;
            var3.addNotify();
            var2.setMenuBar(var3);
         }

         var2.setMaximizedBounds(this.maximizedBounds);
         super.addNotify();
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      String var2 = this.title;
      if (var1 == null) {
         var1 = "";
      }

      synchronized(this) {
         this.title = var1;
         FramePeer var4 = (FramePeer)this.peer;
         if (var4 != null) {
            var4.setTitle(var1);
         }
      }

      this.firePropertyChange("title", var2, var1);
   }

   public Image getIconImage() {
      java.util.List var1 = this.icons;
      return var1 != null && var1.size() > 0 ? (Image)var1.get(0) : null;
   }

   public void setIconImage(Image var1) {
      super.setIconImage(var1);
   }

   public MenuBar getMenuBar() {
      return this.menuBar;
   }

   public void setMenuBar(MenuBar var1) {
      synchronized(this.getTreeLock()) {
         if (this.menuBar != var1) {
            if (var1 != null && var1.parent != null) {
               var1.parent.remove(var1);
            }

            if (this.menuBar != null) {
               this.remove(this.menuBar);
            }

            this.menuBar = var1;
            if (this.menuBar != null) {
               this.menuBar.parent = this;
               FramePeer var3 = (FramePeer)this.peer;
               if (var3 != null) {
                  this.mbManagement = true;
                  this.menuBar.addNotify();
                  this.invalidateIfValid();
                  var3.setMenuBar(this.menuBar);
               }
            }

         }
      }
   }

   public boolean isResizable() {
      return this.resizable;
   }

   public void setResizable(boolean var1) {
      boolean var2 = this.resizable;
      boolean var3 = false;
      synchronized(this) {
         this.resizable = var1;
         FramePeer var5 = (FramePeer)this.peer;
         if (var5 != null) {
            var5.setResizable(var1);
            var3 = true;
         }
      }

      if (var3) {
         this.invalidateIfValid();
      }

      this.firePropertyChange("resizable", var2, var1);
   }

   public synchronized void setState(int var1) {
      int var2 = this.getExtendedState();
      if (var1 == 1 && (var2 & 1) == 0) {
         this.setExtendedState(var2 | 1);
      } else if (var1 == 0 && (var2 & 1) != 0) {
         this.setExtendedState(var2 & -2);
      }

   }

   public void setExtendedState(int var1) {
      if (this.isFrameStateSupported(var1)) {
         synchronized(this.getObjectLock()) {
            this.state = var1;
         }

         FramePeer var2 = (FramePeer)this.peer;
         if (var2 != null) {
            var2.setState(var1);
         }

      }
   }

   private boolean isFrameStateSupported(int var1) {
      if (!this.getToolkit().isFrameStateSupported(var1)) {
         if ((var1 & 1) != 0 && !this.getToolkit().isFrameStateSupported(1)) {
            return false;
         } else {
            var1 &= -2;
            return this.getToolkit().isFrameStateSupported(var1);
         }
      } else {
         return true;
      }
   }

   public synchronized int getState() {
      return (this.getExtendedState() & 1) != 0 ? 1 : 0;
   }

   public int getExtendedState() {
      synchronized(this.getObjectLock()) {
         return this.state;
      }
   }

   public void setMaximizedBounds(Rectangle var1) {
      synchronized(this.getObjectLock()) {
         this.maximizedBounds = var1;
      }

      FramePeer var2 = (FramePeer)this.peer;
      if (var2 != null) {
         var2.setMaximizedBounds(var1);
      }

   }

   public Rectangle getMaximizedBounds() {
      synchronized(this.getObjectLock()) {
         return this.maximizedBounds;
      }
   }

   public void setUndecorated(boolean var1) {
      synchronized(this.getTreeLock()) {
         if (this.isDisplayable()) {
            throw new IllegalComponentStateException("The frame is displayable.");
         } else {
            if (!var1) {
               if (this.getOpacity() < 1.0F) {
                  throw new IllegalComponentStateException("The frame is not opaque");
               }

               if (this.getShape() != null) {
                  throw new IllegalComponentStateException("The frame does not have a default shape");
               }

               Color var3 = this.getBackground();
               if (var3 != null && var3.getAlpha() < 255) {
                  throw new IllegalComponentStateException("The frame background color is not opaque");
               }
            }

            this.undecorated = var1;
         }
      }
   }

   public boolean isUndecorated() {
      return this.undecorated;
   }

   public void setOpacity(float var1) {
      synchronized(this.getTreeLock()) {
         if (var1 < 1.0F && !this.isUndecorated()) {
            throw new IllegalComponentStateException("The frame is decorated");
         } else {
            super.setOpacity(var1);
         }
      }
   }

   public void setShape(Shape var1) {
      synchronized(this.getTreeLock()) {
         if (var1 != null && !this.isUndecorated()) {
            throw new IllegalComponentStateException("The frame is decorated");
         } else {
            super.setShape(var1);
         }
      }
   }

   public void setBackground(Color var1) {
      synchronized(this.getTreeLock()) {
         if (var1 != null && var1.getAlpha() < 255 && !this.isUndecorated()) {
            throw new IllegalComponentStateException("The frame is decorated");
         } else {
            super.setBackground(var1);
         }
      }
   }

   public void remove(MenuComponent var1) {
      if (var1 != null) {
         synchronized(this.getTreeLock()) {
            if (var1 == this.menuBar) {
               this.menuBar = null;
               FramePeer var3 = (FramePeer)this.peer;
               if (var3 != null) {
                  this.mbManagement = true;
                  this.invalidateIfValid();
                  var3.setMenuBar((MenuBar)null);
                  var1.removeNotify();
               }

               var1.parent = null;
            } else {
               super.remove(var1);
            }

         }
      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         FramePeer var2 = (FramePeer)this.peer;
         if (var2 != null) {
            this.getState();
            if (this.menuBar != null) {
               this.mbManagement = true;
               var2.setMenuBar((MenuBar)null);
               this.menuBar.removeNotify();
            }
         }

         super.removeNotify();
      }
   }

   void postProcessKeyEvent(KeyEvent var1) {
      if (this.menuBar != null && this.menuBar.handleShortcut(var1)) {
         var1.consume();
      } else {
         super.postProcessKeyEvent(var1);
      }
   }

   protected String paramString() {
      String var1 = super.paramString();
      if (this.title != null) {
         var1 = var1 + ",title=" + this.title;
      }

      if (this.resizable) {
         var1 = var1 + ",resizable";
      }

      int var2 = this.getExtendedState();
      if (var2 == 0) {
         var1 = var1 + ",normal";
      } else {
         if ((var2 & 1) != 0) {
            var1 = var1 + ",iconified";
         }

         if ((var2 & 6) == 6) {
            var1 = var1 + ",maximized";
         } else if ((var2 & 2) != 0) {
            var1 = var1 + ",maximized_horiz";
         } else if ((var2 & 4) != 0) {
            var1 = var1 + ",maximized_vert";
         }
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public void setCursor(int var1) {
      if (var1 >= 0 && var1 <= 13) {
         this.setCursor(Cursor.getPredefinedCursor(var1));
      } else {
         throw new IllegalArgumentException("illegal cursor type");
      }
   }

   /** @deprecated */
   @Deprecated
   public int getCursorType() {
      return this.getCursor().getType();
   }

   public static Frame[] getFrames() {
      Window[] var0 = Window.getWindows();
      int var1 = 0;
      Window[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Window var5 = var2[var4];
         if (var5 instanceof Frame) {
            ++var1;
         }
      }

      Frame[] var8 = new Frame[var1];
      var3 = 0;
      Window[] var9 = var0;
      int var10 = var0.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         Window var7 = var9[var6];
         if (var7 instanceof Frame) {
            var8[var3++] = (Frame)var7;
         }
      }

      return var8;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.icons != null && this.icons.size() > 0) {
         Image var2 = (Image)this.icons.get(0);
         if (var2 instanceof Serializable) {
            var1.writeObject(var2);
            return;
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      var1.defaultReadObject();

      try {
         Image var2 = (Image)var1.readObject();
         if (this.icons == null) {
            this.icons = new ArrayList();
            this.icons.add(var2);
         }
      } catch (OptionalDataException var3) {
         if (!var3.eof) {
            throw var3;
         }
      }

      if (this.menuBar != null) {
         this.menuBar.parent = this;
      }

      if (this.ownedWindows != null) {
         for(int var4 = 0; var4 < this.ownedWindows.size(); ++var4) {
            this.connectOwnedWindow((Window)this.ownedWindows.elementAt(var4));
         }

         this.ownedWindows = null;
      }

   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Frame.AccessibleAWTFrame();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setFrameAccessor(new AWTAccessor.FrameAccessor() {
         public void setExtendedState(Frame var1, int var2) {
            synchronized(var1.getObjectLock()) {
               var1.state = var2;
            }
         }

         public int getExtendedState(Frame var1) {
            synchronized(var1.getObjectLock()) {
               return var1.state;
            }
         }

         public Rectangle getMaximizedBounds(Frame var1) {
            synchronized(var1.getObjectLock()) {
               return var1.maximizedBounds;
            }
         }
      });
   }

   protected class AccessibleAWTFrame extends Window.AccessibleAWTWindow {
      private static final long serialVersionUID = -6172960752956030250L;

      protected AccessibleAWTFrame() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.FRAME;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (Frame.this.getFocusOwner() != null) {
            var1.add(AccessibleState.ACTIVE);
         }

         if (Frame.this.isResizable()) {
            var1.add(AccessibleState.RESIZABLE);
         }

         return var1;
      }
   }
}
