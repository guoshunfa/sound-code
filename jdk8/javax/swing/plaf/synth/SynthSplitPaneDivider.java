package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import sun.swing.DefaultLookup;

class SynthSplitPaneDivider extends BasicSplitPaneDivider {
   public SynthSplitPaneDivider(BasicSplitPaneUI var1) {
      super(var1);
   }

   protected void setMouseOver(boolean var1) {
      if (this.isMouseOver() != var1) {
         this.repaint();
      }

      super.setMouseOver(var1);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      if (var1.getSource() == this.splitPane && var1.getPropertyName() == "orientation") {
         if (this.leftButton instanceof SynthArrowButton) {
            ((SynthArrowButton)this.leftButton).setDirection(this.mapDirection(true));
         }

         if (this.rightButton instanceof SynthArrowButton) {
            ((SynthArrowButton)this.rightButton).setDirection(this.mapDirection(false));
         }
      }

   }

   public void paint(Graphics var1) {
      Graphics var2 = var1.create();
      SynthContext var3 = ((SynthSplitPaneUI)this.splitPaneUI).getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER);
      Rectangle var4 = this.getBounds();
      var4.x = var4.y = 0;
      SynthLookAndFeel.updateSubregion(var3, var1, var4);
      var3.getPainter().paintSplitPaneDividerBackground(var3, var1, 0, 0, var4.width, var4.height, this.splitPane.getOrientation());
      Object var5 = null;
      var3.getPainter().paintSplitPaneDividerForeground(var3, var1, 0, 0, this.getWidth(), this.getHeight(), this.splitPane.getOrientation());
      var3.dispose();

      for(int var6 = 0; var6 < this.getComponentCount(); ++var6) {
         Component var7 = this.getComponent(var6);
         Rectangle var8 = var7.getBounds();
         Graphics var9 = var1.create(var8.x, var8.y, var8.width, var8.height);
         var7.paint(var9);
         var9.dispose();
      }

      var2.dispose();
   }

   private int mapDirection(boolean var1) {
      if (var1) {
         return this.splitPane.getOrientation() == 1 ? 7 : 1;
      } else {
         return this.splitPane.getOrientation() == 1 ? 3 : 5;
      }
   }

   protected JButton createLeftOneTouchButton() {
      SynthArrowButton var1 = new SynthArrowButton(1);
      int var2 = this.lookupOneTouchSize();
      var1.setName("SplitPaneDivider.leftOneTouchButton");
      var1.setMinimumSize(new Dimension(var2, var2));
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setBorderPainted(false);
      var1.setRequestFocusEnabled(false);
      var1.setDirection(this.mapDirection(true));
      return var1;
   }

   private int lookupOneTouchSize() {
      return DefaultLookup.getInt(this.splitPaneUI.getSplitPane(), this.splitPaneUI, "SplitPaneDivider.oneTouchButtonSize", 6);
   }

   protected JButton createRightOneTouchButton() {
      SynthArrowButton var1 = new SynthArrowButton(1);
      int var2 = this.lookupOneTouchSize();
      var1.setName("SplitPaneDivider.rightOneTouchButton");
      var1.setMinimumSize(new Dimension(var2, var2));
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setBorderPainted(false);
      var1.setRequestFocusEnabled(false);
      var1.setDirection(this.mapDirection(false));
      return var1;
   }
}
