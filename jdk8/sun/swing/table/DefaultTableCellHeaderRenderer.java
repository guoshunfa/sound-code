package sun.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import sun.swing.DefaultLookup;

public class DefaultTableCellHeaderRenderer extends DefaultTableCellRenderer implements javax.swing.plaf.UIResource {
   private boolean horizontalTextPositionSet;
   private Icon sortArrow;
   private DefaultTableCellHeaderRenderer.EmptyIcon emptyIcon = new DefaultTableCellHeaderRenderer.EmptyIcon();

   public DefaultTableCellHeaderRenderer() {
      this.setHorizontalAlignment(0);
   }

   public void setHorizontalTextPosition(int var1) {
      this.horizontalTextPositionSet = true;
      super.setHorizontalTextPosition(var1);
   }

   public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
      Icon var7 = null;
      boolean var8 = false;
      if (var1 != null) {
         JTableHeader var9 = var1.getTableHeader();
         if (var9 != null) {
            Color var10 = null;
            Color var11 = null;
            if (var4) {
               var10 = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellForeground");
               var11 = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellBackground");
            }

            if (var10 == null) {
               var10 = var9.getForeground();
            }

            if (var11 == null) {
               var11 = var9.getBackground();
            }

            this.setForeground(var10);
            this.setBackground(var11);
            this.setFont(var9.getFont());
            var8 = var9.isPaintingForPrint();
         }

         if (!var8 && var1.getRowSorter() != null) {
            if (!this.horizontalTextPositionSet) {
               this.setHorizontalTextPosition(10);
            }

            SortOrder var12 = getColumnSortOrder(var1, var6);
            if (var12 != null) {
               switch(var12) {
               case ASCENDING:
                  var7 = DefaultLookup.getIcon(this, this.ui, "Table.ascendingSortIcon");
                  break;
               case DESCENDING:
                  var7 = DefaultLookup.getIcon(this, this.ui, "Table.descendingSortIcon");
                  break;
               case UNSORTED:
                  var7 = DefaultLookup.getIcon(this, this.ui, "Table.naturalSortIcon");
               }
            }
         }
      }

      this.setText(var2 == null ? "" : var2.toString());
      this.setIcon(var7);
      this.sortArrow = var7;
      Border var13 = null;
      if (var4) {
         var13 = DefaultLookup.getBorder(this, this.ui, "TableHeader.focusCellBorder");
      }

      if (var13 == null) {
         var13 = DefaultLookup.getBorder(this, this.ui, "TableHeader.cellBorder");
      }

      this.setBorder(var13);
      return this;
   }

   public static SortOrder getColumnSortOrder(JTable var0, int var1) {
      SortOrder var2 = null;
      if (var0 != null && var0.getRowSorter() != null) {
         List var3 = var0.getRowSorter().getSortKeys();
         if (var3.size() > 0 && ((RowSorter.SortKey)var3.get(0)).getColumn() == var0.convertColumnIndexToModel(var1)) {
            var2 = ((RowSorter.SortKey)var3.get(0)).getSortOrder();
         }

         return var2;
      } else {
         return var2;
      }
   }

   public void paintComponent(Graphics var1) {
      boolean var2 = DefaultLookup.getBoolean(this, this.ui, "TableHeader.rightAlignSortArrow", false);
      if (var2 && this.sortArrow != null) {
         this.emptyIcon.width = this.sortArrow.getIconWidth();
         this.emptyIcon.height = this.sortArrow.getIconHeight();
         this.setIcon(this.emptyIcon);
         super.paintComponent(var1);
         Point var3 = this.computeIconPosition(var1);
         this.sortArrow.paintIcon(this, var1, var3.x, var3.y);
      } else {
         super.paintComponent(var1);
      }

   }

   private Point computeIconPosition(Graphics var1) {
      FontMetrics var2 = var1.getFontMetrics();
      Rectangle var3 = new Rectangle();
      Rectangle var4 = new Rectangle();
      Rectangle var5 = new Rectangle();
      Insets var6 = this.getInsets();
      var3.x = var6.left;
      var3.y = var6.top;
      var3.width = this.getWidth() - (var6.left + var6.right);
      var3.height = this.getHeight() - (var6.top + var6.bottom);
      SwingUtilities.layoutCompoundLabel(this, var2, this.getText(), this.sortArrow, this.getVerticalAlignment(), this.getHorizontalAlignment(), this.getVerticalTextPosition(), this.getHorizontalTextPosition(), var3, var5, var4, this.getIconTextGap());
      int var7 = this.getWidth() - var6.right - this.sortArrow.getIconWidth();
      int var8 = var5.y;
      return new Point(var7, var8);
   }

   private class EmptyIcon implements Icon, Serializable {
      int width;
      int height;

      private EmptyIcon() {
         this.width = 0;
         this.height = 0;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return this.width;
      }

      public int getIconHeight() {
         return this.height;
      }

      // $FF: synthetic method
      EmptyIcon(Object var2) {
         this();
      }
   }
}
