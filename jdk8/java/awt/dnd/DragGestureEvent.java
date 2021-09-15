package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class DragGestureEvent extends EventObject {
   private static final long serialVersionUID = 9080172649166731306L;
   private transient List events;
   private DragSource dragSource;
   private Component component;
   private Point origin;
   private int action;

   public DragGestureEvent(DragGestureRecognizer var1, int var2, Point var3, List<? extends InputEvent> var4) {
      super(var1);
      if ((this.component = var1.getComponent()) == null) {
         throw new IllegalArgumentException("null component");
      } else if ((this.dragSource = var1.getDragSource()) == null) {
         throw new IllegalArgumentException("null DragSource");
      } else if (var4 != null && !var4.isEmpty()) {
         if (var2 != 1 && var2 != 2 && var2 != 1073741824) {
            throw new IllegalArgumentException("bad action");
         } else if (var3 == null) {
            throw new IllegalArgumentException("null origin");
         } else {
            this.events = var4;
            this.action = var2;
            this.origin = var3;
         }
      } else {
         throw new IllegalArgumentException("null or empty list of events");
      }
   }

   public DragGestureRecognizer getSourceAsDragGestureRecognizer() {
      return (DragGestureRecognizer)this.getSource();
   }

   public Component getComponent() {
      return this.component;
   }

   public DragSource getDragSource() {
      return this.dragSource;
   }

   public Point getDragOrigin() {
      return this.origin;
   }

   public Iterator<InputEvent> iterator() {
      return this.events.iterator();
   }

   public Object[] toArray() {
      return this.events.toArray();
   }

   public Object[] toArray(Object[] var1) {
      return this.events.toArray(var1);
   }

   public int getDragAction() {
      return this.action;
   }

   public InputEvent getTriggerEvent() {
      return this.getSourceAsDragGestureRecognizer().getTriggerEvent();
   }

   public void startDrag(Cursor var1, Transferable var2) throws InvalidDnDOperationException {
      this.dragSource.startDrag(this, var1, var2, (DragSourceListener)null);
   }

   public void startDrag(Cursor var1, Transferable var2, DragSourceListener var3) throws InvalidDnDOperationException {
      this.dragSource.startDrag(this, var1, var2, var3);
   }

   public void startDrag(Cursor var1, Image var2, Point var3, Transferable var4, DragSourceListener var5) throws InvalidDnDOperationException {
      this.dragSource.startDrag(this, var1, var2, var3, var4, var5);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(SerializationTester.test(this.events) ? this.events : null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      ObjectInputStream.GetField var2 = var1.readFields();
      DragSource var3 = (DragSource)var2.get("dragSource", (Object)null);
      if (var3 == null) {
         throw new InvalidObjectException("null DragSource");
      } else {
         this.dragSource = var3;
         Component var4 = (Component)var2.get("component", (Object)null);
         if (var4 == null) {
            throw new InvalidObjectException("null component");
         } else {
            this.component = var4;
            Point var5 = (Point)var2.get("origin", (Object)null);
            if (var5 == null) {
               throw new InvalidObjectException("null origin");
            } else {
               this.origin = var5;
               int var6 = var2.get("action", (int)0);
               if (var6 != 1 && var6 != 2 && var6 != 1073741824) {
                  throw new InvalidObjectException("bad action");
               } else {
                  this.action = var6;

                  List var7;
                  try {
                     var7 = (List)var2.get("events", (Object)null);
                  } catch (IllegalArgumentException var9) {
                     var7 = (List)var1.readObject();
                  }

                  if (var7 != null && var7.isEmpty()) {
                     throw new InvalidObjectException("empty list of events");
                  } else {
                     if (var7 == null) {
                        var7 = Collections.emptyList();
                     }

                     this.events = var7;
                  }
               }
            }
         }
      }
   }
}
