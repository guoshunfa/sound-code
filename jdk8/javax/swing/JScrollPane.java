package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.UIResource;

public class JScrollPane extends JComponent implements ScrollPaneConstants, Accessible {
   private Border viewportBorder;
   private static final String uiClassID = "ScrollPaneUI";
   protected int verticalScrollBarPolicy;
   protected int horizontalScrollBarPolicy;
   protected JViewport viewport;
   protected JScrollBar verticalScrollBar;
   protected JScrollBar horizontalScrollBar;
   protected JViewport rowHeader;
   protected JViewport columnHeader;
   protected Component lowerLeft;
   protected Component lowerRight;
   protected Component upperLeft;
   protected Component upperRight;
   private boolean wheelScrollState;

   public JScrollPane(Component var1, int var2, int var3) {
      this.verticalScrollBarPolicy = 20;
      this.horizontalScrollBarPolicy = 30;
      this.wheelScrollState = true;
      this.setLayout(new ScrollPaneLayout.UIResource());
      this.setVerticalScrollBarPolicy(var2);
      this.setHorizontalScrollBarPolicy(var3);
      this.setViewport(this.createViewport());
      this.setVerticalScrollBar(this.createVerticalScrollBar());
      this.setHorizontalScrollBar(this.createHorizontalScrollBar());
      if (var1 != null) {
         this.setViewportView(var1);
      }

      this.setUIProperty("opaque", true);
      this.updateUI();
      if (!this.getComponentOrientation().isLeftToRight()) {
         this.viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
      }

   }

   public JScrollPane(Component var1) {
      this(var1, 20, 30);
   }

   public JScrollPane(int var1, int var2) {
      this((Component)null, var1, var2);
   }

   public JScrollPane() {
      this((Component)null, 20, 30);
   }

   public ScrollPaneUI getUI() {
      return (ScrollPaneUI)this.ui;
   }

   public void setUI(ScrollPaneUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ScrollPaneUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ScrollPaneUI";
   }

   public void setLayout(LayoutManager var1) {
      if (var1 instanceof ScrollPaneLayout) {
         super.setLayout(var1);
         ((ScrollPaneLayout)var1).syncWithScrollPane(this);
      } else {
         if (var1 != null) {
            String var2 = "layout of JScrollPane must be a ScrollPaneLayout";
            throw new ClassCastException(var2);
         }

         super.setLayout(var1);
      }

   }

   public boolean isValidateRoot() {
      return true;
   }

   public int getVerticalScrollBarPolicy() {
      return this.verticalScrollBarPolicy;
   }

   public void setVerticalScrollBarPolicy(int var1) {
      switch(var1) {
      case 20:
      case 21:
      case 22:
         int var2 = this.verticalScrollBarPolicy;
         this.verticalScrollBarPolicy = var1;
         this.firePropertyChange("verticalScrollBarPolicy", var2, var1);
         this.revalidate();
         this.repaint();
         return;
      default:
         throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
      }
   }

   public int getHorizontalScrollBarPolicy() {
      return this.horizontalScrollBarPolicy;
   }

   public void setHorizontalScrollBarPolicy(int var1) {
      switch(var1) {
      case 30:
      case 31:
      case 32:
         int var2 = this.horizontalScrollBarPolicy;
         this.horizontalScrollBarPolicy = var1;
         this.firePropertyChange("horizontalScrollBarPolicy", var2, var1);
         this.revalidate();
         this.repaint();
         return;
      default:
         throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
      }
   }

   public Border getViewportBorder() {
      return this.viewportBorder;
   }

   public void setViewportBorder(Border var1) {
      Border var2 = this.viewportBorder;
      this.viewportBorder = var1;
      this.firePropertyChange("viewportBorder", var2, var1);
   }

