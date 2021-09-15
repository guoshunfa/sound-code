package sun.awt.dnd;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.SortedMap;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;

public abstract class SunDragSourceContextPeer implements DragSourceContextPeer {
   private DragGestureEvent trigger;
   private Component component;
   private Cursor cursor;
   private Image dragImage;
   private Point dragImageOffset;
   private long nativeCtxt;
   private DragSourceContext dragSourceContext;
   private int sourceActions;
   private static boolean dragDropInProgress = false;
   private static boolean discardingMouseEvents = false;
   protected static final int DISPATCH_ENTER = 1;
   protected static final int DISPATCH_MOTION = 2;
   protected static final int DISPATCH_CHANGED = 3;
   protected static final int DISPATCH_EXIT = 4;
   protected static final int DISPATCH_FINISH = 5;
   protected static final int DISPATCH_MOUSE_MOVED = 6;

   public SunDragSourceContextPeer(DragGestureEvent var1) {
      this.trigger = var1;
      if (this.trigger != null) {
         this.component = this.trigger.getComponent();
      } else {
         this.component = null;
      }

   }

   public void startSecondaryEventLoop() {
   }

   public void quitSecondaryEventLoop() {
   }

   public void startDrag(DragSourceContext var1, Cursor var2, Image var3, Point var4) throws InvalidDnDOperationException {
      if (this.getTrigger().getTriggerEvent() == null) {
         throw new InvalidDnDOperationException("DragGestureEvent has a null trigger");
      } else {
         this.dragSourceContext = var1;
         this.cursor = var2;
         this.sourceActions = this.getDragSourceContext().getSourceActions();
         this.dragImage = var3;
         this.dragImageOffset = var4;
         Transferable var5 = this.getDragSourceContext().getTransferable();
         SortedMap var6 = DataTransferer.getInstance().getFormatsForTransferable(var5, DataTransferer.adaptFlavorMap(this.getTrigger().getDragSource().getFlavorMap()));
         DataTransferer.getInstance();
         long[] var7 = DataTransferer.keysToLongArray(var6);
         this.startDrag(var5, var7, var6);
         discardingMouseEvents = true;
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               SunDragSourceContextPeer.discardingMouseEvents = false;
            }
         });
      }
   }

   protected abstract void startDrag(Transferable var1, long[] var2, Map var3);

   public void setCursor(Cursor var1) throws InvalidDnDOperationException {
      synchronized(this) {
         if (this.cursor == null || !this.cursor.equals(var1)) {
            this.cursor = var1;
            this.setNativeCursor(this.getNativeContext(), var1, var1 != null ? var1.getType() : 0);
         }

      }
   }

   public Cursor getCursor() {
      return this.cursor;
   }

   public Image getDragImage() {
      return this.dragImage;
   }

   public Point getDragImageOffset() {
      return this.dragImageOffset == null ? new Point(0, 0) : new Point(this.dragImageOffset);
   }

   protected abstract void setNativeCursor(long var1, Cursor var3, int var4);

   protected synchronized void setTrigger(DragGestureEvent var1) {
      this.trigger = var1;
      if (this.trigger != null) {
         this.component = this.trigger.getComponent();
      } else {
         this.component = null;
      }

   }

   protected DragGestureEvent getTrigger() {
      return this.trigger;
   }

   protected Component getComponent() {
      return this.component;
   }

   protected synchronized void setNativeContext(long var1) {
      this.nativeCtxt = var1;
   }

   protected synchronized long getNativeContext() {
      return this.nativeCtxt;
   }

   protected DragSourceContext getDragSourceContext() {
      return this.dragSourceContext;
   }

   public void transferablesFlavorsChanged() {
   }

   protected final void postDragSourceDragEvent(int var1, int var2, int var3, int var4, int var5) {
      int var6 = convertModifiersToDropAction(var2, this.sourceActions);
      DragSourceDragEvent var7 = new DragSourceDragEvent(this.getDragSourceContext(), var6, var1 & this.sourceActions, var2, var3, var4);
      SunDragSourceContextPeer.EventDispatcher var8 = new SunDragSourceContextPeer.EventDispatcher(var5, var7);
      SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(this.getComponent()), var8);
      this.startSecondaryEventLoop();
   }

   protected void dragEnter(int var1, int var2, int var3, int var4) {
      this.postDragSourceDragEvent(var1, var2, var3, var4, 1);
   }

   private void dragMotion(int var1, int var2, int var3, int var4) {
      this.postDragSourceDragEvent(var1, var2, var3, var4, 2);
   }

   private void operationChanged(int var1, int var2, int var3, int var4) {
      this.postDragSourceDragEvent(var1, var2, var3, var4, 3);
   }

   protected final void dragExit(int var1, int var2) {
      DragSourceEvent var3 = new DragSourceEvent(this.getDragSourceContext(), var1, var2);
      SunDragSourceContextPeer.EventDispatcher var4 = new SunDragSourceContextPeer.EventDispatcher(4, var3);
      SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(this.getComponent()), var4);
      this.startSecondaryEventLoop();
   }

   private void dragMouseMoved(int var1, int var2, int var3, int var4) {
      this.postDragSourceDragEvent(var1, var2, var3, var4, 6);
   }

   protected final void dragDropFinished(boolean var1, int var2, int var3, int var4) {
      DragSourceDropEvent var5 = new DragSourceDropEvent(this.getDragSourceContext(), var2 & this.sourceActions, var1, var3, var4);
      SunDragSourceContextPeer.EventDispatcher var6 = new SunDragSourceContextPeer.EventDispatcher(5, var5);
      SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(this.getComponent()), var6);
      this.startSecondaryEventLoop();
      this.setNativeContext(0L);
      this.dragImage = null;
      this.dragImageOffset = null;
   }

   public static void setDragDropInProgress(boolean var0) throws InvalidDnDOperationException {
      Class var1 = SunDragSourceContextPeer.class;
      synchronized(SunDragSourceContextPeer.class) {
         if (dragDropInProgress == var0) {
            throw new InvalidDnDOperationException(getExceptionMessage(var0));
         } else {
            dragDropInProgress = var0;
         }
      }
   }

   public static boolean checkEvent(AWTEvent var0) {
      if (discardingMouseEvents && var0 instanceof MouseEvent) {
         MouseEvent var1 = (MouseEvent)var0;
         if (!(var1 instanceof SunDropTargetEvent)) {
            return false;
         }
      }

      return true;
   }

   public static void checkDragDropInProgress() throws InvalidDnDOperationException {
      if (dragDropInProgress) {
         throw new InvalidDnDOperationException(getExceptionMessage(true));
      }
   }

   private static String getExceptionMessage(boolean var0) {
      return var0 ? "Drag and drop in progress" : "No drag in progress";
   }

   public static int convertModifiersToDropAction(int var0, int var1) {
      int var2 = 0;
      switch(var0 & 192) {
      case 64:
         var2 = 2;
         break;
      case 128:
         var2 = 1;
         break;
      case 192:
         var2 = 1073741824;
         break;
      default:
         if ((var1 & 2) != 0) {
            var2 = 2;
         } else if ((var1 & 1) != 0) {
            var2 = 1;
         } else if ((var1 & 1073741824) != 0) {
            var2 = 1073741824;
         }
      }

      return var2 & var1;
   }

   private void cleanup() {
      this.trigger = null;
      this.component = null;
      this.cursor = null;
      this.dragSourceContext = null;
      SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable((Transferable)null);
      setDragDropInProgress(false);
   }

   private class EventDispatcher implements Runnable {
      private final int dispatchType;
      private final DragSourceEvent event;

      EventDispatcher(int var2, DragSourceEvent var3) {
         switch(var2) {
         case 1:
         case 2:
         case 3:
         case 6:
            if (!(var3 instanceof DragSourceDragEvent)) {
               throw new IllegalArgumentException("Event: " + var3);
            }
         case 4:
            break;
         case 5:
            if (!(var3 instanceof DragSourceDropEvent)) {
               throw new IllegalArgumentException("Event: " + var3);
            }
            break;
         default:
            throw new IllegalArgumentException("Dispatch type: " + var2);
         }

         this.dispatchType = var2;
         this.event = var3;
      }

      public void run() {
         DragSourceContext var1 = SunDragSourceContextPeer.this.getDragSourceContext();

         try {
            switch(this.dispatchType) {
            case 1:
               var1.dragEnter((DragSourceDragEvent)this.event);
               break;
            case 2:
               var1.dragOver((DragSourceDragEvent)this.event);
               break;
            case 3:
               var1.dropActionChanged((DragSourceDragEvent)this.event);
               break;
            case 4:
               var1.dragExit(this.event);
               break;
            case 5:
               try {
                  var1.dragDropEnd((DragSourceDropEvent)this.event);
                  break;
               } finally {
                  SunDragSourceContextPeer.this.cleanup();
               }
            case 6:
               var1.dragMouseMoved((DragSourceDragEvent)this.event);
               break;
            default:
               throw new IllegalStateException("Dispatch type: " + this.dispatchType);
            }
         } finally {
            SunDragSourceContextPeer.this.quitSecondaryEventLoop();
         }

      }
   }
}
