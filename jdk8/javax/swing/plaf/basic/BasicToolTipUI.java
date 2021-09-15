package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class BasicToolTipUI extends ToolTipUI {
   static BasicToolTipUI sharedInstance = new BasicToolTipUI();
   private static PropertyChangeListener sharedPropertyChangedListener;
   private PropertyChangeListener propertyChangeListener;

   public static ComponentUI createUI(JComponent var0) {
      return sharedInstance;
   }

   public void installUI(JComponent var1) {
      this.installDefaults(var1);
      this.installComponents(var1);
      this.installListeners(var1);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults(var1);
      this.uninstallComponents(var1);
      this.uninstallListeners(var1);
   }

   protected void installDefaults(JComponent var1) {
      LookAndFeel.installColorsAndFont(var1, "ToolTip.background", "ToolTip.foreground", "ToolTip.font");
      LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
      this.componentChanged(var1);
   }

   protected void uninstallDefaults(JComponent var1) {
      LookAndFeel.uninstallBorder(var1);
   }

   private void installComponents(JComponent var1) {
      BasicHTML.updateRenderer(var1, ((JToolTip)var1).getTipText());
   }

   private void uninstallComponents(JComponent var1) {
      BasicHTML.updateRenderer(var1, "");
   }

   protected void installListeners(JComponent var1) {
      this.propertyChangeListener = this.createPropertyChangeListener(var1);
      var1.addPropertyChangeListener(this.propertyChangeListener);
   }

   protected void uninstallListeners(JComponent var1) {
      var1.removePropertyChangeListener(this.propertyChangeListener);
      this.propertyChangeListener = null;
   }

   private PropertyChangeListener createPropertyChangeListener(JComponent var1) {
      if (sharedPropertyChangedListener == null) {
         sharedPropertyChangedListener = new BasicToolTipUI.PropertyChangeHandler();
      }

      return sharedPropertyChangedListener;
   }

   public void paint(Graphics var1, JComponent var2) {
      Font var3 = var2.getFont();
      FontMetrics var4 = SwingUtilities2.getFontMetrics(var2, var1, var3);
      Dimension var5 = var2.getSize();
      var1.setColor(var2.getForeground());
      String var6 = ((JToolTip)var2).getTipText();
      if (var6 == null) {
         var6 = "";
      }

      Insets var7 = var2.getInsets();
      Rectangle var8 = new Rectangle(var7.left + 3, var7.top, var5.width - (var7.left + var7.right) - 6, var5.height - (var7.top + var7.bottom));
      View var9 = (View)var2.getClientProperty("html");
      if (var9 != null) {
         var9.paint(var1, var8);
      } else {
         var1.setFont(var3);
         SwingUtilities2.drawString(var2, var1, var6, var8.x, var8.y + var4.getAscent());
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      Font var2 = var1.getFont();
      FontMetrics var3 = var1.getFontMetrics(var2);
      Insets var4 = var1.getInsets();
      Dimension var5 = new Dimension(var4.left + var4.right, var4.top + var4.bottom);
      String var6 = ((JToolTip)var1).getTipText();
      if (var6 != null && !var6.equals("")) {
         View var7 = var1 != null ? (View)var1.getClientProperty("html") : null;
         if (var7 != null) {
            var5.width += (int)var7.getPreferredSpan(0) + 6;
            var5.height += (int)var7.getPreferredSpan(1);
         } else {
            var5.width += SwingUtilities2.stringWidth(var1, var3, var6) + 6;
            var5.height += var3.getHeight();
         }
      } else {
         var6 = "";
      }

      return var5;
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width - (var3.getPreferredSpan(0) - var3.getMinimumSpan(0)));
      }

      return var2;
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width + (var3.getMaximumSpan(0) - var3.getPreferredSpan(0)));
      }

      return var2;
   }

   private void componentChanged(JComponent var1) {
      JComponent var2 = ((JToolTip)var1).getComponent();
      if (var2 != null && !var2.isEnabled()) {
         if (UIManager.getBorder("ToolTip.borderInactive") != null) {
            LookAndFeel.installBorder(var1, "ToolTip.borderInactive");
         } else {
            LookAndFeel.installBorder(var1, "ToolTip.border");
         }

         if (UIManager.getColor("ToolTip.backgroundInactive") != null) {
            LookAndFeel.installColors(var1, "ToolTip.backgroundInactive", "ToolTip.foregroundInactive");
         } else {
            LookAndFeel.installColors(var1, "ToolTip.background", "ToolTip.foreground");
         }
      } else {
         LookAndFeel.installBorder(var1, "ToolTip.border");
         LookAndFeel.installColors(var1, "ToolTip.background", "ToolTip.foreground");
      }

   }

   private static class PropertyChangeHandler implements PropertyChangeListener {
      private PropertyChangeHandler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         JToolTip var3;
         if (!var2.equals("tiptext") && !"font".equals(var2) && !"foreground".equals(var2)) {
            if ("component".equals(var2)) {
               var3 = (JToolTip)var1.getSource();
               if (var3.getUI() instanceof BasicToolTipUI) {
                  ((BasicToolTipUI)var3.getUI()).componentChanged(var3);
               }
            }
         } else {
            var3 = (JToolTip)var1.getSource();
            String var4 = var3.getTipText();
            BasicHTML.updateRenderer(var3, var4);
         }

      }

      // $FF: synthetic method
      PropertyChangeHandler(Object var1) {
         this();
      }
   }
}
