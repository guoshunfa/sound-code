package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.border.Border;

public class ScrollPaneLayout implements LayoutManager, ScrollPaneConstants, Serializable {
   protected JViewport viewport;
   protected JScrollBar vsb;
   protected JScrollBar hsb;
   protected JViewport rowHead;
   protected JViewport colHead;
   protected Component lowerLeft;
   protected Component lowerRight;
   protected Component upperLeft;
   protected Component upperRight;
   protected int vsbPolicy = 20;
   protected int hsbPolicy = 30;

   public void syncWithScrollPane(JScrollPane var1) {
      this.viewport = var1.getViewport();
      this.vsb = var1.getVerticalScrollBar();
      this.hsb = var1.getHorizontalScrollBar();
      this.rowHead = var1.getRowHeader();
      this.colHead = var1.getColumnHeader();
      this.lowerLeft = var1.getCorner("LOWER_LEFT_CORNER");
      this.lowerRight = var1.getCorner("LOWER_RIGHT_CORNER");
      this.upperLeft = var1.getCorner("UPPER_LEFT_CORNER");
      this.upperRight = var1.getCorner("UPPER_RIGHT_CORNER");
      this.vsbPolicy = var1.getVerticalScrollBarPolicy();
      this.hsbPolicy = var1.getHorizontalScrollBarPolicy();
   }

   protected Component addSingletonComponent(Component var1, Component var2) {
      if (var1 != null && var1 != var2) {
         var1.getParent().remove(var1);
      }

      return var2;
   }

   public void addLayoutComponent(String var1, Component var2) {
      if (var1.equals("VIEWPORT")) {
         this.viewport = (JViewport)this.addSingletonComponent(this.viewport, var2);
      } else if (var1.equals("VERTICAL_SCROLLBAR")) {
         this.vsb = (JScrollBar)this.addSingletonComponent(this.vsb, var2);
      } else if (var1.equals("HORIZONTAL_SCROLLBAR")) {
         this.hsb = (JScrollBar)this.addSingletonComponent(this.hsb, var2);
      } else if (var1.equals("ROW_HEADER")) {
         this.rowHead = (JViewport)this.addSingletonComponent(this.rowHead, var2);
      } else if (var1.equals("COLUMN_HEADER")) {
         this.colHead = (JViewport)this.addSingletonComponent(this.colHead, var2);
      } else if (var1.equals("LOWER_LEFT_CORNER")) {
         this.lowerLeft = this.addSingletonComponent(this.lowerLeft, var2);
      } else if (var1.equals("LOWER_RIGHT_CORNER")) {
         this.lowerRight = this.addSingletonComponent(this.lowerRight, var2);
      } else if (var1.equals("UPPER_LEFT_CORNER")) {
         this.upperLeft = this.addSingletonComponent(this.upperLeft, var2);
      } else {
         if (!var1.equals("UPPER_RIGHT_CORNER")) {
            throw new IllegalArgumentException("invalid layout key " + var1);
         }

         this.upperRight = this.addSingletonComponent(this.upperRight, var2);
      }

   }

   public void removeLayoutComponent(Component var1) {
      if (var1 == this.viewport) {
         this.viewport = null;
      } else if (var1 == this.vsb) {
         this.vsb = null;
      } else if (var1 == this.hsb) {
         this.hsb = null;
      } else if (var1 == this.rowHead) {
         this.rowHead = null;
      } else if (var1 == this.colHead) {
         this.colHead = null;
      } else if (var1 == this.lowerLeft) {
         this.lowerLeft = null;
      } else if (var1 == this.lowerRight) {
         this.lowerRight = null;
      } else if (var1 == this.upperLeft) {
         this.upperLeft = null;
      } else if (var1 == this.upperRight) {
         this.upperRight = null;
      }

   }

   public int getVerticalScrollBarPolicy() {
      return this.vsbPolicy;
   }

   public void setVerticalScrollBarPolicy(int var1) {
      switch(var1) {
      case 20:
      case 21:
      case 22:
         this.vsbPolicy = var1;
         return;
      default:
         throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
      }
   }

   public int getHorizontalScrollBarPolicy() {
      return this.hsbPolicy;
   }

   public void setHorizontalScrollBarPolicy(int var1) {
      switch(var1) {
      case 30:
      case 31:
      case 32:
         this.hsbPolicy = var1;
         return;
      default:
         throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
      }
   }

   public JViewport getViewport() {
      return this.viewport;
   }

