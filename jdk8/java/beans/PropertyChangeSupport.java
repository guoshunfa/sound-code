package java.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class PropertyChangeSupport implements Serializable {
   private PropertyChangeSupport.PropertyChangeListenerMap map = new PropertyChangeSupport.PropertyChangeListenerMap();
   private Object source;
   private static final ObjectStreamField[] serialPersistentFields;
   static final long serialVersionUID = 6401253773779951803L;

   public PropertyChangeSupport(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.source = var1;
      }
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      if (var1 != null) {
         if (var1 instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy var2 = (PropertyChangeListenerProxy)var1;
            this.addPropertyChangeListener(var2.getPropertyName(), (PropertyChangeListener)var2.getListener());
         } else {
            this.map.add((String)null, var1);
         }

      }
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      if (var1 != null) {
         if (var1 instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy var2 = (PropertyChangeListenerProxy)var1;
            this.removePropertyChangeListener(var2.getPropertyName(), (PropertyChangeListener)var2.getListener());
         } else {
            this.map.remove((String)null, var1);
         }

      }
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return (PropertyChangeListener[])this.map.getListeners();
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null && var1 != null) {
         var2 = this.map.extract(var2);
         if (var2 != null) {
            this.map.add(var1, var2);
         }

      }
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null && var1 != null) {
         var2 = this.map.extract(var2);
         if (var2 != null) {
            this.map.remove(var1, var2);
         }

      }
   }

   public PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      return (PropertyChangeListener[])this.map.getListeners(var1);
   }

   public void firePropertyChange(String var1, Object var2, Object var3) {
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         this.firePropertyChange(new PropertyChangeEvent(this.source, var1, var2, var3));
      }

   }

   public void firePropertyChange(String var1, int var2, int var3) {
      if (var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }

   }

   public void firePropertyChange(String var1, boolean var2, boolean var3) {
      if (var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }

   }

   public void firePropertyChange(PropertyChangeEvent var1) {
      Object var2 = var1.getOldValue();
      Object var3 = var1.getNewValue();
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         String var4 = var1.getPropertyName();
         PropertyChangeListener[] var5 = (PropertyChangeListener[])this.map.get((String)null);
         PropertyChangeListener[] var6 = var4 != null ? (PropertyChangeListener[])this.map.get(var4) : null;
         fire(var5, var1);
         fire(var6, var1);
      }

   }

   private static void fire(PropertyChangeListener[] var0, PropertyChangeEvent var1) {
      if (var0 != null) {
         PropertyChangeListener[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PropertyChangeListener var5 = var2[var4];
            var5.propertyChange(var1);
         }
      }

   }

   public void fireIndexedPropertyChange(String var1, int var2, Object var3, Object var4) {
      if (var3 == null || var4 == null || !var3.equals(var4)) {
         this.firePropertyChange(new IndexedPropertyChangeEvent(this.source, var1, var3, var4, var2));
      }

   }

   public void fireIndexedPropertyChange(String var1, int var2, int var3, int var4) {
      if (var3 != var4) {
         this.fireIndexedPropertyChange(var1, var2, var3, var4);
      }

   }

   public void fireIndexedPropertyChange(String var1, int var2, boolean var3, boolean var4) {
      if (var3 != var4) {
         this.fireIndexedPropertyChange(var1, var2, var3, var4);
      }

   }

   public boolean hasListeners(String var1) {
      return this.map.hasListeners(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = null;
      PropertyChangeListener[] var3 = null;
      synchronized(this.map) {
         Iterator var5 = this.map.getEntries().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            String var7 = (String)var6.getKey();
            if (var7 == null) {
               var3 = (PropertyChangeListener[])var6.getValue();
            } else {
               if (var2 == null) {
                  var2 = new Hashtable();
               }

               PropertyChangeSupport var8 = new PropertyChangeSupport(this.source);
               var8.map.set((String)null, (EventListener[])var6.getValue());
               var2.put(var7, var8);
            }
         }
      }

      ObjectOutputStream.PutField var4 = var1.putFields();
      var4.put("children", var2);
      var4.put("source", this.source);
      var4.put("propertyChangeSupportSerializedDataVersion", (int)2);
      var1.writeFields();
      if (var3 != null) {
         PropertyChangeListener[] var11 = var3;
         int var12 = var3.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            PropertyChangeListener var14 = var11[var13];
            if (var14 instanceof Serializable) {
               var1.writeObject(var14);
            }
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.map = new PropertyChangeSupport.PropertyChangeListenerMap();
      ObjectInputStream.GetField var2 = var1.readFields();
      Hashtable var3 = (Hashtable)var2.get("children", (Object)null);
      this.source = var2.get("source", (Object)null);
      var2.get("propertyChangeSupportSerializedDataVersion", (int)2);

      Object var4;
      while(null != (var4 = var1.readObject())) {
         this.map.add((String)null, (PropertyChangeListener)var4);
      }

      if (var3 != null) {
         Iterator var5 = var3.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            PropertyChangeListener[] var7 = ((PropertyChangeSupport)var6.getValue()).getPropertyChangeListeners();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               PropertyChangeListener var10 = var7[var9];
               this.map.add((String)var6.getKey(), var10);
            }
         }
      }

   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("propertyChangeSupportSerializedDataVersion", Integer.TYPE)};
   }

   private static final class PropertyChangeListenerMap extends ChangeListenerMap<PropertyChangeListener> {
      private static final PropertyChangeListener[] EMPTY = new PropertyChangeListener[0];

      private PropertyChangeListenerMap() {
      }

      protected PropertyChangeListener[] newArray(int var1) {
         return 0 < var1 ? new PropertyChangeListener[var1] : EMPTY;
      }

      protected PropertyChangeListener newProxy(String var1, PropertyChangeListener var2) {
         return new PropertyChangeListenerProxy(var1, var2);
      }

      public final PropertyChangeListener extract(PropertyChangeListener var1) {
         while(var1 instanceof PropertyChangeListenerProxy) {
            var1 = (PropertyChangeListener)((PropertyChangeListenerProxy)var1).getListener();
         }

         return var1;
      }

      // $FF: synthetic method
      PropertyChangeListenerMap(Object var1) {
         this();
      }
   }
}
