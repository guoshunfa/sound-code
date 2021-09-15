package javax.swing;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollBarUI;

public class JScrollBar extends JComponent implements Adjustable, Accessible {
   private static final String uiClassID = "ScrollBarUI";
   private ChangeListener fwdAdjustmentEvents;
   protected BoundedRangeModel model;
   protected int orientation;
   protected int unitIncrement;
   protected int blockIncrement;

   private void checkOrientation(int var1) {
      switch(var1) {
      case 0:
      case 1:
         return;
      default:
         throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
      }
   }

   public JScrollBar(int var1, int var2, int var3, int var4, int var5) {
      this.fwdAdjustmentEvents = new JScrollBar.ModelListener();
      this.checkOrientation(var1);
      this.unitIncrement = 1;
      this.blockIncrement = var3 == 0 ? 1 : var3;
      this.orientation = var1;
      this.model = new DefaultBoundedRangeModel(var2, var3, var4, var5);
      this.model.addChangeListener(this.fwdAdjustmentEvents);
      this.setRequestFocusEnabled(false);
      this.updateUI();
   }

   public JScrollBar(int var1) {
      this(var1, 0, 10, 0, 100);
   }

   public JScrollBar() {
      this(1);
   }

   public void setUI(ScrollBarUI var1) {
      super.setUI(var1);
   }

   public ScrollBarUI getUI() {
      return (ScrollBarUI)this.ui;
   }

