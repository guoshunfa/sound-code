package javax.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.plaf.LayerUI;
import sun.awt.AWTAccessor;

public final class JLayer<V extends Component> extends JComponent implements Scrollable, PropertyChangeListener, Accessible {
   private V view;
   private LayerUI<? super V> layerUI;
   private JPanel glassPane;
   private long eventMask;
   private transient boolean isPainting;
   private transient boolean isPaintingImmediately;
   private static final JLayer.LayerEventController eventController = new JLayer.LayerEventController();

   public JLayer() {
      this((Component)null);
   }

   public JLayer(V var1) {
      this(var1, new LayerUI());
   }

   public JLayer(V var1, LayerUI<V> var2) {
      this.setGlassPane(this.createGlassPane());
      this.setView(var1);
      this.setUI(var2);
   }

   public V getView() {
      return this.view;
   }

   public void setView(V var1) {
      Component var2 = this.getView();
      if (var2 != null) {
         super.remove(var2);
      }

      if (var1 != null) {
         super.addImpl(var1, (Object)null, this.getComponentCount());
      }

      this.view = var1;
      this.firePropertyChange("view", var2, var1);
      this.revalidate();
      this.repaint();
   }

   public void setUI(LayerUI<? super V> var1) {
      this.layerUI = var1;
      super.setUI(var1);
   }

   public LayerUI<? super V> getUI() {
      return this.layerUI;
   }

   public JPanel getGlassPane() {
      return this.glassPane;
   }

   public void setGlassPane(JPanel var1) {
      JPanel var2 = this.getGlassPane();
      boolean var3 = false;
      if (var2 != null) {
         var3 = var2.isVisible();
         super.remove(var2);
      }

      if (var1 != null) {
         AWTAccessor.getComponentAccessor().setMixingCutoutShape(var1, new Rectangle());
         var1.setVisible(var3);
         super.addImpl(var1, (Object)null, 0);
      }

      this.glassPane = var1;
      this.firePropertyChange("glassPane", var2, var1);
      this.revalidate();
      this.repaint();
   }

   public JPanel createGlassPane() {
      return new JLayer.DefaultLayerGlassPane();
   }

   public void setLayout(LayoutManager var1) {
      if (var1 != null) {
         throw new IllegalArgumentException("JLayer.setLayout() not supported");
      }
   }

   public void setBorder(Border var1) {
      if (var1 != null) {
         throw new IllegalArgumentException("JLayer.setBorder() not supported");
      }
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      throw new UnsupportedOperationException("Adding components to JLayer is not supported, use setView() or setGlassPane() instead");
   }

   public void remove(Component var1) {
      if (var1 == null) {
         super.remove(var1);
      } else if (var1 == this.getView()) {
         this.setView((Component)null);
      } else if (var1 == this.getGlassPane()) {
         this.setGlassPane((JPanel)null);
      } else {
         super.remove(var1);
      }

   }

   public void removeAll() {
      if (this.view != null) {
         this.setView((Component)null);
      }

      if (this.glassPane != null) {
         this.setGlassPane((JPanel)null);
      }

   }

   protected boolean isPaintingOrigin() {
      return true;
   }

   public void paintImmediately(int var1, int var2, int var3, int var4) {
      if (!this.isPaintingImmediately && this.getUI() != null) {
         this.isPaintingImmediately = true;

         try {
            this.getUI().paintImmediately(var1, var2, var3, var4, this);
         } finally {
            this.isPaintingImmediately = false;
         }
      } else {
         super.paintImmediately(var1, var2, var3, var4);
      }

   }

   public void paint(Graphics var1) {
      if (!this.isPainting) {
         this.isPainting = true;

         try {
            super.paintComponent(var1);
         } finally {
            this.isPainting = false;
         }
      } else {
         super.paint(var1);
      }

   }

   protected void paintComponent(Graphics var1) {
   }

   public boolean isOptimizedDrawingEnabled() {
      return false;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (this.getUI() != null) {
         this.getUI().applyPropertyChange(var1, this);
      }

   }

   public void setLayerEventMask(long var1) {
      long var3 = this.getLayerEventMask();
      this.eventMask = var1;
      this.firePropertyChange("layerEventMask", var3, var1);
      if (var1 != var3) {
         this.disableEvents(var3);
         this.enableEvents(this.eventMask);
         if (this.isDisplayable()) {
            eventController.updateAWTEventListener(var3, var1);
         }
      }

   }

   public long getLayerEventMask() {
      return this.eventMask;
   }

   public void updateUI() {
      if (this.getUI() != null) {
         this.getUI().updateUI(this);
      }

   }

   public Dimension getPreferredScrollableViewportSize() {
      return this.getView() instanceof Scrollable ? ((Scrollable)this.getView()).getPreferredScrollableViewportSize() : this.getPreferredSize();
   }

   public int getScrollableBlockIncrement(Rectangle var1, int var2, int var3) {
      if (this.getView() instanceof Scrollable) {
         return ((Scrollable)this.getView()).getScrollableBlockIncrement(var1, var2, var3);
      } else {
         return var2 == 1 ? var1.height : var1.width;
      }
   }

