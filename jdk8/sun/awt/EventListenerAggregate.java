package sun.awt;

import java.lang.reflect.Array;
import java.util.EventListener;

public class EventListenerAggregate {
   private EventListener[] listenerList;

   public EventListenerAggregate(Class<? extends EventListener> var1) {
      if (var1 == null) {
         throw new NullPointerException("listener class is null");
      } else {
         this.listenerList = (EventListener[])((EventListener[])Array.newInstance(var1, 0));
      }
   }

   private Class<?> getListenerClass() {
      return this.listenerList.getClass().getComponentType();
   }

   public synchronized void add(EventListener var1) {
      Class var2 = this.getListenerClass();
      if (!var2.isInstance(var1)) {
         throw new ClassCastException("listener " + var1 + " is not an instance of listener class " + var2);
      } else {
         EventListener[] var3 = (EventListener[])((EventListener[])Array.newInstance(var2, this.listenerList.length + 1));
         System.arraycopy(this.listenerList, 0, var3, 0, this.listenerList.length);
         var3[this.listenerList.length] = var1;
         this.listenerList = var3;
      }
   }

   public synchronized boolean remove(EventListener var1) {
      Class var2 = this.getListenerClass();
      if (!var2.isInstance(var1)) {
         throw new ClassCastException("listener " + var1 + " is not an instance of listener class " + var2);
      } else {
         for(int var3 = 0; var3 < this.listenerList.length; ++var3) {
            if (this.listenerList[var3].equals(var1)) {
               EventListener[] var4 = (EventListener[])((EventListener[])Array.newInstance(var2, this.listenerList.length - 1));
               System.arraycopy(this.listenerList, 0, var4, 0, var3);
               System.arraycopy(this.listenerList, var3 + 1, var4, var3, this.listenerList.length - var3 - 1);
               this.listenerList = var4;
               return true;
            }
         }

         return false;
      }
   }

   public synchronized EventListener[] getListenersInternal() {
      return this.listenerList;
   }

   public synchronized EventListener[] getListenersCopy() {
      return this.listenerList.length == 0 ? this.listenerList : (EventListener[])this.listenerList.clone();
   }

   public synchronized int size() {
      return this.listenerList.length;
   }

   public synchronized boolean isEmpty() {
      return this.listenerList.length == 0;
   }
}