   public JScrollBar getHorizontalScrollBar() {
      return this.hsb;
   }

   public JScrollBar getVerticalScrollBar() {
      return this.vsb;
   }

   public JViewport getRowHeader() {
      return this.rowHead;
   }

   public JViewport getColumnHeader() {
      return this.colHead;
   }

   public Component getCorner(String var1) {
      if (var1.equals("LOWER_LEFT_CORNER")) {
         return this.lowerLeft;
      } else if (var1.equals("LOWER_RIGHT_CORNER")) {
         return this.lowerRight;
      } else if (var1.equals("UPPER_LEFT_CORNER")) {
         return this.upperLeft;
      } else {
         return var1.equals("UPPER_RIGHT_CORNER") ? this.upperRight : null;
      }
   }

   public Dimension preferredLayoutSize(Container var1) {
      JScrollPane var2 = (JScrollPane)var1;
      this.vsbPolicy = var2.getVerticalScrollBarPolicy();
      this.hsbPolicy = var2.getHorizontalScrollBarPolicy();
      Insets var3 = var1.getInsets();
      int var4 = var3.left + var3.right;
      int var5 = var3.top + var3.bottom;
      Dimension var6 = null;
      Dimension var7 = null;
      Component var8 = null;
      if (this.viewport != null) {
         var6 = this.viewport.getPreferredSize();
         var8 = this.viewport.getView();
         if (var8 != null) {
            var7 = var8.getPreferredSize();
         } else {
            var7 = new Dimension(0, 0);
         }
      }

      if (var6 != null) {
         var4 += var6.width;
         var5 += var6.height;
      }

      Border var9 = var2.getViewportBorder();
      if (var9 != null) {
         Insets var10 = var9.getBorderInsets(var1);
         var4 += var10.left + var10.right;
         var5 += var10.top + var10.bottom;
      }

      if (this.rowHead != null && this.rowHead.isVisible()) {
         var4 += this.rowHead.getPreferredSize().width;
      }

      if (this.colHead != null && this.colHead.isVisible()) {
         var5 += this.colHead.getPreferredSize().height;
      }

      boolean var11;
      if (this.vsb != null && this.vsbPolicy != 21) {
         if (this.vsbPolicy == 22) {
            var4 += this.vsb.getPreferredSize().width;
         } else if (var7 != null && var6 != null) {
            var11 = true;
            if (var8 instanceof Scrollable) {
               var11 = !((Scrollable)var8).getScrollableTracksViewportHeight();
            }

            if (var11 && var7.height > var6.height) {
               var4 += this.vsb.getPreferredSize().width;
            }
         }
      }

      if (this.hsb != null && this.hsbPolicy != 31) {
         if (this.hsbPolicy == 32) {
            var5 += this.hsb.getPreferredSize().height;
         } else if (var7 != null && var6 != null) {
            var11 = true;
            if (var8 instanceof Scrollable) {
               var11 = !((Scrollable)var8).getScrollableTracksViewportWidth();
            }

            if (var11 && var7.width > var6.width) {
               var5 += this.hsb.getPreferredSize().height;
            }
         }
      }

      return new Dimension(var4, var5);
   }

   public Dimension minimumLayoutSize(Container var1) {
      JScrollPane var2 = (JScrollPane)var1;
      this.vsbPolicy = var2.getVerticalScrollBarPolicy();
      this.hsbPolicy = var2.getHorizontalScrollBarPolicy();
      Insets var3 = var1.getInsets();
      int var4 = var3.left + var3.right;
      int var5 = var3.top + var3.bottom;
      if (this.viewport != null) {
         Dimension var6 = this.viewport.getMinimumSize();
         var4 += var6.width;
         var5 += var6.height;
      }

      Border var8 = var2.getViewportBorder();
      if (var8 != null) {
         Insets var7 = var8.getBorderInsets(var1);
         var4 += var7.left + var7.right;
         var5 += var7.top + var7.bottom;
      }

      Dimension var9;
      if (this.rowHead != null && this.rowHead.isVisible()) {
         var9 = this.rowHead.getMinimumSize();
         var4 += var9.width;
         var5 = Math.max(var5, var9.height);
      }

      if (this.colHead != null && this.colHead.isVisible()) {
         var9 = this.colHead.getMinimumSize();
         var4 = Math.max(var4, var9.width);
         var5 += var9.height;
      }

      if (this.vsb != null && this.vsbPolicy != 21) {
         var9 = this.vsb.getMinimumSize();
         var4 += var9.width;
         var5 = Math.max(var5, var9.height);
      }

      if (this.hsb != null && this.hsbPolicy != 31) {
         var9 = this.hsb.getMinimumSize();
         var4 = Math.max(var4, var9.width);
         var5 += var9.height;
      }

      return new Dimension(var4, var5);
   }

