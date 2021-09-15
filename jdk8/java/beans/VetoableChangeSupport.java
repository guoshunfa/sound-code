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

public class VetoableChangeSupport implements Serializable {
   private VetoableChangeSupport.VetoableChangeListenerMap map = new VetoableChangeSupport.VetoableChangeListenerMap();
   private Object source;
   private static final ObjectStreamField[] serialPersistentFields;
   static final long serialVersionUID = -5090210921595982017L;

   public VetoableChangeSupport(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.source = var1;
      }
   }

   public void addVetoableChangeListener(VetoableChangeListener var1) {
      if (var1 != null) {
         if (var1 instanceof VetoableChangeListenerProxy) {
            VetoableChangeListenerProxy var2 = (VetoableChangeListenerProxy)var1;
            this.addVetoableChangeListener(var2.getPropertyName(), (VetoableChangeListener)var2.getListener());
         } else {
            this.map.add((String)null, var1);
         }

      }
   }

   public void removeVetoableChangeListener(VetoableChangeListener var1) {
      if (var1 != null) {
         if (var1 instanceof VetoableChangeListenerProxy) {
            VetoableChangeListenerProxy var2 = (VetoableChangeListenerProxy)var1;
            this.removeVetoableChangeListener(var2.getPropertyName(), (VetoableChangeListener)var2.getListener());
         } else {
            this.map.remove((String)null, var1);
         }

      }
   }

   public VetoableChangeListener[] getVetoableChangeListeners() {
      return (VetoableChangeListener[])this.map.getListeners();
   }

   public void addVetoableChangeListener(String var1, VetoableChangeListener var2) {
      if (var2 != null && var1 != null) {
         var2 = this.map.extract(var2);
         if (var2 != null) {
            this.map.add(var1, var2);
         }

      }
   }

   public void removeVetoableChangeListener(String var1, VetoableChangeListener var2) {
      if (var2 != null && var1 != null) {
         var2 = this.map.extract(var2);
         if (var2 != null) {
            this.map.remove(var1, var2);
         }

      }
   }

   public VetoableChangeListener[] getVetoableChangeListeners(String var1) {
      return (VetoableChangeListener[])this.map.getListeners(var1);
   }

   public void fireVetoableChange(String var1, Object var2, Object var3) throws PropertyVetoException {
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         this.fireVetoableChange(new PropertyChangeEvent(this.source, var1, var2, var3));
      }

   }

   public void fireVetoableChange(String var1, int var2, int var3) throws PropertyVetoException {
      if (var2 != var3) {
         this.fireVetoableChange(var1, var2, var3);
      }

   }

   public void fireVetoableChange(String var1, boolean var2, boolean var3) throws PropertyVetoException {
      if (var2 != var3) {
         this.fireVetoableChange(var1, var2, var3);
      }

   }

   public void fireVetoableChange(PropertyChangeEvent var1) throws PropertyVetoException {
      Object var2 = var1.getOldValue();
      Object var3 = var1.getNewValue();
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         String var4 = var1.getPropertyName();
         VetoableChangeListener[] var5 = (VetoableChangeListener[])this.map.get((String)null);
         VetoableChangeListener[] var6 = var4 != null ? (VetoableChangeListener[])this.map.get(var4) : null;
         VetoableChangeListener[] var7;
         if (var5 == null) {
            var7 = var6;
         } else if (var6 == null) {
            var7 = var5;
         } else {
            var7 = new VetoableChangeListener[var5.length + var6.length];
            System.arraycopy(var5, 0, var7, 0, var5.length);
            System.arraycopy(var6, 0, var7, var5.length, var6.length);
         }

         if (var7 != null) {
            int var8 = 0;

            try {
               while(var8 < var7.length) {
                  var7[var8].vetoableChange(var1);
                  ++var8;
               }
            } catch (PropertyVetoException var13) {
               var1 = new PropertyChangeEvent(this.source, var4, var3, var2);

               for(int var10 = 0; var10 < var8; ++var10) {
                  try {
                     var7[var10].vetoableChange(var1);
                  } catch (PropertyVetoException var12) {
                  }
               }

               throw var13;
            }
         }
      }

   }

   public boolean hasListeners(String var1) {
      return this.map.hasListeners(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = null;
      VetoableChangeListener[] var3 = null;
      synchronized(this.map) {
         Iterator var5 = this.map.getEntries().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            String var7 = (String)var6.getKey();
            if (var7 == null) {
               var3 = (VetoableChangeListener[])var6.getValue();
            } else {
               if (var2 == null) {
                  var2 = new Hashtable();
               }

               VetoableChangeSupport var8 = new VetoableChangeSupport(this.source);
               var8.map.set((String)null, (EventListener[])var6.getValue());
               var2.put(var7, var8);
            }
         }
      }

      ObjectOutputStream.PutField var4 = var1.putFields();
      var4.put("children", var2);
      var4.put("source", this.source);
      var4.put("vetoableChangeSupportSerializedDataVersion", (int)2);
      var1.writeFields();
      if (var3 != null) {
         VetoableChangeListener[] var11 = var3;
         int var12 = var3.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            VetoableChangeListener var14 = var11[var13];
            if (var14 instanceof Serializable) {
               var1.writeObject(var14);
            }
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.map = new VetoableChangeSupport.VetoableChangeListenerMap();
      ObjectInputStream.GetField var2 = var1.readFields();
      Hashtable var3 = (Hashtable)var2.get("children", (Object)null);
      this.source = var2.get("source", (Object)null);
      var2.get("vetoableChangeSupportSerializedDataVersion", (int)2);

      Object var4;
      while(null != (var4 = var1.readObject())) {
         this.map.add((String)null, (VetoableChangeListener)var4);
      }

      if (var3 != null) {
         Iterator var5 = var3.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            VetoableChangeListener[] var7 = ((VetoableChangeSupport)var6.getValue()).getVetoableChangeListeners();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               VetoableChangeListener var10 = var7[var9];
               this.map.add((String)var6.getKey(), var10);
            }
         }
      }

   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("vetoableChangeSupportSerializedDataVersion", Integer.TYPE)};
   }

   private static final class VetoableChangeListenerMap extends ChangeListenerMap<VetoableChangeListener> {
      private static final VetoableChangeListener[] EMPTY = new VetoableChangeListener[0];

      private VetoableChangeListenerMap() {
      }

      protected VetoableChangeListener[] newArray(int var1) {
         return 0 < var1 ? new VetoableChangeListener[var1] : EMPTY;
      }

      protected VetoableChangeListener newProxy(String var1, VetoableChangeListener var2) {
         return new VetoableChangeListenerProxy(var1, var2);
      }

      public final VetoableChangeListener extract(VetoableChangeListener var1) {
         while(var1 instanceof VetoableChangeListenerProxy) {
            var1 = (VetoableChangeListener)((VetoableChangeListenerProxy)var1).getListener();
         }

         return var1;
      }

      // $FF: synthetic method
      VetoableChangeListenerMap(Object var1) {
         this();
      }
   }
}
