package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class MetalComboBoxEditor extends BasicComboBoxEditor {
   protected static Insets editorBorderInsets = new Insets(2, 2, 2, 0);

   public MetalComboBoxEditor() {
      this.editor = new JTextField("", 9) {
         public void setText(String var1) {
            if (!this.getText().equals(var1)) {
               super.setText(var1);
            }
         }

         public Dimension getPreferredSize() {
            Dimension var1 = super.getPreferredSize();
            var1.height += 4;
            return var1;
         }

         public Dimension getMinimumSize() {
            Dimension var1 = super.getMinimumSize();
            var1.height += 4;
            return var1;
         }
      };
      this.editor.setBorder(new MetalComboBoxEditor.EditorBorder());
   }

   public static class UIResource extends MetalComboBoxEditor implements javax.swing.plaf.UIResource {
   }

   class EditorBorder extends AbstractBorder {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         if (MetalLookAndFeel.usingOcean()) {
            var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            var2.drawRect(0, 0, var5, var6 - 1);
            var2.setColor(MetalLookAndFeel.getControlShadow());
            var2.drawRect(1, 1, var5 - 2, var6 - 3);
         } else {
            var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            var2.drawLine(0, 0, var5 - 1, 0);
            var2.drawLine(0, 0, 0, var6 - 2);
            var2.drawLine(0, var6 - 2, var5 - 1, var6 - 2);
            var2.setColor(MetalLookAndFeel.getControlHighlight());
            var2.drawLine(1, 1, var5 - 1, 1);
            var2.drawLine(1, 1, 1, var6 - 1);
            var2.drawLine(1, var6 - 1, var5 - 1, var6 - 1);
            var2.setColor(MetalLookAndFeel.getControl());
            var2.drawLine(1, var6 - 2, 1, var6 - 2);
         }

         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 2, 0);
         return var2;
      }
   }
}