   public Rectangle getViewportBorderBounds() {
      Rectangle var1 = new Rectangle(this.getSize());
      Insets var2 = this.getInsets();
      var1.x = var2.left;
      var1.y = var2.top;
      var1.width -= var2.left + var2.right;
      var1.height -= var2.top + var2.bottom;
      boolean var3 = SwingUtilities.isLeftToRight(this);
      JViewport var4 = this.getColumnHeader();
      if (var4 != null && var4.isVisible()) {
         int var5 = var4.getHeight();
         var1.y += var5;
         var1.height -= var5;
      }

      JViewport var8 = this.getRowHeader();
      if (var8 != null && var8.isVisible()) {
         int var6 = var8.getWidth();
         if (var3) {
            var1.x += var6;
         }

         var1.width -= var6;
      }

      JScrollBar var9 = this.getVerticalScrollBar();
      if (var9 != null && var9.isVisible()) {
         int var7 = var9.getWidth();
         if (!var3) {
            var1.x += var7;
         }

         var1.width -= var7;
      }

      JScrollBar var10 = this.getHorizontalScrollBar();
      if (var10 != null && var10.isVisible()) {
         var1.height -= var10.getHeight();
      }

      return var1;
   }

   public JScrollBar createHorizontalScrollBar() {
      return new JScrollPane.ScrollBar(0);
   }

   @Transient
   public JScrollBar getHorizontalScrollBar() {
      return this.horizontalScrollBar;
   }

   public void setHorizontalScrollBar(JScrollBar var1) {
      JScrollBar var2 = this.getHorizontalScrollBar();
      this.horizontalScrollBar = var1;
      if (var1 != null) {
         this.add(var1, "HORIZONTAL_SCROLLBAR");
      } else if (var2 != null) {
         this.remove(var2);
      }

      this.firePropertyChange("horizontalScrollBar", var2, var1);
      this.revalidate();
      this.repaint();
   }

   public JScrollBar createVerticalScrollBar() {
      return new JScrollPane.ScrollBar(1);
   }

   @Transient
   public JScrollBar getVerticalScrollBar() {
      return this.verticalScrollBar;
   }

   public void setVerticalScrollBar(JScrollBar var1) {
      JScrollBar var2 = this.getVerticalScrollBar();
      this.verticalScrollBar = var1;
      this.add(var1, "VERTICAL_SCROLLBAR");
      this.firePropertyChange("verticalScrollBar", var2, var1);
      this.revalidate();
      this.repaint();
   }

   protected JViewport createViewport() {
      return new JViewport();
   }

   public JViewport getViewport() {
      return this.viewport;
   }

   public void setViewport(JViewport var1) {
      JViewport var2 = this.getViewport();
      this.viewport = var1;
      if (var1 != null) {
         this.add(var1, "VIEWPORT");
      } else if (var2 != null) {
         this.remove(var2);
      }

      this.firePropertyChange("viewport", var2, var1);
      if (this.accessibleContext != null) {
         ((JScrollPane.AccessibleJScrollPane)this.accessibleContext).resetViewPort();
      }

      this.revalidate();
      this.repaint();
   }

   public void setViewportView(Component var1) {
      if (this.getViewport() == null) {
         this.setViewport(this.createViewport());
      }

      this.getViewport().setView(var1);
   }

   @Transient
   public JViewport getRowHeader() {
      return this.rowHeader;
   }

   public void setRowHeader(JViewport var1) {
      JViewport var2 = this.getRowHeader();
      this.rowHeader = var1;
      if (var1 != null) {
         this.add(var1, "ROW_HEADER");
      } else if (var2 != null) {
         this.remove(var2);
      }

      this.firePropertyChange("rowHeader", var2, var1);
      this.revalidate();
      this.repaint();
   }

   public void setRowHeaderView(Component var1) {
      if (this.getRowHeader() == null) {
         this.setRowHeader(this.createViewport());
      }

      this.getRowHeader().setView(var1);
   }

   @Transient
   public JViewport getColumnHeader() {
      return this.columnHeader;
   }

   public void setColumnHeader(JViewport var1) {
      JViewport var2 = this.getColumnHeader();
      this.columnHeader = var1;
      if (var1 != null) {
         this.add(var1, "COLUMN_HEADER");
      } else if (var2 != null) {
         this.remove(var2);
      }

      this.firePropertyChange("columnHeader", var2, var1);
      this.revalidate();
      this.repaint();
   }

   public void setColumnHeaderView(Component var1) {
      if (this.getColumnHeader() == null) {
         this.setColumnHeader(this.createViewport());
      }

      this.getColumnHeader().setView(var1);
   }

