package javax.swing.plaf.synth;

import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComboBox;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;

class SynthComboPopup extends BasicComboPopup {
   public SynthComboPopup(JComboBox var1) {
      super(var1);
   }

   protected void configureList() {
      this.list.setFont(this.comboBox.getFont());
      this.list.setCellRenderer(this.comboBox.getRenderer());
      this.list.setFocusable(false);
      this.list.setSelectionMode(0);
      int var1 = this.comboBox.getSelectedIndex();
      if (var1 == -1) {
         this.list.clearSelection();
      } else {
         this.list.setSelectedIndex(var1);
         this.list.ensureIndexIsVisible(var1);
      }

      this.installListListeners();
   }

   protected Rectangle computePopupBounds(int var1, int var2, int var3, int var4) {
      ComboBoxUI var5 = this.comboBox.getUI();
      if (var5 instanceof SynthComboBoxUI) {
         SynthComboBoxUI var6 = (SynthComboBoxUI)var5;
         if (var6.popupInsets != null) {
            Insets var7 = var6.popupInsets;
            return super.computePopupBounds(var1 + var7.left, var2 + var7.top, var3 - var7.left - var7.right, var4 - var7.top - var7.bottom);
         }
      }

      return super.computePopupBounds(var1, var2, var3, var4);
   }
}
