package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.plaf.SplitPaneUI;

public class JSplitPane extends JComponent implements Accessible {
   private static final String uiClassID = "SplitPaneUI";
   public static final int VERTICAL_SPLIT = 0;
   public static final int HORIZONTAL_SPLIT = 1;
   public static final String LEFT = "left";
   public static final String RIGHT = "right";
   public static final String TOP = "top";
   public static final String BOTTOM = "bottom";
   public static final String DIVIDER = "divider";
   public static final String ORIENTATION_PROPERTY = "orientation";
   public static final String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";
   public static final String DIVIDER_SIZE_PROPERTY = "dividerSize";
   public static final String ONE_TOUCH_EXPANDABLE_PROPERTY = "oneTouchExpandable";
   public static final String LAST_DIVIDER_LOCATION_PROPERTY = "lastDividerLocation";
   public static final String DIVIDER_LOCATION_PROPERTY = "dividerLocation";
   public static final String RESIZE_WEIGHT_PROPERTY = "resizeWeight";
   protected int orientation;
   protected boolean continuousLayout;
   protected Component leftComponent;
   protected Component rightComponent;
   protected int dividerSize;
   private boolean dividerSizeSet;
   protected boolean oneTouchExpandable;
   private boolean oneTouchExpandableSet;
   protected int lastDividerLocation;
   private double resizeWeight;
   private int dividerLocation;

   public JSplitPane() {
      this(1, UIManager.getBoolean("SplitPane.continuousLayout"), new JButton(UIManager.getString("SplitPane.leftButtonText")), new JButton(UIManager.getString("SplitPane.rightButtonText")));
   }

   @ConstructorProperties({"orientation"})
   public JSplitPane(int var1) {
      this(var1, UIManager.getBoolean("SplitPane.continuousLayout"));
   }

   public JSplitPane(int var1, boolean var2) {
      this(var1, var2, (Component)null, (Component)null);
   }

   public JSplitPane(int var1, Component var2, Component var3) {
      this(var1, UIManager.getBoolean("SplitPane.continuousLayout"), var2, var3);
   }

   public JSplitPane(int var1, boolean var2, Component var3, Component var4) {
      this.dividerSizeSet = false;
      this.dividerLocation = -1;
      this.setLayout((LayoutManager)null);
      this.setUIProperty("opaque", Boolean.TRUE);
      this.orientation = var1;
      if (this.orientation != 1 && this.orientation != 0) {
         throw new IllegalArgumentException("cannot create JSplitPane, orientation must be one of JSplitPane.HORIZONTAL_SPLIT or JSplitPane.VERTICAL_SPLIT");
      } else {
         this.continuousLayout = var2;
         if (var3 != null) {
            this.setLeftComponent(var3);
         }

         if (var4 != null) {
            this.setRightComponent(var4);
         }

         this.updateUI();
      }
   }

   public void setUI(SplitPaneUI var1) {
      if ((SplitPaneUI)this.ui != var1) {
         super.setUI(var1);
         this.revalidate();
      }

   }

   public SplitPaneUI getUI() {
      return (SplitPaneUI)this.ui;
   }

   public void updateUI() {
      this.setUI((SplitPaneUI)UIManager.getUI(this));
      this.revalidate();
   }

   public String getUIClassID() {
      return "SplitPaneUI";
   }

   public void setDividerSize(int var1) {
      int var2 = this.dividerSize;
      this.dividerSizeSet = true;
      if (var2 != var1) {
         this.dividerSize = var1;
         this.firePropertyChange("dividerSize", var2, var1);
      }

   }

   public int getDividerSize() {
      return this.dividerSize;
   }

   public void setLeftComponent(Component var1) {
      if (var1 == null) {
         if (this.leftComponent != null) {
            this.remove(this.leftComponent);
            this.leftComponent = null;
         }
      } else {
         this.add(var1, "left");
      }

   }

