package java.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TooManyListenersException;

public abstract class DragGestureRecognizer implements Serializable {
   private static final long serialVersionUID = 8996673345831063337L;
   protected DragSource dragSource;
   protected Component component;
   protected transient DragGestureListener dragGestureListener;
   protected int sourceActions;
   protected ArrayList<InputEvent> events;

   protected DragGestureRecognizer(DragSource var1, Component var2, int var3, DragGestureListener var4) {
      this.events = new ArrayList(1);
      if (var1 == null) {
         throw new IllegalArgumentException("null DragSource");
      } else {
         this.dragSource = var1;
         this.component = var2;
         this.sourceActions = var3 & 1073741827;

         try {
            if (var4 != null) {
               this.addDragGestureListener(var4);
            }
         } catch (TooManyListenersException var6) {
         }

      }
   }

   protected DragGestureRecognizer(DragSource var1, Component var2, int var3) {
      this(var1, var2, var3, (DragGestureListener)null);
   }

   protected DragGestureRecognizer(DragSource var1, Component var2) {
      this(var1, var2, 0);
   }

   protected DragGestureRecognizer(DragSource var1) {
      this(var1, (Component)null);
   }

   protected abstract void registerListeners();

   protected abstract void unregisterListeners();

   public DragSource getDragSource() {
      return this.dragSource;
   }

   public synchronized Component getComponent() {
      return this.component;
   }

   public synchronized void setComponent(Component var1) {
      if (this.component != null && this.dragGestureListener != null) {
         this.unregisterListeners();
      }

      this.component = var1;
      if (this.component != null && this.dragGestureListener != null) {
         this.registerListeners();
      }

   }

   public synchronized int getSourceActions() {
      return this.sourceActions;
   }

   public synchronized void setSourceActions(int var1) {
      this.sourceActions = var1 & 1073741827;
   }

   public InputEvent getTriggerEvent() {
      return this.events.isEmpty() ? null : (InputEvent)this.events.get(0);
   }

   public void resetRecognizer() {
      this.events.clear();
   }

   public synchronized void addDragGestureListener(DragGestureListener var1) throws TooManyListenersException {
      if (this.dragGestureListener != null) {
         throw new TooManyListenersException();
      } else {
         this.dragGestureListener = var1;
         if (this.component != null) {
            this.registerListeners();
         }

      }
   }

   public synchronized void removeDragGestureListener(DragGestureListener var1) {
      if (this.dragGestureListener != null && this.dragGestureListener.equals(var1)) {
         this.dragGestureListener = null;
         if (this.component != null) {
            this.unregisterListeners();
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   protected synchronized void fireDragGestureRecognized(int var1, Point var2) {
      try {
         if (this.dragGestureListener != null) {
            this.dragGestureListener.dragGestureRecognized(new DragGestureEvent(this, var1, var2, this.events));
         }
      } finally {
         this.events.clear();
      }

   }

   protected synchronized void appendEvent(InputEvent var1) {
      this.events.add(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(SerializationTester.test(this.dragGestureListener) ? this.dragGestureListener : null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      ObjectInputStream.GetField var2 = var1.readFields();
      DragSource var3 = (DragSource)var2.get("dragSource", (Object)null);
      if (var3 == null) {
         throw new InvalidObjectException("null DragSource");
      } else {
         this.dragSource = var3;
         this.component = (Component)var2.get("component", (Object)null);
         this.sourceActions = var2.get("sourceActions", (int)0) & 1073741827;
         this.events = (ArrayList)var2.get("events", new ArrayList(1));
         this.dragGestureListener = (DragGestureListener)var1.readObject();
      }
   }
}
