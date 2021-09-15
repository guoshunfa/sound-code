package java.awt.dnd;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TooManyListenersException;
import javax.swing.Timer;

public class DropTarget implements DropTargetListener, Serializable {
   private static final long serialVersionUID = -6283860791671019047L;
   private DropTargetContext dropTargetContext;
   private Component component;
   private transient ComponentPeer componentPeer;
   private transient ComponentPeer nativePeer;
   int actions;
   boolean active;
   private transient DropTarget.DropTargetAutoScroller autoScroller;
   private transient DropTargetListener dtListener;
   private transient FlavorMap flavorMap;
   private transient boolean isDraggingInside;

   public DropTarget(Component var1, int var2, DropTargetListener var3, boolean var4, FlavorMap var5) throws HeadlessException {
      this.dropTargetContext = this.createDropTargetContext();
      this.actions = 3;
      this.active = true;
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         this.component = var1;
         this.setDefaultActions(var2);
         if (var3 != null) {
            try {
               this.addDropTargetListener(var3);
            } catch (TooManyListenersException var7) {
            }
         }

         if (var1 != null) {
            var1.setDropTarget(this);
            this.setActive(var4);
         }

         if (var5 != null) {
            this.flavorMap = var5;
         } else {
            this.flavorMap = SystemFlavorMap.getDefaultFlavorMap();
         }

      }
   }

   public DropTarget(Component var1, int var2, DropTargetListener var3, boolean var4) throws HeadlessException {
      this(var1, var2, var3, var4, (FlavorMap)null);
   }

   public DropTarget() throws HeadlessException {
      this((Component)null, 3, (DropTargetListener)null, true, (FlavorMap)null);
   }

   public DropTarget(Component var1, DropTargetListener var2) throws HeadlessException {
      this(var1, 3, var2, true, (FlavorMap)null);
   }

   public DropTarget(Component var1, int var2, DropTargetListener var3) throws HeadlessException {
      this(var1, var2, var3, true);
   }

   public synchronized void setComponent(Component var1) {
      if (this.component != var1 && (this.component == null || !this.component.equals(var1))) {
         ComponentPeer var3 = null;
         Component var2;
         if ((var2 = this.component) != null) {
            this.clearAutoscroll();
            this.component = null;
            if (this.componentPeer != null) {
               var3 = this.componentPeer;
               this.removeNotify(this.componentPeer);
            }

            var2.setDropTarget((DropTarget)null);
         }

         if ((this.component = var1) != null) {
            try {
               var1.setDropTarget(this);
            } catch (Exception var5) {
               if (var2 != null) {
                  var2.setDropTarget(this);
                  this.addNotify(var3);
               }
            }
         }

      }
   }

   public synchronized Component getComponent() {
      return this.component;
   }

   public void setDefaultActions(int var1) {
      this.getDropTargetContext().setTargetActions(var1 & 1073741827);
   }

   void doSetDefaultActions(int var1) {
      this.actions = var1;
   }

   public int getDefaultActions() {
      return this.actions;
   }

   public synchronized void setActive(boolean var1) {
      if (var1 != this.active) {
         this.active = var1;
      }

      if (!this.active) {
         this.clearAutoscroll();
      }

   }

   public boolean isActive() {
      return this.active;
   }

   public synchronized void addDropTargetListener(DropTargetListener var1) throws TooManyListenersException {
      if (var1 != null) {
         if (this.equals(var1)) {
            throw new IllegalArgumentException("DropTarget may not be its own Listener");
         } else if (this.dtListener == null) {
            this.dtListener = var1;
         } else {
            throw new TooManyListenersException();
         }
      }
   }

   public synchronized void removeDropTargetListener(DropTargetListener var1) {
      if (var1 != null && this.dtListener != null) {
         if (!this.dtListener.equals(var1)) {
            throw new IllegalArgumentException("listener mismatch");
         }

         this.dtListener = null;
      }

   }

   public synchronized void dragEnter(DropTargetDragEvent var1) {
      this.isDraggingInside = true;
      if (this.active) {
         if (this.dtListener != null) {
            this.dtListener.dragEnter(var1);
         } else {
            var1.getDropTargetContext().setTargetActions(0);
         }

         this.initializeAutoscrolling(var1.getLocation());
      }
   }

   public synchronized void dragOver(DropTargetDragEvent var1) {
      if (this.active) {
         if (this.dtListener != null && this.active) {
            this.dtListener.dragOver(var1);
         }

         this.updateAutoscroll(var1.getLocation());
      }
   }

   public synchronized void dropActionChanged(DropTargetDragEvent var1) {
      if (this.active) {
         if (this.dtListener != null) {
            this.dtListener.dropActionChanged(var1);
         }

         this.updateAutoscroll(var1.getLocation());
      }
   }

   public synchronized void dragExit(DropTargetEvent var1) {
      this.isDraggingInside = false;
      if (this.active) {
         if (this.dtListener != null && this.active) {
            this.dtListener.dragExit(var1);
         }

         this.clearAutoscroll();
      }
   }

   public synchronized void drop(DropTargetDropEvent var1) {
      this.isDraggingInside = false;
      this.clearAutoscroll();
      if (this.dtListener != null && this.active) {
         this.dtListener.drop(var1);
      } else {
         var1.rejectDrop();
      }

   }

   public FlavorMap getFlavorMap() {
      return this.flavorMap;
   }

   public void setFlavorMap(FlavorMap var1) {
      this.flavorMap = var1 == null ? SystemFlavorMap.getDefaultFlavorMap() : var1;
   }

   public void addNotify(ComponentPeer var1) {
      if (var1 != this.componentPeer) {
         this.componentPeer = var1;

         for(Object var2 = this.component; var2 != null && var1 instanceof LightweightPeer; var2 = ((Component)var2).getParent()) {
            var1 = ((Component)var2).getPeer();
         }

         if (var1 instanceof DropTargetPeer) {
            this.nativePeer = var1;
            ((DropTargetPeer)var1).addDropTarget(this);
         } else {
            this.nativePeer = null;
         }

      }
   }

   public void removeNotify(ComponentPeer var1) {
      if (this.nativePeer != null) {
         ((DropTargetPeer)this.nativePeer).removeDropTarget(this);
      }

      this.componentPeer = this.nativePeer = null;
      synchronized(this) {
         if (this.isDraggingInside) {
            this.dragExit(new DropTargetEvent(this.getDropTargetContext()));
         }

      }
   }

   public DropTargetContext getDropTargetContext() {
      return this.dropTargetContext;
   }

   protected DropTargetContext createDropTargetContext() {
      return new DropTargetContext(this);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(SerializationTester.test(this.dtListener) ? this.dtListener : null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      ObjectInputStream.GetField var2 = var1.readFields();

      try {
         this.dropTargetContext = (DropTargetContext)var2.get("dropTargetContext", (Object)null);
      } catch (IllegalArgumentException var5) {
      }

      if (this.dropTargetContext == null) {
         this.dropTargetContext = this.createDropTargetContext();
      }

      this.component = (Component)var2.get("component", (Object)null);
      this.actions = var2.get("actions", (int)3);
      this.active = var2.get("active", true);

      try {
         this.dtListener = (DropTargetListener)var2.get("dtListener", (Object)null);
      } catch (IllegalArgumentException var4) {
         this.dtListener = (DropTargetListener)var1.readObject();
      }

   }

   protected DropTarget.DropTargetAutoScroller createDropTargetAutoScroller(Component var1, Point var2) {
      return new DropTarget.DropTargetAutoScroller(var1, var2);
   }

   protected void initializeAutoscrolling(Point var1) {
      if (this.component != null && this.component instanceof Autoscroll) {
         this.autoScroller = this.createDropTargetAutoScroller(this.component, var1);
      }
   }

   protected void updateAutoscroll(Point var1) {
      if (this.autoScroller != null) {
         this.autoScroller.updateLocation(var1);
      }

   }

   protected void clearAutoscroll() {
      if (this.autoScroller != null) {
         this.autoScroller.stop();
         this.autoScroller = null;
      }

   }

   protected static class DropTargetAutoScroller implements ActionListener {
      private Component component;
      private Autoscroll autoScroll;
      private Timer timer;
      private Point locn;
      private Point prev;
      private Rectangle outer = new Rectangle();
      private Rectangle inner = new Rectangle();
      private int hysteresis = 10;

      protected DropTargetAutoScroller(Component var1, Point var2) {
         this.component = var1;
         this.autoScroll = (Autoscroll)this.component;
         Toolkit var3 = Toolkit.getDefaultToolkit();
         Integer var4 = 100;
         Integer var5 = 100;

         try {
            var4 = (Integer)var3.getDesktopProperty("DnD.Autoscroll.initialDelay");
         } catch (Exception var9) {
         }

         try {
            var5 = (Integer)var3.getDesktopProperty("DnD.Autoscroll.interval");
         } catch (Exception var8) {
         }

         this.timer = new Timer(var5, this);
         this.timer.setCoalesce(true);
         this.timer.setInitialDelay(var4);
         this.locn = var2;
         this.prev = var2;

         try {
            this.hysteresis = (Integer)var3.getDesktopProperty("DnD.Autoscroll.cursorHysteresis");
         } catch (Exception var7) {
         }

         this.timer.start();
      }

      private void updateRegion() {
         Insets var1 = this.autoScroll.getAutoscrollInsets();
         Dimension var2 = this.component.getSize();
         if (var2.width != this.outer.width || var2.height != this.outer.height) {
            this.outer.reshape(0, 0, var2.width, var2.height);
         }

         if (this.inner.x != var1.left || this.inner.y != var1.top) {
            this.inner.setLocation(var1.left, var1.top);
         }

         int var3 = var2.width - (var1.left + var1.right);
         int var4 = var2.height - (var1.top + var1.bottom);
         if (var3 != this.inner.width || var4 != this.inner.height) {
            this.inner.setSize(var3, var4);
         }

      }

      protected synchronized void updateLocation(Point var1) {
         this.prev = this.locn;
         this.locn = var1;
         if (Math.abs(this.locn.x - this.prev.x) <= this.hysteresis && Math.abs(this.locn.y - this.prev.y) <= this.hysteresis) {
            if (!this.timer.isRunning()) {
               this.timer.start();
            }
         } else if (this.timer.isRunning()) {
            this.timer.stop();
         }

      }

      protected void stop() {
         this.timer.stop();
      }

      public synchronized void actionPerformed(ActionEvent var1) {
         this.updateRegion();
         if (this.outer.contains(this.locn) && !this.inner.contains(this.locn)) {
            this.autoScroll.autoscroll(this.locn);
         }

      }
   }
}
