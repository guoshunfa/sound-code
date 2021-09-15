package javax.swing.plaf.synth;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.UIResource;

class SynthArrowButton extends JButton implements SwingConstants, UIResource {
   private int direction;

   public SynthArrowButton(int var1) {
      super.setFocusable(false);
      this.setDirection(var1);
      this.setDefaultCapable(false);
   }

   public String getUIClassID() {
      return "ArrowButtonUI";
   }

   public void updateUI() {
      this.setUI(new SynthArrowButton.SynthArrowButtonUI());
   }

   public void setDirection(int var1) {
      this.direction = var1;
      this.putClientProperty("__arrow_direction__", var1);
      this.repaint();
   }

   public int getDirection() {
      return this.direction;
   }

   public void setFocusable(boolean var1) {
   }

   private static class SynthArrowButtonUI extends SynthButtonUI {
      private SynthArrowButtonUI() {
      }

      protected void installDefaults(AbstractButton var1) {
         super.installDefaults(var1);
         this.updateStyle(var1);
      }

      protected void paint(SynthContext var1, Graphics var2) {
         SynthArrowButton var3 = (SynthArrowButton)var1.getComponent();
         var1.getPainter().paintArrowButtonForeground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight(), var3.getDirection());
      }

      void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
         var1.getPainter().paintArrowButtonBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
      }

      public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var1.getPainter().paintArrowButtonBorder(var1, var2, var3, var4, var5, var6);
      }

      public Dimension getMinimumSize() {
         return new Dimension(5, 5);
      }

      public Dimension getMaximumSize() {
         return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
      }

      public Dimension getPreferredSize(JComponent var1) {
         SynthContext var2 = this.getContext(var1);
         Dimension var3 = null;
         if (var2.getComponent().getName() == "ScrollBar.button") {
            var3 = (Dimension)var2.getStyle().get(var2, "ScrollBar.buttonSize");
         }

         if (var3 == null) {
            int var4 = var2.getStyle().getInt(var2, "ArrowButton.size", 16);
            var3 = new Dimension(var4, var4);
         }

         Container var6 = var2.getComponent().getParent();
         if (var6 instanceof JComponent && !(var6 instanceof JComboBox)) {
            Object var5 = ((JComponent)var6).getClientProperty("JComponent.sizeVariant");
            if (var5 != null) {
               if ("large".equals(var5)) {
                  var3 = new Dimension((int)((double)var3.width * 1.15D), (int)((double)var3.height * 1.15D));
               } else if ("small".equals(var5)) {
                  var3 = new Dimension((int)((double)var3.width * 0.857D), (int)((double)var3.height * 0.857D));
               } else if ("mini".equals(var5)) {
                  var3 = new Dimension((int)((double)var3.width * 0.714D), (int)((double)var3.height * 0.714D));
               }
            }
         }

         var2.dispose();
         return var3;
      }

      // $FF: synthetic method
      SynthArrowButtonUI(Object var1) {
         this();
      }
   }
}
