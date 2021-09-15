package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.beans.ConstructorProperties;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class TitledBorder extends AbstractBorder {
   protected String title;
   protected Border border;
   protected int titlePosition;
   protected int titleJustification;
   protected Font titleFont;
   protected Color titleColor;
   private final JLabel label;
   public static final int DEFAULT_POSITION = 0;
   public static final int ABOVE_TOP = 1;
   public static final int TOP = 2;
   public static final int BELOW_TOP = 3;
   public static final int ABOVE_BOTTOM = 4;
   public static final int BOTTOM = 5;
   public static final int BELOW_BOTTOM = 6;
   public static final int DEFAULT_JUSTIFICATION = 0;
   public static final int LEFT = 1;
   public static final int CENTER = 2;
   public static final int RIGHT = 3;
   public static final int LEADING = 4;
   public static final int TRAILING = 5;
   protected static final int EDGE_SPACING = 2;
   protected static final int TEXT_SPACING = 2;
   protected static final int TEXT_INSET_H = 5;

   public TitledBorder(String var1) {
      this((Border)null, var1, 4, 0, (Font)null, (Color)null);
   }

   public TitledBorder(Border var1) {
      this(var1, "", 4, 0, (Font)null, (Color)null);
   }

   public TitledBorder(Border var1, String var2) {
      this(var1, var2, 4, 0, (Font)null, (Color)null);
   }

   public TitledBorder(Border var1, String var2, int var3, int var4) {
      this(var1, var2, var3, var4, (Font)null, (Color)null);
   }

   public TitledBorder(Border var1, String var2, int var3, int var4, Font var5) {
      this(var1, var2, var3, var4, var5, (Color)null);
   }

   @ConstructorProperties({"border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor"})
   public TitledBorder(Border var1, String var2, int var3, int var4, Font var5, Color var6) {
      this.title = var2;
      this.border = var1;
      this.titleFont = var5;
      this.titleColor = var6;
      this.setTitleJustification(var3);
      this.setTitlePosition(var4);
      this.label = new JLabel();
      this.label.setOpaque(false);
      this.label.putClientProperty("html", (Object)null);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Border var7 = this.getBorder();
      String var8 = this.getTitle();
      if (var8 != null && !var8.isEmpty()) {
         int var9 = var7 instanceof TitledBorder ? 0 : 2;
         JLabel var10 = this.getLabel(var1);
         Dimension var11 = var10.getPreferredSize();
         Insets var12 = getBorderInsets(var7, var1, new Insets(0, 0, 0, 0));
         int var13 = var3 + var9;
         int var14 = var4 + var9;
         int var15 = var5 - var9 - var9;
         int var16 = var6 - var9 - var9;
         int var17 = var4;
         int var18 = var11.height;
         int var19 = this.getPosition();
         switch(var19) {
         case 1:
            var12.left = 0;
            var12.right = 0;
            var14 += var18 - var9;
            var16 -= var18 - var9;
            break;
         case 2:
            var12.top = var9 + var12.top / 2 - var18 / 2;
            if (var12.top < var9) {
               var14 -= var12.top;
               var16 += var12.top;
            } else {
               var17 = var4 + var12.top;
            }
            break;
         case 3:
            var17 = var4 + var12.top + var9;
            break;
         case 4:
            var17 = var4 + (var6 - var18 - var12.bottom - var9);
            break;
         case 5:
            var17 = var4 + (var6 - var18);
            var12.bottom = var9 + (var12.bottom - var18) / 2;
            if (var12.bottom < var9) {
               var16 += var12.bottom;
            } else {
               var17 -= var12.bottom;
            }
            break;
         case 6:
            var12.left = 0;
            var12.right = 0;
            var17 = var4 + (var6 - var18);
            var16 -= var18 - var9;
         }

         var12.left += var9 + 5;
         var12.right += var9 + 5;
         int var20 = var3;
         int var21 = var5 - var12.left - var12.right;
         if (var21 > var11.width) {
            var21 = var11.width;
         }

         switch(this.getJustification(var1)) {
         case 1:
            var20 = var3 + var12.left;
            break;
         case 2:
            var20 = var3 + (var5 - var21) / 2;
            break;
         case 3:
            var20 = var3 + (var5 - var12.right - var21);
         }

         if (var7 != null) {
            if (var19 != 2 && var19 != 5) {
               var7.paintBorder(var1, var2, var13, var14, var15, var16);
            } else {
               Graphics var22 = var2.create();
               if (var22 instanceof Graphics2D) {
                  Graphics2D var23 = (Graphics2D)var22;
                  Path2D.Float var24 = new Path2D.Float();
                  var24.append((Shape)(new Rectangle(var13, var14, var15, var17 - var14)), false);
                  var24.append((Shape)(new Rectangle(var13, var17, var20 - var13 - 2, var18)), false);
                  var24.append((Shape)(new Rectangle(var20 + var21 + 2, var17, var13 - var20 + var15 - var21 - 2, var18)), false);
                  var24.append((Shape)(new Rectangle(var13, var17 + var18, var15, var14 - var17 + var16 - var18)), false);
                  var23.clip(var24);
               }

               var7.paintBorder(var1, var22, var13, var14, var15, var16);
               var22.dispose();
            }
         }

         var2.translate(var20, var17);
         var10.setSize(var21, var18);
         var10.paint(var2);
         var2.translate(-var20, -var17);
      } else if (var7 != null) {
         var7.paintBorder(var1, var2, var3, var4, var5, var6);
      }

   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      Border var3 = this.getBorder();
      var2 = getBorderInsets(var3, var1, var2);
      String var4 = this.getTitle();
      if (var4 != null && !var4.isEmpty()) {
         int var5 = var3 instanceof TitledBorder ? 0 : 2;
         JLabel var6 = this.getLabel(var1);
         Dimension var7 = var6.getPreferredSize();
         switch(this.getPosition()) {
         case 1:
            var2.top += var7.height - var5;
            break;
         case 2:
            if (var2.top < var7.height) {
               var2.top = var7.height - var5;
            }
            break;
         case 3:
            var2.top += var7.height;
            break;
         case 4:
            var2.bottom += var7.height;
            break;
         case 5:
            if (var2.bottom < var7.height) {
               var2.bottom = var7.height - var5;
            }
            break;
         case 6:
            var2.bottom += var7.height - var5;
         }

         var2.top += var5 + 2;
         var2.left += var5 + 2;
         var2.right += var5 + 2;
         var2.bottom += var5 + 2;
      }

      return var2;
   }

   public boolean isBorderOpaque() {
      return false;
   }

   public String getTitle() {
      return this.title;
   }

   public Border getBorder() {
      return this.border != null ? this.border : UIManager.getBorder("TitledBorder.border");
   }

   public int getTitlePosition() {
      return this.titlePosition;
   }

   public int getTitleJustification() {
      return this.titleJustification;
   }

   public Font getTitleFont() {
      return this.titleFont == null ? UIManager.getFont("TitledBorder.font") : this.titleFont;
   }

   public Color getTitleColor() {
      return this.titleColor == null ? UIManager.getColor("TitledBorder.titleColor") : this.titleColor;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public void setBorder(Border var1) {
      this.border = var1;
   }

   public void setTitlePosition(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
         this.titlePosition = var1;
         return;
      default:
         throw new IllegalArgumentException(var1 + " is not a valid title position.");
      }
   }

   public void setTitleJustification(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
         this.titleJustification = var1;
         return;
      default:
         throw new IllegalArgumentException(var1 + " is not a valid title justification.");
      }
   }

   public void setTitleFont(Font var1) {
      this.titleFont = var1;
   }

   public void setTitleColor(Color var1) {
      this.titleColor = var1;
   }

   public Dimension getMinimumSize(Component var1) {
      Insets var2 = this.getBorderInsets(var1);
      Dimension var3 = new Dimension(var2.right + var2.left, var2.top + var2.bottom);
      String var4 = this.getTitle();
      if (var4 != null && !var4.isEmpty()) {
         JLabel var5 = this.getLabel(var1);
         Dimension var6 = var5.getPreferredSize();
         int var7 = this.getPosition();
         if (var7 != 1 && var7 != 6) {
            var3.width += var6.width;
         } else if (var3.width < var6.width) {
            var3.width += var6.width;
         }
      }

      return var3;
   }

   public int getBaseline(Component var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("Must supply non-null component");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("Width must be >= 0");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("Height must be >= 0");
      } else {
         Border var4 = this.getBorder();
         String var5 = this.getTitle();
         if (var5 != null && !var5.isEmpty()) {
            int var6 = var4 instanceof TitledBorder ? 0 : 2;
            JLabel var7 = this.getLabel(var1);
            Dimension var8 = var7.getPreferredSize();
            Insets var9 = getBorderInsets(var4, var1, new Insets(0, 0, 0, 0));
            int var10 = var7.getBaseline(var8.width, var8.height);
            switch(this.getPosition()) {
            case 1:
               return var10;
            case 2:
               var9.top = var6 + (var9.top - var8.height) / 2;
               return var9.top < var6 ? var10 : var10 + var9.top;
            case 3:
               return var10 + var9.top + var6;
            case 4:
               return var10 + var3 - var8.height - var9.bottom - var6;
            case 5:
               var9.bottom = var6 + (var9.bottom - var8.height) / 2;
               return var9.bottom < var6 ? var10 + var3 - var8.height : var10 + var3 - var8.height + var9.bottom;
            case 6:
               return var10 + var3 - var8.height;
            }
         }

         return -1;
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component var1) {
      super.getBaselineResizeBehavior(var1);
      switch(this.getPosition()) {
      case 1:
      case 2:
      case 3:
         return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      case 4:
      case 5:
      case 6:
         return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
      default:
         return Component.BaselineResizeBehavior.OTHER;
      }
   }

   private int getPosition() {
      int var1 = this.getTitlePosition();
      if (var1 != 0) {
         return var1;
      } else {
         Object var2 = UIManager.get("TitledBorder.position");
         if (var2 instanceof Integer) {
            int var3 = (Integer)var2;
            if (0 < var3 && var3 <= 6) {
               return var3;
            }
         } else if (var2 instanceof String) {
            String var4 = (String)var2;
            if (var4.equalsIgnoreCase("ABOVE_TOP")) {
               return 1;
            }

            if (var4.equalsIgnoreCase("TOP")) {
               return 2;
            }

            if (var4.equalsIgnoreCase("BELOW_TOP")) {
               return 3;
            }

            if (var4.equalsIgnoreCase("ABOVE_BOTTOM")) {
               return 4;
            }

            if (var4.equalsIgnoreCase("BOTTOM")) {
               return 5;
            }

            if (var4.equalsIgnoreCase("BELOW_BOTTOM")) {
               return 6;
            }
         }

         return 2;
      }
   }

   private int getJustification(Component var1) {
      int var2 = this.getTitleJustification();
      if (var2 != 4 && var2 != 0) {
         if (var2 == 5) {
            return var1.getComponentOrientation().isLeftToRight() ? 3 : 1;
         } else {
            return var2;
         }
      } else {
         return var1.getComponentOrientation().isLeftToRight() ? 1 : 3;
      }
   }

   protected Font getFont(Component var1) {
      Font var2 = this.getTitleFont();
      if (var2 != null) {
         return var2;
      } else {
         if (var1 != null) {
            var2 = var1.getFont();
            if (var2 != null) {
               return var2;
            }
         }

         return new Font("Dialog", 0, 12);
      }
   }

   private Color getColor(Component var1) {
      Color var2 = this.getTitleColor();
      if (var2 != null) {
         return var2;
      } else {
         return var1 != null ? var1.getForeground() : null;
      }
   }

   private JLabel getLabel(Component var1) {
      this.label.setText(this.getTitle());
      this.label.setFont(this.getFont(var1));
      this.label.setForeground(this.getColor(var1));
      this.label.setComponentOrientation(var1.getComponentOrientation());
      this.label.setEnabled(var1.isEnabled());
      return this.label;
   }

   private static Insets getBorderInsets(Border var0, Component var1, Insets var2) {
      if (var0 == null) {
         var2.set(0, 0, 0, 0);
      } else if (var0 instanceof AbstractBorder) {
         AbstractBorder var3 = (AbstractBorder)var0;
         var2 = var3.getBorderInsets(var1, var2);
      } else {
         Insets var4 = var0.getBorderInsets(var1);
         var2.set(var4.top, var4.left, var4.bottom, var4.right);
      }

      return var2;
   }
}
