package javax.swing;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class AbstractListModel<E> implements ListModel<E>, Serializable {
   protected EventListenerList listenerList = new EventListenerList();

   public void addListDataListener(ListDataListener var1) {
      this.listenerList.add(ListDataListener.class, var1);
   }

   public void removeListDataListener(ListDataListener var1) {
      this.listenerList.remove(ListDataListener.class, var1);
   }

   public ListDataListener[] getListDataListeners() {
      return (ListDataListener[])this.listenerList.getListeners(ListDataListener.class);
   }

   protected void fireContentsChanged(Object var1, int var2, int var3) {
      Object[] var4 = this.listenerList.getListenerList();
      ListDataEvent var5 = null;

      for(int var6 = var4.length - 2; var6 >= 0; var6 -= 2) {
         if (var4[var6] == ListDataListener.class) {
            if (var5 == null) {
               var5 = new ListDataEvent(var1, 0, var2, var3);
            }

            ((ListDataListener)var4[var6 + 1]).contentsChanged(var5);
         }
      }

   }

   protected void fireIntervalAdded(Object var1, int var2, int var3) {
      Object[] var4 = this.listenerList.getListenerList();
      ListDataEvent var5 = null;

      for(int var6 = var4.length - 2; var6 >= 0; var6 -= 2) {
         if (var4[var6] == ListDataListener.class) {
            if (var5 == null) {
               var5 = new ListDataEvent(var1, 1, var2, var3);
            }

            ((ListDataListener)var4[var6 + 1]).intervalAdded(var5);
         }
      }

   }

   protected void fireIntervalRemoved(Object var1, int var2, int var3) {
      Object[] var4 = this.listenerList.getListenerList();
      ListDataEvent var5 = null;

      for(int var6 = var4.length - 2; var6 >= 0; var6 -= 2) {
         if (var4[var6] == ListDataListener.class) {
            if (var5 == null) {
               var5 = new ListDataEvent(var1, 2, var2, var3);
            }

            ((ListDataListener)var4[var6 + 1]).intervalRemoved(var5);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }
}
