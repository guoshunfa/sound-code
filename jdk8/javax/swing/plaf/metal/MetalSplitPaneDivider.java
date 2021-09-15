package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

class MetalSplitPaneDivider extends BasicSplitPaneDivider {
   private MetalBumps bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
   private MetalBumps focusBumps = new MetalBumps(10, 10, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), UIManager.getColor("SplitPane.dividerFocusColor"));
   private int inset = 2;
   private Color controlColor = MetalLookAndFeel.getControl();
   private Color primaryControlColor = UIManager.getColor("SplitPane.dividerFocusColor");

   public MetalSplitPaneDivider(BasicSplitPaneUI var1) {
      super(var1);
   }

   public void paint(Graphics var1) {
      MetalBumps var2;
      if (this.splitPane.hasFocus()) {
         var2 = this.focusBumps;
         var1.setColor(this.primaryControlColor);
      } else {
         var2 = this.bumps;
         var1.setColor(this.controlColor);
      }

      Rectangle var3 = var1.getClipBounds();
      Insets var4 = this.getInsets();
      var1.fillRect(var3.x, var3.y, var3.width, var3.height);
      Dimension var5 = this.getSize();
      var5.width -= this.inset * 2;
      var5.height -= this.inset * 2;
      int var6 = this.inset;
      int var7 = this.inset;
      if (var4 != null) {
         var5.width -= var4.left + var4.right;
         var5.height -= var4.top + var4.bottom;
         var6 += var4.left;
         var7 += var4.top;
      }

      var2.setBumpArea(var5);
      var2.paintIcon(this, var1, var6, var7);
      super.paint(var1);
   }

   protected JButton createLeftOneTouchButton() {
      JButton var1 = new JButton() {
         int[][] buffer = new int[][]{{0, 0, 0, 2, 2, 0, 0, 0, 0}, {0, 0, 2, 1, 1, 1, 0, 0, 0}, {0, 2, 1, 1, 1, 1, 1, 0, 0}, {2, 1, 1, 1, 1, 1, 1, 1, 0}, {0, 3, 3, 3, 3, 3, 3, 3, 3}};

         public void setBorder(Border var1) {
         }

         public void paint(Graphics var1) {
            JSplitPane var2 = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
            if (var2 != null) {
               int var3 = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
               int var4 = MetalSplitPaneDivider.this.getOrientationFromSuper();
               int var5 = Math.min(MetalSplitPaneDivider.this.getDividerSize(), var3);
               Color[] var6 = new Color[]{this.getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight()};
               var1.setColor(this.getBackground());
               if (this.isOpaque()) {
                  var1.fillRect(0, 0, this.getWidth(), this.getHeight());
               }

               if (this.getModel().isPressed()) {
                  var6[1] = var6[2];
               }

               int var7;
               int var8;
               if (var4 == 0) {
                  for(var7 = 1; var7 <= this.buffer[0].length; ++var7) {
                     for(var8 = 1; var8 < var5; ++var8) {
                        if (this.buffer[var8 - 1][var7 - 1] != 0) {
                           var1.setColor(var6[this.buffer[var8 - 1][var7 - 1]]);
                           var1.drawLine(var7, var8, var7, var8);
                        }
                     }
                  }
               } else {
                  for(var7 = 1; var7 <= this.buffer[0].length; ++var7) {
                     for(var8 = 1; var8 < var5; ++var8) {
                        if (this.buffer[var8 - 1][var7 - 1] != 0) {
                           var1.setColor(var6[this.buffer[var8 - 1][var7 - 1]]);
                           var1.drawLine(var8, var7, var8, var7);
                        }
                     }
                  }
               }
            }

         }

         public boolean isFocusTraversable() {
            return false;
         }
      };
      var1.setRequestFocusEnabled(false);
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setBorderPainted(false);
      this.maybeMakeButtonOpaque(var1);
      return var1;
   }

   private void maybeMakeButtonOpaque(JComponent var1) {
      Object var2 = UIManager.get("SplitPane.oneTouchButtonsOpaque");
      if (var2 != null) {
         var1.setOpaque((Boolean)var2);
      }

   }

   protected JButton createRightOneTouchButton() {
      JButton var1 = new JButton() {
         int[][] buffer = new int[][]{{2, 2, 2, 2, 2, 2, 2, 2}, {0, 1, 1, 1, 1, 1, 1, 3}, {0, 0, 1, 1, 1, 1, 3, 0}, {0, 0, 0, 1, 1, 3, 0, 0}, {0, 0, 0, 0, 3, 0, 0, 0}};

         public void setBorder(Border var1) {
         }

         public void paint(Graphics var1) {
            JSplitPane var2 = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
            if (var2 != null) {
               int var3 = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
               int var4 = MetalSplitPaneDivider.this.getOrientationFromSuper();
               int var5 = Math.min(MetalSplitPaneDivider.this.getDividerSize(), var3);
               Color[] var6 = new Color[]{this.getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight()};
               var1.setColor(this.getBackground());
               if (this.isOpaque()) {
                  var1.fillRect(0, 0, this.getWidth(), this.getHeight());
               }

               if (this.getModel().isPressed()) {
                  var6[1] = var6[2];
               }

               int var7;
               int var8;
               if (var4 == 0) {
                  for(var7 = 1; var7 <= this.buffer[0].length; ++var7) {
                     for(var8 = 1; var8 < var5; ++var8) {
                        if (this.buffer[var8 - 1][var7 - 1] != 0) {
                           var1.setColor(var6[this.buffer[var8 - 1][var7 - 1]]);
                           var1.drawLine(var7, var8, var7, var8);
                        }
                     }
                  }
               } else {
                  for(var7 = 1; var7 <= this.buffer[0].length; ++var7) {
                     for(var8 = 1; var8 < var5; ++var8) {
                        if (this.buffer[var8 - 1][var7 - 1] != 0) {
                           var1.setColor(var6[this.buffer[var8 - 1][var7 - 1]]);
                           var1.drawLine(var8, var7, var8, var7);
                        }
                     }
                  }
               }
            }

         }

         public boolean isFocusTraversable() {
            return false;
         }
      };
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setBorderPainted(false);
      var1.setRequestFocusEnabled(false);
      this.maybeMakeButtonOpaque(var1);
      return var1;
   }

   int getOneTouchSizeFromSuper() {
      return 6;
   }

   int getOneTouchOffsetFromSuper() {
      return 2;
   }

   int getOrientationFromSuper() {
      return super.orientation;
   }

   JSplitPane getSplitPaneFromSuper() {
      return super.splitPane;
   }

   JButton getLeftButtonFromSuper() {
      return super.leftButton;
   }

   JButton getRightButtonFromSuper() {
      return super.rightButton;
   }

   public class MetalDividerLayout implements LayoutManager {
      public void layoutContainer(Container var1) {
         JButton var2 = MetalSplitPaneDivider.this.getLeftButtonFromSuper();
         JButton var3 = MetalSplitPaneDivider.this.getRightButtonFromSuper();
         JSplitPane var4 = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
         int var5 = MetalSplitPaneDivider.this.getOrientationFromSuper();
         int var6 = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
         int var7 = MetalSplitPaneDivider.this.getOneTouchOffsetFromSuper();
         Insets var8 = MetalSplitPaneDivider.this.getInsets();
         if (var2 != null && var3 != null && var1 == MetalSplitPaneDivider.this) {
            if (var4.isOneTouchExpandable()) {
               int var9;
               int var10;
               if (var5 == 0) {
                  var9 = var8 != null ? var8.top : 0;
                  var10 = MetalSplitPaneDivider.this.getDividerSize();
                  if (var8 != null) {
                     var10 -= var8.top + var8.bottom;
                  }

                  var10 = Math.min(var10, var6);
                  var2.setBounds(var7, var9, var10 * 2, var10);
                  var3.setBounds(var7 + var6 * 2, var9, var10 * 2, var10);
               } else {
                  var9 = MetalSplitPaneDivider.this.getDividerSize();
                  var10 = var8 != null ? var8.left : 0;
                  if (var8 != null) {
                     var9 -= var8.left + var8.right;
                  }

                  var9 = Math.min(var9, var6);
                  var2.setBounds(var10, var7, var9, var9 * 2);
                  var3.setBounds(var10, var7 + var6 * 2, var9, var9 * 2);
               }
            } else {
               var2.setBounds(-5, -5, 1, 1);
               var3.setBounds(-5, -5, 1, 1);
            }
         }

      }

      public Dimension minimumLayoutSize(Container var1) {
         return new Dimension(0, 0);
      }

      public Dimension preferredLayoutSize(Container var1) {
         return new Dimension(0, 0);
      }

      public void removeLayoutComponent(Component var1) {
      }

      public void addLayoutComponent(String var1, Component var2) {
      }
   }
}