   public void layoutContainer(Container var1) {
      JScrollPane var2 = (JScrollPane)var1;
      this.vsbPolicy = var2.getVerticalScrollBarPolicy();
      this.hsbPolicy = var2.getHorizontalScrollBarPolicy();
      Rectangle var3 = var2.getBounds();
      var3.x = var3.y = 0;
      Insets var4 = var1.getInsets();
      var3.x = var4.left;
      var3.y = var4.top;
      var3.width -= var4.left + var4.right;
      var3.height -= var4.top + var4.bottom;
      boolean var5 = SwingUtilities.isLeftToRight(var2);
      Rectangle var6 = new Rectangle(0, var3.y, 0, 0);
      if (this.colHead != null && this.colHead.isVisible()) {
         int var7 = Math.min(var3.height, this.colHead.getPreferredSize().height);
         var6.height = var7;
         var3.y += var7;
         var3.height -= var7;
      }

      Rectangle var24 = new Rectangle(0, 0, 0, 0);
      if (this.rowHead != null && this.rowHead.isVisible()) {
         int var8 = Math.min(var3.width, this.rowHead.getPreferredSize().width);
         var24.width = var8;
         var3.width -= var8;
         if (var5) {
            var24.x = var3.x;
            var3.x += var8;
         } else {
            var24.x = var3.x + var3.width;
         }
      }

      Border var25 = var2.getViewportBorder();
      Insets var9;
      if (var25 != null) {
         var9 = var25.getBorderInsets(var1);
         var3.x += var9.left;
         var3.y += var9.top;
         var3.width -= var9.left + var9.right;
         var3.height -= var9.top + var9.bottom;
      } else {
         var9 = new Insets(0, 0, 0, 0);
      }

      Component var10 = this.viewport != null ? this.viewport.getView() : null;
      Dimension var11 = var10 != null ? var10.getPreferredSize() : new Dimension(0, 0);
      Dimension var12 = this.viewport != null ? this.viewport.toViewCoordinates(var3.getSize()) : new Dimension(0, 0);
      boolean var13 = false;
      boolean var14 = false;
      boolean var15 = var3.width < 0 || var3.height < 0;
      Scrollable var16;
      if (!var15 && var10 instanceof Scrollable) {
         var16 = (Scrollable)var10;
         var13 = var16.getScrollableTracksViewportWidth();
         var14 = var16.getScrollableTracksViewportHeight();
      } else {
         var16 = null;
      }

      Rectangle var17 = new Rectangle(0, var3.y - var9.top, 0, 0);
      boolean var18;
      if (var15) {
         var18 = false;
      } else if (this.vsbPolicy == 22) {
         var18 = true;
      } else if (this.vsbPolicy == 21) {
         var18 = false;
      } else {
         var18 = !var14 && var11.height > var12.height;
      }

      if (this.vsb != null && var18) {
         this.adjustForVSB(true, var3, var17, var9, var5);
         var12 = this.viewport.toViewCoordinates(var3.getSize());
      }

      Rectangle var19 = new Rectangle(var3.x - var9.left, 0, 0, 0);
      boolean var20;
      if (var15) {
         var20 = false;
      } else if (this.hsbPolicy == 32) {
         var20 = true;
      } else if (this.hsbPolicy == 31) {
         var20 = false;
      } else {
         var20 = !var13 && var11.width > var12.width;
      }

      if (this.hsb != null && var20) {
         this.adjustForHSB(true, var3, var19, var9);
         if (this.vsb != null && !var18 && this.vsbPolicy != 21) {
            var12 = this.viewport.toViewCoordinates(var3.getSize());
            var18 = var11.height > var12.height;
            if (var18) {
               this.adjustForVSB(true, var3, var17, var9, var5);
            }
         }
      }

      if (this.viewport != null) {
         this.viewport.setBounds(var3);
         if (var16 != null) {
            var12 = this.viewport.toViewCoordinates(var3.getSize());
            var13 = var16.getScrollableTracksViewportWidth();
            var14 = var16.getScrollableTracksViewportHeight();
            boolean var23;
            if (this.vsb != null && this.vsbPolicy == 20) {
               var23 = !var14 && var11.height > var12.height;
               if (var23 != var18) {
                  var18 = var23;
                  this.adjustForVSB(var23, var3, var17, var9, var5);
                  var12 = this.viewport.toViewCoordinates(var3.getSize());
               }
            }

            if (this.hsb != null && this.hsbPolicy == 30) {
               var23 = !var13 && var11.width > var12.width;
               if (var23 != var20) {
                  var20 = var23;
                  this.adjustForHSB(var23, var3, var19, var9);
                  if (this.vsb != null && !var18 && this.vsbPolicy != 21) {
                     var12 = this.viewport.toViewCoordinates(var3.getSize());
                     var18 = var11.height > var12.height;
                     if (var18) {
                        this.adjustForVSB(true, var3, var17, var9, var5);
                     }
                  }
               }
            }

            if (var20 != var20 || var18 != var18) {
               this.viewport.setBounds(var3);
            }
         }
      }

      var17.height = var3.height + var9.top + var9.bottom;
      var19.width = var3.width + var9.left + var9.right;
      var24.height = var3.height + var9.top + var9.bottom;
      var24.y = var3.y - var9.top;
      var6.width = var3.width + var9.left + var9.right;
      var6.x = var3.x - var9.left;
      if (this.rowHead != null) {
         this.rowHead.setBounds(var24);
      }

      if (this.colHead != null) {
         this.colHead.setBounds(var6);
      }

      if (this.vsb != null) {
         if (var18) {
            if (this.colHead != null && UIManager.getBoolean("ScrollPane.fillUpperCorner") && (var5 && this.upperRight == null || !var5 && this.upperLeft == null)) {
               var17.y = var6.y;
               var17.height += var6.height;
            }

            this.vsb.setVisible(true);
            this.vsb.setBounds(var17);
         } else {
            this.vsb.setVisible(false);
         }
      }

      if (this.hsb != null) {
         if (var20) {
            if (this.rowHead != null && UIManager.getBoolean("ScrollPane.fillLowerCorner") && (var5 && this.lowerLeft == null || !var5 && this.lowerRight == null)) {
               if (var5) {
                  var19.x = var24.x;
               }

               var19.width += var24.width;
            }

            this.hsb.setVisible(true);
            this.hsb.setBounds(var19);
         } else {
            this.hsb.setVisible(false);
         }
      }

      if (this.lowerLeft != null) {
         this.lowerLeft.setBounds(var5 ? var24.x : var17.x, var19.y, var5 ? var24.width : var17.width, var19.height);
      }

      if (this.lowerRight != null) {
         this.lowerRight.setBounds(var5 ? var17.x : var24.x, var19.y, var5 ? var17.width : var24.width, var19.height);
      }

      if (this.upperLeft != null) {
         this.upperLeft.setBounds(var5 ? var24.x : var17.x, var6.y, var5 ? var24.width : var17.width, var6.height);
      }

      if (this.upperRight != null) {
         this.upperRight.setBounds(var5 ? var17.x : var24.x, var6.y, var5 ? var17.width : var24.width, var6.height);
      }

   }

   private void adjustForVSB(boolean var1, Rectangle var2, Rectangle var3, Insets var4, boolean var5) {
      int var6 = var3.width;
      if (var1) {
         int var7 = Math.max(0, Math.min(this.vsb.getPreferredSize().width, var2.width));
         var2.width -= var7;
         var3.width = var7;
         if (var5) {
            var3.x = var2.x + var2.width + var4.right;
         } else {
            var3.x = var2.x - var4.left;
            var2.x += var7;
         }
      } else {
         var2.width += var6;
      }

   }

   private void adjustForHSB(boolean var1, Rectangle var2, Rectangle var3, Insets var4) {
      int var5 = var3.height;
      if (var1) {
         int var6 = Math.max(0, Math.min(var2.height, this.hsb.getPreferredSize().height));
         var2.height -= var6;
         var3.y = var2.y + var2.height + var4.bottom;
         var3.height = var6;
      } else {
         var2.height += var5;
      }

   }

   /** @deprecated */
   @Deprecated
   public Rectangle getViewportBorderBounds(JScrollPane var1) {
      return var1.getViewportBorderBounds();
   }

   public static class UIResource extends ScrollPaneLayout implements javax.swing.plaf.UIResource {
   }
}
