package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.UIResource;

public class JToolBar extends JComponent implements SwingConstants, Accessible {
   private static final String uiClassID = "ToolBarUI";
   private boolean paintBorder;
   private Insets margin;
   private boolean floatable;
   private int orientation;

   public JToolBar() {
      this(0);
   }

   public JToolBar(int var1) {
      this((String)null, var1);
   }

   public JToolBar(String var1) {
      this(var1, 0);
   }

   public JToolBar(String var1, int var2) {
      this.paintBorder = true;
      this.margin = null;
      this.floatable = true;
      this.orientation = 0;
      this.setName(var1);
      this.checkOrientation(var2);
      this.orientation = var2;
      JToolBar.DefaultToolBarLayout var3 = new JToolBar.DefaultToolBarLayout(var2);
      this.setLayout(var3);
      this.addPropertyChangeListener(var3);
      this.updateUI();
   }

   public ToolBarUI getUI() {
      return (ToolBarUI)this.ui;
   }

   public void setUI(ToolBarUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ToolBarUI)UIManager.getUI(this));
      if (this.getLayout() == null) {
         this.setLayout(new JToolBar.DefaultToolBarLayout(this.getOrientation()));
      }

      this.invalidate();
   }

   public String getUIClassID() {
      return "ToolBarUI";
   }

   public int getComponentIndex(Component var1) {
      int var2 = this.getComponentCount();
      Component[] var3 = this.getComponents();

      for(int var4 = 0; var4 < var2; ++var4) {
         Component var5 = var3[var4];
         if (var5 == var1) {
            return var4;
         }
      }

      return -1;
   }

   public Component getComponentAtIndex(int var1) {
      int var2 = this.getComponentCount();
      if (var1 >= 0 && var1 < var2) {
         Component[] var3 = this.getComponents();
         return var3[var1];
      } else {
         return null;
      }
   }

   public void setMargin(Insets var1) {
      Insets var2 = this.margin;
      this.margin = var1;
      this.firePropertyChange("margin", var2, var1);
      this.revalidate();
      this.repaint();
   }

   public Insets getMargin() {
      return this.margin == null ? new Insets(0, 0, 0, 0) : this.margin;
   }

   public boolean isBorderPainted() {
      return this.paintBorder;
   }

   public void setBorderPainted(boolean var1) {
      if (this.paintBorder != var1) {
         boolean var2 = this.paintBorder;
         this.paintBorder = var1;
         this.firePropertyChange("borderPainted", var2, var1);
         this.revalidate();
         this.repaint();
      }

   }

   protected void paintBorder(Graphics var1) {
      if (this.isBorderPainted()) {
         super.paintBorder(var1);
      }

   }

   public boolean isFloatable() {
      return this.floatable;
   }

   public void setFloatable(boolean var1) {
      if (this.floatable != var1) {
         boolean var2 = this.floatable;
         this.floatable = var1;
         this.firePropertyChange("floatable", var2, var1);
         this.revalidate();
         this.repaint();
      }

   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setOrientation(int var1) {
      this.checkOrientation(var1);
      if (this.orientation != var1) {
         int var2 = this.orientation;
         this.orientation = var1;
         this.firePropertyChange("orientation", var2, var1);
         this.revalidate();
         this.repaint();
      }

   }

   public void setRollover(boolean var1) {
      this.putClientProperty("JToolBar.isRollover", var1 ? Boolean.TRUE : Boolean.FALSE);
   }

   public boolean isRollover() {
      Boolean var1 = (Boolean)this.getClientProperty("JToolBar.isRollover");
      return var1 != null ? var1 : false;
   }

   private void checkOrientation(int var1) {
      switch(var1) {
      case 0:
      case 1:
         return;
      default:
         throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
      }
   }

   public void addSeparator() {
      this.addSeparator((Dimension)null);
   }

   public void addSeparator(Dimension var1) {
      JToolBar.Separator var2 = new JToolBar.Separator(var1);
      this.add(var2);
   }

   public JButton add(Action var1) {
      JButton var2 = this.createActionComponent(var1);
      var2.setAction(var1);
      this.add(var2);
      return var2;
   }

   protected JButton createActionComponent(Action var1) {
      JButton var2 = new JButton() {
         protected PropertyChangeListener createActionPropertyChangeListener(Action var1) {
            PropertyChangeListener var2 = JToolBar.this.createActionChangeListener(this);
            if (var2 == null) {
               var2 = super.createActionPropertyChangeListener(var1);
            }

            return var2;
         }
      };
      if (var1 != null && (var1.getValue("SmallIcon") != null || var1.getValue("SwingLargeIconKey") != null)) {
         var2.setHideActionText(true);
      }

      var2.setHorizontalTextPosition(0);
      var2.setVerticalTextPosition(3);
      return var2;
   }

   protected PropertyChangeListener createActionChangeListener(JButton var1) {
      return null;
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      if (var1 instanceof JToolBar.Separator) {
         if (this.getOrientation() == 1) {
            ((JToolBar.Separator)var1).setOrientation(0);
         } else {
            ((JToolBar.Separator)var1).setOrientation(1);
         }
      }

      super.addImpl(var1, var2, var3);
      if (var1 instanceof JButton) {
         ((JButton)var1).setDefaultCapable(false);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ToolBarUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.paintBorder ? "true" : "false";
      String var2 = this.margin != null ? this.margin.toString() : "";
      String var3 = this.floatable ? "true" : "false";
      String var4 = this.orientation == 0 ? "HORIZONTAL" : "VERTICAL";
      return super.paramString() + ",floatable=" + var3 + ",margin=" + var2 + ",orientation=" + var4 + ",paintBorder=" + var1;
   }

   public void setLayout(LayoutManager var1) {
      LayoutManager var2 = this.getLayout();
      if (var2 instanceof PropertyChangeListener) {
         this.removePropertyChangeListener((PropertyChangeListener)var2);
      }

      super.setLayout(var1);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JToolBar.AccessibleJToolBar();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJToolBar extends JComponent.AccessibleJComponent {
      protected AccessibleJToolBar() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TOOL_BAR;
      }
   }

   private class DefaultToolBarLayout implements LayoutManager2, Serializable, PropertyChangeListener, UIResource {
      BoxLayout lm;

      DefaultToolBarLayout(int var2) {
         if (var2 == 1) {
            this.lm = new BoxLayout(JToolBar.this, 3);
         } else {
            this.lm = new BoxLayout(JToolBar.this, 2);
         }

      }

      public void addLayoutComponent(String var1, Component var2) {
         this.lm.addLayoutComponent(var1, var2);
      }

      public void addLayoutComponent(Component var1, Object var2) {
         this.lm.addLayoutComponent(var1, var2);
      }

      public void removeLayoutComponent(Component var1) {
         this.lm.removeLayoutComponent(var1);
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.lm.preferredLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         return this.lm.minimumLayoutSize(var1);
      }

      public Dimension maximumLayoutSize(Container var1) {
         return this.lm.maximumLayoutSize(var1);
      }

      public void layoutContainer(Container var1) {
         this.lm.layoutContainer(var1);
      }

      public float getLayoutAlignmentX(Container var1) {
         return this.lm.getLayoutAlignmentX(var1);
      }

      public float getLayoutAlignmentY(Container var1) {
         return this.lm.getLayoutAlignmentY(var1);
      }

      public void invalidateLayout(Container var1) {
         this.lm.invalidateLayout(var1);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("orientation")) {
            int var3 = (Integer)var1.getNewValue();
            if (var3 == 1) {
               this.lm = new BoxLayout(JToolBar.this, 3);
            } else {
               this.lm = new BoxLayout(JToolBar.this, 2);
            }
         }

      }
   }

   public static class Separator extends JSeparator {
      private Dimension separatorSize;

      public Separator() {
         this((Dimension)null);
      }

      public Separator(Dimension var1) {
         super(0);
         this.setSeparatorSize(var1);
      }

      public String getUIClassID() {
         return "ToolBarSeparatorUI";
      }

      public void setSeparatorSize(Dimension var1) {
         if (var1 != null) {
            this.separatorSize = var1;
         } else {
            super.updateUI();
         }

         this.invalidate();
      }

      public Dimension getSeparatorSize() {
         return this.separatorSize;
      }

      public Dimension getMinimumSize() {
         return this.separatorSize != null ? this.separatorSize.getSize() : super.getMinimumSize();
      }

      public Dimension getMaximumSize() {
         return this.separatorSize != null ? this.separatorSize.getSize() : super.getMaximumSize();
      }

      public Dimension getPreferredSize() {
         return this.separatorSize != null ? this.separatorSize.getSize() : super.getPreferredSize();
      }
   }
}