   public void updateUI() {
      this.setUI((ScrollBarUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ScrollBarUI";
   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setOrientation(int var1) {
      this.checkOrientation(var1);
      int var2 = this.orientation;
      this.orientation = var1;
      this.firePropertyChange("orientation", var2, var1);
      if (var2 != var1 && this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleState", var2 == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, var1 == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
      }

      if (var1 != var2) {
         this.revalidate();
      }

   }

   public BoundedRangeModel getModel() {
      return this.model;
   }

   public void setModel(BoundedRangeModel var1) {
      Integer var2 = null;
      BoundedRangeModel var3 = this.model;
      if (this.model != null) {
         this.model.removeChangeListener(this.fwdAdjustmentEvents);
         var2 = this.model.getValue();
      }

      this.model = var1;
      if (this.model != null) {
         this.model.addChangeListener(this.fwdAdjustmentEvents);
      }

      this.firePropertyChange("model", var3, this.model);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleValue", var2, new Integer(this.model.getValue()));
      }

   }

   public int getUnitIncrement(int var1) {
      return this.unitIncrement;
   }

   public void setUnitIncrement(int var1) {
      int var2 = this.unitIncrement;
      this.unitIncrement = var1;
      this.firePropertyChange("unitIncrement", var2, var1);
   }

   public int getBlockIncrement(int var1) {
      return this.blockIncrement;
   }

   public void setBlockIncrement(int var1) {
      int var2 = this.blockIncrement;
      this.blockIncrement = var1;
      this.firePropertyChange("blockIncrement", var2, var1);
   }

   public int getUnitIncrement() {
      return this.unitIncrement;
   }

   public int getBlockIncrement() {
      return this.blockIncrement;
   }

   public int getValue() {
      return this.getModel().getValue();
   }

   public void setValue(int var1) {
      BoundedRangeModel var2 = this.getModel();
      int var3 = var2.getValue();
      var2.setValue(var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleValue", var3, var2.getValue());
      }

   }

   public int getVisibleAmount() {
      return this.getModel().getExtent();
   }

   public void setVisibleAmount(int var1) {
      this.getModel().setExtent(var1);
   }

   public int getMinimum() {
      return this.getModel().getMinimum();
   }

   public void setMinimum(int var1) {
      this.getModel().setMinimum(var1);
   }

   public int getMaximum() {
      return this.getModel().getMaximum();
   }

   public void setMaximum(int var1) {
      this.getModel().setMaximum(var1);
   }

   public boolean getValueIsAdjusting() {
      return this.getModel().getValueIsAdjusting();
   }

   public void setValueIsAdjusting(boolean var1) {
      BoundedRangeModel var2 = this.getModel();
      boolean var3 = var2.getValueIsAdjusting();
      var2.setValueIsAdjusting(var1);
      if (var3 != var1 && this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleState", var3 ? AccessibleState.BUSY : null, var1 ? AccessibleState.BUSY : null);
      }

   }

   public void setValues(int var1, int var2, int var3, int var4) {
      BoundedRangeModel var5 = this.getModel();
      int var6 = var5.getValue();
      var5.setRangeProperties(var1, var2, var3, var4, var5.getValueIsAdjusting());
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleValue", var6, var5.getValue());
      }

   }

   public void addAdjustmentListener(AdjustmentListener var1) {
      this.listenerList.add(AdjustmentListener.class, var1);
   }

   public void removeAdjustmentListener(AdjustmentListener var1) {
      this.listenerList.remove(AdjustmentListener.class, var1);
   }

   public AdjustmentListener[] getAdjustmentListeners() {
      return (AdjustmentListener[])this.listenerList.getListeners(AdjustmentListener.class);
   }

   protected void fireAdjustmentValueChanged(int var1, int var2, int var3) {
      this.fireAdjustmentValueChanged(var1, var2, var3, this.getValueIsAdjusting());
   }

   private void fireAdjustmentValueChanged(int var1, int var2, int var3, boolean var4) {
      Object[] var5 = this.listenerList.getListenerList();
      AdjustmentEvent var6 = null;

      for(int var7 = var5.length - 2; var7 >= 0; var7 -= 2) {
         if (var5[var7] == AdjustmentListener.class) {
            if (var6 == null) {
               var6 = new AdjustmentEvent(this, var1, var2, var3, var4);
            }

            ((AdjustmentListener)var5[var7 + 1]).adjustmentValueChanged(var6);
         }
      }

   }

   public Dimension getMinimumSize() {
      Dimension var1 = this.getPreferredSize();
      return this.orientation == 1 ? new Dimension(var1.width, 5) : new Dimension(5, var1.height);
   }

   public Dimension getMaximumSize() {
      Dimension var1 = this.getPreferredSize();
      return this.getOrientation() == 1 ? new Dimension(var1.width, 32767) : new Dimension(32767, var1.height);
   }

   public void setEnabled(boolean var1) {
      super.setEnabled(var1);
      Component[] var2 = this.getComponents();
      Component[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Component var6 = var3[var5];
         var6.setEnabled(var1);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ScrollBarUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.orientation == 0 ? "HORIZONTAL" : "VERTICAL";
      return super.paramString() + ",blockIncrement=" + this.blockIncrement + ",orientation=" + var1 + ",unitIncrement=" + this.unitIncrement;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JScrollBar.AccessibleJScrollBar();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJScrollBar extends JComponent.AccessibleJComponent implements AccessibleValue {
      protected AccessibleJScrollBar() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JScrollBar.this.getValueIsAdjusting()) {
            var1.add(AccessibleState.BUSY);
         }

         if (JScrollBar.this.getOrientation() == 1) {
            var1.add(AccessibleState.VERTICAL);
         } else {
            var1.add(AccessibleState.HORIZONTAL);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SCROLL_BAR;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         return JScrollBar.this.getValue();
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 == null) {
            return false;
         } else {
            JScrollBar.this.setValue(var1.intValue());
            return true;
         }
      }

      public Number getMinimumAccessibleValue() {
         return JScrollBar.this.getMinimum();
      }

      public Number getMaximumAccessibleValue() {
         return new Integer(JScrollBar.this.model.getMaximum() - JScrollBar.this.model.getExtent());
      }
   }

   private class ModelListener implements ChangeListener, Serializable {
      private ModelListener() {
      }

      public void stateChanged(ChangeEvent var1) {
         Object var2 = var1.getSource();
         if (var2 instanceof BoundedRangeModel) {
            short var3 = 601;
            byte var4 = 5;
            BoundedRangeModel var5 = (BoundedRangeModel)var2;
            int var6 = var5.getValue();
            boolean var7 = var5.getValueIsAdjusting();
            JScrollBar.this.fireAdjustmentValueChanged(var3, var4, var6, var7);
         }

      }

      // $FF: synthetic method
      ModelListener(Object var2) {
         this();
      }
   }
}
