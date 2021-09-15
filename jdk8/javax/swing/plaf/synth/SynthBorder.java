package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

class SynthBorder extends AbstractBorder implements UIResource {
   private SynthUI ui;
   private Insets insets;

   SynthBorder(SynthUI var1, Insets var2) {
      this.ui = var1;
      this.insets = var2;
   }

   SynthBorder(SynthUI var1) {
      this(var1, (Insets)null);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JComponent var7 = (JComponent)var1;
      SynthContext var8 = this.ui.getContext(var7);
      SynthStyle var9 = var8.getStyle();
      if (var9 == null) {
         assert false : "SynthBorder is being used outside after the UI has been uninstalled";

      } else {
         this.ui.paintBorder(var8, var2, var3, var4, var5, var6);
         var8.dispose();
      }
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      if (this.insets != null) {
         if (var2 == null) {
            var2 = new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
         } else {
            var2.top = this.insets.top;
            var2.bottom = this.insets.bottom;
            var2.left = this.insets.left;
            var2.right = this.insets.right;
         }
      } else if (var2 == null) {
         var2 = new Insets(0, 0, 0, 0);
      } else {
         var2.top = var2.bottom = var2.left = var2.right = 0;
      }

      if (var1 instanceof JComponent) {
         Region var3 = Region.getRegion((JComponent)var1);
         Insets var4 = null;
         if ((var3 == Region.ARROW_BUTTON || var3 == Region.BUTTON || var3 == Region.CHECK_BOX || var3 == Region.CHECK_BOX_MENU_ITEM || var3 == Region.MENU || var3 == Region.MENU_ITEM || var3 == Region.RADIO_BUTTON || var3 == Region.RADIO_BUTTON_MENU_ITEM || var3 == Region.TOGGLE_BUTTON) && var1 instanceof AbstractButton) {
            var4 = ((AbstractButton)var1).getMargin();
         } else if ((var3 == Region.EDITOR_PANE || var3 == Region.FORMATTED_TEXT_FIELD || var3 == Region.PASSWORD_FIELD || var3 == Region.TEXT_AREA || var3 == Region.TEXT_FIELD || var3 == Region.TEXT_PANE) && var1 instanceof JTextComponent) {
            var4 = ((JTextComponent)var1).getMargin();
         } else if (var3 == Region.TOOL_BAR && var1 instanceof JToolBar) {
            var4 = ((JToolBar)var1).getMargin();
         } else if (var3 == Region.MENU_BAR && var1 instanceof JMenuBar) {
            var4 = ((JMenuBar)var1).getMargin();
         }

         if (var4 != null) {
            var2.top += var4.top;
            var2.bottom += var4.bottom;
            var2.left += var4.left;
            var2.right += var4.right;
         }
      }

      return var2;
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
