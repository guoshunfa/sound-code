package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class MetalToolTipUI extends BasicToolTipUI {
   static MetalToolTipUI sharedInstance = new MetalToolTipUI();
   private Font smallFont;
   private JToolTip tip;
   public static final int padSpaceBetweenStrings = 12;
   private String acceleratorDelimiter;

   public static ComponentUI createUI(JComponent var0) {
      return sharedInstance;
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.tip = (JToolTip)var1;
      Font var2 = var1.getFont();
      this.smallFont = new Font(var2.getName(), var2.getStyle(), var2.getSize() - 2);
      this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
      if (this.acceleratorDelimiter == null) {
         this.acceleratorDelimiter = "-";
      }

   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      this.tip = null;
   }

   public void paint(Graphics var1, JComponent var2) {
      JToolTip var3 = (JToolTip)var2;
      Font var4 = var2.getFont();
      FontMetrics var5 = SwingUtilities2.getFontMetrics(var2, var1, var4);
      Dimension var6 = var2.getSize();
      var1.setColor(var2.getForeground());
      String var8 = var3.getTipText();
      if (var8 == null) {
         var8 = "";
      }

      String var9 = this.getAcceleratorString(var3);
      FontMetrics var10 = SwingUtilities2.getFontMetrics(var2, var1, this.smallFont);
      int var11 = this.calcAccelSpacing(var2, var10, var9);
      Insets var12 = var3.getInsets();
      Rectangle var13 = new Rectangle(var12.left + 3, var12.top, var6.width - (var12.left + var12.right) - 6 - var11, var6.height - (var12.top + var12.bottom));
      View var14 = (View)var2.getClientProperty("html");
      int var7;
      if (var14 != null) {
         var14.paint(var1, var13);
         var7 = BasicHTML.getHTMLBaseline(var14, var13.width, var13.height);
      } else {
         var1.setFont(var4);
         SwingUtilities2.drawString(var3, var1, (String)var8, var13.x, var13.y + var5.getAscent());
         var7 = var5.getAscent();
      }

      if (!var9.equals("")) {
         var1.setFont(this.smallFont);
         var1.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         SwingUtilities2.drawString(var3, var1, (String)var9, var3.getWidth() - 1 - var12.right - var11 + 12 - 3, var13.y + var7);
      }

   }

   private int calcAccelSpacing(JComponent var1, FontMetrics var2, String var3) {
      return var3.equals("") ? 0 : 12 + SwingUtilities2.stringWidth(var1, var2, var3);
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = super.getPreferredSize(var1);
      String var3 = this.getAcceleratorString((JToolTip)var1);
      if (!var3.equals("")) {
         var2.width += this.calcAccelSpacing(var1, var1.getFontMetrics(this.smallFont), var3);
      }

      return var2;
   }

   protected boolean isAcceleratorHidden() {
      Boolean var1 = (Boolean)UIManager.get("ToolTip.hideAccelerator");
      return var1 != null && var1;
   }

   private String getAcceleratorString(JToolTip var1) {
      this.tip = var1;
      String var2 = this.getAcceleratorString();
      this.tip = null;
      return var2;
   }

   public String getAcceleratorString() {
      if (this.tip != null && !this.isAcceleratorHidden()) {
         JComponent var1 = this.tip.getComponent();
         if (!(var1 instanceof AbstractButton)) {
            return "";
         } else {
            KeyStroke[] var2 = var1.getInputMap(2).keys();
            if (var2 == null) {
               return "";
            } else {
               String var3 = "";
               byte var4 = 0;
               if (var4 < var2.length) {
                  int var5 = var2[var4].getModifiers();
                  var3 = KeyEvent.getKeyModifiersText(var5) + this.acceleratorDelimiter + KeyEvent.getKeyText(var2[var4].getKeyCode());
               }

               return var3;
            }
         }
      } else {
         return "";
      }
   }
}
