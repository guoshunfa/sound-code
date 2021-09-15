package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import sun.swing.SwingUtilities2;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class WindowsTableHeaderUI extends BasicTableHeaderUI {
   private TableCellRenderer originalHeaderRenderer;

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsTableHeaderUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      if (XPStyle.getXP() != null) {
         this.originalHeaderRenderer = this.header.getDefaultRenderer();
         if (this.originalHeaderRenderer instanceof UIResource) {
            this.header.setDefaultRenderer(new WindowsTableHeaderUI.XPDefaultRenderer());
         }
      }

   }

   public void uninstallUI(JComponent var1) {
      if (this.header.getDefaultRenderer() instanceof WindowsTableHeaderUI.XPDefaultRenderer) {
         this.header.setDefaultRenderer(this.originalHeaderRenderer);
      }

      super.uninstallUI(var1);
   }

   protected void rolloverColumnUpdated(int var1, int var2) {
      if (XPStyle.getXP() != null) {
         this.header.repaint(this.header.getHeaderRect(var1));
         this.header.repaint(this.header.getHeaderRect(var2));
      }

   }

   private static class IconBorder implements Border, UIResource {
      private final Icon icon;
      private final int top;
      private final int left;
      private final int bottom;
      private final int right;

      public IconBorder(Icon var1, int var2, int var3, int var4, int var5) {
         this.icon = var1;
         this.top = var2;
         this.left = var3;
         this.bottom = var4;
         this.right = var5;
      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(this.icon.getIconHeight() + this.top, this.left, this.bottom, this.right);
      }

      public boolean isBorderOpaque() {
         return false;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         this.icon.paintIcon(var1, var2, var3 + this.left + (var5 - this.left - this.right - this.icon.getIconWidth()) / 2, var4 + this.top);
      }
   }

   private class XPDefaultRenderer extends DefaultTableCellHeaderRenderer {
      XPStyle.Skin skin;
      boolean isSelected;
      boolean hasFocus;
      boolean hasRollover;
      int column;

      XPDefaultRenderer() {
         this.setHorizontalAlignment(10);
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         super.getTableCellRendererComponent(var1, var2, var3, var4, var5, var6);
         this.isSelected = var3;
         this.hasFocus = var4;
         this.column = var6;
         this.hasRollover = var6 == WindowsTableHeaderUI.this.getRolloverColumn();
         if (this.skin == null) {
            XPStyle var7 = XPStyle.getXP();
            this.skin = var7 != null ? var7.getSkin(WindowsTableHeaderUI.this.header, TMSchema.Part.HP_HEADERITEM) : null;
         }

         Insets var16 = this.skin != null ? this.skin.getContentMargin() : null;
         Object var8 = null;
         int var9 = 0;
         int var10 = 0;
         int var11 = 0;
         int var12 = 0;
         if (var16 != null) {
            var9 = var16.top;
            var10 = var16.left;
            var11 = var16.bottom;
            var12 = var16.right;
         }

         var10 += 5;
         var11 += 4;
         var12 += 5;
         Icon var13;
         if (!WindowsLookAndFeel.isOnVista() || !((var13 = this.getIcon()) instanceof UIResource) && var13 != null) {
            var9 += 3;
            var8 = new EmptyBorder(var9, var10, var11, var12);
         } else {
            ++var9;
            this.setIcon((Icon)null);
            var13 = null;
            SortOrder var14 = getColumnSortOrder(var1, var6);
            if (var14 != null) {
               switch(var14) {
               case ASCENDING:
                  var13 = UIManager.getIcon("Table.ascendingSortIcon");
                  break;
               case DESCENDING:
                  var13 = UIManager.getIcon("Table.descendingSortIcon");
               }
            }

            if (var13 != null) {
               var11 = var13.getIconHeight();
               var8 = new WindowsTableHeaderUI.IconBorder(var13, var9, var10, var11, var12);
            } else {
               var13 = UIManager.getIcon("Table.ascendingSortIcon");
               int var15 = var13 != null ? var13.getIconHeight() : 0;
               if (var15 != 0) {
                  var11 = var15;
               }

               var8 = new EmptyBorder(var15 + var9, var10, var11, var12);
            }
         }

         this.setBorder((Border)var8);
         return this;
      }

      public void paint(Graphics var1) {
         Dimension var2 = this.getSize();
         TMSchema.State var3 = TMSchema.State.NORMAL;
         TableColumn var4 = WindowsTableHeaderUI.this.header.getDraggedColumn();
         if (var4 != null && this.column == SwingUtilities2.convertColumnIndexToView(WindowsTableHeaderUI.this.header.getColumnModel(), var4.getModelIndex())) {
            var3 = TMSchema.State.PRESSED;
         } else if (this.isSelected || this.hasFocus || this.hasRollover) {
            var3 = TMSchema.State.HOT;
         }

         if (WindowsLookAndFeel.isOnVista()) {
            SortOrder var5 = getColumnSortOrder(WindowsTableHeaderUI.this.header.getTable(), this.column);
            if (var5 != null) {
               switch(var5) {
               case ASCENDING:
               case DESCENDING:
                  switch(var3) {
                  case NORMAL:
                     var3 = TMSchema.State.SORTEDNORMAL;
                     break;
                  case PRESSED:
                     var3 = TMSchema.State.SORTEDPRESSED;
                     break;
                  case HOT:
                     var3 = TMSchema.State.SORTEDHOT;
                  }
               }
            }
         }

         this.skin.paintSkin(var1, 0, 0, var2.width - 1, var2.height - 1, var3);
         super.paint(var1);
      }
   }
}
