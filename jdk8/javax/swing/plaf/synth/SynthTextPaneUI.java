package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SynthTextPaneUI extends SynthEditorPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new SynthTextPaneUI();
   }

   protected String getPropertyPrefix() {
      return "TextPane";
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.updateForeground(var1.getForeground());
      this.updateFont(var1.getFont());
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      String var2 = var1.getPropertyName();
      if (var2.equals("foreground")) {
         this.updateForeground((Color)var1.getNewValue());
      } else if (var2.equals("font")) {
         this.updateFont((Font)var1.getNewValue());
      } else if (var2.equals("document")) {
         JTextComponent var3 = this.getComponent();
         this.updateForeground(var3.getForeground());
         this.updateFont(var3.getFont());
      }

   }

   private void updateForeground(Color var1) {
      StyledDocument var2 = (StyledDocument)this.getComponent().getDocument();
      Style var3 = var2.getStyle("default");
      if (var3 != null) {
         if (var1 == null) {
            var3.removeAttribute(StyleConstants.Foreground);
         } else {
            StyleConstants.setForeground(var3, var1);
         }

      }
   }

   private void updateFont(Font var1) {
      StyledDocument var2 = (StyledDocument)this.getComponent().getDocument();
      Style var3 = var2.getStyle("default");
      if (var3 != null) {
         if (var1 == null) {
            var3.removeAttribute(StyleConstants.FontFamily);
            var3.removeAttribute(StyleConstants.FontSize);
            var3.removeAttribute(StyleConstants.Bold);
            var3.removeAttribute(StyleConstants.Italic);
         } else {
            StyleConstants.setFontFamily(var3, var1.getName());
            StyleConstants.setFontSize(var3, var1.getSize());
            StyleConstants.setBold(var3, var1.isBold());
            StyleConstants.setItalic(var3, var1.isItalic());
         }

      }
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      var1.getPainter().paintTextPaneBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTextPaneBorder(var1, var2, var3, var4, var5, var6);
   }
}
