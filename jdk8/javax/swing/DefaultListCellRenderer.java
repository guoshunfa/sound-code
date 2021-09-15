package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import sun.swing.DefaultLookup;

public class DefaultListCellRenderer extends JLabel implements ListCellRenderer<Object>, Serializable {
   private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
   private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
   protected static Border noFocusBorder;

   public DefaultListCellRenderer() {
      this.setOpaque(true);
      this.setBorder(this.getNoFocusBorder());
      this.setName("List.cellRenderer");
   }

   private Border getNoFocusBorder() {
      Border var1 = DefaultLookup.getBorder(this, this.ui, "List.cellNoFocusBorder");
      if (System.getSecurityManager() != null) {
         return var1 != null ? var1 : SAFE_NO_FOCUS_BORDER;
      } else {
         return var1 == null || noFocusBorder != null && noFocusBorder != DEFAULT_NO_FOCUS_BORDER ? noFocusBorder : var1;
      }
   }

   public Component getListCellRendererComponent(JList<?> var1, Object var2, int var3, boolean var4, boolean var5) {
      this.setComponentOrientation(var1.getComponentOrientation());
      Color var6 = null;
      Color var7 = null;
      JList.DropLocation var8 = var1.getDropLocation();
      if (var8 != null && !var8.isInsert() && var8.getIndex() == var3) {
         var6 = DefaultLookup.getColor(this, this.ui, "List.dropCellBackground");
         var7 = DefaultLookup.getColor(this, this.ui, "List.dropCellForeground");
         var4 = true;
      }

      if (var4) {
         this.setBackground(var6 == null ? var1.getSelectionBackground() : var6);
         this.setForeground(var7 == null ? var1.getSelectionForeground() : var7);
      } else {
         this.setBackground(var1.getBackground());
         this.setForeground(var1.getForeground());
      }

      if (var2 instanceof Icon) {
         this.setIcon((Icon)var2);
         this.setText("");
      } else {
         this.setIcon((Icon)null);
         this.setText(var2 == null ? "" : var2.toString());
      }

      this.setEnabled(var1.isEnabled());
      this.setFont(var1.getFont());
      Border var9 = null;
      if (var5) {
         if (var4) {
            var9 = DefaultLookup.getBorder(this, this.ui, "List.focusSelectedCellHighlightBorder");
         }

         if (var9 == null) {
            var9 = DefaultLookup.getBorder(this, this.ui, "List.focusCellHighlightBorder");
         }
      } else {
         var9 = this.getNoFocusBorder();
      }

      this.setBorder(var9);
      return this;
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

   public void validate() {
   }

   public void invalidate() {
   }

   public void repaint() {
   }

   public void revalidate() {
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
   }

   public void repaint(Rectangle var1) {
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (var1 == "text" || (var1 == "font" || var1 == "foreground") && var2 != var3 && this.getClientProperty("html") != null) {
         super.firePropertyChange(var1, var2, var3);
      }

   }

   public void firePropertyChange(String var1, byte var2, byte var3) {
   }

   public void firePropertyChange(String var1, char var2, char var3) {
   }

   public void firePropertyChange(String var1, short var2, short var3) {
   }

   public void firePropertyChange(String var1, int var2, int var3) {
   }

   public void firePropertyChange(String var1, long var2, long var4) {
   }

   public void firePropertyChange(String var1, float var2, float var3) {
   }

   public void firePropertyChange(String var1, double var2, double var4) {
   }

   public void firePropertyChange(String var1, boolean var2, boolean var3) {
   }

   static {
      noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
   }

   public static class UIResource extends DefaultListCellRenderer implements javax.swing.plaf.UIResource {
   }
}
