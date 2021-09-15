package javax.swing;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.NumberFormat;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;

public class JProgressBar extends JComponent implements SwingConstants, Accessible {
   private static final String uiClassID = "ProgressBarUI";
   protected int orientation;
   protected boolean paintBorder;
   protected BoundedRangeModel model;
   protected String progressString;
   protected boolean paintString;
   private static final int defaultMinimum = 0;
   private static final int defaultMaximum = 100;
   private static final int defaultOrientation = 0;
   protected transient ChangeEvent changeEvent;
   protected ChangeListener changeListener;
   private transient Format format;
   private boolean indeterminate;

   public JProgressBar() {
      this(0);
   }

   public JProgressBar(int var1) {
      this(var1, 0, 100);
   }

   public JProgressBar(int var1, int var2) {
      this(0, var1, var2);
   }

   public JProgressBar(int var1, int var2, int var3) {
      this.changeEvent = null;
      this.changeListener = null;
      this.setModel(new DefaultBoundedRangeModel(var2, 0, var2, var3));
      this.updateUI();
      this.setOrientation(var1);
      this.setBorderPainted(true);
      this.setStringPainted(false);
      this.setString((String)null);
      this.setIndeterminate(false);
   }

   public JProgressBar(BoundedRangeModel var1) {
      this.changeEvent = null;
      this.changeListener = null;
      this.setModel(var1);
      this.updateUI();
      this.setOrientation(0);
      this.setBorderPainted(true);
      this.setStringPainted(false);
      this.setString((String)null);
      this.setIndeterminate(false);
   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setOrientation(int var1) {
      if (this.orientation != var1) {
         switch(var1) {
         case 0:
         case 1:
            int var2 = this.orientation;
            this.orientation = var1;
            this.firePropertyChange("orientation", var2, var1);
            if (this.accessibleContext != null) {
               this.accessibleContext.firePropertyChange("AccessibleState", var2 == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, this.orientation == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
            }

            this.revalidate();
            break;
         default:
            throw new IllegalArgumentException(var1 + " is not a legal orientation");
         }
      }

   }

   public boolean isStringPainted() {
      return this.paintString;
   }

   public void setStringPainted(boolean var1) {
      boolean var2 = this.paintString;
      this.paintString = var1;
      this.firePropertyChange("stringPainted", var2, this.paintString);
      if (this.paintString != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public String getString() {
      if (this.progressString != null) {
         return this.progressString;
      } else {
         if (this.format == null) {
            this.format = NumberFormat.getPercentInstance();
         }

         return this.format.format(new Double(this.getPercentComplete()));
      }
   }

   public void setString(String var1) {
      String var2 = this.progressString;
      this.progressString = var1;
      this.firePropertyChange("string", var2, this.progressString);
      if (this.progressString == null || var2 == null || !this.progressString.equals(var2)) {
         this.repaint();
      }

   }

   public double getPercentComplete() {
      long var1 = (long)(this.model.getMaximum() - this.model.getMinimum());
      double var3 = (double)this.model.getValue();
      double var5 = (var3 - (double)this.model.getMinimum()) / (double)var1;
      return var5;
   }

   public boolean isBorderPainted() {
      return this.paintBorder;
   }

   public void setBorderPainted(boolean var1) {
      boolean var2 = this.paintBorder;
      this.paintBorder = var1;
      this.firePropertyChange("borderPainted", var2, this.paintBorder);
      if (this.paintBorder != var2) {
         this.repaint();
      }

   }

   protected void paintBorder(Graphics var1) {
      if (this.isBorderPainted()) {
         super.paintBorder(var1);
      }

   }

   public ProgressBarUI getUI() {
      return (ProgressBarUI)this.ui;
   }

   public void setUI(ProgressBarUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ProgressBarUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ProgressBarUI";
   }

   protected ChangeListener createChangeListener() {
      return new JProgressBar.ModelListener();
   }

   public void addChangeListener(ChangeListener var1) {
      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public BoundedRangeModel getModel() {
      return this.model;
   }

   public void setModel(BoundedRangeModel var1) {
      BoundedRangeModel var2 = this.getModel();
      if (var1 != var2) {
         if (var2 != null) {
            var2.removeChangeListener(this.changeListener);
            this.changeListener = null;
         }

         this.model = var1;
         if (var1 != null) {
            this.changeListener = this.createChangeListener();
            var1.addChangeListener(this.changeListener);
         }

         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", var2 == null ? null : var2.getValue(), var1 == null ? null : var1.getValue());
         }

         if (this.model != null) {
            this.model.setExtent(0);
         }

         this.repaint();
      }

   }

   public int getValue() {
      return this.getModel().getValue();
   }

   public int getMinimum() {
      return this.getModel().getMinimum();
   }

   public int getMaximum() {
      return this.getModel().getMaximum();
   }

   public void setValue(int var1) {
      BoundedRangeModel var2 = this.getModel();
      int var3 = var2.getValue();
      var2.setValue(var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleValue", var3, var2.getValue());
      }

   }

   public void setMinimum(int var1) {
      this.getModel().setMinimum(var1);
   }

   public void setMaximum(int var1) {
      this.getModel().setMaximum(var1);
   }

   public void setIndeterminate(boolean var1) {
      boolean var2 = this.indeterminate;
      this.indeterminate = var1;
      this.firePropertyChange("indeterminate", var2, this.indeterminate);
   }

   public boolean isIndeterminate() {
      return this.indeterminate;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ProgressBarUI")) {
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
      String var2 = this.paintBorder ? "true" : "false";
      String var3 = this.progressString != null ? this.progressString : "";
      String var4 = this.paintString ? "true" : "false";
      String var5 = this.indeterminate ? "true" : "false";
      return super.paramString() + ",orientation=" + var1 + ",paintBorder=" + var2 + ",paintString=" + var4 + ",progressString=" + var3 + ",indeterminateString=" + var5;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JProgressBar.AccessibleJProgressBar();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJProgressBar extends JComponent.AccessibleJComponent implements AccessibleValue {
      protected AccessibleJProgressBar() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JProgressBar.this.getModel().getValueIsAdjusting()) {
            var1.add(AccessibleState.BUSY);
         }

         if (JProgressBar.this.getOrientation() == 1) {
            var1.add(AccessibleState.VERTICAL);
         } else {
            var1.add(AccessibleState.HORIZONTAL);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PROGRESS_BAR;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         return JProgressBar.this.getValue();
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 == null) {
            return false;
         } else {
            JProgressBar.this.setValue(var1.intValue());
            return true;
         }
      }

      public Number getMinimumAccessibleValue() {
         return JProgressBar.this.getMinimum();
      }

      public Number getMaximumAccessibleValue() {
         return JProgressBar.this.model.getMaximum() - JProgressBar.this.model.getExtent();
      }
   }

   private class ModelListener implements ChangeListener, Serializable {
      private ModelListener() {
      }

      public void stateChanged(ChangeEvent var1) {
         JProgressBar.this.fireStateChanged();
      }

      // $FF: synthetic method
      ModelListener(Object var2) {
         this();
      }
   }
}