   public Component getCorner(String var1) {
      boolean var2 = this.getComponentOrientation().isLeftToRight();
      if (var1.equals("LOWER_LEADING_CORNER")) {
         var1 = var2 ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
      } else if (var1.equals("LOWER_TRAILING_CORNER")) {
         var1 = var2 ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
      } else if (var1.equals("UPPER_LEADING_CORNER")) {
         var1 = var2 ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
      } else if (var1.equals("UPPER_TRAILING_CORNER")) {
         var1 = var2 ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
      }

      if (var1.equals("LOWER_LEFT_CORNER")) {
         return this.lowerLeft;
      } else if (var1.equals("LOWER_RIGHT_CORNER")) {
         return this.lowerRight;
      } else if (var1.equals("UPPER_LEFT_CORNER")) {
         return this.upperLeft;
      } else {
         return var1.equals("UPPER_RIGHT_CORNER") ? this.upperRight : null;
      }
   }

   public void setCorner(String var1, Component var2) {
      boolean var4 = this.getComponentOrientation().isLeftToRight();
      if (var1.equals("LOWER_LEADING_CORNER")) {
         var1 = var4 ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
      } else if (var1.equals("LOWER_TRAILING_CORNER")) {
         var1 = var4 ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
      } else if (var1.equals("UPPER_LEADING_CORNER")) {
         var1 = var4 ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
      } else if (var1.equals("UPPER_TRAILING_CORNER")) {
         var1 = var4 ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
      }

      Component var3;
      if (var1.equals("LOWER_LEFT_CORNER")) {
         var3 = this.lowerLeft;
         this.lowerLeft = var2;
      } else if (var1.equals("LOWER_RIGHT_CORNER")) {
         var3 = this.lowerRight;
         this.lowerRight = var2;
      } else if (var1.equals("UPPER_LEFT_CORNER")) {
         var3 = this.upperLeft;
         this.upperLeft = var2;
      } else {
         if (!var1.equals("UPPER_RIGHT_CORNER")) {
            throw new IllegalArgumentException("invalid corner key");
         }

         var3 = this.upperRight;
         this.upperRight = var2;
      }

      if (var3 != null) {
         this.remove(var3);
      }

      if (var2 != null) {
         this.add(var2, var1);
      }

      this.firePropertyChange(var1, var3, var2);
      this.revalidate();
      this.repaint();
   }

   public void setComponentOrientation(ComponentOrientation var1) {
      super.setComponentOrientation(var1);
      if (this.verticalScrollBar != null) {
         this.verticalScrollBar.setComponentOrientation(var1);
      }

      if (this.horizontalScrollBar != null) {
         this.horizontalScrollBar.setComponentOrientation(var1);
      }

   }

   public boolean isWheelScrollingEnabled() {
      return this.wheelScrollState;
   }

