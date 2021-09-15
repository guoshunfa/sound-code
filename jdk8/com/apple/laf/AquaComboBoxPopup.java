package com.apple.laf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboPopup;

class AquaComboBoxPopup extends BasicComboPopup {
   static final int FOCUS_RING_PAD_LEFT = 6;
   static final int FOCUS_RING_PAD_RIGHT = 6;
   static final int FOCUS_RING_PAD_BOTTOM = 5;
   protected Component topStrut;
   protected Component bottomStrut;
   protected boolean isPopDown = false;

   public AquaComboBoxPopup(JComboBox var1) {
      super(var1);
   }

   protected void configurePopup() {
      super.configurePopup();
      this.setBorderPainted(false);
      this.setBorder((Border)null);
      this.updateContents(false);
      this.putClientProperty("apple.awt._windowFadeOut", new Integer(150));
   }

   public void updateContents(boolean var1) {
      this.isPopDown = this.isPopdown();
      if (this.isPopDown) {
         if (var1) {
            if (this.topStrut != null) {
               this.remove(this.topStrut);
            }

            if (this.bottomStrut != null) {
               this.remove(this.bottomStrut);
            }
         } else {
            this.add(this.scroller);
         }
      } else {
         if (this.topStrut == null) {
            this.topStrut = Box.createVerticalStrut(4);
            this.bottomStrut = Box.createVerticalStrut(4);
         }

         if (var1) {
            this.remove(this.scroller);
         }

         this.add(this.topStrut);
         this.add(this.scroller);
         this.add(this.bottomStrut);
      }

   }

   protected Dimension getBestPopupSizeForRowCount(int var1) {
      int var2 = this.comboBox.getModel().getSize();
      int var3 = Math.min(var1, var2);
      Dimension var4 = new Dimension();
      ListCellRenderer var5 = this.list.getCellRenderer();

      for(int var6 = 0; var6 < var3; ++var6) {
         Object var7 = this.list.getModel().getElementAt(var6);
         Component var8 = var5.getListCellRendererComponent(this.list, var7, var6, false, false);
         Dimension var9 = var8.getPreferredSize();
         var4.height += var9.height;
         var4.width = Math.max(var9.width, var4.width);
      }

      var4.width += 10;
      return var4;
   }

   protected boolean shouldScroll() {
      return this.comboBox.getItemCount() > this.comboBox.getMaximumRowCount();
   }

   protected boolean isPopdown() {
      return this.shouldScroll() || AquaComboBoxUI.isPopdown(this.comboBox);
   }

   public void show() {
      int var1 = this.comboBox.getItemCount();
      Rectangle var2 = this.adjustPopupAndGetBounds();
      if (var2 != null) {
         this.comboBox.firePopupMenuWillBecomeVisible();
         this.show(this.comboBox, var2.x, var2.y);
         int var3 = this.comboBox.getItemCount();
         if (var3 == 0) {
            this.hide();
         } else {
            if (var1 != var3) {
               Rectangle var4 = this.adjustPopupAndGetBounds();
               this.list.setSize(var4.width, var4.height);
               this.pack();
               Point var5 = this.comboBox.getLocationOnScreen();
               this.setLocation(var5.x + var4.x, var5.y + var4.y);
            }

            this.list.requestFocusInWindow();
         }
      }
   }

   protected JList createList() {
      return new JList(this.comboBox.getModel()) {
         public void processMouseEvent(MouseEvent var1) {
            if (var1.isMetaDown()) {
               var1 = new MouseEvent((Component)var1.getSource(), var1.getID(), var1.getWhen(), var1.getModifiers() ^ 4, var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
            }

            super.processMouseEvent(var1);
         }
      };
   }

   protected Rectangle adjustPopupAndGetBounds() {
      if (this.isPopDown != this.isPopdown()) {
         this.updateContents(true);
      }

      Dimension var1 = this.getBestPopupSizeForRowCount(this.comboBox.getMaximumRowCount());
      Rectangle var2 = this.computePopupBounds(0, this.comboBox.getBounds().height, var1.width, var1.height);
      if (var2 == null) {
         return null;
      } else {
         Dimension var3 = var2.getSize();
         this.scroller.setMaximumSize(var3);
         this.scroller.setPreferredSize(var3);
         this.scroller.setMinimumSize(var3);
         this.list.invalidate();
         int var4 = this.comboBox.getSelectedIndex();
         if (var4 == -1) {
            this.list.clearSelection();
         } else {
            this.list.setSelectedIndex(var4);
         }

         this.list.ensureIndexIsVisible(this.list.getSelectedIndex());
         return var2;
      }
   }

   Rectangle getBestScreenBounds(Point var1) {
      GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] var3 = var2.getScreenDevices();
      Rectangle var4 = this.comboBox.getBounds();
      if (var3.length == 1) {
         Dimension var15 = Toolkit.getDefaultToolkit().getScreenSize();
         return var1.x + var4.width >= 0 && var1.y + var4.height >= 0 && var1.x <= var15.width && var1.y <= var15.height ? new Rectangle(0, 22, var15.width, var15.height - 22) : null;
      } else {
         GraphicsDevice[] var5 = var3;
         int var6 = var3.length;

         int var7;
         GraphicsDevice var8;
         GraphicsConfiguration[] var9;
         GraphicsConfiguration[] var10;
         int var11;
         int var12;
         GraphicsConfiguration var13;
         Rectangle var14;
         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            var9 = var8.getConfigurations();
            var10 = var9;
            var11 = var9.length;

            for(var12 = 0; var12 < var11; ++var12) {
               var13 = var10[var12];
               var14 = var13.getBounds();
               if (var14.contains(var1)) {
                  return var14;
               }
            }
         }

         var4.setLocation(var1);
         var5 = var3;
         var6 = var3.length;

         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            var9 = var8.getConfigurations();
            var10 = var9;
            var11 = var9.length;

            for(var12 = 0; var12 < var11; ++var12) {
               var13 = var10[var12];
               var14 = var13.getBounds();
               if (var14.intersects(var4)) {
                  return var14;
               }
            }
         }

