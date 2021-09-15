package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

class AncestorNotifier implements ComponentListener, PropertyChangeListener, Serializable {
   transient Component firstInvisibleAncestor;
   EventListenerList listenerList = new EventListenerList();
   JComponent root;

   AncestorNotifier(JComponent var1) {
      this.root = var1;
      this.addListeners(var1, true);
   }

   void addAncestorListener(AncestorListener var1) {
      this.listenerList.add(AncestorListener.class, var1);
   }

   void removeAncestorListener(AncestorListener var1) {
      this.listenerList.remove(AncestorListener.class, var1);
   }

   AncestorListener[] getAncestorListeners() {
      return (AncestorListener[])this.listenerList.getListeners(AncestorListener.class);
   }

   protected void fireAncestorAdded(JComponent var1, int var2, Container var3, Container var4) {
      Object[] var5 = this.listenerList.getListenerList();

      for(int var6 = var5.length - 2; var6 >= 0; var6 -= 2) {
         if (var5[var6] == AncestorListener.class) {
            AncestorEvent var7 = new AncestorEvent(var1, var2, var3, var4);
            ((AncestorListener)var5[var6 + 1]).ancestorAdded(var7);
         }
      }

   }

   protected void fireAncestorRemoved(JComponent var1, int var2, Container var3, Container var4) {
      Object[] var5 = this.listenerList.getListenerList();

      for(int var6 = var5.length - 2; var6 >= 0; var6 -= 2) {
         if (var5[var6] == AncestorListener.class) {
            AncestorEvent var7 = new AncestorEvent(var1, var2, var3, var4);
            ((AncestorListener)var5[var6 + 1]).ancestorRemoved(var7);
         }
      }

   }

   protected void fireAncestorMoved(JComponent var1, int var2, Container var3, Container var4) {
      Object[] var5 = this.listenerList.getListenerList();

      for(int var6 = var5.length - 2; var6 >= 0; var6 -= 2) {
         if (var5[var6] == AncestorListener.class) {
            AncestorEvent var7 = new AncestorEvent(var1, var2, var3, var4);
            ((AncestorListener)var5[var6 + 1]).ancestorMoved(var7);
         }
      }

   }

   void removeAllListeners() {
      this.removeListeners(this.root);
   }

   void addListeners(Component var1, boolean var2) {
      this.firstInvisibleAncestor = null;

      for(Object var3 = var1; this.firstInvisibleAncestor == null; var3 = ((Component)var3).getParent()) {
         if (var2 || var3 != var1) {
            ((Component)var3).addComponentListener(this);
            if (var3 instanceof JComponent) {
               JComponent var4 = (JComponent)var3;
               var4.addPropertyChangeListener(this);
            }
         }

         if (!((Component)var3).isVisible() || ((Component)var3).getParent() == null || var3 instanceof Window) {
            this.firstInvisibleAncestor = (Component)var3;
         }
      }

      if (this.firstInvisibleAncestor instanceof Window && this.firstInvisibleAncestor.isVisible()) {
         this.firstInvisibleAncestor = null;
      }

   }

   void removeListeners(Component var1) {
      for(Object var2 = var1; var2 != null; var2 = ((Component)var2).getParent()) {
         ((Component)var2).removeComponentListener(this);
         if (var2 instanceof JComponent) {
            JComponent var3 = (JComponent)var2;
            var3.removePropertyChangeListener(this);
         }

         if (var2 == this.firstInvisibleAncestor || var2 instanceof Window) {
            break;
         }
      }

   }

   public void componentResized(ComponentEvent var1) {
   }

   public void componentMoved(ComponentEvent var1) {
      Component var2 = var1.getComponent();
      this.fireAncestorMoved(this.root, 3, (Container)var2, var2.getParent());
   }

   public void componentShown(ComponentEvent var1) {
      Component var2 = var1.getComponent();
      if (var2 == this.firstInvisibleAncestor) {
         this.addListeners(var2, false);
         if (this.firstInvisibleAncestor == null) {
            this.fireAncestorAdded(this.root, 1, (Container)var2, var2.getParent());
         }
      }

   }

   public void componentHidden(ComponentEvent var1) {
      Component var2 = var1.getComponent();
      boolean var3 = this.firstInvisibleAncestor == null;
      if (!(var2 instanceof Window)) {
         this.removeListeners(var2.getParent());
      }

      this.firstInvisibleAncestor = var2;
      if (var3) {
         this.fireAncestorRemoved(this.root, 2, (Container)var2, var2.getParent());
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 != null && (var2.equals("parent") || var2.equals("ancestor"))) {
         JComponent var3 = (JComponent)var1.getSource();
         if (var1.getNewValue() != null) {
            if (var3 == this.firstInvisibleAncestor) {
               this.addListeners(var3, false);
               if (this.firstInvisibleAncestor == null) {
                  this.fireAncestorAdded(this.root, 1, var3, var3.getParent());
               }
            }
         } else {
            boolean var4 = this.firstInvisibleAncestor == null;
            Container var5 = (Container)var1.getOldValue();
            this.removeListeners(var5);
            this.firstInvisibleAncestor = var3;
            if (var4) {
               this.fireAncestorRemoved(this.root, 2, var3, var5);
            }
         }
      }

   }
}
