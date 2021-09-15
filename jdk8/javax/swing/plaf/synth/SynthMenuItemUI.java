package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuItemUI;
import sun.swing.MenuItemLayoutHelper;

public class SynthMenuItemUI extends BasicMenuItemUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private SynthStyle accStyle;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthMenuItemUI();
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      JComponent var2 = MenuItemLayoutHelper.getMenuItemParent((JMenuItem)var1);
      if (var2 != null) {
         var2.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, (Object)null);
      }

   }

   protected void installDefaults() {
      this.updateStyle(this.menuItem);
   }

   protected void installListeners() {
      super.installListeners();
      this.menuItem.addPropertyChangeListener(this);
   }

   private void updateStyle(JMenuItem var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (var3 != this.style) {
         String var4 = this.getPropertyPrefix();
         Object var5 = this.style.get(var2, var4 + ".textIconGap");
         if (var5 != null) {
            LookAndFeel.installProperty(var1, "iconTextGap", var5);
         }

         this.defaultTextIconGap = var1.getIconTextGap();
         if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof UIResource) {
            Insets var6 = (Insets)this.style.get(var2, var4 + ".margin");
            if (var6 == null) {
               var6 = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
            }

            this.menuItem.setMargin(var6);
         }

         this.acceleratorDelimiter = this.style.getString(var2, var4 + ".acceleratorDelimiter", "+");
         this.arrowIcon = this.style.getIcon(var2, var4 + ".arrowIcon");
         this.checkIcon = this.style.getIcon(var2, var4 + ".checkIcon");
         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
      SynthContext var7 = this.getContext(var1, Region.MENU_ITEM_ACCELERATOR, 1);
      this.accStyle = SynthLookAndFeel.updateStyle(var7, this);
      var7.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.menuItem, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      SynthContext var2 = this.getContext(this.menuItem, Region.MENU_ITEM_ACCELERATOR, 1);
      this.accStyle.uninstallDefaults(var2);
      var2.dispose();
      this.accStyle = null;
      super.uninstallDefaults();
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.menuItem.removePropertyChangeListener(this);
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   SynthContext getContext(JComponent var1, Region var2) {
      return this.getContext(var1, var2, this.getComponentState(var1, var2));
   }

   private SynthContext getContext(JComponent var1, Region var2, int var3) {
      return SynthContext.getContext(var1, var2, this.accStyle, var3);
   }

   private int getComponentState(JComponent var1) {
      int var2;
      if (!var1.isEnabled()) {
         var2 = 8;
      } else if (this.menuItem.isArmed()) {
         var2 = 2;
      } else {
         var2 = SynthLookAndFeel.getComponentState(var1);
      }

      if (this.menuItem.isSelected()) {
         var2 |= 512;
      }

      return var2;
   }

   private int getComponentState(JComponent var1, Region var2) {
      return this.getComponentState(var1);
   }

   protected Dimension getPreferredMenuItemSize(JComponent var1, Icon var2, Icon var3, int var4) {
      SynthContext var5 = this.getContext(var1);
      SynthContext var6 = this.getContext(var1, Region.MENU_ITEM_ACCELERATOR);
      Dimension var7 = SynthGraphicsUtils.getPreferredMenuItemSize(var5, var6, var1, var2, var3, var4, this.acceleratorDelimiter, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), this.getPropertyPrefix());
      var5.dispose();
      var6.dispose();
      return var7;
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      this.paintBackground(var3, var1, var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      SynthContext var3 = this.getContext(this.menuItem, Region.MENU_ITEM_ACCELERATOR);
      String var4 = this.getPropertyPrefix();
      Icon var5 = this.style.getIcon(var1, var4 + ".checkIcon");
      Icon var6 = this.style.getIcon(var1, var4 + ".arrowIcon");
      SynthGraphicsUtils.paint(var1, var3, var2, var5, var6, this.acceleratorDelimiter, this.defaultTextIconGap, this.getPropertyPrefix());
      var3.dispose();
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      SynthGraphicsUtils.paintBackground(var1, var2, var3);
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintMenuItemBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JMenuItem)var1.getSource());
      }

   }
}
