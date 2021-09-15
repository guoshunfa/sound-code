package javax.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import sun.swing.DefaultLookup;

public class DefaultTableCellRenderer extends JLabel implements TableCellRenderer, Serializable {
   private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
   private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
   protected static Border noFocusBorder;
   private Color unselectedForeground;
   private Color unselectedBackground;

   public DefaultTableCellRenderer() {
      this.setOpaque(true);
      this.setBorder(this.getNoFocusBorder());
      this.setName("Table.cellRenderer");
   }

   private Border getNoFocusBorder() {
      Border var1 = DefaultLookup.getBorder(this, this.ui, "Table.cellNoFocusBorder");
      if (System.getSecurityManager() != null) {
         return var1 != null ? var1 : SAFE_NO_FOCUS_BORDER;
      } else {
         return var1 == null || noFocusBorder != null && noFocusBorder != DEFAULT_NO_FOCUS_BORDER ? noFocusBorder : var1;
      }
   }

   public void setForeground(Color var1) {
      super.setForeground(var1);
      this.unselectedForeground = var1;
   }

   public void setBackground(Color var1) {
      super.setBackground(var1);
      this.unselectedBackground = var1;
   }

   public void updateUI() {
      super.updateUI();
      this.setForeground((Color)null);
      this.setBackground((Color)null);
   }

   public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
      if (var1 == null) {
         return this;
      } else {
         Color var7 = null;
         Color var8 = null;
         JTable.DropLocation var9 = var1.getDropLocation();
         if (var9 != null && !var9.isInsertRow() && !var9.isInsertColumn() && var9.getRow() == var5 && var9.getColumn() == var6) {
            var7 = DefaultLookup.getColor(this, this.ui, "Table.dropCellForeground");
            var8 = DefaultLookup.getColor(this, this.ui, "Table.dropCellBackground");
            var3 = true;
         }

         Color var11;
         if (var3) {
            super.setForeground(var7 == null ? var1.getSelectionForeground() : var7);
            super.setBackground(var8 == null ? var1.getSelectionBackground() : var8);
         } else {
            Color var10 = this.unselectedBackground != null ? this.unselectedBackground : var1.getBackground();
            if (var10 == null || var10 instanceof javax.swing.plaf.UIResource) {
               var11 = DefaultLookup.getColor(this, this.ui, "Table.alternateRowColor");
               if (var11 != null && var5 % 2 != 0) {
                  var10 = var11;
               }
            }

            super.setForeground(this.unselectedForeground != null ? this.unselectedForeground : var1.getForeground());
            super.setBackground(var10);
         }

         this.setFont(var1.getFont());
         if (var4) {
            Border var12 = null;
            if (var3) {
               var12 = DefaultLookup.getBorder(this, this.ui, "Table.focusSelectedCellHighlightBorder");
            }

            if (var12 == null) {
               var12 = DefaultLookup.getBorder(this, this.ui, "Table.focusCellHighlightBorder");
            }

            this.setBorder(var12);
            if (!var3 && var1.isCellEditable(var5, var6)) {
               var11 = DefaultLookup.getColor(this, this.ui, "Table.focusCellForeground");
               if (var11 != null) {
                  super.setForeground(var11);
               }

               var11 = DefaultLookup.getColor(this, this.ui, "Table.focusCellBackground");
               if (var11 != null) {
                  super.setBackground(var11);
               }
            }
         } else {
            this.setBorder(this.getNoFocusBorder());
         }

         this.setValue(var2);
         return this;
      }
   }

   public boolean isOpaque() {
      Color var1 = this.getBackground();
      Container var2 = this.getParent();
      if (var2 != null) {
         var2 = var2.getParent();
      }

      boolean var3 = var1 != null && var2 != null && var1.equals(var2.getBackground()) && var2.isOpaque();
      return !var3 && super.isOpaque();
   }

   public void invalidate() {
   }

   public void validate() {
   }

   public void revalidate() {
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
   }

   public void repaint(Rectangle var1) {
   }

   public void repaint() {
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (var1 == "text" || var1 == "labelFor" || var1 == "displayedMnemonic" || (var1 == "font" || var1 == "foreground") && var2 != var3 && this.getClientProperty("html") != null) {
         super.firePropertyChange(var1, var2, var3);
      }

   }

   public void firePropertyChange(String var1, boolean var2, boolean var3) {
   }

   protected void setValue(Object var1) {
      this.setText(var1 == null ? "" : var1.toString());
   }

   static {
      noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
   }

   public static class UIResource extends DefaultTableCellRenderer implements javax.swing.plaf.UIResource {
   }
}
