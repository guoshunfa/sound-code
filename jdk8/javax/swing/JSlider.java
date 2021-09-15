package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.UIResource;

public class JSlider extends JComponent implements SwingConstants, Accessible {
   private static final String uiClassID = "SliderUI";
   private boolean paintTicks;
   private boolean paintTrack;
   private boolean paintLabels;
   private boolean isInverted;
   protected BoundedRangeModel sliderModel;
   protected int majorTickSpacing;
   protected int minorTickSpacing;
   protected boolean snapToTicks;
   boolean snapToValue;
   protected int orientation;
   private Dictionary labelTable;
   protected ChangeListener changeListener;
   protected transient ChangeEvent changeEvent;

   private void checkOrientation(int var1) {
      switch(var1) {
      case 0:
      case 1:
         return;
      default:
         throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
      }
   }

   public JSlider() {
      this(0, 0, 100, 50);
   }

   public JSlider(int var1) {
      this(var1, 0, 100, 50);
   }

   public JSlider(int var1, int var2) {
      this(0, var1, var2, (var1 + var2) / 2);
   }

   public JSlider(int var1, int var2, int var3) {
      this(0, var1, var2, var3);
   }

   public JSlider(int var1, int var2, int var3, int var4) {
      this.paintTicks = false;
      this.paintTrack = true;
      this.paintLabels = false;
      this.isInverted = false;
      this.snapToTicks = false;
      this.snapToValue = true;
      this.changeListener = this.createChangeListener();
      this.changeEvent = null;
      this.checkOrientation(var1);
      this.orientation = var1;
      this.setModel(new DefaultBoundedRangeModel(var4, 0, var2, var3));
      this.updateUI();
   }

   public JSlider(BoundedRangeModel var1) {
      this.paintTicks = false;
      this.paintTrack = true;
      this.paintLabels = false;
      this.isInverted = false;
      this.snapToTicks = false;
      this.snapToValue = true;
      this.changeListener = this.createChangeListener();
      this.changeEvent = null;
      this.orientation = 0;
      this.setModel(var1);
      this.updateUI();
   }

   public SliderUI getUI() {
      return (SliderUI)this.ui;
   }

   public void setUI(SliderUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((SliderUI)UIManager.getUI(this));
      this.updateLabelUIs();
   }

   public String getUIClassID() {
      return "SliderUI";
   }

   protected ChangeListener createChangeListener() {
      return new JSlider.ModelListener();
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
      return this.sliderModel;
   }

   public void setModel(BoundedRangeModel var1) {
      BoundedRangeModel var2 = this.getModel();
      if (var2 != null) {
         var2.removeChangeListener(this.changeListener);
      }

      this.sliderModel = var1;
      if (var1 != null) {
         var1.addChangeListener(this.changeListener);
      }

      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleValue", var2 == null ? null : var2.getValue(), var1 == null ? null : var1.getValue());
      }