   public void setWheelScrollingEnabled(boolean var1) {
      boolean var2 = this.wheelScrollState;
      this.wheelScrollState = var1;
      this.firePropertyChange("wheelScrollingEnabled", var2, var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ScrollPaneUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.viewportBorder != null ? this.viewportBorder.toString() : "";
      String var2 = this.viewport != null ? this.viewport.toString() : "";
      String var3;
      if (this.verticalScrollBarPolicy == 20) {
         var3 = "VERTICAL_SCROLLBAR_AS_NEEDED";
      } else if (this.verticalScrollBarPolicy == 21) {
         var3 = "VERTICAL_SCROLLBAR_NEVER";
      } else if (this.verticalScrollBarPolicy == 22) {
         var3 = "VERTICAL_SCROLLBAR_ALWAYS";
      } else {
         var3 = "";
      }

      String var4;
      if (this.horizontalScrollBarPolicy == 30) {
         var4 = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
      } else if (this.horizontalScrollBarPolicy == 31) {
         var4 = "HORIZONTAL_SCROLLBAR_NEVER";
      } else if (this.horizontalScrollBarPolicy == 32) {
         var4 = "HORIZONTAL_SCROLLBAR_ALWAYS";
      } else {
         var4 = "";
      }

      String var5 = this.horizontalScrollBar != null ? this.horizontalScrollBar.toString() : "";
      String var6 = this.verticalScrollBar != null ? this.verticalScrollBar.toString() : "";
      String var7 = this.columnHeader != null ? this.columnHeader.toString() : "";
      String var8 = this.rowHeader != null ? this.rowHeader.toString() : "";
      String var9 = this.lowerLeft != null ? this.lowerLeft.toString() : "";
      String var10 = this.lowerRight != null ? this.lowerRight.toString() : "";
      String var11 = this.upperLeft != null ? this.upperLeft.toString() : "";
      String var12 = this.upperRight != null ? this.upperRight.toString() : "";
      return super.paramString() + ",columnHeader=" + var7 + ",horizontalScrollBar=" + var5 + ",horizontalScrollBarPolicy=" + var4 + ",lowerLeft=" + var9 + ",lowerRight=" + var10 + ",rowHeader=" + var8 + ",upperLeft=" + var11 + ",upperRight=" + var12 + ",verticalScrollBar=" + var6 + ",verticalScrollBarPolicy=" + var3 + ",viewport=" + var2 + ",viewportBorder=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JScrollPane.AccessibleJScrollPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJScrollPane extends JComponent.AccessibleJComponent implements ChangeListener, PropertyChangeListener {
      protected JViewport viewPort = null;

      public void resetViewPort() {
         if (this.viewPort != null) {
            this.viewPort.removeChangeListener(this);
            this.viewPort.removePropertyChangeListener(this);
         }

         this.viewPort = JScrollPane.this.getViewport();
         if (this.viewPort != null) {
            this.viewPort.addChangeListener(this);
            this.viewPort.addPropertyChangeListener(this);
         }

      }

      public AccessibleJScrollPane() {
         super();
         this.resetViewPort();
         JScrollBar var2 = JScrollPane.this.getHorizontalScrollBar();
         if (var2 != null) {
            this.setScrollBarRelations(var2);
         }

         var2 = JScrollPane.this.getVerticalScrollBar();
         if (var2 != null) {
            this.setScrollBarRelations(var2);
         }

      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SCROLL_PANE;
      }

      public void stateChanged(ChangeEvent var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.firePropertyChange("AccessibleVisibleData", false, true);
         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ((var2 == "horizontalScrollBar" || var2 == "verticalScrollBar") && var1.getNewValue() instanceof JScrollBar) {
            this.setScrollBarRelations((JScrollBar)var1.getNewValue());
         }

      }

      void setScrollBarRelations(JScrollBar var1) {
         AccessibleRelation var2 = new AccessibleRelation(AccessibleRelation.CONTROLLED_BY, var1);
         AccessibleRelation var3 = new AccessibleRelation(AccessibleRelation.CONTROLLER_FOR, JScrollPane.this);
         AccessibleContext var4 = var1.getAccessibleContext();
         var4.getAccessibleRelationSet().add(var3);
         this.getAccessibleRelationSet().add(var2);
      }
   }

   protected class ScrollBar extends JScrollBar implements UIResource {
      private boolean unitIncrementSet;
      private boolean blockIncrementSet;

      public ScrollBar(int var2) {
         super(var2);
         this.putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
      }

      public void setUnitIncrement(int var1) {
         this.unitIncrementSet = true;
         this.putClientProperty("JScrollBar.fastWheelScrolling", (Object)null);
         super.setUnitIncrement(var1);
      }

      public int getUnitIncrement(int var1) {
         JViewport var2 = JScrollPane.this.getViewport();
         if (!this.unitIncrementSet && var2 != null && var2.getView() instanceof Scrollable) {
            Scrollable var3 = (Scrollable)((Scrollable)var2.getView());
            Rectangle var4 = var2.getViewRect();
            return var3.getScrollableUnitIncrement(var4, this.getOrientation(), var1);
         } else {
            return super.getUnitIncrement(var1);
         }
      }

      public void setBlockIncrement(int var1) {
         this.blockIncrementSet = true;
         this.putClientProperty("JScrollBar.fastWheelScrolling", (Object)null);
         super.setBlockIncrement(var1);
      }

      public int getBlockIncrement(int var1) {
         JViewport var2 = JScrollPane.this.getViewport();
         if (!this.blockIncrementSet && var2 != null) {
            if (var2.getView() instanceof Scrollable) {
               Scrollable var3 = (Scrollable)((Scrollable)var2.getView());
               Rectangle var4 = var2.getViewRect();
               return var3.getScrollableBlockIncrement(var4, this.getOrientation(), var1);
            } else {
               return this.getOrientation() == 1 ? var2.getExtentSize().height : var2.getExtentSize().width;
            }
         } else {
            return super.getBlockIncrement(var1);
         }
      }
   }
}
