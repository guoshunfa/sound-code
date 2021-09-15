package javax.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class ActionPropertyChangeListener<T extends JComponent> implements PropertyChangeListener, Serializable {
   private static ReferenceQueue<JComponent> queue;
   private transient ActionPropertyChangeListener.OwnedWeakReference<T> target;
   private Action action;

   private static ReferenceQueue<JComponent> getQueue() {
      Class var0 = ActionPropertyChangeListener.class;
      synchronized(ActionPropertyChangeListener.class) {
         if (queue == null) {
            queue = new ReferenceQueue();
         }
      }

      return queue;
   }

   public ActionPropertyChangeListener(T var1, Action var2) {
      this.setTarget(var1);
      this.action = var2;
   }

   public final void propertyChange(PropertyChangeEvent var1) {
      JComponent var2 = this.getTarget();
      if (var2 == null) {
         this.getAction().removePropertyChangeListener(this);
      } else {
         this.actionPropertyChanged(var2, this.getAction(), var1);
      }

   }

   protected abstract void actionPropertyChanged(T var1, Action var2, PropertyChangeEvent var3);

   private void setTarget(T var1) {
      ReferenceQueue var2 = getQueue();

      ActionPropertyChangeListener.OwnedWeakReference var3;
      while((var3 = (ActionPropertyChangeListener.OwnedWeakReference)var2.poll()) != null) {
         ActionPropertyChangeListener var4 = var3.getOwner();
         Action var5 = var4.getAction();
         if (var5 != null) {
            var5.removePropertyChangeListener(var4);
         }
      }

      this.target = new ActionPropertyChangeListener.OwnedWeakReference(var1, var2, this);
   }

   public T getTarget() {
      return this.target == null ? null : (JComponent)this.target.get();
   }

   public Action getAction() {
      return this.action;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getTarget());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      JComponent var2 = (JComponent)var1.readObject();
      if (var2 != null) {
         this.setTarget(var2);
      }

   }

   private static class OwnedWeakReference<U extends JComponent> extends WeakReference<U> {
      private ActionPropertyChangeListener<?> owner;

      OwnedWeakReference(U var1, ReferenceQueue<? super U> var2, ActionPropertyChangeListener<?> var3) {
         super(var1, var2);
         this.owner = var3;
      }

      public ActionPropertyChangeListener<?> getOwner() {
         return this.owner;
      }
   }
}
