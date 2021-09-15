package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TooManyListenersException;

public class DragSourceContext implements DragSourceListener, DragSourceMotionListener, Serializable {
   private static final long serialVersionUID = -115407898692194719L;
   protected static final int DEFAULT = 0;
   protected static final int ENTER = 1;
   protected static final int OVER = 2;
   protected static final int CHANGED = 3;
   private static Transferable emptyTransferable;
   private transient DragSourceContextPeer peer;
   private DragGestureEvent trigger;
   private Cursor cursor;
   private transient Transferable transferable;
   private transient DragSourceListener listener;
   private boolean useCustomCursor;
   private int sourceActions;

   public DragSourceContext(DragSourceContextPeer var1, DragGestureEvent var2, Cursor var3, Image var4, Point var5, Transferable var6, DragSourceListener var7) {
      if (var1 == null) {
         throw new NullPointerException("DragSourceContextPeer");
      } else if (var2 == null) {
         throw new NullPointerException("Trigger");
      } else if (var2.getDragSource() == null) {
         throw new IllegalArgumentException("DragSource");
      } else if (var2.getComponent() == null) {
         throw new IllegalArgumentException("Component");
      } else if (var2.getSourceAsDragGestureRecognizer().getSourceActions() == 0) {
         throw new IllegalArgumentException("source actions");
      } else if (var2.getDragAction() == 0) {
         throw new IllegalArgumentException("no drag action");
      } else if (var6 == null) {
         throw new NullPointerException("Transferable");
      } else if (var4 != null && var5 == null) {
         throw new NullPointerException("offset");
      } else {
         this.peer = var1;
         this.trigger = var2;
         this.cursor = var3;
         this.transferable = var6;
         this.listener = var7;
         this.sourceActions = var2.getSourceAsDragGestureRecognizer().getSourceActions();
         this.useCustomCursor = var3 != null;
         this.updateCurrentCursor(var2.getDragAction(), this.getSourceActions(), 0);
      }
   }

   public DragSource getDragSource() {
      return this.trigger.getDragSource();
   }

   public Component getComponent() {
      return this.trigger.getComponent();
   }

   public DragGestureEvent getTrigger() {
      return this.trigger;
   }

   public int getSourceActions() {
      return this.sourceActions;
   }

   public synchronized void setCursor(Cursor var1) {
      this.useCustomCursor = var1 != null;
      this.setCursorImpl(var1);
   }

   public Cursor getCursor() {
      return this.cursor;
   }

   public synchronized void addDragSourceListener(DragSourceListener var1) throws TooManyListenersException {
      if (var1 != null) {
         if (this.equals(var1)) {
            throw new IllegalArgumentException("DragSourceContext may not be its own listener");
         } else if (this.listener != null) {
            throw new TooManyListenersException();
         } else {
            this.listener = var1;
         }
      }
   }

   public synchronized void removeDragSourceListener(DragSourceListener var1) {
      if (this.listener != null && this.listener.equals(var1)) {
         this.listener = null;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void transferablesFlavorsChanged() {
      if (this.peer != null) {
         this.peer.transferablesFlavorsChanged();
      }

   }

   public void dragEnter(DragSourceDragEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragEnter(var1);
      }

      this.getDragSource().processDragEnter(var1);
      this.updateCurrentCursor(this.getSourceActions(), var1.getTargetActions(), 1);
   }

   public void dragOver(DragSourceDragEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragOver(var1);
      }

      this.getDragSource().processDragOver(var1);
      this.updateCurrentCursor(this.getSourceActions(), var1.getTargetActions(), 2);
   }

   public void dragExit(DragSourceEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragExit(var1);
      }

      this.getDragSource().processDragExit(var1);
      this.updateCurrentCursor(0, 0, 0);
   }

   public void dropActionChanged(DragSourceDragEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dropActionChanged(var1);
      }

      this.getDragSource().processDropActionChanged(var1);
      this.updateCurrentCursor(this.getSourceActions(), var1.getTargetActions(), 3);
   }

   public void dragDropEnd(DragSourceDropEvent var1) {
      DragSourceListener var2 = this.listener;
      if (var2 != null) {
         var2.dragDropEnd(var1);
      }

      this.getDragSource().processDragDropEnd(var1);
   }

   public void dragMouseMoved(DragSourceDragEvent var1) {
      this.getDragSource().processDragMouseMoved(var1);
   }

   public Transferable getTransferable() {
      return this.transferable;
   }

   protected synchronized void updateCurrentCursor(int var1, int var2, int var3) {
      if (!this.useCustomCursor) {
         Cursor var4 = null;
         switch(var3) {
         default:
            var2 = 0;
         case 1:
         case 2:
         case 3:
            int var5 = var1 & var2;
            if (var5 == 0) {
               if ((var1 & 1073741824) == 1073741824) {
                  var4 = DragSource.DefaultLinkNoDrop;
               } else if ((var1 & 2) == 2) {
                  var4 = DragSource.DefaultMoveNoDrop;
               } else {
                  var4 = DragSource.DefaultCopyNoDrop;
               }
            } else if ((var5 & 1073741824) == 1073741824) {
               var4 = DragSource.DefaultLinkDrop;
            } else if ((var5 & 2) == 2) {
               var4 = DragSource.DefaultMoveDrop;
            } else {
               var4 = DragSource.DefaultCopyDrop;
            }

            this.setCursorImpl(var4);
         }
      }
   }

   private void setCursorImpl(Cursor var1) {
      if (this.cursor == null || !this.cursor.equals(var1)) {
         this.cursor = var1;
         if (this.peer != null) {
            this.peer.setCursor(this.cursor);
         }
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(SerializationTester.test(this.transferable) ? this.transferable : null);
      var1.writeObject(SerializationTester.test(this.listener) ? this.listener : null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      ObjectInputStream.GetField var2 = var1.readFields();
      DragGestureEvent var3 = (DragGestureEvent)var2.get("trigger", (Object)null);
      if (var3 == null) {
         throw new InvalidObjectException("Null trigger");
      } else if (var3.getDragSource() == null) {
         throw new InvalidObjectException("Null DragSource");
      } else if (var3.getComponent() == null) {
         throw new InvalidObjectException("Null trigger component");
      } else {
         int var4 = var2.get("sourceActions", (int)0) & 1073741827;
         if (var4 == 0) {
            throw new InvalidObjectException("Invalid source actions");
         } else {
            int var5 = var3.getDragAction();
            if (var5 != 1 && var5 != 2 && var5 != 1073741824) {
               throw new InvalidObjectException("No drag action");
            } else {
               this.trigger = var3;
               this.cursor = (Cursor)var2.get("cursor", (Object)null);
               this.useCustomCursor = var2.get("useCustomCursor", false);
               this.sourceActions = var4;
               this.transferable = (Transferable)var1.readObject();
               this.listener = (DragSourceListener)var1.readObject();
               if (this.transferable == null) {
                  if (emptyTransferable == null) {
                     emptyTransferable = new Transferable() {
                        public DataFlavor[] getTransferDataFlavors() {
                           return new DataFlavor[0];
                        }

                        public boolean isDataFlavorSupported(DataFlavor var1) {
                           return false;
                        }

                        public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException {
                           throw new UnsupportedFlavorException(var1);
                        }
                     };
                  }

                  this.transferable = emptyTransferable;
               }

            }
         }
      }
   }
}
