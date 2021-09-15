package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuUI;
import sun.swing.MenuItemLayoutHelper;

public class SynthMenuUI extends BasicMenuUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private SynthStyle accStyle;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthMenuUI();
   }

   protected void installDefaults() {
      this.updateStyle(this.menuItem);
   }

   protected void installListeners() {
      super.installListeners();
      this.menuItem.addPropertyChangeListener(this);
   }

   private void updateStyle(JMenuItem var1) {
      SynthStyle var2 = this.style;
      SynthContext var3 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var3, this);
      if (var2 != this.style) {
         String var4 = this.getPropertyPrefix();
         this.defaultTextIconGap = this.style.getInt(var3, var4 + ".textIconGap", 4);
         if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof UIResource) {
            Insets var5 = (Insets)this.style.get(var3, var4 + ".margin");
            if (var5 == null) {
               var5 = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
            }

            this.menuItem.setMargin(var5);
         }

         this.acceleratorDelimiter = this.style.getString(var3, var4 + ".acceleratorDelimiter", "+");
         if (MenuItemLayoutHelper.useCheckAndArrow(this.menuItem)) {
            this.checkIcon = this.style.getIcon(var3, var4 + ".checkIcon");
            this.arrowIcon = this.style.getIcon(var3, var4 + ".arrowIcon");
         } else {
            this.checkIcon = null;
            this.arrowIcon = null;
         }

         ((JMenu)this.menuItem).setDelay(this.style.getInt(var3, var4 + ".delay", 200));
         if (var2 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var3.dispose();
      SynthContext var6 = this.getContext(var1, Region.MENU_ITEM_ACCELERATOR, 1);
      this.accStyle = SynthLookAndFeel.updateStyle(var6, this);
      var6.dispose();
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      JComponent var2 = MenuItemLayoutHelper.getMenuItemParent((JMenuItem)var1);
      if (var2 != null) {
         var2.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, (Object)null);
      }

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
      if (!var1.isEnabled()) {
         return 8;
      } else {
         int var2;
         if (this.menuItem.isArmed()) {
            var2 = 2;
         } else {
            var2 = SynthLookAndFeel.getComponentState(var1);
         }

         if (this.menuItem.isSelected()) {
            var2 |= 512;
         }

         return var2;
      }
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
      var3.getPainter().paintMenuBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
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

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintMenuBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1) || var1.getPropertyName().equals("ancestor") && UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus")) {
         this.updateStyle((JMenu)var1.getSource());
      }

   }
}
