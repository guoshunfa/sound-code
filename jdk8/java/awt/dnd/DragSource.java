package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventListener;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.security.action.GetIntegerAction;

public class DragSource implements Serializable {
   private static final long serialVersionUID = 6236096958971414066L;
   public static final Cursor DefaultCopyDrop = load("DnD.Cursor.CopyDrop");
   public static final Cursor DefaultMoveDrop = load("DnD.Cursor.MoveDrop");
   public static final Cursor DefaultLinkDrop = load("DnD.Cursor.LinkDrop");
   public static final Cursor DefaultCopyNoDrop = load("DnD.Cursor.CopyNoDrop");
   public static final Cursor DefaultMoveNoDrop = load("DnD.Cursor.MoveNoDrop");
   public static final Cursor DefaultLinkNoDrop = load("DnD.Cursor.LinkNoDrop");
   private static final DragSource dflt = GraphicsEnvironment.isHeadless() ? null : new DragSource();
   static final String dragSourceListenerK = "dragSourceL";
   static final String dragSourceMotionListenerK = "dragSourceMotionL";
   private transient FlavorMap flavorMap = SystemFlavorMap.getDefaultFlavorMap();
   private transient DragSourceListener listener;
   private transient DragSourceMotionListener motionListener;

   private static Cursor load(String var0) {
      if (GraphicsEnvironment.isHeadless()) {
         return null;
      } else {
         try {
            return (Cursor)Toolkit.getDefaultToolkit().getDesktopProperty(var0);
         } catch (Exception var2) {
            var2.printStackTrace();
            throw new RuntimeException("failed to load system cursor: " + var0 + " : " + var2.getMessage());
         }
      }
   }

   public static DragSource getDefaultDragSource() {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         return dflt;
      }
   }

   public static boolean isDragImageSupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();

      try {
         Boolean var1 = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.isDragImageSupported");
         return var1;
      } catch (Exception var3) {
         return false;
      }
   }

   public DragSource() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      }
   }

   public void startDrag(DragGestureEvent var1, Cursor var2, Image var3, Point var4, Transferable var5, DragSourceListener var6, FlavorMap var7) throws InvalidDnDOperationException {
      SunDragSourceContextPeer.setDragDropInProgress(true);

      try {
         if (var7 != null) {
            this.flavorMap = var7;
         }

         DragSourceContextPeer var8 = Toolkit.getDefaultToolkit().createDragSourceContextPeer(var1);
         DragSourceContext var9 = this.createDragSourceContext(var8, var1, var2, var3, var4, var5, var6);
         if (var9 == null) {
            throw new InvalidDnDOperationException();
         } else {
            var8.startDrag(var9, var9.getCursor(), var3, var4);
         }
      } catch (RuntimeException var10) {
         SunDragSourceContextPeer.setDragDropInProgress(false);
         throw var10;
      }
   }

   public void startDrag(DragGestureEvent var1, Cursor var2, Transferable var3, DragSourceListener var4, FlavorMap var5) throws InvalidDnDOperationException {
      this.startDrag(var1, var2, (Image)null, (Point)null, var3, var4, var5);
   }

   public void startDrag(DragGestureEvent var1, Cursor var2, Image var3, Point var4, Transferable var5, DragSourceListener var6) throws InvalidDnDOperationException {
      this.startDrag(var1, var2, var3, var4, var5, var6, (FlavorMap)null);
   }

   public void startDrag(DragGestureEvent var1, Cursor var2, Transferable var3, DragSourceListener var4) throws InvalidDnDOperationException {
      this.startDrag(var1, var2, (Image)null, (Point)null, var3, var4, (FlavorMap)null);
   }

   protected DragSourceContext createDragSourceContext(DragSourceContextPeer var1, DragGestureEvent var2, Cursor var3, Image var4, Point var5, Transferable var6, DragSourceListener var7) {
      return new DragSourceContext(var1, var2, var3, var4, var5, var6, var7);
   }

   public FlavorMap getFlavorMap() {
      return this.flavorMap;
   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, Component var2, int var3, DragGestureListener var4) {
      return Toolkit.getDefaultToolkit().createDragGestureRecognizer(var1, this, var2, var3, var4);
   }

   public DragGestureRecognizer createDefaultDragGestureRecognizer(Component var1, int var2, DragGestureListener var3) {
      return Toolkit.getDefaultToolkit().createDragGestureRecognizer(MouseDragGestureRecognizer.class, this, var1, var2, var3);
   }

   public void addDragSourceListener(DragSourceListener var1) {
      if (var1 != null) {
         synchronized(this) {
            this.listener = DnDEventMulticaster.add(this.listener, var1);
         }
      }

   }

   public void removeDragSourceListener(DragSourceListener var1) {
      if (var1 != null) {
         synchronized(this) {
            this.listener = DnDEventMulticaster.remove(this.listener, var1);
         }
      }

   }

   public DragSourceListener[] getDragSourceListeners() {
      return (DragSourceListener[])this.getListeners(DragSourceListener.class);
   }

   public void addDragSourceMotionListener(DragSourceMotionListener var1) {
      if (var1 != null) {
         synchronized(this) {
            this.motionListener = DnDEventMulticaster.add(this.motionListener, var1);
         }
      }

   }

   public void removeDragSourceMotionListener(DragSourceMotionListener var1) {
      if (var1 != null) {
         synchronized(this) {
            this.motionListener = DnDEventMulticaster.remove(this.motionListener, var1);
         }
      }

   }

   public DragSourceMotionListener[] getDragSourceMotionListeners() {
      return (DragSourceMotionListener[])this.getListeners(DragSourceMotionListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      Object var2 = null;
      if (var1 == DragSourceListener.class) {
         var2 = this.listener;
      } else if (var1 == DragSourceMotionListener.class) {
         var2 = this.motionListener;
      }

      return DnDEventMulticaster.getListeners((EventListener)var2, var1);
   }

   void processDragEnter(DragSourceDragEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragEnter(var1);
      }

   }

   void processDragOver(DragSourceDragEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragOver(var1);
      }

   }

   void processDropActionChanged(DragSourceDragEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dropActionChanged(var1);
      }

   }

   void processDragExit(DragSourceEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragExit(var1);
      }

   }

   void processDragDropEnd(DragSourceDropEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragDropEnd(var1);
      }

   }

   void processDragMouseMoved(DragSourceDragEvent var1) {
      DragSourceMotionListener var2 = this.motionListener;
      if (var2 != null) {
         var2.dragMouseMoved(var1);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(SerializationTester.test(this.flavorMap) ? this.flavorMap : null);
      DnDEventMulticaster.save(var1, "dragSourceL", this.listener);
      DnDEventMulticaster.save(var1, "dragSourceMotionL", this.motionListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.flavorMap = (FlavorMap)var1.readObject();
      if (this.flavorMap == null) {
         this.flavorMap = SystemFlavorMap.getDefaultFlavorMap();
      }

      Object var2;
      while(null != (var2 = var1.readObject())) {
         String var3 = ((String)var2).intern();
         if ("dragSourceL" == var3) {
            this.addDragSourceListener((DragSourceListener)((DragSourceListener)var1.readObject()));
         } else if ("dragSourceMotionL" == var3) {
            this.addDragSourceMotionListener((DragSourceMotionListener)((DragSourceMotionListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

   }

   public static int getDragThreshold() {
      int var0 = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("awt.dnd.drag.threshold", 0)));
      if (var0 > 0) {
         return var0;
      } else {
         Integer var1 = (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");
         return var1 != null ? var1 : 5;
      }
   }
}
