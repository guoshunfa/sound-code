package sun.swing;

import java.awt.Container;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

public class DefaultLayoutStyle extends LayoutStyle {
   private static final DefaultLayoutStyle INSTANCE = new DefaultLayoutStyle();

   public static LayoutStyle getInstance() {
      return INSTANCE;
   }

   public int getPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3, int var4, Container var5) {
      if (var1 != null && var2 != null && var3 != null) {
         this.checkPosition(var4);
         if (var3 == LayoutStyle.ComponentPlacement.INDENT && (var4 == 3 || var4 == 7)) {
            int var6 = this.getIndent(var1, var4);
            if (var6 > 0) {
               return var6;
            }
         }

         return var3 == LayoutStyle.ComponentPlacement.UNRELATED ? 12 : 6;
      } else {
         throw new NullPointerException();
      }
   }

   public int getContainerGap(JComponent var1, int var2, Container var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.checkPosition(var2);
         return 6;
      }
   }

   protected boolean isLabelAndNonlabel(JComponent var1, JComponent var2, int var3) {
      if (var3 != 3 && var3 != 7) {
         return false;
      } else {
         boolean var4 = var1 instanceof JLabel;
         boolean var5 = var2 instanceof JLabel;
         return (var4 || var5) && var4 != var5;
      }
   }

   protected int getButtonGap(JComponent var1, JComponent var2, int var3, int var4) {
      var4 -= this.getButtonGap(var1, var3);
      if (var4 > 0) {
         var4 -= this.getButtonGap(var2, this.flipDirection(var3));
      }

      return var4 < 0 ? 0 : var4;
   }

   protected int getButtonGap(JComponent var1, int var2, int var3) {
      var3 -= this.getButtonGap(var1, var2);
      return Math.max(var3, 0);
   }

   public int getButtonGap(JComponent var1, int var2) {
      String var3 = var1.getUIClassID();
      if ((var3 == "CheckBoxUI" || var3 == "RadioButtonUI") && !((AbstractButton)var1).isBorderPainted()) {
         Border var4 = var1.getBorder();
         if (var4 instanceof UIResource) {
            return this.getInset(var1, var2);
         }
      }

      return 0;
   }

   private void checkPosition(int var1) {
      if (var1 != 1 && var1 != 5 && var1 != 7 && var1 != 3) {
         throw new IllegalArgumentException();
      }
   }

   protected int flipDirection(int var1) {
      switch(var1) {
      case 1:
         return 5;
      case 2:
      case 4:
      case 6:
      default:
         assert false;

         return 0;
      case 3:
         return 7;
      case 5:
         return 1;
      case 7:
         return 3;
      }
   }

   protected int getIndent(JComponent var1, int var2) {
      String var3 = var1.getUIClassID();
      if (var3 == "CheckBoxUI" || var3 == "RadioButtonUI") {
         AbstractButton var4 = (AbstractButton)var1;
         Insets var5 = var1.getInsets();
         Icon var6 = this.getIcon(var4);
         int var7 = var4.getIconTextGap();
         if (this.isLeftAligned(var4, var2)) {
            return var5.left + var6.getIconWidth() + var7;
         }

         if (this.isRightAligned(var4, var2)) {
            return var5.right + var6.getIconWidth() + var7;
         }
      }

      return 0;
   }

   private Icon getIcon(AbstractButton var1) {
      Icon var2 = var1.getIcon();
      if (var2 != null) {
         return var2;
      } else {
         String var3 = null;
         if (var1 instanceof JCheckBox) {
            var3 = "CheckBox.icon";
         } else if (var1 instanceof JRadioButton) {
            var3 = "RadioButton.icon";
         }

         if (var3 != null) {
            Object var4 = UIManager.get(var3);
            if (var4 instanceof Icon) {
               return (Icon)var4;
            }
         }

         return null;
      }
   }

   private boolean isLeftAligned(AbstractButton var1, int var2) {
      if (var2 != 7) {
         return false;
      } else {
         boolean var3 = var1.getComponentOrientation().isLeftToRight();
         int var4 = var1.getHorizontalAlignment();
         return var3 && (var4 == 2 || var4 == 10) || !var3 && var4 == 11;
      }
   }

   private boolean isRightAligned(AbstractButton var1, int var2) {
      if (var2 != 3) {
         return false;
      } else {
         boolean var3 = var1.getComponentOrientation().isLeftToRight();
         int var4 = var1.getHorizontalAlignment();
         return var3 && (var4 == 4 || var4 == 11) || !var3 && var4 == 10;
      }
   }

   private int getInset(JComponent var1, int var2) {
      return this.getInset(var1.getInsets(), var2);
   }

   private int getInset(Insets var1, int var2) {
      if (var1 == null) {
         return 0;
      } else {
         switch(var2) {
         case 1:
            return var1.top;
         case 2:
         case 4:
         case 6:
         default:
            assert false;

            return 0;
         case 3:
            return var1.right;
         case 5:
            return var1.bottom;
         case 7:
            return var1.left;
         }
      }
   }
}