         return null;
      }
   }

   protected Rectangle computePopupBounds(int var1, int var2, int var3, int var4) {
      int var5 = this.comboBox.getModel().getSize();
      boolean var6 = this.isPopdown();
      boolean var7 = AquaComboBoxUI.isTableCellEditor(this.comboBox);
      if (var6 && !var7) {
         var2 = Math.min(var2 / 2 + 9, var2);
      }

      Point var8 = new Point(0, 0);
      SwingUtilities.convertPointToScreen(var8, this.comboBox);
      Rectangle var9 = this.getBestScreenBounds(var8);
      if (var9 == null) {
         return super.computePopupBounds(var1, var2, var3, var4);
      } else {
         Insets var10 = this.comboBox.getInsets();
         Rectangle var11 = this.comboBox.getBounds();
         if (this.shouldScroll()) {
            var3 += 15;
         }

         if (var6) {
            var3 += 4;
         }

         int var12 = var11.width - (var10.left + var10.right);
         var3 = Math.max(var12, var3);
         boolean var13 = AquaUtils.isLeftToRight(this.comboBox);
         if (var13) {
            var1 += var10.left;
            if (!this.isPopDown) {
               var1 -= 6;
            }
         } else {
            var1 = var11.width - var3 - var10.right;
            if (!this.isPopDown) {
               var1 += 6;
            }
         }

         var2 -= var10.bottom;
         var8.x += var1;
         var8.y += var2;
         if (var8.x < var9.x) {
            var1 -= var8.x + var9.x;
         }

         if (var8.y < var9.y) {
            var2 -= var8.y + var9.y;
         }

         Point var14 = new Point(0, 0);
         SwingUtilities.convertPointFromScreen(var14, this.comboBox);
         int var15 = Math.min(var9.width, var14.x + var9.x + var9.width) - 2;
         var3 = Math.min(var15, var3);
         if (var3 < var12) {
            var1 -= var12 - var3;
            var3 = var12;
         }

         if (!var6) {
            var3 -= 6;
            return this.computePopupBoundsForMenu(var1, var2, var3, var4, var5, var9);
         } else {
            if (!var7) {
               var3 -= 12;
               if (var13) {
                  var1 += 6;
               }
            }

            Rectangle var16 = new Rectangle(var1, var2, var3, var4);
            return var16.y + var16.height < var14.y + var9.y + var9.height ? var16 : new Rectangle(var1, -var16.height + var10.top, var16.width, var16.height);
         }
      }
   }

   protected Rectangle computePopupBoundsForMenu(int var1, int var2, int var3, int var4, int var5, Rectangle var6) {
      int var7 = 0;
      if (this.list != null && var5 > 0) {
         Rectangle var8 = this.list.getCellBounds(0, 0);
         if (var8 != null) {
            var7 = var8.height;
         }
      }

      int var20 = this.comboBox.getSelectedIndex();
      if (var20 < 0) {
         var20 = 0;
      }

      this.list.setSelectedIndex(var20);
      int var9 = var7 * var20;
      Point var10 = new Point(0, var6.y);
      Point var11 = new Point(0, var6.y + var6.height - 20);
      SwingUtilities.convertPointFromScreen(var10, this.comboBox);
      SwingUtilities.convertPointFromScreen(var11, this.comboBox);
      Rectangle var12 = new Rectangle(var1, var2, var3, var4);
      int var13 = var4 - var9;
      boolean var14 = var9 > -var10.y;
      boolean var15 = var13 > var11.y;
      if (var14) {
         var12.y = var10.y + 1;
         var12.y = var12.y / var7 * var7;
      } else if (var15) {
         var12.y = var11.y - var12.height;
      } else {
         var12.y = -var9;
      }

      int var16 = this.comboBox.getHeight();
      Insets var17 = this.comboBox.getInsets();
      int var18 = var16 - (var17.top + var17.bottom);
      int var19 = (var18 - var7) / 2 + var17.top;
      var12.y += var19 - 5;
      return var12;
   }
}