   public Component getLeftComponent() {
      return this.leftComponent;
   }

   public void setTopComponent(Component var1) {
      this.setLeftComponent(var1);
   }

   public Component getTopComponent() {
      return this.leftComponent;
   }

   public void setRightComponent(Component var1) {
      if (var1 == null) {
         if (this.rightComponent != null) {
            this.remove(this.rightComponent);
            this.rightComponent = null;
         }
      } else {
         this.add(var1, "right");
      }

   }

   public Component getRightComponent() {
      return this.rightComponent;
   }

   public void setBottomComponent(Component var1) {
      this.setRightComponent(var1);
   }

   public Component getBottomComponent() {
      return this.rightComponent;
   }

   public void setOneTouchExpandable(boolean var1) {
      boolean var2 = this.oneTouchExpandable;
      this.oneTouchExpandable = var1;
      this.oneTouchExpandableSet = true;
      this.firePropertyChange("oneTouchExpandable", var2, var1);
      this.repaint();
   }

   public boolean isOneTouchExpandable() {
      return this.oneTouchExpandable;
   }

   public void setLastDividerLocation(int var1) {
      int var2 = this.lastDividerLocation;
      this.lastDividerLocation = var1;
      this.firePropertyChange("lastDividerLocation", var2, var1);
   }

   public int getLastDividerLocation() {
      return this.lastDividerLocation;
   }

   public void setOrientation(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("JSplitPane: orientation must be one of JSplitPane.VERTICAL_SPLIT or JSplitPane.HORIZONTAL_SPLIT");
      } else {
         int var2 = this.orientation;
         this.orientation = var1;
         this.firePropertyChange("orientation", var2, var1);
      }
   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setContinuousLayout(boolean var1) {
      boolean var2 = this.continuousLayout;
      this.continuousLayout = var1;
      this.firePropertyChange("continuousLayout", var2, var1);
   }

   public boolean isContinuousLayout() {
      return this.continuousLayout;
   }

   public void setResizeWeight(double var1) {
      if (var1 >= 0.0D && var1 <= 1.0D) {
         double var3 = this.resizeWeight;
         this.resizeWeight = var1;
         this.firePropertyChange("resizeWeight", var3, var1);
      } else {
         throw new IllegalArgumentException("JSplitPane weight must be between 0 and 1");
      }
   }

   public double getResizeWeight() {
      return this.resizeWeight;
   }

   public void resetToPreferredSizes() {
      SplitPaneUI var1 = this.getUI();
      if (var1 != null) {
         var1.resetToPreferredSizes(this);
      }

   }

   public void setDividerLocation(double var1) {
      if (var1 >= 0.0D && var1 <= 1.0D) {
         if (this.getOrientation() == 0) {
            this.setDividerLocation((int)((double)(this.getHeight() - this.getDividerSize()) * var1));
         } else {
            this.setDividerLocation((int)((double)(this.getWidth() - this.getDividerSize()) * var1));
         }

      } else {
         throw new IllegalArgumentException("proportional location must be between 0.0 and 1.0.");
      }
   }

   public void setDividerLocation(int var1) {
      int var2 = this.dividerLocation;
      this.dividerLocation = var1;
      SplitPaneUI var3 = this.getUI();
      if (var3 != null) {
         var3.setDividerLocation(this, var1);
      }

      this.firePropertyChange("dividerLocation", var2, var1);
      this.setLastDividerLocation(var2);
   }

   public int getDividerLocation() {
      return this.dividerLocation;
   }

   public int getMinimumDividerLocation() {
      SplitPaneUI var1 = this.getUI();
      return var1 != null ? var1.getMinimumDividerLocation(this) : -1;
   }

   public int getMaximumDividerLocation() {
      SplitPaneUI var1 = this.getUI();
      return var1 != null ? var1.getMaximumDividerLocation(this) : -1;
   }