      this.firePropertyChange("model", var2, this.sliderModel);
   }

   public int getValue() {
      return this.getModel().getValue();
   }

   public void setValue(int var1) {
      BoundedRangeModel var2 = this.getModel();
      int var3 = var2.getValue();
      if (var3 != var1) {
         var2.setValue(var1);
         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", var3, var2.getValue());
         }

      }
   }

   public int getMinimum() {
      return this.getModel().getMinimum();
   }

   public void setMinimum(int var1) {
      int var2 = this.getModel().getMinimum();
      this.getModel().setMinimum(var1);
      this.firePropertyChange("minimum", var2, var1);
   }

   public int getMaximum() {
      return this.getModel().getMaximum();
   }

   public void setMaximum(int var1) {
      int var2 = this.getModel().getMaximum();
      this.getModel().setMaximum(var1);
      this.firePropertyChange("maximum", var2, var1);
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

   public int getExtent() {
      return this.getModel().getExtent();
   }

   public void setExtent(int var1) {
      this.getModel().setExtent(var1);
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

   public void setFont(Font var1) {
      super.setFont(var1);
      this.updateLabelSizes();
   }

   public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      if (!this.isShowing()) {
         return false;
      } else {
         Enumeration var7 = this.labelTable.elements();

         JLabel var9;
         do {
            Component var8;
            do {
               if (!var7.hasMoreElements()) {
                  return false;
               }

               var8 = (Component)var7.nextElement();
            } while(!(var8 instanceof JLabel));

            var9 = (JLabel)var8;
         } while(!SwingUtilities.doesIconReferenceImage(var9.getIcon(), var1) && !SwingUtilities.doesIconReferenceImage(var9.getDisabledIcon(), var1));

         return super.imageUpdate(var1, var2, var3, var4, var5, var6);
      }
   }

   public Dictionary getLabelTable() {
      return this.labelTable;
   }

   public void setLabelTable(Dictionary var1) {
      Dictionary var2 = this.labelTable;
      this.labelTable = var1;
      this.updateLabelUIs();
      this.firePropertyChange("labelTable", var2, this.labelTable);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   protected void updateLabelUIs() {
      Dictionary var1 = this.getLabelTable();
      if (var1 != null) {
         Enumeration var2 = var1.keys();

         while(var2.hasMoreElements()) {
            JComponent var3 = (JComponent)var1.get(var2.nextElement());
            var3.updateUI();
            var3.setSize(var3.getPreferredSize());
         }

      }
   }

   private void updateLabelSizes() {
      Dictionary var1 = this.getLabelTable();
      if (var1 != null) {
         Enumeration var2 = var1.elements();

         while(var2.hasMoreElements()) {
            JComponent var3 = (JComponent)var2.nextElement();
            var3.setSize(var3.getPreferredSize());
         }
      }

   }

   public Hashtable createStandardLabels(int var1) {
      return this.createStandardLabels(var1, this.getMinimum());
   }

   public Hashtable createStandardLabels(int var1, int var2) {
      if (var2 <= this.getMaximum() && var2 >= this.getMinimum()) {
         if (var1 <= 0) {
            throw new IllegalArgumentException("Label incremement must be > 0");
         } else {
            class SmartHashtable extends Hashtable<Object, Object> implements PropertyChangeListener {
               int increment = 0;
               int start = 0;
               boolean startAtMin = false;

               public SmartHashtable(int var2, int var3) {
                  this.increment = var2;
                  this.start = var3;
                  this.startAtMin = var3 == JSlider.this.getMinimum();
                  this.createLabels();
               }

               public void propertyChange(PropertyChangeEvent var1) {
                  if (var1.getPropertyName().equals("minimum") && this.startAtMin) {
                     this.start = JSlider.this.getMinimum();
                  }

                  if (var1.getPropertyName().equals("minimum") || var1.getPropertyName().equals("maximum")) {
                     Enumeration var2 = JSlider.this.getLabelTable().keys();
                     Hashtable var3 = new Hashtable();

                     Object var4;
                     while(var2.hasMoreElements()) {
                        var4 = var2.nextElement();
                        Object var5 = JSlider.this.labelTable.get(var4);
                        if (!(var5 instanceof SmartHashtable.LabelUIResource)) {
                           var3.put(var4, var5);
                        }
                     }

                     this.clear();
                     this.createLabels();
                     var2 = var3.keys();

                     while(var2.hasMoreElements()) {
                        var4 = var2.nextElement();
                        this.put(var4, var3.get(var4));
                     }

                     ((JSlider)var1.getSource()).setLabelTable(this);
                  }

               }

               void createLabels() {
                  for(int var1 = this.start; var1 <= JSlider.this.getMaximum(); var1 += this.increment) {
                     this.put(var1, new SmartHashtable.LabelUIResource("" + var1, 0));
                  }

               }

               class LabelUIResource extends JLabel implements UIResource {
                  public LabelUIResource(String var2, int var3) {
                     super(var2, var3);
                     this.setName("Slider.label");
                  }

                  public Font getFont() {
                     Font var1 = super.getFont();
                     return var1 != null && !(var1 instanceof UIResource) ? var1 : JSlider.this.getFont();
                  }

                  public Color getForeground() {
                     Color var1 = super.getForeground();
                     if (var1 != null && !(var1 instanceof UIResource)) {
                        return var1;
                     } else {
                        return !(JSlider.this.getForeground() instanceof UIResource) ? JSlider.this.getForeground() : var1;
                     }
                  }
               }
            }

            SmartHashtable var3 = new SmartHashtable(var1, var2);
            Dictionary var4 = this.getLabelTable();
            if (var4 != null && var4 instanceof PropertyChangeListener) {
               this.removePropertyChangeListener((PropertyChangeListener)var4);
            }

            this.addPropertyChangeListener(var3);
            return var3;
         }
      } else {
         throw new IllegalArgumentException("Slider label start point out of range.");
      }
   }

   public boolean getInverted() {
      return this.isInverted;
   }

   public void setInverted(boolean var1) {
      boolean var2 = this.isInverted;
      this.isInverted = var1;
      this.firePropertyChange("inverted", var2, this.isInverted);
      if (var1 != var2) {
         this.repaint();
      }

   }

   public int getMajorTickSpacing() {
      return this.majorTickSpacing;
   }

   public void setMajorTickSpacing(int var1) {
      int var2 = this.majorTickSpacing;
      this.majorTickSpacing = var1;
      if (this.labelTable == null && this.getMajorTickSpacing() > 0 && this.getPaintLabels()) {
         this.setLabelTable(this.createStandardLabels(this.getMajorTickSpacing()));
      }

      this.firePropertyChange("majorTickSpacing", var2, this.majorTickSpacing);
      if (this.majorTickSpacing != var2 && this.getPaintTicks()) {
         this.repaint();
      }

   }

   public int getMinorTickSpacing() {
      return this.minorTickSpacing;
   }

   public void setMinorTickSpacing(int var1) {
      int var2 = this.minorTickSpacing;
      this.minorTickSpacing = var1;
      this.firePropertyChange("minorTickSpacing", var2, this.minorTickSpacing);
      if (this.minorTickSpacing != var2 && this.getPaintTicks()) {
         this.repaint();
      }

   }

   public boolean getSnapToTicks() {
      return this.snapToTicks;
   }

   boolean getSnapToValue() {
      return this.snapToValue;
   }

   public void setSnapToTicks(boolean var1) {
      boolean var2 = this.snapToTicks;
      this.snapToTicks = var1;
      this.firePropertyChange("snapToTicks", var2, this.snapToTicks);
   }

   void setSnapToValue(boolean var1) {
      boolean var2 = this.snapToValue;
      this.snapToValue = var1;
      this.firePropertyChange("snapToValue", var2, this.snapToValue);
   }

   public boolean getPaintTicks() {
      return this.paintTicks;
   }

   public void setPaintTicks(boolean var1) {
      boolean var2 = this.paintTicks;
      this.paintTicks = var1;
      this.firePropertyChange("paintTicks", var2, this.paintTicks);
      if (this.paintTicks != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public boolean getPaintTrack() {
      return this.paintTrack;
   }

   public void setPaintTrack(boolean var1) {
      boolean var2 = this.paintTrack;
      this.paintTrack = var1;
      this.firePropertyChange("paintTrack", var2, this.paintTrack);
      if (this.paintTrack != var2) {
         this.repaint();
      }

   }

   public boolean getPaintLabels() {
      return this.paintLabels;
   }

   public void setPaintLabels(boolean var1) {
      boolean var2 = this.paintLabels;
      this.paintLabels = var1;
      if (this.labelTable == null && this.getMajorTickSpacing() > 0) {
         this.setLabelTable(this.createStandardLabels(this.getMajorTickSpacing()));
      }

      this.firePropertyChange("paintLabels", var2, this.paintLabels);
      if (this.paintLabels != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("SliderUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.paintTicks ? "true" : "false";
      String var2 = this.paintTrack ? "true" : "false";
      String var3 = this.paintLabels ? "true" : "false";
      String var4 = this.isInverted ? "true" : "false";
      String var5 = this.snapToTicks ? "true" : "false";
      String var6 = this.snapToValue ? "true" : "false";
      String var7 = this.orientation == 0 ? "HORIZONTAL" : "VERTICAL";
      return super.paramString() + ",isInverted=" + var4 + ",majorTickSpacing=" + this.majorTickSpacing + ",minorTickSpacing=" + this.minorTickSpacing + ",orientation=" + var7 + ",paintLabels=" + var3 + ",paintTicks=" + var1 + ",paintTrack=" + var2 + ",snapToTicks=" + var5 + ",snapToValue=" + var6;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JSlider.AccessibleJSlider();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJSlider extends JComponent.AccessibleJComponent implements AccessibleValue {
      protected AccessibleJSlider() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JSlider.this.getValueIsAdjusting()) {
            var1.add(AccessibleState.BUSY);
         }

         if (JSlider.this.getOrientation() == 1) {
            var1.add(AccessibleState.VERTICAL);
         } else {
            var1.add(AccessibleState.HORIZONTAL);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SLIDER;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         return JSlider.this.getValue();
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 == null) {
            return false;
         } else {
            JSlider.this.setValue(var1.intValue());
            return true;
         }
      }

      public Number getMinimumAccessibleValue() {
         return JSlider.this.getMinimum();
      }

      public Number getMaximumAccessibleValue() {
         BoundedRangeModel var1 = JSlider.this.getModel();
         return var1.getMaximum() - var1.getExtent();
      }
   }

   private class ModelListener implements ChangeListener, Serializable {
      private ModelListener() {
      }

      public void stateChanged(ChangeEvent var1) {
         JSlider.this.fireStateChanged();
      }

      // $FF: synthetic method
      ModelListener(Object var2) {
         this();
      }
   }
}
