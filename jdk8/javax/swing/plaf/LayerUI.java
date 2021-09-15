package javax.swing.plaf;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

public class LayerUI<V extends Component> extends ComponentUI implements Serializable {
   private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

   public void paint(Graphics var1, JComponent var2) {
      var2.paint(var1);
   }

   public void eventDispatched(AWTEvent var1, JLayer<? extends V> var2) {
      if (var1 instanceof FocusEvent) {
         this.processFocusEvent((FocusEvent)var1, var2);
      } else if (var1 instanceof MouseEvent) {
         switch(var1.getID()) {
         case 500:
         case 501:
         case 502:
         case 504:
         case 505:
            this.processMouseEvent((MouseEvent)var1, var2);
            break;
         case 503:
         case 506:
            this.processMouseMotionEvent((MouseEvent)var1, var2);
            break;
         case 507:
            this.processMouseWheelEvent((MouseWheelEvent)var1, var2);
         }
      } else if (var1 instanceof KeyEvent) {
         this.processKeyEvent((KeyEvent)var1, var2);
      } else if (var1 instanceof ComponentEvent) {
         this.processComponentEvent((ComponentEvent)var1, var2);
      } else if (var1 instanceof InputMethodEvent) {
         this.processInputMethodEvent((InputMethodEvent)var1, var2);
      } else if (var1 instanceof HierarchyEvent) {
         switch(var1.getID()) {
         case 1400:
            this.processHierarchyEvent((HierarchyEvent)var1, var2);
            break;
         case 1401:
         case 1402:
            this.processHierarchyBoundsEvent((HierarchyEvent)var1, var2);
         }
      }

   }

   protected void processComponentEvent(ComponentEvent var1, JLayer<? extends V> var2) {
   }

   protected void processFocusEvent(FocusEvent var1, JLayer<? extends V> var2) {
   }

   protected void processKeyEvent(KeyEvent var1, JLayer<? extends V> var2) {
   }

   protected void processMouseEvent(MouseEvent var1, JLayer<? extends V> var2) {
   }

   protected void processMouseMotionEvent(MouseEvent var1, JLayer<? extends V> var2) {
   }

   protected void processMouseWheelEvent(MouseWheelEvent var1, JLayer<? extends V> var2) {
   }

   protected void processInputMethodEvent(InputMethodEvent var1, JLayer<? extends V> var2) {
   }

   protected void processHierarchyEvent(HierarchyEvent var1, JLayer<? extends V> var2) {
   }

   protected void processHierarchyBoundsEvent(HierarchyEvent var1, JLayer<? extends V> var2) {
   }

   public void updateUI(JLayer<? extends V> var1) {
   }

   public void installUI(JComponent var1) {
      this.addPropertyChangeListener((JLayer)var1);
   }

   public void uninstallUI(JComponent var1) {
      this.removePropertyChangeListener((JLayer)var1);
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.propertyChangeSupport.addPropertyChangeListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.propertyChangeSupport.removePropertyChangeListener(var1);
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return this.propertyChangeSupport.getPropertyChangeListeners();
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.propertyChangeSupport.addPropertyChangeListener(var1, var2);
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.propertyChangeSupport.removePropertyChangeListener(var1, var2);
   }

   public PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      return this.propertyChangeSupport.getPropertyChangeListeners(var1);
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      this.propertyChangeSupport.firePropertyChange(var1, var2, var3);
   }

   public void applyPropertyChange(PropertyChangeEvent var1, JLayer<? extends V> var2) {
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      JLayer var4 = (JLayer)var1;
      return var4.getView() != null ? var4.getView().getBaseline(var2, var3) : super.getBaseline(var1, var2, var3);
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      JLayer var2 = (JLayer)var1;
      return var2.getView() != null ? var2.getView().getBaselineResizeBehavior() : super.getBaselineResizeBehavior(var1);
   }

   public void doLayout(JLayer<? extends V> var1) {
      Component var2 = var1.getView();
      if (var2 != null) {
         var2.setBounds(0, 0, var1.getWidth(), var1.getHeight());
      }

      JPanel var3 = var1.getGlassPane();
      if (var3 != null) {
         var3.setBounds(0, 0, var1.getWidth(), var1.getHeight());
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      JLayer var2 = (JLayer)var1;
      Component var3 = var2.getView();
      return var3 != null ? var3.getPreferredSize() : super.getPreferredSize(var1);
   }

   public Dimension getMinimumSize(JComponent var1) {
      JLayer var2 = (JLayer)var1;
      Component var3 = var2.getView();
      return var3 != null ? var3.getMinimumSize() : super.getMinimumSize(var1);
   }

   public Dimension getMaximumSize(JComponent var1) {
      JLayer var2 = (JLayer)var1;
      Component var3 = var2.getView();
      return var3 != null ? var3.getMaximumSize() : super.getMaximumSize(var1);
   }

   public void paintImmediately(int var1, int var2, int var3, int var4, JLayer<? extends V> var5) {
      var5.paintImmediately(var1, var2, var3, var4);
   }
}