   public void remove(Component var1) {
      if (var1 == this.leftComponent) {
         this.leftComponent = null;
      } else if (var1 == this.rightComponent) {
         this.rightComponent = null;
      }

      super.remove(var1);
      this.revalidate();
      this.repaint();
   }

   public void remove(int var1) {
      Component var2 = this.getComponent(var1);
      if (var2 == this.leftComponent) {
         this.leftComponent = null;
      } else if (var2 == this.rightComponent) {
         this.rightComponent = null;
      }

      super.remove(var1);
      this.revalidate();
      this.repaint();
   }

   public void removeAll() {
      this.leftComponent = this.rightComponent = null;
      super.removeAll();
      this.revalidate();
      this.repaint();
   }

   public boolean isValidateRoot() {
      return true;
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      if (var2 != null && !(var2 instanceof String)) {
         throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
      } else {
         if (var2 == null) {
            if (this.getLeftComponent() == null) {
               var2 = "left";
            } else if (this.getRightComponent() == null) {
               var2 = "right";
            }
         }

         Component var4;
         if (var2 == null || !var2.equals("left") && !var2.equals("top")) {
            if (var2 != null && (var2.equals("right") || var2.equals("bottom"))) {
               var4 = this.getRightComponent();
               if (var4 != null) {
                  this.remove(var4);
               }

               this.rightComponent = var1;
               var3 = -1;
            } else if (var2 != null && var2.equals("divider")) {
               var3 = -1;
            }
         } else {
            var4 = this.getLeftComponent();
            if (var4 != null) {
               this.remove(var4);
            }

            this.leftComponent = var1;
            var3 = -1;
         }

         super.addImpl(var1, var2, var3);
         this.revalidate();
         this.repaint();
      }
   }

   protected void paintChildren(Graphics var1) {
      super.paintChildren(var1);
      SplitPaneUI var2 = this.getUI();
      if (var2 != null) {
         Graphics var3 = var1.create();
         var2.finishedPaintingChildren(this, var3);
         var3.dispose();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("SplitPaneUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   void setUIProperty(String var1, Object var2) {
      if (var1 == "dividerSize") {
         if (!this.dividerSizeSet) {
            this.setDividerSize(((Number)var2).intValue());
            this.dividerSizeSet = false;
         }
      } else if (var1 == "oneTouchExpandable") {
         if (!this.oneTouchExpandableSet) {
            this.setOneTouchExpandable((Boolean)var2);
            this.oneTouchExpandableSet = false;
         }
      } else {
         super.setUIProperty(var1, var2);
      }

   }

   protected String paramString() {
      String var1 = this.orientation == 1 ? "HORIZONTAL_SPLIT" : "VERTICAL_SPLIT";
      String var2 = this.continuousLayout ? "true" : "false";
      String var3 = this.oneTouchExpandable ? "true" : "false";
      return super.paramString() + ",continuousLayout=" + var2 + ",dividerSize=" + this.dividerSize + ",lastDividerLocation=" + this.lastDividerLocation + ",oneTouchExpandable=" + var3 + ",orientation=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JSplitPane.AccessibleJSplitPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJSplitPane extends JComponent.AccessibleJComponent implements AccessibleValue {
      protected AccessibleJSplitPane() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JSplitPane.this.getOrientation() == 0) {
            var1.add(AccessibleState.VERTICAL);
         } else {
            var1.add(AccessibleState.HORIZONTAL);
         }

         return var1;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         return JSplitPane.this.getDividerLocation();
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 == null) {
            return false;
         } else {
            JSplitPane.this.setDividerLocation(var1.intValue());
            return true;
         }
      }

      public Number getMinimumAccessibleValue() {
         return JSplitPane.this.getUI().getMinimumDividerLocation(JSplitPane.this);
      }

      public Number getMaximumAccessibleValue() {
         return JSplitPane.this.getUI().getMaximumDividerLocation(JSplitPane.this);
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SPLIT_PANE;
      }
   }
}