   public boolean getScrollableTracksViewportHeight() {
      return this.getView() instanceof Scrollable ? ((Scrollable)this.getView()).getScrollableTracksViewportHeight() : false;
   }

   public boolean getScrollableTracksViewportWidth() {
      return this.getView() instanceof Scrollable ? ((Scrollable)this.getView()).getScrollableTracksViewportWidth() : false;
   }

   public int getScrollableUnitIncrement(Rectangle var1, int var2, int var3) {
      return this.getView() instanceof Scrollable ? ((Scrollable)this.getView()).getScrollableUnitIncrement(var1, var2, var3) : 1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.layerUI != null) {
         this.setUI(this.layerUI);
      }

      if (this.eventMask != 0L) {
         eventController.updateAWTEventListener(0L, this.eventMask);
      }

   }

   public void addNotify() {
      super.addNotify();
      eventController.updateAWTEventListener(0L, this.eventMask);
   }

   public void removeNotify() {
      super.removeNotify();
      eventController.updateAWTEventListener(this.eventMask, 0L);
   }

   public void doLayout() {
      if (this.getUI() != null) {
         this.getUI().doLayout(this);
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JComponent.AccessibleJComponent() {
            public AccessibleRole getAccessibleRole() {
               return AccessibleRole.PANEL;
            }
         };
      }

      return this.accessibleContext;
   }

   private static class DefaultLayerGlassPane extends JPanel {
      public DefaultLayerGlassPane() {
         this.setOpaque(false);
      }

      public boolean contains(int var1, int var2) {
         for(int var3 = 0; var3 < this.getComponentCount(); ++var3) {
            Component var4 = this.getComponent(var3);
            Point var5 = SwingUtilities.convertPoint(this, new Point(var1, var2), var4);
            if (var4.isVisible() && var4.contains(var5)) {
               return true;
            }
         }

         if (this.getMouseListeners().length == 0 && this.getMouseMotionListeners().length == 0 && this.getMouseWheelListeners().length == 0 && !this.isCursorSet()) {
            return false;
         } else {
            return super.contains(var1, var2);
         }
      }
   }

   private static class LayerEventController implements AWTEventListener {
      private ArrayList<Long> layerMaskList;
      private long currentEventMask;
      private static final long ACCEPTED_EVENTS = 231487L;

      private LayerEventController() {
         this.layerMaskList = new ArrayList();
      }

      public void eventDispatched(AWTEvent var1) {
         Object var2 = var1.getSource();
         if (var2 instanceof Component) {
            for(Object var3 = (Component)var2; var3 != null; var3 = ((Component)var3).getParent()) {
               if (var3 instanceof JLayer) {
                  JLayer var4 = (JLayer)var3;
                  LayerUI var5 = var4.getUI();
                  if (var5 != null && this.isEventEnabled(var4.getLayerEventMask(), var1.getID()) && (!(var1 instanceof InputEvent) || !((InputEvent)var1).isConsumed())) {
                     var5.eventDispatched(var1, var4);
                  }
               }
            }
         }

      }

      private void updateAWTEventListener(long var1, long var3) {
         if (var1 != 0L) {
            this.layerMaskList.remove(var1);
         }

         if (var3 != 0L) {
            this.layerMaskList.add(var3);
         }

         long var5 = 0L;

         Long var8;
         for(Iterator var7 = this.layerMaskList.iterator(); var7.hasNext(); var5 |= var8) {
            var8 = (Long)var7.next();
         }

         var5 &= 231487L;
         if (var5 == 0L) {
            this.removeAWTEventListener();
         } else if (this.getCurrentEventMask() != var5) {
            this.removeAWTEventListener();
            this.addAWTEventListener(var5);
         }

         this.currentEventMask = var5;
      }

      private long getCurrentEventMask() {
         return this.currentEventMask;
      }

      private void addAWTEventListener(final long var1) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Toolkit.getDefaultToolkit().addAWTEventListener(LayerEventController.this, var1);
               return null;
            }
         });
      }

      private void removeAWTEventListener() {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Toolkit.getDefaultToolkit().removeAWTEventListener(LayerEventController.this);
               return null;
            }
         });
      }

      private boolean isEventEnabled(long var1, int var3) {
         return (var1 & 1L) != 0L && var3 >= 100 && var3 <= 103 || (var1 & 2L) != 0L && var3 >= 300 && var3 <= 301 || (var1 & 4L) != 0L && var3 >= 1004 && var3 <= 1005 || (var1 & 8L) != 0L && var3 >= 400 && var3 <= 402 || (var1 & 131072L) != 0L && var3 == 507 || (var1 & 32L) != 0L && (var3 == 503 || var3 == 506) || (var1 & 16L) != 0L && var3 != 503 && var3 != 506 && var3 != 507 && var3 >= 500 && var3 <= 507 || (var1 & 2048L) != 0L && var3 >= 1100 && var3 <= 1101 || (var1 & 32768L) != 0L && var3 == 1400 || (var1 & 65536L) != 0L && (var3 == 1401 || var3 == 1402);
      }

      // $FF: synthetic method
      LayerEventController(Object var1) {
         this();
      }
   }
}
