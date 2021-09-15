package javax.swing.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;
import sun.reflect.misc.ReflectUtil;

public class EventListenerList implements Serializable {
   private static final Object[] NULL_ARRAY = new Object[0];
   protected transient Object[] listenerList;

   public EventListenerList() {
      this.listenerList = NULL_ARRAY;
   }

   public Object[] getListenerList() {
      return this.listenerList;
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      Object[] var2 = this.listenerList;
      int var3 = this.getListenerCount(var2, var1);
      EventListener[] var4 = (EventListener[])((EventListener[])Array.newInstance(var1, var3));
      int var5 = 0;

      for(int var6 = var2.length - 2; var6 >= 0; var6 -= 2) {
         if (var2[var6] == var1) {
            var4[var5++] = (EventListener)var2[var6 + 1];
         }
      }

      return var4;
   }

   public int getListenerCount() {
      return this.listenerList.length / 2;
   }

   public int getListenerCount(Class<?> var1) {
      Object[] var2 = this.listenerList;
      return this.getListenerCount(var2, var1);
   }

   private int getListenerCount(Object[] var1, Class var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var1.length; var4 += 2) {
         if (var2 == (Class)var1[var4]) {
            ++var3;
         }
      }

      return var3;
   }

   public synchronized <T extends EventListener> void add(Class<T> var1, T var2) {
      if (var2 != null) {
         if (!var1.isInstance(var2)) {
            throw new IllegalArgumentException("Listener " + var2 + " is not of type " + var1);
         } else {
            if (this.listenerList == NULL_ARRAY) {
               this.listenerList = new Object[]{var1, var2};
            } else {
               int var3 = this.listenerList.length;
               Object[] var4 = new Object[var3 + 2];
               System.arraycopy(this.listenerList, 0, var4, 0, var3);
               var4[var3] = var1;
               var4[var3 + 1] = var2;
               this.listenerList = var4;
            }

         }
      }
   }

   public synchronized <T extends EventListener> void remove(Class<T> var1, T var2) {
      if (var2 != null) {
         if (!var1.isInstance(var2)) {
            throw new IllegalArgumentException("Listener " + var2 + " is not of type " + var1);
         } else {
            int var3 = -1;

            for(int var4 = this.listenerList.length - 2; var4 >= 0; var4 -= 2) {
               if (this.listenerList[var4] == var1 && this.listenerList[var4 + 1].equals(var2)) {
                  var3 = var4;
                  break;
               }
            }

            if (var3 != -1) {
               Object[] var5 = new Object[this.listenerList.length - 2];
               System.arraycopy(this.listenerList, 0, var5, 0, var3);
               if (var3 < var5.length) {
                  System.arraycopy(this.listenerList, var3 + 2, var5, var3, var5.length - var3);
               }

               this.listenerList = var5.length == 0 ? NULL_ARRAY : var5;
            }

         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Object[] var2 = this.listenerList;
      var1.defaultWriteObject();

      for(int var3 = 0; var3 < var2.length; var3 += 2) {
         Class var4 = (Class)var2[var3];
         EventListener var5 = (EventListener)var2[var3 + 1];
         if (var5 != null && var5 instanceof Serializable) {
            var1.writeObject(var4.getName());
            var1.writeObject(var5);
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.listenerList = NULL_ARRAY;
      var1.defaultReadObject();

      Object var2;
      while(null != (var2 = var1.readObject())) {
         ClassLoader var3 = Thread.currentThread().getContextClassLoader();
         EventListener var4 = (EventListener)var1.readObject();
         String var5 = (String)var2;
         ReflectUtil.checkPackageAccess(var5);
         this.add(Class.forName(var5, true, var3), var4);
      }

   }

   public String toString() {
      Object[] var1 = this.listenerList;
      String var2 = "EventListenerList: ";
      var2 = var2 + var1.length / 2 + " listeners: ";

      for(int var3 = 0; var3 <= var1.length - 2; var3 += 2) {
         var2 = var2 + " type " + ((Class)var1[var3]).getName();
         var2 = var2 + " listener " + var1[var3 + 1];
      }

      return var2;
   }
}
