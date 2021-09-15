package java.awt.dnd;

import java.awt.AWTEventMulticaster;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventListener;

class DnDEventMulticaster extends AWTEventMulticaster implements DragSourceListener, DragSourceMotionListener {
   protected DnDEventMulticaster(EventListener var1, EventListener var2) {
      super(var1, var2);
   }

   public void dragEnter(DragSourceDragEvent var1) {
      ((DragSourceListener)this.a).dragEnter(var1);
      ((DragSourceListener)this.b).dragEnter(var1);
   }

   public void dragOver(DragSourceDragEvent var1) {
      ((DragSourceListener)this.a).dragOver(var1);
      ((DragSourceListener)this.b).dragOver(var1);
   }

   public void dropActionChanged(DragSourceDragEvent var1) {
      ((DragSourceListener)this.a).dropActionChanged(var1);
      ((DragSourceListener)this.b).dropActionChanged(var1);
   }

   public void dragExit(DragSourceEvent var1) {
      ((DragSourceListener)this.a).dragExit(var1);
      ((DragSourceListener)this.b).dragExit(var1);
   }

   public void dragDropEnd(DragSourceDropEvent var1) {
      ((DragSourceListener)this.a).dragDropEnd(var1);
      ((DragSourceListener)this.b).dragDropEnd(var1);
   }

   public void dragMouseMoved(DragSourceDragEvent var1) {
      ((DragSourceMotionListener)this.a).dragMouseMoved(var1);
      ((DragSourceMotionListener)this.b).dragMouseMoved(var1);
   }

   public static DragSourceListener add(DragSourceListener var0, DragSourceListener var1) {
      return (DragSourceListener)addInternal(var0, var1);
   }

   public static DragSourceMotionListener add(DragSourceMotionListener var0, DragSourceMotionListener var1) {
      return (DragSourceMotionListener)addInternal(var0, var1);
   }

   public static DragSourceListener remove(DragSourceListener var0, DragSourceListener var1) {
      return (DragSourceListener)removeInternal(var0, var1);
   }

   public static DragSourceMotionListener remove(DragSourceMotionListener var0, DragSourceMotionListener var1) {
      return (DragSourceMotionListener)removeInternal(var0, var1);
   }

   protected static EventListener addInternal(EventListener var0, EventListener var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (EventListener)(var1 == null ? var0 : new DnDEventMulticaster(var0, var1));
      }
   }

   protected EventListener remove(EventListener var1) {
      if (var1 == this.a) {
         return this.b;
      } else if (var1 == this.b) {
         return this.a;
      } else {
         EventListener var2 = removeInternal(this.a, var1);
         EventListener var3 = removeInternal(this.b, var1);
         return (EventListener)(var2 == this.a && var3 == this.b ? this : addInternal(var2, var3));
      }
   }

   protected static EventListener removeInternal(EventListener var0, EventListener var1) {
      if (var0 != var1 && var0 != null) {
         return var0 instanceof DnDEventMulticaster ? ((DnDEventMulticaster)var0).remove(var1) : var0;
      } else {
         return null;
      }
   }

   protected static void save(ObjectOutputStream var0, String var1, EventListener var2) throws IOException {
      AWTEventMulticaster.save(var0, var1, var2);
   }
}
