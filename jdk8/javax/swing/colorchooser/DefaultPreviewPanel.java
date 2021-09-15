package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Locale;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.swing.SwingUtilities2;

class DefaultPreviewPanel extends JPanel {
   private int squareSize = 25;
   private int squareGap = 5;
   private int innerGap = 5;
   private int textGap = 5;
   private Font font = new Font("Dialog", 0, 12);
   private String sampleText;
   private int swatchWidth = 50;
   private Color oldColor = null;

   private JColorChooser getColorChooser() {
      return (JColorChooser)SwingUtilities.getAncestorOfClass(JColorChooser.class, this);
   }

   public Dimension getPreferredSize() {
      Object var1 = this.getColorChooser();
      if (var1 == null) {
         var1 = this;
      }

      FontMetrics var2 = ((JComponent)var1).getFontMetrics(this.getFont());
      int var3 = var2.getAscent();
      int var4 = var2.getHeight();
      int var5 = SwingUtilities2.stringWidth((JComponent)var1, var2, this.getSampleText());
      int var6 = var4 * 3 + this.textGap * 3;
      int var7 = this.squareSize * 3 + this.squareGap * 2 + this.swatchWidth + var5 + this.textGap * 3;
      return new Dimension(var7, var6);
   }

   public void paintComponent(Graphics var1) {
      if (this.oldColor == null) {
         this.oldColor = this.getForeground();
      }

      var1.setColor(this.getBackground());
      var1.fillRect(0, 0, this.getWidth(), this.getHeight());
      int var2;
      int var3;
      if (this.getComponentOrientation().isLeftToRight()) {
         var2 = this.paintSquares(var1, 0);
         var3 = this.paintText(var1, var2);
         this.paintSwatch(var1, var2 + var3);
      } else {
         var2 = this.paintSwatch(var1, 0);
         var3 = this.paintText(var1, var2);
         this.paintSquares(var1, var2 + var3);
      }

   }

   private int paintSwatch(Graphics var1, int var2) {
      var1.setColor(this.oldColor);
      var1.fillRect(var2, 0, this.swatchWidth, this.squareSize + this.squareGap / 2);
      var1.setColor(this.getForeground());
      var1.fillRect(var2, this.squareSize + this.squareGap / 2, this.swatchWidth, this.squareSize + this.squareGap / 2);
      return var2 + this.swatchWidth;
   }

   private int paintText(Graphics var1, int var2) {
      var1.setFont(this.getFont());
      Object var3 = this.getColorChooser();
      if (var3 == null) {
         var3 = this;
      }

      FontMetrics var4 = SwingUtilities2.getFontMetrics((JComponent)var3, (Graphics)var1);
      int var5 = var4.getAscent();
      int var6 = var4.getHeight();
      int var7 = SwingUtilities2.stringWidth((JComponent)var3, var4, this.getSampleText());
      int var8 = var2 + this.textGap;
      Color var9 = this.getForeground();
      var1.setColor(var9);
      SwingUtilities2.drawString((JComponent)var3, var1, (String)this.getSampleText(), var8 + this.textGap / 2, var5 + 2);
      var1.fillRect(var8, var6 + this.textGap, var7 + this.textGap, var6 + 2);
      var1.setColor(Color.black);
      SwingUtilities2.drawString((JComponent)var3, var1, (String)this.getSampleText(), var8 + this.textGap / 2, var6 + var5 + this.textGap + 2);
      var1.setColor(Color.white);
      var1.fillRect(var8, (var6 + this.textGap) * 2, var7 + this.textGap, var6 + 2);
      var1.setColor(var9);
      SwingUtilities2.drawString((JComponent)var3, var1, (String)this.getSampleText(), var8 + this.textGap / 2, (var6 + this.textGap) * 2 + var5 + 2);
      return var7 + this.textGap * 3;
   }

   private int paintSquares(Graphics var1, int var2) {
      Color var4 = this.getForeground();
      var1.setColor(Color.white);
      var1.fillRect(var2, 0, this.squareSize, this.squareSize);
      var1.setColor(var4);
      var1.fillRect(var2 + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
      var1.setColor(Color.white);
      var1.fillRect(var2 + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
      var1.setColor(var4);
      var1.fillRect(var2, this.squareSize + this.squareGap, this.squareSize, this.squareSize);
      var1.translate(this.squareSize + this.squareGap, 0);
      var1.setColor(Color.black);
      var1.fillRect(var2, 0, this.squareSize, this.squareSize);
      var1.setColor(var4);
      var1.fillRect(var2 + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
      var1.setColor(Color.white);
      var1.fillRect(var2 + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
      var1.translate(-(this.squareSize + this.squareGap), 0);
      var1.translate(this.squareSize + this.squareGap, this.squareSize + this.squareGap);
      var1.setColor(Color.white);
      var1.fillRect(var2, 0, this.squareSize, this.squareSize);
      var1.setColor(var4);
      var1.fillRect(var2 + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
      var1.translate(-(this.squareSize + this.squareGap), -(this.squareSize + this.squareGap));
      var1.translate((this.squareSize + this.squareGap) * 2, 0);
      var1.setColor(Color.white);
      var1.fillRect(var2, 0, this.squareSize, this.squareSize);
      var1.setColor(var4);
      var1.fillRect(var2 + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
      var1.setColor(Color.black);
      var1.fillRect(var2 + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
      var1.translate(-((this.squareSize + this.squareGap) * 2), 0);
      var1.translate((this.squareSize + this.squareGap) * 2, this.squareSize + this.squareGap);
      var1.setColor(Color.black);
      var1.fillRect(var2, 0, this.squareSize, this.squareSize);
      var1.setColor(var4);
      var1.fillRect(var2 + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
      var1.translate(-((this.squareSize + this.squareGap) * 2), -(this.squareSize + this.squareGap));
      return this.squareSize * 3 + this.squareGap * 2;
   }

   private String getSampleText() {
      if (this.sampleText == null) {
         this.sampleText = UIManager.getString("ColorChooser.sampleText", (Locale)this.getLocale());
      }

      return this.sampleText;
   }
}
